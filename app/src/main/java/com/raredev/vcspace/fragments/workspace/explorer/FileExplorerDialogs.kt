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
package com.raredev.vcspace.fragments.workspace.explorer

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raredev.vcspace.events.OnDeleteFileEvent
import com.raredev.vcspace.events.OnRenameFileEvent
import com.raredev.vcspace.extensions.launchWithProgressDialog
import com.raredev.vcspace.res.R
import com.raredev.vcspace.res.databinding.LayoutTextinputBinding
import com.raredev.vcspace.utils.showShortToast
import com.raredev.vcspace.viewmodel.FileExplorerViewModel
import java.io.File
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

class FileExplorerDialogs(
  private val explorerFragment: FileExplorerFragment,
  val context: Context,
  private val viewModel: FileExplorerViewModel
) {

  @SuppressLint("RestrictedApi")
  @Suppress("DEPRECATION")
  fun showCreateFileDialog(path: String) {
    val binding = LayoutTextinputBinding.inflate(LayoutInflater.from(context))
    val dialog =
      MaterialAlertDialogBuilder(context)
        .setView(binding.root, dp2px(20f), dp2px(15f), dp2px(20f), dp2px(15f))
        .setTitle(R.string.create)
        .setNeutralButton(R.string.cancel, null)
        .setNegativeButton(
          R.string.file
        ) { _, _ ->
          val name = binding.inputEdittext.text.toString().trim()
          val newFile = File(path, name)
          if (newFile.exists()) {
            return@setNegativeButton
          }
          try {
            if (newFile.createNewFile()) {
              viewModel.refreshFiles()
            }
          } catch (e: IOException) {
            e.printStackTrace()
          }
        }
        .setPositiveButton(
          R.string.folder
        ) { _, _ ->
          val name = binding.inputEdittext.text.toString().trim()
          val folder = File(path, name)
          if (folder.exists()) {
            return@setPositiveButton
          }

          if (folder.mkdirs()) {
            viewModel.refreshFiles()
          }
        }
        .create()
    dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    dialog.setOnShowListener {
      binding.inputLayout.setHint(R.string.file_name_hint)
      binding.inputEdittext.requestFocus()
    }
    dialog.show()
  }

  @SuppressLint("RestrictedApi")
  @Suppress("DEPRECATION")
  fun showRenameFileDialog(file: File) {
    val binding = LayoutTextinputBinding.inflate(LayoutInflater.from(context))
    val dialog =
      MaterialAlertDialogBuilder(context)
        .setView(binding.root, dp2px(20f), dp2px(15f), dp2px(20f), dp2px(15f))
        .setTitle(R.string.rename)
        .setNegativeButton(R.string.cancel, null)
        .setPositiveButton(
          R.string.rename
        ) { _, _ ->
          explorerFragment.explorerScope.launchWithProgressDialog(
            configureDialog = { builder ->
              builder.setMessage(R.string.please_wait)
              builder.setCancelable(false)
            },
            action = { _ ->
              val name = binding.inputEdittext.text.toString().trim()
              val newFile = File(file.parentFile, name)
              val renamed = file.renameTo(newFile)

              withContext(Dispatchers.Main) {
                if (!renamed) {
                  return@withContext
                }
                showShortToast(context, context.getString(R.string.renamed_message))
                viewModel.refreshFiles()
              }
              EventBus.getDefault().post(OnRenameFileEvent(file, newFile))
            })
        }
        .create()
    dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    dialog.setOnShowListener {
      binding.inputLayout.setHint(R.string.rename_hint)
      binding.inputEdittext.setText(file.name)
      binding.inputEdittext.requestFocus()
    }
    dialog.show()
  }

  fun showDeleteFileDialog(file: File) {
    MaterialAlertDialogBuilder(context)
      .setTitle(R.string.delete)
      .setMessage(context.getString(R.string.delete_message, file.name))
      .setNegativeButton(R.string.no, null)
      .setPositiveButton(
        R.string.delete
      ) { _, _ ->
        explorerFragment.explorerScope.launchWithProgressDialog(
          configureDialog = { builder ->
            builder.setMessage(R.string.please_wait)
            builder.setCancelable(false)
          },
          action = { _ ->
            val deleted = FileUtils.delete(file)
            withContext(Dispatchers.Main) {
              if (deleted) {
                showShortToast(context, context.getString(R.string.deleted_message))
                EventBus.getDefault().post(OnDeleteFileEvent(file))
              }
              viewModel.refreshFiles()
            }
          })
      }
      .show()
  }
}
