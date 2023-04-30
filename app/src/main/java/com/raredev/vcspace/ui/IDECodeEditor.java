package com.raredev.vcspace.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import com.google.common.collect.ImmutableSet;
import com.raredev.vcspace.ui.editor.EditorTextActions;
import com.raredev.vcspace.ui.editor.completion.CompletionItemAdapter;
import com.raredev.vcspace.ui.editor.completion.CustomCompletionLayout;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.ToastUtils;
import com.raredev.vcspace.util.Utils;
import io.github.rosemoe.sora.event.ContentChangeEvent;
import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.EditorSearcher;
import io.github.rosemoe.sora.widget.VCSpaceSearcher;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import java.io.File;
import java.util.Set;
import org.eclipse.tm4e.languageconfiguration.model.LanguageConfiguration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.ParseError;
import org.jsoup.parser.Parser;

public class IDECodeEditor extends CodeEditor {

  private static final Set<Character> IGNORED_PAIR_ENDS =
      ImmutableSet.<Character>builder()
          .add(')')
          .add(']')
          .add('}')
          .add('"')
          .add('>')
          .add('\'')
          .add(';')
          .build();

  private VCSpaceSearcher searcher;
  private EditorTextActions textActions;

  private boolean isModified;

  private File file;

  public String commentPrefix;
  public String blockCommentOpenPrefix;
  public String blockCommentClosePrefix;

  public IDECodeEditor(Context context) {
    this(context, null);
  }

  public IDECodeEditor(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public IDECodeEditor(Context context, AttributeSet attrs, int defStyleAttr) {
    this(context, attrs, defStyleAttr, 0);
  }

  public IDECodeEditor(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);

    this.isModified = false;

    searcher = new VCSpaceSearcher(this);
    textActions = new EditorTextActions(this);

    getComponent(EditorTextActionWindow.class).setEnabled(false);
    getComponent(EditorAutoCompletion.class).setLayout(new CustomCompletionLayout());
    getComponent(EditorAutoCompletion.class).setAdapter(new CompletionItemAdapter());

    setColorScheme(createScheme());
  }

  @Override
  public void commitText(CharSequence text, boolean applyAutoIndent) {
    if (text.length() == 1) {
      char currentChar = getText().charAt(getCursor().getLeft());
      char c = text.charAt(0);
      if (IGNORED_PAIR_ENDS.contains(c) && c == currentChar) {
        // ignored pair end, just move the cursor over the character
        setSelection(getCursor().getLeftLine(), getCursor().getLeftColumn() + 1);
        return;
      }
    }
    super.commitText(text, applyAutoIndent);
  }

  @Override
  public void hideEditorWindows() {
    super.hideEditorWindows();
    textActions.dismiss();
  }

  @Override
  protected void onFocusChanged(
      boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
    super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    if (!gainFocus && textActions != null && textActions.isShowing()) {
      textActions.dismiss();
    }
  }

  @Override
  public void setEditorLanguage(Language lang) {
    if (lang instanceof VCSpaceTMLanguage) {
      LanguageConfiguration langConfig = ((VCSpaceTMLanguage) lang).getLanguageConfiguration();
      if (langConfig != null) {
        this.commentPrefix = langConfig.getComments().lineComment;
        this.blockCommentOpenPrefix = langConfig.getComments().blockComment.open;
        this.blockCommentClosePrefix = langConfig.getComments().blockComment.close;
      }
    }
    super.setEditorLanguage(lang);
  }

  @Override
  public EditorSearcher getSearcher() {
    return searcher;
  }

  @Override
  public void release() {
    super.release();
    textActions = null;
    file = null;
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

  public void configureEditor() {
    updateEditorFont();
    updateTextSize();
    updateTABSize();
    updateDeleteEmptyLineFast();

    setInputType(createInputFlags());
  }

  public void subscribeContentChangeEvent(Runnable runnable) {
    subscribeEvent(
        ContentChangeEvent.class,
        (event, subscribe) -> {
          switch (event.getAction()) {
            case ContentChangeEvent.ACTION_INSERT:
            case ContentChangeEvent.ACTION_DELETE:
            case ContentChangeEvent.ACTION_SET_NEW_TEXT:
              isModified = true;
              Document doc = Jsoup.parseBodyFragment(getText().toString());

              Parser parser = doc.parser();

              for (ParseError error : parser.getErrors()) {
                ToastUtils.showShort(error.getErrorMessage(), ToastUtils.TYPE_ERROR);
              }

              runnable.run();
              break;
          }
        });
  }

  public void saveFile() {
    if (isModified) {
      FileUtil.writeFile(file.getAbsolutePath(), getText().toString());
      isModified = false;
    }
  }

  public void setFile(File file) {
    this.file = file;
  }

  public File getFile() {
    return file;
  }

  public boolean isModified() {
    return this.isModified;
  }

  public void markUnmodified() {
    this.isModified = false;
  }

  public int appendText(String text) {
    final var content = getText();
    if (getLineCount() <= 0) {
      return 0;
    }
    final int line = getLineCount() - 1;
    int col = content.getColumnCount(line);
    if (col < 0) {
      col = 0;
    }
    content.insert(line, col, text);
    return line;
  }

  private EditorColorScheme createScheme() {
    try {
      ThemeRegistry.getInstance().setTheme(Utils.isDarkMode() ? "vcspace_dark" : "quietlight");
      return TextMateColorScheme.create(ThemeRegistry.getInstance());
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
