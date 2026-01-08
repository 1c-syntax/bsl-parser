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
import com.github._1c_syntax.bsl.parser.description.HyperlinkTypeDescription;
import com.github._1c_syntax.bsl.parser.description.MethodDescription;
import com.github._1c_syntax.bsl.parser.description.ParameterDescription;
import com.github._1c_syntax.bsl.parser.description.SimpleTypeDescription;
import com.github._1c_syntax.bsl.parser.description.TypeDescription;
import com.github._1c_syntax.bsl.parser.description.support.DescriptionElement;
import com.github._1c_syntax.bsl.parser.description.support.Hyperlink;
import com.github._1c_syntax.bsl.parser.description.support.SimpleRange;
import lombok.Getter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Вспомагательный класс для чтения описания метода.
 */
public class MethodDescriptionReader extends BSLDescriptionParserBaseVisitor<ParseTree> {

  private final MethodDescription.MethodDescriptionBuilder builder;

  /**
   * сдвиг номера строки относительно исходного текста
   */
  private final int lineShift;

  /**
   * сдвиг номера символа относительно исходного текста (только для первой строки)
   */
  private final int firstLineCharShift;

  private TempParameterData lastReadParam = null;
  private int typeLevel = -1;

  private MethodDescriptionReader(SimpleRange range) {
    builder = MethodDescription.builder();
    lineShift = Math.max(0, range.startLine() - 1);
    firstLineCharShift = Math.max(0, range.startCharacter() - 1);
  }

  /**
   * Читает описание метода из списока токенов комментария.
   *
   * @param comments Список токенов комментария.
   * @return Описание метода.
   */
  public static MethodDescription read(List<Token> comments) {
    var description = comments.stream()
      .map(Token::getText)
      .collect(Collectors.joining("\n"));

    return read(description, SimpleRange.create(comments));
  }

  /**
   * Читает описание метода из текста.
   *
   * @param descriptionText Текст описания метода.
   * @param range           Область расположения исходного текста
   * @return Описание метода.
   */
  private static MethodDescription read(String descriptionText, SimpleRange range) {
    var tokenizer = new MethodDescriptionTokenizer(descriptionText);
    var ast = requireNonNull(tokenizer.getAst());

    var reader = new MethodDescriptionReader(range);
    reader.builder
      .description(descriptionText.strip())
      .links(ReaderUtils.readLinks(ast))
      .range(range);
    reader.visitMethodDescription(ast);
    return reader.builder.build();
  }

  @Override
  public ParseTree visitMethodDescription(BSLDescriptionParser.MethodDescriptionContext ctx) {
    if (ctx.parametersBlock() == null) {
      builder.parameters(Collections.emptyList());
    }

    if (ctx.returnsValuesBlock() == null) {
      builder.returnedValue(Collections.emptyList());
    }

    return super.visitMethodDescription(ctx);
  }

  @Override
  public ParseTree visitDeprecateBlock(BSLDescriptionParser.DeprecateBlockContext ctx) {
    builder.deprecated(true);
    builder.keyword(newElement(ctx.DEPRECATE_KEYWORD(), DescriptionElement.Type.DEPRECATE_KEYWORD));
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

  @Override
  public ParseTree visitExamplesBlock(BSLDescriptionParser.ExamplesBlockContext ctx) {
    builder.keyword(newElement(ctx.examplesHead().EXAMPLE_KEYWORD(), DescriptionElement.Type.EXAMPLE_KEYWORD));
    var strings = ctx.examplesString();
    if (strings != null) {
      builder.examples(strings.stream()
        .map(ReaderUtils::extractText)
        .collect(Collectors.joining("\n"))
        .strip());
    }
    return ctx;
  }

  @Override
  public ParseTree visitParametersBlock(BSLDescriptionParser.ParametersBlockContext ctx) {
    builder.keyword(newElement(ctx.parametersHead().PARAMETERS_KEYWORD(), DescriptionElement.Type.PARAMETERS_KEYWORD));
    // блок параметры есть, но самих нет
    if (ctx.parameterString() == null || ctx.parameterString().isEmpty()) {
      builder.parameters(Collections.emptyList());
    } else {
      // идем к строкам
      lastReadParam = TempParameterData.empty();
      super.visitParametersBlock(ctx);
      if (!lastReadParam.isEmpty()) {
        builder.parameter(lastReadParam.build(lineShift, firstLineCharShift));
      }
    }
    return ctx;
  }

  @Override
  public ParseTree visitParameter(BSLDescriptionParser.ParameterContext ctx) {
    if (!lastReadParam.isEmpty()) {
      builder.parameter(lastReadParam.build(lineShift, firstLineCharShift));
    }
    lastReadParam = TempParameterData.create(ctx);
    return super.visitParameter(ctx);
  }

  @Override
  public ParseTree visitTypesBlock(BSLDescriptionParser.TypesBlockContext ctx) {
    if (lastReadParam.isEmpty()) {
      return ctx;
    }
    lastReadParam.addType(ctx.type(), ctx.typeDescription());
    return ctx;
  }

  @Override
  public ParseTree visitField(BSLDescriptionParser.FieldContext ctx) {
    lastReadParam.addField(ctx);
    return ctx;
  }

  @Override
  public ParseTree visitTypeDescription(BSLDescriptionParser.TypeDescriptionContext ctx) {
    if (!lastReadParam.isEmpty()) {
      lastReadParam.addTypeDescription(ctx);
      return ctx;
    }

    if (ctx.hyperlink() != null && !ctx.hyperlink().isEmpty()) { // считаем первой ссылкой
      var link = ctx.hyperlink().get(0);
      lastReadParam = TempParameterData.create(link.link);
      lastReadParam.addType(link, ctx);
      return ctx;
    }

    if (ctx.first != null && ctx.second == null) { // если есть только первый токен, считаем его подходящим
      lastReadParam = TempParameterData.create(ctx.first);
    }
    return ctx;
  }

  @Override
  public ParseTree visitReturnsValuesBlock(BSLDescriptionParser.ReturnsValuesBlockContext ctx) {
    builder.keyword(newElement(ctx.returnsValuesHead().RETURNS_KEYWORD(), DescriptionElement.Type.RETURNS_KEYWORD));
    // блок возвращаемого значения есть, но самих нет
    if (ctx.returnsValuesString() == null) {
      builder.returnedValue(Collections.emptyList());
    } else {
      // идем к строкам
      lastReadParam = TempParameterData.fake(false);
      typeLevel = -1;
      super.visitReturnsValuesBlock(ctx);
      builder.returnedValue(lastReadParam.build(lineShift, firstLineCharShift).types());
    }
    return ctx;
  }

  @Override
  public ParseTree visitReturnsValue(BSLDescriptionParser.ReturnsValueContext ctx) {
    var currentLevel = ((BSLDescriptionParser.ReturnsValuesStringContext) ctx.getParent())
      .startPart().getText().length();

    if (typeLevel == -1 || currentLevel == typeLevel) {
      lastReadParam.addType(ctx.type(), ctx.typeDescription());
      typeLevel = currentLevel;
    } else {
      var text = "";
      if (ctx.type() != null && ctx.type().getText() != null) {
        text += ctx.type().getText();
      }
      if (ctx.typeDescription() != null && ctx.typeDescription().getText() != null) {
        text += " - " + ctx.typeDescription().getText();
      }
      lastReadParam.addTypeDescription(text);
    }
    return ctx;
  }

  private DescriptionElement newElement(TerminalNode node, DescriptionElement.Type type) {
    return newElement(node.getSymbol(), type);
  }

  private DescriptionElement newElement(Token token, DescriptionElement.Type type) {
    return new DescriptionElement(
      SimpleRange.create(token, lineShift, firstLineCharShift),
      type);
  }

  /**
   * Служебный класс для временного хранения прочитанной информации из описания параметра
   */
  private static final class TempParameterData {
    private final String name;
    @Getter
    private final boolean empty;
    private final List<TempParameterTypeData> types;
    private int level;
    private final SimpleRange range;

    private TempParameterData(String name, SimpleRange range, boolean empty) {
      this.types = new ArrayList<>();
      this.level = 1;
      this.name = name.strip().intern();
      this.empty = empty;
      this.range = range;
    }

    private static TempParameterData fake(boolean empty) {
      return new TempParameterData("", SimpleRange.create(0, 0, 0, 0), empty);
    }

    private static TempParameterData empty() {
      return new TempParameterData("", SimpleRange.create(0, 0, 0, 0), true);
    }

    private static TempParameterData create(@Nullable ParserRuleContext ctx) {
      if (ctx == null) {
        return empty();
      } else {
        return new TempParameterData(ctx.getText(), SimpleRange.create(ctx.getTokens()), false);
      }
    }

    private static TempParameterData create(@Nullable Token token) {
      if (token == null) {
        return empty();
      } else {
        return new TempParameterData(token.getText(), SimpleRange.create(token), false);
      }
    }

    private static TempParameterData create(BSLDescriptionParser.ParameterContext parameter) {
      return create(parameter.parameterName());
    }

    private static TempParameterData create(BSLDescriptionParser.FieldContext fieldContext, int level) {
      if (fieldContext.parameterName() == null) {
        return empty();
      } else {
        var fld = create(fieldContext.parameterName());
        fld.level = level;
        if (fieldContext.typesBlock() != null) {
          fld.addType(fieldContext.typesBlock().type(), fieldContext.typesBlock().typeDescription());
        }

        return fld;
      }
    }

    private Optional<TempParameterTypeData> lastType() {
      if (!types.isEmpty()) {
        return Optional.of(types.get(types.size() - 1));
      }
      return Optional.empty();
    }

    private ParameterDescription build(int lineShift, int firstLineCharShift) {
      var newRange = SimpleRange.create(
        range.startLine() + lineShift,
        range.startCharacter() + (range.startLine() == 0 ? firstLineCharShift : 0),
        range.endLine() + lineShift,
        range.endCharacter() + (range.startLine() == 0 ? firstLineCharShift : 0));
      return new ParameterDescription(
        name,
        new DescriptionElement(newRange, DescriptionElement.Type.PARAMETER_NAME),
        types.stream()
          .map(type -> type.build(lineShift, firstLineCharShift))
          .toList()
      );
    }

    private void addType(@Nullable BSLDescriptionParser.TypeContext paramType,
                         @Nullable BSLDescriptionParser.TypeDescriptionContext paramDescription) {
      if (isEmpty() || paramType == null) {
        return;
      }

      if (paramType.listTypes() != null) {
        var firstDescription = paramDescription;
        var stringTypes = paramType.listTypes().listType();
        for (var stringType : stringTypes) {
          if (stringType.hyperlinkType() != null) {
            addType(stringType.hyperlinkType(), firstDescription);
          } else if (stringType.simpleType() != null) {
            addType(stringType.simpleType(), firstDescription);
          } else if (stringType.collectionType() != null) {
            addType(stringType.collectionType(), firstDescription);
          } else {
            // noop
          }
          firstDescription = null;
        }
      } else if (paramType.hyperlinkType() != null) {
        addType(paramType.hyperlinkType(), paramDescription);
      } else if (paramType.simpleType() != null) {
        addType(paramType.simpleType(), paramDescription);
      } else if (paramType.collectionType() != null) {
        addType(paramType.collectionType(), paramDescription);
      } else {
        // noop
      }
    }

    private void addType(BSLDescriptionParser.HyperlinkTypeContext ctx,
                         @Nullable BSLDescriptionParser.TypeDescriptionContext description) {
      if (ctx.hyperlink() != null) {
        addType(ctx.hyperlink(), description);
      }
    }

    private void addType(BSLDescriptionParser.SimpleTypeContext typeContext,
                         @Nullable BSLDescriptionParser.TypeDescriptionContext description) {
      var lastType = new TempParameterTypeData(typeContext.typeName, TypeDescription.Variant.SIMPLE, level);
      if (description != null) {
        lastType.addTypeDescription(description);
      }
      types.add(lastType);
    }

    private void addType(BSLDescriptionParser.HyperlinkContext ctx,
                         @Nullable BSLDescriptionParser.TypeDescriptionContext description) {
      var newType = new TempParameterTypeData(
        ctx.link,
        ctx.linkParams,
        level);
      if (description != null) {
        newType.addTypeDescription(description);
      }
      types.add(newType);
    }

    private void addType(BSLDescriptionParser.CollectionTypeContext typeContext,
                         @Nullable BSLDescriptionParser.TypeDescriptionContext description) {
      var newType = new TempParameterTypeData(typeContext.collection, TypeDescription.Variant.COLLECTION, level);
      if (typeContext.type() != null) {
        newType.addType(typeContext.type());
      }
      if (description != null) {
        newType.addTypeDescription(description);
      }
      types.add(newType);
    }

    private void addTypeDescription(BSLDescriptionParser.TypeDescriptionContext typeDescription) {
      lastType().ifPresent(lastType -> lastType.addTypeDescription(typeDescription));
    }

    private void addTypeDescription(String textDescription) {
      lastType().ifPresent(lastType -> lastType.addTypeDescription(textDescription));
    }

    private void addField(BSLDescriptionParser.FieldContext fieldContext) {
      lastType().ifPresent(lastType -> lastType.addField(fieldContext));
    }
  }

  /**
   * Служебный класс для временного хранения прочитанной информации из описания типа
   */
  private static final class TempParameterTypeData {
    private String name;
    private final StringJoiner description;
    private final int level;
    private final List<TempParameterData> fields;
    private final TypeDescription.Variant variant;
    private TempParameterTypeData valueType;
    private Hyperlink hyperlink;

    private final SimpleRange range;

    private TempParameterTypeData(TypeDescription.Variant variant, int level, SimpleRange range) {
      this.name = "";
      this.variant = variant;
      this.level = level;
      this.description = new StringJoiner("\n");
      this.hyperlink = Hyperlink.EMPTY;
      this.fields = new ArrayList<>();
      this.valueType = null;
      this.range = range;
    }

    private TempParameterTypeData(Token link,
                                  @Nullable Token linkParams,
                                  int level) {
      this(TypeDescription.Variant.HYPERLINK, level, SimpleRange.create(link));
      this.name = link.getText();
      this.hyperlink = Hyperlink.create(this.name, linkParams == null ? "" : linkParams.getText());
    }

    private TempParameterTypeData(Token typeName, TypeDescription.Variant variant, int level) {
      this(variant, level, SimpleRange.create(typeName));
      this.name = typeName.getText();
    }

    private void addTypeDescription(BSLDescriptionParser.TypeDescriptionContext typeDescription) {
      if (typeDescription.getText() != null) {
        var lastField = lastField();
        if (lastField.isPresent()) {
          lastField.get().addTypeDescription(typeDescription);
        } else {
          this.description.add(typeDescription.getText().strip());
        }
      }
    }

    private void addTypeDescription(String textDescription) {
      if (!textDescription.isEmpty()) {
        var lastField = lastField();
        if (lastField.isPresent()) {
          lastField.get().addTypeDescription(textDescription);
        } else {
          this.description.add(textDescription.strip());
        }
      }
    }

    private Optional<TempParameterData> lastField() {
      if (!fields.isEmpty()) {
        return Optional.of(fields.get(fields.size() - 1));
      }
      return Optional.empty();
    }

    private void addField(BSLDescriptionParser.FieldContext fieldContext) {
      var star = fieldContext.getToken(BSLDescriptionParser.STAR, 0);
      if (star == null) {
        return;
      }

      if (star.getText().length() == level) {
        fields.add(TempParameterData.create(fieldContext, level + 1));
      } else {
        lastField().ifPresent(field -> field.addField(fieldContext));
      }
    }

    private void addType(BSLDescriptionParser.TypeContext type) {
      var fakeParam = TempParameterData.fake(false);
      fakeParam.addType(type, null);
      fakeParam.lastType().ifPresent(lastType -> valueType = lastType);
    }

    private TypeDescription build(int lineShift, int firstLineCharShift) {
      var fieldList = fields.stream()
        .map(fld -> fld.build(lineShift, firstLineCharShift))
        .toList();

      var newRange = SimpleRange.create(
        range.startLine() + lineShift,
        range.startCharacter() + (range.startLine() == 0 ? firstLineCharShift : 0),
        range.endLine() + lineShift,
        range.endCharacter() + (range.startLine() == 0 ? firstLineCharShift : 0));

      var element = new DescriptionElement(newRange, DescriptionElement.Type.TYPE_NAME);

      return switch (variant) {
        case SIMPLE -> SimpleTypeDescription.create(name, element, description.toString(), fieldList);
        case COLLECTION -> CollectionTypeDescription.create(
          name,
          element,
          description.toString(),
          valueType == null ? SimpleTypeDescription.EMPTY : valueType.build(lineShift, firstLineCharShift),
          fieldList
        );
        case HYPERLINK -> HyperlinkTypeDescription.create(
          hyperlink,
          element,
          description.toString(),
          fieldList
        );
      };
    }
  }
}
