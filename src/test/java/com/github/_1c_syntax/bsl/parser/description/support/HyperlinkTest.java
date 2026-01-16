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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HyperlinkTest {

  @Test
  void testCreateWithLinkAndParams() {
    // given
    var link = "example";
    var params = "param1,param2";

    // when
    var hyperlink = Hyperlink.create(link, params);

    // then
    assertThat(hyperlink.link()).isEqualTo(link);
    assertThat(hyperlink.params()).isEqualTo(params);
  }

  @Test
  void testCreateWithNullLink() {
    // given
    String link = null;
    var params = "param1,param2";

    // when
    var hyperlink = Hyperlink.create(link, params);

    // then
    assertThat(hyperlink).isSameAs(Hyperlink.EMPTY);
  }

  @Test
  void testCreateWithEmptyLink() {
    // given
    var link = "";
    var params = "param1,param2";

    // when
    var hyperlink = Hyperlink.create(link, params);

    // then
    assertThat(hyperlink).isSameAs(Hyperlink.EMPTY);
  }

  @Test
  void testCreateWithNullParams() {
    // given
    var link = "example";
    String params = null;

    // when
    var hyperlink = Hyperlink.create(link, params);

    // then
    assertThat(hyperlink.link()).isEqualTo(link);
    assertThat(hyperlink.params()).isEmpty();
  }

  @Test
  void testCreateFromPresentationWithParams() {
    // given
    var presentation = "example(param1,param2)";

    // when
    var hyperlink = Hyperlink.create(presentation);

    // then
    assertThat(hyperlink.link()).isEqualTo("example");
    assertThat(hyperlink.params()).isEqualTo("param1,param2");
  }

  @Test
  void testCreateFromPresentationWithoutParams() {
    // given
    var presentation = "example";

    // when
    var hyperlink = Hyperlink.create(presentation);

    // then
    assertThat(hyperlink.link()).isEqualTo("example");
    assertThat(hyperlink.params()).isEmpty();
  }

  @Test
  void testCreateFromPresentationWithEmptyParams() {
    // given
    var presentation = "example()";

    // when
    var hyperlink = Hyperlink.create(presentation);

    // then
    assertThat(hyperlink.link()).isEqualTo("example");
    assertThat(hyperlink.params()).isEmpty();
  }

  @Test
  void testEqualsWithEqualObject() {
    // given
    var hyperlink1 = Hyperlink.create("example", "param1,param2");
    var hyperlink2 = Hyperlink.create("example", "param1,param2");

    // when
    var result = hyperlink1.equals(hyperlink2);

    // then
    assertThat(result).isTrue();
  }

  @Test
  void testEqualsWithDifferentLink() {
    // given
    var hyperlink1 = Hyperlink.create("example1", "param1,param2");
    var hyperlink2 = Hyperlink.create("example2", "param1,param2");

    // when
    var result = hyperlink1.equals(hyperlink2);

    // then
    assertThat(result).isFalse();
  }

  @Test
  void testEqualsWithDifferentParams() {
    // given
    var hyperlink1 = Hyperlink.create("example", "param1,param2");
    var hyperlink2 = Hyperlink.create("example", "param3,param4");

    // when
    var result = hyperlink1.equals(hyperlink2);

    // then
    assertThat(result).isFalse();
  }

  @Test
  void testCreateWithRange() {
    // given
    var link = "example";
    var params = "param1,param2";
    var range = SimpleRange.create(1, 2, 3, 4);

    // when
    var hyperlink = Hyperlink.create(link, params, range);

    // then
    assertThat(hyperlink.link()).isEqualTo(link);
    assertThat(hyperlink.params()).isEqualTo(params);
    assertThat(hyperlink.range()).isEqualTo(range);
  }

  @Test
  void testEqualsWithDifferentRange() {
    // given
    var range1 = SimpleRange.create(1, 2, 3, 4);
    var range2 = SimpleRange.create(5, 6, 7, 8);
    var hyperlink1 = Hyperlink.create("example", "param1,param2", range1);
    var hyperlink2 = Hyperlink.create("example", "param1,param2", range2);

    // when
    var result = hyperlink1.equals(hyperlink2);

    // then
    assertThat(result).isFalse();
  }

  @Test
  void testEqualsWithSameRange() {
    // given
    var range = SimpleRange.create(1, 2, 3, 4);
    var hyperlink1 = Hyperlink.create("example", "param1,param2", range);
    var hyperlink2 = Hyperlink.create("example", "param1,param2", range);

    // when
    var result = hyperlink1.equals(hyperlink2);

    // then
    assertThat(result).isTrue();
  }

  @Test
  void testEmptyHasZeroRange() {
    // given when
    var hyperlink = Hyperlink.EMPTY;

    // then
    assertThat(hyperlink.link()).isEmpty();
    assertThat(hyperlink.params()).isEmpty();
    assertThat(hyperlink.range()).isEqualTo(SimpleRange.EMPTY);
  }
}
