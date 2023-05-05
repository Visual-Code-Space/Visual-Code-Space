package com.raredev.vcspace.ui.editor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.raredev.vcspace.databinding.LayoutCodeEditorBinding;
import com.raredev.vcspace.models.DocumentModel;
import com.raredev.vcspace.editor.IDECodeEditor;
import com.raredev.vcspace.ui.VCSpaceSearcherLayout;
import com.raredev.vcspace.util.FileUtil;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage;
import io.github.rosemoe.sora.langs.textmate.provider.TextMateProvider;
import java.util.concurrent.CompletableFuture;

public class CodeEditorView extends LinearLayout {

  private LayoutCodeEditorBinding binding;

  private VCSpaceSearcherLayout searcherLayout;

  private DocumentModel document;

  public CodeEditorView(Context context, DocumentModel document) {
    super(context);
    this.document = document;
    binding = LayoutCodeEditorBinding.inflate(LayoutInflater.from(context));

    setOrientation(VERTICAL);
    removeAllViews();

    searcherLayout = new VCSpaceSearcherLayout(context, binding.editor.getSearcher());

    addView(
        binding.getRoot(),
        new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f));
    addView(searcherLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

    setLoading(true);
    if (document.getContent() == null) {
      readDocument();
    } else {
      binding.editor.setText(document.getContent(), null);
      postRead();
    }

    binding.editor.configureEditor();
  }

  private void readDocument() {
    CompletableFuture.runAsync(
        () -> {
          var editor = binding.editor;
          var content = FileUtil.readFile(document.getPath());
          editor.post(
              () -> {
                editor.setText(content, null);
                document.markUnmodified();
                postRead();
              });
        });
  }

  private void postRead() {
    binding.editor.setEditorLanguage(createLanguage());

    binding.editor.setCursorPosition(document.getPositionLine(), document.getPositionLine());
    binding.editor.setDocument(document);
    setLoading(false);
  }

  public void reloadEditor() {
    getEditor().setColorScheme(TextMateProvider.getColorScheme());
    getEditor().setEditorLanguage(createLanguage());
  }

  public void saveDocument() {
    if (document.isModified()) {
      FileUtil.writeFile(document.getPath(), getCode());
      document.markUnmodified();
    }
  }

  public String getCode() {
    return binding.editor.getText().toString();
  }

  public void showAndHideSearcher() {
    searcherLayout.showAndHide();
  }

  public boolean searcherIsShowing() {
    return searcherLayout.isShowing;
  }

  public void release() {
    binding.editor.release();
  }

  public void undo() {
    if (binding.editor.canUndo()) {
      binding.editor.undo();
    }
  }

  public void redo() {
    if (binding.editor.canRedo()) {
      binding.editor.redo();
    }
  }

  public DocumentModel getDocument() {
    return document;
  }

  public IDECodeEditor getEditor() {
    return binding.editor;
  }

  public void setLoading(boolean loading) {
    binding.circularProgressIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
  }

  public VCSpaceTMLanguage createLanguage() {
    try {
      return TextMateProvider.createLanguage(getEditor(), document.getName());
    } catch (Exception e) {
      return null;
    }
  }
}
