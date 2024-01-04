/*
 * This file is a part of BSL Parser.
 *
 * Copyright (c) 2018-2024
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

import com.github._1c_syntax.bsl.parser.testing.TestLexer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BSLMethodDescriptionLexerTest {

  private TestLexer<BSLMethodDescriptionLexer> testLexer;

  @BeforeEach
  void before() {
    testLexer = new TestLexer<>(BSLMethodDescriptionLexer.class);
  }

  @Test
  void testWhitespaces() {
    var inputString = "//   А";
    testLexer.assertThat(BSLMethodDescriptionLexer.DEFAULT_MODE, inputString)
      .containsExactly(BSLMethodDescriptionLexer.COMMENT,
        BSLMethodDescriptionLexer.SPACE,
        BSLMethodDescriptionLexer.WORD,
        BSLMethodDescriptionLexer.EOF);
  }

  @Test
  void testBOM() {
    testLexer.assertThat('\uFEFF' + "Процедура").containsAll(BSLMethodDescriptionLexer.WORD);
  }

  @Test
  void testHyperlink() {
    testLexer.assertThat("СМ").isEqualTo("СМСМ").containsAll(BSLMethodDescriptionLexer.WORD);
    testLexer.assertThat("SEE").isEqualTo("СМ.").containsAll(BSLMethodDescriptionLexer.SEE_KEYWORD);

    testLexer.assertThat("СМ. ОбщийМодуль")
      .isEqualTo("SEE ОбщийМодуль")
      .isEqualTo("SEE  ОбщийМодуль")
      .containsAll(
        BSLMethodDescriptionLexer.SEE_KEYWORD,
        BSLMethodDescriptionLexer.SPACE,
        BSLMethodDescriptionLexer.WORD);

    testLexer.assertThat("SSEE ОбщийМодуль")
      .containsAll(BSLMethodDescriptionLexer.WORD, BSLMethodDescriptionLexer.SPACE, BSLMethodDescriptionLexer.WORD);

    testLexer.assertThat("СМ. ОбщийМодуль.Метод").isEqualTo("SEE ОбщийМодуль.Метод")
      .containsAll(
        BSLMethodDescriptionLexer.SEE_KEYWORD,
        BSLMethodDescriptionLexer.SPACE,
        BSLMethodDescriptionLexer.DOTSWORD);

    testLexer.assertThat("SEE ОбщийМодуль.Метод()").containsAll(
      BSLMethodDescriptionLexer.SEE_KEYWORD,
      BSLMethodDescriptionLexer.SPACE,
      BSLMethodDescriptionLexer.DOTSWORD,
      BSLMethodDescriptionLexer.LPAREN,
      BSLMethodDescriptionLexer.RPAREN);

    testLexer.assertThat("СМ. ОбщийМодуль.Метод(Параметра, Значение)").containsAll(
      BSLMethodDescriptionLexer.SEE_KEYWORD,
      BSLMethodDescriptionLexer.SPACE,
      BSLMethodDescriptionLexer.DOTSWORD,
      BSLMethodDescriptionLexer.LPAREN,
      BSLMethodDescriptionLexer.WORD,
      BSLMethodDescriptionLexer.COMMA,
      BSLMethodDescriptionLexer.SPACE,
      BSLMethodDescriptionLexer.WORD,
      BSLMethodDescriptionLexer.RPAREN);

    testLexer.assertThat("SEE ОбщийМодуль.Метод() WORD").containsAll(
      BSLMethodDescriptionLexer.SEE_KEYWORD,
      BSLMethodDescriptionLexer.SPACE,
      BSLMethodDescriptionLexer.DOTSWORD,
      BSLMethodDescriptionLexer.LPAREN,
      BSLMethodDescriptionLexer.RPAREN,
      BSLMethodDescriptionLexer.SPACE,
      BSLMethodDescriptionLexer.WORD);

    testLexer.assertThat("SEE. ОбщийМодуль.Метод() WORD").containsAll(
      BSLMethodDescriptionLexer.SEE_KEYWORD,
      BSLMethodDescriptionLexer.ANYSYMBOL,
      BSLMethodDescriptionLexer.SPACE,
      BSLMethodDescriptionLexer.DOTSWORD,
      BSLMethodDescriptionLexer.LPAREN,
      BSLMethodDescriptionLexer.RPAREN,
      BSLMethodDescriptionLexer.SPACE,
      BSLMethodDescriptionLexer.WORD);

    testLexer.assertThat("СМ.   ОбщийМодуль.Метод() WORD").containsAll(
      BSLMethodDescriptionLexer.SEE_KEYWORD,
      BSLMethodDescriptionLexer.SPACE,
      BSLMethodDescriptionLexer.DOTSWORD,
      BSLMethodDescriptionLexer.LPAREN,
      BSLMethodDescriptionLexer.RPAREN,
      BSLMethodDescriptionLexer.SPACE,
      BSLMethodDescriptionLexer.WORD);
  }

  @Test
  void testParameters() {
    testLexer.assertThat("Параметры").isEqualTo("Parameters").containsAll(BSLMethodDescriptionLexer.WORD);
    testLexer.assertThat("NoParameters:")
      .containsAll(BSLMethodDescriptionLexer.WORD, BSLMethodDescriptionLexer.COLON);
    testLexer.assertThat("Параметры:").isEqualTo("Parameters:")
      .containsAll(BSLMethodDescriptionLexer.PARAMETERS_KEYWORD);
    testLexer.assertThat("Параметры :")
      .containsAll(BSLMethodDescriptionLexer.WORD, BSLMethodDescriptionLexer.SPACE, BSLMethodDescriptionLexer.COLON);
  }

  @Test
  void testReturns() {
    testLexer.assertThat("Возвращаемое значение").containsAll(
      BSLMethodDescriptionLexer.WORD,
      BSLMethodDescriptionLexer.SPACE,
      BSLMethodDescriptionLexer.WORD);
    testLexer.assertThat("RETURNS", BSLMethodDescriptionLexer.WORD);
    testLexer.assertThat("Возвращаемое  значение:")
      .isEqualTo("НеВозвращаемое значение:")
      .containsAll(
        BSLMethodDescriptionLexer.WORD,
        BSLMethodDescriptionLexer.SPACE,
        BSLMethodDescriptionLexer.WORD,
        BSLMethodDescriptionLexer.COLON);
    testLexer.assertThat("RETURNS :").containsAll(
      BSLMethodDescriptionLexer.WORD,
      BSLMethodDescriptionLexer.SPACE,
      BSLMethodDescriptionLexer.COLON);
    testLexer.assertThat("Возвращаемое значение:").isEqualTo("RETURNS:")
      .containsAll(BSLMethodDescriptionLexer.RETURNS_KEYWORD);
    testLexer.assertThat("НЕRETURNS:")
      .containsAll(BSLMethodDescriptionLexer.WORD, BSLMethodDescriptionLexer.COLON);
  }

  @Test
  void testExample() {
    testLexer.assertThat("Пример")
      .isEqualTo("ПримерЫ")
      .isEqualTo("Example")
      .isEqualTo("Examples")
      .containsAll(BSLMethodDescriptionLexer.WORD);

    testLexer.assertThat("Примеры:")
      .isEqualTo("Examples:")
      .isEqualTo("Пример:")
      .isEqualTo("Example:")
      .containsAll(BSLMethodDescriptionLexer.EXAMPLE_KEYWORD);

    testLexer.assertThat("Пример :").containsAll(
      BSLMethodDescriptionLexer.WORD,
      BSLMethodDescriptionLexer.SPACE,
      BSLMethodDescriptionLexer.COLON);

    testLexer.assertThat("NoExample:").containsAll(
      BSLMethodDescriptionLexer.WORD,
      BSLMethodDescriptionLexer.COLON);
  }

  @Test
  void testCallOptions() {
    testLexer.assertThat("Варианты вызова")
      .isEqualTo("Call options")
      .containsAll(BSLMethodDescriptionLexer.WORD, BSLMethodDescriptionLexer.SPACE, BSLMethodDescriptionLexer.WORD);

    testLexer.assertThat("Варианты  вызова:")
      .containsAll(
        BSLMethodDescriptionLexer.WORD,
        BSLMethodDescriptionLexer.SPACE,
        BSLMethodDescriptionLexer.WORD,
        BSLMethodDescriptionLexer.COLON);

    testLexer.assertThat("Call options :")
      .containsAll(
        BSLMethodDescriptionLexer.WORD,
        BSLMethodDescriptionLexer.SPACE,
        BSLMethodDescriptionLexer.WORD,
        BSLMethodDescriptionLexer.SPACE,
        BSLMethodDescriptionLexer.COLON);

    testLexer.assertThat("Варианты вызова:").isEqualTo("Call options:")
      .containsAll(BSLMethodDescriptionLexer.CALL_OPTIONS_KEYWORD);

    testLexer.assertThat("Вариант вызова:").isEqualTo("Call option:")
      .containsAll(
        BSLMethodDescriptionLexer.WORD,
        BSLMethodDescriptionLexer.SPACE,
        BSLMethodDescriptionLexer.WORD,
        BSLMethodDescriptionLexer.COLON);
  }

  @Test
  void testDeprecate() {
    testLexer.assertThat("Устарела")
      .isEqualTo("Deprecate")
      .isEqualTo("Устарела.")
      .isEqualTo("Deprecate.")
      .containsAll(BSLMethodDescriptionLexer.DEPRECATE_KEYWORD);

    testLexer.assertThat("Depricate", BSLMethodDescriptionLexer.WORD);

    testLexer.assertThat("Устарела .").containsAll(
      BSLMethodDescriptionLexer.DEPRECATE_KEYWORD,
      BSLMethodDescriptionLexer.SPACE,
      BSLMethodDescriptionLexer.ANYSYMBOL);

    testLexer.assertThat("Deprecate:").containsAll(
      BSLMethodDescriptionLexer.DEPRECATE_KEYWORD,
      BSLMethodDescriptionLexer.COLON);
  }
}
