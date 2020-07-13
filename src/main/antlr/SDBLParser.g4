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

// COMMON
// Общие правила, без окраски

// возможные идентификаторы
identifier:
    IDENTIFIER // просто идентификатор объекта
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
// полное имя объекта метаданных, где mdoName - имя прикладного объекта
mdo         : mdoType DOT mdoName=identifier;
// алиас поля или таблицы, где name - собственно идентификатор
alias       : (AS? name=identifier)?;
// колонка (поле) таблицы, где
//      tableName - идетификатор таблицы (каждой вложенной таблицы), может отсутствовать
//      name - собственно идентификатор колонки
column      : (tableName=identifier DOT)* name=identifier;
// параметр, может быть и таблицей, где name - идентификатор параметра
parameter   : AMPERSAND name=PARAMETER_IDENTIFIER;

// имена виртуальных таблиц
virtualTableName:
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

type    : STRING | BOOLEAN | DATE | NUMBER;                                                     // встроенные типы данных
datePart: MINUTE | HOUR | DAY | WEEK | MONTH | QUARTER | YEAR | TENDAYS | HALFYEAR | SECOND;    // составные части дат

// имена типов метаданных
mdoType :
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

boolOperation       : OR | AND;                                                                 // логические операторы
binaryOperation     : PLUS | MINUS | MUL | QUOTIENT;                                            // математические операторы
compareOperation    : LESS | LESS_OR_EQUAL | GREATER | GREATER_OR_EQUAL | ASSIGN | NOT_EQUAL;   // операторы сраневния











//
//// ROOT
//// пакет запросов, запросы и удаления таблицы должны разделяться через запятую
//queries: queryStatement (SEMICOLON queryStatement)* SEMICOLON? EOF;
//queryStatement: selectSubquery | dropTableSubquery;
//
//dropTableSubquery: DROP temparyTableName=id;
//selectSubquery: subquery ordersAndTotals /*(subquery ordersAndTotalsStatement?) | (subqueryTemparyTable ordersStatement? */indexing;
//
//inlineSubquery: LPAREN subquery /*ordersStatement?*/ RPAREN;
//subquery: query union*;
//query:
//    SELECT limitations
//    selectedFields
//    into
//    from
//    where
//    groupBy
//    having
//    forUpdate
//    ;
//
//union: UNION ALL? query;
//
//selectedFields: selectedField (COMMA selectedField)*;
//selectedField:
//    selectExpression
//    alias
//    ;
//
//selectExpression: selectMember (boolOperation selectMember)*;
//selectMember:
//      selectStatement
//    | (selectStatement (binaryOperation selectStatement)* compareOperation selectStatement (binaryOperation selectStatement)*)
//    | (selectStatement (binaryOperation selectStatement)+)
//    | (LPAREN selectStatement (COMMA selectStatement)+ RPAREN)
//    | (selectStatement IN LPAREN (inlineSubquery | (selectStatement (COMMA selectStatement)*)) RPAREN)
//    | (selectStatement REFS mdo)
//    | (selectStatement IS NOT? NULL)
//    | (selectStatement NOT? BETWEEN selectBetweenStatement)
//    ;
//
//selectStatement:
//      statement
//    | (LPAREN statement RPAREN)
//    | (LPAREN selectExpression RPAREN)
//    | (doCall=ISNULL LPAREN selectExpression COMMA selectExpression RPAREN)
//    | (doCall=DATEADD LPAREN selectExpression COMMA datePart COMMA selectExpression RPAREN)
//    | (doCall=DATEDIFF LPAREN selectExpression COMMA selectExpression COMMA datePart RPAREN)
//    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN selectExpression COMMA datePart RPAREN)
//    | (doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN selectExpression RPAREN)
//    | selectCastStatement
//    | selectAggrMathCallStatement
//    | selectAggrCountCallStatement
//    | selectCaseStatement
//    ;
//selectBetweenStatement: selectExpression AND selectExpression;
//selectCaseStatement: CASE selectExpression? selectWhenBranch+ selectElseBranch? END;
//selectWhenBranch: WHEN selectExpression THEN selectExpression;
//selectElseBranch: ELSE selectExpression;
//selectAggrMathCallStatement: doCall=(SUM | AVG | MIN | MAX) LPAREN selectExpression RPAREN;
//selectAggrCountCallStatement: doCall=COUNT LPAREN (DISTINCT? selectExpression | MUL) RPAREN;
//selectCastStatement:
//    (NOT* | MINUS*) doCall=CAST LPAREN selectExpression AS (
//          BOOLEAN
//        | (NUMBER (LPAREN DECIMAL (COMMA DECIMAL)? RPAREN)?)
//        | (STRING (LPAREN DECIMAL RPAREN)?)
//        | DATE
//        | mdo
//        ) RPAREN (DOT id)*;
//
//limitations:
//    (ALLOWED DISTINCT top)
//    | (ALLOWED top DISTINCT)
//    | (top ALLOWED DISTINCT)
//    | (top DISTINCT ALLOWED)
//    | (DISTINCT ALLOWED top)
//    | (DISTINCT top ALLOWED)
//    | (ALLOWED DISTINCT)
//    | (ALLOWED top)
//    | (DISTINCT ALLOWED)
//    | (DISTINCT top)
//    | (top ALLOWED)
//    | (top DISTINCT)
//    | ((ALLOWED | DISTINCT | top)?)
//    ;
//top: TOP DECIMAL+;
//
//into: (INTO temparyTableName=id)?;
//
//from: (FROM dataSources)?;
//dataSources: dataSource (COMMA dataSource)*;
//dataSource:
//    (
//          (LPAREN dataSource RPAREN)
//        | inlineSubquery
//        | table
//        | virtualTable
//        | parameter
//    ) alias joinPart*
//    ;
//table:
//      mdo
//    | (mdo (DOT tableName=id)+)
//    | tableName=id
//    ;
//virtualTable:
//      (mdo DOT virtualTableName (LPAREN virtualTableParameters RPAREN))
//    | (mdo DOT virtualTableName)
//    | (FILTER_CRITERION_TYPE DOT id LPAREN parameter? RPAREN) // для критерия отбора ВТ не указывается
//    ;
//
//// todo надо для каждого типа ВТ свои параметры прописать, пока - какие-то выажения
//virtualTableParameters: virtualTableExpression? (COMMA virtualTableExpression?)*;
//virtualTableExpression: virtualTableMember (boolOperation virtualTableMember)*;
//virtualTableMember:
//      virtualTableStatement
//    | (virtualTableStatement (binaryOperation virtualTableStatement)* compareOperation virtualTableStatement (binaryOperation virtualTableStatement)*)
//    | (virtualTableStatement (binaryOperation virtualTableStatement)+)
//    ;
//virtualTableStatement:
//      statement
//    | (LPAREN statement RPAREN)
//    | (LPAREN virtualTableExpression RPAREN)
//    ;
//
//joinPart:
//    (INNER? | ((LEFT | RIGHT | FULL) OUTER?))                       // тип соединения
//    JOIN dataSource on=(ON_EN | PO_RU) joinExpression   // имя таблицы и соединение
//    ;
//joinExpression: joinMember (boolOperation joinMember)*;
//joinMember:
//      joinStatement
//    | (joinStatement (binaryOperation joinStatement)* compareOperation joinStatement (binaryOperation joinStatement)*)
//    | (joinStatement (binaryOperation joinStatement)+)
//    | (LPAREN joinStatement (COMMA joinStatement)+ RPAREN)
//    | (joinStatement IN LPAREN (inlineSubquery | (joinStatement (COMMA joinStatement)*)) RPAREN)
//    | (joinStatement REFS mdo)
//    | (joinStatement IS NOT? NULL)
//    | (joinStatement NOT? BETWEEN joinBetweenStatement)
//    ;
//joinStatement:
//      statement
//    | (LPAREN statement RPAREN)
//    | (LPAREN joinExpression RPAREN)
//    | (doCall=ISNULL LPAREN joinExpression COMMA joinExpression RPAREN)
//    | (doCall=DATEADD LPAREN joinExpression COMMA datePart COMMA joinExpression RPAREN)
//    | (doCall=DATEDIFF LPAREN joinExpression COMMA joinExpression COMMA datePart RPAREN)
//    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN joinExpression COMMA datePart RPAREN)
//    | (doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN joinExpression RPAREN)
//    | joinCastStatement
//    ;
//joinCastStatement:
//    doCall=CAST LPAREN joinExpression AS (
//          BOOLEAN
//        | (NUMBER (LPAREN DECIMAL (COMMA DECIMAL)? RPAREN)?)
//        | (STRING (LPAREN DECIMAL RPAREN)?)
//        | DATE
//        | mdo
//        ) RPAREN (DOT id)*;
//joinBetweenStatement: joinStatement AND joinStatement;
//
//where: (WHERE whereExpression)?;
//whereExpression:
//      (whereMember (boolOperation (whereMember | (LPAREN whereMember RPAREN)))*)
//    | (LPAREN whereMember RPAREN (boolOperation (whereMember | (LPAREN whereMember RPAREN)))*)
//    ;
//whereMember:
//      whereStatement
//    | (whereStatement (binaryOperation whereStatement)* compareOperation whereStatement (binaryOperation whereStatement)*)
//    | (whereStatement (binaryOperation whereStatement)+)
//    | (LPAREN whereStatement (COMMA whereStatement)+ RPAREN)
//    | (whereStatement IN LPAREN (inlineSubquery | (whereStatement (COMMA whereStatement)*)) RPAREN)
//    | (whereStatement REFS mdo)
//    | (whereStatement IS NOT? NULL)
//    | (whereStatement NOT? BETWEEN whereBetweenStatement)
//    ;
//whereStatement:
//      statement
//    | (LPAREN statement RPAREN)
//    | ((NOT* | MINUS*) LPAREN whereExpression RPAREN)
//    | (doCall=ISNULL LPAREN whereExpression COMMA whereExpression RPAREN)
//    | (doCall=DATEADD LPAREN whereExpression COMMA datePart COMMA whereExpression RPAREN)
//    | (doCall=DATEDIFF LPAREN whereExpression COMMA whereExpression COMMA datePart RPAREN)
//    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN whereExpression COMMA datePart RPAREN)
//    | (doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN whereExpression RPAREN)
//    | whereCaseStatement
//    ;
//whereBetweenStatement: whereStatement AND whereStatement;
//whereCaseStatement: CASE whereExpression? whereWhenBranch+ whereElseBranch? END;
//whereWhenBranch: WHEN whereExpression THEN whereExpression;
//whereElseBranch: ELSE whereExpression;
//
//groupBy: (GROUP by=(BY_EN | PO_RU) groupByItems)?;
//groupByItems: groupByExpression (COMMA groupByExpression)*;
//
//groupByExpression:
//      (groupByMember (boolOperation (groupByMember | (LPAREN groupByMember RPAREN)))*)
//    | (LPAREN groupByMember RPAREN (boolOperation (groupByMember | (LPAREN groupByMember RPAREN)))*)
//    ;
//groupByMember:
//      (groupByStatement (compareOperation groupByStatement)?)
//    | (groupByStatement (binaryOperation groupByStatement)+)
//    | (groupByStatement IN LPAREN (inlineSubquery | (groupByStatement (COMMA groupByStatement)*)) RPAREN)
//    | (groupByStatement REFS mdo)
//    | (groupByStatement IS NOT? NULL)
//    ;
//groupByStatement:
//      statement
//    | (LPAREN statement RPAREN)
//    | (doCall=ISNULL LPAREN groupByExpression COMMA groupByExpression RPAREN)
//    | (doCall=DATEADD LPAREN groupByExpression COMMA datePart COMMA groupByExpression RPAREN)
//    | (doCall=DATEDIFF LPAREN groupByExpression COMMA groupByExpression COMMA datePart RPAREN)
//    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN groupByExpression COMMA datePart RPAREN)
//    | (doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN groupByExpression RPAREN)
//    | groupByCaseStatement
//    | groupByCastStatement
//    ;
//groupByCaseStatement: CASE groupByExpression? groupByWhenBranch+ groupByElseBranch? END;
//groupByWhenBranch: WHEN groupByExpression THEN groupByExpression;
//groupByElseBranch: ELSE groupByExpression;
//groupByCastStatement:
//    doCall=CAST LPAREN groupByExpression AS (
//          BOOLEAN
//        | (NUMBER (LPAREN DECIMAL (COMMA DECIMAL)? RPAREN)?)
//        | (STRING (LPAREN DECIMAL RPAREN)?)
//        | DATE
//        | mdo
//        ) RPAREN (DOT id)*;
//
//having: (HAVING havingExpression)?;
//havingExpression:
//      (havingMember (boolOperation (havingMember | (LPAREN havingMember RPAREN)))*)
//    | (LPAREN havingMember RPAREN (boolOperation (havingMember | (LPAREN havingMember RPAREN)))*)
//    ;
//havingMember:
//      (havingStatement (compareOperation havingStatement)?)
//    | (havingStatement (binaryOperation havingStatement)+)
//    ;
//havingStatement:
//      statement
//    | (LPAREN statement RPAREN)
//    | (LPAREN havingExpression RPAREN)
//    | havingAggrMathCallStatement
//    | havingAggrCountCallStatement
//    ;
//havingAggrMathCallStatement: doCall=(SUM | AVG | MIN | MAX) LPAREN havingExpression RPAREN;
//havingAggrCountCallStatement: doCall=COUNT LPAREN (DISTINCT? havingExpression | MUL) RPAREN;
//
//forUpdate:;
//indexing: (INDEX by=(BY_EN | PO_RU) indexingItem (COMMA indexingItem)*)?;
//indexingItem: parameter | column;
//
//ordersAndTotals:
//    (
//    //    (AUTOORDER ordersStatement totalsStatement)
//    //    | (ordersStatement AUTOORDER totalsStatement)
//    //    | (ordersStatement totalsStatement AUTOORDER)
//    //    | (AUTOORDER (ordersStatement | totalsStatement)?)
//    //    | (ordersStatement (AUTOORDER | totalsStatement)?)
//        | (totals AUTOORDER?)
//    )?
//    ;
//
//totals: (TOTALS totalsItems? by=(BY_EN | PO_RU) totalsGroups)?;
//totalsItems: totalsItem (COMMA totalsItem)*;
//totalsItem: totalsItemExpression alias;
//totalsItemExpression:
//      (totalsItemMember (boolOperation (totalsItemMember | (LPAREN totalsItemMember RPAREN)))*)
//    | (LPAREN totalsItemMember RPAREN (boolOperation (totalsItemMember | (LPAREN totalsItemMember RPAREN)))*)
//    ;
//totalsItemMember:
//      (totalsItemStatement (compareOperation totalsItemStatement)?)
//    | (totalsItemStatement (binaryOperation totalsItemStatement)+)
//    ;
//totalsItemStatement:
//      statement
//    | (LPAREN statement RPAREN)
//    | (LPAREN totalsItemExpression RPAREN)
//    | totalsItemAggrMathCallStatement
//    | totalsItemAggrCountCallStatement
//    ;
//totalsItemAggrMathCallStatement: doCall=(SUM | AVG | MIN | MAX) LPAREN totalsItemExpression RPAREN;
//totalsItemAggrCountCallStatement: doCall=COUNT LPAREN (DISTINCT? totalsItemExpression | MUL) RPAREN;
//
//totalsGroups: totalsGroup (COMMA totalsGroup)*;
//totalsGroup:
//    (
//        OVERALL
//        | totalsGroupExpression
//    ) alias
//    ;
//totalsGroupExpression:
//    statement;
//
////totalsStatement:
////totalsItems: totalsItemExpression alias? (COMMA totalsItemExpression alias?)*;
////totalsGroups: totals (COMMA totals)*;
////totals:
////   (
////        OVERALL
////      | (withoutAggregateExpression
////          (
////              (ONLY? (hierarhy=(HIERARCHY_EN | HIERARCHYA_RU)))
////            | (doCall=PERIODS LPAREN datePart (COMMA withoutAggregateExpression?)? (COMMA withoutAggregateExpression?)? RPAREN)
////          )?
////      )
////   )
////   alias?
////   ;
//
//
//// COMMON RULES
//statement   :
//    ((NOT* | MINUS*) column)
//    | ((NOT* | MINUS*) parameter)
//    | (doCall=VALUE LPAREN
//        (
//              (mdo DOT ROUTEPOINT_FIELD DOT IDENTIFIER)     // для точки маршрута бизнес процесса
//            | (id DOT id)                                   // для системного перечисления
//            | (mdo DOT name=id?)                            // может быть просто точка - аналог пустой ссылки
//        ) RPAREN
//      )
//    | (NOT* literal=(TRUE | FALSE | NULL))                  // для булева и null можно только отрицание
//    | (MINUS* literal=(DECIMAL | FLOAT))                    // для чисел возможно можно унарные
//    | (literal=(STR | UNDEFINED))
//    | (doCall=DATETIME LPAREN (parameter | DECIMAL) COMMA (parameter | DECIMAL) COMMA (parameter | DECIMAL)
//        (COMMA (parameter | DECIMAL) COMMA (parameter | DECIMAL) (COMMA (parameter | DECIMAL)))? RPAREN)
//
//    ;
//
////subquery: query union*;
////query:
////    SELECT limitations
////    subqueryFields
////    fromStatement
////    (WHERE whereSearch=withoutAggregateExpression)?
////    (GROUP by=(BY_EN | PO_RU) groupByItems)?
////    (HAVING havingSearch=havingExpression)?
////    (FOR UPDATE mdo?)?
////    ;
////
////subqueryTemparyTable: queryTemparyTable union*;
////queryTemparyTable:
////    SELECT limitations
////    temparyTableFields
////    INTO temparyTableName=id
////    fromStatement
////    (WHERE whereSearch=withoutAggregateExpression)?
////    (GROUP by=(BY_EN | PO_RU) groupByItems)?
////    (HAVING havingSearch=havingExpression)?
////    (FOR UPDATE mdo?)?
////    // TODO придумать как здесь использовать subqueryTemparyTable, но с заппретом INTO
////    ;
////
//
////
////ordersAndTotalsStatement:
////        (AUTOORDER ordersStatement totalsStatement)
////        | (ordersStatement AUTOORDER totalsStatement)
////        | (ordersStatement totalsStatement AUTOORDER)
////        | (AUTOORDER (ordersStatement | totalsStatement)?)
////        | (ordersStatement (AUTOORDER | totalsStatement)?)
////        | (totalsStatement AUTOORDER?)
////        ;
////
////ordersStatement: ORDER by=(BY_EN | PO_RU) expression orderDirection? (COMMA expression orderDirection?)*;
////orderDirection: ASC | DESC | (hierarhy=(HIERARCHY_EN | HIERARCHYA_RU) DESC?);
////
////totalsStatement: TOTALS totalsItems? by=(BY_EN | PO_RU) totalsGroups;
////totalsItems: totalsItemExpression alias? (COMMA totalsItemExpression alias?)*;
////totalsGroups: totals (COMMA totals)*;
////totals:
////   (
////        OVERALL
////      | (withoutAggregateExpression
////          (
////              (ONLY? (hierarhy=(HIERARCHY_EN | HIERARCHYA_RU)))
////            | (doCall=PERIODS LPAREN datePart (COMMA withoutAggregateExpression?)? (COMMA withoutAggregateExpression?)? RPAREN)
////          )?
////      )
////   )
////   alias?
////   ;
////
////
//
//
////
////subqueryFields: subqueryField (COMMA subqueryField)*;
////subqueryField:
////    (
////          (emptyTable=EMPTYTABLE DOT LPAREN emptyTableFields RPAREN)
////        | ((tableName=id DOT)* inlineTable=id DOT LPAREN inlineTableFields RPAREN)
////        | ((tableName=id DOT)* MUL)
////        | expression
////    )
////    alias?
////    ;
////
////temparyTableFields: temparyTableField (COMMA temparyTableField)*;
////temparyTableField:
////    (
////          expression
////        | ((tableName=id DOT)* MUL)
////        | (doCall=RECORDAUTONUMBER LPAREN RPAREN)
////    )
////    alias?
////    ;
////
////emptyTableFields: emptyTableField (COMMA emptyTableField)*;
////emptyTableField: alias?;
////
////inlineTableFields: inlineTableField (COMMA inlineTableField)*;
////inlineTableField: inlineTableExpression alias?;
////
////fromStatement: (FROM dataSources)?;
////
//
////
////groupByItems: withoutAggregateExpression (COMMA withoutAggregateExpression)*;
////
////// EXPRESSIONS
////expression: member (boolOperation member)*;
////member:
////    (leftStatement ((compareOperation | binaryOperation) leftStatement)*)
////    | (leftStatement REFS mdo)
////    | (leftStatement negativeOperation=NOT? LIKE expression ESCAPE escape=STR)
////    | (leftStatement negativeOperation=NOT? IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? ((LPAREN expression (COMMA expression)* RPAREN) | inlineSubquery))
////    | (leftStatement negativeOperation=NOT? BETWEEN betweenStatement)
////    | (leftStatement IS negativeOperation=NOT? literal=NULL)
////    ;
////leftStatement:
////    (negativeOperation=NOT* (LPAREN expression (COMMA expression)+ RPAREN))
////    | ((negativeOperation=NOT | unaryOpertion=MINUS)* ((LPAREN expression RPAREN) | parameter | field))
////    | (negativeOperation=NOT* literal=(TRUE | FALSE | NULL))            // для булева и null можно только отрицание
////    | (unaryOpertion=MINUS* literal=(DECIMAL | FLOAT))                    // для чисел возможно можно унарные
////    | (literal=(STR | UNDEFINED))                                   // другого нельзя
////    | callStatement
////    | aggregateCallStatement
////    | caseStatement
////    ;
////betweenStatement: leftStatement AND leftStatement;
////caseStatement: (negativeOperation=NOT | unaryOpertion=MINUS)* CASE expression? whenBranch+ elseBranch? END;
////whenBranch: WHEN expression THEN expression;
////elseBranch: ELSE expression;
////
////aggregateCallStatement:
////    ((negativeOperation=NOT | unaryOpertion=MINUS)* doCall=(SUM | AVG | MIN | MAX) LPAREN expression RPAREN)
////    | (unaryOpertion=MINUS* doCall=COUNT LPAREN (DISTINCT? expression | MUL) RPAREN)
////    ;
////callStatement:
////         (doCall=DATETIME LPAREN (parameter | DECIMAL) COMMA (parameter | DECIMAL) COMMA (parameter | DECIMAL)
////            (COMMA (parameter | DECIMAL) COMMA (parameter | DECIMAL) (COMMA (parameter | DECIMAL)))? RPAREN)
////        | (doCall=TYPE LPAREN (mdo | type) RPAREN)
////        | (doCall=SUBSTRING LPAREN expression COMMA expression COMMA expression RPAREN)
////        | (doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN expression RPAREN)
////        | (doCall=(VALUETYPE | PRESENTATION | REFPRESENTATION) LPAREN expression RPAREN)
////        | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN expression COMMA datePart RPAREN)
////        | (doCall=DATEADD LPAREN expression COMMA datePart COMMA expression RPAREN)
////        | (doCall=DATEDIFF LPAREN expression COMMA expression COMMA datePart RPAREN)
////        | ((negativeOperation=NOT | unaryOpertion=MINUS)* doCall=ISNULL LPAREN expression COMMA expression RPAREN)
////        | ((negativeOperation=NOT | unaryOpertion=MINUS)* doCall=CAST LPAREN expression AS (
////            BOOLEAN
////            | (NUMBER (LPAREN DECIMAL (COMMA DECIMAL)? RPAREN)?)
////            | (STRING (LPAREN DECIMAL RPAREN)?)
////            | DATE
////            | mdo
////          ) RPAREN (DOT id)*)
////        | (negativeOperation=NOT* doCall=VALUE LPAREN (
////            (mdo DOT ROUTEPOINT_FIELD DOT IDENTIFIER)       // для точки маршрута бизнес процесса
////            | (id DOT id)                                   // для системного перечисления
////            | (mdo DOT fieldName=id?)                       // может быть просто точка - аналог пустой ссылки
////          ) RPAREN)
////        ;
////
////// WITHOUT AGGREGATE EXPRESSION
////// без использования агрегатные ф-ии
////withoutAggregateExpression: withoutAggregateMember (boolOperation withoutAggregateMember)*;
////withoutAggregateMember:
////    withoutLeftStatement
////    | (withoutLeftStatement (binaryOperation withoutLeftStatement)*
////        (compareOperation (withoutLeftStatement (binaryOperation withoutLeftStatement)*)+)?)
////    | (withoutLeftStatement REFS mdo)
////    | (withoutLeftStatement negativeOperation=NOT? LIKE withoutAggregateExpression ESCAPE escape=STR)
////    | (withoutLeftStatement negativeOperation=NOT? IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? ((LPAREN withoutAggregateExpression (COMMA withoutAggregateExpression)* RPAREN) | inlineSubquery))
////    | (withoutLeftStatement negativeOperation=NOT? BETWEEN withoutAggregateBetweenStatement)
////    | (withoutLeftStatement IS negativeOperation=NOT? literal=NULL)
////    ;
////withoutLeftStatement:
////    (negativeOperation=NOT* (LPAREN withoutAggregateExpression (COMMA withoutAggregateExpression)+ RPAREN))
////    | ((negativeOperation=NOT | unaryOpertion=MINUS)* ((LPAREN withoutAggregateExpression RPAREN) | parameter | field))
////    | (negativeOperation=NOT* literal=(TRUE | FALSE | NULL))            // для булева и null можно только отрицание
////    | (unaryOpertion=MINUS* literal=(DECIMAL | FLOAT))                    // для чисел возможно можно унарные
////    | (literal=(STR | UNDEFINED))                                   // другого нельзя
////    | withoutAggregateCallStatement
////    | withoutAggregateCaseStatement
////    ;
////withoutAggregateBetweenStatement: withoutLeftStatement AND withoutLeftStatement;
////withoutAggregateCaseStatement: (negativeOperation=NOT | unaryOpertion=MINUS)* CASE withoutAggregateExpression? withoutAggregateWhenBranch+ withoutAggregateElseBranch? END;
////withoutAggregateWhenBranch: WHEN withoutAggregateExpression THEN withoutAggregateExpression;
////withoutAggregateElseBranch: ELSE withoutAggregateExpression;
////withoutAggregateCallStatement:
////         (doCall=DATETIME LPAREN (parameter | DECIMAL) COMMA (parameter | DECIMAL) COMMA (parameter | DECIMAL)
////            (COMMA (parameter | DECIMAL) COMMA (parameter | DECIMAL) (COMMA (parameter | DECIMAL)))? RPAREN)
////        | (doCall=TYPE LPAREN (mdo | type) RPAREN)
////        | (doCall=SUBSTRING LPAREN withoutAggregateExpression COMMA withoutAggregateExpression COMMA withoutAggregateExpression RPAREN)
////        | (unaryOpertion=MINUS* doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN withoutAggregateExpression RPAREN)
////        | (doCall=(VALUETYPE | PRESENTATION | REFPRESENTATION) LPAREN withoutAggregateExpression RPAREN)
////        | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN withoutAggregateExpression COMMA datePart RPAREN)
////        | (doCall=DATEADD LPAREN withoutAggregateExpression COMMA datePart COMMA withoutAggregateExpression RPAREN)
////        | (doCall=DATEDIFF LPAREN withoutAggregateExpression COMMA withoutAggregateExpression COMMA datePart RPAREN)
////        | ((negativeOperation=NOT | unaryOpertion=MINUS)* doCall=ISNULL LPAREN withoutAggregateExpression COMMA withoutAggregateExpression RPAREN)
////        | ((negativeOperation=NOT | unaryOpertion=MINUS)* doCall=CAST LPAREN withoutAggregateExpression AS (
////            BOOLEAN
////            | (NUMBER (LPAREN DECIMAL (COMMA DECIMAL)? RPAREN)?)
////            | (STRING (LPAREN DECIMAL RPAREN)?)
////            | DATE
////            | mdo
////          ) RPAREN (DOT id)*)
////        | (doCall=VALUE LPAREN (
////            (mdo DOT ROUTEPOINT_FIELD DOT IDENTIFIER)       // для точки маршрута бизнес процесса
////            | (id DOT id)                                   // для системного перечисления
////            | (mdo DOT )                                    // может быть просто точка - аналог пустой ссылки
////            | (mdo DOT fieldName=id)
////          ) RPAREN)
////        ;
////
////// todo нужно придумать, как разделить
////inlineTableExpression: expression;
////virtualTableExpression: expression;
////havingExpression: expression;
////totalsItemExpression: expression;
////
////
//
//
////
//


////
//


//
//// todo
//// 1. [ ] Поля в разных секциях отличаются по правилам, надо сделать для каждого варианта
////  - [x] для блока выборки
////  - [x] для пустой таблицы
////  - [ ] для вложенных таблиц
////  - [ ] для итоги
////  - [x] для Для изменения
////  - [x] для индексировать
//// 2. [ ] Выражения в разных секциях отличаются по правилам, надо сделать для каждого варианта
////  - [x] для блока выборки
////  - [ ] для вложенных таблиц
////  - [x] для соединений
////  - [x] для условий
////  - [x] для упорядочить
////  - [x] для сгруппировать
////  - [ ] для итоги
//// 3. [?] Добавить системные перечисления
//// 4. [?] Добавить сопоставление виртуальных таблиц MDO
//// 5. [x] Пробел между выражением и алиасом должен быть
//// 6. [x] Реализовать многострочные строки - могут быть без | вначале
//// 7. [ ] Оптимизировать скорость парсера
//// 8. [ ] Комментарии в многострочной строке
