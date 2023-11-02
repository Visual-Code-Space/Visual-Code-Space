package com.raredev.vcspace.editor

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.blankj.utilcode.util.FileIOUtils
import com.raredev.vcspace.databinding.LayoutSoraEditorPanelBinding
import com.raredev.vcspace.editor.VCSpaceEditor
import com.raredev.vcspace.interfaces.IEditorPanel
import com.raredev.vcspace.providers.GrammarProvider
import com.raredev.vcspace.tasks.TaskExecutor.executeAsync
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

class SoraEditorPanel(
  context: Context,
  file: File
): LinearLayout(context), IEditorPanel {

  private val binding = LayoutSoraEditorPanelBinding.inflate(LayoutInflater.from(context))

  val editor: VCSpaceEditor
    get() = binding.editor

  init {
    binding.editor.apply {
      colorScheme = createColorScheme()
      lineSeparator = LineSeparator.LF
      this.file = file
    }
    binding.searcher.bindSearcher(editor.searcher)

    setLoading(true)
    CoroutineScope(Dispatchers.IO).launch {
      val content = FileIOUtils.readFile2String(file)
      val language = createLanguage()

      withContext(Dispatchers.Main) {
        binding.editor.setText(content, null)
        postRead(language)
      }
    }
    addView(binding.root, LayoutParams(
      LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
    ))
  }

  fun postRead(language: Language) {
    editor.setEditorLanguage(language)
    editor.subscribeEvents()
    setLoading(false)
  }

  override fun setModified(modified: Boolean) {
    editor.modified = modified
  }

  override fun isModified(): Boolean = editor.modified

  override fun undo() = editor.undo()

  override fun redo() = editor.redo()

  override fun canUndo() = editor.canUndo()

  override fun canRedo() = editor.canRedo()

  override fun getFile(): File? = editor.file ?: File("")

  override fun setFile(file: File) {
    editor.file = file
  }

  override fun release() {
    binding.editor.release()
  }

  override fun saveFile() {
    if (!isModified()) {
      return
    }
    if (FileIOUtils.writeFileFromString(getFile(), editor.text.toString())) {
      editor.modified = false
    }
  }

  override fun beginSearcher() {
    
  }

  override fun setLoading(loading: Boolean) {
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

  private fun createLanguage(): Language {
    val scopeName: String? = GrammarProvider.findScopeByFileExtension(getFile()?.extension)

    return if (scopeName != null) {
      VCSpaceTMLanguage.create(scopeName)
    } else EmptyLanguage()
  }
}
