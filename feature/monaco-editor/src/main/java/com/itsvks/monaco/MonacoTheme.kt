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

package com.itsvks.monaco

@JvmInline
value class MonacoTheme private constructor(val value: String) {
    override fun toString(): String {
        return when (value) {
            VisualStudioLight.value -> "VisualStudioLight"
            VisualStudioDark.value -> "VisualStudioDark"
            HighContrastLight.value -> "HighContrastLight"
            HighContrastDark.value -> "HighContrastDark"
            else -> super.toString()
        }
    }

    companion object {
        fun fromString(value: String): MonacoTheme {
            return when (value) {
                "vs" -> VisualStudioLight
                "vs-dark" -> VisualStudioDark
                "hc-light" -> HighContrastLight
                "hc-black" -> HighContrastDark
                else -> VisualStudioDark
            }
        }

        val VisualStudioLight = MonacoTheme("vs")
        val VisualStudioDark = MonacoTheme("vs-dark")
        val HighContrastLight = MonacoTheme("hc-light")
        val HighContrastDark = MonacoTheme("hc-black")
    }
}
