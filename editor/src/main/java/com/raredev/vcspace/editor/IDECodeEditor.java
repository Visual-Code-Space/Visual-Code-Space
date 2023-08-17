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
import com.raredev.vcspace.events.EditorContentChangedEvent;
import com.raredev.vcspace.events.PreferenceChangedEvent;
import com.raredev.vcspace.models.DocumentModel;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.SharedPreferencesKeys;
import io.github.rosemoe.sora.event.ContentChangeEvent;
import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage;
import io.github.rosemoe.sora.langs.textmate.provider.TextMateProvider;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.EditorSearcher;
import io.github.rosemoe.sora.widget.VCSpaceSearcher;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow;
import java.util.Set;
import org.eclipse.tm4e.languageconfiguration.model.CommentRule;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class IDECodeEditor extends CodeEditor {
  
  private static final String LOG = "IDECodeEditor";

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
    setColorScheme(TextMateProvider.getColorScheme());
    subscribeContentChangeEvent();

    if (!EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().register(this);
    }
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
    if (getEditorLanguage() instanceof VCSpaceTMLanguage language) {
      language.editorCommitText(text);
    }
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
    if (language instanceof VCSpaceTMLanguage tmLanguage) {
      var languageConfiguration = tmLanguage.getLanguageConfiguration();
      if (languageConfiguration == null) {
        return null;
      }
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
    if (!EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this);
    }
    super.release();
    textActions = null;
    searcher = null;
    document = null;
  }

  public void setCursorPosition(int line, int column) {
    getCursor().set(line, column);
  }

  public void setDocument(DocumentModel document) {
    this.document = document;
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

    setInputType(createInputFlags());
  }

  public void subscribeContentChangeEvent() {
    subscribeEvent(
        ContentChangeEvent.class,
        (event, subscribe) -> {
          if (document == null) {
            return;
          }
          document.setContent(getText().toString().getBytes());
          EventBus.getDefault().post(new EditorContentChangedEvent(document));
        });
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
    boolean deleteEmptyLineFast = PreferencesUtils.useDeleteEmptyLineFast();
    getProps().deleteEmptyLineFast = deleteEmptyLineFast;
    getProps().deleteMultiSpaces = deleteEmptyLineFast ? -1 : 1;
  }
  
  private void updateLineNumbers() {
    boolean lineNumbers = PreferencesUtils.lineNumbers();
    setLineNumberEnabled(lineNumbers);
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
