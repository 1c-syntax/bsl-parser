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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class AbstractParserTest<P extends Parser, L extends Lexer> {
  protected P parser;
  private final Class<L> lexerClass;
  private final Class<P> parserClass;

  protected AbstractParserTest(Class<P> parserClass, Class<L> lexerClass) {
    this.lexerClass = lexerClass;
    this.parserClass = parserClass;
  }

  protected void setInput(String inputString) {
    setInput(inputString, Lexer.DEFAULT_MODE);
  }

  protected void setInput(String inputString, int mode) {
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

    var lexer = createLexer(input);
    lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
    lexer.mode(mode);

    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
    tokenStream.fill();

    parser = createParser(tokenStream);
    parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
  }

  private L createLexer(CharStream inputStream) {
    try {
      return lexerClass.getDeclaredConstructor(CharStream.class, boolean.class)
        .newInstance(inputStream, true);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  private P createParser(CommonTokenStream tokenStream) {
    try {
      return parserClass.getDeclaredConstructor(TokenStream.class)
        .newInstance(tokenStream);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  protected void assertMatches(ParseTree tree) throws RecognitionException {

    if (parser.getNumberOfSyntaxErrors() != 0) {
      throw new RecognitionException(
        "Syntax error while parsing:\n" + parser.getInputStream().getText(),
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

      if (((ParserRuleContext) tree).parent == null) {
        boolean parseSuccess = lexerClass.cast(parser.getInputStream().getTokenSource())._hitEOF;
        if (!parseSuccess) {
          throw new RecognitionException(
            "Parse error EOF don't hit\n" + parser.getInputStream().getText(),
            parser,
            parser.getInputStream(),
            parser.getContext()
          );
        }
      }
    }

    for (int i = 0; i < tree.getChildCount(); i++) {
      ParseTree child = tree.getChild(i);
      assertMatches(child);
    }
  }

  protected void assertNotMatches(ParseTree tree) {
    assertThat(tree).satisfiesAnyOf(
      (parseTree) -> assertThat(parseTree.getChildCount()).isEqualTo(0),
      (parseTree) -> assertThrows(RecognitionException.class, () -> assertMatches(tree))
    );
  }

  protected String getSourceFromFile(String filePath) {
    try {
      return FileUtils.readFileToString(
        new File(filePath),
        StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
      return "";
    }
  }
}
