/*
 * This file is a part of BSL Parser.
 *
 * Copyright (c) 2018-2024
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

import static org.assertj.core.api.Assertions.assertThat;

class BSLParserWithChildrenTest {

  private TestParser<BSLParser, BSLLexer> testParser;

  @BeforeEach
  void before() {
    testParser = new TestParser<>(BSLParser.class, BSLLexer.class);
  }

  @Test
  void testPreproc_Expression() {
    var content = testParser.assertThat("#Если (Клиент Или (НЕ Клиент)) И НЕ Клиент Тогда\n" +
      "#ИначеЕсли ((((Не (ВебКлиент))) И ((НЕ МобильныйКлиент)))) Тогда\n" +
      "#КонецЕсли");

    var file = testParser.parser().file();
    content.matches(file);

    var preprocessors = file.preprocessor();
    assertThat(preprocessors).isNotNull().hasSize(3);

    var preproc_if = preprocessors.get(0);
    var preproc_elif = preprocessors.get(1);
    var preproc_endif = preprocessors.get(2);
    content.matches(preproc_if.preproc_if());
    content.matches(preproc_if.preproc_if().preproc_expression());
    content.matches(preproc_elif.preproc_elsif());
    content.matches(preproc_elif.preproc_elsif().preproc_expression());
    content.matches(preproc_endif.preproc_endif());

    // в выражении условия все есть логическое условие
    var preproc_exp = preproc_if.preproc_if().preproc_expression().preproc_logicalExpression();
    content.matches(preproc_exp);
    // логическое условие содержит два операнда
    assertThat(preproc_exp.preproc_logicalOperand()).isNotNull().hasSize(2);

    var preproc_exp_inside = preproc_exp.preproc_logicalOperand(0);
    assertThat(preproc_exp_inside).isNotNull();
    // первый операнд это тоже логическое условие
    content.matches(preproc_exp_inside.preproc_logicalExpression());
  }

  @Test
  void testDeletePreproc() {
    var content = testParser.assertThat("""
      &ИзменениеИКонтроль("ПроверитьЗавершитьДоговорВАрхиве")
      Функция ХФ164150_ПроверитьЗавершитьДоговорВАрхиве(ДоговорОбъект, ДопНастройки, ИмяРеквизитаДатаОкончания)
      \tТекущаяДата   = НачалоДня(ТекущаяДатаСеанса());
      \t#Удаление
      \tЕсли СтароеУсловие
      \t#КонецУдаления
      \t#Вставка
      \tНовоеУсловие = Выражение;
      \tЕсли НовоеУсловие
      \t#КонецВставки
      \t\tИ ЧастьСтарогоУсловия Тогда
      \t\t    Возврат Истина;
      \tКонецЕсли;
      \tВозврат Ложь;
      КонецФункции""");

    var file = testParser.parser().file();
    content.matches(file);

    var subs = file.subs();
    content.matches(subs);

    var listSubs = subs.sub();
    listSubs.forEach(content::matches);

    var func = listSubs.get(0);
    content.matches(func);

    assertThat(func.getText())
      .doesNotContain("#Удаление")
      .doesNotContain("#КонецУдаления")
      .doesNotContain("#ЕслиСтароеУсловие")
      .doesNotContain("#Вставка")
      .doesNotContain("#КонецВставки")
      .contains("ИЧастьСтарогоУсловияТогда")
    ;
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      """
        &ИзменениеИКонтроль("Тест")
        Асинх Функция Тест(Параметры)
        \tВозврат Ложь;
        КонецФункции""",
      """
        &НаКлиенте
        Асинх Процедура Тест(Параметры)
        \tВозврат;
        КонецПроцедуры"""
    }
  )
  void testAsync(String inputString) {
    var content = testParser.assertThat(inputString);

    var file = testParser.parser().file();
    content.matches(file);

    var subs = file.subs();
    content.matches(subs);

    var listSubs = subs.sub();
    listSubs.forEach(content::matches);

    var method = listSubs.get(0);
    content.matches(method);

    if (method.function() == null) {
      var procDeclare = method.procedure().procDeclaration();
      content.matches(procDeclare.ASYNC_KEYWORD());
    } else {
      var funcDeclare = method.function().funcDeclaration();
      content.matches(funcDeclare.ASYNC_KEYWORD());
    }
  }

  @Test
  void testWait() {
    var content = testParser.assertThat("""
      Асинх Процедура Test()
      Ждать КопироватьФайлыАсинх(ИсходныйКаталог, ЦелевойКаталог); //1    \s
      КопироватьФайлы(ИсходныйКаталог, ЦелевойКаталог); //1    \s
      Файлы = Ждать НайтиФайлыАсинх(ИсхКаталог, "*", Ложь); //2
      Сч = Ждать КопироватьФайлыАсинх(ИсходныйКаталог, ЦелевойКаталог); //1
      Об = КопироватьФайлАсинх(ИсхФайл, ЦелФайл);\s
      Ждать Об;
      если Ждать Об тогда\s
        возврат;
      конецесли;
      если Ждать мояФункция(а) тогда\s
        возврат;
      конецесли;
      EndProcedure""");

    var file = testParser.parser().file();
    content.matches(file);

    var subs = file.subs();
    content.matches(subs);

    var listSubs = subs.sub();
    listSubs.forEach(content::matches);

    var proc = listSubs.get(0);
    content.matches(proc);

    var subCodeblock = proc.procedure().subCodeBlock();
    content.matches(subCodeblock);

    var codeBlock = subCodeblock.codeBlock();
    content.matches(codeBlock);

    var statements = codeBlock.statement();
    statements.forEach(content::matches);
    assertThat(statements.stream().filter(statementContext -> statementContext.callStatement() != null))
      .hasSize(1);
    assertThat(statements.stream().filter(statementContext -> statementContext.waitStatement() != null))
      .hasSize(2);
  }

  @Test
  void testAnotherWait() {
    var content = testParser.assertThat("""
      Асинх Функция Test()
      Ждать 1; \s
      Ждать (Ждать 1);\s
      Существует = Ждать ФайлНаДиске.СуществуетАсинх();
      Возврат Ждать (Ждать 1) + Ждать (Ждать 2);\s
      КонецФункции""");

    var file = testParser.parser().file();
    content.matches(file);

    var codeBlockContext = file.subs().sub(0).function().subCodeBlock().codeBlock();

    content.matches(codeBlockContext.statement(0).waitStatement());
    content.matches(codeBlockContext.statement(1).waitStatement());
    content.matches(codeBlockContext.statement(2).assignment());
    content.matches(codeBlockContext.statement(3).compoundStatement().returnStatement().expression().member(0)
      .waitExpression());
  }

  @Test
  void testNoWait() {
    var content = testParser.assertThat("""
      Процедура Test(Парам1, Ждать, wAit)
      Ждать = КопироватьФайлыАсинх(ИсходныйКаталог, ЦелевойКаталог, Ждать, wait); //1    \s
      если Ждать тогда\s
        возврат;
      конецесли;
      если Ждать > мояФункция(а) тогда\s
        возврат;
      конецесли;
      EndProcedure""");

    var file = testParser.parser().file();
    content.matches(file);

    var subs = file.subs();
    content.matches(subs);

    var listSubs = subs.sub();
    listSubs.forEach(content::matches);

    var proc = listSubs.get(0);
    content.matches(proc);

    var param = proc.procedure().procDeclaration().paramList().param(1);
    content.matches(param);

    var subCodeblock = proc.procedure().subCodeBlock();
    content.matches(subCodeblock);

    var codeBlock = subCodeblock.codeBlock();
    content.matches(codeBlock);

    var statements = codeBlock.statement();
    statements.forEach(content::matches);
  }

  @Test
  void testAnnotateParams() {
    var content = testParser.assertThat("""
      Процедура САннотированнымиПараметрами(
      \t
      \t&АннотацияДляПараметра
      \tЗнач Парам1,

      \t&АннотацияДляПараметра
      \t&АннотацияДляПараметра1
      \t&АннотацияДляПараметра2(СПараметрами = 3, 4, 5)
      \tЗнач Парам2,

      \tПарам3,
      \tПарам4 = Неопределено
      ) Экспорт

      КонецПроцедуры""");

    var file = testParser.parser().file();
    content.matches(file);
    assertThat(file.subs()).isNotNull();
    assertThat(file.subs().sub()).isNotNull().hasSize(1);

    var sub = file.subs().sub(0);
    content.matches(sub.procedure());
    content.matches(sub.procedure().procDeclaration());
    assertThat(sub.procedure().procDeclaration().paramList()).isNotNull();
    assertThat(sub.procedure().procDeclaration().paramList().param()).isNotNull().hasSize(4);

    var param1 = sub.procedure().procDeclaration().paramList().param(0);
    assertThat(param1.annotation()).isNotNull().hasSize(1);

    var param2 = sub.procedure().procDeclaration().paramList().param(1);
    assertThat(param2.annotation()).isNotNull().hasSize(3);

    var annotation2 = param2.annotation().get(2);
    assertThat(annotation2.annotationParams()).isNotNull();
    assertThat(annotation2.annotationParams().annotationParam()).isNotNull().hasSize(3);
  }

  @Test
  void testRaise() {
    var content = testParser.assertThat("ВызватьИсключение (\"Документ не может быть проведен\", " +
      "КатегорияОшибки.ОшибкаКонфигурации, " +
      "\"ERR.DOCS.0001\", " +
      "\"Клиенту запрещена отгрузка\");");

    var file = testParser.parser().file();
    content.matches(file);
    assertThat(file.fileCodeBlock()).isNotNull();
    assertThat(file.fileCodeBlock().codeBlock()).isNotNull();
    assertThat(file.fileCodeBlock().codeBlock().statement()).isNotNull().hasSize(1);

    var statement = file.fileCodeBlock().codeBlock().statement().get(0);
    assertThat(statement).isNotNull();
    assertThat(statement.compoundStatement()).isNotNull();
    assertThat(statement.compoundStatement().raiseStatement()).isNotNull();

    var raise = statement.compoundStatement().raiseStatement();
    assertThat(raise.expression()).isNull();
    assertThat(raise.doCall()).isNotNull();
    assertThat(raise.doCall().callParamList()).isNotNull();
    assertThat(raise.doCall().callParamList().callParam()).isNotNull().hasSize(4);
  }
}
