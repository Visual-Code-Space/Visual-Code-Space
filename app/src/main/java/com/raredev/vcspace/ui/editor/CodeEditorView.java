package com.raredev.vcspace.ui.editor;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.core.content.res.ResourcesCompat;
import com.raredev.vcspace.models.LanguageScope;
import com.raredev.vcspace.ui.editor.completion.CompletionItemAdapter;
import com.raredev.vcspace.ui.editor.completion.CustomCompletionLayout;
import com.raredev.vcspace.ui.language.html.HtmlLanguage;
import com.raredev.vcspace.ui.language.java.JavaLanguage;
import com.raredev.vcspace.ui.language.lua.LuaLanguage;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.PreferencesUtils;
import io.github.rosemoe.sora.event.ContentChangeEvent;
import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTextMateColorScheme;
import io.github.rosemoe.sora.lsp.editor.LspEditor;
import io.github.rosemoe.sora.text.LineSeparator;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import java.io.File;
import java.util.concurrent.CompletableFuture;

public class CodeEditorView extends CodeEditor {
  private File file;
  private LspEditor lspEditor;

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
                setText(content, null);
                Language lang = createLanguage();
                if (lang != null) {
                  setEditorLanguage(lang);
                }
              });
        });

    getComponent(EditorAutoCompletion.class).setLayout(new CustomCompletionLayout());
    getComponent(EditorAutoCompletion.class).setAdapter(new CompletionItemAdapter());

    replaceComponent(EditorTextActionWindow.class, new EditorTextActions(this));

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
      return VCSpaceTextMateColorScheme.create(getContext());
    } catch (Exception e) {
      return null;
    }
  }

  public Language createLanguage() {
    try {
      final LanguageScope langScope = LanguageScope.Factory.forFile(file);

      switch (langScope) {
        case JAVA:
          return new JavaLanguage();
        case HTML:
          return new HtmlLanguage();
        case LUA:
          return new LuaLanguage();
      }

      return TextMateLanguage.create(langScope.getScope(), true);
    } catch (Exception e) {
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
