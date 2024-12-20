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

package com.itsvks.monaco.option


@JvmInline
value class EditorAutoClosingStrategy private constructor(
  override val value: String
) : Option<String> {
  companion object {
    val Never = EditorAutoClosingStrategy("never")
    val Always = EditorAutoClosingStrategy("always")
    val LanguageDefined = EditorAutoClosingStrategy("languageDefined")
    val BeforeWhitespace = EditorAutoClosingStrategy("beforeWhitespace")
  }
}

@JvmInline
value class EditorAutoClosingEditStrategy private constructor(
  override val value: String
) : Option<String> {
  companion object {
    val Always = EditorAutoClosingEditStrategy("always")
    val Auto = EditorAutoClosingEditStrategy("auto")
    val Never = EditorAutoClosingEditStrategy("never")
  }
}
