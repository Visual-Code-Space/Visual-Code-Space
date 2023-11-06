package com.raredev.vcspace.activities.editor

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.core.util.forEach
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.raredev.vcspace.R.*
import com.raredev.vcspace.activities.BaseActivity
import com.raredev.vcspace.databinding.ActivityEditorBinding
import com.raredev.vcspace.editor.AceEditorPanel
import com.raredev.vcspace.editor.SoraEditorPanel
import com.raredev.vcspace.events.OnContentChangeEvent
import com.raredev.vcspace.events.OnPreferenceChangeEvent
import com.raredev.vcspace.interfaces.IEditorPanel
import com.raredev.vcspace.res.R
import com.raredev.vcspace.tasks.TaskExecutor.executeAsync
import com.raredev.vcspace.tasks.TaskExecutor.executeAsyncProvideError
import com.raredev.vcspace.utils.PreferencesUtils
import com.raredev.vcspace.utils.UniqueNameBuilder
import com.raredev.vcspace.utils.Utils
import com.raredev.vcspace.utils.showSuccessToast
import com.raredev.vcspace.viewmodel.EditorViewModel
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import java.io.File
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

open class BaseEditorActivity :
    BaseActivity(),
    TabLayout.OnTabSelectedListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

  private var _binding: ActivityEditorBinding? = null

  protected val viewModel by viewModels<EditorViewModel>()

  protected val binding: ActivityEditorBinding
    get() = checkNotNull(_binding)

  override fun getLayout(): View {
    _binding = ActivityEditorBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setSupportActionBar(binding.toolbar)
    binding.tabs.addOnTabSelectedListener(this)

    viewModel.observeFiles(this) { files ->
      if (files.isEmpty()) {
        binding.noFiles.visibility = View.VISIBLE
        binding.tabs.visibility = View.GONE
        binding.container.visibility = View.GONE
        binding.symbolInput.visibility = View.GONE
        binding.bottomDivider.visibility = View.GONE
        invalidateOptionsMenu()
      } else {
        binding.noFiles.visibility = View.GONE
        binding.tabs.visibility = View.VISIBLE
        binding.container.visibility = View.VISIBLE
        binding.symbolInput.visibility = View.VISIBLE
        binding.bottomDivider.visibility = View.VISIBLE
      }
    }
    viewModel.selectedFilePosition.observe(this) { position ->
      if (position >= 0) {
        val tab = binding.tabs.getTabAt(position)
        if (tab != null && !tab.isSelected) {
          tab.select()
        }
        binding.symbolInput.bindEditor(getEditorPanelAt(position))
        binding.container.displayedChild = position
        invalidateOptionsMenu()
      } else {
        binding.symbolInput.bindEditor(null)
      }
    }

    PreferencesUtils.prefs.registerOnSharedPreferenceChangeListener(this)
    ThemeRegistry.getInstance().setTheme(if (Utils.isDarkMode()) "darcula" else "quietlight")
    EventBus.getDefault().register(this)
  }

  override fun onSharedPreferenceChanged(prefs: SharedPreferences, prefKey: String?) {
    if (prefKey != null) EventBus.getDefault().post(OnPreferenceChangeEvent(prefKey))
  }

  override fun onDestroy() {
    super.onDestroy()
    closeAll()

    PreferencesUtils.prefs.unregisterOnSharedPreferenceChangeListener(this)

    EventBus.getDefault().unregister(this)
    GrammarRegistry.getInstance().dispose()

    _binding = null
  }

  override fun onTabReselected(tab: TabLayout.Tab) {
    val pm = PopupMenu(this, tab.view)
    menuInflater.inflate(menu.menu_file_tab, pm.menu)

    pm.setOnMenuItemClickListener { item ->
      when (item.itemId) {
        id.menu_close -> closeFile(tab.position)
        id.menu_close_others -> closeOthers()
        id.menu_close_all -> closeAll()
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

    val editorView =
        if (PreferencesUtils.useAceEditor) {
          AceEditorPanel(this, file)
        } else {
          SoraEditorPanel(this, file)
        }

    viewModel.addFile(file)
    binding.container.addView(editorView)
    binding.tabs.addTab(binding.tabs.newTab())

    viewModel.setSelectedFile(position)
    updateTabs()
  }

  fun closeFile(position: Int) {
    if (position >= 0 && position < viewModel.getFileCount()) {
      val editor = getEditorPanelAt(position)
      if (editor == null) return

      val file = editor.getFile()
      if (editor.isModified() && file != null) {
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
    if (file == null) {
      return
    }

    val unsavedFilesCount = getUnsavedFilesCount()
    if (unsavedFilesCount > 0) {
      notifyUnsavedFiles(getUnsavedFiles()) { closeOthers() }
      return
    }

    var pos: Int = 0
    while (viewModel.getFileCount() != 1) {
      val editor = getEditorPanelAt(pos)
      if (editor != null) {
        if (file != editor.getFile()) {
          closeFile(pos)
        } else {
          pos = 1
        }
      }
    }
    viewModel.setSelectedFile(0)
  }

  fun closeAll() {
    val unsavedFilesCount = getUnsavedFilesCount()
    if (unsavedFilesCount > 0) {
      notifyUnsavedFiles(getUnsavedFiles()) { closeAll() }
      return
    }
    for (i in 0 until viewModel.getFileCount()) {
      getEditorPanelAt(i)?.release()
    }

    viewModel.removeAllFiles()
    binding.apply {
      tabs.removeAllTabs()
      tabs.requestLayout()
      container.removeAllViews()
    }
  }

  fun saveAll(showMsg: Boolean, post: Runnable? = null) {
    val selectedEditor = getSelectedEditorPanel()
    selectedEditor?.setLoading(true)

    executeAsync({
      for (i in 0 until viewModel.getFileCount()) {
        getEditorPanelAt(i)?.saveFile()
      }
    }) { _ ->
      selectedEditor?.setLoading(false)
      invalidateOptionsMenu()
      updateTabs()
      if (showMsg) showSuccessToast(this, getString(R.string.saved_files))

      post?.run()
    }
  }

  fun saveFile(showMsg: Boolean, post: Runnable? = null) {
    val editor = getSelectedEditorPanel()
    editor?.setLoading(true)
    executeAsync({ editor?.saveFile() }) { _ ->
      editor?.setLoading(false)
      invalidateOptionsMenu()
      updateTabs()
      if (showMsg) showSuccessToast(this, getString(R.string.saved))

      post?.run()
    }
  }

  fun getSelectedEditorPanel(): IEditorPanel? {
    return if (viewModel.getSelectedFilePos() >= 0) {
      getEditorPanelAt(viewModel.getSelectedFilePos())
    } else null
  }

  fun getEditorPanelAt(position: Int): IEditorPanel? {
    return binding.container.getChildAt(position) as? IEditorPanel
  }

  fun findPositionAtFile(file: File?): Int {
    val files = viewModel.getOpenedFiles()
    for (i in 0 until files.size) {
      if (files[i] == file) {
        return i
      }
    }
    return -1
  }

  fun getUnsavedFilesCount(): Int {
    var count = 0
    for (i in 0 until viewModel.getOpenedFiles().size) {
      if (getEditorPanelAt(i)?.isModified() ?: false) count++
    }
    return count
  }

  fun getUnsavedFiles(): List<File> {
    val unsavedFiles = mutableListOf<File>()
    for (i in 0 until viewModel.getFileCount()) {
      val editor = getEditorPanelAt(i)
      if (editor == null) continue

      val file = editor.getFile()
      if (file != null && editor.isModified()) {
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

    val tab = binding.tabs.getTabAt(position)
    if (tab?.text?.startsWith("*") ?: true) {
      return
    }
    tab?.text = "*${tab?.text}"
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
        val isModified = getEditorPanelAt(i)?.isModified() ?: false
        val name = if (count > 1) nameBuilder.getShortPath(file) else file.name
        names[i] = if (isModified) "*$name" else name
      }
      names
    }) { result, error ->
      if (result == null || error != null) {
        return@executeAsyncProvideError
      }

      runOnUiThread { result.forEach { index, name -> binding.tabs.getTabAt(index)?.text = name } }
    }
  }

  private fun notifyUnsavedFile(unsavedFile: File, callback: Runnable) {
    showUnsavedFilesAlert(
        unsavedFile.name,
        { _, _ ->
          val position = findPositionAtFile(unsavedFile)
          if (position != -1) {
            getEditorPanelAt(position)?.saveFile()
          }
          callback.run()
        },
        { _, _ ->
          val position = findPositionAtFile(unsavedFile)
          if (position != -1) {
            getEditorPanelAt(position)?.setModified(false)
          }
          callback.run()
        })
  }

  private fun notifyUnsavedFiles(unsavedFiles: List<File>, callback: Runnable) {
    val sb = StringBuilder()
    for (file in unsavedFiles) {
      sb.append(" " + file.name)
    }

    showUnsavedFilesAlert(
        sb.toString(),
        { _, _ -> saveAll(true) { callback.run() } },
        { _, _ ->
          for (i in 0 until viewModel.getFileCount()) {
            getEditorPanelAt(i)?.setModified(false)
          }
          callback.run()
        })
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
