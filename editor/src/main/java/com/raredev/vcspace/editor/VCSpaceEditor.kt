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

package com.raredev.vcspace.editor

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.core.content.res.ResourcesCompat
import com.raredev.vcspace.adapters.CompletionListAdapter
import com.raredev.vcspace.editor.completion.CustomCompletionLayout
import com.raredev.vcspace.events.OnContentChangeEvent
import com.raredev.vcspace.events.OnPreferenceChangeEvent
import com.raredev.vcspace.utils.PreferencesUtils
import com.raredev.vcspace.utils.SharedPreferencesKeys
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow
import java.io.File
import org.eclipse.tm4e.languageconfiguration.model.CommentRule
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class VCSpaceEditor : CodeEditor {

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

  constructor(
      context: Context,
      attrs: AttributeSet?,
      defStyleAttr: Int
  ) : super(context, attrs, defStyleAttr)

  private var textActions: TextActionsWindow? = TextActionsWindow(this)

  var file: File? = null
  var modified = false

  init {
    getComponent(EditorTextActionWindow::class.java).isEnabled = false
    getComponent(EditorAutoCompletion::class.java).setLayout(CustomCompletionLayout())
    getComponent(EditorAutoCompletion::class.java).setAdapter(CompletionListAdapter())
    configureEditor()

    EventBus.getDefault().register(this)
  }

  override fun hideEditorWindows() {
    super.hideEditorWindows()
    textActions?.dismiss()
  }

  override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
    super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
    if (!gainFocus) {
      hideEditorWindows()
    }
  }

  override fun release() {
    super.release()
    textActions = null
    file = null

    EventBus.getDefault().unregister(this)
  }

  fun subscribeEvents() {
    subscribeEvent(ContentChangeEvent::class.java) { _, _ ->
      modified = true

      EventBus.getDefault().post(OnContentChangeEvent(file))
    }
  }

  fun getCommentRule(): CommentRule? {
    return (editorLanguage as? VCSpaceTMLanguage)?.languageConfiguration?.comments
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onSharedPreferenceChanged(event: OnPreferenceChangeEvent) {
    when (event.prefKey) {
      SharedPreferencesKeys.KEY_EDITOR_TEXT_SIZE -> updateTextSize()
      SharedPreferencesKeys.KEY_EDITOR_TAB_SIZE -> updateTABSize()
      SharedPreferencesKeys.KEY_STICKYSCROLL -> updateStickyScroll()
      SharedPreferencesKeys.KEY_FONTLIGATURES -> updateFontLigatures()
      SharedPreferencesKeys.KEY_WORDWRAP -> updateWordWrap()
      SharedPreferencesKeys.KEY_DELETE_EMPTY_LINE_FAST -> updateDeleteEmptyLineFast()
      SharedPreferencesKeys.KEY_EDITOR_FONT -> updateEditorFont()
      SharedPreferencesKeys.KEY_LINENUMBERS -> updateLineNumbers()
      SharedPreferencesKeys.KEY_DELETETABS -> updateDeleteTabs()
    }
  }

  private fun configureEditor() {
    updateEditorFont()
    updateTextSize()
    updateTABSize()
    updateStickyScroll()
    updateFontLigatures()
    updateWordWrap()
    updateLineNumbers()
    updateDeleteEmptyLineFast()
    updateDeleteTabs()

    inputType = createInputTypeFlags()
  }

  private fun updateTextSize() {
    setTextSize(PreferencesUtils.textSize.toFloat())
  }

  private fun updateTABSize() {
    tabWidth = PreferencesUtils.tabSize
  }

  private fun updateEditorFont() {
    val font = PreferencesUtils.selectedFont
    typefaceText = ResourcesCompat.getFont(context, font)
    typefaceLineNumber = ResourcesCompat.getFont(context, font)
  }

  private fun updateStickyScroll() {
    props.stickyScroll = PreferencesUtils.stickyScroll
  }

  private fun updateFontLigatures() {
    isLigatureEnabled = PreferencesUtils.fontLigatures
  }

  private fun updateWordWrap() {
    isWordwrap = PreferencesUtils.wordWrap
  }

  private fun updateLineNumbers() {
    isLineNumberEnabled = PreferencesUtils.lineNumbers
  }

  private fun updateDeleteEmptyLineFast() {
    props.deleteEmptyLineFast = PreferencesUtils.deleteEmptyLineFast
  }

  private fun updateDeleteTabs() {
    props.deleteMultiSpaces = if (PreferencesUtils.deleteMultiSpaces) -1 else 1
  }

  companion object {

    fun createInputTypeFlags(): Int {
      return EditorInfo.TYPE_CLASS_TEXT or
          EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE or
          EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    }
  }
}
