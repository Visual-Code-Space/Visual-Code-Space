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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raredev.vcspace.adapters.git.ChangedFileAdapter
import com.raredev.vcspace.adapters.git.CommitListAdapter
import com.raredev.vcspace.app.BaseApplication
import com.raredev.vcspace.databinding.FragmentGitControlBinding
import com.raredev.vcspace.fragments.workspace.WorkspaceFragment
import com.raredev.vcspace.progressdialog.ProgressDialog
import com.raredev.vcspace.utils.GitUtils
import com.raredev.vcspace.utils.PreferencesUtils.prefs
import com.raredev.vcspace.utils.SharedPreferencesKeys
import com.raredev.vcspace.utils.showShortToast
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GitControlFragment : Fragment() {
  private var _binding: FragmentGitControlBinding? = null
  private val binding: FragmentGitControlBinding
    get() = checkNotNull(_binding)

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    _binding = FragmentGitControlBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (parentFragment?.parentFragment as? WorkspaceFragment)?.setTitle("Git Control")
    val openedFolderPath = BaseApplication.getInstance().getPrefs().getString("openedFolder", "")
    val git = GitUtils(File(openedFolderPath, ".git"))

    binding.branchName.text = git.getCurrentBranchName()

    val uncommittedChangesSize = git.getStatus().getUncommittedChanges().size
    val itemDecoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)

    val username = prefs.getString(SharedPreferencesKeys.KEY_CREDENTIAL_USERNAME, "")
    val password = prefs.getString(SharedPreferencesKeys.KEY_CREDENTIAL_PASSWORD, "")

    if (uncommittedChangesSize == 0) {
      showCommitCard(false)
    } else {
      showCommitCard(true)
      try {
        binding.changedFileCount.text = "$uncommittedChangesSize changed file"

        binding.changedFileList.adapter =
            ChangedFileAdapter(git.getStatus().getUncommittedChanges())
        binding.changedFileList.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.changedFileList.addItemDecoration(itemDecoration)
      } catch (e: Exception) {
        showShortToast(requireContext(), e.message.toString())
      }
    }

    try {
      val commitsSize = git.getCommitsForCurrentBranch().size
      if (commitsSize != 0) {
        setPublishButtonEnabled(true)
        binding.commitList.adapter = CommitListAdapter(git.getCommitsForCurrentBranch())
        binding.commitList.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.commitList.addItemDecoration(itemDecoration)
      } else setPublishButtonEnabled(false)
    } catch (e: Exception) {
      showShortToast(requireContext(), e.message.toString())
      setPublishButtonEnabled(false)
    }

    binding.commitChanges.setOnClickListener {
      if (binding.commitMessage.text.isEmpty()) {
        showShortToast(requireContext(), "Empty")
      } else {
        val dialog =
            ProgressDialog.create(requireContext())
                .setTitle("Committing changes")
                .setLoadingMessage("Please wait..")
                .create()
                .apply { setCancelable(false) }

        dialog.show()
        CoroutineScope(Dispatchers.IO).launch {
          try {
            git.getStatus().getUncommittedChanges().forEach { git.add(it) }
            git.commitChanges(binding.commitMessage.text.toString())
          } catch (e: Exception) {
            withContext(Dispatchers.Main) { showShortToast(requireContext(), e.message.toString()) }
          } finally {
            withContext(Dispatchers.Main) { dialog.cancel() }
          }
        }
      }
    }

    binding.publish.setOnClickListener {
      val dialog =
          ProgressDialog.create(requireContext())
              .setTitle("Pushing changes")
              .setLoadingMessage("Please wait..")
              .create()
              .apply { setCancelable(false) }

      dialog.show()
      CoroutineScope(Dispatchers.IO).launch {
        try {
          // git.addRemoteOrigin("https://github.com/itsvks19/tes.git")
          git.pushToOrigin(git.getCurrentBranchName(), username, password)
        } catch (e: Exception) {
          withContext(Dispatchers.Main) { showShortToast(requireContext(), e.message.toString()) }
        } finally {
          withContext(Dispatchers.Main) { dialog.cancel() }
        }
      }
    }

    binding.resetAll.setOnClickListener { showMsg() }

    binding.refresh.setOnClickListener { showMsg() }

    binding.settings.setOnClickListener { showMsg() }

    binding.stageAll.setOnClickListener { showMsg() }
  }

  fun showCommitCard(show: Boolean) {
    binding.commitMessageCard.visibility = if (show) View.VISIBLE else View.GONE
    binding.reviewChangesCard.visibility = if (show) View.VISIBLE else View.GONE
    binding.commitChanges.visibility = if (show) View.VISIBLE else View.GONE
  }

  fun setPublishButtonEnabled(enabled: Boolean) {
    binding.publish.isEnabled = enabled
  }

  fun showMsg() {
    showShortToast(requireContext(), "Will be implemented soon.")
  }
}
