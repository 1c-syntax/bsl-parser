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

import com.github._1c_syntax.utils.Lazy;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Расширение базового класса контекста парсера
 */
public class BSLParserRuleContext extends ParserRuleContext {

  /**
   * Ленивое хранение текста узла
   */
  private final Lazy<String> text = new Lazy<>(super::getText);
  /**
   * Ленивое хранение токенов узла
   */
  private final Lazy<List<Token>> tokens = new Lazy<>(this::computeTokens);

  public BSLParserRuleContext() {
    super();
  }

  public BSLParserRuleContext(ParserRuleContext parent, int invokingStateNumber) {
    super(parent, invokingStateNumber);
  }

  private static void getTokensFromParseTree(ParseTree tree, List<Token> tokens) {
    for (int i = 0; i < tree.getChildCount(); i++) {
      ParseTree child = tree.getChild(i);
      if (child instanceof TerminalNode) {
        TerminalNode node = (TerminalNode) child;
        Token token = node.getSymbol();
        tokens.add(token);
      } else {
        getTokensFromParseTree(child, tokens);
      }
    }
  }

  @Override
  public String getText() {
    return text.getOrCompute();
  }

  public List<Token> getTokens() {
    return tokens.getOrCompute();
  }

  @Override
  public BSLParserRuleContext getParent() {
    return (BSLParserRuleContext) super.getParent();
  }

  @Override
  public BSLParserRuleContext getRuleContext() {
    return (BSLParserRuleContext) super.getRuleContext();
  }

  private List<Token> computeTokens() {
    if (children == null) {
      return Collections.emptyList();
    }

    List<Token> results = new ArrayList<>();
    getTokensFromParseTree(this, results);
    return Collections.unmodifiableList(results);
  }

}
