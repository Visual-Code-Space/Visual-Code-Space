package com.raredev.vcspace.ui

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raredev.vcspace.adapters.PathListAdapter
import com.raredev.vcspace.viewmodel.FileExplorerViewModel
import java.io.File

class PathListView : RecyclerView {

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

  constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
  ) : super(context, attrs, defStyleAttr)

  private val adapter = PathListAdapter()

  init {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    setAdapter(adapter)
  }

  fun setFileExplorerViewModel(viewModel: FileExplorerViewModel) {
    adapter.setFileExplorerViewModel(viewModel)
  }

  fun setPath(path: String) {
    if (path.startsWith("/data")) {
      adapter.setPath(null)
      return
    }

    adapter.setPath(File(path))

    scrollToPosition(adapter.itemCount - 1)
  }
}
