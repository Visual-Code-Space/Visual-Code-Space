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

import android.graphics.RectF
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import com.blankj.utilcode.util.SizeUtils
import io.github.rosemoe.sora.event.HandleStateChangeEvent
import io.github.rosemoe.sora.event.InterceptTarget
import io.github.rosemoe.sora.event.LongPressEvent
import io.github.rosemoe.sora.event.ScrollEvent
import io.github.rosemoe.sora.event.SelectionChangeEvent
import io.github.rosemoe.sora.widget.base.EditorPopupWindow
import kotlin.math.max
import kotlin.math.min

class TextActionsWindow(
    private val editor: VCSpaceEditor,
    private val rootView: View? = null,
    private val onUpdateButtonRequest: () -> Unit = {}
) : EditorPopupWindow(editor, FEATURE_SHOW_OUTSIDE_VIEW_ALLOWED) {

    companion object {
        private const val DELAY = 200L
    }

    private val eventHandler = editor.eventHandler
    private val eventManager = editor.createSubEventManager()

    private val view = LinearLayout(editor.context)

    private var lastScroll: Long = 0
    private var lastPosition: Int = 0
    private var lastCause: Int = 0

    init {
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        rootView?.let { (view as ViewGroup).addView(it) }
        popup.contentView = view
        popup.animationStyle = io.github.rosemoe.sora.R.style.text_action_popup_animation
        setSize(0, WindowManager.LayoutParams.WRAP_CONTENT)

        eventManager.subscribeAlways(SelectionChangeEvent::class.java, ::onSelectionChanged)
        eventManager.subscribeAlways(ScrollEvent::class.java, ::onEditorScroll)
        eventManager.subscribeAlways(HandleStateChangeEvent::class.java, ::onHandleStateChange)
        eventManager.subscribeAlways(LongPressEvent::class.java, ::onEditorLongPress)
    }

    private fun onEditorLongPress(event: LongPressEvent) {
        if (editor.cursor.isSelected && lastCause == SelectionChangeEvent.CAUSE_SEARCH) {
            val idx = event.index
            if (idx >= editor.cursor.left && idx <= editor.cursor.right) {
                lastCause = 0
                displayWindow()
            }
            event.intercept(InterceptTarget.TARGET_EDITOR)
        }
    }

    private fun onEditorScroll(event: ScrollEvent) {
        val last = lastScroll
        lastScroll = System.currentTimeMillis()
        if (lastScroll - last < DELAY) {
            postDisplay()
        }
    }

    private fun onHandleStateChange(event: HandleStateChangeEvent) {
        if (event.isHeld) {
            postDisplay()
        }
    }

    override fun show() {
        if (editor.snippetController.isInSnippet() || editor.isInMouseMode) {
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

    private fun onSelectionChanged(event: SelectionChangeEvent) {
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
        return if (rect.top - rowHeight * 9 / 2F > height) {
            (rect.top - rowHeight * 9 / 2 - height).toInt()
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
        val panelX =
            ((handleLeftX + handleRightX) / 2f - (rootView?.measuredWidth ?: width) / 2f).toInt()
        setLocationAbsolutely(panelX, top)
        show()
    }

    private fun updateActions() {
        onUpdateButtonRequest()

        val dp8 = SizeUtils.dp2px(8f)
        val dp32 = dp8 * 4
        rootView?.measure(
            View.MeasureSpec.makeMeasureSpec(editor.width - dp32 * 5, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(
                (260 * editor.dpUnit).toInt() - dp32 * 5,
                View.MeasureSpec.AT_MOST,
            ),
        )
        rootView?.measuredWidth?.let { setSize(it, height) }
    }
}
