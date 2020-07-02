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

import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SDBLLexerTest extends AbstractLexerTest<SDBLLexer> {
  protected SDBLLexerTest() {
    super(SDBLLexer.class);
  }

  @Test
  void testWhitespaces() {
    String inputString = "   А";

    List<Token> tokens = getTokens(SDBLLexer.DEFAULT_MODE, inputString);

    assertThat(tokens).extracting(Token::getType).containsExactly(
      SDBLLexer.WHITE_SPACE,
      SDBLLexer.IDENTIFIER,
      SDBLLexer.EOF
    );
  }

  @Test
  void testKeyWords() {
    assertMatch("ИСТиНА", "TRuE", SDBLLexer.TRUE);
    assertMatch("ЛоЖЬ", "FaLSE", SDBLLexer.FALSE);
    assertMatch("НеопределенО", "UNDEFINeD", SDBLLexer.UNDEFINED);
    assertMatch("NUlL", SDBLLexer.NULL);

    assertMatch("Автоупорядочивание", "AUTOORDEr", SDBLLexer.AUTOORDER_KEYWORD);
    assertMatch("Булево", "Boolean", SDBLLexer.BOOLEAN_KEYWORD);
    assertMatch("В", "IN", SDBLLexer.IN_KEYWORD);
    assertMatch("ВНЕШНЕе", "OUTEr", SDBLLexer.OUTER_KEYWORD);
    assertMatch("ВНУТРЕННее", "INNeR", SDBLLexer.INNER_KEYWORD);
    assertMatch("ВОЗр", "aSC", SDBLLexer.ASC_KEYWORD);
    assertMatch("ВсЕ", "AlL", SDBLLexer.ALL_KEYWORD);
    assertMatch("ВыБОР", "CAsE", SDBLLexer.CASE_KEYWORD);
    assertMatch("ВЫБРАТь", "SELECt", SDBLLexer.SELECT_KEYWORD);
    assertMatch("ВЫРАзИТЬ", "CAST", SDBLLexer.CAST_KEYWORD);
    assertMatch("ГДЕ", "WHERE", SDBLLexer.WHERE_KEYWORD);
    assertMatch("ГОД", "YEAR", SDBLLexer.YEAR_KEYWORD);
    assertMatch("ДАТА", "DATE", SDBLLexer.DATE_KEYWORD);
    assertMatch("ДАТАВРЕМЯ", "DATETIME", SDBLLexer.DATETIME_KEYWORD);
    assertMatch("ДЕКАДА", "TENDAYS", SDBLLexer.TENDAYS_KEYWORD);
    assertMatch("ДЕНЬ", "DAY", SDBLLexer.DAY_KEYWORD);
    assertMatch("ДЕНЬГОДА", "DAYOFYEAR", SDBLLexer.DAYOFYEAR_KEYWORD);
    assertMatch("ДЕНЬНЕДЕЛИ", "WEEKDAY", SDBLLexer.WEEKDAY_KEYWORD);
    assertMatch("ДЛЯ ИЗМЕНЕНИЯ", "FOR UPDATE", SDBLLexer.FOR_KEYWORD, SDBLLexer.UPDATE_KEYWORD);
    assertMatch("FOR UPDATE OF", SDBLLexer.FOR_KEYWORD, SDBLLexer.UPDATE_KEYWORD, SDBLLexer.OF_KEYWORD);
    assertMatch("ДОБАВИТЬКДАТЕ", "DATEADD", SDBLLexer.DATEADD_KEYWORD);
    assertMatch("ЕСТЬ", "IS", SDBLLexer.IS_KEYWORD);
    assertMatch("ЕСТЬNULL", "ISNULL", SDBLLexer.ISNULL_KEYWORD);
    assertMatch("Значение", "VALUE", SDBLLexer.VALUE_KEYWORD);
    assertMatch("И", "AND", SDBLLexer.AND_KEYWORD);
    assertMatch("HIERARCHY", SDBLLexer.EN_HIERARCHY_KEYWORD);
    assertMatch("ИЕРАРХИЯ", SDBLLexer.RU_HIERARCHYA_KEYWORD);
    assertMatch("ИЕРАРХИи", SDBLLexer.RU_HIERARCHII_KEYWORD);
    assertMatch("ИЗ", "FROM", SDBLLexer.FROM_KEYWORD);
    assertMatch("ИЛИ", "Or", SDBLLexer.OR_KEYWORD);
    assertMatch("ИМЕЮЩИЕ", "HAVING", SDBLLexer.HAVING_KEYWORD);
    assertMatch("ИНАЧЕ", "ELSE", SDBLLexer.ELSE_KEYWORD);
    assertMatch("ИНДЕКСИРОВАТЬ", "INDEX", SDBLLexer.INDEX_KEYWORD);
    assertMatch("ИТОГИ", "TOTALS", SDBLLexer.TOTALS_KEYWORD);
    assertMatch("КАК", "AS", SDBLLexer.AS_KEYWORD);
    assertMatch("КВАРТАЛ", "QUARTER", SDBLLexer.QUARTER_KEYWORD);
    assertMatch("КОГДА", "WHEN", SDBLLexer.WHEN_KEYWORD);
    assertMatch("КОЛИЧЕСТВО", "COUNT", SDBLLexer.COUNT_KEYWORD);
    assertMatch("КОНЕЦПЕРИОДА", "ENDOFPERIOD", SDBLLexer.ENDOFPERIOD_KEYWORD);
    assertMatch("КОНЕЦ", "END", SDBLLexer.END_KEYWORD);
    assertMatch("ЛЕВОЕ", "LEFT", SDBLLexer.LEFT_KEYWORD);
    assertMatch("МАКСИМУМ", "MAX", SDBLLexer.MAX_KEYWORD);
    assertMatch("МЕЖДУ", "BETWEEN", SDBLLexer.BETWEEN_KEYWORD);
    assertMatch("МЕСЯЦ", "MONTH", SDBLLexer.MONTH_KEYWORD);
    assertMatch("МИНИМУМ", "MIN", SDBLLexer.MIN_KEYWORD);
    assertMatch("МИНУТА", "MINUTE", SDBLLexer.MINUTE_KEYWORD);
    assertMatch("НАЧАЛОПЕРИОДА", "BEGINOFPERIOD", SDBLLexer.BEGINOFPERIOD_KEYWORD);
    assertMatch("НЕ", "Not", SDBLLexer.NOT_KEYWORD);
    assertMatch("НЕДЕЛЯ", "WEEK", SDBLLexer.WEEK_KEYWORD);
    assertMatch("ОБЩИЕ", "OVERALL", SDBLLexer.OVERALL_KEYWORD);
    assertMatch("ОБЪЕДИНИТЬ", "UNION", SDBLLexer.UNION_KEYWORD);
    assertMatch("ПЕРВЫЕ", "TOP", SDBLLexer.TOP_KEYWORD);
    assertMatch("ПЕРИОДАМИ", "PERIODS", SDBLLexer.PERIODS_KEYWORD);
    assertMatch("ПОДОБНО", "LIKE", SDBLLexer.LIKE_KEYWORD);
    assertMatch("ПОЛНОЕ", "FULL", SDBLLexer.FULL_KEYWORD);
    assertMatch("ПОЛУГОДИЕ", "HALFYEAR", SDBLLexer.HALFYEAR_KEYWORD);
    assertMatch("ПОМЕСТИТЬ", "INTO", SDBLLexer.INTO_KEYWORD);
    assertMatch("ПРАВОЕ", "RIGHT", SDBLLexer.RIGHT_KEYWORD);
    assertMatch("ПРЕДСТАВЛЕНИЕ", "PRESENTATION", SDBLLexer.PRESENTATION_KEYWORD);
    assertMatch("ПУСТАЯТАБЛИЦА", "EMPTYTABLE", SDBLLexer.EMPTYTABLE_KEYWORD);
    assertMatch("РАЗЛИЧНЫЕ", "DISTINCT", SDBLLexer.DISTINCT_KEYWORD);
    assertMatch("РАЗРЕШЕННЫЕ", "ALLOWED", SDBLLexer.ALLOWED_KEYWORD);
    assertMatch("Сгруппировать По", SDBLLexer.GROUP_KEYWORD, SDBLLexer.RU_PO_KEYWORD);
    assertMatch("GROUP BY", SDBLLexer.GROUP_KEYWORD, SDBLLexer.EN_BY_KEYWORD);
    assertMatch("СЕКУНДА", "SECOND", SDBLLexer.SECOND_KEYWORD);
    assertMatch("СОЕДИНЕНИЕ ПО", SDBLLexer.JOIN_KEYWORD, SDBLLexer.RU_PO_KEYWORD);
    assertMatch("JOIN ON", SDBLLexer.JOIN_KEYWORD, SDBLLexer.EN_ON_KEYWORD);
    assertMatch("СПЕЦСИМВОЛ", "ESCAPE", SDBLLexer.ESCAPE_KEYWORD);
    assertMatch("ПОДСТРОКА", "SUBSTRING", SDBLLexer.SUBSTRING_KEYWORD);
    assertMatch("СРЕДНЕЕ", "AVG", SDBLLexer.AVG_KEYWORD);
    assertMatch("ССЫЛКА", "REFS", SDBLLexer.REFS_KEYWORD);
    assertMatch("СТРОКА", "STRING", SDBLLexer.STRING_KEYWORD);
    assertMatch("СУММА", "SUM", SDBLLexer.SUM_KEYWORD);
    assertMatch("ТИП", "TYPE", SDBLLexer.TYPE_KEYWORD);
    assertMatch("ТИПЗНАЧЕНИЯ", "VALUETYPE", SDBLLexer.VALUETYPE_KEYWORD);
    assertMatch("ТОГДА", "THEN", SDBLLexer.THEN_KEYWORD);
    assertMatch("ТОЛЬКО", "ONLY", SDBLLexer.ONLY_KEYWORD);
    assertMatch("УБЫВ", "DESC", SDBLLexer.DESC_KEYWORD);
    assertMatch("УПОРЯДОЧИТЬ", "ORDER", SDBLLexer.ORDER_KEYWORD);
    assertMatch("ЧАС", "HOUR", SDBLLexer.HOUR_KEYWORD);
    assertMatch("ЧИСЛО", "NUMBER", SDBLLexer.NUMBER_KEYWORD);
    assertMatch("УНИЧТОЖИТЬ", "DROP", SDBLLexer.DROP_KEYWORD);

    assertMatch("РазностьДат", "DateDiff", SDBLLexer.DATEDIFF_KEYWORD);
    assertMatch("автономерзаписи", "autorecordnumber", SDBLLexer.AUTORECORDNUMBER_KEYWORD);

  }

  @Test
  void testStandardFields() {
    assertMatch("ПустаяСсылка", "EmptyRef", SDBLLexer.EMPTYREF_FIELD);
    assertMatch("ТочкаМаршрута", "RoutePoint", SDBLLexer.ROUTEPOINT_FIELD);
  }

  @Test
  void testMDOTypes() {
    assertMatch("БизнесПроцесс", "BusinessProcess", SDBLLexer.BUSINESSPROCESS_TYPE);
    assertMatch("Справочник", "Catalog", SDBLLexer.CATALOG_TYPE);
    assertMatch("ДОкумент", "Document", SDBLLexer.DOCUMENT_TYPE);
    assertMatch("РегистрСведений", "InformationRegister", SDBLLexer.INFORMATION_REGISTER_TYPE);
    assertMatch("Константа", "Constant", SDBLLexer.CONSTANT_TYPE);
    assertMatch("КритерийОтбора", "FilterCriterion", SDBLLexer.FILTER_CRITERION_TYPE);
    assertMatch("ПланОбмена", "ExchangePlan", SDBLLexer.EXCHANGE_PLAN_TYPE);
    assertMatch("Последовательность", "SEQUENCE", SDBLLexer.SEQUENCE_TYPE);
    assertMatch("ЖурналДокументов", "DocumentJournal", SDBLLexer.DOCUMENT_JOURNAL_TYPE);
    assertMatch("Перечисление", "Enum", SDBLLexer.ENUM_TYPE);
    assertMatch("ПланВидовХарактеристик", "ChartOfCharacteristicTypes", SDBLLexer.CHART_OF_CHARACTERISTIC_TYPES_TYPE);
    assertMatch("ПланСчетов", "ChartOfAccounts", SDBLLexer.CHART_OF_ACCOUNTS_TYPE);
    assertMatch("ПланВидоВРасчета", "ChartOfCalculationTypes", SDBLLexer.CHART_OF_CALCULATION_TYPES_TYPE);
    assertMatch("РегистрНакопления", "AccumulationRegister", SDBLLexer.ACCUMULATION_REGISTER_TYPE);
    assertMatch("РегистрБухгалтерии", "AccountingRegister", SDBLLexer.ACCOUNTING_REGISTER_TYPE);
    assertMatch("РегистрРасчета", "CalculationRegister", SDBLLexer.CALCULATION_REGISTER_TYPE);
    assertMatch("Задача", "Task", SDBLLexer.TASK_TYPE);
    assertMatch("ВнешнийИсточникДанных", "ExternalDataSource", SDBLLexer.EXTERNAL_DATA_SOURCE_TYPE);
  }

  @Test
  void testMDOTT() {
    assertMatch("СрезПоследних", "SLICELAST", SDBLLexer.SLICELAST_TT);
    assertMatch("СрезПервых", "SLICEFIRST", SDBLLexer.SLICEFIRST_TT);
    assertMatch("Границы", "BOUNDARIES", SDBLLexer.BOUNDARIES_TT);
    assertMatch("Обороты", "TURNOVERS", SDBLLexer.TURNOVERS_TT);
    assertMatch("Остатки", "BALANCE", SDBLLexer.BALANCE_TT);
    assertMatch("ОстаткиИОбороты", "BALANCEANDTURNOVERS", SDBLLexer.BALANCE_AND_TURNOVERS_TT);
    assertMatch("Субконто", "EXTDIMENSIONS", SDBLLexer.EXT_DIMENSIONS_TT);
    assertMatch("Движенияссубконто", "RECORDSWITHEXTDIMENSIONS", SDBLLexer.RECORDS_WITH_EXT_DIMENSIONS_TT);
    assertMatch("ОборотыДтКт", "DrCrTURNOVERS", SDBLLexer.DR_CR_TURNOVERS_TT);
    assertMatch("ФактическийПериодДействия", "ACTUALACTIONPERIOD", SDBLLexer.ACTUAL_ACTION_PERIOD_TT);
    assertMatch("ДанныеГрафика", "SCHEDULEDATA", SDBLLexer.SCHEDULE_DATA_TT);
    assertMatch("ЗадачиПоИсполнителю", "TASKBYPERFORMER", SDBLLexer.TASK_BY_PERFORMER_TT);
  }
}
