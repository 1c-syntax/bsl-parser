/*
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018
 * Alexey Sosnoviy <labotamy@yandex.ru>, Nikita Gryzlov <nixel2007@gmail.com>, Sergey Batanov <sergey.batanov@dmpas.ru>
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
package org.github._1c_syntax.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BSLParserRuleContext extends ParserRuleContext {

  public BSLParserRuleContext() {
    super();
  }

  public BSLParserRuleContext(ParserRuleContext parent, int invokingStateNumber) {
    super(parent, invokingStateNumber);
  }

  public List<Token> getTokens() {
    if ( children==null ) {
      return Collections.emptyList();
    }
    List<Token> tokens = new ArrayList<>();
    for (ParseTree tree: children) {
      tokens.addAll(getTokensFromParseTree(tree));
    }

    return tokens;
  }

  private static List<Token> getTokensFromParseTree(ParseTree tree) {
    List<Token> tokens = new ArrayList<>();
    for (int i = 0; i < tree.getChildCount(); i++) {
      ParseTree child = tree.getChild(i);
      if (child instanceof TerminalNode) {
        TerminalNode node = (TerminalNode) child;
        Token token = node.getSymbol();
        tokens.add(token);
      } else {
        tokens.addAll(getTokensFromParseTree(child));
      }
    }
    return tokens;
  }
}
