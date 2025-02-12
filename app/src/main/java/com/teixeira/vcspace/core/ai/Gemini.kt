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

package com.teixeira.vcspace.core.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.teixeira.vcspace.app.BaseApplication
import com.teixeira.vcspace.core.Secrets
import com.teixeira.vcspace.resources.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Gemini {
  private val model = GenerativeModel(
    modelName = "gemini-2.0-flash-thinking-exp-01-21",
    apiKey = Secrets.getGenerativeAiApiKey(),
    generationConfig = generationConfig {
      temperature = 0.7f
      topK = 64
      topP = 0.95f
      maxOutputTokens = 65536
    },
    safetySettings = listOf(
      SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
      SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
      SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
      SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
    )
  )

  private suspend fun generateContent(prompt: String) = withContext(Dispatchers.IO) {
    runCatching {
      model.generateContent(
        content { text(prompt) }
      )
    }
  }

  suspend fun explainCode(code: String): Result<GenerateContentResponse> {
    return generateContent(BaseApplication.instance.getString(R.string.explain_code_msg, code))
  }

  suspend fun importComponents(code: String): Result<GenerateContentResponse> {
    if (!isJetpackComposeCode(code)) {
      return Result.failure(IllegalArgumentException("The provided code does not appear to be Jetpack Compose code."))
    }

    return generateContent(
      BaseApplication.instance.getString(
        R.string.import_compose_components_msg,
        code
      )
    )
  }

  private fun isJetpackComposeCode(code: String): Boolean {
    val composeKeywords = listOf(
      "@Composable", "Modifier", "Column", "Row", "Button", "Text", "Box",
      "LazyColumn", "LazyRow", "remember", "mutableStateOf"
    )

    return composeKeywords.any { keyword -> code.contains(keyword) }
  }

  suspend fun generateCode(
    prompt: String,
    fileExtension: String? = null
  ): Result<GenerateContentResponse> {
    return generateContent("Write the code on based on my prompt${if (!fileExtension.isNullOrEmpty()) " for file extension $fileExtension" else ""} and provide me only code:\nThe prompt:\n\n$prompt")
  }

  fun removeBackticksFromMarkdownCodeBlock(codeWithBackticks: String?): String {
    codeWithBackticks ?: return ""

    val trimmedCode = codeWithBackticks.trim()

    if (trimmedCode.startsWith("```") && trimmedCode.endsWith("```")) {
      val firstNewlineIndex = trimmedCode.indexOf("\n")
      return if (firstNewlineIndex > 3) {
        trimmedCode.substring(firstNewlineIndex + 1, trimmedCode.length - 3).trim()
      } else {
        trimmedCode.substring(3, trimmedCode.length - 3).trim()
      }
    }

    return codeWithBackticks
  }

  suspend fun completeCode(completionMetadata: CompletionMetadata): Result<GenerateContentResponse> {
    return generateContent(
      """
      Please complete the following ${completionMetadata.language} code:
      
      ${completionMetadata.textBeforeCursor}
      <cursor>
      ${completionMetadata.textAfterCursor}
      
      Use modern ${completionMetadata.language} practices and hooks where appropriate. Please provide only the completed part of the
      code without additional comments or explanations.
    """.trimIndent()
    )
  }
}
