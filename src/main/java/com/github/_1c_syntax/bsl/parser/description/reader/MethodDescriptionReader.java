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
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Вспомогательный класс для чтения описания метода.
 */
public final class MethodDescriptionReader extends BSLDescriptionParserBaseVisitor<ParseTree> {

  private final MethodDescription.MethodDescriptionBuilder builder;

  /**
   * Сдвиг номера строки относительно исходного текста
   */
  private final int lineShift;

  /**
   * Сдвиг номера символа относительно исходного текста (только для первой строки)
   */
  private final int firstLineCharShift;

  private TempParameterData lastReadParam;
  private int typeLevel = -1;

  private MethodDescriptionReader(SimpleRange range) {
    builder = MethodDescription.builder();
    lineShift = Math.max(0, range.startLine());
    firstLineCharShift = Math.max(0, range.startCharacter());
    lastReadParam = TempParameterData.empty();
  }

  /**
   * Читает описание метода из списка токенов комментария.
   *
   * @param comments Список токенов комментария.
   *
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
   *
   * @return Описание метода.
   */
  private static MethodDescription read(String descriptionText, SimpleRange range) {
    var tokenizer = new MethodDescriptionTokenizer(descriptionText);
    var ast = requireNonNull(tokenizer.getAst());

    var reader = new MethodDescriptionReader(range);
    reader.builder
      .description(descriptionText.strip())
      .links(ReaderUtils.readLinks(ast, reader.lineShift, reader.firstLineCharShift))
      .range(range);
    reader.visitMethodDescription(ast);
    return reader.builder.build();
  }

  @Override
  public @Nullable ParseTree visitMethodDescription(BSLDescriptionParser.MethodDescriptionContext ctx) {
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
    Optional.ofNullable(ctx.DEPRECATE_KEYWORD())
      .ifPresent((TerminalNode keyword) -> {
          builder.keyword(newElement(keyword, DescriptionElement.Type.DEPRECATE_KEYWORD));
          var deprecationDescription = ctx.deprecateDescription();
          if (deprecationDescription != null) {
            builder.deprecationInfo(deprecationDescription.getText().strip());
          } else {
            builder.deprecationInfo("");
          }
        }
      );
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

  @Override
  public ParseTree visitExamplesBlock(BSLDescriptionParser.ExamplesBlockContext ctx) {
    Optional.ofNullable(ctx.examplesHead())
      .map(BSLDescriptionParser.ExamplesHeadContext::EXAMPLE_KEYWORD)
      .ifPresent((TerminalNode keyword) -> {
          builder.keyword(newElement(keyword, DescriptionElement.Type.EXAMPLE_KEYWORD));
          builder.examples(ctx.examplesString().stream()
            .map(ReaderUtils::extractText)
            .collect(Collectors.joining("\n"))
            .strip());
        }
      );
    return ctx;
  }

  @Override
  public ParseTree visitCallOptionsBlock(BSLDescriptionParser.CallOptionsBlockContext ctx) {
    Optional.ofNullable(ctx.callOptionsHead())
      .map(BSLDescriptionParser.CallOptionsHeadContext::CALL_OPTIONS_KEYWORD)
      .ifPresent((TerminalNode keyword) -> {
          builder.keyword(newElement(keyword, DescriptionElement.Type.CALL_OPTIONS_KEYWORD));
          builder.callOptions(ctx.callOptionsString().stream()
            .map(ReaderUtils::extractText)
            .collect(Collectors.joining("\n"))
            .strip());
        }
      );
    return ctx;
  }

  @Override
  public ParseTree visitParametersBlock(BSLDescriptionParser.ParametersBlockContext ctx) {
    Optional.ofNullable(ctx.parametersHead())
      .map(BSLDescriptionParser.ParametersHeadContext::PARAMETERS_KEYWORD)
      .ifPresent((TerminalNode keyword) -> {
          builder.keyword(newElement(keyword, DescriptionElement.Type.PARAMETERS_KEYWORD));
          // блок параметры есть, но самих нет
          if (ctx.parameterString().isEmpty()) {
            builder.parameters(Collections.emptyList());
          } else {
            // идем к строкам
            lastReadParam = TempParameterData.empty();
            super.visitParametersBlock(ctx);
            if (!lastReadParam.isEmpty()) {
              builder.parameter(lastReadParam.build(lineShift, firstLineCharShift));
            }
          }
        }
      );
    return ctx;
  }

  @Override
  public @Nullable ParseTree visitParameter(BSLDescriptionParser.ParameterContext ctx) {
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

    if (!ctx.hyperlink().isEmpty()) { // считаем первой ссылкой
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
    Optional.ofNullable(ctx.returnsValuesHead())
      .map(BSLDescriptionParser.ReturnsValuesHeadContext::RETURNS_KEYWORD)
      .ifPresent((TerminalNode keyword) -> {
          builder.keyword(newElement(keyword, DescriptionElement.Type.RETURNS_KEYWORD));
          // блок возвращаемого значения есть, но самих нет
          if (ctx.returnsValuesString().isEmpty()) {
            builder.returnedValue(Collections.emptyList());
          } else {
            // идем к строкам
            lastReadParam = TempParameterData.fake();
            typeLevel = -1;
            super.visitReturnsValuesBlock(ctx);
            builder.returnedValue(lastReadParam.build(lineShift, firstLineCharShift).types());
          }
        }
      );
    return ctx;
  }

  @Override
  public ParseTree visitReturnsValue(BSLDescriptionParser.ReturnsValueContext ctx) {
    Optional.ofNullable((BSLDescriptionParser.ReturnsValuesStringContext) ctx.getParent())
      .map(BSLDescriptionParser.ReturnsValuesStringContext::startPart)
      .ifPresent((BSLDescriptionParser.StartPartContext startPart) -> {

          var currentLevel = startPart.getText().length();
          var type = ctx.type();
          var description = ctx.typeDescription();
          if (typeLevel == -1 || currentLevel == typeLevel) {
            lastReadParam.addType(type, description);
            typeLevel = currentLevel;
          } else {
            var text = "";
            if (type != null) {
              text += type.getText();
            }
            if (description != null) {
              text += " - " + description.getText();
            }
            lastReadParam.addTypeDescription(text);
          }
        }
      );
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

    private static TempParameterData fake() {
      return new TempParameterData("", SimpleRange.EMPTY, false);
    }

    private static TempParameterData empty() {
      return new TempParameterData("", SimpleRange.EMPTY, true);
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
        var typesBlockContext = fieldContext.typesBlock();
        if (typesBlockContext != null) {
          fld.addType(typesBlockContext.type(), typesBlockContext.typeDescription());
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
      var newRange = SimpleRange.shift(range, lineShift, firstLineCharShift);
      return new ParameterDescription(
        name,
        new DescriptionElement(newRange, DescriptionElement.Type.PARAMETER_NAME),
        types.stream()
          .map(type -> type.build(lineShift, firstLineCharShift))
          .toList()
      );
    }

    private void addType(BSLDescriptionParser.@Nullable TypeContext paramType,
                         BSLDescriptionParser.@Nullable TypeDescriptionContext paramDescription) {
      if (isEmpty() || paramType == null) {
        return;
      }

      var listTypes = paramType.listTypes();
      var hyperlinkType = paramType.hyperlinkType();
      var simpleType = paramType.simpleType();
      var collectionType = paramType.collectionType();
      if (listTypes != null) {
        var firstDescription = paramDescription;
        var stringTypes = listTypes.listType();
        for (var stringType : stringTypes) {
          addType(stringType, firstDescription);
          firstDescription = null;
        }
      } else if (hyperlinkType != null) {
        addType(hyperlinkType, paramDescription);
      } else if (simpleType != null) {
        addType(simpleType, paramDescription);
      } else if (collectionType != null) {
        addType(collectionType, paramDescription);
      } else {
        // noop
      }
    }

    private void addType(BSLDescriptionParser.ListTypeContext paramType,
                         BSLDescriptionParser.@Nullable TypeDescriptionContext paramDescription) {
      var hyperlinkType = paramType.hyperlinkType();
      var simpleType = paramType.simpleType();
      var collectionType = paramType.collectionType();
      if (hyperlinkType != null) {
        addType(hyperlinkType, paramDescription);
      } else if (simpleType != null) {
        addType(simpleType, paramDescription);
      } else if (collectionType != null) {
        addType(collectionType, paramDescription);
      } else {
        // noop
      }
    }

    private void addType(BSLDescriptionParser.HyperlinkTypeContext ctx,
                         BSLDescriptionParser.@Nullable TypeDescriptionContext description) {
      var hyperlink = ctx.hyperlink();
      if (hyperlink != null) {
        addType(hyperlink, description);
      }
    }

    private void addType(BSLDescriptionParser.SimpleTypeContext typeContext,
                         BSLDescriptionParser.@Nullable TypeDescriptionContext description) {
      if (typeContext.typeName != null) {
        var lastType = new TempParameterTypeData(typeContext.typeName, TypeDescription.Variant.SIMPLE, level);
        if (description != null) {
          lastType.addTypeDescription(description);
        }
        types.add(lastType);
      }
    }

    private void addType(BSLDescriptionParser.HyperlinkContext ctx,
                         BSLDescriptionParser.@Nullable TypeDescriptionContext description) {
      if (ctx.link != null) {
        var newType = new TempParameterTypeData(
          ctx.link,
          ctx.linkParams,
          level);
        if (description != null) {
          newType.addTypeDescription(description);
        }
        types.add(newType);
      }
    }

    private void addType(BSLDescriptionParser.CollectionTypeContext typeContext,
                         BSLDescriptionParser.@Nullable TypeDescriptionContext description) {
      if (typeContext.collection != null) {
        var newType = new TempParameterTypeData(typeContext.collection, TypeDescription.Variant.COLLECTION, level);
        var type = typeContext.type();
        if (type != null) {
          newType.addType(type);
        }
        if (description != null) {
          newType.addTypeDescription(description);
        }
        types.add(newType);
      }
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
    private @Nullable TempParameterTypeData valueType;
    private @Nullable Token linkParamsToken;

    private final SimpleRange range;

    private TempParameterTypeData(TypeDescription.Variant variant, int level, SimpleRange range) {
      this.name = "";
      this.variant = variant;
      this.level = level;
      this.description = new StringJoiner("\n");
      this.linkParamsToken = null;
      this.fields = new ArrayList<>();
      this.valueType = null;
      this.range = range;
    }

    private TempParameterTypeData(Token link,
                                  @Nullable Token linkParams,
                                  int level) {
      this(TypeDescription.Variant.HYPERLINK, level, SimpleRange.create(link));
      this.name = link.getText();
      this.linkParamsToken = linkParams;
    }

    private TempParameterTypeData(Token typeName, TypeDescription.Variant variant, int level) {
      this(variant, level, SimpleRange.create(typeName));
      this.name = typeName.getText();
    }

    private void addTypeDescription(BSLDescriptionParser.TypeDescriptionContext typeDescription) {
      var lastField = lastField();
      if (lastField.isPresent()) {
        lastField.get().addTypeDescription(typeDescription);
      } else {
        this.description.add(typeDescription.getText().strip());
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
      var fakeParam = TempParameterData.fake();
      fakeParam.addType(type, null);
      fakeParam.lastType().ifPresent(lastType -> valueType = lastType);
    }

    private TypeDescription build(int lineShift, int firstLineCharShift) {
      var fieldList = fields.stream()
        .map(fld -> fld.build(lineShift, firstLineCharShift))
        .toList();

      var newRange = SimpleRange.shift(range, lineShift, firstLineCharShift);
      var element = new DescriptionElement(newRange, DescriptionElement.Type.TYPE_NAME);

      return switch (variant) {
        case SIMPLE -> SimpleTypeDescription.create(name, element, description.toString(), fieldList);
        case COLLECTION -> CollectionTypeDescription.create(
          name, element, description.toString(),
          valueType == null ? SimpleTypeDescription.EMPTY : valueType.build(lineShift, firstLineCharShift),
          fieldList
        );
        case HYPERLINK -> HyperlinkTypeDescription.create(
          Hyperlink.create(name, linkParamsToken == null ? "" : linkParamsToken.getText(), newRange),
          element,
          description.toString(),
          fieldList
        );
      };
    }
  }
}
