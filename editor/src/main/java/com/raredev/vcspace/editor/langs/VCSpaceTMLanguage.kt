/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.raredev.vcspace.editor.langs

import com.raredev.vcspace.utils.PreferencesUtils
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import org.eclipse.tm4e.core.grammar.IGrammar
import org.eclipse.tm4e.languageconfiguration.model.LanguageConfiguration

class VCSpaceTMLanguage private constructor(iGrammar: IGrammar, languageConfiguration: LanguageConfiguration?) :
  TextMateLanguage(iGrammar, languageConfiguration) {

  override fun getTabSize(): Int {
    return PreferencesUtils.tabSize
  }

  override fun useTab(): Boolean {
    return PreferencesUtils.useTab
  }

  companion object {

    fun create(scopeName: String): VCSpaceTMLanguage {
      val grammarRegistry = GrammarRegistry.getInstance()
      val iGrammar =
        grammarRegistry.findGrammar(scopeName)
          ?: throw IllegalArgumentException("Language with scopeName scope name not found")

      return VCSpaceTMLanguage(iGrammar, grammarRegistry.findLanguageConfiguration(scopeName))
    }
  }
}
