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
package com.github._1c_syntax.bsl.parser.description;

import com.github._1c_syntax.bsl.parser.description.support.Hyperlink;
import com.github._1c_syntax.bsl.parser.description.support.SimpleRange;
import org.antlr.v4.runtime.Token;

import java.util.List;

/**
 * Интерфейс описания символов, определенных в исходном коде.
 * <p>
 * Предоставляет доступ к описанию символа из комментариев,
 * включая информацию об устаревании, назначении и примерах использования.
 */
public interface SourceDefinedSymbolDescription {

  /**
   * Содержит полное описание (весь текст).
   *
   * @return Строка с описанием
   */
  String getDescription();

  /**
   * Содержит часть строки после ключевого слова, в которой должно быть
   * описание причины устаревания либо альтернативы.
   *
   * @return Строка с описанием причины устаревания
   */
  String getDeprecationInfo();

  /**
   * Признак устаревания.
   *
   * @return Признак устаревания: True - устарел
   */
  boolean isDeprecated();

  /**
   * Описание назначения (при наличии).
   *
   * @return Строка с описанием
   */
  String getPurposeDescription();

  /**
   * Список всех ссылок, которые могут быть в описании.
   *
   * @return Список ссылок
   */
  List<Hyperlink> getLinks();

  /**
   * Диапазон, в котором располагается описание.
   *
   * @return Область описания
   */
  SimpleRange getRange();

  /**
   * Проверяет вхождение области заданной двумя пограничными токенами в область описания
   *
   * @param first Токен левого верхнего угла области
   * @param last  Токен нижнего правого узла области
   * @return Признак вхождения
   */
  default boolean contains(Token first, Token last) {
    return SimpleRange.containsRange(getRange(), SimpleRange.create(first, last));
  }
}
