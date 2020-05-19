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
parser grammar BSLPreprocessorParser;

options {
    tokenVocab = BSLLexer;
    contextSuperClass = 'BSLParserRuleContext';
}

@parser::header
{
import java.util.Stack;
import java.util.HashSet;
}

@parser::members
{
Stack<Boolean> conditions = new Stack<Boolean>();
public HashSet<String> predefinedSymbols = new HashSet<String>();

private boolean allConditions() {
	for(boolean condition: conditions) {
		if (!condition)
			return false;
	}
	return true;
}
}


// preprocessor
shebang          : HASH PREPROC_EXCLAMATION_MARK (PREPROC_ANY | PREPROC_IDENTIFIER)*;

preprocessor : HASH preprocessor_directive[true];

preprocessor_directive [boolean value]
    : PREPROC_IF_KEYWORD expr=preproc_expression PREPROC_THEN_KEYWORD
    {
        if ($expr.value == null) $value = false;
        else {
            $value = $expr.value.equals("true") && allConditions();
            conditions.push($expr.value.equals("true"));
        }
    }
    | PREPROC_ELSIF_KEYWORD expr=preproc_expression PREPROC_THEN_KEYWORD
    {
        if (!conditions.peek()) {
            conditions.pop();
            $value = $expr.value.equals("true") && allConditions();
            conditions.push($expr.value.equals("true"));
        } else $value = false;
    }
    | PREPROC_ELSE_KEYWORD
    {
        if (!conditions.peek()) {
            conditions.pop();
            $value = true && allConditions();
            conditions.push(true);
        } else $value = false;
    }
    | PREPROC_ENDIF_KEYWORD
    {
        conditions.pop();
        try {
            $value = conditions.peek();
        } catch (Exception e) {
            _errHandler.reportMatch(this);
        }
    }
    | PREPROC_REGION regionName=PREPROC_IDENTIFIER
    { $value = allConditions(); }
    | PREPROC_END_REGION
    { $value = allConditions(); }
    | PREPROC_INSERT_SYMBOL
    { $value = allConditions(); }
    | PREPROC_ENDINSERT_SYMBOL
    { $value = allConditions(); }
    | PREPROC_DELETE_SYMBOL
    { $value = allConditions(); }
    | PREPROC_ENDDELETE_SYMBOL
    { $value = allConditions(); }
    | PREPROC_USE_KEYWORD usedLib=(PREPROC_STRING | PREPROC_IDENTIFIER)
    { $value = allConditions(); };

preproc_expression returns [String value]
    : PREPROC_LPAREN expr=preproc_expression PREPROC_RPAREN
    { $value = $expr.value; }
    | preproc_symbol
    {
    if($preproc_symbol.start.getType() == BSLLexer.PREPROC_IDENTIFIER){
        $value = predefinedSymbols.contains($preproc_symbol.text.toLowerCase())? "true" : "false";
    }else{
        $value = predefinedSymbols.contains(VOCABULARY.getSymbolicName($preproc_symbol.start.getType())) ? "true" : "false";
    }
    }
    | PREPROC_NOT_KEYWORD expr=preproc_expression
    { $value = $expr.value.equals("true") ? "false" : "true"; }
    | expr1=preproc_expression PREPROC_AND_KEYWORD expr2=preproc_expression
    { $value = ($expr1.value.equals("true") && $expr2.value.equals("true") ? "true" : "false"); }
    | expr1=preproc_expression PREPROC_OR_KEYWORD expr2=preproc_expression
    { $value = ($expr1.value.equals("true") || $expr2.value.equals("true") ? "true" : "false"); }
    ;

preproc_symbol
    : PREPROC_CLIENT_SYMBOL                         #symbol
    | PREPROC_ATCLIENT_SYMBOL                       #symbol
    | PREPROC_SERVER_SYMBOL                         #symbol
    | PREPROC_ATSERVER_SYMBOL                       #symbol
    | PREPROC_MOBILEAPPCLIENT_SYMBOL                #symbol
    | PREPROC_MOBILEAPPSERVER_SYMBOL                #symbol
    | PREPROC_MOBILECLIENT_SYMBOL                   #symbol
    | PREPROC_THICKCLIENTORDINARYAPPLICATION_SYMBOL #symbol
    | PREPROC_THICKCLIENTMANAGEDAPPLICATION_SYMBOL  #symbol
    | PREPROC_EXTERNALCONNECTION_SYMBOL             #symbol
    | PREPROC_THINCLIENT_SYMBOL                     #symbol
    | PREPROC_WEBCLIENT_SYMBOL                      #symbol
    | PREPROC_IDENTIFIER                            #unksymbol
    ;

