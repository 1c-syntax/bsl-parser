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

class BSLMethodDescriptionParserCheckSourceTest {

  private TestParser<BSLMethodDescriptionParser, BSLMethodDescriptionLexer> testParser;

  @BeforeEach
  void before() {
    testParser = new TestParser<>(BSLMethodDescriptionParser.class, BSLMethodDescriptionLexer.class);
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example1.bsl'")
  void testExample1() {
    testParser.assertThatFile("methodDescription/example1.bsl")
      .containsRule(BSLMethodDescriptionParser.RULE_deprecate, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_deprecateDescription, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_descriptionBlock, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_description, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_descriptionString, 15)
      .containsRule(BSLMethodDescriptionParser.RULE_examples, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_examplesString, 30)
      .containsRule(BSLMethodDescriptionParser.RULE_callOptions, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_callOptionsString, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_parameters, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_parameterString, 16)
      .containsRule(BSLMethodDescriptionParser.RULE_parameter, 9)
      .containsRule(BSLMethodDescriptionParser.RULE_subParameter, 5)
      .containsRule(BSLMethodDescriptionParser.RULE_parameterName, 14)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValues, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValuesString, 12)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValue, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_typesBlock, 14)
      .containsRule(BSLMethodDescriptionParser.RULE_typeDescription, 16)
      .containsRule(BSLMethodDescriptionParser.RULE_type, 15)
      .containsRule(BSLMethodDescriptionParser.RULE_simpleType, 14)
      .containsRule(BSLMethodDescriptionParser.RULE_listTypes, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_complexType, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_hyperlinkType, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_spitter, 22)
      .containsRule(BSLMethodDescriptionParser.RULE_hyperlinkBlock, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_startPart, 76);
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example2.bsl'")
  void testExample2() {
    testParser.assertThatFile("methodDescription/example2.bsl")
      .containsRule(BSLMethodDescriptionParser.RULE_deprecate, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_deprecateDescription, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_descriptionBlock, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_description, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_descriptionString, 2)
      .containsRule(BSLMethodDescriptionParser.RULE_examples, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_examplesString, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_callOptions, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_callOptionsString, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_parameters, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_parameterString, 2)
      .containsRule(BSLMethodDescriptionParser.RULE_parameter, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_subParameter, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_parameterName, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValues, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValuesString, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValue, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_typesBlock, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_typeDescription, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_type, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_simpleType, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_listTypes, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_complexType, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_hyperlinkType, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_spitter, 2)
      .containsRule(BSLMethodDescriptionParser.RULE_hyperlinkBlock, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_startPart, 8);
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example3.bsl'")
  void testExample3() {
    testParser.assertThatFile("methodDescription/example3.bsl")
      .containsRule(BSLMethodDescriptionParser.RULE_deprecate, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_deprecateDescription, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_descriptionBlock, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_description, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_descriptionString, 13)
      .containsRule(BSLMethodDescriptionParser.RULE_examples, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_examplesString, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_callOptions, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_callOptionsString, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_parameters, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_parameterString, 7)
      .containsRule(BSLMethodDescriptionParser.RULE_parameter, 5)
      .containsRule(BSLMethodDescriptionParser.RULE_subParameter, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_parameterName, 5)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValues, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValuesString, 2)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValue, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_typesBlock, 5)
      .containsRule(BSLMethodDescriptionParser.RULE_typeDescription, 7)
      .containsRule(BSLMethodDescriptionParser.RULE_type, 6)
      .containsRule(BSLMethodDescriptionParser.RULE_simpleType, 6)
      .containsRule(BSLMethodDescriptionParser.RULE_listTypes, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_complexType, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_hyperlinkType, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_spitter, 11)
      .containsRule(BSLMethodDescriptionParser.RULE_hyperlinkBlock, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_startPart, 24);
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example4.bsl'")
  void testExample4() {
    testParser.assertThatFile("methodDescription/example4.bsl")
      .containsRule(BSLMethodDescriptionParser.RULE_deprecate, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_deprecateDescription, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_descriptionBlock, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_description, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_descriptionString, 3)
      .containsRule(BSLMethodDescriptionParser.RULE_examples, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_examplesString, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_callOptions, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_callOptionsString, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_parameters, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_parameterString, 4)
      .containsRule(BSLMethodDescriptionParser.RULE_parameter, 3)
      .containsRule(BSLMethodDescriptionParser.RULE_subParameter, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_parameterName, 3)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValues, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValuesString, 2)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValue, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_typesBlock, 3)
      .containsRule(BSLMethodDescriptionParser.RULE_typeDescription, 4)
      .containsRule(BSLMethodDescriptionParser.RULE_type, 4)
      .containsRule(BSLMethodDescriptionParser.RULE_simpleType, 5)
      .containsRule(BSLMethodDescriptionParser.RULE_listTypes, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_complexType, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_hyperlinkType, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_spitter, 7)
      .containsRule(BSLMethodDescriptionParser.RULE_hyperlinkBlock, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_startPart, 11);
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example5.bsl'")
  void testExample5() {
    testParser.assertThatFile("methodDescription/example5.bsl")
      .containsRule(BSLMethodDescriptionParser.RULE_deprecate, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_deprecateDescription, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_descriptionBlock, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_description, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_descriptionString, 14)
      .containsRule(BSLMethodDescriptionParser.RULE_examples, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_examplesString, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_callOptions, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_callOptionsString, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_parameters, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_parameterString, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_parameter, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_subParameter, 33)
      .containsRule(BSLMethodDescriptionParser.RULE_parameterName, 33)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValues, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValuesString, 316)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValue, 12)
      .containsRule(BSLMethodDescriptionParser.RULE_typesBlock, 36)
      .containsRule(BSLMethodDescriptionParser.RULE_typeDescription, 266)
      .containsRule(BSLMethodDescriptionParser.RULE_type, 48)
      .containsRule(BSLMethodDescriptionParser.RULE_simpleType, 48)
      .containsRule(BSLMethodDescriptionParser.RULE_listTypes, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_complexType, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_hyperlinkType, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_spitter, 73)
      .containsRule(BSLMethodDescriptionParser.RULE_hyperlinkBlock, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_startPart, 331);
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example6.bsl'")
  void testExample6() {
    testParser.assertThatFile("methodDescription/example6.bsl")
      .containsRule(BSLMethodDescriptionParser.RULE_deprecate, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_deprecateDescription, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_descriptionBlock, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_description, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_descriptionString, 10)
      .containsRule(BSLMethodDescriptionParser.RULE_examples, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_examplesString, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_callOptions, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_callOptionsString, 7)
      .containsRule(BSLMethodDescriptionParser.RULE_parameters, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_parameterString, 39)
      .containsRule(BSLMethodDescriptionParser.RULE_parameter, 4)
      .containsRule(BSLMethodDescriptionParser.RULE_subParameter, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_parameterName, 4)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValues, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValuesString, 2)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValue, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_typesBlock, 28)
      .containsRule(BSLMethodDescriptionParser.RULE_typeDescription, 16)
      .containsRule(BSLMethodDescriptionParser.RULE_type, 29)
      .containsRule(BSLMethodDescriptionParser.RULE_simpleType, 29)
      .containsRule(BSLMethodDescriptionParser.RULE_listTypes, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_complexType, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_hyperlinkType, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_spitter, 37)
      .containsRule(BSLMethodDescriptionParser.RULE_hyperlinkBlock, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_startPart, 61);
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example9.bsl'")
  void testExample9() {
    testParser.assertThatFile("methodDescription/example9.bsl")
      .containsRule(BSLMethodDescriptionParser.RULE_deprecate, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_deprecateDescription, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_descriptionBlock, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_description, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_descriptionString, 6)
      .containsRule(BSLMethodDescriptionParser.RULE_examples, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_examplesString, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_callOptions, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_callOptionsString, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_parameters, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_parameterString, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_parameter, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_subParameter, 23)
      .containsRule(BSLMethodDescriptionParser.RULE_parameterName, 23)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValues, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValuesString, 43)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValue, 2)
      .containsRule(BSLMethodDescriptionParser.RULE_typesBlock, 25)
      .containsRule(BSLMethodDescriptionParser.RULE_typeDescription, 42)
      .containsRule(BSLMethodDescriptionParser.RULE_type, 29) // на самом деле меньше todo исправить
      .containsRule(BSLMethodDescriptionParser.RULE_simpleType, 32)
      .containsRule(BSLMethodDescriptionParser.RULE_listTypes, 7)
      .containsRule(BSLMethodDescriptionParser.RULE_complexType, 2)
      .containsRule(BSLMethodDescriptionParser.RULE_hyperlinkType, 2)
      .containsRule(BSLMethodDescriptionParser.RULE_spitter, 52)
      .containsRule(BSLMethodDescriptionParser.RULE_hyperlinkBlock, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_startPart, 50);
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example10.bsl'")
  void testExample10() {
    testParser.assertThatFile("methodDescription/example10.bsl")
      .containsRule(BSLMethodDescriptionParser.RULE_deprecate, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_deprecateDescription, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_descriptionBlock, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_description, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_descriptionString, 5)
      .containsRule(BSLMethodDescriptionParser.RULE_examples, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_examplesString, 11)
      .containsRule(BSLMethodDescriptionParser.RULE_callOptions, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_callOptionsString, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_parameters, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_parameterString, 13)
      .containsRule(BSLMethodDescriptionParser.RULE_parameter, 2)
      .containsRule(BSLMethodDescriptionParser.RULE_subParameter, 2)
      .containsRule(BSLMethodDescriptionParser.RULE_parameterName, 4)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValues, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValuesString, 2)
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValue, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_typesBlock, 4)
      .containsRule(BSLMethodDescriptionParser.RULE_typeDescription, 12)
      .containsRule(BSLMethodDescriptionParser.RULE_type, 5)
      .containsRule(BSLMethodDescriptionParser.RULE_simpleType, 6)
      .containsRule(BSLMethodDescriptionParser.RULE_listTypes, 1)
      .containsRule(BSLMethodDescriptionParser.RULE_complexType, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_hyperlinkType, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_spitter, 9)
      .containsRule(BSLMethodDescriptionParser.RULE_hyperlinkBlock, 0)
      .containsRule(BSLMethodDescriptionParser.RULE_startPart, 34);
  }
}
