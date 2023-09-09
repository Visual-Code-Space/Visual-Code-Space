package com.raredev.vcspace.activity;

import static com.raredev.vcspace.res.R.id;
import static com.raredev.vcspace.res.R.string;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.UriUtils;
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
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.ui.panels.Panel;
import com.raredev.vcspace.ui.panels.PanelsManager;
import com.raredev.vcspace.ui.panels.compiler.ExecutePanel;
import com.raredev.vcspace.ui.panels.compiler.WebViewPanel;
import com.raredev.vcspace.ui.panels.editor.EditorPanel;
import com.raredev.vcspace.ui.panels.editor.SearcherPanel;
import com.raredev.vcspace.ui.panels.editor.UserSnippetsPanel;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.Logger;
import com.raredev.vcspace.util.PanelUtils;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.ToastUtils;
import com.raredev.vcspace.util.Utils;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class EditorActivity extends BaseActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener {

  public static final String RECENT_PANELS_PATH =
      PathUtils.getExternalAppDataPath() + "/files/recentPanels/editorPanels.json";

  private final Logger logger = Logger.newInstance("EditorActivity");

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

    panelsManager = new PanelsManager(this, binding.panelArea);

    KeyboardUtils.registerSoftInputChangedListener(this, (i) -> invalidateOptionsMenu());

    CompletionProvider.registerCompletionProviders();
    ThemeRegistry.getInstance().setTheme(Utils.isDarkMode() ? "darcula" : "quietlight");
    PreferencesUtils.getDefaultPrefs().registerOnSharedPreferenceChangeListener(this);
    registerResultActivity();

    openRecentPanels();
    if (isPermissionGaranted(this)) {
      Uri fileUri = getIntent().getData();
      if (fileUri != null) {
        logger.i("Opening file from Uri: " + fileUri.toString());
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
        executeDocument(document);
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
      else if (id == R.id.menu_reload) editorPanel.reloadFile();

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
    else if (id == R.id.menu_usersnippets)
      panelsManager.addFloatingPanel(UserSnippetsPanel.createFloating(this, binding.panelArea));
    else if (id == R.id.menu_terminal) startActivity(new Intent(this, TerminalActivity.class));
    else if (id == R.id.menu_settings) startActivity(new Intent(this, SettingsActivity.class));

    return true;
  }

  @Override
  public void onBackPressed() {
    WebViewPanel webViewPanel = getSelectedWebViewPanel();
    if (webViewPanel != null && webViewPanel.getWebView().canGoBack()) {
      webViewPanel.getWebView().goBack();
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
    panelsManager.sendEvent(new PreferenceChangedEvent(key));
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
                    e.printStackTrace();
                    logger.e(e);
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
                  e.printStackTrace();
                  logger.e(e);
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
                  e.printStackTrace();
                  logger.e(e);
                }
              }
            });
  }

  private void executeDocument(DocumentModel document) {
    saveAllFiles(false);
    if (document.getName().endsWith(".html")) {
      panelsManager.addWebViewPanel(document.getPath());
    } else {
      panelsManager.addFloatingPanel(ExecutePanel.createFloating(this, binding.panelArea));
      panelsManager.sendEvent(
          new UpdateExecutePanelEvent(
              document.getPath(), FileUtils.getFileExtension(document.getPath())));
    }
  }

  // Document Opener

  public void openFile(@NonNull FileModel file) {
    if (!file.isFile()) {
      return;
    }
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
      editorPanel.saveDocument();
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

  public void updateCurrentPanel(Panel panel) {
    if (panel instanceof EditorPanel) {
      EditorPanel editorPanel = (EditorPanel) panel;
      var editor = editorPanel.getEditor();
      var document = editorPanel.getDocument();

      panelsManager.sendEvent(new UpdateSearcherEvent(editorPanel.getEditor().getSearcher()));
      panelsManager.sendEvent(
          new UpdateExecutePanelEvent(
              document.getPath(), FileUtils.getFileExtension(document.getPath())));
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
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEditorContentChanged(EditorContentChangedEvent event) {
    invalidateOptionsMenu();
  }

  public void savePanels() {
    List<Panel> panels = panelsManager.getPanelAreaPanels();
    try {
      String json = PanelUtils.panelsToJson(panels);
      FileIOUtils.writeFileFromString(
          RECENT_PANELS_PATH, EncodeUtils.base64Encode2String(json.getBytes()));
    } catch (Exception e) {
      e.printStackTrace();
      logger.e(e);
    }
  }

  private void openRecentPanels() {
    try {
      var json =
          new String(EncodeUtils.base64Decode(FileIOUtils.readFile2String(RECENT_PANELS_PATH)));
      PanelUtils.addJsonPanelsInArea(this, json, panelsManager.getPanelArea());
    } catch (Exception e) {
      e.printStackTrace();
      logger.e(e);
    }
  }
}
