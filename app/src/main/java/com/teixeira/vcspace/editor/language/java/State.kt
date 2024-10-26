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

package com.teixeira.vcspace.editor.language.java

import java.util.Objects

class State {
  var state: Int = 0
  var hasBraces: Boolean = false
  var identifiers: MutableList<String>? = null

  fun addIdentifier(idt: CharSequence) {
    if (identifiers == null) {
      identifiers = ArrayList()
    }
    if (idt is String) {
      identifiers!!.add(idt)
    } else {
      identifiers!!.add(idt.toString())
    }
  }

  override fun equals(other: Any?): Boolean {
    // `identifiers` is ignored because it is unrelated to tokenization for next line
    if (this === other) return true
    if (other == null || javaClass != other.javaClass) return false
    val state1 = other as State
    return state == state1.state && hasBraces == state1.hasBraces
  }

  override fun hashCode(): Int {
    return Objects.hash(state, hasBraces)
  }
}