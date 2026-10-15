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

/**
 * Части описания (ключевые слова, конструкции).
 * <p>
 * Область хранится числами, а {@link SimpleRange} собирается при обращении: элементов описаний в
 * большом проекте миллионы, и отдельный объект области на каждый стоил больше самого элемента.
 *
 * @param startLine      Строка начала области расположения части описания
 * @param startCharacter Символ начала области
 * @param endLine        Строка конца области
 * @param endCharacter   Символ конца области
 * @param type           Тип части описания
 */
public record DescriptionElement(int startLine, int startCharacter, int endLine, int endCharacter, Type type) {

  /**
   * @param range Область расположения части описания
   * @param type  Тип части описания
   */
  public DescriptionElement(SimpleRange range, Type type) {
    this(range.startLine(), range.startCharacter(), range.endLine(), range.endCharacter(), type);
  }

  /**
   * @return Область расположения части описания
   */
  public SimpleRange range() {
    return new SimpleRange(startLine, startCharacter, endLine, endCharacter);
  }

  public enum Type {
    UNKNOWN,
    RETURNS_KEYWORD,
    EXAMPLE_KEYWORD,
    PARAMETERS_KEYWORD,
    CALL_OPTIONS_KEYWORD,
    DEPRECATE_KEYWORD,
    PARAMETER_NAME,
    TYPE_NAME
  }
}
