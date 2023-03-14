package com.raredev.vcspace.ui.editor;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import androidx.core.content.res.ResourcesCompat;
import com.raredev.common.util.FileUtil;
import com.raredev.vcspace.databinding.LayoutCodeEditorBinding;
import com.raredev.vcspace.ui.editor.textmate.DynamicTextMateColorScheme;
import com.raredev.vcspace.ui.editor.textmate.VCSpaceTextMateLanguage;
import com.raredev.vcspace.util.PreferencesUtils;
import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.text.LineSeparator;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import java.io.File;
import java.util.concurrent.CompletableFuture;
import org.json.JSONObject;

public class CodeEditorView extends LinearLayout
    implements SharedPreferences.OnSharedPreferenceChangeListener {
  private LayoutCodeEditorBinding binding;

  private File file;

  public CodeEditorView(Context context, File file) {
    super(context);
    this.file = file;
    binding = LayoutCodeEditorBinding.inflate(LayoutInflater.from(context));
    binding.editor.setHighlightCurrentBlock(true);
    binding.editor.setColorScheme(createScheme());
    binding.editor.setLineSeparator(LineSeparator.LF);

    removeAllViews();
    addView(
        binding.getRoot(), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

    CompletableFuture.runAsync(
        () -> {
          var content = FileUtil.readFile(file.getAbsolutePath());
          var editor = binding.editor;

          editor.post(
              () -> {
                editor.setText(content);
                editor.setEditorLanguage(createLanguage());
              });
        });
    configureEditor();
    PreferencesUtils.getDefaultPrefs().registerOnSharedPreferenceChangeListener(this);
  }

  private void configureEditor() {
    updateEditorFont();
    updateTextSize();
    updateDeleteEmptyLineFast();
  }
<<<<<<< HEAD
=======

  public void format() {
    binding.editor.formatCodeAsync();
  }
>>>>>>> 4a1c7a13a36abf8a561b1695dc91390879a67eab

  @Override
  public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
    switch (key) {
      case "pref_editortextsize":
        updateTextSize();
        break;
      case "pref_deleteemptylinefast":
        updateDeleteEmptyLineFast();
        break;
      case "pref_editorfont":
        updateEditorFont();
        break;
    }
  }

  public void release() {
    PreferencesUtils.getDefaultPrefs().unregisterOnSharedPreferenceChangeListener(this);
    binding.editor.release();
  }

  public File getFile() {
    return file;
  }

  public CodeEditor getEditor() {
    return binding.editor;
  }

  public void save() {
    if (file != null && file.exists()) {
      String oldContent = FileUtil.readFile(file.getAbsolutePath());
      String newContent = binding.editor.getText().toString();

      if (oldContent == newContent) return;

      FileUtil.writeFile(file.getAbsolutePath(), binding.editor.getText().toString());
    }
  }

  public void undo() {
    if (binding.editor.canUndo()) binding.editor.undo();
  }

  public void redo() {
    if (binding.editor.canRedo()) binding.editor.redo();
  }

  private EditorColorScheme createScheme() {
    try {
      return DynamicTextMateColorScheme.create(getContext(), ThemeRegistry.getInstance());
    } catch (Exception e) {
      return new EditorColorScheme();
    }
  }

  private Language createLanguage() {
    try {
      JSONObject jsonObj =
          new JSONObject(FileUtil.readAssetFile(getContext(), "textmate/language_scopes.json"));
      String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);

      return VCSpaceTextMateLanguage.create(jsonObj.getString(extension), extension);
    } catch (Exception e) {
      return new EmptyLanguage();
    }
  }

  private void updateTextSize() {
    int textSize = PreferencesUtils.getEditorTextSize();
    binding.editor.setTextSize(textSize);
  }

  private void updateEditorFont() {
    binding.editor.setTypefaceText(
        ResourcesCompat.getFont(getContext(), PreferencesUtils.getSelectedFont()));
    binding.editor.setTypefaceLineNumber(
        ResourcesCompat.getFont(getContext(), PreferencesUtils.getSelectedFont()));
  }

  private void updateDeleteEmptyLineFast() {
    binding.editor.getProps().deleteEmptyLineFast = PreferencesUtils.useDeleteEmptyLineFast();
  }

  private void updateNonPrintablePaintingFlags() {
    /*binding.editor.setNonPrintablePaintingFlags(
    CodeEditor.FLAG_DRAW_WHITESPACE_LEADING
        | CodeEditor.FLAG_DRAW_WHITESPACE_INNER
        | CodeEditor.FLAG_DRAW_WHITESPACE_FOR_EMPTY_LINE);*/
  }
}
