package com.teixeira.vcspace.ui.screens.editor.components.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.blankj.utilcode.util.FileIOUtils
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teixeira.vcspace.editor.VCSpaceEditor
import com.teixeira.vcspace.editor.databinding.LayoutCodeEditorBinding
import com.teixeira.vcspace.editor.language.java.JavaLanguage
import com.teixeira.vcspace.events.OnPreferenceChangeEvent
import com.teixeira.vcspace.preferences.PREF_APPEARANCE_UI_MODE_KEY
import com.teixeira.vcspace.preferences.PREF_EDITOR_COLORSCHEME_KEY
import com.teixeira.vcspace.preferences.PREF_EDITOR_DELETELINEONBACKSPACE_KEY
import com.teixeira.vcspace.preferences.PREF_EDITOR_DELETETABONBACKSPACE_KEY
import com.teixeira.vcspace.preferences.PREF_EDITOR_FONTLIGATURES_KEY
import com.teixeira.vcspace.preferences.PREF_EDITOR_FONT_KEY
import com.teixeira.vcspace.preferences.PREF_EDITOR_FONT_SIZE_KEY
import com.teixeira.vcspace.preferences.PREF_EDITOR_INDENT_KEY
import com.teixeira.vcspace.preferences.PREF_EDITOR_LINENUMBER_KEY
import com.teixeira.vcspace.preferences.PREF_EDITOR_STICKYSCROLL_KEY
import com.teixeira.vcspace.preferences.PREF_EDITOR_USETAB_KEY
import com.teixeira.vcspace.preferences.PREF_EDITOR_WORDWRAP_KEY
import com.teixeira.vcspace.preferences.editorColorScheme
import com.teixeira.vcspace.preferences.editorDeleteLineOnBackspace
import com.teixeira.vcspace.preferences.editorDeleteTabOnBackspace
import com.teixeira.vcspace.preferences.editorFont
import com.teixeira.vcspace.preferences.editorFontLigatures
import com.teixeira.vcspace.preferences.editorFontSize
import com.teixeira.vcspace.preferences.editorIndent
import com.teixeira.vcspace.preferences.editorLineNumber
import com.teixeira.vcspace.preferences.editorStickyScroll
import com.teixeira.vcspace.preferences.editorUseTab
import com.teixeira.vcspace.preferences.editorWordWrap
import com.teixeira.vcspace.providers.GrammarProvider
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.utils.cancelIfActive
import com.teixeira.vcspace.utils.isDarkMode
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.text.LineSeparator
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import io.github.rosemoe.sora.widget.schemes.SchemeEclipse
import io.github.rosemoe.sora.widget.schemes.SchemeVS2019
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

@SuppressLint("ViewConstructor")
class CodeEditorView(context: Context, file: File) : LinearLayout(context) {

  private val binding = LayoutCodeEditorBinding.inflate(LayoutInflater.from(context))

  private val editorScope = CoroutineScope(Dispatchers.Default)

  val editor: VCSpaceEditor
    get() = binding.editor

  val modified: Boolean
    get() = editor.modified

  var file: File?
    get() = editor.file
    set(value) {
      editor.file = value
    }

  init {
    EventBus.getDefault().register(this)
    binding.searcher.bindSearcher(editor.searcher)
    binding.editor.apply {
      this.colorScheme = createColorScheme()
      this.lineSeparator = LineSeparator.LF
      this.file = file
    }
    configureEditor()
    readFile(file)

    addView(binding.root, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
  }

  fun applyDynamicColor(activity: Activity) {
    DynamicColors.applyToActivityIfAvailable(activity)
  }

  private fun readFile(file: File) {
    setLoading(true)
    editorScope.launch(Dispatchers.IO) {
      val content = FileIOUtils.readFile2String(file)
      val language = createLanguage()

      if (language is JavaLanguage) withContext(Dispatchers.Main) {
        editor.colorScheme = when (context.isDarkMode()) {
          true -> SchemeVS2019()
          false -> SchemeEclipse()
        }
        editor.setText(content, null)
        editor.setEditorLanguage(language)
        setLoading(false)
      } else withContext(Dispatchers.Main) {
        editor.colorScheme = TextMateColorScheme.create(ThemeRegistry.getInstance())
        editor.setText(content, null)
        editor.setEditorLanguage(language)
        setLoading(false)
      }
    }
  }

  fun confirmReload() {
    if (modified) {
      MaterialAlertDialogBuilder(context)
        .setTitle(R.string.file_reload)
        .setMessage(R.string.file_reload_unsaved_message)
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

  fun updateFile(file: File, updateContent: Boolean) {
    this.file = file

    if (updateContent) {
      readFile(file)
    } else updateLanguage()
  }

  fun updateLanguage() {
    setLoading(true)
    editorScope.launch {
      val language = createLanguage()

      withContext(Dispatchers.Main) {
        editor.setEditorLanguage(language)
        setLoading(false)
      }
    }
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
      PREF_APPEARANCE_UI_MODE_KEY,
      PREF_EDITOR_COLORSCHEME_KEY -> updateEditorColorScheme()

      PREF_EDITOR_FONT_KEY -> updateEditorFont()
      PREF_EDITOR_FONT_SIZE_KEY -> updateFontSize()
      PREF_EDITOR_INDENT_KEY -> updateEditorIndent()
      PREF_EDITOR_STICKYSCROLL_KEY -> updateStickyScroll()
      PREF_EDITOR_FONTLIGATURES_KEY -> updateFontLigatures()
      PREF_EDITOR_WORDWRAP_KEY -> updateWordWrap()
      PREF_EDITOR_LINENUMBER_KEY -> updateLineNumbers()
      PREF_EDITOR_USETAB_KEY -> updateEditorUseTab()
      PREF_EDITOR_DELETELINEONBACKSPACE_KEY -> updateDeleteEmptyLineFast()
      PREF_EDITOR_DELETETABONBACKSPACE_KEY -> updateDeleteTabs()
    }
  }

  private fun configureEditor() {
    updateEditorFont()
    updateFontSize()
    updateEditorIndent()
    updateStickyScroll()
    updateFontLigatures()
    updateWordWrap()
    updateLineNumbers()
    updateDeleteEmptyLineFast()
    updateDeleteTabs()
  }

  private fun updateEditorColorScheme() {
    ThemeRegistry.getInstance().setTheme(editorColorScheme)
    // Required to update colors correctly :-)
    editor.setText(editor.text.toString())
  }

  private fun updateEditorFont() {
    val font = ResourcesCompat.getFont(context, editorFont)
    editor.typefaceText = font
    editor.typefaceLineNumber = font
  }

  private fun updateFontSize() {
    editor.setTextSize(editorFontSize)
  }

  private fun updateEditorIndent() {
    (editor.editorLanguage as? TextMateLanguage)?.tabSize = editorIndent
    editor.tabWidth = editorIndent
  }

  private fun updateEditorUseTab() {
    (editor.editorLanguage as? TextMateLanguage)?.useTab(editorUseTab)
  }

  private fun updateStickyScroll() {
    editor.props.stickyScroll = editorStickyScroll
  }

  private fun updateFontLigatures() {
    editor.isLigatureEnabled = editorFontLigatures
  }

  private fun updateWordWrap() {
    editor.isWordwrap = editorWordWrap
  }

  private fun updateLineNumbers() {
    editor.isLineNumberEnabled = editorLineNumber
  }

  private fun updateDeleteEmptyLineFast() {
    editor.props.deleteEmptyLineFast = editorDeleteLineOnBackspace
  }

  private fun updateDeleteTabs() {
    editor.props.deleteMultiSpaces = if (editorDeleteTabOnBackspace) -1 else 1
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
    if (file?.extension == "java") {
      return JavaLanguage()
    }

    val scopeName: String? = GrammarProvider.findScopeByFileExtension(file?.extension)

    return if (scopeName != null) {
      TextMateLanguage.create(scopeName, GrammarRegistry.getInstance(), true).apply {
        tabSize = editorIndent
        useTab(editorUseTab)
      }
    } else EmptyLanguage()
  }
}
