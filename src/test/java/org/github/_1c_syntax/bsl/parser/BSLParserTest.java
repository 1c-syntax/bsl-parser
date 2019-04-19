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
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BSLParserTest {
  private BSLParser parser = new BSLParser(null);
  private BSLLexer lexer = new BSLLexer(null);

  private void setInput(String inputString) {
    setInput(inputString, BSLLexer.DEFAULT_MODE);
  }

  private void setInput(String inputString, int mode) {
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
  void testPreproc_Expression() {
    setInput("((((Не (ВебКлиент))) И ((НЕ МобильныйКлиент))))", BSLLexer.PREPROCESSOR_MODE);
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

    setInput("?(Истина, М, М)(А)");
    assertMatches(parser.complexIdentifier());

    setInput("?(Истина, М, М)[0]");
    assertMatches(parser.complexIdentifier());

    setInput("?(Истина, С, С).Свойство");
    assertMatches(parser.complexIdentifier());

    setInput("А");
    assertMatches(parser.complexIdentifier());

    setInput("А()");
    assertMatches(parser.complexIdentifier());

    setInput("А[Б]");
    assertMatches(parser.complexIdentifier());

    setInput("Новый Массив");
    assertMatches(parser.complexIdentifier());

    setInput("Выполнить");
    assertThrows(RecognitionException.class, () -> assertMatches(parser.complexIdentifier()));

  }

  @Test
  void testStatement() {
    setInput("A = 0;");
    assertMatches(parser.statement());

    setInput("F = 0");
    assertMatches(parser.statement());

    setInput(";");
    assertMatches(parser.statement());

    setInput("~Метка: \n F = 0");
    assertMatches(parser.statement());

    setInput("~Метка: \n F = 0;");
    assertMatches(parser.statement());

    setInput("~Метка: \n ;");
    assertMatches(parser.statement());

    setInput("~Метка: \n");
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

  @Test
  void tesForEach() {
    setInput("Для каждого Переменная Из Коллекция Цикл\n" +
            "\t\n" +
            "КонецЦикла;");
    assertMatches(parser.forEachStatement());

    setInput("For Each varible In collection Do\n" +
            "\n" +
            "EndDo;");
    assertMatches(parser.forEachStatement());

  }

  @Test
  void tesLabel() {
    setInput("~Метка:");
    assertMatches(parser.label());
    setInput("~Если:");
    assertMatches(parser.label());

  }

  @Test
  void testHandler() {
    setInput("ДобавитьОбработчик Событие, Тест2;");
    assertMatches(parser.addHandlerStatement());
    setInput("ДобавитьОбработчик Параметр.Событие, Тест2;");
    assertMatches(parser.addHandlerStatement());
    setInput("УдалитьОбработчик Событие, Тест2;");
    assertMatches(parser.removeHandlerStatement());

  }

  @Test
  void testCompoundStatement() {

    setInput("Если А Тогда КонецЕсли");
    assertMatches(parser.compoundStatement());

    setInput("Пока А Цикл КонецЦикла");
    assertMatches(parser.compoundStatement());

    setInput("Для А = Б По В Цикл КонецЦикла");
    assertMatches(parser.compoundStatement());

    setInput("Для Каждого А Из Б Цикл КонецЦикла");
    assertMatches(parser.compoundStatement());

    setInput("Для Каждого А Из Б Цикл КонецЦикла");
    assertMatches(parser.compoundStatement());

    setInput("Попытка Исключение КонецПопытки");
    assertMatches(parser.compoundStatement());

    setInput("Возврат А");
    assertMatches(parser.compoundStatement());

    setInput("Продолжить");
    assertMatches(parser.compoundStatement());

    setInput("Прервать");
    assertMatches(parser.compoundStatement());

    setInput("ВызватьИсключение А");
    assertMatches(parser.compoundStatement());

    setInput("Выполнить А");
    assertMatches(parser.compoundStatement());

    setInput("Перейти ~А");
    assertMatches(parser.compoundStatement());

    setInput("Перейти ~А");
    assertMatches(parser.compoundStatement());

    setInput("ДобавитьОбработчик А, Б");
    assertMatches(parser.compoundStatement());

    setInput("УдалитьОбработчик А, Б");
    assertMatches(parser.compoundStatement());

    setInput("А");
    assertThrows(RecognitionException.class, () -> assertMatches(parser.compoundStatement()));

  }

  @Test
  void TestDoCall() {

    setInput("()");
    assertMatches(parser.doCall());

    setInput("(А)");
    assertMatches(parser.doCall());

    setInput("(А, Б)");
    assertMatches(parser.doCall());

    setInput("(А, Б, )");
    assertMatches(parser.doCall());

    setInput("(,)");
    assertMatches(parser.doCall());

    setInput("А()");
    assertThrows(RecognitionException.class, () -> assertMatches(parser.doCall()));

  }


  @Test
  void TestAccess_property() {

    setInput(".А");
    assertMatches(parser.access_property());

    setInput("А.А");
    assertThrows(RecognitionException.class, () -> assertMatches(parser.access_property()));

  }

  @Test
  void TestAccess_index() {

    setInput("[А]");
    assertMatches(parser.access_index());

    setInput("А[A]");
    assertThrows(RecognitionException.class, () -> assertMatches(parser.access_index()));

  }

  @Test
  void TestModifier() {

    setInput("[А]");
    assertMatches(parser.modifier());

    setInput(".А");
    assertMatches(parser.modifier());

    setInput("(А)");
    assertMatches(parser.modifier());

    setInput("А[A]");
    assertThrows(RecognitionException.class, () -> assertMatches(parser.modifier()));

  }

  @Test
  void TestTypeName() {

    setInput("Массив");
    assertMatches(parser.typeName());

    setInput("Выполнить");
    assertThrows(RecognitionException.class, () -> assertMatches(parser.typeName()));

  }

  @Test
  void TestNewExpression() {

    setInput("Новый Массив");
    assertMatches(parser.newExpression());

    setInput("Новый(Массив)");
    assertMatches(parser.newExpression());

    setInput("Новый Массив(А, Б)");
    assertMatches(parser.newExpression());

    setInput("Новый(Массив, А, Б)");
    assertMatches(parser.newExpression());

    setInput("А");
    assertThrows(RecognitionException.class, () -> assertMatches(parser.newExpression()));

  }

  @Test
  void TestMember() {

    setInput("Истина");
    assertMatches(parser.member());

    setInput("А");
    assertMatches(parser.member());

    setInput("(А)");
    assertMatches(parser.member());

    setInput("НЕ Истина");
    assertMatches(parser.member());

    setInput("НЕ А");
    assertMatches(parser.member());

    setInput("НЕ (А)");
    assertMatches(parser.member());

    setInput("Выполнить");
    assertThrows(RecognitionException.class, () -> assertMatches(parser.member()));

  }

  @Test
  void TestUnaryModifier() {

    setInput("НЕ");
    assertMatches(parser.unaryModifier());

    setInput("-");
    assertMatches(parser.unaryModifier());

    setInput("+");
    assertMatches(parser.unaryModifier());

    setInput("А");
    assertThrows(RecognitionException.class, () -> assertMatches(parser.unaryModifier()));

  }

  @Test
  void TestBoolOperation() {

    setInput("И");
    assertMatches(parser.boolOperation());

    setInput("ИЛИ");
    assertMatches(parser.boolOperation());

    setInput("НЕ");
    assertThrows(RecognitionException.class, () -> assertMatches(parser.boolOperation()));

  }

  @Test
  void TestCompareOperation() {

    setInput("<");
    assertMatches(parser.compareOperation());

    setInput("<=");
    assertMatches(parser.compareOperation());

    setInput(">");
    assertMatches(parser.compareOperation());

    setInput(">=");
    assertMatches(parser.compareOperation());

    setInput("=");
    assertMatches(parser.compareOperation());

    setInput("<>");
    assertMatches(parser.compareOperation());

    setInput("И");
    assertThrows(RecognitionException.class, () -> assertMatches(parser.compareOperation()));

  }

  @Test
  void TestOperation() {

    setInput("+");
    assertMatches(parser.operation());

    setInput("-");
    assertMatches(parser.operation());

    setInput("*");
    assertMatches(parser.operation());

    setInput("/");
    assertMatches(parser.operation());

    setInput("%");
    assertMatches(parser.operation());

    setInput(">");
    assertMatches(parser.operation());

    setInput("И");
    assertMatches(parser.operation());

    setInput("НЕ");
    assertNotMatches(parser.operation());

  }

}