package com.raredev.vcspace.editor.language.java.completion;

import com.raredev.vcspace.editor.completion.CompletionParams;
import com.raredev.vcspace.editor.completion.SimpleCompletionIconDrawer;
import com.raredev.vcspace.editor.completion.SimpleCompletionItem;
import com.raredev.vcspace.editor.completion.SimpleCompletionItemKind;
import com.raredev.vcspace.editor.completion.VCSpaceCompletionItem;
import com.raredev.vcspace.editor.completion.CompletionProvider;
import java.util.ArrayList;
import java.util.List;

public class JavaCompletionProvider extends CompletionProvider {

  @Override
  public List<VCSpaceCompletionItem> getCompletions(CompletionParams params) {
    String prefix = params.getPrefix();
    List<VCSpaceCompletionItem> completions = new ArrayList<>();
    for (String keyword : javaKeywords) {
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

  public static final String[] javaKeywords = {
    "assert",
    "abstract",
    "boolean",
    "byte",
    "char",
    "class",
    "do",
    "double",
    "final",
    "float",
    "for",
    "if",
    "int",
    "long",
    "new",
    "public",
    "private",
    "protected",
    "package",
    "return",
    "static",
    "short",
    "super",
    "switch",
    "else",
    "volatile",
    "synchronized",
    "strictfp",
    "goto",
    "continue",
    "break",
    "transient",
    "void",
    "try",
    "catch",
    "finally",
    "while",
    "case",
    "default",
    "const",
    "enum",
    "extends",
    "implements",
    "import",
    "instanceof",
    "interface",
    "native",
    "this",
    "throw",
    "throws",
    "true",
    "false",
    "null",
    "var",
    "sealed",
    "permits"
  };
}
