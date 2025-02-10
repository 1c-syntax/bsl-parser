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

options { caseInsensitive=true; }

@members {
public BSLLexer(CharStream input, boolean crAwareCostructor) {
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
    : '#' [ \t]* ('УДАЛЕНИЕ' | 'DELETE')
    -> pushMode(PREPROC_DELETE_MODE), channel(PREPROC_DELETE_CHANNEL)
    ;
PREPROC_INSERT
    : '#' [ \t]* ('ВСТАВКА' | 'INSERT')
    -> channel(HIDDEN)
    ;
PREPROC_ENDINSERT
    : '#' [ \t]* ('КОНЕЦВСТАВКИ' | 'ENDINSERT')
    -> channel(HIDDEN)
    ;
HASH: '#' -> pushMode(PREPROCESSOR_MODE);

fragment SQUOTE: '\'';
BAR: '|';
TILDA: '~' -> pushMode(LABEL_MODE);

// literals
TRUE: 'ИСТИНА' | 'TRUE';
FALSE: 'ЛОЖЬ' | 'FALSE';
UNDEFINED: 'НЕОПРЕДЕЛЕНО' | 'UNDEFINED';
NULL: 'NULL';
DECIMAL: DIGIT+;
DATETIME: SQUOTE(~['\n\r])*SQUOTE?; // TODO: Честная регулярка

FLOAT : DIGIT+ '.' DIGIT+;
STRING: '"' (~[\r\n"] | '""')* '"';
STRINGSTART: '"' (~["\n\r]| '""')*;
STRINGTAIL: BAR (~["\n\r] | '""')* '"';
STRINGPART: BAR (~[\r\n"] | '""')*;

// keywords
PROCEDURE_KEYWORD: 'ПРОЦЕДУРА' | 'PROCEDURE';
FUNCTION_KEYWORD: 'ФУНКЦИЯ' | 'FUNCTION';
ENDPROCEDURE_KEYWORD: 'КОНЕЦПРОЦЕДУРЫ' | 'ENDPROCEDURE';
ENDFUNCTION_KEYWORD: 'КОНЕЦФУНКЦИИ' | 'ENDFUNCTION';
EXPORT_KEYWORD: 'ЭКСПОРТ' | 'EXPORT';
VAL_KEYWORD: 'ЗНАЧ' | 'VAL';
ENDIF_KEYWORD: 'КОНЕЦЕСЛИ' | 'ENDIF';
ENDDO_KEYWORD: 'КОНЕЦЦИКЛА' | 'ENDDO';
IF_KEYWORD: 'ЕСЛИ' | 'IF';
ELSIF_KEYWORD: 'ИНАЧЕЕСЛИ' | 'ELSIF';
ELSE_KEYWORD: 'ИНАЧЕ' | 'ELSE';
THEN_KEYWORD: 'ТОГДА' | 'THEN';
WHILE_KEYWORD: 'ПОКА' | 'WHILE';
DO_KEYWORD: 'ЦИКЛ' | 'DO';
FOR_KEYWORD: 'ДЛЯ' | 'FOR';
TO_KEYWORD: 'ПО' | 'TO';
EACH_KEYWORD: 'КАЖДОГО' | 'EACH';
IN_KEYWORD: 'ИЗ' | 'IN';
TRY_KEYWORD: 'ПОПЫТКА' | 'TRY';
EXCEPT_KEYWORD: 'ИСКЛЮЧЕНИЕ' | 'EXCEPT';
ENDTRY_KEYWORD: 'КОНЕЦПОПЫТКИ' | 'ENDTRY';
RETURN_KEYWORD: 'ВОЗВРАТ' | 'RETURN';
CONTINUE_KEYWORD: 'ПРОДОЛЖИТЬ' | 'CONTINUE';
RAISE_KEYWORD: 'ВЫЗВАТЬИСКЛЮЧЕНИЕ' | 'RAISE';
VAR_KEYWORD: 'ПЕРЕМ' | 'VAR';
NOT_KEYWORD: 'НЕ' | 'NOT';
OR_KEYWORD: 'ИЛИ' | 'OR';
AND_KEYWORD: 'И' | 'AND';
NEW_KEYWORD: 'НОВЫЙ' | 'NEW';
GOTO_KEYWORD: 'ПЕРЕЙТИ' | 'GOTO';
BREAK_KEYWORD: 'ПРЕРВАТЬ' | 'BREAK';
EXECUTE_KEYWORD: 'ВЫПОЛНИТЬ' | 'EXECUTE';
ADDHANDLER_KEYWORD: 'ДОБАВИТЬОБРАБОТЧИК' | 'ADDHANDLER';
REMOVEHANDLER_KEYWORD: 'УДАЛИТЬОБРАБОТЧИК' | 'REMOVEHANDLER';
ASYNC_KEYWORD: ('АСИНХ' | 'ASYNC') -> pushMode(ASYNC_MODE);

fragment LETTER: [\p{Letter}] | '_';
IDENTIFIER : LETTER (LETTER | DIGIT)*;

UNKNOWN: . -> channel(HIDDEN);

mode PREPROCESSOR_MODE;

PREPROC_EXCLAMATION_MARK: '!';
PREPROC_LPAREN: LPAREN;
PREPROC_RPAREN: RPAREN;

PREPROC_STRING: '"' (~["\n\r])* '"';
PREPROC_NATIVE: 'NATIVE';
PREPROC_USE_KEYWORD: ('ИСПОЛЬЗОВАТЬ' | 'USE') -> pushMode(USE_MODE);
PREPROC_REGION: ('ОБЛАСТЬ' | 'REGION') -> pushMode(REGION_MODE);
PREPROC_END_REGION: 'КОНЕЦОБЛАСТИ' | 'ENDREGION';
PREPROC_NOT_KEYWORD: NOT_KEYWORD;
PREPROC_OR_KEYWORD: OR_KEYWORD;
PREPROC_AND_KEYWORD: AND_KEYWORD;
PREPROC_IF_KEYWORD: IF_KEYWORD;
PREPROC_THEN_KEYWORD: THEN_KEYWORD;
PREPROC_ELSIF_KEYWORD: ELSIF_KEYWORD;
PREPROC_ENDIF_KEYWORD: ENDIF_KEYWORD;
PREPROC_ELSE_KEYWORD: ELSE_KEYWORD;

PREPROC_MOBILEAPPCLIENT_SYMBOL: 'МОБИЛЬНОЕПРИЛОЖЕНИЕКЛИЕНТ' | 'MOBILEAPPCLIENT';
PREPROC_MOBILEAPPSERVER_SYMBOL: 'МОБИЛЬНОЕПРИЛОЖЕНИЕСЕРВЕР' | 'MOBILEAPPSERVER';
PREPROC_MOBILECLIENT_SYMBOL: 'МОБИЛЬНЫЙКЛИЕНТ' | 'MOBILECLIENT';
PREPROC_THICKCLIENTORDINARYAPPLICATION_SYMBOL: 'ТОЛСТЫЙКЛИЕНТОБЫЧНОЕПРИЛОЖЕНИЕ' | 'THICKCLIENTORDINARYAPPLICATION';
PREPROC_THICKCLIENTMANAGEDAPPLICATION_SYMBOL: 'ТОЛСТЫЙКЛИЕНТУПРАВЛЯЕМОЕПРИЛОЖЕНИЕ' | 'THICKCLIENTMANAGEDAPPLICATION';
PREPROC_EXTERNALCONNECTION_SYMBOL: 'ВНЕШНЕЕСОЕДИНЕНИЕ' | 'EXTERNALCONNECTION';
PREPROC_THINCLIENT_SYMBOL: 'ТОНКИЙКЛИЕНТ' | 'THINCLIENT';
PREPROC_WEBCLIENT_SYMBOL: 'ВЕБКЛИЕНТ' | 'WEBCLIENT';
PREPROC_ATCLIENT_SYMBOL: 'НАКЛИЕНТЕ' | 'ATCLIENT';
PREPROC_CLIENT_SYMBOL: 'КЛИЕНТ' | 'CLIENT';
PREPROC_ATSERVER_SYMBOL: 'НАСЕРВЕРЕ' | 'ATSERVER';
PREPROC_SERVER_SYMBOL: 'СЕРВЕР' | 'SERVER';
PREPROC_MOBILE_STANDALONE_SERVER: 'МОБИЛЬНЫЙАВТОНОМНЫЙСЕРВЕР' | 'MOBILESTANDALONESERVER';

PREPROC_LINUX   : 'LINUX';
PREPROC_WINDOWS : 'WINDOWS';
PREPROC_MACOS   : 'MACOS';

PREPROC_IDENTIFIER : IDENTIFIER;

PREPROC_WHITE_SPACE: [ \t\f]+ -> channel(HIDDEN), type(WHITE_SPACE);
PREPROC_LINE_COMMENT: LINE_COMMENT -> channel(HIDDEN), type(LINE_COMMENT);
PREPROC_NEWLINE: '\r'?'\n' -> popMode, channel(HIDDEN);

PREPROC_ANY: ~[\r\n];

mode ANNOTATION_MODE;
ANNOTATION_ATSERVERNOCONTEXT_SYMBOL: ('НАСЕРВЕРЕБЕЗКОНТЕКСТА' | 'ATSERVERNOCONTEXT') -> popMode;
ANNOTATION_ATCLIENTATSERVERNOCONTEXT_SYMBOL: ('НАКЛИЕНТЕНАСЕРВЕРЕБЕЗКОНТЕКСТА' | 'ATCLIENTATSERVERNOCONTEXT') -> popMode;
ANNOTATION_ATCLIENTATSERVER_SYMBOL: ('НАКЛИЕНТЕНАСЕРВЕРЕ' | 'ATCLIENTATSERVER') -> popMode;
ANNOTATION_ATCLIENT_SYMBOL: ('НАКЛИЕНТЕ' | 'ATCLIENT') -> popMode;
ANNOTATION_ATSERVER_SYMBOL: ('НАСЕРВЕРЕ' | 'ATSERVER') -> popMode;
ANNOTATION_BEFORE_SYMBOL: ('ПЕРЕД' | 'BEFORE') -> popMode;
ANNOTATION_AFTER_SYMBOL: ('ПОСЛЕ' | 'AFTER') -> popMode;
ANNOTATION_AROUND_SYMBOL: ('ВМЕСТО' | 'AROUND') -> popMode;
ANNOTATION_CHANGEANDVALIDATE_SYMBOL: ('ИЗМЕНЕНИЕИКОНТРОЛЬ' | 'CHANGEANDVALIDATE') -> popMode;
ANNOTATION_CUSTOM_SYMBOL: IDENTIFIER -> popMode;
ANNOTATION_WHITE_SPACE: WHITE_SPACE -> channel(HIDDEN), type(WHITE_SPACE);
ANNOTATION_UNKNOWN: UNKNOWN -> channel(HIDDEN);

mode LABEL_MODE;
LABEL_IDENTIFIER : IDENTIFIER -> type(IDENTIFIER), popMode;

mode REGION_MODE;
REGION_WHITE_SPACE: PREPROC_WHITE_SPACE -> channel(HIDDEN), type(WHITE_SPACE);
REGION_IDENTIFIER : IDENTIFIER -> type(PREPROC_IDENTIFIER), popMode;

mode USE_MODE;
fragment USE_LETTER: LETTER | '-';
USE_WHITE_SPACE: PREPROC_WHITE_SPACE -> channel(HIDDEN), type(WHITE_SPACE);
USE_STRING : PREPROC_STRING -> type(PREPROC_STRING), popMode;
USE_IDENTIFIER : ( USE_LETTER | DIGIT )+ -> type(PREPROC_IDENTIFIER), popMode;

mode DOT_MODE;
DOT_WHITE_SPACE: PREPROC_WHITE_SPACE -> channel(HIDDEN), type(WHITE_SPACE);
DOT_IDENTIFIER : IDENTIFIER -> type(IDENTIFIER), popMode;

mode PREPROC_DELETE_MODE;
PREPROC_ENDDELETE
    : '#' [ \t]*
    (
          'КОНЕЦУДАЛЕНИЯ'
        | 'ENDDELETE'
    )
    -> popMode, channel(PREPROC_DELETE_CHANNEL);
PREPROC_DELETE_WHITE_SPACE: PREPROC_WHITE_SPACE -> channel(HIDDEN), type(WHITE_SPACE);
PREPROC_DELETE_LINE_COMMENT: LINE_COMMENT -> channel(HIDDEN), type(LINE_COMMENT);
PREPROC_DELETE_NEWLINE: PREPROC_NEWLINE -> channel(HIDDEN), type(PREPROC_NEWLINE);
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
AWAIT_KEYWORD: 'ЖДАТЬ' | 'AWAIT';

// всегда в конце мода
Async_IDENTIFIER: IDENTIFIER -> type(IDENTIFIER);
Async_UNKNOWN: UNKNOWN -> type(UNKNOWN);
