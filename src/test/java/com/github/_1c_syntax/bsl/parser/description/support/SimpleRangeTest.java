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
package com.github._1c_syntax.bsl.parser.description.support;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleRangeTest {

  @Test
  void testContainsRange() {
    var bigger = new SimpleRange(1, 1, 3, 10);
    var smaller = new SimpleRange(2, 5, 2, 15);

    assertThat(SimpleRange.containsRange(bigger, smaller)).isTrue();

    var notContained = new SimpleRange(4, 1, 5, 10);
    assertThat(SimpleRange.containsRange(bigger, notContained)).isFalse();

    var edgeCase = new SimpleRange(1, 1, 1, 10);
    var edgeSmaller = new SimpleRange(1, 1, 1, 10);
    assertThat(SimpleRange.containsRange(edgeCase, edgeSmaller)).isTrue();

    var edgeCase2 = new SimpleRange(1, 5, 1, 10);
    var edgeSmaller2 = new SimpleRange(1, 1, 1, 10);
    assertThat(SimpleRange.containsRange(edgeCase2, edgeSmaller2)).isFalse();
  }

  @Test
  void testCreateFromTokens() {
    var startToken = new CommonToken(0);
    startToken.setLine(2);
    startToken.setCharPositionInLine(5);

    var endToken = new CommonToken(0);
    endToken.setLine(4);
    endToken.setCharPositionInLine(10);
    endToken.setText("test");

    var range = SimpleRange.create(startToken, endToken);

    assertThat(range.startLine()).isEqualTo(1);
    assertThat(range.startCharacter()).isEqualTo(5);
    assertThat(range.endLine()).isEqualTo(3);
    assertThat(range.endCharacter()).isEqualTo(14);
  }

  @Test
  void testCreateFromToken() {
    var token = new CommonToken(0);
    token.setLine(2);
    token.setCharPositionInLine(5);
    token.setText("test");

    var range = SimpleRange.create(token);

    assertThat(range.startLine()).isEqualTo(1);
    assertThat(range.startCharacter()).isEqualTo(5);
    assertThat(range.endLine()).isEqualTo(1);
    assertThat(range.endCharacter()).isEqualTo(9);
  }

  @Test
  void testCreateFromTokensList() {
    var tokens = new ArrayList<Token>();

    var firstToken = new CommonToken(0);
    firstToken.setLine(2);
    firstToken.setCharPositionInLine(5);

    var lastToken = new CommonToken(0);
    lastToken.setLine(4);
    lastToken.setCharPositionInLine(10);
    lastToken.setText("test");

    tokens.add(firstToken);
    tokens.add(lastToken);

    var range = SimpleRange.create(tokens);

    assertThat(range.startLine()).isEqualTo(1);
    assertThat(range.startCharacter()).isEqualTo(5);
    assertThat(range.endLine()).isEqualTo(3);
    assertThat(range.endCharacter()).isEqualTo(14);

    var emptyRange = SimpleRange.create(new ArrayList<>());
    assertThat(emptyRange.isEmpty()).isTrue();
  }

  @Test
  void testCreateWithCoordinates() {
    var range = SimpleRange.create(1, 5, 3, 10);

    assertThat(range.startLine()).isEqualTo(1);
    assertThat(range.startCharacter()).isEqualTo(5);
    assertThat(range.endLine()).isEqualTo(3);
    assertThat(range.endCharacter()).isEqualTo(10);

    var lineRange = SimpleRange.create(2, 5, 10);
    assertThat(lineRange.startLine()).isEqualTo(2);
    assertThat(lineRange.startCharacter()).isEqualTo(5);
    assertThat(lineRange.endLine()).isEqualTo(2);
    assertThat(lineRange.endCharacter()).isEqualTo(10);
  }

  @Test
  void testIsEmpty() {
    var emptyRange = new SimpleRange(0, 0, 0, 0);
    assertThat(emptyRange.isEmpty()).isTrue();

    var nonEmptyRange = new SimpleRange(0, 0, 0, 1);
    assertThat(nonEmptyRange.isEmpty()).isFalse();
  }

  @Test
  void testEOFTokenHandling() {
    var startToken = new CommonToken(0);
    startToken.setLine(2);
    startToken.setCharPositionInLine(5);

    var eofToken = new CommonToken(Token.EOF);
    eofToken.setLine(4);
    eofToken.setCharPositionInLine(10);

    var range = SimpleRange.create(startToken, eofToken);

    assertThat(range.endCharacter()).isEqualTo(10);
  }

  @Test
  void testConstructor() {
    var range = new SimpleRange(1, 2, 3, 4);
    assertThat(range.startLine()).isEqualTo(1);
    assertThat(range.startCharacter()).isEqualTo(2);
    assertThat(range.endLine()).isEqualTo(3);
    assertThat(range.endCharacter()).isEqualTo(4);
  }

  @Test
  void testEqualsAndHashCode() {
    var range1 = new SimpleRange(1, 2, 3, 4);
    var range2 = new SimpleRange(1, 2, 3, 4);
    var range3 = new SimpleRange(1, 2, 3, 5);

    assertThat(range1)
      .isEqualTo(range2)
      .hasSameHashCodeAs(range2)
      .isNotEqualTo(range3);
  }

  @Test
  void testToString() {
    var range = new SimpleRange(1, 2, 3, 4);
    assertThat(range.toString()).contains("startLine=1", "startCharacter=2", "endLine=3", "endCharacter=4");
  }

  @Test
  void equalsRange() {
    var range1 = new SimpleRange(1, 2, 3, 4);
    var range2 = new SimpleRange(2, 3, 4, 5);
    var range21 = new SimpleRange(2, 3, 4, 5);

    assertThat(range1.equals(range2)).isFalse();
    assertThat(range1.equals(range1)).isTrue();
    assertThat(range2.equals(range21)).isTrue();
    assertThat(range2.equals("range21")).isFalse();
  }

  @Test
  void testCreateWithTokenAndShifts() {
    // given
    var token = new CommonToken(0);
    token.setLine(1);
    token.setCharPositionInLine(5);
    token.setText("test");

    int lineShift = 2;
    int firstLineCharShift = 3;

    // when
    var result = SimpleRange.create(token, lineShift, firstLineCharShift);

    // then
    assertThat(result.startLine()).isEqualTo(2); // 1 - 1 + 2
    assertThat(result.startCharacter()).isEqualTo(8); // 5 + 3
    assertThat(result.endLine()).isEqualTo(2); // same as startLine
    assertThat(result.endCharacter()).isEqualTo(12); // 5 + 3 + 4 (length of "test")
  }

  @Test
  void testCreateWithTokenAndShiftsOnSecondLine() {
    // given
    var token = new CommonToken(0);
    token.setLine(2);
    token.setCharPositionInLine(5);
    token.setText("test");

    int lineShift = 2;
    int firstLineCharShift = 3;

    // when
    var result = SimpleRange.create(token, lineShift, firstLineCharShift);

    // then
    assertThat(result.startLine()).isEqualTo(3); // 2 - 1 + 2
    assertThat(result.startCharacter()).isEqualTo(5); // no shift for non-first line
    assertThat(result.endLine()).isEqualTo(3); // same as startLine
    assertThat(result.endCharacter()).isEqualTo(9); // 5 + 4 (length of "test")
  }

  @Test
  void testCreateWithEOF() {
    // given
    var token = new CommonToken(Token.EOF);
    token.setLine(1);
    token.setCharPositionInLine(5);

    int lineShift = 2;
    int firstLineCharShift = 3;

    // when
    var result = SimpleRange.create(token, lineShift, firstLineCharShift);

    // then
    assertThat(result.startLine()).isEqualTo(2); // 1 - 1 + 2
    assertThat(result.startCharacter()).isEqualTo(8); // 5 + 3
    assertThat(result.endLine()).isEqualTo(2); // same as startLine
    assertThat(result.endCharacter()).isEqualTo(8); // 5 + 3 for EOF
  }
}