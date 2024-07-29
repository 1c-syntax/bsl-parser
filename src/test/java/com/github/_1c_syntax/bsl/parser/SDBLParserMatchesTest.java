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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SDBLParserMatchesTest {

  private TestParser<SDBLParser, SDBLLexer> testParser;

  @BeforeEach
  void before() {
    testParser = new TestParser<>(SDBLParser.class, SDBLLexer.class);
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "ВЫБРАТь 1",
      "Select Fld from table",
      "Drop table",
      """
        Выбрать * из Справочник.Пользователи;
        Выбрать * поместиТЬ ВТ Из &Таблица где поле = 1;
        Уничтожить ТЧ;        
        """
    }
  )
  void testQueryPackage(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().queryPackage());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "", ";", "Какой-то текст", "Запрос = Новый Запрос()"
    }
  )
  void testNoQueryPackage(String inputString) {
    testParser.assertThat(inputString).noMatches(testParser.parser().queryPackage());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "ВЫБРАТь 1",
      "Select Fld from table",
      "drop table",
      """
        Выбрать * из Справочник.Пользователи;
        Выбрать * поместиТЬ ВТ Из &Таблица где поле = 1;
        Уничтожить ТЧ;        
        """
    }
  )
  void testQueries(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().queries());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "ВЫБРАТь",
      "Уничтожить;",
      """
        УничтожитьТЧ;        
        Уничтожить;
        """
    }
  )
  void testNoQueries(String inputString) {
    testParser.assertThat(inputString).noMatches(testParser.parser().queries());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "drop table",
      """
        Уничтожить ТЧ;        
        Уничтожить ТЧ2;
        """
    }
  )
  void testDropTableQuery(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().dropTableQuery());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "drops table",
      """
        Выбрать 1;        
        Уничтожить ТЧ2;
        """,
      ""
    }
  )
  void testNoDropTableQuery(String inputString) {
    testParser.assertThat(inputString).noMatches(testParser.parser().dropTableQuery());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "выбрать поле из таблица",
      "выбрать поле из таблица объединить выбрать поле2 из таблица2 упорядочить поле",
      "выбрать первые 1 поле из таблица объединить выбрать поле2 из таблица2 упорядочить поле сгруппировать по поле",
      "выбрать * из таблица, таблица2 сгруппировать по поле1 итоги по поле2",
    }
  )
  void testSelectQuery(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().selectQuery());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "выбрать поле из таблица",
      "выбрать поле из таблица объединить выбрать поле2 из таблица2 упорядочить поле",
      "выбрать первые 1 поле из таблица объединить выбрать поле2 из таблица2 упорядочить поле сгруппировать по поле",
      "выбрать * из таблица сгруппировать по поле1 итоги сумма(1) по поле2",
    }
  )
  void testSubquery(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().subquery());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "Объединить выбрать 1", "объединить все выбрать 2", "объединить все выбрать 2 упорядочить по поле"
    }
  )
  void testUnion(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().union());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "выбрать 1", "выбрать первые 1 2", "выбрать 1 как поле поместить вт",
      "выбрать 1 как поле поместить .вт", "выбрать 1 как поле поместить вт#1#", "выбрать 1 как поле поместить ##",
      "выбрать различные поле из таблица", "выбрать различные Таблица.поле как поле из таблица",
      "выбрать различные Сумма(Sin(поле)) из таблица индексировать по поле",
      "выбрать поле, поле1, таблица.поле3 как как из таблица сгруппировать по поле для изменения"
    }
  )
  void testQuery(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().query());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "первые 0",
      "первые 10 разрешенные различные",
      "различные первые 10 разрешенные ",
      "разрешенные различные первые 10 "
    }
  )
  void testLimitations(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().limitations());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "0", "\"текст\"", "NULL, Поле.Поле.Поле", "Поле.Поле.Поле КАК Поле", "*, \"\" as fld",
      "Поле", "ПустаяТаблица(), Sin(f), f"
    }
  )
  void testSelectedFields(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().selectedFields());
  }

  @ParameterizedTest
  @ValueSource(strings =
    {
      "0", "\"текст\"", "NULL как Поле", "Поле.Поле.Поле КАК Поле", "Поле.*", "\"\" as fld",
      "Поле", "Sin(f) as f"
    }
  )
  void testSelectedField(String inputString) {
    testParser.assertThat(inputString).matches(testParser.parser().selectedField());
  }
}
