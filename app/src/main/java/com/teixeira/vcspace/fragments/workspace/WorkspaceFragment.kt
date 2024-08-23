package com.teixeira.vcspace.fragments.workspace

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.teixeira.vcspace.R
import com.teixeira.vcspace.activities.PreferencesActivity
import com.teixeira.vcspace.activities.TerminalActivity
import com.teixeira.vcspace.databinding.FragmentWorkspaceBinding
import com.teixeira.vcspace.ui.workspace.configureNavigationRailBackground

class WorkspaceFragment : Fragment(), NavController.OnDestinationChangedListener {

  private var _binding: FragmentWorkspaceBinding? = null
  private val binding: FragmentWorkspaceBinding
    get() = checkNotNull(_binding)

  private val navController by lazy {
    (childFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment)
      .navController
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    _binding = FragmentWorkspaceBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    navController.addOnDestinationChangedListener(this)
    NavigationUI.setupWithNavController(binding.navRail, navController)
    binding.navRail.setOnItemSelectedListener { item ->
      when (item.itemId) {
        R.id.menu_terminal -> startActivity(Intent(requireContext(), TerminalActivity::class.java))
        R.id.menu_preferences ->
          startActivity(Intent(requireContext(), PreferencesActivity::class.java))
      }
      false
    }
    binding.navRail.configureNavigationRailBackground()
  }

  override fun onDestinationChanged(
    controller: NavController,
    destination: NavDestination,
    arguments: Bundle?,
  ) {
    val children = binding.navRail.menu.children
    val menuItem = children.find { item -> destination.id == item.itemId }
    menuItem?.let {
      binding.title.text = it.title
      it.isChecked = true
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    navController.removeOnDestinationChangedListener(this)
    _binding = null
  }
}
