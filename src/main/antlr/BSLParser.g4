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
}

// ROOT
file: shebang? preprocessor* moduleVars? preprocessor* (fileCodeBlockBeforeSub subs)? fileCodeBlock EOF;

// preprocessor
shebang          : HASH PREPROC_EXCLAMATION_MARK (PREPROC_ANY | PREPROC_IDENTIFIER)*;

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
    : PREPROC_LPAREN preproc_expression PREPROC_RPAREN
    | PREPROC_NOT_KEYWORD preproc_expression
    | preproc_expression preproc_boolOperation preproc_expression
    | preproc_symbol
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
procDeclaration  : beforeDeclaration PROCEDURE_KEYWORD subName LPAREN paramList? RPAREN EXPORT_KEYWORD?;
funcDeclaration  : beforeDeclaration FUNCTION_KEYWORD subName LPAREN paramList? RPAREN EXPORT_KEYWORD?;
beforeDeclaration: (preprocessor | compilerDirective | annotation)*;
subCodeBlock     : subVars? codeBlock;

// statements
continueStatement : CONTINUE_KEYWORD;
breakStatement    : BREAK_KEYWORD;
raiseStatement    : RAISE_KEYWORD expression?;
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
whileStatement    : WHILE_KEYWORD cycleBody;
forStatement      : FOR_KEYWORD (forVarStatement|forEachStatement) cycleBody;
forVarStatement   : IDENTIFIER ASSIGN expression TO_KEYWORD;
forEachStatement  : EACH_KEYWORD IDENTIFIER IN_KEYWORD;
cycleBody         : expression DO_KEYWORD codeBlock ENDDO_KEYWORD;

tryStatement      : TRY_KEYWORD tryCodeBlock EXCEPT_KEYWORD exceptCodeBlock ENDTRY_KEYWORD;
returnStatement   : RETURN_KEYWORD expression?;
executeStatement  : EXECUTE_KEYWORD (doCall | callParamList);
labelRef          : TILDA labelName=IDENTIFIER;
label             : labelRef COLON;
gotoStatement     : GOTO_KEYWORD labelRef;

tryCodeBlock :  codeBlock;
exceptCodeBlock : codeBlock;

addHandlerStatement
    : ADDHANDLER_KEYWORD event=expression COMMA handler=expression
    ;
removeHandlerStatement
    : REMOVEHANDLER_KEYWORD event=expression COMMA handler=expression
    ;

ternaryOperator   : QUESTION LPAREN expression COMMA expression COMMA expression RPAREN;

// main
fileCodeBlockBeforeSub
    : codeBlock
    ;
fileCodeBlock
    : codeBlock
    ;
codeBlock        : statement*;
numeric          : FLOAT | DECIMAL;
paramList        : param (COMMA param)*;
param            : VAL_KEYWORD? IDENTIFIER (ASSIGN constValue)?;
constValue       : (MINUS | PLUS)? numeric | string | TRUE | FALSE | UNDEFINED | NULL | DATETIME;
multilineString  : STRINGSTART (STRINGPART | BAR)* STRINGTAIL;
string           : (STRING | multilineString)+;
statement
     : preprocessor
     | label statement
     | assignment
     | ifStatement
     | whileStatement
     | forStatement
     | tryStatement
     | returnStatement
     | continueStatement
     | breakStatement
     | raiseStatement
     | executeStatement
     | gotoStatement
     | addHandlerStatement
     | removeHandlerStatement
     | methodCall
     | compositionCall
     | SEMICOLON
    ;
assignment       : lValue preprocessor* ASSIGN expression;
callParamList    : callParam (COMMA callParam)*;
callParam        : expression?;
expression       : preprocessor expression
                 | expression preprocessor
                 | IDENTIFIER
                 | methodCall
                 | ternaryOperator
                 | constValue
                 | newExpression
                 | LPAREN expression RPAREN
                 | composition
                 | unaryMathOperation expression
                 | expression numberoperation expression
                 | expression mathOperation expression
                 | NOT_KEYWORD expression
                 | expression compareOperation expression
                 | expression AND_KEYWORD expression
                 | expression OR_KEYWORD expression
                 | preprocessor
                 ;

numberoperation         : MUL | QUOTIENT | MODULO;
compareOperation        : LESS | LESS_OR_EQUAL | GREATER | GREATER_OR_EQUAL | ASSIGN | NOT_EQUAL;
unaryMathOperation      : PLUS | MINUS;
mathOperation           : PLUS | MINUS;
newExpression           : NEW_KEYWORD (typeName doCall? | doCall);
typeName                : IDENTIFIER;
methodCall              : methodName=IDENTIFIER doCall;
composition             : (IDENTIFIER | methodCall | newExpression|ternaryOperator| LPAREN composition RPAREN) member*;
compositionCall         : composition DOT methodCall;
member                  : DOT accessProperty
                        | DOT methodCall
                        | accessIndex;

lValue                  : IDENTIFIER
                        | composition (DOT accessProperty|accessIndex);
accessIndex             : LBRACK expression RBRACK;
accessProperty          : IDENTIFIER;
doCall                  : LPAREN callParamList RPAREN;
