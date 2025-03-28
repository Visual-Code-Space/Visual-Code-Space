package com.teixeira.vcspace.keyboard.model

import androidx.compose.runtime.CompositionContext
import androidx.compose.ui.input.key.Key

data class Command(
    val name: String,
    val description: String? = null,
    val keybinding: String?,
    val action: Command.(compositionContext: CompositionContext) -> Unit
) {

    companion object {

        @JvmStatic
        val newCommand =
            { name: String, keybinding: String?, action: Command.(compositionContext: CompositionContext) -> Unit ->
                Command(
                    name = name,
                    keybinding = keybinding,
                    action = action
                )
            }
    }
}

// Common keys for keybindings
fun String.toKey(): Key? {
    return when (this) {
        "A" -> Key.A
        "B" -> Key.B
        "C" -> Key.C
        "D" -> Key.D
        "E" -> Key.E
        "F" -> Key.F
        "G" -> Key.G
        "H" -> Key.H
        "I" -> Key.I
        "J" -> Key.J
        "K" -> Key.K
        "L" -> Key.L
        "M" -> Key.M
        "N" -> Key.N
        "O" -> Key.O
        "P" -> Key.P
        "Q" -> Key.Q
        "R" -> Key.R
        "S" -> Key.S
        "T" -> Key.T
        "U" -> Key.U
        "V" -> Key.V
        "W" -> Key.W
        "X" -> Key.X
        "Y" -> Key.Y
        "Z" -> Key.Z
        "0" -> Key.Zero
        "1" -> Key.One
        "2" -> Key.Two
        "3" -> Key.Three
        "4" -> Key.Four
        "5" -> Key.Five
        "6" -> Key.Six
        "7" -> Key.Seven
        "8" -> Key.Eight
        "9" -> Key.Nine
        "Ctrl" -> Key.CtrlLeft // Or Key.CtrlRight
        "Shift" -> Key.ShiftLeft // Or Key.ShiftRight
        "Alt" -> Key.AltLeft // Or Key.AltRight
        "Enter" -> Key.Enter
        "Backspace" -> Key.Backspace
        "Space" -> Key.Spacebar
        "Tab" -> Key.Tab
        "Esc" -> Key.Escape
        "Delete" -> Key.Delete
        "Home" -> Key.Home
        "End" -> Key.MoveEnd
        "Up" -> Key.DirectionUp
        "Down" -> Key.DirectionDown
        "Left" -> Key.DirectionLeft
        "Right" -> Key.DirectionRight
        "/" -> Key.Slash
        "." -> Key.Period
        "," -> Key.Comma
        "-" -> Key.Minus
        "=" -> Key.Equals
        "+" -> Key.Plus
        "[" -> Key.LeftBracket
        "]" -> Key.RightBracket
        else -> null // Return null if no match is found
    }
}

// Vice-versa of toKey()
fun Key.toShortcut(): String? {
    return when (this) {
        Key.A -> "A"
        Key.B -> "B"
        Key.C -> "C"
        Key.D -> "D"
        Key.E -> "E"
        Key.F -> "F"
        Key.G -> "G"
        Key.H -> "H"
        Key.I -> "I"
        Key.J -> "J"
        Key.K -> "K"
        Key.L -> "L"
        Key.M -> "M"
        Key.N -> "N"
        Key.O -> "O"
        Key.P -> "P"
        Key.Q -> "Q"
        Key.R -> "R"
        Key.S -> "S"
        Key.T -> "T"
        Key.U -> "U"
        Key.V -> "V"
        Key.W -> "W"
        Key.X -> "X"
        Key.Y -> "Y"
        Key.Z -> "Z"
        Key.Zero -> "0"
        Key.One -> "1"
        Key.Two -> "2"
        Key.Three -> "3"
        Key.Four -> "4"
        Key.Five -> "5"
        Key.Six -> "6"
        Key.Seven -> "7"
        Key.Eight -> "8"
        Key.Nine -> "9"
        Key.Enter -> "Enter"
        Key.Backspace -> "Backspace"
        Key.Spacebar -> "Space"
        Key.Tab -> "Tab"
        Key.Escape -> "Esc"
        else -> null // Return null if no match is found
    }
}
