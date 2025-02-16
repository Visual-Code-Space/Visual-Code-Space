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

package com.vcspace.plugins.command;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vcspace.plugins.Editor;

/**
 * The {@code EditorCommand} interface defines a command that can be executed within an editor.
 * Each command has a unique ID, a user-friendly name, an optional key binding, and an execute method.
 *
 * <p>Implementations of this interface represent specific actions that can be performed within
 * an editor, such as saving a file, formatting code, or performing a search.  They encapsulate
 * the logic for performing these actions and can be invoked directly or through user interactions
 * like menu selections or key bindings.
 *
 * <p>The execute method is the core of a command. It is called when the command is triggered,
 * and it carries out the action on a specified editor instance.
 */
public interface EditorCommand {
    /**
     * Returns the unique ID of this command.
     *
     * @return the command ID
     */
    @NonNull
    String getCommandId();

    /**
     * Returns the user-friendly name of this command.
     *
     * @return the command name
     */
    @NonNull
    String getName();

    /**
     * Returns the key binding associated with this command, if any.
     * The key binding is a string representation, e.g., "Ctrl+S".
     *
     * @return the key binding, or null if none
     */
    @Nullable
    String getKeyBinding();

    /**
     * Executes this command on the given editor instance.
     *
     * @param editor the editor instance
     */
    void execute(@NonNull Editor editor);
}
