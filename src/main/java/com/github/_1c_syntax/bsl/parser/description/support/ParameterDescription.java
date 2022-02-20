/*
 * This file is a part of BSL Parser.
 *
 * Copyright (c) 2018-2022
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
package com.github._1c_syntax.bsl.parser.description.support;

import java.util.List;

/**
 * Описание параметра из комментария - описания метода
 */
public class ParameterDescription {
  /**
   * Имя параметра
   */
  private final String name;
  /**
   * Возможные типы параметра. Может быть пустым
   */
  private final List<TypeDescription> types;
  /**
   * Если описание параметров содержит только ссылку, то здесь будет ее значение
   * <p>
   * TODO Временное решение, надо будет продумать в следующем релизе
   */
  private final String link;
  /**
   * Признак того, что параметр является гиперссылкой
   */
  private final boolean isHyperlink;

  protected ParameterDescription(String name,
                                 List<TypeDescription> types,
                                 String link,
                                 boolean isHyperlink) {
    this.name = name;
    this.types = types;
    this.link = link;
    this.isHyperlink = isHyperlink;
  }

  public String getName() {
    return name;
  }

  public List<TypeDescription> getTypes() {
    return types;
  }

  public String getLink() {
    return link;
  }

  public boolean isHyperlink() {
    return isHyperlink;
  }
}
