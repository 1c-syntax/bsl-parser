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
        | (descriptionBlock? parameters? callOptions? returnsValues? examples? deprecate?)
        | (descriptionBlock? parameters? examples? returnsValues? callOptions? deprecate?)
        | (descriptionBlock? parameters? callOptions? examples? returnsValues? deprecate?)
        | (descriptionBlock? parameters? examples? callOptions? returnsValues? deprecate?)
    ) EOF;

// deprecate
deprecate: startPart DEPRECATE_KEYWORD (SPACE deprecateDescription)? EOL?;
deprecateDescription: ~(SPACE | EOL) ~EOL*;

// description
descriptionBlock: (hyperlinkBlock | description) EOL?;
description: descriptionString+;
descriptionString:
      (startPart ~(PARAMETERS_KEYWORD | RETURNS_KEYWORD | EXAMPLE_KEYWORD | CALL_OPTIONS_KEYWORD | DEPRECATE_KEYWORD | EOL | SPACE) ~EOL* EOL?)
    | (startPart EOL)
    ;

// examples
examples: startPart EXAMPLE_KEYWORD (EOL examplesString*)?;
examplesString:
      (startPart ~(CALL_OPTIONS_KEYWORD | RETURNS_KEYWORD | EOL | SPACE) ~EOL* EOL?)
    | (startPart EOL?)
    ;

// callOptions
callOptions: startPart CALL_OPTIONS_KEYWORD (EOL callOptionsString*)?;
callOptionsString:
      (startPart ~(RETURNS_KEYWORD | EXAMPLE_KEYWORD | EOL | SPACE) ~EOL* EOL?)
    | (startPart EOL?)
    ;

// parameters
parameters: startPart PARAMETERS_KEYWORD SPACE? (EOL (hyperlinkBlock | parameterString+)?)? EOL?;
parameterString:
      parameter
    | (startPart typesBlock)
    | subParameter
    | (startPart typeDescription)
    | (startPart EOL?)
    ;
parameter: startPart parameterName typesBlock;
subParameter: startPart STAR SPACE? parameterName typesBlock;
parameterName: WORD;

// returnsValues
returnsValues: startPart RETURNS_KEYWORD SPACE? (EOL (hyperlinkBlock | returnsValuesString+)?)? EOL?;
returnsValuesString:
    returnsValue
    | (startPart typesBlock)
    | subParameter
    | (startPart typeDescription)
    | (startPart EOL?)
;

returnsValue: startPart type ((spitter typeDescription?) | EOL);

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
listTypes: (simpleType | complexType | hyperlinkType) (COMMA SPACE? (simpleType | complexType | hyperlinkType))+;

complexType:
    collection=(WORD | DOTSWORD) SPACE OF_KEYWORD SPACE type;
hyperlinkType:
    SEE_KEYWORD SPACE link=(WORD | DOTSWORD) (LPAREN linkParams=~(EOL)* RPAREN)?;

spitter: SPACE? DASH SPACE?;

hyperlinkBlock: (startPart EOL)* startPart hyperlinkType SPACE? (EOL startPart)*;
startPart: SPACE? COMMENT SPACE?;