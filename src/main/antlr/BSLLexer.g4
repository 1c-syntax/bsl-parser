/**
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018
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
lexer grammar BSLLexer;

@lexer::members {
    int lastTokenType = 0;

    @Override
    public void emit(Token token) {
        super.emit(token);
        if (token.getChannel() == DEFAULT_TOKEN_CHANNEL) {
          lastTokenType = token.getType();
        };
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
AMPERSAND: '&' -> pushMode(ANNOTATION_MODE);
HASH: '#' -> pushMode(PREPROCESSOR_MODE);

SQUOTE: '\'';
BAR: '|';
TILDA: '~' -> pushMode(LABEL_MODE);

// literals
TRUE
    :
    { lastTokenType != DOT }?
    ( 'ИСТИНА'
    | 'TRUE' )
    ;
FALSE
    :
    { lastTokenType != DOT }?
    ( 'ЛОЖЬ'
    | 'FALSE' )
    ;
UNDEFINED
    :
    { lastTokenType != DOT }?
    ( 'НЕОПРЕДЕЛЕНО'
    | 'UNDEFINED' )
    ;
NULL
    :
    { lastTokenType != DOT }?
    'NULL'
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
    { lastTokenType != DOT }?
    ( 'ПРОЦЕДУРА'
    | 'PROCEDURE' )
    ;
FUNCTION_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ФУНКЦИЯ'
    | 'FUNCTION' )
    ;
ENDPROCEDURE_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'КОНЕЦПРОЦЕДУРЫ'
    | 'ENDPROCEDURE' )
    ;
ENDFUNCTION_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'КОНЕЦФУНКЦИИ'
    | 'ENDFUNCTION' )
    ;
EXPORT_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ЭКСПОРТ'
    | 'EXPORT' )
    ;
VAL_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ЗНАЧ'
    | 'VAL' )
    ;
ENDIF_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'КОНЕЦЕСЛИ'
    | 'ENDIF' )
    ;
ENDDO_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'КОНЕЦЦИКЛА'
    | 'ENDDO' )
    ;
IF_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ЕСЛИ'
    | 'IF' )
    ;
ELSIF_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ИНАЧЕЕСЛИ'
    | 'ELSIF' )
    ;
ELSE_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ИНАЧЕ'
    | 'ELSE' )
    ;
THEN_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ТОГДА'
    | 'THEN' )
    ;
WHILE_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ПОКА'
    | 'WHILE' )
    ;
DO_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ЦИКЛ'
    | 'DO' )
    ;
FOR_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ДЛЯ'
    | 'FOR' );
TO_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ПО'
    | 'TO' )
    ;
EACH_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'КАЖДОГО'
    | 'EACH' )
    ;
IN_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ИЗ'
    | 'IN' )
    ;
TRY_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ПОПЫТКА'
    | 'TRY' )
    ;
EXCEPT_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ИСКЛЮЧЕНИЕ'
    | 'EXCEPT' )
    ;
ENDTRY_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'КОНЕЦПОПЫТКИ'
    | 'ENDTRY' )
    ;
RETURN_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ВОЗВРАТ'
    | 'RETURN' )
    ;
CONTINUE_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ПРОДОЛЖИТЬ'
    | 'CONTINUE' )
    ;
RAISE_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ВЫЗВАТЬИСКЛЮЧЕНИЕ'
    | 'RAISE' )
    ;
VAR_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ПЕРЕМ'
    | 'VAR' );
NOT_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'НЕ'
    | 'NOT' )
    ;
OR_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ИЛИ'
    | 'OR' )
    ;
AND_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'И'
    | 'AND' )
    ;
NEW_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'НОВЫЙ'
    | 'NEW' )
    ;
GOTO_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ПЕРЕЙТИ'
    | 'GOTO' )
    ;
BREAK_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ПРЕРВАТЬ'
    | 'BREAK' )
    ;
EXECUTE_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ВЫПОЛНИТЬ'
    | 'EXECUTE' )
    ;
ADDHANDLER_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'ДОБАВИТЬОБРАБОТЧИК'
    | 'ADDHANDLER' )
    ;
REMOVEHANDLER_KEYWORD
    :
    { lastTokenType != DOT }?
    ( 'УДАЛИТЬОБРАБОТЧИК'
    | 'REMOVEHANDLER' )
    ;

fragment LETTER: [\p{Letter}] | '_';   
IDENTIFIER : LETTER ( LETTER | DIGIT )*;

UNKNOWN: . -> channel(HIDDEN);

mode PREPROCESSOR_MODE;

PREPROC_EXCLAMATION_MARK: '!';
PREPROC_LPAREN: '(';
PREPROC_RPAREN: ')';

PREPROC_STRINGSTART: '"' (~["\n\r])*;
PREPROC_STRING: '"' (~["\n\r])* '"';
PREPROC_STRINGTAIL: BAR (~["\n\r])* '"';
PREPROC_STRINGPART: BAR (~["\n\r])*;

PREPROC_USE_KEYWORD
    :
    ('ИСПОЛЬЗОВАТЬ'
    | 'USE') -> pushMode(USE_MODE);

PREPROC_REGION
    :
    { lastTokenType == HASH }?
    ( 'ОБЛАСТЬ'
    | 'REGION' ) -> pushMode(REGION_MODE)
    ;
PREPROC_END_REGION
    :
    { lastTokenType == HASH }?
    ( 'КОНЕЦОБЛАСТИ'
    | 'ENDREGION' )
    ;

PREPROC_NOT_KEYWORD
    :
      'НЕ'
    | 'NOT'
    ;
PREPROC_OR_KEYWORD
    :
      'ИЛИ'
    | 'OR'
    ;
PREPROC_AND_KEYWORD
    :
      'И'
    | 'AND'
    ;

PREPROC_IF_KEYWORD
    :
      'ЕСЛИ'
    | 'IF'
    ;
PREPROC_THEN_KEYWORD
    :
      'ТОГДА'
    | 'THEN'
    ;
PREPROC_ELSIF_KEYWORD
    :
      'ИНАЧЕЕСЛИ'
    | 'ELSIF'
    ;
PREPROC_ENDIF_KEYWORD
    :
      'КОНЕЦЕСЛИ'
    | 'ENDIF'
    ;
PREPROC_ELSE_KEYWORD
    :
      'ИНАЧЕ'
    | 'ELSE'
    ;

PREPROC_MOBILEAPPCLIENT_SYMBOL
    :
      'МОБИЛЬНОЕПРИЛОЖЕНИЕКЛИЕНТ'
    | 'MOBILEAPPCLIENT'
    ;
PREPROC_MOBILEAPPSERVER_SYMBOL
    :
      'МОБИЛЬНОЕПРИЛОЖЕНИЕСЕРВЕР'
    | 'MOBILEAPPSERVER'
    ;
PREPROC_MOBILECLIENT_SYMBOL
    :
      'МОБИЛЬНЫЙКЛИЕНТ' 
    | 'MOBILECLIENT'
    ;
PREPROC_THICKCLIENTORDINARYAPPLICATION_SYMBOL
    :
      'ТОЛСТЫЙКЛИЕНТОБЫЧНОЕПРИЛОЖЕНИЕ'
    | 'THICKCLIENTORDINARYAPPLICATION'
    ;
PREPROC_THICKCLIENTMANAGEDAPPLICATION_SYMBOL
    :
      'ТОЛСТЫЙКЛИЕНТУПРАВЛЯЕМОЕПРИЛОЖЕНИЕ'
    | 'THICKCLIENTMANAGEDAPPLICATION'
    ;
PREPROC_EXTERNALCONNECTION_SYMBOL
    :
      'ВНЕСHНЕЕСОЕДИНЕНИЕ'
    | 'EXTERNALCONNECTION'
    ;
PREPROC_THINCLIENT_SYMBOL
    :
      'ТОНКИЙКЛИЕНТ'
    | 'THINCLIENT'
    ;
PREPROC_WEBCLIENT_SYMBOL
    :
      'ВЕБКЛИЕНТ'
    | 'WEBCLIENT'
    ;
PREPROC_ATCLIENT_SYMBOL
    :
      'НАКЛИЕНТЕ'
    | 'ATCLIENT'
    ;
PREPROC_CLIENT_SYMBOL
    :
      'КЛИЕНТ'
    | 'CLIENT'
    ;
PREPROC_ATSERVER_SYMBOL
    :
      'НАСЕРВЕРЕ'
    | 'ATSERVER'
    ;
PREPROC_SERVER_SYMBOL
    :
      'СЕРВЕР'
    | 'SERVER'
    ;
PREPROC_IDENTIFIER : LETTER ( LETTER | DIGIT )*;

PREPROC_WHITE_SPACE: [ \t\f] -> skip;
PREPROC_LINE_COMMENT: '//' ~[\r\n]* -> channel(HIDDEN);
PREPROC_NEWLINE: [\r\n] -> popMode, channel(HIDDEN);

PREPROC_ANY: ~[\r\n];

mode ANNOTATION_MODE;

ANNOTATION_ATSERVERNOCONTEXT_SYMBOL
    : (
      'НАСЕРВЕРЕБЕЗКОНТЕКСТА'
    | 'ATSERVERNOCONTEXT'
    ) -> popMode
    ;

ANNOTATION_ATCLIENTATSERVERNOCONTEXT_SYMBOL
    : (
      'НАКЛИЕНТЕНАСЕРВЕРЕБЕЗКОНТЕКСТА'
    | 'ATCLIENTATSERVERNOCONTEXT'
    ) -> popMode
    ;

ANNOTATION_ATCLIENTATSERVER_SYMBOL
    : (
      'НАКЛИЕНТЕНАСЕРВЕРЕ'
    | 'ATCLIENTATSERVER'
    ) -> popMode
    ;

ANNOTATION_ATCLIENT_SYMBOL
    : (
      'НАКЛИЕНТЕ'
    | 'ATCLIENT'
    ) -> popMode
    ;

ANNOTATION_ATSERVER_SYMBOL
    : ( 'НАСЕРВЕРЕ'
    | 'ATSERVER'
    ) -> popMode
    ;

ANNOTATION_CUSTOM_SYMBOL
    : (
    { lastTokenType == AMPERSAND }?
    LETTER ( LETTER | DIGIT )*
    ) -> popMode
    ;

ANNOTATION_WHITE_SPACE
    : [ \n\r\t\f]
    -> channel(HIDDEN)
    ;

ANNOTATION_UKNOWN
    : .
    -> channel(HIDDEN)
    ;

mode LABEL_MODE;
LABEL_IDENTIFIER : LETTER ( LETTER | DIGIT )* -> type(IDENTIFIER), popMode;

mode REGION_MODE;
REGION_WHITE_SPACE
    : [ \t\f]
    -> channel(HIDDEN)
    ;
REGION_IDENTIFIER : LETTER ( LETTER | DIGIT )* -> type(PREPROC_IDENTIFIER), popMode;

mode USE_MODE;
fragment USE_LETTER: [\p{Letter}] | '_' | '-';
USE_WHITE_SPACE
    : [ \t\f]
    -> channel(HIDDEN)
    ;
USE_STRING : '"' (~["\n\r])* '"' -> type(PREPROC_STRING), popMode;
USE_IDENTIFIER : USE_LETTER ( USE_LETTER | DIGIT )* -> type(PREPROC_IDENTIFIER), popMode;

