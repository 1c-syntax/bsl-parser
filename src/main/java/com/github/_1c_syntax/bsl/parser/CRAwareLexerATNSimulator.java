/*
 * This file is a part of BSL Parser.
 *
 * Copyright (c) 2018-2022
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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.LexerATNSimulator;

public class CRAwareLexerATNSimulator extends LexerATNSimulator {
  public CRAwareLexerATNSimulator(ATN atn) {
    super(atn);
  }

  public CRAwareLexerATNSimulator(Lexer recog, ATN atn) {
    super(recog, atn);
  }

  @Override
  public void consume(CharStream input) {
    int curChar = input.LA(1);
    if (curChar == '\n') {
      line++;
      charPositionInLine = 0;
    } else if (curChar == '\r') {
      int nextChar = input.LA(2);
      if (nextChar != '\n') {
        line++;
        charPositionInLine = 0;
      }
    } else {
      charPositionInLine++;
    }
    input.consume();
  }
}
