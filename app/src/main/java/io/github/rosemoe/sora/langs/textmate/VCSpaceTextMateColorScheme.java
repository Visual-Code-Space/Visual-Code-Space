package io.github.rosemoe.sora.langs.textmate;

import android.content.Context;
import android.graphics.Color;
import com.google.android.material.elevation.SurfaceColors;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import java.util.List;
import org.eclipse.tm4e.core.internal.theme.IRawTheme;
import org.eclipse.tm4e.core.internal.theme.Theme;
import org.eclipse.tm4e.core.internal.theme.ThemeRaw;

public class VCSpaceTextMateColorScheme extends EditorColorScheme
    implements ThemeRegistry.ThemeChangeListener {

  private Theme theme;

  private IRawTheme rawTheme;

  private ThemeModel currentTheme;

  private final ThemeRegistry themeRegistry;

  private Context context;

  public VCSpaceTextMateColorScheme(
      Context context, ThemeRegistry themeRegistry, ThemeModel themeModel) throws Exception {
    this.themeRegistry = themeRegistry;

    currentTheme = themeModel;

    this.context = context;
  }

  public static VCSpaceTextMateColorScheme create(Context context) throws Exception {
    return new VCSpaceTextMateColorScheme(
        context, ThemeRegistry.getInstance(), ThemeRegistry.getInstance().getCurrentThemeModel());
  }

  @Override
  public void onChangeTheme(ThemeModel newTheme) {}

  @Override
  public void applyDefault() {
    if (themeRegistry != null && !themeRegistry.hasListener(this)) {
      themeRegistry.addListener(this);
    }

    if (rawTheme == null) {
      return;
    }
    var settings = rawTheme.getSettings();

    ThemeRaw themeRaw;

    themeRaw = (ThemeRaw) ((List<?>) settings).get(0);
    themeRaw = (ThemeRaw) themeRaw.getSetting();

    applyTMTheme(themeRaw);
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
    setColor(WHOLE_BACKGROUND, SurfaceColors.SURFACE_0.getColor(context));
    setColor(LINE_NUMBER_BACKGROUND, SurfaceColors.SURFACE_0.getColor(context));

    setColor(CURRENT_LINE, SurfaceColors.SURFACE_1.getColor(context));

    setColor(BLOCK_LINE, SurfaceColors.SURFACE_3.getColor(context));

    String caret = (String) themeRaw.get("caret");
    if (caret != null) {
      setColor(SELECTION_INSERT, Color.parseColor(caret));
      setColor(SELECTION_HANDLE, Color.parseColor(caret));

      int blockLineColorCur = (getColor(BLOCK_LINE)) | Color.parseColor(caret);
      setColor(BLOCK_LINE_CURRENT, blockLineColorCur);
      setColor(SELECTED_TEXT_BACKGROUND, blockLineColorCur);

      setColor(HIGHLIGHTED_DELIMITERS_FOREGROUND, blockLineColorCur);
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

  public void setTheme(ThemeModel themeModel) {
    currentTheme = themeModel;
    super.colors.clear();
    this.rawTheme = themeModel.getRawTheme();
    this.theme = themeModel.getTheme();
    applyDefault();
  }
}
