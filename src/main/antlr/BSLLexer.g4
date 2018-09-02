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
HASH: '#';

QUOTE: '"';
SQUOTE: '\'';
BAR: '|';

fragment А: 'А' | 'а';
fragment В: 'В' | 'в';
fragment Г: 'Г' | 'г';
fragment Д: 'Д' | 'д';
fragment Ё: 'Ё' | 'ё';
fragment Е: 'Е' | 'е';
fragment Ж: 'Ж' | 'ж';
fragment З: 'З' | 'з';
fragment И: 'И' | 'и';
fragment Й: 'Й' | 'й';
fragment К: 'К' | 'к';
fragment Л: 'Л' | 'л';
fragment М: 'М' | 'м';
fragment Н: 'Н' | 'н';
fragment О: 'О' | 'о';
fragment П: 'П' | 'п';
fragment Р: 'Р' | 'р';
fragment С: 'С' | 'с';
fragment Т: 'Т' | 'т';
fragment У: 'У' | 'у';
fragment Ф: 'Ф' | 'ф';
fragment Х: 'Х' | 'х';
fragment Ц: 'Ц' | 'ц';
fragment Ч: 'Ч' | 'ч';
fragment Ш: 'Ш' | 'ш';
fragment Щ: 'Щ' | 'щ';
fragment Ъ: 'Ъ' | 'ъ';
fragment Ы: 'Ы' | 'ы';
fragment Ь: 'Ь' | 'ь';
fragment Э: 'Э' | 'э';
fragment Ю: 'Ю' | 'ю';
fragment Я: 'Я' | 'я';
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
TRUE: И С Т И Н А | T R U E;
FALSE: Л О Ж Ь | F A L S E;
UNDEFINED: Н Е О П Р Е Д Е Л Е Н О | U N D E F I N E D;
NULL: N U L L;
DECIMAL: DIGIT+;
DATETIME: SQUOTE(~['\n\r])*SQUOTE?; // TODO: Честная регулярка

//FLOAT_EXPONENT : [eE] [+-]? {DIGIT}+; экспонента? в 1С?
FLOAT : DIGIT+ '.' DIGIT*;
STRINGSTART: QUOTE(~["\n\r])*;
STRING: QUOTE(~["\n\r])*QUOTE;
STRINGTAIL: BAR(~["\n\r])*QUOTE;
STRINGPART: BAR(~["\n\r])*;

// keywords
PROCEDURE_KEYWORD: П Р О Ц Е Д У Р А | P R O C E D U R E;
FUNCTION_KEYWORD: Ф У Н К Ц И Я | F U N C T I O N;
ENDPROCEDURE_KEYWORD: К О Н Е Ц П Р О Ц Е Д У Р Ы | E N D P R O C E D U R E;
ENDFUNCTION_KEYWORD: К О Н Е Ц Ф У Н К Ц И И | E N D F U N C T I O N;
EXPORT_KEYWORD: Э К С П О Р Т | E X P O R T;
VAL_KEYWORD: З Н А Ч | V A L;
ENDIF_KEYWORD: К О Н Е Ц Е С Л И | E N D I F;
ENDDO_KEYWORD: К О Н Е Ц Ц И К Л А | E N D D O;
IF_KEYWORD: Е С Л И | I F;
ELSEIF_KEYWORD: И Н А Ч Е Е С Л И | E L S I F;
ELSE_KEYWORD: И Н А Ч Е | E L S E;
THEN_KEYWORD: Т О Г Д А | T H E N;
WHILE_KEYWORD: П О К А | W H I L E;
DO_KEYWORD: Ц И К Л | D O;
FOR_KEYWORD: Д Л Я | F O R;
TO_KEYWORD: П О | T O;
EACH_KEYWORD: К А Ж Д О Г О | E A C H;
FROM_KEYWORD: И З | F R O M;
TRY_KEYWORD: П О П Ы Т К А | T R Y;
EXCEPT_KEYWORD: И С К Л Ю Ч Е Н И Е | E X C E P T;
ENDTRY_KEYWORD: К О Н Е Ц П О П Ы Т К И | E N D T R Y;
RETURN_KEYWORD: В О З В Р А Т | R E T U R N;
CONTINUE_KEYWORD: П Р О Д О Л Ж И Т Ь | C O N T I N U E;
RAISE_KEYWORD: В Ы З В А Т Ь И С К Л Ю Ч Е Н И Е | R A I S E;
VAR_KEYWORD: П Е Р Е М | V A R;
NOT_KEYWORD: Н Е| N O T;
OR_KEYWORD: И Л И | O R;
AND_KEYWORD: И | A N D;
NEW_KEYWORD: Н О В Ы Й | N E W;
GOTO_KEYWORD: П Е Р Е Й Т И | G O T O;
BREAK_KEYWORD: П Р Е Р В А Т Ь | B R E A K;
EXECUTE_KEYWORD: В Ы П О Л Н И Т Ь | E X E C U T E;

fragment LETTER: [\p{Letter}] | '_';   
IDENTIFIER : LETTER ( LETTER | DIGIT )*;
