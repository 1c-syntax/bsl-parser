/*
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018-2019
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

  private void setInput(String inputString) {
    setInput(inputString, BSLLexer.DEFAULT_MODE);
  }

  private void setInput(String inputString, int mode) {
    InputStream inputStream = IOUtils.toInputStream(inputString, Charset.forName("UTF-8"));
    CharStream input;
    try {
      input = CharStreams.fromStream(inputStream, Charset.forName("UTF-8"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
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
  void testUse() {
    setInput("Использовать lib", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.use());

    setInput("Использовать \"./lib\"", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.use());

    setInput("Использовать 1", BSLLexer.PREPROCESSOR_MODE);
    assertNotMatches(parser.use());
  }

  @Test
  void testExecute() {
    setInput("Выполнить(\"\")");
    assertMatches(parser.executeStatement());

    setInput("Выполнить(\"строка\")");
    assertMatches(parser.executeStatement());

    setInput("Выполнить(Переменная)");
    assertMatches(parser.executeStatement());
  }

  @Test
  void moduleVar() {
    setInput("Перем ИмяПерем");
    assertMatches(parser.moduleVar());

    setInput("Перем ИмяПерем Экспорт");
    assertMatches(parser.moduleVar());

    setInput("Перем ИмяПерем1, ИмяПерем2");
    assertMatches(parser.moduleVar());

    setInput("Перем ИмяПерем1 Экспорт, ИмяПерем2 Экспорт");
    assertMatches(parser.moduleVar());

    setInput("&Аннотация\nПерем ИмяПерем");
    assertMatches(parser.moduleVar());

    setInput("&Аннотация\n&ВтораяАннотация\nПерем ИмяПерем");
    assertMatches(parser.moduleVar());

    setInput("&Аннотация\n#Область ИмяОбласти\n&ВтораяАннотация\nПерем ИмяПерем");
    assertMatches(parser.moduleVar());
  }

  @Test
  void testAnnotation() {
    setInput("&Аннотация");
    assertMatches(parser.annotation());

    setInput("&Аннотация()");
    assertMatches(parser.annotation());

    setInput("&Аннотация(П)");
    assertMatches(parser.annotation());

    setInput("&Аннотация(П = 0)");
    assertMatches(parser.annotation());

    setInput("&Аннотация(П = 0, П2 = Истина)");
    assertMatches(parser.annotation());

    setInput("&Аннотация(Истина, Ложь)");
    assertMatches(parser.annotation());

    setInput("&Аннотация(П = 0, П2, Истина, \"строка\", П3)");
    assertMatches(parser.annotation());
  }

  @Test
  void testExecuteStatement() {
    setInput("Выполнить(А)");
    assertMatches(parser.executeStatement());

    setInput("Выполнить А");
    assertMatches(parser.executeStatement());
  }

  @Test
  void testComplexIdentifier() {
    setInput("Запрос.Пустой()");
    assertMatches(parser.complexIdentifier());

    setInput("Запрос.Выполнить()");
    assertMatches(parser.complexIdentifier());

    setInput("?(Истина, Истина, Ложь).Выполнить()");
    assertMatches(parser.complexIdentifier());
  }

  @Test
  void testStatement() {
    setInput("A = 0;");
    assertMatches(parser.statement());

    setInput(";");
    assertMatches(parser.statement());
  }

  @Test
  void testAssignment() {
    setInput("A = \n" +
      "#Region Name\n" +
      "0 +\n" +
      "#EndRegion\n" +
      "1\n" +
      "#Region Name2\n" +
      "#Region Name2\n" +
      "+\n" +
      "#EndRegion\n" +
      "0\n" +
      ";\n" +
      "\n" +
      "#EndRegion");
    assertMatches(parser.expression());
  }

  @Test
  void testDefaultValue() {
    setInput("0");
    assertMatches(parser.defaultValue());

    setInput("-1");
    assertMatches(parser.defaultValue());

    setInput("+1");
    assertMatches(parser.defaultValue());

    setInput("ИСТИНА");
    assertMatches(parser.defaultValue());
  }

  @Test
  void testPreproc_symbol() {
    setInput("Клиент", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_symbol());

    setInput("ТолстыйКлиентОбычноеПриложение", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_symbol());

    setInput("Нечто", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_symbol());

  }

  @Test
  void testExpression() {
    setInput("A = 0");
    assertMatches(parser.expression());

    setInput("A = A + 1");
    assertMatches(parser.expression());

    setInput("A = +0");
    assertMatches(parser.expression());

    setInput("A = -0");
    assertMatches(parser.expression());

    setInput("A = 1 ++ 2");
    assertMatches(parser.expression());

    setInput("A = 1 -- 2");
    assertMatches(parser.expression());

    setInput("A = 1 +- 2");
    assertMatches(parser.expression());

    setInput("A = 1 -+ 2");
    assertMatches(parser.expression());
  }

}