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

package com.teixeira.vcspace.plugins.helper;

import com.teixeira.vcspace.keyboard.CommandAction;
import com.teixeira.vcspace.keyboard.CommandPaletteManager;
import com.teixeira.vcspace.keyboard.model.Command;

public class CommandManager {
  private final CommandPaletteManager commandPaletteManager = CommandPaletteManager.getInstance();

  public void show() {
    commandPaletteManager.show();
  }

  public void hide() {
    commandPaletteManager.hide();
  }

  public void addCommand(Command... command) {
    commandPaletteManager.addCommand(command);
  }

  public void addCommand(String name, String keybinding, CommandAction action) {
    addCommand(Command.getNewCommand().invoke(
      name,
      keybinding,
      (command, compositionContext) -> {
        action.performAction(command);
        return null;
      }
    ));
  }
}
