/*
 * This file is a part of BSL Parser.
 *
 * Copyright (c) 2018-2024
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

import com.github._1c_syntax.bsl.parser.BSLMethodDescriptionTokenizer;
import com.github._1c_syntax.bsl.parser.description.support.DescriptionReader;
import com.github._1c_syntax.bsl.parser.description.support.SimpleRange;
import lombok.Value;
import org.antlr.v4.runtime.Token;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Класс-описание переменной.
 */
@Value
public class VariableDescription implements SourceDefinedSymbolDescription {

  /**
   * Содержит полное описание переменной (весь текст)
   */
  String description;

  /**
   * Содержит часть строки после ключевого слова, в которой должно быть
   * описание причины устаревания переменной либо альтернативы
   */
  String deprecationInfo;

  /**
   * Признак устаревания переменной
   */
  boolean deprecated;

  /**
   * Описание назначения переменной
   */
  String purposeDescription;

  /**
   * Если описание содержит только ссылку, то здесь будет ее значение
   * <p>
   * TODO Временное решение, надо будет продумать кошерное решение
   */
  String link;

  /**
   * Диапазон, в котором располагается описание.
   */
  SimpleRange range;

  /**
   * Описание "висячего" комментария
   */
  VariableDescription trailingDescription;

  VariableDescription(List<Token> comments) {
    this(comments, null);
  }

  VariableDescription(List<Token> comments, @Nullable Token trailingComment) {
    description = comments.stream()
      .map(Token::getText)
      .collect(Collectors.joining("\n"));

    var tokenizer = new BSLMethodDescriptionTokenizer(description);
    var ast = requireNonNull(tokenizer.getAst());

    range = SimpleRange.create(comments);
    purposeDescription = DescriptionReader.readPurposeDescription(ast);
    link = DescriptionReader.readLink(ast);
    deprecated = ast.deprecate() != null;
    deprecationInfo = DescriptionReader.readDeprecationInfo(ast);
    if (trailingComment == null) {
      trailingDescription = null;
    } else {
      trailingDescription = new VariableDescription(List.of(trailingComment));
    }
  }

  @Override
  public SimpleRange getSimpleRange() {
    return range;
  }

  public Optional<VariableDescription> getTrailingDescription() {
    return Optional.ofNullable(trailingDescription);
  }
}
