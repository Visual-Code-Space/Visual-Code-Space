package com.raredev.vcspace.adapters

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.raredev.vcspace.databinding.LayoutSymbolItemBinding
import com.raredev.vcspace.editor.AceCodeEditor
import com.raredev.vcspace.editor.AceEditorPanel
import com.raredev.vcspace.editor.SoraEditorPanel
import com.raredev.vcspace.editor.VCSpaceEditor
import com.raredev.vcspace.interfaces.IEditorPanel
import com.raredev.vcspace.models.Symbol
import com.raredev.vcspace.res.R
import com.raredev.vcspace.utils.PreferencesUtils

class SymbolInputAdapter : RecyclerView.Adapter<SymbolInputAdapter.VH>() {

  private val symbols: Array<Symbol> = getDefaultSymbols()
  private var editor: IEditorPanel? = null

  inner class VH(internal val binding: LayoutSymbolItemBinding) :
      RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    return VH(LayoutSymbolItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
  }

  override fun onBindViewHolder(holder: VH, position: Int) {
    holder.binding.apply {
      val symbol = symbols[position]
      label.text = symbol.label
      val typeface = ResourcesCompat.getFont(label.context, R.font.jetbrains_mono)
      label.typeface = typeface

      root.setOnClickListener { insertSymbol(symbol) }
    }
  }

  override fun getItemCount(): Int {
    return symbols.size
  }

  private fun insertSymbol(symbol: Symbol) {
    if (editor != null) {
      if (editor is SoraEditorPanel) {
        insertSymbolSoraEditor((editor as SoraEditorPanel).editor, symbol)
      } else if (editor is AceEditorPanel) {
        insertSymbolAceEditor((editor as AceEditorPanel).editor, symbol)
      }
    }
  }

  private fun insertSymbolSoraEditor(editor: VCSpaceEditor, symbol: Symbol) {
    if (!editor.isEditable()) {
      return
    }

    val controller = editor.getSnippetController()
    if ("→".equals(symbol.label) && controller.isInSnippet()) {
      controller.shiftToNextTabStop()
      return
    }

    if ("→".equals(symbol.label)) {
      editor.commitText(PreferencesUtils.identationString)
      return
    }

    val insertText = symbol.insert
    if (insertText.length == 2) {
      editor.insertText(insertText, 1)
    } else {
      editor.commitText(insertText, false)
    }
  }

  private fun insertSymbolAceEditor(editor: AceCodeEditor, symbol: Symbol) {
    if ("→".equals(symbol.label)) {
      editor.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_TAB))
      return
    }
    editor.insert(symbol.insert[0].toString())
  }

  fun bindEditor(editor: IEditorPanel?) {
    this.editor = editor
  }

  fun getDefaultSymbols(): Array<Symbol> {
    val baseSymbols =
        arrayOf(
            "→",
            "()",
            ")",
            "{}",
            "}",
            ";",
            "\"\"",
            "''",
            ":",
            "[]",
            "]",
            "=",
            "+",
            "-",
            "*",
            "/",
            "%",
            "&",
            "|",
            "^",
            "!",
            "?",
            "<",
            ">")
    return baseSymbols.map { Symbol(it[0].toString(), it) }.toTypedArray()
  }
}
