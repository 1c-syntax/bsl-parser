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
package com.github._1c_syntax.bsl.parser.description.reader;

import com.github._1c_syntax.bsl.parser.description.MethodDescription;
import com.github._1c_syntax.bsl.parser.description.VariableDescription;
import org.antlr.v4.runtime.Token;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Читатель объектов описания по набору токенов из текста
 */
public final class BSLDescriptionReader {

  private BSLDescriptionReader() {
    // utility class
  }

  /**
   * Возвращает объект описания метода по списку токенов описания
   *
   * @param tokens Список токенов описания метода
   * @return Объект описания
   */
  public static MethodDescription parseMethodDescription(List<Token> tokens) {
    return new MethodDescription(tokens);
  }

  /**
   * Возвращает объект описания переменной по списку токенов описания
   *
   * @param tokens Список токенов описания переменной
   * @return Объект описания
   */
  public static VariableDescription parseVariableDescription(List<Token> tokens) {
    return new VariableDescription(tokens);
  }

  /**
   * Возвращает объект описания переменной по списку токенов описания и токену "висящего" описания
   *
   * @param tokens   Список токенов описания переменной
   * @param trailing Токен "висящего" описания
   * @return Объект описания
   */
  public static VariableDescription parseVariableDescription(List<Token> tokens, @Nullable Token trailing) {
    return new VariableDescription(tokens, trailing);
  }
}
