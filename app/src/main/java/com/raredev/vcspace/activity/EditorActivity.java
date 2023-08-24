package com.raredev.vcspace.activity;

import static com.raredev.vcspace.res.R.id;
import static com.raredev.vcspace.res.R.string;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.UriUtils;
import com.google.android.material.tabs.TabLayout;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.ActivityEditorBinding;
import com.raredev.vcspace.editor.completion.CompletionProvider;
import com.raredev.vcspace.events.EditorContentChangedEvent;
import com.raredev.vcspace.events.OnFileRenamedEvent;
import com.raredev.vcspace.events.PreferenceChangedEvent;
import com.raredev.vcspace.events.UpdateExecutePanelEvent;
import com.raredev.vcspace.events.UpdateSearcherEvent;
import com.raredev.vcspace.models.DocumentModel;
import com.raredev.vcspace.models.FileModel;
import com.raredev.vcspace.models.Symbol;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.ui.PathListView;
import com.raredev.vcspace.ui.panels.Panel;
import com.raredev.vcspace.ui.panels.PanelsManager;
import com.raredev.vcspace.ui.panels.compiler.ExecutePanel;
import com.raredev.vcspace.ui.panels.compiler.WebViewPanel;
import com.raredev.vcspace.ui.panels.editor.EditorPanel;
import com.raredev.vcspace.ui.panels.editor.SearcherPanel;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.ILogger;
import com.raredev.vcspace.util.PanelUtils;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.SharedPreferencesKeys;
import com.raredev.vcspace.util.ToastUtils;
import com.raredev.vcspace.util.UniqueNameBuilder;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class EditorActivity extends BaseActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener {

  private static final String LOG_TAG = EditorActivity.class.getSimpleName();

  public static final String RECENT_PANELS_PATH =
      PathUtils.getExternalAppDataPath() + "/files/recentPanels/editorPanels.json";

  public ActivityResultLauncher<Intent> launcher;
  public ActivityResultLauncher<String> createFile;
  public ActivityResultLauncher<String> pickFile;

  private ActivityEditorBinding binding;

  private PanelsManager panelsManager;

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

    panelsManager = new PanelsManager(this, binding.panelArea);
    binding.symbolInput.setSymbols(Symbol.baseSymbols());
    binding.pathList.setEnabled(PreferencesUtils.showFilePath());
    binding.pathList.setType(PathListView.TYPE_FILE_PATH);

    KeyboardUtils.registerSoftInputChangedListener(
        this,
        new KeyboardUtils.OnSoftInputChangedListener() {
          @Override
          public void onSoftInputChanged(int i) {
            Panel panel = panelsManager.getSelectedPanel();
            if (panel != null && panel instanceof EditorPanel) {
              EditorPanel editorPanel = (EditorPanel) panel;
              if (i > 1) {
                binding.layoutSymbol.setVisibility(View.VISIBLE);
                binding.symbolInput.bindEditor(editorPanel.getEditor());
              } else {
                binding.layoutSymbol.setVisibility(View.GONE);
              }
            } else {
              binding.layoutSymbol.setVisibility(View.GONE);
            }
            invalidateOptionsMenu();
          }
        });

    CompletionProvider.registerCompletionProviders();
    PreferencesUtils.getDefaultPrefs().registerOnSharedPreferenceChangeListener(this);
    registerResultActivity();

    openRecentPanels();
    if (isPermissionGaranted(this)) {
      Uri fileUri = getIntent().getData();
      if (fileUri != null) {
        openFile(FileModel.fileToFileModel(UriUtils.uri2File(fileUri)));
      }
    }
    panelsManager.addDefaultPanels();
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    EditorPanel editorPanel = getSelectedEditorPanel();
    WebViewPanel webViewPanel = getSelectedWebViewPanel();
    if (editorPanel != null) {
      var document = editorPanel.getDocument();
      menu.findItem(R.id.menu_execute).setVisible(true);
      menu.findItem(R.id.menu_undo).setVisible(KeyboardUtils.isSoftInputVisible(this));
      menu.findItem(R.id.menu_redo).setVisible(KeyboardUtils.isSoftInputVisible(this));
      menu.findItem(R.id.menu_undo).setEnabled(editorPanel.getEditor().canUndo());
      menu.findItem(R.id.menu_redo).setEnabled(editorPanel.getEditor().canRedo());
      menu.findItem(R.id.menu_save).setEnabled(document.isModified());
      menu.findItem(R.id.menu_save_as).setEnabled(true);
      menu.findItem(R.id.menu_save_all).setEnabled(getUnsavedDocumentsCount() > 0);
      menu.findItem(R.id.menu_reload).setEnabled(true);
      menu.findItem(R.id.menu_editor).setVisible(true);
    } else if (webViewPanel != null) {
      menu.findItem(R.id.menu_webview).setVisible(true);
      menu.findItem(R.id.zooming).setChecked(webViewPanel.isSupportZoom());
      menu.findItem(R.id.desktop_mode).setChecked(webViewPanel.isDesktopMode());
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
    EditorPanel editorPanel = getSelectedEditorPanel();
    WebViewPanel webViewPanel = getSelectedWebViewPanel();
    if (editorPanel != null) {
      var document = editorPanel.getDocument();
      if (id == R.id.menu_execute) {
        saveAllFiles(false);
        if (document.getName().endsWith(".html")) {
          panelsManager.addWebViewPanel(document.getPath());
        } else {
          panelsManager.addFloatingPanel(ExecutePanel.createFloating(this, binding.panelArea));
          panelsManager.sendEvent(
              new UpdateExecutePanelEvent(
                  document.getPath(),
                  FileUtils.getFileExtension(document.getPath()),
                  editorPanel.getCode()));
        }
      } else if (id == R.id.menu_undo) editorPanel.undo();
      else if (id == R.id.menu_redo) editorPanel.redo();
      else if (id == R.id.menu_search) {
        panelsManager.addFloatingPanel(SearcherPanel.createFloating(this, binding.panelArea));
        panelsManager.sendEvent(new UpdateSearcherEvent(editorPanel.getEditor().getSearcher()));
      } else if (id == R.id.menu_format) editorPanel.getEditor().formatCodeAsync();
      else if (id == R.id.menu_save) saveFile(true);
      else if (id == R.id.menu_save_as) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_TITLE, editorPanel.getDocument().getName());
        launcher.launch(intent);
      } else if (id == R.id.menu_save_all) saveAllFiles(true);
      else if (id == R.id.menu_reload) editorPanel.reloadFile(() -> updateTabs());

    } else if (webViewPanel != null) {
      WebView webView = webViewPanel.getWebView();
      if (id == R.id.back) {
        if (webView.canGoBack()) webView.goBack();
        else ToastUtils.showShort("Can't go back...", ToastUtils.TYPE_ERROR);
      } else if (id == R.id.forward) {
        if (webView.canGoForward()) webView.goForward();
        else ToastUtils.showShort("Can't go forward...", ToastUtils.TYPE_ERROR);
      } else if (id == R.id.zooming) {
        webViewPanel.setSupportZoom(!item.isChecked());
        item.setChecked(!item.isChecked());
      } else if (id == R.id.desktop_mode) {
        webViewPanel.setDesktopMode(!item.isChecked());
        item.setChecked(!item.isChecked());
      } else if (id == R.id.refresh) {
        webView.reload();
      } else if (id == R.id.open_in_browser) {
        webViewPanel.openInBrowser();
      }
    }

    if (id == R.id.menu_new_file) createFile.launch("untitled");
    else if (id == R.id.menu_open_file) pickFile.launch("text/*");
    else if (id == R.id.menu_settings) startActivity(new Intent(this, SettingsActivity.class));

    return true;
  }

  @Override
  public void onBackPressed() {
    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
      binding.drawerLayout.closeDrawer(GravityCompat.START);
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
    savePanels();
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    PreferencesUtils.getDefaultPrefs().unregisterOnSharedPreferenceChangeListener(this);
    super.onDestroy();
    binding = null;
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
    if (key.equals(SharedPreferencesKeys.KEY_FILE_PATH)) {
      binding.pathList.setEnabled(PreferencesUtils.showFilePath());
    }
    EventBus.getDefault().post(new PreferenceChangedEvent(key));
  }

  private void setupDrawer() {
    DrawerLayout drawerLayout = binding.drawerLayout;

    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(this, drawerLayout, binding.toolbar, string.open, string.close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();
  }

  private void registerResultActivity() {
    launcher =
        registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
              if (result.getResultCode() == RESULT_OK) {
                EditorPanel editorPanel = getSelectedEditorPanel();
                if (editorPanel != null) {
                  Uri uri = result.getData().getData();
                  try {
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    outputStream.write(editorPanel.getCode().getBytes());
                    outputStream.close();
                    openFile(FileModel.fileToFileModel(FileUtil.getFileFromUri(this, uri)));
                  } catch (IOException e) {
                    ILogger.error(LOG_TAG, Log.getStackTraceString(e));
                  }
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

  // Document Opener

  public void openFile(@NonNull FileModel file) {
    if (!BaseActivity.isPermissionGaranted(this)) {
      BaseActivity.takeFilePermissions(this);
      return;
    }
    if (!file.isFile()) {
      return;
    }
    binding.drawerLayout.closeDrawer(GravityCompat.START);
    int openedFileIndex = indexOfDocument(file.getPath());
    if (openedFileIndex != -1) {
      panelsManager.getPanelArea().setSelectedPanel(panelsManager.getPanel(openedFileIndex));
      return;
    }

    EditorPanel editorPanel = new EditorPanel(this, DocumentModel.fileModelToDocument(file));
    panelsManager.addPanel(editorPanel, true);
  }

  public int indexOfDocument(String path) {
    for (int i = 0; i < panelsManager.getPanelAreaPanels().size(); i++) {
      Panel panel = panelsManager.getPanel(i);
      if (panel instanceof EditorPanel) {
        EditorPanel editorPanel = (EditorPanel) panel;
        if (editorPanel.getDocument().getPath().equals(path)) {
          return i;
        }
      }
    }
    return -1;
  }

  public int getUnsavedDocumentsCount() {
    int count = 0;
    for (Panel panel : panelsManager.getPanelAreaPanels()) {
      if (panel instanceof EditorPanel) {
        EditorPanel editor = (EditorPanel) panel;
        if (editor.getDocument().isModified()) {
          count++;
        }
      }
    }
    return count;
  }

  // Document Savers

  public void saveFile(boolean showMsg) {
    if (!panelsManager.getPanelAreaPanels().isEmpty()) {
      TaskExecutor.executeAsync(
          () -> {
            saveDocument(panelsManager.getSelectedPanel());
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
    if (!panelsManager.getPanelAreaPanels().isEmpty()) {
      TaskExecutor.executeAsync(
          () -> {
            for (int i = 0; i < panelsManager.getPanelAreaPanels().size(); i++) {
              Panel panel = panelsManager.getPanel(i);
              if (panel instanceof EditorPanel) {
                saveDocument(panel);
              }
            }
            savePanels();
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

  private void saveDocument(Panel panel) {
    if (panel != null && panel instanceof EditorPanel) {
      EditorPanel editorPanel = (EditorPanel) panel;
      TabLayout.Tab tab =
          panelsManager
              .getPanelArea()
              .getTabLayout()
              .getTabAt(panelsManager.getPanelAreaPanels().indexOf(panel));
      if (tab != null) {
        editorPanel.saveDocument();
        markUnmodifiedTab(tab);
      }
    }
  }

  public EditorPanel getSelectedEditorPanel() {
    Panel panel = panelsManager.getSelectedPanel();
    if (panel != null && panel instanceof EditorPanel) {
      return (EditorPanel) panel;
    }
    return null;
  }

  public WebViewPanel getSelectedWebViewPanel() {
    Panel panel = panelsManager.getSelectedPanel();
    if (panel != null && panel instanceof WebViewPanel) {
      return (WebViewPanel) panel;
    }
    return null;
  }

  public void onRemovePanel() {
    if (panelsManager.getPanelAreaPanels().isEmpty()) {
      binding.pathList.setPath(null);
      invalidateOptionsMenu();
    }
    savePanels();
  }

  public void updateCurrentPanel(Panel panel) {
    if (panel instanceof EditorPanel) {
      EditorPanel editorPanel = (EditorPanel) panel;
      var editor = editorPanel.getEditor();
      var document = editorPanel.getDocument();
      binding.symbolInput.bindEditor(editor);
      binding.pathList.setPath(document.getPath());

      panelsManager.sendEvent(new UpdateSearcherEvent(editorPanel.getEditor().getSearcher()));
      panelsManager.sendEvent(
          new UpdateExecutePanelEvent(
              document.getPath(),
              FileUtils.getFileExtension(document.getPath()),
              editorPanel.getCode()));
    } else {
      binding.pathList.setPath(null);
    }
    invalidateOptionsMenu();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onFileRenamed(OnFileRenamedEvent event) {
    var index = indexOfDocument(event.oldFile.getPath());
    if (index != -1 && index >= 0) {
      Panel panel = panelsManager.getPanel(index);
      if (panel instanceof EditorPanel) {
        EditorPanel editorPanel = (EditorPanel) panel;
        var document = editorPanel.getDocument();
        document.setName(event.newFile.getName());
        document.setPath(event.newFile.getAbsolutePath());
        editorPanel.setDocument(document);
      }
      updateCurrentPanel(panelsManager.getSelectedPanel());
      updateTabs();
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEditorContentChanged(EditorContentChangedEvent event) {
    DocumentModel document = event.getDocument();
    document.markModified();
    invalidateOptionsMenu();
    int index = indexOfDocument(document.getPath());
    if (index == -1) {
      return;
    }
    if (!PreferencesUtils.autoSave()) {
      TabLayout.Tab tab = panelsManager.getPanelArea().getTabLayout().getTabAt(index);
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

  private void markUnmodifiedTab(TabLayout.Tab tab) {
    runOnUiThread(
        () -> {
          TextView tabTitle = tab.getCustomView().findViewById(id.title);
          String name = tabTitle.getText().toString();
          if (name.startsWith("*")) {
            tabTitle.setText(name.replace("*", ""));
          }
        });
  }

  public void updateTabs() {
    TaskExecutor.executeAsyncProvideError(
        () -> getUniqueNames(),
        (result, error) -> {
          if (result == null || error != null) {
            return;
          }

          result.forEach(
              (index, name) -> {
                TabLayout.Tab tab = panelsManager.getPanelArea().getTabLayout().getTabAt(index);
                if (tab != null) {
                  TextView tabTitle = tab.getCustomView().findViewById(id.title);
                  tabTitle.setText(name);
                }
              });
        });
  }

  private Map<Integer, String> getUniqueNames() {
    List<Panel> panels = panelsManager.getPanelAreaPanels();
    Map<String, Integer> dupliCount = new HashMap<>();
    Map<Integer, String> names = new HashMap<>();
    UniqueNameBuilder<DocumentModel> nameBuilder = new UniqueNameBuilder<>("", File.separator);

    for (Panel panel : panels) {
      if (panel instanceof EditorPanel) {
        EditorPanel editorPanel = (EditorPanel) panel;
        DocumentModel document = editorPanel.getDocument();
        int count = dupliCount.getOrDefault(document.getName(), 0);
        dupliCount.put(document.getName(), ++count);
        nameBuilder.addPath(document, document.getPath());
      }
    }

    for (int i = 0; i < panelsManager.getPanelArea().getTabLayout().getTabCount(); i++) {
      Panel panel = panels.get(i);
      if (panel instanceof EditorPanel) {
        EditorPanel editorPanel = (EditorPanel) panel;
        DocumentModel document = editorPanel.getDocument();

        int count = dupliCount.getOrDefault(document.getName(), 0);
        boolean isModified = document.isModified();
        String name = (count > 1) ? nameBuilder.getShortPath(document) : document.getName();
        if (isModified) {
          name = "*" + name;
        }
        names.put(i, name);
      }
    }
    return names;
  }

  public void savePanels() {
    List<Panel> panels = panelsManager.getPanelAreaPanels();
    try {
      String json = PanelUtils.panelsToJson(panels);
      FileIOUtils.writeFileFromString(
          RECENT_PANELS_PATH, EncodeUtils.base64Encode2String(json.getBytes()));
    } catch (Exception err) {
      err.printStackTrace();
      ToastUtils.showShort(err.getLocalizedMessage(), ToastUtils.TYPE_ERROR);
    }
  }

  private void openRecentPanels() {
    try {
      List<Panel> panels =
          PanelUtils.jsonToPanels(
              this,
              new String(
                  EncodeUtils.base64Decode(FileIOUtils.readFile2String(RECENT_PANELS_PATH))));
      for (Panel panel : panels) {
        panelsManager.addPanel(panel, false);
      }
      updateTabs();
    } catch (Exception e) {
      e.printStackTrace();
      ToastUtils.showShort(e.getLocalizedMessage(), ToastUtils.TYPE_ERROR);
    }
  }
}
