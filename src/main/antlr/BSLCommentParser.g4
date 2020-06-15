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
parser grammar BSLCommentParser;

@members {
    int currentMulLevel = 0;
}
options {
    tokenVocab = BSLCommentLexer;
    contextSuperClass = 'BSLParserRuleContext';
}

// ROOT
doc: description? parameters? returnSection? example?;

description: (COMMENT_STRING | COMMENT_COMMA | COMMENT_MINUS | COMMENT_NEWLINE)+;
parameters: COMMENT_NEWLINE? COMMENT_PARAMETERS parameter+;

returnSection: COMMENT_NEWLINE? COMMENT_RETURNS COMMENT_NEWLINE (COMMENT_MINUS? type)? (COMMENT_MINUS description?)?;
example: COMMENT_NEWLINE? COMMENT_EXAMPLE exampleDescription;

parameter: COMMENT_NEWLINE parameterBody subparameters?;
parameterBody: parameterName COMMENT_MINUS (type (COMMENT_COMMA type)*) COMMENT_MINUS? parameterDescription=description;
subparameters:
    subparameter+
;
subparameter:
    COMMENT_NEWLINE
    COMMENT_MUL
    parameterBody
    {$COMMENT_MUL.text.length() == currentMulLevel + 1}?
    { currentMulLevel = $COMMENT_MUL.text.length(); }
    subparameters?
;
parameterName: COMMENT_STRING;
type: COMMENT_STRING (COMMENT_CONTAINS COMMENT_STRING)?;

exampleDescription : COMMENT_STRING+;
