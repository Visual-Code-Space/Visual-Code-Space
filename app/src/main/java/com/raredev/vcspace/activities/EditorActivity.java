package com.raredev.vcspace.activities;

import static com.raredev.vcspace.res.R.id;
import static com.raredev.vcspace.res.R.string;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.drawerlayout.widget.DrawerLayout;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.UriUtils;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.ActivityEditorBinding;
import com.raredev.vcspace.editor.completion.CompletionProvider;
import com.raredev.vcspace.events.OnFileRenamedEvent;
import com.raredev.vcspace.events.PreferenceChangedEvent;
import com.raredev.vcspace.events.UpdateExecutePanelEvent;
import com.raredev.vcspace.events.UpdateSearcherEvent;
import com.raredev.vcspace.models.DocumentModel;
import com.raredev.vcspace.models.FileModel;
import com.raredev.vcspace.ui.panels.Panel;
import com.raredev.vcspace.ui.panels.PanelsManager;
import com.raredev.vcspace.ui.panels.compiler.ExecutePanel;
import com.raredev.vcspace.ui.panels.editor.EditorPanel;
import com.raredev.vcspace.ui.panels.searcher.SearcherPanel;
import com.raredev.vcspace.ui.panels.snippets.SnippetsPanel;
import com.raredev.vcspace.ui.panels.web.WebViewPanel;
import com.raredev.vcspace.utils.Logger;
import com.raredev.vcspace.utils.PanelUtils;
import com.raredev.vcspace.utils.PreferencesUtils;
import com.raredev.vcspace.utils.ToastUtils;
import com.raredev.vcspace.utils.Utils;
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
  protected View getLayout() {
    binding = ActivityEditorBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setSupportActionBar(binding.toolbar);
    setupWorkspaceDrawer();

    panelsManager = new PanelsManager(this, binding.workspaceArea, binding.panelArea);

    KeyboardUtils.registerSoftInputChangedListener(this, (i) -> invalidateOptionsMenu());

    CompletionProvider.registerCompletionProviders();
    ThemeRegistry.getInstance().setTheme(Utils.isDarkMode() ? "darcula" : "quietlight");
    PreferencesUtils.getDefaultPrefs().registerOnSharedPreferenceChangeListener(this);
    registerResultActivity();
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    EditorPanel editorPanel = panelsManager.getPanelArea().getSelectedEditorPanel();
    WebViewPanel webViewPanel = panelsManager.getPanelArea().getSelectedWebViewPanel();
    if (editorPanel != null) {
      var document = editorPanel.getDocument();
      menu.findItem(R.id.menu_execute).setVisible(true);
      menu.findItem(R.id.menu_editor).setVisible(!KeyboardUtils.isSoftInputVisible(this));
      menu.findItem(R.id.menu_undo).setVisible(KeyboardUtils.isSoftInputVisible(this));
      menu.findItem(R.id.menu_redo).setVisible(KeyboardUtils.isSoftInputVisible(this));
      menu.findItem(R.id.menu_undo).setEnabled(editorPanel.getEditor().canUndo());
      menu.findItem(R.id.menu_redo).setEnabled(editorPanel.getEditor().canRedo());
      menu.findItem(R.id.menu_save).setEnabled(document.isModified());
      menu.findItem(R.id.menu_save_as).setEnabled(true);
      menu.findItem(R.id.menu_save_all)
          .setEnabled(panelsManager.getPanelArea().getUnsavedDocumentsCount() > 0);
      menu.findItem(R.id.menu_reload).setEnabled(true);
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
    EditorPanel editorPanel = panelsManager.getPanelArea().getSelectedEditorPanel();
    WebViewPanel webViewPanel = panelsManager.getPanelArea().getSelectedWebViewPanel();
    if (editorPanel != null) {
      var document = editorPanel.getDocument();
      if (id == R.id.menu_execute) executeDocument(document);
      else if (id == R.id.menu_undo) editorPanel.undo();
      else if (id == R.id.menu_redo) editorPanel.redo();
      else if (id == R.id.menu_search) {
        panelsManager.addFloatingPanel(SearcherPanel.createFloating(this, binding.panelArea));
        panelsManager.sendEvent(new UpdateSearcherEvent(editorPanel.getEditor().getSearcher()));
      } else if (id == R.id.menu_save)
        panelsManager.getPanelArea().saveFile(true, () -> invalidateOptionsMenu());
      else if (id == R.id.menu_save_as) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_TITLE, editorPanel.getDocument().getName());
        launcher.launch(intent);
      } else if (id == R.id.menu_save_all)
        panelsManager.getPanelArea().saveAllFiles(true, () -> invalidateOptionsMenu());
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
    else if (id == R.id.menu_snippets)
      panelsManager.addFloatingPanel(SnippetsPanel.createFloating(this, binding.panelArea));
    else if (id == R.id.menu_terminal) startActivity(new Intent(this, TerminalActivity.class));
    else if (id == R.id.menu_settings) startActivity(new Intent(this, SettingsActivity.class));

    return true;
  }

  @Override
  public void onBackPressed() {
    if (binding.drawerLayout.isDrawerOpen(Gravity.START)) {
      binding.drawerLayout.closeDrawer(Gravity.START);
      return;
    }
    WebViewPanel webViewPanel = panelsManager.getPanelArea().getSelectedWebViewPanel();
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
    if (panelsManager.getPanelArea().getPanels().isEmpty()) {
      openRecentPanels();
      panelsManager.addDefaultPanels();
    }

    Uri fileUri = getIntent().getData();
    if (fileUri != null) {
      logger.i("Opening file from Uri: " + fileUri.toString());
      openFile(UriUtils.uri2File(fileUri).getPath());
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
  protected void onPause() {
    super.onPause();
    savePanels();
  }

  @Override
  protected void onDestroy() {
    PreferencesUtils.getDefaultPrefs().unregisterOnSharedPreferenceChangeListener(this);
    unregisterResultActivity();
    super.onDestroy();
    panelsManager = null;
    binding = null;
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
    panelsManager.sendEvent(new PreferenceChangedEvent(key));
  }

  private void setupWorkspaceDrawer() {
    DrawerLayout drawerLayout = binding.drawerLayout;

    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(this, drawerLayout, binding.toolbar, string.open, string.close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    drawerLayout.addDrawerListener(
        new DrawerLayout.DrawerListener() {
          @Override
          public void onDrawerSlide(@NonNull View view, float v) {
            float slideX = view.getWidth() * v;
            binding.root.setTranslationX(slideX);
          }

          @Override
          public void onDrawerOpened(@NonNull View view) {}

          @Override
          public void onDrawerClosed(@NonNull View view) {}

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
                EditorPanel editorPanel = panelsManager.getPanelArea().getSelectedEditorPanel();
                if (editorPanel != null) {
                  Uri uri = result.getData().getData();
                  try {
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    outputStream.write(editorPanel.getCode().getBytes());
                    outputStream.close();
                    openFile(UriUtils.uri2File(uri).getPath());
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
                openFile(UriUtils.uri2File(uri).getPath());
              }
            });
    pickFile =
        registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
              if (uri != null) {
                openFile(UriUtils.uri2File(uri).getPath());
              }
            });
  }

  private void unregisterResultActivity() {
    launcher.unregister();
    createFile.unregister();
    pickFile.unregister();
  }

  private void executeDocument(DocumentModel document) {
    panelsManager.getPanelArea().saveAllFiles(false);
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
    openFile(file.getPath());
  }

  public void openFile(@NonNull String path) {
    if (binding.drawerLayout.isDrawerOpen(Gravity.START)) {
      binding.drawerLayout.closeDrawer(Gravity.START);
    }
    int openedFileIndex = panelsManager.getPanelArea().indexOfFile(path);
    if (openedFileIndex != -1) {
      panelsManager.getPanelArea().setSelectedPanel(panelsManager.getPanel(openedFileIndex));
      return;
    }

    EditorPanel editorPanel =
        new EditorPanel(this, new DocumentModel(path, FileUtils.getFileName(path)));
    panelsManager.addPanel(editorPanel, true);
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
    var index = panelsManager.getPanelArea().indexOfFile(event.oldFile.getPath());
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
