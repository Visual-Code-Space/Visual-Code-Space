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
package com.teixeira.vcspace.fragments.workspace.explorer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teixeira.vcspace.adapters.FileListAdapter
import com.teixeira.vcspace.databinding.FragmentFileExplorerBinding
import com.teixeira.vcspace.events.OnDeleteFileEvent
import com.teixeira.vcspace.events.OnRenameFileEvent
import com.teixeira.vcspace.fragments.sheets.OptionsListBottomSheet
import com.teixeira.vcspace.models.SheetOptionItem
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.resources.databinding.LayoutTextinputBinding
import com.teixeira.vcspace.utils.ApkInstaller
import com.teixeira.vcspace.utils.isValidTextFile
import com.teixeira.vcspace.utils.launchWithProgressDialog
import com.teixeira.vcspace.utils.showShortToast
import com.teixeira.vcspace.viewmodel.EditorViewModel
import com.teixeira.vcspace.viewmodel.FileExplorerViewModel
import java.io.File
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

class FileExplorerFragment : Fragment(), FileListAdapter.OnFileClickListener {

  private val editorViewModel by viewModels<EditorViewModel>(ownerProducer = { requireActivity() })
  private val fileViewModel by
    viewModels<FileExplorerViewModel>(ownerProducer = { requireActivity() })

  private var _binding: FragmentFileExplorerBinding? = null
  private val binding: FragmentFileExplorerBinding
    get() = checkNotNull(_binding)

  private val coroutineScope = CoroutineScope(Dispatchers.Default)
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

    fileViewModel.files.observe(viewLifecycleOwner) { files ->
      binding.emptyFolder.isVisible = files.isEmpty()
      adapter.submitList(files)
    }

    fileViewModel.currentPath.observe(viewLifecycleOwner) { path -> binding.pathList.setPath(path) }
    binding.pathList.setFileExplorerViewModel(fileViewModel)

    binding.navigationSpace.addItem(R.string.refresh, R.drawable.ic_refresh) { refreshFiles() }
    binding.navigationSpace.addItem(R.string.create, R.drawable.ic_add) {
      showCreateFileDialog(fileViewModel.currentPath.value!!)
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
    } else if (file.name.endsWith(".apk")) {
      ApkInstaller.installApplication(requireContext(), file)
    } else if (isValidTextFile(file.name)) {
      editorViewModel.openFile(file)
    }
  }

  override fun onFileLongClickListener(file: File, view: View): Boolean {
    val sheet = OptionsListBottomSheet()
    sheet.addOption(SheetOptionItem(R.drawable.ic_copy, getString(R.string.file_copy_path)))
    sheet.addOption(SheetOptionItem(R.drawable.ic_file_rename, getString(R.string.file_rename)))
    sheet.addOption(SheetOptionItem(R.drawable.ic_delete, getString(R.string.file_delete)))

    sheet.setOptionClickListener { option ->
      when (option.name) {
        getString(R.string.file_copy_path) -> ClipboardUtils.copyText(file.absolutePath)
        getString(R.string.file_rename) -> showRenameFileDialog(file)
        getString(R.string.file_delete) -> showDeleteFileDialog(file)
      }
      sheet.dismiss()
    }
    sheet.show(childFragmentManager, null)

    return true
  }

  fun setCurrentPath(path: String) {
    fileViewModel.setCurrentPath(path)
  }

  fun refreshFiles() {
    fileViewModel.refreshFiles()
  }

  @SuppressLint("RestrictedApi")
  @Suppress("DEPRECATION")
  private fun showCreateFileDialog(path: String) {
    MaterialAlertDialogBuilder(requireContext()).apply {
      val binding = LayoutTextinputBinding.inflate(LayoutInflater.from(requireContext()))
      setView(binding.root, dp2px(20f), dp2px(5f), dp2px(20f), 0)
      setTitle(R.string.create)
      setNeutralButton(R.string.no, null)
      setNegativeButton(R.string.file) { _, _ ->
        val name = binding.inputEdittext.text.toString().trim()
        with(File(path, name)) {
          try {
            if (!exists() && createNewFile()) {
              fileViewModel.refreshFiles()
            }
          } catch (ioe: IOException) {
            ioe.printStackTrace()
          }
        }
      }
      setPositiveButton(R.string.file_folder) { _, _ ->
        val name = binding.inputEdittext.text.toString().trim()
        with(File(path, name)) {
          try {
            if (!exists() && mkdirs()) {
              fileViewModel.refreshFiles()
            }
          } catch (ioe: IOException) {
            ioe.printStackTrace()
          }
        }
      }
      val dialog = create()
      dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
      dialog.setOnShowListener {
        binding.inputLayout.setHint(R.string.file_enter_name)
        binding.inputEdittext.requestFocus()
      }
      dialog.show()
    }
  }

  @SuppressLint("RestrictedApi")
  @Suppress("DEPRECATION")
  private fun showRenameFileDialog(file: File) {
    MaterialAlertDialogBuilder(requireContext()).apply {
      val binding = LayoutTextinputBinding.inflate(LayoutInflater.from(requireContext()))
      setView(binding.root, dp2px(20f), dp2px(5f), dp2px(20f), 0)
      setTitle(R.string.file_rename)
      setNegativeButton(R.string.no, null)
      setPositiveButton(R.string.yes) { _, _ ->
        coroutineScope.launchWithProgressDialog(
          uiContext = requireContext(),
          configureBuilder = { builder ->
            builder.setMessage(R.string.file_renaming)
            builder.setCancelable(false)
          },
          action = { _ ->
            val name = binding.inputEdittext.text.toString().trim()
            val newFile = File(file.parentFile, name)
            val renamed = file.renameTo(newFile)

            if (!renamed) {
              return@launchWithProgressDialog
            }

            EventBus.getDefault().post(OnRenameFileEvent(file, newFile))

            withContext(Dispatchers.Main) {
              showShortToast(requireContext(), getString(R.string.file_renamed))
              fileViewModel.refreshFiles()
            }
          }
        )
      }
      val dialog = create()
      dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
      dialog.setOnShowListener {
        binding.inputLayout.setHint(R.string.file_enter_name)
        binding.inputEdittext.setText(file.name)
        binding.inputEdittext.requestFocus()
      }
      dialog.show()
    }
  }

  private fun showDeleteFileDialog(file: File) {
    MaterialAlertDialogBuilder(requireContext())
      .setTitle(R.string.file_delete)
      .setMessage(getString(R.string.file_delete_message, file.name))
      .setNegativeButton(R.string.no, null)
      .setPositiveButton(R.string.yes) { _, _ ->
        coroutineScope.launchWithProgressDialog(
          uiContext = requireContext(),
          configureBuilder = { builder ->
            builder.setMessage(R.string.file_deleting)
            builder.setCancelable(false)
          },
          action = { _ ->
            val deleted = FileUtils.delete(file)

            if (!deleted) {
              return@launchWithProgressDialog
            }

            EventBus.getDefault().post(OnDeleteFileEvent(file))

            withContext(Dispatchers.Main) {
              showShortToast(requireContext(), getString(R.string.file_deleted))
              fileViewModel.refreshFiles()
            }
          }
        )
      }
      .show()
  }
}
