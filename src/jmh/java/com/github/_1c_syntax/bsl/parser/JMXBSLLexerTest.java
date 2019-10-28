package com.github._1c_syntax.bsl.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@BenchmarkMode(Mode.SampleTime)
@Warmup(iterations = 2) // число итераций для прогрева нашей функции
@Measurement(iterations = 2, batchSize = 2)
@State(Scope.Thread)
public class JMXBSLLexerTest {

    @Param({"true", "false"})
    public boolean wrapIgnoreCase;

    private String content;
    {
        try {
            content = FileUtils.readFileToString(new File("C:/git/1c-syntax/bsl-parser/src/jmh/resources/Module.bsl"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Benchmark
    public void testCharStream() {
        CommonTokenStream tokenStream = getTokenStream(content, wrapIgnoreCase);

        tokenStream.getTokens();
    }

    private CommonTokenStream getTokenStream(String inputString, boolean wrapIgnoreCase) {
        Lexer lexer;
        if (wrapIgnoreCase) {
            lexer = new BSLLexer(null);
        } else {
            lexer = new OldLexer(null);
        }

        CharStream input;

        try {
            InputStream inputStream = IOUtils.toInputStream(inputString, StandardCharsets.UTF_8);

            UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(inputStream);
            ubis.skipBOM();

            CharStream inputTemp = CharStreams.fromStream(ubis, StandardCharsets.UTF_8);
            if (wrapIgnoreCase) {
                input = new CaseChangingCharStream(inputTemp, true);
            } else {
                input = inputTemp;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        lexer.setInputStream(input);

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();
        return tokenStream;
    }
}