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

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.UriUtils
import com.raredev.vcspace.R
import com.raredev.vcspace.app.BaseApplication
import com.raredev.vcspace.databinding.FragmentNoOpenedFolderBinding
import com.raredev.vcspace.edit
import com.raredev.vcspace.fragments.workspace.WorkspaceFragment

class NoOpenedFolderFragment : Fragment() {
  private var _binding: FragmentNoOpenedFolderBinding? = null
  private val binding: FragmentNoOpenedFolderBinding
    get() = checkNotNull(_binding)

  private lateinit var navController: NavController
  private lateinit var openFolder: ActivityResultLauncher<Uri?>

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    _binding = FragmentNoOpenedFolderBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    navController = findNavController()
    ((parentFragment?.parentFragment) as WorkspaceFragment)?.setTitle("No opened folder")

    openFolder =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
          if (uri != null) {
            val pickedDir: DocumentFile? = DocumentFile.fromTreeUri(requireContext(), uri)
            val openedFolderPath = UriUtils.uri2File(pickedDir!!.uri).absolutePath
            val result = Bundle()
            result.putString("folderPath", openedFolderPath)
            BaseApplication.getInstance().getPrefs().edit {
              putString("openedFolder", openedFolderPath)
            }
            navController.navigate(R.id.action_go_to_file_explorer, result)
          }
        }

    binding.openFolder.setOnClickListener { openFolder.launch(null) }
    binding.openRecent.setOnClickListener {
      // I will implement this later
      // RecentFileWindow(requireContext()).show()
    }
  }
}
