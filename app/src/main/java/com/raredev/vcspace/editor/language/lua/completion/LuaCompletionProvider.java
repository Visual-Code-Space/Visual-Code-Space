package com.raredev.vcspace.editor.language.lua.completion;

import com.raredev.vcspace.editor.completion.CompletionParams;
import com.raredev.vcspace.editor.completion.CompletionProvider;
import com.raredev.vcspace.editor.completion.SimpleCompletionIconDrawer;
import com.raredev.vcspace.editor.completion.SimpleCompletionItem;
import com.raredev.vcspace.editor.completion.SimpleCompletionItemKind;
import com.raredev.vcspace.editor.completion.VCSpaceCompletionItem;
import java.util.ArrayList;
import java.util.List;

public class LuaCompletionProvider extends CompletionProvider {

  @Override
  public List<VCSpaceCompletionItem> getCompletions(CompletionParams params) {
    String prefix = params.getPrefix();
    List<VCSpaceCompletionItem> completions = new ArrayList<>();
    for (String keyword : luaKeywords) {
      if (keyword.startsWith(prefix) && prefix.length() > 0) {
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

  public static final String[] luaKeywords = {
    "and",
    "break",
    "do",
    "else",
    "elseif",
    "end",
    "false",
    "for",
    "function",
    "goto",
    "if",
    "in",
    "local",
    "nil",
    "not",
    "or",
    "repeat",
    "return",
    "then",
    "true",
    "until",
    "while",
  };
}
