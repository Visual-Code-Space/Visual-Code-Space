package com.raredev.vcspace.activities

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.raredev.vcspace.databinding.ActivitySettingsBinding
import com.raredev.vcspace.fragments.SettingsFragment

class SettingsActivity: BaseActivity() {

  private var _binding: ActivitySettingsBinding? = null
  private val binding: ActivitySettingsBinding
    get() = checkNotNull(_binding)

  override fun getLayout(): View {
    _binding = ActivitySettingsBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setSupportActionBar(binding.toolbar)

    val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
    NavigationUI.setupWithNavController(
      binding.toolbar, navHostFragment.navController
    )
  }

  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }
}
