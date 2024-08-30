package com.teixeira.vcspace.activities

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.R.attr
import com.teixeira.vcspace.R.id
import com.teixeira.vcspace.databinding.ActivityPreferencesBinding
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.utils.getAttrColor

class PreferencesActivity : BaseActivity() {

  private var _binding: ActivityPreferencesBinding? = null
  private val binding: ActivityPreferencesBinding
    get() = checkNotNull(_binding)

  private lateinit var navController: NavController

  override val navigationBarDividerColor: Int
    get() = getAttrColor(attr.colorOutlineVariant)

  override fun getLayout(): View {
    _binding = ActivityPreferencesBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setSupportActionBar(binding.toolbar)

    supportActionBar?.apply {
      setTitle(R.string.settings)
      setDisplayHomeAsUpEnabled(true)
    }

    binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

    val navHostFragment = supportFragmentManager.findFragmentById(
      id.fragment_container_view
    ) as NavHostFragment

    navController = navHostFragment.navController
    setupActionBarWithNavController(navController)
  }

  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }

  override fun onSupportNavigateUp(): Boolean {
    return navController.navigateUp() || super.onSupportNavigateUp()
  }
}
