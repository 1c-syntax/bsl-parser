/*
 * This file is a part of BSL Parser.
 *
 * Copyright (c) 2018-2026
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Fedkin <nixel2007@gmail.com> and contributors
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
import com.github._1c_syntax.bsl.parser.description.support.DescriptionElement;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Координаты элементов описания отсчитываются от начала файла на каждой строке,
 * а не только на первой: строки описания могут иметь отступ, и он у каждой свой.
 */
class DescriptionIndentedRangeTest {

  private List<Token> getTokens(String example) {
    var tokenizer = new BSLTokenizer(example);
    return tokenizer.getTokens().stream()
      .filter(token -> token.getType() == BSLParser.LINE_COMMENT)
      .collect(Collectors.toList());
  }

  @Test
  void elementRangesAccountForIndentOfEveryLine() {
    // given: отступ первой строки — 4, второй — 8.
    var src = "    // Параметры:\n        //  Парам - Строка - описание\n";

    // when
    var description = MethodDescription.create(getTokens(src));

    // then: ключевое слово «Параметры:» стоит в столбце 7, имя параметра — в 12.
    assertThat(description.getElements())
      .filteredOn(element -> element.type() == DescriptionElement.Type.PARAMETERS_KEYWORD)
      .singleElement()
      .satisfies(element -> {
        assertThat(element.range().startLine()).isZero();
        assertThat(element.range().startCharacter()).isEqualTo(7);
      });
    assertThat(description.getElements())
      .filteredOn(element -> element.type() == DescriptionElement.Type.PARAMETER_NAME)
      .singleElement()
      .satisfies(element -> {
        assertThat(element.range().startLine()).isEqualTo(1);
        assertThat(element.range().startCharacter()).isEqualTo(12);
      });
  }
}
