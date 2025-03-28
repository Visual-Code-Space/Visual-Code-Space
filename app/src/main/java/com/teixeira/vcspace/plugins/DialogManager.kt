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

package com.teixeira.vcspace.plugins

import androidx.compose.runtime.mutableStateOf
import com.vcspace.plugins.dialog.DialogButtonClickListener

class DialogManager {
    companion object {
        @JvmStatic
        val instance by lazy { DialogManager() }
    }

    val showDialog = mutableStateOf(false)

    val title = mutableStateOf("")
    val message = mutableStateOf("")
    val positiveButtonText = mutableStateOf("")
    val negativeButtonText = mutableStateOf("")
    val positiveButtonClickListener = mutableStateOf<DialogButtonClickListener?>(null)
    val negativeButtonClickListener = mutableStateOf<DialogButtonClickListener?>(null)

    fun showDialog() {
        showDialog.value = true
    }

    fun hideDialog() {
        showDialog.value = false
    }
}
