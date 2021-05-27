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
package com.github._1c_syntax.bsl.parser.description.support;

import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import java.util.List;

public final class Ranges {

  Ranges() {
    // utility class
  }

  public static Range create(Token startToken, Token endToken) {
    int startLine = startToken.getLine() - 1;
    int startChar = startToken.getCharPositionInLine();
    int endLine = endToken.getLine() - 1;
    int endChar;
    if (endToken.getType() == Token.EOF) {
      endChar = endToken.getCharPositionInLine();
    } else {
      endChar = endToken.getCharPositionInLine() + endToken.getText().length();
    }

    return new Range(new Position(startLine, startChar), new Position(endLine, endChar));
  }

  public static Range create(List<Token> tokens) {
    if (tokens.isEmpty()) {
      return new Range(new Position(0, 0), new Position(0, 0));
    }
    var firstElement = tokens.get(0);
    var lastElement = tokens.get(tokens.size() - 1);

    return create(firstElement, lastElement);
  }

}
