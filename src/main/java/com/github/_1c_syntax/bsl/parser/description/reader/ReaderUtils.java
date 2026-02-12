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
package com.github._1c_syntax.bsl.parser.description.reader;

import com.github._1c_syntax.bsl.parser.BSLDescriptionParser;
import com.github._1c_syntax.bsl.parser.description.support.Hyperlink;
import com.github._1c_syntax.bsl.parser.description.support.SimpleRange;
import edu.umd.cs.findbugs.annotations.Nullable;
import lombok.experimental.UtilityClass;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.Trees;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * Вспомогательные методы для чтения описаний
 */
@UtilityClass
public class ReaderUtils {

  /**
   * Извлекает и объединяет текст из дочерних элементов заданного контекста правила парсера
   *
   * @param ctx контекст правила парсера, из которого извлекается текст; может быть null
   *
   * @return объединенный текст из дочерних элементов контекста без начальных и конечных пробелов, или пустая строка,
   * если контекст равен null или пуст
   */
  public String extractText(@Nullable ParserRuleContext ctx) {
    if (ctx == null || ctx.isEmpty()) {
      return "";
    }
    var strings = new StringJoiner("");
    for (var i = 0; i < ctx.getChildCount(); i++) {
      var child = ctx.getChild(i);

      if (child != null && !(child instanceof BSLDescriptionParser.StartPartContext)) {
        strings.add(child.getText());
      }
    }

    return strings.toString().strip();
  }

  /**
   * Извлекает все ссылки из всех подчиненных узлов указанного
   *
   * @param ast                Корневой узел дерева для извлечения ссылок
   * @param lineShift          Сдвиг строк для корректировки позиций
   * @param firstLineCharShift Сдвиг символов в первой строке для корректировки позиций
   *
   * @return Список ссылок
   */
  public List<Hyperlink> readLinks(ParserRuleContext ast, int lineShift, int firstLineCharShift) {
    Collection<BSLDescriptionParser.HyperlinkContext> links =
      Trees.findAllRuleNodes(ast, BSLDescriptionParser.RULE_hyperlink);
    if (!links.isEmpty()) {
      return links.stream()
        .map(
          (BSLDescriptionParser.HyperlinkContext hyperlinkContext) -> {
            var link = hyperlinkContext.link == null ? "" : hyperlinkContext.link.getText();
            var params = hyperlinkContext.linkParams == null ? "" : hyperlinkContext.linkParams.getText();
            var range = SimpleRange.create(hyperlinkContext.getStart(), hyperlinkContext.getStop());
            var shiftedRange = SimpleRange.shift(range, lineShift, firstLineCharShift);
            return Hyperlink.create(link, params, shiftedRange);
          })
        .toList();
    }
    return Collections.emptyList();
  }
}
