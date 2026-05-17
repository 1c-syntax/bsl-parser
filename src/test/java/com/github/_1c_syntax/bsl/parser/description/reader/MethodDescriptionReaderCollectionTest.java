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

import com.github._1c_syntax.bsl.parser.BSLParser;
import com.github._1c_syntax.bsl.parser.BSLTokenizer;
import com.github._1c_syntax.bsl.parser.description.CollectionTypeDescription;
import com.github._1c_syntax.bsl.parser.description.MethodDescription;
import com.github._1c_syntax.bsl.parser.description.TypeDescription;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Регресс: для записи {@code Массив из Тип1, Тип2} парсер должен сохранять все
 * value-типы, а не только последний.
 */
class MethodDescriptionReaderCollectionTest {

  private List<Token> getTokens(String example) {
    var tokenizer = new BSLTokenizer(example);
    return tokenizer.getTokens().stream()
      .filter(token -> token.getType() == BSLParser.LINE_COMMENT)
      .collect(Collectors.toList());
  }

  @Test
  void singleValueTypeIsPreserved() {
    var src = "// Возвращаемое значение:\n// Массив из Число\n";
    var description = MethodDescription.create(getTokens(src));
    var returns = description.getReturnedValue();
    assertThat(returns).hasSize(1);
    var collection = (CollectionTypeDescription) returns.getFirst();
    assertThat(collection.collectionName()).isEqualToIgnoringCase("Массив");
    assertThat(collection.valueTypes())
      .extracting(TypeDescription::name)
      .containsExactly("Число");
  }

  @Test
  void multipleValueTypesArePreserved() {
    var src = "// Возвращаемое значение:\n// Массив из Число, Строка\n";
    var description = MethodDescription.create(getTokens(src));
    var returns = description.getReturnedValue();
    assertThat(returns).hasSize(1);
    var collection = (CollectionTypeDescription) returns.getFirst();
    assertThat(collection.collectionName()).isEqualToIgnoringCase("Массив");
    assertThat(collection.valueTypes())
      .as("Массив из Число, Строка → оба value-типа должны быть сохранены")
      .extracting(TypeDescription::name)
      .containsExactly("Число", "Строка");
    assertThat(collection.name()).isEqualToIgnoringCase("Массив<Число, Строка>");
  }
}
