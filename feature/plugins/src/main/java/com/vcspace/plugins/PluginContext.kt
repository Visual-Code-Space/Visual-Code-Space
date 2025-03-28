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

package com.vcspace.plugins

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.vcspace.plugins.command.EditorCommand
import com.vcspace.plugins.dialog.DialogButtonClickListener
import com.vcspace.plugins.editor.Position
import com.vcspace.plugins.event.EventListener
import com.vcspace.plugins.event.EventType
import com.vcspace.plugins.menu.MenuAction
import com.vcspace.plugins.menu.MenuItem
import com.vcspace.plugins.network.HttpResponse
import com.vcspace.plugins.panel.ComposeFactory
import com.vcspace.plugins.panel.Panel
import com.vcspace.plugins.panel.ViewFactory
import com.vcspace.plugins.panel.ViewUpdater
import java.io.File

/**
 * Provides the context for interacting with the application while developing a plugin.
 */
interface PluginContext {
    companion object {
        private const val TAG = "PluginContext"
    }

    /**
     * Gets the application context.
     *
     * @return the application context.
     */
    val appContext: Context

    /**
     * Gets the editor instance for file and cursor manipulation.
     *
     * @return the editor instance.
     */
    val editor: Editor

    val rootView: ViewGroup
    val openedFolder: File?

    /**
     * Registers a custom editor command in command palette.
     *
     * @param command the editor command to register.
     */
    fun registerCommand(command: EditorCommand)

    /**
     * Adds a menu item to the application's menu system.
     *
     * @param menuItem the menu item to add.
     */
    fun addMenu(menuItem: MenuItem)

    /**
     * Opens a specified file in the editor.
     *
     * @param file the file to open.
     */
    fun openFile(file: File)

    /**
     * Sets the cursor position in the editor.
     *
     * @param position the new cursor position.
     */
    fun setCursorPosition(position: Position) {
        editor.cursorPosition = position
    }

    /**
     * Opens a specified file using a file path.
     *
     * @param filePath the file path of the file to open.
     */
    fun openFile(filePath: String) {
        openFile(File(filePath))
    }

    /**
     * Adds a menu item to the application's menu system with a title, ID, and action.
     *
     * @param title  the title of the menu item.
     * @param id     the unique identifier for the menu item.
     * @param action the action to execute when the menu item is selected.
     */
    fun addMenu(title: String, id: Int, action: MenuAction) {
        addMenu(MenuItem(title, id = id, action = action))
    }

    /**
     * Logs a message to the console for debugging purposes.
     *
     * @param message the message to log.
     */
    fun log(message: String) {
        Log.i(TAG, message)
    }

    /**
     * Displays a short toast message to the user.
     *
     * Example Usage:
     * ```kotlin
     * toast("This is a toast message")
     * ```
     *
     * @param message The message to be displayed in the toast.
     *
     */
    fun toast(message: String) {
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
    }

    fun createComposePanel(
        title: String,
        content: ComposeFactory,
        dismissOnClickOutside: Boolean
    ): Panel

    fun createComposePanel(
        title: String,
        content: ComposeFactory
    ): Panel = createComposePanel(title, content, true)

    fun <T : View> createViewPanel(
        title: String,
        factory: ViewFactory<T>,
        update: ViewUpdater<T>,
        dismissOnClickOutside: Boolean
    ): Panel

    fun <T : View> createViewPanel(
        title: String,
        factory: ViewFactory<T>,
        update: ViewUpdater<T>
    ): Panel = createViewPanel(title, factory, update, true)

    fun removePanel(panelId: String): Boolean

    /**
     * Executes the specified action on the IO thread.
     *
     * @param action the action to execute.
     */
    fun doActionOnIOThread(action: Runnable)

    /**
     * Executes the specified action on the main thread.
     *
     * @param action the action to execute.
     */
    fun doActionOnMainThread(action: Runnable)

    /**
     * Registers an event listener for a specific event type.
     *
     * @param type    the type of event to listen for.
     * @param listener the listener to be notified when the event occurs.
     */
    fun registerEventListener(type: EventType, listener: EventListener)

    /**
     * Unregisters an event listener for a specific event type.
     *
     * @param type    the type of event to stop listening for.
     * @param listener the listener to be removed.
     */
    fun unregisterEventListener(type: EventType, listener: EventListener)

    /**
     * Saves a configuration value associated with a key.
     *
     * @param key   the key to associate with the value.
     * @param value the value to save.
     */
    fun saveConfig(key: String, value: String)

    /**
     * Retrieves a configuration value associated with a key.
     *
     * @param key          the key to look up.
     * @param defaultValue the default value to return if the key is not found.
     * @return the configuration value associated with the key, or the default value if not found.
     */
    fun loadConfig(key: String, defaultValue: String): String

    /**
     * Displays a dialog with a title, message, and two buttons (positive and negative).
     *
     * @param title             the title of the dialog.
     * @param message           the message to display in the dialog.
     * @param positiveButtonText the text for the positive button.
     * @param negativeButtonText the text for the negative button.
     * @param onPositiveClick   the action to perform when the positive button is clicked.
     * @param onNegativeClick   the action to perform when the negative button is clicked.
     */
    fun showDialog(
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String?,
        onPositiveClick: DialogButtonClickListener?,
        onNegativeClick: DialogButtonClickListener?
    )

    fun showDialog(
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String? = null,
        onPositiveClick: DialogButtonClickListener? = null
    ) = showDialog(title, message, positiveButtonText, negativeButtonText, onPositiveClick, null)

    fun showDialog(
        title: String,
        message: String,
        positiveButtonText: String
    ) = showDialog(title, message, positiveButtonText, null, null)

    /**
     * Retrieves the current workspace instance.
     *
     * @return the current workspace instance, or null if not available.
     */
    fun getWorkspace(): Workspace?

    /**
     * Registers a shortcut key binding for a specific action.
     *
     * @param keyBinding the key binding to register.
     * @param action     the action to execute when the key binding is triggered.
     */
    fun registerShortcut(keyBinding: String, action: Runnable)

    /**
     * Performs an HTTP GET request to the specified URL.
     */
    fun httpGet(url: String): HttpResponse
}
