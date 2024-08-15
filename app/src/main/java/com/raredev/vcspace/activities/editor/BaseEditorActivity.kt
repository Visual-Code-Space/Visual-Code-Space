/*
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

package com.raredev.vcspace.activities.editor

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ThreadUtils
import com.raredev.vcspace.activities.BaseActivity
import com.raredev.vcspace.databinding.ActivityEditorBinding
import com.raredev.vcspace.events.OnPreferenceChangeEvent
import com.raredev.vcspace.extensions.cancelIfActive
import com.raredev.vcspace.res.R.string
import com.raredev.vcspace.utils.PreferencesUtils
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.greenrobot.eventbus.EventBus

/**
 * Base class for EditorActivity. which handles most activity related stuff.
 *
 * @author Felipe Teixeira
 */
abstract class BaseEditorActivity :
  BaseActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

  private var optionsMenuInvalidator: Runnable? = null
  private var _binding: ActivityEditorBinding? = null
  private var _isDestroying: Boolean = false

  private val onBackPressedCallback: OnBackPressedCallback =
    object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
          binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
      }
    }

  protected val coroutineScope = CoroutineScope(Dispatchers.Default)

  protected val binding: ActivityEditorBinding
    get() = checkNotNull(_binding) { "Activity has been destroyed" }

  protected val isDestroying: Boolean
    get() = _isDestroying

  override fun getLayout(): View {
    _binding = ActivityEditorBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setSupportActionBar(binding.toolbar)
    optionsMenuInvalidator = Runnable { super.invalidateOptionsMenu() }

    onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    KeyboardUtils.registerSoftInputChangedListener(this) { invalidateOptionsMenu() }
    PreferencesUtils.prefs.registerOnSharedPreferenceChangeListener(this)
    EventBus.getDefault().register(this)
    configureDrawer()
  }

  override fun invalidateOptionsMenu() {
    optionsMenuInvalidator?.also {
      ThreadUtils.getMainHandler().removeCallbacks(it)
      ThreadUtils.getMainHandler().postDelayed(it, OPTIONS_MENU_INVALIDATION_DELAY)
    }
  }

  override fun onSharedPreferenceChanged(prefs: SharedPreferences, prefKey: String?) {
    if (prefKey != null) EventBus.getDefault().post(OnPreferenceChangeEvent(prefKey))
  }

  override fun onDestroy() {
    _isDestroying = true
    preDestroy()
    super.onDestroy()
    postDestroy()
  }

  protected open fun preDestroy() {
    PreferencesUtils.prefs.unregisterOnSharedPreferenceChangeListener(this)
    EventBus.getDefault().unregister(this)
  }

  protected open fun postDestroy() {
    GrammarRegistry.getInstance().dispose()

    coroutineScope.cancelIfActive("Activity has been destroyed!")

    optionsMenuInvalidator?.also { ThreadUtils.getMainHandler().removeCallbacks(it) }
    optionsMenuInvalidator = null
    _binding = null
  }

  private fun configureDrawer() {
    val drawerToggle =
      ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, string.open, string.close)
    binding.drawerLayout.addDrawerListener(drawerToggle)
    drawerToggle.syncState()

    binding.drawerLayout.addDrawerListener(
      object : DrawerLayout.SimpleDrawerListener() {
        override fun onDrawerSlide(view: View, slideOffset: Float) {
          binding.main.translationX = view.width * slideOffset / 2
        }

        override fun onDrawerStateChanged(state: Int) {}

        override fun onDrawerClosed(view: View) {
          onBackPressedCallback.isEnabled = false
        }

        override fun onDrawerOpened(view: View) {
          onBackPressedCallback.isEnabled = true
        }
      }
    )
  }

  companion object {
    private const val OPTIONS_MENU_INVALIDATION_DELAY = 150L
  }
}
