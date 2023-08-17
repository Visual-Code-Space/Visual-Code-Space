package io.github.rosemoe.sora.langs.textmate;

import android.graphics.Color;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import java.util.List;
import org.eclipse.tm4e.core.internal.theme.IRawTheme;
import org.eclipse.tm4e.core.internal.theme.Theme;
import org.eclipse.tm4e.core.internal.theme.ThemeRaw;

public class VCSpaceTMScheme extends EditorColorScheme
    implements ThemeRegistry.ThemeChangeListener {

  private Theme theme;

  private IRawTheme rawTheme;

  private ThemeModel currentTheme;

  private final ThemeRegistry themeRegistry;

  public VCSpaceTMScheme(ThemeModel themeModel) throws Exception {
    currentTheme = themeModel;

    themeRegistry = ThemeRegistry.getInstance();
  }

  @Override
  public void applyDefault() {
    super.applyDefault();
    if (themeRegistry != null && !themeRegistry.hasListener(this)) {
      themeRegistry.addListener(this);
    }
    if (rawTheme == null) {
      return;
    }
    var settings = rawTheme.getSettings();

    ThemeRaw themeRaw;

    if (settings == null) {
      themeRaw = ((ThemeRaw) ((ThemeRaw) rawTheme).get("colors"));
      applyVSCTheme(themeRaw);
    } else {
      themeRaw = (ThemeRaw) ((List<?>) settings).get(0);
      themeRaw = (ThemeRaw) themeRaw.getSetting();

      applyTMTheme(themeRaw);
    }
  }

  private void applyVSCTheme(ThemeRaw themeRaw) {
    setColor(LINE_DIVIDER, Color.TRANSPARENT);

    String caret = (String) themeRaw.get("editorCursor.foreground");
    if (caret != null) {
      setColor(SELECTION_INSERT, Color.parseColor(caret));
    }

    String selection = (String) themeRaw.get("editor.selectionBackground");
    if (selection != null) {
      setColor(SELECTED_TEXT_BACKGROUND, Color.parseColor(selection));
    }

    String selectedText = (String) themeRaw.get("selectedText");
    if (selectedText != null) {
      setColor(TEXT_SELECTED, Color.parseColor(selectedText));
    }

    String invisibles = (String) themeRaw.get("editorWhitespace.foreground");
    if (invisibles != null) {
      setColor(NON_PRINTABLE_CHAR, Color.parseColor(invisibles));
    }

    String lineHighlight = (String) themeRaw.get("editor.lineHighlightBackground");
    if (lineHighlight != null) {
      setColor(CURRENT_LINE, Color.parseColor(lineHighlight));
    }

    String background = (String) themeRaw.get("editor.background");
    if (background != null) {
      setColor(WHOLE_BACKGROUND, Color.parseColor(background));
      setColor(LINE_NUMBER_BACKGROUND, Color.parseColor(background));
    }

    String lineHighlightBackground = (String) themeRaw.get("editorLineNumber.foreground");

    if (lineHighlightBackground != null) {
      setColor(LINE_NUMBER, Color.parseColor(lineHighlightBackground));
    }

    String lineHighlightActiveForeground =
        (String) themeRaw.get("editorLineNumber.activeForeground");

    if (lineHighlightActiveForeground != null) {
      setColor(LINE_NUMBER_CURRENT, Color.parseColor(lineHighlightActiveForeground));
    }

    String foreground = (String) themeRaw.get("editor.foreground");

    if (foreground != null) {
      setColor(TEXT_NORMAL, Color.parseColor(foreground));
    }

    String highlightedDelimetersForeground =
        (String) themeRaw.get("highlightedDelimetersForeground");
    if (highlightedDelimetersForeground != null) {
      setColor(
          HIGHLIGHTED_DELIMITERS_FOREGROUND, Color.parseColor(highlightedDelimetersForeground));
    }

    String editorIndentGuideBackground = (String) themeRaw.get("editorIndentGuide.background");
    int blockLineColor =
        ((getColor(WHOLE_BACKGROUND) + getColor(TEXT_NORMAL)) / 2) & 0x00FFFFFF | 0x88000000;
    int blockLineColorCur = (blockLineColor) | 0xFF000000;

    if (editorIndentGuideBackground != null) {
      setColor(BLOCK_LINE, Color.parseColor(editorIndentGuideBackground));
    } else {
      setColor(BLOCK_LINE, blockLineColor);
    }

    String editorIndentGuideActiveBackground =
        (String) themeRaw.get("editorIndentGuide.activeBackground");

    if (editorIndentGuideActiveBackground != null) {
      setColor(BLOCK_LINE_CURRENT, Color.parseColor(editorIndentGuideActiveBackground));
    } else {
      setColor(BLOCK_LINE_CURRENT, blockLineColorCur);
    }
  }

  @Override
  public boolean isDark() {
    var superIsDark = super.isDark();
    if (superIsDark) {
      return true;
    }
    if (currentTheme != null) {
      return currentTheme.isDark();
    }
    return false;
  }

  private void applyTMTheme(ThemeRaw themeRaw) {
    setColor(LINE_DIVIDER, Color.TRANSPARENT);

    String caret = (String) themeRaw.get("caret");
    if (caret != null) {
      setColor(SELECTION_INSERT, Color.parseColor(caret));
      setColor(SELECTION_HANDLE, Color.parseColor(caret));
    }

    String selection = (String) themeRaw.get("selection");
    if (selection != null) {
      setColor(SELECTED_TEXT_BACKGROUND, Color.parseColor(selection));
    }

    String selectedText = (String) themeRaw.get("selectedText");
    if (selectedText != null) {
      setColor(TEXT_SELECTED, Color.parseColor(selectedText));
    }

    String invisibles = (String) themeRaw.get("invisibles");
    if (invisibles != null) {
      setColor(NON_PRINTABLE_CHAR, Color.parseColor(invisibles));
    }

    String lineHighlight = (String) themeRaw.get("lineHighlight");
    if (lineHighlight != null) {
      setColor(CURRENT_LINE, Color.parseColor(lineHighlight));
    }

    String background = (String) themeRaw.get("background");
    if (background != null) {
      setColor(WHOLE_BACKGROUND, Color.parseColor(background));
      setColor(LINE_NUMBER_BACKGROUND, Color.parseColor(background));
    }

    String foreground = (String) themeRaw.get("foreground");
    if (foreground != null) {
      setColor(TEXT_NORMAL, Color.parseColor(foreground));
    }

    String highlightedDelimetersForeground =
        (String) themeRaw.get("highlightedDelimetersForeground");
    if (highlightedDelimetersForeground != null) {
      setColor(
          HIGHLIGHTED_DELIMITERS_FOREGROUND, Color.parseColor(highlightedDelimetersForeground));
    }

    String lineNumber = (String) themeRaw.get("lineNumber");

    if (lineNumber != null) {
      setColor(LINE_NUMBER, Color.parseColor(lineNumber));
    }

    String lineNumberCurrent = (String) themeRaw.get("lineNumberCurrent");

    if (lineNumberCurrent != null) {
      setColor(LINE_NUMBER_CURRENT, Color.parseColor(lineNumberCurrent));
    }

    String blockLine = (String) themeRaw.get("blockLine");
    if (blockLine != null) {
      setColor(BLOCK_LINE, Color.parseColor(blockLine));
    }

    String blockLineCurrent = (String) themeRaw.get("blockLineCurrent");

    if (blockLineCurrent != null) {
      setColor(BLOCK_LINE_CURRENT, Color.parseColor(blockLineCurrent));
    }
  }

  @Override
  public int getColor(int type) {
    if (type >= 255) {
      // Cache colors in super class
      var superColor = super.getColor(type);
      if (superColor == 0) {
        if (theme != null) {
          String color = theme.getColor(type - 255);
          var newColor = color != null ? Color.parseColor(color) : super.getColor(TEXT_NORMAL);
          super.colors.put(type, newColor);
          return newColor;
        }
        return super.getColor(TEXT_NORMAL);
      } else {
        return superColor;
      }
    }
    return super.getColor(type);
  }

  @Override
  public void detachEditor(CodeEditor editor) {
    super.detachEditor(editor);
    themeRegistry.removeListener(this);
  }

  @Override
  public void attachEditor(CodeEditor editor) {
    super.attachEditor(editor);
    try {
      themeRegistry.loadTheme(currentTheme);
    } catch (Exception e) {
      // throw new RuntimeException(e);
    }
    setTheme(currentTheme);
  }

  @Override
  public void onChangeTheme(ThemeModel newTheme) {
    setTheme(newTheme);
  }

  public void setTheme(ThemeModel themeModel) {
    currentTheme = themeModel;
    super.colors.clear();
    this.rawTheme = themeModel.getRawTheme();
    this.theme = themeModel.getTheme();
    applyDefault();
  }
}
