package com.teixeira.vcspace.editor

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.PopupMenu
import com.teixeira.vcspace.editor.databinding.LayoutSearcherBinding
import com.teixeira.vcspace.resources.R
import io.github.rosemoe.sora.widget.EditorSearcher
import io.github.rosemoe.sora.widget.EditorSearcher.SearchOptions

class SearcherLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes), View.OnClickListener {

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

                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }
            }
        )

        optionsMenu = PopupMenu(context, binding.searchOptions)
        optionsMenu.menu.add(0, 0, 0, R.string.editor_search_option_ignore_case).apply {
            isCheckable = true
            isChecked = true
        }
        optionsMenu.menu.add(0, 1, 0, R.string.editor_search_option_use_regex).apply {
            isCheckable = true
            isChecked = false
        }

        optionsMenu.setOnMenuItemClickListener { item ->
            item.isChecked = !item.isChecked

            var ignoreCase: Boolean = searchOptions.caseInsensitive
            var useRegex: Boolean = searchOptions.type == SearchOptions.TYPE_REGULAR_EXPRESSION
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

                    searcher?.stopSearch()
                }
            }
        }
    }

    fun beginSearchMode() {
        if (!isSearching) {
            binding.root.visibility = View.VISIBLE
            isSearching = true

            search(binding.searchText.text.toString())
        }
    }

    fun bindSearcher(searcher: EditorSearcher) {
        this.searcher = searcher
    }

    private fun search(text: String) {
        if (text.isNotEmpty()) {
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
            searcher?.replaceCurrentMatch(binding.replaceText.text.toString())
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
}
