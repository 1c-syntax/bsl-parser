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
import com.github._1c_syntax.bsl.parser.description.VariableDescription;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Типовые описания повторяются в тысячах модулей слово в слово, поэтому их тексты у разных
 * описаний — один экземпляр строки.
 */
class DescriptionTextSharingTest {

  private static final String METHOD_DESCRIPTION = """
    // Формирует структуру данных отчета.
    //
    // Параметры:
    //  Объект - Структура - данные отчета
    //
    // Возвращаемое значение:
    //  Строка - адрес во временном хранилище
    """;

  @Test
  void sameMethodDescriptionsShareTexts() {
    // when
    var first = MethodDescription.create(commentsOf(METHOD_DESCRIPTION));
    var second = MethodDescription.create(commentsOf(METHOD_DESCRIPTION));

    // then
    assertThat(second.getDescription()).isEqualTo(first.getDescription()).isSameAs(first.getDescription());
    assertThat(second.getPurposeDescription()).isSameAs(first.getPurposeDescription());
    assertThat(second.getParameters().getFirst().types().getFirst().description())
      .isNotEmpty()
      .isSameAs(first.getParameters().getFirst().types().getFirst().description());
    assertThat(second.getReturnedValue().getFirst().description())
      .isNotEmpty()
      .isSameAs(first.getReturnedValue().getFirst().description());
  }

  @Test
  void sameVariableDescriptionsShareTexts() {
    // given
    var text = "// Кэш настроек отчета\n";

    // when
    var first = VariableDescription.create(commentsOf(text));
    var second = VariableDescription.create(commentsOf(text));

    // then
    assertThat(second.getDescription()).isSameAs(first.getDescription());
    assertThat(second.getPurposeDescription()).isNotEmpty().isSameAs(first.getPurposeDescription());
  }

  private static List<Token> commentsOf(String text) {
    return new BSLTokenizer(text).getTokens().stream()
      .filter(token -> token.getType() == BSLParser.LINE_COMMENT)
      .toList();
  }
}
