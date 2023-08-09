package com.raredev.vcspace.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKeys;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.UriUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raredev.vcspace.R;
import com.raredev.vcspace.SimpleExecuter;
import com.raredev.vcspace.databinding.ActivityEditorBinding;
import com.raredev.vcspace.editor.completion.CompletionProvider;
import com.raredev.vcspace.events.EditorContentChangedEvent;
import com.raredev.vcspace.events.PreferenceChangedEvent;
import com.raredev.vcspace.fragments.filemanager.models.FileModel;
import com.raredev.vcspace.models.DocumentModel;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.ui.PathListView;
import com.raredev.vcspace.ui.SearcherPopupWindow;
import com.raredev.vcspace.ui.editor.CodeEditorView;
import com.raredev.vcspace.ui.editor.Symbol;
import com.raredev.vcspace.ui.viewmodel.EditorViewModel;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.ILogger;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.SharedPreferencesKeys;
import com.raredev.vcspace.util.ToastUtils;
import com.raredev.vcspace.util.UniqueNameBuilder;
import com.raredev.vcspace.util.Utils;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class EditorActivity extends BaseActivity
    implements TabLayout.OnTabSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {

  private static final String LOG_TAG = EditorActivity.class.getSimpleName();
  private static final String RECENT_OPENED_PATH =
      PathUtils.getExternalAppDataPath() + "/recentOpened/documents.json";

  public ActivityResultLauncher<Intent> launcher;
  public ActivityResultLauncher<String> createFile;
  public ActivityResultLauncher<String> pickFile;

  private ActivityEditorBinding binding;

  public EditorViewModel viewModel;
  
  private SearcherPopupWindow searcher;

  // Overrides

  @Override
  public View getLayout() {
    binding = ActivityEditorBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setSupportActionBar(binding.toolbar);
    setupDrawer();

    viewModel = new ViewModelProvider(this).get(EditorViewModel.class);
    searcher = new SearcherPopupWindow(this, binding.getRoot());

    binding.tabLayout.addOnTabSelectedListener(this);
    binding.noFileOpened.setOnClickListener(v -> viewModel.setDrawerState(true));
    binding.symbolInput.setSymbols(Symbol.baseSymbols());

    KeyboardUtils.registerSoftInputChangedListener(
        this,
        new KeyboardUtils.OnSoftInputChangedListener() {
          @Override
          public void onSoftInputChanged(int i) {
            if (i > 1 && getCurrentEditor() != null) {
              binding.layoutSymbol.setVisibility(View.VISIBLE);
              binding.symbolInput.bindEditor(getCurrentEditor().getEditor());
            } else {
              binding.layoutSymbol.setVisibility(View.GONE);
            }
            invalidateOptionsMenu();
          }
        });

    CompletionProvider.registerCompletionProviders();
    PreferencesUtils.getDefaultPrefs().registerOnSharedPreferenceChangeListener(this);
    ThemeRegistry.getInstance().setTheme(Utils.isDarkMode() ? "darcula" : "quietlight");
    registerResultActivity();
    observeViewModel();

    if (isPermissionGaranted(this)) {
      Uri fileUri = getIntent().getData();
      if (fileUri != null) {
        openRecentDocuments();
        openFile(
            new FileModel(
                UriUtils.uri2File(fileUri).getAbsolutePath(),
                FileUtil.getFileName(this, fileUri),
                true));
      }
    }

    binding.pathList.setEnabled(PreferencesUtils.showFilePath());
    binding.pathList.setType(PathListView.TYPE_FILE_PATH);
    openRecentDocuments();
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    var editorView = getCurrentEditor();
    if (editorView != null) {
      var document = editorView.getDocument();
      menu.findItem(R.id.menu_execute_opt)
          .setVisible(SimpleExecuter.isExecutable(document.getName()));
      menu.findItem(R.id.menu_undo).setVisible(KeyboardUtils.isSoftInputVisible(this));
      menu.findItem(R.id.menu_redo).setVisible(KeyboardUtils.isSoftInputVisible(this));
      menu.findItem(R.id.menu_undo).setEnabled(editorView.getEditor().canUndo());
      menu.findItem(R.id.menu_redo).setEnabled(editorView.getEditor().canRedo());
      menu.findItem(R.id.menu_save).setEnabled(document.isModified());
      menu.findItem(R.id.menu_save_as).setEnabled(true);
      menu.findItem(R.id.menu_save_all).setEnabled(true);
      menu.findItem(R.id.menu_editor).setVisible(true);
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_main_menu, menu);
    if (menu instanceof MenuBuilder) {
      ((MenuBuilder) menu).setOptionalIconsVisible(true);
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    var id = item.getItemId();
    var editorView = getCurrentEditor();

    if (id == R.id.menu_execute) {
      saveAllFiles(true);
      SimpleExecuter.run(this, editorView.getDocument().toFile(), false);
    } else if (id == R.id.menu_execute_new_task) {
      saveAllFiles(true);
      SimpleExecuter.run(this, editorView.getDocument().toFile(), true);
    } else if (id == R.id.menu_undo) editorView.undo();
    else if (id == R.id.menu_redo) editorView.redo();
    else if (id == R.id.menu_search) searcher.showAndHide();
    else if (id == R.id.menu_format) editorView.getEditor().formatCodeAsync();
    else if (id == R.id.menu_new_file) createFile.launch("untitled");
    else if (id == R.id.menu_open_file) pickFile.launch("text/*");
    else if (id == R.id.menu_save) saveFile(true);
    else if (id == R.id.menu_save_as) {
      Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
      intent.addCategory(Intent.CATEGORY_OPENABLE);
      intent.setType("text/*");
      intent.putExtra(Intent.EXTRA_TITLE, viewModel.getCurrentDocument().getName());
      launcher.launch(intent);
    } else if (id == R.id.menu_save_all) saveAllFiles(true);
    else if (id == R.id.menu_settings) startActivity(new Intent(this, SettingsActivity.class));

    return true;
  }

  @Override
  public void onBackPressed() {
    if (viewModel.getDrawerState()) {
      viewModel.setDrawerState(false);
      return;
    }
    if (searcher.isShowing()) {
      searcher.showAndHide();
      return;
    }
    super.onBackPressed();
  }

  @Override
  protected void onStart() {
    super.onStart();
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

  // OnTabSelectedListener

  @Override
  public void onTabUnselected(TabLayout.Tab p1) {}

  @Override
  public void onTabReselected(TabLayout.Tab p1) {
    PopupMenu pm = new PopupMenu(this, p1.view);
    pm.getMenu().add(R.string.close);
    pm.getMenu().add(R.string.close_others);
    pm.getMenu().add(R.string.close_all);
    pm.setOnMenuItemClickListener(
        item -> {
          if (item.getTitle() == getString(R.string.close)) {
            closeFile(viewModel.getCurrentPosition());
          } else if (item.getTitle().equals(getString(R.string.close_others))) {
            closeOthers();
          } else if (item.getTitle().equals(getString(R.string.close_all))) {
            closeAllFiles();
          }
          return true;
        });
    pm.show();
  }

  @Override
  public void onTabSelected(TabLayout.Tab p1) {
    viewModel.setCurrentPosition(p1.getPosition());
  }

  // OnSharedPreferenceChangeListener

  @Override
  public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
    EventBus.getDefault().post(new PreferenceChangedEvent(key));
    if (key.equals(SharedPreferencesKeys.KEY_FILE_PATH)) {
      binding.pathList.setEnabled(PreferencesUtils.showFilePath());
    }
  }

  // getters

  public CodeEditorView getEditorAtIndex(int index) {
    return (CodeEditorView) binding.container.getChildAt(index);
  }

  public CodeEditorView getCurrentEditor() {
    return (CodeEditorView) binding.container.getChildAt(viewModel.getCurrentPosition());
  }

  // Document Opener

  public void openFile(@NonNull FileModel file) {
    if (!BaseActivity.isPermissionGaranted(this)) {
      BaseActivity.takeFilePermissions(this);
      return;
    }
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

    ILogger.debug(LOG_TAG, file.getName() + " Openening at index: " + index);

    CodeEditorView editor = new CodeEditorView(this, document);
    binding.container.addView(editor);

    binding.tabLayout.addTab(binding.tabLayout.newTab());
    viewModel.addDocument(document);
    updateTabs();
    return index;
  }

  // Document closers

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
    if (viewModel.getDocuments().isEmpty()) {
      return;
    }
    DocumentModel document = viewModel.getCurrentDocument();
    if (document == null) return;

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
    clearRecentOpenedDocuments();
  }

  // Document Savers

  public void saveFile(boolean showMsg) {
    if (!viewModel.getDocuments().isEmpty()) {
      TaskExecutor.executeAsync(
          () -> {
            saveDocumentAtIndex(viewModel.getCurrentPosition());
            return null;
          },
          (result) -> {
            if (showMsg) ToastUtils.showShort(getString(R.string.saved), ToastUtils.TYPE_SUCCESS);
            invalidateOptionsMenu();
          });
    }
  }

  public void saveAllFiles(boolean showMsg) {
    saveAllFiles(showMsg, () -> {});
  }

  public void saveAllFiles(boolean showMsg, Runnable post) {
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
            post.run();
          });
    }
  }

  private void saveDocumentAtIndex(int index) {
    CodeEditorView editorView = getEditorAtIndex(index);
    TabLayout.Tab tab = binding.tabLayout.getTabAt(index);
    if (editorView != null && tab != null) {
      editorView.saveDocument();
      runOnUiThread(
          () -> {
            String name = tab.getText().toString();
            if (name.startsWith("*")) {
              tab.setText(name.replace("*", ""));
            }
          });
    }
  }

  // Others

  private void setupDrawer() {
    DrawerLayout drawerLayout = binding.drawerLayout;

    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(
            this, drawerLayout, binding.toolbar, R.string.open, R.string.close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    drawerLayout.addDrawerListener(
        new DrawerLayout.DrawerListener() {
          @Override
          public void onDrawerSlide(@NonNull View view, float v) {}

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
            binding.main.setDisplayedChild(1);
            searcher.dismiss();
            invalidateOptionsMenu();
          } else {
            binding.main.setDisplayedChild(0);
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
          if (index == -1) return;
          binding.container.setDisplayedChild(index);
          final var tab = binding.tabLayout.getTabAt(index);
          if (tab != null && index >= 0 && !tab.isSelected()) {
            tab.select();
          }
          var editorView = getCurrentEditor();
          if (editorView != null) {
            binding.symbolInput.bindEditor(editorView.getEditor());
            binding.pathList.setPath(editorView.getDocument().getPath());
            searcher.bindSearcher(editorView.getEditor().getSearcher());
          }

          invalidateOptionsMenu();
        });
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEditorContentChanged(EditorContentChangedEvent event) {
    DocumentModel document = event.getDocument();
    document.markModified();
    invalidateOptionsMenu();
    saveOpenedDocuments();
    int index = viewModel.indexOf(document);
    if (index == -1) {
      return;
    }
    if (!PreferencesUtils.autoSave()) {
      TabLayout.Tab tab = binding.tabLayout.getTabAt(index);
      if (tab == null) {
        return;
      }

      String name = tab.getText().toString();
      if (name.startsWith("*")) {
        return;
      }
      tab.setText("*" + name);
    } else {
      saveFile(false);
    }
  }

  private void updateTabs() {
    TaskExecutor.executeAsyncProvideError(
        () -> getUniqueNames(),
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
    saveOpenedDocuments();
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
        name = "*" + name;
      }
      names.put(i, name);
    }
    return names;
  }

  private void saveOpenedDocuments() {
    List<DocumentModel> documents = viewModel.getDocuments();

    byte[] json = new Gson().toJson(documents).getBytes(StandardCharsets.UTF_8);
    try {
      if (FileUtils.isFileExists(new File(RECENT_OPENED_PATH))) {
        FileUtils.delete(new File(RECENT_OPENED_PATH));
      }
      EncryptedFile encryptedFile =
          new EncryptedFile.Builder(
                  new File(RECENT_OPENED_PATH),
                  this,
                  MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                  EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB)
              .build();
      OutputStream outputStream = encryptedFile.openFileOutput();
      outputStream.write(json);
      outputStream.flush();
      outputStream.close();
    } catch (GeneralSecurityException | IOException err) {
      err.printStackTrace();
      ToastUtils.showShort(err.getLocalizedMessage(), ToastUtils.TYPE_ERROR);
    }
  }

  private void clearRecentOpenedDocuments() {
    FileIOUtils.writeFileFromString(RECENT_OPENED_PATH, "[]");
  }

  private void openRecentDocuments() {
    try {
      var type = new TypeToken<List<DocumentModel>>() {}.getType();
      EncryptedFile encryptedFile =
          new EncryptedFile.Builder(
                  new File(RECENT_OPENED_PATH),
                  this,
                  MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                  EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB)
              .build();

      InputStream inputStream = encryptedFile.openFileInput();
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      int nextByte = inputStream.read();

      while (nextByte != -1) {
        byteArrayOutputStream.write(nextByte);
        nextByte = inputStream.read();
      }

      byte[] plaintext = byteArrayOutputStream.toByteArray();
      List<DocumentModel> documents = new Gson().fromJson(new String(plaintext), type);

      if (documents == null) return;

      for (DocumentModel doc : documents) {
        if (!BaseActivity.isPermissionGaranted(this)) {
          BaseActivity.takeFilePermissions(this);
          return;
        }
        if (!doc.isFile()) {
          return;
        }
        int index = openFileAndGetIndex(doc);
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }
  
}
