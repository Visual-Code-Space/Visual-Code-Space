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
package com.teixeira.vcspace.editor.language.textmate

import android.os.Bundle
import androidx.annotation.WorkerThread
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager
import io.github.rosemoe.sora.lang.completion.CompletionHelper
import io.github.rosemoe.sora.lang.completion.CompletionPublisher
import io.github.rosemoe.sora.lang.completion.IdentifierAutoComplete
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.DefaultGrammarDefinition
import io.github.rosemoe.sora.langs.textmate.registry.model.GrammarDefinition
import io.github.rosemoe.sora.langs.textmate.utils.StringUtils
import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.text.ContentReference
import io.github.rosemoe.sora.util.MyCharacter
import kotlinx.coroutines.runBlocking
import org.eclipse.tm4e.core.grammar.IGrammar
import org.eclipse.tm4e.core.registry.IGrammarSource
import org.eclipse.tm4e.core.registry.IThemeSource
import org.eclipse.tm4e.languageconfiguration.internal.model.LanguageConfiguration
import java.io.Reader

class VCSpaceTMLanguage protected constructor(
    val grammar: IGrammar?,
    @JvmField
    var languageConfiguration: LanguageConfiguration?,
    var grammarRegistry: GrammarRegistry,
    var themeRegistry: ThemeRegistry,
    @JvmField val createIdentifiers: Boolean
) : EmptyLanguage() {
    /**
     * Set tab size. The tab size is used to compute code blocks.
     */
    @JvmField
    var tabSize: Int = 4

    private var useTab = false

    val autoCompleter: IdentifierAutoComplete = IdentifierAutoComplete()
    var isAutoCompleteEnabled: Boolean = true

    var textMateAnalyzer: VCSpaceTMAnalyzer? = null

    private lateinit var newlineHandlers: Array<VCSpaceTMNewlineHandler>

    // this.grammar = grammar;
    var symbolPairMatch: VCSpaceTMSymbolPairMatch = VCSpaceTMSymbolPairMatch(this)

    var newlineHandler: VCSpaceTMNewlineHandler? = null
        private set

    init {
        createAnalyzerAndNewlineHandler(grammar, languageConfiguration)
    }


    /**
     * When you update the [TextMateColorScheme] for editor, you need to synchronize the updates here
     *
     * @param theme IThemeSource creates from file
     */
    @WorkerThread
    @Deprecated(
        "Use {@link ThemeRegistry#setTheme(String)}",
        ReplaceWith("themeRegistry.loadTheme(theme)")
    )
    @Throws(
        Exception::class
    )
    fun updateTheme(theme: IThemeSource?) {
        //if (textMateAnalyzer != null) {
        //  textMateAnalyzer.updateTheme(theme);
        //}
        themeRegistry.loadTheme(theme)
    }


    private fun createAnalyzerAndNewlineHandler(
        grammar: IGrammar?,
        languageConfiguration: LanguageConfiguration?
    ) {
        val old = textMateAnalyzer
        if (old != null) {
            old.receiver = null
            old.destroy()
        }
        try {
            textMateAnalyzer =
                VCSpaceTMAnalyzer(
                    this,
                    grammar,
                    languageConfiguration,  /*grammarRegistry,*/
                    themeRegistry
                )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        this.languageConfiguration = languageConfiguration
        newlineHandler = VCSpaceTMNewlineHandler(this)
        newlineHandlers = arrayOf(newlineHandler!!)
        if (languageConfiguration != null) {
            // because the editor will only get the symbol pair matcher once
            // (caching object to stop repeated new object created),
            // the symbol pair needs to be updated inside the symbol pair matcher.
            symbolPairMatch.updatePair()
        }
    }

    fun updateLanguage(scopeName: String?) {
        val grammar = grammarRegistry.findGrammar(scopeName)
        val languageConfiguration = grammarRegistry.findLanguageConfiguration(
            grammar!!.scopeName
        )
        createAnalyzerAndNewlineHandler(grammar, languageConfiguration)
    }

    fun updateLanguage(grammarDefinition: GrammarDefinition?) {
        val grammar = grammarRegistry.loadGrammar(grammarDefinition)

        val languageConfiguration = grammarRegistry.findLanguageConfiguration(grammar.scopeName)

        createAnalyzerAndNewlineHandler(grammar, languageConfiguration)
    }

    override fun getAnalyzeManager(): AnalyzeManager {
        if (textMateAnalyzer == null) {
            return EmptyAnalyzeManager.INSTANCE
        }
        return textMateAnalyzer as VCSpaceTMAnalyzer
    }


    override fun useTab(): Boolean {
        return useTab
    }

    fun useTab(useTab: Boolean) {
        this.useTab = useTab
    }

    override fun getSymbolPairs(): VCSpaceTMSymbolPairMatch {
        return symbolPairMatch
    }

    override fun getNewlineHandlers(): Array<VCSpaceTMNewlineHandler> {
        return newlineHandlers
    }

    override fun requireAutoComplete(
        content: ContentReference,
        position: CharPosition,
        publisher: CompletionPublisher,
        extraArguments: Bundle
    ) {
        if (!isAutoCompleteEnabled) {
            return
        }
        val prefix =
            CompletionHelper.computePrefix(content, position, MyCharacter::isJavaIdentifierPart)

        val ref = content.reference
        val cursor = ref.cursor

        if (prefix.isNotEmpty()) {
            runBlocking {
                val idt = textMateAnalyzer!!.syncIdentifiers
                autoCompleter.requireAutoComplete(content, position, prefix, publisher, idt)

                grammar?.name?.let { grammarName ->
                    println(grammarName)
//          Gemini.completeCode(
//            CompletionMetadata(
//              language = grammarName,
//              textBeforeCursor = ref.subContent(
//                1,
//                1,
//                cursor.leftLine,
//                cursor.leftColumn
//              ).toString(),
//              textAfterCursor = ref.subContent(
//                cursor.leftLine,
//                cursor.leftColumn,
//                ref.lineCount - 1,
//                ref.getColumnCount(ref.lineCount - 1)
//              ).toString()
//            )
//          ).onSuccess {
//            val response = Gemini.removeBackticksFromMarkdownCodeBlock(
//              it.candidates[0].content.parts[0].asTextOrNull()
//            )
//            println(response)
//            withContext(Dispatchers.Main.immediate) {
//              publisher.addItem(
//                AICompletionItem(
//                  label = "$prefix${response.trim()}",
//                  desc = "AI Generated",
//                  commitText = response
//                )
//              )
//            }
//          }.onFailure(Throwable::printStackTrace)
                }
            }
        }
    }

    fun setCompleterKeywords(keywords: Array<String?>?) {
        autoCompleter.setKeywords(keywords, false)
    }

    companion object {
        @Deprecated("")
        fun prepareLoad(
            grammarSource: IGrammarSource,
            languageConfiguration: Reader?,
            themeSource: IThemeSource?
        ): IGrammar {
            val definition = DefaultGrammarDefinition.withGrammarSource(
                grammarSource,
                StringUtils.getFileNameWithoutExtension(grammarSource.filePath),
                null
            )
            val languageRegistry = GrammarRegistry.getInstance()
            val grammar = languageRegistry.loadGrammar(definition)
            if (languageConfiguration != null) {
                languageRegistry.languageConfigurationToGrammar(
                    LanguageConfiguration.load(
                        languageConfiguration
                    ), grammar
                )
            }
            val themeRegistry = ThemeRegistry.getInstance()
            try {
                themeRegistry.loadTheme(themeSource)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return grammar
        }

        @Deprecated("")
        fun create(
            grammarSource: IGrammarSource,
            languageConfiguration: Reader?,
            themeSource: IThemeSource?
        ): VCSpaceTMLanguage {
            val grammar = prepareLoad(grammarSource, languageConfiguration, themeSource)
            return create(grammar.scopeName, true)
        }

        @Deprecated("")
        fun create(grammarSource: IGrammarSource, themeSource: IThemeSource?): VCSpaceTMLanguage {
            val grammar = prepareLoad(grammarSource, null, themeSource)
            return create(grammar.scopeName, true)
        }

        @Deprecated("")
        fun createNoCompletion(
            grammarSource: IGrammarSource,
            languageConfiguration: Reader?,
            themeSource: IThemeSource?
        ): VCSpaceTMLanguage {
            val grammar = prepareLoad(grammarSource, languageConfiguration, themeSource)
            return create(grammar.scopeName, false)
        }

        @Deprecated("")
        fun createNoCompletion(
            grammarSource: IGrammarSource,
            themeSource: IThemeSource?
        ): VCSpaceTMLanguage {
            val grammar = prepareLoad(grammarSource, null, themeSource)
            return create(grammar.scopeName, false)
        }

        fun create(languageScopeName: String?, autoCompleteEnabled: Boolean): VCSpaceTMLanguage {
            return create(languageScopeName, GrammarRegistry.getInstance(), autoCompleteEnabled)
        }

        fun create(
            languageScopeName: String?,
            grammarRegistry: GrammarRegistry,
            autoCompleteEnabled: Boolean
        ): VCSpaceTMLanguage {
            return create(
                languageScopeName,
                grammarRegistry,
                ThemeRegistry.getInstance(),
                autoCompleteEnabled
            )
        }

        fun create(
            languageScopeName: String?,
            grammarRegistry: GrammarRegistry,
            themeRegistry: ThemeRegistry,
            autoCompleteEnabled: Boolean
        ): VCSpaceTMLanguage {
            val grammar = grammarRegistry.findGrammar(languageScopeName)

            requireNotNull(grammar) {
                String.format(
                    "Language with %s scope name not found",
                    grammarRegistry
                )
            }

            val languageConfiguration = grammarRegistry.findLanguageConfiguration(grammar.scopeName)


            return VCSpaceTMLanguage(
                grammar,
                languageConfiguration,
                grammarRegistry,
                themeRegistry,
                autoCompleteEnabled
            )
        }


        fun create(
            grammarDefinition: GrammarDefinition?,
            autoCompleteEnabled: Boolean
        ): VCSpaceTMLanguage {
            return create(grammarDefinition, GrammarRegistry.getInstance(), autoCompleteEnabled)
        }

        fun create(
            grammarDefinition: GrammarDefinition?,
            grammarRegistry: GrammarRegistry,
            autoCompleteEnabled: Boolean
        ): VCSpaceTMLanguage {
            return create(
                grammarDefinition,
                grammarRegistry,
                ThemeRegistry.getInstance(),
                autoCompleteEnabled
            )
        }

        fun create(
            grammarDefinition: GrammarDefinition?,
            grammarRegistry: GrammarRegistry,
            themeRegistry: ThemeRegistry,
            autoCompleteEnabled: Boolean
        ): VCSpaceTMLanguage {
            val grammar = grammarRegistry.loadGrammar(grammarDefinition)

            requireNotNull(grammar) {
                String.format(
                    "Language with %s scope name not found",
                    grammarRegistry
                )
            }

            val languageConfiguration = grammarRegistry.findLanguageConfiguration(grammar.scopeName)

            return VCSpaceTMLanguage(
                grammar,
                languageConfiguration,
                grammarRegistry,
                themeRegistry,
                autoCompleteEnabled
            )
        }
    }
}
