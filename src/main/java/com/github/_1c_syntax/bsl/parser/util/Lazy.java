/*
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018-2020
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Gryzlov <nixel2007@gmail.com>, Sergey Batanov <sergey.batanov@dmpas.ru>
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
package com.github._1c_syntax.bsl.parser.util;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public final class Lazy<T> {

  private Supplier<T> supplier;
  private volatile T value;

  public Lazy() {
    // no need to initialize lazy-value
  }

  public Lazy(Supplier<T> supplier) {
    // no need to initialize lazy-value
    this.supplier = supplier;
  }

  public T getOrCompute(Supplier<T> supplier) {
    final T result = value; // Just one volatile read
    return result == null ? maybeCompute(supplier) : result;
  }

  public T getOrCompute() {
    return getOrCompute(supplier);
  }

  public boolean isPresent() {
    final T result = value;
    return result != null;
  }

  public void clear() {
    value = null;
  }

  private synchronized T maybeCompute(Supplier<T> supplier) {
    if (value == null) {
      requireNonNull(supplier);
      value = requireNonNull(supplier.get());
    }
    return value;
  }
}
