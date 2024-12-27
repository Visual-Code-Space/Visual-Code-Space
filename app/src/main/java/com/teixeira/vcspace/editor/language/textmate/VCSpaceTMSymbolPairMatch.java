/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.teixeira.vcspace.editor.language.textmate;

import org.eclipse.tm4e.core.internal.grammar.tokenattrs.StandardTokenType;
import org.eclipse.tm4e.languageconfiguration.internal.model.AutoClosingPairConditional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.rosemoe.sora.lang.styling.Span;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentLine;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.SymbolPairMatch;

public class VCSpaceTMSymbolPairMatch extends SymbolPairMatch {
  private static final String surroundingPairFlag = "surroundingPair";

  private static final List<String> surroundingPairFlagWithList = List.of(surroundingPairFlag);

  private final VCSpaceTMLanguage language;

  private boolean enabled = true;

  public VCSpaceTMSymbolPairMatch(VCSpaceTMLanguage language) {
    super(new SymbolPairMatch.DefaultSymbolPairs());
    this.language = language;

    updatePair();
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

    var languageConfiguration = language.languageConfiguration;

    if (languageConfiguration == null) {
      return;
    }

    removeAllPairs();

    var surroundingPairs = languageConfiguration.getSurroundingPairs();

    var autoClosingPairs = languageConfiguration.getAutoClosingPairs();

    var mergePairs = new ArrayList<AutoClosingPairConditional>();

    if (autoClosingPairs != null) {
      mergePairs.addAll(autoClosingPairs);
    }

    if (surroundingPairs != null) {

      for (var surroundingPair : surroundingPairs) {

        var newPair = new AutoClosingPairConditional(surroundingPair.open, surroundingPair.close,
          surroundingPairFlagWithList);

        mergePairs.add(newPair);
      }
    }

    for (var pair : mergePairs) {
      putPair(pair.open, new SymbolPair(pair.open, pair.close, new VCSpaceTMSymbolPairMatch.SymbolPairEx(pair)));
    }
  }

  static class SymbolPairEx implements SymbolPair.SymbolPairEx {

    int[] notInTokenTypeArray;

    boolean isSurroundingPair = false;

    public SymbolPairEx(AutoClosingPairConditional pair) {

      var notInList = pair.notIn;

      if (notInList == null || notInList.isEmpty()) {
        notInTokenTypeArray = null;
        return;
      }

      if (notInList.contains(surroundingPairFlag)) {
        //
        isSurroundingPair = true;
        if (notInList == surroundingPairFlagWithList) {
          return;
        } else {
          notInList.remove(surroundingPairFlag);
        }
      }

      notInTokenTypeArray = new int[notInList.size()];

      for (int i = 0; i < notInTokenTypeArray.length; i++) {
        var notInValue = notInList.get(i).toLowerCase();

        var notInTokenType = StandardTokenType.String;

        switch (notInValue) {
          case "string":
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
    public boolean shouldReplace(CodeEditor editor, ContentLine contentLine, int leftColumn) {
      if (editor.getCursor().isSelected()) {
        return isSurroundingPair;
      }
      // No text was selected，so should not complete surrounding pair
      if (isSurroundingPair) {
        return false;
      }

      if (notInTokenTypeArray == null) {
        return true;
      }

      var cursor = editor.getCursor();

      var currentLine = cursor.getLeftLine();
      var currentColumn = cursor.getLeftColumn();

      var spansOnCurrentLine = editor.getSpansForLine(currentLine);

      var currentSpan = binarySearchSpan(spansOnCurrentLine, currentColumn);
      var extra = currentSpan.getExtra();

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
        if (currentSpan.getColumn() == column) {
          break;
        }

        if (currentSpan.getColumn() < column) {
          var nextSpan = spanList.get(checkIndex(middle + 1, size));

          if (nextSpan.getColumn() > column) {
            return currentSpan;
          }

          start++;

          continue;

        }

        // if (currentSpan.column > column)
        var previousSpan = spanList.get(checkIndex(middle - 1, size));

        if (previousSpan.getColumn() < column) {
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
