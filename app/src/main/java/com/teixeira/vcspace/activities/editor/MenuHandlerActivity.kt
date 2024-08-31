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

package com.teixeira.vcspace.activities.editor

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Process
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.view.menu.MenuBuilder
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.UriUtils
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.google.android.material.snackbar.Snackbar
import com.hzy.libp7zip.P7ZipApi
import com.teixeira.vcspace.PYTHON_PACKAGE_URL_32_BIT
import com.teixeira.vcspace.PYTHON_PACKAGE_URL_64_BIT
import com.teixeira.vcspace.R
import com.teixeira.vcspace.activities.TerminalActivity
import com.teixeira.vcspace.preferences.pythonDownloaded
import com.teixeira.vcspace.preferences.pythonExtracted
import com.teixeira.vcspace.resources.R.string
import com.teixeira.vcspace.utils.launchWithProgressDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Base class for EditorActivity. Handles the menu options logic.
 *
 * @author Felipe Teixeira, Vivek
 */
abstract class MenuHandlerActivity : EditorHandlerActivity() {

  private val createFile =
    registerForActivityResult(ActivityResultContracts.CreateDocument("text/*")) {
      if (it != null) openFile(UriUtils.uri2File(it))
    }
  private val openFile =
    registerForActivityResult(ActivityResultContracts.OpenDocument()) {
      if (it != null) openFile(UriUtils.uri2File(it))
    }

  @SuppressLint("RestrictedApi")
  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_editor_activity, menu)
    if (menu is MenuBuilder) {
      menu.setOptionalIconsVisible(true)
    }
    return super.onCreateOptionsMenu(menu)
  }

  override fun onPrepareOptionsMenu(menu: Menu): Boolean {
    val editor = getSelectedEditor()
    if (editor != null) {
      menu.findItem(R.id.menu_execute).isVisible = true
      menu.findItem(R.id.menu_undo).isVisible = KeyboardUtils.isSoftInputVisible(this)
      menu.findItem(R.id.menu_redo).isVisible = KeyboardUtils.isSoftInputVisible(this)
      menu.findItem(R.id.menu_undo).isEnabled = editor.canUndo()
      menu.findItem(R.id.menu_redo).isEnabled = editor.canRedo()
      menu.findItem(R.id.menu_search).isVisible = true
      menu.findItem(R.id.menu_save).isEnabled = editor.modified
      menu.findItem(R.id.menu_save_as).isEnabled = true
      menu.findItem(R.id.menu_save_all).isEnabled = areModifiedFiles()
      menu.findItem(R.id.menu_reload).isEnabled = true
    }
    return super.onPrepareOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val editor = getSelectedEditor()
    when (item.itemId) {
      R.id.menu_execute -> {
        saveAllFilesAsync(false) {
          if (editor?.file?.name?.endsWith(".py") == true) {
            downloadPythonPackage {
              startActivity(
                Intent(this, TerminalActivity::class.java)
                  .putExtra(TerminalActivity.KEY_PYTHON_FILE_PATH, editor.file?.absolutePath)
              )
            }
          }
        }
      }

      R.id.menu_search -> editor?.beginSearchMode()
      R.id.menu_undo -> editor?.undo()
      R.id.menu_redo -> editor?.redo()
      R.id.menu_new_file -> createFile.launch("filename.txt")
      R.id.menu_open_file -> openFile.launch(arrayOf("text/*"))
      R.id.menu_save -> saveFileAsync(true, editorViewModel.selectedFileIndex)
      R.id.menu_save_all -> saveAllFilesAsync(true)
      R.id.menu_reload -> editor?.confirmReload()
    }
    return true
  }

  private fun extractPythonFile(filePath: String, onDone: Runnable) {
    if (pythonDownloaded) {
      onDone.run()
    } else {
      coroutineScope.launchWithProgressDialog(
        uiContext = this,
        context = Dispatchers.IO,
        configureBuilder = {
          it.setMessage(string.python_extracting_python_compiler)
            .setCancelable(false)
        },
        invokeOnCompletion = { throwable ->
          if (throwable == null) {
            pythonDownloaded = true
            onDone.run()
          }
        }
      ) { _, _ ->
        File(filePath).inputStream().use { temp7zStream ->
          val file = File("${filesDir.absolutePath}/python.7z").apply { createNewFile() }
          Files.copy(temp7zStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
          val exitCode =
            P7ZipApi.executeCommand("7z x ${file.absolutePath} -o${filesDir.absolutePath}")
          Log.d("EditorActivity", "extractFiles: $exitCode")
          file.delete()
        }
      }
    }
  }

  private fun downloadPythonPackage(onDownloaded: () -> Unit) {
    if (pythonDownloaded) {
      onDownloaded()
      return
    }

    if (pythonExtracted) {
      FileUtils.deleteAllInDir(filesDir)
    }

    val url = if (Process.is64Bit()) PYTHON_PACKAGE_URL_64_BIT else PYTHON_PACKAGE_URL_32_BIT
    val outputFile = File(filesDir, "python.7z")

    CoroutineScope(Dispatchers.IO).launchWithProgressDialog(
      uiContext = this,
      context = Dispatchers.IO,
      configureBuilder = {
        it.setTitle("Downloading Python")
          .setMessage(string.python_downloading_python_compiler)
          .setCancelable(false)
          .setIndeterminate(false)
          .setMax(100)
      }
    ) { builder, dialog ->
      PRDownloader.download(url, outputFile.parent, outputFile.name)
        .build()
        .setOnProgressListener {
          val progress = (it.currentBytes * 100 / it.totalBytes).toInt()
          builder.setProgress(progress).setMessage("Downloading... $progress%")
        }
        .start(object : OnDownloadListener {
          override fun onDownloadComplete() {
            dialog.dismiss()
            extractPythonFile(outputFile.absolutePath) {
              outputFile.delete()
              pythonDownloaded = true
              onDownloaded()
            }
          }

          override fun onError(error: Error) {
            dialog.dismiss()

            if (error.isConnectionError) {
              Snackbar.make(window.decorView, "Connection failed!", Snackbar.LENGTH_SHORT)
                .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                .show()
            } else if (error.isServerError) {
              Snackbar.make(window.decorView, "Server error!", Snackbar.LENGTH_SHORT)
                .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                .show()
            } else {
              Snackbar.make(
                window.decorView,
                "Download failed! Something went wrong.",
                Snackbar.LENGTH_SHORT
              ).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show()
            }
          }
        })
    }
  }
}
