package com.raredev.vcspace.ui.language.java;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.raredev.vcspace.extension.ExtensionAPI;
import com.raredev.vcspace.extension.ExtensionAPIImpl;
import com.raredev.vcspace.extension.completion.CodeCompletionProvider;
import com.raredev.vcspace.ui.editor.CodeEditorView;
import com.raredev.vcspace.ui.editor.formatter.VCSpaceFormatter;
import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.completion.SimpleCompletionItem;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;
import io.github.rosemoe.sora.widget.SymbolPairMatch;
import java.util.List;

public class JavaLanguage extends VCSpaceTMLanguage {

  public JavaLanguage(CodeEditorView editor) {
    super(
        GrammarRegistry.getInstance().findGrammar("source.java"),
        GrammarRegistry.getInstance().findLanguageConfiguration("source.java"),
        ThemeRegistry.getInstance(),
        false);

    setCompleterKeywords(javaKeywords);
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

    ExtensionAPI extensionAPI = ExtensionAPIImpl.getInstance();

    CodeCompletionProvider codeCompletionProvider =
        (CodeCompletionProvider) extensionAPI.getExtension("javaCodeCompletion");
    List<String> codeCompletions = codeCompletionProvider.getCodeCompletions("System.");

    for (String completion : codeCompletions) {
      if (completion.startsWith(prefix) && prefix.length() > 0) {
        publisher.addItem(
            new SimpleCompletionItem(completion, "ExtensionAPI Test", prefix.length(), completion));
      }
    }
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
