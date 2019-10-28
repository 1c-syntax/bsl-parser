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
     'ИСТИНА'

    ;
FALSE
    :
    { lastTokenType != DOT }?
    'ЛОЖЬ';
UNDEFINED
    :
    { lastTokenType != DOT }?
    'НЕОПРЕДЕЛЕНО';
NULL
    :
    { lastTokenType != DOT }?
    'NULL';

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
    'ПРОЦЕДУРА';
FUNCTION_KEYWORD
    :
    { lastTokenType != DOT }?
    'ФУНКЦИЯ';

ENDPROCEDURE_KEYWORD
    :
    { lastTokenType != DOT }?
    'КОНЕЦПРОЦЕДУРЫ';

ENDFUNCTION_KEYWORD
    :
    { lastTokenType != DOT }?
    'КОНЕЦФУНКЦИИ';
EXPORT_KEYWORD
    :
    { lastTokenType != DOT }?
    'ЭКСПОРТ'

    ;
VAL_KEYWORD
    :
    { lastTokenType != DOT }?
    'ЗНАЧ'

    ;
ENDIF_KEYWORD
    :
    { lastTokenType != DOT }?
    'КОНЕЦЕСЛИ'

    ;
ENDDO_KEYWORD
    :
    { lastTokenType != DOT }?
    'КОНЕЦЦИКЛА'

    ;
IF_KEYWORD
    :
    { lastTokenType != DOT }?
    'ЕСЛИ'

    ;
ELSIF_KEYWORD
    :
    { lastTokenType != DOT }?
    'ИНАЧЕЕСЛИ'

    ;
ELSE_KEYWORD
    :
    { lastTokenType != DOT }?
    'ИНАЧЕ'

    ;
THEN_KEYWORD
    :
    { lastTokenType != DOT }?
    'ТОГДА'

    ;
WHILE_KEYWORD
    :
    { lastTokenType != DOT }?
    'ПОКА'

    ;
DO_KEYWORD
    :
    { lastTokenType != DOT }?
    'ЦИКЛ'

    ;
FOR_KEYWORD
    :
    { lastTokenType != DOT }?
    'ДЛЯ'
;
TO_KEYWORD
    :
    { lastTokenType != DOT }?
     'ПО'

    ;
EACH_KEYWORD
    :
    { lastTokenType != DOT }?
    'КАЖДОГО'

    ;
IN_KEYWORD
    :
    { lastTokenType != DOT }?
    'ИЗ'

    ;
TRY_KEYWORD
    :
    { lastTokenType != DOT }?
    'ПОПЫТКА'

    ;
EXCEPT_KEYWORD
    :
    { lastTokenType != DOT }?
    'ИСКЛЮЧЕНИЕ'

    ;
ENDTRY_KEYWORD
    :
    { lastTokenType != DOT }?
    'КОНЕЦПОПЫТКИ'

    ;
RETURN_KEYWORD
    :
    { lastTokenType != DOT }?
    'ВОЗВРАТ'

    ;
CONTINUE_KEYWORD
    :
    { lastTokenType != DOT }?
    'ПРОДОЛЖИТЬ'

    ;
RAISE_KEYWORD
    :
    { lastTokenType != DOT }?
    'ВЫЗВАТЬИСКЛЮЧЕНИЕ'

    ;
VAR_KEYWORD
    :
    { lastTokenType != DOT }?
    'ПЕРЕМ'
;
NOT_KEYWORD
    :
    { lastTokenType != DOT }?
    'НЕ'

    ;
OR_KEYWORD
    :
    { lastTokenType != DOT }?
    'ИЛИ'

    ;
AND_KEYWORD
    :
    { lastTokenType != DOT }?
    'И'

    ;
NEW_KEYWORD
    :
    { lastTokenType != DOT }?
    'НОВЫЙ'

    ;
GOTO_KEYWORD
    :
    { lastTokenType != DOT }?
    'ПЕРЕЙТИ'

    ;
BREAK_KEYWORD
    :
    { lastTokenType != DOT }?
    'ПРЕРВАТЬ'

    ;
EXECUTE_KEYWORD
    :
    { lastTokenType != DOT }?
    'ВЫПОЛНИТЬ'

    ;
ADDHANDLER_KEYWORD
    :
    { lastTokenType != DOT }?
    'ДОБАВИТЬОБРАБОТЧИК'

    ;
REMOVEHANDLER_KEYWORD
    :
    { lastTokenType != DOT }?
    'УДАЛИТЬОБРАБОТЧИК'

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
    ('ИСПОЛЬЗОВАТЬ') -> pushMode(USE_MODE);

PREPROC_REGION
    :
    { lastTokenType == HASH }?
    'ОБЛАСТЬ' -> pushMode(REGION_MODE)
    ;
PREPROC_END_REGION
    :
    { lastTokenType == HASH }?
    'КОНЕЦОБЛАСТИ'

    ;

PREPROC_NOT_KEYWORD
    :
      'НЕ'
    ;
PREPROC_OR_KEYWORD
    :
      'ИЛИ'
    ;
PREPROC_AND_KEYWORD
    :
      'И'
    ;

PREPROC_IF_KEYWORD
    :
      'ЕСЛИ'
    ;
PREPROC_THEN_KEYWORD
    :
      'ТОГДА'

    ;
PREPROC_ELSIF_KEYWORD
    :
      'ИНАЧЕЕСЛИ'

    ;
PREPROC_ENDIF_KEYWORD
    :
      'КОНЕЦЕСЛИ'

    ;
PREPROC_ELSE_KEYWORD
    :
      'ИНАЧЕ'

    ;

PREPROC_MOBILEAPPCLIENT_SYMBOL
    :
      'МОБИЛЬНОЕПРИЛОЖЕНИЕКЛИЕНТ'

    ;
PREPROC_MOBILEAPPSERVER_SYMBOL
    :
      'МОБИЛЬНОЕПРИЛОЖЕНИЕСЕРВЕР'

    ;
PREPROC_MOBILECLIENT_SYMBOL
    :
      'МОБИЛЬНЫЙКЛИЕНТ'

    ;
PREPROC_THICKCLIENTORDINARYAPPLICATION_SYMBOL
    :
      'ТОЛСТЫЙКЛИЕНТОБЫЧНОЕПРИЛОЖЕНИЕ'

    ;
PREPROC_THICKCLIENTMANAGEDAPPLICATION_SYMBOL
    :
      'ТОЛСТЫЙКЛИЕНТУПРАВЛЯЕМОЕПРИЛОЖЕНИЕ'

    ;
PREPROC_EXTERNALCONNECTION_SYMBOL
    :
      'ВНЕШНЕЕСОЕДИНЕНИЕ'

    ;
PREPROC_THINCLIENT_SYMBOL
    :
      'ТОНКИЙКЛИЕНТ'

    ;
PREPROC_WEBCLIENT_SYMBOL
    :
      'ВЕБКЛИЕНТ'

    ;
PREPROC_ATCLIENT_SYMBOL
    :
      'НАКЛИЕНТЕ'

    ;
PREPROC_CLIENT_SYMBOL
    :
      'КЛИЕНТ'

    ;
PREPROC_ATSERVER_SYMBOL
    :
      'НАСЕРВЕРЕ'

    ;
PREPROC_SERVER_SYMBOL
    :
      'СЕРВЕР'

    ;
PREPROC_IDENTIFIER : LETTER ( LETTER | DIGIT )*;

PREPROC_WHITE_SPACE: [ \t\f] -> skip;
PREPROC_LINE_COMMENT: '//' ~[\r\n]* -> channel(HIDDEN);
PREPROC_NEWLINE: [\r\n] -> popMode, channel(HIDDEN);

PREPROC_ANY: ~[\r\n];

mode ANNOTATION_MODE;

ANNOTATION_ATSERVERNOCONTEXT_SYMBOL
    :
      'НАСЕРВЕРЕБЕЗКОНТЕКСТА' -> popMode
    ;

ANNOTATION_ATCLIENTATSERVERNOCONTEXT_SYMBOL
    :
      'НАКЛИЕНТЕНАСЕРВЕРЕБЕЗКОНТЕКСТА'

 -> popMode
    ;

ANNOTATION_ATCLIENTATSERVER_SYMBOL
    :
      'НАКЛИЕНТЕНАСЕРВЕРЕ' -> popMode;

ANNOTATION_ATCLIENT_SYMBOL
    :
      'НАКЛИЕНТЕ'

 -> popMode
    ;

ANNOTATION_ATSERVER_SYMBOL
    : 'НАСЕРВЕРЕ'

 -> popMode
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

