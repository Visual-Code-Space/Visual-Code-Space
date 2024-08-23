package com.teixeira.vcspace.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teixeira.vcspace.databinding.LayoutSymbolItemBinding
import com.teixeira.vcspace.editor.VCSpaceEditor
import com.teixeira.vcspace.models.Symbol

class SymbolInputAdapter : RecyclerView.Adapter<SymbolInputAdapter.VH>() {

  private val symbols: Array<Symbol> = defaultSymbols
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

    val controller = editor.snippetController
    if ("→" == symbol.label && controller.isInSnippet()) {
      controller.shiftToNextTabStop()
      return
    }

    if ("→" == symbol.label) {
      editor.indentOrCommitTab()
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

  private val defaultSymbols: Array<Symbol>
    get() {
      val baseSymbols =
        arrayOf(
          "→",
          "{}",
          "}",
          "()",
          ")",
          "\"\"",
          "''",
          "[]",
          "]",
          ";",
          "<",
          "/",
          ">",
          ":",
          "=",
          "+",
          "-",
          "*",
          "%",
          "&",
          "|",
          "^",
          "!",
          "?",
        )
      return baseSymbols.map { Symbol(it[0].toString(), it) }.toTypedArray()
    }
}
