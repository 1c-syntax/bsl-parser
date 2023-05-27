/*
 * This file is a part of BSL Parser.
 *
 * Copyright (c) 2018-2023
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

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.TestUtils;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class BSLMethodDescriptionParserTest
  extends AbstractParserTest<BSLMethodDescriptionParser, BSLMethodDescriptionLexer> {

  protected BSLMethodDescriptionParserTest() {
    super(BSLMethodDescriptionParser.class, BSLMethodDescriptionLexer.class);
  }

  @Test
  void testMethodDescription() {
    setInput("");
    assertMatches(parser.methodDescription());

    setInput("//Устарела но не\n//\n//\t//34567890-dfghjkl_)(*&^%$#@ Совсем \n//" +
      "//||Описание метода\n// \n//" +
      "//Параметры//"
    );
    assertMatches(parser.methodDescription());

    setInput("//Устарела.");
    assertMatches(parser.methodDescription());

    setInput("//Параметры:");
    assertMatches(parser.methodDescription());

    setInput("//Варианты вызова:");
    assertMatches(parser.methodDescription());

    setInput("//Пример:");
    assertMatches(parser.methodDescription());

    setInput("//Возвращаемое значение:");
    assertMatches(parser.methodDescription());
  }

  @Test
  void testDeprecate() {
    setInput("");
    assertNotMatches(parser.deprecate());
    setInput("//Устарела");
    assertMatches(parser.deprecate());
    setInput("//Описание\nУстарела");
    assertNotMatches(parser.deprecate());
    setInput("//Описание\nУстарела.");
    assertNotMatches(parser.deprecate());

    setInput("//Устарела.");
    assertMatches(parser.deprecate());
    assertNotMatches(parser.deprecateDescription());
    setInput("//Устарела.");
    assertMatches(parser.deprecate());
    assertNotMatches(parser.deprecateDescription());
    setInput("//Устарела.\n");
    assertMatches(parser.deprecate());
    assertNotMatches(parser.deprecateDescription());
    setInput("//Устарела.\n Использовать другой метод");
    assertMatches(parser.deprecate());
    var nodes = getNodes("//Устарела.\n Использовать другой метод",
      BSLMethodDescriptionParser.RULE_deprecateDescription);
    assertThat(nodes).isEmpty();

    setInput("//Устарела. Использовать другой метод");
    assertMatches(parser.deprecate());
    nodes = getNodes("//Устарела. Использовать другой метод",
      BSLMethodDescriptionParser.RULE_deprecateDescription);
    assertThat(nodes).hasSize(1);
    assertThat(nodes.get(0).getText()).isEqualTo("Использовать другой метод");
    nodes = getNodes("//Устарела. Использовать другой метод",
      BSLMethodDescriptionParser.RULE_deprecateDescription);
    assertThat(nodes).hasSize(1);
    assertThat(nodes.get(0).getText()).isEqualTo("Использовать другой метод");
    nodes = getNodes("//Устарела.Использовать другой метод",
      BSLMethodDescriptionParser.RULE_description);
    assertThat(nodes).hasSize(1);
    assertThat(nodes.get(0).getText()).isEqualTo("//Устарела.Использовать другой метод\n");
  }

  @Test
  void testDescription() {
    var nodes = getNodes("//Устарела.",
      BSLMethodDescriptionParser.RULE_description);
    assertThat(nodes).isEmpty();

    nodes = getNodes("//Устарела.\n//Описание\n//\n//ногостчрочное",
      BSLMethodDescriptionParser.RULE_description);
    assertThat(nodes).hasSize(1);
    nodes = getNodes("//Устарела.\n//Параметры:",
      BSLMethodDescriptionParser.RULE_description);
    assertThat(nodes).isEmpty();
    nodes = getNodes("//Устарела.\n//Call options:",
      BSLMethodDescriptionParser.RULE_description);
    assertThat(nodes).isEmpty();
    nodes = getNodes("//returns:",
      BSLMethodDescriptionParser.RULE_description);
    assertThat(nodes).isEmpty();
    nodes = getNodes("//example:",
      BSLMethodDescriptionParser.RULE_description);
    assertThat(nodes).isEmpty();
    nodes = getNodes("//Описание \n// многострочное:",
      BSLMethodDescriptionParser.RULE_descriptionString);
    assertThat(nodes).hasSize(2);
  }

  @Test
  void testParameters() {
    var nodes = getNodes("//Параметры",
      BSLMethodDescriptionParser.RULE_parameters);
    assertThat(nodes).isEmpty();
    nodes = getNodes("//Параметры:\n//Параметр1\n//\n//Параметр2\n",
      BSLMethodDescriptionParser.RULE_parameters);
    assertThat(nodes).hasSize(1);
    nodes = getNodes("//Параметры:\n//Параметр1\n//\n//Параметр2\n",
      BSLMethodDescriptionParser.RULE_parameterString);
    assertThat(nodes).hasSize(3);
    nodes = getNodes("//Параметры:\n//Параметр1\n//\n//Параметр2\n//Пример:",
      BSLMethodDescriptionParser.RULE_parameterString);
    assertThat(nodes).hasSize(3);
    nodes = getNodes("//Параметры:\n//Параметр1\n//\n//Параметр2\n//Варианты вызова:",
      BSLMethodDescriptionParser.RULE_parameterString);
    assertThat(nodes).hasSize(3);
    nodes = getNodes("//Параметры:\n//Параметр1\n//\n//Параметр2\n//Возвращаемое значение:",
      BSLMethodDescriptionParser.RULE_parameterString);
    assertThat(nodes).hasSize(3);
    nodes = getNodes("//Параметры:\n//Параметр1 - Тип  Описание\n//\n//Параметр2\n//Возвращаемое значение:",
      BSLMethodDescriptionParser.RULE_parameterString);
    assertThat(nodes).hasSize(3);
  }

  @Test
  void testCallOptions() {
    var nodes = getNodes("//Варианты вызова",
      BSLMethodDescriptionParser.RULE_callOptions);
    assertThat(nodes).isEmpty();
    nodes = getNodes("//Call options:\n//Var 1\n//\n//Var 2 bla ba()",
      BSLMethodDescriptionParser.RULE_callOptions);
    assertThat(nodes).hasSize(1);
    nodes = getNodes("//Call options:\n//Var 1\n//\n//Var 2 bla ba()",
      BSLMethodDescriptionParser.RULE_callOptionsString);
    assertThat(nodes).hasSize(3);
    nodes = getNodes("//Call options:\n//Var 1\n//\n//Var 2 bla ba()\n//Example:",
      BSLMethodDescriptionParser.RULE_callOptionsString);
    assertThat(nodes).hasSize(3);
    nodes = getNodes("//Call options:\n//Var 1\n//\n//Var 2 bla ba()\n//Возвращаемое значение:",
      BSLMethodDescriptionParser.RULE_callOptionsString);
    assertThat(nodes).hasSize(3);
  }

  @Test
  void testReturns() {
    var nodes = getNodes("//returns",
      BSLMethodDescriptionParser.RULE_returnsValues);
    assertThat(nodes).isEmpty();
    nodes = getNodes("//returns:\n//boolean - description\n",
      BSLMethodDescriptionParser.RULE_returnsValues);
    assertThat(nodes).hasSize(1);
    nodes = getNodes("//returns:\n//boolean - description\n//Example:",
      BSLMethodDescriptionParser.RULE_returnsValuesString);
    assertThat(nodes).hasSize(1);
    nodes = getNodes("//returns:\n// - ref - description\n// - boolean - description\n//Example:",
      BSLMethodDescriptionParser.RULE_returnsValuesString);
    assertThat(nodes).hasSize(2);
  }

  @Test
  void testExample() {
    var nodes = getNodes("//Пример",
      BSLMethodDescriptionParser.RULE_examples);
    assertThat(nodes).isEmpty();
    nodes = getNodes("//Пример:\n//Пример - описаниепримера",
      BSLMethodDescriptionParser.RULE_examples);
    assertThat(nodes).hasSize(1);
    nodes = getNodes("//Пример:\n//Пример: - описаниепримера",
      BSLMethodDescriptionParser.RULE_examplesString);
    assertThat(nodes).hasSize(1);
    nodes = getNodes("//Пример:\n//Пример:\n//Пример: - описаниепримера",
      BSLMethodDescriptionParser.RULE_examplesString);
    assertThat(nodes).hasSize(2);
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example1.bsl'")
  void testExample1() {
    checkSource("src/test/resources/methodDescription/example1.bsl",
      new Pair(BSLMethodDescriptionParser.RULE_deprecate, 0),
      new Pair(BSLMethodDescriptionParser.RULE_deprecateDescription, 0),
      new Pair(BSLMethodDescriptionParser.RULE_descriptionBlock, 1),
      new Pair(BSLMethodDescriptionParser.RULE_description, 1),
      new Pair(BSLMethodDescriptionParser.RULE_descriptionString, 15),
      new Pair(BSLMethodDescriptionParser.RULE_examples, 1),
      new Pair(BSLMethodDescriptionParser.RULE_examplesString, 30),
      new Pair(BSLMethodDescriptionParser.RULE_callOptions, 0),
      new Pair(BSLMethodDescriptionParser.RULE_callOptionsString, 0),
      new Pair(BSLMethodDescriptionParser.RULE_parameters, 1),
      new Pair(BSLMethodDescriptionParser.RULE_parameterString, 16),
      new Pair(BSLMethodDescriptionParser.RULE_parameter, 9),
      new Pair(BSLMethodDescriptionParser.RULE_subParameter, 5),
      new Pair(BSLMethodDescriptionParser.RULE_parameterName, 14),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValues, 1),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValuesString, 12),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValue, 1),
      new Pair(BSLMethodDescriptionParser.RULE_typesBlock, 14),
      new Pair(BSLMethodDescriptionParser.RULE_typeDescription, 16),
      new Pair(BSLMethodDescriptionParser.RULE_type, 15),
      new Pair(BSLMethodDescriptionParser.RULE_simpleType, 14),
      new Pair(BSLMethodDescriptionParser.RULE_listTypes, 0),
      new Pair(BSLMethodDescriptionParser.RULE_complexType, 0),
      new Pair(BSLMethodDescriptionParser.RULE_hyperlinkType, 1),
      new Pair(BSLMethodDescriptionParser.RULE_spitter, 22),
      new Pair(BSLMethodDescriptionParser.RULE_hyperlinkBlock, 0),
      new Pair(BSLMethodDescriptionParser.RULE_startPart, 76)
    );
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example2.bsl'")
  void testExample2() {
    checkSource("src/test/resources/methodDescription/example2.bsl",
      new Pair(BSLMethodDescriptionParser.RULE_deprecate, 0),
      new Pair(BSLMethodDescriptionParser.RULE_deprecateDescription, 0),
      new Pair(BSLMethodDescriptionParser.RULE_descriptionBlock, 1),
      new Pair(BSLMethodDescriptionParser.RULE_description, 1),
      new Pair(BSLMethodDescriptionParser.RULE_descriptionString, 2),
      new Pair(BSLMethodDescriptionParser.RULE_examples, 0),
      new Pair(BSLMethodDescriptionParser.RULE_examplesString, 0),
      new Pair(BSLMethodDescriptionParser.RULE_callOptions, 0),
      new Pair(BSLMethodDescriptionParser.RULE_callOptionsString, 0),
      new Pair(BSLMethodDescriptionParser.RULE_parameters, 1),
      new Pair(BSLMethodDescriptionParser.RULE_parameterString, 2),
      new Pair(BSLMethodDescriptionParser.RULE_parameter, 1),
      new Pair(BSLMethodDescriptionParser.RULE_subParameter, 0),
      new Pair(BSLMethodDescriptionParser.RULE_parameterName, 1),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValues, 1),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValuesString, 0),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValue, 0),
      new Pair(BSLMethodDescriptionParser.RULE_typesBlock, 1),
      new Pair(BSLMethodDescriptionParser.RULE_typeDescription, 1),
      new Pair(BSLMethodDescriptionParser.RULE_type, 1),
      new Pair(BSLMethodDescriptionParser.RULE_simpleType, 1),
      new Pair(BSLMethodDescriptionParser.RULE_listTypes, 0),
      new Pair(BSLMethodDescriptionParser.RULE_complexType, 0),
      new Pair(BSLMethodDescriptionParser.RULE_hyperlinkType, 1),
      new Pair(BSLMethodDescriptionParser.RULE_spitter, 2),
      new Pair(BSLMethodDescriptionParser.RULE_hyperlinkBlock, 1),
      new Pair(BSLMethodDescriptionParser.RULE_startPart, 8)
    );
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example3.bsl'")
  void testExample3() {
    checkSource("src/test/resources/methodDescription/example3.bsl",
      new Pair(BSLMethodDescriptionParser.RULE_deprecate, 0),
      new Pair(BSLMethodDescriptionParser.RULE_deprecateDescription, 0),
      new Pair(BSLMethodDescriptionParser.RULE_descriptionBlock, 1),
      new Pair(BSLMethodDescriptionParser.RULE_description, 1),
      new Pair(BSLMethodDescriptionParser.RULE_descriptionString, 13),
      new Pair(BSLMethodDescriptionParser.RULE_examples, 0),
      new Pair(BSLMethodDescriptionParser.RULE_examplesString, 0),
      new Pair(BSLMethodDescriptionParser.RULE_callOptions, 0),
      new Pair(BSLMethodDescriptionParser.RULE_callOptionsString, 0),
      new Pair(BSLMethodDescriptionParser.RULE_parameters, 1),
      new Pair(BSLMethodDescriptionParser.RULE_parameterString, 7),
      new Pair(BSLMethodDescriptionParser.RULE_parameter, 5),
      new Pair(BSLMethodDescriptionParser.RULE_subParameter, 0),
      new Pair(BSLMethodDescriptionParser.RULE_parameterName, 5),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValues, 1),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValuesString, 2),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValue, 1),
      new Pair(BSLMethodDescriptionParser.RULE_typesBlock, 5),
      new Pair(BSLMethodDescriptionParser.RULE_typeDescription, 7),
      new Pair(BSLMethodDescriptionParser.RULE_type, 6),
      new Pair(BSLMethodDescriptionParser.RULE_simpleType, 6),
      new Pair(BSLMethodDescriptionParser.RULE_listTypes, 0),
      new Pair(BSLMethodDescriptionParser.RULE_complexType, 0),
      new Pair(BSLMethodDescriptionParser.RULE_hyperlinkType, 0),
      new Pair(BSLMethodDescriptionParser.RULE_spitter, 11),
      new Pair(BSLMethodDescriptionParser.RULE_hyperlinkBlock, 0),
      new Pair(BSLMethodDescriptionParser.RULE_startPart, 24)
    );
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example4.bsl'")
  void testExample4() {
    checkSource("src/test/resources/methodDescription/example4.bsl",
      new Pair(BSLMethodDescriptionParser.RULE_deprecate, 0),
      new Pair(BSLMethodDescriptionParser.RULE_deprecateDescription, 0),
      new Pair(BSLMethodDescriptionParser.RULE_descriptionBlock, 1),
      new Pair(BSLMethodDescriptionParser.RULE_description, 1),
      new Pair(BSLMethodDescriptionParser.RULE_descriptionString, 3),
      new Pair(BSLMethodDescriptionParser.RULE_examples, 0),
      new Pair(BSLMethodDescriptionParser.RULE_examplesString, 0),
      new Pair(BSLMethodDescriptionParser.RULE_callOptions, 0),
      new Pair(BSLMethodDescriptionParser.RULE_callOptionsString, 0),
      new Pair(BSLMethodDescriptionParser.RULE_parameters, 1),
      new Pair(BSLMethodDescriptionParser.RULE_parameterString, 4),
      new Pair(BSLMethodDescriptionParser.RULE_parameter, 3),
      new Pair(BSLMethodDescriptionParser.RULE_subParameter, 0),
      new Pair(BSLMethodDescriptionParser.RULE_parameterName, 3),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValues, 1),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValuesString, 2),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValue, 1),
      new Pair(BSLMethodDescriptionParser.RULE_typesBlock, 3),
      new Pair(BSLMethodDescriptionParser.RULE_typeDescription, 4),
      new Pair(BSLMethodDescriptionParser.RULE_type, 4),
      new Pair(BSLMethodDescriptionParser.RULE_simpleType, 5),
      new Pair(BSLMethodDescriptionParser.RULE_listTypes, 1),
      new Pair(BSLMethodDescriptionParser.RULE_complexType, 0),
      new Pair(BSLMethodDescriptionParser.RULE_hyperlinkType, 0),
      new Pair(BSLMethodDescriptionParser.RULE_spitter, 7),
      new Pair(BSLMethodDescriptionParser.RULE_hyperlinkBlock, 0),
      new Pair(BSLMethodDescriptionParser.RULE_startPart, 11)
    );
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example5.bsl'")
  void testExample5() {
    checkSource("src/test/resources/methodDescription/example5.bsl",
      new Pair(BSLMethodDescriptionParser.RULE_deprecate, 0),
      new Pair(BSLMethodDescriptionParser.RULE_deprecateDescription, 0),
      new Pair(BSLMethodDescriptionParser.RULE_descriptionBlock, 1),
      new Pair(BSLMethodDescriptionParser.RULE_description, 1),
      new Pair(BSLMethodDescriptionParser.RULE_descriptionString, 14),
      new Pair(BSLMethodDescriptionParser.RULE_examples, 0),
      new Pair(BSLMethodDescriptionParser.RULE_examplesString, 0),
      new Pair(BSLMethodDescriptionParser.RULE_callOptions, 0),
      new Pair(BSLMethodDescriptionParser.RULE_callOptionsString, 0),
      new Pair(BSLMethodDescriptionParser.RULE_parameters, 0),
      new Pair(BSLMethodDescriptionParser.RULE_parameterString, 0),
      new Pair(BSLMethodDescriptionParser.RULE_parameter, 0),
      new Pair(BSLMethodDescriptionParser.RULE_subParameter, 33),
      new Pair(BSLMethodDescriptionParser.RULE_parameterName, 33),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValues, 1),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValuesString, 316),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValue, 12),
      new Pair(BSLMethodDescriptionParser.RULE_typesBlock, 36),
      new Pair(BSLMethodDescriptionParser.RULE_typeDescription, 266),
      new Pair(BSLMethodDescriptionParser.RULE_type, 48),
      new Pair(BSLMethodDescriptionParser.RULE_simpleType, 48),
      new Pair(BSLMethodDescriptionParser.RULE_listTypes, 0),
      new Pair(BSLMethodDescriptionParser.RULE_complexType, 0),
      new Pair(BSLMethodDescriptionParser.RULE_hyperlinkType, 0),
      new Pair(BSLMethodDescriptionParser.RULE_spitter, 73),
      new Pair(BSLMethodDescriptionParser.RULE_hyperlinkBlock, 0),
      new Pair(BSLMethodDescriptionParser.RULE_startPart, 331)
    );
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example6.bsl'")
  void testExample6() {
    checkSource("src/test/resources/methodDescription/example6.bsl",
      new Pair(BSLMethodDescriptionParser.RULE_deprecate, 0),
      new Pair(BSLMethodDescriptionParser.RULE_deprecateDescription, 0),
      new Pair(BSLMethodDescriptionParser.RULE_descriptionBlock, 1),
      new Pair(BSLMethodDescriptionParser.RULE_description, 1),
      new Pair(BSLMethodDescriptionParser.RULE_descriptionString, 10),
      new Pair(BSLMethodDescriptionParser.RULE_examples, 0),
      new Pair(BSLMethodDescriptionParser.RULE_examplesString, 0),
      new Pair(BSLMethodDescriptionParser.RULE_callOptions, 1),
      new Pair(BSLMethodDescriptionParser.RULE_callOptionsString, 7),
      new Pair(BSLMethodDescriptionParser.RULE_parameters, 1),
      new Pair(BSLMethodDescriptionParser.RULE_parameterString, 39),
      new Pair(BSLMethodDescriptionParser.RULE_parameter, 4),
      new Pair(BSLMethodDescriptionParser.RULE_subParameter, 0),
      new Pair(BSLMethodDescriptionParser.RULE_parameterName, 4),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValues, 1),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValuesString, 2),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValue, 1),
      new Pair(BSLMethodDescriptionParser.RULE_typesBlock, 28),
      new Pair(BSLMethodDescriptionParser.RULE_typeDescription, 16),
      new Pair(BSLMethodDescriptionParser.RULE_type, 29),
      new Pair(BSLMethodDescriptionParser.RULE_simpleType, 29),
      new Pair(BSLMethodDescriptionParser.RULE_listTypes, 0),
      new Pair(BSLMethodDescriptionParser.RULE_complexType, 0),
      new Pair(BSLMethodDescriptionParser.RULE_hyperlinkType, 0),
      new Pair(BSLMethodDescriptionParser.RULE_spitter, 37),
      new Pair(BSLMethodDescriptionParser.RULE_hyperlinkBlock, 0),
      new Pair(BSLMethodDescriptionParser.RULE_startPart, 61)
    );
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example9.bsl'")
  void testExample9() {
    checkSource("src/test/resources/methodDescription/example9.bsl",
      new Pair(BSLMethodDescriptionParser.RULE_deprecate, 0),
      new Pair(BSLMethodDescriptionParser.RULE_deprecateDescription, 0),
      new Pair(BSLMethodDescriptionParser.RULE_descriptionBlock, 1),
      new Pair(BSLMethodDescriptionParser.RULE_description, 1),
      new Pair(BSLMethodDescriptionParser.RULE_descriptionString, 6),
      new Pair(BSLMethodDescriptionParser.RULE_examples, 0),
      new Pair(BSLMethodDescriptionParser.RULE_examplesString, 0),
      new Pair(BSLMethodDescriptionParser.RULE_callOptions, 0),
      new Pair(BSLMethodDescriptionParser.RULE_callOptionsString, 0),
      new Pair(BSLMethodDescriptionParser.RULE_parameters, 0),
      new Pair(BSLMethodDescriptionParser.RULE_parameterString, 0),
      new Pair(BSLMethodDescriptionParser.RULE_parameter, 0),
      new Pair(BSLMethodDescriptionParser.RULE_subParameter, 23),
      new Pair(BSLMethodDescriptionParser.RULE_parameterName, 23),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValues, 1),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValuesString, 43),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValue, 2),
      new Pair(BSLMethodDescriptionParser.RULE_typesBlock, 25),
      new Pair(BSLMethodDescriptionParser.RULE_typeDescription, 42),
      new Pair(BSLMethodDescriptionParser.RULE_type, 28),
      new Pair(BSLMethodDescriptionParser.RULE_simpleType, 32),
      new Pair(BSLMethodDescriptionParser.RULE_listTypes, 6),
      new Pair(BSLMethodDescriptionParser.RULE_complexType, 1),
      new Pair(BSLMethodDescriptionParser.RULE_hyperlinkType, 1),
      new Pair(BSLMethodDescriptionParser.RULE_spitter, 52),
      new Pair(BSLMethodDescriptionParser.RULE_hyperlinkBlock, 0),
      new Pair(BSLMethodDescriptionParser.RULE_startPart, 50)
    );
  }

  @Test
  @DisplayName("Parse 'src/test/resources/methodDescription/example10.bsl'")
  void testExample10() {
    checkSource("src/test/resources/methodDescription/example10.bsl",
      new Pair(BSLMethodDescriptionParser.RULE_deprecate, 0),
      new Pair(BSLMethodDescriptionParser.RULE_deprecateDescription, 0),
      new Pair(BSLMethodDescriptionParser.RULE_descriptionBlock, 1),
      new Pair(BSLMethodDescriptionParser.RULE_description, 1),
      new Pair(BSLMethodDescriptionParser.RULE_descriptionString, 5),
      new Pair(BSLMethodDescriptionParser.RULE_examples, 1),
      new Pair(BSLMethodDescriptionParser.RULE_examplesString, 11),
      new Pair(BSLMethodDescriptionParser.RULE_callOptions, 0),
      new Pair(BSLMethodDescriptionParser.RULE_callOptionsString, 0),
      new Pair(BSLMethodDescriptionParser.RULE_parameters, 1),
      new Pair(BSLMethodDescriptionParser.RULE_parameterString, 13),
      new Pair(BSLMethodDescriptionParser.RULE_parameter, 2),
      new Pair(BSLMethodDescriptionParser.RULE_subParameter, 2),
      new Pair(BSLMethodDescriptionParser.RULE_parameterName, 4),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValues, 1),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValuesString, 2),
      new Pair(BSLMethodDescriptionParser.RULE_returnsValue, 1),
      new Pair(BSLMethodDescriptionParser.RULE_typesBlock, 4),
      new Pair(BSLMethodDescriptionParser.RULE_typeDescription, 12),
      new Pair(BSLMethodDescriptionParser.RULE_type, 5),
      new Pair(BSLMethodDescriptionParser.RULE_simpleType, 6),
      new Pair(BSLMethodDescriptionParser.RULE_listTypes, 1),
      new Pair(BSLMethodDescriptionParser.RULE_complexType, 0),
      new Pair(BSLMethodDescriptionParser.RULE_hyperlinkType, 0),
      new Pair(BSLMethodDescriptionParser.RULE_spitter, 9),
      new Pair(BSLMethodDescriptionParser.RULE_hyperlinkBlock, 0),
      new Pair(BSLMethodDescriptionParser.RULE_startPart, 34)
    );
  }

  private ArrayList<ParseTree> getNodes(String text, int rule) {
    return new ArrayList<>(Trees.findAllRuleNodes((new BSLMethodDescriptionTokenizer(text)).getAst(), rule));
  }

  private ArrayList<ParseTree> getNodes(BSLMethodDescriptionParser.MethodDescriptionContext ast, int rule) {
    return new ArrayList<>(Trees.findAllRuleNodes(ast, rule));
  }

  private BSLMethodDescriptionParser.MethodDescriptionContext getAst(String text) {
    return (new BSLMethodDescriptionTokenizer(text)).getAst();
  }

  private void checkSource(String filePath, Pair... rules) {
    var exampleString = TestUtils.getSourceFromFile(filePath);
    setInput(exampleString);
    assertMatches(parser.methodDescription());
    var ast = getAst(exampleString);
    Arrays.stream(rules).forEach(rule -> {
      var nodes = getNodes(ast, rule.ruleID);
      assertThat(nodes).as(BSLMethodDescriptionParser.ruleNames[rule.ruleID]).hasSize(rule.size);
    });
  }

  private static class Pair {
    private final int ruleID;
    private final int size;

    private Pair(int ruleID, int size) {
      this.ruleID = ruleID;
      this.size = size;
    }
  }
}
