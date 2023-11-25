/**
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
package com.raredev.vcspace.dialogs

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raredev.vcspace.res.databinding.LayoutProgressDialogBinding

class ProgressDialogBuilder(val context: Context) {

  private val binding = LayoutProgressDialogBinding.inflate(LayoutInflater.from(context))
  private val builder = MaterialAlertDialogBuilder(context).setView(binding.root)

  fun getDialogBuilder(): MaterialAlertDialogBuilder {
    return this.builder
  }

  fun setPositiveButton(
      text: String,
      listener: DialogInterface.OnClickListener
  ): ProgressDialogBuilder {
    builder.setPositiveButton(text, listener)
    return this
  }

  fun setPositiveButton(
      @StringRes text: Int,
      listener: DialogInterface.OnClickListener
  ): ProgressDialogBuilder {
    builder.setPositiveButton(text, listener)
    return this
  }

  fun setNegativeButton(
      text: String,
      listener: DialogInterface.OnClickListener
  ): ProgressDialogBuilder {
    builder.setNegativeButton(text, listener)
    return this
  }

  fun setNegativeButton(
      @StringRes text: Int,
      listener: DialogInterface.OnClickListener
  ): ProgressDialogBuilder {
    builder.setNegativeButton(text, listener)
    return this
  }

  fun setNeutralButton(
      text: String,
      listener: DialogInterface.OnClickListener
  ): ProgressDialogBuilder {
    builder.setNeutralButton(text, listener)
    return this
  }

  fun setNeutralButton(
      @StringRes text: Int,
      listener: DialogInterface.OnClickListener
  ): ProgressDialogBuilder {
    builder.setNeutralButton(text, listener)
    return this
  }

  fun show(): AlertDialog = builder.show()

  fun create(): AlertDialog = builder.create()

  fun setTitle(title: String): ProgressDialogBuilder {
    builder.setTitle(title)
    return this
  }

  fun setTitle(@StringRes title: Int): ProgressDialogBuilder {
    builder.setTitle(title)
    return this
  }

  fun setMessage(message: String): ProgressDialogBuilder {
    binding.message.text = message
    return this
  }

  fun setMessage(@StringRes message: Int): ProgressDialogBuilder {
    binding.message.setText(message)
    return this
  }

  fun setProgress(progress: Int): ProgressDialogBuilder {
    binding.indicator.setProgressCompat(progress, true)
    return this
  }

  fun setMax(max: Int): ProgressDialogBuilder {
    binding.indicator.setMax(max)
    return this
  }

  fun setMin(min: Int): ProgressDialogBuilder {
    binding.indicator.setMin(min)
    return this
  }

  fun setCancelable(cancelable: Boolean): ProgressDialogBuilder {
    builder.setCancelable(cancelable)
    return this
  }
}
