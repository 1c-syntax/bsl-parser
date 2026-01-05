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
import com.github._1c_syntax.bsl.parser.description.MethodDescription;
import com.github._1c_syntax.bsl.parser.description.ParameterDescription;
import com.github._1c_syntax.bsl.parser.description.TypeDescription;
import com.github._1c_syntax.bsl.parser.description.VariableDescription;
import com.github._1c_syntax.bsl.parser.description.support.Hyperlink;
import com.github._1c_syntax.bsl.parser.description.support.SimpleRange;
import lombok.experimental.UtilityClass;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.Trees;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Вспомогательный класс для чтения данных из описания метода
 */
@UtilityClass
public class DescriptionReader {

  private static final int HYPERLINK_REF_LEN = 4;

  public static MethodDescription readMethodDescription(List<Token> comments) {
    var description = comments.stream()
      .map(Token::getText)
      .collect(Collectors.joining("\n"));

    var tokenizer = new BSLMethodDescriptionTokenizer(description);
    var ast = requireNonNull(tokenizer.getAst());

    return MethodDescription.builder()
      .description(description)
      .purposeDescription(DescriptionReader.readPurposeDescription(ast))
      .links(DescriptionReader.readLinks(ast))
      .deprecated(ast.deprecateBlock() != null)
      .deprecationInfo(DescriptionReader.readDeprecationInfo(ast))
      .examples(DescriptionReader.readExamples(ast))
      .parameters(DescriptionReader.readParameters(ast))
      .returnedValue(DescriptionReader.readReturnedValue(ast))
      .range(SimpleRange.create(comments))
      .build();
  }

  public static VariableDescription readVariableDescription(List<Token> comments) {
    return readVariableDescription(comments, Optional.empty());
  }

  public static VariableDescription readVariableDescription(List<Token> comments, Optional<Token> trailingComment) {
    var description = comments.stream()
      .map(Token::getText)
      .collect(Collectors.joining("\n"));

    var tokenizer = new BSLMethodDescriptionTokenizer(description);
    var ast = requireNonNull(tokenizer.getAst());

    return VariableDescription.builder()
      .description(description)
      .purposeDescription(DescriptionReader.readPurposeDescription(ast))
      .links(DescriptionReader.readLinks(ast))
      .deprecated(ast.deprecateBlock() != null)
      .deprecationInfo(DescriptionReader.readDeprecationInfo(ast))
      .range(SimpleRange.create(comments))
      .trailingDescription(trailingComment.map(List::of).map(VariableDescription::create))
      .build();
  }

  /**
   * Выполняет разбор прочитанного AST дерева описания метода и формирует список описаний параметров метода
   *
   * @param ctx Дерево описания метода
   * @return Список описаний параметров метода
   */
  private List<ParameterDescription> readParameters(BSLDescriptionParser.MethodDescriptionContext ctx) {

    // параметров нет
    if (ctx.parameters() == null) {
      return Collections.emptyList();
    }

    // есть только гиперссылка вместо параметров
    if (ctx.parameters().hyperlinkBlock() != null) {
      List<ParameterDescription> result = new ArrayList<>();
      if (ctx.parameters().hyperlinkBlock().hyperlinkType() != null) {
        result.add(new ParameterDescription("",
          Collections.emptyList(),
          getDescriptionString(ctx.parameters().hyperlinkBlock()).substring(HYPERLINK_REF_LEN),
          true));
      }
      return result;
    }

    // блок параметры есть, но самих нет
    if (ctx.parameters().parameterString() == null) {
      return Collections.emptyList();
    }

    return getParametersStrings(ctx.parameters().parameterString());

  }

  /**
   * Выполняет разбор прочитанного AST дерева описания метода и формирует список описаний возвращаемых значений
   *
   * @param ctx Дерево описания метода
   * @return Список описаний возвращаемых значений
   */
  private List<TypeDescription> readReturnedValue(BSLDescriptionParser.MethodDescriptionContext ctx) {

    // возвращаемого значения нет
    if (ctx.returnsValues() == null) {
      return Collections.emptyList();
    }

    // есть только гиперссылка вместо значения
    if (ctx.returnsValues().hyperlinkBlock() != null) {
      List<TypeDescription> result = new ArrayList<>();
      if (ctx.returnsValues().hyperlinkBlock().hyperlinkType() != null) {
        var hyperlink = getDescriptionString(ctx.returnsValues().hyperlinkBlock());
        result.add(new TypeDescription(hyperlink,
          "",
          Collections.emptyList(),
          hyperlink.substring(HYPERLINK_REF_LEN),
          true));
      }
      return result;
    }

    // блок возвращаемого значения есть, но самих нет
    if (ctx.returnsValues().returnsValuesString() == null) {
      return Collections.emptyList();
    }

    var fakeParam = new TempParameterData("");
    var typeStartStringLen = -1;
    for (BSLDescriptionParser.ReturnsValuesStringContext string : ctx.returnsValues().returnsValuesString()) {
      // это строка с возвращаемым значением
      if (string.returnsValue() != null) {
        if (typeStartStringLen == -1 || string.returnsValue().startPart().getText().length() == typeStartStringLen) {
          fakeParam.addType(string.returnsValue().type(), string.returnsValue().typeDescription());
          typeStartStringLen = string.returnsValue().startPart().getText().length();
        } else {
          var text = "";
          if (string.returnsValue().type() != null && string.returnsValue().type().getText() != null) {
            text += string.returnsValue().type().getText();
          }
          if (string.returnsValue().typeDescription() != null && string.returnsValue().typeDescription().getText() != null) {
            text += " - " + string.returnsValue().typeDescription().getText();
          }
          fakeParam.addTypeDescription(text);
        }
      } else if (string.typesBlock() != null) { // это строка с описанием параметра
        fakeParam.addType(string.typesBlock().type(), string.typesBlock().typeDescription());
      } else if (string.typeDescription() != null) { // это строка с описанием
        fakeParam.addTypeDescription(string.typeDescription());
      } else if (string.field() != null) { // это строка с вложенным параметром типа
        fakeParam.addField(string.field());
      } else { // прочее - пустая строка
        // noop
      }
    }

    return fakeParam.makeParameterDescription().getTypes();
  }

  /**
   * Выполняет разбор прочитанного AST дерева описания метода и возвращает описание устаревшего метода
   *
   * @param ctx Дерево описания метода
   * @return Описание устаревшего метода
   */
  private String readDeprecationInfo(BSLDescriptionParser.MethodDescriptionContext ctx) {
    if (ctx.deprecateBlock() != null) {
      var deprecationDescription = ctx.deprecateBlock().deprecateDescription();
      if (deprecationDescription != null) {
        return deprecationDescription.getText().strip();
      }
    }
    return "";
  }

  /**
   * Выполняет разбор прочитанного AST дерева описания метода и возвращает список примеров
   *
   * @param ctx Дерево описания метода
   * @return Список примеров
   */
  private List<String> readExamples(BSLDescriptionParser.MethodDescriptionContext ctx) {
    if (ctx.examples() != null) {
      var strings = ctx.examples().examplesString();
      if (strings != null) {
        return strings.stream()
          .map(DescriptionReader::getDescriptionString)
          .filter((String s) -> !s.isBlank())
          .map(String::intern)
          .collect(Collectors.toList());
      }
    }
    return Collections.emptyList();
  }

  /**
   * Выполняет разбор прочитанного AST дерева описания метода и возвращает описание назначения метода.
   *
   * @param ctx Дерево описания метода
   * @return Описание назначения метода
   */
  private String readPurposeDescription(BSLDescriptionParser.MethodDescriptionContext ctx) {
    if (ctx.descriptionBlock() != null) {
      if (ctx.descriptionBlock().descriptionString() != null) {
        return ctx.descriptionBlock().descriptionString().stream()
          .map(DescriptionReader::getDescriptionString)
          .collect(Collectors.joining("\n"))
          .strip();
      }
    }
    return "";
  }

  private List<Hyperlink> readLinks(BSLDescriptionParser.MethodDescriptionContext ast) {
    Collection<BSLDescriptionParser.HyperlinkContext> links = Trees.findAllRuleNodes(ast, BSLDescriptionParser.RULE_hyperlink);
    if (!links.isEmpty()) {
      return links.stream()
        .map(
          (BSLDescriptionParser.HyperlinkContext hyperlinkContext) -> {
            var link = hyperlinkContext.link == null ? "" : hyperlinkContext.link.getText();
            var params = hyperlinkContext.linkParams == null ? "" : hyperlinkContext.linkParams.getText();
            return Hyperlink.create(link, params);
          })
        .toList();
    }
    return Collections.emptyList();
  }

  private String getDescriptionString(ParserRuleContext ctx) {
    var strings = new StringJoiner("");
    for (var i = 0; i < ctx.getChildCount(); i++) {
      var child = ctx.getChild(i);

      if (!(child instanceof BSLDescriptionParser.StartPartContext)) {
        strings.add(child.getText());
      }
    }

    return strings.toString().strip();
  }

  private List<ParameterDescription> getParametersStrings(List<? extends BSLDescriptionParser.ParameterStringContext> strings) {
    List<ParameterDescription> result = new ArrayList<>();
    var current = new TempParameterData();

    for (BSLDescriptionParser.ParameterStringContext string : strings) {
      // это строка с параметром
      if (string.parameter() != null) {
        if (!current.isEmpty()) {
          result.add(current.makeParameterDescription());
        }
        current = new TempParameterData(string.parameter());
      } else if (string.typesBlock() != null) { // это строка с описанием параметра
        current.addType(string.typesBlock().type(), string.typesBlock().typeDescription());
      } else if (string.typeDescription() != null) { // это строка с описанием
        if (current.isEmpty()) {
          var text = string.typeDescription().getText().strip();
          if (text.split("\\s").length == 1) {
            current = new TempParameterData(text);
          }
        } else {
          current.addTypeDescription(string.typeDescription());
        }
      } else if (string.field() != null) { // это строка с вложенным параметром типа
        current.addField(string.field());
      } else { // прочее - пустая строка
        // noop
      }
    }

    if (!current.isEmpty()) {
      result.add(current.makeParameterDescription());
    }

    return result;
  }


  /**
   * Служебный класс для временного хранения прочитанной информации из описания параметра
   */
  private static final class TempParameterData {
    private String name;
    private boolean empty;
    private final List<TempParameterTypeData> types;
    private int level;

    private TempParameterData() {
      this.name = "";
      this.empty = true;
      this.types = new ArrayList<>();
      this.level = 1;
    }

    private TempParameterData(BSLDescriptionParser.ParameterContext parameter) {
      this();
      if (parameter.parameterName() != null) {
        this.name = parameter.parameterName().getText().strip().intern();
        this.empty = false;
        if (parameter.typesBlock() != null) {
          addType(parameter.typesBlock().type(), parameter.typesBlock().typeDescription());
        }
      }
    }

    private TempParameterData(BSLDescriptionParser.FieldContext fieldContext, int level) {
      this();
      this.level = level;
      if (fieldContext.parameterName() != null) {
        this.name = fieldContext.parameterName().getText().strip().intern();
        this.empty = false;
        if (fieldContext.typesBlock() != null) {
          addType(fieldContext.typesBlock().type(), fieldContext.typesBlock().typeDescription());
        }
      }
    }

    private TempParameterData(String name) {
      this();
      this.name = name.strip().intern();
      this.empty = false;
    }

    private boolean isEmpty() {
      return empty;
    }

    private Optional<TempParameterTypeData> lastType() {
      if (!types.isEmpty()) {
        return Optional.of(types.get(types.size() - 1));
      }
      return Optional.empty();
    }

    private ParameterDescription makeParameterDescription() {
      var parameterTypes = types.stream()
        .map((TempParameterTypeData child) -> {
          List<ParameterDescription> fields = new ArrayList<>();
          if (!child.fields.isEmpty()) {
            child.fields.forEach(field -> fields.add(field.makeParameterDescription()));
          }
          var link = "";
          if (child.isHyperlink) {
            link = child.name.substring(HYPERLINK_REF_LEN);
          }
          return new TypeDescription(
            child.name.intern(),
            child.description.toString(),
            fields,
            link,
            child.isHyperlink
          );
        }).collect(Collectors.toList());
      return new ParameterDescription(name.intern(), parameterTypes, "", false);
    }

    private void addType(@Nullable BSLDescriptionParser.TypeContext paramType,
                         @Nullable BSLDescriptionParser.TypeDescriptionContext paramDescription) {
      if (isEmpty() || paramType == null) {
        return;
      }

      if (paramType.listTypes() != null) {
        var stringTypes = paramType.listTypes().getText().split(",");
        for (String stringType : stringTypes) {
          if (!stringType.isBlank()) {
            addType(paramDescription, stringType.strip(), false);
          }
        }
      } else if (paramType.hyperlinkType() != null) {
        addType(paramDescription, paramType.hyperlinkType().getText(), true);
      } else if (paramType.simpleType() != null) {
        addType(paramDescription, paramType.simpleType().getText(), false);
      } else if (paramType.complexType() != null) {
        addType(paramDescription, paramType.complexType().getText(), false);
      } else {
        // noop
      }
    }

    private void addType(@Nullable BSLDescriptionParser.TypeDescriptionContext descriptionContext,
                         String text,
                         boolean isHyperlink) {
      var newType = new TempParameterTypeData(text, level, isHyperlink);
      if (descriptionContext != null) {
        newType.addTypeDescription(descriptionContext);
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
    private final String name;
    private final StringJoiner description;
    private final int level;
    private final List<TempParameterData> fields;
    private final boolean isHyperlink;

    private TempParameterTypeData(String name, int level, boolean isHyperlink) {
      this.name = name.intern();
      this.description = new StringJoiner("\n");
      this.level = level;
      this.fields = new ArrayList<>();
      this.isHyperlink = isHyperlink;
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
        fields.add(new TempParameterData(fieldContext, level + 1));
      } else {
        lastField().ifPresent(field -> field.addField(fieldContext));
      }
    }
  }
}
