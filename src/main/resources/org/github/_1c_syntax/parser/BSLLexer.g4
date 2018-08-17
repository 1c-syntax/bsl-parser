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

// literals
TRUE:'true'|'истина';
FALSE:'false'|'ложь';
UNDEFINED:'undefined'|'неопределено';
NULL:'null';
DECIMAL: DIGIT+;
DATETIME: SQUOTE(~['\n\r])*SQUOTE?; // TODO: Честная регулярка

//FLOAT_EXPONENT : [eE] [+-]? {DIGIT}+; экспонента? в 1С?
FLOAT : DIGIT+ '.' DIGIT*;
STRINGSTART: QUOTE(~["\n\r])*;
STRING: QUOTE(~["\n\r])*QUOTE;
STRINGTAIL: BAR(~["\n\r])*QUOTE;
STRINGPART: BAR(~["\n\r])*;

// keywords
PROCEDURE_KEYWORD: 'процедура'|'procedure';
FUNCTION_KEYWORD: 'функция'|'function';
ENDPROCEDURE_KEYWORD: 'конецпроцедуры'|'endprocedure';
ENDFUNCTION_KEYWORD: 'конецфункции'|'endfunction';
EXPORT_KEYWORD:'экспорт'|'export';
VAL_KEYWORD:'знач'|'val';
ENDIF_KEYWORD: 'конецесли'|'endif';
ENDDO_KEYWORD: 'конеццикла'|'enddo';
IF_KEYWORD: 'если'|'if';
ELSEIF_KEYWORD: 'иначеесли'|'elsif';
ELSE_KEYWORD: 'иначе'|'else';
THEN_KEYWORD: 'тогда'|'then';
WHILE_KEYWORD: 'пока'|'while';
DO_KEYWORD: 'цикл'|'do';
FOR_KEYWORD: 'для'|'for';
TO_KEYWORD: 'по'|'to';
EACH_KEYWORD: 'каждого'|'each';
FROM_KEYWORD: 'из'|'from';
TRY_KEYWORD: 'попытка'|'try';
EXCEPT_KEYWORD: 'исключение'|'except';
ENDTRY_KEYWORD: 'конецпопытки'|'endtry';
RETURN_KEYWORD: 'возврат'|'return';
CONTINUE_KEYWORD: 'продолжить'|'continue';
RAISE_KEYWORD: 'вызватьисключение'|'raise';
VAR_KEYWORD: 'перем'|'var';
NOT_KEYWORD: 'не'|'not';
OR_KEYWORD: 'или'|'or';
AND_KEYWORD: 'и'|'and';
NEW_KEYWORD: 'новый'|'new';

fragment LETTER: [\p{Letter}] | '_';   
IDENTIFIER : LETTER ( LETTER | DIGIT )*;
