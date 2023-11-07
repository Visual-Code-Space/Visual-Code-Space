package com.raredev.vcspace.activities.editor

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
      registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
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

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_editor_activity, menu)
    if (menu is MenuBuilder) {
      menu.setOptionalIconsVisible(true)
    }
    return super.onCreateOptionsMenu(menu)
  }

  override fun onPrepareOptionsMenu(menu: Menu): Boolean {
    val editor = getSelectedEditorPanel()
    if (editor != null) {
      menu.findItem(R.id.menu_execute).isVisible = true
      menu.findItem(R.id.menu_editor).isVisible = true
      menu.findItem(R.id.menu_undo).isVisible = KeyboardUtils.isSoftInputVisible(this)
      menu.findItem(R.id.menu_redo).isVisible = KeyboardUtils.isSoftInputVisible(this)
      menu.findItem(R.id.menu_undo).isEnabled = editor.canUndo()
      menu.findItem(R.id.menu_redo).isEnabled = editor.canRedo()
      menu.findItem(R.id.menu_save).isEnabled = editor.isModified()
      menu.findItem(R.id.menu_save_as).isEnabled = true
      menu.findItem(R.id.menu_save_all).isEnabled = getUnsavedFilesCount() > 0
      menu.findItem(R.id.menu_reload).isEnabled = true
    }
    return super.onPrepareOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val editor = getSelectedEditorPanel()
    when (item.itemId) {
      R.id.menu_execute -> {}
      R.id.menu_search -> editor?.beginSearcher()
      R.id.menu_undo -> editor?.undo()
      R.id.menu_redo -> editor?.redo()
      R.id.menu_save -> saveFile(true)
      R.id.menu_save_all -> saveAll(true)
      R.id.menu_new_file -> createFile.launch("filename.txt")
      R.id.menu_open_file -> openFile.launch(arrayOf("text/*"))
    }
    return true
  }

  fun configureDrawer() {
    val drawerToggle =
        ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar, string.open, string.close)
    binding.drawerLayout.addDrawerListener(drawerToggle)
    drawerToggle.syncState()

    binding.drawerLayout.addDrawerListener(
        object : DrawerLayout.SimpleDrawerListener() {
          override fun onDrawerSlide(view: View, slideOffset: Float) {
            binding.main.setTranslationX(view.getWidth() * slideOffset / 2)
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

/*
Para evitar a inflação repetida ao chamar o método `invalidateOptionsMenu` no seu código, você pode adicionar uma verificação para garantir que o método só seja chamado quando necessário. Uma abordagem comum é armazenar uma variável de controle que indica se o menu precisa ser atualizado ou não. Aqui está um exemplo de como você pode fazer isso:

class EditorActivity : BaseEditorActivity() {

  private var menuNeedsUpdate: Boolean = false // Variável de controle

  // ... restante do seu código

  override fun onPrepareOptionsMenu(menu: Menu): Boolean {
    if (menuNeedsUpdate) { // Verifica se o menu precisa ser atualizado
      val editor = getSelectedEditorPanel()
      if (editor != null) {
        // ... seu código existente para atualização do menu

        menuNeedsUpdate = false // Define a variável de controle como false após a atualização
      }
    }
    return super.onPrepareOptionsMenu(menu)
  }

  // ... restante do seu código

  // Em algum lugar do seu código onde você precisa atualizar o menu
  fun updateOptionsMenu() {
    menuNeedsUpdate = true // Define a variável de controle como true
    invalidateOptionsMenu() // Chama o método para atualizar o menu
  }
}

Agora, em vez de chamar `invalidateOptionsMenu` diretamente, você chama o método `updateOptionsMenu`. Isso define a variável de controle como `true`, indicando que o menu precisa ser atualizado na próxima vez que o método `onPrepareOptionsMenu` for chamado. Isso evita inflações repetidas e mantém o menu atualizado apenas quando necessário. Certifique-se de chamar `updateOptionsMenu` nos locais apropriados do seu código onde as atualizações do menu são necessárias.
*/