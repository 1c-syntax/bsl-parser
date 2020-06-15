package com.github._1c_syntax.bsl.parser;

import org.junit.jupiter.api.Test;

class CommentTokenizerTest {

  @Test
  void computeTokens() {
    // given
    CommentTokenizer tokenizer = new CommentTokenizer("Параметры:\n" +
      " Значение - ПеречислениеСсылка - значение\n" +
      "    * перечисления - фв - фыв\n" +
      "      sadasd\n" +
      "        ** asd - a - a\n" +
      "        *** a - a - a\n" +
      "        ** a - a - a\n" +
      "    * a - a - a\n" +
      "Возвращаемое значение:\n" +
      "  Строка - имя переданного значения перечисления\n");

    // when
    BSLCommentParser.DocContext doc = tokenizer.getAst();

    // then
//    assertThat(tokens).hasSize(10);
//    assertThat(tokens.get(9).getType()).isEqualTo(Lexer.EOF);
//    assertThat(tokens.get(9).getChannel()).isEqualTo(Lexer.HIDDEN);
  }

}