parser grammar BSL;

options {
    tokenVocab = BSLLexer;
}

//file: shebang? LINE_COMMENT* preproceccor* using* moduleVars? subs? codeBlock? EOF;
file: LINE_COMMENT* moduleVars? subs? codeBlock? EOF;

//shebang          : HASH '!' ~WHITE_SPACE*;
//preproceccor     : HASH ~WHITE_SPACE*;
//using            : HASH ('use'|'использовать') (STRING | IDENTIFIER);
moduleVars       : (VAR_KEYWORD moduleVarsList SEMICOLON?)+;
moduleVarsList   : moduleVarDeclaration (COMMA moduleVarDeclaration)*;
moduleVarDeclaration: var_name EXPORT_KEYWORD?;
subs             : sub+;
sub              : subdeclaration subCodeBlock? endOfSub;
subCodeBlock     : (subVarDeclaration SEMICOLON?)* codeBlock;
subVarDeclaration: VAR_KEYWORD subVarsList;
subVarsList      : var_name (COMMA var_name)*;
var_name         : IDENTIFIER;
paramName        : IDENTIFIER;
annotationParam  : (paramName (ASSIGN const_value)?) | const_value;
annotationParams : LPAREN annotationParam (COMMA annotationParam)* RPAREN;
annotation       : AMPERSAND annotationName annotationParams?;
annotationName   : IDENTIFIER;
subdeclaration   : annotation* (PROCEDURE_KEYWORD | FUNCTION_KEYWORD) subName LPAREN param_list? RPAREN EXPORT_KEYWORD?;
subName          : IDENTIFIER;
codeBlock        : (command SEMICOLON*)*;
numeric          : FLOAT | DECIMAL;
param_list       : param (COMMA param)*;
param            : VAL_KEYWORD? IDENTIFIER (ASSIGN default_value)?;
default_value    : const_value;
const_value      : numeric | string_constant | TRUE | FALSE | UNDEFINED | NULL | DATETIME;
string_constant  : (STRING | (STRINGSTART STRINGPART* STRINGTAIL))+;
endOfSub         : ENDPROCEDURE_KEYWORD | ENDFUNCTION_KEYWORD;
command          : (assignment | construction);
assignment       : complexIDENTIFIER (ASSIGN expression)?;
call_param_list  : call_param (COMMA call_param)*;
call_param       : expression?;
expression       : member (operation member)*;
operation        : PLUS | MINUS | MUL | QUOTIENT | MODULO | boolOperation | compareOperation;
compareOperation : LESS | LESS_OR_EQUAL | GREATER | GREATER_OR_EQUAL | ASSIGN | NOT_EQUAL;
boolOperation    : OR_KEYWORD | AND_KEYWORD;
unaryModifier    : NOT_KEYWORD | MINUS;
member           : unaryModifier? (const_value | complexIdentifier | ( LPAREN expression RPAREN ) | questionOperator);
newExpression    : NEW_KEYWORD typeName do_call? | NEW_KEYWORD do_call;
typeName         : IDENTIFIER;
complexIdentifier: (IDENTIFIER | newExpression) modifier*;
modifier         : access_property | access_index | do_call;
access_index     : LBRACK expression RBRACK;
access_property  : DOT IDENTIFIER;
do_call          : LPAREN call_param_list? RPAREN;
construction     : if_expression | while_expression | for_expression
    | try_expression | return_expression | continue_expression | raise_expression;
continue_expression: CONTINUE_KEYWORD;
raise_expression : RAISE_KEYWORD expression?;
if_expression    : IF_KEYWORD expression THEN_KEYWORD codeBlock
    (ELSEIF_KEYWORD expression THEN_KEYWORD codeBlock)* (ELSE_KEYWORD codeBlock)? ENDIF_KEYWORD;
while_expression : WHILE_KEYWORD expression DO_KEYWORD codeBlock ENDDO_KEYWORD;
for_expression   : countable_for_expression | for_each_expression;
countable_for_expression: FOR_KEYWORD IDENTIFIER ASSIGN expression TO_KEYWORD expression DO_KEYWORD codeBlock ENDDO_KEYWORD;
for_each_expression: FOR_KEYWORD EACH_KEYWORD IDENTIFIER FROM_KEYWORD expression DO_KEYWORD codeBlock ENDDO_KEYWORD;
try_expression    : TRY_KEYWORD codeBlock EXCEPT_KEYWORD codeBlock ENDTRY_KEYWORD;
return_expression: RETURN_KEYWORD expression?;
questionOperator : QUESTION LPAREN expression COMMA expression COMMA expression RPAREN;



