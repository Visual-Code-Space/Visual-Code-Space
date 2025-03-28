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

package com.vcspace.plugins.event

enum class EventType {
    /**
     * Event triggered when a file is opened.
     */
    FILE_OPENED,

    /**
     * Event triggered when a file is closed.
     */
    FILE_CLOSED,

    /**
     * Event triggered when a file is saved.
     */
    FILE_SAVED,

    /**
     * Event triggered when a file is modified.
     */
    FILE_MODIFIED,

    /**
     * Event triggered when a file is deleted.
     */
    FILE_DELETED,

    /**
     * Event triggered when a file is renamed.
     */
    FILE_RENAMED,

    /**
     * Event triggered when a file is created.
     */
    FILE_CREATED,

    /**
     * Event triggered when cursor position is changed.
     */
    CURSOR_POSITION_CHANGED,

    /**
     * Event triggered when a key is pressed.
     */
    KEY_PRESSED,

    /**
     * Event triggered when text is changed.
     */
    TEXT_CHANGED
}
