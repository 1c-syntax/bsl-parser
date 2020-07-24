/*
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018-2020
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
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;

import java.io.InputStream;

public class BSLTokenizer extends Tokenizer<BSLParser.FileContext> {
  public BSLTokenizer(String content) {
    super(content, new BSLLexer(CharStreams.fromString(""), true), null, BSLParser.class);
  }

  protected BSLTokenizer(String content, Lexer lexer) {
    super(content, lexer, null, BSLParser.class);
  }

  protected BSLTokenizer(String content, Lexer lexer, Parser parser) {
    super(content, lexer, parser, BSLParser.class);
  }

  protected BSLTokenizer(InputStream content, Lexer lexer) {
    super(content, lexer, null, BSLParser.class);
  }

  protected BSLTokenizer(InputStream content, Lexer lexer, Parser parser) {
    super(content, lexer, parser, parser.getClass());
  }

  @Override
  protected BSLParser.FileContext rootAST() {
    return ((BSLParser) parser).file();
  }

}
