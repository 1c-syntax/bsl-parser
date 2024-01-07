/*
 * This file is a part of BSL Parser.
 *
 * Copyright (c) 2018-2024
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

import com.github._1c_syntax.bsl.parser.testing.TestParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SDBLParserCheckSourceTest {

  private TestParser<SDBLParser, SDBLLexer> testParser;

  @BeforeEach
  void before() {
    testParser = new TestParser<>(SDBLParser.class, SDBLLexer.class);
  }

  @Test
  void testDrop() {
    testParser.assertThatFile("sdbl/drop.sdbl")
      .containsRule(SDBLParser.RULE_queries, 3)
      .containsRule(SDBLParser.RULE_dropTableQuery, 3)
      .containsRule(SDBLParser.RULE_selectQuery, 0)
      .containsRule(SDBLParser.RULE_identifier, 3);
  }

  @Test
  void testSelect01() {
    testParser.assertThatFile("sdbl/select01.sdbl")
      .containsRule(SDBLParser.RULE_queries, 1)
      .containsRule(SDBLParser.RULE_dropTableQuery, 0)
      .containsRule(SDBLParser.RULE_selectQuery, 1)
      .containsRule(SDBLParser.RULE_identifier, 68)
      .containsRule(SDBLParser.RULE_subquery, 1)
      .containsRule(SDBLParser.RULE_union, 0)
      .containsRule(SDBLParser.RULE_query, 1)
      .containsRule(SDBLParser.RULE_limitations, 0)
      .containsRule(SDBLParser.RULE_top, 0)
      .containsRule(SDBLParser.RULE_selectedFields, 1)
      .containsRule(SDBLParser.RULE_selectedField, 1)
      .containsRule(SDBLParser.RULE_columnField, 0)
      .containsRule(SDBLParser.RULE_emptyTableField, 0)
      .containsRule(SDBLParser.RULE_emptyTableColumns, 0)
      .containsRule(SDBLParser.RULE_inlineTableField, 0)
      .containsRule(SDBLParser.RULE_recordAutoNumberFunction, 0)
      .containsRule(SDBLParser.RULE_groupByItem, 0)
      .containsRule(SDBLParser.RULE_indexingItem, 0)
      .containsRule(SDBLParser.RULE_orderBy, 0)
      .containsRule(SDBLParser.RULE_ordersByExpession, 0)
      .containsRule(SDBLParser.RULE_totalBy, 0)
      .containsRule(SDBLParser.RULE_totalsGroup, 0)
      .containsRule(SDBLParser.RULE_periodic, 0)
      .containsRule(SDBLParser.RULE_column, 30)
      .containsRule(SDBLParser.RULE_expression, 103)
      .containsRule(SDBLParser.RULE_primitiveExpression, 49)
      .containsRule(SDBLParser.RULE_caseExpression, 0)
      .containsRule(SDBLParser.RULE_caseBranch, 0)
      .containsRule(SDBLParser.RULE_bracketExpression, 0)
      .containsRule(SDBLParser.RULE_unaryExpression, 24)
      .containsRule(SDBLParser.RULE_functionCall, 0)
      .containsRule(SDBLParser.RULE_builtInFunctions, 0)
      .containsRule(SDBLParser.RULE_aggregateFunctions, 0)
      .containsRule(SDBLParser.RULE_valueFunction, 0)
      .containsRule(SDBLParser.RULE_castFunction, 0)
      .containsRule(SDBLParser.RULE_logicalExpression, 5)
      .containsRule(SDBLParser.RULE_predicate, 29)
      .containsRule(SDBLParser.RULE_expressionList, 0)
      .containsRule(SDBLParser.RULE_dataSources, 1)
      .containsRule(SDBLParser.RULE_dataSource, 3)
      .containsRule(SDBLParser.RULE_table, 2)
      .containsRule(SDBLParser.RULE_virtualTable, 1)
      .containsRule(SDBLParser.RULE_parameterTable, 0)
      .containsRule(SDBLParser.RULE_joinPart, 2)
      .containsRule(SDBLParser.RULE_alias, 4)
      .containsRule(SDBLParser.RULE_datePart, 0)
      .containsRule(SDBLParser.RULE_multiString, 0)
      .containsRule(SDBLParser.RULE_sign, 24)
      .containsRule(SDBLParser.RULE_parameter, 48)
      .containsRule(SDBLParser.RULE_mdo, 1)
      .containsRule(SDBLParser.RULE_likePredicate, 0)
      .containsRule(SDBLParser.RULE_comparePredicate, 3)
      .containsRule(SDBLParser.RULE_betweenPredicate, 24)
      .containsRule(SDBLParser.RULE_inPredicate, 0)
      .containsRule(SDBLParser.RULE_refsPredicate, 0);
  }

  @Test
  void testSelect02() {
    testParser.assertThatFile("sdbl/select02.sdbl")
      .containsRule(SDBLParser.RULE_queries, 1)
      .containsRule(SDBLParser.RULE_dropTableQuery, 0)
      .containsRule(SDBLParser.RULE_selectQuery, 1)
      .containsRule(SDBLParser.RULE_identifier, 15)
      .containsRule(SDBLParser.RULE_subquery, 2)
      .containsRule(SDBLParser.RULE_union, 0)
      .containsRule(SDBLParser.RULE_query, 2)
      .containsRule(SDBLParser.RULE_limitations, 0)
      .containsRule(SDBLParser.RULE_top, 0)
      .containsRule(SDBLParser.RULE_selectedFields, 2)
      .containsRule(SDBLParser.RULE_selectedField, 3)
      .containsRule(SDBLParser.RULE_columnField, 0)
      .containsRule(SDBLParser.RULE_emptyTableField, 0)
      .containsRule(SDBLParser.RULE_emptyTableColumns, 0)
      .containsRule(SDBLParser.RULE_inlineTableField, 0)
      .containsRule(SDBLParser.RULE_recordAutoNumberFunction, 0)
      .containsRule(SDBLParser.RULE_groupByItem, 0)
      .containsRule(SDBLParser.RULE_indexingItem, 0)
      .containsRule(SDBLParser.RULE_orderBy, 0)
      .containsRule(SDBLParser.RULE_ordersByExpession, 0)
      .containsRule(SDBLParser.RULE_totalBy, 0)
      .containsRule(SDBLParser.RULE_totalsGroup, 0)
      .containsRule(SDBLParser.RULE_periodic, 0)
      .containsRule(SDBLParser.RULE_column, 9)
      .containsRule(SDBLParser.RULE_expression, 13)
      .containsRule(SDBLParser.RULE_primitiveExpression, 3)
      .containsRule(SDBLParser.RULE_caseExpression, 0)
      .containsRule(SDBLParser.RULE_caseBranch, 0)
      .containsRule(SDBLParser.RULE_bracketExpression, 0)
      .containsRule(SDBLParser.RULE_unaryExpression, 0)
      .containsRule(SDBLParser.RULE_functionCall, 1)
      .containsRule(SDBLParser.RULE_builtInFunctions, 0)
      .containsRule(SDBLParser.RULE_aggregateFunctions, 1)
      .containsRule(SDBLParser.RULE_valueFunction, 0)
      .containsRule(SDBLParser.RULE_castFunction, 0)
      .containsRule(SDBLParser.RULE_logicalExpression, 14)
      .containsRule(SDBLParser.RULE_predicate, 14)
      .containsRule(SDBLParser.RULE_expressionList, 3)
      .containsRule(SDBLParser.RULE_dataSources, 2)
      .containsRule(SDBLParser.RULE_dataSource, 2)
      .containsRule(SDBLParser.RULE_table, 1)
      .containsRule(SDBLParser.RULE_virtualTable, 1)
      .containsRule(SDBLParser.RULE_parameterTable, 0)
      .containsRule(SDBLParser.RULE_joinPart, 0)
      .containsRule(SDBLParser.RULE_alias, 1)
      .containsRule(SDBLParser.RULE_datePart, 0)
      .containsRule(SDBLParser.RULE_multiString, 0)
      .containsRule(SDBLParser.RULE_sign, 0)
      .containsRule(SDBLParser.RULE_parameter, 3)
      .containsRule(SDBLParser.RULE_mdo, 2)
      .containsRule(SDBLParser.RULE_likePredicate, 0)
      .containsRule(SDBLParser.RULE_comparePredicate, 1)
      .containsRule(SDBLParser.RULE_betweenPredicate, 0)
      .containsRule(SDBLParser.RULE_inPredicate, 2)
      .containsRule(SDBLParser.RULE_refsPredicate, 0);
  }

  @Test
  void testSelect03() {
    testParser.assertThatFile("sdbl/select03.sdbl")
      .containsRule(SDBLParser.RULE_queries, 1)
      .containsRule(SDBLParser.RULE_dropTableQuery, 0)
      .containsRule(SDBLParser.RULE_selectQuery, 1)
      .containsRule(SDBLParser.RULE_identifier, 12)
      .containsRule(SDBLParser.RULE_subquery, 1)
      .containsRule(SDBLParser.RULE_union, 0)
      .containsRule(SDBLParser.RULE_query, 1)
      .containsRule(SDBLParser.RULE_limitations, 0)
      .containsRule(SDBLParser.RULE_top, 0)
      .containsRule(SDBLParser.RULE_selectedFields, 1)
      .containsRule(SDBLParser.RULE_selectedField, 4)
      .containsRule(SDBLParser.RULE_columnField, 0)
      .containsRule(SDBLParser.RULE_emptyTableField, 0)
      .containsRule(SDBLParser.RULE_emptyTableColumns, 0)
      .containsRule(SDBLParser.RULE_inlineTableField, 0)
      .containsRule(SDBLParser.RULE_recordAutoNumberFunction, 0)
      .containsRule(SDBLParser.RULE_groupByItem, 0)
      .containsRule(SDBLParser.RULE_indexingItem, 0)
      .containsRule(SDBLParser.RULE_orderBy, 0)
      .containsRule(SDBLParser.RULE_ordersByExpession, 0)
      .containsRule(SDBLParser.RULE_totalBy, 0)
      .containsRule(SDBLParser.RULE_totalsGroup, 0)
      .containsRule(SDBLParser.RULE_periodic, 0)
      .containsRule(SDBLParser.RULE_column, 4)
      .containsRule(SDBLParser.RULE_expression, 5)
      .containsRule(SDBLParser.RULE_primitiveExpression, 0)
      .containsRule(SDBLParser.RULE_caseExpression, 0)
      .containsRule(SDBLParser.RULE_caseBranch, 0)
      .containsRule(SDBLParser.RULE_bracketExpression, 0)
      .containsRule(SDBLParser.RULE_unaryExpression, 0)
      .containsRule(SDBLParser.RULE_functionCall, 1)
      .containsRule(SDBLParser.RULE_builtInFunctions, 1)
      .containsRule(SDBLParser.RULE_aggregateFunctions, 0)
      .containsRule(SDBLParser.RULE_valueFunction, 0)
      .containsRule(SDBLParser.RULE_castFunction, 0)
      .containsRule(SDBLParser.RULE_logicalExpression, 4)
      .containsRule(SDBLParser.RULE_predicate, 4)
      .containsRule(SDBLParser.RULE_expressionList, 0)
      .containsRule(SDBLParser.RULE_dataSources, 1)
      .containsRule(SDBLParser.RULE_dataSource, 1)
      .containsRule(SDBLParser.RULE_table, 1)
      .containsRule(SDBLParser.RULE_virtualTable, 0)
      .containsRule(SDBLParser.RULE_parameterTable, 0)
      .containsRule(SDBLParser.RULE_joinPart, 0)
      .containsRule(SDBLParser.RULE_alias, 2)
      .containsRule(SDBLParser.RULE_datePart, 0)
      .containsRule(SDBLParser.RULE_multiString, 0)
      .containsRule(SDBLParser.RULE_sign, 0)
      .containsRule(SDBLParser.RULE_parameter, 0)
      .containsRule(SDBLParser.RULE_mdo, 1)
      .containsRule(SDBLParser.RULE_likePredicate, 0)
      .containsRule(SDBLParser.RULE_comparePredicate, 0)
      .containsRule(SDBLParser.RULE_betweenPredicate, 0)
      .containsRule(SDBLParser.RULE_inPredicate, 0)
      .containsRule(SDBLParser.RULE_refsPredicate, 0);
  }

  @Test
  void testSelect04() {
    testParser.assertThatFile("sdbl/select04.sdbl")
      .containsRule(SDBLParser.RULE_queries, 1)
      .containsRule(SDBLParser.RULE_dropTableQuery, 0)
      .containsRule(SDBLParser.RULE_selectQuery, 1)
      .containsRule(SDBLParser.RULE_identifier, 16)
      .containsRule(SDBLParser.RULE_subquery, 2)
      .containsRule(SDBLParser.RULE_union, 0)
      .containsRule(SDBLParser.RULE_query, 2)
      .containsRule(SDBLParser.RULE_limitations, 1)
      .containsRule(SDBLParser.RULE_top, 1)
      .containsRule(SDBLParser.RULE_selectedFields, 2)
      .containsRule(SDBLParser.RULE_selectedField, 2)
      .containsRule(SDBLParser.RULE_columnField, 0)
      .containsRule(SDBLParser.RULE_emptyTableField, 0)
      .containsRule(SDBLParser.RULE_emptyTableColumns, 0)
      .containsRule(SDBLParser.RULE_inlineTableField, 0)
      .containsRule(SDBLParser.RULE_recordAutoNumberFunction, 0)
      .containsRule(SDBLParser.RULE_groupByItem, 0)
      .containsRule(SDBLParser.RULE_indexingItem, 0)
      .containsRule(SDBLParser.RULE_orderBy, 1)
      .containsRule(SDBLParser.RULE_ordersByExpession, 1)
      .containsRule(SDBLParser.RULE_totalBy, 0)
      .containsRule(SDBLParser.RULE_totalsGroup, 0)
      .containsRule(SDBLParser.RULE_periodic, 0)
      .containsRule(SDBLParser.RULE_column, 5)
      .containsRule(SDBLParser.RULE_expression, 5)
      .containsRule(SDBLParser.RULE_primitiveExpression, 0)
      .containsRule(SDBLParser.RULE_caseExpression, 0)
      .containsRule(SDBLParser.RULE_caseBranch, 0)
      .containsRule(SDBLParser.RULE_bracketExpression, 0)
      .containsRule(SDBLParser.RULE_unaryExpression, 0)
      .containsRule(SDBLParser.RULE_functionCall, 0)
      .containsRule(SDBLParser.RULE_builtInFunctions, 0)
      .containsRule(SDBLParser.RULE_aggregateFunctions, 0)
      .containsRule(SDBLParser.RULE_valueFunction, 0)
      .containsRule(SDBLParser.RULE_castFunction, 0)
      .containsRule(SDBLParser.RULE_logicalExpression, 4)
      .containsRule(SDBLParser.RULE_predicate, 4)
      .containsRule(SDBLParser.RULE_expressionList, 0)
      .containsRule(SDBLParser.RULE_dataSources, 2)
      .containsRule(SDBLParser.RULE_dataSource, 2)
      .containsRule(SDBLParser.RULE_table, 2)
      .containsRule(SDBLParser.RULE_virtualTable, 0)
      .containsRule(SDBLParser.RULE_parameterTable, 0)
      .containsRule(SDBLParser.RULE_joinPart, 0)
      .containsRule(SDBLParser.RULE_alias, 4)
      .containsRule(SDBLParser.RULE_datePart, 0)
      .containsRule(SDBLParser.RULE_multiString, 0)
      .containsRule(SDBLParser.RULE_sign, 0)
      .containsRule(SDBLParser.RULE_parameter, 0)
      .containsRule(SDBLParser.RULE_mdo, 2)
      .containsRule(SDBLParser.RULE_likePredicate, 0)
      .containsRule(SDBLParser.RULE_comparePredicate, 0)
      .containsRule(SDBLParser.RULE_betweenPredicate, 0)
      .containsRule(SDBLParser.RULE_inPredicate, 1)
      .containsRule(SDBLParser.RULE_refsPredicate, 0);
  }

  @Test
  void testSelect05() {
    testParser.assertThatFile("sdbl/select05.sdbl")
      .containsRule(SDBLParser.RULE_queries, 1)
      .containsRule(SDBLParser.RULE_selectedField, 4)
      .containsRule(SDBLParser.RULE_builtInFunctions, 4);
  }

  @Test
  void testSelect06() {
    testParser.assertThatFile("sdbl/select06.sdbl")
      .containsRule(SDBLParser.RULE_queries, 4)
      .containsRule(SDBLParser.RULE_dataSource, 4);
  }

  @Test
  void testSelect07() {
    testParser.assertThatFile("sdbl/select07.sdbl")
      .containsRule(SDBLParser.RULE_queries, 1)
      .containsRule(SDBLParser.RULE_dataSource, 1);
  }

  @Test
  void testSelect08() {
    testParser.assertThatFile("sdbl/select08.sdbl")
      .containsRule(SDBLParser.RULE_queries, 1)
      .containsRule(SDBLParser.RULE_dataSource, 1)
      .containsRule(SDBLParser.RULE_builtInFunctions, 35);
  }
}
