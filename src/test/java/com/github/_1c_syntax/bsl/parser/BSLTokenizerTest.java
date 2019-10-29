/*
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018-2019
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

import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

class BSLTokenizerTest {

    @Test
    void computeTokens() {

        BSLTokenizer tokenizer = new BSLTokenizer("Если Условие() Тогда");
        final List<Token> tokens = tokenizer.computeTokens();
        assert tokens.size() == 7;

    }

    @Test
    void computeAST() {

        BSLTokenizer tokenizer = new BSLTokenizer("Если Условие() Тогда");
        final BSLParser.FileContext ast = tokenizer.computeAST();
        assert ast.children.size() == 7;
        assert ast.getStart().getType() == BSLParser.IF_KEYWORD;
        assert ast.getStop().getType() == BSLParser.THEN_KEYWORD;

    }

}