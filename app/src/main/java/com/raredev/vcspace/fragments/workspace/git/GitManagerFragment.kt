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
package com.raredev.vcspace.fragments.workspace.git

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.raredev.vcspace.app.BaseApplication
import com.raredev.vcspace.databinding.FragmentGitManagerBinding
import com.raredev.vcspace.fragments.workspace.WorkspaceFragment
import com.raredev.vcspace.progressdialog.ProgressDialog
import com.raredev.vcspace.utils.GitUtils
import com.raredev.vcspace.utils.showShortToast
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GitManagerFragment : Fragment() {

  private var _binding: FragmentGitManagerBinding? = null
  private val binding: FragmentGitManagerBinding
    get() = checkNotNull(_binding)

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    _binding = FragmentGitManagerBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val navController = findNavController()
    (parentFragment?.parentFragment as? WorkspaceFragment)?.setTitle("Git Control")

    val openedFolderPath = BaseApplication.getInstance().getPrefs().getString("openedFolder", "")
    binding.init.setOnClickListener {
      val dialog =
          ProgressDialog.create(requireContext())
              .setTitle("Initializing repo")
              .setLoadingMessage("Please wait..")
              .create()
              .apply { setCancelable(false) }

      dialog.show()
      CoroutineScope(Dispatchers.IO).launch {
        try {
          GitUtils.init(File(openedFolderPath))
          if (binding.initialCommit.isChecked) {
            if (GitUtils.repositoryExists(File(openedFolderPath))) {
              val git = GitUtils(File(openedFolderPath, ".git"))
              git.createInitialCommit("https://github.com/itsvks19/tes.git")
              git.renameBranchToMain()
            } else
                withContext(Dispatchers.Main) { showShortToast(requireContext(), "Repo not found") }
          }
          withContext(Dispatchers.Main) {
            navController.navigate(GitManagerFragmentDirections.actionGoToGitControl())
          }
        } catch (e: Exception) {
          withContext(Dispatchers.Main) { showShortToast(requireContext(), e.message.toString()) }
        } finally {
          withContext(Dispatchers.Main) { dialog.cancel() }
        }
      }
    }
  }
}
