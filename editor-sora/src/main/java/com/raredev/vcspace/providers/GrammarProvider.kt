package com.raredev.vcspace.providers

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.raredev.vcspace.utils.FileUtil
import com.raredev.vcspace.models.GrammarModel
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.model.DefaultGrammarDefinition
import io.github.rosemoe.sora.langs.textmate.registry.model.GrammarDefinition
import org.eclipse.tm4e.core.registry.IGrammarSource
import java.nio.charset.Charset

/**
 * Class to register and provide TextMate grammars
 *
 * @author Felipe Teixeira
 */
object GrammarProvider {

  private var grammars: List<GrammarModel> = mutableListOf()

  fun initialize(context: Context) {
    if (grammars.isNotEmpty()) {
      return
    }

    val grammarsJson = FileUtil.readFromAsset(context, "editor/sora-editor/textmate/grammars.json")

    grammars = Gson().fromJson(grammarsJson, object: TypeToken<List<GrammarModel>>() {})

    // Create GrammarRegistry instance
    GrammarRegistry.getInstance()
  }

  fun findScopeByFileExtension(extension: String?): String? {
    val grammar = findGrammarByFileExtension(extension)
    if (grammar == null) {
      return null
    }
    if (!GrammarRegistry.getInstance().constainsGrammarByFileName(grammar.name)) {
      registerGrammar(grammar)
    }
    return grammar.scopeName
  }

  fun registerGrammarByFileExtension(extension: String?) {
    val grammar = findGrammarByFileExtension(extension)
    if (grammar == null) {
      return
    }

    registerGrammar(grammar)
  }

  fun registerGrammar(grammar: GrammarModel) {
    val grammarRegistry = GrammarRegistry.getInstance()

    if (!grammarRegistry.constainsGrammarByFileName(grammar.name)) {
      if (grammar.embeddedLanguages != null) {
        registerEmbeddedLanguagesGrammar(grammar)
      }

      val grammarSource = IGrammarSource.fromInputStream(
        FileProviderRegistry.getInstance().tryGetInputStream(grammar.grammar),
        grammar.grammar, Charset.defaultCharset()
      )

      grammarRegistry.loadGrammar(DefaultGrammarDefinition.withLanguageConfiguration(
        grammarSource,
        grammar.languageConfiguration,
        grammar.name,
        grammar.scopeName
      ).withEmbeddedLanguages(grammar.embeddedLanguages))
    }
  }

  fun registerEmbeddedLanguagesGrammar(grammar: GrammarModel) {
    val embeddedLanguages = grammar.embeddedLanguages
    if (embeddedLanguages == null) {
      return
    }

    for ((scopeName, _) in embeddedLanguages) {
      val embeddedGrammar = findGrammarByScope(scopeName)
      if (embeddedGrammar != null) {
        registerGrammar(embeddedGrammar)
      }
    }
  }

  fun findGrammarByFileExtension(extension: String?):
    GrammarModel? = grammars.find { it.fileExtensions.contains(extension) }

  fun findGrammarByScope(scopeName: String):
    GrammarModel? = grammars.find { it.scopeName == scopeName }
}