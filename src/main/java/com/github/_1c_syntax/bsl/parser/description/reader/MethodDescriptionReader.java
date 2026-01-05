package com.github._1c_syntax.bsl.parser.description.reader;

import com.github._1c_syntax.bsl.parser.BSLDescriptionParser;
import com.github._1c_syntax.bsl.parser.BSLDescriptionParserBaseVisitor;
import com.github._1c_syntax.bsl.parser.description.MethodDescription;
import com.github._1c_syntax.bsl.parser.description.ParameterDescription;
import com.github._1c_syntax.bsl.parser.description.TypeDescription;
import com.github._1c_syntax.bsl.parser.description.support.SimpleRange;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class MethodDescriptionReader extends BSLDescriptionParserBaseVisitor<ParseTree> {

  private final MethodDescription.MethodDescriptionBuilder builder = MethodDescription.builder();
  private TempParameterData lastReadParam = null;
  private int typeLevel = -1;

  public static MethodDescription read(List<Token> comments) {
    var description = comments.stream()
      .map(Token::getText)
      .collect(Collectors.joining("\n"));

    return read(description, SimpleRange.create(comments));
  }

  private static MethodDescription read(String descriptionText, SimpleRange range) {
    var tokenizer = new BSLMethodDescriptionTokenizer(descriptionText);
    var ast = requireNonNull(tokenizer.getAst());

    var reader = new MethodDescriptionReader();
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
    var strings = ctx.examplesString();
    if (strings != null) {
      builder.examples(strings.stream()
        .map(ReaderUtils::extractText)
        .collect(Collectors.joining("\n"))
        .strip());
    } else {
      builder.examples("");
    }
    return ctx;
  }

  @Override
  public ParseTree visitParametersBlock(BSLDescriptionParser.ParametersBlockContext ctx) {
    // блок параметры есть, но самих нет
    if (ctx.parameterString() == null || ctx.parameterString().isEmpty()) {
      builder.parameters(Collections.emptyList());
    } else {
      // идем к строкам
      lastReadParam = new TempParameterData();
      super.visitParametersBlock(ctx);
      if (!lastReadParam.isEmpty()) {
        builder.parameter(lastReadParam.makeParameterDescription());
      }
    }
    return ctx;
  }

  @Override
  public ParseTree visitParameterString(BSLDescriptionParser.ParameterStringContext string) {
    // это строка с параметром
    if (string.parameter() != null) {
      if (!lastReadParam.isEmpty()) {
        builder.parameter(lastReadParam.makeParameterDescription());
      }
      lastReadParam = new TempParameterData(string.parameter());
    } else if (string.typesBlock() != null) { // это строка с описанием параметра
      lastReadParam.addType(string.typesBlock().type(), string.typesBlock().typeDescription());
    } else if (string.typeDescription() != null) { // это строка с описанием
      if (lastReadParam.isEmpty()) {
        var text = string.typeDescription().getText().trim();
        if (!text.isEmpty()) {
          if (text.split("\\s").length == 1) {
            lastReadParam = new TempParameterData(text);
          } else if (string.typeDescription().hyperlink() != null) { // считаем первой ссылкой
            builder.parameter(new ParameterDescription("",
              Collections.emptyList(),
              ReaderUtils.extractText(string.typeDescription().hyperlink().get(0)).substring(4),
              true));
          }
        }
      } else {
        lastReadParam.addTypeDescription(string.typeDescription());
      }
    } else if (string.field() != null) { // это строка с вложенным параметром типа
      lastReadParam.addField(string.field());
    } else { // прочее - пустая строка
      // noop
    }
    return super.visitParameterString(string);
  }

  @Override
  public ParseTree visitReturnsValuesBlock(BSLDescriptionParser.ReturnsValuesBlockContext ctx) {
    // блок возвращаемого значения есть, но самих нет
    if (ctx.returnsValuesString() == null) {
      builder.returnedValue(Collections.emptyList());
    } else {
      // идем к строкам
      lastReadParam = new TempParameterData("");
      typeLevel = -1;
      super.visitReturnsValuesBlock(ctx);
      builder.returnedValue(lastReadParam.makeParameterDescription().getTypes());
    }
    return ctx;
  }

  @Override
  public ParseTree visitReturnsValuesString(BSLDescriptionParser.ReturnsValuesStringContext string) {

    // это строка с возвращаемым значением
    if (string.returnsValue() != null) {
      if (typeLevel == -1 || string.startPart().getText().length() == typeLevel) {
        lastReadParam.addType(string.returnsValue().type(), string.returnsValue().typeDescription());
        typeLevel = string.startPart().getText().length();
      } else {
        var text = "";
        if (string.returnsValue().type() != null && string.returnsValue().type().getText() != null) {
          text += string.returnsValue().type().getText();
        }
        if (string.returnsValue().typeDescription() != null && string.returnsValue().typeDescription().getText() != null) {
          text += " - " + string.returnsValue().typeDescription().getText();
        }
        lastReadParam.addTypeDescription(text);
      }
    } else if (string.typesBlock() != null) { // это строка с описанием параметра
      lastReadParam.addType(string.typesBlock().type(), string.typesBlock().typeDescription());
    } else if (string.typeDescription() != null) { // это строка с описанием
      lastReadParam.addTypeDescription(string.typeDescription());
    } else if (string.field() != null) { // это строка с вложенным параметром типа
      lastReadParam.addField(string.field());
    } else { // прочее - пустая строка
      // noop
    }

    return string;
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
            link = child.name.substring(4);
          }
          return new TypeDescription(
            child.name.intern(),
            child.description.toString().strip(),
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
        var stringTypes = paramType.listTypes().listType();
        for (var stringType : stringTypes) {
          if (!stringType.getText().isBlank()) {
            addType(paramDescription, stringType.getText().strip(), false);
          }
        }
      } else if (paramType.hyperlinkType() != null) {
        addType(paramDescription, paramType.hyperlinkType().getText(), true);
      } else if (paramType.simpleType() != null) {
        addType(paramDescription, paramType.simpleType().typeName.getText(), false);
      } else if (paramType.collectionType() != null) {
        addType(paramDescription, paramType.collectionType().getText(), false);
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
