package com.raredev.vcspace.editor

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.blankj.utilcode.util.FileIOUtils
import com.raredev.vcspace.databinding.LayoutCodeEditorBinding
import com.raredev.vcspace.editor.schemes.SchemeVCSpace
import com.raredev.vcspace.extensions.cancelIfActive
import com.raredev.vcspace.providers.GrammarProvider
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.text.LineSeparator
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    binding.editor.apply {
      colorScheme = createColorScheme()
      lineSeparator = LineSeparator.LF
      this.file = file
    }
    binding.searcher.bindSearcher(editor.searcher)

    setLoading(true)
    editorScope.launch {
      val content = FileIOUtils.readFile2String(file)
      val language = createLanguage()

      withContext(Dispatchers.Main) {
        binding.editor.setText(content, null)
        postRead(language)
      }
    }
    addView(binding.root, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
  }

  private fun postRead(language: Language) {
    editor.setEditorLanguage(language)
    editor.subscribeEvents()
    setLoading(false)
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
    editorScope.cancelIfActive("Editor released")
    binding.editor.release()
  }

  suspend fun saveFile(): Boolean {
    if (!modified) {
      return false
    }
    return if (FileIOUtils.writeFileFromString(file, editor.text.toString())) {
      setModified(false)
      true
    } else false
  }

  fun beginSearchMode() {
    binding.searcher.beginSearchMode()
  }

  private fun setLoading(loading: Boolean) {
    binding.progress.isVisible = loading
    editor.isEditable = !loading
  }

  private fun createColorScheme(): EditorColorScheme {
    return try {
      TextMateColorScheme.create(ThemeRegistry.getInstance())
//      SchemeVCSpace()
    } catch (e: Exception) {
      EditorColorScheme()
    }
  }

  private fun createLanguage(): Language {
    val scopeName: String? = GrammarProvider.findScopeByFileExtension(file?.extension)

    return if (scopeName != null) {
      VCSpaceTMLanguage.create(scopeName)
    } else EmptyLanguage()
  }
}
