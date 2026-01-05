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

import com.github._1c_syntax.bsl.parser.description.reader.MethodDescriptionReader;
import com.github._1c_syntax.bsl.parser.description.support.Hyperlink;
import com.github._1c_syntax.bsl.parser.description.support.SimpleRange;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.antlr.v4.runtime.Token;

import java.util.List;

/**
 * Класс-описание метода (процедуры или функции).
 */
@Value
@Builder
public class MethodDescription implements SourceDefinedSymbolDescription {

  /**
   * Содержит полное описание метода (весь текст).
   */
  String description;

  /**
   * Содержит часть строки после ключевого слова, в которой должно быть
   * описание причины устаревания метода либо альтернативы.
   */
  @Builder.Default
  String deprecationInfo = "";

  /**
   * Признак устаревания метода.
   */
  @Builder.Default
  boolean deprecated = false;

  /**
   * Описание назначения метода.
   */
  @Builder.Default
  String purposeDescription = "";

  /**
   * Примеры использования метода.
   */
  @Builder.Default
  String examples = "";

  /**
   * Параметры метода с типами и описанием.
   */
  @Singular
  List<ParameterDescription> parameters;

  /**
   * Возвращаемые значения (типы).
   */
  List<TypeDescription> returnedValue;

  /**
   * Список всех ссылок, которые могут быть в описании.
   */
  List<Hyperlink> links;

  /**
   * Диапазон, в котором располагается описание.
   */
  SimpleRange range;

  public static MethodDescription create(List<Token> comments) {
    return MethodDescriptionReader.read(comments);
  }
}
