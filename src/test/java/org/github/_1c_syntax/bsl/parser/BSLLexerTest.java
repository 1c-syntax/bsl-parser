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
import org.antlr.v4.runtime.Token;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class BSLLexerTest {

  private BSLLexer lexer = new BSLLexer(null);

  private List<Token> getTokens(int mode, String inputString) {
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
    tokenStream.fill();

    return tokenStream.getTokens();
  }

  private void assertMatch(String inputString, Integer... expectedTokens) {
    assertMatch(BSLLexer.DEFAULT_MODE, inputString, expectedTokens);
  }

  private void assertMatch(int mode, String inputString, Integer... expectedTokens) {
    List<Token> tokens = getTokens(mode, inputString);
    Integer[] tokenTypes = tokens.stream()
      .filter(token -> token.getChannel() == BSLLexer.DEFAULT_TOKEN_CHANNEL)
      .filter(token -> token.getType() != Token.EOF)
      .map(Token::getType)
      .toArray(Integer[]::new);
    assertArrayEquals(expectedTokens, tokenTypes);
  }

  @Test
  void testBOM() {
    assertMatch('\uFEFF' + "Процедура", BSLLexer.PROCEDURE_KEYWORD);
  }

  @Test
  void testCRCR() {
    List<Token> tokens = getTokens(BSLLexer.DEFAULT_MODE, "\r\n\r\r\n");
    assert tokens.get(0).getLine() == 1;
    assert tokens.get(1).getLine() == 1;
    assert tokens.get(2).getLine() == 2;
    assert tokens.get(3).getLine() == 3;
    assert tokens.get(4).getLine() == 3;
    assert tokens.get(5).getLine() == 4;
  }

  @Test
  void testUse() {
    assertMatch(BSLLexer.PREPROCESSOR_MODE, "Использовать lib", BSLLexer.PREPROC_USE_KEYWORD, BSLLexer.PREPROC_IDENTIFIER);
    assertMatch(BSLLexer.PREPROCESSOR_MODE, "Использовать \"lib\"", BSLLexer.PREPROC_USE_KEYWORD, BSLLexer.PREPROC_STRING);
    assertMatch(BSLLexer.PREPROCESSOR_MODE, "Использовать lib-name", BSLLexer.PREPROC_USE_KEYWORD, BSLLexer.PREPROC_IDENTIFIER);
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

}
