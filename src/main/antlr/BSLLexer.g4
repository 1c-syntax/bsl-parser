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
lexer grammar BSLLexer;

channels { COMMENTS }

@members {
public BSLLexer(CharStream input, boolean crAwareCostructor) {
  super(input);
  _interp = new CRAwareLexerATNSimulator(this, _ATN);
  validateInputStream(_ATN, input);
}
}

// commons
fragment DIGIT: [0-9];
LINE_COMMENT: '//' ->  channel(HIDDEN), pushMode(COMMENT);
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
HASH: '#' -> pushMode(PREPROCESSOR_MODE);

SQUOTE: '\'';
BAR: '|';
TILDA: '~' -> pushMode(LABEL_MODE);

// literals
TRUE : 'ИСТИНА' | 'TRUE';
FALSE : 'ЛОЖЬ' | 'FALSE';
UNDEFINED : 'НЕОПРЕДЕЛЕНО' | 'UNDEFINED';
NULL : 'NULL';
DECIMAL: DIGIT+;
DATETIME: SQUOTE(~['\n\r])*SQUOTE?; // TODO: Честная регулярка

FLOAT : DIGIT+ '.' DIGIT*;
STRING: '"' (~[\r\n"] | '""')* '"';
STRINGSTART: '"' (~["\n\r]| '""')*;
STRINGTAIL: BAR (~["\n\r] | '""')* '"';
STRINGPART: BAR (~[\r\n"] | '""')*;

// keywords
PROCEDURE_KEYWORD : 'ПРОЦЕДУРА' | 'PROCEDURE';
FUNCTION_KEYWORD : 'ФУНКЦИЯ' | 'FUNCTION';
ENDPROCEDURE_KEYWORD : 'КОНЕЦПРОЦЕДУРЫ' | 'ENDPROCEDURE';
ENDFUNCTION_KEYWORD : 'КОНЕЦФУНКЦИИ' | 'ENDFUNCTION';
EXPORT_KEYWORD : 'ЭКСПОРТ' | 'EXPORT';
VAL_KEYWORD : 'ЗНАЧ' | 'VAL';
ENDIF_KEYWORD : 'КОНЕЦЕСЛИ' | 'ENDIF';
ENDDO_KEYWORD : 'КОНЕЦЦИКЛА' | 'ENDDO';
IF_KEYWORD : 'ЕСЛИ' | 'IF';
ELSIF_KEYWORD : 'ИНАЧЕЕСЛИ' | 'ELSIF';
ELSE_KEYWORD : 'ИНАЧЕ' | 'ELSE';
THEN_KEYWORD : 'ТОГДА' | 'THEN';
WHILE_KEYWORD : 'ПОКА' | 'WHILE';
DO_KEYWORD : 'ЦИКЛ' | 'DO';
FOR_KEYWORD : 'ДЛЯ' | 'FOR';
TO_KEYWORD : 'ПО' | 'TO';
EACH_KEYWORD : 'КАЖДОГО' | 'EACH';
IN_KEYWORD : 'ИЗ' | 'IN';
TRY_KEYWORD : 'ПОПЫТКА' | 'TRY';
EXCEPT_KEYWORD : 'ИСКЛЮЧЕНИЕ' | 'EXCEPT';
ENDTRY_KEYWORD : 'КОНЕЦПОПЫТКИ' | 'ENDTRY';
RETURN_KEYWORD : 'ВОЗВРАТ' | 'RETURN';
CONTINUE_KEYWORD : 'ПРОДОЛЖИТЬ' | 'CONTINUE';
RAISE_KEYWORD : 'ВЫЗВАТЬИСКЛЮЧЕНИЕ' | 'RAISE';
VAR_KEYWORD : 'ПЕРЕМ' | 'VAR';
NOT_KEYWORD : 'НЕ' | 'NOT';
OR_KEYWORD  : 'ИЛИ'| 'OR';
AND_KEYWORD : 'И' | 'AND';
NEW_KEYWORD : 'НОВЫЙ' | 'NEW';
GOTO_KEYWORD : 'ПЕРЕЙТИ' | 'GOTO';
BREAK_KEYWORD : 'ПРЕРВАТЬ' | 'BREAK';
EXECUTE_KEYWORD : 'ВЫПОЛНИТЬ' | 'EXECUTE';
ADDHANDLER_KEYWORD : 'ДОБАВИТЬОБРАБОТЧИК' | 'ADDHANDLER';
REMOVEHANDLER_KEYWORD : 'УДАЛИТЬОБРАБОТЧИК' | 'REMOVEHANDLER';

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
    ( 'ОБЛАСТЬ'
    | 'REGION' ) -> pushMode(REGION_MODE)
    ;
PREPROC_END_REGION
    :
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
PREPROC_INSERT_SYMBOL
    :
      'ВСТАВКА'
    | 'INSERT'
    ;
PREPROC_ENDINSERT_SYMBOL
    :
      'КОНЕЦВСТАВКИ'
    | 'ENDINSERT'
    ;
PREPROC_DELETE_SYMBOL
    :
      'УДАЛЕНИЕ'
    | 'DELETE'
    ;
PREPROC_ENDDELETE_SYMBOL
    :
      'КОНЕЦУДАЛЕНИЯ'
    | 'ENDDELETE'
    ;

PREPROC_IDENTIFIER : LETTER ( LETTER | DIGIT )*;

PREPROC_WHITE_SPACE: [ \t\f]+ -> channel(HIDDEN), type(WHITE_SPACE);
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

ANNOTATION_BEFORE_SYMBOL
    : ( 'ПЕРЕД'
    | 'BEFORE'
    ) -> popMode
    ;

ANNOTATION_AFTER_SYMBOL
    : ( 'ПОСЛЕ'
    | 'AFTER'
    ) -> popMode
    ;

ANNOTATION_AROUND_SYMBOL
    : ( 'ВМЕСТО'
    | 'AROUND'
    ) -> popMode
    ;

ANNOTATION_CHANGEANDVALIDATE_SYMBOL
    : ( 'ИЗМЕНЕНИЕИКОНТРОЛЬ'
    | 'CHANGEANDVALIDATE'
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

ANNOTATION_UKNOWN
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

mode COMMENT;

COMMENT_START: '//';

COMMENT_MINUS: '-' -> type(MINUS);
COMMENT_COMMA: ',' -> type(COMMA);
COMMENT_MUL: '*';
COMMENT_MULTIMUL: '**';
COMMENT_WHITE_SPACE: [ \t\f]+ -> channel(HIDDEN), type(WHITE_SPACE);
COMMENT_PARAMETERS : 'ПАРАМЕТРЫ:' | 'PARAMETERS:';
COMMENT_RETURNS: 'ВОЗВРАЩАЕМОЕ ЗНАЧЕНИЕ:' | 'RETURNS:';
COMMENT_EXAMPLE: 'EXAMPLE:' | 'ПРИМЕР:';
COMMENT_CONTAINS : 'ИЗ' | 'CONTAINS';
COMMENT_STRING : (~[ ,'-'\t\f\n\r])+;
COMMENT_NEWLINE: [\r\n] -> channel(HIDDEN), popMode;
