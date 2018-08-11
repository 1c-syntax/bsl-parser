package org.github._1c_syntax.parser.psi;

import com.intellij.psi.tree.IElementType;
import org.github._1c_syntax.parser.BSLLanguage;
import org.jetbrains.annotations.NotNull;

public class BSLElementType extends IElementType {
  public BSLElementType(@NotNull String debugName) {
    super(debugName, BSLLanguage.INSTANCE);
  }
}
