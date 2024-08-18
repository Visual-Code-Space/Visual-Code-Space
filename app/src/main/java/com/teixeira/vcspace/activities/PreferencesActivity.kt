package com.teixeira.vcspace.activities

import android.os.Bundle
import android.view.View
import com.google.android.material.R.attr
import com.teixeira.vcspace.preferences.databinding.ActivityPreferencesBinding
import com.teixeira.vcspace.preferences.fragments.PreferencesFragment
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.utils.getAttrColor

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
    supportActionBar?.setTitle(R.string.settings)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

    val fragmentManager = supportFragmentManager
    if (fragmentManager.findFragmentByTag(PreferencesFragment.FRAGMENT_TAG) == null) {
      supportFragmentManager
        .beginTransaction()
        .replace(binding.container.id, PreferencesFragment(), PreferencesFragment.FRAGMENT_TAG)
        .commit()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }
}
