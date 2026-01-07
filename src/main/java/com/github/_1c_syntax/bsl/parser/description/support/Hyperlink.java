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
package com.github._1c_syntax.bsl.parser.description.support;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Описывает гиперссылку в комментария-описаниях (см. ссылка(параметры_ссылки))
 *
 * @param link   Сама ссылка (до скобок)
 * @param params Параметры ссылки (в скобках)
 */
public record Hyperlink(String link, String params) {
  /**
   * Пустая ссылка
   */
  public static final Hyperlink EMPTY = new Hyperlink("", "");

  /**
   * Создает новый экземпляр {@link Hyperlink} с указанными ссылкой и параметрами.
   * Если ссылка равна null или пустая, возвращает пустой экземпляр {@link Hyperlink}.
   *
   * @param link   ссылка, может быть null
   * @param params параметры ссылки, может быть null
   * @return новый экземпляр {@link Hyperlink}, или пустой экземпляр, если ссылка равна null или пустая
   */
  public static Hyperlink create(@Nullable String link, @Nullable String params) {
    var linkText = link == null ? "" : link;
    var paramsText = params == null ? "" : params;

    if (linkText.isEmpty()) { // если ссылка пустая, то возвращаем пустой объект
      return EMPTY;
    }
    return new Hyperlink(linkText.intern(), paramsText);
  }

  /**
   * Конструктор гиперссылки на основании полной строки. При создании строка разделяется на части:
   * - часть до открывающей скобки считается ссылкой (#link)
   * - часть между первой открывающей скобкой и последй закрывающей является параметрами ссылки (#params)
   *
   * @param presentation Полный текст гиперссылки
   * @return Созданный объект
   */
  public static Hyperlink create(String presentation) {
    var pos = presentation.indexOf("(");
    if (pos > 0 && pos + 1 < presentation.length()) {
      var posEnd = presentation.lastIndexOf(")");
      if (posEnd < 0) {
        posEnd = presentation.length();
      }
      return create(presentation.substring(0, pos), presentation.substring(pos + 1, posEnd));
    } else {
      return create(presentation, "");
    }
  }

  @Override
  public boolean equals(@Nullable Object other) {
    if (this == other) {
      return true;
    } else if (!(other instanceof Hyperlink otherLink)) {
      return false;
    } else {
      return link.equals(otherLink.link) && params.equals(otherLink.params);
    }
  }

  @Override
  public String toString() {
    return link + (params.isEmpty() ? "" : "(" + params + ")");
  }
}
