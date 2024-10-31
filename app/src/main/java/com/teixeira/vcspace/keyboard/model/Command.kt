package com.teixeira.vcspace.keyboard.model

data class Command(
  val name: String,
  val description: String? = null,
  val keybinding: String,
  val action: Command.() -> Unit
) {

  companion object {

    @JvmStatic
    val newCommand = { name: String, keybinding: String, action: Command.() -> Unit ->
      Command(
        name = name,
        keybinding = keybinding,
        action = action
      )
    }
  }

}
