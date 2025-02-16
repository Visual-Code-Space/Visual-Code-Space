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

import com.google.gson.Gson

data class GuidesOptions(
    val bracketPairs: BracketPairs? = null,
    val bracketPairsHorizontal: BracketPairs? = null,
    val highlightActiveBracketPair: Boolean? = null,
    val indentation: Boolean? = null,
    val highlightActiveIndentation: HighlightActiveIndentation? = null,
) {
    fun toJson() = Gson().toJson(this)
}

@JvmInline
value class BracketPairs private constructor(override val value: String) : Option<String> {
    companion object {
        val On = BracketPairs("true")
        val Off = BracketPairs("false")
        val Active = BracketPairs("active")
    }
}

@JvmInline
value class HighlightActiveIndentation private constructor(override val value: String) :
    Option<String> {
    companion object {
        val True = HighlightActiveIndentation("true")
        val False = HighlightActiveIndentation("false")
        val Always = HighlightActiveIndentation("always")
    }
}
