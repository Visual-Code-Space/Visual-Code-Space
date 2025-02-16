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

package com.vcspace.plugins;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vcspace.plugins.editor.Position;

import java.io.File;

/**
 * Represents the editor within the application.
 */
public interface Editor {

    /**
     * Gets the currently opened file in the editor.
     *
     * @return the currently opened file, or null if no file is open.
     */
    @Nullable
    File getCurrentFile();

    /**
     * Gets the application/activity context of the editor.
     *
     * @return the application/activity context.
     */
    @NonNull
    Context getContext();

    /**
     * Gets the current cursor position in the editor.
     *
     * @return the current cursor position.
     */
    @NonNull
    Position getCursorPosition();

    /**
     * Sets the cursor position in the editor.
     *
     * @param position the new cursor position.
     */
    void setCursorPosition(@NonNull Position position);
}
