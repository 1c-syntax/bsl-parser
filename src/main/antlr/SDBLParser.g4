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
// основная структура пакета запросов:
// Пакет состоит из запросов (мин 1) разделенных ; и в конце тоже допустима ;
queryPackage: queries (SEMICOLON queries)* SEMICOLON? EOF;

// QUERY
// описание элемента пакета
// Запрос может состоять из выборки (с или без сохранения во временную) либо удаления временной таблицы
queries: selectQuery | dropTableQuery;

// DROP TABLE
// удаление временной таблицы, где temporaryTableName идентификатор временной таблицы
dropTableQuery: DROP temporaryTableName=identifier;

// SELECT
// запрос на выборку данных
// Состоит из запроса на выборку (пакета) итогового упорядочивания и итогов
selectQuery:
    subquery
    (
          (autoorder=AUTOORDER orders=orderBy totals=totalBy)
        | (orders=orderBy autoorder=AUTOORDER totals=totalBy)
        | (orders=orderBy totals=totalBy autoorder=AUTOORDER)
        | (AUTOORDER (orders=orderBy | totals=totalBy)?)
        | (orders=orderBy (autoorder=AUTOORDER | totals=totalBy)?)
        | (totals=totalBy autoorder=AUTOORDER?)
    )?
    ;

// SUBQUERIES
// Основная часть запроса
// Состоит из основного запроса и объединения.
// Основной запрос может быть простым запросом для выборки данных И НЕ МОЖЕТ быть выбокой во временную таблицу
subquery:
      main=query
    | main=query orderBy? unions+=union+
    ;

// объединение запросов
union: (UNION | UNION_ALL) query orderBy?;

// структура запроса
query:
    SELECT limitations?
    columns=selectedFields
    (INTO temporaryTableName=identifier)?
    (FROM from=dataSources)?
    (WHERE where=searchConditions)?
    (GROUP_BY groupBy=groupByItem)?
    (HAVING having=searchConditions)?
    (FOR_UPDATE forUpdate=mdo?)?
    (INDEX_BY indexes+=indexingItem (COMMA indexes+=indexingItem)*)?
    ;

// различные ограничения выборки, для ускорения анализа развернуты все варианты
limitations:
     ((top | DISTINCT | ALLOWED))
    | (ALLOWED DISTINCT top)
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
    ;

// Ограничение количества элементов выборки
top: TOP count=DECIMAL;

// поля выборки
selectedFields: fields+=selectedField (COMMA fields+=selectedField)*;
selectedField:
      asterisk
    | columnField
    | emptyTableField
    | inlineTableField
    | expressionField
    ;

// поле выборки-звездочка, либо имя таблицы.* либо просто *. Алиаса не бывает
asterisk: (tableName=identifier DOT)* MUL;

// поле выборки-выражение, алиас может быть
expressionField: expression alias?;

// поле выборки-поле табицы или NULL
columnField:
    (
    NULL
    | recordAutoNumberFunction
    ) alias?;

// поле выборки-пустая таблица
emptyTableField: emptyTable=EMPTYTABLE DOT LPAREN emptyTableColumns RPAREN alias?;
emptyTableColumns: columns+=alias (COMMA columns+=alias)*;

// поле выборки-табличная часть
inlineTableField: inlineTable=column DOT LPAREN inlineTableFields=selectedFields RPAREN alias?;

// функция автономерзаписи может быть использована только как поле выборки
recordAutoNumberFunction: doCall=RECORDAUTONUMBER LPAREN RPAREN;

groupByItem:
    GROUPING_SET LPAREN (LPAREN groupingSet+=expressionList RPAREN (COMMA LPAREN groupingSet+=expressionList RPAREN)*) RPAREN
    | (groupBy+=expression (COMMA groupBy+=expression)*)
    ;

// поле индексирования, может быть колонкой или параметром
indexingItem: parameter | column;

// упорядочивание
orderBy: ORDER_BY orders+=ordersByExpession (COMMA orders+=ordersByExpession)?;
ordersByExpession: expression (direction=(ASC | DESC) | (hierarchy=HIERARCHY direction=DESC?))?;

// итоги
totalBy: TOTALS selectedFields? (BY_EN | PO_RU) totalsGroups+=totalsGroup (COMMA totalsGroups+=totalsGroup)*;
totalsGroup:
      OVERALL
    | (expression ((ONLY? HIERARCHY) | periodic)? alias?)
    ;
// периодичность группы итогов
periodic: PERIODS
    LPAREN
        periodType=(SECOND | MINUTE | HOUR | DAY | WEEK | MONTH | QUARTER | YEAR | TENDAYS | HALFYEAR)
        (COMMA first=expression)? (COMMA second=expression)?
    RPAREN
    ;

// поля-колонки
column:
      mdoName=identifier (DOT columnNames+=identifier)+
    | columnNames+=identifier
    | mdo (DOT columnNames+=identifier)+
    ;

// EXPRESSION
// Выражения
expression:
      primitiveExpression
    | functionCall
    | caseExpression
    | column
    | bracketExpression
    | unaryExpression
    | expression binaryOperation=(MUL | QUOTIENT | PLUS | MINUS) expression
    ;

// Примитивные выражения
primitiveExpression:
      NULL
    | UNDEFINED
    | multiString
    | DECIMAL
    | FLOAT
    | booleanValue=(TRUE | FALSE)
    | (DATETIME LPAREN
            year=datePart COMMA month=datePart COMMA day=datePart
            /* эта часть может быть опущена */ (COMMA
            hour=datePart COMMA minute=datePart COMMA second=datePart)?
       RPAREN)
    | parameter
    | (TYPE LPAREN (mdo | STRING | BOOLEAN | DATE | NUMBER) RPAREN)
    ;

// условные выражения (если...то...иначе)
caseExpression:
      (CASE caseExp=expression caseBranch+ (ELSE elseExp=searchConditions)? END)
    | (CASE caseBranch+ (ELSE elseExp=searchConditions)? END)
    | (caseBranch (ELSE elseExp=searchConditions)? END)
    ;

// ветка со своим условием и результатом
caseBranch: WHEN searchConditions THEN searchConditions;

// выражение в скобках
// в скобках может быть либо подзапрос либо другое выражение
bracketExpression: (LPAREN expression RPAREN) | (LPAREN subquery RPAREN);

// выражение с унарной операцией
unaryExpression: sign expression;

// вызов встроенных ф-ий
functionCall:
      aggregateFunctions
    | builtInFunctions
    | valueFunction
    | (castFunction (DOT columnNames+=identifier)*)
;

// встроенные функции
builtInFunctions:
      (doCall=SUBSTRING LPAREN string=expression COMMA charNo=expression COMMA count=expression RPAREN)
    | (doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN date=expression RPAREN)
    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN date=expression COMMA periodType=(MINUTE | HOUR | DAY | WEEK | MONTH | QUARTER | YEAR | TENDAYS | HALFYEAR) RPAREN)
    | (doCall=DATEADD LPAREN date=expression COMMA periodType=(SECOND | MINUTE | HOUR | DAY | WEEK | MONTH | QUARTER | YEAR | TENDAYS | HALFYEAR) COMMA count=expression RPAREN)
    | (doCall=DATEDIFF LPAREN first=expression COMMA second=expression COMMA periodType=(SECOND | MINUTE | HOUR | DAY | MONTH | QUARTER | YEAR) RPAREN)
    | (doCall=(VALUETYPE | PRESENTATION | REFPRESENTATION | GROUPEDBY) LPAREN value=expression RPAREN)
    | (doCall=ISNULL LPAREN first=expression COMMA second=expression RPAREN)
;

// агрегатные ф-ии
aggregateFunctions:
      (doCall=(SUM | AVG | MIN | MAX) LPAREN expression RPAREN)
    | (doCall=COUNT LPAREN (DISTINCT? expression | MUL) RPAREN)
;

// функция Значение
valueFunction: doCall=VALUE LPAREN
    (
          (type=(BUSINESS_PROCESS_TYPE
                           | CATALOG_TYPE
                           | DOCUMENT_TYPE
                           | FILTER_CRITERION_TYPE
                           | EXCHANGE_PLAN_TYPE
                           | ENUM_TYPE
                           | CHART_OF_CHARACTERISTIC_TYPES_TYPE
                           | CHART_OF_ACCOUNTS_TYPE
                           | CHART_OF_CALCULATION_TYPES_TYPE
                           | TASK_TYPE
                           | EXTERNAL_DATA_SOURCE_TYPE)
                       DOT mdoName=identifier DOT emptyFer=EMPTYREF)
        | (type=(CATALOG_TYPE
                            | ENUM_TYPE
                            | CHART_OF_CHARACTERISTIC_TYPES_TYPE
                            | CHART_OF_ACCOUNTS_TYPE
                            | CHART_OF_CALCULATION_TYPES_TYPE)
                       DOT mdoName=identifier DOT predefinedName=identifier)
                                                                                // для точки маршрута бизнес процесса
        | (type=BUSINESS_PROCESS_TYPE DOT mdoName=identifier DOT ROUTEPOINT_FIELD DOT routePointName=identifier)
        | (systemName=identifier DOT predefinedName=identifier)                 // для системного перечисления
        | (mdo DOT)                                                             // может быть просто точка - аналог пустой ссылки
    ) RPAREN
    ;

castFunction:
    (doCall=CAST LPAREN value=expression AS (
              type=BOOLEAN
            | (type=NUMBER (LPAREN len=DECIMAL (COMMA prec=DECIMAL)? RPAREN)?)
            | (type=STRING (LPAREN len=DECIMAL RPAREN)?)
            | type=DATE
            | mdo
      ) RPAREN)
   ;

// выражения-условия отбора
searchConditions:
      condidions+=searchCondition
      ((AND | OR) condidions+=searchCondition)*
    ;
searchCondition: NOT* (predicate | (LPAREN searchConditions RPAREN));

// логическое выражение
predicate:
                                                                        // сравнение выражений
      (expression compareOperation=(LESS | LESS_OR_EQUAL | GREATER | GREATER_OR_EQUAL | ASSIGN | NOT_EQUAL) expression)
    | (expression BETWEEN expression AND expression)                    // выражение МЕЖДУ выражение1 И выражение2
    | (expression NOT* (IN | IN_HIERARCHY) LPAREN (subquery | expressionList) RPAREN)    // выражение В (подзапрос/список)
    | (expression NOT* LIKE expression (ESCAPE escape=multiString)?)    // выражение подобно выражение [ESC-последовательность]
    | (expression IS NOT? NULL)                                         // выражение ЕСТЬ NULL / ЕСТЬ НЕ NULL
    | (expression REFS mdo)                                             // выражение ССЫЛКА МДО
    | expression                                                        // булево выражение
    ;

// список выражений
expressionList: exp+=expression (COMMA exp+=expression)*;

// перечень таблиц-источников данных для выборки
dataSources: tables+=dataSource (COMMA tables+=dataSource)*;

// варианты источников данных
dataSource:
      (LPAREN dataSource RPAREN)
    | (tableSource joins+=joinPart*)
    | (LPAREN tableSource RPAREN joins+=joinPart*)
    ;

tableSource:
      (table alias?)
    | (virtualTable alias?)
    | (parameterTable alias?)
    | (LPAREN subquery RPAREN alias?)
    ;

// источник-физическая таблица либо ВТ
table:
      mdo
    | mdo DOT objectTableName=identifier
    | tableName=identifier
    ;

// источник-виртуальная таблица
virtualTable:
     (mdo DOT virtualTableName=(
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
        ) (LPAREN virtualTableParameters+=searchConditions (COMMA virtualTableParameters+=searchConditions)* RPAREN)?)
    | (type=FILTER_CRITERION_TYPE DOT tableName=identifier LPAREN parameter? RPAREN) // для критерия отбора имя ВТ не указывается
    ;

// таблица как параметр, соединяться ни с чем не может
parameterTable: parameter;

// соединения таблиц
joinPart:
    joinType=(INNER_JOIN | LEFT_JOIN | RIGHT_JOIN | FULL_JOIN | JOIN)    // тип соединения
    source=dataSource (ON_EN | PO_RU) condition=searchConditions          // имя таблицы и соединение
    ;

// алиас для поля, таблицы ...
alias: AS? name=identifier;

// состав даты
datePart: (parameter | DECIMAL) ;

// Строки
multiString: STR+;

// Унарные минус и плюс
sign: MINUS | PLUS;

// возможные идентификаторы
identifier:
      IDENTIFIER // просто идентификатор объекта
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
    | DROP
    | END
    | ISNULL
    | JOIN
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
    | VALUE
    | VALUETYPE
    | WEEK
    | WEEKDAY
    | YEAR
;

// параметр запроса
parameter: AMPERSAND name=PARAMETER_IDENTIFIER;

// полное имя объекта метаданных, где tableName - имя прикладного объекта
mdo: type=(
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
     ) DOT tableName=identifier
;
