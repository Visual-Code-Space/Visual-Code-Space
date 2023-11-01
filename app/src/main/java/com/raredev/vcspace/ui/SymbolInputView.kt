package com.raredev.vcspace.ui

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raredev.vcspace.adapters.SymbolInputAdapter
import com.raredev.vcspace.interfaces.IEditorPanel

class SymbolInputView: RecyclerView {

  constructor(context: Context): super(context)
  constructor(context: Context, attrs: AttributeSet?): super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):
    super(context, attrs, defStyleAttr)

  private val adapter = SymbolInputAdapter()

  init {
    setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))
    setAdapter(adapter)
  }

  fun bindEditor(editor: IEditorPanel?) {
    adapter.bindEditor(editor)
  }
}
