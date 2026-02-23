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

import com.github._1c_syntax.bsl.parser.testing.TestParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BSLParserMatchesTest {

  private TestParser<BSLParser, BSLLexer> testParser;

  @BeforeEach
  void before() {
    testParser = new TestParser<>(BSLParser.class, BSLLexer.class);
  }

  @Test
  void testFile() {
    testParser.assertThat("А; Перем А;").noMatches(testParser.parser().file());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "Перем А; \nПерем Б; \nСообщить();",
      "Перем А; \nПерем Б; \nПроцедура В()\nКонецПроцедуры\nСообщить();\n",
      """
        #!
        #Если Сервер Тогда
        Перем А;\s
        Перем Б;\s
        #Область Г
        Процедура В()
        КонецПроцедуры
        #КонецОбласти
        Сообщить();
        #КонецЕсли
        """
    }
  )
  void testFile(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().file());
  }

  @Test
  void testShebang() {
    testParser.assertThat("#!").matches(testParser.parser().shebang());
    testParser.assertThat("#! А").matches(testParser.parser().shebang());
    testParser.assertThat("# А").noMatches(testParser.parser().shebang());
  }

  @Test
  void testNative() {
    testParser.assertThat("#native").matches(testParser.parser().preproc_native());
  }

  @Test
  void testNativeFile() {
    testParser.assertThat("""
      #native
      #Использовать lib
      #Использовать "."
      Перем А;"""
    ).matches(testParser.parser().file());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "#Использовать А", "#Использовать \".\"", "#native"
    }
  )
  void testModuleAnnotations(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().moduleAnnotations());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "#Использовать lib", "#Использовать \"./lib\"", "#Использовать lib-name", "#Использовать 1lib"
    }
  )
  void testUse(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().use());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "Если Клиент Тогда",
      "Если НЕ (ТонкийКлиент ИЛИ ВебКлиент) Тогда",
      "Если НЕ (НЕ ТонкийКлиент ИЛИ НЕ ВебКлиент) Тогда",
      "Если ТонкийКлиент И ВебКлиент Тогда",
      "Если MacOS ИЛИ Linux Тогда"
    }
  )
  void testPreproc_if(String inputString) {
    testParser.assertThat(BSLLexer.PREPROCESSOR_MODE, inputString).matches(testParser.parser().preproc_if());
  }

  @Test
  void testPreproc_if() {
    testParser.assertThat(BSLLexer.PREPROCESSOR_MODE, "Если").noMatches(testParser.parser().preproc_if());
  }

  @Test
  void testPreproc_elseif() {
    testParser.assertThat(BSLLexer.PREPROCESSOR_MODE, "ИначеЕсли Клиент Тогда")
      .matches(testParser.parser().preproc_elsif());
    testParser.assertThat(BSLLexer.PREPROCESSOR_MODE, "ИначеЕсли WINDOWS Тогда")
      .matches(testParser.parser().preproc_elsif());
    testParser.assertThat(BSLLexer.PREPROCESSOR_MODE, "ИначеЕсли")
      .noMatches(testParser.parser().preproc_elsif());
  }

  @Test
  void testPreproc_else() {
    testParser.assertThat(BSLLexer.PREPROCESSOR_MODE, "Иначе").matches(testParser.parser().preproc_else());
    testParser.assertThat(BSLLexer.PREPROCESSOR_MODE, "ИначеЕсли").noMatches(testParser.parser().preproc_else());
  }

  @Test
  void testPreproc_endif() {
    testParser.assertThat(BSLLexer.PREPROCESSOR_MODE, "КонецЕсли").matches(testParser.parser().preproc_endif());
    testParser.assertThat(BSLLexer.PREPROCESSOR_MODE, "ИначеЕсли").noMatches(testParser.parser().preproc_endif());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "Клиент", "НаКлиенте", "НаСервере", "МобильноеПриложениеКлиент", "МобильноеПриложениеСервер", "МобильныйКлиент",
      "МобильныйАвтономныйСервер", "ТолстыйКлиентОбычноеПриложение", "ТолстыйКлиентУправляемоеПриложение", "Сервер",
      "ВнешнееСоединение", "ТонкийКлиент", "ВебКлиент", "Вставка", "КонецВставки", "Удаление", "КонецУдаления",
      "WINdows", "Linux", "MacOS", "WINdows", "Linux", "MacOS"
    }
  )
  void testPreproc_symbol(String inputString) {
    testParser.assertThat(BSLLexer.PREPROCESSOR_MODE, inputString).matches(testParser.parser().preproc_symbol());
  }

  @Test
  void testPreproc_symbol() {
    testParser.assertThat(BSLLexer.PREPROCESSOR_MODE, "Сервер").noMatches(testParser.parser().preproc_unknownSymbol());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "Нечто", "MacОS", "MacОS"
    }
  )
  void testPreproc_unknownSymbol(String inputString) {
    testParser.assertThat(BSLLexer.PREPROCESSOR_MODE, inputString).matches(testParser.parser().preproc_unknownSymbol());
  }

  @Test
  void TestPreproc_boolOperation() {
    testParser.assertThat(BSLLexer.PREPROCESSOR_MODE, "И").matches(testParser.parser().preproc_boolOperation());
    testParser.assertThat(BSLLexer.PREPROCESSOR_MODE, "ИЛИ").matches(testParser.parser().preproc_boolOperation());
    testParser.assertThat(BSLLexer.PREPROCESSOR_MODE, "НЕ").noMatches(testParser.parser().preproc_boolOperation());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "#Область А", "#КонецОбласти", "#Если А Тогда", "#ИначеЕсли А Тогда", "#Иначе", "#КонецЕсли"
    }
  )
  void TestPreprocessor(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().preprocessor());
  }

  @Test
  void TestPreprocessor() {
    testParser.assertThat("#Просто").noMatches(testParser.parser().preprocessor());
    testParser.assertThat("Просто").noMatches(testParser.parser().preprocessor());
  }

  @Test
  void testPreprocInsertBreakCondition() {
    testParser.assertThat("""
      If true or false
          #If Server Then
              Or true
          #EndIf
          Then
      EndIf;""").matches(testParser.parser().ifBranch());

    testParser.assertThat("""
      while (true\s
          #If Server Then
              Or true
          #EndIf
          ) do
      enddo;""").matches(testParser.parser().whileStatement());

    testParser.assertThat("""
      a = false\s
          #If Server Then
              Or true
          #else
              Or false
          #EndIf
          and true;""").matches(testParser.parser().statement());
  }

  @Test
  void testPreprocBreakString() {
    testParser.assertThat("""
      "выбрать
      #Удаление
      |часть строки
      #КонецУдаления
      |конец строки\"""").matches(testParser.parser().multilineString());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "&НаКлиенте", "&НаСервере", "&НаСервереБезКонтекста", "&НаКлиентеНаСервереБезКонтекста", "&НаКлиентеНаСервере"
    }
  )
  void TestCompilerDirectiveSymbol(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().compilerDirective());
    testParser.assertThat("&Аннотация").noMatches(testParser.parser().compilerDirective());
  }

  @Test
  void TestCompilerDirectiveSymbol() {
    testParser.assertThat("&Аннотация").noMatches(testParser.parser().compilerDirective());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "Выполнить(\"\")", "Выполнить(\"строка\")", "Выполнить(Переменная)"
    }
  )
  void testExecute(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().executeStatement());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "Перем ИмяПерем", "Перем ИмяПерем Экспорт", "Перем ИмяПерем1, ИмяПерем2",
      "Перем ИмяПерем1 Экспорт, ИмяПерем2 Экспорт",
      "&Аннотация\nПерем ИмяПерем",
      "&Аннотация\n&ВтораяАннотация\nПерем ИмяПерем",
      "&Аннотация\n#Область ИмяОбласти\n&ВтораяАннотация\nПерем ИмяПерем",
      "&ДляКаждого(Значение = &Тип(\"Строка\"))\nПерем Параметр",
      "&ДляКаждого(&Тип(\"Число\", &Длина(10)))\nПерем Параметр",
      "&ДляКаждого(Значение = &Тип(\"Строка\"), &Тип(\"Число\", &Длина(10)))\nПерем Параметр"
    }
  )
  void moduleVar(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().moduleVar());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "&Аннотация", "&Аннотация()", "&Аннотация(П)", "&Аннотация(П = 0)",
      "&Аннотация(П = 0, П2 = Истина)",
      "&Аннотация(Истина, Ложь)",
      "&Аннотация(П = 0, П2, Истина, \"строка\", П3)",
      "&Аннотация\n#Область ИмяОбласти\n&ВтораяАннотация\nПерем ИмяПерем",
      "&Перед", "&Перед(Парам1 = 1)", "&После", "&После(\"РегламентноеЗадание1\")", "&Вместо", "&ИзменениеИКонтроль",
      "&Тип(&Строка)", "&Тип(&Строка())",
      "&ДляКаждого(Значение = &Тип(\"Строка\"))",
      "&ДляКаждого(&Тип(\"Число\", &Длина(10)))",
      "&ДляКаждого(Значение = &Тип(\"Строка\"), &Тип(\"Число\", &Длина(10)))"
    }
  )
  void testAnnotation(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().annotation());
  }

  @Test
  void testAnnotation() {
    testParser.assertThat("&НаСервере").noMatches(testParser.parser().annotation());
    testParser.assertThat("Аннотация()").noMatches(testParser.parser().annotation());
  }

  @Test
  void testExecuteStatement() {
    testParser.assertThat("Выполнить(А)").matches(testParser.parser().executeStatement());
    testParser.assertThat("Выполнить А").matches(testParser.parser().executeStatement());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "Запрос.Пустой()", "Запрос.Выполнить()", "Запрос. Выполнить()", "?(Истина, Истина, Ложь).Выполнить()",
      "?(Истина, М, М)[0]", "?(Истина, С, С).Свойство", "А", "А()", "А.А()", "А[Б]", "Новый Массив",
      "Новый(\"Файл\").Существует()"
    }
  )
  void testComplexIdentifier(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().complexIdentifier());
  }

  @Test
  void testComplexIdentifier() {
    testParser.assertThat("Выполнить").noMatches(testParser.parser().complexIdentifier());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "Сообщить(А=1);", "Если Истина Тогда Сообщить(А=1); F=0; КонецЕсли;",
      "A = 0;", "F = 0", ";", "~Метка: \n F = 0", "~Метка: \n F = 0;", "~Метка: \n ;", "~Метка: \n",
      "Выполнить (Б = А + 1);", "Модуль.Метод();", "А = Модуль.Метод();", "А = Модуль.Метод() = Истина;", "Сообщить();"
    }
  )
  void testStatement(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().statement());
  }

  @Test
  void testAssignment() {
    testParser.assertThat("""
      A =\s
      #Region Name
      0 +
      #EndRegion
      1
      #Region Name2
      #Region Name2
      +
      #EndRegion
      0
      #EndRegion""").matches(testParser.parser().assignment());

    testParser.assertThat("А.Свойство.Метод() = В.Метод(А)").noMatches(testParser.parser().assignment());
    testParser.assertThat("Модуль.Метод().Свойство[А]").noMatches(testParser.parser().assignment());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "А = А", "А = А + Б[В]", "А = А + Б[В] * Метод()", "А = (А + Б[В] * Метод()) + Модуль.Метод()",
      "А = Модуль.Метод().Свойство", "А = Модуль.Метод(А).Свойство[А]", "А = Б = В.Метод(А)",
      "А.Свойство[0] = В.Метод(А)", "А[0].Свойство = В.Метод(А)", "А.Метод()[0][1].Метод().Свойство = В.Метод(А)"
    }
  )
  void testAssignment(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().assignment());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "0", "-1", "+1", "ИСТИНА"
    }
  )
  void testDefaultValue(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().defaultValue());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "Сообщить(А = 1)",
      "A = 0", "A = A + 1", "A = +0", "A = -0", "A = 1 ++ 2", "A = 1 -- 2", "A = 1 +- 2", "A = 1 -+ 2",
      "Метод()", "Метод().Свойство", "Модуль.Метод().Свойство", "Модуль.Метод(А).Метод2(Б)",
      "Модуль.Метод().Метод2().Свойство", "Модуль.Метод().Метод2().Свойство.Метод()",
      "Модуль.Метод().Метод2().Свойство.Метод()[1]", "Идентификатор[1].Метод().Метод2().Свойство.Метод()[1]",
      "Идентификатор.Свойство.Метод().Метод2().Свойство.Метод()[1]",
      "Новый Файл().Существует()", "(Новый Файл()).Существует()"
    }
  )
  void testExpression(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().expression());
  }

  @Test
  void testExpression() {
    testParser.assertThat("""
      A1 +\s
      #Если (Клиент) Тогда
      А +
      #КонецЕсли
      #Если Клиент Тогда
      Б +
      #Иначе
      #Область Имя
      В(
      А +\s
      Б
      )
      #КонецОбласти
      #КонецЕсли
      + С
      """).matches(testParser.parser().expression());

    testParser.assertThat("Выполнить").noMatches(testParser.parser().expression());
  }

  @Test
  void tesForEach() {
    testParser.assertThat("""
      Для каждого Переменная Из Коллекция Цикл
      \t
      КонецЦикла;""").matches(testParser.parser().forEachStatement());

    testParser.assertThat("""
      For Each varible In collection Do
      
      EndDo;""").matches(testParser.parser().forEachStatement());
  }

  @Test
  void tesLabel() {
    testParser.assertThat("~Метка:").matches(testParser.parser().label());
    testParser.assertThat("~Если:").matches(testParser.parser().label());
  }

  @Test
  void testHandler() {
    testParser.assertThat("ДобавитьОбработчик Событие, Тест2;").matches(testParser.parser().addHandlerStatement());
    testParser.assertThat("ДобавитьОбработчик Параметр.Событие, Тест2;").matches(testParser.parser()
      .addHandlerStatement());

    testParser.assertThat("УдалитьОбработчик Событие, Тест2;").matches(testParser.parser().removeHandlerStatement());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "Если А Тогда КонецЕсли", "Пока А Цикл КонецЦикла", "Для А = Б По В Цикл КонецЦикла",
      "Для Каждого А Из Б Цикл КонецЦикла", "Для Каждого А Из Б Цикл КонецЦикла",
      "Попытка Исключение КонецПопытки", "Возврат А", "Продолжить", "Прервать", "ВызватьИсключение А",
      "Выполнить А", "Перейти ~А", "Перейти ~А", "ДобавитьОбработчик А, Б", "УдалитьОбработчик А, Б"
    }
  )
  void testCompoundStatement(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().compoundStatement());
  }

  @Test
  void testCompoundStatement() {
    testParser.assertThat("А = 1").noMatches(testParser.parser().compoundStatement());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "()", "(А)", "(А, Б)", "(А, Б, )", "(,)"
    }
  )
  void TestDoCall(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().doCall());
  }

  @Test
  void TestDoCall() {
    testParser.assertThat("А()").noMatches(testParser.parser().doCall());
  }

  @Test
  void TestAccessProperty() {
    testParser.assertThat(".А").matches(testParser.parser().accessProperty());
    testParser.assertThat("А.А").noMatches(testParser.parser().accessProperty());
  }

  @Test
  void TestAccessIndex() {
    testParser.assertThat("[А]").matches(testParser.parser().accessIndex());
    testParser.assertThat("А[A]").noMatches(testParser.parser().accessIndex());
  }

  @Test
  void TestAccessCall() {
    testParser.assertThat(".А(А)").matches(testParser.parser().accessCall());
    testParser.assertThat("[А]").noMatches(testParser.parser().accessCall());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "[А]", ".А", ".А(А)"
    }
  )
  void TestModifier(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().modifier());
  }

  @Test
  void TestModifier() {
    testParser.assertThat("А[A]").noMatches(testParser.parser().modifier());
  }

  @Test
  void TestTypeName() {
    testParser.assertThat("Массив").matches(testParser.parser().typeName());
    testParser.assertThat("Выполнить").noMatches(testParser.parser().typeName());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "Новый Массив", "Новый(Массив)", "Новый Массив(А, Б)", "Новый(Массив, А, Б)",
      "Новый(Тип(\"Массив\"), А, Б)", "Новый(\"Массив\")"
    }
  )
  void TestNewExpression(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().newExpression());
  }

  @Test
  void TestNewExpression() {
    testParser.assertThat("А").noMatches(testParser.parser().newExpression());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "Истина", "А", "(А)", "НЕ Истина", "НЕ А", "НЕ (А)"
    }
  )
  void TestMember(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().member());
  }

  @Test
  void TestMember() {
    testParser.assertThat("Выполнить").noMatches(testParser.parser().member());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "НЕ", "-", "+"
    }
  )
  void TestUnaryModifier(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().unaryModifier());
  }

  @Test
  void TestUnaryModifier() {
    testParser.assertThat("А").noMatches(testParser.parser().unaryModifier());
  }

  @Test
  void TestBoolOperation() {
    testParser.assertThat("И").matches(testParser.parser().boolOperation());
    testParser.assertThat("ИЛИ").matches(testParser.parser().boolOperation());
    testParser.assertThat("НЕ").noMatches(testParser.parser().boolOperation());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "<", "<=", ">", ">=", "=", "<>"
    }
  )
  void TestCompareOperation(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().compareOperation());
  }

  @Test
  void TestCompareOperation() {
    testParser.assertThat("И").noMatches(testParser.parser().compareOperation());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "+", "-", "*", "/", "%", ">", "И", "AND"
    }
  )
  void TestOperation(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().operation());
  }

  @Test
  void TestOperation() {
    testParser.assertThat("НЕ").noMatches(testParser.parser().operation());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "", "А", "НЕ А"
    }
  )
  void TestCallParam(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().callParam());
  }

  @Test
  void TestCallParam() {
    testParser.assertThat("НЕ").noMatches(testParser.parser().callParam());
    testParser.assertThat("Если А Тогда").noMatches(testParser.parser().callParam());
  }

  @Test
  void TestCallParamList() {
    testParser.assertThat("НЕ А").matches(testParser.parser().callParamList());
    testParser.assertThat("НЕ А, А").matches(testParser.parser().callParamList());
    testParser.assertThat("НЕ, Если").noMatches(testParser.parser().callParamList());
  }

  @Test
  void TestGlobalMethodCall() {
    testParser.assertThat("Сообщить(А = 1)").matches(testParser.parser().globalMethodCall());
    testParser.assertThat("Сообщить(А + Б)").matches(testParser.parser().globalMethodCall());
    testParser.assertThat("Сообщить(Метод())").matches(testParser.parser().globalMethodCall());
    testParser.assertThat("Модуль.Сообщить()").noMatches(testParser.parser().globalMethodCall());
  }

  @Test
  void TestMethodCall() {
    testParser.assertThat("Сообщить()").matches(testParser.parser().methodCall());
    testParser.assertThat("Сообщить(А, 1)").matches(testParser.parser().methodCall());
    testParser.assertThat("Модуль.Сообщить()").noMatches(testParser.parser().globalMethodCall());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "Сообщить(А, 1)", "А.А[1].А(А)", "А.А()", "А.А(А)", "А(А).А()", "А(А).А.А().А()"
    }
  )
  void TestCallStatement(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().callStatement());
  }

  @Test
  void TestCallStatement() {
    testParser.assertThat("ВызватьИсключение А").noMatches(testParser.parser().callStatement());
  }

  @Test
  void TestTryStatement() {
    testParser.assertThat("Попытка Исключение КонецПопытки").matches(testParser.parser().tryStatement());
    testParser.assertThat("Попытка A = 1; Исключение B = 2; КонецПопытки")
      .matches(testParser.parser().tryStatement());
  }
}
