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
import com.github._1c_syntax.bsl.parser.description.MethodDescription;
import com.github._1c_syntax.bsl.parser.description.TypeDescription;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тип, уточнённый ссылкой: {@code СтрокаТабличнойЧасти: См. Справочник.Товары.ЕдиницыИзмерения}.
 * Голова говорит, чем значение является, ссылка — откуда взять его состав.
 */
class TypeWithReferenceTest {

  private List<Token> getTokens(String example) {
    var tokenizer = new BSLTokenizer(example);
    return tokenizer.getTokens().stream()
      .filter(token -> token.getType() == BSLParser.LINE_COMMENT)
      .collect(Collectors.toList());
  }

  @Test
  void typeRefinedByReferenceKeepsBothParts() {
    // given
    var src = "// Параметры:\n//  Объект - СтрокаТабличнойЧасти: См. Справочник.Товары.ЕдиницыИзмерения\n";

    // when
    var description = MethodDescription.create(getTokens(src));

    // then
    assertThat(description.getParameters())
      .singleElement()
      .satisfies(parameter -> {
        assertThat(parameter.name()).isEqualTo("Объект");
        assertThat(parameter.types()).singleElement().satisfies(type -> {
          assertThat(type.variant()).isEqualTo(TypeDescription.Variant.SIMPLE);
          assertThat(type.name()).isEqualTo("СтрокаТабличнойЧасти");
          assertThat(type.reference())
            .isPresent()
            .hasValueSatisfying(reference ->
              assertThat(reference.link()).isEqualTo("Справочник.Товары.ЕдиницыИзмерения"));
        });
      });
  }

  @Test
  void referenceWithoutColonIsNotATypeReference() {
    // given: разделителем в этой записи служит двоеточие, без него это не уточнение типа.
    var src = "// Параметры:\n//  Объект - СтрокаТабличнойЧасти См. Справочник.Товары.ЕдиницыИзмерения\n";

    // when
    var description = MethodDescription.create(getTokens(src));

    // then
    assertThat(description.getParameters())
      .allSatisfy(parameter -> assertThat(parameter.types())
        .allSatisfy(type -> assertThat(type.reference()).isEmpty()));
  }

  @Test
  void plainTypeHasNoReference() {
    // given
    var src = "// Параметры:\n//  Объект - СтрокаТабличнойЧасти\n";

    // when
    var description = MethodDescription.create(getTokens(src));

    // then
    assertThat(description.getParameters())
      .singleElement()
      .satisfies(parameter -> assertThat(parameter.types())
        .singleElement()
        .satisfies(type -> assertThat(type.reference()).isEmpty()));
  }
}
