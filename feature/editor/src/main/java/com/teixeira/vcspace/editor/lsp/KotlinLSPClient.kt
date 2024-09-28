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

package com.teixeira.vcspace.editor.lsp

import org.eclipse.lsp4j.MessageActionItem
import org.eclipse.lsp4j.MessageParams
import org.eclipse.lsp4j.PublishDiagnosticsParams
import org.eclipse.lsp4j.ShowMessageRequestParams
import org.eclipse.lsp4j.services.LanguageClient
import java.util.concurrent.CompletableFuture

class KotlinLSPClient : LanguageClient {
  override fun telemetryEvent(obj: Any?) {
    println("Telemetry event received: $obj")
  }

  override fun publishDiagnostics(diagnostics: PublishDiagnosticsParams?) {
    diagnostics?.diagnostics?.forEach { diagnostic ->
      println("Diagnostic: ${diagnostic.message} at ${diagnostic.range.start.line}:${diagnostic.range.start.character}")
    }
  }

  override fun showMessage(messageParams: MessageParams?) {
    messageParams?.let {
      println("Message from server: ${it.message}")
    }
  }

  override fun showMessageRequest(requestParams: ShowMessageRequestParams?): CompletableFuture<MessageActionItem> {
    println("Message request from server: ${requestParams?.message}")

    val actionItem = MessageActionItem("DefaultAction")
    return CompletableFuture.completedFuture(actionItem)
  }

  override fun logMessage(message: MessageParams?) {
    message?.let {
      println("Log message from server: ${it.message}")
    }
  }
}