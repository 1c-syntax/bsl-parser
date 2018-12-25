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
parser grammar BSLParser;

options {
    tokenVocab = BSLLexer;
    contextSuperClass = 'org.github._1c_syntax.parser.BSLParserRuleContext';
}

// ROOT
file: shebang? preprocessor* moduleVars? preprocessor* subs? codeBlock? EOF;

// preprocessor
shebang          : HASH PREPROC_EXCLAMATION_MARK (PREPROC_ANY | PREPROC_IDENTIFIER)*;

usedLib          : (PREPROC_STRING | PREPROC_IDENTIFIER);
use              : PREPROC_USE_KEYWORD usedLib;

regionStart      : PREPROC_REGION regionName;
regionEnd        : PREPROC_END_REGION;
regionName       : PREPROC_IDENTIFIER;

preprocessor     : HASH (use | regionStart | regionEnd);

// vars
var_name         : IDENTIFIER;

moduleVars       : moduleVar+;
moduleVar        : VAR_KEYWORD moduleVarsList SEMICOLON?;
moduleVarsList   : moduleVarDeclaration (COMMA moduleVarDeclaration)*;
moduleVarDeclaration: var_name EXPORT_KEYWORD?;

subVars          : subVar;
subVar           : VAR_KEYWORD subVarsList SEMICOLON?;
subVarsList      : subVarDeclaration (COMMA subVarDeclaration)*;
subVarDeclaration: var_name;

// subs
subName          : IDENTIFIER;

subs             : sub+;
sub              : procedure | function;
procedure        : procDeclaration subCodeBlock ENDPROCEDURE_KEYWORD;
function         : funcDeclaration subCodeBlock ENDFUNCTION_KEYWORD;
procDeclaration  : annotation* PROCEDURE_KEYWORD subName LPAREN param_list? RPAREN EXPORT_KEYWORD?;
funcDeclaration  : annotation* FUNCTION_KEYWORD subName LPAREN param_list? RPAREN EXPORT_KEYWORD?;
subCodeBlock     : subVars? codeBlock;

// annotations
annotationParamName : IDENTIFIER;
annotationParam  : (annotationParamName (ASSIGN const_value)?) | const_value;
annotationParams : LPAREN annotationParam (COMMA annotationParam)* RPAREN;
annotation       : AMPERSAND annotationName annotationParams?;
annotationName   : IDENTIFIER;

// main
codeBlock        : (command | preprocessor)*;
numeric          : FLOAT | DECIMAL;
param_list       : param (COMMA param)*;
param            : VAL_KEYWORD? IDENTIFIER (ASSIGN default_value)?;
default_value    : const_value;
const_value      : numeric | string_constant | TRUE | FALSE | UNDEFINED | NULL | DATETIME;
string_constant  : (STRING | (STRINGSTART STRINGPART* STRINGTAIL))+;
command          : (assignment | construction | preprocessor)? SEMICOLON;
assignment       : complexIdentifier (ASSIGN expression)?;
call_param_list  : call_param (COMMA call_param)*;
call_param       : expression;
expression       : member (operation member)*;
operation        : PLUS | MINUS | MUL | QUOTIENT | MODULO | boolOperation | compareOperation;
compareOperation : LESS | LESS_OR_EQUAL | GREATER | GREATER_OR_EQUAL | ASSIGN | NOT_EQUAL;
boolOperation    : OR_KEYWORD | AND_KEYWORD;
unaryModifier    : NOT_KEYWORD | MINUS;
member           : unaryModifier? (const_value | complexIdentifier | ( LPAREN expression RPAREN ) | ternaryOperator);
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
ternaryOperator   : QUESTION LPAREN expression COMMA expression COMMA expression RPAREN;
