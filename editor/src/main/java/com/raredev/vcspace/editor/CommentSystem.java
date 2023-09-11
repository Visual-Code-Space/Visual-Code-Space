package com.raredev.vcspace.editor;

import com.raredev.vcspace.utils.ToastUtils;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.Cursor;
import org.eclipse.tm4e.languageconfiguration.model.CharacterPair;
import org.eclipse.tm4e.languageconfiguration.model.CommentRule;

public class CommentSystem {

  public static void addSingleComment(CommentRule commentRule, Content text, int line) {
    String commentLine = commentRule.lineComment;

    if (commentLine != null) {
      // Insert single-line comment at the beginning of the line
      var lineText = text.getLine(line).toString();
      var column = lineText.indexOf(lineText.trim().charAt(0))-1;
      
      text.insert(line, column, commentLine);
    }
  }

  public static void addBlockComment(CommentRule commentRule, Content text, Cursor cursor) {
    if (!cursor.isSelected()) {
      ToastUtils.showShort("No text selected", ToastUtils.TYPE_ERROR);
      return;
    }

    CharacterPair blockComment = commentRule.blockComment;
    if (blockComment != null) {
      String openPrefix = blockComment.open;
      String closePrefix = blockComment.close;

      if (openPrefix == null || closePrefix == null) {
        for (int line = cursor.getLeftLine(); line <= cursor.getRightLine(); line++) {
          addSingleComment(commentRule, text, line);
        }
        return;
      }

      // Insert multi-line comment at the beginning of the start line
      text.insert(cursor.getLeftLine(), cursor.getLeftColumn(), openPrefix);

      // Insert multi-line comment end at the end of the end line
      text.insert(cursor.getRightLine(), cursor.getRightColumn(), closePrefix);
    }
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
