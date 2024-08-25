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

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.BundleCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.teixeira.vcspace.adapters.SheetOptionsListAdapter
import com.teixeira.vcspace.databinding.LayoutSheetDialogBinding
import com.teixeira.vcspace.models.SheetOptionItem

class OptionsListBottomSheet : DialogFragment() {

  private var onOptionClickListener: ((SheetOptionItem) -> Unit)? = null
  private var binding: LayoutSheetDialogBinding? = null

  companion object {
    const val KEY_OPTIONS = "key_options"

    @JvmStatic
    fun newInstance(
      options: Array<SheetOptionItem>,
      onOptionClickListener: (SheetOptionItem) -> Unit,
    ): OptionsListBottomSheet {
      return OptionsListBottomSheet().also {
        it.onOptionClickListener = onOptionClickListener
        it.arguments = Bundle().apply { putParcelableArray(KEY_OPTIONS, options) }
      }
    }
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val sheetDialog = BottomSheetDialog(requireContext())
    binding = LayoutSheetDialogBinding.inflate(sheetDialog.layoutInflater)
    sheetDialog.setContentView(binding!!.root)
    sheetDialog.behavior.apply {
      peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
      state = BottomSheetBehavior.STATE_EXPANDED
    }

    val args = arguments
    if (args != null && args.containsKey(KEY_OPTIONS)) {
      val options =
        BundleCompat.getParcelableArray(args, KEY_OPTIONS, SheetOptionItem::class.java)
          ?.map { it as SheetOptionItem }
          ?.toTypedArray()
      if (options != null) {
        binding!!.recyclerView.apply {
          layoutManager = LinearLayoutManager(requireContext())
          adapter =
            SheetOptionsListAdapter(options) {
              dismiss()
              onOptionClickListener?.invoke(it)
            }
        }
      }
    }
    return sheetDialog
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }
}
