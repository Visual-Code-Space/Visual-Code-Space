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

package com.teixeira.vcspace.activities.editor

import android.animation.LayoutTransition
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ThreadUtils
import com.teixeira.vcspace.activities.BaseActivity
import com.teixeira.vcspace.databinding.ActivityEditorBinding
import com.teixeira.vcspace.events.OnPreferenceChangeEvent
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.ui.workspace.configureNavigationViewBackground
import com.teixeira.vcspace.utils.cancelIfActive
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.greenrobot.eventbus.EventBus

/**
 * Base class for EditorActivity. which handles most activity related stuff.
 *
 * @author Felipe Teixeira, Vivek
 */
abstract class BaseEditorActivity :
  BaseActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

  private var optionsMenuInvalidator: Runnable? = null
  private var _binding: ActivityEditorBinding? = null
  private var _isDestroying: Boolean = false

  private val onBackPressedCallback: OnBackPressedCallback =
    object : OnBackPressedCallback(false) {
      override fun handleOnBackPressed() {
        closeWorkspace()
      }
    }

  protected val coroutineScope = CoroutineScope(Dispatchers.Default)

  val binding: ActivityEditorBinding
    get() = checkNotNull(_binding) { "Activity has been destroyed" }

  val isDestroying: Boolean
    get() = _isDestroying

  override fun getLayout(): View {
    _binding = ActivityEditorBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setSupportActionBar(binding.toolbar)
    optionsMenuInvalidator = Runnable {
//      super.invalidateOptionsMenu()
    }

    onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    KeyboardUtils.registerSoftInputChangedListener(this) { invalidateOptionsMenu() }
    app.defaultPrefs.registerOnSharedPreferenceChangeListener(this)
    EventBus.getDefault().register(this)

    configureWorkspace()
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
    app.defaultPrefs.unregisterOnSharedPreferenceChangeListener(this)
    EventBus.getDefault().unregister(this)
  }

  protected open fun postDestroy() {
    coroutineScope.cancelIfActive("Activity has been destroyed!")

    optionsMenuInvalidator?.also { ThreadUtils.getMainHandler().removeCallbacks(it) }
    optionsMenuInvalidator = null
    _binding = null
  }

  protected fun closeWorkspace() {
    closeWorkspaceDrawer()
    closeWorkspaceLayout()
  }

  protected fun closeWorkspaceDrawer() {
    if (binding.root is DrawerLayout) {
      val drawerLayout = binding.root as DrawerLayout
      if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
        drawerLayout.closeDrawer(GravityCompat.START)
      }
    }
  }

  protected fun closeWorkspaceLayout() {
    if (binding.root is ConstraintLayout) {
      setWorkspaceLayoutVisible(false)
    }
  }

  private fun configureWorkspace() {
    binding.navWorkspace.configureNavigationViewBackground()
    when (binding.root) {
      is DrawerLayout -> configureWorkspaceDrawer(binding.root as DrawerLayout)
      is ConstraintLayout -> configureWorkspaceLayout(binding.root as ConstraintLayout)
      else -> throw IllegalStateException("The workspace layout is invalid")
    }
  }

  private fun configureWorkspaceDrawer(drawerLayout: DrawerLayout) {
    val drawerToggle =
      ActionBarDrawerToggle(this, drawerLayout, binding.toolbar, R.string.open, R.string.close)
    drawerToggle.syncState()
    drawerLayout.apply {
      addDrawerListener(drawerToggle)
      addDrawerListener(
        object : DrawerLayout.SimpleDrawerListener() {
          override fun onDrawerSlide(view: View, slideOffset: Float) {
            binding.main.translationX = view.width * slideOffset * 0.98f
            binding.main.rotation = slideOffset / 2
          }

          override fun onDrawerStateChanged(state: Int) {
            onBackPressedCallback.isEnabled = !(state == DrawerLayout.STATE_IDLE)
          }
        }
      )
      setScrimColor(Color.TRANSPARENT)
    }
  }

  // New layout features under development for devices with dpi 600 or higher

  private fun configureWorkspaceLayout(layout: ConstraintLayout) {
    binding.toolbar.setNavigationIcon(R.drawable.ic_drawer_toggle)
    binding.toolbar.setNavigationOnClickListener {
      setWorkspaceLayoutVisible(!onBackPressedCallback.isEnabled)
    }
    layout.setLayoutTransition(
      LayoutTransition().apply { enableTransitionType(LayoutTransition.CHANGING) }
    )
    onBackPressedCallback.isEnabled = true
  }

  private fun setWorkspaceLayoutVisible(visible: Boolean) {
    if (onBackPressedCallback.isEnabled == visible) {
      return
    }

    val workspaceWidth = binding.navWorkspace.width
    val translationX = if (visible) 0f else -workspaceWidth.toFloat()
    binding.navWorkspace.animate().translationX(translationX).start()

    val constraintLayout = binding.root as ConstraintLayout
    val constraintSet = ConstraintSet()
    constraintSet.clone(constraintLayout)
    if (visible) {
      constraintSet.connect(
        binding.main.id,
        ConstraintSet.START,
        binding.navWorkspace.id,
        ConstraintSet.END,
      )
    } else {
      constraintSet.connect(
        binding.main.id,
        ConstraintSet.START,
        ConstraintSet.PARENT_ID,
        ConstraintSet.START,
      )
    }
    constraintSet.applyTo(constraintLayout)
    binding.main.requestLayout()

    onBackPressedCallback.isEnabled = visible
  }

  companion object {
    private const val OPTIONS_MENU_INVALIDATION_DELAY = 150L
  }
}
