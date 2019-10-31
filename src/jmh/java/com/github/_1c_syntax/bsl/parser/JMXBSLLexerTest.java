/*
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018-2019
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

import org.antlr.v4.runtime.Lexer;
import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

@BenchmarkMode(Mode.SampleTime)
@Warmup(iterations = 2) // число итераций для прогрева нашей функции
@Measurement(iterations = 2, batchSize = 2)
@State(Scope.Thread)
public class JMXBSLLexerTest {

  @Param({"BSLLexer"})
  public String lexerClassName;

  private String content;

  public JMXBSLLexerTest() {
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    try (InputStream inputStream = classLoader.getResourceAsStream("Module.bsl")) {
      assert inputStream != null;
      content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Benchmark
  public void testCharStream()
    throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {

    Class<Lexer> lexerClass = (Class<Lexer>) Class.forName("com.github._1c_syntax.bsl.parser." + lexerClassName);
    Lexer lexer = (Lexer) lexerClass.getDeclaredConstructors()[0].newInstance((Object) null);

    Tokenizer tokenizer = new Tokenizer(content, lexer);
    tokenizer.getTokens();
  }
}
