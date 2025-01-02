/*
 * This file is a part of BSL Parser.
 *
 * Copyright (c) 2018-2025
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

import com.github._1c_syntax.bsl.parser.testing.ResourceUtils;
import com.github._1c_syntax.bsl.parser.testing.TestLexer;
import org.antlr.v4.runtime.Token;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class BSLLexerTest {

  private static final Pattern PROCEDURE_PATTERN = Pattern.compile("^процедура\\s",
    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
  private static final Pattern FUNCTION_PATTERN = Pattern.compile("^Функция\\s",
    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);

  private TestLexer<BSLLexer> testLexer;

  @BeforeEach
  void before() {
    testLexer = new TestLexer<>(BSLLexer.class);
  }

  @Test
  void testWhitespaces() {
    var inputString = "   А";
    testLexer.assertThat(BSLLexer.DEFAULT_MODE, inputString)
      .containsExactly(BSLLexer.WHITE_SPACE, BSLLexer.IDENTIFIER, BSLLexer.EOF);
    testLexer.assertThat(BSLLexer.ANNOTATION_MODE, inputString)
      .containsExactly(BSLLexer.WHITE_SPACE, BSLLexer.ANNOTATION_CUSTOM_SYMBOL, BSLLexer.EOF);
    testLexer.assertThat(BSLLexer.PREPROCESSOR_MODE, inputString)
      .containsExactly(BSLLexer.WHITE_SPACE, BSLLexer.PREPROC_IDENTIFIER, BSLLexer.EOF);
    testLexer.assertThat(BSLLexer.REGION_MODE, inputString)
      .containsExactly(BSLLexer.WHITE_SPACE, BSLLexer.PREPROC_IDENTIFIER, BSLLexer.EOF);
    testLexer.assertThat(BSLLexer.USE_MODE, inputString)
      .containsExactly(BSLLexer.WHITE_SPACE, BSLLexer.PREPROC_IDENTIFIER, BSLLexer.EOF);
    testLexer.assertThat(BSLLexer.DOT_MODE, inputString)
      .containsExactly(BSLLexer.WHITE_SPACE, BSLLexer.IDENTIFIER, BSLLexer.EOF);
  }

  @Test
  void testBOM() {
    testLexer.assertThat('\uFEFF' + "Процедура").containsAll(BSLLexer.PROCEDURE_KEYWORD);
  }

  @Test
  void testCRCR() {
    testLexer.assertThat("\r\n\r\r\n")
      .tokenOnLine(0, 1)
      .tokenOnLine(1, 4);
  }

  @Test
  void testUse() {
    testLexer.assertThat(BSLLexer.PREPROCESSOR_MODE, "Использовать lib")
      .containsAll(BSLLexer.PREPROC_USE_KEYWORD, BSLLexer.PREPROC_IDENTIFIER);
    testLexer.assertThat(BSLLexer.PREPROCESSOR_MODE, "Использовать \"lib\"")
      .containsAll(BSLLexer.PREPROC_USE_KEYWORD, BSLLexer.PREPROC_STRING);
    testLexer.assertThat(BSLLexer.PREPROCESSOR_MODE, "Использовать lib-name")
      .containsAll(BSLLexer.PREPROC_USE_KEYWORD, BSLLexer.PREPROC_IDENTIFIER);
    testLexer.assertThat(BSLLexer.PREPROCESSOR_MODE, "Использовать 1lib")
      .containsAll(BSLLexer.PREPROC_USE_KEYWORD, BSLLexer.PREPROC_IDENTIFIER)
      .tokenHasText(2, "1lib");
  }

  @Test
  void testPreproc_LineComment() {
    testLexer.assertThat("#КонецОбласти // Концевой комментарий")
      .containsAll(BSLLexer.HASH, BSLLexer.PREPROC_END_REGION);
  }

  @Test
  void testPreproc_Region() {
    testLexer.assertThat("#Область ИмяОбласти")
      .isEqualTo("#Region Name")
      .containsAll(BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    testLexer.assertThat("#Область МобильныйКлиент")
      .isEqualTo("#Region mobileappclient")
      .containsAll(BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    testLexer.assertThat("#Область Область")
      .isEqualTo("#Region Region")
      .containsAll(BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    testLexer.assertThat("#Область КонецОбласти")
      .isEqualTo("#Region EndRegion")
      .containsAll(BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    testLexer.assertThat("#Область НЕ")
      .isEqualTo("#Region NOT")
      .containsAll(BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    testLexer.assertThat("#Область ИЛИ")
      .isEqualTo("#Region OR")
      .containsAll(BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    testLexer.assertThat("#Область И")
      .isEqualTo("#Region AND")
      .containsAll(BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    testLexer.assertThat("#Область Если")
      .isEqualTo("#Region IF")
      .containsAll(BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    testLexer.assertThat("#Область Тогда")
      .isEqualTo("#Region Then")
      .containsAll(BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    testLexer.assertThat("#Область ИначеЕсли")
      .isEqualTo("#Region ElsIf")
      .containsAll(BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    testLexer.assertThat("#Область Иначе")
      .isEqualTo("#Region Else")
      .containsAll(BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    testLexer.assertThat("#Область КонецЕсли")
      .isEqualTo("#Region EndIf")
      .containsAll(BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
  }

  @Test
  void testString() {
    testLexer.assertThat("\"строка\"").containsAll(BSLLexer.STRING);
    testLexer.assertThat("\"").containsAll(BSLLexer.STRINGSTART);
    testLexer.assertThat("|aaa").containsAll(BSLLexer.STRINGPART);
    testLexer.assertThat("|").containsAll(BSLLexer.BAR);
    testLexer.assertThat("|\"").containsAll(BSLLexer.STRINGTAIL);
    testLexer.assertThat("|aaa\"").containsAll(BSLLexer.STRINGTAIL);
    testLexer.assertThat("А = \"строка\" + \"строка\";")
      .containsAll(
        BSLLexer.IDENTIFIER,
        BSLLexer.ASSIGN,
        BSLLexer.STRING,
        BSLLexer.PLUS,
        BSLLexer.STRING,
        BSLLexer.SEMICOLON
      );
    testLexer.assertThat("\"\"\"\"").containsAll(BSLLexer.STRING);
    testLexer.assertThat("|СПЕЦСИМВОЛ \"\"~\"\"\"").containsAll(BSLLexer.STRINGTAIL);
    testLexer.assertThat("\"Минимальная версия платформы \"\"1С:Предприятие 8\"\" указана выше рекомендуемой.")
      .containsAll(BSLLexer.STRINGSTART);
    testLexer.assertThat("А = \" \n | А \"\"\"\" + А \n  |\";")
      .containsAll(
        BSLLexer.IDENTIFIER,
        BSLLexer.ASSIGN,
        BSLLexer.STRINGSTART,
        BSLLexer.STRINGPART,
        BSLLexer.STRINGTAIL,
        BSLLexer.SEMICOLON
      );
  }

  @Test
  void testAnnotation() {
    testLexer.assertThat("&НаСервере").containsAll(BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATSERVER_SYMBOL);
    testLexer.assertThat("&НаКлиентеНаСервере")
      .containsAll(BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATCLIENTATSERVER_SYMBOL);
    testLexer.assertThat("&Аннотация").containsAll(BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_CUSTOM_SYMBOL);
    testLexer.assertThat("&НаСервере &Аннотация &НаСервере")
      .containsAll(
        BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATSERVER_SYMBOL,
        BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_CUSTOM_SYMBOL,
        BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATSERVER_SYMBOL
      );
    testLexer.assertThat("&НаСервере\n&Аннотация\n&НаСервере")
      .containsAll(
        BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATSERVER_SYMBOL,
        BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_CUSTOM_SYMBOL,
        BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATSERVER_SYMBOL
      );
    testLexer.assertThat("&НаСервере").containsAll(BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATSERVER_SYMBOL);
  }

  @Test
  void testProcedure() {
    testLexer.assertThat("Процедура").containsAll(BSLLexer.PROCEDURE_KEYWORD);
    testLexer.assertThat("Поле.Процедура").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testFunction() {
    testLexer.assertThat("Функция").containsAll(BSLLexer.FUNCTION_KEYWORD);
    testLexer.assertThat("Поле.Функция").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testTo() {
    testLexer.assertThat("По").containsAll(BSLLexer.TO_KEYWORD);
    testLexer.assertThat("Поле.По").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testExecute() {
    testLexer.assertThat("Выполнить").containsAll(BSLLexer.EXECUTE_KEYWORD);
    testLexer.assertThat("Запрос.Выполнить")
      .isEqualTo("Запрос.  Выполнить")
      .isEqualTo("Запрос.  \nВыполнить")
      .containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testTry() {
    testLexer.assertThat("Попытка").containsAll(BSLLexer.TRY_KEYWORD);
    testLexer.assertThat("Поле.Попытка").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testBreak() {
    testLexer.assertThat("Прервать").containsAll(BSLLexer.BREAK_KEYWORD);
    testLexer.assertThat("Поле.Прервать").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testNew() {
    testLexer.assertThat("Новый").containsAll(BSLLexer.NEW_KEYWORD);
    testLexer.assertThat("Поле.Новый").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testElse() {
    testLexer.assertThat("Иначе")
      .isEqualTo("ИНАЧЕ")
      .isEqualTo("ИнАчЕ")
      .containsAll(BSLLexer.ELSE_KEYWORD);
    testLexer.assertThat("Поле.Иначе").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testIn() {
    testLexer.assertThat("Из")
      .isEqualTo("In")
      .containsAll(BSLLexer.IN_KEYWORD);
    testLexer.assertThat("Поле.Из").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testMark() {
    testLexer.assertThat("~Метка")
      .isEqualTo("~Если")
      .isEqualTo("~Тогда")
      .isEqualTo("~ИначеЕсли")
      .isEqualTo("~Иначе")
      .isEqualTo("~КонецЕсли")
      .isEqualTo("~Для")
      .isEqualTo("~Каждого")
      .isEqualTo("~Из")
      .isEqualTo("~По")
      .isEqualTo("~Пока")
      .isEqualTo("~Цикл")
      .isEqualTo("~КонецЦикла")
      .isEqualTo("~Процедура")
      .isEqualTo("~Функция")
      .isEqualTo("~КонецПроцедуры")
      .isEqualTo("~КонецФункции")
      .isEqualTo("~Перем")
      .isEqualTo("~Перейти")
      .isEqualTo("~Возврат")
      .isEqualTo("~Продолжить")
      .isEqualTo("~Прервать")
      .isEqualTo("~И")
      .isEqualTo("~Или")
      .isEqualTo("~Не")
      .isEqualTo("~Попытка")
      .isEqualTo("~Исключение")
      .isEqualTo("~ВызватьИсключение")
      .isEqualTo("~КонецПопытки")
      .isEqualTo("~Новый")
      .isEqualTo("~Выполнить")
      .containsAll(BSLLexer.TILDA, BSLLexer.IDENTIFIER);
  }

  @Test
  void testHandlers() {
    testLexer.assertThat("ДобавитьОбработчик")
      .isEqualTo("AddHandler")
      .containsAll(BSLLexer.ADDHANDLER_KEYWORD);
    testLexer.assertThat("УдалитьОбработчик")
      .isEqualTo("RemoveHandler")
      .containsAll(BSLLexer.REMOVEHANDLER_KEYWORD);
  }

  @Test
  void testKeyWords() {
    testLexer.assertThat("ИСТиНА").isEqualTo("TRuE").containsAll(BSLLexer.TRUE);
    testLexer.assertThat("Поле.ИСТИНА")
      .isEqualTo("Field.TRUE").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ЛоЖЬ").isEqualTo("FaLSE").containsAll(BSLLexer.FALSE);
    testLexer.assertThat("Поле.ЛОЖЬ")
      .isEqualTo("Field.FALSE").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("НеопределенО").isEqualTo("UNDEFINeD").containsAll(BSLLexer.UNDEFINED);
    testLexer.assertThat("Поле.НЕОПРЕДЕЛЕНО")
      .isEqualTo("Field.UNDEFINED").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("NUlL").containsAll(BSLLexer.NULL);
    testLexer.assertThat("Поле.NULL")
      .isEqualTo("Field.NULL").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ПРОЦЕДУрА").isEqualTo("PROCEDUrE").containsAll(BSLLexer.PROCEDURE_KEYWORD);
    testLexer.assertThat("Поле.ПРОЦЕДУРА")
      .isEqualTo("Field.PROCEDURE").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("фУНКЦИя").isEqualTo("fUNCTIOn").containsAll(BSLLexer.FUNCTION_KEYWORD);
    testLexer.assertThat("Поле.ФУНКЦИЯ")
      .isEqualTo("Field.FUNCTION").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("КОНЕЦПРОЦЕДУРы")
      .isEqualTo("ENDPROCEDURe").containsAll(BSLLexer.ENDPROCEDURE_KEYWORD);
    testLexer.assertThat("Поле.КОНЕЦПРОЦЕДУРЫ")
      .isEqualTo("Field.ENDPROCEDURE").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("КОНЕЦФУНКЦИИ").isEqualTo("ENDFUNCTION").containsAll(BSLLexer.ENDFUNCTION_KEYWORD);
    testLexer.assertThat("Поле.КОНЕЦФУНКЦИИ")
      .isEqualTo("Field.ENDFUNCTION").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ЭКСПОРТ").isEqualTo("EXPORT").containsAll(BSLLexer.EXPORT_KEYWORD);
    testLexer.assertThat("Поле.ЭКСПОРТ")
      .isEqualTo("Field.EXPORT").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ЗНАЧ").isEqualTo("VAL").containsAll(BSLLexer.VAL_KEYWORD);
    testLexer.assertThat("Поле.ЗНАЧ")
      .isEqualTo("Field.VAL").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("КОНЕЦЕСЛи").isEqualTo("Endif").containsAll(BSLLexer.ENDIF_KEYWORD);
    testLexer.assertThat("Поле.КОНЕЦЕСЛИ")
      .isEqualTo("Field.ENDIF").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("КОНЕЦЦИКЛа").isEqualTo("ENDDo").containsAll(BSLLexer.ENDDO_KEYWORD);
    testLexer.assertThat("Поле.КОНЕЦЦИКЛА")
      .isEqualTo("Field.ENDDO").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ЕСЛи").isEqualTo("If").containsAll(BSLLexer.IF_KEYWORD);
    testLexer.assertThat("Поле.ЕСЛИ")
      .isEqualTo("Field.IF").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ИНАЧЕЕСЛи").isEqualTo("ELSIf").containsAll(BSLLexer.ELSIF_KEYWORD);
    testLexer.assertThat("Поле.ИНАЧЕЕСЛИ")
      .isEqualTo("Field.ELSIF").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ИНАЧе").isEqualTo("ELSe").containsAll(BSLLexer.ELSE_KEYWORD);
    testLexer.assertThat("Поле.ИНАЧЕ")
      .isEqualTo("Field.ELSE").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ТОГДа").isEqualTo("THEn").containsAll(BSLLexer.THEN_KEYWORD);
    testLexer.assertThat("Поле.ТОГДА")
      .isEqualTo("Field.THEN").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ПОКа").isEqualTo("WHILe").containsAll(BSLLexer.WHILE_KEYWORD);
    testLexer.assertThat("Поле.ПОКА")
      .isEqualTo("Field.WHILE").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ЦИКл").isEqualTo("Do").containsAll(BSLLexer.DO_KEYWORD);
    testLexer.assertThat("Поле.ЦИКЛ")
      .isEqualTo("Field.DO").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ДЛя").isEqualTo("FOr").containsAll(BSLLexer.FOR_KEYWORD);
    testLexer.assertThat("Поле.ДЛЯ")
      .isEqualTo("Field.FOR").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("По").isEqualTo("To").containsAll(BSLLexer.TO_KEYWORD);
    testLexer.assertThat("Поле.ПО")
      .isEqualTo("Field.TO").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("КАЖДОГо").isEqualTo("EAcH").containsAll(BSLLexer.EACH_KEYWORD);
    testLexer.assertThat("Поле.КАЖДОГО")
      .isEqualTo("Field.EACH").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("Из").isEqualTo("In").containsAll(BSLLexer.IN_KEYWORD);
    testLexer.assertThat("Поле.ИЗ")
      .isEqualTo("Field.IN").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ПОПЫТКа").isEqualTo("TRy").containsAll(BSLLexer.TRY_KEYWORD);
    testLexer.assertThat("Поле.ПОПЫТКА")
      .isEqualTo("Field.TRY").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ИСКЛЮЧЕНИе").isEqualTo("EXCEPt").containsAll(BSLLexer.EXCEPT_KEYWORD);
    testLexer.assertThat("Поле.ИСКЛЮЧЕНИЕ")
      .isEqualTo("Field.EXCEPT").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("КОНЕЦПОПЫТКи").isEqualTo("ENDTRy").containsAll(BSLLexer.ENDTRY_KEYWORD);
    testLexer.assertThat("Поле.КОНЕЦПОПЫТКИ")
      .isEqualTo("Field.ENDTRY").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ВОЗВРАт").isEqualTo("RETURn").containsAll(BSLLexer.RETURN_KEYWORD);
    testLexer.assertThat("Поле.ВОЗВРАТ")
      .isEqualTo("Field.RETURN").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ПРОДОЛЖИТь").isEqualTo("CONTINUe").containsAll(BSLLexer.CONTINUE_KEYWORD);
    testLexer.assertThat("Поле.ПРОДОЛЖИТЬ")
      .isEqualTo("Field.CONTINUE").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ВЫЗВАТЬИСКЛЮЧЕНИе").isEqualTo("RAISe").containsAll(BSLLexer.RAISE_KEYWORD);
    testLexer.assertThat("Поле.ВЫЗВАТЬИСКЛЮЧЕНИЕ")
      .isEqualTo("Field.RAISE").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ПЕРЕм").isEqualTo("VAr").containsAll(BSLLexer.VAR_KEYWORD);
    testLexer.assertThat("Поле.ПЕРЕМ")
      .isEqualTo("Field.VAR").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("Не").isEqualTo("NOt").containsAll(BSLLexer.NOT_KEYWORD);
    testLexer.assertThat("Поле.НЕ")
      .isEqualTo("Field.NOT").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ИЛи").isEqualTo("Or").containsAll(BSLLexer.OR_KEYWORD);
    testLexer.assertThat("Поле.ИЛИ")
      .isEqualTo("Field.OR").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("и").isEqualTo("ANd").containsAll(BSLLexer.AND_KEYWORD);
    testLexer.assertThat("Поле.И")
      .isEqualTo("Field.AND").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("НОВЫй").isEqualTo("NEw").containsAll(BSLLexer.NEW_KEYWORD);
    testLexer.assertThat("Поле.НОВЫЙ")
      .isEqualTo("Field.NEW").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ПЕРЕЙТи").isEqualTo("GOTo").containsAll(BSLLexer.GOTO_KEYWORD);
    testLexer.assertThat("Поле.ПЕРЕЙТИ")
      .isEqualTo("Field.GOTO").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ПРЕРВАТь").isEqualTo("BREAk").containsAll(BSLLexer.BREAK_KEYWORD);
    testLexer.assertThat("Поле.ПРЕРВАТЬ")
      .isEqualTo("Field.BREAK").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ВЫПОЛНИТь").isEqualTo("EXECUTe").containsAll(BSLLexer.EXECUTE_KEYWORD);
    testLexer.assertThat("Поле.ВЫПОЛНИТЬ")
      .isEqualTo("Field.EXECUTE").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ДОБАВИТЬОБРАБОТЧИк")
      .isEqualTo("ADDHANDLEr").containsAll(BSLLexer.ADDHANDLER_KEYWORD);
    testLexer.assertThat("Поле.ДОБАВИТЬОБРАБОТЧИК")
      .isEqualTo("Field.ADDHANDLER").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("УДАЛИТЬОБРАБОТЧИк")
      .isEqualTo("REMOVEHANDLEr").containsAll(BSLLexer.REMOVEHANDLER_KEYWORD);
    testLexer.assertThat("Поле.УДАЛИТЬОБРАБОТЧИК")
      .isEqualTo("Field.REMOVEHANDLER").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("АсинХ").isEqualTo("ASYNc").containsAll(BSLLexer.ASYNC_KEYWORD);
    testLexer.assertThat("Поле.Асинх")
      .isEqualTo("Field.ASYNC").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("ЖдатЬ").isEqualTo("aWAIt").containsAll(BSLLexer.IDENTIFIER);
    testLexer.assertThat("Поле.ждАть")
      .isEqualTo("Field.aWaIT").containsAll(BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    testLexer.assertThat("асинх функция а() ЖдатЬ Б(); КонецФункции")
      .isEqualTo("async function a() await b(); endfunction")
      .containsAll(
        BSLLexer.ASYNC_KEYWORD,
        BSLLexer.FUNCTION_KEYWORD,
        BSLLexer.IDENTIFIER,
        BSLLexer.LPAREN,
        BSLLexer.RPAREN,
        BSLLexer.AWAIT_KEYWORD,
        BSLLexer.IDENTIFIER,
        BSLLexer.LPAREN,
        BSLLexer.RPAREN,
        BSLLexer.SEMICOLON,
        BSLLexer.ENDFUNCTION_KEYWORD
      );
  }

  @Test
  void testPreproc_DeleteInsert() {
    testLexer.assertThat("#Insert\n", BSLLexer.HIDDEN)
      .containsAll(BSLLexer.PREPROC_INSERT, BSLLexer.WHITE_SPACE);
    testLexer.assertThat("#КонецВставки\n", BSLLexer.HIDDEN)
      .containsAll(BSLLexer.PREPROC_ENDINSERT, BSLLexer.WHITE_SPACE);
    testLexer.assertThat("#Удаление\n", BSLLexer.PREPROC_DELETE_CHANNEL).containsAll(BSLLexer.PREPROC_DELETE);
    testLexer.assertThat(BSLLexer.PREPROC_DELETE_MODE, "#EndDelete\n", BSLLexer.PREPROC_DELETE_CHANNEL)
      .containsAll(BSLLexer.PREPROC_ENDDELETE);
    testLexer.assertThat("#Удаление\r\n", BSLLexer.PREPROC_DELETE_CHANNEL).containsAll(BSLLexer.PREPROC_DELETE);
    testLexer.assertThat("#Удаление\r\n#EndDelete\n", BSLLexer.PREPROC_DELETE_CHANNEL)
      .containsAll(BSLLexer.PREPROC_DELETE, BSLLexer.PREPROC_ENDDELETE);
    testLexer.assertThat(BSLLexer.PREPROC_DELETE_MODE, "#EndDelete\r\n", BSLLexer.PREPROC_DELETE_CHANNEL)
      .containsAll(BSLLexer.PREPROC_ENDDELETE);
    testLexer.assertThat("# Удаление \r\n f", BSLLexer.PREPROC_DELETE_CHANNEL)
      .containsAll(BSLLexer.PREPROC_DELETE, BSLLexer.PREPROC_DELETE_ANY);
    testLexer.assertThat("#Удаление\r", BSLLexer.PREPROC_DELETE_CHANNEL)
      .containsAll(BSLLexer.PREPROC_DELETE, BSLLexer.PREPROC_DELETE_ANY);
  }

  @Test
  void checkAsyncModeMapping() {
    var content = ResourceUtils.byName("Module.bsl");

    var asyncContent = PROCEDURE_PATTERN.matcher(content).replaceAll("Асинх Процедура ");
    asyncContent = FUNCTION_PATTERN.matcher(asyncContent).replaceAll("Асинх Функция ");

    var tokensCount = 76456;
    var asyncCount = 438;
    var asyncSpaceCount = 438;
    var tokens = testLexer.getTokens(BSLLexer.DEFAULT_MODE, content);
    Assertions.assertThat(tokens).hasSize(tokensCount);

    // проверка наличия всех токенов в фикстуре
    var tokenTypes = tokens.stream().map(Token::getType).collect(Collectors.toSet());
    for (int i = 1; i <= BSLLexer.VOCABULARY.getMaxTokenType(); i++) {
      if (BSLLexer.AWAIT_KEYWORD == i // этих токенов быть не должно
        || BSLLexer.ASYNC_KEYWORD == i
        || BSLLexer.PREPROC_EXCLAMATION_MARK == i
        || BSLLexer.PREPROC_STRING == i
        || BSLLexer.PREPROC_USE_KEYWORD == i
        || BSLLexer.PREPROC_LINUX == i
        || BSLLexer.PREPROC_WINDOWS == i
        || BSLLexer.PREPROC_MACOS == i
        || BSLLexer.PREPROC_ANY == i
        || BSLLexer.ANNOTATION_UNKNOWN == i
        || BSLLexer.PREPROC_DELETE_ANY == i
        || BSLLexer.UNKNOWN == i
        || BSLLexer.PREPROC_NATIVE == i) {
        Assertions.assertThat(tokenTypes).doesNotContain(i);
      } else {
        Assertions.assertThat(tokenTypes).contains(i);
      }
    }

    var asyncTokens = testLexer.getTokens(BSLLexer.DEFAULT_MODE, asyncContent);
    Assertions.assertThat(asyncTokens).hasSize(tokensCount + asyncCount + asyncSpaceCount);

    // убираем лишние токены Асинх и пробел после него
    for (int i = asyncTokens.size() - 1; i > 0; i--) {
      var token = asyncTokens.get(i);
      if (token.getType() == BSLLexer.ASYNC_KEYWORD) {
        asyncTokens.remove(i);
        asyncTokens.remove(i);
        i--;
      }
    }

    Assertions.assertThat(asyncTokens).hasSize(tokensCount);

    // должны быть равны
    assertArrayEquals(tokens.stream().map(Token::getType).toArray(),
      asyncTokens.stream().map(Token::getType).toArray());
  }

  @Test
  void testComments() {
    // анализируется канал HIDDEN
    testLexer.assertThat("// Комментарий", BSLLexer.HIDDEN).containsAll(BSLLexer.LINE_COMMENT);
    testLexer.assertThat("#if server then // Комментарий", BSLLexer.HIDDEN)
      .containsAll(BSLLexer.WHITE_SPACE, BSLLexer.WHITE_SPACE, BSLLexer.WHITE_SPACE, BSLLexer.LINE_COMMENT);
    testLexer.assertThat("#Insert // Комментарий", BSLLexer.HIDDEN)
      .containsAll(BSLLexer.PREPROC_INSERT, BSLLexer.WHITE_SPACE, BSLLexer.LINE_COMMENT);
  }
}
