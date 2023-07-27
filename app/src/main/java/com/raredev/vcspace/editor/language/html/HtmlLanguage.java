package com.raredev.vcspace.editor.language.html;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.raredev.vcspace.editor.IDECodeEditor;
import com.raredev.vcspace.editor.completion.CompletionParams;
import com.raredev.vcspace.editor.completion.CompletionProvider;
import com.raredev.vcspace.editor.completion.VCSpaceCompletionItem;
import com.raredev.vcspace.editor.language.html.completion.HtmlCompletionProvider;
import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandleResult;
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandler;
import io.github.rosemoe.sora.lang.styling.Styles;
import io.github.rosemoe.sora.lang.styling.StylesUtils;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.text.Cursor;
import io.github.rosemoe.sora.text.TextRange;
import io.github.rosemoe.sora.text.TextUtils;
import io.github.rosemoe.sora.util.MyCharacter;
import java.util.List;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.DOMParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;

public class HtmlLanguage extends VCSpaceTMLanguage {

  private IDECodeEditor editor;

  public HtmlLanguage(IDECodeEditor editor) {
    super(
        GrammarRegistry.getInstance().findGrammar("text.html.basic"),
        GrammarRegistry.getInstance().findLanguageConfiguration("text.html.basic"),
        ThemeRegistry.getInstance(),
        "text.html.basic");
    this.editor = editor;
  }

  @Override
  public void requireAutoComplete(
      @NonNull ContentReference content,
      @NonNull CharPosition position,
      @NonNull CompletionPublisher publisher,
      @NonNull Bundle extraArguments) {
    super.requireAutoComplete(content, position, publisher, extraArguments);
    var prefix = CompletionHelper.computePrefix(content, position, this::checkIsCompletionChar);

    CompletionParams params =
        new CompletionParams(
            editor,
            editor.getText().toString(),
            prefix,
            position.line,
            position.column,
            position.index);

    List<VCSpaceCompletionItem> completions =
        CompletionProvider.getCompletionProvider(HtmlCompletionProvider.class)
            .getCompletions(params);
    for (VCSpaceCompletionItem item : completions) {
      publisher.addItem(item);
    }
  }

  @Override
  public NewlineHandler[] getNewlineHandlers() {
    return new NewlineHandler[] {new EndTagNewlineHandler(), new StartTagNewlineHandler()};
  }

  @Override
  public String formatCode(Content text, TextRange range) {
    String html = text.toString();

    Document doc = Jsoup.parse(html, Parser.htmlParser());
    var outputSettings = new Document.OutputSettings();
    outputSettings.indentAmount(getTabSize());
    doc.outputSettings(outputSettings.prettyPrint(true));
    return doc.html();
  }

  @Override
  public void editorCommitText(CharSequence text) {
    super.editorCommitText(text);
    try {
      if (text.length() == 1) {
        char c = text.charAt(0);
        if (c != '/') {
          return;
        }
        Cursor cursor = editor.getCursor();

        DOMDocument document = DOMParser.getInstance().parse(editor.getText().toString(), "", null);
        DOMNode nodeAt = document.findNodeAt(cursor.getLeft());
        if (!HtmlUtils.isClosed(nodeAt)
            && nodeAt.getNodeName() != null
            && !Tag.valueOf(nodeAt.getNodeName()).isEmpty()) {
          String insertText = nodeAt.getNodeName() + ">";
          editor.commitText(insertText);
        }
      }
    } catch (Exception e) {
    }
  }

  @Override
  public boolean checkIsCompletionChar(char c) {
    return MyCharacter.isJavaIdentifierPart(c) || c == '\"' || c == '<' || c == '/';
  }

  public class EndTagNewlineHandler implements NewlineHandler {

    @Override
    public boolean matchesRequirement(
        @NonNull Content text, @NonNull CharPosition position, @Nullable Styles style) {
      var line = text.getLine(position.line);
      int index = position.column;
      var beforeText = line.subSequence(0, index).toString();
      var afterText = line.subSequence(index, line.length()).toString();

      if (StylesUtils.checkNoCompletion(style, position)) {
        return false;
      }

      if (beforeText.trim().startsWith("<!") || beforeText.trim().endsWith("/>")) {
        return false;
      }

      return beforeText.trim().endsWith(">") && afterText.trim().startsWith("</");
    }

    @NonNull
    @Override
    public NewlineHandleResult handleNewline(
        @NonNull Content text,
        @NonNull CharPosition position,
        @Nullable Styles style,
        int tabSize) {
      var line = text.getLine(position.line);
      int index = position.column;
      var beforeText = line.subSequence(0, index).toString();
      var afterText = line.subSequence(index, line.length()).toString();
      return handleNewline(beforeText, afterText, tabSize);
    }

    @NonNull
    public NewlineHandleResult handleNewline(String beforeText, String afterText, int tabSize) {
      int count = TextUtils.countLeadingSpaceCount(beforeText, tabSize);
      String text;
      StringBuilder sb =
          new StringBuilder("\n")
              .append(TextUtils.createIndent(count + tabSize, tabSize, useTab()))
              .append('\n')
              .append(text = TextUtils.createIndent(count, tabSize, useTab()));
      int shiftLeft = text.length() + 1;
      return new NewlineHandleResult(sb, shiftLeft);
    }
  }

  public class StartTagNewlineHandler implements NewlineHandler {

    @Override
    public boolean matchesRequirement(
        @NonNull Content text, @NonNull CharPosition position, @Nullable Styles style) {
      var line = text.getLine(position.line);
      int index = position.column;
      var beforeText = line.subSequence(0, index).toString();
      var afterText = line.subSequence(index, line.length()).toString();

      if (StylesUtils.checkNoCompletion(style, position)) {
        return false;
      }

      if (beforeText.trim().startsWith("<!") || beforeText.trim().endsWith("/>")) {
        return false;
      }

      if (beforeText.trim().endsWith(">")) {
        int openTagIndex = beforeText.lastIndexOf('<');
        int closeTagIndex = beforeText.lastIndexOf('>');
        return beforeText.charAt(openTagIndex + 1) != '/';
      }

      return false;
    }

    @NonNull
    @Override
    public NewlineHandleResult handleNewline(
        @NonNull Content text,
        @NonNull CharPosition position,
        @Nullable Styles style,
        int tabSize) {
      var line = text.getLine(position.line);
      int index = position.column;
      var beforeText = line.subSequence(0, index).toString();
      var afterText = line.subSequence(index, line.length()).toString();
      return handleNewline(beforeText, afterText, tabSize);
    }

    @NonNull
    public NewlineHandleResult handleNewline(String beforeText, String afterText, int tabSize) {
      int count = TextUtils.countLeadingSpaceCount(beforeText, tabSize);
      String text;
      StringBuilder sb =
          new StringBuilder()
              .append("\n")
              .append(TextUtils.createIndent(count + tabSize, tabSize, useTab()));
      return new NewlineHandleResult(sb, 0);
    }
  }
}
