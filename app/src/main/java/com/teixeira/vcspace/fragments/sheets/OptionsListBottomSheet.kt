/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.teixeira.vcspace.fragments.sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.teixeira.vcspace.adapters.SheetOptionsListAdapter
import com.teixeira.vcspace.databinding.LayoutSheetDialogBinding
import com.teixeira.vcspace.models.SheetOptionItem

class OptionsListBottomSheet : BottomSheetDialogFragment() {

  private val options: MutableList<SheetOptionItem> = ArrayList()
  private var listener: (SheetOptionItem) -> Unit = {}

  private var binding: LayoutSheetDialogBinding? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    binding = LayoutSheetDialogBinding.inflate(inflater, container, false)
    return binding!!.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    binding!!.recyclerView.apply {
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
