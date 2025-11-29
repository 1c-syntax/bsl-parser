/*
 * This file is a part of BSL Parser.
 *
 * Copyright (c) 2018-2025
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

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@BenchmarkMode(Mode.SampleTime)
@Warmup(iterations = 2)
@Measurement(iterations = 2, batchSize = 2)
@State(Scope.Thread)
public class JMHBSLTokenizerTest {

  private static final String MINIMAL_PROCEDURE = "\nПроцедура Тест()\nКонецПроцедуры\n";

  private String content;
  private String contentWithProcedureAtStart;
  private String contentWithProcedureAtMiddle;
  private String contentWithProcedureAtEnd;
  private BSLTokenizer tokenizer;

  @Setup(Level.Trial)
  public void setup() {
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    try (InputStream inputStream = classLoader.getResourceAsStream("Module.bsl")) {
      assert inputStream != null;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
        content = reader.lines().collect(Collectors.joining("\n"));
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to load Module.bsl from resources", e);
    }

    contentWithProcedureAtStart = MINIMAL_PROCEDURE + content;
    int middleIndex = content.length() / 2;
    int insertPosition = content.indexOf('\n', middleIndex);
    if (insertPosition == -1) {
      insertPosition = middleIndex;
    }
    contentWithProcedureAtMiddle = content.substring(0, insertPosition) + MINIMAL_PROCEDURE + content.substring(insertPosition);
    contentWithProcedureAtEnd = content + MINIMAL_PROCEDURE;

    tokenizer = new BSLTokenizer(content);
  }

  @Benchmark
  public void newTokenizerGetTokensAndAst(Blackhole blackhole) {
    var newTokenizer = new BSLTokenizer(content);
    blackhole.consume(newTokenizer.getTokens());
    blackhole.consume(newTokenizer.getAst());
  }

  @Benchmark
  public void rebuildTokenizerWithSameContent(Blackhole blackhole) {
    var rebuiltTokenizer = new BSLTokenizer(content);
    blackhole.consume(rebuiltTokenizer.getTokens());
    blackhole.consume(rebuiltTokenizer.getAst());
  }

  @Benchmark
  public void insertProcedureAtStart(Blackhole blackhole) {
    var newTokenizer = new BSLTokenizer(contentWithProcedureAtStart);
    blackhole.consume(newTokenizer.getTokens());
    blackhole.consume(newTokenizer.getAst());
  }

  @Benchmark
  public void insertProcedureAtMiddle(Blackhole blackhole) {
    var newTokenizer = new BSLTokenizer(contentWithProcedureAtMiddle);
    blackhole.consume(newTokenizer.getTokens());
    blackhole.consume(newTokenizer.getAst());
  }

  @Benchmark
  public void insertProcedureAtEnd(Blackhole blackhole) {
    var newTokenizer = new BSLTokenizer(contentWithProcedureAtEnd);
    blackhole.consume(newTokenizer.getTokens());
    blackhole.consume(newTokenizer.getAst());
  }
}
