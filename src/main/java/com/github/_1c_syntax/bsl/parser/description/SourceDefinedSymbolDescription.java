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
package com.github._1c_syntax.bsl.parser.description;

import com.github._1c_syntax.bsl.parser.description.support.SimpleRange;

/**
 * Базовый интерфейс объектов, имеющих описание
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
   * Если описание содержит только ссылку, то здесь будет ее значение
   * <p>
   * TODO Временное решение, надо будет продумать кошерное решение
   *
   * @return Строка с текстом ссылки (без префикса см./see)
   */
  String getLink();

  /**
   * Диапазон, в котором располагается описание.
   *
   * @return Область описания
   */
  SimpleRange getSimpleRange();
}
