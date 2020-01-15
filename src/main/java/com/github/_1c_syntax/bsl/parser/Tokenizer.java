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

import com.github._1c_syntax.bsl.parser.util.Lazy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.antlr.v4.runtime.Token.EOF;

public class Tokenizer {

  private InputStream content;
  private Lexer lexer;
  private Lazy<CommonTokenStream> tokenStream = new Lazy<>(this::computeTokenStream);
  private Lazy<List<Token>> tokens = new Lazy<>(this::computeTokens);
  private Lazy<BSLParser.FileContext> ast = new Lazy<>(this::computeAST);

  public Tokenizer(String content) {
    this(content, null);
  }

  protected Tokenizer(String content, Lexer lexer) {
    this(IOUtils.toInputStream(content, StandardCharsets.UTF_8), lexer);
  }

  protected Tokenizer(InputStream content, Lexer lexer) {
    this.content = content;
    this.lexer = lexer;
  }

  public List<Token> getTokens() {
    return tokens.getOrCompute();
  }

  public BSLParser.FileContext getAst() {
    return ast.getOrCompute();
  }

  private List<Token> computeTokens() {
    List<Token> tokensTemp = new ArrayList<>(getTokenStream().getTokens());

    Token lastToken = tokensTemp.get(tokensTemp.size() - 1);
    if (lastToken.getType() == EOF) {
      tokensTemp.remove(tokensTemp.size() - 1);
    }

    return tokensTemp;
  }

  private BSLParser.FileContext computeAST() {
    BSLParser parser = new BSLParser(getTokenStream());
    parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
    try {
      parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
      return parser.file();
    } catch (Exception ex) {
      parser.reset(); // rewind input stream
      parser.getInterpreter().setPredictionMode(PredictionMode.LL);
    }
    return parser.file();
  }

  private CommonTokenStream computeTokenStream() {

    CharStream input;

    try (
      UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(content);
      Reader inputStreamReader = new InputStreamReader(ubis, StandardCharsets.UTF_8);
    ) {
      ubis.skipBOM();
      CodePointCharStream inputTemp = CharStreams.fromReader(inputStreamReader);
      input = new CaseChangingCharStream(inputTemp);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    if (lexer == null) {
      lexer = new BSLLexer(input, true);
    } else {
      lexer.setInputStream(input);
    }
    lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);

    CommonTokenStream tempTokenStream = new CommonTokenStream(lexer);
    tempTokenStream.fill();
    return tempTokenStream;
  }

  private CommonTokenStream getTokenStream() {
    final CommonTokenStream tokenStreamUnboxed = tokenStream.getOrCompute();
    tokenStreamUnboxed.seek(0);
    return tokenStreamUnboxed;
  }

}
