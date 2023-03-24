package com.raredev.vcspace.ui.editor.textmate;

import android.content.Context;
import android.graphics.Color;
import com.google.android.material.elevation.SurfaceColors;
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import java.util.List;
import org.eclipse.tm4e.core.internal.theme.IRawTheme;
import org.eclipse.tm4e.core.internal.theme.ThemeRaw;

public class DynamicTextMateColorScheme extends TextMateColorScheme {
  private Context context;

  public DynamicTextMateColorScheme(
      Context context, ThemeRegistry themeRegistry, ThemeModel themeModel) throws Exception {
    super(themeRegistry, themeModel);
    this.context = context;
  }

  public static DynamicTextMateColorScheme create(Context context, ThemeRegistry themeRegistry)
      throws Exception {
    return create(context, ThemeRegistry.getInstance(), themeRegistry.getCurrentThemeModel());
  }

  public static DynamicTextMateColorScheme create(
      Context context, ThemeRegistry themeRegistry, ThemeModel themeModel) throws Exception {
    return new DynamicTextMateColorScheme(context, themeRegistry, themeModel);
  }

  @Override
  public void applyDefault() {
    super.applyDefault();

    IRawTheme rawTheme = getRawTheme();
    if (rawTheme == null) {
      return;
    }
    var settings = rawTheme.getSettings();

    ThemeRaw themeRaw;

    /*if (settings == null) {
      themeRaw = ((ThemeRaw) ((ThemeRaw) rawTheme).get("colors"));
      applyVSCTheme(themeRaw);
    } else {*/
      themeRaw = (ThemeRaw) ((List<?>) settings).get(0);
      themeRaw = (ThemeRaw) themeRaw.getSetting();

      applyTMTheme(themeRaw);
    //}
  }

  /*private void applyVSCTheme(ThemeRaw themeRaw) {
    setColor(LINE_DIVIDER, Color.TRANSPARENT);
    setColor(WHOLE_BACKGROUND, SurfaceColors.SURFACE_0.getColor(context));
    setColor(LINE_NUMBER_BACKGROUND, SurfaceColors.SURFACE_0.getColor(context));

    setColor(CURRENT_LINE, SurfaceColors.SURFACE_1.getColor(context));
    
    setColor(BLOCK_LINE, SurfaceColors.SURFACE_3.getColor(context));
    int blockLineColorCur = (getColor(BLOCK_LINE)) | 0xFF000000;
    setColor(BLOCK_LINE_CURRENT, blockLineColorCur);
    
    setColor(SELECTED_TEXT_BACKGROUND, blockLineColorCur);

    String caret = (String) themeRaw.get("editorCursor.foreground");
    if (caret != null) {
      setColor(SELECTION_INSERT, Color.parseColor(caret));
    }

    String invisibles = (String) themeRaw.get("editorWhitespace.foreground");
    if (invisibles != null) {
      setColor(NON_PRINTABLE_CHAR, Color.parseColor(invisibles));
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
  }*/

  private void applyTMTheme(ThemeRaw themeRaw) {
    setColor(LINE_DIVIDER, Color.TRANSPARENT);
    setColor(WHOLE_BACKGROUND, SurfaceColors.SURFACE_0.getColor(context));
    setColor(LINE_NUMBER_BACKGROUND, SurfaceColors.SURFACE_0.getColor(context));

    setColor(CURRENT_LINE, SurfaceColors.SURFACE_1.getColor(context));
    
    setColor(BLOCK_LINE, SurfaceColors.SURFACE_3.getColor(context));
    int blockLineColorCur = (getColor(BLOCK_LINE)) | 0xFF000000;
    setColor(BLOCK_LINE_CURRENT, blockLineColorCur);
    
    setColor(SELECTED_TEXT_BACKGROUND, blockLineColorCur);
    
    String caret = (String) themeRaw.get("caret");
    if (caret != null) {
      setColor(SELECTION_INSERT, Color.parseColor(caret));
    }

    String lineHighlightBackground = (String) themeRaw.get("lineNumber");
    if (lineHighlightBackground != null) {
      setColor(LINE_NUMBER, Color.parseColor(lineHighlightBackground));
    }
    
    String lineHighlightActiveForeground = (String) themeRaw.get("currentLineNumber");
    if (lineHighlightBackground != null) {
      setColor(LINE_NUMBER_CURRENT, Color.parseColor(lineHighlightActiveForeground));
    }
    
    String invisibles = (String) themeRaw.get("invisibles");
    if (invisibles != null) {
      setColor(NON_PRINTABLE_CHAR, Color.parseColor(invisibles));
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
  }
}
