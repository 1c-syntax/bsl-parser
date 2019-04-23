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

  private void assertMatches(ParseTree tree) throws RecognitionException {

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

      if (((ParserRuleContext) tree).parent == null) {
        boolean parseSuccess = ((BSLLexer) parser.getInputStream().getTokenSource())._hitEOF;
        if (!parseSuccess) {
          throw new RecognitionException(
                  "Parse error EOF don't hit\n" + parser.getTokenStream().getText(),
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

  private void assertNotMatches(ParseTree tree) {
    assertThrows(RecognitionException.class, () -> assertMatches(tree));
  }

  @Test
  void testFile() {

    setInput("А;");
    assertMatches(parser.file());

    setInput("А; Перем А;");
    assertNotMatches(parser.file());

    setInput("Перем А; \n" +
             "Перем Б; \n" +
             "Сообщить();"
    );
    assertMatches(parser.file());

    setInput("Перем А; \n" +
            "Перем Б; \n" +
            "Процедура В()\n" +
            "КонецПроцедуры\n" +
            "Сообщить();\n"
    );
    assertMatches(parser.file());

    setInput("#!\n" +
            "#Если Сервер Тогда\n" +
            "Перем А; \n" +
            "Перем Б; \n" +
            "#Область Г\n" +
            "Процедура В()\n" +
            "КонецПроцедуры\n" +
            "#КонецОбласти\n" +
            "Сообщить();\n" +
            "#КонецЕсли\n"
    );
    assertMatches(parser.file());

  }

  @Test
  void testShebang(){

    setInput("#!");
    assertMatches(parser.shebang());

    setInput("#! А");
    assertMatches(parser.shebang());

    setInput("# А");
    assertNotMatches(parser.shebang());

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
  void testPreproc_if() {

    setInput("Если Клиент Тогда", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_if());

    setInput("Если", BSLLexer.PREPROCESSOR_MODE);
    assertNotMatches(parser.preproc_if());

  }

  @Test
  void testPreproc_elseif() {

    setInput("ИначеЕсли Клиент Тогда", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_elsif());

    setInput("ИначеЕсли", BSLLexer.PREPROCESSOR_MODE);
    assertNotMatches(parser.preproc_elsif());

  }

  @Test
  void testPreproc_else() {

    setInput("Иначе", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_else());

    setInput("ИначеЕсли", BSLLexer.PREPROCESSOR_MODE);
    assertNotMatches(parser.preproc_else());

  }

  @Test
  void testPreproc_endif() {

    setInput("КонецЕсли", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_endif());

    setInput("ИначеЕсли", BSLLexer.PREPROCESSOR_MODE);
    assertNotMatches(parser.preproc_endif());

  }

  @Test
  void testPreproc_Expression() {
    setInput("((((Не (ВебКлиент))) И ((НЕ МобильныйКлиент))))", BSLLexer.PREPROCESSOR_MODE);
  }

  @Test
  void testPreproc_symbol() {

    setInput("Клиент", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_symbol());

    setInput("НаКлиенте", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_symbol());

    setInput("НаСервере", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_symbol());

    setInput("МобильноеПриложениеКлиент", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_symbol());

    setInput("МобильноеПриложениеСервер", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_symbol());

    setInput("МобильныйКлиент", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_symbol());

    setInput("ТолстыйКлиентОбычноеПриложение", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_symbol());

    setInput("ТолстыйКлиентУправляемоеПриложение", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_symbol());

    setInput("Сервер", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_symbol());

    setInput("ВнешнееСоединение", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_symbol());

    setInput("ТонкийКлиент", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_symbol());

    setInput("ВебКлиент", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_symbol());

    setInput("Нечто", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_symbol());

    setInput("Нечто", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_unknownSymbol());

    setInput("Сервер", BSLLexer.PREPROCESSOR_MODE);
    assertNotMatches(parser.preproc_unknownSymbol());

  }

  @Test
  void TestPreproc_boolOperation() {

    setInput("И", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_boolOperation());

    setInput("ИЛИ", BSLLexer.PREPROCESSOR_MODE);
    assertMatches(parser.preproc_boolOperation());

    setInput("НЕ", BSLLexer.PREPROCESSOR_MODE);
    assertNotMatches(parser.preproc_boolOperation());

  }

  @Test
  void TestPreprocessor() {

    setInput("#Область А");
    assertMatches(parser.preprocessor());

    setInput("#КонецОбласти");
    assertMatches(parser.preprocessor());

    setInput("#Если А Тогда");
    assertMatches(parser.preprocessor());

    setInput("#ИначеЕсли А Тогда");
    assertMatches(parser.preprocessor());

    setInput("#Иначе");
    assertMatches(parser.preprocessor());

    setInput("#КонецЕсли");
    assertMatches(parser.preprocessor());

    setInput("#Использовать А");
    assertMatches(parser.preprocessor());

    setInput("#Просто");
    assertNotMatches(parser.preprocessor());

    setInput("Просто");
    assertNotMatches(parser.preprocessor());

  }

  @Test
  void TestCompilerDirectiveSymbol() {

    setInput("&НаКлиенте");
    assertMatches(parser.compilerDirective());

    setInput("&НаСервере");
    assertMatches(parser.compilerDirective());

    setInput("&НаСервереБезКонтекста");
    assertMatches(parser.compilerDirective());

    setInput("&НаКлиентеНаСервереБезКонтекста");
    assertMatches(parser.compilerDirective());

    setInput("&НаКлиентеНаСервере");
    assertMatches(parser.compilerDirective());

    setInput("&Аннотация");
    assertNotMatches(parser.compilerDirective());

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

    setInput("&НаСервере");
    assertNotMatches(parser.annotation());

    setInput("Аннотация()");
    assertNotMatches(parser.annotation());

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

    setInput("?(Истина, М, М)[0]");
    assertMatches(parser.complexIdentifier());

    setInput("?(Истина, С, С).Свойство");
    assertMatches(parser.complexIdentifier());

    setInput("А");
    assertMatches(parser.complexIdentifier());

    setInput("А()");
    assertMatches(parser.complexIdentifier());

    setInput("А.А()");
    assertMatches(parser.complexIdentifier());

    setInput("А[Б]");
    assertMatches(parser.complexIdentifier());

    setInput("Новый Массив");
    assertMatches(parser.complexIdentifier());

    setInput("Выполнить");
    assertNotMatches(parser.complexIdentifier());

  }

  @Test
  void testStatement() {

    setInput("Сообщить(А=1);");
    assertMatches(parser.statement());

    setInput("Если Истина Тогда Сообщить(А=1); F=0; КонецЕсли;");
    assertMatches(parser.statement());

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

    setInput("Выполнить (Б = А + 1);");
    assertMatches(parser.statement());
    setInput("А;");
    assertMatches(parser.statement());
    setInput("Модуль.Свойство;");
    assertMatches(parser.statement());
    setInput("Модуль.Метод();");
    assertMatches(parser.statement());
    setInput("А = Модуль.Метод();");
    assertMatches(parser.statement());
    setInput("А = Модуль.Метод() = Истина;");
    assertMatches(parser.statement());
    setInput("Сообщить();");
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
      "#EndRegion");
    assertMatches(parser.assignment());

    setInput("А = А");
    assertMatches(parser.assignment());
    setInput("А = А + Б[В]");
    assertMatches(parser.assignment());
    setInput("А = А + Б[В] * Метод()");
    assertMatches(parser.assignment());
    setInput("А = (А + Б[В] * Метод()) + Модуль.Метод()");
    assertMatches(parser.assignment());
    setInput("А = Модуль.Метод().Свойство");
    assertMatches(parser.assignment());
    setInput("А = Модуль.Метод(А).Свойство[А]");
    assertMatches(parser.assignment());
    setInput("А = Б = В.Метод(А)");
    assertMatches(parser.assignment());

    setInput("Модуль.Метод().Свойство[А]");
    assertNotMatches(parser.assignment());

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
  void testExpression() {

    setInput("Сообщить(А = 1)");
    assertMatches(parser.expression());

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

    setInput("A1 + \n" +
            "#Если (Клиент) Тогда\n" +
            "А +\n" +
            "#КонецЕсли\n" +
            "#Если Клиент Тогда\n" +
            "Б +\n" +
            "#Иначе\n" +
            "#Область Имя\n" +
            "В(\n" +
            "А + \n" +
            "Б\n" +
            ")\n" +
            "#КонецОбласти\n" +
            "#КонецЕсли\n" +
            "+ С\n");
    assertMatches(parser.expression());

    setInput("Метод()");
    assertMatches(parser.expression());
    setInput("Метод().Свойство");
    assertMatches(parser.expression());
    setInput("Модуль.Метод().Свойство");
    assertMatches(parser.expression());
    setInput("Модуль.Метод(А).Метод2(Б)");
    assertMatches(parser.expression());
    setInput("Модуль.Метод().Метод2().Свойство");
    assertMatches(parser.expression());
    setInput("Модуль.Метод().Метод2().Свойство.Метод()");
    assertMatches(parser.expression());
    setInput("Модуль.Метод().Метод2().Свойство.Метод()[1]");
    assertMatches(parser.expression());
    setInput("Идентификатор[1].Метод().Метод2().Свойство.Метод()[1]");
    assertMatches(parser.expression());
    setInput("Идентификатор.Свойство.Метод().Метод2().Свойство.Метод()[1]");
    assertMatches(parser.expression());

    setInput("Выполнить");
    assertNotMatches(parser.expression());
    setInput("А = Выполнить");
    assertNotMatches(parser.expression());

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

    setInput("А = 1");
    assertNotMatches(parser.compoundStatement());

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
    assertNotMatches(parser.doCall());

  }


  @Test
  void TestAccessProperty() {

    setInput(".А");
    assertMatches(parser.accessProperty());

    setInput("А.А");
    assertNotMatches(parser.accessProperty());

  }

  @Test
  void TestAccessIndex() {

    setInput("[А]");
    assertMatches(parser.accessIndex());

    setInput("А[A]");
    assertNotMatches(parser.accessIndex());

  }

  @Test
  void TestAccessCall() {

    setInput(".А(А)");
    assertMatches(parser.accessCall());

    setInput("[А]");
    assertNotMatches(parser.accessCall());

  }

  @Test
  void TestModifier() {

    setInput("[А]");
    assertMatches(parser.modifier());

    setInput(".А");
    assertMatches(parser.modifier());

    setInput(".А(А)");
    assertMatches(parser.modifier());

    setInput("А[A]");
    assertNotMatches(parser.modifier());

  }

  @Test
  void TestTypeName() {

    setInput("Массив");
    assertMatches(parser.typeName());

    setInput("Выполнить");
    assertNotMatches(parser.typeName());

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

    setInput("Новый(Тип(\"Массив\"), А, Б)");
    assertMatches(parser.newExpression());

    setInput("Новый(\"Массив\")");
    assertMatches(parser.newExpression());

    setInput("А");
    assertNotMatches(parser.newExpression());

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
    assertNotMatches(parser.member());

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
    assertNotMatches(parser.unaryModifier());

  }

  @Test
  void TestBoolOperation() {

    setInput("И");
    assertMatches(parser.boolOperation());

    setInput("ИЛИ");
    assertMatches(parser.boolOperation());

    setInput("НЕ");
    assertNotMatches(parser.boolOperation());

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
    assertNotMatches(parser.compareOperation());

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

  @Test
  void TestCallParam() {

    setInput("");
    assertMatches(parser.callParam());

    setInput("А");
    assertMatches(parser.callParam());

    setInput("НЕ А");
    assertMatches(parser.callParam());

    setInput("НЕ");
    assertNotMatches(parser.callParam());

    setInput("Если А Тогда");
    assertNotMatches(parser.callParam());

  }

  @Test
  void TestCallParamList() {

    setInput("НЕ А");
    assertMatches(parser.callParamList());

    setInput("НЕ А, А");
    assertMatches(parser.callParamList());

    setInput("НЕ, Если");
    assertNotMatches(parser.callParamList());

  }

  @Test
  void TestGlobalMethodCall() {

    setInput("Сообщить(А = 1)");
    assertMatches(parser.globalMethodCall());
    setInput("Сообщить(А + Б)");
    assertMatches(parser.globalMethodCall());
    setInput("Сообщить(Метод())");
    assertMatches(parser.globalMethodCall());

    setInput("Модуль.Сообщить()");
    assertNotMatches(parser.globalMethodCall());

  }

  @Test
  void TestMethodCall() {

    setInput("Сообщить()");
    assertMatches(parser.methodCall());
    setInput("Сообщить(А, 1)");
    assertMatches(parser.methodCall());

    setInput("Модуль.Сообщить()");
    assertNotMatches(parser.globalMethodCall());

  }

  @Test
  void TestCallStatement() {
    setInput("А");
    assertMatches(parser.callStatement());
    setInput("Сообщить(А, 1)");
    assertMatches(parser.callStatement());
    setInput("А.А");
    assertMatches(parser.callStatement());
    setInput("А.А()");
    assertMatches(parser.callStatement());
    setInput("А.А(А)");
    assertMatches(parser.callStatement());
    setInput("А(А).А");
    assertMatches(parser.callStatement());
    setInput("А(А).А.А().А");
    assertMatches(parser.callStatement());

    setInput("ВызватьИсключение А");
    assertNotMatches(parser.callStatement());
  }

}