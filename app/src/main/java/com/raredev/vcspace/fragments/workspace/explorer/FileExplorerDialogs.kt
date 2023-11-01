package com.raredev.vcspace.fragments.workspace.explorer

import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raredev.vcspace.events.OnRenameFileEvent
import com.raredev.vcspace.progressdialog.ProgressDialog
import com.raredev.vcspace.res.R
import com.raredev.vcspace.res.databinding.LayoutTextinputBinding
import com.raredev.vcspace.tasks.TaskExecutor.executeAsync
import com.raredev.vcspace.utils.showSuccessToast
import com.raredev.vcspace.viewmodel.FileExplorerViewModel
import java.io.File
import java.io.IOException
import org.greenrobot.eventbus.EventBus

class FileExplorerDialogs(
  val context: Context,
  val viewModel: FileExplorerViewModel
) {

  fun showCreateFileDialog(path: String) {
    val binding = LayoutTextinputBinding.inflate(LayoutInflater.from(context))
    val dialog = MaterialAlertDialogBuilder(context)
      .setView(binding.root, dp2px(20f), dp2px(15f), dp2px(20f), dp2px(15f))
      .setTitle(R.string.create)
      .setNeutralButton(R.string.cancel, null)
      .setNegativeButton(R.string.file, { _, _ ->
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
      })
      .setPositiveButton(R.string.folder, { _, _ ->
        val name = binding.inputEdittext.text.toString().trim()
        val folder = File(path, name)
        if (folder.exists()) {
          return@setPositiveButton
        }

        if (folder.mkdirs()) {
          viewModel.refreshFiles()
        }
      }).create()
    dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    dialog.setOnShowListener {
      binding.inputLayout.setHint(R.string.file_name_hint)
      binding.inputEdittext.requestFocus()
    }
    dialog.show()
  }
  
  fun showRenameFileDialog(file: File) {
    val binding = LayoutTextinputBinding.inflate(LayoutInflater.from(context))
    val dialog = MaterialAlertDialogBuilder(context)
      .setView(binding.root, dp2px(20f), dp2px(15f), dp2px(20f), dp2px(15f))
      .setTitle(R.string.rename)
      .setNegativeButton(R.string.cancel, null)
      .setPositiveButton(R.string.rename, { _, _ ->
        val name = binding.inputEdittext.text.toString().trim()

        if (file.name.equals(name)) {
          return@setPositiveButton
        }

        val newFile = File(file.parentFile, name)
        executeAsync({ file.renameTo(newFile) }) {
          val renamed = it ?: false

          if (renamed) {
            showSuccessToast(context, context.getString(R.string.renamed_message))

            EventBus.getDefault().post(OnRenameFileEvent(file, newFile))
            viewModel.refreshFiles()
          }
        }
      }).create()
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
      .setPositiveButton(R.string.delete, { _, _ ->
        val builder = ProgressDialog.create(context)
          .setTitle(R.string.deleting)
          .setLoadingMessage(R.string.deleting_please_wait)

        val dialog = builder.create()
        dialog.setCancelable(false);
        dialog.show();

        executeAsync({ FileUtils.delete(file) }) {
          val deleted = it ?: false

          if (deleted) {
            
            
            
            
            
            showSuccessToast(context, context.getString(R.string.deleted_message))
          }
          viewModel.refreshFiles()
          dialog.cancel()
        }
      }).show()
  }
}
