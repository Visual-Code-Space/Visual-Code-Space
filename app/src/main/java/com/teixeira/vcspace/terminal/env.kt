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

package com.teixeira.vcspace.terminal

import com.blankj.utilcode.util.PathUtils
import java.io.File

val appDataDir: File
    get() = File(PathUtils.getInternalAppDataPath(), "files").apply {
        if (!exists()) {
            mkdirs()
        }
    }

val prefix: File
    get() = File(appDataDir, "usr").apply {
        if (!exists()) {
            mkdirs()
        }
    }

val home: File
    get() = File(appDataDir, "home").apply {
        if (!exists()) {
            mkdirs()
        }
    }

val tmp: File
    get() = File(appDataDir, "tmp").apply {
        if (!exists()) {
            mkdirs()
        }
    }

val bin: File
    get() = File(prefix, "bin").apply {
        if (!exists()) {
            mkdirs()
        }
    }

val lib: File
    get() = File(prefix, "lib").apply {
        if (!exists()) {
            mkdirs()
        }
    }

val alpineDir: File
    get() = File(appDataDir, "local/alpine").apply {
        if (!exists()) {
            mkdirs()
        }
    }
