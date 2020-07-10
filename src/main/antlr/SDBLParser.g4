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
selectStatement: (subquery ordersAndTotalsStatement?) | (subqueryTemparyTable ordersStatement? indexingStatement?);
inlineSubquery: LPAREN subquery ordersStatement? RPAREN;
subquery: query union*;
query:
    SELECT limitations
    subqueryFields
    fromStatement
    (WHERE whereSearch=withoutAggregateExpression)?
    (GROUP by=(BY_EN | PO_RU) groupByItems)?
    (HAVING havingSearch=havingExpression)?
    (FOR UPDATE mdo?)?
    ;

subqueryTemparyTable: queryTemparyTable union*;
queryTemparyTable:
    SELECT limitations
    temparyTableFields
    INTO temparyTableName=id
    fromStatement
    (WHERE whereSearch=withoutAggregateExpression)?
    (GROUP by=(BY_EN | PO_RU) groupByItems)?
    (HAVING havingSearch=havingExpression)?
    (FOR UPDATE mdo?)?
    // TODO придумать как здесь использовать subqueryTemparyTable, но с заппретом INTO
    ;

union: UNION ALL? query;

ordersAndTotalsStatement:
        (AUTOORDER ordersStatement totalsStatement)
        | (ordersStatement AUTOORDER totalsStatement)
        | (ordersStatement totalsStatement AUTOORDER)
        | (AUTOORDER (ordersStatement | totalsStatement)?)
        | (ordersStatement (AUTOORDER | totalsStatement)?)
        | (totalsStatement AUTOORDER?)
        ;

ordersStatement: ORDER by=(BY_EN | PO_RU) expression orderDirection? (COMMA expression orderDirection?)*;
orderDirection: ASC | DESC | (hierarhy=(HIERARCHY_EN | HIERARCHYA_RU) DESC?);

totalsStatement: TOTALS totalsItems? by=(BY_EN | PO_RU) totalsGroups;
totalsItems: totalsItemExpression alias? (COMMA totalsItemExpression alias?)*;
totalsGroups: totals (COMMA totals)*;
totals:
   (
        OVERALL
      | (withoutAggregateExpression
          (
              (ONLY? (hierarhy=(HIERARCHY_EN | HIERARCHYA_RU)))
            | (doCall=PERIODS LPAREN datePart (COMMA withoutAggregateExpression?)? (COMMA withoutAggregateExpression?)? RPAREN)
          )?
      )
   )
   alias?
   ;

indexingStatement: INDEX by=(BY_EN | PO_RU) indexingItem (COMMA indexingItem)*;
indexingItem: parameter | field;

limitations:
    (ALLOWED DISTINCT top)
    | (ALLOWED top DISTINCT)
    | (top ALLOWED DISTINCT)
    | (top DISTINCT ALLOWED)
    | (DISTINCT ALLOWED top)
    | (DISTINCT top ALLOWED)
    | (ALLOWED DISTINCT)
    | (ALLOWED top)
    | (DISTINCT ALLOWED)
    | (DISTINCT top)
    | (top ALLOWED)
    | (top DISTINCT)
    | ((ALLOWED | DISTINCT | top)?)
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
    alias?
    ;

temparyTableFields: temparyTableField (COMMA temparyTableField)*;
temparyTableField:
    (
          expression
        | ((tableName=id DOT)* MUL)
        | (doCall=RECORDAUTONUMBER LPAREN RPAREN)
    )
    alias?
    ;

emptyTableFields: emptyTableField (COMMA emptyTableField)*;
emptyTableField: alias?;

inlineTableFields: inlineTableField (COMMA inlineTableField)*;
inlineTableField: inlineTableExpression alias?;

fromStatement: (FROM dataSources)?;
dataSources: dataSource (COMMA dataSource)*;
dataSource:
    (   (LPAREN dataSource RPAREN)
        | inlineSubquery
        | table
        | temparyTable
        | virtualTable
        | parameter
    ) alias? joinPart*
    ;

table: mdo (DOT inlineTable=id)?;
temparyTable: tableName=id;
virtualTable:
    (mdo DOT virtualTableName (LPAREN virtualTableParameters RPAREN)?)
    | (FILTER_CRITERION_TYPE DOT id LPAREN parameter? RPAREN) // для критерия отбора ВТ не указывается
    ;
// todo надо для каждого типа ВТ свои параметры прописать, пока - какие-то выажения
virtualTableParameters: virtualTableExpression? (COMMA virtualTableExpression?)*;

joinPart:
    (INNER? | ((LEFT | RIGHT | FULL) OUTER?))                       // тип соединения
    JOIN dataSource on=(ON_EN | PO_RU) withoutAggregateExpression   // имя таблицы и соединение
    ;

groupByItems: withoutAggregateExpression (COMMA withoutAggregateExpression)*;

// EXPRESSIONS
expression: member (operation member)*;
member:
    (
          (negativeOperation* ((LPAREN expression (COMMA expression)* RPAREN) | parameter | field))
        | (unaryOpertion* ((LPAREN expression RPAREN) | parameter | field))
        | (negativeOperation* literal=(TRUE | FALSE | NULL))            // для булева и null можно только отрицание
        | (unaryOpertion* literal=(DECIMAL | FLOAT))                    // для чисел возможно можно унарные
        | (literal=(STR | UNDEFINED))                                   // другого нельзя
        | callStatement
        | aggregateCallStatement
        | caseStatement
    ) (
          (REFS mdo)
        | (negativeOperation* LIKE expression ESCAPE escape=STR) // TODO подумать, как сделать проверку на один символ для ESCAPE
        | (negativeOperation* IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? ((LPAREN expression (COMMA expression)* RPAREN) | inlineSubquery))
        | (negativeOperation* BETWEEN pairStatement)
        | (IS negativeOperation* literal=NULL)
    )?
    ;
pairStatement: expression AND expression;

caseStatement: (unaryOpertion* | negativeOperation*) CASE expression? whenBranch+ elseBranch? END;
whenBranch: WHEN expression THEN expression;
elseBranch: ELSE expression;

aggregateCallStatement:
    ((unaryOpertion* | negativeOperation*) doCall=(SUM | AVG | MIN | MAX) LPAREN expression RPAREN)
    | (unaryOpertion* doCall=COUNT LPAREN (DISTINCT? expression | MUL) RPAREN)
    ;
callStatement:
         (doCall=DATETIME LPAREN (parameter | DECIMAL) COMMA (parameter | DECIMAL) COMMA (parameter | DECIMAL)
            (COMMA (parameter | DECIMAL) COMMA (parameter | DECIMAL) (COMMA (parameter | DECIMAL)))? RPAREN)
        | (doCall=TYPE LPAREN (mdo | type) RPAREN)
        | (doCall=SUBSTRING LPAREN expression COMMA expression COMMA expression RPAREN)
        | (doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN expression RPAREN)
        | (doCall=(VALUETYPE | PRESENTATION | REFPRESENTATION) LPAREN expression RPAREN)
        | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN expression COMMA datePart RPAREN)
        | (doCall=DATEADD LPAREN expression COMMA datePart COMMA expression RPAREN)
        | (doCall=DATEDIFF LPAREN expression COMMA expression COMMA datePart RPAREN)
        | ((negativeOperation* | unaryOpertion*) doCall=ISNULL LPAREN expression COMMA expression RPAREN)
        | ((negativeOperation* | unaryOpertion*) doCall=CAST LPAREN expression AS (
            BOOLEAN
            | (NUMBER (LPAREN DECIMAL (COMMA DECIMAL)? RPAREN)?)
            | (STRING (LPAREN DECIMAL RPAREN)?)
            | DATE
            | mdo
          ) RPAREN (DOT id)*)
        | (negativeOperation* doCall=VALUE LPAREN (
            (mdo DOT ROUTEPOINT_FIELD DOT IDENTIFIER)       // для точки маршрута бизнес процесса
            | (id DOT id)                                   // для системного перечисления
            | (mdo DOT fieldName=id?)                       // может быть просто точка - аналог пустой ссылки
          ) RPAREN)
        ;

// WITHOUT AGGREGATE EXPRESSION
// без использования агрегатные ф-ии
withoutAggregateExpression: withoutAggregateMember (operation withoutAggregateMember)*;
withoutAggregateMember:
    (
          (negativeOperation* ((LPAREN withoutAggregateExpression (COMMA withoutAggregateExpression)* RPAREN) | parameter | field))
        | (unaryOpertion* ((LPAREN withoutAggregateExpression RPAREN) | parameter | field))
        | (negativeOperation* literal=(TRUE | FALSE | NULL))            // для булева и null можно только отрицание
        | (unaryOpertion* literal=(DECIMAL | FLOAT))                    // для чисел возможно можно унарные
        | (literal=(STR | UNDEFINED))                                   // другого нельзя
        | withoutAggregateCallStatement
        | withoutAggregateCaseStatement
    ) (
        (REFS mdo)
        | (negativeOperation* LIKE withoutAggregateExpression ESCAPE escape=STR) // TODO подумать, как сделать проверку на один символ для ESCAPE
        | (negativeOperation* IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? ((LPAREN withoutAggregateExpression (COMMA withoutAggregateExpression)* RPAREN) | inlineSubquery))
        | (negativeOperation* BETWEEN withoutAggregatePairStatement)
        | (IS negativeOperation* literal=NULL)
    )?
    ;
withoutAggregatePairStatement: withoutAggregateExpression AND withoutAggregateExpression;
withoutAggregateCaseStatement: (unaryOpertion* | negativeOperation*) CASE withoutAggregateExpression? withoutAggregateWhenBranch+ withoutAggregateElseBranch? END;
withoutAggregateWhenBranch: WHEN withoutAggregateExpression THEN withoutAggregateExpression;
withoutAggregateElseBranch: ELSE withoutAggregateExpression;
withoutAggregateCallStatement:
         (doCall=DATETIME LPAREN (parameter | DECIMAL) COMMA (parameter | DECIMAL) COMMA (parameter | DECIMAL)
            (COMMA (parameter | DECIMAL) COMMA (parameter | DECIMAL) (COMMA (parameter | DECIMAL)))? RPAREN)
        | (doCall=TYPE LPAREN (mdo | type) RPAREN)
        | (doCall=SUBSTRING LPAREN withoutAggregateExpression COMMA withoutAggregateExpression COMMA withoutAggregateExpression RPAREN)
        | (doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN withoutAggregateExpression RPAREN)
        | (doCall=(VALUETYPE | PRESENTATION | REFPRESENTATION) LPAREN withoutAggregateExpression RPAREN)
        | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN withoutAggregateExpression COMMA datePart RPAREN)
        | (doCall=DATEADD LPAREN withoutAggregateExpression COMMA datePart COMMA withoutAggregateExpression RPAREN)
        | (doCall=DATEDIFF LPAREN withoutAggregateExpression COMMA withoutAggregateExpression COMMA datePart RPAREN)
        | ((negativeOperation* | unaryOpertion*) doCall=ISNULL LPAREN withoutAggregateExpression COMMA withoutAggregateExpression RPAREN)
        | ((negativeOperation* | unaryOpertion*) doCall=CAST LPAREN withoutAggregateExpression AS (
            BOOLEAN
            | (NUMBER (LPAREN DECIMAL (COMMA DECIMAL)? RPAREN)?)
            | (STRING (LPAREN DECIMAL RPAREN)?)
            | DATE
            | mdo
          ) RPAREN (DOT id)*)
        | (doCall=VALUE LPAREN (
            (mdo DOT ROUTEPOINT_FIELD DOT IDENTIFIER)       // для точки маршрута бизнес процесса
            | (id DOT id)                                   // для системного перечисления
            | (mdo DOT )                                    // может быть просто точка - аналог пустой ссылки
            | (mdo DOT fieldName=id)
          ) RPAREN)
        ;

// todo нужно придумать, как разделить
inlineTableExpression: expression;
virtualTableExpression: expression;
havingExpression: expression;
totalsItemExpression: expression;

parameter: AMPERSAND parameterName=PARAMETER_IDENTIFIER; // любые символы
field: (tableName=id DOT)* fieldName=id;
alias: AS? id;

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

type: STRING | BOOLEAN | DATE | NUMBER; // встроенные типы
datePart: MINUTE | HOUR | DAY | WEEK | MONTH | QUARTER | YEAR | TENDAYS | HALFYEAR | SECOND; // части дат

// OPERATION
boolOperation       : OR | AND;
negativeOperation   : NOT;
unaryOpertion       : MINUS;
binaryOperation     : PLUS | MINUS | MUL | QUOTIENT;
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
    | RECORDAUTONUMBER
    | REFPRESENTATION
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
    // виртуальные таблицы
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
    | ROUTEPOINT_FIELD
    ;

// todo
// 1. [ ] Поля в разных секциях отличаются по правилам, надо сделать для каждого варианта
//  - [x] для блока выборки
//  - [x] для пустой таблицы
//  - [ ] для вложенных таблиц
//  - [ ] для итоги
//  - [x] для Для изменения
//  - [x] для индексировать
// 2. [ ] Выражения в разных секциях отличаются по правилам, надо сделать для каждого варианта
//  - [x] для блока выборки
//  - [ ] для вложенных таблиц
//  - [x] для соединений
//  - [x] для условий
//  - [x] для упорядочить
//  - [x] для сгруппировать
//  - [ ] для итоги
// 3. [?] Добавить системные перечисления
// 4. [?] Добавить сопоставление виртуальных таблиц MDO
// 5. [x] Пробел между выражением и алиасом должен быть
// 6. [x] Реализовать многострочные строки - могут быть без | вначале
// 7. [ ] Оптимизировать скорость парсера
// 8. [ ] Комментарии в многострочной строке
