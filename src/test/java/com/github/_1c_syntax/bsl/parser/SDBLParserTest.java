/*
 * This file is a part of BSL Parser.
 *
 * Copyright (c) 2018-2022
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
package com.github._1c_syntax.bsl.parser;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;
import org.junit.jupiter.api.Test;
import utils.TestUtils;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class SDBLParserTest extends AbstractParserTest<SDBLParser, SDBLLexer> {

  protected SDBLParserTest() {
    super(SDBLParser.class, SDBLLexer.class);
  }

  @Test
  void testDrop() {
    checkSource("src/test/resources/sdbl/drop.sdbl",
      pair(SDBLParser.RULE_queries, 3),
      pair(SDBLParser.RULE_dropTableQuery, 3),
      pair(SDBLParser.RULE_selectQuery, 0),
      pair(SDBLParser.RULE_identifier, 3)
    );

  }

  @Test
  void testSelect01() {
    checkSource("src/test/resources/sdbl/select01.sdbl",
      pair(SDBLParser.RULE_queries, 1),
      pair(SDBLParser.RULE_dropTableQuery, 0),
      pair(SDBLParser.RULE_selectQuery, 1),
      pair(SDBLParser.RULE_identifier, 68),
      pair(SDBLParser.RULE_subquery, 1),
      pair(SDBLParser.RULE_union, 0),
      pair(SDBLParser.RULE_query, 1),
      pair(SDBLParser.RULE_limitations, 0),
      pair(SDBLParser.RULE_top, 0),
      pair(SDBLParser.RULE_selectedFields, 1),
      pair(SDBLParser.RULE_selectedField, 1),
      pair(SDBLParser.RULE_columnField, 0),
      pair(SDBLParser.RULE_emptyTableField, 0),
      pair(SDBLParser.RULE_emptyTableColumns, 0),
      pair(SDBLParser.RULE_inlineTableField, 0),
      pair(SDBLParser.RULE_recordAutoNumberFunction, 0),
      pair(SDBLParser.RULE_groupByItem, 0),
      pair(SDBLParser.RULE_indexingItem, 0),
      pair(SDBLParser.RULE_orderBy, 0),
      pair(SDBLParser.RULE_ordersByExpession, 0),
      pair(SDBLParser.RULE_totalBy, 0),
      pair(SDBLParser.RULE_totalsGroup, 0),
      pair(SDBLParser.RULE_periodic, 0),
      pair(SDBLParser.RULE_column, 30),
      pair(SDBLParser.RULE_expression, 103),
      pair(SDBLParser.RULE_primitiveExpression, 49),
      pair(SDBLParser.RULE_caseExpression, 0),
      pair(SDBLParser.RULE_caseBranch, 0),
      pair(SDBLParser.RULE_bracketExpression, 0),
      pair(SDBLParser.RULE_unaryExpression, 24),
      pair(SDBLParser.RULE_functionCall, 0),
      pair(SDBLParser.RULE_builtInFunctions, 0),
      pair(SDBLParser.RULE_aggregateFunctions, 0),
      pair(SDBLParser.RULE_valueFunction, 0),
      pair(SDBLParser.RULE_castFunction, 0),
      pair(SDBLParser.RULE_logicalExpression, 5),
      pair(SDBLParser.RULE_predicate, 29),
      pair(SDBLParser.RULE_expressionList, 0),
      pair(SDBLParser.RULE_dataSources, 1),
      pair(SDBLParser.RULE_dataSource, 3),
      pair(SDBLParser.RULE_table, 2),
      pair(SDBLParser.RULE_virtualTable, 1),
      pair(SDBLParser.RULE_parameterTable, 0),
      pair(SDBLParser.RULE_joinPart, 2),
      pair(SDBLParser.RULE_alias, 4),
      pair(SDBLParser.RULE_datePart, 0),
      pair(SDBLParser.RULE_multiString, 0),
      pair(SDBLParser.RULE_sign, 24),
      pair(SDBLParser.RULE_parameter, 48),
      pair(SDBLParser.RULE_mdo, 1),
      pair(SDBLParser.RULE_likePredicate, 0),
      pair(SDBLParser.RULE_comparePredicate, 3),
      pair(SDBLParser.RULE_betweenPredicate, 24),
      pair(SDBLParser.RULE_inPredicate, 0),
      pair(SDBLParser.RULE_refsPredicate, 0)
    );
  }

  @Test
  void testSelect02() {
    checkSource("src/test/resources/sdbl/select02.sdbl",
      pair(SDBLParser.RULE_queries, 1),
      pair(SDBLParser.RULE_dropTableQuery, 0),
      pair(SDBLParser.RULE_selectQuery, 1),
      pair(SDBLParser.RULE_identifier, 15),
      pair(SDBLParser.RULE_subquery, 2),
      pair(SDBLParser.RULE_union, 0),
      pair(SDBLParser.RULE_query, 2),
      pair(SDBLParser.RULE_limitations, 0),
      pair(SDBLParser.RULE_top, 0),
      pair(SDBLParser.RULE_selectedFields, 2),
      pair(SDBLParser.RULE_selectedField, 3),
      pair(SDBLParser.RULE_columnField, 0),
      pair(SDBLParser.RULE_emptyTableField, 0),
      pair(SDBLParser.RULE_emptyTableColumns, 0),
      pair(SDBLParser.RULE_inlineTableField, 0),
      pair(SDBLParser.RULE_recordAutoNumberFunction, 0),
      pair(SDBLParser.RULE_groupByItem, 0),
      pair(SDBLParser.RULE_indexingItem, 0),
      pair(SDBLParser.RULE_orderBy, 0),
      pair(SDBLParser.RULE_ordersByExpession, 0),
      pair(SDBLParser.RULE_totalBy, 0),
      pair(SDBLParser.RULE_totalsGroup, 0),
      pair(SDBLParser.RULE_periodic, 0),
      pair(SDBLParser.RULE_column, 9),
      pair(SDBLParser.RULE_expression, 13),
      pair(SDBLParser.RULE_primitiveExpression, 3),
      pair(SDBLParser.RULE_caseExpression, 0),
      pair(SDBLParser.RULE_caseBranch, 0),
      pair(SDBLParser.RULE_bracketExpression, 0),
      pair(SDBLParser.RULE_unaryExpression, 0),
      pair(SDBLParser.RULE_functionCall, 1),
      pair(SDBLParser.RULE_builtInFunctions, 0),
      pair(SDBLParser.RULE_aggregateFunctions, 1),
      pair(SDBLParser.RULE_valueFunction, 0),
      pair(SDBLParser.RULE_castFunction, 0),
      pair(SDBLParser.RULE_logicalExpression, 14),
      pair(SDBLParser.RULE_predicate, 14),
      pair(SDBLParser.RULE_expressionList, 3),
      pair(SDBLParser.RULE_dataSources, 2),
      pair(SDBLParser.RULE_dataSource, 2),
      pair(SDBLParser.RULE_table, 1),
      pair(SDBLParser.RULE_virtualTable, 1),
      pair(SDBLParser.RULE_parameterTable, 0),
      pair(SDBLParser.RULE_joinPart, 0),
      pair(SDBLParser.RULE_alias, 1),
      pair(SDBLParser.RULE_datePart, 0),
      pair(SDBLParser.RULE_multiString, 0),
      pair(SDBLParser.RULE_sign, 0),
      pair(SDBLParser.RULE_parameter, 3),
      pair(SDBLParser.RULE_mdo, 2),
      pair(SDBLParser.RULE_likePredicate, 0),
      pair(SDBLParser.RULE_comparePredicate, 1),
      pair(SDBLParser.RULE_betweenPredicate, 0),
      pair(SDBLParser.RULE_inPredicate, 2),
      pair(SDBLParser.RULE_refsPredicate, 0)
    );
  }

  @Test
  void testSelect03() {
    checkSource("src/test/resources/sdbl/select03.sdbl",
      pair(SDBLParser.RULE_queries, 1),
      pair(SDBLParser.RULE_dropTableQuery, 0),
      pair(SDBLParser.RULE_selectQuery, 1),
      pair(SDBLParser.RULE_identifier, 12),
      pair(SDBLParser.RULE_subquery, 1),
      pair(SDBLParser.RULE_union, 0),
      pair(SDBLParser.RULE_query, 1),
      pair(SDBLParser.RULE_limitations, 0),
      pair(SDBLParser.RULE_top, 0),
      pair(SDBLParser.RULE_selectedFields, 1),
      pair(SDBLParser.RULE_selectedField, 4),
      pair(SDBLParser.RULE_columnField, 0),
      pair(SDBLParser.RULE_emptyTableField, 0),
      pair(SDBLParser.RULE_emptyTableColumns, 0),
      pair(SDBLParser.RULE_inlineTableField, 0),
      pair(SDBLParser.RULE_recordAutoNumberFunction, 0),
      pair(SDBLParser.RULE_groupByItem, 0),
      pair(SDBLParser.RULE_indexingItem, 0),
      pair(SDBLParser.RULE_orderBy, 0),
      pair(SDBLParser.RULE_ordersByExpession, 0),
      pair(SDBLParser.RULE_totalBy, 0),
      pair(SDBLParser.RULE_totalsGroup, 0),
      pair(SDBLParser.RULE_periodic, 0),
      pair(SDBLParser.RULE_column, 4),
      pair(SDBLParser.RULE_expression, 5),
      pair(SDBLParser.RULE_primitiveExpression, 0),
      pair(SDBLParser.RULE_caseExpression, 0),
      pair(SDBLParser.RULE_caseBranch, 0),
      pair(SDBLParser.RULE_bracketExpression, 0),
      pair(SDBLParser.RULE_unaryExpression, 0),
      pair(SDBLParser.RULE_functionCall, 1),
      pair(SDBLParser.RULE_builtInFunctions, 1),
      pair(SDBLParser.RULE_aggregateFunctions, 0),
      pair(SDBLParser.RULE_valueFunction, 0),
      pair(SDBLParser.RULE_castFunction, 0),
      pair(SDBLParser.RULE_logicalExpression, 4),
      pair(SDBLParser.RULE_predicate, 4),
      pair(SDBLParser.RULE_expressionList, 0),
      pair(SDBLParser.RULE_dataSources, 1),
      pair(SDBLParser.RULE_dataSource, 1),
      pair(SDBLParser.RULE_table, 1),
      pair(SDBLParser.RULE_virtualTable, 0),
      pair(SDBLParser.RULE_parameterTable, 0),
      pair(SDBLParser.RULE_joinPart, 0),
      pair(SDBLParser.RULE_alias, 2),
      pair(SDBLParser.RULE_datePart, 0),
      pair(SDBLParser.RULE_multiString, 0),
      pair(SDBLParser.RULE_sign, 0),
      pair(SDBLParser.RULE_parameter, 0),
      pair(SDBLParser.RULE_mdo, 1),
      pair(SDBLParser.RULE_likePredicate, 0),
      pair(SDBLParser.RULE_comparePredicate, 0),
      pair(SDBLParser.RULE_betweenPredicate, 0),
      pair(SDBLParser.RULE_inPredicate, 0),
      pair(SDBLParser.RULE_refsPredicate, 0)
    );
  }

  @Test
  void testSelect04() {
    checkSource("src/test/resources/sdbl/select04.sdbl",
      pair(SDBLParser.RULE_queries, 1),
      pair(SDBLParser.RULE_dropTableQuery, 0),
      pair(SDBLParser.RULE_selectQuery, 1),
      pair(SDBLParser.RULE_identifier, 16),
      pair(SDBLParser.RULE_subquery, 2),
      pair(SDBLParser.RULE_union, 0),
      pair(SDBLParser.RULE_query, 2),
      pair(SDBLParser.RULE_limitations, 1),
      pair(SDBLParser.RULE_top, 1),
      pair(SDBLParser.RULE_selectedFields, 2),
      pair(SDBLParser.RULE_selectedField, 2),
      pair(SDBLParser.RULE_columnField, 0),
      pair(SDBLParser.RULE_emptyTableField, 0),
      pair(SDBLParser.RULE_emptyTableColumns, 0),
      pair(SDBLParser.RULE_inlineTableField, 0),
      pair(SDBLParser.RULE_recordAutoNumberFunction, 0),
      pair(SDBLParser.RULE_groupByItem, 0),
      pair(SDBLParser.RULE_indexingItem, 0),
      pair(SDBLParser.RULE_orderBy, 1),
      pair(SDBLParser.RULE_ordersByExpession, 1),
      pair(SDBLParser.RULE_totalBy, 0),
      pair(SDBLParser.RULE_totalsGroup, 0),
      pair(SDBLParser.RULE_periodic, 0),
      pair(SDBLParser.RULE_column, 5),
      pair(SDBLParser.RULE_expression, 5),
      pair(SDBLParser.RULE_primitiveExpression, 0),
      pair(SDBLParser.RULE_caseExpression, 0),
      pair(SDBLParser.RULE_caseBranch, 0),
      pair(SDBLParser.RULE_bracketExpression, 0),
      pair(SDBLParser.RULE_unaryExpression, 0),
      pair(SDBLParser.RULE_functionCall, 0),
      pair(SDBLParser.RULE_builtInFunctions, 0),
      pair(SDBLParser.RULE_aggregateFunctions, 0),
      pair(SDBLParser.RULE_valueFunction, 0),
      pair(SDBLParser.RULE_castFunction, 0),
      pair(SDBLParser.RULE_logicalExpression, 4),
      pair(SDBLParser.RULE_predicate, 4),
      pair(SDBLParser.RULE_expressionList, 0),
      pair(SDBLParser.RULE_dataSources, 2),
      pair(SDBLParser.RULE_dataSource, 2),
      pair(SDBLParser.RULE_table, 2),
      pair(SDBLParser.RULE_virtualTable, 0),
      pair(SDBLParser.RULE_parameterTable, 0),
      pair(SDBLParser.RULE_joinPart, 0),
      pair(SDBLParser.RULE_alias, 4),
      pair(SDBLParser.RULE_datePart, 0),
      pair(SDBLParser.RULE_multiString, 0),
      pair(SDBLParser.RULE_sign, 0),
      pair(SDBLParser.RULE_parameter, 0),
      pair(SDBLParser.RULE_mdo, 2),
      pair(SDBLParser.RULE_likePredicate, 0),
      pair(SDBLParser.RULE_comparePredicate, 0),
      pair(SDBLParser.RULE_betweenPredicate, 0),
      pair(SDBLParser.RULE_inPredicate, 1),
      pair(SDBLParser.RULE_refsPredicate, 0)
    );
  }
  @Test
  void testSelect05() {
    checkSource("src/test/resources/sdbl/select05.sdbl",
      pair(SDBLParser.RULE_queries, 1),
      pair(SDBLParser.RULE_selectedField, 4),
      pair(SDBLParser.RULE_builtInFunctions, 4)
    );
  }

  @Test
  void testSelect06() {
    checkSource("src/test/resources/sdbl/select06.sdbl",
      pair(SDBLParser.RULE_queries, 1),
      pair(SDBLParser.RULE_dataSource, 1)
    );
  }

  private void checkSource(String filePath, Pair... rules) {
    var exampleString = TestUtils.getSourceFromFile(filePath);
    setInput(exampleString);
    assertMatches(parser.queryPackage());
    var ast = getAst(exampleString);
    Arrays.stream(rules).forEach(rule -> {
      var nodes = getNodes(ast, rule.ruleID);
      assertThat(nodes).as(SDBLParser.ruleNames[rule.ruleID]).hasSize(rule.size);
    });
  }

  private void checkSource(String filePath, int queryNum, Pair... rules) {
    var exampleString = TestUtils.getSourceFromFile(filePath);
    setInput(exampleString);
    assertMatches(parser.queryPackage());
    var ast = getAst(exampleString);
    var query = ast.queries(queryNum);
    Arrays.stream(rules).forEach(rule -> {
      var nodes = getNodes(query, rule.ruleID);
      assertThat(nodes).as(BSLMethodDescriptionParser.ruleNames[rule.ruleID]).hasSize(rule.size);
    });
  }

  private SDBLParser.QueryPackageContext getAst(String text) {
    return (new SDBLTokenizer(text)).getAst();
  }

  private ArrayList<ParseTree> getNodes(String text, int rule) {
    return new ArrayList<>(Trees.findAllRuleNodes((new SDBLTokenizer(text)).getAst(), rule));
  }

  private ArrayList<ParseTree> getNodes(BSLParserRuleContext ast, int rule) {
    return new ArrayList<>(Trees.findAllRuleNodes(ast, rule));
  }

  private static Pair pair(int ruleID, int size) {
    return new Pair(ruleID, size);
  }

  private static class Pair {
    private final int ruleID;
    private final int size;

    private Pair(int ruleID, int size) {
      this.ruleID = ruleID;
      this.size = size;
    }
  }
}
