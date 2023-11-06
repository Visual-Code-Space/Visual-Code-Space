package com.raredev.vcspace.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.PopupMenu
import com.raredev.vcspace.databinding.LayoutSearcherBinding
import com.raredev.vcspace.res.R
import io.github.rosemoe.sora.widget.EditorSearcher
import io.github.rosemoe.sora.widget.EditorSearcher.SearchOptions

class SearcherLayout : LinearLayout, View.OnClickListener {

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

  constructor(
      context: Context,
      attrs: AttributeSet?,
      defStyleAttr: Int
  ) : super(context, attrs, defStyleAttr)

  private val binding = LayoutSearcherBinding.inflate(LayoutInflater.from(context))
  private val optionsMenu: PopupMenu

  private var searchOptions = SearchOptions(true, false)
  private var searcher: EditorSearcher? = null
  private var isSearching = false

  init {
    binding.searchText.addTextChangedListener(
        object : TextWatcher {
          override fun afterTextChanged(editable: Editable) {
            search(editable.toString())
          }

          override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

          override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        })

    optionsMenu = PopupMenu(context, binding.searchOptions)
    optionsMenu.menu.add(0, 0, 0, R.string.ignore_letter_case).apply {
      isCheckable = true
      isChecked = true
    }
    optionsMenu.menu.add(0, 1, 0, R.string.use_regex).apply {
      isCheckable = true
      isChecked = false
    }

    optionsMenu.setOnMenuItemClickListener { item ->
      item.isChecked = !item.isChecked

      var ignoreCase = searchOptions.ignoreCase
      var useRegex = searchOptions.type == EditorSearcher.SearchOptions.TYPE_REGULAR_EXPRESSION
      when (item.itemId) {
        0 -> ignoreCase = item.isChecked
        1 -> useRegex = item.isChecked
      }
      searchOptions = SearchOptions(ignoreCase, useRegex)
      search(binding.searchText.text.toString())
      true
    }
    binding.searchOptions.setOnClickListener(this)
    binding.gotoLast.setOnClickListener(this)
    binding.gotoNext.setOnClickListener(this)
    binding.replace.setOnClickListener(this)
    binding.replaceAll.setOnClickListener(this)
    binding.close.setOnClickListener(this)

    addView(binding.root, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

    binding.root.visibility = View.GONE
  }

  override fun onClick(view: View) {
    when (view.id) {
      binding.searchOptions.id -> optionsMenu.show()
      binding.gotoLast.id -> gotoLast()
      binding.gotoNext.id -> gotoNext()
      binding.replace.id -> replace()
      binding.replaceAll.id -> replaceAll()
      binding.close.id -> {
        if (isSearching) {
          binding.root.visibility = View.GONE
          isSearching = false
        }
      }
    }
  }

  fun beginSearchMode() {
    if (!isSearching) {
      binding.root.visibility = View.VISIBLE
      isSearching = true
    }
  }

  fun bindSearcher(searcher: EditorSearcher) {
    this.searcher = searcher
  }

  fun search(text: String) {
    if (text.length > 0) {
      searcher?.search(text, searchOptions)
    } else searcher?.stopSearch()
  }

  private fun gotoLast() {
    try {
      searcher?.gotoPrevious()
    } catch (e: IllegalStateException) {
      e.printStackTrace()
    }
  }

  private fun gotoNext() {
    try {
      searcher?.gotoNext()
    } catch (e: IllegalStateException) {
      e.printStackTrace()
    }
  }

  private fun replace() {
    try {
      val replaceText = binding.replaceText.text.toString()
      if (replaceText.length > 0) {
        searcher?.replaceThis(replaceText)
      }
    } catch (e: IllegalStateException) {
      e.printStackTrace()
    }
  }

  private fun replaceAll() {
    try {
      val replaceText = binding.replaceText.text.toString()
      if (replaceText.length > 0) {
        searcher?.replaceAll(replaceText)
      }
    } catch (e: IllegalStateException) {
      e.printStackTrace()
    }
  }
}
