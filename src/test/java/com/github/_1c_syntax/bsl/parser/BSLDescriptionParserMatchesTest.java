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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BSLDescriptionParserMatchesTest {

  private TestParser<BSLDescriptionParser, BSLDescriptionLexer> testParser;

  @BeforeEach
  void before() {
    testParser = new TestParser<>(BSLDescriptionParser.class, BSLDescriptionLexer.class);
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "",
      "//Устарела но не\n//\n//\t//34567890-dfghjkl_)(*&^%$#@ Совсем \n//" +
        "//||Описание метода\n// \n//" +
        "//Параметры//\n",
      "//Устарела.\n",
      "//Параметры:\n",
      "//Варианты вызова:\n",
      "//Пример:\n",
      "//Возвращаемое значение:\n"
    }
  )
  void testMethodDescription(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().methodDescription());
  }

  @Test
  void testDeprecate() {
    testParser.assertThat("").noMatches(testParser.parser().deprecateBlock());
    testParser.assertThat("//Устарела\n").matches(testParser.parser().deprecateBlock());

    testParser.assertThat("//Описание\nУстарела").noMatches(testParser.parser().deprecateBlock());
    testParser.assertThat("//Описание\nУстарела.").noMatches(testParser.parser().deprecateBlock());

    testParser.assertThat("// Устарела.\n")
      .matches(testParser.parser().deprecateBlock())
      .noMatches(testParser.parser().deprecateDescription());
    testParser.assertThat("//Устарела.\n")
      .matches(testParser.parser().deprecateBlock())
      .noMatches(testParser.parser().deprecateDescription());
    testParser.assertThat("//Устарела.\n")
      .matches(testParser.parser().deprecateBlock())
      .noMatches(testParser.parser().deprecateDescription());

    testParser.assertThat("//Устарела. \n//Использовать другой метод\n")
      .matches(testParser.parser().deprecateBlock())
      .containsRule(BSLDescriptionParser.RULE_deprecateDescription, 1);

    testParser.assertThat("//Устарела. Использовать другой метод\n")
      .matches(testParser.parser().deprecateBlock())
      .containsRule(BSLDescriptionParser.RULE_deprecateDescription, 1);

    testParser.assertThat("//Устарела.Использовать другой метод\n")
      .matches(testParser.parser().methodDescription())
      .containsRule(BSLDescriptionParser.RULE_deprecateBlock, 0);
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "//Устарела.", "//Устарела.\n//Параметры:", "//Устарела.\n//Call options:", "//returns:", "//example:"
    }
  )
  void testNoDescription(String inputString) {
    testParser.assertThat(inputString).containsRule(BSLDescriptionParser.RULE_descriptionString, 0);
  }

  @Test
  void testDescription() {
    testParser.assertThat("//Устарела.\n//Описание\n//\n//ногостчрочное")
      .containsRule(BSLDescriptionParser.RULE_descriptionString, 3);
    testParser.assertThat("//Описание \n// многострочное:\n")
      .containsRule(BSLDescriptionParser.RULE_descriptionString, 2);
  }

  @Test
  void testNoParameters() {
    testParser.assertThat("//Параметры").noMatches(testParser.parser().parametersBlock());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "//Параметры:\n//Параметр1\n//\n//Параметр2\n",
      "//Параметры:\n//Параметр1\n//\n//Параметр2\n//Пример:\n",
      "//Параметры:\n//Параметр1\n//\n//Параметр2\n//Варианты вызова:\n",
      "//Параметры:\n//Параметр1\n//\n//Параметр2\n//Возвращаемое значение:\n",
      "//Параметры:\n//Параметр1 - Тип  Описание\n//\n//Параметр2\n//Возвращаемое значение:\n"
    }
  )
  void testParameters(String inputString) {
    testParser.assertThat(inputString)
      .matches(testParser.parser().parametersBlock())
      .containsRule(BSLDescriptionParser.RULE_parameterString, 3);
  }

  @Test
  void testReturns() {
    testParser.assertThat("//returns").noMatches(testParser.parser().returnsValuesBlock());
    testParser.assertThat("//returns:\n//boolean - description\n")
      .matches(testParser.parser().returnsValuesBlock())
      .containsRule(BSLDescriptionParser.RULE_returnsValuesString, 1);
    testParser.assertThat("//returns:\n// - ref - description\n// - boolean - description\n//Example:\n")
      .matches(testParser.parser().returnsValuesBlock())
      .containsRule(BSLDescriptionParser.RULE_returnsValuesString, 2);
  }

  @Test
  void testExample() {
    testParser.assertThat("//Пример").noMatches(testParser.parser().examplesBlock());
    testParser.assertThat("//Пример:\n//Пример - описаниепримера\n")
      .matches(testParser.parser().examplesBlock())
      .containsRule(BSLDescriptionParser.RULE_examplesString, 1);
    testParser.assertThat("//Пример:\n//Пример:\n//Пример: - описаниепримера\n")
      .matches(testParser.parser().examplesBlock())
      .containsRule(BSLDescriptionParser.RULE_examplesString, 2);
  }
}
