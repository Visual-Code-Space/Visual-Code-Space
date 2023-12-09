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

import io.github.rosemoe.sora.lang.styling.Span;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentLine;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.SymbolPairMatch;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.eclipse.tm4e.core.internal.grammar.tokenattrs.StandardTokenType;
import org.eclipse.tm4e.languageconfiguration.model.AutoClosingPairConditional;

public final class TextMateSymbolPairMatch extends SymbolPairMatch {

  private static final String surroundingPairFlag = "surroundingPair";

  private static final List<String> surroundingPairFlagWithList = List.of(surroundingPairFlag);

  private final TextMateLanguage language;

  private boolean enabled;

  public TextMateSymbolPairMatch(TextMateLanguage language) {
    super(null);
    this.language = language;

    setEnabled(true);
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    if (!enabled) {
      removeAllPairs();
    } else {
      updatePair();
    }
  }

  public void updatePair() {
    if (!enabled) {
      return;
    }
    removeAllPairs();
    var languageConfiguration = language.languageConfiguration;
    if (languageConfiguration == null) {
      return;
    }

    var mergePairs = new WeakHashMap<String, AutoClosingPairConditional>();

    var autoClosingPairs = languageConfiguration.getAutoClosingPairs();

    if (autoClosingPairs != null) {
      for (var autoClosingPair : autoClosingPairs) {
        mergePairs.put(autoClosingPair.open, autoClosingPair);
      }
    }

    var surroundingPairs = languageConfiguration.getSurroundingPairs();

    if (surroundingPairs != null) {
      for (var surroundingPair : surroundingPairs) {
        if (mergePairs.containsKey(surroundingPair.open)) {
          var mergePair = mergePairs.get(surroundingPair.open);

          if (mergePair.notIn == null) {
            mergePair.notIn = surroundingPairFlagWithList;
          } else {
            mergePair.notIn.add(surroundingPairFlag);
          }

          mergePairs.put(mergePair.open, mergePair);
          continue;
        }
        mergePairs.put(
            surroundingPair.open,
            new AutoClosingPairConditional(
                surroundingPair.open, surroundingPair.close, surroundingPairFlagWithList));
      }
    }

    for (Map.Entry<String, AutoClosingPairConditional> entry : mergePairs.entrySet()) {
      AutoClosingPairConditional pair = entry.getValue();

      SymbolPair symbol = null;
      if (pair.notIn == null || pair.notIn.isEmpty()) {
        symbol = new SymbolPair(pair.open, pair.close);
      } else {
        symbol = new SymbolPair(pair.open, pair.close, new SymbolPairEx(pair));
      }
      putPair(pair.open, symbol);
    }
  }

  static class SymbolPairEx implements SymbolPair.SymbolPairEx {

    private boolean isSurroundingPair = false;
    private int[] notInTokenTypeArray;

    public SymbolPairEx(AutoClosingPairConditional pair) {
      var notInList = pair.notIn;

      if (notInList.contains(surroundingPairFlag)) {
        isSurroundingPair = true;
      }

      if (notInList == surroundingPairFlagWithList) {
        return;
      } else {
        notInList.remove(surroundingPairFlag);
      }

      ensureNotInTokenTypeArray(notInList);
    }

    private void ensureNotInTokenTypeArray(List<String> notInList) {
      notInTokenTypeArray = new int[notInList.size()];

      for (int i = 0; i < notInTokenTypeArray.length; i++) {
        var notInValue = notInList.get(i).toLowerCase();

        int notInTokenType = StandardTokenType.Other;

        switch (notInValue) {
          case "string":
            notInTokenType = StandardTokenType.String;
            break;
          case "comment":
            notInTokenType = StandardTokenType.Comment;
            break;
          case "regex":
            notInTokenType = StandardTokenType.RegEx;
            break;
        }

        notInTokenTypeArray[i] = notInTokenType;
      }

      Arrays.sort(notInTokenTypeArray);
    }

    @Override
    public boolean shouldDoReplace(CodeEditor editor, ContentLine contentLine, int leftColumn) {

      if (editor.getCursor().isSelected()) {
        return true;
      }

      if (notInTokenTypeArray == null) {
        return true;
      }

      var cursor = editor.getCursor();

      var currentLine = cursor.getLeftLine();
      var currentColumn = cursor.getLeftColumn();

      var spansOnCurrentLine = editor.getSpansForLine(currentLine);

      var currentSpan = binarySearchSpan(spansOnCurrentLine, currentColumn);

      var extra = currentSpan.extra;

      if (extra instanceof Integer) {
        var index = Arrays.binarySearch(notInTokenTypeArray, (Integer) extra);
        return index < 0;
      }

      return true;
    }

    private int checkIndex(int index, int max) {
      return Math.max(Math.min(index, max), 0);
    }

    private Span binarySearchSpan(List<Span> spanList, int column) {
      int start = 0, end = spanList.size() - 1, middle, size = spanList.size() - 1;

      Span currentSpan = null;

      while (start <= end) {
        middle = (start + end) / 2;

        currentSpan = spanList.get(middle);
        if (currentSpan.column == column) {
          break;
        }

        if (currentSpan.column < column) {
          var nextSpan = spanList.get(checkIndex(middle + 1, size));

          if (nextSpan.column > column) {
            return currentSpan;
          }

          start++;

          continue;
        }

        // if (currentSpan.column > column)
        var previousSpan = spanList.get(checkIndex(middle - 1, size));

        if (previousSpan.column < column) {
          return currentSpan;
        }

        end--;
      }

      return currentSpan;
    }

    @Override
    public boolean shouldDoAutoSurround(Content content) {
      return isSurroundingPair && content.getCursor().isSelected();
    }
  }
}
