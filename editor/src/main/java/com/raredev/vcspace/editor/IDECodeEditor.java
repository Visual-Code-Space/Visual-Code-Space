package com.raredev.vcspace.editor;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import com.google.common.collect.ImmutableSet;
import com.raredev.vcspace.editor.completion.CompletionItemAdapter;
import com.raredev.vcspace.editor.completion.CustomCompletionLayout;
import com.raredev.vcspace.models.DocumentModel;
import com.raredev.vcspace.utils.PreferencesUtils;
import com.raredev.vcspace.utils.SharedPreferencesKeys;
import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.Cursor;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.EditorSearcher;
import io.github.rosemoe.sora.widget.SymbolPairMatch;
import io.github.rosemoe.sora.widget.VCSpaceSearcher;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow;
import java.util.List;
import java.util.Set;
import org.eclipse.tm4e.languageconfiguration.model.CommentRule;

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

  private EditorTextActions textActions;
  private VCSpaceSearcher searcher;

  private DocumentModel document;

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

    searcher = new VCSpaceSearcher(this);
    textActions = new EditorTextActions(this);

    getComponent(EditorTextActionWindow.class).setEnabled(false);
    getComponent(EditorAutoCompletion.class).setLayout(new CustomCompletionLayout());
    getComponent(EditorAutoCompletion.class).setAdapter(new CompletionItemAdapter());

    configureEditor();
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
  public void deleteText() {
    if (deleteSymbolPair()) return;
    super.deleteText();
  }

  private boolean deleteSymbolPair() {
    Cursor cursor = getCursor();
    if (cursor.isSelected()) return false;
    Content text = getText();
    int startIndex = cursor.getLeft();

    if (startIndex - 1 >= 0) {
      char deleteChar = text.charAt(startIndex - 1);
      char afterChar = text.charAt(startIndex);
      String deletePairOpen = "" + deleteChar;
      String deletePairClose = "" + afterChar;

      SymbolPairMatch.SymbolPair deletePair = null;
      SymbolPairMatch pairs = getEditorLanguage().getSymbolPairs();

      if (pairs == null) return false;

      List<SymbolPairMatch.SymbolPair> matchPairs = pairs.matchBestPairList(deleteChar);
      for (SymbolPairMatch.SymbolPair pair : matchPairs) {
        if (pair.open.equals(deletePairOpen) && pair.close.equals(deletePairClose)) {
          deletePair = pair;
        }
      }

      if (deletePair == null) return false;

      text.delete(startIndex - 1, startIndex + 1);
      return true;
    }

    return false;
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

  @Nullable
  public CommentRule getCommentRule() {
    Language language = getEditorLanguage();
    if (language instanceof VCSpaceTMLanguage) {
      var languageConfiguration = ((VCSpaceTMLanguage) language).getLanguageConfiguration();
      if (languageConfiguration == null) return null;
      return languageConfiguration.getComments();
    }
    return null;
  }

  @Override
  public EditorSearcher getSearcher() {
    return searcher;
  }

  @Override
  public void release() {
    super.release();
    textActions = null;
    searcher = null;
    document = null;
  }

  public DocumentModel getDocument() {
    return document;
  }

  public void setDocument(DocumentModel document) {
    this.document = document;
  }

  public void setCursorPosition(int line, int column) {
    final var content = getText();
    if (getLineCount() <= 0) {
      return;
    }
    int col = content.getColumnCount(line);
    if (col < 0) {
      col = 0;
    }
    getCursor().set(line, column);
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

  public void onSharedPreferenceChanged(String key) {
    switch (key) {
      case SharedPreferencesKeys.KEY_EDITOR_TEXT_SIZE:
        updateTextSize();
        break;
      case SharedPreferencesKeys.KEY_EDITOR_LINEHEIGHT:
        updateLineHeight();
        break;
      case SharedPreferencesKeys.KEY_EDITOR_TAB_SIZE:
        updateTABSize();
        break;
      case SharedPreferencesKeys.KEY_STICKYSCROLL:
        updateStickyScroll();
        break;
      case SharedPreferencesKeys.KEY_FONTLIGATURES:
        updateFontLigatures();
        break;
      case SharedPreferencesKeys.KEY_WORDWRAP:
        updateWordWrap();
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
      case SharedPreferencesKeys.KEY_DELETETABS:
        updateDeleteTabs();
        break;
    }
  }

  public void configureEditor() {
    updateEditorFont();
    updateTextSize();
    updateLineHeight();
    updateTABSize();
    updateStickyScroll();
    updateFontLigatures();
    updateWordWrap();
    updateLineNumbers();
    updateDeleteEmptyLineFast();
    updateDeleteTabs();

    setInputType(createInputFlags());
  }

  private void updateTextSize() {
    int textSize = PreferencesUtils.getEditorTextSize();
    setTextSize(textSize);
  }

  private void updateLineHeight() {
    int lineHeight = PreferencesUtils.getEditorLineHeight();
    setLineSpacing(lineHeight, 1.1f);
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

  private void updateStickyScroll() {
    boolean stickyScroll = PreferencesUtils.useStickyScroll();
    getProps().stickyScroll = stickyScroll;
  }

  private void updateFontLigatures() {
    boolean fontLigatures = PreferencesUtils.useFontLigatures();
    setLigatureEnabled(fontLigatures);
  }

  private void updateWordWrap() {
    boolean wordWrap = PreferencesUtils.useWordWrap();
    setWordwrap(wordWrap);
  }

  private void updateLineNumbers() {
    boolean lineNumbers = PreferencesUtils.lineNumbers();
    setLineNumberEnabled(lineNumbers);
  }

  private void updateDeleteEmptyLineFast() {
    boolean deleteEmptyLineFast = PreferencesUtils.useDeleteEmptyLineFast();
    getProps().deleteEmptyLineFast = deleteEmptyLineFast;
  }

  private void updateDeleteTabs() {
    boolean deleteTabs = PreferencesUtils.useDeleteTabs();
    getProps().deleteMultiSpaces = deleteTabs ? -1 : 1;
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
