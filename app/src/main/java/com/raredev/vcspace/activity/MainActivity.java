package com.raredev.vcspace.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import com.blankj.utilcode.util.KeyboardUtils;
import com.google.android.material.tabs.TabLayout;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.ActivityMainBinding;
import com.raredev.vcspace.editor.completion.CompletionProvider;
import com.raredev.vcspace.events.EditorContentChangedEvent;
import com.raredev.vcspace.events.PreferenceChangedEvent;
import com.raredev.vcspace.fragments.filemanager.models.FileModel;
import com.raredev.vcspace.models.DocumentModel;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.ui.editor.CodeEditorView;
import com.raredev.vcspace.ui.editor.Symbol;
import com.raredev.vcspace.ui.viewmodel.EditorViewModel;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.ILogger;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.ToastUtils;
import com.raredev.vcspace.util.UniqueNameBuilder;
import com.raredev.vcspace.util.Utils;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.ActionManager;
import com.vcspace.actions.location.DefaultLocations;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends BaseActivity
    implements TabLayout.OnTabSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {
  private static final String LOG_TAG = MainActivity.class.getSimpleName();

  public ActivityResultLauncher<Intent> launcher;
  public ActivityResultLauncher<String> createFile;
  public ActivityResultLauncher<String> pickFile;

  public ActivityMainBinding binding;

  public EditorViewModel viewModel;

  @Override
  public View getLayout() {
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setSupportActionBar(binding.toolbar);

    setupDrawer();

    viewModel = new ViewModelProvider(this).get(EditorViewModel.class);

    binding.tabLayout.addOnTabSelectedListener(this);

    KeyboardUtils.registerSoftInputChangedListener(
        this,
        new KeyboardUtils.OnSoftInputChangedListener() {
          @Override
          public void onSoftInputChanged(int i) {
            if (i > 1 && getCurrentEditor() != null) {
              binding.symbolInput.setVisibility(View.VISIBLE);
              binding.symbolInput.bindEditor(getCurrentEditor().getEditor());
            } else {
              binding.symbolInput.setVisibility(View.GONE);
            }
            invalidateOptionsMenu();
          }
        });

    CompletionProvider.registerCompletionProviders();

    binding.symbolInput.setSymbols(Symbol.baseSymbols());
    ThemeRegistry.getInstance().setTheme(Utils.isDarkMode() ? "darcula" : "quietlight");
    registerResultActivity();
    observeViewModel();
  }

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
    viewModel.setCurrentPosition(p1.getPosition());
  }

  @Override
  public void onBackPressed() {
    if (viewModel.getDrawerState()) {
      viewModel.setDrawerState(false);
      return;
    }
    CodeEditorView editor = getCurrentEditor();
    if (editor != null && editor.searcherIsShowing()) {
      editor.showAndHideSearcher();
      return;
    }
    super.onBackPressed();
  }

  @Override
  protected void onStart() {
    super.onStart();
    PreferencesUtils.getDefaultPrefs().registerOnSharedPreferenceChangeListener(this);
    if (!EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().register(this);
    }
  }

  @Override
  protected void onStop() {
    if (EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this);
    }
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    PreferencesUtils.getDefaultPrefs().unregisterOnSharedPreferenceChangeListener(this);
    viewModel.clearDocuments();
    super.onDestroy();
    binding = null;
  }

  @Override
  protected void onResume() {
    super.onResume();
    invalidateOptionsMenu();
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
    EventBus.getDefault().post(new PreferenceChangedEvent(key));
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
    if (menu instanceof MenuBuilder menuBuilder) {
      menuBuilder.setOptionalIconsVisible(true);
    }
    return true;
  }

  private void setupDrawer() {
    DrawerLayout drawerLayout = binding.drawerLayout;

    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(
            this, drawerLayout, binding.toolbar, R.string.open, R.string.close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    drawerLayout.setScrimColor(Color.TRANSPARENT);
    drawerLayout.setDrawerElevation(0);
    drawerLayout.addDrawerListener(
        new DrawerLayout.DrawerListener() {
          @Override
          public void onDrawerSlide(@NonNull View view, float v) {
            float slideX = view.getWidth() * v;
            binding.main.setTranslationX(slideX);
          }

          @Override
          public void onDrawerOpened(@NonNull View view) {
            viewModel.setDrawerState(true);
          }

          @Override
          public void onDrawerClosed(@NonNull View view) {
            viewModel.setDrawerState(false);
          }

          @Override
          public void onDrawerStateChanged(int i) {}
        });
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
                  outputStream.write(getCurrentEditor().getCode().getBytes());
                  outputStream.close();
                  openFile(FileModel.fileToFileModel(FileUtil.getFileFromUri(this, uri)));
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
                  openFile(FileModel.fileToFileModel(FileUtil.getFileFromUri(this, uri)));
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
                  openFile(FileModel.fileToFileModel(FileUtil.getFileFromUri(this, uri)));
                } catch (IOException e) {
                  ILogger.error(LOG_TAG, Log.getStackTraceString(e));
                }
              }
            });
  }

  private void observeViewModel() {
    viewModel.observeDocuments(
        this,
        documents -> {
          if (documents.isEmpty()) {
            binding.editorContainer.setVisibility(View.GONE);
            binding.noFileOpened.setVisibility(View.VISIBLE);
            invalidateOptionsMenu();
          } else {
            binding.editorContainer.setVisibility(View.VISIBLE);
            binding.noFileOpened.setVisibility(View.GONE);
          }
        });
    viewModel.observeDrawerState(
        this,
        (state) -> {
          if (state) {
            binding.drawerLayout.openDrawer(GravityCompat.START);
          } else {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
          }
        });
    viewModel.observeCurrentPosition(
        this,
        index -> {
          binding.container.setDisplayedChild(index);
          final var tab = binding.tabLayout.getTabAt(index);
          if (tab != null && index >= 0 && !tab.isSelected()) {
            tab.select();
          }
          invalidateOptionsMenu();
        });
  }

  public void openFile(@NonNull FileModel file) {
    if (!file.isFile()) {
      return;
    }
    viewModel.setDrawerState(false);
    int index = openFileAndGetIndex(file);
    viewModel.setCurrentPosition(index);
  }

  private int openFileAndGetIndex(@NonNull FileModel file) {
    int openedFileIndex = viewModel.indexOf(file.getPath());
    if (openedFileIndex != -1) {
      return openedFileIndex;
    }
    DocumentModel document = DocumentModel.fileModelToDocument(file);
    int index = viewModel.getOpenedDocumentCount();

    CodeEditorView editor = new CodeEditorView(this, document);
    binding.container.addView(editor);

    binding.tabLayout.addTab(binding.tabLayout.newTab());
    viewModel.addDocument(document);
    updateTabs();
    return index;
  }

  public void closeFile(int index) {
    if (index >= 0 && index < viewModel.getOpenedDocumentCount()) {
      CodeEditorView editor = getEditorAtIndex(index);
      if (editor != null) {
        editor.release();
      }

      viewModel.removeDocument(index);
      binding.tabLayout.removeTabAt(index);
      binding.container.removeViewAt(index);
      updateTabs();
    }
  }

  public void closeOthers() {
    DocumentModel document = viewModel.getCurrentDocument();
    int index = 0;

    while (viewModel.getOpenedDocumentCount() != 1) {
      CodeEditorView editor = getEditorAtIndex(index);

      if (editor != null) {
        if (document != editor.getDocument()) {
          closeFile(index);
        } else {
          index = 1;
        }
      }
    }
    viewModel.setCurrentPosition(viewModel.indexOf(document));
  }

  public void closeAllFiles() {
    if (viewModel.getDocuments().isEmpty()) {
      return;
    }
    for (int i = 0; i < viewModel.getOpenedDocumentCount(); i++) {
      CodeEditorView editor = getEditorAtIndex(i);
      if (editor != null) {
        editor.release();
      }
    }

    viewModel.clearDocuments();
    binding.tabLayout.removeAllTabs();
    binding.tabLayout.requestLayout();
    binding.container.removeAllViews();
  }

  public void saveFile() {
    if (!viewModel.getDocuments().isEmpty()) {
      TaskExecutor.executeAsync(
          () -> {
            saveDocumentAtIndex(viewModel.getCurrentPosition());
            return null;
          },
          (result) -> {
            ToastUtils.showShort(getString(R.string.saved), ToastUtils.TYPE_SUCCESS);
            invalidateOptionsMenu();
          });
    }
  }

  public void saveAllFiles(boolean showMsg) {
    if (!viewModel.getDocuments().isEmpty()) {
      TaskExecutor.executeAsync(
          () -> {
            for (int i = 0; i < viewModel.getOpenedDocumentCount(); i++) {
              saveDocumentAtIndex(i);
            }
            return null;
          },
          (result) -> {
            if (showMsg) {
              ToastUtils.showShort(getString(R.string.saved_files), ToastUtils.TYPE_SUCCESS);
            }
          });
    }
  }

  private void saveDocumentAtIndex(int index) {
    CodeEditorView editorView = getEditorAtIndex(index);
    TabLayout.Tab tab = binding.tabLayout.getTabAt(index);
    if (editorView != null && tab != null) {
      editorView.saveDocument();
      runOnUiThread(() -> tab.setText(editorView.getDocument().getName()));
    }
  }

  public void onFileDeleted() {}

  public CodeEditorView getEditorAtIndex(int index) {
    return (CodeEditorView) binding.container.getChildAt(index);
  }

  public CodeEditorView getCurrentEditor() {
    return (CodeEditorView) binding.container.getChildAt(viewModel.getCurrentPosition());
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEditorContentChanged(EditorContentChangedEvent event) {
    DocumentModel document = event.getDocument();
    document.markModified();
    invalidateOptionsMenu();
    int index = viewModel.indexOf(document);
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
    List<DocumentModel> documents = viewModel.getDocuments();
    Map<String, Integer> dupliCount = new HashMap<>();
    Map<Integer, String> names = new HashMap<>();
    UniqueNameBuilder<DocumentModel> nameBuilder = new UniqueNameBuilder<>("", File.separator);

    for (DocumentModel document : documents) {
      int count = dupliCount.getOrDefault(document.getName(), 0);
      dupliCount.put(document.getName(), ++count);
      nameBuilder.addPath(document, document.getPath());
    }

    for (int i = 0; i < binding.tabLayout.getTabCount(); i++) {
      DocumentModel document = documents.get(i);
      int count = dupliCount.getOrDefault(document.getName(), 0);
      boolean isModified = document.isModified();
      String name = (count > 1) ? nameBuilder.getShortPath(document) : document.getName();
      if (isModified) {
        name = "• " + name;
      }
      names.put(i, name);
    }
    return names;
  }
}
