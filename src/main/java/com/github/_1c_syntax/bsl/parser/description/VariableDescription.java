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

import com.github._1c_syntax.bsl.parser.description.reader.VariableDescriptionReader;
import com.github._1c_syntax.bsl.parser.description.support.DescriptionElement;
import com.github._1c_syntax.bsl.parser.description.support.Hyperlink;
import com.github._1c_syntax.bsl.parser.description.support.SimpleRange;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.Optional;

/**
 * Класс-описание переменной.
 */
@Value
@Builder
public class VariableDescription implements SourceDefinedSymbolDescription {

  /**
   * Содержит полное описание переменной (весь текст)
   */
  String description;

  /**
   * Содержит часть строки после ключевого слова, в которой должно быть
   * описание причины устаревания переменной либо альтернативы
   */
  @Builder.Default
  String deprecationInfo = "";

  /**
   * Признак устаревания переменной
   */
  boolean deprecated;

  /**
   * Описание назначения переменной
   */
  @Builder.Default
  String purposeDescription = "";

  /**
   * Список всех ссылок, которые могут быть в описании.
   */
  List<Hyperlink> links;

  /**
   * Диапазон, в котором располагается описание.
   */
  SimpleRange range;

  /**
   * Описание "висячего" комментария
   */
  Optional<VariableDescription> trailingDescription;

  @Singular
  List<DescriptionElement> elements;

  public static VariableDescription create(List<Token> comments) {
    return VariableDescriptionReader.read(comments);
  }

  public static VariableDescription create(List<Token> comments, Optional<Token> trailingComment) {
    return VariableDescriptionReader.read(comments, trailingComment);
  }

  @Override
  public String format(String lang, int maxLineLen) {
    return "";
  }
}
