/**
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018
 * Alexey Sosnoviy <labotamy@yandex.ru>, Nikita Gryzlov <nixel2007@gmail.com>, Sergey Batanov <sergey.batanov@dmpas.ru>
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
lexer grammar BSLLexer;

@lexer::members {
    int lastTokenType = 0;
    public void emit(Token token) {
        super.emit(token);
        lastTokenType = token.getType();
    }
}

// commons
fragment DIGIT: [0-9];
LINE_COMMENT: '//' ~[\r\n]* -> channel(HIDDEN);
WHITE_SPACE: [ \n\r\t\f] -> channel(HIDDEN);

// separators
DOT: '.';
LBRACK: '[';
RBRACK: ']';
LPAREN: '(';
RPAREN: ')';
COLON: ':';
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
QUESTION: '?';
AMPERSAND: '&';
HASH: '#' -> pushMode(PREPROCESSOR_MODE);

QUOTE: '"';
SQUOTE: '\'';
BAR: '|';

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
fragment R: 'R' | 'r';   
fragment S: 'S' | 's';   
fragment T: 'T' | 't';
fragment V: 'V' | 'v';   
fragment H: 'H' | 'h';   
fragment W: 'W' | 'w';   
fragment X: 'X' | 'x';   
fragment Y: 'Y' | 'y';   

// literals
TRUE: RU_I RU_S RU_T RU_I RU_N RU_A | T R U E;
FALSE: RU_L RU_O RU_ZH RU_SOFT_SIGN | F A L S E;
UNDEFINED: RU_N RU_E RU_O RU_P RU_R RU_E RU_D RU_E RU_L RU_E RU_N RU_O | U N D E F I N E D;
NULL: N U L L;
DECIMAL: DIGIT+;
DATETIME: SQUOTE(~['\n\r])*SQUOTE?; // TODO: Честная регулярка

FLOAT : DIGIT+ '.' DIGIT*;
STRINGSTART: QUOTE(~["\n\r])*;
STRING: QUOTE(~["\n\r])*QUOTE;
STRINGTAIL: BAR(~["\n\r])*QUOTE;
STRINGPART: BAR(~["\n\r])*;

// keywords
PROCEDURE_KEYWORD: RU_P RU_R RU_O RU_C RU_E RU_D RU_U RU_R RU_A | P R O C E D U R E;
FUNCTION_KEYWORD: RU_F RU_U RU_N RU_K RU_C RU_I RU_YA | F U N C T I O N;
ENDPROCEDURE_KEYWORD: RU_K RU_O RU_N RU_E RU_C RU_P RU_R RU_O RU_C RU_E RU_D RU_U RU_R RU_Y | E N D P R O C E D U R E;
ENDFUNCTION_KEYWORD: RU_K RU_O RU_N RU_E RU_C RU_F RU_U RU_N RU_K RU_C RU_I RU_I | E N D F U N C T I O N;
EXPORT_KEYWORD: RU_EH RU_K RU_S RU_P RU_O RU_R RU_T | E X P O R T;
VAL_KEYWORD: RU_Z RU_N RU_A RU_CH | V A L;
ENDIF_KEYWORD: RU_K RU_O RU_N RU_E RU_C RU_E RU_S RU_L RU_I | E N D I F;
ENDDO_KEYWORD: RU_K RU_O RU_N RU_E RU_C RU_C RU_I RU_K RU_L RU_A | E N D D O;
IF_KEYWORD: RU_E RU_S RU_L RU_I | I F;
ELSEIF_KEYWORD: RU_I RU_N RU_A RU_CH RU_E RU_E RU_S RU_L RU_I | E L S I F;
ELSE_KEYWORD: RU_I RU_N RU_A RU_CH RU_E | E L S E;
THEN_KEYWORD: RU_T RU_O RU_G RU_D RU_A | T H E N;
WHILE_KEYWORD: RU_P RU_O RU_K RU_A | W H I L E;
DO_KEYWORD: RU_C RU_I RU_K RU_L | D O;
FOR_KEYWORD: RU_D RU_L RU_YA | F O R;
TO_KEYWORD: RU_P RU_O | T O;
EACH_KEYWORD: RU_K RU_A RU_ZH RU_D RU_O RU_G RU_O | E A C H;
FROM_KEYWORD: RU_I RU_Z | F R O M;
TRY_KEYWORD: RU_P RU_O RU_P RU_Y RU_T RU_K RU_A | T R Y;
EXCEPT_KEYWORD: RU_I RU_S RU_K RU_L RU_YU RU_CH RU_E RU_N RU_I RU_E | E X C E P T;
ENDTRY_KEYWORD: RU_K RU_O RU_N RU_E RU_C RU_P RU_O RU_P RU_Y RU_T RU_K RU_I | E N D T R Y;
RETURN_KEYWORD: RU_V RU_O RU_Z RU_V RU_R RU_A RU_T | R E T U R N;
CONTINUE_KEYWORD: RU_P RU_R RU_O RU_D RU_O RU_L RU_ZH RU_I RU_T RU_SOFT_SIGN | C O N T I N U E;
RAISE_KEYWORD: RU_V RU_Y RU_Z RU_V RU_A RU_T RU_SOFT_SIGN RU_I RU_S RU_K RU_L RU_YU RU_CH RU_E RU_N RU_I RU_E | R A I S E;
VAR_KEYWORD: RU_P RU_E RU_R RU_E RU_M | V A R;
NOT_KEYWORD: RU_N RU_E| N O T;
OR_KEYWORD: RU_I RU_L RU_I | O R;
AND_KEYWORD: RU_I | A N D;
NEW_KEYWORD: RU_N RU_O RU_V RU_Y RU_J| N E W;
GOTO_KEYWORD: RU_P RU_E RU_R RU_E RU_J RU_T RU_I | G O T O;
BREAK_KEYWORD: RU_P RU_R RU_E RU_R RU_V RU_A RU_T RU_SOFT_SIGN | B R E A K;
EXECUTE_KEYWORD: (RU_V RU_Y RU_P RU_O RU_L RU_N RU_I RU_T RU_SOFT_SIGN | E X E C U T E) { if (lastTokenType == DOT) { setType(IDENTIFIER); } };

fragment LETTER: [\p{Letter}] | '_';   
IDENTIFIER : LETTER ( LETTER | DIGIT )*;

UNKNOWN: . -> channel(HIDDEN);

mode PREPROCESSOR_MODE;

PREPROC_EXCLAMATION_MARK: '!';

PREPROC_STRINGSTART: QUOTE(~["\n\r])*;
PREPROC_STRING: QUOTE(~["\n\r])*QUOTE;
PREPROC_STRINGTAIL: BAR(~["\n\r])*QUOTE;
PREPROC_STRINGPART: BAR(~["\n\r])*;

PREPROC_USE_KEYWORD: RU_I RU_S RU_P RU_O RU_L RU_SOFT_SIGN RU_Z RU_O RU_V RU_A RU_T RU_SOFT_SIGN | U S E;

PREPROC_REGION: (RU_O RU_B RU_L RU_A RU_S RU_T RU_SOFT_SIGN | R E G I O N);
PREPROC_END_REGION: (RU_K RU_O RU_N RU_E RU_C RU_O RU_B RU_L RU_A RU_S RU_T RU_I| E N D R E G I O N);

PREPROC_IDENTIFIER : LETTER ( LETTER | DIGIT )*;

PREPROC_WHITE_SPACE: [ \t\f] -> channel(HIDDEN);
PREPROC_NEWLINE: [\r\n] -> popMode, channel(HIDDEN);

PREPROC_ANY: ~[\r\n];
