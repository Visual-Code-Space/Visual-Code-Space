package com.raredev.vcspace.editor.language.kotlin.completion;

import com.raredev.vcspace.editor.completion.CompletionParams;
import com.raredev.vcspace.editor.completion.CompletionProvider;
import com.raredev.vcspace.editor.completion.SimpleCompletionIconDrawer;
import com.raredev.vcspace.editor.completion.SimpleCompletionItem;
import com.raredev.vcspace.editor.completion.SimpleCompletionItemKind;
import com.raredev.vcspace.editor.completion.VCSpaceCompletionItem;
import java.util.ArrayList;
import java.util.List;

public class KotlinCompletionProvider extends CompletionProvider {

  @Override
  public List<VCSpaceCompletionItem> getCompletions(CompletionParams params) {
    String prefix = params.getPrefix();
    List<VCSpaceCompletionItem> completions = new ArrayList<>();
    for (String keyword : kotlinKeywords) {
      if (keyword.startsWith(prefix) && completions.size() <= 20) {
        completions.add(
            new SimpleCompletionItem(
                keyword,
                null,
                "Keyword",
                SimpleCompletionIconDrawer.draw(SimpleCompletionItemKind.KEYWORD),
                prefix.length(),
                keyword));
      }
    }
    return completions;
  }

  private static final String[] kotlinKeywords = {
    "as",
    "as?",
    "break",
    "class",
    "continue",
    "do",
    "else",
    "false",
    "for",
    "fun",
    "if",
    "in",
    "interface",
    "is",
    "null",
    "object",
    "package",
    "return",
    "super",
    "this",
    "throw",
    "true",
    "try",
    "typealias",
    "val",
    "var",
    "when",
    "while"
  };
}
