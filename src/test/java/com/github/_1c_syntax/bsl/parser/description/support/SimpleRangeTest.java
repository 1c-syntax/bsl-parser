/*
 * This file is a part of BSL Parser.
 *
 * Copyright (c) 2018-2024
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

class SimpleRangeTest {

  @Test
  void containsRange() {
    var range1 = new SimpleRange(1, 2, 3, 4);
    var range2 = new SimpleRange(2, 3, 4, 5);
    var range3 = new SimpleRange(0, 0, 6, 6);

    assertThat(SimpleRange.containsRange(range1, range2)).isFalse();
    assertThat(SimpleRange.containsRange(range1, range3)).isFalse();
    assertThat(SimpleRange.containsRange(range2, range1)).isFalse();
    assertThat(SimpleRange.containsRange(range2, range3)).isFalse();
    assertThat(SimpleRange.containsRange(range3, range1)).isTrue();
    assertThat(SimpleRange.containsRange(range3, range2)).isTrue();
  }
}