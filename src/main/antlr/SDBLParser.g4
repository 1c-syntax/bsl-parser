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
/**
 * @author Maximov Valery <maximovvalery@gmail.com>
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
    | (top ALLOWED)
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
    | (mdo (DOT identifierWithoutTT)+)
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
    | (selectStatement NOT? IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN selectStatement (COMMA selectStatement)*) RPAREN))
    | (LPAREN selectStatement (COMMA selectStatement)+ RPAREN NOT? IN (inlineSubquery | ( LPAREN selectStatement (COMMA selectStatement)*) RPAREN))
    | (NOT+ selectStatement NOT? IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN selectStatement (COMMA selectStatement)*) RPAREN))
    | (NOT+ LPAREN selectStatement (COMMA selectStatement)+ RPAREN NOT? IN (inlineSubquery | ( LPAREN selectStatement (COMMA selectStatement)*) RPAREN))
    | (selectStatement IS NOT? NULL)
    | (selectStatement REFS mdo)
    | (selectStatement NOT? BETWEEN selectBetweenStatement)
    | (selectStatement NOT? LIKE selectStatement (ESCAPE escape=multiString)?)
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
    | (virtualTableStatement NOT? IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN virtualTableStatement (COMMA virtualTableStatement)*) RPAREN))
    | (LPAREN virtualTableStatement (COMMA virtualTableStatement)+ RPAREN NOT? IN (inlineSubquery | (LPAREN virtualTableStatement (COMMA virtualTableStatement)*) RPAREN))
    | (NOT+ virtualTableStatement NOT? IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN virtualTableStatement (COMMA virtualTableStatement)*) RPAREN))
    | (NOT+ LPAREN virtualTableStatement (COMMA virtualTableStatement)+ RPAREN NOT? IN (inlineSubquery | (LPAREN virtualTableStatement (COMMA virtualTableStatement)*) RPAREN))
    | (virtualTableStatement IS NOT? NULL)
    | (virtualTableStatement REFS mdo)
    | (virtualTableStatement NOT? BETWEEN virtualTableBetweenStatement)
    | (virtualTableStatement NOT? LIKE virtualTableStatement (ESCAPE escape=multiString)?)
    ;
joinMember:
      joinStatement
    | joinBinaryStatement
    | joinComparyStatement
    | (joinStatement NOT? IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN joinStatement (COMMA joinStatement)*) RPAREN))
    | (LPAREN joinStatement (COMMA joinStatement)+ RPAREN NOT? IN (inlineSubquery | (LPAREN joinStatement (COMMA joinStatement)*) RPAREN))
    | (NOT+ joinStatement NOT? IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN joinStatement (COMMA joinStatement)*) RPAREN))
    | (NOT+ LPAREN joinStatement (COMMA joinStatement)+ RPAREN NOT? IN (inlineSubquery | (LPAREN joinStatement (COMMA joinStatement)*) RPAREN))
    | (joinStatement IS NOT? NULL)
    | (joinStatement REFS mdo)
    | (joinStatement NOT? BETWEEN joinBetweenStatement)
    | (joinStatement NOT? LIKE joinStatement (ESCAPE escape=multiString)?)
    ;
whereMember:
      whereStatement
    | whereBinaryStatement
    | whereComparyStatement
    | (whereStatement NOT? IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN whereStatement (COMMA whereStatement)*) RPAREN))
    | (LPAREN whereStatement (COMMA whereStatement)+ RPAREN NOT? IN (inlineSubquery | (LPAREN whereStatement (COMMA whereStatement)*) RPAREN))
    | (NOT+ whereStatement NOT? IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN whereStatement (COMMA whereStatement)*) RPAREN))
    | (NOT+ LPAREN whereStatement (COMMA whereStatement)+ RPAREN NOT? IN (inlineSubquery | (LPAREN whereStatement (COMMA whereStatement)*) RPAREN))
    | (whereStatement IS NOT? NULL)
    | (whereStatement REFS mdo)
    | (whereStatement NOT? BETWEEN whereBetweenStatement)
    | (whereStatement NOT? LIKE whereStatement (ESCAPE escape=multiString)?)
    ;
groupByMember:
      groupByStatement
    | groupByBinaryStatement
    | groupByComparyStatement
    | (groupByStatement NOT? IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN groupByStatement (COMMA groupByStatement)*) RPAREN))
    | (LPAREN groupByStatement (COMMA groupByStatement)+ RPAREN NOT? IN (inlineSubquery | (LPAREN groupByStatement (COMMA groupByStatement)*) RPAREN))
    | (NOT+ groupByStatement NOT? IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN groupByStatement (COMMA groupByStatement)*) RPAREN))
    | (NOT+ LPAREN groupByStatement (COMMA groupByStatement)+ RPAREN NOT? IN (inlineSubquery | (LPAREN groupByStatement (COMMA groupByStatement)*) RPAREN))
    | (groupByStatement IS NOT? NULL)
    | (groupByStatement REFS mdo)
    | (groupByStatement NOT? BETWEEN groupByBetweenStatement)
    | (groupByStatement NOT? LIKE groupByStatement (ESCAPE escape=multiString)?)
    ;
havingMember:
      havingStatement
    | havingBinaryStatement
    | havingComparyStatement
    | (havingStatement NOT? IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN havingStatement (COMMA havingStatement)*) RPAREN))
    | (LPAREN havingStatement (COMMA havingStatement)+ RPAREN NOT? IN (inlineSubquery | (LPAREN havingStatement (COMMA havingStatement)*) RPAREN))
    | (NOT+ havingStatement NOT? IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN havingStatement (COMMA havingStatement)*) RPAREN))
    | (NOT+ LPAREN havingStatement (COMMA havingStatement)+ RPAREN NOT? IN (inlineSubquery | (LPAREN havingStatement (COMMA havingStatement)*) RPAREN))
    | (havingStatement IS NOT? NULL)
    | (havingStatement REFS mdo)
    ;
totalsItemMember:
      totalsItemStatement
    | totalsItemBinaryStatement
    | totalsItemComparyStatement
    | (totalsItemStatement NOT? IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN totalsItemStatement (COMMA totalsItemStatement)*) RPAREN))
    | (NOT+ totalsItemStatement NOT? IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN totalsItemStatement (COMMA totalsItemStatement)*) RPAREN))
    | (totalsItemStatement IS NOT? NULL)
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
    | (NOT+ ordersStatement IN (hierarhy=(HIERARCHY_EN | HIERARCHII_RU))? (inlineSubquery | (LPAREN ordersStatement (COMMA ordersStatement)*) RPAREN))
    | (NOT+ LPAREN ordersStatement (COMMA ordersStatement)+ RPAREN IN (inlineSubquery | (LPAREN ordersStatement (COMMA ordersStatement)*) RPAREN))
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
    | ((doCall=ISNULL | (NOT+ doCall=ISNULL) | (MINUS+ doCall=ISNULL)) LPAREN selectExpression COMMA selectExpression RPAREN)
    | (doCall=DATEADD LPAREN selectExpression COMMA datePart COMMA selectExpression RPAREN)
    | (doCall=DATEDIFF LPAREN selectExpression COMMA selectExpression COMMA datePart RPAREN)
    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN selectExpression COMMA datePart RPAREN)
    | (MINUS* doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN selectExpression RPAREN)
    | (doCall=SUBSTRING LPAREN selectExpression COMMA selectExpression COMMA selectExpression RPAREN)
    | (doCall=(VALUETYPE | PRESENTATION | REFPRESENTATION) LPAREN selectExpression RPAREN)
    ;
selectBinaryStatement: selectStatement (binaryOperation selectStatement)+;
selectComparyStatement: (selectBinaryStatement | selectStatement) compareOperation (selectBinaryStatement | selectStatement);
selectCaseStatement: (CASE | (MINUS+ CASE) | (NOT+ CASE)) selectExpression? selectWhenBranch+ selectElseBranch? END;
selectWhenBranch: WHEN selectExpression THEN selectExpression;
selectElseBranch: ELSE selectExpression;
selectAggrMathCallStatement:
    (
          (doCall=(SUM | AVG | MIN | MAX))
        | (MINUS+ doCall=(SUM | AVG | MIN | MAX))
        | (NOT+ doCall=(SUM | AVG | MIN | MAX))
    ) LPAREN selectExpression RPAREN;
selectAggrCountCallStatement: ((doCall=COUNT) | (MINUS+ doCall=COUNT) | (NOT+ doCall=COUNT)) LPAREN (DISTINCT? selectExpression | MUL) RPAREN;
selectCastStatement:
    (doCall=CAST | (NOT+ doCall=CAST) | (MINUS doCall=CAST)) LPAREN selectExpression AS (
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
    | statement
    | ((NOT* | MINUS*) doCall=DATEADD LPAREN inlineTableCaseStatement COMMA datePart COMMA inlineTableCaseStatement RPAREN)
    | (doCall=DATEDIFF LPAREN inlineTableCaseStatement COMMA inlineTableCaseStatement COMMA datePart RPAREN)
    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN inlineTableCaseStatement COMMA datePart RPAREN)
    | (MINUS* doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN inlineTableCaseStatement RPAREN)
    | (doCall=SUBSTRING LPAREN inlineTableExpression COMMA inlineTableExpression COMMA inlineTableExpression RPAREN)
    ;
inlineTableBinaryStatement: inlineTableStatement (binaryOperation inlineTableStatement)+;
inlineTableComparyStatement: (selectBinaryStatement | inlineTableBinaryStatement) compareOperation (inlineTableBinaryStatement | inlineTableStatement);
inlineTableCaseStatement: (CASE | (MINUS+ CASE) | (NOT+ CASE)) inlineTableExpression? inlineTableWhenBranch+ inlineTableElseBranch? END;
inlineTableWhenBranch: WHEN inlineTableExpression THEN inlineTableExpression;
inlineTableElseBranch: ELSE inlineTableExpression;

virtualTableStatement:
      (LPAREN virtualTableExpression RPAREN)
    | (NOT+ LPAREN virtualTableExpression RPAREN)
    | (MINUS+ LPAREN virtualTableExpression RPAREN)
    | statement
    | virtualTableCastStatement
    | virtualTableCaseStatement
    | ((doCall=ISNULL | (NOT+ doCall=ISNULL) | (MINUS+ doCall=ISNULL)) LPAREN virtualTableExpression COMMA virtualTableExpression RPAREN)
    | (doCall=DATEADD LPAREN virtualTableExpression COMMA datePart COMMA virtualTableExpression RPAREN)
    | (doCall=DATEDIFF LPAREN virtualTableExpression COMMA virtualTableExpression COMMA datePart RPAREN)
    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN virtualTableExpression COMMA datePart RPAREN)
    | (MINUS* doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN virtualTableExpression RPAREN)
    | (doCall=SUBSTRING LPAREN virtualTableExpression COMMA virtualTableExpression COMMA virtualTableExpression RPAREN)
    ;
virtualTableBinaryStatement: virtualTableStatement (binaryOperation virtualTableStatement)+;
virtualTableComparyStatement: (virtualTableBinaryStatement | virtualTableStatement) compareOperation (virtualTableBinaryStatement | virtualTableStatement);
virtualTableCaseStatement: (CASE | (MINUS+ CASE) | (NOT+ CASE)) virtualTableExpression? virtualTableWhenBranch+ virtualTableElseBranch? END;
virtualTableWhenBranch: WHEN virtualTableExpression THEN virtualTableExpression;
virtualTableElseBranch: ELSE virtualTableExpression;
virtualTableCastStatement:
    (doCall=CAST | (NOT+ doCall=CAST) | (MINUS doCall=CAST)) LPAREN virtualTableExpression AS (
          BOOLEAN
        | (NUMBER (LPAREN DECIMAL (COMMA DECIMAL)? RPAREN)?)
        | (STRING (LPAREN DECIMAL RPAREN)?)
        | DATE
        | mdo
        ) RPAREN (DOT identifier)*;
virtualTableBetweenStatement: virtualTableExpression AND virtualTableExpression;

joinStatement:
      (LPAREN joinExpression RPAREN)
    | (NOT+ LPAREN joinExpression RPAREN)
    | (MINUS+ LPAREN joinExpression RPAREN)
    | statement
    | joinCastStatement
    | joinCaseStatement
    | ((doCall=ISNULL | (NOT+ doCall=ISNULL) | (MINUS+ doCall=ISNULL)) LPAREN joinExpression COMMA joinExpression RPAREN)
    | (doCall=DATEADD LPAREN joinExpression COMMA datePart COMMA joinExpression RPAREN)
    | (doCall=DATEDIFF LPAREN joinExpression COMMA joinExpression COMMA datePart RPAREN)
    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN joinExpression COMMA datePart RPAREN)
    | (MINUS* doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN joinExpression RPAREN)
    | (doCall=SUBSTRING LPAREN joinExpression COMMA joinExpression COMMA joinExpression RPAREN)
    | (doCall=(VALUETYPE | PRESENTATION | REFPRESENTATION) LPAREN joinExpression RPAREN)
    ;
joinBinaryStatement: joinStatement (binaryOperation joinStatement)+;
joinComparyStatement: (joinBinaryStatement | joinStatement) compareOperation (joinBinaryStatement | joinStatement);
joinCaseStatement: (CASE | (MINUS+ CASE) | (NOT+ CASE)) joinExpression? joinWhenBranch+ joinElseBranch? END;
joinWhenBranch: WHEN joinExpression THEN joinExpression;
joinElseBranch: ELSE joinExpression;
joinCastStatement:
    (doCall=CAST | (NOT+ doCall=CAST) | (MINUS doCall=CAST)) LPAREN joinExpression AS (
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
    | ((doCall=ISNULL | (NOT+ doCall=ISNULL) | (MINUS+ doCall=ISNULL)) LPAREN whereExpression COMMA whereExpression RPAREN)
    | (doCall=DATEADD LPAREN whereExpression COMMA datePart COMMA whereExpression RPAREN)
    | (doCall=DATEDIFF LPAREN whereExpression COMMA whereExpression COMMA datePart RPAREN)
    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN whereExpression COMMA datePart RPAREN)
    | (MINUS* doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN whereExpression RPAREN)
    | (doCall=SUBSTRING LPAREN whereExpression COMMA whereExpression COMMA whereExpression RPAREN)
    ;
whereBinaryStatement: whereStatement (binaryOperation whereStatement)+;
whereComparyStatement: (whereBinaryStatement | whereStatement) compareOperation (whereBinaryStatement | whereStatement);
whereCaseStatement: (CASE | (MINUS+ CASE) | (NOT+ CASE)) whereExpression? whereWhenBranch+ whereElseBranch? END;
whereWhenBranch: WHEN whereExpression THEN whereExpression;
whereElseBranch: ELSE whereExpression;
whereCastStatement:
    (doCall=CAST | (NOT+ doCall=CAST) | (MINUS doCall=CAST)) LPAREN whereExpression AS (
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
    | ((doCall=ISNULL | (NOT+ doCall=ISNULL) | (MINUS+ doCall=ISNULL)) LPAREN groupByExpression COMMA groupByExpression RPAREN)
    | (doCall=DATEADD LPAREN groupByExpression COMMA datePart COMMA groupByExpression RPAREN)
    | (doCall=DATEDIFF LPAREN groupByExpression COMMA groupByExpression COMMA datePart RPAREN)
    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN groupByExpression COMMA datePart RPAREN)
    | (MINUS* doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN groupByExpression RPAREN)
    | (doCall=SUBSTRING LPAREN groupByExpression COMMA groupByExpression COMMA groupByExpression RPAREN)
    | (doCall=(VALUETYPE | PRESENTATION | REFPRESENTATION) LPAREN groupByExpression RPAREN)
    ;
groupByBinaryStatement: groupByStatement (binaryOperation groupByStatement)+;
groupByComparyStatement: (groupByBinaryStatement | groupByStatement) compareOperation (groupByBinaryStatement | groupByStatement);
groupByCaseStatement: (CASE | (MINUS+ CASE) | (NOT+ CASE)) groupByExpression? groupByWhenBranch+ groupByElseBranch? END;
groupByWhenBranch: WHEN groupByExpression THEN groupByExpression;
groupByElseBranch: ELSE groupByExpression;
groupByCastStatement:
    (doCall=CAST | (NOT+ doCall=CAST) | (MINUS doCall=CAST)) LPAREN groupByExpression AS (
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
    | ((doCall=ISNULL | (NOT+ doCall=ISNULL) | (MINUS+ doCall=ISNULL)) LPAREN havingExpression COMMA havingExpression RPAREN)
    | (doCall=DATEADD LPAREN havingExpression COMMA datePart COMMA havingExpression RPAREN)
    | (doCall=DATEDIFF LPAREN havingExpression COMMA havingExpression COMMA datePart RPAREN)
    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN havingExpression COMMA datePart RPAREN)
    | (MINUS* doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN havingExpression RPAREN)
    | (doCall=SUBSTRING LPAREN havingExpression COMMA havingExpression COMMA havingExpression RPAREN)
    ;
havingBinaryStatement: havingStatement (binaryOperation havingStatement)+;
havingComparyStatement: (havingBinaryStatement | havingStatement) compareOperation (havingBinaryStatement | havingStatement);
havingCaseStatement: (CASE | (MINUS+ CASE) | (NOT+ CASE)) havingExpression? havingWhenBranch+ havingElseBranch? END;
havingWhenBranch: WHEN havingExpression THEN havingExpression;
havingElseBranch: ELSE havingExpression;
havingAggrMathCallStatement:
    (
          (doCall=(SUM | AVG | MIN | MAX))
        | (MINUS+ doCall=(SUM | AVG | MIN | MAX))
        | (NOT+ doCall=(SUM | AVG | MIN | MAX))
    ) LPAREN havingExpression RPAREN;
havingAggrCountCallStatement: ((doCall=COUNT) | (MINUS+ doCall=COUNT) | (NOT+ doCall=COUNT)) LPAREN (DISTINCT? havingExpression | MUL) RPAREN;
havingCastStatement:
    (doCall=CAST | (NOT+ doCall=CAST) | (MINUS doCall=CAST)) LPAREN havingExpression AS (
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
    | ((doCall=ISNULL | (NOT+ doCall=ISNULL) | (MINUS+ doCall=ISNULL)) LPAREN totalsItemExpression COMMA totalsItemExpression RPAREN)
    ;
totalsItemBinaryStatement: totalsItemStatement (binaryOperation totalsItemStatement)+;
totalsItemComparyStatement: (totalsItemBinaryStatement | totalsItemStatement) compareOperation (totalsItemBinaryStatement | totalsItemStatement);
totalsItemCaseStatement: (CASE | (MINUS+ CASE) | (NOT+ CASE)) totalsItemExpression? totalsItemWhenBranch+ totalsItemElseBranch? END;
totalsItemWhenBranch: WHEN totalsItemExpression THEN totalsItemExpression;
totalsItemElseBranch: ELSE totalsItemExpression;
totalsItemAggrMathCallStatement:
    (
          (doCall=(SUM | AVG | MIN | MAX))
        | (MINUS+ doCall=(SUM | AVG | MIN | MAX))
        | (NOT+ doCall=(SUM | AVG | MIN | MAX))
    ) LPAREN totalsItemExpression RPAREN;
totalsItemAggrCountCallStatement: ((doCall=COUNT) | (MINUS+ doCall=COUNT) | (NOT+ doCall=COUNT)) LPAREN (DISTINCT? totalsItemExpression | MUL) RPAREN;
totalsItemCastStatement:
    (doCall=CAST | (NOT+ doCall=CAST) | (MINUS doCall=CAST)) LPAREN totalsItemExpression AS (
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
totalsGroupCaseStatement: (CASE | (MINUS+ CASE) | (NOT+ CASE)) totalsGroupExpression? totalsGroupWhenBranch+ totalsGroupElseBranch? END;
totalsGroupWhenBranch: WHEN totalsGroupExpression THEN totalsGroupExpression;
totalsGroupElseBranch: ELSE totalsGroupExpression;

ordersStatement:
    (LPAREN ordersExpression RPAREN)
    | statement
    | ordersAggrMathCallStatement
    | ordersAggrCountCallStatement
    | ordersItemCastStatement
    | ordersCaseStatement
    | ((doCall=ISNULL | (NOT+ doCall=ISNULL) | (MINUS+ doCall=ISNULL)) LPAREN ordersExpression COMMA ordersExpression RPAREN)
    | (doCall=DATEADD LPAREN ordersExpression COMMA datePart COMMA ordersExpression RPAREN)
    | (doCall=DATEDIFF LPAREN ordersExpression COMMA ordersExpression COMMA datePart RPAREN)
    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN ordersExpression COMMA datePart RPAREN)
    | (doCall=SUBSTRING LPAREN ordersExpression COMMA ordersExpression COMMA ordersExpression RPAREN)
    ;
ordersBinaryStatement: ordersStatement (binaryOperation ordersStatement)+;
ordersComparyStatement: (ordersBinaryStatement | ordersStatement) compareOperation (ordersBinaryStatement | ordersStatement);
ordersCaseStatement: (CASE | (MINUS+ CASE) | (NOT+ CASE)) ordersExpression? ordersWhenBranch+ ordersElseBranch? END;
ordersWhenBranch: WHEN ordersExpression THEN ordersExpression;
ordersElseBranch: ELSE ordersExpression;
ordersAggrMathCallStatement:
    (
          (doCall=(SUM | AVG | MIN | MAX))
        | (MINUS+ doCall=(SUM | AVG | MIN | MAX))
        | (NOT+ doCall=(SUM | AVG | MIN | MAX))
    ) LPAREN ordersExpression RPAREN;
ordersAggrCountCallStatement: ((doCall=COUNT) | (MINUS+ doCall=COUNT) | (NOT+ doCall=COUNT)) LPAREN (DISTINCT? ordersExpression | MUL) RPAREN;
ordersItemCastStatement:
    (doCall=CAST | (NOT+ doCall=CAST) | (MINUS doCall=CAST)) LPAREN ordersExpression AS (
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
// для отделения временных таблиц
identifierWithoutTT:
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
      column
    | (NOT+ column)
    | (MINUS+ column)
    | parameter
    | (NOT+ parameter)
    | (MINUS+ parameter)
    | (NOT* literal=(TRUE | FALSE | NULL))
    | (MINUS* literal=(DECIMAL | FLOAT))
    | ((multiString | UNDEFINED))
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

multiString: STR+;

// todo
// [?] Добавить системные перечисления
// [?] Добавить сопоставление виртуальных таблиц MDO
// [ ] Оптимизировать скорость парсера
// [?] Комментарии в многострочной строке
