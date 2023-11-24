package com.raredev.vcspace.activities.editor

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.UriUtils
import com.raredev.vcspace.R
import com.raredev.vcspace.res.R.string
import com.raredev.vcspace.utils.PreferencesUtils

class EditorActivity : BaseEditorActivity() {

  private val onBackPressedCallback: OnBackPressedCallback =
    object : OnBackPressedCallback(false) {
      override fun handleOnBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
          binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
      }
    }

  private val createFile =
    registerForActivityResult(ActivityResultContracts.CreateDocument("text/*")) { uri ->
      if (uri != null) openFile(UriUtils.uri2File(uri))
    }
  private val openFile =
    registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
      if (uri != null) openFile(UriUtils.uri2File(uri))
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    supportActionBar?.title = "VCSpace"

    KeyboardUtils.registerSoftInputChangedListener(this) { invalidateOptionsMenu() }
    configureDrawer()

    onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

    val fileUri: Uri? = intent.data
    if (fileUri != null) openFile(UriUtils.uri2File(fileUri))
  }

  @SuppressLint("RestrictedApi")
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
      menu.findItem(R.id.menu_undo).isEnabled = editor.canUndo()
      menu.findItem(R.id.menu_redo).isEnabled = editor.canRedo()
      menu.findItem(R.id.menu_save).isEnabled = editor.modified && !PreferencesUtils.autoSave
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
      R.id.menu_search -> editor?.beginSearchMode()
      R.id.menu_undo -> editor?.undo()
      R.id.menu_redo -> editor?.redo()
      R.id.menu_new_file -> createFile.launch("filename.txt")
      R.id.menu_open_file -> openFile.launch(arrayOf("text/*"))
      R.id.menu_save -> saveFileAsync(true, viewModel.getSelectedFilePos())
      R.id.menu_save_all -> saveAllFilesAsync(true)
    }
    return true
  }

  private fun configureDrawer() {
    val drawerToggle =
      ActionBarDrawerToggle(
        this, binding.drawerLayout, binding.toolbar, string.open, string.close
      )
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
      })
  }
}
