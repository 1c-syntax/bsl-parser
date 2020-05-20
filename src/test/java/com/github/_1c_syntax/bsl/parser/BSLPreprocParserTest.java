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

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BSLPreprocParserTest {

    private BSLPreprocessorParser parser;

    private void setInput(String inputString) {
        setInput(inputString, BSLLexer.DEFAULT_MODE);
    }

    private void setInput(String inputString, int mode) {
        CharStream input;

        try (
                InputStream inputStream = IOUtils.toInputStream(inputString, StandardCharsets.UTF_8);
                UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(inputStream);
                Reader inputStreamReader = new InputStreamReader(ubis, StandardCharsets.UTF_8)
        ) {
            ubis.skipBOM();
            CodePointCharStream inputTemp = CharStreams.fromReader(inputStreamReader);
            input = new CaseChangingCharStream(inputTemp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BSLLexer lexer = new BSLLexer(input, true);
        lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
        lexer.mode(mode);

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();
        tokenStream.seek(0);

        parser = new BSLPreprocessorParser(tokenStream);
        parser.conditions.push(true);
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
    }

    private CompilationResult setInput(String inputString, List<String> definedSymbols) {
        CharStream input;

        try (
                InputStream content = IOUtils.toInputStream(inputString, StandardCharsets.UTF_8);
                UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(content);
                Reader inputStreamReader = new InputStreamReader(ubis, StandardCharsets.UTF_8)
        ) {
            ubis.skipBOM();
            input = CharStreams.fromReader(inputStreamReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BSLLexer lexer = new BSLLexer(input, true);
        lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);

        CommonTokenStream tempTokenStream = new CommonTokenStream(lexer);
        tempTokenStream.fill();

        List<Token> tokens = tempTokenStream.getTokens();
        List<Token> codeTokens = new ArrayList<>();
        List<List<Token>> regionTokens = new ArrayList<>();
        List<Token> directiveTokens = new ArrayList<>();
        var directiveTokenSource = new ListTokenSource(directiveTokens);
        var directiveTokenStream = new CommonTokenStream(directiveTokenSource, BSLLexer.PREPROCESSOR_MODE);

        // Настройка парсера инструкций препроцессора
        BSLPreprocessorParser preprocessorParser = new BSLPreprocessorParser(null);
        preprocessorParser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        //Добавление списка предопределенных символов
        preprocessorParser.predefinedSymbols.addAll(definedSymbols);
        //По умолчанию токены входят в компилируемый контекст
        preprocessorParser.conditions.push(true);

        int index = 0;
        boolean compiliedTokens = true;
        boolean storeDirective;
        while (index < tokens.size()) {
            var token = tokens.get(index);
            if (token.getType() == BSLLexer.HASH) {
                directiveTokens.clear();
                storeDirective = false;
                int directiveTokenIndex = index + 1;
                // Сбор всех токенов препроцессора
                do {
                    if (directiveTokenIndex < tokens.size()) {
                        Token nextToken = tokens.get(directiveTokenIndex);
                        if (nextToken.getType() == BSLLexer.PREPROC_REGION
                                || nextToken.getType() == BSLLexer.PREPROC_END_REGION) {
                            storeDirective = true;
                        }
                        if (nextToken.getType() != BSLLexer.EOF &&
                                nextToken.getType() != BSLLexer.PREPROC_NEWLINE &&
                                nextToken.getType() != BSLLexer.HASH) {
                            if (nextToken.getChannel() != Lexer.HIDDEN) {
                                directiveTokens.add(nextToken);
                            }
                            directiveTokenIndex++;
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }

                } while (true);

                directiveTokenSource = new ListTokenSource(directiveTokens);
                directiveTokenStream = new CommonTokenStream(directiveTokenSource, BSLLexer.DEFAULT_TOKEN_CHANNEL);
                preprocessorParser.setInputStream(directiveTokenStream);
                preprocessorParser.reset();

                BSLPreprocessorParser.Preprocessor_directiveContext directive = preprocessorParser.preprocessor_directive(true);
                // Если истина следующий код активен согласно директивам компиляции
                compiliedTokens = directive.value;
                if (storeDirective) { // TODO or !compiliedTokens collect only compiled regions
                    regionTokens.add(new ArrayList<>(directiveTokens));
                }
                index = directiveTokenIndex - 1;
            } else if (token.getType() != BSLLexer.PREPROC_NEWLINE &&
                    compiliedTokens) {
                codeTokens.add(token); // сбор активных токенов в компилируемый контекст
            }
            index++;
        }

        //второй этап обработки уже без инструкций препроцессора
        var codeTokenSource = new ListTokenSource(codeTokens);
        var codeTokenStream = new CommonTokenStream(codeTokenSource);
        BSLParser parser = new BSLParser(codeTokenStream);
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        BSLParser.FileContext compilationUnit;
        CompilationResult result = new CompilationResult();
        result.regionTokens = regionTokens;
        try {
            parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
            result.compilationUnit = parser.file();
        } catch (Exception ex) {
            parser.reset(); // rewind input stream
            parser.getInterpreter().setPredictionMode(PredictionMode.LL);
            result.compilationUnit = parser.file();
        }
        return result;
    }

    private void assertMatches(ParseTree tree) throws RecognitionException {

        if (parser.getNumberOfSyntaxErrors() != 0) {
            throw new RecognitionException(
                    "Syntax error while parsing:\n" + parser.getInputStream().getText(),
                    parser,
                    parser.getInputStream(),
                    parser.getContext()
            );
        }

        if (tree instanceof ParserRuleContext) {
            ParserRuleContext ctx = (ParserRuleContext) tree;
            if (ctx.exception != null) {
                throw ctx.exception;
            }

            if (((ParserRuleContext) tree).parent == null) {
                boolean parseSuccess = ((BSLLexer) parser.getInputStream().getTokenSource())._hitEOF;
                if (!parseSuccess) {
                    throw new RecognitionException(
                            "Parse error EOF don't hit\n" + parser.getInputStream().getText(),
                            parser,
                            parser.getInputStream(),
                            parser.getContext()
                    );
                }
            }

            if (tree.getChildCount() == 0 && ((ParserRuleContext) tree).getStart() != null) {
//        throw new RecognitionException(
//          "Node without children and with filled start token\n" + parser.getInputStream().getText(),
//          parser,
//          parser.getInputStream(),
//          parser.getContext()
//        );
            }
        }

        for (int i = 0; i < tree.getChildCount(); i++) {
            ParseTree child = tree.getChild(i);
            assertMatches(child);
        }
    }

    private void assertNotMatches(ParseTree tree) {
        assertThat(tree).satisfiesAnyOf(
                (parseTree) -> assertThat(parseTree.getChildCount()).isEqualTo(0),
                (parseTree) -> assertThrows(RecognitionException.class, () -> assertMatches(tree))
        );
    }

    @Test
    void testFile() {
        String fileContent = "#!os\n" +
                "#Если Сервер Тогда\n" +
                "Перем А; \n" +
                "#Если Клиент Тогда\n" +
                "Перем Б; \n" +
                "#КонецЕсли\n" +
                "#Область Гооо\n" +
                "Процедура В()\n" +
                "КонецПроцедуры\n" +
                "#КонецОбласти\n" +
                "Сообщить();\n" +
                "#КонецЕсли\n";
        var preprocSymbols = Stream.of("Сервер")
                .collect(Collectors.toList());
        var preprocSymbolsClient = Stream.of("Сервер")
                .collect(Collectors.toList());

        var file = setInput(fileContent, preprocSymbolsClient);
        Assertions.assertEquals("<EOF>", file.compilationUnit.getText());
        file = setInput(fileContent, preprocSymbols);
        Assertions.assertNotEquals("", file.compilationUnit.getText());
        Assertions.assertEquals(2, file.regionTokens.size());
        Assertions.assertEquals("Гооо", file.regionTokens.get(0).get(1).getText());
    }

    @Test
    void testFile2() {

        var file = setInput("#!\n" +
                        "#Если Клиент Тогда\n" +
                        "Перем Клиент; \n" +
                        "#Если ВебКлиент Тогда\n" +
                        "Перем ВебКлиент; \n" +
                        "#Если ТОЛСТЫЙКЛИЕНТОБЫЧНОЕПРИЛОЖЕНИЕ Тогда\n" +
                        "Перем ТолстыйКлиент; \n" +
                        "#КонецЕсли\n" +
                        "#Если ВебКлиент И НЕ ТОЛСТЫЙКЛИЕНТОБЫЧНОЕПРИЛОЖЕНИЕ Тогда\n" +
                        "Перем ВебКлиентИНЕТолстыйКлиент; \n" +
                        "#КонецЕсли\n" + "#КонецЕсли\n" +
                        "#Область Гооо\n" +
                        "Процедура В()\n" +
                        "КонецПроцедуры\n" +
                        "#КонецОбласти\n" +
                        "Сообщить();\n" +
                        "#КонецЕсли\n"
                , Stream.of(BSLPreprocessorParser.PREPROC_CLIENT_SYMBOL, BSLPreprocessorParser.PREPROC_WEBCLIENT_SYMBOL)
                        .map(BSLPreprocessorParser.VOCABULARY::getSymbolicName)
                        .collect(Collectors.toList()));

        Assertions.assertEquals("ПеремКлиент;ПеремВебКлиент;ПеремВебКлиентИНЕТолстыйКлиент;ПроцедураВ()КонецПроцедурыСообщить();<EOF>", file.compilationUnit.getText());
    }

    @Test
    void testShebang() {

        setInput("#!");
        assertMatches(parser.shebang());

        setInput("#! А");
        assertMatches(parser.shebang());

        setInput("# А");
        assertNotMatches(parser.shebang());

    }

    @Test
    void testUse() {
        setInput("Использовать lib", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preprocessor_directive(true));

        setInput("Использовать \"./lib\"", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preprocessor_directive(true));

        setInput("Использовать lib-name", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preprocessor_directive(true));

        setInput("Использовать 1lib", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preprocessor_directive(true));
    }

    @Test
    void testPreproc_if() {

        setInput("Если Клиент Тогда", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preprocessor_directive(true));

        setInput("Если НЕ (ТонкийКлиент ИЛИ ВебКлиент) Тогда", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preprocessor_directive(true));

        setInput("Если НЕ (НЕ ТонкийКлиент ИЛИ НЕ ВебКлиент) Тогда", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preprocessor_directive(true));

        setInput("Если ТонкийКлиент И ВебКлиент Тогда", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preprocessor_directive(true));

        setInput("Если", BSLLexer.PREPROCESSOR_MODE);
        assertNotMatches(parser.preprocessor_directive(true));

    }

    @Test
    void testPreproc_elseif() {

        setInput("ИначеЕсли Клиент Тогда", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preprocessor_directive(true));

        setInput("ИначеЕсли", BSLLexer.PREPROCESSOR_MODE);
        assertNotMatches(parser.preprocessor_directive(true));

    }

    @Test
    void testPreproc_else() {

        setInput("Иначе", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preprocessor_directive(true));

        setInput("ИначеЕсли", BSLLexer.PREPROCESSOR_MODE);
        assertNotMatches(parser.preprocessor_directive(true));

    }

    @Test
    void testPreproc_endif() {

        setInput("КонецЕсли", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preprocessor_directive(true));

        setInput("ИначеЕсли", BSLLexer.PREPROCESSOR_MODE);
        assertNotMatches(parser.preprocessor_directive(true));

    }

    @Test
    void testPreproc_Expression() {
        setInput("((((Не (ВебКлиент))) И ((НЕ МобильныйКлиент))))", BSLLexer.PREPROCESSOR_MODE);
    }

    @Test
    void testPreproc_symbol() {

        setInput("Клиент", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preproc_symbol());

        setInput("НаКлиенте", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preproc_symbol());

        setInput("НаСервере", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preproc_symbol());

        setInput("МобильноеПриложениеКлиент", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preproc_symbol());

        setInput("МобильноеПриложениеСервер", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preproc_symbol());

        setInput("МобильныйКлиент", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preproc_symbol());

        setInput("ТолстыйКлиентОбычноеПриложение", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preproc_symbol());

        setInput("ТолстыйКлиентУправляемоеПриложение", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preproc_symbol());

        setInput("Сервер", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preproc_symbol());

        setInput("ВнешнееСоединение", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preproc_symbol());

        setInput("ТонкийКлиент", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preproc_symbol());

        setInput("ВебКлиент", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preproc_symbol());

        setInput("Нечто", BSLLexer.PREPROCESSOR_MODE);
        assertMatches(parser.preproc_symbol());

    }

    @Test
    void TestPreprocessor() {

        setInput("#Область А");
        assertMatches(parser.preprocessor());

        setInput("#КонецОбласти");
        assertMatches(parser.preprocessor());

        setInput("#Если А Тогда");
        assertMatches(parser.preprocessor());

        setInput("#ИначеЕсли А Тогда");
        assertMatches(parser.preprocessor());

        setInput("#Иначе");
        assertMatches(parser.preprocessor());

        setInput("#КонецЕсли");
        assertMatches(parser.preprocessor());

        setInput("#Использовать А");
        assertMatches(parser.preprocessor());

        setInput("#Просто");
        assertNotMatches(parser.preprocessor());

        setInput("Просто");
        assertNotMatches(parser.preprocessor());

    }

    public class CompilationResult {
        public BSLParser.FileContext compilationUnit;
        public List<List<Token>> regionTokens;
    }

}