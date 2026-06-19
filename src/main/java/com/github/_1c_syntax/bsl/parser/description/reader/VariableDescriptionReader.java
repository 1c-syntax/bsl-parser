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
import com.github._1c_syntax.bsl.parser.description.CollectionTypeDescription;
import com.github._1c_syntax.bsl.parser.description.SimpleTypeDescription;
import com.github._1c_syntax.bsl.parser.description.TypeDescription;
import com.github._1c_syntax.bsl.parser.description.VariableDescription;
import com.github._1c_syntax.bsl.parser.description.support.DescriptionElement;
import com.github._1c_syntax.bsl.parser.description.support.SimpleRange;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
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
    lineShift = Math.max(0, range.startLine());
    firstLineCharShift = Math.max(0, range.startCharacter());
  }

  /**
   * Читает описание переменной из списка токенов комментария.
   *
   * @param comments Список токенов комментария.
   *
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
   *
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
      .links(ReaderUtils.readLinks(ast, reader.lineShift, reader.firstLineCharShift))
      .range(range)
      .trailingDescription(trailingDescription);
    reader.visitMethodDescription(ast);

    // Отдельный разбор первой значимой строки описания для извлечения типа переменной
    // (нотация «тип в начале»). Координаты элементов абсолютные, как и у DEPRECATE_KEYWORD.
    var typeTokenizer = new VariableDescriptionTokenizer(descriptionText);
    var variableType = requireNonNull(typeTokenizer.getAst()).variableType();
    if (variableType != null) {
      reader.readType(variableType);
    }

    return reader.builder.build();
  }

  /**
   * Извлекает типы переменной из разобранной первой строки описания (нотация «тип в начале»),
   * регистрирует их как {@link TypeDescription} (для {@link VariableDescription#getTypes()}) и
   * добавляет соответствующие элементы типа {@link DescriptionElement.Type#TYPE_NAME}
   * (для {@link VariableDescription#getElements()}).
   */
  private void readType(BSLDescriptionParser.VariableTypeContext ctx) {
    var returnsValue = ctx.returnsValue();
    if (returnsValue == null) {
      return;
    }
    for (var type : buildTypes(returnsValue.type())) {
      builder.type(type);
      addTypeElements(type);
    }
  }

  private List<TypeDescription> buildTypes(BSLDescriptionParser.@Nullable TypeContext type) {
    if (type == null) {
      return List.of();
    }
    var listTypes = type.listTypes();
    var collectionType = type.collectionType();
    var simpleType = type.simpleType();
    if (listTypes != null) {
      return listTypes.listType().stream()
        .map(this::buildListType)
        .filter(Objects::nonNull)
        .toList();
    }
    if (collectionType != null) {
      return toList(buildCollectionType(collectionType));
    }
    if (simpleType != null) {
      return toList(buildSimpleType(simpleType));
    }
    // hyperlinkType намеренно пропускается — это ссылка, она уже учтена в links,
    // а не объявление типа переменной.
    return List.of();
  }

  private @Nullable TypeDescription buildListType(BSLDescriptionParser.ListTypeContext ctx) {
    if (ctx.collectionType() != null) {
      return buildCollectionType(ctx.collectionType());
    }
    if (ctx.simpleType() != null) {
      return buildSimpleType(ctx.simpleType());
    }
    return null;
  }

  private @Nullable TypeDescription buildCollectionType(BSLDescriptionParser.CollectionTypeContext ctx) {
    if (ctx.collection == null) {
      return null;
    }
    return CollectionTypeDescription.create(
      ctx.collection.getText(),
      newTypeElement(ctx.collection),
      "",
      buildTypes(ctx.type()),
      List.of());
  }

  private @Nullable TypeDescription buildSimpleType(BSLDescriptionParser.SimpleTypeContext ctx) {
    if (ctx.typeName == null) {
      return null;
    }
    return SimpleTypeDescription.create(
      ctx.typeName.getText(),
      newTypeElement(ctx.typeName),
      "",
      List.of());
  }

  private DescriptionElement newTypeElement(Token token) {
    return new DescriptionElement(
      SimpleRange.create(token, lineShift, firstLineCharShift),
      DescriptionElement.Type.TYPE_NAME);
  }

  /**
   * Добавляет в общий список элементов имя самого типа и имена типов значений коллекции,
   * чтобы {@link VariableDescription#getElements()} покрывал все имена типов в описании
   * (в том числе вложенные в {@code Массив из ...}).
   */
  private void addTypeElements(TypeDescription type) {
    builder.element(type.element());
    if (type instanceof CollectionTypeDescription collection) {
      collection.valueTypes().forEach(this::addTypeElements);
    }
  }

  private static List<TypeDescription> toList(@Nullable TypeDescription type) {
    return type == null ? List.of() : List.of(type);
  }

  @Override
  public ParseTree visitDeprecateBlock(BSLDescriptionParser.DeprecateBlockContext ctx) {
    builder.deprecated(true);
    Optional.ofNullable(ctx.DEPRECATE_KEYWORD())
      .ifPresent(keyword -> builder.element(new DescriptionElement(
        SimpleRange.create(keyword.getSymbol(), lineShift, firstLineCharShift),
        DescriptionElement.Type.DEPRECATE_KEYWORD)
      ));

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
    builder.purposeDescription(ctx.descriptionString().stream()
      .map(ReaderUtils::extractText)
      .collect(Collectors.joining("\n"))
      .strip());

    return ctx;
  }
}
