package com.raredev.vcspace.activity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.tabs.TabLayout;
import com.raredev.common.task.TaskExecutor;
import com.raredev.common.util.DialogUtils;
import com.raredev.common.util.Utils;
import com.raredev.vcspace.R;
import com.raredev.vcspace.SimpleExecuter;
import com.raredev.vcspace.databinding.ActivityMainBinding;
import com.raredev.vcspace.ui.editor.CodeEditorView;
import com.raredev.vcspace.ui.editor.EditorViewModel;
import com.raredev.vcspace.ui.editor.manager.EditorManager;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;
import io.github.rosemoe.sora.widget.CodeEditor;
import java.io.File;
import org.eclipse.tm4e.core.registry.IThemeSource;

public class MainActivity extends VCSpaceActivity {
  private ActivityMainBinding binding;

  private EditorViewModel viewModel;
  private EditorManager editorManager;

  private MenuItem undo;
  private MenuItem redo;

  public final Runnable updateMenuItem = () -> updateUndoAndRedo();

  @Override
  public View getLayout() {
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onCreate() {
    setSupportActionBar(binding.toolbar);

    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar, R.string.open, R.string.close);
    binding.drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    viewModel = new ViewModelProvider(this).get(EditorViewModel.class);
    editorManager = new EditorManager(MainActivity.this, binding, viewModel);
    binding.tabLayout.addOnTabSelectedListener(
        new TabLayout.OnTabSelectedListener() {
          @Override
          public void onTabUnselected(TabLayout.Tab p1) {}

          @Override
          public void onTabReselected(TabLayout.Tab p1) {
            showPopupMenu(p1.view, viewModel.getCurrentPosition());
          }

          @Override
          public void onTabSelected(TabLayout.Tab p1) {
            int position = p1.getPosition();
            CodeEditorView editor = editorManager.getEditorAtIndex(position);
            viewModel.setCurrentPosition(position, editor.getFile());

            binding.symbolInput.bindEditor(editor.getEditor());
            binding.searcher.bindEditor(editor.getEditor());
            invalidateOptionsMenu();
          }
        });

    viewModel
        .getFiles()
        .observe(
            this,
            (files) -> {
              if (files.isEmpty()) {
                binding.tabLayout.setVisibility(View.GONE);
                binding.layout.setVisibility(View.GONE);
                binding.noFileOpened.setVisibility(View.VISIBLE);
                binding.searcher.hide();
              } else {
                binding.tabLayout.setVisibility(View.VISIBLE);
                binding.layout.setVisibility(View.VISIBLE);
                binding.noFileOpened.setVisibility(View.GONE);
              }
            });
    viewModel
        .getCurrentPositionPair()
        .observe(
            this,
            (pair) -> {
              binding.container.setDisplayedChild(pair.first);
            });

    viewModel
        .getCurrentPositionPair()
        .observe(
            this,
            (pair) -> {
              binding.container.setDisplayedChild(pair.first);
            });

    initialize();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    undo = menu.findItem(R.id.menu_undo);
    redo = menu.findItem(R.id.menu_redo);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    if (!viewModel.getFiles().getValue().isEmpty()) {
      menu.findItem(R.id.menu_save).setEnabled(true);
      menu.findItem(R.id.menu_undo).setVisible(true);
      menu.findItem(R.id.menu_redo).setVisible(true);
      menu.findItem(R.id.menu_editor).setVisible(true);

      File file = viewModel.getCurrentFile();
      if (file != null) {
        menu.findItem(R.id.menu_compile).setVisible(SimpleExecuter.isExecutable(file));
      }
      updateMenuItem.run();
    } else {
      menu.findItem(R.id.menu_save).setEnabled(false);
      menu.findItem(R.id.menu_undo).setVisible(false);
      menu.findItem(R.id.menu_redo).setVisible(false);
      menu.findItem(R.id.menu_compile).setVisible(false);
      menu.findItem(R.id.menu_editor).setVisible(false);
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    final CodeEditorView editor = editorManager.getCurrentEditor();
    switch (id) {
      case R.id.menu_undo:
        editor.undo();
        break;
      case R.id.menu_redo:
        editor.redo();
        break;
      case R.id.menu_save:
        editorManager.saveAllFiles(true);
        break;
      case R.id.menu_compile:
        editorManager.saveAllFiles(false);
        new SimpleExecuter(this, viewModel.getCurrentFile());
        break;
      case R.id.menu_format:
        editor.getEditor().formatCodeAsync();
        break;
      case R.id.menu_search:
        binding.searcher.showAndHide();
        break;
      case R.id.menu_settings:
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        break;
      case R.id.menu_terminal:
        ToastUtils.showShort("unavailable");
        // startActivity(new Intent(getApplicationContext(), TerminalActivity.class));
        break;
    }
    return true;
  }

  @Override
  public void onBackPressed() {
    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
      binding.drawerLayout.closeDrawer(GravityCompat.START);
      return;
    }
    if (binding.searcher.isShowing) {
      binding.searcher.hide();
      return;
    }
    editorManager.saveAllFiles(false);
    super.onBackPressed();
  }

  public EditorManager getEditorManager() {
    return editorManager;
  }

  private synchronized void initialize() {
    TaskExecutor.executeAsyncProvideError(
        () -> {
          loadTextMate();
          return null;
        },
        (result, error) -> {
          if (error != null) {
            DialogUtils.newErrorDialog(this, getString(R.string.error), error.toString());
          }
          editorManager.tryOpenFileFromIntent(getIntent());
        });
  }

  private void updateUndoAndRedo() {
    CodeEditorView editor = editorManager.getCurrentEditor();
    if (editor != null) {
      undo.setEnabled(editor.getEditor().canUndo());
      redo.setEnabled(editor.getEditor().canRedo());
    }
  }

  private void showPopupMenu(View v, int pos) {
    PopupMenu pm = new PopupMenu(this, v);
    pm.getMenu().add(R.string.close);
    pm.getMenu().add(R.string.close_others);
    pm.getMenu().add(R.string.close_all);
    pm.setOnMenuItemClickListener(
        item -> {
          var title = item.getTitle();
          if (title == getString(R.string.close)) {
            editorManager.closeFile(pos);
          } else if (title == getString(R.string.close_others)) {
            editorManager.closeOthers();
          } else if (title == getString(R.string.close_all)) {
            editorManager.closeAllFiles();
          }
          invalidateOptionsMenu();
          return true;
        });
    pm.show();
  }

  private void loadTextMate() throws Exception {
    // Load editor themes
    FileProviderRegistry.getInstance().addFileProvider(new AssetsFileResolver(getAssets()));

    String[] themes = new String[] {"darcula", "quietlight"};
    ThemeRegistry themeRegistry = ThemeRegistry.getInstance();
    for (String name : themes) {
      String path = "textmate/" + name + ".json";
      themeRegistry.loadTheme(
          new ThemeModel(
              IThemeSource.fromInputStream(
                  FileProviderRegistry.getInstance().tryGetInputStream(path), path, null),
              name));
    }
    // Load editor languages
    GrammarRegistry.getInstance().loadGrammars("textmate/languages.json");
    // Register current theme
    ThemeRegistry.getInstance().setTheme(Utils.isDarkMode(this) ? "darcula" : "quietlight");
  }
}
