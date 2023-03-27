package com.raredev.vcspace.ui.editor.manager;

import android.content.Context;
import android.widget.ViewFlipper;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.tabs.TabLayout;
import com.raredev.vcspace.R;
import com.raredev.vcspace.activity.MainActivity;
import com.raredev.vcspace.databinding.ActivityMainBinding;
import com.raredev.vcspace.util.ILogger;
import com.raredev.vcspace.ui.editor.CodeEditorView;
import com.raredev.vcspace.ui.viewmodel.EditorViewModel;
import java.io.File;

public class EditorManager {
  private final String LOG_TAG = EditorManager.class.getSimpleName();

  private DrawerLayout drawerLayout;
  private ViewFlipper container;
  private TabLayout tabLayout;

  private Context context;

  private EditorViewModel viewModel;

  public EditorManager(Context context, ActivityMainBinding binding, EditorViewModel viewModel) {
    this.context = context;

    this.drawerLayout = binding.drawerLayout;
    this.container = binding.container;
    this.tabLayout = binding.tabLayout;
    this.viewModel = viewModel;
  }

  public void onSharedPreferenceChanged(String key) {
    for (int i = 0; i < viewModel.getOpenedFileCount(); i++) {
      CodeEditorView editor = getEditorAtIndex(i);
      if (editor != null) {
        editor.onSharedPreferenceChanged(key);
      }
    }
  }

  public void openFile(File file) {
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START);
    }
    if (file == null) {
      return;
    }
    if (!file.isFile() && !file.exists()) {
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

    ILogger.info(LOG_TAG, "Opening file: " + file.toString() + " index: " + index);

    CodeEditorView editor = new CodeEditorView(context, file);
    editor.subscribeContentChangeEvent(() -> ((MainActivity) context).invalidateOptionsMenu());
    container.addView(editor);

    tabLayout.addTab(tabLayout.newTab().setText(file.getName()));
    viewModel.addFile(file);
    return index;
  }

  public void closeFile(int index) {
    if (index >= 0 && index < viewModel.getOpenedFileCount()) {
      ILogger.info(LOG_TAG, "Closing file: " + viewModel.getOpenedFiles().get(index).toString());
      CodeEditorView editor = getEditorAtIndex(index);
      if (editor != null) {
        editor.release();
      }

      viewModel.removeFile(index);
      tabLayout.removeTabAt(index);
      container.removeViewAt(index);
    }
    tabLayout.requestLayout();
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
    tabLayout.removeAllTabs();
    tabLayout.requestLayout();
    container.removeAllViews();
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
    return (CodeEditorView) container.getChildAt(index);
  }

  public CodeEditorView getCurrentEditor() {
    return (CodeEditorView) container.getChildAt(viewModel.getCurrentFileIndex());
  }

  private void setCurrent(int index) {
    final var tab = tabLayout.getTabAt(index);
    if (tab != null && index >= 0 && !tab.isSelected()) {
      tab.select();
    }
  }
}
