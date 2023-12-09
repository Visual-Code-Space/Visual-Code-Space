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

import android.animation.LayoutTransition
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.raredev.vcspace.adapters.TextActionListAdapter
import com.raredev.vcspace.extensions.getAttrColor
import com.raredev.vcspace.models.TextAction
import com.raredev.vcspace.res.R
import io.github.rosemoe.sora.event.HandleStateChangeEvent
import io.github.rosemoe.sora.event.ScrollEvent
import io.github.rosemoe.sora.event.SelectionChangeEvent
import io.github.rosemoe.sora.widget.base.EditorPopupWindow
import kotlin.math.max
import kotlin.math.min

class TextActionsWindow(editor: VCSpaceEditor) :
  EditorPopupWindow(editor, FEATURE_SHOW_OUTSIDE_VIEW_ALLOWED) {

  companion object {
    const val DELAY: Long = 200
  }

  private val eventHandler = editor.eventHandler
  private val rootView = RelativeLayout(editor.context)
  private val recyclerView = RecyclerView(editor.context)
  private val actionsAdapter = TextActionListAdapter(this)

  private var lastScroll: Long = 0
  private var lastPosition: Int = 0
  private var lastCause: Int = 0

  init {
    recyclerView.apply {
      layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
      adapter = actionsAdapter
    }

    rootView.background =
      GradientDrawable().apply {
        setStroke(2, rootView.context.getAttrColor(com.google.android.material.R.attr.colorOutline))
        setColor(rootView.context.getAttrColor(com.google.android.material.R.attr.colorSurface))
        setCornerRadius(25f)
      }
    rootView.addView(recyclerView)

    popup.contentView = rootView
    popup.animationStyle = io.github.rosemoe.sora.R.style.text_action_popup_animation
    setSize(0, (editor.dpUnit * 48).toInt())

    editor.subscribeEvent(SelectionChangeEvent::class.java) { event, _ ->
      onSelectionChanged(event)
    }
    editor.subscribeEvent(ScrollEvent::class.java) { _, _ ->
      val last = lastScroll
      lastScroll = System.currentTimeMillis()
      if (lastScroll - last < DELAY) {
        postDisplay()
      }
    }
    editor.subscribeEvent(HandleStateChangeEvent::class.java) { event, _ ->
      if (event.isHeld) {
        postDisplay()
      }
    }
    setupAnimation()
  }

  fun executeTextAction(action: TextAction) {
    when (action.text) {
      R.string.comment_line -> {
        val commentRule = (editor as VCSpaceEditor).getCommentRule()
        if (!editor.cursor.isSelected) {
          addSingleComment(commentRule, editor.text)
        } else {
          addBlockComment(commentRule, editor.text)
        }
        editor.setSelection(editor.cursor.rightLine, editor.cursor.rightColumn)
      }
      R.string.select_all -> {
        editor.selectAll()
        return
      }
      R.string.long_select -> editor.beginLongSelect()
      R.string.copy -> {
        editor.copyText()
        editor.setSelection(editor.cursor.rightLine, editor.cursor.rightColumn)
      }
      R.string.paste -> {
        editor.pasteText()
        editor.setSelection(editor.cursor.rightLine, editor.cursor.rightColumn)
      }
      R.string.cut -> {
        if (editor.cursor.isSelected) {
          editor.cutText()
        }
      }
      R.string.menu_format -> {
        editor.setSelection(editor.cursor.rightLine, editor.cursor.rightColumn)
        editor.formatCodeAsync()
      }
    }
    dismiss()
  }

  override fun show() {
    if (editor.snippetController.isInSnippet()) {
      return
    }
    super.show()
  }

  private fun postDisplay() {
    if (!isShowing) {
      return
    }
    dismiss()
    if (!editor.cursor.isSelected) {
      return
    }
    editor.postDelayedInLifecycle(
      object : Runnable {
        override fun run() {
          if (
            !eventHandler.hasAnyHeldHandle() &&
              !editor.snippetController.isInSnippet() &&
              System.currentTimeMillis() - lastScroll > DELAY &&
              eventHandler.scroller.isFinished
          ) {
            displayWindow()
          } else {
            editor.postDelayedInLifecycle(this, DELAY)
          }
        }
      },
      DELAY
    )
  }

  fun onSelectionChanged(event: SelectionChangeEvent) {
    if (eventHandler.hasAnyHeldHandle()) {
      return
    }
    lastCause = event.cause
    if (event.isSelected) {
      editor.postInLifecycle { displayWindow() }
      lastPosition = -1
    } else {
      var show = false
      if (
        event.cause == SelectionChangeEvent.CAUSE_TAP &&
          event.left.index == lastPosition &&
          !isShowing &&
          !editor.text.isInBatchEdit
      ) {
        editor.postInLifecycle { displayWindow() }
        show = true
      } else {
        dismiss()
      }
      lastPosition =
        if (event.cause == SelectionChangeEvent.CAUSE_TAP && !show) {
          event.left.index
        } else {
          -1
        }
    }
  }

  private fun selectTop(rect: RectF): Int {
    val rowHeight = editor.rowHeight
    return if (rect.top - rowHeight * 3 / 2F > height) {
      (rect.top - rowHeight * 3 / 2 - height).toInt()
    } else {
      (rect.bottom + rowHeight / 2).toInt()
    }
  }

  private fun displayWindow() {
    updateActions()

    var top: Int
    val cursor = editor.cursor
    top =
      if (cursor.isSelected) {
        val leftRect = editor.leftHandleDescriptor.position
        val rightRect = editor.rightHandleDescriptor.position
        val top1 = selectTop(leftRect)
        val top2 = selectTop(rightRect)
        min(top1, top2)
      } else {
        selectTop(editor.insertHandleDescriptor.position)
      }
    top = max(0, min(top, editor.height - height - 5))
    val handleLeftX = editor.getOffset(editor.cursor.leftLine, editor.cursor.leftColumn)
    val handleRightX = editor.getOffset(editor.cursor.rightLine, editor.cursor.rightColumn)
    val panelX = ((handleLeftX + handleRightX) / 2f - rootView.measuredWidth / 2f).toInt()
    setLocationAbsolutely(panelX, top)
    show()
  }

  private fun updateActions() {
    actionsAdapter.apply {

      // Comment action
      val commentRule = (editor as VCSpaceEditor).getCommentRule()
      updateAction(0, commentRule != null && editor.isEditable)

      // Select all action
      updateAction(1, true)

      // Long select action
      updateAction(2, editor.isEditable)

      // Copy action
      updateAction(3, editor.cursor.isSelected, editor.cursor.isSelected)

      // Paste action
      updateAction(4, true, editor.hasClip())

      // Cut action
      updateAction(5, editor.isEditable && editor.cursor.isSelected)

      // Format action
      updateAction(6, editor.isEditable)

      refreshActions()
    }

    val dp8 = SizeUtils.dp2px(8f)
    val dp16 = dp8 * 2
    rootView.measure(
      View.MeasureSpec.makeMeasureSpec(editor.width - dp16 * 2, View.MeasureSpec.AT_MOST),
      View.MeasureSpec.makeMeasureSpec(
        (260 * editor.dpUnit).toInt() - dp16 * 2,
        View.MeasureSpec.AT_MOST
      )
    )
    setSize(rootView.measuredWidth, height)
  }

  private fun setupAnimation() {
    val transition = LayoutTransition()
    transition.enableTransitionType(LayoutTransition.CHANGING)
    rootView.setLayoutTransition(transition)
  }
}