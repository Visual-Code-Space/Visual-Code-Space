package com.raredev.vcspace.util;

import io.github.rosemoe.sora.text.Content;

public class CommentSystem {
  public static void addSingleComment(String commentPrefix, Content text, int line) {
    String commentLine = commentPrefix;

    // Insert single-line comment at the beginning of the line
    text.insert(line, 0, commentLine);
  }

  public static void addBlockComment(String blockCommentOpenPrefix, String blockCommentClosePrefix, Content text, int startLine, int endLine) {
    // Insert multi-line comment at the beginning of the start line
    text.insert(startLine, 0, blockCommentOpenPrefix + "\n");

    // Insert comment lines after the start line
    
    //    for (int line = startLine + 1; line < endLine; line++) {
    //      String commentLine =
    //          line < startLine + commentLines.length ? commentLines[line - startLine - 1] : "";
    //
    //      text.insert(line, 0, "// " + commentLine + "\n");
    //    }

    // Insert multi-line comment end at the end of the end line
    text.insert(endLine + 2, 0, blockCommentClosePrefix + "\n");
  }
}
