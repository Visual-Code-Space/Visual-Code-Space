package com.raredev.vcspace.ui.editor;

import android.content.Context;
import android.view.inputmethod.EditorInfo;
import androidx.core.content.res.ResourcesCompat;
import com.raredev.vcspace.models.LanguageScope;
import com.raredev.vcspace.ui.editor.completion.CompletionItemAdapter;
import com.raredev.vcspace.ui.editor.completion.CustomCompletionLayout;
import com.raredev.vcspace.ui.language.html.HtmlLanguage;
import com.raredev.vcspace.ui.language.java.JavaLanguage;
import com.raredev.vcspace.ui.language.lua.LuaLanguage;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.Utils;
import io.github.rosemoe.sora.event.ContentChangeEvent;
import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import java.io.File;
import java.util.concurrent.CompletableFuture;

public class CodeEditorView extends CodeEditor {

  private boolean isModified;
  private File file;

  public CodeEditorView(Context context, File file) {
    super(context);
    isModified = false;
    this.file = file;
    setHighlightCurrentBlock(true);
    getProps().autoCompletionOnComposing = true;
    setColorScheme(createScheme());

    setEditable(false);
    CompletableFuture.runAsync(
        () -> {
          var content = FileUtil.readFile(file.getAbsolutePath());
          post(
              () -> {
                setText(content, null);
                setEditorLanguage(createLanguage());
                setEditable(true);
              });
        });

    getComponent(EditorAutoCompletion.class).setLayout(new CustomCompletionLayout());
    getComponent(EditorAutoCompletion.class).setAdapter(new CompletionItemAdapter());

    replaceComponent(EditorTextActionWindow.class, new EditorTextActions(this));

    configureEditor();
  }

  @Override
  public void undo() {
    if (canUndo()) super.undo();
  }

  @Override
  public void redo() {
    if (canRedo()) super.redo();
  }

  @Override
  public void release() {
    super.release();
    file = null;
  }

  private void configureEditor() {
    updateEditorFont();
    updateTextSize();
    updateTABSize();
    updateDeleteEmptyLineFast();

    setInputType(createInputFlags());
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
              isModified = true;
              runnable.run();
              break;
          }
        });
  }

  public boolean isModified() {
    return isModified;
  }

  public File getFile() {
    return file;
  }

  public void save() {
    if (isModified) {
      FileUtil.writeFile(file.getAbsolutePath(), getText().toString());
      isModified = false;
    }
  }

  private EditorColorScheme createScheme() {
    try {
      ThemeRegistry.getInstance().setTheme(Utils.isDarkMode() ? "vcspace_dark" : "quietlight");
      return TextMateColorScheme.create(ThemeRegistry.getInstance());
    } catch (Exception e) {
      return null;
    }
  }

  public Language createLanguage() {
    try {
      final LanguageScope langScope = LanguageScope.Factory.forFile(file);

      switch (langScope) {
        case JAVA:
          return new JavaLanguage(this);
        case HTML:
          return new HtmlLanguage();
        case LUA:
          return new LuaLanguage();
      }

      return VCSpaceTMLanguage.create(langScope.getScope());
    } catch (Exception e) {
      return null;
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

  public static int createInputFlags() {
    return EditorInfo.TYPE_CLASS_TEXT
        | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
        | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        | EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
  }
}
