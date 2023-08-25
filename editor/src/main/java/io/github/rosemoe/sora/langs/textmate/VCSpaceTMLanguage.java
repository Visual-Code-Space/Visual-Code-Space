package io.github.rosemoe.sora.langs.textmate;

import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.PathUtils;
import com.raredev.vcspace.editor.completion.SimpleCompletionIconDrawer;
import com.raredev.vcspace.editor.completion.SimpleCompletionItemKind;
import com.raredev.vcspace.editor.completion.SimpleSnippetCompletionItem;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.util.ILogger;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.ToastUtils;
import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.completion.SnippetDescription;
import io.github.rosemoe.sora.lang.completion.snippet.parser.CodeSnippetParser;
import io.github.rosemoe.sora.lang.format.Formatter;
import io.github.rosemoe.sora.langs.textmate.provider.LanguageScopeProvider;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.text.TextRange;
import io.github.rosemoe.sora.util.MyCharacter;
import io.github.rosemoe.sora.widget.SymbolPairMatch;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.tm4e.core.grammar.IGrammar;
import org.eclipse.tm4e.languageconfiguration.model.AutoClosingPairConditional;
import org.eclipse.tm4e.languageconfiguration.model.LanguageConfiguration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VCSpaceTMLanguage extends TextMateLanguage {

  public static final String SNIPPETS_FOLDER_PATH =
      PathUtils.getExternalAppDataPath() + "/files/snippets/";

  protected final String languageScope;
  protected TMFormatter formatter;

  private List<UserSnippetCompletionItem> languageSnippets;
  private SymbolPairMatch symbolPair;

  protected VCSpaceTMLanguage(
      IGrammar grammar,
      LanguageConfiguration languageConfiguration,
      ThemeRegistry themeRegistry,
      String languageScope) {
    super(grammar, languageConfiguration, null, themeRegistry, false);
    this.languageScope = languageScope;

    languageSnippets = new ArrayList<>();
    addSymbolPairs();
    TaskExecutor.executeAsync(
        () -> {
          readLanguageSnippets();
          return null;
        },
        (result) -> {});
  }

  public static VCSpaceTMLanguage create(String languageScopeName) {
    final GrammarRegistry grammarRegistry = GrammarRegistry.getInstance();
    var grammar = grammarRegistry.findGrammar(languageScopeName);

    if (grammar == null) {
      throw new IllegalArgumentException(
          String.format("Language with %s scope name not found", grammarRegistry));
    }

    var languageConfiguration = grammarRegistry.findLanguageConfiguration(grammar.getScopeName());

    return new VCSpaceTMLanguage(
        grammar, languageConfiguration, ThemeRegistry.getInstance(), languageScopeName);
  }

  @Override
  public void requireAutoComplete(
      @NonNull ContentReference content,
      @NonNull CharPosition position,
      @NonNull CompletionPublisher publisher,
      @NonNull Bundle extraArguments) {
    var prefix =
        CompletionHelper.computePrefix(content, position, MyCharacter::isJavaIdentifierPart);
    for (var snippet : languageSnippets) {
      if (snippet.prefix.startsWith(prefix) && prefix.length() > 0) {
        publisher.addItem(
            new SimpleSnippetCompletionItem(
                snippet.label, /* Label */
                snippet.desc, /* Desc */
                "Snippet", /* Type */
                SimpleCompletionIconDrawer.draw(SimpleCompletionItemKind.SNIPPET),
                new SnippetDescription(
                    prefix.length(), CodeSnippetParser.parse(snippet.body), true)));
      }
    }
  }

  @NonNull
  @Override
  public Formatter getFormatter() {
    if (formatter == null) {
      formatter = new TMFormatter(this);
    }
    return formatter;
  }

  @Override
  public int getTabSize() {
    return PreferencesUtils.getEditorTABSize();
  }

  @Override
  public boolean useTab() {
    return !PreferencesUtils.useSpaces();
  }

  @Override
  public SymbolPairMatch getSymbolPairs() {
    return symbolPair;
  }

  public String formatCode(Content text, TextRange range) {
    return text.toString();
  }

  public LanguageConfiguration getLanguageConfiguration() {
    return this.languageConfiguration;
  }

  public void editorCommitText(CharSequence text) {}

  public boolean checkIsCompletionChar(char c) {
    return MyCharacter.isJavaIdentifierPart(c);
  }

  private void addSymbolPairs() {
    symbolPair = new SymbolPairMatch();
    if (languageConfiguration == null) {
      return;
    }

    List<AutoClosingPairConditional> autoClosingPairs = languageConfiguration.getAutoClosingPairs();
    if (autoClosingPairs == null) {
      return;
    }
    for (AutoClosingPairConditional autoClosingPair : autoClosingPairs) {
      symbolPair.putPair(
          autoClosingPair.open,
          new SymbolPairMatch.SymbolPair(
              autoClosingPair.open,
              autoClosingPair.close,
              new TextMateSymbolPairMatch.SymbolPairEx(autoClosingPair)));
    }
  }

  private void readLanguageSnippets() {
    String fileExtension = LanguageScopeProvider.getFileExtensionByScope(languageScope);
    File snippetsFile = new File(SNIPPETS_FOLDER_PATH + fileExtension + ".json");
    if (snippetsFile.exists() && snippetsFile.isFile()) {
      String json = FileIOUtils.readFile2String(snippetsFile);
      try {
        JSONObject obj = new JSONObject(json);

        var iterator = obj.keys();
        while (iterator.hasNext()) {
          String label = iterator.next();

          JSONObject snippetObject = obj.getJSONObject(label);
          JSONArray bodyArray = snippetObject.getJSONArray("body");

          String prefix = snippetObject.getString("prefix");
          String desc = snippetObject.getString("description");

          if (TextUtils.isEmpty(prefix) || TextUtils.isEmpty(desc) || bodyArray == null) {
            return;
          }

          String body = "";
          for (int i = 0; i < bodyArray.length(); i++) {
            String line = bodyArray.getString(i);
            if (line != null) {
              body += line;
              if (i < bodyArray.length() - 1) {
                body += "\n";
              }
            }
          }

          if (TextUtils.isEmpty(body)) return;

          languageSnippets.add(new UserSnippetCompletionItem(label, prefix, desc, body));
        }
      } catch (JSONException jsone) {
        ILogger.error("VCSpaceTMLanguage", jsone);
        jsone.printStackTrace();
      }
    }
  }

  private class UserSnippetCompletionItem {
    public String label, prefix, desc, body;

    public UserSnippetCompletionItem(String label, String prefix, String desc, String body) {
      this.label = label;
      this.prefix = prefix;
      this.desc = desc;
      this.body = body;
    }
  }
}
