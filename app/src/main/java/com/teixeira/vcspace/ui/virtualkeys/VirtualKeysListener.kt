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

package com.teixeira.vcspace.ui.virtualkeys

import android.view.View
import android.widget.Button
import com.termux.terminal.TerminalSession

// https://github.com/RohitKushvaha01/ReTerminal/blob/main/app/src/main/java/com/rk/terminal/terminal/virtualkeys/VirtualKeysListener.kt
class VirtualKeysListener(private val session: TerminalSession) : VirtualKeysView.IVirtualKeysView {
    override fun onVirtualKeyButtonClick(
        view: View?,
        buttonInfo: VirtualKeyButton?,
        button: Button?,
    ) {
        val key = buttonInfo?.key ?: return
        val writeable: String =
            when (key) {
                "UP" -> "\u001B[A" // Escape sequence for Up Arrow
                "DOWN" -> "\u001B[B" // Escape sequence for Down Arrow
                "LEFT" -> "\u001B[D" // Escape sequence for Left Arrow
                "RIGHT" -> "\u001B[C" // Escape sequence for Right Arrow
                "ENTER" -> "\u000D" // Carriage Return for Enter
                "PGUP" -> "\u001B[5~" // Escape sequence for Page Up
                "PGDN" -> "\u001B[6~" // Escape sequence for Page Down
                "TAB" -> "\u0009" // Horizontal Tab
                "HOME" -> "\u001B[H" // Escape sequence for Home
                "END" -> "\u001B[F" // Escape sequence for End
                "ESC" -> "\u001B" // Escape
                else -> key
            }

        session.write(writeable)
    }

    override fun performVirtualKeyButtonHapticFeedback(
        view: View?,
        buttonInfo: VirtualKeyButton?,
        button: Button?,
    ): Boolean {
        return false
    }
}