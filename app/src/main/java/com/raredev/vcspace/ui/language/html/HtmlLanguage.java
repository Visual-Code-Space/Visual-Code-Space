package com.raredev.vcspace.ui.language.html;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.raredev.vcspace.ui.language.html.completion.HtmlCompletionItem;
import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.completion.SimpleCompletionItem;
import io.github.rosemoe.sora.lang.completion.SimpleSnippetCompletionItem;
import io.github.rosemoe.sora.lang.completion.SnippetDescription;
import io.github.rosemoe.sora.lang.completion.snippet.CodeSnippet;
import io.github.rosemoe.sora.lang.completion.snippet.parser.CodeSnippetParser;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;
import io.github.rosemoe.sora.widget.SymbolPairMatch;

public class HtmlLanguage extends VCSpaceTMLanguage {

  private static final CodeSnippet HTML_SNIPPET =
      CodeSnippetParser.parse(
          "<!DOCTYPE html>\n<html>\n  <head>\n    <meta charset=\"UTF-8\" />\n  </head>\n  <body>\n    $0\n  </body>\n</html>");

  public HtmlLanguage() {
    super(
        GrammarRegistry.getInstance().findGrammar("text.html.basic"),
        GrammarRegistry.getInstance().findLanguageConfiguration("text.html.basic"),
        ThemeRegistry.getInstance(),
        true);
    getSymbolPairs().putPair("<", new SymbolPairMatch.SymbolPair("<", ">"));
  }

  @Override
  public void requireAutoComplete(
      @NonNull ContentReference content,
      @NonNull CharPosition position,
      @NonNull CompletionPublisher publisher,
      @NonNull Bundle extraArguments) {
    var prefix = CompletionHelper.computePrefix(content, position, this::checkIsCompletionChar);

    if ("html".startsWith(prefix) && prefix.length() > 0) {
      publisher.addItem(
          new SimpleSnippetCompletionItem(
              "html",
              "Snippet - HTML",
              new SnippetDescription(prefix.length(), HTML_SNIPPET, true)));
    }
    for (String tag : noCloseTags) {
      if (tag.startsWith(prefix) && prefix.length() > 0) {
        publisher.addItem(new SimpleCompletionItem(tag, "No Close Tag", prefix.length(), tag));
      }
    }

    for (String tag : htmlTags) {
      String tagOpen = "<" + tag;
      if (tagOpen.startsWith(prefix) && prefix.length() > 0) {
        publisher.addItem(new HtmlCompletionItem(tag, "Tag", prefix.length(), tag));
      }
    }
  }

  private boolean checkIsCompletionChar(char c) {
    return MyCharacter.isJavaIdentifierPart(c) || c == '<' || c == '/';
  }

  private static final String[] noCloseTags = {"<br>", "<hr>", "<img>", "<input>", "<link>", "<meta>"};

  private static final String[] htmlTags = {
    "html",
    "head",
    "title",
    "base",
    "style",
    "script",
    "noscript",
    "template",
    "body",
    "article",
    "section",
    "nav",
    "aside",
    "h1",
    "h2",
    "h3",
    "h4",
    "h5",
    "h6",
    "header",
    "footer",
    "address",
    "main",
    "p",
    "pre",
    "blockquote",
    "ol",
    "ul",
    "li",
    "dl",
    "dt",
    "dd",
    "figure",
    "figcaption",
    "div",
    "a",
    "em",
    "strong",
    "small",
    "s",
    "cite",
    "q",
    "dfn",
    "abbr",
    "data",
    "time",
    "code",
    "var",
    "samp",
    "kbd",
    "sub",
    "sup",
    "i",
    "b",
    "u",
    "mark",
    "ruby",
    "rt",
    "rp",
    "bdi",
    "bdo",
    "span",
    "wbr",
    "ins",
    "del",
    "picture",
    "source",
    "iframe",
    "embed",
    "object",
    "param",
    "video",
    "audio",
    "track",
    "map",
    "area",
    "table",
    "caption",
    "colgroup",
    "col",
    "tbody",
    "thead",
    "tfoot",
    "tr",
    "td",
    "th",
    "form",
    "fieldset",
    "legend",
    "label",
    "button",
    "select",
    "datalist",
    "optgroup",
    "option",
    "textarea",
    "output",
    "progress",
    "meter",
    "details",
    "summary",
    "menu",
    "menuitem",
    "dialog"
  };
}
