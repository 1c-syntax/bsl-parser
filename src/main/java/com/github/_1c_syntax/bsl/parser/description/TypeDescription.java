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

import lombok.Value;

import java.util.List;

/**
 * Описание типа параметра, прочитанного из описания метода
 */
@Value
public class TypeDescription {

  /**
   * Имя типа. На данный момент может быть строковый массив перечисления типов а также гиперссылка на метод
   */
  String name;

  /**
   * Описание типа. Может быть пустым
   */
  String description;

  /**
   * Параметры (ключи или поля) типа для сложных типов данных. Может быть пустым
   */
  List<ParameterDescription> parameters;

  /**
   * Если описание параметров содержит только ссылку, то здесь будет ее значение
   * <p>
   * TODO Временное решение, надо будет продумать в следующем релизе
   */
  String link;

  /**
   * Признак того, что параметр является гиперссылкой
   */
  boolean isHyperlink;

  public TypeDescription(String name,
                         String description,
                         List<ParameterDescription> parameters,
                         String link,
                         boolean isHyperlink) {
    this.name = name;
    this.description = description;
    this.parameters = parameters;
    this.link = link;
    this.isHyperlink = isHyperlink;
  }
}
