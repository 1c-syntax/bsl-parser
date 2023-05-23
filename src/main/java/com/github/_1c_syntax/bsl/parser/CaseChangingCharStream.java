/*
 * This file is a part of BSL Parser.
 *
 * Copyright (c) 2018-2023
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
/*
 Originally released by BSD 3-clause license:

    Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:

     1. Redistributions of source code must retain the above copyright
        notice, this list of conditions and the following disclaimer.
     2. Redistributions in binary form must reproduce the above copyright
        notice, this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.
     3. Neither the name of the copyright holder nor the names of its contributors
        may be used to endorse or promote products derived from this software
        without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
    IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
    OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
    IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
    NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
    THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github._1c_syntax.bsl.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.UnicodeCharStream;
import org.antlr.v4.runtime.misc.Interval;

/**
 * This class supports case-insensitive lexing by wrapping an existing
 * {@link CharStream} and forcing the lexer to see either upper or
 * lowercase characters. Grammar literals should then be either upper or
 * lower case such as 'BEGIN' or 'begin'. The text of the character
 * stream is unaffected. Example: input 'BeGiN' would match lexer rule
 * 'BEGIN' if constructor parameter upper=true but getText() would return
 * 'BeGiN'.
 */
public class CaseChangingCharStream implements UnicodeCharStream {

  private final UnicodeCharStream stream;

  /**
   * Constructs a new CaseChangingCharStream wrapping the given {@link UnicodeCharStream} forcing
   * all characters to upper case.
   *
   * @param stream The stream to wrap.
   */
  public CaseChangingCharStream(UnicodeCharStream stream) {
    this.stream = stream;
  }

  @Override
  public String getText(Interval interval) {
    return stream.getText(interval);
  }

  @Override
  public void consume() {
    stream.consume();
  }

  @Override
  public int LA(int i) {
    int c = stream.LA(i);
    if (c <= 0) {
      return c;
    }
    return Character.toUpperCase(c);
  }

  @Override
  public int mark() {
    return stream.mark();
  }

  @Override
  public void release(int marker) {
    stream.release(marker);
  }

  @Override
  public int index() {
    return stream.index();
  }

  @Override
  public void seek(int index) {
    stream.seek(index);
  }

  @Override
  public int size() {
    return stream.size();
  }

  @Override
  public String getSourceName() {
    return stream.getSourceName();
  }

  @Override
  public boolean supportsUnicodeCodePoints() {
    return stream.supportsUnicodeCodePoints();
  }
}