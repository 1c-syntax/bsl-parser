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
import com.github._1c_syntax.bsl.parser.description.support.ParameterDescription;
import com.github._1c_syntax.bsl.parser.description.support.SimpleRange;
import com.github._1c_syntax.bsl.parser.description.support.TypeDescription;
import lombok.Value;
import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Класс-описание метода (процедуры или функции).
 */
@Value
public class MethodDescription implements SourceDefinedSymbolDescription {

  /**
   * Содержит полное описание метода (весь текст).
   */
  String description;

  /**
   * Содержит часть строки после ключевого слова, в которой должно быть
   * описание причины устаревания метода либо альтернативы.
   */
  String deprecationInfo;

  /**
   * Признак устаревания метода.
   */
  boolean deprecated;

  /**
   * Описание назначения метода.
   */
  String purposeDescription;

  /**
   * Примеры использования метода.
   */
  List<String> examples;

  /**
   * Варианты вызова метода.
   */
  List<String> callOptions;

  /**
   * Параметры метода с типами и описанием.
   */
  List<ParameterDescription> parameters;

  /**
   * Возвращаемые значения (типы).
   */
  List<TypeDescription> returnedValue;

  /**
   * Если описание содержит только ссылку, то здесь будет ее значение.
   * <p>
   * TODO Временное решение, надо будет продумать в следующем релизе
   */
  String link;

  /**
   * Диапазон, в котором располагается описание.
   */
  SimpleRange range;

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
  public SimpleRange getSimpleRange() {
    return range;
  }
}
