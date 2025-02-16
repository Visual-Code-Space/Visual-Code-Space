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

package com.teixeira.vcspace.plugins.internal

import java.io.File
import java.util.Properties

class PluginInfo(private val file: File) {
    private val properties = Properties()
    private val comment = "VCSpace Plugin Properties"

    var enabled: Boolean
        get() = properties.getProperty("enabled", "true") == "true"
        set(value) {
            properties.setProperty("enabled", value.toString())
            updateProperties()
        }

    init {
        properties.load(file.inputStream())
        enabled = true
        updateProperties()
    }

    private fun updateProperties() {
        properties.store(file.outputStream(), comment)
    }

    var name: String?
        get() = properties.getProperty("name")
        set(value) {
            if (value == null) {
                properties.remove("name")
            } else properties.setProperty("name", value)
            updateProperties()
        }

    var icon: String?
        get() = properties.getProperty("icon")
        set(value) {
            if (value == null) {
                properties.remove("icon")
            } else properties.setProperty("icon", value)
            updateProperties()
        }

    var id: String
        get() = properties.getProperty("id", name)
        set(value) {
            properties.setProperty("id", value)
            updateProperties()
        }

    var pluginFileName: String?
        get() = properties.getProperty("pluginFileName")
        set(value) {
            if (value == null) {
                properties.remove("pluginFileName")
            } else properties.setProperty("pluginFileName", value)
            updateProperties()
        }

    var version: String?
        get() = properties.getProperty("version")
        set(value) {
            if (value == null) {
                properties.remove("version")
            } else properties.setProperty("version", value)
            updateProperties()
        }

    var description: String?
        get() = properties.getProperty("description")
        set(value) {
            if (value == null) {
                properties.remove("description")
            } else properties.setProperty("description", value)
            updateProperties()
        }

    var author: String?
        get() = properties.getProperty("author")
        set(value) {
            if (value == null) {
                properties.remove("author")
            } else properties.setProperty("author", value)
            updateProperties()
        }

    var website: String?
        get() = properties.getProperty("website")
        set(value) {
            if (value == null) {
                properties.remove("website")
            } else properties.setProperty("website", value)
            updateProperties()
        }

    var license: String?
        get() = properties.getProperty("license")
        set(value) {
            if (value == null) {
                properties.remove("license")
            } else properties.setProperty("license", value)
            updateProperties()
        }

    var dependencies: List<String>
        get() = properties.getProperty("dependencies", "").split(",")
        set(value) {
            if (value.isEmpty()) {
                properties.remove("dependencies")
            } else properties.setProperty("dependencies", value.joinToString(","))
            updateProperties()
        }

    var mainClass: String?
        get() = properties.getProperty("mainClass")
        set(value) {
            if (value == null) {
                properties.remove("mainClass")
            } else properties.setProperty("mainClass", value)
            updateProperties()
        }

    var packageName: String?
        get() = properties.getProperty("packageName")
        set(value) {
            if (value == null) {
                properties.remove("packageName")
            } else properties.setProperty("packageName", value)
            updateProperties()
        }
}
