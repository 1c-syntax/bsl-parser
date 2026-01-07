/*
 * This file is a part of BSL Parser.
 *
 * Copyright (c) 2018-2026
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
package com.github._1c_syntax.bsl.parser;

import com.github._1c_syntax.bsl.parser.testing.TestParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BSLDescriptionParserCheckSourceTest {

  private TestParser<BSLDescriptionParser, BSLDescriptionLexer> testParser;

  @BeforeEach
  void before() {
    testParser = new TestParser<>(BSLDescriptionParser.class, BSLDescriptionLexer.class);
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example1.bsl'")
  void testExample1() {
    testParser.assertThatFile("methodDescription/example1.bsl")
      .containsRule(BSLDescriptionParser.RULE_deprecateBlock, 0)
      .containsRule(BSLDescriptionParser.RULE_deprecateDescription, 0)
      .containsRule(BSLDescriptionParser.RULE_descriptionBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_descriptionString, 15)
      .containsRule(BSLDescriptionParser.RULE_examplesBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_examplesHead, 1)
      .containsRule(BSLDescriptionParser.RULE_examplesString, 30)
      .containsRule(BSLDescriptionParser.RULE_parametersBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_parametersHead, 1)
      .containsRule(BSLDescriptionParser.RULE_parameterString, 16)
      .containsRule(BSLDescriptionParser.RULE_parameter, 9)
      .containsRule(BSLDescriptionParser.RULE_field, 5)
      .containsRule(BSLDescriptionParser.RULE_parameterName, 14)
      .containsRule(BSLDescriptionParser.RULE_returnsValuesBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_returnsValuesString, 12)
      .containsRule(BSLDescriptionParser.RULE_returnsValue, 1)
      .containsRule(BSLDescriptionParser.RULE_typesBlock, 14)
      .containsRule(BSLDescriptionParser.RULE_typeDescription, 21)
      .containsRule(BSLDescriptionParser.RULE_type, 15)
      .containsRule(BSLDescriptionParser.RULE_simpleType, 14)
      .containsRule(BSLDescriptionParser.RULE_listTypes, 0)
      .containsRule(BSLDescriptionParser.RULE_collectionType, 0)
      .containsRule(BSLDescriptionParser.RULE_hyperlinkType, 1)
      .containsRule(BSLDescriptionParser.RULE_splitter, 22)
      .containsRule(BSLDescriptionParser.RULE_hyperlink, 1)
      .containsRule(BSLDescriptionParser.RULE_startPart, 76);
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example2.bsl'")
  void testExample2() {
    testParser.assertThatFile("methodDescription/example2.bsl")
      .containsRule(BSLDescriptionParser.RULE_deprecateBlock, 0)
      .containsRule(BSLDescriptionParser.RULE_deprecateDescription, 0)
      .containsRule(BSLDescriptionParser.RULE_descriptionBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_descriptionString, 2)
      .containsRule(BSLDescriptionParser.RULE_examplesBlock, 0)
      .containsRule(BSLDescriptionParser.RULE_examplesHead, 0)
      .containsRule(BSLDescriptionParser.RULE_examplesString, 0)
      .containsRule(BSLDescriptionParser.RULE_parametersBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_parametersHead, 1)
      .containsRule(BSLDescriptionParser.RULE_parameterString, 2)
      .containsRule(BSLDescriptionParser.RULE_parameter, 1)
      .containsRule(BSLDescriptionParser.RULE_field, 0)
      .containsRule(BSLDescriptionParser.RULE_parameterName, 1)
      .containsRule(BSLDescriptionParser.RULE_returnsValuesBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_returnsValuesString, 2)
      .containsRule(BSLDescriptionParser.RULE_returnsValue, 1)
      .containsRule(BSLDescriptionParser.RULE_typesBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_typeDescription, 3)
      .containsRule(BSLDescriptionParser.RULE_type, 2)
      .containsRule(BSLDescriptionParser.RULE_simpleType, 1)
      .containsRule(BSLDescriptionParser.RULE_listTypes, 0)
      .containsRule(BSLDescriptionParser.RULE_collectionType, 0)
      .containsRule(BSLDescriptionParser.RULE_hyperlinkType, 1)
      .containsRule(BSLDescriptionParser.RULE_splitter, 2)
      .containsRule(BSLDescriptionParser.RULE_hyperlink, 1)
      .containsRule(BSLDescriptionParser.RULE_startPart, 8);
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example3.bsl'")
  void testExample3() {
    testParser.assertThatFile("methodDescription/example3.bsl")
      .containsRule(BSLDescriptionParser.RULE_deprecateBlock, 0)
      .containsRule(BSLDescriptionParser.RULE_deprecateDescription, 0)
      .containsRule(BSLDescriptionParser.RULE_descriptionBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_descriptionString, 13)
      .containsRule(BSLDescriptionParser.RULE_examplesBlock, 0)
      .containsRule(BSLDescriptionParser.RULE_examplesHead, 0)
      .containsRule(BSLDescriptionParser.RULE_examplesString, 0)
      .containsRule(BSLDescriptionParser.RULE_parametersBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_parametersHead, 1)
      .containsRule(BSLDescriptionParser.RULE_parameterString, 7)
      .containsRule(BSLDescriptionParser.RULE_parameter, 5)
      .containsRule(BSLDescriptionParser.RULE_field, 0)
      .containsRule(BSLDescriptionParser.RULE_parameterName, 5)
      .containsRule(BSLDescriptionParser.RULE_returnsValuesBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_returnsValuesString, 2)
      .containsRule(BSLDescriptionParser.RULE_returnsValue, 1)
      .containsRule(BSLDescriptionParser.RULE_typesBlock, 5)
      .containsRule(BSLDescriptionParser.RULE_typeDescription, 9)
      .containsRule(BSLDescriptionParser.RULE_type, 6)
      .containsRule(BSLDescriptionParser.RULE_simpleType, 6)
      .containsRule(BSLDescriptionParser.RULE_listTypes, 0)
      .containsRule(BSLDescriptionParser.RULE_collectionType, 0)
      .containsRule(BSLDescriptionParser.RULE_hyperlinkType, 0)
      .containsRule(BSLDescriptionParser.RULE_splitter, 11)
      .containsRule(BSLDescriptionParser.RULE_hyperlink, 5)
      .containsRule(BSLDescriptionParser.RULE_startPart, 24);
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example4.bsl'")
  void testExample4() {
    testParser.assertThatFile("methodDescription/example4.bsl")
      .containsRule(BSLDescriptionParser.RULE_deprecateBlock, 0)
      .containsRule(BSLDescriptionParser.RULE_deprecateDescription, 0)
      .containsRule(BSLDescriptionParser.RULE_descriptionBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_descriptionString, 3)
      .containsRule(BSLDescriptionParser.RULE_examplesBlock, 0)
      .containsRule(BSLDescriptionParser.RULE_examplesHead, 0)
      .containsRule(BSLDescriptionParser.RULE_examplesString, 0)
      .containsRule(BSLDescriptionParser.RULE_parametersBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_parametersHead, 1)
      .containsRule(BSLDescriptionParser.RULE_parameterString, 4)
      .containsRule(BSLDescriptionParser.RULE_parameter, 3)
      .containsRule(BSLDescriptionParser.RULE_field, 0)
      .containsRule(BSLDescriptionParser.RULE_parameterName, 3)
      .containsRule(BSLDescriptionParser.RULE_returnsValuesBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_returnsValuesString, 2)
      .containsRule(BSLDescriptionParser.RULE_returnsValue, 1)
      .containsRule(BSLDescriptionParser.RULE_typesBlock, 3)
      .containsRule(BSLDescriptionParser.RULE_typeDescription, 6)
      .containsRule(BSLDescriptionParser.RULE_type, 4)
      .containsRule(BSLDescriptionParser.RULE_simpleType, 5)
      .containsRule(BSLDescriptionParser.RULE_listTypes, 1)
      .containsRule(BSLDescriptionParser.RULE_collectionType, 0)
      .containsRule(BSLDescriptionParser.RULE_hyperlinkType, 0)
      .containsRule(BSLDescriptionParser.RULE_splitter, 7)
      .containsRule(BSLDescriptionParser.RULE_hyperlink, 0)
      .containsRule(BSLDescriptionParser.RULE_startPart, 11);
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example5.bsl'")
  void testExample5() {
    testParser.assertThatFile("methodDescription/example5.bsl")
      .containsRule(BSLDescriptionParser.RULE_deprecateBlock, 0)
      .containsRule(BSLDescriptionParser.RULE_deprecateDescription, 0)
      .containsRule(BSLDescriptionParser.RULE_descriptionBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_descriptionString, 14)
      .containsRule(BSLDescriptionParser.RULE_examplesBlock, 0)
      .containsRule(BSLDescriptionParser.RULE_examplesHead, 0)
      .containsRule(BSLDescriptionParser.RULE_examplesString, 0)
      .containsRule(BSLDescriptionParser.RULE_parametersBlock, 0)
      .containsRule(BSLDescriptionParser.RULE_parametersHead, 0)
      .containsRule(BSLDescriptionParser.RULE_parameterString, 0)
      .containsRule(BSLDescriptionParser.RULE_parameter, 0)
      .containsRule(BSLDescriptionParser.RULE_field, 33)
      .containsRule(BSLDescriptionParser.RULE_parameterName, 33)
      .containsRule(BSLDescriptionParser.RULE_returnsValuesBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_returnsValuesString, 316)
      .containsRule(BSLDescriptionParser.RULE_returnsValue, 12)
      .containsRule(BSLDescriptionParser.RULE_typesBlock, 36)
      .containsRule(BSLDescriptionParser.RULE_typeDescription, 305)
      .containsRule(BSLDescriptionParser.RULE_type, 48)
      .containsRule(BSLDescriptionParser.RULE_simpleType, 48)
      .containsRule(BSLDescriptionParser.RULE_listTypes, 0)
      .containsRule(BSLDescriptionParser.RULE_collectionType, 0)
      .containsRule(BSLDescriptionParser.RULE_hyperlinkType, 0)
      .containsRule(BSLDescriptionParser.RULE_splitter, 73)
      .containsRule(BSLDescriptionParser.RULE_hyperlink, 8)
      .containsRule(BSLDescriptionParser.RULE_startPart, 331);
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example6.bsl'")
  void testExample6() {
    testParser.assertThatFile("methodDescription/example6.bsl")
      .containsRule(BSLDescriptionParser.RULE_deprecateBlock, 0)
      .containsRule(BSLDescriptionParser.RULE_deprecateDescription, 0)
      .containsRule(BSLDescriptionParser.RULE_descriptionBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_descriptionString, 10)
      .containsRule(BSLDescriptionParser.RULE_examplesBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_examplesHead, 1)
      .containsRule(BSLDescriptionParser.RULE_examplesString, 7)
      .containsRule(BSLDescriptionParser.RULE_parametersBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_parametersHead, 1)
      .containsRule(BSLDescriptionParser.RULE_parameterString, 39)
      .containsRule(BSLDescriptionParser.RULE_parameter, 4)
      .containsRule(BSLDescriptionParser.RULE_field, 0)
      .containsRule(BSLDescriptionParser.RULE_parameterName, 4)
      .containsRule(BSLDescriptionParser.RULE_returnsValuesBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_returnsValuesString, 2)
      .containsRule(BSLDescriptionParser.RULE_returnsValue, 1)
      .containsRule(BSLDescriptionParser.RULE_typesBlock, 28)
      .containsRule(BSLDescriptionParser.RULE_typeDescription, 21)
      .containsRule(BSLDescriptionParser.RULE_type, 29)
      .containsRule(BSLDescriptionParser.RULE_simpleType, 29)
      .containsRule(BSLDescriptionParser.RULE_listTypes, 0)
      .containsRule(BSLDescriptionParser.RULE_collectionType, 0)
      .containsRule(BSLDescriptionParser.RULE_hyperlinkType, 0)
      .containsRule(BSLDescriptionParser.RULE_splitter, 37)
      .containsRule(BSLDescriptionParser.RULE_hyperlink, 1)
      .containsRule(BSLDescriptionParser.RULE_startPart, 61);
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example9.bsl'")
  void testExample9() {
    testParser.assertThatFile("methodDescription/example9.bsl")
      .containsRule(BSLDescriptionParser.RULE_deprecateBlock, 0)
      .containsRule(BSLDescriptionParser.RULE_deprecateDescription, 0)
      .containsRule(BSLDescriptionParser.RULE_descriptionBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_descriptionString, 6)
      .containsRule(BSLDescriptionParser.RULE_examplesBlock, 0)
      .containsRule(BSLDescriptionParser.RULE_examplesHead, 0)
      .containsRule(BSLDescriptionParser.RULE_examplesString, 0)
      .containsRule(BSLDescriptionParser.RULE_parametersBlock, 0)
      .containsRule(BSLDescriptionParser.RULE_parametersHead, 0)
      .containsRule(BSLDescriptionParser.RULE_parameterString, 0)
      .containsRule(BSLDescriptionParser.RULE_parameter, 0)
      .containsRule(BSLDescriptionParser.RULE_field, 23)
      .containsRule(BSLDescriptionParser.RULE_parameterName, 23)
      .containsRule(BSLDescriptionParser.RULE_returnsValuesBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_returnsValuesString, 43)
      .containsRule(BSLDescriptionParser.RULE_returnsValue, 2)
      .containsRule(BSLDescriptionParser.RULE_typesBlock, 25)
      .containsRule(BSLDescriptionParser.RULE_typeDescription, 43)
      .containsRule(BSLDescriptionParser.RULE_type, 29)
      .containsRule(BSLDescriptionParser.RULE_simpleType, 32)
      .containsRule(BSLDescriptionParser.RULE_listTypes, 7)
      .containsRule(BSLDescriptionParser.RULE_collectionType, 2)
      .containsRule(BSLDescriptionParser.RULE_hyperlinkType, 2)
      .containsRule(BSLDescriptionParser.RULE_splitter, 52)
      .containsRule(BSLDescriptionParser.RULE_hyperlink, 8)
      .containsRule(BSLDescriptionParser.RULE_startPart, 50);
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example10.bsl'")
  void testExample10() {
    testParser.assertThatFile("methodDescription/example10.bsl")
      .containsRule(BSLDescriptionParser.RULE_deprecateBlock, 0)
      .containsRule(BSLDescriptionParser.RULE_deprecateDescription, 0)
      .containsRule(BSLDescriptionParser.RULE_descriptionBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_descriptionString, 5)
      .containsRule(BSLDescriptionParser.RULE_examplesBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_examplesHead, 1)
      .containsRule(BSLDescriptionParser.RULE_examplesString, 11)
      .containsRule(BSLDescriptionParser.RULE_parametersBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_parametersHead, 1)
      .containsRule(BSLDescriptionParser.RULE_parameterString, 13)
      .containsRule(BSLDescriptionParser.RULE_parameter, 2)
      .containsRule(BSLDescriptionParser.RULE_field, 2)
      .containsRule(BSLDescriptionParser.RULE_parameterName, 4)
      .containsRule(BSLDescriptionParser.RULE_returnsValuesBlock, 1)
      .containsRule(BSLDescriptionParser.RULE_returnsValuesString, 2)
      .containsRule(BSLDescriptionParser.RULE_returnsValue, 1)
      .containsRule(BSLDescriptionParser.RULE_typesBlock, 4)
      .containsRule(BSLDescriptionParser.RULE_typeDescription, 15)
      .containsRule(BSLDescriptionParser.RULE_type, 5)
      .containsRule(BSLDescriptionParser.RULE_simpleType, 6)
      .containsRule(BSLDescriptionParser.RULE_listTypes, 1)
      .containsRule(BSLDescriptionParser.RULE_collectionType, 0)
      .containsRule(BSLDescriptionParser.RULE_hyperlinkType, 0)
      .containsRule(BSLDescriptionParser.RULE_splitter, 9)
      .containsRule(BSLDescriptionParser.RULE_hyperlink, 0)
      .containsRule(BSLDescriptionParser.RULE_startPart, 34);
  }
}
