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
HASH: '#' -> pushMode(PREPROCESSOR_MODE);

SQUOTE: '\'';
BAR: '|';
TILDA: '~' -> pushMode(LABEL_MODE);

// literals
TRUE
    :
    RU_TRUE
    |
    EN_TRUE
    ;

RU_TRUE :  'ИСТИНА';
EN_TRUE :  'TRUE';


FALSE
    :
    RU_FALSE
    |
    EN_FALSE
    ;

RU_FALSE :   'ЛОЖЬ';
EN_FALSE :   'FALSE';


UNDEFINED
    :
   RU_UNDEFINED
   |
   EN_UNDEFINED
   ;

RU_UNDEFINED :   'НЕОПРЕДЕЛЕНО';
EN_UNDEFINED :   'UNDEFINED';

NULL
    :

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
    RU_PROCEDURE_KEYWORD
    |
    EN_PROCEDURE_KEYWORD
    ;

RU_PROCEDURE_KEYWORD :   'ПРОЦЕДУРА';
EN_PROCEDURE_KEYWORD :   'PROCEDURE';

FUNCTION_KEYWORD
    :
    RU_FUNCTION_KEYWORD
    |
    EN_FUNCTION_KEYWORD
    ;

RU_FUNCTION_KEYWORD :   'ФУНКЦИЯ';
EN_FUNCTION_KEYWORD :   'FUNCTION';

ENDPROCEDURE_KEYWORD
    :
    RU_ENDPROCEDURE_KEYWORD
    |
    EN_ENDPROCEDURE_KEYWORD
    ;

RU_ENDPROCEDURE_KEYWORD :   'КОНЕЦПРОЦЕДУРЫ';
EN_ENDPROCEDURE_KEYWORD :   'ENDPROCEDURE';


ENDFUNCTION_KEYWORD
    :
    RU_ENDFUNCTION_KEYWORD
    |
    EN_ENDFUNCTION_KEYWORD
    ;

RU_ENDFUNCTION_KEYWORD :   'КОНЕЦФУНКЦИИ';
EN_ENDFUNCTION_KEYWORD :   'ENDFUNCTION';


EXPORT_KEYWORD
    :
    RU_EXPORT_KEYWORD
    |
    EN_EXPORT_KEYWORD
    ;

RU_EXPORT_KEYWORD :   'ЭКСПОРТ';
EN_EXPORT_KEYWORD :   'EXPORT';


VAL_KEYWORD
    :
    RU_VAL_KEYWORD
    |
    EN_VAL_KEYWORD
    ;

RU_VAL_KEYWORD :   'ЗНАЧ';
EN_VAL_KEYWORD :   'VAL';

ENDIF_KEYWORD
    :
    RU_ENDIF_KEYWORD
    |
    EN_ENDIF_KEYWORD
    ;

RU_ENDIF_KEYWORD :  'КОНЕЦЕСЛИ';
EN_ENDIF_KEYWORD :  'ENDIF';

ENDDO_KEYWORD
    :
    RU_ENDDO_KEYWORD
    |
    EN_ENDDO_KEYWORD
    ;

RU_ENDDO_KEYWORD :  'КОНЕЦЦИКЛА';
EN_ENDDO_KEYWORD :  'ENDDO';


IF_KEYWORD
    :
    RU_IF_KEYWORD
    |
    EN_IF_KEYWORD
    ;

RU_IF_KEYWORD :  'ЕСЛИ';
EN_IF_KEYWORD :  'IF';


ELSIF_KEYWORD
    :
    RU_ELSIF_KEYWORD
    |
    EN_ELSIF_KEYWORD
    ;

RU_ELSIF_KEYWORD :  'ИНАЧЕЕСЛИ';
EN_ELSIF_KEYWORD :  'ELSIF';


ELSE_KEYWORD
    :
    RU_ELSE_KEYWORD
    |
    EN_ELSE_KEYWORD
    ;

RU_ELSE_KEYWORD :  'ИНАЧЕ';
EN_ELSE_KEYWORD :  'ELSE';

THEN_KEYWORD
    :
    RU_THEN_KEYWORD
    |
    EN_THEN_KEYWORD
    ;

RU_THEN_KEYWORD :  'ТОГДА';
EN_THEN_KEYWORD :  'THEN';

WHILE_KEYWORD
    :
    RU_WHILE_KEYWORD
    |
    EN_WHILE_KEYWORD
    ;

RU_WHILE_KEYWORD :  'ПОКА';
EN_WHILE_KEYWORD :  'WHILE';


DO_KEYWORD
    :
    RU_DO_KEYWORD
    |
    EN_DO_KEYWORD
    ;

RU_DO_KEYWORD :  'ЦИКЛ';
EN_DO_KEYWORD :  'DO';


FOR_KEYWORD
    :
    RU_FOR_KEYWORD
    |
    EN_FOR_KEYWORD
    ;

RU_FOR_KEYWORD :  'ДЛЯ';
EN_FOR_KEYWORD :  'FOR';


TO_KEYWORD
    :
    RU_TO_KEYWORD
    |
    EN_TO_KEYWORD
    ;

RU_TO_KEYWORD :  'ПО';
EN_TO_KEYWORD :  'TO';


EACH_KEYWORD
    :
    RU_EACH_KEYWORD
    |
    EN_EACH_KEYWORD
    ;

RU_EACH_KEYWORD :  'КАЖДОГО';
EN_EACH_KEYWORD :  'EACH';

IN_KEYWORD
    :
    RU_IN_KEYWORD
    |
    EN_IN_KEYWORD
    ;

RU_IN_KEYWORD :  'ИЗ';
EN_IN_KEYWORD :  'IN';

TRY_KEYWORD
    :
    RU_TRY_KEYWORD
    |
    EN_TRY_KEYWORD
    ;

RU_TRY_KEYWORD :  'ПОПЫТКА';
EN_TRY_KEYWORD :  'TRY';

EXCEPT_KEYWORD
    :
    RU_EXCEPT_KEYWORD
    |
    EN_EXCEPT_KEYWORD
    ;

RU_EXCEPT_KEYWORD :  'ИСКЛЮЧЕНИЕ';
EN_EXCEPT_KEYWORD :  'EXCEPT';


ENDTRY_KEYWORD
    :
    RU_ENDTRY_KEYWORD
    |
    EN_ENDTRY_KEYWORD
    ;

RU_ENDTRY_KEYWORD :  'КОНЕЦПОПЫТКИ';
EN_ENDTRY_KEYWORD :  'ENDTRY';

RETURN_KEYWORD
    :
    RU_RETURN_KEYWORD
    |
    EN_RETURN_KEYWORD
    ;

RU_RETURN_KEYWORD :  'ВОЗВРАТ';
EN_RETURN_KEYWORD :  'RETURN';


CONTINUE_KEYWORD
    :
    RU_CONTINUE_KEYWORD
    |
    EN_CONTINUE_KEYWORD
    ;

RU_CONTINUE_KEYWORD :  'ПРОДОЛЖИТЬ';
EN_CONTINUE_KEYWORD :  'CONTINUE';

RAISE_KEYWORD
    :
    RU_RAISE_KEYWORD
    |
    EN_RAISE_KEYWORD
    ;

RU_RAISE_KEYWORD :  'ВЫЗВАТЬИСКЛЮЧЕНИЕ';
EN_RAISE_KEYWORD :  'RAISE';


VAR_KEYWORD
    :
    RU_VAR_KEYWORD
    |
    EN_VAR_KEYWORD
    ;

RU_VAR_KEYWORD :  'ПЕРЕМ';
EN_VAR_KEYWORD :  'VAR';

NOT_KEYWORD
    :
    RU_NOT_KEYWORD
    |
    EN_NOT_KEYWORD
    ;


RU_NOT_KEYWORD :  'НЕ';
EN_NOT_KEYWORD :  'NOT';

OR_KEYWORD
    :
    RU_OR_KEYWORD
    |
    EN_OR_KEYWORD
    ;

RU_OR_KEYWORD :  'ИЛИ';
EN_OR_KEYWORD :  'OR';


AND_KEYWORD
    :
    RU_AND_KEYWORD
    |
    EN_AND_KEYWORD
    ;

RU_AND_KEYWORD :  'И';
EN_AND_KEYWORD :   'AND';


NEW_KEYWORD
    :
    RU_NEW_KEYWORD
    |
    EN_NEW_KEYWORD
    ;

RU_NEW_KEYWORD :  'НОВЫЙ';
EN_NEW_KEYWORD :  'NEW';


GOTO_KEYWORD
    :
    RU_GOTO_KEYWORD
    |
    EN_GOTO_KEYWORD
    ;

RU_GOTO_KEYWORD :  'ПЕРЕЙТИ';
EN_GOTO_KEYWORD :  'GOTO';

BREAK_KEYWORD
    :
    RU_BREAK_KEYWORD
    |
    EN_BREAK_KEYWORD
    ;

RU_BREAK_KEYWORD :  'ПРЕРВАТЬ';
EN_BREAK_KEYWORD :  'BREAK';


EXECUTE_KEYWORD
    :
    RU_EXECUTE_KEYWORD
    |
    EN_EXECUTE_KEYWORD
    ;


RU_EXECUTE_KEYWORD :  'ВЫПОЛНИТЬ';
EN_EXECUTE_KEYWORD :  'EXECUTE';


ADDHANDLER_KEYWORD
    :
    RU_ADDHANDLER_KEYWORD
    |
    EN_ADDHANDLER_KEYWORD
    ;

RU_ADDHANDLER_KEYWORD :  'ДОБАВИТЬОБРАБОТЧИК';
EN_ADDHANDLER_KEYWORD :  'ADDHANDLER';


REMOVEHANDLER_KEYWORD
    :
    RU_REMOVEHANDLER_KEYWORD
    |
    EN_REMOVEHANDLER_KEYWORD
    ;

 RU_REMOVEHANDLER_KEYWORD :  'УДАЛИТЬОБРАБОТЧИК';
 EN_REMOVEHANDLER_KEYWORD :  'REMOVEHANDLER';

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
      'ВНЕШНЕЕСОЕДИНЕНИЕ'
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

mode DOT_MODE;
DOT_WHITE_SPACE
    : [ \t\f]
    -> channel(HIDDEN)
    ;
DOT_IDENTIFIER : LETTER ( LETTER | DIGIT )* -> type(IDENTIFIER), popMode;
