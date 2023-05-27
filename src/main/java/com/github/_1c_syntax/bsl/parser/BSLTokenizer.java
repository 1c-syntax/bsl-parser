/*
 * This file is a part of BSL Parser.
 *
 * Copyright (c) 2018-2023
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
package com.github._1c_syntax.bsl.parser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;

public class BSLTokenizer extends Tokenizer<BSLParser.FileContext, BSLParser> {
  public BSLTokenizer(String content) {
    super(content, new BSLLexer(CharStreams.fromString(""), true), BSLParser.class);
  }

  public BSLTokenizer(String content, Lexer lexer) {
    super(content, lexer, BSLParser.class);
  }

  @Override
  protected BSLParser.FileContext rootAST() {
    return parser.file();
  }

}
