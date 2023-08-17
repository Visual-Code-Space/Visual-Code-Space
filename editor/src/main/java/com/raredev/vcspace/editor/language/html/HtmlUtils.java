package com.raredev.vcspace.editor.language.html;

import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

public class HtmlUtils {

  public static boolean isClosed(DOMNode nodeAt) {
    if (!nodeAt.isClosed()) {
      return false;
    }
    DOMElement parent = nodeAt.getParentElement();
    if (parent != null && !parent.isClosed()) {
      if (nodeAt.getNodeName().equals(parent.getTagName())) {
        return false;
      }
    }
    return nodeAt.isClosed();
  }
}
