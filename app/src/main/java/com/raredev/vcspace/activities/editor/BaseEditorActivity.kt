package com.raredev.vcspace.activities.editor

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.util.SparseArray
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.core.util.forEach
import com.google.android.material.tabs.TabLayout
import com.raredev.vcspace.R.*
import com.raredev.vcspace.res.R
import com.raredev.vcspace.activities.BaseActivity
import com.raredev.vcspace.databinding.ActivityEditorBinding
import com.raredev.vcspace.events.OnContentChangeEvent
import com.raredev.vcspace.tasks.TaskExecutor.executeAsync
import com.raredev.vcspace.ui.CodeEditorView
import com.raredev.vcspace.tasks.TaskExecutor.executeAsyncProvideError
import com.raredev.vcspace.utils.showSuccessToast
import com.raredev.vcspace.utils.Utils
import com.raredev.vcspace.utils.UniqueNameBuilder
import com.raredev.vcspace.viewmodel.EditorViewModel
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import java.io.File
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

open class BaseEditorActivity: BaseActivity(), TabLayout.OnTabSelectedListener {

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

    viewModel.files.observe(this) { files ->
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
        binding.symbolInput.bindEditor(getEditorAt(position)?.editor)
        binding.container.displayedChild = position
        invalidateOptionsMenu()
      } else {
        binding.symbolInput.bindEditor(null)
      }
    }

    ThemeRegistry.getInstance().setTheme(if (Utils.isDarkMode()) "darcula" else "quietlight")
    EventBus.getDefault().register(this)
  }

  override fun onDestroy() {
    super.onDestroy()
    closeAll()

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

    viewModel.addFile(file)
    binding.container.addView(CodeEditorView(this, file))
    binding.tabs.addTab(binding.tabs.newTab())

    viewModel.setSelectedFile(position)
    updateTabs()
  }

  fun closeFile(position: Int) {
    if (position >= 0 && position < viewModel.getFileCount()) {
      getEditorAt(position)?.release()

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
  }

  fun closeAll() {
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

  fun saveAll(showMsg: Boolean) {
    val selectedEditor = getSelectedEditor()
    selectedEditor?.setLoading(true)

    executeAsync({
      for (i in 0 until viewModel.getFileCount()) {
        getEditorAt(i)?.saveFile()
      }
    }) { _ ->
      selectedEditor?.setLoading(false)
      invalidateOptionsMenu()
      updateTabs()
      if (showMsg)
        showSuccessToast(this, getString(R.string.saved_files))
    }
  }

  fun saveFile(showMsg: Boolean) {
    val editor = getSelectedEditor()
    editor?.setLoading(true)
    executeAsync({ editor?.saveFile() }) { _ ->
      editor?.setLoading(false)
      invalidateOptionsMenu()
      updateTabs()
      if (showMsg)
        showSuccessToast(this, getString(R.string.saved))
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
    for (i in 0 until files.size) {
      if (files[i] == file) {
        return i
      }
    }
    return -1
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

  /**
   * from AndroidIDE
   * com.itsaky.androidide.activities.editor.EditorHandlerActivity
   */
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
        //log.error("Failed to compute names for file tabs", error)
        return@executeAsyncProvideError
      }

      runOnUiThread {
        result.forEach { index, name ->
          binding.tabs.getTabAt(index)?.text = name
        }
      }
    }
  }
}
