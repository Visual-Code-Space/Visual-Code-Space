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

import androidx.annotation.NonNull;

/**
 * Represents a plugin interface that every plugin must implement.
 * The main entry point for the plugin, providing access to the application context and functionalities.
 */
public interface Plugin {

    /**
     * Called when the plugin is loaded into the application.
     *
     * @param context the context provided to interact with the application.
     */
    void onPluginLoaded(@NonNull PluginContext context);
}
