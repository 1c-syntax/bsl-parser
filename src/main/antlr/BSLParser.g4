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
parser grammar BSLParser;

options {
    tokenVocab = BSLLexer;
    contextSuperClass = 'BSLParserRuleContext';
}

// ROOT
file: shebang? native? preprocessor* moduleVars? preprocessor* (fileCodeBlockBeforeSub subs)? fileCodeBlock EOF;

// preprocessor
shebang          : HASH PREPROC_EXCLAMATION_MARK (PREPROC_ANY | PREPROC_IDENTIFIER)*;

native           : HASH PREPROC_NATIVE;

usedLib          : (PREPROC_STRING | PREPROC_IDENTIFIER);
use              : PREPROC_USE_KEYWORD usedLib;

regionStart      : PREPROC_REGION regionName;
regionEnd        : PREPROC_END_REGION;
regionName       : PREPROC_IDENTIFIER;

preproc_if       : PREPROC_IF_KEYWORD preproc_expression PREPROC_THEN_KEYWORD;
preproc_elsif    : PREPROC_ELSIF_KEYWORD preproc_expression PREPROC_THEN_KEYWORD;
preproc_else     : PREPROC_ELSE_KEYWORD;
preproc_endif    : PREPROC_ENDIF_KEYWORD;

preproc_expression
    : ( PREPROC_NOT_KEYWORD? (PREPROC_LPAREN preproc_expression PREPROC_RPAREN ) )
    | preproc_logicalExpression
    ;
preproc_logicalOperand
    : (PREPROC_LPAREN PREPROC_NOT_KEYWORD? preproc_logicalOperand PREPROC_RPAREN)
    | (PREPROC_NOT_KEYWORD? preproc_symbol)
    | (PREPROC_LPAREN preproc_logicalExpression PREPROC_RPAREN)
    ;
preproc_logicalExpression
    : preproc_logicalOperand (preproc_boolOperation preproc_logicalOperand)*
    ;
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
    | PREPROC_MOBILE_STANDALONE_SERVER
    | PREPROC_LINUX
    | PREPROC_WINDOWS
    | PREPROC_MACOS
    | preproc_unknownSymbol
    ;
preproc_unknownSymbol
    : PREPROC_IDENTIFIER
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
    | ANNOTATION_BEFORE_SYMBOL
    | ANNOTATION_AFTER_SYMBOL
    | ANNOTATION_AROUND_SYMBOL
    | ANNOTATION_CHANGEANDVALIDATE_SYMBOL
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
moduleVar        : (preprocessor | compilerDirective | annotation)* VAR_KEYWORD moduleVarsList SEMICOLON?;
moduleVarsList   : moduleVarDeclaration (COMMA moduleVarDeclaration)*;
moduleVarDeclaration: var_name EXPORT_KEYWORD?;

subVars          : subVar+;
subVar           : (preprocessor | compilerDirective | annotation)* VAR_KEYWORD subVarsList SEMICOLON?;
subVarsList      : subVarDeclaration (COMMA subVarDeclaration)*;
subVarDeclaration: var_name;

// subs
subName          : IDENTIFIER;

subs             : sub+;
sub              : procedure | function;
procedure        : procDeclaration subCodeBlock ENDPROCEDURE_KEYWORD;
function         : funcDeclaration subCodeBlock ENDFUNCTION_KEYWORD;
procDeclaration  : (preprocessor | compilerDirective | annotation)* ASYNC_KEYWORD? PROCEDURE_KEYWORD subName LPAREN paramList? RPAREN EXPORT_KEYWORD?;
funcDeclaration  : (preprocessor | compilerDirective | annotation)* ASYNC_KEYWORD? FUNCTION_KEYWORD subName LPAREN paramList? RPAREN EXPORT_KEYWORD?;
subCodeBlock     : subVars? codeBlock;

// statements
continueStatement : CONTINUE_KEYWORD;
breakStatement    : BREAK_KEYWORD;
raiseStatement    : RAISE_KEYWORD (expression? | doCall);
ifStatement
    : ifBranch elsifBranch* elseBranch? ENDIF_KEYWORD
    ;
ifBranch
    : IF_KEYWORD expression THEN_KEYWORD codeBlock
    ;
elsifBranch
    : ELSIF_KEYWORD expression THEN_KEYWORD codeBlock
    ;
elseBranch
    : ELSE_KEYWORD codeBlock
    ;
whileStatement    : WHILE_KEYWORD expression DO_KEYWORD codeBlock ENDDO_KEYWORD;
forStatement      : FOR_KEYWORD IDENTIFIER ASSIGN expression TO_KEYWORD expression DO_KEYWORD codeBlock ENDDO_KEYWORD;
forEachStatement  : FOR_KEYWORD EACH_KEYWORD IDENTIFIER IN_KEYWORD expression DO_KEYWORD codeBlock ENDDO_KEYWORD;
tryStatement      : TRY_KEYWORD tryCodeBlock EXCEPT_KEYWORD exceptCodeBlock ENDTRY_KEYWORD;
returnStatement   : RETURN_KEYWORD expression?;
executeStatement  : EXECUTE_KEYWORD (doCall | callParamList);
callStatement     : ((IDENTIFIER | globalMethodCall) modifier* accessCall) | globalMethodCall;
waitStatement     : waitExpression;

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
waitExpression    : AWAIT_KEYWORD expression;

// main
fileCodeBlockBeforeSub
    : codeBlock
    ;
fileCodeBlock
    : codeBlock
    ;
codeBlock        : (statement | preprocessor)*;
numeric          : FLOAT | DECIMAL;
paramList        : param (COMMA param)*;
param            : (annotation)* VAL_KEYWORD? IDENTIFIER (ASSIGN defaultValue)?;
defaultValue     : constValue;
constValue       : (MINUS | PLUS)? numeric | string | TRUE | FALSE | UNDEFINED | NULL | DATETIME;
multilineString  : STRINGSTART (STRINGPART | BAR | preprocessor)* STRINGTAIL;
string           : (STRING | multilineString)+;
statement
     : (
        (
            ( label (callStatement | waitStatement | compoundStatement | assignment | preprocessor)?)
            |
            (callStatement | waitStatement | compoundStatement | assignment| preprocessor)
        )
        SEMICOLON?
    )
    | SEMICOLON
    ;
assignment       : lValue preprocessor* ASSIGN (preprocessor* expression);
callParamList    : callParam (COMMA callParam)*;
callParam        : expression?;
expression       : member (preprocessor* operation preprocessor* member preprocessor*)*;
operation        : PLUS | MINUS | MUL | QUOTIENT | MODULO | boolOperation | compareOperation;
compareOperation : LESS | LESS_OR_EQUAL | GREATER | GREATER_OR_EQUAL | ASSIGN | NOT_EQUAL;
boolOperation    : OR_KEYWORD | AND_KEYWORD;
unaryModifier    : NOT_KEYWORD | MINUS | PLUS;
member
    : unaryModifier?
    (
        constValue
        | complexIdentifier
        | (( LPAREN expression RPAREN ) modifier*)
        | (IDENTIFIER | globalMethodCall)
        | waitExpression
    );
newExpression    : NEW_KEYWORD typeName doCall? | NEW_KEYWORD doCall;
typeName         : IDENTIFIER;
methodCall       : methodName doCall;
globalMethodCall : methodName doCall;
methodName       : IDENTIFIER;
complexIdentifier: (IDENTIFIER | newExpression | ternaryOperator | globalMethodCall) modifier*;
modifier         : accessProperty | accessIndex | accessCall;
acceptor         : modifier* (accessProperty | accessIndex);
lValue           : (IDENTIFIER | globalMethodCall) acceptor?;
accessCall       : DOT methodCall;
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
