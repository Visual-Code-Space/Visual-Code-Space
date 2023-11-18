package com.raredev.vcspace.fragments.workspace.explorer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ClipboardUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raredev.vcspace.activities.editor.EditorActivity
import com.raredev.vcspace.adapters.FileListAdapter
import com.raredev.vcspace.app.BaseApplication
import com.raredev.vcspace.databinding.FragmentFileExplorerBinding
import com.raredev.vcspace.edit
import com.raredev.vcspace.fragments.sheets.OptionsListBottomSheet
import com.raredev.vcspace.models.SheetOptionItem
import com.raredev.vcspace.res.R
import com.raredev.vcspace.utils.ApkInstaller
import com.raredev.vcspace.utils.FileUtil
import com.raredev.vcspace.viewmodel.FileExplorerViewModel
import java.io.File

class FileExplorerFragment : Fragment(), FileListAdapter.OnFileClickListener {

  private val viewModel by viewModels<FileExplorerViewModel>(ownerProducer = { requireActivity() })

  private var _binding: FragmentFileExplorerBinding? = null
  private val binding: FragmentFileExplorerBinding
    get() = checkNotNull(_binding)

  private val dialogs by lazy { FileExplorerDialogs(requireContext(), viewModel) }
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

    setCurrentPath(
        checkNotNull(
            arguments?.getString("folderPath")
                ?: BaseApplication.getInstance().getPrefs().getString("openedFolder", "")))
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
    binding.navigationSpace.addItem(R.string.close, R.drawable.ic_close) {
      MaterialAlertDialogBuilder(requireContext()).apply {
        setTitle("Close folder?")
        setMessage("Do you want to close this folder?")
        setPositiveButton(android.R.string.ok) { dialog, which ->
          findNavController().navigate(FileExplorerFragmentDirections.actionGoToNoFolder())
          BaseApplication.getInstance().getPrefs().edit { putString("openedFolder", "") }
        }
        setNegativeButton(android.R.string.cancel) { dialog, which -> dialog.dismiss() }
        show()
      }
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
