package io.github.rosemoe.sora.langs.textmate

import com.raredev.vcspace.utils.PreferencesUtils
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import org.eclipse.tm4e.core.grammar.IGrammar
import org.eclipse.tm4e.languageconfiguration.model.LanguageConfiguration

class VCSpaceTMLanguage(iGrammar: IGrammar, languageConfiguration: LanguageConfiguration?) :
    TextMateLanguage(iGrammar, languageConfiguration, GrammarRegistry.getInstance()) {

  init {
    tabSize = PreferencesUtils.tabSize
    useTab(PreferencesUtils.useTab)
  }

  companion object {

    fun create(scope: String): VCSpaceTMLanguage {
      val grammarRegistry = GrammarRegistry.getInstance()
      val iGrammar = grammarRegistry.findGrammar(scope)
        ?: throw IllegalArgumentException("Language with $scope scope name not found")

      return VCSpaceTMLanguage(iGrammar, grammarRegistry.findLanguageConfiguration(scope))
    }
  }
}
