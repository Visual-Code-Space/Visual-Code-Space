package com.raredev.vcspace.ui.editor;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import androidx.core.content.res.ResourcesCompat;
import com.raredev.common.util.FileUtil;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.LayoutCodeEditorBinding;
import com.raredev.vcspace.util.PreferencesUtils;
import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.widget.CodeEditor;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

public class CodeEditorView extends LinearLayout
    implements SharedPreferences.OnSharedPreferenceChangeListener {
  private LayoutCodeEditorBinding binding;

  private File file;

  public CodeEditorView(Context context, File file) {
    super(context);
    this.file = file;
    binding = LayoutCodeEditorBinding.inflate(LayoutInflater.from(context));
    addView(
        binding.getRoot(), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    binding.editor.setNonPrintablePaintingFlags(
        CodeEditor.FLAG_DRAW_WHITESPACE_LEADING
            | CodeEditor.FLAG_DRAW_WHITESPACE_INNER
            | CodeEditor.FLAG_DRAW_WHITESPACE_FOR_EMPTY_LINE);
    binding.editor.setTypefaceText(ResourcesCompat.getFont(context, R.font.jetbrains_mono));
    binding.editor.setTypefaceLineNumber(ResourcesCompat.getFont(context, R.font.jetbrains_mono));
    binding.editor.setDefaultFocusHighlightEnabled(true);
    setupTheme();

    CompletableFuture.runAsync(
        () -> {
          try {
            var editor = binding.editor;
            var content = FileUtils.readFileToString(file);
            editor.post(
                () -> {
                  editor.setText(content);
                  setLanguage();
                });
          } catch (IOException ioe) {
            ioe.printStackTrace();
          }
        });
    PreferencesUtils.getDefaultPrefs().registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
    switch (key) {
      case "textsize":
        updateTextSize();
        break;
      case "deletefast":
        updateDeleteEmptyLineFast();
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
    FileUtil.writeFile(file.getAbsolutePath(), binding.editor.getText().toString());
  }

  public void undo() {
    if (binding.editor.canUndo()) binding.editor.undo();
  }

  public void redo() {
    if (binding.editor.canRedo()) binding.editor.redo();
  }

  private void setLanguage() {
    try {
      var editor = binding.editor;
      JSONObject jsonObj =
          new JSONObject(FileUtil.readAssetFile(getContext(), "textmate/language_scopes.json"));
      String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);

      if (extension != null) {
        editor.setEditorLanguage(TextMateLanguage.create(jsonObj.getString(extension), true));
      } else {
        editor.setEditorLanguage(new EmptyLanguage());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void setupTheme() {
    try {
      ThemeRegistry.getInstance().setTheme(isDarkMode() ? "darcula" : "quietlight");

      var editor = binding.editor;
      editor.setColorScheme(TextMateColorScheme.create(ThemeRegistry.getInstance()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void updateTextSize() {
    int textSize = PreferencesUtils.getTextSize();
    if (textSize < 14) {
      textSize = 14;
    }
    binding.editor.setTextSize(textSize);
  }

  private void updateDeleteEmptyLineFast() {
    binding.editor.getProps().deleteEmptyLineFast = PreferencesUtils.isDeleleteEmptyLineFast();
  }

  private boolean isDarkMode() {
    int uiMode =
        getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    return uiMode == Configuration.UI_MODE_NIGHT_YES;
  }
}
