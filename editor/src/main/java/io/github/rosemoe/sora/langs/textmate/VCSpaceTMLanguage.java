package io.github.rosemoe.sora.langs.textmate;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.PathUtils;
import com.raredev.vcspace.editor.completion.SimpleCompletionIconDrawer;
import com.raredev.vcspace.editor.completion.SimpleCompletionItemKind;
import com.raredev.vcspace.editor.completion.SimpleSnippetCompletionItem;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.utils.Logger;
import com.raredev.vcspace.utils.PreferencesUtils;
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

  private final Logger logger = Logger.newInstance("VCSpaceTMLanguage");
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

    if (prefix.length() <= 0) {
      return;
    }
    for (UserSnippetCompletionItem snippet : languageSnippets) {
      for (String snippetPrefix : snippet.prefix) {
        if (snippetPrefix.startsWith(prefix)) {
          publisher.addItem(
              new SimpleSnippetCompletionItem(
                  snippetPrefix, /* Label */
                  snippet.desc, /* Desc */
                  "Snippet", /* Type */
                  SimpleCompletionIconDrawer.draw(SimpleCompletionItemKind.SNIPPET),
                  new SnippetDescription(
                      prefix.length(), CodeSnippetParser.parse(snippet.body), true)));
        }
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

    if (!snippetsFile.exists() || !snippetsFile.isFile()) {
      return;
    }

    String json = FileIOUtils.readFile2String(snippetsFile);
    try {
      JSONObject obj = new JSONObject(json);
      var iterator = obj.keys();
      while (iterator.hasNext()) {
        String label = iterator.next();
        processSnippet(label, obj.getJSONObject(label));
      }
    } catch (JSONException jsone) {
      jsone.printStackTrace();
      logger.e(jsone);
    }
  }

  private void processSnippet(String label, JSONObject snippetObject) throws JSONException {
    Object bodyObj = snippetObject.opt("body");
    Object prefixObj = snippetObject.opt("prefix");
    Object descObj = snippetObject.opt("description");

    if (prefixObj == null || descObj == null || bodyObj == null) {
      return;
    }

    if (!(bodyObj instanceof JSONArray)
        || !(prefixObj instanceof JSONArray || prefixObj instanceof String)
        || !(descObj instanceof String)) {
      return;
    }

    var bodyArray = (JSONArray) bodyObj;
    var desc = (String) descObj;

    if (desc.isEmpty()) {
      desc = label;
    }

    String[] prefix = getPrefixArray(prefixObj);
    if (prefix == null) {
      return;
    }

    String body = getBodyString(bodyArray);

    if (body.isEmpty()) {
      return;
    }

    var userSnippet = new UserSnippetCompletionItem(label, prefix, desc, body);
    languageSnippets.add(userSnippet);

    logger.d("Snippet: " + userSnippet.toString() + ". added!");
  }

  private String[] getPrefixArray(Object prefixObj) {
    if (prefixObj instanceof JSONArray) {
      JSONArray prefixArray = (JSONArray) prefixObj;
      String[] prefix = new String[prefixArray.length()];
      for (int i = 0; i < prefixArray.length(); i++) {
        Object value = prefixArray.opt(i);
        if (value instanceof String) {
          prefix[i] = (String) value;
        }
      }
      return prefix;
    } else if (prefixObj instanceof String) {
      return new String[] {(String) prefixObj};
    }
    return null;
  }

  private String getBodyString(JSONArray bodyArray) {
    StringBuilder body = new StringBuilder();
    for (int i = 0; i < bodyArray.length(); i++) {
      Object value = bodyArray.opt(i);
      if (value instanceof String) {
        body.append(value);
        if (i < bodyArray.length() - 1) {
          body.append("\n");
        }
      }
    }
    return body.toString();
  }

  private class UserSnippetCompletionItem {
    public String label, desc, body;
    public String[] prefix;

    public UserSnippetCompletionItem(String label, String[] prefix, String desc, String body) {
      this.label = label;
      this.prefix = prefix;
      this.desc = desc;
      this.body = body;
    }

    @Override
    public String toString() {
      return "UserSnippetCompletionItem[label="
          + label
          + ", desc="
          + desc
          + ", body="
          + body
          + ", prefix="
          + prefix
          + "]";
    }
  }
}
