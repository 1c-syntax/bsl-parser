/**
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018-2026
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Fedkin <nixel2007@gmail.com>, Sergey Batanov <sergey.batanov@dmpas.ru>
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
lexer grammar SDBLLexer;

options { caseInsensitive=true; }

// COMMONS
WHITE_SPACE: [ \t\f\r\n]+ -> channel(HIDDEN);
LINE_COMMENT: LINE_COMMENT_BODY [\f\r\n]?-> channel(HIDDEN);
fragment LINE_COMMENT_BODY: '//' ~[\r\n]*;

// SEPARATORS
DOT: '.' -> pushMode(DOT_MODE);
LPAREN: '(';
RPAREN: ')';
SEMICOLON: ';';
COMMA: ',';
ASSIGN: '=';
PLUS: '+';
MINUS: '-';
LESS_OR_EQUAL: '<=';
NOT_EQUAL: '<>';
LESS: '<';
GREATER_OR_EQUAL: '>=';
GREATER: '>';
MUL: '*';
QUOTIENT: '/';
NUMBER_SIGH: '#';
AMPERSAND: '&' -> pushMode(PARAMETER_MODE);
BRACE: '{' -> pushMode(BRACE_MODE), channel(HIDDEN);

// KEYWORDS         RU                                EN
ALLOWED:        'РАЗРЕШЕННЫЕ'                   | 'ALLOWED';
ADD:            'ДОБАВИТЬ'                      | 'ADD';
AND:            'И'                             | 'AND';
AS:             ('КАК'                          | 'AS')             -> pushMode(ID_MODE);
ASC:            'ВОЗР'                          | 'ASC';
AUTOORDER:      'АВТОУПОРЯДОЧИВАНИЕ'            | 'AUTOORDER';
BETWEEN:        'МЕЖДУ'                         | 'BETWEEN';
BY:             'ПО'                            | 'BY' | 'ON';
CASE:           'ВЫБОР'                         | 'CASE';
CAST:           'ВЫРАЗИТЬ'                      | 'CAST';
DESC:           'УБЫВ'                          | 'DESC';
DISTINCT:       'РАЗЛИЧНЫЕ'                     | 'DISTINCT';
DROP:           ('УНИЧТОЖИТЬ'                   | 'DROP')           -> pushMode(ID_MODE);
ELSE:           'ИНАЧЕ'                         | 'ELSE';
END:            'КОНЕЦ'                         | 'END';
ESCAPE:         'СПЕЦСИМВОЛ'                    | 'ESCAPE';
FALSE:          'ЛОЖЬ'                          | 'FALSE';
FROM:           'ИЗ'                            | 'FROM';
HAVING:         'ИМЕЮЩИЕ'                       | 'HAVING';
INTO:           'ПОМЕСТИТЬ'                     | 'INTO';
IS:             'ЕСТЬ'                          | 'IS';
ISNULL:         'ЕСТЬNULL'                      | 'ISNULL';
LIKE:           'ПОДОБНО'                       | 'LIKE';
NOT:            'НЕ'                            | 'NOT';
NULL:           'NULL';
OF:                                               'OF';
OR:             'ИЛИ'                           | 'OR';
OVERALL:        'ОБЩИЕ'                         | 'OVERALL';
SELECT:         'ВЫБРАТЬ'                       | 'SELECT';
THEN:           'ТОГДА'                         | 'THEN';
TOP:            'ПЕРВЫЕ'                        | 'TOP';
TOTALS:         'ИТОГИ'                         | 'TOTALS';
TRUE:           'ИСТИНА'                        | 'TRUE';
UNDEFINED:      'НЕОПРЕДЕЛЕНО'                  | 'UNDEFINED';
WHEN:           'КОГДА'                         | 'WHEN';
WHERE:          'ГДЕ'                           | 'WHERE';

// KEYWORDS            RU                              EN
ACOS:               'ACOS';
ASIN:               'ASIN';
ATAN:               'ATAN';
AVG:                'СРЕДНЕЕ'                       | 'AVG';
BEGINOFPERIOD:      'НАЧАЛОПЕРИОДА'                 | 'BEGINOFPERIOD';
BOOLEAN:            'БУЛЕВО'                        | 'BOOLEAN';
COS:                'COS';
COUNT:              'КОЛИЧЕСТВО'                    | 'COUNT';
DATE:               'ДАТА'                          | 'DATE';
DATEADD:            'ДОБАВИТЬКДАТЕ'                 | 'DATEADD';
DATEDIFF:           'РАЗНОСТЬДАТ'                   | 'DATEDIFF';
DATETIME:           'ДАТАВРЕМЯ'                     | 'DATETIME';
DAY:                'ДЕНЬ'                          | 'DAY';
DAYOFYEAR:          'ДЕНЬГОДА'                      | 'DAYOFYEAR';
EMPTYTABLE:         'ПУСТАЯТАБЛИЦА'                 | 'EMPTYTABLE';
EMPTYREF:           'ПУСТАЯССЫЛКА'                  | 'EMPTYREF';
ENDOFPERIOD:        'КОНЕЦПЕРИОДА'                  | 'ENDOFPERIOD';
EXP:                'EXP';
HALFYEAR:           'ПОЛУГОДИЕ'                     | 'HALFYEAR';
HOUR:               'ЧАС'                           | 'HOUR';
INT:                'ЦЕЛ'                           | 'INT';
LEFT:               'ЛЕВ'                           | 'LEFT';
LOG:                'LOG';
LOG10:              'LOG10';
LOWER:              'НРЕГ'                          | 'LOWER';
MAX:                'МАКСИМУМ'                      | 'MAX';
MIN:                'МИНИМУМ'                       | 'MIN';
MINUTE:             'МИНУТА'                        | 'MINUTE';
MONTH:              'МЕСЯЦ'                         | 'MONTH';
NUMBER:             'ЧИСЛО'                         | 'NUMBER';
UNIQUE:             'УНИКАЛЬНО'                     | 'UNIQUE';
QUARTER:            'КВАРТАЛ'                       | 'QUARTER';
PERIODS:            'ПЕРИОДАМИ'                     | 'PERIODS';
REFS:               'ССЫЛКА'                        | 'REFS';
PRESENTATION:       'ПРЕДСТАВЛЕНИЕ'                 | 'PRESENTATION';
RECORDAUTONUMBER:   'АВТОНОМЕРЗАПИСИ'               | 'RECORDAUTONUMBER';
REFPRESENTATION:    'ПРЕДСТАВЛЕНИЕССЫЛКИ'           | 'REFPRESENTATION';
POW:                'POW';
RIGHT:              'ПРАВ'                          | 'RIGHT';
ROUND:              'ОКР'                           | 'ROUND';
SECOND:             'СЕКУНДА'                       | 'SECOND';
SIN:                'SIN';
SQRT:               'SQRT';
STOREDDATASIZE:     'РАЗМЕРХРАНИМЫХДАННЫХ'          | 'STOREDDATASIZE';
STRING:             'СТРОКА'                        | 'STRING';
STRINGLENGTH:       'ДЛИНАСТРОКИ'                   | 'STRINGLENGTH';
STRFIND:            'СТРНАЙТИ'                      | 'STRFIND';
STRREPLACE:         'СТРЗАМЕНИТЬ'                   | 'STRREPLACE';
SUBSTRING:          'ПОДСТРОКА'                     | 'SUBSTRING';
SUM:                'СУММА'                         | 'SUM';
TAN:                'TAN';
TENDAYS:            'ДЕКАДА'                        | 'TENDAYS';
TRIMALL:            'СОКРЛП'                        | 'TRIMALL';
TRIML:              'СОКРЛ'                         | 'TRIML';
TRIMR:              'СОКРП'                         | 'TRIMR';
TYPE:               'ТИП'                           | 'TYPE';
UPPER:              'ВРЕГ'                          | 'UPPER';
VALUE:              'ЗНАЧЕНИЕ'                      | 'VALUE';
VALUETYPE:          'ТИПЗНАЧЕНИЯ'                   | 'VALUETYPE';
WEEK:               'НЕДЕЛЯ'                        | 'WEEK';
WEEKDAY:            'ДЕНЬНЕДЕЛИ'                    | 'WEEKDAY';
YEAR:               'ГОД'                           | 'YEAR';
UUID:               'УНИКАЛЬНЫЙИДЕНТИФИКАТОР'       | 'UUID';

// MDO TYPES                            RU                          EN
ACCOUNTING_REGISTER_TYPE:           'РЕГИСТРБУХГАЛТЕРИИ'        | 'ACCOUNTINGREGISTER';
ACCUMULATION_REGISTER_TYPE:         'РЕГИСТРНАКОПЛЕНИЯ'         | 'ACCUMULATIONREGISTER';
BUSINESS_PROCESS_TYPE:              'БИЗНЕСПРОЦЕСС'             | 'BUSINESSPROCESS';
CALCULATION_REGISTER_TYPE:          'РЕГИСТРРАСЧЕТА'            | 'CALCULATIONREGISTER';
CATALOG_TYPE:                       'СПРАВОЧНИК'                | 'CATALOG';
CHART_OF_ACCOUNTS_TYPE:             'ПЛАНСЧЕТОВ'                | 'CHARTOFACCOUNTS';
CHART_OF_CALCULATION_TYPES_TYPE:    'ПЛАНВИДОВРАСЧЕТА'          | 'CHARTOFCALCULATIONTYPES';
CHART_OF_CHARACTERISTIC_TYPES_TYPE: 'ПЛАНВИДОВХАРАКТЕРИСТИК'    | 'CHARTOFCHARACTERISTICTYPES';
CONSTANT_TYPE:                      'КОНСТАНТА'                 | 'CONSTANT';
DOCUMENT_TYPE:                      'ДОКУМЕНТ'                  | 'DOCUMENT';
DOCUMENT_JOURNAL_TYPE:              'ЖУРНАЛДОКУМЕНТОВ'          | 'DOCUMENTJOURNAL';
ENUM_TYPE:                          'ПЕРЕЧИСЛЕНИЕ'              | 'ENUM';
EXCHANGE_PLAN_TYPE:                 'ПЛАНОБМЕНА'                | 'EXCHANGEPLAN';
EXTERNAL_DATA_SOURCE_TYPE:          ('ВНЕШНИЙИСТОЧНИКДАННЫХ'    | 'EXTERNALDATASOURCE')                 -> pushMode(EXTERNAL_DATA_SOURCE_MODE);
FILTER_CRITERION_TYPE:              'КРИТЕРИЙОТБОРА'            | 'FILTERCRITERION';
INFORMATION_REGISTER_TYPE:          'РЕГИСТРСВЕДЕНИЙ'           | 'INFORMATIONREGISTER';
SEQUENCE_TYPE:                      'ПОСЛЕДОВАТЕЛЬНОСТЬ'        | 'SEQUENCE';
TASK_TYPE:                          'ЗАДАЧА'                    | 'TASK';

// FIELDS                               RU                          EN
ROUTEPOINT_FIELD:                   'ТОЧКАМАРШРУТА'             |
                                    'ТОЧКИ'                     | 'ROUTEPOINT';

// compex keywords
INDEX_BY_SETS:          ('ИНДЕКСИРОВАТЬ'    | 'INDEX')  WHITE_SPACE_COMMENT ('ПО'           | 'BY') WHITE_SPACE_COMMENT ('НАБОРАМ'      | 'SETS') ;
INDEX_BY:               ('ИНДЕКСИРОВАТЬ'    | 'INDEX')  WHITE_SPACE_COMMENT ('ПО'           | 'BY');
GROUP_BY_GROUPING_SETS: ('СГРУППИРОВАТЬ'    | 'GROUP')  WHITE_SPACE_COMMENT ('ПО'           | 'BY') WHITE_SPACE_COMMENT ('ГРУППИРУЮЩИМ' | 'GROUPING') WHITE_SPACE_COMMENT ('НАБОРАМ' | 'SETS');
GROUP_BY:               ('СГРУППИРОВАТЬ'    | 'GROUP')  WHITE_SPACE_COMMENT ('ПО'           | 'BY');
ORDER_BY:               ('УПОРЯДОЧИТЬ'      | 'ORDER')  WHITE_SPACE_COMMENT ('ПО'           | 'BY');
FOR_UPDATE:             ('ДЛЯ'              | 'FOR')    WHITE_SPACE_COMMENT ('ИЗМЕНЕНИЯ'    | 'UPDATE');

RIGHT_OUTER_JOIN:       ('ПРАВОЕ'       | 'RIGHT')  WHITE_SPACE_COMMENT ('ВНЕШНЕЕ'      | 'OUTER') WHITE_SPACE_COMMENT ('СОЕДИНЕНИЕ' | 'JOIN');
RIGHT_JOIN:             ('ПРАВОЕ'       | 'RIGHT')  WHITE_SPACE_COMMENT ('СОЕДИНЕНИЕ'   | 'JOIN');
LEFT_OUTER_JOIN:        ('ЛЕВОЕ'        | 'LEFT')   WHITE_SPACE_COMMENT ('ВНЕШНЕЕ'      | 'OUTER') WHITE_SPACE_COMMENT ('СОЕДИНЕНИЕ' | 'JOIN');
LEFT_JOIN:              ('ЛЕВОЕ'        | 'LEFT')   WHITE_SPACE_COMMENT ('СОЕДИНЕНИЕ'   | 'JOIN');
FULL_OUTER_JOIN:        ('ПОЛНОЕ'       | 'FULL')   WHITE_SPACE_COMMENT ('ВНЕШНЕЕ'      | 'OUTER') WHITE_SPACE_COMMENT ('СОЕДИНЕНИЕ' | 'JOIN');
FULL_JOIN:              ('ПОЛНОЕ'       | 'FULL')   WHITE_SPACE_COMMENT ('СОЕДИНЕНИЕ'   | 'JOIN');
INNER_JOIN:             ('ВНУТРЕННЕЕ'   | 'INNER')  WHITE_SPACE_COMMENT ('СОЕДИНЕНИЕ'   | 'JOIN');
JOIN:                   'СОЕДИНЕНИЕ'    | 'JOIN';

UNION_ALL:              ('ОБЪЕДИНИТЬ'   | 'UNION')  WHITE_SPACE_COMMENT ('ВСЕ'          | 'ALL');
UNION:                  'ОБЪЕДИНИТЬ'    | 'UNION';

ONLY_HIERARCHY:         ('ТОЛЬКО'       | 'ONLY')   WHITE_SPACE_COMMENT ('ИЕРАРХИЯ' | 'HIERARCHY');
HIERARCHY:              ('ИЕРАРХИЯ'     | 'HIERARCHY');

IN_HIERARCHY:           ('В'            | 'IN')     WHITE_SPACE_COMMENT ('ИЕРАРХИИ' | 'HIERARCHY');
IN:                     'В'             | 'IN';

GROUPEDBY:              'СГРУППИРОВАНОПО' | 'GROUPEDBY';

fragment DIGIT: [0-9];
fragment LETTER: [\p{Letter}] | '_';

fragment WHITE_SPACE_COMMENT: (LINE_COMMENT_BODY [\f\r\n])* (WHITE_SPACE (LINE_COMMENT_BODY [\f\r\n])*)+;

DECIMAL     : DIGIT+;
FLOAT       : DIGIT+ '.' DIGIT*;
STR         : '"' -> more, pushMode(STRINGS);
INCORRECT_IDENTIFIER  : DIGIT+ LETTER (LETTER | DIGIT)*;
IDENTIFIER  : LETTER (LETTER | DIGIT)*;
UNKNOWN     : . -> channel(HIDDEN);

mode STRINGS;
STRFULL     :  (~["\n\r] | '""')* '"' -> type(STR), popMode;
SKIPNEWLINE : [\n\r][ \t\f]* -> channel(HIDDEN), type(WHITE_SPACE);
STRPART     :  (~["\n\r] | '""')+ -> type(STR);

// PARAMETERS
mode PARAMETER_MODE;
PARAMETER_WHITE_SPACE   : WHITE_SPACE -> channel(HIDDEN), type(WHITE_SPACE);
PARAMETER_IDENTIFIER    : IDENTIFIER -> popMode;
PARAMETER_UKNOWN        : . -> channel(HIDDEN), type(UNKNOWN);

mode DOT_MODE;
DOT_WHITE_SPACE         : WHITE_SPACE -> channel(HIDDEN), type(WHITE_SPACE);
DOT_MUL                 : MUL -> type(MUL), popMode;
DOT_LPAREN              : LPAREN -> type(LPAREN), popMode;
DOT_RPAREN              : RPAREN -> type(RPAREN), popMode;
DOT_ROUTEPOINT_FIELD    : ROUTEPOINT_FIELD -> type(ROUTEPOINT_FIELD), popMode;
DOT_EMPTYREF            : EMPTYREF -> type(EMPTYREF), popMode;

// VIRTUAL TABLES                       RU                                  EN
ACTUAL_ACTION_PERIOD_VT:            ('ФАКТИЧЕСКИЙПЕРИОДДЕЙСТВИЯ'        | 'ACTUALACTIONPERIOD')         -> popMode;
BALANCE_VT:                         ('ОСТАТКИ'                          | 'BALANCE')                    -> popMode;
BALANCE_AND_TURNOVERS_VT:           ('ОСТАТКИИОБОРОТЫ'                  | 'BALANCEANDTURNOVERS')        -> popMode;
BOUNDARIES_VT:                      ('ГРАНИЦЫ'                          | 'BOUNDARIES')                 -> popMode;
DR_CR_TURNOVERS_VT:                 ('ОБОРОТЫДТКТ'                      | 'DRCRTURNOVERS')              -> popMode;
EXT_DIMENSIONS_VT:                  ('СУБКОНТО'                         | 'EXTDIMENSIONS')              -> popMode;
RECORDS_WITH_EXT_DIMENSIONS_VT:     ('ДВИЖЕНИЯССУБКОНТО'                | 'RECORDSWITHEXTDIMENSIONS')   -> popMode;
SCHEDULE_DATA_VT:                   ('ДАННЫЕГРАФИКА'                    | 'SCHEDULEDATA')               -> popMode;
SLICEFIRST_VT:                      ('СРЕЗПЕРВЫХ'                       | 'SLICEFIRST')                 -> popMode;
SLICELAST_VT:                       ('СРЕЗПОСЛЕДНИХ'                    | 'SLICELAST')                  -> popMode;
TASK_BY_PERFORMER_VT:               ('ЗАДАЧИПОИСПОЛНИТЕЛЮ'              | 'TASKBYPERFORMER')            -> popMode;
TURNOVERS_VT:                       ('ОБОРОТЫ'                          | 'TURNOVERS')                  -> popMode;

DOT_IDENTIFIER      : IDENTIFIER -> type(IDENTIFIER), popMode;

mode BRACE_MODE;
BRACE_WHITE_SPACE   : WHITE_SPACE -> channel(HIDDEN), type(WHITE_SPACE);
BRACE_IDENTIFIER    : IDENTIFIER -> channel(HIDDEN);
BRACE_START         : '{' -> pushMode(BRACE_MODE), channel(HIDDEN);
BRACE_END           : '}' -> channel(HIDDEN), type(UNKNOWN), popMode;
BRACE_UNKNOWN       : . -> channel(HIDDEN), type(UNKNOWN);

mode ID_MODE;
ID_BOOLEAN          : BOOLEAN -> type(BOOLEAN), popMode;
ID_NUMBER           : NUMBER -> type(NUMBER), popMode;
ID_STRING           : STRING -> type(STRING), popMode;
ID_DATE             : DATE -> type(DATE), popMode;
ID_BUSINESS_PROCESS_TYPE    : BUSINESS_PROCESS_TYPE -> type(BUSINESS_PROCESS_TYPE), popMode;
ID_CATALOG_TYPE             : CATALOG_TYPE -> type(CATALOG_TYPE), popMode;
ID_CHART_OF_ACCOUNTS_TYPE   : CHART_OF_ACCOUNTS_TYPE -> type(CHART_OF_ACCOUNTS_TYPE), popMode;
ID_CHART_OF_CALCULATION_TYPES_TYPE      : CHART_OF_CALCULATION_TYPES_TYPE -> type(CHART_OF_CALCULATION_TYPES_TYPE), popMode;
ID_CHART_OF_CHARACTERISTIC_TYPES_TYPE   : CHART_OF_CHARACTERISTIC_TYPES_TYPE -> type(CHART_OF_CHARACTERISTIC_TYPES_TYPE), popMode;
ID_DOCUMENT_TYPE            : DOCUMENT_TYPE -> type(DOCUMENT_TYPE), popMode;
ID_ENUM_TYPE                : ENUM_TYPE -> type(ENUM_TYPE), popMode;
ID_EXCHANGE_PLAN_TYPE       : EXCHANGE_PLAN_TYPE -> type(EXCHANGE_PLAN_TYPE), popMode;
ID_TASK_TYPE                : TASK_TYPE -> type(TASK_TYPE), popMode;
ID_DOT              : DOT -> type(DOT), pushMode(DOT_MODE), popMode;
ID_IDENTIFIER       : IDENTIFIER -> type(IDENTIFIER), popMode;
ID_UNKNOWN          : . -> channel(HIDDEN), type(UNKNOWN);

mode EXTERNAL_DATA_SOURCE_MODE;
EDS_TABLE:              ('ТАБЛИЦА'              | 'TABLE')              -> popMode;
EDS_CUBE:               ('КУБ'                  | 'CUBE');
EDS_CUBE_DIMTABLE:      ('ТАБЛИЦАИЗМЕРЕНИЯ'     | 'DIMENSIONTABLE')     -> popMode;

EDS_WHITE_SPACE: WHITE_SPACE -> channel(HIDDEN), type(WHITE_SPACE);
EDS_MUL: MUL -> type(MUL);
EDS_DOT: DOT -> type(DOT);
EDS_IDENTIFIER : IDENTIFIER -> type(IDENTIFIER);
