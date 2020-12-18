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
/**
 * @author Maximov Valery <maximovvalery@gmail.com>
 */
parser grammar BSLMethodDescriptionParser;

options {
    tokenVocab = BSLMethodDescriptionLexer;
    contextSuperClass = 'BSLParserRuleContext';
}

// структура описания
methodDescription:
    (
          (deprecate? descriptionBlock? parameters? callOptions? returnsValues? examples?)
        | (deprecate? descriptionBlock? parameters? examples? returnsValues? callOptions?)
        | (deprecate? descriptionBlock? parameters? callOptions? examples? returnsValues?)
        | (deprecate? descriptionBlock? parameters? examples? callOptions? returnsValues?)
    ) EOF;

// deprecate
deprecate: SPACE? DEPRECATE_KEYWORD (SPACE deprecateDescription)? EOL?;
deprecateDescription: ~(SPACE | EOL) ~EOL*;

// description
descriptionBlock: hyperlinkBlock | description;
description: descriptionString+;
descriptionString:
      (SPACE? ~(PARAMETERS_KEYWORD | RETURNS_KEYWORD | EXAMPLE_KEYWORD | CALL_OPTIONS_KEYWORD | EOL | SPACE) ~EOL* EOL?)
    | (SPACE EOL)
    | EOL
    ;

// examples
examples: SPACE? EXAMPLE_KEYWORD (EOL examplesString*)?;
examplesString:
      (SPACE? ~(CALL_OPTIONS_KEYWORD | RETURNS_KEYWORD | EOL | SPACE) ~EOL* EOL?)
    | (SPACE EOL?)
    | EOL
    ;

// callOptions
callOptions: SPACE? CALL_OPTIONS_KEYWORD (EOL callOptionsString*)?;
callOptionsString:
      (SPACE? ~(RETURNS_KEYWORD | EXAMPLE_KEYWORD | EOL | SPACE) ~EOL* EOL?)
    | (SPACE EOL?)
    | EOL
    ;

// parameters
parameters: SPACE? PARAMETERS_KEYWORD (EOL (hyperlinkBlock | parameterString+)?)?;
parameterString:
      parameter
    | subParameter
    | typeDescription
    | (SPACE EOL)
    | EOL
    ;
parameter: SPACE? parameterName typesBlock;
subParameter: SPACE? STAR SPACE? parameterName typesBlock;
parameterName: WORD;

// returnsValues
returnsValues: SPACE? RETURNS_KEYWORD (EOL (hyperlinkBlock | returnsValuesString+)?)?;
returnsValuesString:
    returnsValue
    | typesBlock
    | subParameter
    | typeDescription
    | (SPACE EOL)
    | EOL
;

returnsValue: SPACE? type ((spitter typeDescription?) | EOL);

typesBlock: spitter type ((spitter typeDescription?) | EOL);

typeDescription:
    SPACE? ~(RETURNS_KEYWORD | EXAMPLE_KEYWORD | CALL_OPTIONS_KEYWORD | EOL | SPACE | STAR) ~EOL* EOL;

type:
    hyperlinkType
    | listTypes
    | (simpleType COLON?)
    | complexType
    ;
simpleType: (WORD | DOTSWORD);
listTypes: simpleType (COMMA SPACE? simpleType)+;
complexType: COMPLEX_TYPE;
hyperlinkType: HYPERLINK;

spitter: SPACE? DASH SPACE?;

hyperlinkBlock: EOL* SPACE? HYPERLINK SPACE? EOL*;
