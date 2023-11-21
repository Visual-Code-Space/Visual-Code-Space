package com.raredev.vcspace.editor

import io.github.rosemoe.sora.text.Content
import org.eclipse.tm4e.languageconfiguration.model.CommentRule

object CommentSystem {

  @JvmStatic
  fun addSingleComment(commentRule: CommentRule?, text: Content) {
    if (commentRule == null) return
    val comment = commentRule.lineComment

    comment?.let { text.insert(text.cursor.leftLine, 0, it) }
  }

  @JvmStatic
  fun addBlockComment(commentRule: CommentRule?, text: Content) {
    if (commentRule == null) return
    val blockComment = commentRule.blockComment
    if (blockComment != null) {
      val openPrefix = blockComment.open
      val closePrefix = blockComment.close

      if (openPrefix == null || closePrefix == null) {
        return
      }

      val cursor = text.cursor
      if (!cursor.isSelected) {
        return
      }

      // Insert multi-line comment at the beginning of the start line
      text.insert(cursor.getLeftLine(), cursor.getLeftColumn(), openPrefix)

      // Insert multi-line comment end at the end of the end line
      text.insert(cursor.getRightLine(), cursor.getRightColumn(), closePrefix)
    }
  }
}
