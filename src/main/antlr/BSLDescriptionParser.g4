/**
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018-2026
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
parser grammar BSLDescriptionParser;

options {
    tokenVocab = BSLDescriptionLexer;
}

// структура описания
methodDescription:
    (
          (deprecateBlock? descriptionBlock? parametersBlock? returnsValuesBlock? callOptionsBlock? examplesBlock?)
        | (deprecateBlock? descriptionBlock? parametersBlock? returnsValuesBlock? examplesBlock? callOptionsBlock?)
        | (descriptionBlock? parametersBlock? returnsValuesBlock? callOptionsBlock? examplesBlock? deprecateBlock?)
        | (descriptionBlock? parametersBlock? returnsValuesBlock? examplesBlock? callOptionsBlock? deprecateBlock?)
    )
    skipBlock?
    EOF;

// deprecate
deprecateBlock: startPart DEPRECATE_KEYWORD (deprecateDescription | EOL);
deprecateDescription: (hyperlink | ~(EOL | EOF))+ EOL;

// description
descriptionBlock: descriptionString+;
descriptionString:
      (startPart
            // гиперссылка или не ключевое (ну и не конец строки)
            (hyperlink | ~(PARAMETERS_KEYWORD | CALL_OPTIONS_KEYWORD | RETURNS_KEYWORD | EXAMPLE_KEYWORD | DEPRECATE_KEYWORD | EOL | EOF | SPACE))
            (hyperlink | ~(EOL | EOF))* // любой
        EOL)
    | (startPart EOL)
    ;

// examples
examplesBlock: examplesHead examplesStrings=examplesString*;
examplesHead: startPart EXAMPLE_KEYWORD SPACE? EOL;
examplesString:
      (startPart
            // гиперссылка или не ключевое (ну и не конец строки)
            (hyperlink | ~(DEPRECATE_KEYWORD | CALL_OPTIONS_KEYWORD | EOL | EOF | SPACE))
            (hyperlink | ~(EOL | EOF))* // любой
        EOL)
    | (startPart EOL)
    ;

// callOptions
callOptionsBlock: callOptionsHead callOptionsStrings=callOptionsString*;
callOptionsHead: startPart CALL_OPTIONS_KEYWORD SPACE? EOL;
callOptionsString:
      (startPart
            // гиперссылка или не ключевое (ну и не конец строки)
            (hyperlink | ~(DEPRECATE_KEYWORD | EXAMPLE_KEYWORD | EOL | EOF | SPACE))
            (hyperlink | ~(EOL | EOF))* // любой
        EOL)
    | (startPart EOL)
    ;

// parameters
parametersBlock: parametersHead parameterStrings=parameterString*;
parametersHead: startPart PARAMETERS_KEYWORD SPACE? EOL;
parameterString:
      (startPart parameter)
    | (startPart field)
    | (startPart typesBlock)
    | (startPart typeDescription)
    ;

parameter: parameterName typesBlock;
field: level=STAR SPACE? parameterName typesBlock;
parameterName: WORD;

// returnsValues
returnsValuesBlock: returnsValuesHead returnsValuesStrings=returnsValuesString*;
returnsValuesHead: startPart RETURNS_KEYWORD SPACE? EOL;
returnsValuesString:
    (startPart returnsValue)
    | (startPart typesBlock)
    | (startPart field)
    | (startPart typeDescription)
    ;

// skipBlock
skipBlock: startPart EDT_SKIP_KEYWORD (SPACE skipKey? (SPACE ~(EOL | EOF)*)?) EOL;
skipKey: ~(EOL | EOF | SPACE)+;

returnsValue: type
    (
        (splitter typeDescription)
        | (splitter EOL)
        | EOL
    )
    ;

typeDescription:
    (
        (hyperlink | first=~(RETURNS_KEYWORD | EXAMPLE_KEYWORD | CALL_OPTIONS_KEYWORD | DEPRECATE_KEYWORD
                            | EDT_SKIP_KEYWORD | EOL | EOF | SPACE))
        (hyperlink | second=~(EOL | EOF))*
        EOL
    )
    | (SPACE* EOL)
    ;

typesBlock: splitter type
    (
        (splitter typeDescription)
        | (splitter EOL)
        | (SPACE* EOL)
    )
    ;

type: listTypes | collectionType | hyperlinkType | simpleType;
simpleType: typeName=(WORD | DOTSWORD) colon=COLON?;
collectionType: collection=(WORD | DOTSWORD) SPACE OF_KEYWORD SPACE value=type;
hyperlinkType: hyperlink;
listTypes: listType (COMMA SPACE? listType?)+;
listType: simpleType | collectionType | hyperlinkType;

hyperlink: SEE_KEYWORD SPACE link=(WORD | DOTSWORD) (LPAREN linkParams=~EOL* RPAREN)?;
splitter: SPACE? DASH SPACE?;
startPart: SPACE? COMMENT SPACE?;
