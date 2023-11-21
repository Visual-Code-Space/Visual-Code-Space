/**
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
package com.raredev.vcspace.fragments.workspace.explorer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ClipboardUtils
import com.raredev.vcspace.activities.editor.EditorActivity
import com.raredev.vcspace.adapters.FileListAdapter
import com.raredev.vcspace.databinding.FragmentFileExplorerBinding
import com.raredev.vcspace.fragments.sheets.OptionsListBottomSheet
import com.raredev.vcspace.models.SheetOptionItem
import com.raredev.vcspace.res.R
import com.raredev.vcspace.utils.ApkInstaller
import com.raredev.vcspace.utils.FileUtil
import com.raredev.vcspace.viewmodel.FileExplorerViewModel
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class FileExplorerFragment : Fragment(), FileListAdapter.OnFileClickListener {

  private val viewModel by viewModels<FileExplorerViewModel>(ownerProducer = { requireActivity() })

  private var _binding: FragmentFileExplorerBinding? = null
  private val binding: FragmentFileExplorerBinding
    get() = checkNotNull(_binding)

  internal val explorerScope = CoroutineScope(Dispatchers.Default)
  private val dialogs by lazy { FileExplorerDialogs(this, requireContext(), viewModel) }
  private val adapter by lazy { FileListAdapter(this) }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    _binding = FragmentFileExplorerBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewModel.files.observe(viewLifecycleOwner) { files ->
      if (files.isEmpty()) {
        binding.emptyFolder.visibility = View.VISIBLE
      } else {
        binding.emptyFolder.visibility = View.GONE
      }
      adapter.submitList(files)
    }

    viewModel.currentPath.observe(viewLifecycleOwner) { path -> binding.pathList.setPath(path) }
    binding.pathList.setFileExplorerViewModel(viewModel)

    binding.navigationSpace.addItem(R.string.refresh, R.drawable.ic_refresh) { refreshFiles() }
    binding.navigationSpace.addItem(R.string.create, R.drawable.ic_add) {
      dialogs.showCreateFileDialog(viewModel.currentPath.value!!)
    }

    binding.rvFiles.layoutManager = LinearLayoutManager(requireContext())
    binding.rvFiles.adapter = adapter
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onStart() {
    super.onStart()
    refreshFiles()
  }

  override fun onFileClickListener(file: File) {
    if (file.isDirectory) {
      setCurrentPath(file.absolutePath)
    } else {
      if (FileUtil.isValidTextFile(file.name)) {
        (requireActivity() as EditorActivity).openFile(file)
      } else {
        if (file.name.endsWith(".apk")) {
          ApkInstaller.installApplication(requireContext(), file)
        }
      }
    }
  }

  override fun onFileLongClickListener(file: File, view: View): Boolean {
    val sheet = OptionsListBottomSheet()

    sheet.addOption(SheetOptionItem(R.drawable.ic_copy, getString(R.string.copy_path)))
    sheet.addOption(SheetOptionItem(R.drawable.ic_file_rename, getString(R.string.rename)))
    sheet.addOption(SheetOptionItem(R.drawable.ic_delete, getString(R.string.delete)))

    sheet.setOptionClickListener { option ->
      when (option.name) {
        getString(R.string.copy_path) -> ClipboardUtils.copyText(file.absolutePath)
        getString(R.string.rename) -> dialogs.showRenameFileDialog(file)
        getString(R.string.delete) -> dialogs.showDeleteFileDialog(file)
      }
      sheet.dismiss()
    }
    sheet.show(childFragmentManager, null)
    return true
  }

  fun setCurrentPath(path: String) {
    viewModel.setCurrentPath(path)
  }

  fun refreshFiles() {
    viewModel.refreshFiles()
  }
}
