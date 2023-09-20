package com.raredev.vcspace.editor;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.SizeUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.MaterialColors;
import com.raredev.vcspace.editor.databinding.LayoutTextActionItemBinding;
import com.raredev.vcspace.res.R;
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
import java.util.LinkedList;
import java.util.List;
import org.eclipse.tm4e.languageconfiguration.model.CommentRule;

public class EditorTextActions extends EditorPopupWindow {
  private static final long DELAY = 200;

  private final EditorTouchEventHandler handler;
  private final IDECodeEditor editor;

  private final List<TextAction> actions = new LinkedList<>();
  private final TextActionsAdapter adapter;

  private final RelativeLayout root;
  private final RecyclerView list;

  private long lastScroll;
  private int lastPosition;
  private int lastCause;

  public EditorTextActions(IDECodeEditor editor) {
    super(editor, FEATURE_SHOW_OUTSIDE_VIEW_ALLOWED);
    this.editor = editor;
    handler = editor.getEventHandler();

    Context context = editor.getContext();

    adapter = new TextActionsAdapter();

    root = new RelativeLayout(context);
    list = new RecyclerView(context);

    list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    list.setAdapter(adapter);
    root.addView(list);

    ensureTextActions();
    applyBackground();

    setContentView(root);
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
      // Always post show. See #193
      if (event.getCause() != SelectionChangeEvent.CAUSE_SEARCH) {
        editor.postInLifecycle(this::displayWindow);
      } else {
        dismiss();
      }
      lastPosition = -1;
    } else {
      var show = false;
      if (event.getCause() == SelectionChangeEvent.CAUSE_TAP
          && event.getLeft().index == lastPosition
          && !isShowing()
          && !editor.getText().isInBatchEdit()
          && editor.isEditable()) {
        editor.postInLifecycle(this::displayWindow);
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
    updateActionsBtnState();
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
    int panelX = (int) ((handleLeftX + handleRightX) / 2f - root.getMeasuredWidth() / 2f);
    setLocationAbsolutely(panelX, top);
    show();
  }

  private void updateActionsBtnState() {
    CommentRule commentRule = editor.getCommentRule();
    actions.get(0).visible =
        commentRule != null
            && (commentRule.lineComment != null || commentRule.blockComment != null) && editor.isEditable();

    actions.get(2).visible = editor.getCursor().isSelected();

    actions.get(3).enabled = editor.hasClip();
    actions.get(3).visible = editor.isEditable();

    actions.get(4).visible = (!editor.getCursor().isSelected() && editor.isEditable());
    actions.get(5).visible = (editor.getCursor().isSelected() && editor.isEditable());
    actions.get(6).visible = editor.isEditable();

    adapter.clear();
    for (TextAction action : actions) {
      if (action.visible) {
        adapter.addAction(action);
      }
    }
    adapter.notifyDataSetChanged();

    int dp8 = SizeUtils.dp2px(8f);
    int dp16 = dp8 * 2;

    root.measure(
        View.MeasureSpec.makeMeasureSpec(editor.getWidth() - dp16 * 2, View.MeasureSpec.AT_MOST),
        View.MeasureSpec.makeMeasureSpec(
            (int) (260 * editor.getDpUnit()) - dp16 * 2, View.MeasureSpec.AT_MOST));
    setSize(Math.min(root.getMeasuredWidth(), (int) (editor.getDpUnit() * 260)), getHeight());
  }

  @Override
  public void show() {
    if (editor.getSnippetController().isInSnippet()) {
      return;
    }
    super.show();
  }

  public void onTextActionClick(TextAction action) {
    if (editor == null) {
      dismiss();
      return;
    }
    int name = action.name;
    if (name == R.string.comment_line) {
      CommentRule commentRule = editor.getCommentRule();

      Cursor cursor = editor.getCursor();
      Content text = editor.getText();

      if (commentRule.lineComment != null && !cursor.isSelected()) {
        CommentSystem.addSingleComment(commentRule, text, cursor.getLeftLine());
      } else {
        CommentSystem.addBlockComment(commentRule, text, cursor);
      }
      editor.setSelection(cursor.getRightLine(), cursor.getRightColumn());
    } else if (name == R.string.select_all) {
      editor.selectAll();
      return;
    } else if (name == R.string.long_select) {
      editor.beginLongSelect();
    } else if (name == R.string.copy) {
      editor.copyText();
      editor.setSelection(editor.getCursor().getRightLine(), editor.getCursor().getRightColumn());
    } else if (name == R.string.paste) {
      editor.pasteText();
      editor.setSelection(editor.getCursor().getRightLine(), editor.getCursor().getRightColumn());
    } else if (name == R.string.cut) {
      if (editor.getCursor().isSelected()) {
        editor.cutText();
      }
    } else if (name == R.string.menu_format) {
      Cursor cursor = editor.getCursor();
      editor.setSelection(cursor.getRightLine(), cursor.getRightColumn());
      editor.formatCodeAsync();
    }
    dismiss();
  }

  private void ensureTextActions() {
    actions.add(new TextAction(R.drawable.ic_comment_text_outline, R.string.comment_line));
    actions.add(new TextAction(R.drawable.ic_select_all, R.string.select_all));
    actions.add(new TextAction(R.drawable.content_copy, R.string.copy));
    actions.add(new TextAction(R.drawable.ic_paste, R.string.paste));
    actions.add(new TextAction(R.drawable.editor_text_select_start, R.string.long_select));
    actions.add(new TextAction(R.drawable.ic_cut, R.string.cut));
    actions.add(new TextAction(R.drawable.format_align_left, R.string.menu_format));
  }

  private void applyBackground() {
    final var colorSurface =
        MaterialColors.getColor(
            editor.getContext(), com.google.android.material.R.attr.colorSurface, 0);
    final var colorOutline =
        MaterialColors.getColor(
            editor.getContext(), com.google.android.material.R.attr.colorOutline, 0);

    GradientDrawable drawable = new GradientDrawable();
    drawable.setShape(GradientDrawable.RECTANGLE);
    drawable.setCornerRadius(SizeUtils.dp2px(20));
    drawable.setColor(ColorStateList.valueOf(colorSurface));
    drawable.setStroke(2, colorOutline);
    root.setBackground(drawable);
  }

  class TextActionsAdapter extends RecyclerView.Adapter<VH> {

    private List<TextAction> data = new LinkedList<>();

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
      return new VH(
          LayoutTextActionItemBinding.inflate(
              LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
      var action = data.get(position);
      holder.action.setClickable(action.enabled);
      holder.action.setIconResource(action.icon);
      holder.action.setTooltipText(holder.itemView.getContext().getString(action.name));
      holder.action.setOnClickListener(v -> onTextActionClick(action));
    }

    @Override
    public int getItemCount() {
      return data.size();
    }

    public void addAction(TextAction action) {
      data.add(action);
    }

    public void clear() {
      data.clear();
    }
  }

  class VH extends RecyclerView.ViewHolder {
    MaterialButton action;

    public VH(LayoutTextActionItemBinding binding) {
      super(binding.getRoot());
      action = binding.action;
    }
  }

  class TextAction {
    public int icon, name;
    public boolean visible, enabled;

    public TextAction(int icon, int name) {
      this.icon = icon;
      this.name = name;

      visible = true;
      enabled = true;
    }
  }
}
