package com.raredev.vcspace.ui.editor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import androidx.core.content.res.ResourcesCompat;
import com.raredev.vcspace.databinding.LayoutCodeEditorBinding;
import com.raredev.vcspace.editor.IDECodeEditor;
import com.raredev.vcspace.events.PreferenceChangedEvent;
import com.raredev.vcspace.models.DocumentModel;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.SharedPreferencesKeys;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage;
import io.github.rosemoe.sora.langs.textmate.provider.TextMateProvider;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class CodeEditorView extends LinearLayout {

  private LayoutCodeEditorBinding binding;
  private DocumentModel document;

  public CodeEditorView(Context context, DocumentModel document) {
    super(context);
    binding = LayoutCodeEditorBinding.inflate(LayoutInflater.from(context));

    setOrientation(VERTICAL);
    removeAllViews();

    addView(
        binding.getRoot(),
        new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f));

    setDocument(document);
    setLoading(true);
    
    var modified = document.isModified();

    TaskExecutor.executeAsync(
        () -> {
          String content = "";
          if (document.getContent() != null) {
            content = new String(document.getContent());
          } else {
            content = FileUtil.readFile(document.getPath());
          }
          return content;
        },
        (result) -> {
          binding.editor.setText((String) result, null);
          if (!modified) document.markUnmodified();
          configureEditor();
          postRead();
        });
  }

  private void postRead() {
    binding.editor.setEditorLanguage(createLanguage());
    binding.editor.setCursorPosition(document.getPositionLine(), document.getPositionColumn());
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

  public void setDocument(DocumentModel document) {
    binding.editor.setDocument(document);
    this.document = document;
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

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onSharedPreferenceChanged(PreferenceChangedEvent event) {
    String key = event.getKey();
    switch (key) {
      case SharedPreferencesKeys.KEY_FONT_SIZE_PREFERENCE:
        updateTextSize();
        break;
      case SharedPreferencesKeys.KEY_EDITOR_TAB_SIZE:
        updateTABSize();
        break;
      case SharedPreferencesKeys.KEY_DELETE_EMPTY_LINE_FAST:
        updateDeleteEmptyLineFast();
        break;
      case SharedPreferencesKeys.KEY_EDITOR_FONT:
        updateEditorFont();
        break;
      case SharedPreferencesKeys.KEY_LINENUMBERS:
        updateLineNumbers();
        break;
    }
  }

  public void configureEditor() {
    updateEditorFont();
    updateTextSize();
    updateTABSize();
    updateLineNumbers();
    updateDeleteEmptyLineFast();

    binding.editor.setInputType(createInputFlags());
  }

  private void updateTextSize() {
    int textSize = PreferencesUtils.getEditorTextSize();
    binding.editor.setTextSize(textSize);
  }

  private void updateTABSize() {
    int tabSize = PreferencesUtils.getEditorTABSize();
    binding.editor.setTabWidth(tabSize);
  }

  private void updateEditorFont() {
    binding.editor.setTypefaceText(
        ResourcesCompat.getFont(getContext(), PreferencesUtils.getSelectedFont()));
    binding.editor.setTypefaceLineNumber(
        ResourcesCompat.getFont(getContext(), PreferencesUtils.getSelectedFont()));
  }

  private void updateDeleteEmptyLineFast() {
    boolean deleteEmptyLineFast = PreferencesUtils.useDeleteEmptyLineFast();
    binding.editor.getProps().deleteEmptyLineFast = deleteEmptyLineFast;
    binding.editor.getProps().deleteMultiSpaces = deleteEmptyLineFast ? -1 : 1;
  }

  private void updateLineNumbers() {
    boolean lineNumbers = PreferencesUtils.lineNumbers();
    binding.editor.setLineNumberEnabled(lineNumbers);
  }

  private void updateNonPrintablePaintingFlags() {
    /*binding.editor.setNonPrintablePaintingFlags(
    CodeEditor.FLAG_DRAW_WHITESPACE_LEADING
        | CodeEditor.FLAG_DRAW_WHITESPACE_INNER
        | CodeEditor.FLAG_DRAW_WHITESPACE_FOR_EMPTY_LINE);*/
  }

  private int createInputFlags() {
    return EditorInfo.TYPE_CLASS_TEXT
        | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
        | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        | EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
  }
}
