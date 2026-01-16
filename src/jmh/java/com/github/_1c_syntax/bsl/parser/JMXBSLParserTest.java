/*
 * This file is a part of BSL Parser.
 *
 * Copyright (c) 2018-2026
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
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.util.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

@BenchmarkMode(Mode.SampleTime)
@Warmup(iterations = 2) // число итераций для прогрева нашей функции
@Measurement(iterations = 2, batchSize = 2)
@State(Scope.Thread)
public class JMXBSLParserTest {

  @Param({
    "BSLLexer"
    // , "BSLLexerOld"
  })
  public String lexerClassName;
  @Param({
    "BSLParser"
    //, "BSLParserOld"
  })
  public String parserClassName;
  @Param({"file"})
  public String parserRootASTMethodName;

  private String content;

  public JMXBSLParserTest() {
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    try (InputStream inputStream = classLoader.getResourceAsStream("Module.bsl")) {
      assert inputStream != null;
      content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Benchmark
  public void parserTest()
    throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
    var parserClass = (Class<Parser>) ClassUtils.loadClass(
      "com.github._1c_syntax.bsl.parser." + parserClassName);
    var parserRootASTMethod = parserClass.getMethod(parserRootASTMethodName);
    var lexerClass = (Class<Lexer>) ClassUtils.loadClass(
      "com.github._1c_syntax.bsl.parser." + lexerClassName);
    var lexer = (Lexer) lexerClass.getConstructor(CharStream.class)
      .newInstance(CharStreams.fromString(""));

    var tokenizer = new Tokenizer<>(content, lexer, parserClass) {

      @Override
      protected BSLParserRuleContext rootAST() {
        try {
          return (BSLParserRuleContext) parserRootASTMethod.invoke(parser);
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new RuntimeException("Error: ", e);
        }
      }
    };
    tokenizer.getAst();
  }
}
