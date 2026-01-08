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
package com.github._1c_syntax.bsl.parser.description;

import com.github._1c_syntax.bsl.parser.description.support.DescriptionElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Описание типа параметра, прочитанного из описания метода
 *
 */
public interface TypeDescription {
  /**
   * Возвращает имя типа
   *
   * @return имя типа
   */
  String name();

  /**
   * Возвращает текстовое описание типа
   *
   * @return Описание типа
   */
  String description();

  /**
   * Возвращает вариант типа
   *
   * @return Вариант типа
   */
  Variant variant();

  /**
   * Поля типа. Используется для описания сложных типов, хотя, иногда, из-за некорректности описания могут быть и у
   * простых.
   *
   * @return Список полей типа
   */
  List<ParameterDescription> fields();

  /**
   * Элемент описания имени параметра
   */
  DescriptionElement element();

  /**
   * Список элементов описания включая все дочерние описания (поля, типы...)
   *
   * @return Список элементов описания
   */
  default List<DescriptionElement> allElements() {
    List<DescriptionElement> elements = new ArrayList<>();
    elements.add(element());
    fields().forEach(field -> elements.addAll(field.allElements()));
    return elements;
  }

  /**
   * Возможные виды типов
   */
  enum Variant {
    SIMPLE,     // простой тип
    COLLECTION, // коллекция
    HYPERLINK   // гиперссылка
  }
}
