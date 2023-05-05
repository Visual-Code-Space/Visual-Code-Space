package com.raredev.vcspace.editor.language.java.completion;

import com.raredev.vcspace.editor.completion.CompletionParams;
import com.raredev.vcspace.editor.completion.VCSpaceCompletionItem;
import com.raredev.vcspace.editor.completion.CompletionProvider;
import java.util.ArrayList;
import java.util.List;

public class JavaCompletionProvider extends CompletionProvider {

  @Override
  public List<VCSpaceCompletionItem> getCompletions(CompletionParams params) {
    List<VCSpaceCompletionItem> completions = new ArrayList<>();
    
    return completions;
  }
  
  private static final String[] javaKeywords = {
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
