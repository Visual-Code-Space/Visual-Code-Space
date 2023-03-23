package com.raredev.vcspace.activity;

import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuCompat;
import androidx.lifecycle.ViewModelProvider;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.tabs.TabLayout;
import com.raredev.common.task.TaskExecutor;
import com.raredev.common.util.DialogUtils;
import com.raredev.common.util.FileUtil;
import com.raredev.common.util.Utils;
import com.raredev.vcspace.R;
import com.raredev.vcspace.SimpleExecuter;
import com.raredev.vcspace.actions.ActionData;
import com.raredev.vcspace.actions.ActionManager;
import com.raredev.vcspace.actions.ActionPlaces;
import com.raredev.vcspace.databinding.ActivityMainBinding;
import com.raredev.vcspace.fragments.ToolsFragment;
import com.raredev.vcspace.ui.editor.CodeEditorView;
import com.raredev.vcspace.ui.viewmodel.EditorViewModel;
import com.raredev.vcspace.ui.editor.Symbol;
import com.raredev.vcspace.ui.editor.manager.EditorManager;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;
import io.github.rosemoe.sora.widget.CodeEditor;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.eclipse.tm4e.core.registry.IThemeSource;

public class MainActivity extends VCSpaceActivity {
  public ActivityMainBinding binding;

  public EditorViewModel viewModel;
  public EditorManager editorManager;

  private MenuItem undo;
  private MenuItem redo;

  public final Runnable updateMenuItem =
      () -> {
        CodeEditorView editor = editorManager.getCurrentEditor();
        if (editor != null) {
          undo.setEnabled(editor.getEditor().canUndo());
          redo.setEnabled(editor.getEditor().canRedo());
        }
      };
  private ActivityResultLauncher<Intent> launcher;
  private ActivityResultLauncher<String> createFile;
  private ActivityResultLauncher<String> pickFile;

  @Override
  public View getLayout() {
    binding = ActivityMainBinding.inflate(getLayoutInflater());
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
    editorManager = new EditorManager(this, binding, viewModel);
    initialize();

    binding.tabLayout.addOnTabSelectedListener(
        new TabLayout.OnTabSelectedListener() {
          @Override
          public void onTabUnselected(TabLayout.Tab p1) {}

          @Override
          public void onTabReselected(TabLayout.Tab p1) {
            ActionData data = new ActionData();
            data.put("activity", MainActivity.this);

            ActionManager.getInstance()
                .fillMenu(MainActivity.this, p1.view, data, ActionPlaces.EDITOR);
          }

          @Override
          public void onTabSelected(TabLayout.Tab p1) {
            int position = p1.getPosition();
            CodeEditorView editor = editorManager.getEditorAtIndex(position);
            viewModel.setCurrentFile(position, editor.getFile());

            binding.searcher.bindEditor(editor.getEditor());
            refreshSymbolInput(editor.getEditor());
            invalidateOptionsMenu();
          }
        });
    registerResultActivity();
    observeViewModel();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    MenuCompat.setGroupDividerEnabled(menu, true);
    menu.findItem(R.id.menu_terminal).setVisible(false);
    undo = menu.findItem(R.id.menu_undo);
    redo = menu.findItem(R.id.menu_redo);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    if (!viewModel.getOpenedFiles().isEmpty()) {
      menu.findItem(R.id.menu_save).setEnabled(true);
      menu.findItem(R.id.menu_save_as).setEnabled(true);
      menu.findItem(R.id.menu_save_all).setEnabled(true);
      menu.findItem(R.id.menu_undo).setVisible(true);
      menu.findItem(R.id.menu_redo).setVisible(true);
      menu.findItem(R.id.menu_edit).setVisible(true);

      File file = viewModel.getCurrentFile();
      if (file != null) {
        menu.findItem(R.id.menu_compile).setVisible(SimpleExecuter.isExecutable(file));
      }
      updateMenuItem.run();
    } else {
      menu.findItem(R.id.menu_save).setEnabled(false);
      menu.findItem(R.id.menu_save_as).setEnabled(false);
      menu.findItem(R.id.menu_save_all).setEnabled(false);
      menu.findItem(R.id.menu_undo).setVisible(false);
      menu.findItem(R.id.menu_redo).setVisible(false);
      menu.findItem(R.id.menu_compile).setVisible(false);
      menu.findItem(R.id.menu_edit).setVisible(false);
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    final CodeEditorView editor = editorManager.getCurrentEditor();
    switch (id) {
      case R.id.menu_undo:
        editor.undo();
        break;
      case R.id.menu_redo:
        editor.redo();
        break;
      case R.id.menu_save:
        editorManager.getCurrentEditor().save();
        ToastUtils.showShort(R.string.saved);
        break;
      case R.id.menu_save_as:
        saveAs(viewModel.getCurrentFile());
        break;
      case R.id.menu_save_all:
        editorManager.saveAllFiles(true);
        break;
      case R.id.menu_compile:
        editorManager.saveAllFiles(false);
        new SimpleExecuter(this, viewModel.getCurrentFile());
        break;
      case R.id.menu_format:
        editor.getEditor().formatCodeAsync();
        break;
      case R.id.menu_search:
        binding.searcher.showAndHide();
        break;
      case R.id.menu_settings:
        editorManager.saveAllFiles(false);
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        break;
      case R.id.menu_terminal:
        // startActivity(new Intent(getApplicationContext(), TerminalActivity.class));
        break;
      case R.id.menu_open_folder:
        ((ToolsFragment) getSupportFragmentManager().findFragmentByTag("tools_fragment"))
            .mStartForResult.launch(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE));
        break;
      case R.id.menu_open_recent:
        ((ToolsFragment) getSupportFragmentManager().findFragmentByTag("tools_fragment"))
            .getTreeViewFragment()
            .tryOpenRecentFolder();
        if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START))
          binding.drawerLayout.openDrawer(GravityCompat.START);
        break;
      case R.id.menu_new_file:
        createFile.launch("untitled");
        break;
      case R.id.menu_open_file:
        pickFile.launch("text/*");
        break;
    }
    return true;
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
    editorManager.saveAllFiles(false);
    super.onBackPressed();
  }

  private synchronized void initialize() {
    TaskExecutor.executeAsyncProvideError(
        () -> {
          loadTextMate();
          return null;
        },
        (result, error) -> {
          if (error != null) {
            DialogUtils.newErrorDialog(this, getString(R.string.error), error.toString());
          }
          viewModel.removeAllFiles();
          editorManager.tryOpenFileFromIntent(getIntent());
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
                  outputStream.write(
                      editorManager.getCurrentEditor().getEditor().getText().toString().getBytes());
                  outputStream.close();
                  editorManager.openFile(FileUtil.getFileFromUri(MainActivity.this, uri));
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
            });
    createFile =
        registerForActivityResult(
            new ActivityResultContracts.CreateDocument("text/*"), this::onCreateNewFile);
    pickFile =
        registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
              if (uri != null) {
                try {
                  editorManager.openFile(FileUtil.getFileFromUri(this, uri));
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
            });
  }

  private void observeViewModel() {
    viewModel.observeFiles(
        this,
        (files) -> {
          if (files.isEmpty()) {
            binding.tabLayout.setVisibility(View.GONE);
            binding.layout.setVisibility(View.GONE);
            binding.noFileOpened.setVisibility(View.VISIBLE);
            binding.searcher.hide();
          } else {
            binding.tabLayout.setVisibility(View.VISIBLE);
            binding.layout.setVisibility(View.VISIBLE);
            binding.noFileOpened.setVisibility(View.GONE);
          }
        });

    viewModel
        .getDisplayedFile()
        .observe(this, (index) -> binding.container.setDisplayedChild(index));
  }

  private void loadTextMate() throws Exception {
    // Load editor themes
    FileProviderRegistry.getInstance().addFileProvider(new AssetsFileResolver(getAssets()));

    String[] themes = new String[] {"vcspace_dark", "vcspace_light"};
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
    // Register current theme
    ThemeRegistry.getInstance().setTheme(Utils.isDarkMode(this) ? "vcspace_dark" : "vcspace_light");
  }

  private void refreshSymbolInput(CodeEditor editor) {
    binding.symbolInput.setSymbols(Symbol.baseSymbols());
    binding.symbolInput.bindEditor(editor);
  }

  private void saveAs(File fileToSave) {
    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.setType("text/*");
    intent.putExtra(Intent.EXTRA_TITLE, fileToSave.getName());

    launcher.launch(intent);
  }

  private void onCreateNewFile(Uri uri) {
    if (uri != null) {
      try {
        editorManager.openFile(FileUtil.getFileFromUri(this, uri));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
