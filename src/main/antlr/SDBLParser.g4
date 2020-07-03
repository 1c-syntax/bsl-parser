/**
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018-2020
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Gryzlov <nixel2007@gmail.com>, Sergey Batanov <sergey.batanov@dmpas.ru>
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * BSL Parser is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * BSL Parser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BSL Parser.
 */
parser grammar SDBLParser;

options {
    tokenVocab = SDBLLexer;
    contextSuperClass = 'BSLParserRuleContext';
}

// ROOT
// пакет запросов, запросы и удаления таблицы должны разделяться через запятую
queries: (query | dropTable) (SEMICOLON (query | dropTable))* EOF;

// DROP TABLE
// удаление таблицы
dropTable: DROP_KEYWORD tableName SEMICOLON?;

// QUERY
// описание запроса
query:
    (
        (subqueries AUTOORDER_KEYWORD? totals? forUpdate?)   // подзапросы с объдинениями и тогами
        | subqueriesTemparyTable forUpdate?             // ВТ с объединениями
    ) SEMICOLON?
    ;

// основная часть запроса
subqueries: subquery (union subquery)*;
subquery: SELECT_KEYWORD limitations fields froms? filters? grouping? ordering?;
subqueriesTemparyTable: subqueryTemparyTable (union subquery)*;
subqueryTemparyTable: SELECT_KEYWORD limitations fields temporaryTable froms? filters? grouping? ordering? indexing?;

// ограничения выборки
limitations:
      (ALLOWED_KEYWORD? DISTINCT_KEYWORD? top?)
    | (ALLOWED_KEYWORD? top? DISTINCT_KEYWORD?)
    | (top? ALLOWED_KEYWORD? DISTINCT_KEYWORD?)
    | (DISTINCT_KEYWORD? ALLOWED_KEYWORD? top?)
    | (DISTINCT_KEYWORD? top? ALLOWED_KEYWORD?)
    | (top? DISTINCT_KEYWORD? ALLOWED_KEYWORD?)
    ;
top     : TOP_KEYWORD DECIMAL+; // первые N

// поля выборки
fields: field (COMMA field)*;
field: fieldValue alias?;
fieldValue:
    mul
    | emptyTable
    | inlineTable
    | expression
    ;
emptyTable : EMPTYTABLE_KEYWORD DOT LPAREN fields RPAREN;
mul        : (tableName DOT)* MUL;
inlineTable: (tableName DOT)+ LPAREN fields RPAREN;

// помещение во временную таблицу
temporaryTable: INTO_KEYWORD tableName;
indexing: INDEX_KEYWORD by fieldName (COMMA fieldName)*;

// блок перечисления источников, минимум 1, остальные через запятую
froms: FROM_KEYWORD from (COMMA from)*;
from: dataSource alias? joins?;

// DATA SOURCES
dataSource:
    (LPAREN subquery RPAREN)
    | table
    | virtualTable
    | parameterTable
    | (LPAREN dataSource RPAREN)
    ;
table               : ((mdoName (DOT tableName)?) | tableName);
dataSourceField     : (tableName DOT)* fieldName;
parameterTable      : parameter;
virtualTable        :
    mdoName (
        (DOT virtualTableName (LPAREN virtualTableParameters? RPAREN)?)
        | (LPAREN virtualTableParameters RPAREN) // для критерия отбора ВТ не указывается
    );
// todo надо для каждого типа ВТ свои параметры прописать
virtualTableParameters: expression (COMMA expression);

// соединения таблиц
joins: join+;
join:
    (INNER_KEYWORD?
        | (LEFT_KEYWORD | RIGHT_KEYWORD | FULL_KEYWORD) OUTER_KEYWORD?)  // тип соединения
    JOIN_KEYWORD dataSource alias? (EN_ON_KEYWORD | RU_PO_KEYWORD) expression   // имя таблицы и соединение
    ;

// блок отборов к источникам
filters:
    WHERE_KEYWORD expression;

// группировка данных
grouping:
    GROUP_KEYWORD by expression (COMMA expression)* hanving?;
hanving:
    HAVING_KEYWORD expression;

// упорядочивание данных
ordering:
    ORDER_KEYWORD by dataSourceField orderDirection? (COMMA dataSourceField orderDirection?)*;
orderDirection: ASC_KEYWORD | DESC_KEYWORD | (hierarhy DESC_KEYWORD?);

// итоги
totals:
    TOTALS_KEYWORD fields? by total (COMMA total)*;
total:
    OVERALL_KEYWORD
    | (expression (
            (ONLY_KEYWORD? hierarhy)?
            | (PERIODS_KEYWORD LPAREN datePart (COMMA expression)? (COMMA expression)? RPAREN)
        ) alias?
    )
;

// EXPRESSION
expression: member (boolOperation expression)*;

member:
    ((unaryOpertion | negativeOperation)? (
        dataSourceField
            | aggregateFunction
            | inlineFunction
            | caseStatement
            | castStatement
            | literal
            | predefined
            | (LPAREN expression RPAREN)
            | parameter
        )
    ) (
        ((binaryOperation | compareOperation) member)
        | (REFS_KEYWORD mdoName)
        | (negativeOperation? (LIKE_KEYWORD | ESCAPE_KEYWORD) member)
        | (negativeOperation? IN_KEYWORD (EN_HIERARCHY_KEYWORD | RU_HIERARCHII_KEYWORD)? LPAREN ((member (COMMA member)?) | subqueries) RPAREN)
        | (negativeOperation? BETWEEN_KEYWORD member AND_KEYWORD member)
        | (IS_KEYWORD negativeOperation? NULL)
    )?
;

aggregateFunction:
    (
        ((SUM_KEYWORD | AVG_KEYWORD | MIN_KEYWORD | MAX_KEYWORD) LPAREN expression)
        | (COUNT_KEYWORD LPAREN DISTINCT_KEYWORD? (expression | MUL))
    ) RPAREN
    ;

inlineFunction:
    (SUBSTRING_KEYWORD LPAREN expression COMMA DECIMAL COMMA DECIMAL RPAREN)
    | (
        (YEAR_KEYWORD
            | QUARTER_KEYWORD
            | MONTH_KEYWORD
            | DAYOFYEAR_KEYWORD
            | DAY_KEYWORD
            | WEEK_KEYWORD
            | WEEKDAY_KEYWORD
            | HOUR_KEYWORD
            | MINUTE_KEYWORD
            | SECOND_KEYWORD
            | VALUETYPE_KEYWORD
            | PRESENTATION_KEYWORD
        )
        LPAREN expression RPAREN
     )
    | ((BEGINOFPERIOD_KEYWORD | ENDOFPERIOD_KEYWORD) LPAREN expression COMMA datePart RPAREN)
    | (DATEADD_KEYWORD LPAREN expression COMMA datePart COMMA expression RPAREN)
    | (DATEDIFF_KEYWORD LPAREN expression COMMA expression COMMA datePart RPAREN )
    | (ISNULL_KEYWORD LPAREN expression COMMA expression RPAREN)
    | (AUTORECORDNUMBER_KEYWORD LPAREN RPAREN)
    ;

predefined:
    VALUE_KEYWORD LPAREN (
        (mdoName DOT ROUTEPOINT_FIELD DOT routePointName)    // для точки маршрута бизнес процесса
        | (systemEnumName DOT enumValueName)                 // для системного перечисления
        | (mdoName DOT (EMPTYREF_FIELD | fieldName)?)        // может быть просто точка - аналог пустой ссылки
    ) RPAREN;

literal:   // литералы
    TRUE
    | FALSE
    | DECIMAL
    | FLOAT
    | STRING
    | NULL
    | UNDEFINED
    | (DATETIME_KEYWORD LPAREN DECIMAL COMMA DECIMAL COMMA DECIMAL (COMMA DECIMAL)? (COMMA DECIMAL)? (COMMA DECIMAL)? RPAREN)
    | (TYPE_KEYWORD LPAREN (mdoName | typeName) RPAREN)
    ;

caseStatement: CASE_KEYWORD whenBranch+ elseBranch? END_KEYWORD;
whenBranch: WHEN_KEYWORD expression THEN_KEYWORD expression;
elseBranch: ELSE_KEYWORD expression;

castStatement:
    CAST_KEYWORD LPAREN expression AS_KEYWORD (
        BOOLEAN_KEYWORD
        | (NUMBER_KEYWORD (LPAREN DECIMAL (COMMA DECIMAL)? RPAREN)?)
        | (STRING_KEYWORD (LPAREN DECIMAL RPAREN)?)
        | DATE_KEYWORD
        | mdoName
    ) RPAREN
    ;

parameter: AMPERSAND PARAMETER_IDENTIFIER; // имя параметра

// OPERATION
binaryOperation     : PLUS | MINUS | MUL | QUOTIENT | MODULO;
unaryOpertion       : PLUS | MINUS;
compareOperation    : LESS | LESS_OR_EQUAL | GREATER | GREATER_OR_EQUAL | ASSIGN | NOT_EQUAL;
negativeOperation   : NOT_KEYWORD;
boolOperation       : OR_KEYWORD | AND_KEYWORD;

// NAMES
alias       : AS_KEYWORD? aliasName; // псевдоним
aliasName   : IDENTIFIER | keywordAsIdentifier;
tableName   : IDENTIFIER | keywordAsIdentifier; // имя таблицы
fieldName   : IDENTIFIER | keywordAsIdentifier; // имя поля

mdoName     : mdoTypeName DOT mdoTableName; // полное имя объекта метаданных
mdoTableName: IDENTIFIER | mdoTypeName;     // имя объекта метаданных
mdoTypeName :                               // имя типа метаданных
    BUSINESSPROCESS_TYPE
    | CATALOG_TYPE
    | DOCUMENT_TYPE
    | INFORMATION_REGISTER_TYPE
    | CONSTANT_TYPE
    | FILTER_CRITERION_TYPE
    | EXCHANGE_PLAN_TYPE
    | SEQUENCE_TYPE
    | DOCUMENT_JOURNAL_TYPE
    | ENUM_TYPE
    | CHART_OF_CHARACTERISTIC_TYPES_TYPE
    | CHART_OF_ACCOUNTS_TYPE
    | CHART_OF_CALCULATION_TYPES_TYPE
    | ACCUMULATION_REGISTER_TYPE
    | ACCOUNTING_REGISTER_TYPE
    | CALCULATION_REGISTER_TYPE
    | TASK_TYPE
    | EXTERNAL_DATA_SOURCE_TYPE
    ;

systemEnumName  : IDENTIFIER;   // имя системного перечисления
enumValueName   : IDENTIFIER;   // имя значения перечисления
routePointName  : IDENTIFIER;   // имя точки маршрута

// TODO надо прописать все имена VT
virtualTableName:   // имя виртуальной таблицы
    SLICELAST_TT
    | SLICEFIRST_TT
    | BOUNDARIES_TT
    | TURNOVERS_TT
    | BALANCE_TT
    | BALANCE_AND_TURNOVERS_TT
    | EXT_DIMENSIONS_TT
    | RECORDS_WITH_EXT_DIMENSIONS_TT
    | DR_CR_TURNOVERS_TT
    | ACTUAL_ACTION_PERIOD_TT
    | SCHEDULE_DATA_TT
    | TASK_BY_PERFORMER_TT
    ;

typeName    :                               // встроенные типы
    STRING_KEYWORD
    | BOOLEAN_KEYWORD
    | DATE_KEYWORD
    | NUMBER_KEYWORD
    | UNDEFINED
    ;
inlineFunctionName:                         // встроенные функции
    SUBSTRING_KEYWORD
    ;
aggregateFunctionName:                      // агрегатные функции
    SUM_KEYWORD
    | AVG_KEYWORD
    | MIN_KEYWORD
    | MAX_KEYWORD
    | COUNT_KEYWORD
    ;

keywordAsIdentifier: // ключевые слова, которые можно использовать как идентификаторы
    typeName
    | aggregateFunctionName
    | inlineFunctionName
    | mdoTypeName
    | virtualTableName
    | ROUTEPOINT_FIELD
    | REFS_KEYWORD
    ;

// OTHER
union: UNION_KEYWORD ALL_KEYWORD?;
forUpdate: FOR_KEYWORD UPDATE_KEYWORD OF_KEYWORD?
    ((mdoName (DOT virtualTableName)?)? (COMMA (mdoName (DOT virtualTableName)?)?)); // для изменения

datePart: // части дат
    MINUTE_KEYWORD
    | HOUR_KEYWORD
    | DAY_KEYWORD
    | WEEK_KEYWORD
    | MONTH_KEYWORD
    | QUARTER_KEYWORD
    | YEAR_KEYWORD
    | TENDAYS_KEYWORD
    | HALFYEAR_KEYWORD
    | SECOND_KEYWORD
    ;

by: EN_BY_KEYWORD | RU_PO_KEYWORD;
hierarhy: EN_HIERARCHY_KEYWORD | RU_HIERARCHYA_KEYWORD;
