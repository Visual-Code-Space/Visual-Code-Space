package com.raredev.vcspace.activity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.tabs.TabLayout;
import com.raredev.common.task.TaskExecutor;
import com.raredev.common.util.DialogUtils;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.ActivityMainBinding;
import com.raredev.vcspace.ui.editor.EditorViewModel;
import com.raredev.vcspace.ui.editor.action.FormatterAction;
import com.raredev.vcspace.ui.editor.language.html.ExecuteHtml;
import com.raredev.vcspace.ui.editor.manager.EditorManager;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;
import org.eclipse.tm4e.core.registry.IThemeSource;

public class MainActivity extends VCSpaceActivity {
  private ActivityMainBinding binding;

  private EditorViewModel viewModel;
  private EditorManager editorManager;

  @Override
  public void findBinding() {
    binding = ActivityMainBinding.inflate(getLayoutInflater());
  }

  @Override
  public View getLayout() {
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
            viewModel.setCurrentPosition(p1.getPosition());
            updateTab(p1.getPosition());
          }
        });

    editorManager
        .getViewModel()
        .getFiles()
        .observe(
            this,
            (files) -> {
              if (files.isEmpty()) {
                binding.tabLayout.setVisibility(View.GONE);
                binding.layout.setVisibility(View.GONE);
                binding.empty.setVisibility(View.VISIBLE);
              } else {
                binding.tabLayout.setVisibility(View.VISIBLE);
                binding.layout.setVisibility(View.VISIBLE);
                binding.empty.setVisibility(View.GONE);
              }
            });
    initialize();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    if (!viewModel.getFiles().getValue().isEmpty()) {
      menu.findItem(R.id.menu_save).setEnabled(true);
      menu.findItem(R.id.menu_editor).setVisible(true);
    } else {
      menu.findItem(R.id.menu_save).setEnabled(false);
      menu.findItem(R.id.menu_editor).setVisible(false);
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    switch (id) {
      case R.id.menu_undo:
        editorManager.undo();
        break;
      case R.id.menu_redo:
        editorManager.redo();
        break;
      case R.id.menu_save:
        editorManager.saveAll(true);
        break;
      case R.id.menu_compile:
        editorManager.saveAll(false);
        new ExecuteHtml(this, viewModel.getCurrentFile());
        break;
      case R.id.menu_format:
        new FormatterAction(editorManager.getCurrentEditor()).format();
        break;
      case R.id.menu_search:
        break;
      case R.id.menu_settings:
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        break;
    }
    return true;
  }

  @Override
  public void onBackPressed() {
    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
      binding.drawerLayout.close();
      return;
    }
    editorManager.saveAllFiles(false);
    super.onBackPressed();
  }

  public EditorManager getEditorManager() {
    return editorManager;
  }

  private void initialize() {
    binding.progress.setVisibility(View.VISIBLE);
    getSupportActionBar().setSubtitle("Loading..");
    TaskExecutor.executeAsyncProvideError(
        () -> {
          loadTextMate();
          return null;
        },
        (result, error) -> {
          if (error != null) {
            DialogUtils.newErrorDialog(this, "Editor", error.toString());
          }
          getSupportActionBar().setSubtitle(null);
          binding.progress.setVisibility(View.GONE);
          //editorManager.openRecentOpenedFiles();
          loadShortcuts();
        });
  }

  private void updateTab(int pos) {
    binding.container.setDisplayedChild(pos);
    editorManager.getEditorAtIndex(pos).requestFocus();
    binding.shortcuts.bindEditor(editorManager.getEditorAtIndex(pos).getEditor());
    invalidateOptionsMenu();
  }

  private void showPopupMenu(View v, int pos) {
    PopupMenu pm = new PopupMenu(this, v);
    pm.getMenu().add(R.string.close);
    pm.getMenu().add(R.string.close_others);
    pm.getMenu().add(R.string.close_all);
    pm.setOnMenuItemClickListener(
        item -> {
          if (item.getTitle() == getResources().getString(R.string.close)) {
            editorManager.closeFile(pos);
          } else if (item.getTitle() == getResources().getString(R.string.close_others)) {
            editorManager.closeOthers();
          } else if (item.getTitle() == getResources().getString(R.string.close_all)) {
            editorManager.closeAllFiles();
          }
          editorManager.saveOpenedFiles();
          invalidateOptionsMenu();
          return true;
        });
    pm.show();
  }

  private void loadShortcuts() {
    binding.shortcuts.setBackgroundColor(SurfaceColors.SURFACE_0.getColor(this));
    binding.shortcuts.removeSymbols();

    binding.shortcuts.addSymbols(
        new String[] {"→", "\"", ";", "(", ")", "{", "}", "[", "]", "<", ">"},
        new String[] {"    ", "\"", ";", "(", ")", "{", "}", "[", "]", "<", ">"});
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
  }
}
