package com.raredev.vcspace.activities.editor

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.core.util.forEach
import com.blankj.utilcode.util.ThreadUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.raredev.vcspace.activities.BaseActivity
import com.raredev.vcspace.databinding.ActivityEditorBinding
import com.raredev.vcspace.editor.CodeEditorView
import com.raredev.vcspace.events.OnContentChangeEvent
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

open class BaseEditorActivity :
  BaseActivity(),
  TabLayout.OnTabSelectedListener,
  SharedPreferences.OnSharedPreferenceChangeListener {

  private var _binding: ActivityEditorBinding? = null

  private var optionsMenuInvalidator: Runnable? = null
  private var autoSave: Runnable? = null

  private val mainHandler = ThreadUtils.getMainHandler()
  private val backroundCoroutineScope = CoroutineScope(Dispatchers.Default)
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
    autoSave = Runnable { saveFileAsync(false, viewModel.getSelectedFilePos()) }

    binding.tabs.addOnTabSelectedListener(this)
    viewModel.observeFiles(this) { files ->
      val visibility = if (files.isEmpty()) View.GONE else View.VISIBLE

      with(binding) {
        listOf(noFiles, tabs, container, symbolInput, bottomDivider).forEachIndexed { index, it ->
          if (index == 0) it.visibility = if (files.isNotEmpty()) View.GONE else View.VISIBLE
          else it.visibility = visibility
        }

        if (files.isEmpty()) invalidateOptionsMenu()
      }
    }
    viewModel.selectedFilePosition.observe(this) { position ->
      if (position >= 0) {
        val tab = binding.tabs.getTabAt(position)
        if (tab != null && !tab.isSelected) {
          tab.select()
        }
        binding.symbolInput.bindEditor(getEditorAt(position)?.editor)
        binding.container.displayedChild = position
        invalidateOptionsMenu()
      } else {
        binding.symbolInput.bindEditor(null)
      }
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

    backroundCoroutineScope.cancelIfActive("Activity has been destroyed!")

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
    val openedFilePosition = findPositionAtFile(file)
    if (openedFilePosition != -1) {
      viewModel.setSelectedFile(openedFilePosition)
      return
    }

    val position = viewModel.getFileCount()

    val editorView = CodeEditorView(this, file)

    viewModel.addFile(file)
    binding.container.addView(editorView)
    binding.tabs.addTab(binding.tabs.newTab())

    viewModel.setSelectedFile(position)
    updateTabs()
  }

  fun closeFile(position: Int) {
    if (position >= 0 && position < viewModel.getFileCount()) {
      val editor = getEditorAt(position) ?: return

      val file = editor.file
      if (editor.modified && file != null) {
        notifyUnsavedFile(file) { closeFile(position) }
        return
      }
      editor.release()

      viewModel.removeFile(position)
      binding.apply {
        tabs.removeTabAt(position)
        container.removeViewAt(position)
      }
      updateTabs()
    }
  }

  fun closeOthers() {
    val file = viewModel.getSelectedFile()

    if (getUnsavedFilesCount() > 0) {
      notifyUnsavedFiles(getUnsavedFiles()) { closeOthers() }
      return
    }

    var pos: Int = 0
    while (viewModel.getFileCount() != 1) {
      val editor = getEditorAt(pos)
      if (editor != null) {
        if (file != editor.file) {
          closeFile(pos)
        } else {
          pos = 1
        }
      }
    }
    viewModel.setSelectedFile(findPositionAtFile(file))
  }

  fun closeAll() {
    if (getUnsavedFilesCount() > 0 && !isDestroying) {
      notifyUnsavedFiles(getUnsavedFiles()) { closeAll() }
      return
    }
    for (i in 0 until viewModel.getFileCount()) {
      getEditorAt(i)?.release()
    }

    viewModel.removeAllFiles()
    binding.apply {
      tabs.removeAllTabs()
      tabs.requestLayout()
      container.removeAllViews()
    }
  }

  fun saveAllFilesAsync(notify: Boolean, whenSave: Runnable? = null) {
    backroundCoroutineScope.launch {
      for (i in 0 until viewModel.getFileCount()) {
        saveFile(i, null)
      }

      withContext(Dispatchers.Main) {
        if (notify) showShortToast(this@BaseEditorActivity, getString(R.string.saved_files))
        whenSave?.run()
      }
    }
  }

  fun saveFileAsync(notify: Boolean, position: Int, whenSave: Runnable? = null) {
    backroundCoroutineScope.launch {
      saveFile(position) {
        if (notify) showShortToast(this@BaseEditorActivity, getString(R.string.saved))
        whenSave?.run()
      }
    }
  }

  private suspend fun saveFile(position: Int, whenSave: Runnable?) {
    getEditorAt(position)?.saveFile()

    withContext(Dispatchers.Main) {
      val tab = binding.tabs.getTabAt(position) ?: return@withContext
      if (tab.text!!.startsWith("*")) {
        tab.text = tab.text!!.substring(startIndex = 1)
      }
      whenSave?.run()
    }
  }

  fun getSelectedEditor(): CodeEditorView? {
    return if (viewModel.getSelectedFilePos() >= 0) {
      getEditorAt(viewModel.getSelectedFilePos())
    } else null
  }

  fun getEditorAt(position: Int): CodeEditorView? {
    return binding.container.getChildAt(position) as? CodeEditorView
  }

  fun findPositionAtFile(file: File?): Int {
    val files = viewModel.getOpenedFiles()
    for (i in files.indices) {
      if (files[i] == file) {
        return i
      }
    }
    return -1
  }

  fun getUnsavedFilesCount(): Int {
    var count = 0
    for (i in 0 until viewModel.getOpenedFiles().size) {
      if (getEditorAt(i)?.modified == true) count++
    }
    return count
  }

  fun getUnsavedFiles(): List<File> {
    val unsavedFiles = mutableListOf<File>()
    for (i in 0 until viewModel.getFileCount()) {
      val editor = getEditorAt(i) ?: continue

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
    val position = findPositionAtFile(event.file)
    if (position == -1) {
      return
    }

    if (PreferencesUtils.autoSave) {
      autoSave?.also {
        mainHandler.removeCallbacks(it)
        mainHandler.postDelayed(it, AUTO_SAVE_DELAY)
      }
    }

    val tab = binding.tabs.getTabAt(position)
    if (tab?.text?.startsWith("*") != false) {
      return
    }
    tab.text = "*${tab.text}"
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onFileRenamed(event: OnRenameFileEvent) {
    invalidateOptionsMenu()
    val position = findPositionAtFile(event.oldFile)
    // If the position is -1 it will not find any editor and will return.
    val editor = getEditorAt(position) ?: return
    viewModel.updateFile(position, event.newFile)
    editor.setFile(event.newFile)

    updateTabs()
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onFileDeleted(event: OnDeleteFileEvent) {
    invalidateOptionsMenu()
    val position = findPositionAtFile(event.file)
    if (position == -1) return
    closeFile(position)
  }

  /** from AndroidIDE com.itsaky.androidide.activities.editor.EditorHandlerActivity */
  private fun updateTabs() {
    executeAsyncProvideError({
      val files = viewModel.getOpenedFiles()
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
        val isModified = getEditorAt(i)?.modified ?: false
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
      { _, _ -> saveFileAsync(true, findPositionAtFile(unsavedFile)) { runAfter.run() } },
      { _, _ ->
        getEditorAt(findPositionAtFile(unsavedFile))?.setModified(false)
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
        for (i in 0 until viewModel.getFileCount()) {
          getEditorAt(i)?.setModified(false)
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
