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
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BSLParserTest {
  private BSLParser parser = new BSLParser(null);
  private BSLLexer lexer = new BSLLexer(null);

  private void setInput(String inputString) throws IOException {
    setInput(inputString, BSLLexer.DEFAULT_MODE);
  }

  private void setInput(String inputString, int mode) throws IOException {
    InputStream inputStream = IOUtils.toInputStream(inputString, Charset.forName("UTF-8"));
    CharStream input = CharStreams.fromStream(inputStream, Charset.forName("UTF-8"));
    lexer.setInputStream(input);
    lexer.mode(mode);

    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
    parser.setTokenStream(tokenStream);
  }

  private void assertMatches(ParseTree tree) {

    if (parser.getNumberOfSyntaxErrors() != 0) {
      throw new RecognitionException(
        "Syntax error while parsing:\n" + parser.getTokenStream().getText(),
        parser,
        parser.getInputStream(),
        parser.getContext()
      );
    }

    if (tree instanceof ParserRuleContext) {
      ParserRuleContext ctx = (ParserRuleContext) tree;
      if (ctx.exception != null) {
        throw ctx.exception;
      }
    }

    for (int i = 0; i < tree.getChildCount(); i++) {
      ParseTree child = tree.getChild(i);
      assertMatches(child);
    }
  }

  private void assertNotMatches(ParseTree tree) {
    assertThrows(RecognitionException.class, () -> assertMatches(tree));
  }

  @Test
  void testUse() throws IOException {
    setInput("Использовать lib", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.use());

    setInput("Использовать \"./lib\"", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.use());

    setInput("Использовать 1", BSLLexer.PREPROCESSOR_MODE);
    assertNotMatches(parser.use());
  }

  @Test
  void testExecute() throws IOException {
    setInput("Выполнить(\"\")");
    assertMatches(parser.executeStatement());

    setInput("Выполнить(\"строка\")");
    assertMatches(parser.executeStatement());

    setInput("Выполнить(Переменная)");
    assertMatches(parser.executeStatement());
  }
}