package io.github.rosemoe.sora.langs.textmate;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.raredev.vcspace.editor.completion.SimpleCompletionIconDrawer;
import com.raredev.vcspace.editor.completion.SimpleCompletionItemKind;
import com.raredev.vcspace.editor.completion.SimpleSnippetCompletionItem;
import com.raredev.vcspace.plugin.Plugin;
import com.raredev.vcspace.plugin.PluginsLoader;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.ILogger;
import com.raredev.vcspace.util.PreferencesUtils;
import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.completion.SnippetDescription;
import io.github.rosemoe.sora.lang.completion.snippet.parser.CodeSnippetParser;
import io.github.rosemoe.sora.lang.format.Formatter;
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
import java.util.Map;
import org.eclipse.tm4e.core.grammar.IGrammar;
import org.eclipse.tm4e.languageconfiguration.model.AutoClosingPairConditional;
import org.eclipse.tm4e.languageconfiguration.model.LanguageConfiguration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VCSpaceTMLanguage extends TextMateLanguage {

  protected TMFormatter formatter;
  protected final String languageScope;

  private List<SimplePluginCompletionItem> pluginsCompletion;

  protected VCSpaceTMLanguage(
      IGrammar grammar,
      LanguageConfiguration languageConfiguration,
      ThemeRegistry themeRegistry,
      String languageScope) {
    super(grammar, languageConfiguration, null, themeRegistry, false);
    this.languageScope = languageScope;

    pluginsCompletion = new ArrayList<>();
    readCompletionsPlugin();
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

    for (var pluginSnippet : pluginsCompletion) {
      if (pluginSnippet.label.startsWith(prefix) && prefix.length() > 0) {
        publisher.addItem(
            new SimpleSnippetCompletionItem(
                pluginSnippet.label, /* Label */
                pluginSnippet.desc, /* Desc */
                pluginSnippet.type, /* Type */
                SimpleCompletionIconDrawer.draw(SimpleCompletionItemKind.SNIPPET),
                new SnippetDescription(
                    prefix.length(), CodeSnippetParser.parse(pluginSnippet.snippet), true)));
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
    SymbolPairMatch symbolPair = new SymbolPairMatch();
    if (languageConfiguration == null) {
      return symbolPair;
    }

    List<AutoClosingPairConditional> autoClosingPairs = languageConfiguration.getAutoClosingPairs();
    if (autoClosingPairs == null) {
      return symbolPair;
    }
    for (AutoClosingPairConditional autoClosingPair : autoClosingPairs) {
      symbolPair.putPair(
          autoClosingPair.open,
          new SymbolPairMatch.SymbolPair(
              autoClosingPair.open,
              autoClosingPair.close,
              new TextMateSymbolPairMatch.SymbolPairEx(autoClosingPair)));
    }
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

  // Load Snippets from plugins
  public void readCompletionsPlugin() {
    var plugins = PluginsLoader.plugins;

    for (Map.Entry<String, Plugin> entry : plugins.entrySet()) {
      var plugin = entry.getValue();

      if (plugin.completion != null && plugin.completion.getLanguageScope().equals(languageScope)) {
        for (String completionPath : plugin.completion.getCompletionFiles()) {
          var completionFile = new File(entry.getKey() + "/" + completionPath);
          readCompletionsPluginHandler(completionFile);
        }
      }
    }
  }

  public void readCompletionsPluginHandler(File completionFile) {
    if (completionFile.exists()) {

      String json = FileUtil.readFile(completionFile);
      try {
        JSONObject obj = new JSONObject(json);

        var iterator = obj.keys();
        while (iterator.hasNext()) {
          String key = iterator.next();

          JSONObject completion = obj.getJSONObject(key);
          JSONArray body = completion.getJSONArray("body");

          String bodyCode = "";

          if (body != null) {
            for (int i = 0; i < body.length(); i++) {
              String line = body.getString(i);
              if (line != null) {
                bodyCode += line;
                if (i < body.length() - 1) {
                  bodyCode += "\n";
                }
              }
            }
          }

          pluginsCompletion.add(
              new SimplePluginCompletionItem(
                  key, /* Label */
                  completion.getString("desc"), /* Desc */
                  completion.getString("type"), /* Type */
                  bodyCode /* bodyCode */));
        }
      } catch (JSONException jsone) {
        ILogger.error("VCSpaceTMLanguage", jsone);
        jsone.printStackTrace();
      }
    }
  }

  private class SimplePluginCompletionItem {
    public String label;
    public String desc;
    public String type;
    public String snippet;

    public SimplePluginCompletionItem(String label, String desc, String type, String snippet) {
      this.label = label;
      this.desc = desc;
      this.type = type;
      this.snippet = snippet;
    }
  }
}
