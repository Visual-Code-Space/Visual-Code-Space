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
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.TextEdit
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.TextDocumentService
import java.util.concurrent.CompletableFuture

class HtmlTextDocumentService : TextDocumentService {
    val documents = mutableMapOf<String, String>()
    private var client: LanguageClient? = null

    companion object {
        private val HTML_TAGS = arrayOf(
            "html", "head", "title", "body", "h1", "h2", "h3", "h4", "h5", "h6",
            "p", "br", "hr", "div", "span", "a", "img", "table", "tr", "td",
            "th", "ul", "ol", "li", "form", "input", "button", "select", "option",
            "label", "textarea", "script", "style", "link", "meta", "header",
            "footer", "nav", "section", "article", "aside", "main", "canvas"
        )
        
        private val HTML_ATTRIBUTES = mapOf(
            "class" to "CSS class",
            "id" to "Unique identifier",
            "style" to "Inline CSS",
            "href" to "Hyperlink reference",
            "src" to "Source URL",
            "alt" to "Alternative text",
            "width" to "Width",
            "height" to "Height"
        )
    }

    fun connect(client: LanguageClient) {
        this.client = client
    }

    override fun completion(position: CompletionParams): CompletableFuture<Either<MutableList<CompletionItem>, CompletionList>> {
        return CompletableFuture.supplyAsync {
            val documentUri = position.textDocument.uri
            val content = documents[documentUri] ?: return@supplyAsync Either.forLeft(mutableListOf())
            
            val lineStart = findLineStart(content, position.position.line)
            val cursorPos = lineStart + position.position.character
            val linePrefix = if (cursorPos <= content.length) content.substring(lineStart, cursorPos) else ""
            
            val completions = mutableListOf<CompletionItem>()
            
            // HTML tag completion after <
            if (linePrefix.lastIndexOf("<") > linePrefix.lastIndexOf(">")) {
                // Check if we're in a closing tag
                if (linePrefix.endsWith("</")) {
                    // Offer closing tags based on context
                    for (tag in HTML_TAGS) {
                        val item = CompletionItem(tag)
                        item.kind = CompletionItemKind.Property
                        item.insertText = "$tag>"
                        item.detail = "Close $tag element"
                        completions.add(item)
                    }
                } else {
                    // Offer opening tags
                    for (tag in HTML_TAGS) {
                        val item = CompletionItem(tag)
                        item.kind = CompletionItemKind.Property
                        
                        // Create a text edit that will insert the tag with closing bracket
                        val textEdit = TextEdit(
                            Range(position.position, position.position),
                            "$tag></$tag>"
                        )
                        item.textEdit = Either.forLeft(textEdit)
                        
                        item.detail = "HTML element"
                        item.documentation = Either.forLeft("The $tag element")
                        completions.add(item)
                    }
                }
            }
            
            // Attribute completion inside a tag
            val tagStart = linePrefix.lastIndexOf("<")
            val tagEnd = linePrefix.lastIndexOf(">")
            if (tagStart > tagEnd && linePrefix.contains(" ") && !linePrefix.endsWith(">")) {
                // We're inside a tag after a space, suggest attributes
                for ((attr, description) in HTML_ATTRIBUTES) {
                    val item = CompletionItem(attr)
                    item.kind = CompletionItemKind.Property
                    item.insertText = "$attr=\"\""
                    item.detail = description
                    completions.add(item)
                }
            }
            
            Either.forLeft(completions)
        }
    }
    
    private fun findLineStart(content: String, line: Int): Int {
        var lineStart = 0
        var lineCount = 0
        var pos = 0
        
        // Find the start of the current line
        while (lineCount < line && pos < content.length) {
            if (content[pos] == '\n') {
                lineStart = pos + 1
                lineCount++
            }
            pos++
        }
        
        return lineStart
    }

    override fun didOpen(params: DidOpenTextDocumentParams) {
        println("didOpen: $params")
        documents[params.textDocument.uri] = params.textDocument.text
    }

    override fun didChange(params: DidChangeTextDocumentParams) {
        println("didChange: $params")
        if (params.contentChanges.isNotEmpty()) {
            val change = params.contentChanges[0]
            if (change.range == null) {
                // Full document update
                documents[params.textDocument.uri] = change.text
            } else {
                // Incremental update
                val document = documents[params.textDocument.uri] ?: return
                val startPos = calculatePosition(document, change.range.start)
                val endPos = calculatePosition(document, change.range.end)
                
                if (startPos != -1 && endPos != -1 && startPos <= endPos) {
                    val newText = document.substring(0, startPos) + change.text + document.substring(endPos)
                    documents[params.textDocument.uri] = newText
                }
            }
        }
    }
    
    private fun calculatePosition(document: String, position: Position): Int {
        var currentLine = 0
        var currentPos = 0
        
        while (currentLine < position.line && currentPos < document.length) {
            if (document[currentPos] == '\n') {
                currentLine++
            }
            currentPos++
        }
        
        return if (currentLine == position.line) {
            currentPos + position.character
        } else {
            -1
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