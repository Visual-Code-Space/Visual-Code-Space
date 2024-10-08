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

import android.content.DialogInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.core.util.forEach
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.UriUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.teixeira.vcspace.editor.CodeEditorView
import com.teixeira.vcspace.editor.events.OnContentChangeEvent
import com.teixeira.vcspace.events.OnDeleteFileEvent
import com.teixeira.vcspace.events.OnRenameFileEvent
import com.teixeira.vcspace.extensions.toFile
import com.teixeira.vcspace.models.FileHistory
import com.teixeira.vcspace.preferences.editorTabsAutosave
import com.teixeira.vcspace.preferences.pluginsPath
import com.teixeira.vcspace.preferences.rememberLastOpenedFile
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.tasks.TaskExecutor.executeAsyncProvideError
import com.teixeira.vcspace.utils.UniqueNameBuilder
import com.teixeira.vcspace.utils.showShortToast
import com.teixeira.vcspace.viewmodel.EditorViewModel
import com.teixeira.vcspace.viewmodel.EditorViewModel.EditorAction
import com.vcspace.plugins.Manifest
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lsp.client.languageserver.wrapper.EventHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

/**
 * Base class for EditorActivity. Handles logic for working with file editors.
 *
 * @author Akash Yadav, Felipe Teixeira, Vivek
 */
abstract class EditorHandlerActivity : BaseEditorActivity(), TabLayout.OnTabSelectedListener {

  protected val editorViewModel by viewModels<EditorViewModel>()
  private var fileSaver: Runnable? = null

  private val openedFiles = mutableListOf<String>()

  companion object {
    const val EXTRA_KEY_PLUGIN_MANIFEST = "plugin_manifest"

    val LAST_OPENED_FILES_JSON_PATH =
      "${PathUtils.getExternalAppFilesPath()}/settings/lastOpenedFile.json"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    fileSaver = Runnable { saveAllFilesAsync(false) }

    binding.tabs.addOnTabSelectedListener(this)
    observeEditorViewModel()

    if (rememberLastOpenedFile) openLastFiles()

    val fileUri: Uri? = intent.data
    if (fileUri != null) openFile(UriUtils.uri2File(fileUri))

    // Open plugin files if opened from PluginsActivity
    run {
      @Suppress("DEPRECATION")
      val manifest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        intent.getSerializableExtra(EXTRA_KEY_PLUGIN_MANIFEST, Manifest::class.java)
      } else intent.getSerializableExtra(EXTRA_KEY_PLUGIN_MANIFEST) as? Manifest

      if (manifest != null) {
        val pluginPath = "${pluginsPath}/${manifest.packageName}"
        openFile(
          "$pluginPath/manifest.json".toFile(),
          "$pluginPath/${manifest.scripts.first().name}".toFile(),
        )
      }
    }
  }

  override fun preDestroy() {
    super.preDestroy()
    closeAll()
  }

  override fun postDestroy() {
    super.postDestroy()

    fileSaver?.also { ThreadUtils.getMainHandler().removeCallbacks(it) }
    fileSaver = null
  }

  override fun onTabReselected(tab: TabLayout.Tab) {
    val pm = PopupMenu(this, tab.view)
    pm.menu.add(0, 0, 0, R.string.editor_close)
    pm.menu.add(0, 1, 0, R.string.editor_close_others)
    pm.menu.add(0, 2, 0, R.string.editor_close_all)

    pm.setOnMenuItemClickListener { item ->
      when (item.itemId) {
        0 -> closeFile(tab.position)
        1 -> closeOthers()
        2 -> closeAll()
      }
      true
    }
    pm.show()
  }

  override fun onTabSelected(tab: TabLayout.Tab) {
    editorViewModel.setSelectedFile(tab.position)
  }

  override fun onTabUnselected(tab: TabLayout.Tab) {}

  private fun observeEditorViewModel() {
    editorViewModel.observeFiles(this) { files ->
      val isEmpty = files.isEmpty()
      binding.noFiles.isVisible = isEmpty
      binding.tabs.isVisible = !isEmpty
      binding.container.isVisible = !isEmpty
      binding.symbolInput.isVisible = !isEmpty
      binding.bottomDivider.isVisible = !isEmpty

      if (isEmpty) invalidateOptionsMenu()
    }
    editorViewModel.observeAction(this) { action ->
      when (action) {
        is EditorAction.OpenFileAction -> openFile(action.file)
        is EditorAction.CloseFileAction -> closeFile(action.fileIndex)
        is EditorAction.CloseOthersAction -> closeOthers()
        is EditorAction.CloseAllAction -> closeAll()
      }
    }
    editorViewModel.observeSelectedFile(this) { (index, _) ->
      binding.apply {
        val tab = tabs.getTabAt(index)
        if (tab != null && !tab.isSelected) {
          tab.select()
        }
        container.displayedChild = index
        symbolInput.bindEditor(getEditorAtIndex(index)?.editor)
      }
      invalidateOptionsMenu()
    }
  }

  fun openFile(vararg files: File) {
    files.forEach { file ->
      if (!file.isFile || !file.exists() || isDestroying) {
        return
      }
      closeWorkspaceDrawer()
      val openedFileIndex = findIndexAtFile(file)
      if (openedFileIndex != -1) {
        editorViewModel.setSelectedFile(openedFileIndex)
        return
      }

      val index = editorViewModel.fileCount

      val editorView = CodeEditorView(this, file)

      if (file.extension == "kt") {
        lifecycleScope.launch {
          editorView.editor.connectToKotlinLsp(
            lifecycleScope,
            EmptyLanguage(),
            object : EventHandler.EventListener {}
          )
        }
      }

      editorViewModel.addFile(file)
      binding.container.addView(editorView)
      binding.tabs.addTab(binding.tabs.newTab())

      editorViewModel.setSelectedFile(index)
      openedFiles.add(file.absolutePath)
      updateTabs()
    }
  }

  fun closeFile(index: Int) {
    if (index >= 0 && index < editorViewModel.fileCount) {
      val editor = getEditorAtIndex(index) ?: return

      val file = editor.file
      if (editor.modified && file != null) {
        notifyUnsavedFile(file) { closeFile(index) }
        return
      }
      editor.release()

      editorViewModel.removeFile(index)
      binding.apply {
        tabs.removeTabAt(index)
        container.removeViewAt(index)
      }
      openedFiles.remove(file?.absolutePath)
      updateTabs()
    }
  }

  fun closeOthers() {
    if (editorViewModel.selectedFileIndex >= 0) {
      if (areModifiedFiles()) {
        notifyUnsavedFiles(getUnsavedFiles()) { closeOthers() }
        return
      }
      val file = editorViewModel.selectedFile
      var pos: Int = 0
      while (editorViewModel.fileCount > 1) {
        val editor = getEditorAtIndex(pos) ?: continue

        if (file != editor.file) {
          closeFile(pos)
        } else {
          pos = 1
        }
      }
      editorViewModel.setSelectedFile(findIndexAtFile(file))
    }
  }

  fun closeAll() {
    if (areModifiedFiles() && !isDestroying) {
      notifyUnsavedFiles(getUnsavedFiles()) { closeAll() }
      return
    }
    for (i in 0 until editorViewModel.fileCount) {
      getEditorAtIndex(i)?.release()
    }

    editorViewModel.removeAllFiles()
    binding.apply {
      tabs.removeAllTabs()
      tabs.requestLayout()
      container.removeAllViews()
    }
    openedFiles.clear()
  }

  fun saveAllFilesAsync(notify: Boolean, whenSave: Runnable? = null) {
    coroutineScope.launch {
      for (i in 0 until editorViewModel.fileCount) {
        saveFile(i, null)
      }

      withContext(Dispatchers.Main) {
        if (notify) showShortToast(this@EditorHandlerActivity, getString(R.string.file_saved_all))
        whenSave?.run()
      }
    }
  }

  fun saveFileAsync(notify: Boolean, index: Int, whenSave: Runnable? = null) {
    coroutineScope.launch {
      saveFile(index) {
        if (notify) showShortToast(this@EditorHandlerActivity, getString(R.string.file_saved))
        whenSave?.run()
      }
    }
  }

  private suspend fun saveFile(index: Int, whenSave: Runnable?) {
    getEditorAtIndex(index)?.saveFile()

    withContext(Dispatchers.Main) {
      binding.tabs.getTabAt(index)?.markUnmodified()
      invalidateOptionsMenu()
      whenSave?.run()
    }
  }

  fun areModifiedFiles(): Boolean {
    for (i in 0 until editorViewModel.fileCount) {
      if (getEditorAtIndex(i)?.modified == true) {
        return true
      }
    }
    return false
  }

  fun getUnsavedFiles(): List<File> {
    val unsavedFiles = mutableListOf<File>()
    for (i in 0 until editorViewModel.fileCount) {
      val editor = getEditorAtIndex(i) ?: continue

      val file = editor.file
      if (file != null && editor.modified) {
        unsavedFiles.add(file)
      }
    }
    return unsavedFiles
  }

  fun getSelectedEditor(): CodeEditorView? {
    return if (editorViewModel.selectedFileIndex >= 0) {
      getEditorAtIndex(editorViewModel.selectedFileIndex)
    } else null
  }

  fun getEditorAtIndex(index: Int): CodeEditorView? {
    return binding.container.getChildAt(index) as? CodeEditorView
  }

  fun findIndexAtFile(file: File?): Int {
    val files = editorViewModel.openedFiles
    for (i in files.indices) {
      if (files[i] == file) {
        return i
      }
    }
    return -1
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onContentChangeEvent(event: OnContentChangeEvent) {
    invalidateOptionsMenu()
    val index = findIndexAtFile(event.file)
    if (index == -1) {
      return
    }

    if (editorTabsAutosave) {
      fileSaver?.also {
        ThreadUtils.getMainHandler().removeCallbacks(it)
        ThreadUtils.getMainHandler().postDelayed(it, 100L)
      }
    }

    val tab = binding.tabs.getTabAt(index) ?: return
    val modified = getEditorAtIndex(index)!!.modified

    if (modified) {
      tab.markModified()
    } else tab.markUnmodified()
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onFileRenamed(event: OnRenameFileEvent) {
    invalidateOptionsMenu()
    val index = findIndexAtFile(event.oldFile)
    val editor = getEditorAtIndex(index) ?: return
    editorViewModel.updateFile(index, event.newFile)
    editor.updateFile(event.newFile, false)
    updateTabs()
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onFileDeleted(event: OnDeleteFileEvent) {
    closeFile(findIndexAtFile(event.file))
  }

  /** from AndroidIDE com.itsaky.androidide.activities.editor.EditorHandlerActivity */
  private fun updateTabs() {
    executeAsyncProvideError({
      val files = editorViewModel.openedFiles
      val dupliCount = mutableMapOf<String, Int>()
      val names = SparseArray<String>()
      val nameBuilder = UniqueNameBuilder<File>("", File.separator)

      files.forEach {
        dupliCount[it.name] = (dupliCount[it.name] ?: 0) + 1
        nameBuilder.addPath(it, it.path)
      }

      for (i in 0 until binding.tabs.tabCount) {
        val file = files[i]
        val count = dupliCount[file.name] ?: 0
        val isModified = getEditorAtIndex(i)?.modified ?: false
        val name = if (count > 1) nameBuilder.getShortPath(file) else file.name
        names[i] = if (isModified) "*$name" else name
      }
      names
    }) { result, error ->
      if (result == null || error != null || isDestroying) {
        return@executeAsyncProvideError
      }

      ThreadUtils.runOnUiThread {
        result.forEach { index, name -> binding.tabs.getTabAt(index)?.text = name }
      }
    }
  }

  private fun notifyUnsavedFile(unsavedFile: File, runAfter: Runnable) {
    showUnsavedFilesAlert(
      unsavedFile.name,
      { _, _ -> saveFileAsync(true, findIndexAtFile(unsavedFile)) { runAfter.run() } },
      { _, _ ->
        getEditorAtIndex(findIndexAtFile(unsavedFile))?.setModified(false)
        runAfter.run()
      },
    )
  }

  private fun notifyUnsavedFiles(unsavedFiles: List<File>, runAfter: Runnable) {
    val sb = StringBuilder()
    for (file in unsavedFiles) {
      sb.append(" " + file.name)
    }

    showUnsavedFilesAlert(
      sb.toString(),
      { _, _ -> saveAllFilesAsync(true) { runAfter.run() } },
      { _, _ ->
        for (i in 0 until editorViewModel.fileCount) {
          getEditorAtIndex(i)?.setModified(false)
        }
        runAfter.run()
      },
    )
  }

  private fun showUnsavedFilesAlert(
    unsavedFileName: String,
    positive: DialogInterface.OnClickListener,
    negative: DialogInterface.OnClickListener,
  ) {
    MaterialAlertDialogBuilder(this)
      .setTitle(R.string.editor_unsaved_files)
      .setMessage(getString(R.string.editor_unsaved_files_message, unsavedFileName))
      .setPositiveButton(R.string.editor_save_and_close, positive)
      .setNegativeButton(R.string.close, negative)
      .setNeutralButton(R.string.no, null)
      .show()
  }

  private fun TabLayout.Tab.markModified() {
    if (!text!!.startsWith("*")) {
      text = "*$text"
    }
  }

  private fun TabLayout.Tab.markUnmodified() {
    if (text!!.startsWith("*")) {
      text = text!!.substring(startIndex = 1)
    }
  }

  private fun openLastFiles() {
    File(LAST_OPENED_FILES_JSON_PATH).apply {
      if (!exists()) return@apply

      val fileHistory = Gson().fromJson(readText(), FileHistory::class.java)
      fileHistory.lastOpenedFilesPath.forEach { openFile(it.toFile()) }
    }
  }

  private fun rememberLastFiles() {
    val lastOpenedFiles = Gson().toJson(FileHistory(openedFiles))
    File(LAST_OPENED_FILES_JSON_PATH).apply {
      FileUtils.createOrExistsFile(this)
      writeText(lastOpenedFiles)
    }
  }

  override fun onPause() {
    rememberLastFiles()
    super.onPause()
  }

  override fun onDestroy() {
    rememberLastFiles()
    super.onDestroy()
  }
}
