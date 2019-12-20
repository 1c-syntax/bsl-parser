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

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.PredictionMode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class BSLExtendedParser extends BSLParser {

    CommonTokenStream tokenStream;
    private BSLLexer lexer = new BSLLexer(null);

    public BSLExtendedParser() {
        super(null);
    }

    public BSLExtendedParser(TokenStream input) {
        super(input);
    }

    public BSLParser.FileContext parseFile(Path path) {
        prepareParser(path);
        return file();
    }

    public BSLParser.FileContext parseFile(File file) {
        prepareParser(file.toPath());
        try {
            this.getInterpreter().setPredictionMode(PredictionMode.SLL);
            return file();
        } catch (Exception ex) {
            tokenStream.seek(0); // rewind input stream
            this.reset();
            this.getInterpreter().setPredictionMode(PredictionMode.LL);
        }

        return file();
    }

    private void prepareParser(Path path) {
        this.removeErrorListener(ConsoleErrorListener.INSTANCE);
        CharStream input;

        try (InputStream fis = new BufferedInputStream(new FileInputStream(path.toAbsolutePath().toString()));
             UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(fis)
        ) {

            ubis.skipBOM();

            input =  new CaseChangingCharStream(CharStreams.fromStream(ubis), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        lexer.setInputStream(input);

        tokenStream = new CommonTokenStream(lexer);
        this.setInputStream(tokenStream);
    }
}
