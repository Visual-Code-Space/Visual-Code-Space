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

package com.teixeira.vcspace.utils

import android.content.Context

// From
// https://github.com/PsiCodes/KtxPy/blob/master/app/src/main/java/github/psicodes/ktxpy/utils/Commands.kt
object TerminalPythonCommands {
    fun getInterpreterCommand(context: Context, filePath: String): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        val pythonExecName = "libpython3.so"
        return "export PATH=\$PATH:$appLibDirPath && export PYTHONHOME=$pythonBuildDirPath && export LD_LIBRARY_PATH=\"\$LD_LIBRARY_PATH:\" && export LD_LIBRARY_PATH=\"\$LD_LIBRARY_PATH${pythonLibDirPath}\" && clear && $pythonExecName $filePath && echo \'\n[Process completed - press Enter]\' && read junk && exit"
    }
}
