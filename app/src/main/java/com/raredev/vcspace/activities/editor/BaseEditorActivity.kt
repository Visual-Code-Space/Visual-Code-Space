package com.raredev.vcspace.activities.editor

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.core.util.forEach
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ThreadUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.raredev.vcspace.activities.BaseActivity
import com.raredev.vcspace.databinding.ActivityEditorBinding
import com.raredev.vcspace.editor.CodeEditorView
import com.raredev.vcspace.editor.events.OnContentChangeEvent
import com.raredev.vcspace.events.OnDeleteFileEvent
import com.raredev.vcspace.events.OnPreferenceChangeEvent
import com.raredev.vcspace.events.OnRenameFileEvent
import com.raredev.vcspace.extensions.cancelIfActive
import com.raredev.vcspace.res.R
import com.raredev.vcspace.tasks.TaskExecutor.executeAsyncProvideError
import com.raredev.vcspace.utils.PreferencesUtils
import com.raredev.vcspace.utils.UniqueNameBuilder
import com.raredev.vcspace.utils.Utils
import com.raredev.vcspace.utils.showShortToast
import com.raredev.vcspace.viewmodel.EditorViewModel
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

open class BaseEditorActivity :
  BaseActivity(),
  TabLayout.OnTabSelectedListener,
  SharedPreferences.OnSharedPreferenceChangeListener {

  private var _binding: ActivityEditorBinding? = null

  private var optionsMenuInvalidator: Runnable? = null
  private var autoSave: Runnable? = null

  private val mainHandler = ThreadUtils.getMainHandler()
  protected val coroutineScope = CoroutineScope(Dispatchers.Default)
  protected val viewModel by viewModels<EditorViewModel>()
  protected var isDestroying = false

  protected val binding: ActivityEditorBinding
    get() = checkNotNull(_binding) { "Activity has been destroyed" }

  companion object {
    private const val OPTIONS_MENU_INVALIDATION_DELAY = 150L
    private const val AUTO_SAVE_DELAY = 220L
  }

  override fun getLayout(): View {
    _binding = ActivityEditorBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setSupportActionBar(binding.toolbar)

    optionsMenuInvalidator = Runnable { super.invalidateOptionsMenu() }
    autoSave = Runnable { saveAllFilesAsync(false) }

    binding.tabs.addOnTabSelectedListener(this)
    viewModel.observeFiles(this) { files ->
      val isEmpty = files.isEmpty()
      binding.noFiles.isVisible = isEmpty
      binding.tabs.isVisible = !isEmpty
      binding.container.isVisible = !isEmpty
      binding.symbolInput.isVisible = !isEmpty
      binding.bottomDivider.isVisible = !isEmpty

      if (isEmpty) invalidateOptionsMenu()
    }
    viewModel.observeSelectedFile(this) { (index, _) ->
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

    PreferencesUtils.prefs.registerOnSharedPreferenceChangeListener(this)
    ThemeRegistry.getInstance().setTheme(if (Utils.isDarkMode) "darcula" else "quietlight")
    EventBus.getDefault().register(this)
  }

  override fun invalidateOptionsMenu() {
    optionsMenuInvalidator?.also {
      mainHandler.removeCallbacks(it)
      mainHandler.postDelayed(it, OPTIONS_MENU_INVALIDATION_DELAY)
    }
  }

  override fun onSharedPreferenceChanged(prefs: SharedPreferences, prefKey: String?) {
    if (prefKey != null) EventBus.getDefault().post(OnPreferenceChangeEvent(prefKey))
  }

  override fun onDestroy() {
    isDestroying = true
    super.onDestroy()
    PreferencesUtils.prefs.unregisterOnSharedPreferenceChangeListener(this)

    EventBus.getDefault().unregister(this)
    GrammarRegistry.getInstance().dispose()
    closeAll()

    coroutineScope.cancelIfActive("Activity has been destroyed!")

    optionsMenuInvalidator = null
    autoSave = null
    _binding = null
  }

  override fun onTabReselected(tab: TabLayout.Tab) {
    val pm = PopupMenu(this, tab.view)
    pm.menu.add(0, 0, 0, R.string.close)
    pm.menu.add(0, 1, 0, R.string.close_others)
    pm.menu.add(0, 2, 0, R.string.close_all)

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
    viewModel.setSelectedFile(tab.position)
  }

  override fun onTabUnselected(tab: TabLayout.Tab) {}

  fun openFile(file: File) {
    if (!file.isFile || !file.exists()) {
      return
    }
    binding.drawerLayout.closeDrawers()
    val openedFileIndex = findIndexAtFile(file)
    if (openedFileIndex != -1) {
      viewModel.setSelectedFile(openedFileIndex)
      return
    }

    val index = viewModel.fileCount

    val editorView = CodeEditorView(this, file)

    viewModel.addFile(file)
    binding.container.addView(editorView)
    binding.tabs.addTab(binding.tabs.newTab())

    viewModel.setSelectedFile(index)
    updateTabs()
  }

  fun closeFile(index: Int) {
    if (index >= 0 && index < viewModel.fileCount) {
      val editor = getEditorAtIndex(index) ?: return

      val file = editor.file
      if (editor.modified && file != null) {
        notifyUnsavedFile(file) { closeFile(index) }
        return
      }
      editor.release()

      viewModel.removeFile(index)
      binding.apply {
        tabs.removeTabAt(index)
        container.removeViewAt(index)
      }
      updateTabs()
    }
  }

  fun closeOthers() {
    if (getUnsavedFilesCount() > 0) {
      notifyUnsavedFiles(getUnsavedFiles()) { closeOthers() }
      return
    }
    val file = viewModel.selectedFile
    var pos: Int = 0
    while (viewModel.fileCount != 1) {
      val editor = getEditorAtIndex(pos) ?: continue

      if (file != editor.file) {
        closeFile(pos)
      } else {
        pos = 1
      }
    }
    viewModel.setSelectedFile(findIndexAtFile(file))
  }

  fun closeAll() {
    if (getUnsavedFilesCount() > 0 && !isDestroying) {
      notifyUnsavedFiles(getUnsavedFiles()) { closeAll() }
      return
    }
    for (i in 0 until viewModel.fileCount) {
      getEditorAtIndex(i)?.release()
    }

    viewModel.removeAllFiles()
    binding.apply {
      tabs.removeAllTabs()
      tabs.requestLayout()
      container.removeAllViews()
    }
  }

  fun saveAllFilesAsync(notify: Boolean, whenSave: Runnable? = null) {
    coroutineScope.launch {
      for (i in 0 until viewModel.fileCount) {
        saveFile(i, null)
      }

      withContext(Dispatchers.Main) {
        if (notify) showShortToast(this@BaseEditorActivity, getString(R.string.saved_files))
        whenSave?.run()
      }
    }
  }

  fun saveFileAsync(notify: Boolean, index: Int, whenSave: Runnable? = null) {
    coroutineScope.launch {
      saveFile(index) {
        if (notify) showShortToast(this@BaseEditorActivity, getString(R.string.saved))
        whenSave?.run()
      }
    }
  }

  private suspend fun saveFile(index: Int, whenSave: Runnable?) {
    getEditorAtIndex(index)?.saveFile()

    withContext(Dispatchers.Main) {
      val tab = binding.tabs.getTabAt(index) ?: return@withContext
      if (tab.text!!.startsWith("*")) {
        tab.text = tab.text!!.substring(startIndex = 1)
      }
      invalidateOptionsMenu()
      whenSave?.run()
    }
  }

  fun getSelectedEditor(): CodeEditorView? {
    return if (viewModel.selectedFileIndex >= 0) {
      getEditorAtIndex(viewModel.selectedFileIndex)
    } else null
  }

  fun getEditorAtIndex(index: Int): CodeEditorView? {
    return binding.container.getChildAt(index) as? CodeEditorView
  }

  fun findIndexAtFile(file: File?): Int {
    val files = viewModel.openedFiles
    for (i in files.indices) {
      if (files[i] == file) {
        return i
      }
    }
    return -1
  }

  fun getUnsavedFilesCount(): Int {
    var count = 0
    for (i in 0 until viewModel.openedFiles.size) {
      if (getEditorAtIndex(i)?.modified == true) count++
    }
    return count
  }

  fun getUnsavedFiles(): List<File> {
    val unsavedFiles = mutableListOf<File>()
    for (i in 0 until viewModel.fileCount) {
      val editor = getEditorAtIndex(i) ?: continue

      val file = editor.file
      if (file != null && editor.modified) {
        unsavedFiles.add(file)
      }
    }
    return unsavedFiles
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onContentChangeEvent(event: OnContentChangeEvent) {
    invalidateOptionsMenu()
    val index = findIndexAtFile(event.file)
    if (index == -1) {
      return
    }

    if (PreferencesUtils.autoSave) {
      autoSave?.also {
        mainHandler.removeCallbacks(it)
        mainHandler.postDelayed(it, AUTO_SAVE_DELAY)
      }
    }

    val tab = binding.tabs.getTabAt(index)
    if (tab?.text?.startsWith("*") != false) {
      return
    }
    tab.text = "*${tab.text}"
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onFileRenamed(event: OnRenameFileEvent) {
    invalidateOptionsMenu()
    val index = findIndexAtFile(event.oldFile)
    val editor = getEditorAtIndex(index) ?: return
    viewModel.updateFile(index, event.newFile)
    editor.setFile(event.newFile)

    updateTabs()
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onFileDeleted(event: OnDeleteFileEvent) {
    closeFile(findIndexAtFile(event.file))
  }

  /** from AndroidIDE com.itsaky.androidide.activities.editor.EditorHandlerActivity */
  private fun updateTabs() {
    executeAsyncProvideError({
      val files = viewModel.openedFiles
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
      if (result == null || error != null) {
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
      }
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
        for (i in 0 until viewModel.fileCount) {
          getEditorAtIndex(i)?.setModified(false)
        }
        runAfter.run()
      }
    )
  }

  private fun showUnsavedFilesAlert(
    unsavedFileName: String,
    positive: DialogInterface.OnClickListener,
    negative: DialogInterface.OnClickListener
  ) {
    MaterialAlertDialogBuilder(this)
      .setTitle(R.string.unsaved_files_title)
      .setMessage(getString(R.string.unsaved_files_message, unsavedFileName))
      .setPositiveButton(R.string.save_and_close, positive)
      .setNegativeButton(R.string.close, negative)
      .setNeutralButton(R.string.cancel, null)
      .show()
  }
}
