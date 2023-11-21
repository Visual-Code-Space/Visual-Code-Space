package com.raredev.vcspace.fragments.sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.raredev.vcspace.adapters.SheetOptionsListAdapter
import com.raredev.vcspace.databinding.LayoutSheetDialogBinding
import com.raredev.vcspace.models.SheetOptionItem
import java.util.ArrayList

class OptionsListBottomSheet: BottomSheetDialogFragment() {

  private val options: MutableList<SheetOptionItem> = ArrayList()
  private var listener: (SheetOptionItem) -> Unit = {}

  private var binding: LayoutSheetDialogBinding? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    binding = LayoutSheetDialogBinding.inflate(inflater, container, false)
    return binding!!.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding?.recyclerView?.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = SheetOptionsListAdapter(options, listener)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    options.clear()
    binding = null
  }

  fun setOptionClickListener(listener: (SheetOptionItem) -> Unit) {
    this.listener = listener
  }

  fun addOption(option: SheetOptionItem) {
    options.add(option)
  }
}
