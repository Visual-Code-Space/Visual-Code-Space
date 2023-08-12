package com.raredev.vcspace.editor;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;
import com.google.android.material.color.MaterialColors;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.LayoutTextActionsBinding;
import com.raredev.vcspace.util.CommentSystem;
import com.raredev.vcspace.util.Utils;
import io.github.rosemoe.sora.event.HandleStateChangeEvent;
import io.github.rosemoe.sora.event.InterceptTarget;
import io.github.rosemoe.sora.event.LongPressEvent;
import io.github.rosemoe.sora.event.ScrollEvent;
import io.github.rosemoe.sora.event.SelectionChangeEvent;
import io.github.rosemoe.sora.event.Unsubscribe;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.Cursor;
import io.github.rosemoe.sora.widget.EditorTouchEventHandler;
import io.github.rosemoe.sora.widget.base.EditorPopupWindow;
import org.eclipse.tm4e.languageconfiguration.model.CommentRule;

public class EditorTextActions extends EditorPopupWindow implements View.OnClickListener {
  private static final long DELAY = 200;

  private LayoutTextActionsBinding binding;

  private final EditorTouchEventHandler handler;
  private final IDECodeEditor editor;

  private long lastScroll;
  private int lastPosition;
  private int lastCause;

  public EditorTextActions(IDECodeEditor editor) {
    super(editor, FEATURE_SHOW_OUTSIDE_VIEW_ALLOWED);
    this.editor = editor;
    handler = editor.getEventHandler();

    Context tempContext = editor.getContext();

    binding = LayoutTextActionsBinding.inflate(LayoutInflater.from(tempContext));

    applyBackground();

    binding.comment.setOnClickListener(this);
    binding.selectAll.setOnClickListener(this);
    binding.cut.setOnClickListener(this);
    binding.copy.setOnClickListener(this);
    binding.paste.setOnClickListener(this);

    TooltipCompat.setTooltipText(binding.comment, tempContext.getString(R.string.comment_line));
    TooltipCompat.setTooltipText(binding.uncomment, tempContext.getString(R.string.uncomment_line));
    TooltipCompat.setTooltipText(binding.selectAll, tempContext.getString(R.string.select_all));
    TooltipCompat.setTooltipText(binding.cut, tempContext.getString(R.string.cut));
    TooltipCompat.setTooltipText(binding.copy, tempContext.getString(R.string.copy));
    TooltipCompat.setTooltipText(binding.paste, tempContext.getString(R.string.paste));

    setContentView(binding.getRoot());
    setSize(0, (int) (this.editor.getDpUnit() * 48));

    editor.subscribeEvent(SelectionChangeEvent.class, this::onSelectionChanged);
    editor.subscribeEvent(
        ScrollEvent.class,
        ((event, unsubscribe) -> {
          var last = lastScroll;
          lastScroll = System.currentTimeMillis();
          if (lastScroll - last < DELAY && lastCause != SelectionChangeEvent.CAUSE_SEARCH) {
            postDisplay();
          }
        }));
    editor.subscribeEvent(
        HandleStateChangeEvent.class,
        ((event, unsubscribe) -> {
          if (event.isHeld()) {
            postDisplay();
          }
        }));
    editor.subscribeEvent(
        LongPressEvent.class,
        ((event, unsubscribe) -> {
          if (editor.getCursor().isSelected() && lastCause == SelectionChangeEvent.CAUSE_SEARCH) {
            var idx = event.getIndex();
            if (idx >= editor.getCursor().getLeft() && idx <= editor.getCursor().getRight()) {
              lastCause = 0;
              displayWindow();
            }
            event.intercept(InterceptTarget.TARGET_EDITOR);
          }
        }));
    editor.subscribeEvent(
        HandleStateChangeEvent.class,
        ((event, unsubscribe) -> {
          if (!event.getEditor().getCursor().isSelected()
              && event.getHandleType() == HandleStateChangeEvent.HANDLE_TYPE_INSERT
              && !event.isHeld()) {
            displayWindow();
            // Also, post to hide the window on handle disappearance
            editor.postDelayedInLifecycle(
                new Runnable() {
                  @Override
                  public void run() {
                    if (!editor.getEventHandler().shouldDrawInsertHandle()
                        && !editor.getCursor().isSelected()) {
                      dismiss();
                    } else if (!editor.getCursor().isSelected()) {
                      editor.postDelayedInLifecycle(this, 100);
                    }
                  }
                },
                100);
          }
        }));

    getPopup().setAnimationStyle(io.github.rosemoe.sora.R.style.text_action_popup_animation);
  }

  public ViewGroup getView() {
    return (ViewGroup) getPopup().getContentView();
  }

  private void postDisplay() {
    if (!isShowing()) {
      return;
    }
    dismiss();
    if (!editor.getCursor().isSelected()) {
      return;
    }
    editor.postDelayed(
        new Runnable() {
          @Override
          public void run() {
            if (!handler.hasAnyHeldHandle()
                && !editor.getSnippetController().isInSnippet()
                && System.currentTimeMillis() - lastScroll > DELAY
                && editor.getScroller().isFinished()) {
              displayWindow();
            } else {
              editor.postDelayedInLifecycle(this, DELAY);
            }
          }
        },
        DELAY);
  }

  public void onSelectionChanged(
      @NonNull SelectionChangeEvent event, @NonNull Unsubscribe unsubscribe) {
    if (handler.hasAnyHeldHandle()) {
      return;
    }
    lastCause = event.getCause();
    if (event.isSelected()) {
      editor.post(this::displayWindow);
      lastPosition = -1;
    } else {
      var show = false;
      if (event.getCause() == SelectionChangeEvent.CAUSE_TAP
          && event.getLeft().index == lastPosition
          && !isShowing()
          && !editor.getText().isInBatchEdit()
          && editor.isEditable()) {
        editor.post(this::displayWindow);
        show = true;
      } else {
        dismiss();
      }
      if (event.getCause() == SelectionChangeEvent.CAUSE_TAP && !show) {
        lastPosition = event.getLeft().index;
      } else {
        lastPosition = -1;
      }
    }
  }

  private int selectTop(@NonNull RectF rect) {
    var rowHeight = editor.getRowHeight();
    if (rect.top - rowHeight * 3 / 2F > getHeight()) {
      return (int) (rect.top - rowHeight * 3 / 2 - getHeight());
    } else {
      return (int) (rect.bottom + rowHeight / 2);
    }
  }

  public void displayWindow() {
    updateBtnState();
    int top;
    var cursor = editor.getCursor();
    if (cursor.isSelected()) {
      var leftRect = editor.getLeftHandleDescriptor().position;
      var rightRect = editor.getRightHandleDescriptor().position;
      var top1 = selectTop(leftRect);
      var top2 = selectTop(rightRect);
      top = Math.min(top1, top2);
    } else {
      top = selectTop(editor.getInsertHandleDescriptor().position);
    }
    top = Math.max(0, Math.min(top, editor.getHeight() - getHeight() - 5));
    float handleLeftX =
        editor.getOffset(editor.getCursor().getLeftLine(), editor.getCursor().getLeftColumn());
    float handleRightX =
        editor.getOffset(editor.getCursor().getRightLine(), editor.getCursor().getRightColumn());
    int panelX =
        (int) ((handleLeftX + handleRightX) / 2f - binding.getRoot().getMeasuredWidth() / 2f);
    setLocationAbsolutely(panelX, top);
    show();
  }

  private void updateBtnState() {
    binding.paste.setEnabled(editor.hasClip());

    CommentRule commentRule = editor.getCommentRule();
    if (commentRule != null
        && (commentRule.lineComment != null || commentRule.blockComment != null)) {
      binding.comment.setVisibility(View.VISIBLE);
    }

    binding.uncomment.setVisibility(View.GONE);
    binding.copy.setVisibility(editor.getCursor().isSelected() ? View.VISIBLE : View.GONE);
    binding.paste.setVisibility(editor.isEditable() ? View.VISIBLE : View.GONE);
    binding.cut.setVisibility(
        (editor.getCursor().isSelected() && editor.isEditable()) ? View.VISIBLE : View.GONE);
    binding
        .getRoot()
        .measure(
            View.MeasureSpec.makeMeasureSpec(1000000, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(100000, View.MeasureSpec.AT_MOST));
    setSize(
        Math.min(binding.getRoot().getMeasuredWidth(), (int) (editor.getDpUnit() * 230)),
        getHeight());
  }

  @Override
  public void show() {
    if (editor.getSnippetController().isInSnippet()) {
      return;
    }
    super.show();
  }

  @Override
  public void onClick(View view) {
    int id = view.getId();
    if (id == R.id.comment) {
      CommentRule commentRule = editor.getCommentRule();

      Cursor cursor = editor.getCursor();
      Content text = editor.getText();

      if (commentRule.lineComment != null && !cursor.isSelected()) {
        CommentSystem.addSingleComment(commentRule, text, cursor.getLeftLine());
      } else {
        CommentSystem.addBlockComment(commentRule, text, cursor);
      }
      editor.setSelection(cursor.getRightLine(), cursor.getRightColumn());
    } /* else if (id == R.id.uncomment) {
        String commentPrefix = editor.commentPrefix;
        if (commentPrefix != null) {
          Cursor cursor = editor.getCursor();
          Content text = editor.getText();
          text.replace(cursor.getLeftLine(), cursor.getRightLine(), CommentSystem.uncomment(text));
        }
      }*/ else if (id == R.id.select_all) {
      editor.selectAll();
      return;
    } else if (id == R.id.cut) {
      if (editor.getCursor().isSelected()) {
        editor.cutText();
      }
    } else if (id == R.id.paste) {
      editor.pasteText();
      editor.setSelection(editor.getCursor().getRightLine(), editor.getCursor().getRightColumn());
    } else if (id == R.id.copy) {
      editor.copyText();
      editor.setSelection(editor.getCursor().getRightLine(), editor.getCursor().getRightColumn());
    }
    dismiss();
  }

  protected void applyBackground() {
    final var colorSurface =
        MaterialColors.getColor(
            editor.getContext(), com.google.android.material.R.attr.colorSurface, 0);
    final var colorOutline =
        MaterialColors.getColor(
            editor.getContext(), com.google.android.material.R.attr.colorOutline, 0);

    GradientDrawable drawable = new GradientDrawable();
    drawable.setShape(GradientDrawable.RECTANGLE);
    drawable.setCornerRadius(Utils.pxToDp(20));
    drawable.setColor(ColorStateList.valueOf(colorSurface));
    drawable.setStroke(2, colorOutline);
    binding.getRoot().setBackground(drawable);
  }
}
