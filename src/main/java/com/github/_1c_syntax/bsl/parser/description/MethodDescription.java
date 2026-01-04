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

import com.github._1c_syntax.bsl.parser.BSLMethodDescriptionTokenizer;
import com.github._1c_syntax.bsl.parser.description.support.DescriptionReader;
import com.github._1c_syntax.bsl.parser.description.support.ParameterDescription;
import com.github._1c_syntax.bsl.parser.description.support.SimpleRange;
import com.github._1c_syntax.bsl.parser.description.support.TypeDescription;
import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Класс-описание метода (процедуры или функции).
 */
public final class MethodDescription implements SourceDefinedSymbolDescription {
  /**
   * Содержит полное описание метода (весь текст).
   */
  private final String description;
  /**
   * Содержит часть строки после ключевого слова, в которой должно быть
   * описание причины устаревания метода либо альтернативы.
   */
  private final String deprecationInfo;
  /**
   * Признак устаревания метода.
   */
  private final boolean deprecated;
  /**
   * Описание назначения метода.
   */
  private final String purposeDescription;
  /**
   * Примеры использования метода.
   */
  private final List<String> examples;
  /**
   * Варианты вызова метода.
   */
  private final List<String> callOptions;
  /**
   * Параметры метода с типами и описанием.
   */
  private final List<ParameterDescription> parameters;
  /**
   * Возвращаемые значения (типы).
   */
  private final List<TypeDescription> returnedValue;
  /**
   * Если описание содержит только ссылку, то здесь будет ее значение.
   * <p>
   * TODO Временное решение, надо будет продумать в следующем релизе
   */
  private final String link;
  /**
   * Диапазон, в котором располагается описание.
   */
  private final SimpleRange range;

  MethodDescription(List<Token> comments) {
    description = comments.stream()
      .map(Token::getText)
      .collect(Collectors.joining("\n"));

    var tokenizer = new BSLMethodDescriptionTokenizer(description);
    var ast = requireNonNull(tokenizer.getAst());

    purposeDescription = DescriptionReader.readPurposeDescription(ast);
    link = DescriptionReader.readLink(ast);
    deprecated = ast.deprecate() != null;
    deprecationInfo = DescriptionReader.readDeprecationInfo(ast);
    callOptions = DescriptionReader.readCallOptions(ast);
    examples = DescriptionReader.readExamples(ast);
    parameters = DescriptionReader.readParameters(ast);
    returnedValue = DescriptionReader.readReturnedValue(ast);
    range = SimpleRange.create(comments);
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getDeprecationInfo() {
    return deprecationInfo;
  }

  @Override
  public boolean isDeprecated() {
    return deprecated;
  }

  @Override
  public String getPurposeDescription() {
    return purposeDescription;
  }

  @Override
  public String getLink() {
    return link;
  }

  @Override
  public SimpleRange getSimpleRange() {
    return range;
  }

  public List<String> getExamples() {
    return examples;
  }

  public List<String> getCallOptions() {
    return callOptions;
  }

  public List<ParameterDescription> getParameters() {
    return parameters;
  }

  public List<TypeDescription> getReturnedValue() {
    return returnedValue;
  }
}
