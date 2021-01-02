/*
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018-2021
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

    assertMatch("Автоупорядочивание", "AUTOORDEr", SDBLLexer.AUTOORDER);
    assertMatch("Булево", "Boolean", SDBLLexer.BOOLEAN);
    assertMatch("В", "IN", SDBLLexer.IN);
    assertMatch("ВНЕШНЕе", "OUTEr", SDBLLexer.OUTER);
    assertMatch("ВНУТРЕННее", "INNeR", SDBLLexer.INNER);
    assertMatch("ВОЗр", "aSC", SDBLLexer.ASC);
    assertMatch("ВсЕ", "AlL", SDBLLexer.ALL);
    assertMatch("ВыБОР", "CAsE", SDBLLexer.CASE);
    assertMatch("ВЫБРАТь", "SELECt", SDBLLexer.SELECT);
    assertMatch("ВЫРАзИТЬ", "CAST", SDBLLexer.CAST);
    assertMatch("ГДЕ", "WHERE", SDBLLexer.WHERE);
    assertMatch("ГОД", "YEAR", SDBLLexer.YEAR);
    assertMatch("ДАТА", "DATE", SDBLLexer.DATE);
    assertMatch("ДАТАВРЕМЯ", "DATETIME", SDBLLexer.DATETIME);
    assertMatch("ДЕКАДА", "TENDAYS", SDBLLexer.TENDAYS);
    assertMatch("ДЕНЬ", "DAY", SDBLLexer.DAY);
    assertMatch("ДЕНЬГОДА", "DAYOFYEAR", SDBLLexer.DAYOFYEAR);
    assertMatch("ДЕНЬНЕДЕЛИ", "WEEKDAY", SDBLLexer.WEEKDAY);
    assertMatch("ДЛЯ ИЗМЕНЕНИЯ", "FOR UPDATE", SDBLLexer.FOR, SDBLLexer.UPDATE);
    assertMatch("FOR UPDATE OF", SDBLLexer.FOR, SDBLLexer.UPDATE, SDBLLexer.OF);
    assertMatch("ДОБАВИТЬКДАТЕ", "DATEADD", SDBLLexer.DATEADD);
    assertMatch("ЕСТЬ", "IS", SDBLLexer.IS);
    assertMatch("ЕСТЬNULL", "ISNULL", SDBLLexer.ISNULL);
    assertMatch("Значение", "VALUE", SDBLLexer.VALUE);
    assertMatch("И", "AND", SDBLLexer.AND);
    assertMatch("HIERARCHY", SDBLLexer.HIERARCHY_EN);
    assertMatch("ИЕРАРХИЯ", SDBLLexer.HIERARCHYA_RU);
    assertMatch("ИЕРАРХИи", SDBLLexer.HIERARCHII_RU);
    assertMatch("ИЗ", "FROM", SDBLLexer.FROM);
    assertMatch("ИЛИ", "Or", SDBLLexer.OR);
    assertMatch("ИМЕЮЩИЕ", "HAVING", SDBLLexer.HAVING);
    assertMatch("ИНАЧЕ", "ELSE", SDBLLexer.ELSE);
    assertMatch("ИНДЕКСИРОВАТЬ", "INDEX", SDBLLexer.INDEX);
    assertMatch("ИТОГИ", "TOTALS", SDBLLexer.TOTALS);
    assertMatch("КАК", "AS", SDBLLexer.AS);
    assertMatch("КВАРТАЛ", "QUARTER", SDBLLexer.QUARTER);
    assertMatch("КОГДА", "WHEN", SDBLLexer.WHEN);
    assertMatch("КОЛИЧЕСТВО", "COUNT", SDBLLexer.COUNT);
    assertMatch("КОНЕЦПЕРИОДА", "ENDOFPERIOD", SDBLLexer.ENDOFPERIOD);
    assertMatch("КОНЕЦ", "END", SDBLLexer.END);
    assertMatch("ЛЕВОЕ", "LEFT", SDBLLexer.LEFT);
    assertMatch("МАКСИМУМ", "MAX", SDBLLexer.MAX);
    assertMatch("МЕЖДУ", "BETWEEN", SDBLLexer.BETWEEN);
    assertMatch("МЕСЯЦ", "MONTH", SDBLLexer.MONTH);
    assertMatch("МИНИМУМ", "MIN", SDBLLexer.MIN);
    assertMatch("МИНУТА", "MINUTE", SDBLLexer.MINUTE);
    assertMatch("НАЧАЛОПЕРИОДА", "BEGINOFPERIOD", SDBLLexer.BEGINOFPERIOD);
    assertMatch("НЕ", "Not", SDBLLexer.NOT);
    assertMatch("НЕДЕЛЯ", "WEEK", SDBLLexer.WEEK);
    assertMatch("ОБЩИЕ", "OVERALL", SDBLLexer.OVERALL);
    assertMatch("ОБЪЕДИНИТЬ", "UNION", SDBLLexer.UNION);
    assertMatch("ПЕРВЫЕ", "TOP", SDBLLexer.TOP);
    assertMatch("ПЕРИОДАМИ", "PERIODS", SDBLLexer.PERIODS);
    assertMatch("ПОДОБНО", "LIKE", SDBLLexer.LIKE);
    assertMatch("ПОЛНОЕ", "FULL", SDBLLexer.FULL);
    assertMatch("ПОЛУГОДИЕ", "HALFYEAR", SDBLLexer.HALFYEAR);
    assertMatch("ПОМЕСТИТЬ", "INTO", SDBLLexer.INTO);
    assertMatch("ПРАВОЕ", "RIGHT", SDBLLexer.RIGHT);
    assertMatch("ПРЕДСТАВЛЕНИЕ", "PRESENTATION", SDBLLexer.PRESENTATION);
    assertMatch("ПУСТАЯТАБЛИЦА", "EMPTYTABLE", SDBLLexer.EMPTYTABLE);
    assertMatch("РАЗЛИЧНЫЕ", "DISTINCT", SDBLLexer.DISTINCT);
    assertMatch("РАЗРЕШЕННЫЕ", "ALLOWED", SDBLLexer.ALLOWED);
    assertMatch("Сгруппировать По", SDBLLexer.GROUP, SDBLLexer.PO_RU);
    assertMatch("GROUP BY", SDBLLexer.GROUP, SDBLLexer.BY_EN);
    assertMatch("СЕКУНДА", "SECOND", SDBLLexer.SECOND);
    assertMatch("СОЕДИНЕНИЕ ПО", SDBLLexer.JOIN, SDBLLexer.PO_RU);
    assertMatch("JOIN ON", SDBLLexer.JOIN, SDBLLexer.ON_EN);
    assertMatch("СПЕЦСИМВОЛ", "ESCAPE", SDBLLexer.ESCAPE);
    assertMatch("ПОДСТРОКА", "SUBSTRING", SDBLLexer.SUBSTRING);
    assertMatch("СРЕДНЕЕ", "AVG", SDBLLexer.AVG);
    assertMatch("ССЫЛКА", "REFS", SDBLLexer.REFS);
    assertMatch("СТРОКА", "STRING", SDBLLexer.STRING);
    assertMatch("СУММА", "SUM", SDBLLexer.SUM);
    assertMatch("ТИП", "TYPE", SDBLLexer.TYPE);
    assertMatch("ТИПЗНАЧЕНИЯ", "VALUETYPE", SDBLLexer.VALUETYPE);
    assertMatch("ТОГДА", "THEN", SDBLLexer.THEN);
    assertMatch("ТОЛЬКО", "ONLY", SDBLLexer.ONLY);
    assertMatch("УБЫВ", "DESC", SDBLLexer.DESC);
    assertMatch("УПОРЯДОЧИТЬ", "ORDER", SDBLLexer.ORDER);
    assertMatch("ЧАС", "HOUR", SDBLLexer.HOUR);
    assertMatch("ЧИСЛО", "NUMBER", SDBLLexer.NUMBER);
    assertMatch("УНИЧТОЖИТЬ", "DROP", SDBLLexer.DROP);

    assertMatch("РазностьДат", "DateDiff", SDBLLexer.DATEDIFF);
    assertMatch("автономерзаписи", "RECORDAUTONUMBER", SDBLLexer.RECORDAUTONUMBER);

  }

  @Test
  void testStandardFields() {
    assertMatch("ТочкаМаршрута", "RoutePoint", SDBLLexer.ROUTEPOINT_FIELD);
  }

  @Test
  void testMDOTypes() {
    assertMatch("БизнесПроцесс", "BusinessProcess", SDBLLexer.BUSINESS_PROCESS_TYPE);
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
    assertMatch(".СрезПоследних", ".SLICELAST", SDBLLexer.DOT, SDBLLexer.SLICELAST_VT);
    assertMatch(".СрезПервых", ".SLICEFIRST", SDBLLexer.DOT, SDBLLexer.SLICEFIRST_VT);
    assertMatch(".Границы", ".BOUNDARIES", SDBLLexer.DOT, SDBLLexer.BOUNDARIES_VT);
    assertMatch(".Обороты", ".TURNOVERS", SDBLLexer.DOT, SDBLLexer.TURNOVERS_VT);
    assertMatch(".Остатки", ".BALANCE", SDBLLexer.DOT, SDBLLexer.BALANCE_VT);
    assertMatch(".ОстаткиИОбороты", ".BALANCEANDTURNOVERS", SDBLLexer.DOT, SDBLLexer.BALANCE_AND_TURNOVERS_VT);
    assertMatch(".Субконто", ".EXTDIMENSIONS", SDBLLexer.DOT, SDBLLexer.EXT_DIMENSIONS_VT);
    assertMatch(".Движенияссубконто", ".RECORDSWITHEXTDIMENSIONS", SDBLLexer.DOT, SDBLLexer.RECORDS_WITH_EXT_DIMENSIONS_VT);
    assertMatch(".ОборотыДтКт", ".DrCrTURNOVERS", SDBLLexer.DOT, SDBLLexer.DR_CR_TURNOVERS_VT);
    assertMatch(".ФактическийПериодДействия", ".ACTUALACTIONPERIOD", SDBLLexer.DOT, SDBLLexer.ACTUAL_ACTION_PERIOD_VT);
    assertMatch(".ДанныеГрафика", ".SCHEDULEDATA", SDBLLexer.DOT, SDBLLexer.SCHEDULE_DATA_VT);
    assertMatch(".ЗадачиПоИсполнителю", ".TASKBYPERFORMER", SDBLLexer.DOT, SDBLLexer.TASK_BY_PERFORMER_VT);
  }
}
