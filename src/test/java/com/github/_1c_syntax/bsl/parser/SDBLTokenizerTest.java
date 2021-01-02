/*
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018-2021
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Gryzlov <nixel2007@gmail.com>, Sergey Batanov <sergey.batanov@dmpas.ru>
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
package com.github._1c_syntax.bsl.parser;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SDBLTokenizerTest {

  @Test
  void computeTokens() {
    // given
    SDBLTokenizer tokenizer = new SDBLTokenizer("Выбрать Ссылка Из Справочник.Контрагенты");

    // when
    final List<Token> tokens = tokenizer.getTokens();

    // then
    assertThat(tokens).hasSize(10);
    assertThat(tokens.get(9).getType()).isEqualTo(Lexer.EOF);
    assertThat(tokens.get(9).getChannel()).isEqualTo(Lexer.HIDDEN);
  }

  @Test
  void computeAST() {
    // given
    SDBLTokenizer tokenizer = new SDBLTokenizer("Выбрать Ссылка Из Справочник.Контрагенты");

    // when
    final SDBLParser.QueryPackageContext ast = tokenizer.getAst();

    // then
    List<? extends SDBLParser.QueriesContext> queries = ast.queries();

    assertThat(queries).isNotNull();
    assertThat(queries).hasSize(1);
    SDBLParser.QueriesContext query = queries.get(0);
    assertThat(query.getStart().getType()).isEqualTo(SDBLParser.SELECT);
    assertThat(query.getStop().getType()).isEqualTo(SDBLParser.IDENTIFIER);
  }

}