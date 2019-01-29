/*
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018-2019
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
package org.github._1c_syntax.bsl.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class BSLLexerTest {

  private BSLLexer lexer = new BSLLexer(null);

  private void assertMatch(String inputString, Integer... expectedTokens) {
    assertMatch(BSLLexer.DEFAULT_MODE, inputString, expectedTokens);
  }

  private void assertMatch(int mode, String inputString, Integer... expectedTokens) {
    CharStream input;

    try {
      InputStream inputStream = IOUtils.toInputStream(inputString, StandardCharsets.UTF_8);

      UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(inputStream);
      ubis.skipBOM();

      input = CharStreams.fromStream(ubis, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    lexer.setInputStream(input);
    lexer.mode(mode);

    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
    tokenStream.fill();
    List<Token> tokens = tokenStream.getTokens();
    Integer[] tokenTypes = tokens.stream()
      .filter(token -> token.getChannel() == BSLLexer.DEFAULT_TOKEN_CHANNEL)
      .filter(token -> token.getType() != Token.EOF)
      .map(Token::getType)
      .toArray(Integer[]::new);
    assertArrayEquals(expectedTokens, tokenTypes);
  }

  @Test
  void testBOM() {
    assertMatch('\uFEFF' + "Процедура", BSLLexer.PROCEDURE_KEYWORD);
  }

  @Test
  void testUse() {
    assertMatch(BSLLexer.PREPROCESSOR_MODE, "Использовать lib", BSLLexer.PREPROC_USE_KEYWORD, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch(BSLLexer.PREPROCESSOR_MODE, "Использовать \"lib\"", BSLLexer.PREPROC_USE_KEYWORD, BSLLexer.PREPROC_STRING);
  }

  @Test
  void testPreproc_LineComment() {
    assertMatch("#КонецОбласти // Концевой комментарий", BSLLexer.HASH, BSLLexer.PREPROC_END_REGION);
  }

  @Test
  void testPreproc_Region() {
    assertMatch("#Область ИмяОбласти", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Область МобильныйКлиент", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
  }

  @Test
  void testString() {
    assertMatch("\"строка\"", BSLLexer.STRING);
    assertMatch("\"", BSLLexer.STRINGSTART);
    assertMatch("|aaa", BSLLexer.STRINGPART);
    assertMatch("|", BSLLexer.BAR);
    assertMatch("|\"", BSLLexer.STRINGTAIL);
    assertMatch("|aaa\"", BSLLexer.STRINGTAIL);
  }

  @Test
  void testAnnotation() {
    assertMatch("&НаСервере", BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATSERVER_SYMBOL);
    assertMatch("&НаКлиентеНаСервере", BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATCLIENTATSERVER_SYMBOL);
    assertMatch("&Аннотация", BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_CUSTOM_SYMBOL);
    assertMatch("&НаСервере &Аннотация &НаСервере",
      BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATSERVER_SYMBOL,
      BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_CUSTOM_SYMBOL,
      BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATSERVER_SYMBOL
    );
    assertMatch("&НаСервере\n&Аннотация\n&НаСервере",
      BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATSERVER_SYMBOL,
      BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_CUSTOM_SYMBOL,
      BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATSERVER_SYMBOL
    );
    assertMatch("&НаСервере", BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATSERVER_SYMBOL);
  }

  @Test
  void testProcedure() {
    assertMatch("Процедура", BSLLexer.PROCEDURE_KEYWORD);
    assertMatch("Поле.Процедура", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testFunction() {
    assertMatch("Функция", BSLLexer.FUNCTION_KEYWORD);
    assertMatch("Поле.Функция", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testTo() {
    assertMatch("По", BSLLexer.TO_KEYWORD);
    assertMatch("Поле.По", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testExecute() {
    assertMatch("Выполнить", BSLLexer.EXECUTE_KEYWORD);
    assertMatch("Запрос.Выполнить", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testTry() {
    assertMatch("Попытка", BSLLexer.TRY_KEYWORD);
    assertMatch("Поле.Попытка", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testBreak() {
    assertMatch("Прервать", BSLLexer.BREAK_KEYWORD);
    assertMatch("Поле.Прервать", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testNew() {
    assertMatch("Новый", BSLLexer.NEW_KEYWORD);
    assertMatch("Поле.Новый", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testIn() {
    assertMatch("Из", BSLLexer.IN_KEYWORD);
    assertMatch("In", BSLLexer.IN_KEYWORD);
    assertMatch("Поле.Из", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }
  
}
