package com.github._1c_syntax.bsl.parser.description.reader;

import com.github._1c_syntax.bsl.parser.BSLDescriptionParser;
import com.github._1c_syntax.bsl.parser.BSLDescriptionParserBaseVisitor;
import com.github._1c_syntax.bsl.parser.description.VariableDescription;
import com.github._1c_syntax.bsl.parser.description.support.SimpleRange;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class VariableDescriptionReader extends BSLDescriptionParserBaseVisitor<ParseTree> {

  private final VariableDescription.VariableDescriptionBuilder builder = VariableDescription.builder();

  public static VariableDescription read(List<Token> comments) {
    return read(comments, Optional.empty());
  }

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
    var tokenizer = new BSLMethodDescriptionTokenizer(descriptionText);
    var ast = requireNonNull(tokenizer.getAst());

    var reader = new VariableDescriptionReader();
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
