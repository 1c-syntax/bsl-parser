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
// удаление временной таблицы, где temporaryTableName идентификатор временной таблицы
dropTableQuery: DROP temporaryTableName=identifier;

// SELECT
// запрос на выборку данных, может состять из подзапроса или подзапроса с временной таблицей
selectQuery: (subquery ordersAndTotals) | (temporaryTableSubquery orders? indexing);

// SUBQUERIES
// различные виды подзапросов

// простой подзапрос для выборки данных, состоит из первого запроса (main) и объединений с остальными
subquery: main=query union*;
// объединение запросов
union: (UNION | UNION_ALL) query;
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
        | expression
    ) alias
    ;
// поле-пустая таблица
emptyTableFields: emptyTableField (COMMA emptyTableField)*;
// само поле состоит только из алиаса (который вообще может быть пустым)
emptyTableField: alias;
// поле-вложенная таблица (табличная часть)
inlineTableFields: inlineTableField (COMMA inlineTableField)*;
// поле вложенной таблицы
inlineTableField: expression alias;
// источник данных запроса
from: (FROM dataSources)?;

// подзапрос с выборкой данных во временную таблицу, состоит из первого запроса с помещением во временню таблицу (main)
// и объединения с "обычными" запросами
temporaryTableSubquery: main=temporaryTableMainQuery temporaryTableUnion*;
// объединение запросов
temporaryTableUnion: (UNION | UNION_ALL) temporaryTableQuery;
// структура запроса помещения во временную таблицу (основной)
temporaryTableMainQuery:
    SELECT limitations
    temporaryTableSelectedFields
    into
    from
    where
    groupBy
    having
    forUpdate
    ;
// структура запроса помещения во временную таблицу (объединение)
temporaryTableQuery:
    SELECT limitations
    temporaryTableSelectedFields
    from
    where
    groupBy
    having
    forUpdate
    ;
// выбираемые поля временной таблицы
temporaryTableSelectedFields: temporaryTableSelectedField (COMMA temporaryTableSelectedField)*;
temporaryTableSelectedField:
    (
          ((tableName=identifier DOT)* MUL)
        | (doCall=RECORDAUTONUMBER LPAREN RPAREN)
        | expression
    ) alias
    ;
// помещение во временную таблицу
into: INTO temporaryTableName=identifier;
// таблица как параметр, соединяться ни с чем не может
parameterTable: parameter alias;
// индексирование во временной таблице
indexing: (INDEX_BY indexingItem (COMMA indexingItem)*)?;
// поле индексирования, может быть колонкой или параметром
indexingItem: parameter | column;

// вложенный подзапрос
inlineSubquery: LPAREN subquery orders? RPAREN;

// COMMON FOR QUERIES

// конструкция для изменения, может содержать перечень таблиц, которые необходимо заблокировать для изменения
forUpdate: (FOR_UPDATE mdo?)?;

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
totalsItem: expression alias;
totalsGroups: totalsGroup (COMMA totalsGroup)*;
totalsGroup:
    (
        OVERALL
        | expression
    ) alias
    ;

// только упорядочивание
orders: ORDER_BY ordersItems;
ordersItems: ordersItem (COMMA ordersItem)*;
ordersItem: expression orderDirection? alias;
orderDirection: ASC | DESC | (HIERARCHY DESC?);

// перечень таблиц-источников данных для выборки
dataSources: dataSource (COMMA dataSource)*;
// варианты источников данных
dataSource:
    (
          (LPAREN dataSource RPAREN)
        | inlineSubquery
        | table
        | virtualTable
        | parameterTable
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
virtualTableParameter: expression?;

// соединения таблиц
joinPart:
    (INNER_JOIN | LEFT_JOIN | RIGHT_JOIN | FULL_JOIN | JOIN)    // тип соединения
    dataSource on=(ON_EN | PO_RU) expression                    // имя таблицы и соединение
    ;

// условия выборки
where: (WHERE expression)?;

// группировка данных
groupBy: (GROUP_BY groupByItems)?;
groupByItems: expression (COMMA expression)*;

// условия на аггрегированные данные
having: (HAVING expression)?;

// объединенные выражения
expression: member (boolOperation member)*;

member:
      statement
    | binaryStatement
    | comparyStatement
    | inStatement
    | isnullStatement
    | refsStatement
    | beetweenStatement
    | likeStatement
    ;

statement:
      unaryModifier* column
    | unaryModifier* parameter
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
    | (unaryModifier* LPAREN expression RPAREN)
    | aggrMathCallStatement
    | aggrCountCallStatement
    | castStatement
    | caseStatement
    | ((doCall=ISNULL | (NOT+ doCall=ISNULL) | (MINUS+ doCall=ISNULL)) LPAREN expression COMMA expression RPAREN)
    | (doCall=DATEADD LPAREN expression COMMA datePart COMMA expression RPAREN)
    | (doCall=DATEDIFF LPAREN expression COMMA expression COMMA datePart RPAREN)
    | (doCall=(BEGINOFPERIOD | ENDOFPERIOD) LPAREN expression COMMA datePart RPAREN)
    | (MINUS* doCall=(YEAR | QUARTER | MONTH | DAYOFYEAR | DAY | WEEK | WEEKDAY | HOUR | MINUTE | SECOND) LPAREN expression RPAREN)
    | (doCall=SUBSTRING LPAREN expression COMMA expression COMMA expression RPAREN)
    | (doCall=(VALUETYPE | PRESENTATION | REFPRESENTATION) LPAREN expression RPAREN)
;

aggrMathCallStatement:
    (
          (doCall=(SUM | AVG | MIN | MAX))
        | (MINUS+ doCall=(SUM | AVG | MIN | MAX))
        | (NOT+ doCall=(SUM | AVG | MIN | MAX))
    ) LPAREN expression RPAREN;
aggrCountCallStatement:
    ((doCall=COUNT) | (MINUS+ doCall=COUNT) | (NOT+ doCall=COUNT)) LPAREN (DISTINCT? expression | MUL) RPAREN;
castStatement:
    (doCall=CAST | (NOT+ doCall=CAST) | (MINUS doCall=CAST)) LPAREN expression AS (
          BOOLEAN
        | (NUMBER (LPAREN DECIMAL (COMMA DECIMAL)? RPAREN)?)
        | (STRING (LPAREN DECIMAL RPAREN)?)
        | DATE
        | mdo
        ) RPAREN (DOT identifier)*
    ;

caseStatement: (CASE | (MINUS+ CASE) | (NOT+ CASE)) expression? whenBranch+ elseBranch? END;
whenBranch: WHEN expression THEN expression;
elseBranch: ELSE expression;

binaryStatement : statement (binaryOperation statement)+;
comparyStatement: (binaryStatement | statement) compareOperation (binaryStatement | statement);
inStatement     :
      (statement NOT? in (inlineSubquery | (LPAREN statement (COMMA statement)*) RPAREN))
    | (NOT* LPAREN statement (COMMA statement)+ RPAREN NOT? IN (inlineSubquery | ( LPAREN statement (COMMA statement)*) RPAREN))
;

isnullStatement     : statement IS NOT? NULL;
refsStatement       : statement REFS mdo;
beetweenStatement   : statement NOT? BETWEEN between;
likeStatement       : statement NOT? LIKE statement (ESCAPE escape=multiString)?;

between: expression AND expression;
unaryModifier: NOT | MINUS;

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

in          : (IN | IN_HIERARCHY);

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

multiString: STR+;

// todo
// [?] Добавить системные перечисления
// [?] Добавить сопоставление виртуальных таблиц MDO
// [ ] Оптимизировать скорость парсера
// [?] Комментарии в многострочной строке
