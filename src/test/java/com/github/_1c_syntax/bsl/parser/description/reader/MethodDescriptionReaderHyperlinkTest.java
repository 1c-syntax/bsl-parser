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
import com.github._1c_syntax.bsl.parser.description.HyperlinkTypeDescription;
import com.github._1c_syntax.bsl.parser.description.MethodDescription;
import com.github._1c_syntax.bsl.parser.description.ParameterDescription;
import com.github._1c_syntax.bsl.parser.description.TypeDescription;
import com.github._1c_syntax.bsl.parser.description.support.Hyperlink;
import com.github._1c_syntax.bsl.parser.description.support.SimpleRange;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class MethodDescriptionReaderHyperlinkTest {

  private List<Token> getTokensFromString(String exampleString) {
    var tokenizer = new BSLTokenizer(exampleString);
    return tokenizer.getTokens().stream()
      .filter(token -> token.getType() == BSLParser.LINE_COMMENT)
      .collect(Collectors.toList());
  }

  @Test
  void testHyperlinkInParametersWithShift() {
    var exampleString = "\n\n// Parameters:\n// Parameter1 - &lt;See MyFunc()&gt; Description of parameter";
    var tokens = getTokensFromString(exampleString);
    var methodDescription = MethodDescription.create(tokens);

    assertThat(methodDescription.getParameters()).hasSize(1);
    var parameter = methodDescription.getParameters().get(0);
    assertThat(parameter.types()).hasSize(1);

    var type = parameter.types().get(0);
    assertThat(type).isInstanceOf(HyperlinkTypeDescription.class);

    var hyperlinkType = (HyperlinkTypeDescription) type;
    var hyperlink = hyperlinkType.hyperlink();

    assertThat(hyperlink.link()).isEqualTo("MyFunc");
    assertThat(hyperlink.params()).isEqualTo("");

    // Проверяем, что диапазон гиперссылки учитывает сдвиг строк и символов
    // Гиперссылка должна начинаться с 3-й строки и с 24-го символа (позиция "MyFunc")
    assertThat(hyperlink.range()).isEqualTo(SimpleRange.create(3, 24, 3, 30));
  }

  @Test
  void testHyperlinkInParametersWithLineShift() {
    var exampleString = "\n\n\n// Parameters:\n// Parameter1 - &lt;See MyFunc()&gt; Description of parameter";
    var tokens = getTokensFromString(exampleString);
    var methodDescription = MethodDescription.create(tokens);

    assertThat(methodDescription.getParameters()).hasSize(1);
    var parameter = methodDescription.getParameters().get(0);
    assertThat(parameter.types()).hasSize(1);

    var type = parameter.types().get(0);
    assertThat(type).isInstanceOf(HyperlinkTypeDescription.class);

    var hyperlinkType = (HyperlinkTypeDescription) type;
    var hyperlink = hyperlinkType.hyperlink();

    assertThat(hyperlink.link()).isEqualTo("MyFunc");
    assertThat(hyperlink.params()).isEqualTo("");

    // Проверяем, что диапазон гиперссылки учитывает сдвиг строк и символов
    // Гиперссылка должна начинаться с 4-й строки  и с 24-го символа (позиция "MyFunc")
    assertThat(hyperlink.range()).isEqualTo(SimpleRange.create(4, 24, 4, 30));
  }
}