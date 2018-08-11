package org.github._1c_syntax.parser.psi;

import com.intellij.psi.tree.IElementType;
import org.github._1c_syntax.parser.BSLLanguage;
import org.jetbrains.annotations.NotNull;

public class BSLTokenType extends IElementType {
  public BSLTokenType(@NotNull String debugName) {
    super(debugName, BSLLanguage.INSTANCE);
  }

  @Override
  public String toString() {
    return "BSLTokenType." + super.toString();
  }
}
