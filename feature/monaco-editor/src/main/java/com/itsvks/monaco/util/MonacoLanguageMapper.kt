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

package com.itsvks.monaco.util

import com.itsvks.monaco.MonacoLanguage

object MonacoLanguageMapper {
  private val extensionToLanguageMap = mapOf(
    "txt" to MonacoLanguage.Plaintext,
    "abap" to MonacoLanguage.Abap,
    "apex" to MonacoLanguage.Apex,
    "azcli" to MonacoLanguage.Azcli,
    "bat" to MonacoLanguage.Bat,
    "bicep" to MonacoLanguage.Bicep,
    "mligo" to MonacoLanguage.Cameligo,
    "clj" to MonacoLanguage.Clojure,
    "coffee" to MonacoLanguage.Coffee,
    "cpp" to MonacoLanguage.Cpp,
    "c" to MonacoLanguage.Cpp,
    "cs" to MonacoLanguage.Csharp,
    "css" to MonacoLanguage.CSS,
    "cypher" to MonacoLanguage.Cypher,
    "dart" to MonacoLanguage.Dart,
    "dockerfile" to MonacoLanguage.Dockerfile,
    "ecl" to MonacoLanguage.Ecl,
    "ex" to MonacoLanguage.Elixir,
    "go" to MonacoLanguage.Go,
    "graphql" to MonacoLanguage.GraphQl,
    "html" to MonacoLanguage.HTML,
    "ini" to MonacoLanguage.Ini,
    "java" to MonacoLanguage.Java,
    "js" to MonacoLanguage.Javascript,
    "json" to MonacoLanguage.JSON,
    "jsx" to MonacoLanguage.Javascript,
    "kt" to MonacoLanguage.Kotlin,
    "less" to MonacoLanguage.Less,
    "lua" to MonacoLanguage.Lua,
    "md" to MonacoLanguage.Markdown,
    "php" to MonacoLanguage.Php,
    "py" to MonacoLanguage.Python,
    "r" to MonacoLanguage.R,
    "rb" to MonacoLanguage.Ruby,
    "rs" to MonacoLanguage.Rust,
    "scss" to MonacoLanguage.SCSS,
    "sh" to MonacoLanguage.Shell,
    "sql" to MonacoLanguage.SQL,
    "swift" to MonacoLanguage.Swift,
    "ts" to MonacoLanguage.TypeScript,
    "vb" to MonacoLanguage.VB,
    "xml" to MonacoLanguage.XML,
    "yaml" to MonacoLanguage.YAML,
    "yml" to MonacoLanguage.YAML
  )

  fun getLanguageByExtension(extension: String): MonacoLanguage {
    return extensionToLanguageMap[extension.lowercase()] ?: MonacoLanguage.Plaintext
  }
}