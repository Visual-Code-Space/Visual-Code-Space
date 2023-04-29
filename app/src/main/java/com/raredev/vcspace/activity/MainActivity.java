package com.raredev.vcspace.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import com.blankj.utilcode.util.KeyboardUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.ActivityMainBinding;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.ui.editor.CodeEditorView;
import com.raredev.vcspace.ui.editor.Symbol;
import com.raredev.vcspace.ui.viewmodel.EditorViewModel;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.ILogger;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.ToastUtils;
import com.raredev.vcspace.util.UniqueNameBuilder;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.ActionManager;
import com.vcspace.actions.location.DefaultLocations;
import io.github.rosemoe.sora.widget.CodeEditor;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener {
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
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setSupportActionBar(binding.toolbar);

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
            PopupMenu pm = new PopupMenu(MainActivity.this, p1.view);

            ActionData data = new ActionData();
            data.put(MainActivity.class, MainActivity.this);
            ActionManager.getInstance().fillMenu(pm.getMenu(), data, DefaultLocations.FILE_TAB);

            pm.show();
          }

          @Override
          public void onTabSelected(TabLayout.Tab p1) {
            var position = p1.getPosition();
            var editor = getEditorAtIndex(position).getEditor();
            viewModel.setCurrentFile(position, editor.getFile());

            binding.searcher.bindEditor(editor);

            saveRecentlyOpenedFiles();
            invalidateOptionsMenu();
          }
        });

    KeyboardUtils.registerSoftInputChangedListener(
        this,
        new KeyboardUtils.OnSoftInputChangedListener() {
          @Override
          public void onSoftInputChanged(int i) {
            if (i > 1 && getCurrentEditor() != null) {
              binding.symbolInput.setVisibility(View.VISIBLE);
              refreshSymbolInput(getCurrentEditor().getEditor());
            } else {
              binding.symbolInput.setVisibility(View.GONE);
              binding.symbolInput.clear();
            }
            invalidateOptionsMenu();
          }
        });

    getLifecycle().addObserver(new LifecyclerObserver());
    registerResultActivity();
    observeViewModel();

    //openRecentlyOpenedFiles();
  }

  @Override
  public void onBackPressed() {
    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
      binding.drawerLayout.closeDrawer(GravityCompat.START);
      return;
    }
    if (binding.searcher.isShowing) {
      binding.searcher.showAndHide();
      return;
    }
    TabLayout.Tab tab = binding.tabLayout.getTabAt(0);
    if (tab != null && !tab.isSelected()) {
      binding.tabLayout.selectTab(tab, true);
      return;
    }
    saveRecentlyOpenedFiles();
    saveAllFiles(false);
    super.onBackPressed();
  }

  @Override
  protected void onStart() {
    super.onStart();
    PreferencesUtils.getDefaultPrefs().registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    PreferencesUtils.getDefaultPrefs().unregisterOnSharedPreferenceChangeListener(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    invalidateOptionsMenu();
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
    for (int i = 0; i < viewModel.getOpenedFileCount(); i++) {
      CodeEditorView editor = getEditorAtIndex(i);
      if (editor != null) {
        editor.getEditor().onSharedPreferenceChanged(key);
      }
    }
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    menu.clear();
    ActionData data = new ActionData();
    data.put(MainActivity.class, this);

    ActionManager.getInstance().fillMenu(menu, data, DefaultLocations.MAIN_TOOLBAR);
    return true;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (menu instanceof MenuBuilder) {
      ((MenuBuilder) menu).setOptionalIconsVisible(true);
    }
    return true;
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
                  outputStream.write(getCurrentEditor().getEditor().getText().toString().getBytes());
                  outputStream.close();
                  openFile(FileUtil.getFileFromUri(this, uri));
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
            binding.editorContainer.setVisibility(View.GONE);
            binding.noFileOpened.setVisibility(View.VISIBLE);
            binding.searcher.hide();

            invalidateOptionsMenu();
          } else {
            binding.editorContainer.setVisibility(View.VISIBLE);
            binding.noFileOpened.setVisibility(View.GONE);
          }
          saveRecentlyOpenedFiles();
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
    if (!file.isFile() || !file.exists()) {
      return;
    }
    int index = openFileAndGetIndex(file);
    setCurrent(index);
  }

  private int openFileAndGetIndex(File file) {
    int openedFileIndex = findIndexOfEditorByFile(file);
    if (openedFileIndex != -1) {
      return openedFileIndex;
    }
    int index = viewModel.getOpenedFileCount();

    CodeEditorView editor = new CodeEditorView(this, file);
    editor.getEditor().subscribeContentChangeEvent(() -> onEditorContentChanged(file));
    binding.container.addView(editor);

    TabLayout.Tab tabItem = binding.tabLayout.newTab();
    tabItem.setText(file.getName());

    binding.tabLayout.addTab(tabItem, index, false);
    viewModel.addFile(file);
    updateTabs();
    return index;
  }

  public void closeFile(int index) {
    if (index >= 0 && index < viewModel.getOpenedFileCount()) {
      CodeEditorView editor = getEditorAtIndex(index);
      if (editor != null) {
        editor.release();
      }

      viewModel.removeFile(index);
      binding.tabLayout.removeTabAt(index);
      binding.container.removeViewAt(index);
      updateTabs();
    }
    binding.tabLayout.requestLayout();
  }

  public void closeOthers() {
    File file = viewModel.getCurrentFile();
    if (file == null) {
      return;
    }
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

  public void saveFile() {
    if (!viewModel.getOpenedFiles().isEmpty()) {
      getCurrentEditor().saveFile();

      ToastUtils.showShort(getString(R.string.saved), ToastUtils.TYPE_SUCCESS);

      invalidateOptionsMenu();
      updateTabs();
    }
  }

  public void saveAllFiles(boolean showMsg) {
    if (!viewModel.getOpenedFiles().isEmpty()) {
      for (int i = 0; i < viewModel.getOpenedFileCount(); i++) {
        getEditorAtIndex(i).saveFile();
      }

      if (showMsg) {
        ToastUtils.showShort(getString(R.string.saved_files), ToastUtils.TYPE_SUCCESS);
      }
      updateTabs();
    }
  }

  public void onFileDeleted() {
    List<File> deletedFiles = new ArrayList<>();
    for (int i = 0; i < viewModel.getOpenedFileCount(); i++) {
      File openedFile = viewModel.getOpenedFiles().get(i);
      if (!openedFile.exists()) {
        deletedFiles.add(openedFile);
      }
    }
    removeDeletedFiles(deletedFiles);
  }

  private void removeDeletedFiles(List<File> deletedFiles) {
    for (File deletedFile : deletedFiles) {
      closeFile(viewModel.getOpenedFiles().indexOf(deletedFile));
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

  private void saveRecentlyOpenedFiles() {
    List<File> openedFiles = viewModel.getOpenedFiles();

    String json = new Gson().toJson(openedFiles);

    SharedPreferences.Editor editor = PreferencesUtils.getDefaultPrefs().edit();
    editor.putString("openedFiles", json);
    editor.putInt("selectedFile", viewModel.getCurrentFileIndex());
    editor.apply();
  }

  private void openRecentlyOpenedFiles() {
    try {
      SharedPreferences pref = PreferencesUtils.getDefaultPrefs();
      String json = pref.getString("openedFiles", "");
      List<File> recentlyOpenedFiles =
          new Gson().fromJson(json, new TypeToken<List<File>>() {}.getType());

      for (File file : recentlyOpenedFiles) {
        if (file == null) {
          return;
        }
        if (!file.isFile() || !file.exists()) {
          return;
        }
        openFileAndGetIndex(file);
      }
      setCurrent(pref.getInt("selectedFile", -1));
    } catch (Throwable e) {
      ILogger.error(LOG_TAG, Log.getStackTraceString(e));
    }
  }

  private void setCurrent(int index) {
    final var tab = binding.tabLayout.getTabAt(index);
    if (tab != null && index >= 0 && !tab.isSelected()) {
      binding.tabLayout.selectTab(tab, true);
    }
  }
  
  private void onEditorContentChanged(File file) {
    int index = findIndexOfEditorByFile(file);
    if (index == -1) {
      return;
    }

    TabLayout.Tab tab = binding.tabLayout.getTabAt(index);
    if (tab == null) {
      return;
    }

    String name = tab.getText().toString();
    if (name.startsWith("• ")) {
      return;
    }
    tab.setText("• " + name);
    invalidateOptionsMenu();
  }

  private void updateTabs() {
    TaskExecutor.executeAsyncProvideError(
        () -> {
          Map<Integer, String> names = getUniqueNames();
          return names;
        },
        (result, error) -> {
          if (result == null || error != null) {
            return;
          }

          result.forEach(
              (index, name) -> {
                TabLayout.Tab tab = binding.tabLayout.getTabAt(index);
                if (tab != null) {
                  tab.setText(name);
                }
              });
        });
  }

  private Map<Integer, String> getUniqueNames() {
    List<File> files = viewModel.getOpenedFiles();
    Map<String, Integer> dupliCount = new HashMap<>();
    Map<Integer, String> names = new HashMap<>();
    UniqueNameBuilder<File> nameBuilder = new UniqueNameBuilder<>("", File.separator);

    for (File file : files) {
      int count = dupliCount.getOrDefault(file.getName(), 0);
      dupliCount.put(file.getName(), ++count);
      nameBuilder.addPath(file, file.getPath());
    }

    for (int i = 0; i < binding.tabLayout.getTabCount(); i++) {
      File file = files.get(i);
      int count = dupliCount.getOrDefault(file.getName(), 0);
      boolean isModified = getEditorAtIndex(i).getEditor().isModified();
      String name = (count > 1) ? nameBuilder.getShortPath(file) : file.getName();
      if (isModified) {
        name = "• " + name;
      }
      names.put(i, name);
    }
    return names;
  }
}
