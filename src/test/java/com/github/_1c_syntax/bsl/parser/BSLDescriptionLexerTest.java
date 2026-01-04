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

import com.github._1c_syntax.bsl.parser.testing.TestLexer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BSLDescriptionLexerTest {

  private TestLexer<BSLDescriptionLexer> testLexer;

  @BeforeEach
  void before() {
    testLexer = new TestLexer<>(BSLDescriptionLexer.class);
  }

  @Test
  void testWhitespaces() {
    var inputString = "//   А";
    testLexer.assertThat(BSLDescriptionLexer.DEFAULT_MODE, inputString)
      .containsExactly(BSLDescriptionLexer.COMMENT,
        BSLDescriptionLexer.SPACE,
        BSLDescriptionLexer.WORD,
        BSLDescriptionLexer.EOF);
  }

  @Test
  void testBOM() {
    testLexer.assertThat('\uFEFF' + "Процедура").containsAll(BSLDescriptionLexer.WORD);
  }

  @Test
  void testHyperlink() {
    testLexer.assertThat("СМ").isEqualTo("СМСМ").containsAll(BSLDescriptionLexer.WORD);
    testLexer.assertThat("SEE").isEqualTo("СМ.").containsAll(BSLDescriptionLexer.SEE_KEYWORD);

    testLexer.assertThat("СМ. ОбщийМодуль")
      .isEqualTo("SEE ОбщийМодуль")
      .isEqualTo("SEE  ОбщийМодуль")
      .containsAll(
        BSLDescriptionLexer.SEE_KEYWORD,
        BSLDescriptionLexer.SPACE,
        BSLDescriptionLexer.WORD);

    testLexer.assertThat("SSEE ОбщийМодуль")
      .containsAll(BSLDescriptionLexer.WORD, BSLDescriptionLexer.SPACE, BSLDescriptionLexer.WORD);

    testLexer.assertThat("СМ. ОбщийМодуль.Метод").isEqualTo("SEE ОбщийМодуль.Метод")
      .containsAll(
        BSLDescriptionLexer.SEE_KEYWORD,
        BSLDescriptionLexer.SPACE,
        BSLDescriptionLexer.DOTSWORD);

    testLexer.assertThat("SEE ОбщийМодуль.Метод()").containsAll(
      BSLDescriptionLexer.SEE_KEYWORD,
      BSLDescriptionLexer.SPACE,
      BSLDescriptionLexer.DOTSWORD,
      BSLDescriptionLexer.LPAREN,
      BSLDescriptionLexer.RPAREN);

    testLexer.assertThat("СМ. ОбщийМодуль.Метод(Параметра, Значение)").containsAll(
      BSLDescriptionLexer.SEE_KEYWORD,
      BSLDescriptionLexer.SPACE,
      BSLDescriptionLexer.DOTSWORD,
      BSLDescriptionLexer.LPAREN,
      BSLDescriptionLexer.WORD,
      BSLDescriptionLexer.COMMA,
      BSLDescriptionLexer.SPACE,
      BSLDescriptionLexer.WORD,
      BSLDescriptionLexer.RPAREN);

    testLexer.assertThat("SEE ОбщийМодуль.Метод() WORD").containsAll(
      BSLDescriptionLexer.SEE_KEYWORD,
      BSLDescriptionLexer.SPACE,
      BSLDescriptionLexer.DOTSWORD,
      BSLDescriptionLexer.LPAREN,
      BSLDescriptionLexer.RPAREN,
      BSLDescriptionLexer.SPACE,
      BSLDescriptionLexer.WORD);

    testLexer.assertThat("SEE. ОбщийМодуль.Метод() WORD").containsAll(
      BSLDescriptionLexer.SEE_KEYWORD,
      BSLDescriptionLexer.ANYSYMBOL,
      BSLDescriptionLexer.SPACE,
      BSLDescriptionLexer.DOTSWORD,
      BSLDescriptionLexer.LPAREN,
      BSLDescriptionLexer.RPAREN,
      BSLDescriptionLexer.SPACE,
      BSLDescriptionLexer.WORD);

    testLexer.assertThat("СМ.   ОбщийМодуль.Метод() WORD").containsAll(
      BSLDescriptionLexer.SEE_KEYWORD,
      BSLDescriptionLexer.SPACE,
      BSLDescriptionLexer.DOTSWORD,
      BSLDescriptionLexer.LPAREN,
      BSLDescriptionLexer.RPAREN,
      BSLDescriptionLexer.SPACE,
      BSLDescriptionLexer.WORD);
  }

  @Test
  void testParameters() {
    testLexer.assertThat("Параметры").isEqualTo("Parameters").containsAll(BSLDescriptionLexer.WORD);
    testLexer.assertThat("NoParameters:")
      .containsAll(BSLDescriptionLexer.WORD, BSLDescriptionLexer.COLON);
    testLexer.assertThat("Параметры:").isEqualTo("Parameters:")
      .containsAll(BSLDescriptionLexer.PARAMETERS_KEYWORD);
    testLexer.assertThat("Параметры :")
      .containsAll(BSLDescriptionLexer.WORD, BSLDescriptionLexer.SPACE, BSLDescriptionLexer.COLON);
  }

  @Test
  void testReturns() {
    testLexer.assertThat("Возвращаемое значение").containsAll(
      BSLDescriptionLexer.WORD,
      BSLDescriptionLexer.SPACE,
      BSLDescriptionLexer.WORD);
    testLexer.assertThat("RETURNS", BSLDescriptionLexer.WORD);
    testLexer.assertThat("Возвращаемое  значение:")
      .isEqualTo("НеВозвращаемое значение:")
      .containsAll(
        BSLDescriptionLexer.WORD,
        BSLDescriptionLexer.SPACE,
        BSLDescriptionLexer.WORD,
        BSLDescriptionLexer.COLON);
    testLexer.assertThat("RETURNS :").containsAll(
      BSLDescriptionLexer.WORD,
      BSLDescriptionLexer.SPACE,
      BSLDescriptionLexer.COLON);
    testLexer.assertThat("Возвращаемое значение:").isEqualTo("RETURNS:")
      .containsAll(BSLDescriptionLexer.RETURNS_KEYWORD);
    testLexer.assertThat("НЕRETURNS:")
      .containsAll(BSLDescriptionLexer.WORD, BSLDescriptionLexer.COLON);
  }

  @Test
  void testExample() {
    testLexer.assertThat("Пример")
      .isEqualTo("ПримерЫ")
      .isEqualTo("Example")
      .isEqualTo("Examples")
      .containsAll(BSLDescriptionLexer.WORD);

    testLexer.assertThat("Примеры:")
      .isEqualTo("Examples:")
      .isEqualTo("Пример:")
      .isEqualTo("Example:")
      .containsAll(BSLDescriptionLexer.EXAMPLE_KEYWORD);

    testLexer.assertThat("Пример :").containsAll(
      BSLDescriptionLexer.WORD,
      BSLDescriptionLexer.SPACE,
      BSLDescriptionLexer.COLON);

    testLexer.assertThat("NoExample:").containsAll(
      BSLDescriptionLexer.WORD,
      BSLDescriptionLexer.COLON);
  }

  @Test
  void testCallOptions() {
    testLexer.assertThat("Варианты вызова")
      .isEqualTo("Call options")
      .containsAll(BSLDescriptionLexer.WORD, BSLDescriptionLexer.SPACE, BSLDescriptionLexer.WORD);

    testLexer.assertThat("Варианты  вызова:")
      .containsAll(
        BSLDescriptionLexer.WORD,
        BSLDescriptionLexer.SPACE,
        BSLDescriptionLexer.WORD,
        BSLDescriptionLexer.COLON);

    testLexer.assertThat("Call options :")
      .containsAll(
        BSLDescriptionLexer.WORD,
        BSLDescriptionLexer.SPACE,
        BSLDescriptionLexer.WORD,
        BSLDescriptionLexer.SPACE,
        BSLDescriptionLexer.COLON);

    testLexer.assertThat("Вариант вызова:").isEqualTo("Call option:")
      .containsAll(
        BSLDescriptionLexer.WORD,
        BSLDescriptionLexer.SPACE,
        BSLDescriptionLexer.WORD,
        BSLDescriptionLexer.COLON);
  }

  @Test
  void testDeprecate() {
    testLexer.assertThat("Устарела")
      .isEqualTo("Deprecate")
      .isEqualTo("Устарела.")
      .isEqualTo("Deprecate.")
      .containsAll(BSLDescriptionLexer.DEPRECATE_KEYWORD);

    testLexer.assertThat("Depricate", BSLDescriptionLexer.WORD);

    testLexer.assertThat("Устарела .").containsAll(
      BSLDescriptionLexer.DEPRECATE_KEYWORD,
      BSLDescriptionLexer.SPACE,
      BSLDescriptionLexer.ANYSYMBOL);

    testLexer.assertThat("Deprecate:").containsAll(
      BSLDescriptionLexer.DEPRECATE_KEYWORD,
      BSLDescriptionLexer.COLON);
  }
}
