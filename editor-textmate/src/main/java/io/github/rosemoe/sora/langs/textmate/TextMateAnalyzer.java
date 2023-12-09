/*
 *    sora-editor - the awesome code editor for Android
 *    https://github.com/Rosemoe/sora-editor
 *    Copyright (C) 2020-2023  Rosemoe
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 *
 *     Please contact Rosemoe by email 2073412493@qq.com if you need
 *     additional information or have any questions
 */
package io.github.rosemoe.sora.langs.textmate;

import android.annotation.SuppressLint;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.github.rosemoe.sora.lang.analysis.AsyncIncrementalAnalyzeManager;
import io.github.rosemoe.sora.lang.brackets.BracketsProvider;
import io.github.rosemoe.sora.lang.brackets.OnlineBracketsMatcher;
import io.github.rosemoe.sora.lang.styling.CodeBlock;
import io.github.rosemoe.sora.lang.styling.Span;
import io.github.rosemoe.sora.lang.styling.TextStyle;
import io.github.rosemoe.sora.langs.textmate.folding.FoldingHelper;
import io.github.rosemoe.sora.langs.textmate.folding.IndentRange;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.langs.textmate.utils.StringUtils;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentLine;
import io.github.rosemoe.sora.util.ArrayList;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.eclipse.tm4e.core.grammar.IGrammar;
import org.eclipse.tm4e.core.internal.grammar.tokenattrs.EncodedTokenAttributes;
import org.eclipse.tm4e.core.internal.oniguruma.OnigRegExp;
import org.eclipse.tm4e.core.internal.oniguruma.OnigResult;
import org.eclipse.tm4e.core.internal.oniguruma.OnigString;
import org.eclipse.tm4e.core.internal.theme.FontStyle;
import org.eclipse.tm4e.core.internal.theme.Theme;
import org.eclipse.tm4e.languageconfiguration.model.LanguageConfiguration;

public final class TextMateAnalyzer extends AsyncIncrementalAnalyzeManager<LineState, Span>
    implements FoldingHelper, ThemeRegistry.ThemeChangeListener {

  @NonNull private final TextMateLanguage language;

  @NonNull private final IGrammar grammar;

  @NonNull private final ThemeRegistry themeRegistry;

  @NonNull private Theme theme;

  @Nullable private BracketsProvider bracketsProvider;

  @Nullable private OnigRegExp cachedRegExp;

  private boolean foldingOffside;

  public TextMateAnalyzer(TextMateLanguage language, IGrammar grammar) {
    this.language = language;

    this.themeRegistry = ThemeRegistry.getInstance();

    this.theme = themeRegistry.getCurrentThemeModel().getTheme();

    this.grammar = grammar;

    if (!themeRegistry.hasListener(this)) {
      themeRegistry.addListener(this);
    }

    createBracketsProvider(language.languageConfiguration);
    createFoldingExp(language.languageConfiguration);
  }

  private void createBracketsProvider(LanguageConfiguration languageConfiguration) {
    if (languageConfiguration != null) {
      var pairs = languageConfiguration.getBrackets();
      if (pairs == null) return;

      int size = 0;
      for (var pair : pairs) {
        if (pair.open.length() == 1 && pair.close.length() == 1) {
          size++;
        }
      }

      if (size > 0) {
        var pairArr = new char[size * 2];
        int i = 0;

        for (var pair : pairs) {
          if (pair.open.length() == 1 && pair.close.length() == 1) {
            pairArr[i * 2] = pair.open.charAt(0);
            pairArr[i * 2 + 1] = pair.close.charAt(0);
            i++;
          }
        }

        bracketsProvider = new OnlineBracketsMatcher(pairArr, 100000);
      }
    }
  }

  private void createFoldingExp(LanguageConfiguration languageConfiguration) {
    if (languageConfiguration != null) {
      var markers = languageConfiguration.getFolding();
      if (markers == null) return;
      foldingOffside = markers.offSide;
      cachedRegExp =
          new OnigRegExp("(" + markers.markersStart + ")|(?:" + markers.markersEnd + ")");
    }
  }

  @Override
  public LineState getInitialState() {
    return null;
  }

  @Override
  public boolean stateEquals(LineState state, LineState another) {
    if (state == null && another == null) {
      return true;
    }
    if (state != null && another != null) {
      return Objects.equals(state.tokenizeState, another.tokenizeState);
    }
    return false;
  }

  @Override
  public int getIndentFor(int line) {
    return getState(line).state.indent;
  }

  @Override
  public OnigResult getResultFor(int line) {
    return getState(line).state.foldingCache;
  }

  @Override
  public List<CodeBlock> computeBlocks(Content text, CodeBlockAnalyzeDelegate delegate) {
    ArrayList<CodeBlock> list = new ArrayList<>();
    analyzeCodeBlocks(text, list, delegate);
    if (delegate.isNotCancelled()) {
      withReceiver(r -> r.updateBracketProvider(this, bracketsProvider));
    }
    return list;
  }

  public void analyzeCodeBlocks(
      Content model, ArrayList<CodeBlock> blocks, CodeBlockAnalyzeDelegate delegate) {
    if (cachedRegExp == null) {
      return;
    }

    try {
      var foldingRegions =
          IndentRange.computeRanges(
              model, language.getTabSize(), foldingOffside, this, cachedRegExp, delegate);
      blocks.ensureCapacity(foldingRegions.length());

      for (int i = 0; i < foldingRegions.length() && delegate.isNotCancelled(); i++) {
        int startLine = foldingRegions.getStartLineNumber(i);
        int endLine = foldingRegions.getEndLineNumber(i);

        if (startLine != endLine) {
          var codeBlock = new CodeBlock();
          codeBlock.toBottomOfEndLine = true;
          codeBlock.startLine = startLine;
          codeBlock.endLine = endLine;

          // It's safe here to use raw data because the Content is only held by this thread
          var length = model.getColumnCount(startLine);
          var chars = model.getLine(startLine).getRawData();

          codeBlock.startColumn =
              IndentRange.computeStartColumn(chars, length, language.getTabSize());
          codeBlock.endColumn = codeBlock.startColumn;
          blocks.add(codeBlock);
        }
      }

      Collections.sort(blocks, CodeBlock.COMPARATOR_END);
    } catch (Exception e) {
      e.printStackTrace();
    }

    getManagedStyles().setIndentCountMode(true);
  }

  @Override
  @SuppressLint("NewApi")
  public synchronized LineTokenizeResult<LineState, Span> tokenizeLine(
      CharSequence lineC, LineState state, int lineIndex) {
    String line = ((ContentLine) lineC).toStringWithNewline();
    List<Span> tokens = new ArrayList<>();
    boolean surrogate = StringUtils.checkSurrogate(line);
    var lineTokens =
        grammar.tokenizeLine2(
            line, state != null ? state.tokenizeState : null, Duration.ofSeconds(2));

    int tokensLength = lineTokens.getTokens().length / 2;
    int startIndex = 0;

    for (int i = 0; i < tokensLength; i++) {
      startIndex =
          StringUtils.convertUnicodeOffsetToUtf16(line, lineTokens.getTokens()[2 * i], surrogate);
      if (i == 0 && startIndex != 0) {
        tokens.add(Span.obtain(0, EditorColorScheme.TEXT_NORMAL));
      }

      int metadata = lineTokens.getTokens()[2 * i + 1];
      int foreground = EncodedTokenAttributes.getForeground(metadata);
      int fontStyle = EncodedTokenAttributes.getFontStyle(metadata);
      int tokenType = EncodedTokenAttributes.getTokenType(metadata);

      long style =
          TextStyle.makeStyle(
              foreground + 255,
              0,
              (fontStyle & FontStyle.Bold) != 0,
              (fontStyle & FontStyle.Italic) != 0,
              false);
      Span span = Span.obtain(startIndex, style);
      span.extra = tokenType;

      if ((fontStyle & FontStyle.Underline) != 0) {
        String color = theme.getColor(foreground);
        if (color != null) {
          span.underlineColor = Color.parseColor(color);
        }
      }
      tokens.add(span);
    }

    var onigResult = cachedRegExp != null ? cachedRegExp.search(OnigString.of(line), 0) : null;

    var newState =
        new LineState(
            lineTokens.getRuleStack(),
            onigResult,
            IndentRange.computeIndentLevel(
                ((ContentLine) lineC).getRawData(), line.length() - 1, language.getTabSize()));

    return new LineTokenizeResult<>(newState, null, tokens);
  }

  @Override
  public void destroy() {
    super.destroy();
    themeRegistry.removeListener(this);
  }

  @Override
  public List<Span> generateSpansForLine(LineTokenizeResult<LineState, Span> tokens) {
    return null;
  }

  @Override
  public void onChangeTheme(ThemeModel newTheme) {
    this.theme = newTheme.getTheme();
  }
}
