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

import org.antlr.v4.runtime.CharStreams;

public class BSLMethodDescriptionTokenizer extends Tokenizer<BSLMethodDescriptionParser.MethodDescriptionContext, BSLMethodDescriptionParser> {
  public BSLMethodDescriptionTokenizer(String content) {
    super(content + "\n", new BSLMethodDescriptionLexer(CharStreams.fromString(""), true), BSLMethodDescriptionParser.class);
  }

  @Override
  protected BSLMethodDescriptionParser.MethodDescriptionContext rootAST() {
    return parser.methodDescription();
  }
}
