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
import com.github._1c_syntax.bsl.parser.description.support.SimpleRange;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

/**
 * Описание простого типа
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleTypeDescription implements TypeDescription {
  public static final SimpleTypeDescription EMPTY = new SimpleTypeDescription(
    "",
    "",
    Collections.emptyList(),
    new DescriptionElement(SimpleRange.create(0, 0, 0, 0), DescriptionElement.Type.UNKNOWN)
  );

  @Accessors(fluent = true)
  String name;

  @Accessors(fluent = true)
  String description;

  @Accessors(fluent = true)
  List<ParameterDescription> fields;

  @Accessors(fluent = true)
  DescriptionElement element;

  public static TypeDescription create(String name, DescriptionElement element, String description, List<ParameterDescription> fieldList) {
    if (name.isBlank() && description.isBlank()) {
      return EMPTY;
    }
    return new SimpleTypeDescription(
      name.strip().intern(),
      description.strip(),
      fieldList,
      element
    );
  }

  @Override
  public Variant variant() {
    return Variant.SIMPLE;
  }
}
