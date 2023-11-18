package com.raredev.vcspace.fragments.workspace

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.raredev.vcspace.R
import com.raredev.vcspace.activities.SettingsActivity
import com.raredev.vcspace.activities.TerminalActivity
import com.raredev.vcspace.app.BaseApplication
import com.raredev.vcspace.databinding.FragmentWorkspaceBinding
import com.raredev.vcspace.fragments.workspace.explorer.FileExplorerFragment
import com.raredev.vcspace.fragments.workspace.explorer.FileExplorerFragmentDirections
import com.raredev.vcspace.fragments.workspace.explorer.NoOpenedFolderFragment
import com.raredev.vcspace.fragments.workspace.git.GitControlFragment
import com.raredev.vcspace.fragments.workspace.git.GitControlFragmentDirections
import com.raredev.vcspace.fragments.workspace.git.GitManagerFragment
import com.raredev.vcspace.fragments.workspace.git.GitManagerFragmentDirections
import com.raredev.vcspace.utils.GitUtils
import com.raredev.vcspace.utils.showShortToast
import com.raredev.vcspace.viewmodel.FileExplorerViewModel
import java.io.File

class WorkspaceFragment : Fragment(), NavController.OnDestinationChangedListener {

  private val fileViewModel by
      viewModels<FileExplorerViewModel>(ownerProducer = { requireActivity() })

  private var _binding: FragmentWorkspaceBinding? = null
  private val binding: FragmentWorkspaceBinding
    get() = checkNotNull(_binding)

  private lateinit var navController: NavController

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    _binding = FragmentWorkspaceBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val navHostFragment =
        (childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
    val navController = navHostFragment.navController
    NavigationUI.setupWithNavController(binding.navRail, navController)
    navController.addOnDestinationChangedListener(this)

    binding.navRail.setOnItemSelectedListener { item ->
      val child = navHostFragment.childFragmentManager?.fragments?.get(0)
      val openedFolderPath = BaseApplication.getInstance().getPrefs().getString("openedFolder", "")

      when (item.itemId) {
        R.id.fragmentExplorer -> {
          if (child !is FileExplorerFragment &&
              child !is NoOpenedFolderFragment &&
              (child is GitManagerFragment || child is GitControlFragment)) {
            navController.navigate(
                if (child is GitManagerFragment) GitManagerFragmentDirections.actionGoToExplorer()
                else GitControlFragmentDirections.actionGoToExplorer())
            item.isChecked = true
          }
        }
        R.id.menu_git -> {
          if (child is FileExplorerFragment) {
            if (GitUtils.repositoryExists(File(openedFolderPath))) {
              navController.navigate(FileExplorerFragmentDirections.actionGoToGitControl())
            } else {
              navController.navigate(FileExplorerFragmentDirections.actionGoToGitManager())
            }
            item.isChecked = true
          } else if (child !is GitManagerFragment && child !is GitControlFragment) {
            showShortToast(requireContext(), "Open a folder")
          }
        }
        R.id.menu_terminal ->
            TerminalActivity.startTerminalWithDir(requireContext(), fileViewModel.currentPath.value)
        R.id.menu_settings -> startActivity(Intent(requireContext(), SettingsActivity::class.java))
      }
      false
    }
  }

  override fun onDestinationChanged(
      controller: NavController,
      destination: NavDestination,
      arguments: Bundle?
  ) {
    binding.navRail.menu.forEach { item ->
      if (destination.id == item.itemId) {
        binding.title.text = item.title
        item.isChecked = true
      }
    }
  }

  fun setTitle(title: String) {
    binding.title.text = title
  }

  override fun onDestroyView() {
    super.onDestroyView()
    navController.removeOnDestinationChangedListener(this)
    _binding = null
  }
}
