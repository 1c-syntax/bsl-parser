/*
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018-2021
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
package com.github._1c_syntax.bsl.parser.description.support;

import org.antlr.v4.runtime.Token;

import java.util.List;

/**
 * Класс для хранения области символов
 */
public final class SimpleRange {

  /**
   * Номер первой строки области
   */
  private final int startLine;

  /**
   * Номер первого символа области
   */
  private final int startCharacter;

  /**
   * Номер последней строки области
   */
  private final int endLine;

  /**
   * Номер последнего символа области
   */
  private final int endCharacter;

  public SimpleRange(int startLine, int startCharacter, int endLine, int endCharacter) {
    this.startLine = startLine;
    this.startCharacter = startCharacter;
    this.endLine = endLine;
    this.endCharacter = endCharacter;
  }

  /**
   * Проверяет вхождение второй области в первую
   *
   * @param bigger  Первая область
   * @param smaller Вторая область
   * @return Признак вхождения второй в первую
   */
  public static boolean containsRange(SimpleRange bigger, SimpleRange smaller) {
    if (bigger.getStartLine() > smaller.getStartLine()
      || bigger.getEndLine() < smaller.getEndLine()) {
      return false;
    }

    if (bigger.getStartLine() == smaller.getStartLine()
      && bigger.getStartCharacter() > smaller.getStartCharacter()) {
      return false;
    }

    return bigger.getEndLine() != smaller.getEndLine()
      || bigger.getEndCharacter() >= smaller.getEndCharacter();
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

  public int getStartLine() {
    return startLine;
  }

  public int getStartCharacter() {
    return startCharacter;
  }

  public int getEndLine() {
    return endLine;
  }

  public int getEndCharacter() {
    return endCharacter;
  }

  public String toString() {
    return ("SimpleRange(startLine=" + startLine
      + ", startCharacter=" + startCharacter
      + ", endLine=" + endLine
      + ", endCharacter=" + endCharacter + ")").intern();
  }

  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    } else if (!(other instanceof SimpleRange)) {
      return false;
    } else {
      SimpleRange otherRange = (SimpleRange) other;
      return (startLine == otherRange.getStartLine()
        && startCharacter == otherRange.getStartCharacter()
        && endLine == otherRange.getEndLine()
        && endCharacter == otherRange.getEndCharacter());
    }
  }
}
