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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Разделителем имени и секции типов служит дефис-минус, а вместе с ним — видимые виды
 * тире, которые редакторы и автозамена подставляют вместо него. Разбор описания от
 * выбора символа не зависит.
 */
class DescriptionSplitterDashTest {

  private static List<Token> getTokens(String example) {
    var tokenizer = new BSLTokenizer(example);
    return tokenizer.getTokens().stream()
      .filter(token -> token.getType() == BSLParser.LINE_COMMENT)
      .collect(Collectors.toList());
  }

  @ParameterizedTest(name = "разделитель {0}")
  @ValueSource(strings = {"-", "‒", "–", "—", "―", "−"})
  void dashSplitsParameterFromItsTypes(String dash) {
    // given
    var src = "// Параметры:\n//  Объект " + dash + " СправочникОбъект.Справочник1\n";

    // when
    var description = MethodDescription.create(getTokens(src));

    // then
    var params = description.getParameters();
    assertThat(params).hasSize(1);
    assertThat(params.getFirst().name()).isEqualToIgnoringCase("Объект");
    assertThat(params.getFirst().types())
      .extracting(TypeDescription::name)
      .containsExactly("СправочникОбъект.Справочник1");
  }

  @ParameterizedTest(name = "разделитель {0}")
  @ValueSource(strings = {"-", "‒", "–", "—", "―", "−"})
  void dashSplitsReturnedValueFromItsDescription(String dash) {
    // given
    var src = "// Возвращаемое значение:\n//  Строка " + dash + " текст сообщения\n";

    // when
    var description = MethodDescription.create(getTokens(src));

    // then
    assertThat(description.getReturnedValue())
      .extracting(TypeDescription::name)
      .containsExactly("Строка");
  }
}
