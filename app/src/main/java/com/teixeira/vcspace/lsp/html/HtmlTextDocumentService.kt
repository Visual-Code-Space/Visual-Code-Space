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

package com.teixeira.vcspace.lsp.html

import org.eclipse.lsp4j.CompletionItem
import org.eclipse.lsp4j.CompletionItemKind
import org.eclipse.lsp4j.CompletionList
import org.eclipse.lsp4j.CompletionParams
import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.DidSaveTextDocumentParams
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.TextDocumentService
import java.util.concurrent.CompletableFuture

class HtmlTextDocumentService : TextDocumentService {
    val documents = mutableMapOf<String, String>()

    companion object {
        private val HTML_TAGS = arrayOf(
            "html", "head", "title", "body", "h1", "h2", "h3", "h4", "h5", "h6",
            "p", "br", "hr", "div", "span", "a", "img", "table", "tr", "td",
            "th", "ul", "ol", "li", "form", "input", "button", "select", "option",
            "label", "textarea", "script", "style", "link", "meta", "header",
            "footer", "nav", "section", "article", "aside", "main", "canvas"
        )
    }

    override fun completion(position: CompletionParams): CompletableFuture<Either<MutableList<CompletionItem>, CompletionList>> {
        println("completion: $position")

        val content = documents[position.textDocument.uri]!!
        var lineStart = 0
        var lineCount = 0
        var pos = 0

        // Find the start of the current line
        while (lineCount < position.position.line && pos < content.length) {
            if (content[pos] == '\n') {
                lineStart = pos + 1
                lineCount++
            }
            pos++
        }

        // Get text up to the cursor
        val cursorPos = lineStart + position.position.character
        var linePrefix = ""
        if (cursorPos <= content.length) {
            linePrefix = content.substring(lineStart, cursorPos)
        }

        val completions = mutableListOf<CompletionItem>()

        // HTML tag completion after <
        if (linePrefix.lastIndexOf("<") > linePrefix.lastIndexOf(">")) {
            for (tag in HTML_TAGS) {
                val item = CompletionItem()
                item.label = tag
                item.kind = CompletionItemKind.Property
                item.insertText = tag
                completions.add(item)
            }
        }

        return CompletableFuture.supplyAsync { Either.forLeft(completions) }
    }

    override fun didOpen(params: DidOpenTextDocumentParams) {
        println("didOpen: $params")
        documents[params.textDocument.uri] = params.textDocument.text
    }

    override fun didChange(params: DidChangeTextDocumentParams) {
        println("didChange: $params")
        if (params.contentChanges.size == 1 && params.contentChanges[0].range == null) {
            documents[params.textDocument.uri] = params.contentChanges[0].text
        }
    }

    override fun didClose(params: DidCloseTextDocumentParams) {
        println("didClose: $params")
        documents.remove(params.textDocument.uri)
    }

    override fun didSave(params: DidSaveTextDocumentParams) {
        println("didSave: $params")
    }
}