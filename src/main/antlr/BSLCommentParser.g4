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

options {
    tokenVocab = BSLLexer;
    contextSuperClass = 'BSLParserRuleContext';
}

// ROOT
doc: description parameters? return?;

description: COMMENT_STRING+;
parameters:
    COMMENT_PARAMETERS
    firstparamet+;

return: COMMENT_RETURNS (MINUS? type)? (MINUS description?)?;

firstparamet: parameter subparameters;
parameter: parameterName MINUS (type (COMMA type)*) MINUS parametrdescription;
subparameters: subparameter* ;
parametrdescription : COMMENT_STRING+;
subparameter: COMMENT_MUL parameter subsubparameter*;
subsubparameter: COMMENT_MULTIMUL parameter;

//parameter: PASTRING;
parameterName: COMMENT_STRING;
type: COMMENT_STRING (COMMENT_CONTAINS COMMENT_STRING)?;