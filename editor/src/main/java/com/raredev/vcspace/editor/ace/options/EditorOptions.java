package com.raredev.vcspace.editor.ace.options;

import com.raredev.vcspace.editor.ace.VCSpaceEditorLanguage;
import com.raredev.vcspace.editor.ace.VCSpaceEditorTheme;
import com.raredev.vcspace.utils.PreferencesUtils;

public class EditorOptions {
  private static volatile EditorOptions instance;

  public static EditorOptions getInstance() {
    if (instance == null) {
      // To make thread safe
      synchronized (EditorOptions.class) {
        // check again as multiple threads
        // can reach above step
        if (instance == null)
          instance = new EditorOptions();
      }
    }
    return instance;
  }

  public SelectionStyle selectionStyle = SelectionStyle.TEXT;
  public CursorStyle cursorStyle = CursorStyle.SMOOTH;
  public VCSpaceEditorTheme theme = VCSpaceEditorTheme.GITHUB_DARK;
  public VCSpaceEditorLanguage language = VCSpaceEditorLanguage.TEXT;
  public NewLineMode newLineMode = NewLineMode.AUTO;
  public FoldStyle foldStyle = FoldStyle.MARKBEGIN;
  public MergeUndoDeltas mergeUndoDeltas = MergeUndoDeltas.ALWAYS;
  
  public boolean highlightActiveLine = true;
  public boolean highlightSelectedWord = false;
  public boolean readOnly = false;
  public boolean enableAutoIndent = true;
  public boolean enableBasicAutocompletion = true;
  public boolean enableLiveAutocompletion = true;
  public boolean enableSnippets = true;
  // public boolean autoScrollEditorIntoView = true;
  public boolean hScrollBarAlwaysVisible = false;
  public boolean vScrollBarAlwaysVisible = false;
  public boolean highlightGutterLine = true;
  public boolean animatedScroll = false;
  public boolean showInvisibles = false;
  public boolean showPrintMargin = true;
  public boolean fadeFoldWidgets = false;
  public boolean showFoldWidgets = true;
  public boolean showLineNumbers = PreferencesUtils.lineNumbers();
  public boolean showGutter = true;
  public boolean displayIndentGuides = true;
  public boolean fixedWidthGutter = false;
  public boolean useSvgGutterIcons = false;
  public boolean useSoftTabs = PreferencesUtils.useSpaces();
  public boolean enableEmmet = false;
  public boolean useElasticTabstops = false;

  public int firstLineNumber = 1;
  public int fontSize = PreferencesUtils.getEditorTextSize();
  public int printMarginColumn = 80;
  public int tabSize = PreferencesUtils.getEditorTABSize();
  public int maxLines = 0;
  public int minLines = 0;
  
  public float scrollPastEnd = 0.5f; // typical values are 0, 0.5, and 1

  public void printMargin(int printMarginColumn) {
    this.showPrintMargin = true;
    this.printMarginColumn = printMarginColumn;
  }

  public static enum CursorStyle {
    ACE,
    SLIM,
    SMOOTH,
    WIDE;
  }

  public static enum SelectionStyle {
    LINE,
    TEXT;
  }

  public static enum NewLineMode {
    AUTO,
    UNIX,
    WINDOWS;
  }

  public static enum FoldStyle {
    MARKBEGIN,
    MARKBEGINEND,
    MANUAL;
  }
  
  public static enum MergeUndoDeltas {
    ALWAYS, NEVER, TIMED;
  }
}
