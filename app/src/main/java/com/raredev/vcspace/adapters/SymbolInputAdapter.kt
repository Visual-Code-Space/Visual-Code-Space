package com.raredev.vcspace.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raredev.vcspace.databinding.LayoutSymbolItemBinding
import com.raredev.vcspace.editor.VCSpaceEditor
import com.raredev.vcspace.models.Symbol
import com.raredev.vcspace.utils.PreferencesUtils

class SymbolInputAdapter : RecyclerView.Adapter<SymbolInputAdapter.VH>() {

  private val symbols: Array<Symbol> = getDefaultSymbols()
  private var editor: VCSpaceEditor? = null

  inner class VH(internal val binding: LayoutSymbolItemBinding) :
      RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    return VH(LayoutSymbolItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
  }

  override fun onBindViewHolder(holder: VH, position: Int) {
    holder.binding.apply {
      val symbol = symbols[position]
      label.text = symbol.label

      root.setOnClickListener { insertSymbol(symbol) }
    }
  }

  override fun getItemCount(): Int {
    return symbols.size
  }

  private fun insertSymbol(symbol: Symbol) {
    val editor = editor ?: return
    if (!editor.isEditable) {
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

  fun bindEditor(editor: VCSpaceEditor?) {
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
