package com.raredev.vcspace.editor

import android.content.Context
import android.view.LayoutInflater
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.blankj.utilcode.util.FileIOUtils
import com.raredev.vcspace.databinding.LayoutAceEditorPanelBinding
import com.raredev.vcspace.editor.ace.OnEditorLoadedListener
import com.raredev.vcspace.interfaces.IEditorPanel
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AceEditorPanel(context: Context, file: File) :
    LinearLayout(context), OnEditorLoadedListener, IEditorPanel {

  private val binding = LayoutAceEditorPanelBinding.inflate(LayoutInflater.from(context))

  val editor: AceCodeEditor
    get() = binding.editor

  init {
    binding.editor.onEditorLoadedListener = this
    binding.editor.file = file
    addView(binding.root)
  }

  override fun onLoaded(webView: WebView) {
    CoroutineScope(Dispatchers.IO).launch {
      val content = FileIOUtils.readFile2String(getFile())

      withContext(Dispatchers.Main) {
        binding.editor.apply {
          setText(content)
          setModified(false)
          setLanguageFromFile(getFile()?.absolutePath)
        }
        setLoading(false)
      }
    }
  }

  override fun setModified(modified: Boolean) {
    editor.isModified = modified
  }

  override fun isModified(): Boolean = editor.isModified

  override fun undo() = editor.undo()

  override fun redo() = editor.redo()

  override fun canUndo(): Boolean = editor.hasUndo()

  override fun canRedo(): Boolean = editor.hasRedo()

  override fun getFile(): File? = editor.file

  override fun setFile(file: File) {
    editor.file = file
  }

  override fun release() {
    editor.release()
  }

  override fun saveFile() {
    if (!isModified()) {
      return
    }
    if (FileIOUtils.writeFileFromString(getFile(), editor.getText())) {
      editor.isModified = false
    }
  }

  override fun beginSearcher() {}

  override fun setLoading(loading: Boolean) {
    binding.progress.isVisible = loading
    editor.setReadOnly(loading)
  }
}
