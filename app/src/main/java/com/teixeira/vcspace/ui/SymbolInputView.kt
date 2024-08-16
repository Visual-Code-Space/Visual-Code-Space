package com.teixeira.vcspace.ui

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teixeira.vcspace.adapters.SymbolInputAdapter
import com.teixeira.vcspace.editor.VCSpaceEditor

class SymbolInputView : RecyclerView {

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

  constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
  ) : super(context, attrs, defStyleAttr)

  private val adapter = SymbolInputAdapter()

  init {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    setAdapter(adapter)
  }

  fun bindEditor(editor: VCSpaceEditor?) {
    adapter.bindEditor(editor)
  }
}
