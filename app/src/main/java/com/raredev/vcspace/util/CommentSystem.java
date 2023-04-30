package com.raredev.vcspace.util;

import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.Cursor;

public class CommentSystem {
  public static void addSingleComment(String commentPrefix, Content text, int line) {
    String commentLine = commentPrefix;

    // Insert single-line comment at the beginning of the line
    text.insert(line, 0, commentLine);
  }

  public static void addBlockComment(
      String blockCommentOpenPrefix,
      String blockCommentClosePrefix,
      Content text,
      int startLine,
      int endLine) {
    // Insert multi-line comment at the beginning of the start line
    text.insert(startLine, 0, blockCommentOpenPrefix + "\n");

    // Insert multi-line comment end at the end of the end line
    text.insert(endLine + 2, 0, blockCommentClosePrefix + "\n");
  }

/*
  public static String uncomment(Content text) {
    StringBuilder sb = new StringBuilder();
    boolean inBlockComment = false;
    int i = 0;

    while (i < text.length()) {
      char c = text.charAt(i);

      if (inBlockComment) {
        if (c == '*' && i < text.length() - 1 && text.charAt(i + 1) == '/') {
          inBlockComment = false;
          i++;
        }
      } else {
        if (c == '/' && i < text.length() - 1 && text.charAt(i + 1) == '/') {
          i = text.toString().indexOf('\n', i + 2);
          if (i == -1) {
            break;
          }
        } else if (c == '/' && i < text.length() - 1 && text.charAt(i + 1) == '*') {
          inBlockComment = true;
          i++;
        } else {
          sb.append(c);
        }
      }

      i++;
    }

    return sb.toString();
  }
  */
}
