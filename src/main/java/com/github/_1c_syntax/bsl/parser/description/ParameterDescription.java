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
import com.github._1c_syntax.bsl.parser.description.support.Hyperlink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Описание параметра из комментария - описания метода
 *
 * @param name    Имя параметра
 * @param element Положение параметра в тексте
 * @param types   Список типов параметра
 */
public record ParameterDescription(String name, DescriptionElement element, List<TypeDescription> types) {
  /**
   * @param name    Имя параметра
   * @param element Положение параметра в тексте
   * @param types   Возможные типы параметра. Может быть пустым
   */
  public ParameterDescription(String name, DescriptionElement element, List<TypeDescription> types) {
    this.name = name.strip().intern();
    this.types = Collections.unmodifiableList(types);
    this.element = element;
  }

  /**
   * Если параметр имеет только один тип, равные ссылке - вернет истину
   *
   * @return Тип параметра является ссылкой
   */
  public boolean isHyperlink() {
    return types.size() == 1
      && types.get(0).variant() == TypeDescription.Variant.HYPERLINK;
  }

  /**
   * Если параметр является гиперссылкой, то вернет эту ссылку, иначе - пустую
   *
   * @return Содержимое гиперссылки
   */
  public Hyperlink link() {
    return (isHyperlink()) ? ((HyperlinkTypeDescription) types.get(0)).hyperlink() : Hyperlink.EMPTY;
  }

  /**
   * Список элементов описания включая все дочерние описания (поля, типы...)
   *
   * @return Список элементов описания
   */
  public List<DescriptionElement> allElements() {
    List<DescriptionElement> elements = new ArrayList<>();
    elements.add(element);
    types.forEach(type -> elements.addAll(type.allElements()));
    return Collections.unmodifiableList(elements);
  }
}
