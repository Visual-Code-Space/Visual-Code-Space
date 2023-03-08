package com.raredev.vcspace.activity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.tabs.TabLayout;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.ActivityMainBinding;
import com.raredev.vcspace.fragments.callback.FileManagerCallBack;
import com.raredev.vcspace.ui.editor.EditorViewModel;
import com.raredev.vcspace.ui.editor.action.*;
import com.raredev.vcspace.ui.editor.language.html.ExecuteHtml;
import com.raredev.vcspace.ui.editor.manager.EditorManager;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;
import java.io.File;
import org.eclipse.tm4e.core.registry.IThemeSource;

public class MainActivity extends VCSpaceActivity implements FileManagerCallBack {
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
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(false);

    binding.toolbar.setNavigationIcon(R.drawable.ic_menu);
    binding.toolbar.setNavigationOnClickListener(
        v -> {
          if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            viewModel.setDrawerState(false);
          } else {
            viewModel.setDrawerState(true);
          }
        });

    editorManager = new EditorManager(MainActivity.this, binding.container, binding.tabLayout);
    viewModel = editorManager.getViewModel();

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
    viewModel
        .getDrawerState()
        .observe(
            this,
            (state) -> {
              if (state) {
                binding.drawerLayout.open();
              } else {
                binding.drawerLayout.close();
              }
            });
    try {
      loadDefaultThemes();
      loadDefaultLanguages();
    } catch (Exception e) {
      e.printStackTrace();
    }
    loadShortcuts();
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
        editorManager.saveAllFiles(true);
        break;
      case R.id.menu_compile:
        // editor.saveAllFilesAndTabs(true);
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
    if (viewModel.getDrawerState().getValue()) {
      viewModel.setDrawerState(false);
      return;
    }
    editorManager.saveAllFiles(false);
    super.onBackPressed();
  }

  @Override
  public void onFileClicked(File file) {
    editorManager.openFile(file);
  }

  @Override
  public void onFileRenamed(File old, File file) {}

  @Override
  public void onFileDeleted() {
    // editor.onFileDeleted();
    // onTabRemoved();
  }

  private void loadShortcuts() {
    binding.shortcuts.setBackgroundColor(SurfaceColors.SURFACE_0.getColor(this));
    binding.shortcuts.removeSymbols();

    binding.shortcuts.addSymbols(
        new String[] {"→", "\"", ";", "(", ")", "{", "}", "[", "]", "<", ">"},
        new String[] {"    ", "\"", ";", "(", ")", "{", "}", "[", "]", "<", ">"});
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
          invalidateOptionsMenu();
          return true;
        });
    pm.show();
  }

  private void loadDefaultThemes() throws Exception {
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
    ThemeRegistry.getInstance().setTheme("darcula");
  }

  private void loadDefaultLanguages() {
    GrammarRegistry.getInstance().loadGrammars("textmate/languages.json");
  }
}
