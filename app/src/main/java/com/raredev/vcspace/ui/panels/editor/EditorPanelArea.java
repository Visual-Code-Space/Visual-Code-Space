package com.raredev.vcspace.ui.panels.editor;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.FrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.ui.panels.Panel;
import com.raredev.vcspace.ui.panels.PanelArea;
import com.raredev.vcspace.ui.panels.web.WebViewPanel;
import com.raredev.vcspace.utils.ToastUtils;
import java.util.ArrayList;
import java.util.List;

public class EditorPanelArea extends PanelArea {

  public EditorPanelArea(Context context, FrameLayout parent) {
    super(context, parent);
  }

  @Override
  public void onTabSelected(TabLayout.Tab tab) {
    super.onTabSelected(tab);
  }

  @Override
  public void onTabUnselected(TabLayout.Tab tab) {
    super.onTabUnselected(tab);
  }

  @Override
  public boolean removePanel(Panel panel) {
    var editorPanel = getEditorPanel(panel);
    if (editorPanel != null && editorPanel.getDocument().isModified()) {
      notifyUnsavedFile(editorPanel, () -> removePanel(panel));
      return false;
    }
    return super.removePanel(panel);
  }

  @Override
  public void removeOthers() {
    var unsavedDocuments = getUnsavedDocuments();
    if (!unsavedDocuments.isEmpty()) {
      notifyUnsavedFiles(unsavedDocuments, () -> removeOthers());
      return;
    }
    super.removeOthers();
  }

  @Override
  public void removeAllPanels() {
    var unsavedDocuments = getUnsavedDocuments();
    if (!unsavedDocuments.isEmpty()) {
      notifyUnsavedFiles(unsavedDocuments, () -> removeAllPanels());
      return;
    }
    super.removeAllPanels();
  }
  
  public int indexOfFile(String path) {
    for (int i = 0; i < panels.size(); i++) {
      Panel panel = getPanel(i);
      if (panel instanceof EditorPanel) {
        EditorPanel editorPanel = (EditorPanel) panel;
        if (editorPanel.getDocument().getPath().equals(path)) {
          return i;
        }
      }
    }
    return -1;
  }

  public void saveAllFiles(boolean showMsg) {
    saveAllFiles(showMsg, () -> {});
  }

  public void saveAllFiles(boolean showMsg, Runnable post) {
    if (!panels.isEmpty()) {
      TaskExecutor.executeAsync(
          () -> {
            for (int i = 0; i < panels.size(); i++) {
              saveDocument(getPanel(i));
            }
            return null;
          },
          (result) -> {
            if (showMsg)
              ToastUtils.showShort(
                  context.getString(R.string.saved_files), ToastUtils.TYPE_SUCCESS);
            post.run();
          });
    }
  }
  
  public void saveFile(boolean showMsg) {
    saveFile(showMsg, () -> {});
  }

  public void saveFile(boolean showMsg, Runnable post) {
    if (!panels.isEmpty()) {
      TaskExecutor.executeAsync(
          () -> {
            saveDocument(selectedPanel);
            return null;
          },
          (result) -> {
            if (showMsg)
              ToastUtils.showShort(context.getString(R.string.saved), ToastUtils.TYPE_SUCCESS);
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
    return getEditorPanel(selectedPanel);
  }

  public EditorPanel getEditorPanel(Panel panel) {
    if (panel instanceof EditorPanel) {
      return (EditorPanel) panel;
    }
    return null;
  }

  public WebViewPanel getSelectedWebViewPanel() {
    return getWebViewPanel(selectedPanel);
  }

  public WebViewPanel getWebViewPanel(Panel panel) {
    if (panel instanceof WebViewPanel) {
      return (WebViewPanel) panel;
    }
    return null;
  }

  public List<EditorPanel> getUnsavedDocuments() {
    List<EditorPanel> unsavedPanels = new ArrayList<>();
    for (Panel panel : panels) {
      var editorPanel = getEditorPanel(panel);
      if (editorPanel == null || panel.isPinned()) continue;
      if (editorPanel.getDocument().isModified()) {
        unsavedPanels.add(editorPanel);
      }
    }
    return unsavedPanels;
  }

  public int getUnsavedDocumentsCount() {
    int count = 0;
    for (Panel panel : panels) {
      var editorPanel = getEditorPanel(panel);
      if (editorPanel == null) continue;
      if (editorPanel.getDocument().isModified()) {
        count++;
      }
    }
    return count;
  }

  private void notifyUnsavedFile(EditorPanel unsavedPanel, Runnable post) {
    showUnsavedFilesDialog(
        unsavedPanel.getDocument().getName(),
        (d, w) -> {
          saveDocument(unsavedPanel);
          post.run();
        },
        (d, w) -> {
          unsavedPanel.markUnmodifiedPanel();
          post.run();
        });
  }

  private void notifyUnsavedFiles(List<EditorPanel> unsavedPanels, Runnable post) {
    StringBuilder sb = new StringBuilder();
    for (EditorPanel panel : unsavedPanels) {
      sb.append(" " + panel.getDocument().getName());
    }
    showUnsavedFilesDialog(
        sb.toString(),
        (d, w) -> {
          saveAllFiles(true, () -> post.run());
        },
        (d, w) -> {
          for (EditorPanel panel : unsavedPanels) {
            panel.markUnmodifiedPanel();
          }
          post.run();
        });
  }

  private void showUnsavedFilesDialog(
      String unsavedFilesName,
      DialogInterface.OnClickListener positive,
      DialogInterface.OnClickListener negative) {
    new MaterialAlertDialogBuilder(context)
        .setTitle(R.string.unsaved_files_title)
        .setMessage(context.getString(R.string.unsaved_files_message, unsavedFilesName))
        .setPositiveButton(R.string.save_and_close, positive)
        .setNegativeButton(R.string.close, negative)
        .setNeutralButton(R.string.cancel, null)
        .show();
  }
}
