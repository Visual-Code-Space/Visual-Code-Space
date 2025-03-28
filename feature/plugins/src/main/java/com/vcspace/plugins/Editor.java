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
import com.vcspace.plugins.editor.Range;

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

    /**
     * Inserts text at the specified position in the editor.
     *
     * @param position the position to insert the text at.
     * @param text     the text to insert.
     */
    void insertText(@NonNull Position position, @NonNull String text);

    /**
     * Replaces text in the editor between the specified start and end positions with the given text.
     *
     * @param start the starting position of the text to replace.
     * @param end   the ending position of the text to replace.
     * @param text  the text to replace with.
     */
    void replaceText(@NonNull Position start, @NonNull Position end, String text);

    /**
     * Replaces text in the editor within the specified range with the given text.
     *
     * @param range the range of text to replace.
     * @param text  the text to replace with.
     */
    default void replaceText(@NonNull Range range, String text) {
        // Default implementation: replace with the specified text
        replaceText(range.getStart(), range.getEnd(), text);
    }

    /**
     * Deletes text in the editor between the specified start and end positions.
     *
     * @param start the starting position of the text to delete.
     * @param end   the ending position of the text to delete.
     */
    default void deleteText(@NonNull Position start, Position end) {
        // Default implementation: replace with an empty string
        replaceText(start, end, "");
    }

    /**
     * Deletes text in the editor within the specified range.
     *
     * @param range the range of text to delete.
     */
    default void deleteText(@NonNull Range range) {
        replaceText(range, "");
    }

    /**
     * Gets the current selection range in the editor.
     *
     * @return the current selection range.
     */
    @Nullable
    Range getSelectionRange();

    /**
     * Gets the text in the editor.
     *
     * @return the text in the editor.
     */
    @NonNull
    String getText();

    /**
     * Gets the text in the editor within the specified range.
     *
     * @param range the range of text to get.
     * @return the text within the specified range.
     */
    @Nullable
    String getText(@Nullable Range range);
}
