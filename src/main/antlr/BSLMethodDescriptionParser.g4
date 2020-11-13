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
          (deprecate? description? parameters? callOptions? returnsValues? examples?)
        | (deprecate? description? parameters? examples? returnsValues? callOptions?)
        | (deprecate? description? parameters? callOptions? examples? returnsValues?)
        | (deprecate? description? parameters? callOptions? examples? returnsValues?)
        | (deprecate? description? parameters? examples? callOptions? returnsValues?)
    ) EOF;

deprecate: SPACE* DEPRECATE_KEYWORD deprecateDescription? EOL?;
deprecateDescription: ~EOL+;

description: EOL* descriptionString+;
descriptionString: ~(PARAMETERS_KEYWORD | RETURNS_KEYWORD | EXAMPLE_KEYWORD | CALL_OPTIONS_KEYWORD | EOL)+ EOL*;

parameters: SPACE* PARAMETERS_KEYWORD (EOL parametersString*)?;
parametersString:
    parameterString
    | subParameterString
    | typeWithDescription
    | (~(RETURNS_KEYWORD | EXAMPLE_KEYWORD | CALL_OPTIONS_KEYWORD | EOL)+ EOL*)
;
parameterString: SPACE* parameterName typeWithDescription;
subParameterString: SPACE* starPreffix SPACE* parameterName typeWithDescription;
parameterName: WORD;

callOptions: SPACE* CALL_OPTIONS_KEYWORD (EOL callOptionsString*)?;
callOptionsString: ~(RETURNS_KEYWORD | EXAMPLE_KEYWORD | EOL)+ EOL*;

returnsValues: SPACE* RETURNS_KEYWORD (EOL returnsValuesString*)?;
returnsValuesString:
    subParameterString
    | typeWithDescription
    | returnsValueString
    | (~(EXAMPLE_KEYWORD | CALL_OPTIONS_KEYWORD | EOL)+ EOL*)
    ;
returnsValueString: SPACE* types typeDescriptionString EOL*;

examples: SPACE* EXAMPLE_KEYWORD (EOL examplesString*)?;
examplesString: (~(RETURNS_KEYWORD | CALL_OPTIONS_KEYWORD | EOL)+ EOL*);

typeWithDescription: spitter types typeDescriptionString? EOL*;
typeDescriptionString:
    (spitter typeDescription)
    | typeDescription
    | spitter
    ;
typeDescription: ~EOL+ EOL*;

spitter: SPACE* DASH SPACE*;
starPreffix: STAR+;
types:
    HYPERLINK
    | COMPLEX_TYPE
    | ((WORD | DOTSWORD) COLON)
    | ((WORD | DOTSWORD) (COMMA SPACE* (WORD | DOTSWORD))*)
    ;