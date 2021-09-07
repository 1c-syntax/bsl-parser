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
lexer grammar SDBLLexer;

@members {
public SDBLLexer(CharStream input, boolean crAwareCostructor) {
  super(input);
  _interp = new CRAwareLexerATNSimulator(this, _ATN);
  validateInputStream(_ATN, input);
}
}

// COMMONS
WHITE_SPACE: [ \t\f\r\n]+ -> channel(HIDDEN);
LINE_COMMENT: '//' ~[\r\n]* -> channel(HIDDEN);

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
AMPERSAND: '&' -> pushMode(PARAMETER_MODE);
BRACE: '{' -> pushMode(BRACE_MODE), channel(HIDDEN);

// KEYWORDS         RU                                                                          EN
ALLOWED:        RU_R RU_A RU_Z RU_R RU_E RU_SH RU_E RU_N RU_N RU_Y RU_E                     | A L L O W E D;
AND:            RU_I                                                                        | A N D;
AS:             (RU_K RU_A RU_K                                                             | A S) -> pushMode(ID_MODE);
ASC:            RU_V RU_O RU_Z RU_R                                                         | A S C;
AUTOORDER:      RU_A RU_V RU_T RU_O RU_U RU_P RU_O RU_R RU_YA RU_D RU_O
                    RU_CH RU_I RU_V RU_A RU_N RU_I RU_E                                     | A U T O O R D E R;
BETWEEN:        RU_M RU_E RU_ZH RU_D RU_U                                                   | B E T W E E N;
BY_EN:                                                                                        B Y;
CASE:           RU_V RU_Y RU_B RU_O RU_R                                                    | C A S E;
CAST:           RU_V RU_Y RU_R RU_A RU_Z RU_I RU_T RU_SOFT_SIGN                             | C A S T;
DESC:           RU_U RU_B RU_Y RU_V                                                         | D E S C;
DISTINCT:       RU_R RU_A RU_Z RU_L RU_I RU_CH RU_N RU_Y RU_E                               | D I S T I N C T;
DROP:           (RU_U RU_N RU_I RU_CH RU_T RU_O RU_ZH RU_I RU_T RU_SOFT_SIGN                 | D R O P) -> pushMode(ID_MODE);
ELSE:           RU_I RU_N RU_A RU_CH RU_E                                                   | E L S E;
END:            RU_K RU_O RU_N RU_E RU_C                                                    | E N D;
ESCAPE:         RU_S RU_P RU_E RU_C RU_S RU_I RU_M RU_V RU_O RU_L                           | E S C A P E;
FALSE:          RU_L RU_O RU_ZH RU_SOFT_SIGN                                                | F A L S E;
FROM:           RU_I RU_Z                                                                   | F R O M;
HAVING:         RU_I RU_M RU_E RU_YU RU_SCH RU_I RU_E                                       | H A V I N G;
HIERARCHY:      RU_I RU_E RU_R RU_A RU_R RU_H RU_I RU_YA                                    | H I E R A R C H Y;
INTO:           RU_P RU_O RU_M RU_E RU_S RU_T RU_I RU_T RU_SOFT_SIGN                        | I N T O;
IS:             RU_E RU_S RU_T RU_SOFT_SIGN                                                 | I S;
ISNULL:         RU_E RU_S RU_T RU_SOFT_SIGN N U L L                                         | I S N U L L;
LIKE:           RU_P RU_O RU_D RU_O RU_B RU_N RU_O                                          | L I K E;
NOT:            RU_N RU_E                                                                   | N O T;
NULL:                                                                                         N U L L;
OF:                                                                                           O F;
ON_EN:                                                                                        O N;
OR:             RU_I RU_L RU_I                                                              | O R;
OVERALL:        RU_O RU_B RU_SCH RU_I RU_E                                                  | O V E R A L L;
PO_RU:          RU_P RU_O;
SELECT:         RU_V RU_Y RU_B RU_R RU_A RU_T RU_SOFT_SIGN                                  | S E L E C T;
THEN:           RU_T RU_O RU_G RU_D RU_A                                                    | T H E N;
TOP:            RU_P RU_E RU_R RU_V RU_Y RU_E                                               | T O P;
TOTALS:         RU_I RU_T RU_O RU_G RU_I                                                    | T O T A L S;
TRUE:           RU_I RU_S RU_T RU_I RU_N RU_A                                               | T R U E;
UNDEFINED:      RU_N RU_E RU_O RU_P RU_R RU_E RU_D RU_E RU_L RU_E RU_N RU_O                 | U N D E F I N E D;
WHEN:           RU_K RU_O RU_G RU_D RU_A                                                    | W H E N;
WHERE:          RU_G RU_D RU_E                                                              | W H E R E;

// KEYWORDS         RU                                                                          EN
AVG:            RU_S RU_R RU_E RU_D RU_N RU_E RU_E                                          | A V G;
BEGINOFPERIOD:  RU_N RU_A RU_CH RU_A RU_L RU_O RU_P RU_E RU_R RU_I RU_O RU_D RU_A           | B E G I N O F P E R I O D;
BOOLEAN:        RU_B RU_U RU_L RU_E RU_V RU_O                                               | B O O L E A N;
COUNT:          RU_K RU_O RU_L RU_I RU_CH RU_E RU_S RU_T RU_V RU_O                          | C O U N T;
DATE:           RU_D RU_A RU_T RU_A                                                         | D A T E;
DATEADD:        RU_D RU_O RU_B RU_A RU_V RU_I RU_T RU_SOFT_SIGN RU_K RU_D RU_A RU_T RU_E    | D A T E A D D;
DATEDIFF:       RU_R RU_A RU_Z RU_N RU_O RU_S RU_T RU_SOFT_SIGN RU_D RU_A RU_T              | D A T E D I F F;
DATETIME:       RU_D RU_A RU_T RU_A RU_V RU_R RU_E RU_M RU_YA                               | D A T E T I M E;
DAY:            RU_D RU_E RU_N RU_SOFT_SIGN                                                 | D A Y;
DAYOFYEAR:      RU_D RU_E RU_N RU_SOFT_SIGN RU_G RU_O RU_D RU_A                             | D A Y O F Y E A R;
EMPTYTABLE:     RU_P RU_U RU_S RU_T RU_A RU_YA RU_T RU_A RU_B RU_L RU_I RU_C RU_A           | E M P T Y T A B L E;
EMPTYREF:       RU_P RU_U RU_S RU_T RU_A RU_YA RU_S RU_S RU_Y RU_L RU_K RU_A                | E M P T Y R E F;
ENDOFPERIOD:    RU_K RU_O RU_N RU_E RU_C RU_P RU_E RU_R RU_I RU_O RU_D RU_A                 | E N D O F P E R I O D;
HALFYEAR:       RU_P RU_O RU_L RU_U RU_G RU_O RU_D RU_I RU_E                                | H A L F Y E A R;
HOUR:           RU_CH RU_A RU_S                                                             | H O U R;
MAX:            RU_M RU_A RU_K RU_S RU_I RU_M RU_U RU_M                                     | M A X;
MIN:            RU_M RU_I RU_N RU_I RU_M RU_U RU_M                                          | M I N;
MINUTE:         RU_M RU_I RU_N RU_U RU_T RU_A                                               | M I N U T E;
MONTH:          RU_M RU_E RU_S RU_YA RU_C                                                   | M O N T H;
NUMBER:         RU_CH RU_I RU_S RU_L RU_O                                                   | N U M B E R;
QUARTER:        RU_K RU_V RU_A RU_R RU_T RU_A RU_L                                          | Q U A R T E R;
ONLY:           RU_T RU_O RU_L RU_SOFT_SIGN RU_K RU_O                                       | O N L Y;
PERIODS:        RU_P RU_E RU_R RU_I RU_O RU_D RU_A RU_M RU_I                                | P E R I O D S;
REFS:           RU_S RU_S RU_Y RU_L RU_K RU_A                                               | R E F S;
PRESENTATION:   RU_P RU_R RU_E RU_D RU_S RU_T RU_A RU_V RU_L RU_E RU_N RU_I RU_E            | P R E S E N T A T I O N;
RECORDAUTONUMBER:
                RU_A RU_V RU_T RU_O RU_N RU_O RU_M RU_E RU_R RU_Z RU_A RU_P RU_I RU_S RU_I  | R E C O R D A U T O N U M B E R;
REFPRESENTATION:
                RU_P RU_R RU_E RU_D RU_S RU_T RU_A RU_V RU_L RU_E RU_N RU_I RU_E RU_S RU_S RU_Y RU_L RU_K RU_I
                                                                                            | R E F P R E S E N T A T I O N;
SECOND:         RU_S RU_E RU_K RU_U RU_N RU_D RU_A                                          | S E C O N D;
STRING:         RU_S RU_T RU_R RU_O RU_K RU_A                                               | S T R I N G;
SUBSTRING:      RU_P RU_O RU_D RU_S RU_T RU_R RU_O RU_K RU_A                                | S U B S T R I N G;
SUM:            RU_S RU_U RU_M RU_M RU_A                                                    | S U M;
TENDAYS:        RU_D RU_E RU_K RU_A RU_D RU_A                                               | T E N D A Y S;
TYPE:           RU_T RU_I RU_P                                                              | T Y P E;
VALUE:          RU_Z RU_N RU_A RU_CH RU_E RU_N RU_I RU_E                                    | V A L U E;
VALUETYPE:      RU_T RU_I RU_P RU_Z RU_N RU_A RU_CH RU_E RU_N RU_I RU_YA                    | V A L U E T Y P E;
WEEK:           RU_N RU_E RU_D RU_E RU_L RU_YA                                              | W E E K;
WEEKDAY:        RU_D RU_E RU_N RU_SOFT_SIGN RU_N RU_E RU_D RU_E RU_L RU_I                   | W E E K D A Y;
YEAR:           RU_G RU_O RU_D                                                              | Y E A R;

// MDO TYPES                RU                                                                  EN
ACCOUNTING_REGISTER_TYPE:
                REGISTER_RU RU_B RU_U RU_H RU_G RU_A RU_L RU_T RU_E RU_R RU_I RU_I     | A C C O U N T I N G REGISTER_EN;
ACCUMULATION_REGISTER_TYPE:
                REGISTER_RU RU_N RU_A RU_K RU_O RU_P RU_L RU_E RU_N RU_I RU_YA              | A C C U M U L A T I O N REGISTER_EN;
BUSINESS_PROCESS_TYPE:
                RU_B RU_I RU_Z RU_N RU_E RU_S RU_P RU_R RU_O RU_C RU_E RU_S RU_S            | B U S I N E S S P R O C E S S;
CALCULATION_REGISTER_TYPE:
                REGISTER_RU RU_R RU_A RU_S RU_CH RU_E RU_T RU_A                             | C A L C U L A T I O N REGISTER_EN;
CATALOG_TYPE:   RU_S RU_P RU_R RU_A RU_V RU_O RU_CH RU_N RU_I RU_K                          | C A T A L O G;
CHART_OF_ACCOUNTS_TYPE:
                PLAN_RU RU_S RU_CH RU_E RU_T RU_O RU_V                                      | PLAN_EN A C C O U N T S;
CHART_OF_CALCULATION_TYPES_TYPE:
                PLAN_RU RU_V RU_I RU_D RU_O RU_V RU_R RU_A RU_S RU_CH RU_E RU_T RU_A        | PLAN_EN C A L C U L A T I O N T Y P E S;
CHART_OF_CHARACTERISTIC_TYPES_TYPE:
                PLAN_RU RU_V RU_I RU_D RU_O RU_V RU_H RU_A RU_R RU_A RU_K RU_T RU_E RU_R RU_I RU_S RU_T RU_I RU_K
                                                                                            | PLAN_EN C H A R A C T E R I S T I C T Y P E S;
CONSTANT_TYPE:  RU_K RU_O RU_N RU_S RU_T RU_A RU_N RU_T RU_A                                | C O N S T A N T;
DOCUMENT_TYPE:  DOCUMENT_RU                                                                 | DOCUMENT_EN;
DOCUMENT_JOURNAL_TYPE:
                RU_ZH RU_U RU_R RU_N RU_A RU_L DOCUMENT_RU RU_O RU_V                        | DOCUMENT_EN J O U R N A L;
ENUM_TYPE:      RU_P RU_E RU_R RU_E RU_CH RU_I RU_S RU_L RU_E RU_N RU_I RU_E                | E N U M;
EXCHANGE_PLAN_TYPE:
                PLAN_RU RU_O RU_B RU_M RU_E RU_N RU_A                                       | E X C H A N G E P L A N;
EXTERNAL_DATA_SOURCE_TYPE:
                RU_V RU_N RU_E RU_SH RU_N RU_I RU_J RU_I RU_S RU_T RU_O RU_CH RU_N RU_I RU_K RU_D RU_A RU_N RU_N RU_Y RU_H
                                                                                            | E X T E R N A L D A T A S O U R C E;
FILTER_CRITERION_TYPE:
                RU_K RU_R RU_I RU_T RU_E RU_R RU_I RU_J RU_O RU_T RU_B RU_O RU_R RU_A       | F I L T E R C R I T E R I O N;
INFORMATION_REGISTER_TYPE:
                REGISTER_RU RU_S RU_V RU_E RU_D RU_E RU_N RU_I RU_J                         | I N F O R M A T I O N REGISTER_EN;
SEQUENCE_TYPE:
                RU_P RU_O RU_S RU_L RU_E RU_D RU_O RU_V RU_A RU_T RU_E RU_L RU_SOFT_SIGN RU_N RU_O RU_S RU_T RU_SOFT_SIGN
                                                                                            | S E Q U E N C E;
TASK_TYPE:      RU_Z RU_A RU_D RU_A RU_CH RU_A                                              | T A S K;

// FIELDS                 RU                                                                  EN
ROUTEPOINT_FIELD:   RU_T RU_O RU_CH RU_K RU_A RU_M RU_A RU_R RU_SH RU_R RU_U RU_T RU_A      | R O U T E P O I N T;

// compex keywords
INDEX_BY:  (RU_I RU_N RU_D RU_E RU_K RU_S RU_I RU_R RU_O RU_V RU_A RU_T RU_SOFT_SIGN  | I N D E X)
           KEYWORD_SPLIT BY_PART;

GROUP_BY:  (RU_S RU_G RU_R RU_U RU_P RU_P RU_I RU_R RU_O RU_V RU_A RU_T RU_SOFT_SIGN  | G R O U P)
           KEYWORD_SPLIT BY_PART;

ORDER_BY:  (RU_U RU_P RU_O RU_R RU_YA RU_D RU_O RU_CH RU_I RU_T RU_SOFT_SIGN          | O R D E R)
           KEYWORD_SPLIT BY_PART;

GROUPEDBY   : (RU_S RU_G RU_R RU_U RU_P RU_P RU_I RU_R RU_O RU_V RU_A RU_N RU_O RU_P RU_O | G R O U P E D B Y);

GROUPING_SET: (RU_G RU_R RU_U RU_P RU_P RU_I RU_R RU_U RU_YU RU_SCH RU_I RU_M | G R O U P I N G)
               KEYWORD_SPLIT (RU_N RU_A RU_B RU_O RU_R RU_A RU_M | S E T);

RIGHT_JOIN  : (RU_P RU_R RU_A RU_V RU_O RU_E | R I G H T) KEYWORD_SPLIT (OUTER_PART KEYWORD_SPLIT)? JOIN_PART;
LEFT_JOIN   : (RU_L RU_E RU_V RU_O RU_E | L E F T) KEYWORD_SPLIT (OUTER_PART KEYWORD_SPLIT)? JOIN_PART;
INNER_JOIN  : (RU_V RU_N RU_U RU_T RU_R RU_E RU_N RU_N RU_E RU_E | I N N E R) KEYWORD_SPLIT JOIN_PART;
FULL_JOIN   : (RU_P RU_O RU_L RU_N RU_O RU_E | F U L L) KEYWORD_SPLIT (OUTER_PART KEYWORD_SPLIT)? JOIN_PART;
JOIN        : JOIN_PART;

FOR_UPDATE  : (RU_D RU_L RU_YA | F O R)
              KEYWORD_SPLIT
              (RU_I RU_Z RU_M RU_E RU_N RU_E RU_N RU_I RU_YA | U P D A T E);

UNION_ALL   : UNION_PART KEYWORD_SPLIT (RU_V RU_S RU_E | A L L);
UNION       : UNION_PART;

IN_HIERARCHY: (RU_V | I N)
              KEYWORD_SPLIT
              (RU_I RU_E RU_R RU_A RU_R RU_H RU_I RU_I | H I E R A R C H Y);
IN:           (RU_V | I N);

// keywords fragments
fragment KEYWORD_SPLIT: [ \r\n\t]+;
fragment BY_PART    : (RU_P RU_O  | B Y);
fragment JOIN_PART  : (RU_S RU_O RU_E RU_D RU_I RU_N RU_E RU_N RU_I RU_E | J O I N);
fragment OUTER_PART : (RU_V RU_N RU_E RU_SH RU_N RU_E RU_E | O U T E R);
fragment UNION_PART : (RU_O RU_B RU_SOLID_SIGN RU_E RU_D RU_I RU_N RU_I RU_T RU_SOFT_SIGN | U N I O N);
// tables
fragment BALANCE_RU: RU_O RU_S RU_T RU_A RU_T RU_K RU_I;
fragment BALANCE_EN: B A L A N C E;
fragment DOCUMENT_RU: RU_D RU_O RU_K RU_U RU_M RU_E RU_N RU_T;
fragment DOCUMENT_EN: D O C U M E N T;
fragment EXT_DIMENSIONS_RU: RU_S RU_U RU_B RU_K RU_O RU_N RU_T RU_O;
fragment EXT_DIMENSIONS_EN: E X T D I M E N S I O N S;
fragment PLAN_RU: RU_P RU_L RU_A RU_N;
fragment PLAN_EN: C H A R T O F;
fragment REGISTER_RU: RU_R RU_E RU_G RU_I RU_S RU_T RU_R;
fragment REGISTER_EN: R E G I S T E R;
fragment TURNOVERS_RU: RU_O RU_B RU_O RU_R RU_O RU_T RU_Y;
fragment TURNOVERS_EN: T U R N O V E R S;

// LETTERS
fragment RU_A: 'А' | 'а';
fragment RU_B: 'Б' | 'б';
fragment RU_V: 'В' | 'в';
fragment RU_G: 'Г' | 'г';
fragment RU_D: 'Д' | 'д';
fragment RU_YO: 'Ё' | 'ё';
fragment RU_E: 'Е' | 'е';
fragment RU_ZH: 'Ж' | 'ж';
fragment RU_Z: 'З' | 'з';
fragment RU_I: 'И' | 'и';
fragment RU_J: 'Й' | 'й';
fragment RU_K: 'К' | 'к';
fragment RU_L: 'Л' | 'л';
fragment RU_M: 'М' | 'м';
fragment RU_N: 'Н' | 'н';
fragment RU_O: 'О' | 'о';
fragment RU_P: 'П' | 'п';
fragment RU_R: 'Р' | 'р';
fragment RU_S: 'С' | 'с';
fragment RU_T: 'Т' | 'т';
fragment RU_U: 'У' | 'у';
fragment RU_F: 'Ф' | 'ф';
fragment RU_H: 'Х' | 'х';
fragment RU_C: 'Ц' | 'ц';
fragment RU_CH: 'Ч' | 'ч';
fragment RU_SH: 'Ш' | 'ш';
fragment RU_SCH: 'Щ' | 'щ';
fragment RU_SOLID_SIGN: 'Ъ' | 'ъ';
fragment RU_Y: 'Ы' | 'ы';
fragment RU_SOFT_SIGN: 'Ь' | 'ь';
fragment RU_EH: 'Э' | 'э';
fragment RU_YU: 'Ю' | 'ю';
fragment RU_YA: 'Я' | 'я';
fragment A: 'A' | 'a';
fragment B: 'B' | 'b';
fragment C: 'C' | 'c';
fragment D: 'D' | 'd';
fragment I: 'I' | 'i';
fragment J: 'J' | 'j';
fragment E: 'E' | 'e';
fragment F: 'F' | 'f';
fragment G: 'G' | 'g';
fragment U: 'U' | 'u';
fragment K: 'K' | 'k';
fragment L: 'L' | 'l';
fragment M: 'M' | 'm';
fragment N: 'N' | 'n';
fragment O: 'O' | 'o';
fragment P: 'P' | 'p';
fragment Q: 'Q' | 'q';
fragment R: 'R' | 'r';
fragment S: 'S' | 's';
fragment T: 'T' | 't';
fragment V: 'V' | 'v';
fragment H: 'H' | 'h';
fragment W: 'W' | 'w';
fragment X: 'X' | 'x';
fragment Y: 'Y' | 'y';

// LITERALS
fragment DIGIT: [0-9];
fragment LETTER: [\p{Letter}] | '_';

DECIMAL     : DIGIT+;
FLOAT       : DIGIT+ '.' DIGIT*;
STR         : '"' -> pushMode(STRINGS);
INCORRECT_IDENTIFIER  : DIGIT+ LETTER (LETTER | DIGIT)*;
IDENTIFIER  : LETTER (LETTER | DIGIT)*;
UNKNOWN: . -> channel(HIDDEN);

mode STRINGS;
STRFULL:  (~["\n\r] | '""')* '"' -> type(STR), popMode;
SKIPNEWLINE: [\n\r][ \t\f]* -> channel(HIDDEN), type(WHITE_SPACE);
STRPART:  (~["\n\r] | '""')+ -> type(STR);

// PARAMETERS
mode PARAMETER_MODE;
PARAMETER_WHITE_SPACE   : WHITE_SPACE -> channel(HIDDEN), type(WHITE_SPACE);
PARAMETER_IDENTIFIER    : IDENTIFIER -> popMode;
PARAMETER_UKNOWN        : . -> channel(HIDDEN), type(UNKNOWN);

mode DOT_MODE;
DOT_WHITE_SPACE         : [ \t\f]+ -> channel(HIDDEN), type(WHITE_SPACE);
DOT_MUL                 : MUL -> type(MUL), popMode;
DOT_LPAREN              : LPAREN -> type(LPAREN), popMode;
DOT_RPAREN              : RPAREN -> type(RPAREN), popMode;
DOT_ROUTEPOINT_FIELD    : ROUTEPOINT_FIELD -> type(ROUTEPOINT_FIELD), popMode;
DOT_EMPTYREF            : EMPTYREF -> type(EMPTYREF), popMode;

// VIRTUAL TABLES                 RU                                                                  EN
ACTUAL_ACTION_PERIOD_VT:
                (RU_F RU_A RU_K RU_T RU_I RU_CH RU_E RU_S RU_K RU_I RU_J RU_P RU_E RU_R RU_I RU_O RU_D RU_D RU_E RU_J RU_S RU_T RU_V RU_I RU_YA
                                                                                             | A C T U A L A C T I O N P E R I O D) -> popMode;
BALANCE_VT:     (BALANCE_RU                                                                  | BALANCE_EN) -> popMode;
BALANCE_AND_TURNOVERS_VT:
                (BALANCE_RU RU_I TURNOVERS_RU                                                | BALANCE_EN A N D TURNOVERS_EN) -> popMode;
BOUNDARIES_VT:  (RU_G RU_R RU_A RU_N RU_I RU_C RU_Y                                          | B O U N D A R I E S) -> popMode;
DR_CR_TURNOVERS_VT:
                (TURNOVERS_RU RU_D RU_T RU_K RU_T                                            | D R C R TURNOVERS_EN) -> popMode;
EXT_DIMENSIONS_VT:
                (EXT_DIMENSIONS_RU                                                           | EXT_DIMENSIONS_EN) -> popMode;
RECORDS_WITH_EXT_DIMENSIONS_VT:
                (RU_D RU_V RU_I RU_ZH RU_E RU_N RU_I RU_YA RU_S EXT_DIMENSIONS_RU            | R E C O R D S W I T H EXT_DIMENSIONS_EN) -> popMode;
SCHEDULE_DATA_VT:
                (RU_D RU_A RU_N RU_N RU_Y RU_E RU_G RU_R RU_A RU_F RU_I RU_K RU_A            | S C H E D U L E D A T A) -> popMode;
SLICEFIRST_VT:  (RU_S RU_R RU_E RU_Z RU_P RU_E RU_R RU_V RU_Y RU_H                           | S L I C E F I R S T) -> popMode;
SLICELAST_VT:   (RU_S RU_R RU_E RU_Z RU_P RU_O RU_S RU_L RU_E RU_D RU_N RU_I RU_H            | S L I C E L A S T) -> popMode;
TASK_BY_PERFORMER_VT:
                (RU_Z RU_A RU_D RU_A RU_CH RU_I RU_P RU_O RU_I RU_S RU_P RU_O RU_L RU_N RU_I RU_T RU_E RU_L RU_YU
                                                                                             | T A S K B Y P E R F O R M E R) -> popMode;
TURNOVERS_VT:   (TURNOVERS_RU                                                                | TURNOVERS_EN) -> popMode;

DOT_IDENTIFIER      : IDENTIFIER -> type(IDENTIFIER), popMode;

mode BRACE_MODE;
BRACE_WHITE_SPACE   : WHITE_SPACE -> channel(HIDDEN), type(WHITE_SPACE);
BRACE_IDENTIFIER    : IDENTIFIER -> channel(HIDDEN);
BRACE_START         : '{' -> pushMode(BRACE_MODE), channel(HIDDEN);
BRACE_END           : '}' -> channel(HIDDEN), type(UNKNOWN), popMode;
BRACE_UNKNOWN       : . -> channel(HIDDEN), type(UNKNOWN);

mode ID_MODE;
ID_IDENTIFIER    : IDENTIFIER -> type(IDENTIFIER), popMode;
ID_UNKNOWN       : . -> channel(HIDDEN), type(UNKNOWN);