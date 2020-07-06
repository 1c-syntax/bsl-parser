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
queries: queryStatement (SEMICOLON queryStatement)* SEMICOLON? EOF;
queryStatement: selectStatement | dropTableStatement;

dropTableStatement: DROP temparyTableName=id;
selectStatement: (subquery ordersAndTotalsStatement) | (subqueryTemparyTable ordersStatement? indexingStatement?);

subquery:
    SELECT limitations
    subqueryFields
    (FROM dataSources)?
//    (WHERE whereSearh)?
//    (GROUP (BY_EN | PO_RU) groupByItems)?
//    (HAVING havingSearch)?
//    (FOR UPDATE forUpdateItems?)?
    (UNION ALL? subquery)*
    ;
subqueryTemparyTable:
    SELECT limitations
    temparyTableFields
    INTO temparyTableName=id
    (FROM dataSources)?
//    (WHERE whereSearh)?
//    (GROUP (BY_EN | PO_RU) groupByItems)?
//    (HAVING havingSearch)?
//    (FOR UPDATE forUpdateItems?)?
    (UNION ALL? subquery)* // TODO придумать как здесь использовать subqueryTemparyTable, но с заппретом INTO
    ;
ordersAndTotalsStatement:
        (AUTOORDER? ordersStatement? totalsStatement?)
        | (ordersStatement? AUTOORDER? totalsStatement?)
        | (ordersStatement? totalsStatement? AUTOORDER?);
ordersStatement: ORDER (BY_EN | PO_RU) ordersItem orderDirection? (COMMA ordersItem orderDirection?)*;
orderDirection: ASC | DESC | (hierarhy=(HIERARCHY_EN | HIERARCHYA_RU) DESC?);
ordersItem: expression;

totalsStatement: TOTALS (BY_EN | PO_RU);

indexingStatement: INDEX (BY_EN | PO_RU) indexingItem (COMMA indexingItem)*;
indexingItem: parameter | field;

limitations:
      (ALLOWED? DISTINCT? top?)
    | (ALLOWED? top? DISTINCT?)
    | (top? ALLOWED? DISTINCT?)
    | (DISTINCT? ALLOWED? top?)
    | (DISTINCT? top? ALLOWED?)
    | (top? DISTINCT? ALLOWED?)
    ;
top: TOP DECIMAL+;

subqueryFields: subqueryField (COMMA subqueryField)*;
subqueryField:
    (
          (emptyTable=EMPTYTABLE DOT LPAREN emptyTableFields RPAREN)
        | ((tableName=id DOT)* inlineTable=id DOT LPAREN inlineTableFields RPAREN)
        | ((tableName=id DOT)* MUL)
        | expression
    )
    (AS? alias=id)?
    ;

temparyTableFields: temparyTableField (COMMA temparyTableField)*;
temparyTableField:
    (
          expression
        | ((tableName=id DOT)* MUL)
    )
    (AS? alias=id)?
    ;

emptyTableFields: ((AS? alias=id)? COMMA)+;

inlineTableFields: inlineTableField (COMMA inlineTableField)*;
inlineTableField: expression (AS? alias=id)?;

dataSources: dataSource (COMMA dataSource)*;
dataSource:
    (     (LPAREN subquery RPAREN)
        | table
        | virtualTable
        | parameter
        | (LPAREN dataSource RPAREN)
    ) (AS? alias=id)? joinPart*
    ;
table: ((mdo (DOT inlineTable=id)?) | tableName=id);
virtualTable:
    (mdo DOT virtualTableName (LPAREN virtualTableParameters? RPAREN)?)
    | (FILTER_CRITERION_TYPE DOT id LPAREN parameter RPAREN) // для критерия отбора ВТ не указывается
    ;
// todo надо для каждого типа ВТ свои параметры прописать, пока - какие-то выажения
virtualTableParameters: expression (COMMA expression)*;

joinPart:
    (INNER? | (LEFT | RIGHT | FULL) OUTER?)  // тип соединения
    JOIN dataSource (ON_EN | PO_RU) expression   // имя таблицы и соединение
    ;


whereSearh:IDENTIFIER;

groupByItems:IDENTIFIER;

havingSearch:IDENTIFIER;

forUpdateItems:IDENTIFIER;


// EXPRESSIONS

expression: member (operation member)*;
member:
    ((unaryOpertion | negativeOperation)?
    (
          (LPAREN expression RPAREN)
        | (parameter | field | literal))
    )
    ;

// COMMON

parameter: AMPERSAND parameterName=PARAMETER_IDENTIFIER; // любые символы
field: (tableName=id DOT)* fieldName=id;
literal:   // литералы
    TRUE
    | FALSE
    | DECIMAL
    | FLOAT
    | STR
    | NULL
    | UNDEFINED
    | (function=DATETIME LPAREN DECIMAL COMMA DECIMAL COMMA DECIMAL (COMMA DECIMAL)? (COMMA DECIMAL)? (COMMA DECIMAL)? RPAREN)
    | (function=TYPE LPAREN (mdo | type) RPAREN)
    ;

mdo: mdoType DOT mdoName=id; // полное имя объекта метаданных
mdoType:                             // имя типа метаданных
    BUSINESS_PROCESS_TYPE
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
virtualTableName:            // имя виртуальной таблицы
    SLICELAST_VT
    | SLICEFIRST_VT
    | BOUNDARIES_VT
    | TURNOVERS_VT
    | BALANCE_VT
    | BALANCE_AND_TURNOVERS_VT
    | EXT_DIMENSIONS_VT
    | RECORDS_WITH_EXT_DIMENSIONS_VT
    | DR_CR_TURNOVERS_VT
    | ACTUAL_ACTION_PERIOD_VT
    | SCHEDULE_DATA_VT
    | TASK_BY_PERFORMER_VT
    ;

type:                        // встроенные типы
    STRING
    | BOOLEAN
    | DATE
    | NUMBER
    ;

// OPERATION
boolOperation       : OR | AND;
negativeOperation   : NOT;
unaryOpertion       : PLUS | MINUS;
binaryOperation     : PLUS | MINUS | MUL | QUOTIENT | MODULO;
compareOperation    : LESS | LESS_OR_EQUAL | GREATER | GREATER_OR_EQUAL | ASSIGN | NOT_EQUAL;
operation           : binaryOperation | compareOperation | boolOperation;

id: // возможные идентификаторы
    IDENTIFIER // просто идентификатор
    // типы метаданных
    | BUSINESS_PROCESS_TYPE
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
    // ключевые слова
    | ALL
    | DROP
    | END
    | FULL
    | HIERARCHY_EN
    | HIERARCHII_RU
    | HIERARCHYA_RU
    | INDEX
    | ISNULL
    | JOIN
    | LEFT
    | ORDER
    | OUTER
    | RIGHT
    | SELECT
    | TOTALS
    | UNION
    | AUTORECORDNUMBER
    | AVG
    | BEGINOFPERIOD
    | BOOLEAN
    | COUNT
    | DATE
    | DATEADD
    | DATEDIFF
    | DATETIME
    | DAY
    | DAYOFYEAR
    | EMPTYTABLE
    | ENDOFPERIOD
    | HALFYEAR
    | HOUR
    | MAX
    | MIN
    | MINUTE
    | MONTH
    | NUMBER
    | QUARTER
    | ONLY
    | PERIODS
    | REFS
    | PRESENTATION
    | SECOND
    | STRING
    | SUBSTRING
    | SUM
    | TENDAYS
    | TYPE
    | UPDATE
    | VALUE
    | VALUETYPE
    | WEEK
    | WEEKDAY
    | YEAR
    // виртуаьные таблицы
    | ACTUAL_ACTION_PERIOD_VT
    | BALANCE_VT
    | BALANCE_AND_TURNOVERS_VT
    | BOUNDARIES_VT
    | DR_CR_TURNOVERS_VT
    | EXT_DIMENSIONS_VT
    | RECORDS_WITH_EXT_DIMENSIONS_VT
    | SCHEDULE_DATA_VT
    | SLICEFIRST_VT
    | SLICELAST_VT
    | TASK_BY_PERFORMER_VT
    | TURNOVERS_VT
    // системные поля
    | EMPTYREF_FIELD
    | ROUTEPOINT_FIELD
    ;


//
//dataSourceField     : (tableName DOT)* fieldName;


//

//
//// блок отборов к источникам
//filters: WHERE_KEYWORD expression;
//
//// группировка данных
//grouping: GROUP_KEYWORD by expression (COMMA expression)* (HAVING_KEYWORD expression)?;
//
//// упорядочивание данных
//ordering:
//    ORDER_KEYWORD by dataSourceField orderDirection? (COMMA dataSourceField orderDirection?)*;
//
//
//// итоги
//totals: TOTALS_KEYWORD fields? by total (COMMA total)*;
//total:
//    OVERALL_KEYWORD
//    | (expression (
//            (ONLY_KEYWORD? hierarhy)?
//            | (PERIODS_KEYWORD LPAREN datePart (COMMA expression)? (COMMA expression)? RPAREN)
//        ) alias?
//    )
//;
//
//// EXPRESSION
//expression: member (boolOperation expression)*;
//
//member:
//    ((unaryOpertion | negativeOperation)? (
//        dataSourceField
//            | aggregateFunction
//            | inlineFunction
//            | caseStatement
//            | castStatement
//            | literal
//            | predefined
//            | (LPAREN expression RPAREN)
//            | parameter
//        )
//    ) (
//        ((binaryOperation | compareOperation) member)
//        | (REFS_KEYWORD mdoName)
//        | (negativeOperation? (LIKE_KEYWORD | ESCAPE_KEYWORD) member)
//        | (negativeOperation? IN_KEYWORD (EN_HIERARCHY_KEYWORD | RU_HIERARCHII_KEYWORD)? LPAREN ((member (COMMA member)?) | subqueries) RPAREN)
//        | (negativeOperation? BETWEEN_KEYWORD member AND_KEYWORD member)
//        | (IS_KEYWORD negativeOperation? NULL)
//    )?
//;
//
//aggregateFunction:
//    (
//        ((SUM_KEYWORD | AVG_KEYWORD | MIN_KEYWORD | MAX_KEYWORD) LPAREN expression)
//        | (COUNT_KEYWORD LPAREN DISTINCT_KEYWORD? (expression | MUL))
//    ) RPAREN
//    ;
//
//inlineFunction:
//    (SUBSTRING_KEYWORD LPAREN expression COMMA DECIMAL COMMA DECIMAL RPAREN)
//    | (
//        (YEAR_KEYWORD
//            | QUARTER_KEYWORD
//            | MONTH_KEYWORD
//            | DAYOFYEAR_KEYWORD
//            | DAY_KEYWORD
//            | WEEK_KEYWORD
//            | WEEKDAY_KEYWORD
//            | HOUR_KEYWORD
//            | MINUTE_KEYWORD
//            | SECOND_KEYWORD
//            | VALUETYPE_KEYWORD
//            | PRESENTATION_KEYWORD
//        )
//        LPAREN expression RPAREN
//     )
//    | ((BEGINOFPERIOD_KEYWORD | ENDOFPERIOD_KEYWORD) LPAREN expression COMMA datePart RPAREN)
//    | (DATEADD_KEYWORD LPAREN expression COMMA datePart COMMA expression RPAREN)
//    | (DATEDIFF_KEYWORD LPAREN expression COMMA expression COMMA datePart RPAREN )
//    | (ISNULL_KEYWORD LPAREN expression COMMA expression RPAREN)
//    | (AUTORECORDNUMBER_KEYWORD LPAREN RPAREN)
//    ;
//
//predefined:
//    VALUE_KEYWORD LPAREN (
//        (mdoName DOT ROUTEPOINT_FIELD DOT IDENTIFIER)   // для точки маршрута бизнес процесса
//        | (IDENTIFIER DOT IDENTIFIER)                   // для системного перечисления
//        | (mdoName DOT (EMPTYREF_FIELD | fieldName)?)   // может быть просто точка - аналог пустой ссылки
//    ) RPAREN;
//

//
//caseStatement: CASE_KEYWORD whenBranch+ elseBranch? END_KEYWORD;
//whenBranch: WHEN_KEYWORD expression THEN_KEYWORD expression;
//elseBranch: ELSE_KEYWORD expression;
//
//castStatement:
//    CAST_KEYWORD LPAREN expression AS_KEYWORD (
//        BOOLEAN_KEYWORD
//        | (NUMBER_KEYWORD (LPAREN DECIMAL (COMMA DECIMAL)? RPAREN)?)
//        | (STRING_KEYWORD (LPAREN DECIMAL RPAREN)?)
//        | DATE_KEYWORD
//        | mdoName
//    ) RPAREN
//    ;
//
//parameter: AMPERSAND PARAMETER_IDENTIFIER; // имя параметра
//
//// OPERATION

//
//

//
//
//// NAMES
//alias       : AS_KEYWORD? aliasName; // псевдоним
//aliasName   : IDENTIFIER | keywordAsIdentifier;
//tableName   : IDENTIFIER | keywordAsIdentifier; // имя таблицы
//fieldName   : IDENTIFIER | keywordAsIdentifier; // имя поля
//

//
//// TODO надо прописать все имена VT
//

//    ;
//inlineFunctionName:                         // встроенные функции
//    SUBSTRING_KEYWORD
//    ;
//aggregateFunctionName:                      // агрегатные функции
//    SUM_KEYWORD
//    | AVG_KEYWORD
//    | MIN_KEYWORD
//    | MAX_KEYWORD
//    | COUNT_KEYWORD
//    ;
//
//keywordAsIdentifier: // ключевые слова, которые можно использовать как идентификаторы
//    typeName
//    | aggregateFunctionName
//    | inlineFunctionName
//    | mdoTypeName
//    | virtualTableName
//    | ROUTEPOINT_FIELD
//    | REFS_KEYWORD
//    ;
//
//// OTHER
//union: UNION_KEYWORD ALL_KEYWORD?;
//forUpdate: FOR_KEYWORD UPDATE_KEYWORD OF_KEYWORD?
//    ((mdoName (DOT virtualTableName)?)? (COMMA (mdoName (DOT virtualTableName)?)?)); // для изменения
//
//datePart: // части дат
//    MINUTE_KEYWORD
//    | HOUR_KEYWORD
//    | DAY_KEYWORD
//    | WEEK_KEYWORD
//    | MONTH_KEYWORD
//    | QUARTER_KEYWORD
//    | YEAR_KEYWORD
//    | TENDAYS_KEYWORD
//    | HALFYEAR_KEYWORD
//    | SECOND_KEYWORD
//    ;
//
//by: EN_BY_KEYWORD | RU_PO_KEYWORD;
//hierarhy: EN_HIERARCHY_KEYWORD | RU_HIERARCHYA_KEYWORD;
//
//// todo
//// 1. [ ] Поля в разных секциях отличаются по правилам, надо сделать для каждого варианта
////  - [x] для блока выборки
////  - [x] для пустой таблицы
////  - [ ] для вложенных таблиц
////  - [ ] для упорядочить
////  - [ ] для сгруппировать
////  - [ ] для итоги
////  - [x] для Для изменения
////  - [ ] для индексировать
//// 2. [ ] Выражения в разных секциях отличаются по правилам, надо сделать для каждого варианта
////  - [x] для блока выборки
////  - [ ] для вложенных таблиц
////  - [ ] для соединений
////  - [ ] для упорядочить
////  - [ ] для сгруппировать
////  - [ ] для итоги
//// 3. [?] Добавить системные перечисления
//// 4. [?] Добавить сопоставление виртуальных таблиц MDO
