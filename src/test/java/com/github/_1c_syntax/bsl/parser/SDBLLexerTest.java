/*
 * This file is a part of BSL Parser.
 *
 * Copyright (c) 2018-2025
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

import com.github._1c_syntax.bsl.parser.testing.TestLexer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SDBLLexerTest {
  private TestLexer<SDBLLexer> testLexer;

  @BeforeEach
  void before() {
    testLexer = new TestLexer<>(SDBLLexer.class);
  }

  @Test
  void testWhitespaces() {
    var inputString = "   А";
    testLexer.assertThat(SDBLLexer.DEFAULT_MODE, inputString)
      .containsExactly(SDBLLexer.WHITE_SPACE, SDBLLexer.IDENTIFIER, SDBLLexer.EOF);
  }

  @Test
  void testKeyWords() {
    testLexer.assertThat("ИСТиНА").isEqualTo("TRuE").containsAll(SDBLLexer.TRUE);
    testLexer.assertThat("ЛоЖЬ").isEqualTo("FaLSE").containsAll(SDBLLexer.FALSE);
    testLexer.assertThat("НеопределенО").isEqualTo("UNDEFINeD").containsAll(SDBLLexer.UNDEFINED);
    testLexer.assertThat("NUlL").containsAll(SDBLLexer.NULL);

    testLexer.assertThat("Автоупорядочивание").isEqualTo("AUTOORDEr").containsAll(SDBLLexer.AUTOORDER);
    testLexer.assertThat("Булево").isEqualTo("Boolean").containsAll(SDBLLexer.BOOLEAN);
    testLexer.assertThat("В").isEqualTo("IN").containsAll(SDBLLexer.IN);
    testLexer.assertThat("ВОЗр").isEqualTo("aSC").containsAll(SDBLLexer.ASC);
    testLexer.assertThat("ВыБОР").isEqualTo("CAsE").containsAll(SDBLLexer.CASE);
    testLexer.assertThat("ВЫБРАТь").isEqualTo("SELECt").containsAll(SDBLLexer.SELECT);
    testLexer.assertThat("ВЫРАзИТЬ").isEqualTo("CAST").containsAll(SDBLLexer.CAST);
    testLexer.assertThat("ГДЕ").isEqualTo("WHERE").containsAll(SDBLLexer.WHERE);
    testLexer.assertThat("ГОД").isEqualTo("YEAR").containsAll(SDBLLexer.YEAR);
    testLexer.assertThat("ДАТА").isEqualTo("DATE").containsAll(SDBLLexer.DATE);
    testLexer.assertThat("ДАТАВРЕМЯ").isEqualTo("DATETIME").containsAll(SDBLLexer.DATETIME);
    testLexer.assertThat("ДЕКАДА").isEqualTo("TENDAYS").containsAll(SDBLLexer.TENDAYS);
    testLexer.assertThat("ДЕНЬ").isEqualTo("DAY").containsAll(SDBLLexer.DAY);
    testLexer.assertThat("ДЕНЬГОДА").isEqualTo("DAYOFYEAR").containsAll(SDBLLexer.DAYOFYEAR);
    testLexer.assertThat("ДЕНЬНЕДЕЛИ").isEqualTo("WEEKDAY").containsAll(SDBLLexer.WEEKDAY);
    testLexer.assertThat("ДЛЯ ИЗМЕНЕНИЯ")
      .isEqualTo("FOR UPDATE").containsAll(SDBLLexer.FOR, SDBLLexer.UPDATE);
    testLexer.assertThat("FOR UPDATE OF").containsAll(SDBLLexer.FOR, SDBLLexer.UPDATE, SDBLLexer.OF);
    testLexer.assertThat("ДОБАВИТЬКДАТЕ").isEqualTo("DATEADD").containsAll(SDBLLexer.DATEADD);
    testLexer.assertThat("ЕСТЬ").isEqualTo("IS").containsAll(SDBLLexer.IS);
    testLexer.assertThat("ЕСТЬNULL").isEqualTo("ISNULL").containsAll(SDBLLexer.ISNULL);
    testLexer.assertThat("Значение").isEqualTo("VALUE").containsAll(SDBLLexer.VALUE);
    testLexer.assertThat("И").isEqualTo("AND").containsAll(SDBLLexer.AND);
    testLexer.assertThat("HIERARCHY").containsAll(SDBLLexer.HIERARCHY);
    testLexer.assertThat("ИЕРАРХИЯ").containsAll(SDBLLexer.HIERARCHY);
    testLexer.assertThat("ИЗ").isEqualTo("FROM").containsAll(SDBLLexer.FROM);
    testLexer.assertThat("ИЛИ").isEqualTo("Or").containsAll(SDBLLexer.OR);
    testLexer.assertThat("ИМЕЮЩИЕ").isEqualTo("HAVING").containsAll(SDBLLexer.HAVING);
    testLexer.assertThat("ИНАЧЕ").isEqualTo("ELSE").containsAll(SDBLLexer.ELSE);
    testLexer.assertThat("ИНДЕКСИРОВАТЬ BY")
      .isEqualTo("INDEX BY").containsAll(SDBLLexer.INDEX, SDBLLexer.BY_EN);
    testLexer.assertThat("ИТОГИ").isEqualTo("TOTALS").containsAll(SDBLLexer.TOTALS);
    testLexer.assertThat("КАК").isEqualTo("AS").containsAll(SDBLLexer.AS);
    testLexer.assertThat("КВАРТАЛ").isEqualTo("QUARTER").containsAll(SDBLLexer.QUARTER);
    testLexer.assertThat("КОГДА").isEqualTo("WHEN").containsAll(SDBLLexer.WHEN);
    testLexer.assertThat("КОЛИЧЕСТВО").isEqualTo("COUNT").containsAll(SDBLLexer.COUNT);
    testLexer.assertThat("КОНЕЦПЕРИОДА").isEqualTo("ENDOFPERIOD").containsAll(SDBLLexer.ENDOFPERIOD);
    testLexer.assertThat("КОНЕЦ").isEqualTo("END").containsAll(SDBLLexer.END);
    testLexer.assertThat("МАКСИМУМ").isEqualTo("MAX").containsAll(SDBLLexer.MAX);
    testLexer.assertThat("МЕЖДУ").isEqualTo("BETWEEN").containsAll(SDBLLexer.BETWEEN);
    testLexer.assertThat("МЕСЯЦ").isEqualTo("MONTH").containsAll(SDBLLexer.MONTH);
    testLexer.assertThat("МИНИМУМ").isEqualTo("MIN").containsAll(SDBLLexer.MIN);
    testLexer.assertThat("МИНУТА").isEqualTo("MINUTE").containsAll(SDBLLexer.MINUTE);
    testLexer.assertThat("НАЧАЛОПЕРИОДА").isEqualTo("BEGINOFPERIOD").containsAll(SDBLLexer.BEGINOFPERIOD);
    testLexer.assertThat("НЕ").isEqualTo("Not").containsAll(SDBLLexer.NOT);
    testLexer.assertThat("НЕДЕЛЯ").isEqualTo("WEEK").containsAll(SDBLLexer.WEEK);
    testLexer.assertThat("ОБЩИЕ").isEqualTo("OVERALL").containsAll(SDBLLexer.OVERALL);
    testLexer.assertThat("ОБЪЕДИНИТЬ").isEqualTo("UNION").containsAll(SDBLLexer.UNION);
    testLexer.assertThat("ПЕРВЫЕ").isEqualTo("TOP").containsAll(SDBLLexer.TOP);
    testLexer.assertThat("ПЕРИОДАМИ").isEqualTo("PERIODS").containsAll(SDBLLexer.PERIODS);
    testLexer.assertThat("ПОДОБНО").isEqualTo("LIKE").containsAll(SDBLLexer.LIKE);
    testLexer.assertThat("ПОЛУГОДИЕ").isEqualTo("HALFYEAR").containsAll(SDBLLexer.HALFYEAR);
    testLexer.assertThat("ПОМЕСТИТЬ").isEqualTo("INTO").containsAll(SDBLLexer.INTO);
    testLexer.assertThat("ПРАВОЕ JOIN")
      .isEqualTo("RIGHT JOIN").containsAll(SDBLLexer.RIGHT, SDBLLexer.JOIN);
    testLexer.assertThat("ПРЕДСТАВЛЕНИЕ").isEqualTo("PRESENTATION").containsAll(SDBLLexer.PRESENTATION);
    testLexer.assertThat("ПУСТАЯТАБЛИЦА").isEqualTo("EMPTYTABLE").containsAll(SDBLLexer.EMPTYTABLE);
    testLexer.assertThat("РАЗЛИЧНЫЕ").isEqualTo("DISTINCT").containsAll(SDBLLexer.DISTINCT);
    testLexer.assertThat("РАЗРЕШЕННЫЕ").isEqualTo("ALLOWED").containsAll(SDBLLexer.ALLOWED);
    testLexer.assertThat("Сгруппировать По").containsAll(SDBLLexer.GROUP, SDBLLexer.PO_RU);
    testLexer.assertThat("GROUP BY").containsAll(SDBLLexer.GROUP, SDBLLexer.BY_EN);
    testLexer.assertThat("СЕКУНДА").isEqualTo("SECOND").containsAll(SDBLLexer.SECOND);
    testLexer.assertThat("СОЕДИНЕНИЕ ПО").containsAll(SDBLLexer.JOIN, SDBLLexer.PO_RU);
    testLexer.assertThat("JOIN ON").containsAll(SDBLLexer.JOIN, SDBLLexer.ON_EN);
    testLexer.assertThat("СПЕЦСИМВОЛ").isEqualTo("ESCAPE").containsAll(SDBLLexer.ESCAPE);
    testLexer.assertThat("ПОДСТРОКА").isEqualTo("SUBSTRING").containsAll(SDBLLexer.SUBSTRING);
    testLexer.assertThat("СРЕДНЕЕ").isEqualTo("AVG").containsAll(SDBLLexer.AVG);
    testLexer.assertThat("ССЫЛКА").isEqualTo("REFS").containsAll(SDBLLexer.REFS);
    testLexer.assertThat("СТРОКА").isEqualTo("STRING").containsAll(SDBLLexer.STRING);
    testLexer.assertThat("СУММА").isEqualTo("SUM").containsAll(SDBLLexer.SUM);
    testLexer.assertThat("ТИП").isEqualTo("TYPE").containsAll(SDBLLexer.TYPE);
    testLexer.assertThat("ТИПЗНАЧЕНИЯ").isEqualTo("VALUETYPE").containsAll(SDBLLexer.VALUETYPE);
    testLexer.assertThat("ТОГДА").isEqualTo("THEN").containsAll(SDBLLexer.THEN);
    testLexer.assertThat("ТОЛЬКО").isEqualTo("ONLY").containsAll(SDBLLexer.ONLY);
    testLexer.assertThat("УБЫВ").isEqualTo("DESC").containsAll(SDBLLexer.DESC);
    testLexer.assertThat("УПОРЯДОЧИТЬ ПО")
      .isEqualTo("ORDER ПО").containsAll(SDBLLexer.ORDER, SDBLLexer.PO_RU);
    testLexer.assertThat("ЧАС").isEqualTo("HOUR").containsAll(SDBLLexer.HOUR);
    testLexer.assertThat("ЧИСЛО").isEqualTo("NUMBER").containsAll(SDBLLexer.NUMBER);
    testLexer.assertThat("УНИЧТОЖИТЬ").isEqualTo("DROP").containsAll(SDBLLexer.DROP);

    testLexer.assertThat("РазностьДат").isEqualTo("DateDiff").containsAll(SDBLLexer.DATEDIFF);
    testLexer.assertThat("автономерзаписи")
      .isEqualTo("RECORDAUTONUMBER").containsAll(SDBLLexer.RECORDAUTONUMBER);

  }

  @Test
  void testKeyWordsP2() {
    testLexer.assertThat("ЦЕЛ").isEqualTo("int").containsAll(SDBLLexer.INT);
    testLexer.assertThat("ACOS").isEqualTo("ACOs").containsAll(SDBLLexer.ACOS);
    testLexer.assertThat("ASIN").isEqualTo("ASIn").containsAll(SDBLLexer.ASIN);
    testLexer.assertThat("ATAN").isEqualTo("ATaN").containsAll(SDBLLexer.ATAN);
    testLexer.assertThat("COS").isEqualTo("cOS").containsAll(SDBLLexer.COS);
    testLexer.assertThat("SIN").isEqualTo("SiN").containsAll(SDBLLexer.SIN);
    testLexer.assertThat("TAN").isEqualTo("TAn").containsAll(SDBLLexer.TAN);
    testLexer.assertThat("LOG").isEqualTo("LOg").containsAll(SDBLLexer.LOG);
    testLexer.assertThat("LOG10").isEqualTo("loG10").containsAll(SDBLLexer.LOG10);
    testLexer.assertThat("EXP").isEqualTo("EXp").containsAll(SDBLLexer.EXP);
    testLexer.assertThat("POW").isEqualTo("POw").containsAll(SDBLLexer.POW);
    testLexer.assertThat("SQRT").isEqualTo("SqRT").containsAll(SDBLLexer.SQRT);
    testLexer.assertThat("LOWER").isEqualTo("Нрег").containsAll(SDBLLexer.LOWER);
    testLexer.assertThat("STRINGLENGTH").isEqualTo("ДлинаСТроки").containsAll(SDBLLexer.STRINGLENGTH);
    testLexer.assertThat("TRIMALL").isEqualTo("Сокрлп").containsAll(SDBLLexer.TRIMALL);
    testLexer.assertThat("TRIML").isEqualTo("Сокрл").containsAll(SDBLLexer.TRIML);
    testLexer.assertThat("TRIMR").isEqualTo("СокрП").containsAll(SDBLLexer.TRIMR);
    testLexer.assertThat("UPPER").isEqualTo("вреГ").containsAll(SDBLLexer.UPPER);
    testLexer.assertThat("ROUND").isEqualTo("окр").containsAll(SDBLLexer.ROUND);
    testLexer.assertThat("STOREDDATASIZE")
      .isEqualTo("РазмерХранимыхДанных").containsAll(SDBLLexer.STOREDDATASIZE);
    testLexer.assertThat("UUID").isEqualTo("УникальныйиДентификатор").containsAll(SDBLLexer.UUID);
    testLexer.assertThat("STRFIND").isEqualTo("стрнайТи").containsAll(SDBLLexer.STRFIND);
    testLexer.assertThat("STRREPLACE").isEqualTo("стрЗАМЕнить").containsAll(SDBLLexer.STRREPLACE);
  }

  @Test
  void testStandardFields() {
    testLexer.assertThat("ТочкаМаршрута").isEqualTo("RoutePoint").containsAll(SDBLLexer.ROUTEPOINT_FIELD);
  }

  @Test
  void testMDOTypes() {
    testLexer.assertThat("БизнесПроцесс")
      .isEqualTo("BusinessProcess").containsAll(SDBLLexer.BUSINESS_PROCESS_TYPE);
    testLexer.assertThat("Справочник").isEqualTo("Catalog").containsAll(SDBLLexer.CATALOG_TYPE);
    testLexer.assertThat("ДОкумент").isEqualTo("Document").containsAll(SDBLLexer.DOCUMENT_TYPE);
    testLexer.assertThat("РегистрСведений")
      .isEqualTo("InformationRegister").containsAll(SDBLLexer.INFORMATION_REGISTER_TYPE);
    testLexer.assertThat("Константа").isEqualTo("Constant").containsAll(SDBLLexer.CONSTANT_TYPE);
    testLexer.assertThat("КритерийОтбора")
      .isEqualTo("FilterCriterion").containsAll(SDBLLexer.FILTER_CRITERION_TYPE);
    testLexer.assertThat("ПланОбмена").isEqualTo("ExchangePlan").containsAll(SDBLLexer.EXCHANGE_PLAN_TYPE);
    testLexer.assertThat("Последовательность").isEqualTo("SEQUENCE").containsAll(SDBLLexer.SEQUENCE_TYPE);
    testLexer.assertThat("ЖурналДокументов")
      .isEqualTo("DocumentJournal").containsAll(SDBLLexer.DOCUMENT_JOURNAL_TYPE);
    testLexer.assertThat("Перечисление").isEqualTo("Enum").containsAll(SDBLLexer.ENUM_TYPE);
    testLexer.assertThat("ПланВидовХарактеристик")
      .isEqualTo("ChartOfCharacteristicTypes").containsAll(SDBLLexer.CHART_OF_CHARACTERISTIC_TYPES_TYPE);
    testLexer.assertThat("ПланСчетов").isEqualTo("ChartOfAccounts").containsAll(SDBLLexer.CHART_OF_ACCOUNTS_TYPE);
    testLexer.assertThat("ПланВидоВРасчета")
      .isEqualTo("ChartOfCalculationTypes").containsAll(SDBLLexer.CHART_OF_CALCULATION_TYPES_TYPE);
    testLexer.assertThat("РегистрНакопления")
      .isEqualTo("AccumulationRegister").containsAll(SDBLLexer.ACCUMULATION_REGISTER_TYPE);
    testLexer.assertThat("РегистрБухгалтерии")
      .isEqualTo("AccountingRegister").containsAll(SDBLLexer.ACCOUNTING_REGISTER_TYPE);
    testLexer.assertThat("РегистрРасчета")
      .isEqualTo("CalculationRegister").containsAll(SDBLLexer.CALCULATION_REGISTER_TYPE);
    testLexer.assertThat("Задача").isEqualTo("Task").containsAll(SDBLLexer.TASK_TYPE);
    testLexer.assertThat("ВнешнийИсточникДанных")
      .isEqualTo("ExternalDataSource").containsAll(SDBLLexer.EXTERNAL_DATA_SOURCE_TYPE);
  }

  @Test
  void testMDOTT() {
    testLexer.assertThat(".СрезПоследних")
      .isEqualTo(".SLICELAST").containsAll(SDBLLexer.DOT, SDBLLexer.SLICELAST_VT);
    testLexer.assertThat(".СрезПервых")
      .isEqualTo(".SLICEFIRST").containsAll(SDBLLexer.DOT, SDBLLexer.SLICEFIRST_VT);
    testLexer.assertThat(".Границы")
      .isEqualTo(".BOUNDARIES").containsAll(SDBLLexer.DOT, SDBLLexer.BOUNDARIES_VT);
    testLexer.assertThat(".Обороты")
      .isEqualTo(".TURNOVERS").containsAll(SDBLLexer.DOT, SDBLLexer.TURNOVERS_VT);
    testLexer.assertThat(".Остатки")
      .isEqualTo(".BALANCE").containsAll(SDBLLexer.DOT, SDBLLexer.BALANCE_VT);
    testLexer.assertThat(".ОстаткиИОбороты")
      .isEqualTo(".BALANCEANDTURNOVERS").containsAll(SDBLLexer.DOT, SDBLLexer.BALANCE_AND_TURNOVERS_VT);
    testLexer.assertThat(".Субконто")
      .isEqualTo(".EXTDIMENSIONS").containsAll(SDBLLexer.DOT, SDBLLexer.EXT_DIMENSIONS_VT);
    testLexer.assertThat(".Движенияссубконто")
      .isEqualTo(".RECORDSWITHEXTDIMENSIONS").containsAll(SDBLLexer.DOT, SDBLLexer.RECORDS_WITH_EXT_DIMENSIONS_VT);
    testLexer.assertThat(".ОборотыДтКт")
      .isEqualTo(".DrCrTURNOVERS").containsAll(SDBLLexer.DOT, SDBLLexer.DR_CR_TURNOVERS_VT);
    testLexer.assertThat(".ФактическийПериодДействия")
      .isEqualTo(".ACTUALACTIONPERIOD").containsAll(SDBLLexer.DOT, SDBLLexer.ACTUAL_ACTION_PERIOD_VT);
    testLexer.assertThat(".ДанныеГрафика")
      .isEqualTo(".SCHEDULEDATA").containsAll(SDBLLexer.DOT, SDBLLexer.SCHEDULE_DATA_VT);
    testLexer.assertThat(".ЗадачиПоИсполнителю")
      .isEqualTo(".TASKBYPERFORMER").containsAll(SDBLLexer.DOT, SDBLLexer.TASK_BY_PERFORMER_VT);
  }

  @Test
  void testExternalTypes() {
    testLexer.assertThat("ВнешнийИсточникДанных.ВИД1.Таблица")
      .isEqualTo("ExternalDataSource.EDS1.Table")
      .containsAll(
        SDBLLexer.EXTERNAL_DATA_SOURCE_TYPE,
        SDBLLexer.DOT,
        SDBLLexer.IDENTIFIER,
        SDBLLexer.DOT,
        SDBLLexer.EDS_TABLE);
    testLexer.assertThat("ВнешнийИсточникДанных.ВИД1.Куб.Куб1.ТаблицаИзмерения")
      .isEqualTo("ExternalDataSource.EDS1.Cube.Cube1.DimensionTable")
      .containsAll(
        SDBLLexer.EXTERNAL_DATA_SOURCE_TYPE,
        SDBLLexer.DOT,
        SDBLLexer.IDENTIFIER,
        SDBLLexer.DOT,
        SDBLLexer.EDS_CUBE,
        SDBLLexer.DOT,
        SDBLLexer.IDENTIFIER,
        SDBLLexer.DOT,
        SDBLLexer.EDS_CUBE_DIMTABLE);
  }
}
