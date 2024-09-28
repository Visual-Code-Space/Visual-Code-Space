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

package com.teixeira.vcspace.editor

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import com.blankj.utilcode.util.ToastUtils
import com.teixeira.vcspace.editor.completion.CompletionListAdapter
import com.teixeira.vcspace.editor.completion.CustomCompletionLayout
import com.teixeira.vcspace.editor.events.OnContentChangeEvent
import com.teixeira.vcspace.editor.lsp.service.KotlinLSPService
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.lsp.client.connection.SocketStreamConnectionProvider
import io.github.rosemoe.sora.lsp.client.languageserver.serverdefinition.CustomLanguageServerDefinition
import io.github.rosemoe.sora.lsp.client.languageserver.serverdefinition.CustomLanguageServerDefinition.ServerConnectProvider
import io.github.rosemoe.sora.lsp.client.languageserver.wrapper.EventHandler
import io.github.rosemoe.sora.lsp.editor.LspEditor
import io.github.rosemoe.sora.lsp.editor.LspProject
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eclipse.lsp4j.DidChangeWorkspaceFoldersParams
import org.eclipse.lsp4j.WorkspaceFolder
import org.eclipse.lsp4j.WorkspaceFoldersChangeEvent
import org.eclipse.tm4e.languageconfiguration.internal.model.CommentRule
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.net.ServerSocket

class VCSpaceEditor @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0,
) : CodeEditor(context, attrs, defStyleAttr, defStyleRes) {

  private var textActions: TextActionsWindow? = TextActionsWindow(this)

  private lateinit var lspEditor: LspEditor
  private lateinit var lspProject: LspProject

  var file: File? = null
  var modified: Boolean = false

  val commentRule: CommentRule?
    get() = (editorLanguage as? TextMateLanguage)?.languageConfiguration?.comments

  init {
    getComponent(EditorTextActionWindow::class.java).isEnabled = false
    getComponent(EditorAutoCompletion::class.java).setLayout(CustomCompletionLayout())
    getComponent(EditorAutoCompletion::class.java).setAdapter(CompletionListAdapter())
    subscribeEvent(ContentChangeEvent::class.java) { event, _ ->
      modified = event.action != ContentChangeEvent.ACTION_SET_NEW_TEXT
      EventBus.getDefault().post(OnContentChangeEvent(file))
    }
    inputType = createInputTypeFlags()
  }

  override fun hideEditorWindows() {
    super.hideEditorWindows()
    textActions?.dismiss()
  }

  override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
    super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
    if (!gainFocus) hideEditorWindows()
  }

  override fun release() {
    super.release()
    textActions = null
    file = null
  }

  suspend fun connectToKotlinLsp(
    scope: CoroutineScope,
    wrapperLanguage: Language,
    lspEventListener: EventHandler.EventListener
  ) {
    withContext(Dispatchers.IO) {
      withContext(Dispatchers.Main) { editable = false }

      val port = randomPort()
      val projectPath = file?.parentFile?.absolutePath ?: ""

      context.startService(
        Intent(
          context,
          KotlinLSPService::class.java
        ).apply { putExtra("port", port) }
      )

      val kotlinServerDefinition = object : CustomLanguageServerDefinition(
        ext = "kt",
        serverConnectProvider = ServerConnectProvider {
          SocketStreamConnectionProvider(port)
        }
      ) {
        override val eventListener: EventHandler.EventListener
          get() = lspEventListener
      }

      lspProject = LspProject(projectPath)
      lspProject.addServerDefinition(kotlinServerDefinition)

      withContext(Dispatchers.Main) {
        if (file != null) {
          lspEditor = lspProject.createEditor(file!!.absolutePath)
          lspEditor.wrapperLanguage = wrapperLanguage
          lspEditor.editor = this@VCSpaceEditor
        }
      }

      var connected: Boolean
      try {
        lspEditor.connectWithTimeout()

        lspEditor.requestManager?.didChangeWorkspaceFolders(DidChangeWorkspaceFoldersParams().apply {
          this.event = WorkspaceFoldersChangeEvent().apply {
            added = listOf(WorkspaceFolder("file://$projectPath", file?.parentFile?.name))
          }
        })

        connected = true
      } catch (err: Exception) {
        connected = false
        err.printStackTrace()
      }

      scope.launch(Dispatchers.Main) {
        if (connected) {
          ToastUtils.showShort("Initialized language server for Kotlin")
        } else {
          ToastUtils.showShort("Unable to connect language server")
        }

        editable = true
      }
    }
  }

  private fun randomPort(): Int {
    val serverSocket = ServerSocket(0)
    val port = serverSocket.localPort
    serverSocket.close()
    return port
  }

  companion object {
    fun createInputTypeFlags(): Int {
      return EditorInfo.TYPE_CLASS_TEXT or
        EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE or
        EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    }
  }
}
