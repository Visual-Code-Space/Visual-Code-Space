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

/**
 * The kind of animation in which the editor's cursor should be rendered.
 */
@JvmInline
value class TextEditorCursorBlinkingStyle private constructor(val value: Int) {
    override fun toString(): String {
        return when (value) {
            0 -> "Hidden"
            1 -> "Blink"
            2 -> "Smooth"
            3 -> "Phase"
            4 -> "Expand"
            5 -> "Solid"
            else -> throw IllegalArgumentException("Unknown value: $value")
        }
    }

    companion object {
        fun fromValue(value: Int) = when (value) {
            0 -> Hidden
            1 -> Blink
            2 -> Smooth
            3 -> Phase
            4 -> Expand
            5 -> Solid
            else -> throw IllegalArgumentException("Unknown value: $value")
        }

        /**
         * Hidden
         */
        val Hidden = TextEditorCursorBlinkingStyle(0)

        /**
         * Blinking
         */
        val Blink = TextEditorCursorBlinkingStyle(1)

        /**
         * Blinking with smooth fading
         */
        val Smooth = TextEditorCursorBlinkingStyle(2)

        /**
         * Blinking with prolonged filled state and smooth fading
         */
        val Phase = TextEditorCursorBlinkingStyle(3)

        /**
         * Expand collapse animation on the y axis
         */
        val Expand = TextEditorCursorBlinkingStyle(4)

        /**
         * No-Blinking
         */
        val Solid = TextEditorCursorBlinkingStyle(5)
    }
}