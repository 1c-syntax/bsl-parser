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
parser grammar BSLParser;

options {
    tokenVocab = BSLLexer;
    contextSuperClass = 'BSLParserRuleContext';
    incremental = true;
}

// ROOT
file: shebang? moduleVars? codeBlockBeforeSub subs? codeBlock EOF;

// preprocessor
shebang          : HASH PREPROC_EXCLAMATION_MARK (PREPROC_ANY | PREPROC_IDENTIFIER)*;

usedLib          : (PREPROC_STRING | PREPROC_IDENTIFIER);
use              : PREPROC_USE_KEYWORD usedLib;

regionStart      : PREPROC_REGION regionName;
regionEnd        : PREPROC_END_REGION;
regionName       : PREPROC_IDENTIFIER;

// compiler directives
compilerDirectiveSymbol
    : ANNOTATION_ATSERVERNOCONTEXT_SYMBOL
    | ANNOTATION_ATCLIENTATSERVERNOCONTEXT_SYMBOL
    | ANNOTATION_ATCLIENTATSERVER_SYMBOL
    | ANNOTATION_ATCLIENT_SYMBOL
    | ANNOTATION_ATSERVER_SYMBOL
    ;

compilerDirective
    : AMPERSAND compilerDirectiveSymbol
    ;

// annotations
annotationName
    : ANNOTATION_CUSTOM_SYMBOL
    ;
annotationParamName
    : IDENTIFIER
    ;
annotation
    : AMPERSAND annotationName annotationParams?
    ;
annotationParams
    : LPAREN
      (
        annotationParam
        (COMMA annotationParam)*
      )?
      RPAREN
    ;
annotationParam
    : (annotationParamName (ASSIGN constValue)?)
    | constValue
    ;

// vars
var_name         : IDENTIFIER;

moduleVars       : moduleVar+;
moduleVar        : ( compilerDirective | annotation)* VAR_KEYWORD moduleVarsList SEMICOLON?;
moduleVarsList   : moduleVarDeclaration (COMMA moduleVarDeclaration)*;
moduleVarDeclaration: var_name EXPORT_KEYWORD?;

subVars          : subVar+;
subVar           : ( compilerDirective | annotation)* VAR_KEYWORD subVarsList SEMICOLON?;
subVarsList      : subVarDeclaration (COMMA subVarDeclaration)*;
subVarDeclaration: var_name;

// subs
subName          : IDENTIFIER;

subs             : sub+;
sub              : procedure | function;
procedure        : procDeclaration subCodeBlock ENDPROCEDURE_KEYWORD;
function         : funcDeclaration subCodeBlock ENDFUNCTION_KEYWORD;
procDeclaration  : ( compilerDirective | annotation)* PROCEDURE_KEYWORD subName LPAREN paramList? RPAREN EXPORT_KEYWORD?;
funcDeclaration  : ( compilerDirective | annotation)* FUNCTION_KEYWORD subName LPAREN paramList? RPAREN EXPORT_KEYWORD?;
subCodeBlock     : subVars? codeBlock;

// statements
continueStatement : CONTINUE_KEYWORD;
breakStatement    : BREAK_KEYWORD;
raiseStatement    : RAISE_KEYWORD expression?;
ifStatement       : IF_KEYWORD expression THEN_KEYWORD codeBlock
    (ELSIF_KEYWORD expression THEN_KEYWORD codeBlock)* (ELSE_KEYWORD codeBlock)? ENDIF_KEYWORD;
whileStatement    : WHILE_KEYWORD expression DO_KEYWORD codeBlock ENDDO_KEYWORD;
forStatement      : FOR_KEYWORD IDENTIFIER ASSIGN expression TO_KEYWORD expression DO_KEYWORD codeBlock ENDDO_KEYWORD;
forEachStatement  : FOR_KEYWORD EACH_KEYWORD IDENTIFIER IN_KEYWORD expression DO_KEYWORD codeBlock ENDDO_KEYWORD;
tryStatement      : TRY_KEYWORD tryCodeBlock EXCEPT_KEYWORD exceptCodeBlock ENDTRY_KEYWORD;
returnStatement   : RETURN_KEYWORD expression?;
executeStatement  : EXECUTE_KEYWORD (doCall | callParamList);
callStatement     : ((IDENTIFIER | globalMethodCall) modifier* accessCall) | globalMethodCall;

labelName         : IDENTIFIER;
label             : TILDA labelName COLON;
gotoStatement     : GOTO_KEYWORD TILDA labelName;

tryCodeBlock :  codeBlock;
exceptCodeBlock : codeBlock;

event
    : expression
    ;

handler
    : expression
    ;
addHandlerStatement
    : ADDHANDLER_KEYWORD event COMMA handler
    ;
removeHandlerStatement
    : REMOVEHANDLER_KEYWORD event COMMA handler
    ;

ternaryOperator   : QUESTION LPAREN expression COMMA expression COMMA expression RPAREN;

// main
codeBlockBeforeSub
    : codeBlock
    ;
codeBlock        : (statement)*;
numeric          : FLOAT | DECIMAL;
paramList        : param (COMMA param)*;
param            : VAL_KEYWORD? IDENTIFIER (ASSIGN defaultValue)?;
defaultValue     : constValue;
constValue       : (MINUS | PLUS)? numeric | string | TRUE | FALSE | UNDEFINED | NULL | DATETIME;
multilineString  : STRINGSTART (STRINGPART | BAR)* STRINGTAIL;
string           : (STRING | multilineString)+;
statement
     : (
        (
            ( label (callStatement | compoundStatement | assignment)?)
            |
            (callStatement | compoundStatement | assignment)
        )
        SEMICOLON?
    )
    | SEMICOLON
    ;
assignment       : complexIdentifier  ASSIGN (expression)?;
callParamList    : callParam (COMMA callParam)*;
callParam        : expression?;
expression       : member (operation member)*;
operation        : PLUS | MINUS | MUL | QUOTIENT | MODULO | boolOperation | compareOperation;
compareOperation : LESS | LESS_OR_EQUAL | GREATER | GREATER_OR_EQUAL | ASSIGN | NOT_EQUAL;
boolOperation    : OR_KEYWORD | AND_KEYWORD;
unaryModifier    : NOT_KEYWORD | MINUS | PLUS;
member           : unaryModifier? (constValue | complexIdentifier | ( LPAREN expression RPAREN ));
newExpression    : NEW_KEYWORD typeName doCall? | NEW_KEYWORD doCall;
typeName         : IDENTIFIER;
methodCall       : methodName doCall;
globalMethodCall : methodName doCall;
methodName       : IDENTIFIER;
complexIdentifier: (IDENTIFIER | newExpression | ternaryOperator | globalMethodCall) modifier*;
modifier         : accessProperty | accessIndex| accessCall;
accessCall        : DOT methodCall;
accessIndex      : LBRACK expression RBRACK;
accessProperty   : DOT IDENTIFIER;
doCall           : LPAREN callParamList RPAREN;

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
    | executeStatement
    | gotoStatement
    | addHandlerStatement
    | removeHandlerStatement
    ;
