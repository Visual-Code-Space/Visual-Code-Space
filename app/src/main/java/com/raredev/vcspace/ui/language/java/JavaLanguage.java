package com.raredev.vcspace.ui.language.java;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.raredev.vcspace.util.PreferencesUtils;
import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.completion.SimpleSnippetCompletionItem;
import io.github.rosemoe.sora.lang.completion.SnippetDescription;
import io.github.rosemoe.sora.lang.completion.snippet.CodeSnippet;
import io.github.rosemoe.sora.lang.completion.snippet.parser.CodeSnippetParser;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;
import io.github.rosemoe.sora.widget.SymbolPairMatch;

public class JavaLanguage extends TextMateLanguage {

  private static final CodeSnippet FOR_SNIPPET =
      CodeSnippetParser.parse("for(int ${1:i} = 0;$1 < ${2:count};$1++) {\n    $0\n}");

  public JavaLanguage() {
    super(
        GrammarRegistry.getInstance().findGrammar("source.java"),
        GrammarRegistry.getInstance().findLanguageConfiguration("source.java"),
        null,
        ThemeRegistry.getInstance(),
        true);

    setCompleterKeywords(javaKeywords);
    loadSymbolPairs();
  }

  @Override
  public void requireAutoComplete(
      @NonNull ContentReference content,
      @NonNull CharPosition position,
      @NonNull CompletionPublisher publisher,
      @NonNull Bundle extraArguments) {
    super.requireAutoComplete(content, position, publisher, extraArguments);
    var prefix =
        CompletionHelper.computePrefix(content, position, MyCharacter::isJavaIdentifierPart);
    
    if ("fori".startsWith(prefix) && prefix.length() > 0) {
      publisher.addItem(
          new SimpleSnippetCompletionItem(
              "fori",
              "Snippet - For loop on index",
              new SnippetDescription(prefix.length(), FOR_SNIPPET, true)));
    }
  }

  @Override
  public boolean useTab() {
    return !PreferencesUtils.useUseSpaces();
  }

  private void loadSymbolPairs() {
    SymbolPairMatch symbolPairs = getSymbolPairs();
    symbolPairs.putPair("(", new SymbolPairMatch.SymbolPair("(", ")"));
    symbolPairs.putPair("{", new SymbolPairMatch.SymbolPair("{", "}"));
    symbolPairs.putPair("[", new SymbolPairMatch.SymbolPair("[", "]"));
    symbolPairs.putPair("\"", new SymbolPairMatch.SymbolPair("\"", "\""));
    symbolPairs.putPair("'", new SymbolPairMatch.SymbolPair("'", "'"));
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
