package com.raredev.vcspace.activity;

import static com.raredev.vcspace.res.R.drawable;
import static com.raredev.vcspace.res.R.string;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.UriUtils;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raredev.vcspace.R;
import com.raredev.vcspace.SimpleExecuter;
import com.raredev.vcspace.databinding.ActivityEditorBinding;
import com.raredev.vcspace.databinding.LayoutTabItemBinding;
import com.raredev.vcspace.editor.completion.CompletionProvider;
import com.raredev.vcspace.events.EditorContentChangedEvent;
import com.raredev.vcspace.events.OnFileRenamedEvent;
import com.raredev.vcspace.events.PreferenceChangedEvent;
import com.raredev.vcspace.fragments.explorer.FileExplorerFragment;
import com.raredev.vcspace.models.DocumentModel;
import com.raredev.vcspace.models.FileModel;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.ui.PathListView;
import com.raredev.vcspace.ui.editor.CodeEditorView;
import com.raredev.vcspace.ui.editor.Symbol;
import com.raredev.vcspace.ui.window.SearcherWindow;
import com.raredev.vcspace.ui.window.VCSpaceWindow;
import com.raredev.vcspace.ui.window.VCSpaceWindowManager;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.ILogger;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.SharedPreferencesKeys;
import com.raredev.vcspace.util.ToastUtils;
import com.raredev.vcspace.util.UniqueNameBuilder;
import com.raredev.vcspace.viewmodel.EditorViewModel;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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
      PathUtils.getExternalAppDataPath() + "/files/recentOpened/documents.json";

  public ActivityResultLauncher<Intent> launcher;
  public ActivityResultLauncher<String> createFile;
  public ActivityResultLauncher<String> pickFile;

  public EditorViewModel viewModel;

  private ActivityEditorBinding binding;
  private FileExplorerFragment fileExplorer;
  private SearcherWindow searcher;

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

    fileExplorer =
        ((FileExplorerFragment) getSupportFragmentManager().findFragmentByTag("filemanager"));
    viewModel = new ViewModelProvider(this).get(EditorViewModel.class);

    searcher =
        (SearcherWindow)
            VCSpaceWindowManager.getInstance(this).getWindow(VCSpaceWindowManager.SEARCHER_WINDOW);
  
    var windows = VCSpaceWindowManager.getInstance(this).getWindows();
    for (Map.Entry<String, VCSpaceWindow> entry : windows.entrySet()) {
      var window = entry.getValue();
      if (window.getParent() != null) {
        ((ViewGroup) window.getParent()).removeView(window);
      }
      binding.floatingContainer.addView(window);
    }

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
    registerResultActivity();
    observeViewModel();

    if (isPermissionGaranted(this)) {
      Uri fileUri = getIntent().getData();
      if (fileUri != null) {
        openRecentDocuments();
        openFile(FileModel.fileToFileModel(UriUtils.uri2File(fileUri)));
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
      menu.findItem(R.id.menu_execute).setVisible(SimpleExecuter.isExecutable(document.getName()));
      menu.findItem(R.id.menu_undo).setVisible(KeyboardUtils.isSoftInputVisible(this));
      menu.findItem(R.id.menu_redo).setVisible(KeyboardUtils.isSoftInputVisible(this));
      menu.findItem(R.id.menu_undo).setEnabled(editorView.getEditor().canUndo());
      menu.findItem(R.id.menu_redo).setEnabled(editorView.getEditor().canRedo());
      menu.findItem(R.id.menu_save).setEnabled(document.isModified());
      menu.findItem(R.id.menu_save_as).setEnabled(true);
      menu.findItem(R.id.menu_save_all).setEnabled(viewModel.getUnsavedDocumentsCount() > 0);
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
      SimpleExecuter.run(this, editorView.getDocument().toFile());
    } else if (id == R.id.menu_undo) editorView.undo();
    else if (id == R.id.menu_redo) editorView.redo();
    else if (id == R.id.menu_search) {
      searcher.bindSearcher(editorView.getEditor().getSearcher());
      searcher.showAndHide();
    } else if (id == R.id.menu_format) editorView.getEditor().formatCodeAsync();
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
      if (!fileExplorer.viewModel.getCurrentDir().getPath().equals("/storage/emulated/0")) {
        fileExplorer.onBackPressed();
        return;
      }
      viewModel.setDrawerState(false);
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
    saveOpenedDocuments();
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
  public void onTabUnselected(TabLayout.Tab p1) {
    TextView tabTitle = p1.getCustomView().findViewById(R.id.title);
    ImageView close = p1.getCustomView().findViewById(R.id.close);
    tabTitle.setTextColor(
        MaterialColors.getColor(this, com.google.android.material.R.attr.colorControlNormal, 0));
  }

  @Override
  public void onTabReselected(TabLayout.Tab p1) {
    tabPopupMenu(p1.getPosition(), p1.view).show();
  }

  @Override
  public void onTabSelected(TabLayout.Tab p1) {
    TextView tabTitle = p1.getCustomView().findViewById(R.id.title);
    ImageView close = p1.getCustomView().findViewById(R.id.close);
    tabTitle.setTextColor(
        MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimary, 0));

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
    int index = openFileAndGetIndex(DocumentModel.fileModelToDocument(file));
    viewModel.setCurrentPosition(index);
  }

  private int openFileAndGetIndex(@NonNull DocumentModel document) {
    int openedFileIndex = viewModel.indexOf(document.getPath());
    if (openedFileIndex != -1) {
      return openedFileIndex;
    }
    int index = viewModel.getOpenedDocumentCount();

    CodeEditorView editor = new CodeEditorView(this, document);
    binding.container.addView(editor);

    binding.tabLayout.addTab(createTabItem());
    viewModel.addDocument(document);
    updateTabs();
    return index;
  }

  private TabLayout.Tab createTabItem() {
    var bind = LayoutTabItemBinding.inflate(getLayoutInflater());
    var tab = binding.tabLayout.newTab();

    bind.close.setOnClickListener(v -> closeFile(tab.getPosition()));
    tab.setCustomView(bind.getRoot());
    return tab;
  }

  // Document closers

  public void closeFile(int index) {
    var doc = viewModel.getDocument(index);
    if (doc.isPinned()) {
      return;
    }

    var currentIndex = viewModel.getCurrentPosition();
    var currentDocument = viewModel.getCurrentDocument();

    if (currentIndex != -1 || currentDocument != null) {
      closeFileHandler(index);
      if (index != currentIndex) {
        viewModel.setCurrentPosition(viewModel.indexOf(currentDocument));
      }
      var newCurrentDocument = viewModel.getCurrentDocument();
      if (newCurrentDocument != null) {
        binding.pathList.setPath(newCurrentDocument.getPath());
      }
    }
  }

  public void closeFileHandler(int index) {
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

    List<DocumentModel> documentsToClose = new ArrayList<>();
    for (DocumentModel doc : viewModel.getDocuments()) {
      if (doc != null && !document.equals(doc) && !doc.isPinned()) {
        documentsToClose.add(doc);
      }
    }

    for (DocumentModel doc : documentsToClose) {
      closeFileHandler(viewModel.indexOf(doc));
    }
    viewModel.setCurrentPosition(viewModel.indexOf(document));
  }

  public void closeAllFiles() {
    if (viewModel.getDocuments().isEmpty()) {
      return;
    }

    List<DocumentModel> documentsToClose = new ArrayList<>();
    for (DocumentModel doc : viewModel.getDocuments()) {
      if (doc != null && !doc.isPinned()) {
        documentsToClose.add(doc);
      }
    }

    for (DocumentModel doc : documentsToClose) {
      closeFileHandler(viewModel.indexOf(doc));
    }
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
            if (showMsg) ToastUtils.showShort(getString(string.saved), ToastUtils.TYPE_SUCCESS);
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
            if (showMsg)
              ToastUtils.showShort(getString(string.saved_files), ToastUtils.TYPE_SUCCESS);
            invalidateOptionsMenu();
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
            TextView tabTitle = tab.getCustomView().findViewById(R.id.title);
            String name = tabTitle.getText().toString();
            if (name.startsWith("*")) {
              tabTitle.setText(name.replace("*", ""));
            }
          });
    }
  }

  // Others

  private void setupDrawer() {
    DrawerLayout drawerLayout = binding.drawerLayout;

    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(this, drawerLayout, binding.toolbar, string.open, string.close);
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
            viewModel.setDrawerState(true);
            searcher.dismiss();
            invalidateOptionsMenu();
          } else {
            binding.main.setDisplayedChild(0);
          }
          saveOpenedDocuments();
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

  private PopupMenu tabPopupMenu(int index, View view) {
    final var doc = viewModel.getDocument(index);
    final var pm = new PopupMenu(this, view);
    pm.inflate(R.menu.editor_tab_menu);
    pm.getMenu().getItem(3).setTitle(doc.isPinned() ? string.unpin : string.pin);

    pm.setOnMenuItemClickListener(
        item -> {
          if (item.getItemId() == R.id.close) {
            closeFile(index);
          } else if (item.getItemId() == R.id.close_others) {
            closeOthers();
          } else if (item.getItemId() == R.id.close_all) {
            closeAllFiles();
          } else if (item.getItemId() == R.id.pin) {
            doc.setPinned(!doc.isPinned());
            item.setTitle(doc.isPinned() ? string.unpin : string.pin);
            updateTabs();
          }
          return true;
        });
    return pm;
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onFileRenamed(OnFileRenamedEvent event) {
    var index = viewModel.indexOf(event.oldFile.getPath());
    if (index != -1) {
      var oldDocument = viewModel.getDocument(index);
      var newDocument = DocumentModel.fileToDocument(event.newFile);
      newDocument.setModified(oldDocument.isModified());
      if (viewModel.getCurrentPosition() == index) {
        binding.pathList.setPath(newDocument.getPath());
      }
      getEditorAtIndex(index).setDocument(newDocument);
      viewModel.setDocumentAtIndex(index, newDocument);
      updateTabs();
    }
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
    if (!PreferencesUtils.autoSave()) {
      TabLayout.Tab tab = binding.tabLayout.getTabAt(index);
      if (tab == null) {
        return;
      }
      TextView tabTitle = tab.getCustomView().findViewById(R.id.title);
      String name = tabTitle.getText().toString();
      if (name.startsWith("*")) {
        return;
      }
      tabTitle.setText("*" + name);
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
                  var doc = viewModel.getDocument(index);
                  TextView tabTitle = tab.getCustomView().findViewById(R.id.title);
                  TooltipCompat.setTooltipText(tab.view, doc.getPath());
                  tabTitle.setText(name);

                  ImageView close = tab.getCustomView().findViewById(R.id.close);
                  close.setImageResource(doc.isPinned() ? drawable.ic_pin : drawable.close);
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
        name = "*" + name;
      }
      names.put(i, name);
    }
    return names;
  }

  private void saveOpenedDocuments() {
    List<DocumentModel> documents = viewModel.getDocuments();

    String json = new Gson().toJson(documents);
    try {
      FileIOUtils.writeFileFromString(
          RECENT_OPENED_PATH, EncodeUtils.base64Encode2String(json.getBytes()));
    } catch (Exception err) {
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
      List<DocumentModel> documents =
          new Gson()
              .fromJson(
                  new String(
                      EncodeUtils.base64Decode(FileIOUtils.readFile2String(RECENT_OPENED_PATH))),
                  type);

      if (documents == null) return;

      if (!BaseActivity.isPermissionGaranted(this)) {
        BaseActivity.takeFilePermissions(this);
        return;
      }

      for (DocumentModel doc : documents) {
        if (!doc.toFile().isFile()) {
          return;
        }
        int index = openFileAndGetIndex(doc);
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }
}
