/**
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018-2022
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
lexer grammar BSLMethodDescriptionLexer;

@members {
public BSLMethodDescriptionLexer(CharStream input, boolean crAwareCostructor) {
  super(input);
  _interp = new CRAwareLexerATNSimulator(this, _ATN);
  validateInputStream(_ATN, input);
}
}

// KEYWORDS
PARAMETERS_KEYWORD:     (P A R A M E T E R S        | RU_P RU_A RU_R RU_A RU_M RU_E RU_T RU_R RU_Y) ':';
RETURNS_KEYWORD:        (R E T U R N S              | (RU_V RU_O RU_Z RU_V RU_R RU_A RU_SCH RU_A RU_E RU_M RU_O RU_E ' ' RU_Z RU_N RU_A RU_CH RU_E RU_N RU_I RU_E)) ':';
EXAMPLE_KEYWORD:        (E X A M P L E S?           | RU_P RU_R RU_I RU_M RU_E RU_R RU_Y?) ':';
CALL_OPTIONS_KEYWORD:   (C A L L ' ' O P T I O N S  | RU_V RU_A RU_R RU_I RU_A RU_N RU_T RU_Y ' ' RU_V RU_Y RU_Z RU_O RU_V RU_A) ':';
DEPRECATE_KEYWORD:      (D E P R E C A T E          | RU_U RU_S RU_T RU_A RU_R RU_E RU_L RU_A) '.'?;
SEE_KEYWORD:            (S E E                      | RU_S RU_M '.');
OF_KEYWORD:             (O F | C O N T A I N S      | RU_I RU_Z);

// COMMON
EOL     : '\r'? '\n';
SPACE   : [ \t]+;
STAR    : '*'+;
DASH    : [-–];
COLON   : ':';
COMMA   : ',';
// OTHER
COMMENT : '//';
WORD    : LETTER (LETTER | DIGIT)*;
DOTSWORD: LETTER (LETTER | DIGIT)* ('.' LETTER (LETTER | DIGIT)*)+;
LPAREN  : '(';
RPAREN  : ')';

ANYSYMBOL: .;

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
