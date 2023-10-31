package com.raredev.vcspace.ui

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.TooltipCompat
import com.google.android.material.color.MaterialColors
import com.raredev.vcspace.res.R
import com.raredev.vcspace.databinding.LayoutSearcherBinding
import com.raredev.vcspace.utils.PreferencesUtils.prefs
import com.raredev.vcspace.utils.Utils
import io.github.rosemoe.sora.widget.EditorSearcher

class SearcherLayout: LinearLayout, View.OnClickListener {

  constructor(context: Context): super(context)
  constructor(context: Context, attrs: AttributeSet?): super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):
    super(context, attrs, defStyleAttr)

  private val binding = LayoutSearcherBinding.inflate(LayoutInflater.from(context))

  private var searchOptions: EditorSearcher.SearchOptions? = null
  private var searcher: EditorSearcher? = null

  private var isSearching = false

  init {
    binding.searchText.addTextChangedListener(object: TextWatcher {
      override fun afterTextChanged(editable: Editable) {
        search(editable.toString())
      }

      override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
      override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    })

    binding.ignoreLetterCase.setOnClickListener {
      val ignoreCase = prefs.getBoolean(KEY_SEARCH_IGNORE_LETTER_CASE, true)
      prefs.edit().putBoolean(KEY_SEARCH_IGNORE_LETTER_CASE, !ignoreCase).commit()
      updateSearchOptions()
    }
    binding.useRegex.setOnClickListener {
      val useRegex = prefs.getBoolean(KEY_SEARCH_USE_REGEX, false)
      prefs.edit().putBoolean(KEY_SEARCH_USE_REGEX, !useRegex).commit()
      updateSearchOptions()
    }
    binding.gotoLast.setOnClickListener(this)
    binding.gotoNext.setOnClickListener(this)
    binding.replace.setOnClickListener(this)
    binding.replaceAll.setOnClickListener(this)

    TooltipCompat.setTooltipText(binding.ignoreLetterCase, context.getString(R.string.ignore_letter_case))
    TooltipCompat.setTooltipText(binding.useRegex, context.getString(R.string.use_regex))
    addView(binding.root, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

    visibility = View.GONE
  }

  override fun onClick(v: View) {
    if (searchOptions == null || searcher == null) {
      return
    }
    val id = v.id
    if (id == binding.gotoLast.id) {
      gotoLast()
    } else if (id == binding.gotoNext.id) {
      gotoNext()
    } else if (id == binding.replace.id) {
      replace()
    } else if (id == binding.replaceAll.id) {
      replaceAll()
    }
  }

  fun beginSearchMode() {
    isSearching = !isSearching
    if (isSearching) {
      visibility = View.VISIBLE
      updateSearchOptions()

      search(binding.searchText.text.toString())
    } else {
      searcher?.stopSearch()
      visibility = View.GONE
    }
  }

  fun bindSearcher(searcher: EditorSearcher) {
    this.searcher = searcher
  }

  fun search(text: String) {
    if (searchOptions == null || searcher == null) {
      return
    }
    if (TextUtils.isEmpty(text)) {
      searcher!!.stopSearch()
      return
    }
    searcher!!.search(text, searchOptions!!)
  }

  fun updateSearchOptions() {
    val ignoreCase = prefs.getBoolean(KEY_SEARCH_IGNORE_LETTER_CASE, true)
    val useRegex = prefs.getBoolean(KEY_SEARCH_USE_REGEX, false)

    var colorPrimary = MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimary)
    var colorControlNormal = MaterialColors.getColor(this, com.google.android.material.R.attr.colorControlNormal)

    Utils.setDrawableTint(binding.ignoreLetterCase.drawable, if (ignoreCase) colorPrimary else colorControlNormal)
    Utils.setDrawableTint(binding.useRegex.drawable, if (useRegex) colorPrimary else colorControlNormal)

    searchOptions = EditorSearcher.SearchOptions(ignoreCase, useRegex)
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
      searcher?.gotoNext();
    } catch (e: IllegalStateException) {
      e.printStackTrace()
    }
  }

  private fun replace() {
    try {
      searcher?.replaceThis(binding.replaceText.text.toString());
    } catch (e: IllegalStateException) {
      e.printStackTrace()
    }
  }

  private fun replaceAll() {
    try {
      searcher?.replaceAll(binding.replaceText.text.toString())
    } catch (e: IllegalStateException) {
      e.printStackTrace()
    }
  }

  companion object {
    const val KEY_SEARCH_IGNORE_LETTER_CASE = "searcher_ignoreLetterCase"
    const val KEY_SEARCH_USE_REGEX = "searcher_useRegex"
  }
}
