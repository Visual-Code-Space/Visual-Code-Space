package com.raredev.vcspace.util;

import io.github.rosemoe.sora.text.Content;

public class CommentSystem {
  private static final String SINGLE_COMMENT_PREFIX = "//";
  private static final String MULTI_COMMENT_START = "/*";
  private static final String MULTI_COMMENT_END = "*/";

  public static void addSingleComment(String comment, Content text, int line) {
    String commentLine = SINGLE_COMMENT_PREFIX + comment;

    // Insert single-line comment at the beginning of the line
    text.insert(line, 0, commentLine);
  }

  public static void addMultiComment(String comment, Content text, int startLine, int endLine) {
    String[] commentLines = comment.split("\\n");

    // Insert multi-line comment at the beginning of the start line
    text.insert(startLine, 0, MULTI_COMMENT_START + "\n");

    // Insert comment lines after the start line
    
    //    for (int line = startLine + 1; line < endLine; line++) {
    //      String commentLine =
    //          line < startLine + commentLines.length ? commentLines[line - startLine - 1] : "";
    //
    //      text.insert(line, 0, "// " + commentLine + "\n");
    //    }

    // Insert multi-line comment end at the end of the end line
    text.insert(endLine + 2, 0, MULTI_COMMENT_END + "\n");
  }
}
