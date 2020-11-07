/*
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018-2020
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Gryzlov <nixel2007@gmail.com>, Sergey Batanov <sergey.batanov@dmpas.ru>
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
package com.github._1c_syntax.bsl.parser;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class BSLMethodDescriptionParserTest
  extends AbstractParserTest<BSLMethodDescriptionParser, BSLMethodDescriptionLexer> {

  protected BSLMethodDescriptionParserTest() {
    super(BSLMethodDescriptionParser.class, BSLMethodDescriptionLexer.class);
  }

  @Test
  void testMethodDescription() {
    setInput("");
    assertMatches(parser.methodDescription());

    setInput("Устарела но не\n\n\t34567890-dfghjkl_)(*&^%$#@ Совсем \n" +
      "||Описание метода\n \n" +
      "//Параметры//"
    );
    assertMatches(parser.methodDescription());

    setInput("Устарела.");
    assertMatches(parser.methodDescription());

    setInput("Параметры:");
    assertMatches(parser.methodDescription());

    setInput("Варианты вызова:");
    assertMatches(parser.methodDescription());

    setInput("Пример:");
    assertMatches(parser.methodDescription());

    setInput("Возвращаемое значение:");
    assertMatches(parser.methodDescription());
  }

  @Test
  void testDeprecate() {
    setInput("");
    assertNotMatches(parser.deprecate());
    setInput("Устарела");
    assertMatches(parser.deprecate());
    setInput("Описание\nУстарела");
    assertNotMatches(parser.deprecate());
    setInput("Описание\nУстарела.");
    assertNotMatches(parser.deprecate());

    setInput("Устарела.");
    assertMatches(parser.deprecate());
    assertNotMatches(parser.deprecateDescription());
    setInput("//Устарела.");
    assertMatches(parser.deprecate());
    assertNotMatches(parser.deprecateDescription());
    setInput("Устарела.\n");
    assertMatches(parser.deprecate());
    assertNotMatches(parser.deprecateDescription());
    setInput("//Устарела.\n Использовать другой метод");
    assertMatches(parser.deprecate());
    var nodes = getNodes("//Устарела.\n Использовать другой метод",
      BSLMethodDescriptionParser.RULE_deprecateDescription);
    assertThat(nodes).isEmpty();

    setInput("Устарела. Использовать другой метод");
    assertMatches(parser.deprecate());
    nodes = getNodes("Устарела. Использовать другой метод",
      BSLMethodDescriptionParser.RULE_deprecateDescription);
    assertThat(nodes).hasSize(1);
    assertThat(nodes.get(0).getText()).isEqualTo(" Использовать другой метод");
    nodes = getNodes("Устарела. Использовать другой метод\n",
      BSLMethodDescriptionParser.RULE_deprecateDescription);
    assertThat(nodes).hasSize(1);
    assertThat(nodes.get(0).getText()).isEqualTo(" Использовать другой метод");
    nodes = getNodes("Устарела.Использовать другой метод\n",
      BSLMethodDescriptionParser.RULE_description);
    assertThat(nodes).hasSize(1);
    assertThat(nodes.get(0).getText()).isEqualTo("Устарела.Использовать другой метод\n");
  }

  @Test
  void testDescription() {
    var nodes = getNodes("//Устарела.",
      BSLMethodDescriptionParser.RULE_description);
    assertThat(nodes).isEmpty();

    nodes = getNodes("//Устарела.\nОписание\n\nногостчрочное",
      BSLMethodDescriptionParser.RULE_description);
    assertThat(nodes).hasSize(1);
    nodes = getNodes("//Устарела.\nПараметры:",
      BSLMethodDescriptionParser.RULE_description);
    assertThat(nodes).isEmpty();
    nodes = getNodes("//Устарела.\nCall options:",
      BSLMethodDescriptionParser.RULE_description);
    assertThat(nodes).isEmpty();
    nodes = getNodes("returns:",
      BSLMethodDescriptionParser.RULE_description);
    assertThat(nodes).isEmpty();
    nodes = getNodes("example:",
      BSLMethodDescriptionParser.RULE_description);
    assertThat(nodes).isEmpty();
    nodes = getNodes("Описание \n многострочное:",
      BSLMethodDescriptionParser.RULE_descriptionString);
    assertThat(nodes).hasSize(2);
  }

  @Test
  void testParameters() {
    var nodes = getNodes("//Параметры",
      BSLMethodDescriptionParser.RULE_parameters);
    assertThat(nodes).isEmpty();
    nodes = getNodes("//Параметры:\nПараметр1\n\nПараметр2",
      BSLMethodDescriptionParser.RULE_parameters);
    assertThat(nodes).hasSize(1);
    nodes = getNodes("//Параметры:\nПараметр1\n\nПараметр2",
      BSLMethodDescriptionParser.RULE_parametersString);
    assertThat(nodes).hasSize(2);
    nodes = getNodes("//Параметры:\nПараметр1\n\nПараметр2\nПример:",
      BSLMethodDescriptionParser.RULE_parametersString);
    assertThat(nodes).hasSize(2);
    nodes = getNodes("//Параметры:\nПараметр1\n\nПараметр2\nВарианты вызова:",
      BSLMethodDescriptionParser.RULE_parametersString);
    assertThat(nodes).hasSize(2);
    nodes = getNodes("//Параметры:\nПараметр1\n\nПараметр2\nВозвращаемое значение:",
      BSLMethodDescriptionParser.RULE_parametersString);
    assertThat(nodes).hasSize(2);
    nodes = getNodes("//Параметры:\nПараметр1 - Тип  Описание\n\nПараметр2\nВозвращаемое значение:",
      BSLMethodDescriptionParser.RULE_parametersString);
    assertThat(nodes).hasSize(2);
  }

  @Test
  void testCallOptions() {
    var nodes = getNodes("//Варианты вызова",
      BSLMethodDescriptionParser.RULE_callOptions);
    assertThat(nodes).isEmpty();
    nodes = getNodes("//Call options:\nVar 1\n\nVar 2 bla ba()",
      BSLMethodDescriptionParser.RULE_callOptions);
    assertThat(nodes).hasSize(1);
    nodes = getNodes("//Call options:\nVar 1\n\nVar 2 bla ba()",
      BSLMethodDescriptionParser.RULE_callOptionsString);
    assertThat(nodes).hasSize(2);
    nodes = getNodes("//Call options:\nVar 1\n\nVar 2 bla ba()\nExample:",
      BSLMethodDescriptionParser.RULE_callOptionsString);
    assertThat(nodes).hasSize(2);
    nodes = getNodes("//Call options:\nVar 1\n\nVar 2 bla ba()\nВозвращаемое значение:",
      BSLMethodDescriptionParser.RULE_callOptionsString);
    assertThat(nodes).hasSize(2);
  }

  @Test
  void testReturns() {
    var nodes = getNodes("//returns",
      BSLMethodDescriptionParser.RULE_returnsValues);
    assertThat(nodes).isEmpty();
    nodes = getNodes("returns:\nboolean - description",
      BSLMethodDescriptionParser.RULE_returnsValues);
    assertThat(nodes).hasSize(1);
    nodes = getNodes("returns:\nboolean - description\nExample:",
      BSLMethodDescriptionParser.RULE_returnsValuesString);
    assertThat(nodes).hasSize(1);
    nodes = getNodes("returns:\n - ref - description\n - boolean - description\nExample:",
      BSLMethodDescriptionParser.RULE_returnsValuesString);
    assertThat(nodes).hasSize(2);
  }

  @Test
  void testExample() {
    var nodes = getNodes("//Пример",
      BSLMethodDescriptionParser.RULE_examples);
    assertThat(nodes).isEmpty();
    nodes = getNodes("Пример:\nПример - описаниепримера",
      BSLMethodDescriptionParser.RULE_examples);
    assertThat(nodes).hasSize(1);
    nodes = getNodes("Пример:\nПример: - описаниепримера",
      BSLMethodDescriptionParser.RULE_examplesString);
    assertThat(nodes).hasSize(1);
    nodes = getNodes("Пример:\nПример:\nПример: - описаниепримера",
      BSLMethodDescriptionParser.RULE_examplesString);
    assertThat(nodes).hasSize(2);
  }

  private ArrayList<ParseTree> getNodes(String text, int rule) {
    return new ArrayList<>(Trees.findAllRuleNodes((new BSLMethodDescriptionTokenizer(text)).getAst(), rule));
  }
}
