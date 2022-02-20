/*
 * This file is a part of BSL Parser.
 *
 * Copyright (c) 2018-2022
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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

abstract class AbstractLexerTest<T extends Lexer> {

  private final T lexer;

  protected AbstractLexerTest(Class<T> lexerClass) {
    this.lexer = createLexer(lexerClass);
  }

  private T createLexer(Class<T> lexerClass) {
    try {
      return lexerClass.getDeclaredConstructor(CharStream.class, boolean.class)
        .newInstance(CharStreams.fromString(""), true);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  protected List<Token> getTokens(int mode, String inputString) {
    CharStream input;

    try (
      InputStream inputStream = IOUtils.toInputStream(inputString, StandardCharsets.UTF_8);
      UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(inputStream);
      Reader inputStreamReader = new InputStreamReader(ubis, StandardCharsets.UTF_8)
    ) {
      ubis.skipBOM();
      CodePointCharStream inputTemp = CharStreams.fromReader(inputStreamReader);
      input = new CaseChangingCharStream(inputTemp);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    lexer.setInputStream(input);
    lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
    lexer.pushMode(mode);

    CommonTokenStream tempTokenStream = new CommonTokenStream(lexer);
    tempTokenStream.fill();

    return tempTokenStream.getTokens();
  }

  protected void assertMatch(String inputString, Integer... expectedTokens) {
    assertMatch(T.DEFAULT_MODE, inputString, expectedTokens);
  }

  protected void assertMatch(String inputStringRu, String inputStringEn, Integer... expectedTokens) {
    assertMatch(T.DEFAULT_MODE, inputStringRu, expectedTokens);
    assertMatch(T.DEFAULT_MODE, inputStringEn, expectedTokens);
  }

  protected void assertMatch(int mode, String inputString, Integer... expectedTokens) {
    List<Token> tokens = getTokens(mode, inputString);
    Integer[] tokenTypes = tokens.stream()
      .filter(token -> token.getChannel() == T.DEFAULT_TOKEN_CHANNEL)
      .filter(token -> token.getType() != Token.EOF)
      .map(Token::getType)
      .toArray(Integer[]::new);
    assertArrayEquals(expectedTokens, tokenTypes);
  }

  protected void assertMatchChannel(int channel, String inputString, Integer... expectedTokens) {
    List<Token> tokens = getTokens(T.DEFAULT_MODE, inputString);
    Integer[] tokenTypes = tokens.stream()
      .filter(token -> token.getChannel() == channel)
      .filter(token -> token.getType() != Token.EOF)
      .map(Token::getType)
      .toArray(Integer[]::new);
    assertArrayEquals(expectedTokens, tokenTypes);
  }

  protected void assertMatchChannel(int mode, int channel, String inputString, Integer... expectedTokens) {
    List<Token> tokens = getTokens(mode, inputString);
    Integer[] tokenTypes = tokens.stream()
      .filter(token -> token.getChannel() == channel)
      .filter(token -> token.getType() != Token.EOF)
      .map(Token::getType)
      .toArray(Integer[]::new);
    assertArrayEquals(expectedTokens, tokenTypes);
  }
}
