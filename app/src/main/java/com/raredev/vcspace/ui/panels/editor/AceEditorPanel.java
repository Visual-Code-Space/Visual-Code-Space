package com.raredev.vcspace.ui.panels.editor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.vcspace.databinding.LayoutAceEditorPanelBinding;
import com.raredev.vcspace.editor.VCSpaceCodeEditor;
import com.raredev.vcspace.editor.ace.OnEditorLoadedListener;
import com.raredev.vcspace.editor.ace.VCSpaceWebInterface;
import com.raredev.vcspace.models.DocumentModel;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.tasks.TaskExecutor;
import com.raredev.vcspace.ui.panels.Panel;
import com.raredev.vcspace.utils.PanelUtils;
import com.raredev.vcspace.utils.PreferencesUtils;

public class AceEditorPanel extends Panel implements OnEditorLoadedListener {

  private LayoutAceEditorPanelBinding binding;
  private DocumentModel document;

  public AceEditorPanel(Context context, DocumentModel document) {
    super(context, document.getName());
    this.document = document;
  }

  @Override
  public void unselected() {}

  @Override
  public void selected() {}

  @Override
  public void destroy() {}

  @Override
  public android.view.View createView(LayoutInflater inflater) {
    binding = LayoutAceEditorPanelBinding.inflate(inflater);
    binding.editor.setOnEditorLoadedListener(this);
    return binding.getRoot();
  }

  @Override
  public void viewCreated(View view) {
    super.viewCreated(view);
    binding.pathList.setEnabled(PreferencesUtils.showFilePath());
  }

  @Override
  public void updatePanelTab() {
    super.updatePanelTab();
    var title = PanelUtils.getUniqueTabTitle(this, getPanelArea().getPanels());
    if (document.isModified() && !title.startsWith("*")) {
      title = "*" + title;
    }
    updateTitle(title);
  }

  @Override
  public void onLoaded(WebView view) {
    VCSpaceCodeEditor editor = binding.editor;
    TaskExecutor.executeAsync(
        () -> {
          String content = "";
          if (document.getContent() != null) {
            content = new String(document.getContent());
          } else {
            content = FileIOUtils.readFile2String(document.getPath());
          }
          return content;
        },
        (result) -> {
          String finalContent = String.valueOf(result);
          editor.post(
              () -> {
                editor.setText(finalContent);
                editor.moveCursorTo(document.getPositionLine(), document.getPositionColumn());
                editor.setLanguageFromFile(document.getPath());
                editor.requestFocus();
                subscribeContentChangeEvent(editor);
                updatePathList();
              });
          saveDocument(VCSpaceWebInterface.value);
          ((AppCompatActivity) getContext()).invalidateOptionsMenu();
        });
  }

  public void subscribeContentChangeEvent(VCSpaceCodeEditor editor) {
    editor.setOnContentChangeEventCallback(
        (initialValue, modifiedValue) -> {
          if (!PreferencesUtils.autoSave()) {
            markModifiedPanel();
          } else {
            saveDocument(modifiedValue);
          }
          ((AppCompatActivity) getContext()).invalidateOptionsMenu();
        });
  }

  public void markModifiedPanel() {
    document.markModified();
    ThreadUtils.runOnUiThread(
        () -> {
          String panelTitle = getTitle();
          if (!panelTitle.startsWith("*")) {
            updateTitle("*" + panelTitle);
          }
        });
  }

  public void markUnmodifiedPanel() {
    document.markUnmodified();
    ThreadUtils.runOnUiThread(
        () -> {
          String panelTitle = getTitle();
          if (panelTitle.startsWith("*")) {
            updateTitle(panelTitle.replace("*", ""));
          }
        });
  }

  public void reloadFile() {
    if (document.isModified()) {
      new MaterialAlertDialogBuilder(getContext())
          .setTitle(R.string.menu_reload)
          .setMessage(R.string.discard_changes)
          .setNegativeButton(R.string.cancel, null)
          .setPositiveButton(
              android.R.string.ok,
              (dlg, witch) -> {
                reloadFileHandler();
              })
          .show();
    } else {
      reloadFileHandler();
    }
  }

  private void reloadFileHandler() {
    //    setLoading(true);
    TaskExecutor.executeAsync(
        () -> {
          return FileIOUtils.readFile2String(document.getPath());
        },
        (result) -> {
          binding.editor.setText(result);
          markUnmodifiedPanel();
          //          setLoading(false);
        });
  }

  public void saveDocument(String modifiedCode) {
    if (document.isModified() || PreferencesUtils.autoSave()) {
      FileIOUtils.writeFileFromString(document.getPath(), modifiedCode);
      markUnmodifiedPanel();
    }
  }

  public DocumentModel getDocument() {
    return document;
  }

  public VCSpaceCodeEditor getEditor() {
    return binding.editor;
  }
  
  private void updatePathList() {
    binding.pathList.setPath(document.getPath());
  }
}
