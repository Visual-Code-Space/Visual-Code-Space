/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

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
      text.insert(cursor.leftLine, cursor.leftColumn, openPrefix)

      // Insert multi-line comment end at the end of the end line
      text.insert(cursor.rightLine, cursor.rightColumn, closePrefix)
    }
  }
}
