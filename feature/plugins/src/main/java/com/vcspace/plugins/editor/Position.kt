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

package com.vcspace.plugins.editor

/**
 * Represents a cursor position within the editor.
 *
 * @property lineNumber The line number of the cursor position (1-based). Defaults to 1.
 * @property column The column number of the cursor position (1-based). Defaults to 1.
 *
 * @constructor Creates a new Position instance with the specified line and column numbers.
 *
 */
data class Position @JvmOverloads constructor(
  val lineNumber: Int = 1,
  val column: Int = 1
)
