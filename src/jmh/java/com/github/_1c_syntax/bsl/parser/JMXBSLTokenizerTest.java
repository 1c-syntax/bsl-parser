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
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * JMH benchmark tests for BSLTokenizer.
 * Tests new tokenizer creation, rebuild, and incremental text modifications.
 */
@BenchmarkMode(Mode.SampleTime)
@Warmup(iterations = 1, time = 10)
@Measurement(iterations = 2, time = 10)
@Fork(1)
@State(Scope.Thread)
public class JMXBSLTokenizerTest {

  private static final String MINIMAL_PROCEDURE = "\nПроцедура ТестоваяПроцедура()\n\tВозврат;\nКонецПроцедуры\n";

  private String content;
  private String contentWithProcedureAtBeginning;
  private String contentWithProcedureAtMiddle;
  private String contentWithProcedureAtEnd;
  private BSLTokenizer tokenizerForRebuild;

  public JMXBSLTokenizerTest() {
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    try (InputStream inputStream = classLoader.getResourceAsStream("Module.bsl")) {
      if (inputStream == null) {
        throw new IllegalStateException("Module.bsl resource not found");
      }
      content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read Module.bsl resource", e);
    }

    // Prepare content with procedure inserted at various positions
    contentWithProcedureAtBeginning = MINIMAL_PROCEDURE + content;
    contentWithProcedureAtEnd = content + MINIMAL_PROCEDURE;

    // Insert procedure at the middle of the text
    int middlePosition = content.length() / 2;
    // Find the nearest line break to avoid breaking in the middle of a statement
    int lineBreakPos = content.indexOf('\n', middlePosition);
    if (lineBreakPos == -1) {
      lineBreakPos = middlePosition;
    }
    contentWithProcedureAtMiddle = content.substring(0, lineBreakPos) + MINIMAL_PROCEDURE + content.substring(lineBreakPos);
  }

  @Setup(Level.Iteration)
  public void setupRebuildTokenizer() {
    tokenizerForRebuild = new BSLTokenizer(content);
    // Compute initial tokens and AST to prime the tokenizer
    tokenizerForRebuild.getTokens();
    tokenizerForRebuild.getAst();
  }

  /**
   * Test 1: Create new BSLTokenizer, call getTokens and getAst.
   */
  @Benchmark
  public void newTokenizerGetTokensAndAst() {
    var tokenizer = new BSLTokenizer(content);
    tokenizer.getTokens();
    tokenizer.getAst();
  }

  /**
   * Test 2: Use rebuild with the same text (no new tokenizer creation).
   */
  @Benchmark
  public void rebuildWithSameText() {
    tokenizerForRebuild.rebuild(content);
    tokenizerForRebuild.getTokens();
    tokenizerForRebuild.getAst();
  }

  /**
   * Test 3: Insert minimal procedure at the beginning of the text.
   */
  @Benchmark
  public void insertProcedureAtBeginning() {
    tokenizerForRebuild.rebuild(contentWithProcedureAtBeginning);
    tokenizerForRebuild.getTokens();
    tokenizerForRebuild.getAst();
  }

  /**
   * Test 4: Insert minimal procedure at the middle of the text.
   */
  @Benchmark
  public void insertProcedureAtMiddle() {
    tokenizerForRebuild.rebuild(contentWithProcedureAtMiddle);
    tokenizerForRebuild.getTokens();
    tokenizerForRebuild.getAst();
  }

  /**
   * Test 5: Insert minimal procedure at the end of the text.
   */
  @Benchmark
  public void insertProcedureAtEnd() {
    tokenizerForRebuild.rebuild(contentWithProcedureAtEnd);
    tokenizerForRebuild.getTokens();
    tokenizerForRebuild.getAst();
  }
}
