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
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.atn.PredictionMode;

import java.io.InputStream;

public class SDBLTokenizer extends Tokenizer<SDBLParser.QueryPackageContext> {
  public SDBLTokenizer(String content) {
    super(content, new SDBLLexer(CharStreams.fromString(""), true), null);
  }

  protected SDBLTokenizer(String content, Lexer lexer) {
    super(content, lexer, null);
  }

  protected SDBLTokenizer(String content, Lexer lexer, Parser parser) {
    super(content, lexer, parser);
  }

  protected SDBLTokenizer(InputStream content, Lexer lexer) {
    super(content, lexer, null);
  }

  protected SDBLTokenizer(InputStream content, Lexer lexer, Parser parser) {
    super(content, lexer, parser);
  }

  @Override
  protected SDBLParser.QueryPackageContext computeAST() {
    if (parser == null) {
      parser = new SDBLParser(getTokenStream());
    }

    parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
    try {
      parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
      return ((SDBLParser) parser).queryPackage();
    } catch (Exception ex) {
      parser.reset(); // rewind input stream
      parser.getInterpreter().setPredictionMode(PredictionMode.LL);
    }
    return ((SDBLParser) parser).queryPackage();
  }
}
