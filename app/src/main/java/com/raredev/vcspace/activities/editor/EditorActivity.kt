package com.raredev.vcspace.activities.editor

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.view.menu.MenuBuilder
import com.blankj.utilcode.util.KeyboardUtils
import com.raredev.vcspace.R
import com.raredev.vcspace.res.R.string

class EditorActivity: BaseEditorActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    supportActionBar?.title = "VCSpace"

    KeyboardUtils.registerSoftInputChangedListener(this) { invalidateOptionsMenu() }
    configureDrawer()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_editor_activity, menu)
    if (menu is MenuBuilder) {
      menu.setOptionalIconsVisible(true)
    }
    return super.onCreateOptionsMenu(menu)
  }

  override fun onPrepareOptionsMenu(menu: Menu): Boolean {
    val editor = getSelectedEditor()
    if (editor != null) {
      menu.findItem(R.id.menu_execute).isVisible = true
      menu.findItem(R.id.menu_editor).isVisible = true
      menu.findItem(R.id.menu_undo).isVisible = KeyboardUtils.isSoftInputVisible(this)
      menu.findItem(R.id.menu_redo).isVisible = KeyboardUtils.isSoftInputVisible(this)
      menu.findItem(R.id.menu_undo).isEnabled = editor.editor.canUndo()
      menu.findItem(R.id.menu_redo).isEnabled = editor.editor.canRedo()
      menu.findItem(R.id.menu_save).isEnabled = editor.modified
      menu.findItem(R.id.menu_save_as).isEnabled = true
      menu.findItem(R.id.menu_save_all).isEnabled = getUnsavedFilesCount() > 0
      menu.findItem(R.id.menu_reload).isEnabled = true
    }
    return super.onPrepareOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val editor = getSelectedEditor()
    when (item.itemId) {
      R.id.menu_execute -> {}
      R.id.menu_undo -> editor?.editor?.undo()
      R.id.menu_redo -> editor?.editor?.redo()
      R.id.menu_save -> saveFile(true)
      R.id.menu_save_all -> saveAll(true)
    }
    return true
  }

  fun configureDrawer() {
    val drawerToggle = ActionBarDrawerToggle(
      this, binding.drawerLayout, binding.toolbar, string.open, string.close)
    binding.drawerLayout.addDrawerListener(drawerToggle)
    drawerToggle.syncState()

    binding.drawerLayout.addDrawerListener(object: DrawerLayout.SimpleDrawerListener() {
      override fun onDrawerSlide(view: View, slideOffset: Float) {
        binding.main.setTranslationX(view.getWidth() * slideOffset / 2)
      }
      override fun onDrawerStateChanged(state: Int) {}
      override fun onDrawerClosed(view: View) {}
      override fun onDrawerOpened(view: View) {}
    })
  }
  
  private fun getUnsavedFilesCount(): Int {
    var count = 0
    for (i in 0 until viewModel.getOpenedFiles().size) {
      if (getEditorAt(i)?.modified ?: false) count++
    }
    return count
  }
}
