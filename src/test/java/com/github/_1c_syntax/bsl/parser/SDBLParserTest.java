/*
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
package com.github._1c_syntax.bsl.parser;

import org.junit.jupiter.api.Test;

public class SDBLParserTest extends AbstractParserTest<SDBLParser, SDBLLexer> {

  protected SDBLParserTest() {
    super(SDBLParser.class, SDBLLexer.class);
  }

  @Test
  void testQueries() {

    setInput("А; Перем А;");
    assertNotMatches(parser.queries());

    setInput("Выборка из таблицы");
    assertNotMatches(parser.queries());

    setInput("Выбрать 1");
    assertMatches(parser.queries());

    setInput("Выбрать 1; \n" +
      "Уничтожить Б");
    assertMatches(parser.queries());

    setInput("Выбрать 1, \n" +
      "УничтожитьБорна как Б");
    assertMatches(parser.queries());

    setInput("Выбрать 1, \n" +
      "Уничтожить Б");
    assertNotMatches(parser.queries());
  }

  @Test
  void testDropTable() {

    setInput("Уничтожить �?мяТаблицы");
    assertMatches(parser.queries());
    setInput("drop �?мяТаблицы;");
    assertMatches(parser.queries());
    setInput("drop");
    assertNotMatches(parser.queries());
  }

}
