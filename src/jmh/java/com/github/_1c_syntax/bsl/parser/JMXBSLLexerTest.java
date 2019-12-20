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
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class JMXBSLLexerTest {


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .addProfiler(StackProfiler.class)
                .addProfiler(GCProfiler.class)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Fork(1)
    public void testLexer(Blackhole blackhole, MyState state) throws IOException {
        Tokenizer tokenizer;
        if (state.mode.equals("As stream")) {
            tokenizer = new Tokenizer(Thread.currentThread().getContextClassLoader().getResourceAsStream(state.fileName), state.lexer);
        } else {
            tokenizer = new Tokenizer(state.content, state.lexer);
        }
        blackhole.consume(tokenizer.getTokens());
    }

    @Benchmark
    @Fork(1)
    public void testParser(Blackhole blackhole, MyState state) throws IOException {
        Tokenizer tokenizer;
        if (state.mode.equals("As stream")) {
            tokenizer = new Tokenizer(Thread.currentThread().getContextClassLoader().getResourceAsStream(state.fileName), state.lexer);
        } else {
            tokenizer = new Tokenizer(state.content, state.lexer);
        }
        blackhole.consume(tokenizer.getAst());
    }

    @State(Scope.Thread)
    public static class MyState {

        public String lexerClassName = "BSLLexer";
        @Param({"As stream", "As text"})
        public String mode;
        public Lexer lexer;
        private String fileName = "Module.bsl";
        private String content;

        public String getContent() {
            return content;
        }

        @Setup
        public void init() throws Exception {
            Class<?> lexerClass = (Class<Lexer>) Class.forName("com.github._1c_syntax.bsl.parser." + lexerClassName);
            lexer = (Lexer) lexerClass.getDeclaredConstructors()[0].newInstance((Object) null);
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            content = IOUtils.toString(new BufferedInputStream(classLoader.getResourceAsStream(fileName)), Charset.defaultCharset());
        }
    }
}
