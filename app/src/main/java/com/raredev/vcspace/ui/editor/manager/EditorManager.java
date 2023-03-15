package com.raredev.vcspace.ui.editor.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ViewFlipper;
import androidx.core.view.GravityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.drawerlayout.widget.DrawerLayout;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.tabs.TabLayout;
import com.raredev.common.util.DialogUtils;
import com.raredev.common.util.FileUtil;
import com.raredev.vcspace.R;
import com.raredev.vcspace.activity.MainActivity;
import com.raredev.vcspace.databinding.ActivityMainBinding;
import com.raredev.vcspace.ui.editor.CodeEditorView;
import com.raredev.vcspace.ui.editor.EditorViewModel;
import java.io.File;
import java.util.List;

public class EditorManager {

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
  
  public void tryOpenFileFromIntent(Intent it) {
    try {
      Uri uri = it.getData();
      if (uri != null) {
        DocumentFile receivedFile = DocumentFile.fromSingleUri(context, uri);
        File file = FileUtil.getFileFromUri(context, receivedFile.getUri());

        openFile(file);
      }
    } catch (Exception e) {
      DialogUtils.newErrorDialog(
          context,
          context.getString(R.string.error),
          context.getString(R.string.error_editor_opening_recent_files) + "\n\n" + e.toString());
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
    int index = findIndexOfEditorByFile(file);
    if (index != -1) {
      return index;
    }
    int position = viewModel.getFiles().getValue().size();

    CodeEditorView editor = new CodeEditorView(context, file);
    editor.subscribeContentChangeEvent(((MainActivity) context).updateMenuItem);
    container.addView(editor);

    tabLayout.addTab(tabLayout.newTab().setText(file.getName()));
    viewModel.addFile(file);
    return position;
  }

  public void closeFile(int index) {
    if (index >= 0 && index < viewModel.getFiles().getValue().size()) {
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

    while (viewModel.getFiles().getValue().size() != 1) {
      CodeEditorView editor = getEditorAtIndex(index);

      if (editor != null) {
        if (file != editor.getFile()) {
          closeFile(index);
        } else {
          index = 1;
        }
      }
    }
    int size = viewModel.getFiles().getValue().size() -1;
    viewModel.setCurrentPosition(size, file);
    setCurrent(size);
  }

  public void closeAllFiles() {
    if (!viewModel.getFiles().getValue().isEmpty()) {
      for (int i = 0; i < viewModel.getFiles().getValue().size(); i++) {
        CodeEditorView editor = getEditorAtIndex(i);
        if (editor != null) {
          editor.release();
        }
      }

      viewModel.clear();
      tabLayout.removeAllTabs();
      tabLayout.requestLayout();
      container.removeAllViews();
    }
  }

  public void saveAllFiles(boolean showMsg) {
    if (!viewModel.getFiles().getValue().isEmpty()) {
      for (int i = 0; i < viewModel.getFiles().getValue().size(); i++) {
        getEditorAtIndex(i).save();
      }

      if (showMsg) {
        ToastUtils.showShort(R.string.saved_files);
      }
    }
  }

  public void onFileDeleted() {
    for (int i = 0; i < viewModel.getFiles().getValue().size(); i++) {
      File openedFile = viewModel.getFiles().getValue().get(i);
      if (!openedFile.exists()) {
        closeFile(i);
      }
    }
  }

  public int findIndexOfEditorByFile(File file) {
    for (int i = 0; i < viewModel.getFiles().getValue().size(); i++) {
      File openedFile = viewModel.getFiles().getValue().get(i);
      if (openedFile == file) {
        return i;
      }
    }
    return -1;
  }

  public CodeEditorView getEditorAtIndex(int index) {
    return (CodeEditorView) container.getChildAt(index);
  }

  public CodeEditorView getCurrentEditor() {
    return (CodeEditorView) container.getChildAt(viewModel.getCurrentPosition());
  }
  
  private void setCurrent(int index) {
    final var tab = tabLayout.getTabAt(index);
    if (tab != null && index >= 0 && !tab.isSelected()) {
      tab.select();
    }
  }
}
