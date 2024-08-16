package com.raredev.vcspace.activities

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.R.attr
import com.raredev.vcspace.databinding.ActivityPreferencesBinding
import com.raredev.vcspace.utils.getAttrColor

class PreferencesActivity : BaseActivity() {

  private var _binding: ActivityPreferencesBinding? = null
  private val binding: ActivityPreferencesBinding
    get() = checkNotNull(_binding)

  override val navigationBarDividerColor: Int
    get() = getAttrColor(attr.colorOutlineVariant)

  override fun getLayout(): View {
    _binding = ActivityPreferencesBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setSupportActionBar(binding.toolbar)

    val navHostFragment =
      supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
    NavigationUI.setupWithNavController(binding.toolbar, navHostFragment.navController)
  }

  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }
}
