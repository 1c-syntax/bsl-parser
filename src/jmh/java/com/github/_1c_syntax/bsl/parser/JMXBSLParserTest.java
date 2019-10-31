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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

//@BenchmarkMode(Mode.SampleTime)
//@Warmup(iterations = 2) // число итераций для прогрева нашей функции
//@Measurement(iterations = 2, batchSize = 2)
//@State(Scope.Thread)
public class JMXBSLParserTest {

  //@Param({"true", "false"})
  public boolean liteParser;

  private String content;

  {
    try {
      content = FileUtils.readFileToString(new File("C:/git/1c-syntax/bsl-parser/src/jmh/resources/Module.bsl"), StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //@Benchmark
  public void testCharStream() {
    CommonTokenStream tokenStream = getTokenStream(content);

    BSLParser parser = new BSLParser(tokenStream);
    parser.file();


  }

  private CommonTokenStream getTokenStream(String inputString) {
    Lexer lexer = new BSLLexer(null);
    CharStream input;

    try {
      InputStream inputStream = IOUtils.toInputStream(inputString, StandardCharsets.UTF_8);

      UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(inputStream);
      ubis.skipBOM();

      CharStream inputTemp = CharStreams.fromStream(ubis, StandardCharsets.UTF_8);
      input = new CaseChangingCharStream(inputTemp, true);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    lexer.setInputStream(input);

    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
    tokenStream.fill();
    return tokenStream;
  }
}