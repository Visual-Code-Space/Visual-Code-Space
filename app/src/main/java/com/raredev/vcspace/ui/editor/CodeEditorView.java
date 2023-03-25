package com.raredev.vcspace.ui.editor;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.core.content.res.ResourcesCompat;
import com.raredev.common.util.FileUtil;
import com.raredev.common.util.ILogger;
import com.raredev.vcspace.models.LanguageScope;
import com.raredev.vcspace.ui.editor.textmate.DynamicTextMateColorScheme;
import com.raredev.vcspace.ui.language.html.HtmlLanguage;
import com.raredev.vcspace.ui.language.java.JavaLanguage;
import com.raredev.vcspace.util.PreferencesUtils;
import io.github.rosemoe.sora.event.ContentChangeEvent;
import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.text.LineSeparator;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import java.io.File;
import java.util.concurrent.CompletableFuture;

public class CodeEditorView extends CodeEditor {
  private File file;

  public CodeEditorView(Context context, File file) {
    super(context);
    this.file = file;
    setHighlightCurrentBlock(true);
    getProps().autoCompletionOnComposing = true;
    setColorScheme(createScheme());
    setLineSeparator(LineSeparator.LF);

    CompletableFuture.runAsync(
        () -> {
          var content = FileUtil.readFile(file.getAbsolutePath());

          post(
              () -> {
                setText(content);
                setEditorLanguage(createLanguage());
              });
        });
    configureEditor();
  }

  private void configureEditor() {
    updateEditorFont();
    updateTextSize();
    updateTABSize();
    updateDeleteEmptyLineFast();
  }

  public void onSharedPreferenceChanged(String key) {
    switch (key) {
      case "pref_editortextsize":
        updateTextSize();
        break;
      case "pref_editortabsize":
        updateTABSize();
        break;
      case "pref_deleteemptylinefast":
        updateDeleteEmptyLineFast();
        break;
      case "pref_editorfont":
        updateEditorFont();
        break;
    }
  }

  public void subscribeContentChangeEvent(Runnable runnable) {
    subscribeEvent(
        ContentChangeEvent.class,
        (event, subscribe) -> {
          switch (event.getAction()) {
            case ContentChangeEvent.ACTION_INSERT:
            case ContentChangeEvent.ACTION_DELETE:
            case ContentChangeEvent.ACTION_SET_NEW_TEXT:
              new Handler(Looper.getMainLooper()).postDelayed(runnable, 10);
              break;
          }
        });
  }

  public File getFile() {
    return file;
  }

  public void save() {
    if (file != null && file.exists()) {
      String oldContent = FileUtil.readFile(file.getAbsolutePath());
      String newContent = getText().toString();

      if (oldContent == newContent) return;

      FileUtil.writeFile(file.getAbsolutePath(), getText().toString());
    }
  }

  @Override
  public void undo() {
    if (canUndo()) super.undo();
  }

  @Override
  public void redo() {
    if (canRedo()) super.redo();
  }

  private EditorColorScheme createScheme() {
    try {
      return DynamicTextMateColorScheme.create(getContext(), ThemeRegistry.getInstance());
    } catch (Exception e) {
      return null;
    }
  }

  private Language createLanguage() {
    try {
      final LanguageScope langScope = LanguageScope.Factory.forFile(file);

      switch (langScope) {
        case JAVA:
          return new JavaLanguage();
        case HTML:
          return new HtmlLanguage();
      }

      return TextMateLanguage.create(langScope.getScope(), true);
    } catch (Exception e) {
      ILogger.error("LoadEditorLanguage", Log.getStackTraceString(e));
      return new EmptyLanguage();
    }
  }

  private void updateTextSize() {
    int textSize = PreferencesUtils.getEditorTextSize();
    setTextSize(textSize);
  }

  private void updateTABSize() {
    int tabSize = PreferencesUtils.getEditorTABSize();
    setTabWidth(tabSize);
  }

  private void updateEditorFont() {
    setTypefaceText(ResourcesCompat.getFont(getContext(), PreferencesUtils.getSelectedFont()));
    setTypefaceLineNumber(
        ResourcesCompat.getFont(getContext(), PreferencesUtils.getSelectedFont()));
  }

  private void updateDeleteEmptyLineFast() {
    getProps().deleteEmptyLineFast = PreferencesUtils.useDeleteEmptyLineFast();
  }

  private void updateNonPrintablePaintingFlags() {
    /*binding.editor.setNonPrintablePaintingFlags(
    CodeEditor.FLAG_DRAW_WHITESPACE_LEADING
        | CodeEditor.FLAG_DRAW_WHITESPACE_INNER
        | CodeEditor.FLAG_DRAW_WHITESPACE_FOR_EMPTY_LINE);*/
  }
}
