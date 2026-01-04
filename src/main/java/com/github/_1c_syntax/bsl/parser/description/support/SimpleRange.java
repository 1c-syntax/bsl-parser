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
package com.github._1c_syntax.bsl.parser.description.support;

import org.antlr.v4.runtime.Token;

import java.util.List;

/**
 * Класс для хранения области символов
 *
 * @param startLine      Номер первой строки области
 * @param startCharacter Номер первого символа области
 * @param endLine        Номер последней строки области
 * @param endCharacter   Номер последнего символа области
 */
public record SimpleRange(int startLine, int startCharacter, int endLine, int endCharacter) {

  /**
   * Проверяет вхождение второй области в первую
   *
   * @param bigger  Первая область
   * @param smaller Вторая область
   * @return Признак вхождения второй в первую
   */
  public static boolean containsRange(SimpleRange bigger, SimpleRange smaller) {
    if (bigger.startLine() > smaller.startLine()
      || bigger.endLine() < smaller.endLine()) {
      return false;
    }

    if (bigger.startLine() == smaller.startLine()
      && bigger.startCharacter() > smaller.startCharacter()) {
      return false;
    }

    return bigger.endLine() != smaller.endLine()
      || bigger.endCharacter() >= smaller.endCharacter();
  }

  /**
   * Создает новую область по токенам углов области
   *
   * @param startToken Токен левого верхнего угла
   * @param endToken   Токен правого нижнего узла
   * @return Созданная область
   */
  public static SimpleRange create(Token startToken, Token endToken) {
    int startLine = startToken.getLine() - 1;
    int startChar = startToken.getCharPositionInLine();
    int endLine = endToken.getLine() - 1;
    int endChar;
    if (endToken.getType() == Token.EOF) {
      endChar = endToken.getCharPositionInLine();
    } else {
      endChar = endToken.getCharPositionInLine() + endToken.getText().length();
    }

    return new SimpleRange(startLine, startChar, endLine, endChar);
  }

  /**
   * Создает область по одному токену
   *
   * @param token Токен, для которого нужно создать область
   * @return Область
   */
  public static SimpleRange create(Token token) {
    return create(token, token);
  }

  /**
   * Создает область по списку токенов
   *
   * @param tokens Список токенов области
   * @return Созданная область
   */
  public static SimpleRange create(List<Token> tokens) {
    if (tokens.isEmpty()) {
      return new SimpleRange(0, 0, 0, 0);
    }
    var firstElement = tokens.get(0);
    var lastElement = tokens.get(tokens.size() - 1);

    return create(firstElement, lastElement);
  }

  /**
   * Создает новую область {@link SimpleRange} с заданными координатами.
   *
   * @param startLine      номер начальной строки (начиная с 0)
   * @param startCharacter номер начального символа в строке (начиная с 0)
   * @param endLine        номер конечной строки (начиная с 0)
   * @param endCharacter   номер конечного символа в строке (начиная с 0)
   * @return новый экземпляр {@link SimpleRange}, представляющий заданный диапазон
   */
  public static SimpleRange create(int startLine, int startCharacter, int endLine, int endCharacter) {
    return new SimpleRange(startLine, startCharacter, endLine, endCharacter);
  }

  /**
   * Создает новую область для строки {@link SimpleRange} с заданными координатами.
   *
   * @param lineNo         номер строки (начиная с 0)
   * @param startCharacter номер начального символа в строке (начиная с 0)
   * @param endCharacter   номер конечного символа в строке (начиная с 0)
   * @return новый экземпляр {@link SimpleRange}, представляющий заданный диапазон
   */
  public static SimpleRange create(int lineNo, int startCharacter, int endCharacter) {
    return new SimpleRange(lineNo, startCharacter, lineNo, endCharacter);
  }

  /**
   * Возвращает признак пустой области, т.е. все координаты равны 0
   *
   * @return признак пустой области
   */
  public boolean isEmpty() {
    return startLine == 0 && startCharacter == 0
      && endLine == 0 && endCharacter == 0;
  }
}
