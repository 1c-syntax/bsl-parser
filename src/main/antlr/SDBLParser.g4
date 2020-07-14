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
// основная структура пакета запросов
queryPackage: queries (SEMICOLON queries)* SEMICOLON? EOF;

// QUERY
// описание элемента пакета
queries: dropTableQuery | selectQuery;

// DROP TABLE
// удаление временной таблицы, где temparyTableName идентификатор временной таблицы
dropTableQuery: DROP temparyTableName=identifier;

// SELECT
// запрос на выборку данных, может состять из подзапроса или подзапроса с временной таблицей
selectQuery: (subquery ordersAndTotals) | (temparyTableSubquery orders? indexing);

// SUBQUERIES
// различные виды подзапросов

// простой подзапрос для выборки данных, состоит из первого запроса (main) и объединений с остальными
subquery: main=query union*;
// объединение запросов
union: UNION all=ALL? query;
// структура запроса
query:
    SELECT limitations
    selectedFields
    from
    where
    groupBy
    having
    forUpdate
    ;
// выбираемые поля
selectedFields: selectedField (COMMA selectedField)*;
selectedField:
    (
          (emptyTable=EMPTYTABLE DOT LPAREN emptyTableFields RPAREN)
        | ((tableName=identifier DOT)* inlineTable=identifier DOT LPAREN inlineTableFields RPAREN)
        | ((tableName=identifier DOT)* MUL)
        | selectExpression
    ) alias
    ;
// поле-пустая таблица
emptyTableFields: emptyTableField (COMMA emptyTableField)*;
// само поле состоит только из алиаса (который вообще может быть пустым)
emptyTableField: alias;
// поле-вложенная таблица (табличная часть)
inlineTableFields: inlineTableField (COMMA inlineTableField)*;
// поле вложенной таблицы
inlineTableField: inlineTableExpression alias;
// источник данных запроса
from: (FROM dataSources)?;

// подзапрос с выборкой данных во временную таблицу, состоит из первого запроса с помещением во временню таблицу (main)
// и объединения с "обычными" запросами
temparyTableSubquery: main=temparyTableMainQuery temparyTableUnion*;
// объединение запросов
temparyTableUnion: UNION all=ALL? temparyTableQuery;
// структура запроса помещения во временную таблицу (основной)
temparyTableMainQuery:
    SELECT limitations
    temparyTableSelectedFields
    into
    temparyTableFrom
    where
    groupBy
    having
    forUpdate
    ;
// структура запроса помещения во временную таблицу (объединение)
temparyTableQuery:
    SELECT limitations
    temparyTableSelectedFields
    temparyTableFrom
    where
    groupBy
    having
    forUpdate
    ;
// выбираемые поля временной таблицы
temparyTableSelectedFields: temparyTableSelectedField (COMMA temparyTableSelectedField)*;
temparyTableSelectedField:
    (
          ((tableName=identifier DOT)* MUL)
        | (doCall=RECORDAUTONUMBER LPAREN RPAREN)
        | selectExpression
    ) alias
    ;
// помещение во временную таблицу
into: INTO temparyTableName=identifier;
// источники данных для временной таблицы
temparyTableFrom: (FROM temparyTableDataSources)?;
// перечень таблиц-источников данных для выборки
temparyTableDataSources: (dataSources | parameterTable) (COMMA dataSources | parameterTable)*;
// таблица как параметр, соединяться ни с чем не может
parameterTable: parameter alias;
// индексирование во временной таблице
indexing: (INDEX by=(BY_EN | PO_RU) indexingItem (COMMA indexingItem)*)?;
// поле индексирования, может быть колонкой или параметром
indexingItem: parameter | column;

// вложенный подзапрос
inlineSubquery: LPAREN subquery orders? RPAREN;

// COMMON FOR QUERIES

// конструкция для изменения, может содержать перечень таблиц, которые необходимо заблокировать для изменения
forUpdate: (FOR UPDATE mdo?)?;

// ограничения выборки, для ускорения анализа развернуты все варианты
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
    | (top ALLOWED | DISTINCT)
    | (top DISTINCT)
    | ((ALLOWED | DISTINCT | top)?)
    ;
// выборка первых N элементов, где count - количество элементов
top: TOP count=DECIMAL+;

// упорядочивание и итоги
ordersAndTotals:
    (
          (AUTOORDER orders totals)
        | (orders AUTOORDER totals)
        | (orders totals AUTOORDER)
        | (AUTOORDER (orders | totals)?)
        | (orders (AUTOORDER | totals)?)
        | (totals AUTOORDER?)
    )?
    ;
// итоги
totals: TOTALS totalsItems? by=(BY_EN | PO_RU) totalsGroups;
totalsItems: totalsItem (COMMA totalsItem)*;
totalsItem: totalsItemExpression alias;
totalsGroups: totalsGroup (COMMA totalsGroup)*;
totalsGroup:
    (
        OVERALL
        | totalsGroupExpression
    ) alias
    ;

// только упорядочивание
orders: ORDER by=(BY_EN | PO_RU) ordersItems;
ordersItems: ordersItem (COMMA ordersItem)*;
ordersItem: ordersExpression orderDirection? alias;
orderDirection: ASC | DESC | (hierarhy=(HIERARCHY_EN | HIERARCHYA_RU) DESC?);

// перечень таблиц-источников данных для выборки
dataSources: dataSource (COMMA dataSource)*;
// варианты источников данных
dataSource:
    (
          (LPAREN dataSource RPAREN)
        | inlineSubquery
        | table
        | virtualTable
    ) alias joinPart*
    ;

// истоник-таблица, может быть временной таблице или таблицей объекта метаданных
table:
      mdo
    | (mdo (DOT tableName=identifier)+)
    | tableName=identifier
    ;
// виртуальная таблица объекта метаданных
virtualTable:
      (mdo DOT virtualTableName (LPAREN virtualTableParameters RPAREN))
    | (mdo DOT virtualTableName)
    | (FILTER_CRITERION_TYPE DOT identifier LPAREN parameter? RPAREN) // для критерия отбора имя ВТ не указывается
    ;
// параметры виртуальной таблицы, могут отсутствовать, могут быть просто запятые, без значений
virtualTableParameters: virtualTableParameter (COMMA virtualTableParameter)*;
virtualTableParameter: virtualTableExpression?;

// соединения таблиц
joinPart:
    (INNER? | ((LEFT | RIGHT | FULL) OUTER?))           // тип соединения
    JOIN dataSource on=(ON_EN | PO_RU) joinExpression   // имя таблицы и соединение
    ;

// условия выборки
where: (WHERE whereExpression)?;

// группировка данных
groupBy: (GROUP by=(BY_EN | PO_RU) groupByItems)?;
groupByItems: groupByExpression (COMMA groupByExpression)*;

// условия на аггрегированные данные
having: (HAVING havingExpression)?;

// EXPRESSIONS
// все виды выражений
selectExpression        : selectMember (boolOperation selectMember)*;
inlineTableExpression   : inlineTableMember (boolOperation inlineTableMember)*;
virtualTableExpression  : virtualTableMember (boolOperation virtualTableMember)*;
joinExpression          : joinMember (boolOperation joinMember)*;
whereExpression         : whereMember (boolOperation whereMember)*;
groupByExpression       : groupByMember (boolOperation groupByMember)*;
havingExpression        : havingMember (boolOperation havingMember)*;
totalsItemExpression    : totalsItemMember (boolOperation totalsItemMember)*;
totalsGroupExpression   : totalsGroupMember (boolOperation totalsGroupMember)*;
ordersExpression        : ordersMember (boolOperation ordersMember)*;

// MEMBERS
// члены выражений
selectMember:
      selectStatement
    | selectBinaryStatement
    | selectComparyStatement
    | (selectStatement IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN selectStatement (COMMA selectStatement)*) RPAREN))
    | (LPAREN selectStatement (COMMA selectStatement)+ RPAREN IN (inlineSubquery | ( LPAREN selectStatement (COMMA selectStatement)*) RPAREN))
    | (selectStatement IS NOT? NULL)
    | (selectStatement REFS mdo)
    | (selectStatement NOT? BETWEEN selectBetweenStatement)
    | (selectStatement NOT? LIKE selectStatement ESCAPE escape=STR)
    ;
inlineTableMember:
      inlineTableStatement
    | inlineTableBinaryStatement
    | inlineTableComparyStatement
    ;
virtualTableMember:
      virtualTableStatement
    | virtualTableBinaryStatement
    | virtualTableComparyStatement
    | (virtualTableStatement IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN virtualTableStatement (COMMA virtualTableStatement)*) RPAREN))
    | (LPAREN virtualTableStatement (COMMA virtualTableStatement)+ RPAREN IN (inlineSubquery | (LPAREN virtualTableStatement (COMMA virtualTableStatement)*) RPAREN))
    | (virtualTableStatement IS NOT? NULL)
    | (virtualTableStatement REFS mdo)
    | (virtualTableStatement NOT? BETWEEN virtualTableBetweenStatement)
    | (virtualTableStatement NOT? LIKE virtualTableStatement ESCAPE escape=STR)
    ;
joinMember:
      joinStatement
    | joinBinaryStatement
    | joinComparyStatement
    | (joinStatement IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN joinStatement (COMMA joinStatement)*) RPAREN))
    | (LPAREN joinStatement (COMMA joinStatement)+ RPAREN IN (inlineSubquery | (LPAREN joinStatement (COMMA joinStatement)*) RPAREN))
    | (joinStatement IS NOT? NULL)
    | (joinStatement REFS mdo)
    | (joinStatement NOT? BETWEEN joinBetweenStatement)
    | (joinStatement NOT? LIKE joinStatement ESCAPE escape=STR)
    ;
whereMember:
      whereStatement
    | whereBinaryStatement
    | whereComparyStatement
    | (whereStatement IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN whereStatement (COMMA whereStatement)*) RPAREN))
    | (LPAREN whereStatement (COMMA whereStatement)+ RPAREN IN (inlineSubquery | (LPAREN whereStatement (COMMA whereStatement)*) RPAREN))
    | (whereStatement IS NOT? NULL)
    | (whereStatement REFS mdo)
    | (whereStatement NOT? BETWEEN whereBetweenStatement)
    | (whereStatement NOT? LIKE whereStatement ESCAPE escape=STR)
    ;
groupByMember:
      groupByStatement
    | groupByBinaryStatement
    | groupByComparyStatement
    | (groupByStatement IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN groupByStatement (COMMA groupByStatement)*) RPAREN))
    | (LPAREN groupByStatement (COMMA groupByStatement)+ RPAREN IN (inlineSubquery | (LPAREN groupByStatement (COMMA groupByStatement)*) RPAREN))
    | (groupByStatement IS NOT? NULL)
    | (groupByStatement REFS mdo)
    | (groupByStatement NOT? BETWEEN groupByBetweenStatement)
    | (groupByStatement NOT? LIKE groupByStatement ESCAPE escape=STR)
    ;
havingMember:
      havingStatement
    | havingBinaryStatement
    | havingComparyStatement
    | (havingStatement IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN havingStatement (COMMA havingStatement)*) RPAREN))
    | (LPAREN havingStatement (COMMA havingStatement)+ RPAREN IN (inlineSubquery | (LPAREN havingStatement (COMMA havingStatement)*) RPAREN))
    | (havingStatement IS NOT? NULL)
    | (havingStatement REFS mdo)
    ;
totalsItemMember:
      totalsItemStatement
    | totalsItemBinaryStatement
    | totalsItemComparyStatement
    ;
totalsGroupMember:
      totalsGroupStatement
    | totalsGroupBinaryStatement
    | totalsGroupComparyStatement
    ;
ordersMember:
      ordersStatement
    | ordersBinaryStatement
    | ordersComparyStatement
    | (ordersStatement IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN ordersStatement (COMMA ordersStatement)*) RPAREN))
    | (LPAREN ordersStatement (COMMA ordersStatement)+ RPAREN IN (inlineSubquery | (LPAREN ordersStatement (COMMA ordersStatement)*) RPAREN))
    ;

// STATEMENTS
// части выражения
selectStatement:
      (LPAREN selectExpression RPAREN)
    | (NOT+ LPAREN selectExpression RPAREN)
    | (MINUS+ LPAREN selectExpression RPAREN)
    | statement
    | selectAggrMathCallStatement
    | selectAggrCountCallStatement
    | selectCastStatement
    | selectCaseStatement
    | ((NOT* | MINUS*) doCall=ISNULL LPAREN selectExpression COMMA selectExpression RPAREN)
    | (doCall=DATEADD LPAREN selectExpression COMMA datePart COMMA selectExpression RPAREN)
    | (doCall=DATEDIFF LPAREN selectExpression COMMA selectExpression COMMA datePart RPAREN)
    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN selectExpression COMMA datePart RPAREN)
    | (MINUS* doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN selectExpression RPAREN)
    | (doCall=SUBSTRING LPAREN selectExpression COMMA selectExpression COMMA selectExpression RPAREN)
    | (doCall=(VALUETYPE | PRESENTATION | REFPRESENTATION) LPAREN selectExpression RPAREN)
    ;
selectBinaryStatement: selectStatement (binaryOperation selectStatement)+;
selectComparyStatement: (selectBinaryStatement | selectStatement) compareOperation (selectBinaryStatement | selectStatement);
selectCaseStatement: (NOT* | MINUS*) CASE selectExpression? selectWhenBranch+ selectElseBranch? END;
selectWhenBranch: WHEN selectExpression THEN selectExpression;
selectElseBranch: ELSE selectExpression;
selectAggrMathCallStatement: doCall=(SUM | AVG | MIN | MAX) LPAREN selectExpression RPAREN;
selectAggrCountCallStatement: doCall=COUNT LPAREN (DISTINCT? selectExpression | MUL) RPAREN;
selectCastStatement:
    (NOT* | MINUS*) doCall=CAST LPAREN selectExpression AS (
          BOOLEAN
        | (NUMBER (LPAREN DECIMAL (COMMA DECIMAL)? RPAREN)?)
        | (STRING (LPAREN DECIMAL RPAREN)?)
        | DATE
        | mdo
        ) RPAREN (DOT identifier)*;
selectBetweenStatement: selectExpression AND selectExpression;

inlineTableStatement:
      (LPAREN inlineTableExpression RPAREN)
    | (NOT+ LPAREN inlineTableExpression RPAREN)
    | (MINUS+ LPAREN inlineTableExpression RPAREN)
    | inlineTableCaseStatement
    | ((NOT* | MINUS*) doCall=DATEADD LPAREN inlineTableCaseStatement COMMA datePart COMMA inlineTableCaseStatement RPAREN)
    | (doCall=DATEDIFF LPAREN inlineTableCaseStatement COMMA inlineTableCaseStatement COMMA datePart RPAREN)
    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN inlineTableCaseStatement COMMA datePart RPAREN)
    | (MINUS* doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN inlineTableCaseStatement RPAREN)
    | (doCall=SUBSTRING LPAREN inlineTableExpression COMMA inlineTableExpression COMMA inlineTableExpression RPAREN)
    ;
inlineTableBinaryStatement: inlineTableStatement (binaryOperation inlineTableStatement)+;
inlineTableComparyStatement: (selectBinaryStatement | inlineTableBinaryStatement) compareOperation (inlineTableBinaryStatement | inlineTableStatement);
inlineTableCaseStatement: (NOT* | MINUS*) CASE inlineTableExpression? inlineTableWhenBranch+ inlineTableElseBranch? END;
inlineTableWhenBranch: WHEN inlineTableExpression THEN inlineTableExpression;
inlineTableElseBranch: ELSE inlineTableExpression;

virtualTableStatement:
      (LPAREN virtualTableExpression RPAREN)
    | (NOT+ LPAREN virtualTableExpression RPAREN)
    | (MINUS+ LPAREN virtualTableExpression RPAREN)
    | statement
    | virtualTableCaseStatement
    | ((NOT* | MINUS*) doCall=ISNULL LPAREN virtualTableExpression COMMA virtualTableExpression RPAREN)
    | (doCall=DATEADD LPAREN virtualTableExpression COMMA datePart COMMA virtualTableExpression RPAREN)
    | (doCall=DATEDIFF LPAREN virtualTableExpression COMMA virtualTableExpression COMMA datePart RPAREN)
    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN virtualTableExpression COMMA datePart RPAREN)
    | (MINUS* doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN virtualTableExpression RPAREN)
    | (doCall=SUBSTRING LPAREN virtualTableExpression COMMA virtualTableExpression COMMA virtualTableExpression RPAREN)
    ;
virtualTableBinaryStatement: virtualTableStatement (binaryOperation virtualTableStatement)+;
virtualTableComparyStatement: (virtualTableBinaryStatement | virtualTableStatement) compareOperation (virtualTableBinaryStatement | virtualTableStatement);
virtualTableCaseStatement: (NOT* | MINUS*) CASE virtualTableExpression? virtualTableWhenBranch+ virtualTableElseBranch? END;
virtualTableWhenBranch: WHEN virtualTableExpression THEN virtualTableExpression;
virtualTableElseBranch: ELSE virtualTableExpression;
virtualTableBetweenStatement: virtualTableExpression AND virtualTableExpression;

joinStatement:
      (LPAREN joinExpression RPAREN)
    | (NOT+ LPAREN joinExpression RPAREN)
    | (MINUS+ LPAREN joinExpression RPAREN)
    | statement
    | joinCastStatement
    | joinCaseStatement
    | ((NOT* | MINUS*) doCall=ISNULL LPAREN joinExpression COMMA joinExpression RPAREN)
    | (doCall=DATEADD LPAREN joinExpression COMMA datePart COMMA joinExpression RPAREN)
    | (doCall=DATEDIFF LPAREN joinExpression COMMA joinExpression COMMA datePart RPAREN)
    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN joinExpression COMMA datePart RPAREN)
    | (MINUS* doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN joinExpression RPAREN)
    | (doCall=SUBSTRING LPAREN joinExpression COMMA joinExpression COMMA joinExpression RPAREN)
    ;
joinBinaryStatement: joinStatement (binaryOperation joinStatement)+;
joinComparyStatement: (joinBinaryStatement | joinStatement) compareOperation (joinBinaryStatement | joinStatement);
joinCaseStatement: (NOT* | MINUS*) CASE joinExpression? joinWhenBranch+ joinElseBranch? END;
joinWhenBranch: WHEN joinExpression THEN joinExpression;
joinElseBranch: ELSE joinExpression;
joinCastStatement:
    (NOT* | MINUS*) doCall=CAST LPAREN joinExpression AS (
          BOOLEAN
        | (NUMBER (LPAREN DECIMAL (COMMA DECIMAL)? RPAREN)?)
        | (STRING (LPAREN DECIMAL RPAREN)?)
        | DATE
        | mdo
        ) RPAREN (DOT identifier)*;
joinBetweenStatement: joinExpression AND joinExpression;

whereStatement:
      (LPAREN whereExpression RPAREN)
    | (NOT+ LPAREN whereExpression RPAREN)
    | (MINUS+ LPAREN whereExpression RPAREN)
    | statement
    | whereCastStatement
    | whereCaseStatement
    | ((NOT* | MINUS*) doCall=ISNULL LPAREN whereExpression COMMA whereExpression RPAREN)
    | (doCall=DATEADD LPAREN whereExpression COMMA datePart COMMA whereExpression RPAREN)
    | (doCall=DATEDIFF LPAREN whereExpression COMMA whereExpression COMMA datePart RPAREN)
    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN whereExpression COMMA datePart RPAREN)
    | (MINUS* doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN whereExpression RPAREN)
    | (doCall=SUBSTRING LPAREN whereExpression COMMA whereExpression COMMA whereExpression RPAREN)
    ;
whereBinaryStatement: whereStatement (binaryOperation whereStatement)+;
whereComparyStatement: (whereBinaryStatement | whereStatement) compareOperation (whereBinaryStatement | whereStatement);
whereCaseStatement: (NOT* | MINUS*) CASE whereExpression? whereWhenBranch+ whereElseBranch? END;
whereWhenBranch: WHEN whereExpression THEN whereExpression;
whereElseBranch: ELSE whereExpression;
whereCastStatement:
    (NOT* | MINUS*) doCall=CAST LPAREN whereExpression AS (
          BOOLEAN
        | (NUMBER (LPAREN DECIMAL (COMMA DECIMAL)? RPAREN)?)
        | (STRING (LPAREN DECIMAL RPAREN)?)
        | DATE
        | mdo
        ) RPAREN (DOT identifier)*;
whereBetweenStatement: whereExpression AND whereExpression;

groupByStatement:
      (LPAREN groupByExpression RPAREN)
    | (NOT+ LPAREN groupByExpression RPAREN)
    | (MINUS+ LPAREN groupByExpression RPAREN)
    | statement
    | groupByCastStatement
    | groupByCaseStatement
    | ((NOT* | MINUS*) doCall=ISNULL LPAREN groupByExpression COMMA groupByExpression RPAREN)
    | (doCall=DATEADD LPAREN groupByExpression COMMA datePart COMMA groupByExpression RPAREN)
    | (doCall=DATEDIFF LPAREN groupByExpression COMMA groupByExpression COMMA datePart RPAREN)
    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN groupByExpression COMMA datePart RPAREN)
    | (MINUS* doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN groupByExpression RPAREN)
    | (doCall=SUBSTRING LPAREN groupByExpression COMMA groupByExpression COMMA groupByExpression RPAREN)
    | (doCall=(VALUETYPE | PRESENTATION | REFPRESENTATION) LPAREN groupByExpression RPAREN)
    ;
groupByBinaryStatement: groupByStatement (binaryOperation groupByStatement)+;
groupByComparyStatement: (groupByBinaryStatement | groupByStatement) compareOperation (groupByBinaryStatement | groupByStatement);
groupByCaseStatement: (NOT* | MINUS*) CASE groupByExpression? groupByWhenBranch+ groupByElseBranch? END;
groupByWhenBranch: WHEN groupByExpression THEN groupByExpression;
groupByElseBranch: ELSE groupByExpression;
groupByCastStatement:
    (NOT* | MINUS*) doCall=CAST LPAREN groupByExpression AS (
          BOOLEAN
        | (NUMBER (LPAREN DECIMAL (COMMA DECIMAL)? RPAREN)?)
        | (STRING (LPAREN DECIMAL RPAREN)?)
        | DATE
        | mdo
        ) RPAREN (DOT identifier)*;
groupByBetweenStatement: groupByExpression AND groupByExpression;

havingStatement:
      (LPAREN havingExpression RPAREN)
    | (NOT+ LPAREN havingExpression RPAREN)
    | (MINUS+ LPAREN havingExpression RPAREN)
    | statement
    | havingAggrMathCallStatement
    | havingAggrCountCallStatement
    | havingCastStatement
    | havingCaseStatement
    | ((NOT* | MINUS*) doCall=ISNULL LPAREN havingExpression COMMA havingExpression RPAREN)
    | (doCall=DATEADD LPAREN havingExpression COMMA datePart COMMA havingExpression RPAREN)
    | (doCall=DATEDIFF LPAREN havingExpression COMMA havingExpression COMMA datePart RPAREN)
    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN havingExpression COMMA datePart RPAREN)
    | (MINUS* doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN havingExpression RPAREN)
    | (doCall=SUBSTRING LPAREN havingExpression COMMA havingExpression COMMA havingExpression RPAREN)
    ;
havingBinaryStatement: havingStatement (binaryOperation havingStatement)+;
havingComparyStatement: (havingBinaryStatement | havingStatement) compareOperation (havingBinaryStatement | havingStatement);
havingCaseStatement: (NOT* | MINUS*) CASE havingExpression? havingWhenBranch+ havingElseBranch? END;
havingWhenBranch: WHEN havingExpression THEN havingExpression;
havingElseBranch: ELSE havingExpression;
havingAggrMathCallStatement: (NOT* | MINUS*) doCall=(SUM | AVG | MIN | MAX) LPAREN havingExpression RPAREN;
havingAggrCountCallStatement: MINUS* doCall=COUNT LPAREN (DISTINCT? havingExpression | MUL) RPAREN;
havingCastStatement:
    (NOT* | MINUS*) doCall=CAST LPAREN havingExpression AS (
          BOOLEAN
        | (NUMBER (LPAREN DECIMAL (COMMA DECIMAL)? RPAREN)?)
        | (STRING (LPAREN DECIMAL RPAREN)?)
        | DATE
        | mdo
        ) RPAREN (DOT identifier)*;

totalsItemStatement:
    (LPAREN totalsItemExpression RPAREN)
    | statement
    | totalsItemAggrMathCallStatement
    | totalsItemAggrCountCallStatement
    | totalsItemCastStatement
    | totalsItemCaseStatement
    | ((NOT* | MINUS*) doCall=ISNULL LPAREN totalsItemExpression COMMA totalsItemExpression RPAREN)
    ;
totalsItemBinaryStatement: totalsItemStatement (binaryOperation totalsItemStatement)+;
totalsItemComparyStatement: (totalsItemBinaryStatement | totalsItemStatement) compareOperation (totalsItemBinaryStatement | totalsItemStatement);
totalsItemCaseStatement: (NOT* | MINUS*) CASE totalsItemExpression? totalsItemWhenBranch+ totalsItemElseBranch? END;
totalsItemWhenBranch: WHEN totalsItemExpression THEN totalsItemExpression;
totalsItemElseBranch: ELSE totalsItemExpression;
totalsItemAggrMathCallStatement: doCall=(SUM | AVG | MIN | MAX) LPAREN totalsItemExpression RPAREN;
totalsItemAggrCountCallStatement: doCall=COUNT LPAREN (DISTINCT? totalsItemExpression | MUL) RPAREN;
totalsItemCastStatement:
    (NOT* | MINUS*) doCall=CAST LPAREN totalsItemExpression AS (
          BOOLEAN
        | (NUMBER (LPAREN DECIMAL (COMMA DECIMAL)? RPAREN)?)
        | (STRING (LPAREN DECIMAL RPAREN)?)
        | DATE
        | mdo
        ) RPAREN (DOT identifier)*;

totalsGroupStatement:
    (LPAREN totalsGroupExpression RPAREN)
    | statement
    | totalsGroupCaseStatement
    ;
totalsGroupBinaryStatement: totalsGroupStatement (binaryOperation totalsGroupStatement)+;
totalsGroupComparyStatement: (totalsGroupBinaryStatement | totalsGroupStatement) compareOperation (totalsGroupBinaryStatement | totalsGroupStatement);
totalsGroupCaseStatement: (NOT* | MINUS*) CASE totalsGroupExpression? totalsGroupWhenBranch+ totalsGroupElseBranch? END;
totalsGroupWhenBranch: WHEN totalsGroupExpression THEN totalsGroupExpression;
totalsGroupElseBranch: ELSE totalsGroupExpression;

ordersStatement:
    (LPAREN ordersExpression RPAREN)
    | statement
    | ordersAggrMathCallStatement
    | ordersAggrCountCallStatement
    | ordersItemCastStatement
    | ordersCaseStatement
    | ((NOT* | MINUS*) doCall=ISNULL LPAREN ordersExpression COMMA ordersExpression RPAREN)
    | (doCall=DATEADD LPAREN ordersExpression COMMA datePart COMMA ordersExpression RPAREN)
    | (doCall=DATEDIFF LPAREN ordersExpression COMMA ordersExpression COMMA datePart RPAREN)
    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN ordersExpression COMMA datePart RPAREN)
    | (doCall=SUBSTRING LPAREN ordersExpression COMMA ordersExpression COMMA ordersExpression RPAREN)
    ;
ordersBinaryStatement: ordersStatement (binaryOperation ordersStatement)+;
ordersComparyStatement: (ordersBinaryStatement | ordersStatement) compareOperation (ordersBinaryStatement | ordersStatement);
ordersCaseStatement: (NOT* | MINUS*) CASE ordersExpression? ordersWhenBranch+ ordersElseBranch? END;
ordersWhenBranch: WHEN ordersExpression THEN ordersExpression;
ordersElseBranch: ELSE ordersExpression;
ordersAggrMathCallStatement: doCall=(SUM | AVG | MIN | MAX) LPAREN ordersExpression RPAREN;
ordersAggrCountCallStatement: doCall=COUNT LPAREN (DISTINCT? ordersExpression | MUL) RPAREN;
ordersItemCastStatement:
    (NOT* | MINUS*) doCall=CAST LPAREN ordersExpression AS (
          BOOLEAN
        | (NUMBER (LPAREN DECIMAL (COMMA DECIMAL)? RPAREN)?)
        | (STRING (LPAREN DECIMAL RPAREN)?)
        | DATE
        | mdo
        ) RPAREN (DOT identifier)*;

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

// части выражения, используемые везде
statement:
    ((NOT* | MINUS*) column)
    | ((NOT* | MINUS*) parameter)
    | (NOT* literal=(TRUE | FALSE | NULL))
    | (MINUS* literal=(DECIMAL | FLOAT))
    | (literal=(STR | UNDEFINED))
    | (doCall=DATETIME LPAREN
                                          (parameter | DECIMAL) COMMA (parameter | DECIMAL) COMMA (parameter | DECIMAL)
       /* эта часть может быть опущена */ (COMMA (parameter | DECIMAL) COMMA (parameter | DECIMAL) (COMMA (parameter | DECIMAL)))?
       RPAREN
      )
    | (doCall=VALUE LPAREN
        (
              (mdo DOT ROUTEPOINT_FIELD DOT IDENTIFIER)     // для точки маршрута бизнес процесса
            | (identifier DOT identifier)                   // для системного перечисления
            | (mdo DOT name=identifier?)                    // может быть просто точка - аналог пустой ссылки
        ) RPAREN
      )
    | (doCall=TYPE LPAREN (mdo | type) RPAREN)
;








//selectExpression: selectMember (boolOperation selectMember)*;
//selectMember:
//    |
//    |

//selectStatement:


//
//    | selectCastStatement
//    | selectAggrMathCallStatement
//    | selectAggrCountCallStatement
//    | selectCaseStatement




//

//


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
