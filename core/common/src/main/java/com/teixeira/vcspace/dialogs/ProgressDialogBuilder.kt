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

package com.teixeira.vcspace.dialogs

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teixeira.vcspace.resources.databinding.LayoutProgressDialogBinding

class ProgressDialogBuilder(val context: Context) {

  private val binding = LayoutProgressDialogBinding.inflate(LayoutInflater.from(context))
  private val _builder = MaterialAlertDialogBuilder(context).setView(binding.root)

  val builder: MaterialAlertDialogBuilder
    get() = _builder

  fun setPositiveButton(text: String, listener: DialogInterface.OnClickListener) = apply {
    _builder.setPositiveButton(text, listener)
  }

  fun setPositiveButton(@StringRes text: Int, listener: DialogInterface.OnClickListener) = apply {
    _builder.setPositiveButton(text, listener)
  }

  fun setNegativeButton(text: String, listener: DialogInterface.OnClickListener) = apply {
    _builder.setNegativeButton(text, listener)
  }

  fun setNegativeButton(@StringRes text: Int, listener: DialogInterface.OnClickListener) = apply {
    _builder.setNegativeButton(text, listener)
  }

  fun setNeutralButton(text: String, listener: DialogInterface.OnClickListener) = apply {
    _builder.setNeutralButton(text, listener)
  }

  fun setNeutralButton(@StringRes text: Int, listener: DialogInterface.OnClickListener) = apply {
    _builder.setNeutralButton(text, listener)
  }

  fun show(): AlertDialog = _builder.show()

  fun create(): AlertDialog = _builder.create()

  fun setTitle(title: String) = apply { _builder.setTitle(title) }

  fun setTitle(@StringRes title: Int) = apply { _builder.setTitle(title) }

  fun setMessage(message: String) = apply { binding.message.text = message }

  fun setMessage(@StringRes message: Int) = apply { binding.message.setText(message) }

  fun setProgress(progress: Int) = apply { binding.indicator.setProgressCompat(progress, true) }

  fun setMax(max: Int) = apply { binding.indicator.setMax(max) }

  fun setMin(min: Int) = apply { binding.indicator.setMin(min) }

  fun setCancelable(cancelable: Boolean) = apply { _builder.setCancelable(cancelable) }
}
