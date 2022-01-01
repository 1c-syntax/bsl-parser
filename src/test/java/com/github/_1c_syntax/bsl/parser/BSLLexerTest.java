/*
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018-2022
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

import org.antlr.v4.runtime.Token;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class BSLLexerTest extends AbstractLexerTest<BSLLexer> {

  private static final Pattern PROCEDURE_PATTERN = Pattern.compile("^процедура\\s",
    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
  private static final Pattern FUNCTION_PATTERN = Pattern.compile("^Функция\\s",
    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);

  BSLLexerTest() {
    super(BSLLexer.class);
  }

  @Test
  void testWhitespaces() {
    String inputString = "   А";

    List<Token> tokens = getTokens(BSLLexer.DEFAULT_MODE, inputString);

    assertThat(tokens).extracting(Token::getType).containsExactly(
      BSLLexer.WHITE_SPACE,
      BSLLexer.IDENTIFIER,
      BSLLexer.EOF
    );

    tokens = getTokens(BSLLexer.ANNOTATION_MODE, inputString);

    assertThat(tokens).extracting(Token::getType).containsExactly(
      BSLLexer.WHITE_SPACE,
      BSLLexer.ANNOTATION_CUSTOM_SYMBOL,
      BSLLexer.EOF
    );

    tokens = getTokens(BSLLexer.ANNOTATION_MODE, inputString);

    assertThat(tokens).extracting(Token::getType).containsExactly(
      BSLLexer.WHITE_SPACE,
      BSLLexer.ANNOTATION_CUSTOM_SYMBOL,
      BSLLexer.EOF
    );

    tokens = getTokens(BSLLexer.PREPROCESSOR_MODE, inputString);

    assertThat(tokens).extracting(Token::getType).containsExactly(
      BSLLexer.WHITE_SPACE,
      BSLLexer.PREPROC_IDENTIFIER,
      BSLLexer.EOF
    );

    tokens = getTokens(BSLLexer.REGION_MODE, inputString);

    assertThat(tokens).extracting(Token::getType).containsExactly(
      BSLLexer.WHITE_SPACE,
      BSLLexer.PREPROC_IDENTIFIER,
      BSLLexer.EOF
    );

    tokens = getTokens(BSLLexer.USE_MODE, inputString);

    assertThat(tokens).extracting(Token::getType).containsExactly(
      BSLLexer.WHITE_SPACE,
      BSLLexer.PREPROC_IDENTIFIER,
      BSLLexer.EOF
    );

    tokens = getTokens(BSLLexer.DOT_MODE, inputString);

    assertThat(tokens).extracting(Token::getType).containsExactly(
      BSLLexer.WHITE_SPACE,
      BSLLexer.IDENTIFIER,
      BSLLexer.EOF
    );
  }

  @Test
  void testBOM() {
    assertMatch('\uFEFF' + "Процедура", BSLLexer.PROCEDURE_KEYWORD);
  }

  @Test
  void testCRCR() {
    List<Token> tokens = getTokens(BSLLexer.DEFAULT_MODE, "\r\n\r\r\n");
    assertThat(tokens.get(0).getLine()).isEqualTo(1);
    assertThat(tokens.get(1).getLine()).isEqualTo(4);
  }

  @Test
  void testUse() {
    assertMatch(BSLLexer.PREPROCESSOR_MODE, "Использовать lib", BSLLexer.PREPROC_USE_KEYWORD, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch(BSLLexer.PREPROCESSOR_MODE, "Использовать \"lib\"", BSLLexer.PREPROC_USE_KEYWORD, BSLLexer.PREPROC_STRING);
    assertMatch(BSLLexer.PREPROCESSOR_MODE, "Использовать lib-name", BSLLexer.PREPROC_USE_KEYWORD, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch(BSLLexer.PREPROCESSOR_MODE, "Использовать 1lib", BSLLexer.PREPROC_USE_KEYWORD, BSLLexer.PREPROC_IDENTIFIER);

    final List<Token> tokens = getTokens(BSLLexer.PREPROCESSOR_MODE, "Использовать 1lib");
    assertThat(tokens.get(2).getText()).isEqualTo("1lib");
  }

  @Test
  void testPreproc_LineComment() {
    assertMatch("#КонецОбласти // Концевой комментарий", BSLLexer.HASH, BSLLexer.PREPROC_END_REGION);
  }

  @Test
  void testPreproc_Region() {
    assertMatch("#Область ИмяОбласти", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Область МобильныйКлиент", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Область Область", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Область КонецОбласти", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Область НЕ", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Область ИЛИ", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Область И", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Область Если", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Область Тогда", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Область ИначеЕсли", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Область Иначе", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Область КонецЕсли", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Region Name", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Region mobileappclient", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Region Region", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Region EndRegion", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Region NOT", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Region OR", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Region AND", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Region IF", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Region Then", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Region ElsIf", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Region Else", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch("#Region EndIf", BSLLexer.HASH, BSLLexer.PREPROC_REGION, BSLLexer.PREPROC_IDENTIFIER);
  }

  @Test
  void testString() {
    assertMatch("\"строка\"", BSLLexer.STRING);
    assertMatch("\"", BSLLexer.STRINGSTART);
    assertMatch("|aaa", BSLLexer.STRINGPART);
    assertMatch("|", BSLLexer.BAR);
    assertMatch("|\"", BSLLexer.STRINGTAIL);
    assertMatch("|aaa\"", BSLLexer.STRINGTAIL);
    assertMatch("А = \"строка\" + \"строка\";",
      BSLLexer.IDENTIFIER,
      BSLLexer.ASSIGN,
      BSLLexer.STRING,
      BSLLexer.PLUS,
      BSLLexer.STRING,
      BSLLexer.SEMICOLON
    );
    assertMatch("\"\"\"\"", BSLLexer.STRING);
    assertMatch("|СПЕЦСИМВОЛ \"\"~\"\"\"", BSLLexer.STRINGTAIL);
    assertMatch("\"Минимальная версия платформы \"\"1С:Предприятие 8\"\" указана выше рекомендуемой.", BSLLexer.STRINGSTART);
    assertMatch("А = \" \n | А \"\"\"\" + А \n  |\";",
      BSLLexer.IDENTIFIER,
      BSLLexer.ASSIGN,
      BSLLexer.STRINGSTART,
      BSLLexer.STRINGPART,
      BSLLexer.STRINGTAIL,
      BSLLexer.SEMICOLON);
  }

  @Test
  void testAnnotation() {
    assertMatch("&НаСервере", BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATSERVER_SYMBOL);
    assertMatch("&НаКлиентеНаСервере", BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATCLIENTATSERVER_SYMBOL);
    assertMatch("&Аннотация", BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_CUSTOM_SYMBOL);
    assertMatch("&НаСервере &Аннотация &НаСервере",
      BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATSERVER_SYMBOL,
      BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_CUSTOM_SYMBOL,
      BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATSERVER_SYMBOL
    );
    assertMatch("&НаСервере\n&Аннотация\n&НаСервере",
      BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATSERVER_SYMBOL,
      BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_CUSTOM_SYMBOL,
      BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATSERVER_SYMBOL
    );
    assertMatch("&НаСервере", BSLLexer.AMPERSAND, BSLLexer.ANNOTATION_ATSERVER_SYMBOL);
  }

  @Test
  void testProcedure() {
    assertMatch("Процедура", BSLLexer.PROCEDURE_KEYWORD);
    assertMatch("Поле.Процедура", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testFunction() {
    assertMatch("Функция", BSLLexer.FUNCTION_KEYWORD);
    assertMatch("Поле.Функция", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testTo() {
    assertMatch("По", BSLLexer.TO_KEYWORD);
    assertMatch("Поле.По", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testExecute() {
    assertMatch("Выполнить", BSLLexer.EXECUTE_KEYWORD);
    assertMatch("Запрос.Выполнить", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Запрос.  Выполнить", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Запрос.  \nВыполнить", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testTry() {
    assertMatch("Попытка", BSLLexer.TRY_KEYWORD);
    assertMatch("Поле.Попытка", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testBreak() {
    assertMatch("Прервать", BSLLexer.BREAK_KEYWORD);
    assertMatch("Поле.Прервать", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testNew() {
    assertMatch("Новый", BSLLexer.NEW_KEYWORD);
    assertMatch("Поле.Новый", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testElse() {
    assertMatch("Иначе", BSLLexer.ELSE_KEYWORD);
    assertMatch("ИНАЧЕ", BSLLexer.ELSE_KEYWORD);
    assertMatch("ИнАчЕ", BSLLexer.ELSE_KEYWORD);
    assertMatch("Поле.Иначе", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testIn() {
    assertMatch("Из", BSLLexer.IN_KEYWORD);
    assertMatch("In", BSLLexer.IN_KEYWORD);
    assertMatch("Поле.Из", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
  }

  @Test
  void testMark() {
    assertMatch("~Метка", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Если", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Тогда", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~ИначеЕсли", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Иначе", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~КонецЕсли", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Для", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Каждого", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Из", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~По", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Пока", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Цикл", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~КонецЦикла", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Процедура", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Функция", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~КонецПроцедуры", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~КонецФункции", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Перем", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Перейти", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Возврат", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Продолжить", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Прервать", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~И", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Или", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Не", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Попытка", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Исключение", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~ВызватьИсключение", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~КонецПопытки", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Новый", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
    assertMatch("~Выполнить", BSLLexer.TILDA, BSLLexer.IDENTIFIER);
  }

  @Test
  void testHandlers() {
    assertMatch("ДобавитьОбработчик", BSLLexer.ADDHANDLER_KEYWORD);
    assertMatch("AddHandler", BSLLexer.ADDHANDLER_KEYWORD);
    assertMatch("УдалитьОбработчик", BSLLexer.REMOVEHANDLER_KEYWORD);
    assertMatch("RemoveHandler", BSLLexer.REMOVEHANDLER_KEYWORD);
  }

  @Test
  void testKeyWords() {
    assertMatch("ИСТиНА", BSLLexer.TRUE);
    assertMatch("TRuE", BSLLexer.TRUE);
    assertMatch("Поле.ИСТИНА", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.TRUE", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ЛоЖЬ", BSLLexer.FALSE);
    assertMatch("FaLSE", BSLLexer.FALSE);
    assertMatch("Поле.ЛОЖЬ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.FALSE", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("НеопределенО", BSLLexer.UNDEFINED);
    assertMatch("UNDEFINeD", BSLLexer.UNDEFINED);
    assertMatch("Поле.НЕОПРЕДЕЛЕНО", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.UNDEFINED", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("NUlL", BSLLexer.NULL);
    assertMatch("Поле.NULL", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.NULL", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ПРОЦЕДУрА", BSLLexer.PROCEDURE_KEYWORD);
    assertMatch("PROCEDUrE", BSLLexer.PROCEDURE_KEYWORD);
    assertMatch("Поле.ПРОЦЕДУРА", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.PROCEDURE", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("фУНКЦИя", BSLLexer.FUNCTION_KEYWORD);
    assertMatch("fUNCTIOn", BSLLexer.FUNCTION_KEYWORD);
    assertMatch("Поле.ФУНКЦИЯ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.FUNCTION", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("КОНЕЦПРОЦЕДУРы", BSLLexer.ENDPROCEDURE_KEYWORD);
    assertMatch("ENDPROCEDURe", BSLLexer.ENDPROCEDURE_KEYWORD);
    assertMatch("Поле.КОНЕЦПРОЦЕДУРЫ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.ENDPROCEDURE", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("КОНЕЦФУНКЦИИ", BSLLexer.ENDFUNCTION_KEYWORD);
    assertMatch("ENDFUNCTION", BSLLexer.ENDFUNCTION_KEYWORD);
    assertMatch("Поле.КОНЕЦФУНКЦИИ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.ENDFUNCTION", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ЭКСПОРТ", BSLLexer.EXPORT_KEYWORD);
    assertMatch("EXPORT", BSLLexer.EXPORT_KEYWORD);
    assertMatch("Поле.ЭКСПОРТ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.EXPORT", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ЗНАЧ", BSLLexer.VAL_KEYWORD);
    assertMatch("VAL", BSLLexer.VAL_KEYWORD);
    assertMatch("Поле.ЗНАЧ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.VAL", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("КОНЕЦЕСЛи", BSLLexer.ENDIF_KEYWORD);
    assertMatch("Endif", BSLLexer.ENDIF_KEYWORD);
    assertMatch("Поле.КОНЕЦЕСЛИ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.ENDIF", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("КОНЕЦЦИКЛа", BSLLexer.ENDDO_KEYWORD);
    assertMatch("ENDDo", BSLLexer.ENDDO_KEYWORD);
    assertMatch("Поле.КОНЕЦЦИКЛА", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.ENDDO", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ЕСЛи", BSLLexer.IF_KEYWORD);
    assertMatch("If", BSLLexer.IF_KEYWORD);
    assertMatch("Поле.ЕСЛИ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.IF", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ИНАЧЕЕСЛи", BSLLexer.ELSIF_KEYWORD);
    assertMatch("ELSIf", BSLLexer.ELSIF_KEYWORD);
    assertMatch("Поле.ИНАЧЕЕСЛИ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.ELSIF", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ИНАЧе", BSLLexer.ELSE_KEYWORD);
    assertMatch("ELSe", BSLLexer.ELSE_KEYWORD);
    assertMatch("Поле.ИНАЧЕ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.ELSE", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ТОГДа", BSLLexer.THEN_KEYWORD);
    assertMatch("THEn", BSLLexer.THEN_KEYWORD);
    assertMatch("Поле.ТОГДА", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.THEN", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ПОКа", BSLLexer.WHILE_KEYWORD);
    assertMatch("WHILe", BSLLexer.WHILE_KEYWORD);
    assertMatch("Поле.ПОКА", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.WHILE", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ЦИКл", BSLLexer.DO_KEYWORD);
    assertMatch("Do", BSLLexer.DO_KEYWORD);
    assertMatch("Поле.ЦИКЛ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.DO", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ДЛя", BSLLexer.FOR_KEYWORD);
    assertMatch("FOr", BSLLexer.FOR_KEYWORD);
    assertMatch("Поле.ДЛЯ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.FOR", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("По", BSLLexer.TO_KEYWORD);
    assertMatch("To", BSLLexer.TO_KEYWORD);
    assertMatch("Поле.ПО", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.TO", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("КАЖДОГо", BSLLexer.EACH_KEYWORD);
    assertMatch("EAcH", BSLLexer.EACH_KEYWORD);
    assertMatch("Поле.КАЖДОГО", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.EACH", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("Из", BSLLexer.IN_KEYWORD);
    assertMatch("In", BSLLexer.IN_KEYWORD);
    assertMatch("Поле.ИЗ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.IN", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ПОПЫТКа", BSLLexer.TRY_KEYWORD);
    assertMatch("TRy", BSLLexer.TRY_KEYWORD);
    assertMatch("Поле.ПОПЫТКА", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.TRY", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ИСКЛЮЧЕНИе", BSLLexer.EXCEPT_KEYWORD);
    assertMatch("EXCEPt", BSLLexer.EXCEPT_KEYWORD);
    assertMatch("Поле.ИСКЛЮЧЕНИЕ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.EXCEPT", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("КОНЕЦПОПЫТКи", BSLLexer.ENDTRY_KEYWORD);
    assertMatch("ENDTRy", BSLLexer.ENDTRY_KEYWORD);
    assertMatch("Поле.КОНЕЦПОПЫТКИ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.ENDTRY", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ВОЗВРАт", BSLLexer.RETURN_KEYWORD);
    assertMatch("RETURn", BSLLexer.RETURN_KEYWORD);
    assertMatch("Поле.ВОЗВРАТ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.RETURN", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ПРОДОЛЖИТь", BSLLexer.CONTINUE_KEYWORD);
    assertMatch("CONTINUe", BSLLexer.CONTINUE_KEYWORD);
    assertMatch("Поле.ПРОДОЛЖИТЬ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.CONTINUE", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ВЫЗВАТЬИСКЛЮЧЕНИе", BSLLexer.RAISE_KEYWORD);
    assertMatch("RAISe", BSLLexer.RAISE_KEYWORD);
    assertMatch("Поле.ВЫЗВАТЬИСКЛЮЧЕНИЕ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.RAISE", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ПЕРЕм", BSLLexer.VAR_KEYWORD);
    assertMatch("VAr", BSLLexer.VAR_KEYWORD);
    assertMatch("Поле.ПЕРЕМ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.VAR", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("Не", BSLLexer.NOT_KEYWORD);
    assertMatch("NOt", BSLLexer.NOT_KEYWORD);
    assertMatch("Поле.НЕ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.NOT", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ИЛи", BSLLexer.OR_KEYWORD);
    assertMatch("Or", BSLLexer.OR_KEYWORD);
    assertMatch("Поле.ИЛИ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.OR", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("и", BSLLexer.AND_KEYWORD);
    assertMatch("ANd", BSLLexer.AND_KEYWORD);
    assertMatch("Поле.И", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.AND", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("НОВЫй", BSLLexer.NEW_KEYWORD);
    assertMatch("NEw", BSLLexer.NEW_KEYWORD);
    assertMatch("Поле.НОВЫЙ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.NEW", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ПЕРЕЙТи", BSLLexer.GOTO_KEYWORD);
    assertMatch("GOTo", BSLLexer.GOTO_KEYWORD);
    assertMatch("Поле.ПЕРЕЙТИ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.GOTO", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ПРЕРВАТь", BSLLexer.BREAK_KEYWORD);
    assertMatch("BREAk", BSLLexer.BREAK_KEYWORD);
    assertMatch("Поле.ПРЕРВАТЬ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.BREAK", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ВЫПОЛНИТь", BSLLexer.EXECUTE_KEYWORD);
    assertMatch("EXECUTe", BSLLexer.EXECUTE_KEYWORD);
    assertMatch("Поле.ВЫПОЛНИТЬ", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.EXECUTE", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ДОБАВИТЬОБРАБОТЧИк", BSLLexer.ADDHANDLER_KEYWORD);
    assertMatch("ADDHANDLEr", BSLLexer.ADDHANDLER_KEYWORD);
    assertMatch("Поле.ДОБАВИТЬОБРАБОТЧИК", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.ADDHANDLER", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("УДАЛИТЬОБРАБОТЧИк", BSLLexer.REMOVEHANDLER_KEYWORD);
    assertMatch("REMOVEHANDLEr", BSLLexer.REMOVEHANDLER_KEYWORD);
    assertMatch("Поле.УДАЛИТЬОБРАБОТЧИК", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.REMOVEHANDLER", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("АсинХ", BSLLexer.ASYNC_KEYWORD);
    assertMatch("ASYNc", BSLLexer.ASYNC_KEYWORD);
    assertMatch("Поле.Асинх", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.ASYNC", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("ЖдатЬ", BSLLexer.IDENTIFIER);
    assertMatch("WAIt", BSLLexer.IDENTIFIER);
    assertMatch("Поле.ждАть", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);
    assertMatch("Field.WaIT", BSLLexer.IDENTIFIER, BSLLexer.DOT, BSLLexer.IDENTIFIER);

    assertMatch("асинх функция а() ЖдатЬ Б(); КонецФункции",
      BSLLexer.ASYNC_KEYWORD,
      BSLLexer.FUNCTION_KEYWORD,
      BSLLexer.IDENTIFIER,
      BSLLexer.LPAREN,
      BSLLexer.RPAREN,
      BSLLexer.WAIT_KEYWORD,
      BSLLexer.IDENTIFIER,
      BSLLexer.LPAREN,
      BSLLexer.RPAREN,
      BSLLexer.SEMICOLON,
      BSLLexer.ENDFUNCTION_KEYWORD
    );
    assertMatch("async function a() wait b(); endfunction",
      BSLLexer.ASYNC_KEYWORD,
      BSLLexer.FUNCTION_KEYWORD,
      BSLLexer.IDENTIFIER,
      BSLLexer.LPAREN,
      BSLLexer.RPAREN,
      BSLLexer.WAIT_KEYWORD,
      BSLLexer.IDENTIFIER,
      BSLLexer.LPAREN,
      BSLLexer.RPAREN,
      BSLLexer.SEMICOLON,
      BSLLexer.ENDFUNCTION_KEYWORD
    );
  }

  @Test
  void testPreproc_DeleteInsert() {
    assertMatchChannel(BSLLexer.HIDDEN, "#Insert\n", BSLLexer.PREPROC_INSERT, BSLLexer.WHITE_SPACE);
    assertMatchChannel(BSLLexer.HIDDEN, "#КонецВставки\n", BSLLexer.PREPROC_ENDINSERT, BSLLexer.WHITE_SPACE);
    assertMatchChannel(BSLLexer.PREPROC_DELETE_CHANNEL, "#Удаление\n", BSLLexer.PREPROC_DELETE);
    assertMatchChannel(BSLLexer.PREPROC_DELETE_MODE, BSLLexer.PREPROC_DELETE_CHANNEL,
      "#EndDelete\n", BSLLexer.PREPROC_ENDDELETE);
    assertMatchChannel(BSLLexer.PREPROC_DELETE_CHANNEL, "#Удаление\r\n", BSLLexer.PREPROC_DELETE);
    assertMatchChannel(BSLLexer.PREPROC_DELETE_CHANNEL, "#Удаление\r\n#EndDelete\n", BSLLexer.PREPROC_DELETE,
      BSLLexer.PREPROC_ENDDELETE);
    assertMatchChannel(BSLLexer.PREPROC_DELETE_MODE, BSLLexer.PREPROC_DELETE_CHANNEL,
      "#EndDelete\r\n", BSLLexer.PREPROC_ENDDELETE);
    assertMatchChannel(BSLLexer.PREPROC_DELETE_CHANNEL, "# Удаление \r\n f", BSLLexer.PREPROC_DELETE,
      BSLLexer.PREPROC_DELETE_ANY);
    assertMatchChannel(BSLLexer.PREPROC_DELETE_CHANNEL, "#Удаление\r", BSLLexer.PREPROC_DELETE,
      BSLLexer.PREPROC_DELETE_ANY);
  }

  @Test
  void checkAsyncModeMapping() {
    String content;
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    try (InputStream inputStream = classLoader.getResourceAsStream("Module.bsl")) {
      assert inputStream != null;
      content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Error: ", e);
    }

    var asyncContent = PROCEDURE_PATTERN.matcher(content).replaceAll("Асинх Процедура ");
    asyncContent = FUNCTION_PATTERN.matcher(asyncContent).replaceAll("Асинх Функция ");

    var tokensCount = 76456;
    var asyncCount = 438;
    var asyncSpaceCount = 438;
    var tokens = getTokens(BSLLexer.DEFAULT_MODE, content);
    assertThat(tokens).hasSize(tokensCount);

    // проверка наличия всех токенов в фикстуре
    var tokenTypes = tokens.stream().map(Token::getType).collect(Collectors.toSet());
    for (int i = 1; i <= BSLLexer.VOCABULARY.getMaxTokenType(); i++) {
      if (BSLLexer.WAIT_KEYWORD == i // этих токенов быть не должно
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
        || BSLLexer.UNKNOWN == i) {
        assertThat(tokenTypes).doesNotContain(i);
      } else {
        assertThat(tokenTypes).contains(i);
      }
    }

    var asyncTokens = getTokens(BSLLexer.DEFAULT_MODE, asyncContent);
    assertThat(asyncTokens).hasSize(tokensCount + asyncCount + asyncSpaceCount);

    // убираем лишние токены Асинх и пробел после него
    for (int i = asyncTokens.size() - 1; i > 0; i--) {
      var token = asyncTokens.get(i);
      if (token.getType() == BSLLexer.ASYNC_KEYWORD) {
        asyncTokens.remove(i);
        asyncTokens.remove(i);
        i--;
      }
    }

    assertThat(asyncTokens).hasSize(tokensCount);

    // должны быть равны
    assertArrayEquals(tokens.stream().map(Token::getType).toArray(),
      asyncTokens.stream().map(Token::getType).toArray());
  }

  @Test
  void testComments() {
    // анализируется канал HIDDEN
    assertMatchChannel(BSLLexer.HIDDEN, "// Комментарий", BSLLexer.LINE_COMMENT);
    assertMatchChannel(BSLLexer.HIDDEN, "#if server then // Комментарий",
      BSLLexer.WHITE_SPACE, BSLLexer.WHITE_SPACE, BSLLexer.WHITE_SPACE,
      BSLLexer.LINE_COMMENT);
    assertMatchChannel(BSLLexer.HIDDEN, "#Insert // Комментарий",
      BSLLexer.PREPROC_INSERT, BSLLexer.WHITE_SPACE, BSLLexer.LINE_COMMENT);
  }
}
