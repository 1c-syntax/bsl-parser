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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс для хранения описания типа коллекции.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionTypeDescription implements TypeDescription {

  @Accessors(fluent = true)
  String name;

  @Accessors(fluent = true)
  String description;

  @Accessors(fluent = true)
  List<ParameterDescription> fields;

  /**
   * Имя коллекции типа
   */
  @Accessors(fluent = true)
  String collectionName;

  /**
   * Список значений типов элементов коллекции.
   * <p>
   * Соответствует записям вида {@code Массив из Тип1, Тип2}. Для коллекций
   * без указания типов значений список пустой.
   */
  @Accessors(fluent = true)
  List<TypeDescription> valueTypes;

  /**
   * Элемент описания имени коллекции
   */
  @Accessors(fluent = true)
  DescriptionElement element;

  public static TypeDescription create(String collectionName,
                                       DescriptionElement element,
                                       String description,
                                       List<TypeDescription> valueTypes,
                                       List<ParameterDescription> fieldList) {
    var meaningfulValues = valueTypes.stream()
      .filter(vt -> !vt.name().isEmpty())
      .collect(Collectors.toUnmodifiableList());
    var joined = meaningfulValues.stream()
      .map(TypeDescription::name)
      .collect(Collectors.joining(", "));
    var name = collectionName;
    if (!joined.isEmpty()) {
      name += "<" + joined + ">";
    }

    return new CollectionTypeDescription(
      name.strip().intern(),
      description.strip(),
      fieldList,
      collectionName.strip().intern(),
      meaningfulValues,
      element
    );
  }

  @Override
  public Variant variant() {
    return Variant.COLLECTION;
  }
}
