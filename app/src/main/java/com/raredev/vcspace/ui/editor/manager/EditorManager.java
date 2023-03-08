package com.raredev.vcspace.ui.editor.manager;

import android.content.Context;
import android.widget.ViewFlipper;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.tabs.TabLayout;
import com.raredev.common.util.FileUtil;
import com.raredev.vcspace.ui.editor.CodeEditorView;
import com.raredev.vcspace.ui.editor.EditorViewModel;
import java.io.File;

public class EditorManager {
  private ViewFlipper container;
  private TabLayout tabLayout;

  private Context context;

  private EditorViewModel viewModel;

  public EditorManager(Context context, ViewFlipper container, TabLayout tabLayout) {
    this.context = context;
    this.container = container;
    this.tabLayout = tabLayout;

    viewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(EditorViewModel.class);
  }

  public EditorViewModel getViewModel() {
    return viewModel;
  }

  public void undo() {
    getCurrentEditor().undo();
  }

  public void redo() {
    getCurrentEditor().redo();
  }

  public void openFile(File file) {
    viewModel.setDrawerState(false);
    if (!file.exists()) {
      return;
    }

    if (viewModel.contains(file)) {
      setCurrentPosition(viewModel.indexOf(file));
      return;
    }
    viewModel.openFile(file);
    CodeEditorView editor = new CodeEditorView(context, file);

    container.addView(editor);

    tabLayout.addTab(tabLayout.newTab().setText(file.getName()));
    setCurrentPosition(viewModel.indexOf(file));
  }

  public void closeFile(int index) {
    File file = viewModel.getFiles().getValue().get(index);
    if (viewModel.contains(file)) {
      if (file.exists()) {
        getEditorAtIndex(index).save();
      }

      getEditorAtIndex(index).release();
      viewModel.removeFile(file);
      tabLayout.removeTabAt(index);
      container.removeViewAt(index);
    }
    tabLayout.requestLayout();
  }

  public void closeOthers() {
    saveAllFiles(false);

    File editorFile = getEditorAtIndex(viewModel.getCurrentPosition()).getFile();
    for (int i = 0; i < viewModel.getFiles().getValue().size(); i++) {
      if (editorFile != viewModel.getFiles().getValue().get(i)) {
        closeFile(i);
      }
    }
  }

  public void closeAllFiles() {
    saveAllFiles(false);
    for (int i = 0; i < viewModel.getFiles().getValue().size(); i++) {
      getEditorAtIndex(i).release();
    }
    container.removeAllViews();
    tabLayout.removeAllTabs();
    viewModel.clear();
  }

  public void saveAllFiles(boolean showMsg) {
    if (!viewModel.getFiles().getValue().isEmpty()) {
      for (int i = 0; i < viewModel.getFiles().getValue().size(); i++) {
        getEditorAtIndex(i).save();
      }
      
      if (showMsg) {
        ToastUtils.showShort("All Files Saved");
      }
    }
  }

  public CodeEditorView getEditorAtIndex(int index) {
    return (CodeEditorView) container.getChildAt(index);
  }

  public CodeEditorView getCurrentEditor() {
    return (CodeEditorView) container.getChildAt(viewModel.getCurrentPosition());
  }

  public void setCurrentPosition(int index) {
    final var tab = tabLayout.getTabAt(index);
    if (tab != null && index >= 0 && !tab.isSelected()) {
      tab.select();
    }
    viewModel.setCurrentPosition(index);
  }
}
