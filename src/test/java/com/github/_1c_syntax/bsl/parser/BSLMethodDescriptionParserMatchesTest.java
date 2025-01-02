/*
 * This file is a part of BSL Parser.
 *
 * Copyright (c) 2018-2025
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

class BSLMethodDescriptionParserMatchesTest {

  private TestParser<BSLMethodDescriptionParser, BSLMethodDescriptionLexer> testParser;

  @BeforeEach
  void before() {
    testParser = new TestParser<>(BSLMethodDescriptionParser.class, BSLMethodDescriptionLexer.class);
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "",
      "//Устарела но не\n//\n//\t//34567890-dfghjkl_)(*&^%$#@ Совсем \n//" +
        "//||Описание метода\n// \n//" +
        "//Параметры//",
      "//Устарела.", "//Параметры:", "//Варианты вызова:", "//Пример:", "//Возвращаемое значение:"
    }
  )
  void testMethodDescription(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().methodDescription());
  }

  @Test
  void testDeprecate() {
    testParser.assertThat("").noMatches(testParser.parser().deprecate());
    testParser.assertThat("//Устарела").matches(testParser.parser().deprecate());

    testParser.assertThat("//Описание\nУстарела").noMatches(testParser.parser().deprecate());
    testParser.assertThat("//Описание\nУстарела.").noMatches(testParser.parser().deprecate());

    testParser.assertThat("// Устарела.")
      .matches(testParser.parser().deprecate())
      .noMatches(testParser.parser().deprecateDescription());
    testParser.assertThat("//Устарела.")
      .matches(testParser.parser().deprecate())
      .noMatches(testParser.parser().deprecateDescription());
    testParser.assertThat("//Устарела.\n")
      .matches(testParser.parser().deprecate())
      .noMatches(testParser.parser().deprecateDescription());

    testParser.assertThat("//Устарела. \nИспользовать другой метод")
      .matches(testParser.parser().deprecate())
      .containsRule(BSLMethodDescriptionParser.RULE_deprecateDescription, 0);
    testParser.assertThat("//Устарела. \n//Использовать другой метод")
      .matches(testParser.parser().deprecate())
      .containsRule(BSLMethodDescriptionParser.RULE_deprecateDescription, 0);

    testParser.assertThat("//Устарела. Использовать другой метод")
      .matches(testParser.parser().deprecate())
      .containsRule(BSLMethodDescriptionParser.RULE_deprecateDescription, 1);

    testParser.assertThat("//Устарела.Использовать другой метод")
      .matches(testParser.parser().methodDescription())
      .containsRule(BSLMethodDescriptionParser.RULE_deprecate, 0);
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "//Устарела.", "//Устарела.\n//Параметры:", "//Устарела.\n//Call options:", "//returns:", "//example:"
    }
  )
  void testNoDescription(String inputString) {
    testParser.assertThat(inputString).containsRule(BSLMethodDescriptionParser.RULE_description, 0);
  }

  @Test
  void testDescription() {
    testParser.assertThat("//Устарела.\n//Описание\n//\n//ногостчрочное")
      .containsRule(BSLMethodDescriptionParser.RULE_description, 1);
    testParser.assertThat("//Описание \n// многострочное:")
      .containsRule(BSLMethodDescriptionParser.RULE_description, 1);
  }

  @Test
  void testNoParameters() {
    testParser.assertThat("//Параметры").noMatches(testParser.parser().parameters());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "//Параметры:\n//Параметр1\n//\n//Параметр2\n",
      "//Параметры:\n//Параметр1\n//\n//Параметр2\n//Пример:",
      "//Параметры:\n//Параметр1\n//\n//Параметр2\n//Варианты вызова:",
      "//Параметры:\n//Параметр1\n//\n//Параметр2\n//Возвращаемое значение:",
      "//Параметры:\n//Параметр1 - Тип  Описание\n//\n//Параметр2\n//Возвращаемое значение:"
    }
  )
  void testParameters(String inputString) {
    testParser.assertThat(inputString)
      .matches(testParser.parser().parameters())
      .containsRule(BSLMethodDescriptionParser.RULE_parameterString, 3);
  }

  @Test
  void testNoCallOptions() {
    testParser.assertThat("//Варианты вызова").noMatches(testParser.parser().callOptions());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "//Call options:\n//Var 1\n//\n//Var 2 bla ba()",
      "//Call options:\n//Var 1\n//\n//Var 2 bla ba()\n//Example:",
      "//Call options:\n//Var 1\n//\n//Var 2 bla ba()\n//Возвращаемое значение:"
    }
  )
  void testCallOptions(String inputString) {
    testParser.assertThat(inputString)
      .matches(testParser.parser().callOptions())
      .containsRule(BSLMethodDescriptionParser.RULE_callOptionsString, 3);
  }

  @Test
  void testReturns() {
    testParser.assertThat("//returns").noMatches(testParser.parser().returnsValues());
    testParser.assertThat("//returns:\n//boolean - description\n")
      .matches(testParser.parser().returnsValues())
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValuesString, 1);
    testParser.assertThat("//returns:\n// - ref - description\n// - boolean - description\n//Example:")
      .matches(testParser.parser().returnsValues())
      .containsRule(BSLMethodDescriptionParser.RULE_returnsValuesString, 2);
  }

  @Test
  void testExample() {
    testParser.assertThat("//Пример").noMatches(testParser.parser().examples());
    testParser.assertThat("//Пример:\n//Пример - описаниепримера")
      .matches(testParser.parser().examples())
      .containsRule(BSLMethodDescriptionParser.RULE_examplesString, 1);
    testParser.assertThat("//Пример:\n//Пример:\n//Пример: - описаниепримера")
      .matches(testParser.parser().examples())
      .containsRule(BSLMethodDescriptionParser.RULE_examplesString, 2);
  }
}
