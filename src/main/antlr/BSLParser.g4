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
file: shebang? preprocessor* moduleVars? preprocessor* subs? codeBlock EOF;

// preprocessor
shebang          : HASH PREPROC_EXCLAMATION_MARK (PREPROC_ANY | PREPROC_IDENTIFIER)*;

usedLib          : (PREPROC_STRING | PREPROC_IDENTIFIER);
use              : PREPROC_USE_KEYWORD usedLib;

regionStart      : PREPROC_REGION regionName;
regionEnd        : PREPROC_END_REGION;
regionName       : PREPROC_IDENTIFIER;

preproc_if       : PREPROC_IF_KEYWORD preproc_logicalExpression PREPROC_THEN_KEYWORD;
preproc_elsif    : PREPROC_ELSIF_KEYWORD preproc_logicalExpression PREPROC_THEN_KEYWORD;
preproc_else     : PREPROC_ELSE_KEYWORD;
preproc_endif    : PREPROC_ENDIF_KEYWORD;

preproc_logicalExpression
    : PREPROC_NOT_KEYWORD? preproc_symbol (preproc_boolOperation PREPROC_NOT_KEYWORD? preproc_symbol)*;
preproc_symbol
    : PREPROC_CLIENT_SYMBOL
    | PREPROC_ATCLIENT_SYMBOL
    | PREPROC_SERVER_SYMBOL
    | PREPROC_ATSERVER_SYMBOL
    | PREPROC_MOBILEAPPCLIENT_SYMBOL
    | PREPROC_MOBILEAPPSERVER_SYMBOL
    | PREPROC_MOBILECLIENT_SYMBOL
    | PREPROC_THICKCLIENTORDINARYAPPLICATION_SYMBOL
    | PREPROC_THICKCLIENTMANAGEDAPPLICATION_SYMBOL
    | PREPROC_EXTERNALCONNECTION_SYMBOL
    | PREPROC_THINCLIENT_SYMBOL
    | PREPROC_WEBCLIENT_SYMBOL
    ;
preproc_boolOperation
    : PREPROC_OR_KEYWORD
    | PREPROC_AND_KEYWORD
    ;

preprocessor
    : HASH
        (regionStart
        | regionEnd
        | preproc_if
        | preproc_elsif
        | preproc_else
        | preproc_endif
        | use
        )
    ;

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
procDeclaration  : preprocessor* annotation* PROCEDURE_KEYWORD subName LPAREN param_list? RPAREN EXPORT_KEYWORD?;
funcDeclaration  : preprocessor* annotation* FUNCTION_KEYWORD subName LPAREN param_list? RPAREN EXPORT_KEYWORD?;
subCodeBlock     : subVars? codeBlock;

// annotations
annotationParamName : IDENTIFIER;
annotationParam  : (annotationParamName (ASSIGN const_value)?) | const_value;
annotationParams : LPAREN annotationParam (COMMA annotationParam)* RPAREN;
annotation       : AMPERSAND annotationName annotationParams?;
annotationName   : IDENTIFIER;

// statements
continueStatement : CONTINUE_KEYWORD;
breakStatement    : BREAK_KEYWORD;
raiseStatement    : RAISE_KEYWORD expression?;
ifStatement       : IF_KEYWORD expression THEN_KEYWORD codeBlock
    (ELSIF_KEYWORD expression THEN_KEYWORD codeBlock)* (ELSE_KEYWORD codeBlock)? ENDIF_KEYWORD;
whileStatement    : WHILE_KEYWORD expression DO_KEYWORD codeBlock ENDDO_KEYWORD;
forStatement      : FOR_KEYWORD IDENTIFIER ASSIGN expression TO_KEYWORD expression DO_KEYWORD codeBlock ENDDO_KEYWORD;
forEachStatement  : FOR_KEYWORD EACH_KEYWORD IDENTIFIER FROM_KEYWORD expression DO_KEYWORD codeBlock ENDDO_KEYWORD;
tryStatement      : TRY_KEYWORD codeBlock EXCEPT_KEYWORD codeBlock ENDTRY_KEYWORD;
returnStatement   : RETURN_KEYWORD expression?;

labelName         : IDENTIFIER;
label             : TILDA labelName COLON;
gotoStatement     : GOTO_KEYWORD TILDA labelName;

event
    : expression
    ;

handler
    : expression
    ;
addHandlerStatement
    : ADDHANDLER_KEYWORD event handler
    ;
removeHandlerStatement
    : REMOVEHANDLER_KEYWORD event handler
    ;

ternaryOperator   : QUESTION LPAREN expression COMMA expression COMMA expression RPAREN;

// main
codeBlock        : (statement | preprocessor)*;
numeric          : FLOAT | DECIMAL;
param_list       : param (COMMA param)*;
param            : VAL_KEYWORD? IDENTIFIER (ASSIGN default_value)?;
default_value    : const_value;
const_value      : numeric | string | TRUE | FALSE | UNDEFINED | NULL | DATETIME;
multilineString  : (STRINGSTART | QUOTE) STRINGPART* STRINGTAIL;
string           : (STRING | multilineString)+;
statement        : label? (assignment | compoundStatement | preprocessor) SEMICOLON?;
assignment       : complexIdentifier (ASSIGN expression)?;
call_param_list  : call_param (COMMA call_param)*;
call_param       : expression?;
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
do_call          : LPAREN call_param_list RPAREN;
compoundStatement
    : ifStatement
    | whileStatement
    | forStatement
    | forEachStatement
    | tryStatement
    | returnStatement
    | continueStatement
    | breakStatement
    | raiseStatement
    | gotoStatement
    | addHandlerStatement
    | removeHandlerStatement
    ;
