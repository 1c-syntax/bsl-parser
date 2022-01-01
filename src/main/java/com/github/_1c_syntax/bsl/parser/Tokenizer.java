/*
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018-2022
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
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.antlr.v4.runtime.Token.EOF;

abstract public class Tokenizer<T extends BSLParserRuleContext, P extends Parser> {

  private final InputStream content;
  private final Lexer lexer;
  private final Lazy<CommonTokenStream> tokenStream = new Lazy<>(this::computeTokenStream);
  private final Lazy<List<Token>> tokens = new Lazy<>(this::computeTokens);
  private final Lazy<T> ast = new Lazy<>(this::computeAST);
  private final Class<P> parserClass;
  protected P parser;

  protected Tokenizer(String content, Lexer lexer, Class<P> parserClass) {
    this(IOUtils.toInputStream(content, StandardCharsets.UTF_8), lexer, parserClass);
  }

  protected Tokenizer(InputStream content, Lexer lexer, Class<P> parserClass) {
    requireNonNull(content);
    requireNonNull(lexer);
    this.content = content;
    this.lexer = lexer;
    this.parserClass = parserClass;
  }

  public List<Token> getTokens() {
    return tokens.getOrCompute();
  }

  public T getAst() {
    return ast.getOrCompute();
  }

  private List<Token> computeTokens() {
    List<Token> tokensTemp = new ArrayList<>(getTokenStream().getTokens());

    Token lastToken = tokensTemp.get(tokensTemp.size() - 1);
    if (lastToken.getType() == EOF && lastToken instanceof CommonToken) {
      ((CommonToken) lastToken).setChannel(Lexer.HIDDEN);
    }

    return tokensTemp;
  }

  private T computeAST() {
    parser = createParser(getTokenStream());
    parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
    try {
      parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
      return rootAST();
    } catch (Exception ex) {
      parser.reset(); // rewind input stream
      parser.getInterpreter().setPredictionMode(PredictionMode.LL);
    }
    return rootAST();
  }

  abstract protected T rootAST();

  private CommonTokenStream computeTokenStream() {

    CharStream input;

    try (
      UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(content);
      Reader inputStreamReader = new InputStreamReader(ubis, StandardCharsets.UTF_8)
    ) {
      ubis.skipBOM();
      input = CharStreams.fromReader(inputStreamReader);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    lexer.setInputStream(input);
    lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);

    CommonTokenStream tempTokenStream = new CommonTokenStream(lexer);
    tempTokenStream.fill();
    return tempTokenStream;
  }

  protected CommonTokenStream getTokenStream() {
    final CommonTokenStream tokenStreamUnboxed = tokenStream.getOrCompute();
    tokenStreamUnboxed.seek(0);
    return tokenStreamUnboxed;
  }

  private P createParser(CommonTokenStream tokenStream) {
    try {
      return parserClass.getDeclaredConstructor(TokenStream.class)
        .newInstance(tokenStream);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
}
