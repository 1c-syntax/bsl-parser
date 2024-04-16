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
lexer grammar BSLLexer;

channels {
    // для хранения удаленного блока
    PREPROC_DELETE_CHANNEL
}

@members {
public BSLLexer(CharStream input, boolean crAwareCostructor) {
  super(input);
  _interp = new CRAwareLexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
}
}

// commons
fragment DIGIT: [0-9];
LINE_COMMENT: '//' ~[\r\n]* -> channel(HIDDEN);
WHITE_SPACE: [ \t\f\r\n]+ -> channel(HIDDEN);

// separators
DOT: '.' -> pushMode(DOT_MODE);
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
AMPERSAND: '&' -> pushMode(ANNOTATION_MODE);
PREPROC_DELETE
    : '#' [ \t]*
    (
          RU_U RU_D RU_A RU_L RU_E RU_N RU_I RU_E
        | D E L E T E
    )
    -> pushMode(PREPROC_DELETE_MODE), channel(PREPROC_DELETE_CHANNEL)
    ;
PREPROC_INSERT
    : '#' [ \t]*
    (
          RU_V RU_S RU_T RU_A RU_V RU_K RU_A
        | I N S E R T
    )
    -> channel(HIDDEN)
    ;
PREPROC_ENDINSERT
    : '#' [ \t]*
    (
          RU_K RU_O RU_N RU_E RU_C RU_V RU_S RU_T RU_A RU_V RU_K RU_I
        | E N D I N S E R T
    )
    -> channel(HIDDEN)
    ;
HASH: '#' -> pushMode(PREPROCESSOR_MODE);

fragment SQUOTE: '\'';
BAR: '|';
TILDA: '~' -> pushMode(LABEL_MODE);

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
TRUE
    :
     RU_I RU_S RU_T RU_I RU_N RU_A
    | T R U E
    ;
FALSE
    :
     RU_L RU_O RU_ZH RU_SOFT_SIGN
    | F A L S E
    ;
UNDEFINED
    :
     RU_N RU_E RU_O RU_P RU_R RU_E RU_D RU_E RU_L RU_E RU_N RU_O
    | U N D E F I N E D
    ;
NULL
    :
    N U L L
    ;
DECIMAL: DIGIT+;
DATETIME: SQUOTE(~['\n\r])*SQUOTE?; // TODO: Честная регулярка

FLOAT : DIGIT+ '.' DIGIT*;
STRING: '"' (~[\r\n"] | '""')* '"';
STRINGSTART: '"' (~["\n\r]| '""')*;
STRINGTAIL: BAR (~["\n\r] | '""')* '"';
STRINGPART: BAR (~[\r\n"] | '""')*;

// keywords
PROCEDURE_KEYWORD
    :
     RU_P RU_R RU_O RU_C RU_E RU_D RU_U RU_R RU_A
    | P R O C E D U R E
    ;
FUNCTION_KEYWORD
    :
     RU_F RU_U RU_N RU_K RU_C RU_I RU_YA
    | F U N C T I O N
    ;
ENDPROCEDURE_KEYWORD
    :(RU_K RU_O RU_N RU_E RU_C RU_P RU_R RU_O RU_C RU_E RU_D RU_U RU_R RU_Y
    | E N D P R O C E D U R E)
    ;
ENDFUNCTION_KEYWORD
    :(RU_K RU_O RU_N RU_E RU_C RU_F RU_U RU_N RU_K RU_C RU_I RU_I
    | E N D F U N C T I O N)
    ;
EXPORT_KEYWORD
    :
     RU_EH RU_K RU_S RU_P RU_O RU_R RU_T
    | E X P O R T
    ;
VAL_KEYWORD
    :
     RU_Z RU_N RU_A RU_CH
    | V A L
    ;
ENDIF_KEYWORD
    :
     RU_K RU_O RU_N RU_E RU_C RU_E RU_S RU_L RU_I
    | E N D I F
    ;
ENDDO_KEYWORD
    :
     RU_K RU_O RU_N RU_E RU_C RU_C RU_I RU_K RU_L RU_A
    | E N D D O
    ;
IF_KEYWORD
    :
     RU_E RU_S RU_L RU_I
    | I F
    ;
ELSIF_KEYWORD
    :
     RU_I RU_N RU_A RU_CH RU_E RU_E RU_S RU_L RU_I
    | E L S I F
    ;
ELSE_KEYWORD
    :
     RU_I RU_N RU_A RU_CH RU_E
    | E L S E
    ;
THEN_KEYWORD
    :
     RU_T RU_O RU_G RU_D RU_A
    | T H E N
    ;
WHILE_KEYWORD
    :
     RU_P RU_O RU_K RU_A
    | W H I L E
    ;
DO_KEYWORD
    :
     RU_C RU_I RU_K RU_L
    | D O
    ;
FOR_KEYWORD
    :
     RU_D RU_L RU_YA
    | F O R
    ;
TO_KEYWORD
    :
     RU_P RU_O
    | T O
    ;
EACH_KEYWORD
    :
     RU_K RU_A RU_ZH RU_D RU_O RU_G RU_O
    | E A C H
    ;
IN_KEYWORD
    :
     RU_I RU_Z
    | I N
    ;
TRY_KEYWORD
    :
     RU_P RU_O RU_P RU_Y RU_T RU_K RU_A
    | T R Y
    ;
EXCEPT_KEYWORD
    :
     RU_I RU_S RU_K RU_L RU_YU RU_CH RU_E RU_N RU_I RU_E
    | E X C E P T
    ;
ENDTRY_KEYWORD
    :
     RU_K RU_O RU_N RU_E RU_C RU_P RU_O RU_P RU_Y RU_T RU_K RU_I
    | E N D T R Y
    ;
RETURN_KEYWORD
    :
     RU_V RU_O RU_Z RU_V RU_R RU_A RU_T
    | R E T U R N
    ;
CONTINUE_KEYWORD
    :
     RU_P RU_R RU_O RU_D RU_O RU_L RU_ZH RU_I RU_T RU_SOFT_SIGN
    | C O N T I N U E
    ;
RAISE_KEYWORD
    :
     RU_V RU_Y RU_Z RU_V RU_A RU_T RU_SOFT_SIGN RU_I RU_S RU_K RU_L RU_YU RU_CH RU_E RU_N RU_I RU_E
    | R A I S E
    ;
VAR_KEYWORD
    :
     RU_P RU_E RU_R RU_E RU_M
    | V A R
    ;
NOT_KEYWORD
    :
     RU_N RU_E
    | N O T
    ;
OR_KEYWORD
    :
     RU_I RU_L RU_I
    | O R
    ;
AND_KEYWORD
    :
     RU_I
    | A N D
    ;
NEW_KEYWORD
    :
     RU_N RU_O RU_V RU_Y RU_J
    | N E W
    ;
GOTO_KEYWORD
    :
     RU_P RU_E RU_R RU_E RU_J RU_T RU_I
    | G O T O
    ;
BREAK_KEYWORD
    :
     RU_P RU_R RU_E RU_R RU_V RU_A RU_T RU_SOFT_SIGN
    | B R E A K
    ;
EXECUTE_KEYWORD
    :
     RU_V RU_Y RU_P RU_O RU_L RU_N RU_I RU_T RU_SOFT_SIGN
    | E X E C U T E
    ;
ADDHANDLER_KEYWORD
    :
     RU_D RU_O RU_B RU_A RU_V RU_I RU_T RU_SOFT_SIGN RU_O RU_B RU_R RU_A RU_B RU_O RU_T RU_CH RU_I RU_K
    | A D D H A N D L E R
    ;
REMOVEHANDLER_KEYWORD
    :
     RU_U RU_D RU_A RU_L RU_I RU_T RU_SOFT_SIGN RU_O RU_B RU_R RU_A RU_B RU_O RU_T RU_CH RU_I RU_K
    | R E M O V E H A N D L E R
    ;
ASYNC_KEYWORD
    : (RU_A RU_S RU_I RU_N RU_H
    | A S Y N C) -> pushMode(ASYNC_MODE)
    ;

fragment LETTER: [\p{Letter}] | '_';
IDENTIFIER : LETTER ( LETTER | DIGIT )*;

UNKNOWN: . -> channel(HIDDEN);

mode PREPROCESSOR_MODE;

PREPROC_EXCLAMATION_MARK: '!';
PREPROC_LPAREN: '(';
PREPROC_RPAREN: ')';

PREPROC_STRING: '"' (~["\n\r])* '"';

PREPROC_NATIVE
    : N A T I V E
    ;

PREPROC_USE_KEYWORD
    :
    (RU_I RU_S RU_P RU_O RU_L RU_SOFT_SIGN RU_Z RU_O RU_V RU_A RU_T RU_SOFT_SIGN
    | U S E) -> pushMode(USE_MODE);

PREPROC_REGION
    :
    ( RU_O RU_B RU_L RU_A RU_S RU_T RU_SOFT_SIGN
    | R E G I O N ) -> pushMode(REGION_MODE)
    ;
PREPROC_END_REGION
    :
    ( RU_K RU_O RU_N RU_E RU_C RU_O RU_B RU_L RU_A RU_S RU_T RU_I
    | E N D R E G I O N )
    ;

PREPROC_NOT_KEYWORD
    :
      RU_N RU_E
    | N O T
    ;
PREPROC_OR_KEYWORD
    :
      RU_I RU_L RU_I
    | O R
    ;
PREPROC_AND_KEYWORD
    :
      RU_I
    | A N D
    ;

PREPROC_IF_KEYWORD
    :
      RU_E RU_S RU_L RU_I
    | I F
    ;
PREPROC_THEN_KEYWORD
    :
      RU_T RU_O RU_G RU_D RU_A
    | T H E N
    ;
PREPROC_ELSIF_KEYWORD
    :
      RU_I RU_N RU_A RU_CH RU_E RU_E RU_S RU_L RU_I
    | E L S I F
    ;
PREPROC_ENDIF_KEYWORD
    :
      RU_K RU_O RU_N RU_E RU_C RU_E RU_S RU_L RU_I
    | E N D I F
    ;
PREPROC_ELSE_KEYWORD
    :
      RU_I RU_N RU_A RU_CH RU_E
    | E L S E
    ;

PREPROC_MOBILEAPPCLIENT_SYMBOL
    :
      RU_M RU_O RU_B RU_I RU_L RU_SOFT_SIGN RU_N RU_O RU_E
      RU_P RU_R RU_I RU_L RU_O RU_ZH RU_E RU_N RU_I RU_E
      RU_K RU_L RU_I RU_E RU_N RU_T
    | M O B I L E
      A P P
      C L I E N T
    ;
PREPROC_MOBILEAPPSERVER_SYMBOL
    :
      RU_M RU_O RU_B RU_I RU_L RU_SOFT_SIGN RU_N RU_O RU_E
      RU_P RU_R RU_I RU_L RU_O RU_ZH RU_E RU_N RU_I RU_E
      RU_S RU_E RU_R RU_V RU_E RU_R
    | M O B I L E
      A P P
      S E R V E R
    ;
PREPROC_MOBILECLIENT_SYMBOL
    :
      RU_M RU_O RU_B RU_I RU_L RU_SOFT_SIGN RU_N RU_Y RU_J
      RU_K RU_L RU_I RU_E RU_N RU_T
    | M O B I L E
      C L I E N T
    ;
PREPROC_THICKCLIENTORDINARYAPPLICATION_SYMBOL
    :
      RU_T RU_O RU_L RU_S RU_T RU_Y RU_J
      RU_K RU_L RU_I RU_E RU_N RU_T
      RU_O RU_B RU_Y RU_CH RU_N RU_O RU_E
      RU_P RU_R RU_I RU_L RU_O RU_ZH RU_E RU_N RU_I RU_E
    | T H I C K
      C L I E N T
      O R D I N A R Y
      A P P L I C A T I O N
    ;
PREPROC_THICKCLIENTMANAGEDAPPLICATION_SYMBOL
    :
      RU_T RU_O RU_L RU_S RU_T RU_Y RU_J
      RU_K RU_L RU_I RU_E RU_N RU_T
      RU_U RU_P RU_R RU_A RU_V RU_L RU_YA RU_E RU_M RU_O RU_E
      RU_P RU_R RU_I RU_L RU_O RU_ZH RU_E RU_N RU_I RU_E
    | T H I C K
      C L I E N T
      M A N A G E D
      A P P L I C A T I O N
    ;
PREPROC_EXTERNALCONNECTION_SYMBOL
    :
      RU_V RU_N RU_E RU_SH RU_N RU_E RU_E
      RU_S RU_O RU_E RU_D RU_I RU_N RU_E RU_N RU_I RU_E
    | E X T E R N A L
      C O N N E C T I O N
    ;
PREPROC_THINCLIENT_SYMBOL
    :
      RU_T RU_O RU_N RU_K RU_I RU_J
      RU_K RU_L RU_I RU_E RU_N RU_T
    | T H I N
      C L I E N T
    ;
PREPROC_WEBCLIENT_SYMBOL
    :
      RU_V RU_E RU_B
      RU_K RU_L RU_I RU_E RU_N RU_T
    | W E B
      C L I E N T
    ;
PREPROC_ATCLIENT_SYMBOL
    :
      RU_N RU_A RU_K RU_L RU_I RU_E RU_N RU_T RU_E
    | A T C L I E N T
    ;
PREPROC_CLIENT_SYMBOL
    :
      RU_K RU_L RU_I RU_E RU_N RU_T
    | C L I E N T
    ;
PREPROC_ATSERVER_SYMBOL
    :
      RU_N RU_A RU_S RU_E RU_R RU_V RU_E RU_R RU_E
    | A T S E R V E R
    ;
PREPROC_SERVER_SYMBOL
    :
      RU_S RU_E RU_R RU_V RU_E RU_R
    | S E R V E R
    ;
PREPROC_MOBILE_STANDALONE_SERVER
    :
      RU_M RU_O RU_B RU_I RU_L RU_SOFT_SIGN RU_N RU_Y RU_J
      RU_A RU_V RU_T RU_O RU_N RU_O RU_M RU_N RU_Y RU_J
      RU_S RU_E RU_R RU_V RU_E RU_R
    | M O B I L E S T A N D A L O N E S E R V E R
    ;

PREPROC_LINUX   : L I N U X;
PREPROC_WINDOWS : W I N D O W S;
PREPROC_MACOS   : M A C O S;

PREPROC_IDENTIFIER : LETTER ( LETTER | DIGIT )*;

PREPROC_WHITE_SPACE: [ \t\f]+ -> channel(HIDDEN), type(WHITE_SPACE);
PREPROC_LINE_COMMENT: '//' ~[\r\n]* -> channel(HIDDEN), type(LINE_COMMENT);
PREPROC_NEWLINE: '\r'?'\n' -> popMode, channel(HIDDEN);

PREPROC_ANY: ~[\r\n];

mode ANNOTATION_MODE;

ANNOTATION_ATSERVERNOCONTEXT_SYMBOL
    : (
      RU_N RU_A RU_S RU_E RU_R RU_V RU_E RU_R RU_E
      RU_B RU_E RU_Z RU_K RU_O RU_N RU_T RU_E RU_K RU_S RU_T RU_A
    | A T S E R V E R
      N O C O N T E X T
    ) -> popMode
    ;

ANNOTATION_ATCLIENTATSERVERNOCONTEXT_SYMBOL
    : (
      RU_N RU_A RU_K RU_L RU_I RU_E RU_N RU_T RU_E
      RU_N RU_A RU_S RU_E RU_R RU_V RU_E RU_R RU_E
      RU_B RU_E RU_Z RU_K RU_O RU_N RU_T RU_E RU_K RU_S RU_T RU_A
    | A T C L I E N T
      A T S E R V E R
      N O C O N T E X T
    ) -> popMode
    ;

ANNOTATION_ATCLIENTATSERVER_SYMBOL
    : (
      RU_N RU_A RU_K RU_L RU_I RU_E RU_N RU_T RU_E
      RU_N RU_A RU_S RU_E RU_R RU_V RU_E RU_R RU_E
    | A T C L I E N T
      A T S E R V E R
    ) -> popMode
    ;

ANNOTATION_ATCLIENT_SYMBOL
    : (
      RU_N RU_A RU_K RU_L RU_I RU_E RU_N RU_T RU_E
    | A T C L I E N T
    ) -> popMode
    ;

ANNOTATION_ATSERVER_SYMBOL
    : ( RU_N RU_A RU_S RU_E RU_R RU_V RU_E RU_R RU_E
    | A T S E R V E R
    ) -> popMode
    ;

ANNOTATION_BEFORE_SYMBOL
    : ( RU_P RU_E RU_R RU_E RU_D
    | B E F O R E
    ) -> popMode
    ;

ANNOTATION_AFTER_SYMBOL
    : ( RU_P RU_O RU_S RU_L RU_E
    | A F T E R
    ) -> popMode
    ;

ANNOTATION_AROUND_SYMBOL
    : ( RU_V RU_M RU_E RU_S RU_T RU_O
    | A R O U N D
    ) -> popMode
    ;

ANNOTATION_CHANGEANDVALIDATE_SYMBOL
    : (  RU_I RU_Z RU_M RU_E RU_N RU_E RU_N RU_I RU_E RU_I RU_K RU_O RU_N RU_T RU_R RU_O RU_L RU_SOFT_SIGN
    | C H A N G E A N D V A L I D A T E
    ) -> popMode
    ;

ANNOTATION_CUSTOM_SYMBOL
    : (
    LETTER ( LETTER | DIGIT )*
    ) -> popMode
    ;

ANNOTATION_WHITE_SPACE
    : [ \n\r\t\f]+
    -> channel(HIDDEN),
       type(WHITE_SPACE)
    ;

ANNOTATION_UNKNOWN
    : .
    -> channel(HIDDEN)
    ;

mode LABEL_MODE;
LABEL_IDENTIFIER : LETTER ( LETTER | DIGIT )* -> type(IDENTIFIER), popMode;

mode REGION_MODE;
REGION_WHITE_SPACE
    : [ \t\f]+
    -> channel(HIDDEN),
       type(WHITE_SPACE)
    ;
REGION_IDENTIFIER : LETTER ( LETTER | DIGIT )* -> type(PREPROC_IDENTIFIER), popMode;

mode USE_MODE;
fragment USE_LETTER: [\p{Letter}] | '_' | '-';
USE_WHITE_SPACE
    : [ \t\f]+
    -> channel(HIDDEN),
       type(WHITE_SPACE)
    ;
USE_STRING : '"' (~["\n\r])* '"' -> type(PREPROC_STRING), popMode;
USE_IDENTIFIER : ( USE_LETTER | DIGIT )+ -> type(PREPROC_IDENTIFIER), popMode;

mode DOT_MODE;
DOT_WHITE_SPACE
    : [ \t\f]+
    -> channel(HIDDEN),
       type(WHITE_SPACE)
    ;
DOT_IDENTIFIER : LETTER ( LETTER | DIGIT )* -> type(IDENTIFIER), popMode;

mode PREPROC_DELETE_MODE;
PREPROC_ENDDELETE
    : '#' [ \t]*
    (
          RU_K RU_O RU_N RU_E RU_C RU_U RU_D RU_A RU_L RU_E RU_N RU_I RU_YA
        | E N D D E L E T E
    )
    -> popMode, channel(PREPROC_DELETE_CHANNEL);
PREPROC_DELETE_WHITE_SPACE: [ \t\f]+ -> channel(HIDDEN), type(WHITE_SPACE);
PREPROC_DELETE_LINE_COMMENT: '//' ~[\r\n]* -> channel(HIDDEN), type(LINE_COMMENT);
PREPROC_DELETE_NEWLINE: '\r'?'\n' -> channel(HIDDEN), type(PREPROC_NEWLINE);
PREPROC_DELETE_ANY: . -> channel(PREPROC_DELETE_CHANNEL);

mode ASYNC_MODE;
Async_LINE_COMMENT: LINE_COMMENT -> type(LINE_COMMENT), channel(HIDDEN);
Async_WHITE_SPACE: WHITE_SPACE -> type(WHITE_SPACE), channel(HIDDEN);

// separators
Async_DOT: DOT -> type(DOT), pushMode(DOT_MODE);
Async_LBRACK: LBRACK -> type(LBRACK);
Async_RBRACK: RBRACK -> type(RBRACK);
Async_LPAREN: LPAREN -> type(LPAREN);
Async_RPAREN: RPAREN -> type(RPAREN);
Async_COLON: COLON -> type(COLON);
Async_SEMICOLON: SEMICOLON -> type(SEMICOLON);
Async_COMMA: COMMA -> type(COMMA);
Async_ASSIGN: ASSIGN -> type(ASSIGN);
Async_PLUS: PLUS -> type(PLUS);
Async_MINUS: MINUS -> type(MINUS);
Async_LESS_OR_EQUAL: LESS_OR_EQUAL -> type(LESS_OR_EQUAL);
Async_NOT_EQUAL: NOT_EQUAL -> type(NOT_EQUAL);
Async_LESS: LESS -> type(LESS);
Async_GREATER_OR_EQUAL: GREATER_OR_EQUAL -> type(GREATER_OR_EQUAL);
Async_GREATER: GREATER -> type(GREATER);
Async_MUL: MUL -> type(MUL);
Async_QUOTIENT: QUOTIENT -> type(QUOTIENT);
Async_MODULO: MODULO -> type(MODULO);
Async_QUESTION: QUESTION -> type(QUESTION);
Async_AMPERSAND: AMPERSAND -> type(AMPERSAND), pushMode(ANNOTATION_MODE);
Async_PREPROC_DELETE: PREPROC_DELETE ->
    type(PREPROC_DELETE),
    pushMode(PREPROC_DELETE_MODE),
    channel(PREPROC_DELETE_CHANNEL);
Async_PREPROC_INSERT: PREPROC_INSERT -> type(PREPROC_INSERT), channel(HIDDEN);
Async_PREPROC_ENDINSERT: PREPROC_ENDINSERT -> type(PREPROC_ENDINSERT), channel(HIDDEN);

Async_HASH: HASH -> type(HASH), pushMode(PREPROCESSOR_MODE);
Async_BAR: BAR -> type(BAR);
Async_TILDA: TILDA -> type(TILDA), pushMode(LABEL_MODE);

// literals
Async_TRUE: TRUE -> type(TRUE);
Async_FALSE: FALSE -> type(FALSE);
Async_UNDEFINED: UNDEFINED -> type(UNDEFINED);
Async_NULL: NULL -> type(NULL);
Async_DECIMAL: DECIMAL -> type(DECIMAL);
Async_DATETIME: DATETIME -> type(DATETIME);
Async_FLOAT: FLOAT -> type(FLOAT);
Async_STRING: STRING -> type(STRING);
Async_STRINGSTART: STRINGSTART -> type(STRINGSTART);
Async_STRINGTAIL: STRINGTAIL -> type(STRINGTAIL);
Async_STRINGPART: STRINGPART -> type(STRINGPART);

// keywords
Async_PROCEDURE_KEYWORD: PROCEDURE_KEYWORD -> type(PROCEDURE_KEYWORD);
Async_FUNCTION_KEYWORD: FUNCTION_KEYWORD -> type(FUNCTION_KEYWORD);
Async_ENDPROCEDURE_KEYWORD: ENDPROCEDURE_KEYWORD -> type(ENDPROCEDURE_KEYWORD), popMode;
Async_ENDFUNCTION_KEYWORD: ENDFUNCTION_KEYWORD -> type(ENDFUNCTION_KEYWORD), popMode;
Async_EXPORT_KEYWORD: EXPORT_KEYWORD -> type(EXPORT_KEYWORD);
Async_VAL_KEYWORD: VAL_KEYWORD -> type(VAL_KEYWORD);
Async_ENDIF_KEYWORD: ENDIF_KEYWORD -> type(ENDIF_KEYWORD);
Async_ENDDO_KEYWORD: ENDDO_KEYWORD -> type(ENDDO_KEYWORD);
Async_IF_KEYWORD: IF_KEYWORD -> type(IF_KEYWORD);
Async_ELSIF_KEYWORD: ELSIF_KEYWORD -> type(ELSIF_KEYWORD);
Async_ELSE_KEYWORD: ELSE_KEYWORD -> type(ELSE_KEYWORD);
Async_THEN_KEYWORD: THEN_KEYWORD -> type(THEN_KEYWORD);
Async_WHILE_KEYWORD: WHILE_KEYWORD -> type(WHILE_KEYWORD);
Async_DO_KEYWORD: DO_KEYWORD -> type(DO_KEYWORD);
Async_FOR_KEYWORD: FOR_KEYWORD -> type(FOR_KEYWORD);
Async_TO_KEYWORD: TO_KEYWORD -> type(TO_KEYWORD);
Async_EACH_KEYWORD: EACH_KEYWORD -> type(EACH_KEYWORD);
Async_IN_KEYWORD: IN_KEYWORD -> type(IN_KEYWORD);
Async_TRY_KEYWORD: TRY_KEYWORD -> type(TRY_KEYWORD);
Async_EXCEPT_KEYWORD: EXCEPT_KEYWORD -> type(EXCEPT_KEYWORD);
Async_ENDTRY_KEYWORD: ENDTRY_KEYWORD -> type(ENDTRY_KEYWORD);
Async_RETURN_KEYWORD: RETURN_KEYWORD -> type(RETURN_KEYWORD);
Async_CONTINUE_KEYWORD: CONTINUE_KEYWORD -> type(CONTINUE_KEYWORD);
Async_RAISE_KEYWORD: RAISE_KEYWORD -> type(RAISE_KEYWORD);
Async_VAR_KEYWORD: VAR_KEYWORD -> type(VAR_KEYWORD);
Async_NOT_KEYWORD: NOT_KEYWORD -> type(NOT_KEYWORD);
Async_OR_KEYWORD: OR_KEYWORD -> type(OR_KEYWORD);
Async_AND_KEYWORD: AND_KEYWORD -> type(AND_KEYWORD);
Async_NEW_KEYWORD: NEW_KEYWORD -> type(NEW_KEYWORD);
Async_GOTO_KEYWORD: GOTO_KEYWORD -> type(GOTO_KEYWORD);
Async_BREAK_KEYWORD: BREAK_KEYWORD -> type(BREAK_KEYWORD);
Async_EXECUTE_KEYWORD: EXECUTE_KEYWORD -> type(EXECUTE_KEYWORD);
Async_ADDHANDLER_KEYWORD: ADDHANDLER_KEYWORD -> type(ADDHANDLER_KEYWORD);
Async_REMOVEHANDLER_KEYWORD: REMOVEHANDLER_KEYWORD -> type(REMOVEHANDLER_KEYWORD);
AWAIT_KEYWORD
    : (RU_ZH RU_D RU_A RU_T RU_SOFT_SIGN
    | A W A I T)
    ;

// всегда в конце мода
Async_IDENTIFIER: IDENTIFIER -> type(IDENTIFIER);
Async_UNKNOWN: UNKNOWN -> type(UNKNOWN);
