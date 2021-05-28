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