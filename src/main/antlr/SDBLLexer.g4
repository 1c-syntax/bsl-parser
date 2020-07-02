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
lexer grammar SDBLLexer;

@members {
public SDBLLexer(CharStream input, boolean crAwareCostructor) {
  super(input);
  _interp = new CRAwareLexerATNSimulator(this, _ATN);
  validateInputStream(_ATN, input);
}
}

// commons
fragment DIGIT: [0-9];
LINE_COMMENT: '//' ~[\r\n]* -> channel(HIDDEN);
WHITE_SPACE: [ \t\f\r\n]+ -> channel(HIDDEN);

// separators
DOT: '.';
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
MODULO: '%';
AMPERSAND: '&' -> pushMode(PARAMETER_MODE);
BAR: '|';

// letters
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

// literals
TRUE:
    RU_I RU_S RU_T RU_I RU_N RU_A
    | T R U E
    ;
FALSE:
    RU_L RU_O RU_ZH RU_SOFT_SIGN
    | F A L S E
    ;
UNDEFINED:
    RU_N RU_E RU_O RU_P RU_R RU_E RU_D RU_E RU_L RU_E RU_N RU_O
    | U N D E F I N E D
    ;
NULL: N U L L;

DECIMAL: DIGIT+;
FLOAT : DIGIT+ '.' DIGIT*;
STRING: '"' (~[\r\n"] | '""')* '"';
STRINGSTART: '"' (~["\n\r]| '""')*;
STRINGTAIL: BAR (~["\n\r] | '""')* '"';
STRINGPART: BAR (~[\r\n"] | '""')*;

// keywords

AUTOORDER_KEYWORD:
    RU_A RU_V RU_T RU_O RU_U RU_P RU_O RU_R RU_YA RU_D RU_O RU_CH RU_I RU_V RU_A RU_N RU_I RU_E
    | A U T O O R D E R
    ;
BOOLEAN_KEYWORD:
    RU_B RU_U RU_L RU_E RU_V RU_O
    | B O O L E A N
    ;
IN_KEYWORD:
    RU_V
    | I N
    ;
OUTER_KEYWORD:
    RU_V RU_N RU_E RU_SH RU_N RU_E RU_E
    | O U T E R
    ;
INNER_KEYWORD:
    RU_V RU_N RU_U RU_T RU_R RU_E RU_N RU_N RU_E RU_E
    | I N N E R
    ;
ASC_KEYWORD:
    RU_V RU_O RU_Z RU_R
    | A S C
    ;
ALL_KEYWORD:
    RU_V RU_S RU_E
    | A L L
    ;
CASE_KEYWORD:
    RU_V RU_Y RU_B RU_O RU_R
    | C A S E
    ;
SELECT_KEYWORD:
    RU_V RU_Y RU_B RU_R RU_A RU_T RU_SOFT_SIGN
    | S E L E C T
    ;
CAST_KEYWORD:
    RU_V RU_Y RU_R RU_A RU_Z RU_I RU_T RU_SOFT_SIGN
    | C A S T
    ;
WHERE_KEYWORD:
    RU_G RU_D RU_E
    | W H E R E
    ;
YEAR_KEYWORD:
    RU_G RU_O RU_D
    | Y E A R
    ;
DATE_KEYWORD:
    RU_D RU_A RU_T RU_A
    | D A T E
    ;
DATETIME_KEYWORD:
    RU_D RU_A RU_T RU_A RU_V RU_R RU_E RU_M RU_YA
    | D A T E T I M E
    ;
TENDAYS_KEYWORD:
    RU_D RU_E RU_K RU_A RU_D RU_A
    | T E N D A Y S
    ;
DAY_KEYWORD:
    RU_D RU_E RU_N RU_SOFT_SIGN
    | D A Y
    ;
DAYOFYEAR_KEYWORD:
    RU_D RU_E RU_N RU_SOFT_SIGN RU_G RU_O RU_D RU_A
    | D A Y O F Y E A R
    ;
WEEKDAY_KEYWORD:
    RU_D RU_E RU_N RU_SOFT_SIGN RU_N RU_E RU_D RU_E RU_L RU_I
    | W E E K D A Y
    ;
FOR_KEYWORD:
    RU_D RU_L RU_YA
    | F O R
    ;
UPDATE_KEYWORD:
    RU_I RU_Z RU_M RU_E RU_N RU_E RU_N RU_I RU_YA
    | U P D A T E
    ;
OF_KEYWORD: O F; // only for english
DATEADD_KEYWORD:
    RU_D RU_O RU_B RU_A RU_V RU_I RU_T RU_SOFT_SIGN RU_K RU_D RU_A RU_T RU_E
    | D A T E A D D
    ;
IS_KEYWORD:
    RU_E RU_S RU_T RU_SOFT_SIGN
    | I S
    ;
ISNULL_KEYWORD:
    RU_E RU_S RU_T RU_SOFT_SIGN N U L L
    | I S N U L L
    ;
VALUE_KEYWORD:
    RU_Z RU_N RU_A RU_CH RU_E RU_N RU_I RU_E
    | V A L U E
    ;
AND_KEYWORD:
    RU_I
    | A N D
    ;
EN_HIERARCHY_KEYWORD: H I E R A R C H Y;
RU_HIERARCHYA_KEYWORD: RU_I RU_E RU_R RU_A RU_R RU_H RU_I RU_YA;
RU_HIERARCHII_KEYWORD: RU_I RU_E RU_R RU_A RU_R RU_H RU_I RU_I;
FROM_KEYWORD:
    RU_I RU_Z
    | F R O M
    ;
OR_KEYWORD:
    RU_I RU_L RU_I
    | O R
    ;
HAVING_KEYWORD:
    RU_I RU_M RU_E RU_YU RU_SCH RU_I RU_E
    | H A V I N G
    ;
ELSE_KEYWORD:
    RU_I RU_N RU_A RU_CH RU_E
    | E L S E
    ;
INDEX_KEYWORD:
    RU_I RU_N RU_D RU_E RU_K RU_S RU_I RU_R RU_O RU_V RU_A RU_T RU_SOFT_SIGN
    | I N D E X
    ;
EN_BY_KEYWORD: B Y;
RU_PO_KEYWORD: RU_P RU_O;
TOTALS_KEYWORD:
    RU_I RU_T RU_O RU_G RU_I
    | T O T A L S
    ;
AS_KEYWORD:
    RU_K RU_A RU_K
    | A S
    ;
QUARTER_KEYWORD:
    RU_K RU_V RU_A RU_R RU_T RU_A RU_L
    | Q U A R T E R
    ;
WHEN_KEYWORD:
    RU_K RU_O RU_G RU_D RU_A
    | W H E N
    ;
COUNT_KEYWORD:
    RU_K RU_O RU_L RU_I RU_CH RU_E RU_S RU_T RU_V RU_O
    | C O U N T
    ;
ENDOFPERIOD_KEYWORD:
    RU_K RU_O RU_N RU_E RU_C RU_P RU_E RU_R RU_I RU_O RU_D RU_A
    | E N D O F P E R I O D
    ;
END_KEYWORD:
    RU_K RU_O RU_N RU_E RU_C
    | E N D
    ;
LEFT_KEYWORD:
    RU_L RU_E RU_V RU_O RU_E
    | L E F T
    ;
MAX_KEYWORD:
    RU_M RU_A RU_K RU_S RU_I RU_M RU_U RU_M
    | M A X
    ;
BETWEEN_KEYWORD:
    RU_M RU_E RU_ZH RU_D RU_U
    | B E T W E E N
    ;
MONTH_KEYWORD:
    RU_M RU_E RU_S RU_YA RU_C
    | M O N T H
    ;
MIN_KEYWORD:
    RU_M RU_I RU_N RU_I RU_M RU_U RU_M
    | M I N
    ;
MINUTE_KEYWORD:
    RU_M RU_I RU_N RU_U RU_T RU_A
    | M I N U T E
    ;
BEGINOFPERIOD_KEYWORD:
    RU_N RU_A RU_CH RU_A RU_L RU_O RU_P RU_E RU_R RU_I RU_O RU_D RU_A
    | B E G I N O F P E R I O D
    ;
NOT_KEYWORD:
    RU_N RU_E
    | N O T
    ;
WEEK_KEYWORD:
    RU_N RU_E RU_D RU_E RU_L RU_YA
    | W E E K
    ;
OVERALL_KEYWORD:
    RU_O RU_B RU_SCH RU_I RU_E
    | O V E R A L L
    ;
UNION_KEYWORD:
    RU_O RU_B RU_SOLID_SIGN RU_E RU_D RU_I RU_N RU_I RU_T RU_SOFT_SIGN
    | U N I O N
    ;
TOP_KEYWORD:
    RU_P RU_E RU_R RU_V RU_Y RU_E
    | T O P
    ;
PERIODS_KEYWORD:
    RU_P RU_E RU_R RU_I RU_O RU_D RU_A RU_M RU_I
    | P E R I O D S
    ;
LIKE_KEYWORD:
    RU_P RU_O RU_D RU_O RU_B RU_N RU_O
    | L I K E
    ;
FULL_KEYWORD:
    RU_P RU_O RU_L RU_N RU_O RU_E
    | F U L L
    ;
HALFYEAR_KEYWORD:
    RU_P RU_O RU_L RU_U RU_G RU_O RU_D RU_I RU_E
    | H A L F Y E A R
    ;
INTO_KEYWORD:
    RU_P RU_O RU_M RU_E RU_S RU_T RU_I RU_T RU_SOFT_SIGN
    | I N T O
    ;
RIGHT_KEYWORD:
    RU_P RU_R RU_A RU_V RU_O RU_E
    | R I G H T
    ;
PRESENTATION_KEYWORD:
    RU_P RU_R RU_E RU_D RU_S RU_T RU_A RU_V RU_L RU_E RU_N RU_I RU_E
    | P R E S E N T A T I O N
    ;
EMPTYTABLE_KEYWORD:
    RU_P RU_U RU_S RU_T RU_A RU_YA RU_T RU_A RU_B RU_L RU_I RU_C RU_A
    | E M P T Y T A B L E
    ;
DISTINCT_KEYWORD:
    RU_R RU_A RU_Z RU_L RU_I RU_CH RU_N RU_Y RU_E
    | D I S T I N C T
    ;
ALLOWED_KEYWORD:
    RU_R RU_A RU_Z RU_R RU_E RU_SH RU_E RU_N RU_N RU_Y RU_E
    | A L L O W E D
    ;
GROUP_KEYWORD:
    RU_S RU_G RU_R RU_U RU_P RU_P RU_I RU_R RU_O RU_V RU_A RU_T RU_SOFT_SIGN
    | G R O U P
    ;
SECOND_KEYWORD:
    RU_S RU_E RU_K RU_U RU_N RU_D RU_A
    | S E C O N D
    ;
JOIN_KEYWORD:
    RU_S RU_O RU_E RU_D RU_I RU_N RU_E RU_N RU_I RU_E
    | J O I N
    ;
EN_ON_KEYWORD: O N;
ESCAPE_KEYWORD:
    RU_S RU_P RU_E RU_C RU_S RU_I RU_M RU_V RU_O RU_L
    | E S C A P E
    ;
SUBSTRING_KEYWORD:
    RU_P RU_O RU_D RU_S RU_T RU_R RU_O RU_K RU_A
    | S U B S T R I N G
    ;
AVG_KEYWORD:
    RU_S RU_R RU_E RU_D RU_N RU_E RU_E
    | A V G
    ;
REFS_KEYWORD:
    RU_S RU_S RU_Y RU_L RU_K RU_A
    | R E F S
    ;
STRING_KEYWORD:
    RU_S RU_T RU_R RU_O RU_K RU_A
    | S T R I N G
    ;
SUM_KEYWORD:
    RU_S RU_U RU_M RU_M RU_A
    | S U M
    ;
TYPE_KEYWORD:
    RU_T RU_I RU_P
    | T Y P E
    ;
VALUETYPE_KEYWORD:
    RU_T RU_I RU_P RU_Z RU_N RU_A RU_CH RU_E RU_N RU_I RU_YA
    | V A L U E T Y P E
    ;
THEN_KEYWORD:
    RU_T RU_O RU_G RU_D RU_A
    | T H E N
    ;
ONLY_KEYWORD:
    RU_T RU_O RU_L RU_SOFT_SIGN RU_K RU_O
    | O N L Y
    ;
DESC_KEYWORD:
    RU_U RU_B RU_Y RU_V
    | D E S C
    ;
ORDER_KEYWORD:
    RU_U RU_P RU_O RU_R RU_YA RU_D RU_O RU_CH RU_I RU_T RU_SOFT_SIGN
    | O R D E R
    ;
HOUR_KEYWORD:
    RU_CH RU_A RU_S
    | H O U R
    ;
NUMBER_KEYWORD:
    RU_CH RU_I RU_S RU_L RU_O
    | N U M B E R
    ;
DROP_KEYWORD:
    RU_U RU_N RU_I RU_CH RU_T RU_O RU_ZH RU_I RU_T RU_SOFT_SIGN
    | D R O P
    ;
DATEDIFF_KEYWORD:
    RU_R RU_A RU_Z RU_N RU_O RU_S RU_T RU_SOFT_SIGN RU_D RU_A RU_T
    | D A T E D I F F
    ;
AUTORECORDNUMBER_KEYWORD:
    RU_A RU_V RU_T RU_O RU_N RU_O RU_M RU_E RU_R RU_Z RU_A RU_P RU_I RU_S RU_I
    | A U T O R E C O R D N U M B E R
    ;

EMPTYREF_FIELD:
    RU_P RU_U RU_S RU_T RU_A RU_YA RU_S RU_S RU_Y RU_L RU_K RU_A
    | E M P T Y R E F
    ;
ROUTEPOINT_FIELD:
    RU_T RU_O RU_CH RU_K RU_A RU_M RU_A RU_R RU_SH RU_R RU_U RU_T RU_A
    | R O U T E P O I N T
    ;

BUSINESSPROCESS_TYPE:
    RU_B RU_I RU_Z RU_N RU_E RU_S RU_P RU_R RU_O RU_C RU_E RU_S RU_S
    | B U S I N E S S P R O C E S S
    ;
CATALOG_TYPE:
    RU_S RU_P RU_R RU_A RU_V RU_O RU_CH RU_N RU_I RU_K
    | C A T A L O G
    ;
DOCUMENT_TYPE:
    RU_D RU_O RU_K RU_U RU_M RU_E RU_N RU_T
    | D O C U M E N T
    ;
INFORMATION_REGISTER_TYPE:
    RU_R RU_E RU_G RU_I RU_S RU_T RU_R RU_S RU_V RU_E RU_D RU_E RU_N RU_I RU_J
    | I N F O R M A T I O N R E G I S T E R
    ;
CONSTANT_TYPE:
    RU_K RU_O RU_N RU_S RU_T RU_A RU_N RU_T RU_A
    | C O N S T A N T
    ;
FILTER_CRITERION_TYPE:
    RU_K RU_R RU_I RU_T RU_E RU_R RU_I RU_J RU_O RU_T RU_B RU_O RU_R RU_A
    | F I L T E R C R I T E R I O N
    ;
EXCHANGE_PLAN_TYPE:
    RU_P RU_L RU_A RU_N RU_O RU_B RU_M RU_E RU_N RU_A
    | E X C H A N G E P L A N
    ;
SEQUENCE_TYPE:
    RU_P RU_O RU_S RU_L RU_E RU_D RU_O RU_V RU_A RU_T RU_E RU_L RU_SOFT_SIGN RU_N RU_O RU_S RU_T RU_SOFT_SIGN
    | S E Q U E N C E
    ;
DOCUMENT_JOURNAL_TYPE:
    RU_ZH RU_U RU_R RU_N RU_A RU_L RU_D RU_O RU_K RU_U RU_M RU_E RU_N RU_T RU_O RU_V
    | D O C U M E N T J O U R N A L
    ;
ENUM_TYPE:
    RU_P RU_E RU_R RU_E RU_CH RU_I RU_S RU_L RU_E RU_N RU_I RU_E
    | E N U M
    ;
CHART_OF_CHARACTERISTIC_TYPES_TYPE:
    RU_P RU_L RU_A RU_N RU_V RU_I RU_D RU_O RU_V RU_H RU_A RU_R RU_A RU_K RU_T RU_E RU_R RU_I RU_S RU_T RU_I RU_K
    | C H A R T O F C H A R A C T E R I S T I C T Y P E S
    ;
CHART_OF_ACCOUNTS_TYPE:
    RU_P RU_L RU_A RU_N RU_S RU_CH RU_E RU_T RU_O RU_V
    | C H A R T O F A C C O U N T S
    ;
CHART_OF_CALCULATION_TYPES_TYPE:
    RU_P RU_L RU_A RU_N RU_V RU_I RU_D RU_O RU_V RU_R RU_A RU_S RU_CH RU_E RU_T RU_A
    | C H A R T O F C A L C U L A T I O N T Y P E S
    ;
ACCUMULATION_REGISTER_TYPE:
    RU_R RU_E RU_G RU_I RU_S RU_T RU_R RU_N RU_A RU_K RU_O RU_P RU_L RU_E RU_N RU_I RU_YA
    | A C C U M U L A T I O N R E G I S T E R
    ;
ACCOUNTING_REGISTER_TYPE:
    RU_R RU_E RU_G RU_I RU_S RU_T RU_R RU_B RU_U RU_H RU_G RU_A RU_L RU_T RU_E RU_R RU_I RU_I
    | A C C O U N T I N G R E G I S T E R
    ;
CALCULATION_REGISTER_TYPE:
    RU_R RU_E RU_G RU_I RU_S RU_T RU_R RU_R RU_A RU_S RU_CH RU_E RU_T RU_A
    | C A L C U L A T I O N R E G I S T E R
    ;
TASK_TYPE:
    RU_Z RU_A RU_D RU_A RU_CH RU_A
    | T A S K
    ;
EXTERNAL_DATA_SOURCE_TYPE:
    RU_V RU_N RU_E RU_SH RU_N RU_I RU_J RU_I RU_S RU_T RU_O RU_CH RU_N RU_I RU_K RU_D RU_A RU_N RU_N RU_Y RU_H
    | E X T E R N A L D A T A S O U R C E
    ;

SLICELAST_TT:
    RU_S RU_R RU_E RU_Z RU_P RU_O RU_S RU_L RU_E RU_D RU_N RU_I RU_H
    | S L I C E L A S T
    ;
SLICEFIRST_TT:
    RU_S RU_R RU_E RU_Z RU_P RU_E RU_R RU_V RU_Y RU_H
    | S L I C E F I R S T
    ;
BOUNDARIES_TT:
    RU_G RU_R RU_A RU_N RU_I RU_C RU_Y
    | B O U N D A R I E S
    ;
TURNOVERS_TT:
    RU_O RU_B RU_O RU_R RU_O RU_T RU_Y
    | T U R N O V E R S
    ;
BALANCE_TT:
    RU_O RU_S RU_T RU_A RU_T RU_K RU_I
    | B A L A N C E
    ;
BALANCE_AND_TURNOVERS_TT:
    RU_O RU_S RU_T RU_A RU_T RU_K RU_I RU_I RU_O RU_B RU_O RU_R RU_O RU_T RU_Y
    | B A L A N C E A N D T U R N O V E R S
    ;
EXT_DIMENSIONS_TT:
    RU_S RU_U RU_B RU_K RU_O RU_N RU_T RU_O
    | E X T D I M E N S I O N S
    ;
RECORDS_WITH_EXT_DIMENSIONS_TT:
    RU_D RU_V RU_I RU_ZH RU_E RU_N RU_I RU_YA RU_S RU_S RU_U RU_B RU_K RU_O RU_N RU_T RU_O
    | R E C O R D S W I T H E X T D I M E N S I O N S
    ;
DR_CR_TURNOVERS_TT:
    RU_O RU_B RU_O RU_R RU_O RU_T RU_Y RU_D RU_T RU_K RU_T
    | D R C R T U R N O V E R S
    ;
ACTUAL_ACTION_PERIOD_TT:
    RU_F RU_A RU_K RU_T RU_I RU_CH RU_E RU_S RU_K RU_I RU_J RU_P RU_E RU_R RU_I RU_O RU_D RU_D RU_E RU_J RU_S RU_T RU_V RU_I RU_YA
    | A C T U A L A C T I O N P E R I O D
    ;
SCHEDULE_DATA_TT:
    RU_D RU_A RU_N RU_N RU_Y RU_E RU_G RU_R RU_A RU_F RU_I RU_K RU_A
    | S C H E D U L E D A T A
    ;
TASK_BY_PERFORMER_TT:
    RU_Z RU_A RU_D RU_A RU_CH RU_I RU_P RU_O RU_I RU_S RU_P RU_O RU_L RU_N RU_I RU_T RU_E RU_L RU_YU
    | T A S K B Y P E R F O R M E R
    ;

fragment LETTER: [\p{Letter}] | '_';
IDENTIFIER : LETTER (LETTER | DIGIT)*;

UNKNOWN: . -> channel(HIDDEN);

mode PARAMETER_MODE;
PARAMETER_WHITE_SPACE
    : [ \n\r\t\f]+
    -> channel(HIDDEN),
       type(WHITE_SPACE)
    ;

PARAMETER_IDENTIFIER : LETTER (LETTER | DIGIT)* -> popMode;

PARAMETER_UKNOWN
    : . -> channel(HIDDEN)
    ;