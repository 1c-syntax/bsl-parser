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
package com.github._1c_syntax.bsl.parser.description.reader;

import com.github._1c_syntax.bsl.parser.BSLDescriptionParser;
import com.github._1c_syntax.bsl.parser.BSLDescriptionParserBaseVisitor;
import com.github._1c_syntax.bsl.parser.description.VariableDescription;
import com.github._1c_syntax.bsl.parser.description.support.DescriptionElement;
import com.github._1c_syntax.bsl.parser.description.support.SimpleRange;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Вспомогательный класс для чтения описания переменной.
 */
public final class VariableDescriptionReader extends BSLDescriptionParserBaseVisitor<ParseTree> {

  private final VariableDescription.VariableDescriptionBuilder builder;

  /**
   * Сдвиг номера строки относительно исходного текста
   */
  private final int lineShift;

  /**
   * Сдвиг номера символа относительно исходного текста (только для первой строки)
   */
  private final int firstLineCharShift;

  private VariableDescriptionReader(SimpleRange range) {
    builder = VariableDescription.builder();
    lineShift = Math.max(0, range.startLine() - 1);
    firstLineCharShift = Math.max(0, range.startCharacter() - 1);
  }

  /**
   * Читает описание переменной из списка токенов комментария.
   *
   * @param comments Список токенов комментария.
   * @return Описание переменной.
   */
  public static VariableDescription read(List<Token> comments) {
    return read(comments, Optional.empty());
  }

  /**
   * Читает описание переменной из списка токенов комментария и токена висячего комментария.
   *
   * @param comments        Список токенов комментария.
   * @param trailingComment Висячий комментарий.
   * @return Описание переменной.
   */
  public static VariableDescription read(List<Token> comments, Optional<Token> trailingComment) {
    var description = comments.stream()
      .map(Token::getText)
      .collect(Collectors.joining("\n"));

    return read(description,
      SimpleRange.create(comments),
      trailingComment.map(List::of).map(VariableDescription::create));
  }

  private static VariableDescription read(String descriptionText,
                                          SimpleRange range,
                                          Optional<VariableDescription> trailingDescription) {
    var tokenizer = new MethodDescriptionTokenizer(descriptionText);
    var ast = requireNonNull(tokenizer.getAst());

    var reader = new VariableDescriptionReader(range);
    reader.builder
      .description(descriptionText.strip())
      .links(ReaderUtils.readLinks(ast))
      .range(range)
      .trailingDescription(trailingDescription);
    reader.visitMethodDescription(ast);
    return reader.builder.build();
  }

  @Override
  public ParseTree visitDeprecateBlock(BSLDescriptionParser.DeprecateBlockContext ctx) {
    builder.deprecated(true);
    builder.element(new DescriptionElement(
      SimpleRange.create(ctx.DEPRECATE_KEYWORD().getSymbol(), lineShift, firstLineCharShift),
      DescriptionElement.Type.DEPRECATE_KEYWORD)
    );
    var deprecationDescription = ctx.deprecateDescription();
    if (deprecationDescription != null) {
      builder.deprecationInfo(deprecationDescription.getText().strip());
    } else {
      builder.deprecationInfo("");
    }
    return ctx;
  }

  @Override
  public ParseTree visitDescriptionBlock(BSLDescriptionParser.DescriptionBlockContext ctx) {
    if (ctx.descriptionString() != null) {
      builder.purposeDescription(ctx.descriptionString().stream()
        .map(ReaderUtils::extractText)
        .collect(Collectors.joining("\n"))
        .strip());
    }

    return ctx;
  }
}
