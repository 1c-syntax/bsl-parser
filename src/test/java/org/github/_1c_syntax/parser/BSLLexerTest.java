/*
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018
 * Alexey Sosnoviy <labotamy@yandex.ru>, Nikita Gryzlov <nixel2007@gmail.com>, Sergey Batanov <sergey.batanov@dmpas.ru>
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
package org.github._1c_syntax.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BSLLexerTest {

  private BSLLexer lexer = new BSLLexer(null);

  private void assertMatch(String inputString, Integer... expectedTokens) throws IOException {
    assertMatch(BSLLexer.DEFAULT_MODE, inputString, expectedTokens);
  }

  private void assertMatch(int mode, String inputString, Integer... expectedTokens) throws IOException {
    InputStream inputStream = IOUtils.toInputStream(inputString, Charset.forName("UTF-8"));
    CharStream input = CharStreams.fromStream(inputStream, Charset.forName("UTF-8"));
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
  void testUse() throws IOException {
    assertMatch(BSLLexer.PREPROCESSOR_MODE, "Использовать lib", BSLLexer.PREPROC_USE_KEYWORD, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch(BSLLexer.PREPROCESSOR_MODE, "Использовать \"lib\"", BSLLexer.PREPROC_USE_KEYWORD, BSLLexer.PREPROC_STRING);
  }

  @Test
  void testString() throws IOException {
    assertMatch("\"строка\"", BSLLexer.STRING);
    assertMatch("\"", BSLLexer.STRINGSTART);
    assertMatch("|aaa", BSLLexer.STRINGPART);
    assertMatch("|", BSLLexer.BAR);
    assertMatch("|\"", BSLLexer.STRINGTAIL);
    assertMatch("|aaa\"", BSLLexer.STRINGTAIL);
  }

  @Test
  void testAnnotation() throws IOException {
    assertMatch("&Аннотация", BSLLexer.AMPERSAND, BSLLexer.IDENTIFIER);
  }
}
