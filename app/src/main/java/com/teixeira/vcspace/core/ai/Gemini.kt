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

object Gemini {
  private val model = GenerativeModel(
    modelName = "gemini-1.5-flash-latest",
    apiKey = Secrets.getGenerativeAiApiKey(),
    generationConfig = generationConfig {
      temperature = 0.15f
      topK = 32
      topP = 1f
      maxOutputTokens = 4096
    },
    safetySettings = listOf(
      SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
      SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
      SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
      SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
    )
  )

  suspend fun explainCode(code: String): GenerateContentResponse {
    return model.generateContent(
      content {
        text(
          BaseApplication.instance.getString(R.string.explain_code_msg, code)
        )
      }
    )
  }

  suspend fun importComponents(code: String): GenerateContentResponse {
    if (!isJetpackComposeCode(code)) {
      throw IllegalArgumentException("The provided code does not appear to be Jetpack Compose code.")
    }

    return model.generateContent(
      content {
        text(
          BaseApplication.instance.getString(R.string.import_compose_components_msg, code)
        )
      }
    )
  }

  private fun isJetpackComposeCode(code: String): Boolean {
    val composeKeywords = listOf(
      "@Composable", "Modifier", "Column", "Row", "Button", "Text", "Box",
      "LazyColumn", "LazyRow", "remember", "mutableStateOf"
    )

    return composeKeywords.any { keyword -> code.contains(keyword) }
  }
}