package com.raredev.vcspace.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.lifecycle.ViewModelProvider;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.Action;
import com.raredev.vcspace.actions.ActionData;
import com.raredev.vcspace.actions.ActionManager;
import com.raredev.vcspace.databinding.ActivityMainBinding;
import com.raredev.vcspace.managers.SettingsManager;
import com.raredev.vcspace.ui.editor.CodeEditorView;
import com.raredev.vcspace.ui.editor.Symbol;
import com.raredev.vcspace.ui.viewmodel.EditorViewModel;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.ILogger;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.Utils;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.widget.CodeEditor;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends VCSpaceActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener,
        NavigationView.OnNavigationItemSelectedListener {
  protected final String LOG_TAG = MainActivity.class.getSimpleName();
  public ActivityMainBinding binding;

  public EditorViewModel viewModel;

  public ActivityResultLauncher<Intent> launcher;
  public ActivityResultLauncher<String> createFile;
  public ActivityResultLauncher<String> pickFile;

  @Override
  public View getLayout() {
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onCreate() {
    setSupportActionBar(binding.toolbar);
    binding.navEnd.setNavigationItemSelectedListener(this);
    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar, R.string.open, R.string.close);
    binding.drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    viewModel = new ViewModelProvider(this).get(EditorViewModel.class);
    viewModel.removeAllFiles();

    binding.tabLayout.addOnTabSelectedListener(
        new TabLayout.OnTabSelectedListener() {
          @Override
          public void onTabUnselected(TabLayout.Tab p1) {}

          @Override
          public void onTabReselected(TabLayout.Tab p1) {
            ActionData data = new ActionData();
            data.put(MainActivity.class, MainActivity.this);

            PopupMenu pm = new PopupMenu(MainActivity.this, p1.view);
            ActionManager.getInstance().fillMenu(pm.getMenu(), data, Action.Location.EDITOR);

            p1.view.setOnTouchListener(pm.getDragToOpenListener());
            pm.show();
          }

          @Override
          public void onTabSelected(TabLayout.Tab p1) {
            int position = p1.getPosition();
            CodeEditorView editor = getEditorAtIndex(position);
            viewModel.setCurrentFile(position, editor.getFile());

            binding.searcher.bindEditor(editor);
            refreshSymbolInput(editor);
            invalidateOptionsMenu();
          }
        });

    getLifecycle().addObserver(new LifecyclerObserver());
    ThemeRegistry.getInstance().setTheme(Utils.isDarkMode(this) ? "vcspace_dark" : "vcspace_light");
    registerResultActivity();
    observeViewModel();
  }

  @Override
  protected void onResume() {
    super.onResume();
    invalidateOptionsMenu();
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
    if (key.equals(SettingsManager.KEY_EDITOR_TAB_SIZE)) {
      refreshSymbolInput(getCurrentEditor());
    }
    for (int i = 0; i < viewModel.getOpenedFileCount(); i++) {
      CodeEditorView editor = getEditorAtIndex(i);
      if (editor != null) {
        editor.onSharedPreferenceChanged(key);
      }
    }
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    menu.clear();
    ActionData data = new ActionData();
    data.put(MainActivity.class, this);

    ActionManager.getInstance().fillMenu(menu, data, Action.Location.MAIN_TOOLBAR);
    return true;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (menu instanceof MenuBuilder) {
      ((MenuBuilder) menu).setOptionalIconsVisible(true);
    }
    return true;
  }

  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    int id = item.getItemId();
    switch (id) {
      case R.id.menu_viewlogs:
        saveAllFiles(false);
        startActivity(new Intent(getApplicationContext(), LogViewActivity.class));
        break;
      case R.id.menu_settings:
        saveAllFiles(false);
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        break;
    }
    binding.drawerLayout.closeDrawers();
    return true;
  }

  @Override
  public void onBackPressed() {
    if (binding.drawerLayout.isOpen()) {
      binding.drawerLayout.closeDrawers();
      return;
    }
    if (binding.searcher.isShowing) {
      binding.searcher.showAndHide();
      return;
    }
    saveAllFiles(false);
    super.onBackPressed();
  }

  private void registerResultActivity() {
    launcher =
        registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
              if (result.getResultCode() == RESULT_OK) {
                Uri uri = result.getData().getData();
                try {
                  OutputStream outputStream = getContentResolver().openOutputStream(uri);
                  outputStream.write(getCurrentEditor().getText().toString().getBytes());
                  outputStream.close();
                  openFile(FileUtil.getFileFromUri(MainActivity.this, uri));
                } catch (IOException e) {
                  ILogger.error(LOG_TAG, Log.getStackTraceString(e));
                }
              }
            });
    createFile =
        registerForActivityResult(
            new ActivityResultContracts.CreateDocument("text/*"),
            uri -> {
              if (uri != null) {
                try {
                  openFile(FileUtil.getFileFromUri(this, uri));
                } catch (IOException e) {
                  ILogger.error(LOG_TAG, Log.getStackTraceString(e));
                }
              }
            });
    pickFile =
        registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
              if (uri != null) {
                try {
                  openFile(FileUtil.getFileFromUri(this, uri));
                } catch (IOException e) {
                  ILogger.error(LOG_TAG, Log.getStackTraceString(e));
                }
              }
            });
  }

  private void observeViewModel() {
    viewModel.observeFiles(
        this,
        files -> {
          if (files.isEmpty()) {
            PreferencesUtils.getDefaultPrefs().unregisterOnSharedPreferenceChangeListener(this);
            binding.tabLayout.setVisibility(View.GONE);
            binding.layout.setVisibility(View.GONE);
            binding.noFileOpened.setVisibility(View.VISIBLE);
            binding.searcher.hide();
          } else {
            PreferencesUtils.getDefaultPrefs().registerOnSharedPreferenceChangeListener(this);
            binding.tabLayout.setVisibility(View.VISIBLE);
            binding.layout.setVisibility(View.VISIBLE);
            binding.noFileOpened.setVisibility(View.GONE);
          }
        });

    viewModel.getDisplayedFile().observe(this, index -> binding.container.setDisplayedChild(index));
  }

  private void refreshSymbolInput(CodeEditor editor) {
    binding.symbolInput.setSymbols(Symbol.baseSymbols());
    binding.symbolInput.bindEditor(editor);
  }

  public void openFile(File file) {
    binding.drawerLayout.closeDrawers();
    if (file == null) {
      return;
    }
    if (!file.isFile() && !file.exists()) {
      return;
    }
    int index = openFileAndGetIndex(file);
    setCurrent(index);
    /*if (file.getAbsolutePath().endsWith(".java")) {
      try {
        new LspConnector(file)
            .connectToLanguageServer(
                getCurrentEditor(),
                getCurrentEditor().createLanguage(),
                ".java",
                JavaLanguageServerService.class);
      } catch (IOException e) {
        ILogger.error(LOG_TAG, Log.getStackTraceString(e));
      }
    }*/
  }

  private int openFileAndGetIndex(File file) {
    int openedFileIndex = findIndexOfEditorByFile(file);
    if (openedFileIndex != -1) {
      return openedFileIndex;
    }
    int index = viewModel.getOpenedFileCount();

    ILogger.info(LOG_TAG, "Opening file: " + file.toString() + " at index: " + index);

    CodeEditorView editor = new CodeEditorView(this, file);
    editor.subscribeContentChangeEvent(() -> invalidateOptionsMenu());
    binding.container.addView(editor);

    binding.tabLayout.addTab(binding.tabLayout.newTab().setText(file.getName()));
    viewModel.addFile(file);
    return index;
  }

  public void closeFile(int index) {
    if (index >= 0 && index < viewModel.getOpenedFileCount()) {
      ILogger.info(LOG_TAG, "Closing file: " + viewModel.getOpenedFiles().get(index).toString());
      CodeEditorView editor = getEditorAtIndex(index);
      if (editor != null) editor.release();
      // LspConnector.shutdown(editor, viewModel.getCurrentFile());

      viewModel.removeFile(index);
      binding.tabLayout.removeTabAt(index);
      binding.container.removeViewAt(index);
    }
    binding.tabLayout.requestLayout();
  }

  public void closeOthers() {
    File file = viewModel.getCurrentFile();
    int index = 0;

    while (viewModel.getOpenedFileCount() != 1) {
      CodeEditorView editor = getEditorAtIndex(index);

      if (editor != null) {
        if (file != editor.getFile()) {
          closeFile(index);
        } else {
          index = 1;
        }
      }
    }
    int size = viewModel.getOpenedFileCount() - 1;
    viewModel.setCurrentFile(size, file);
    setCurrent(size);
  }

  public void closeAllFiles() {
    if (viewModel.getOpenedFiles().isEmpty()) {
      return;
    }
    for (int i = 0; i < viewModel.getOpenedFileCount(); i++) {
      CodeEditorView editor = getEditorAtIndex(i);
      if (editor != null) {
        editor.release();
      }
    }

    viewModel.removeAllFiles();
    binding.tabLayout.removeAllTabs();
    binding.tabLayout.requestLayout();
    binding.container.removeAllViews();
  }

  public void saveAllFiles(boolean showMsg) {
    if (!viewModel.getOpenedFiles().isEmpty()) {
      for (int i = 0; i < viewModel.getOpenedFileCount(); i++) {
        getEditorAtIndex(i).save();
      }

      if (showMsg) {
        ToastUtils.showShort(R.string.saved_files);
      }
    }
  }

  public void onFileDeleted() {
    for (int i = 0; i < viewModel.getOpenedFileCount(); i++) {
      File openedFile = viewModel.getOpenedFiles().get(i);
      if (!openedFile.exists()) {
        closeFile(i);
      }
    }
  }

  public int findIndexOfEditorByFile(File file) {
    for (int i = 0; i < viewModel.getOpenedFileCount(); i++) {
      File openedFile = viewModel.getOpenedFiles().get(i);
      if (openedFile.getAbsolutePath().equals(file.getAbsolutePath())) {
        return i;
      }
    }
    return -1;
  }

  public CodeEditorView getEditorAtIndex(int index) {
    return (CodeEditorView) binding.container.getChildAt(index);
  }

  public CodeEditorView getCurrentEditor() {
    return (CodeEditorView) binding.container.getChildAt(viewModel.getCurrentFileIndex());
  }

  private void setCurrent(int index) {
    final var tab = binding.tabLayout.getTabAt(index);
    if (tab != null && index >= 0 && !tab.isSelected()) {
      tab.select();
    }
  }

  @Override
  protected void onDestroy() {
    // LspConnector.shutdown();
    super.onDestroy();
  }
}
