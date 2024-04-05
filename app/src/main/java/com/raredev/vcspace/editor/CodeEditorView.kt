package com.raredev.vcspace.editor

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.blankj.utilcode.util.FileIOUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raredev.vcspace.databinding.LayoutCodeEditorBinding
import com.raredev.vcspace.editor.langs.VCSpaceTMLanguage
import com.raredev.vcspace.events.OnPreferenceChangeEvent
import com.raredev.vcspace.extensions.cancelIfActive
import com.raredev.vcspace.providers.GrammarProvider
import com.raredev.vcspace.res.R
import com.raredev.vcspace.utils.PreferencesUtils
import com.raredev.vcspace.utils.SharedPreferencesKeys
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.text.LineSeparator
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@SuppressLint("ViewConstructor")
class CodeEditorView(context: Context, file: File) : LinearLayout(context) {

  private val binding = LayoutCodeEditorBinding.inflate(LayoutInflater.from(context))

  private val editorScope = CoroutineScope(Dispatchers.Default)

  val editor: VCSpaceEditor
    get() = binding.editor

  val modified: Boolean
    get() = editor.modified

  val file: File?
    get() = editor.file

  init {
    EventBus.getDefault().register(this)
    binding.editor.apply {
      colorScheme = createColorScheme()
      lineSeparator = LineSeparator.LF
      this.file = file
    }
    binding.searcher.bindSearcher(editor.searcher)
    addView(binding.root, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    configureEditor()
    readFile(file)
  }

  private fun readFile(file: File) {
    setLoading(true)
    editorScope.launch {
      val content = FileIOUtils.readFile2String(file)
      val language = createLanguage()

      withContext(Dispatchers.Main) {
        binding.editor.setText(content, null)
        postRead(language)
      }
    }
  }

  private fun postRead(language: Language) {
    setLoading(false)

    editor.setEditorLanguage(language)
  }

  fun confirmReload() {
    if (modified) {
      MaterialAlertDialogBuilder(context)
        .setTitle(R.string.menu_reload)
        .setMessage(R.string.discard_changes)
        .setPositiveButton(R.string.yes) { _, _ -> readFile(file!!) }
        .setNegativeButton(R.string.no, null)
        .show()
    } else readFile(file!!)
  }

  fun undo() = editor.undo()

  fun redo() = editor.redo()

  fun canUndo() = editor.canUndo()

  fun canRedo() = editor.canRedo()

  fun setModified(modified: Boolean) {
    editor.modified = modified
  }

  fun setFile(file: File) {
    editor.file = file
  }

  fun release() {
    EventBus.getDefault().unregister(this)
    editorScope.cancelIfActive("Editor has been released")
    editor.release()
  }

  suspend fun saveFile(): Boolean {
    return if (modified && FileIOUtils.writeFileFromString(file, editor.text.toString())) {
      setModified(false)
      true
    } else false
  }

  fun beginSearchMode() {
    binding.searcher.beginSearchMode()
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onSharedPreferenceChanged(event: OnPreferenceChangeEvent) {
    when (event.prefKey) {
      SharedPreferencesKeys.KEY_EDITOR_TEXT_SIZE -> updateTextSize()
      SharedPreferencesKeys.KEY_EDITOR_TAB_SIZE -> updateTABSize()
      SharedPreferencesKeys.KEY_STICKY_SCROLL -> updateStickyScroll()
      SharedPreferencesKeys.KEY_FONT_LIGATURES -> updateFontLigatures()
      SharedPreferencesKeys.KEY_WORDWRAP -> updateWordWrap()
      SharedPreferencesKeys.KEY_DELETE_EMPTY_LINE_FAST -> updateDeleteEmptyLineFast()
      SharedPreferencesKeys.KEY_EDITOR_FONT -> updateEditorFont()
      SharedPreferencesKeys.KEY_LINE_NUMBERS -> updateLineNumbers()
      SharedPreferencesKeys.KEY_DELETE_TABS -> updateDeleteTabs()
    }
  }

  private fun configureEditor() {
    updateEditorFont()
    updateTextSize()
    updateTABSize()
    updateStickyScroll()
    updateFontLigatures()
    updateWordWrap()
    updateLineNumbers()
    updateDeleteEmptyLineFast()
    updateDeleteTabs()
  }

  private fun updateTextSize() {
    editor.setTextSize(PreferencesUtils.textSize.toFloat())
  }

  private fun updateTABSize() {
    editor.tabWidth = PreferencesUtils.tabSize
  }

  private fun updateEditorFont() {
    val font = PreferencesUtils.selectedFont
    editor.typefaceText = ResourcesCompat.getFont(context, font)
    editor.typefaceLineNumber = ResourcesCompat.getFont(context, font)
  }

  private fun updateStickyScroll() {
    editor.props.stickyScroll = PreferencesUtils.stickyScroll
  }

  private fun updateFontLigatures() {
    editor.isLigatureEnabled = PreferencesUtils.fontLigatures
  }

  private fun updateWordWrap() {
    editor.isWordwrap = PreferencesUtils.wordWrap
  }

  private fun updateLineNumbers() {
    editor.isLineNumberEnabled = PreferencesUtils.lineNumbers
  }

  private fun updateDeleteEmptyLineFast() {
    editor.props.deleteEmptyLineFast = PreferencesUtils.deleteEmptyLineFast
  }

  private fun updateDeleteTabs() {
    editor.props.deleteMultiSpaces = if (PreferencesUtils.deleteMultiSpaces) -1 else 1
  }

  private fun setLoading(loading: Boolean) {
    binding.progress.isVisible = loading
    editor.isEditable = !loading
  }

  private fun createColorScheme(): EditorColorScheme {
    return try {
      TextMateColorScheme.create(ThemeRegistry.getInstance())
    } catch (e: Exception) {
      EditorColorScheme()
    }
  }

  private suspend fun createLanguage(): Language {
    val scopeName: String? = GrammarProvider.findScopeByFileExtension(file?.extension)

    return if (scopeName != null) {
      VCSpaceTMLanguage.create(scopeName)
    } else EmptyLanguage()
  }
}
