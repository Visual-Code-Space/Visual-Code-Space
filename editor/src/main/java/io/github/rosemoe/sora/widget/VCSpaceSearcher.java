package io.github.rosemoe.sora.widget;

import com.raredev.vcspace.res.R;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.util.DialogUtils;
import com.raredev.vcspace.util.ToastUtils;
import io.github.rosemoe.sora.event.SelectionChangeEvent;
import io.github.rosemoe.sora.util.IntPair;

public class VCSpaceSearcher extends EditorSearcher {

  private CodeEditor editor;

  public VCSpaceSearcher(CodeEditor editor) {
    super(editor);
    this.editor = editor;
  }

  @Override
  public boolean gotoPrevious() {
    if (!hasQuery()) {
      throw new IllegalStateException("pattern not set");
    }
    if (isResultValid()) {
      var res = lastResults;
      if (res == null || res.size() == 0) {
        return false;
      }
      var left = editor.getCursor().getLeft();
      var index = res.lowerBoundByFirst(left);
      if (index == res.size() || IntPair.getFirst(res.get(index)) >= index) {
        index--;
      }
      if (index < 0) {
        index = res.size() - 1;
      }
      if (index >= 0 && index < res.size()) {
        var data = res.get(index);
        var end = IntPair.getSecond(data);
        var pos1 = editor.getText().getIndexer().getCharPosition(IntPair.getFirst(data));
        var pos2 = editor.getText().getIndexer().getCharPosition(end);
        editor.setSelectionRegion(
            pos1.line, pos1.column, pos2.line, pos2.column, SelectionChangeEvent.CAUSE_SEARCH);
        return true;
      }
    }
    ToastUtils.showShort("Text not found", ToastUtils.TYPE_ERROR);
    return false;
  }

  @Override
  public boolean gotoNext() {
    if (!hasQuery()) {
      throw new IllegalStateException("pattern not set");
    }
    if (isResultValid()) {
      var res = lastResults;
      if (res == null) {
        return false;
      }
      var right = editor.getCursor().getRight();
      var index = res.lowerBoundByFirst(right);
      if (index == res.size()) {
        index = 0;
      }
      if (index < res.size()) {
        var data = res.get(index);
        var start = IntPair.getFirst(data);
        var pos1 = editor.getText().getIndexer().getCharPosition(start);
        var pos2 = editor.getText().getIndexer().getCharPosition(IntPair.getSecond(data));
        editor.setSelectionRegion(
            pos1.line, pos1.column, pos2.line, pos2.column, SelectionChangeEvent.CAUSE_SEARCH);
        return true;
      }
    }
    ToastUtils.showShort("Text not found", ToastUtils.TYPE_ERROR);
    return false;
  }

  @Override
  public void replaceAll(String replacement, Runnable whenSucceeded) {
    if (!editor.isEditable()) {
      return;
    }
    if (!hasQuery()) {
      throw new IllegalStateException("pattern not set");
    }
    if (!isResultValid()) {
      ToastUtils.showShort("Editor is still preparing…", ToastUtils.TYPE_ERROR);
      return;
    }
    var context = editor.getContext();

    final var dialog =
        DialogUtils.newProgressDialog(
                context,
                context.getString(R.string.replace_all),
                context.getString(R.string.replacing_texts))
            .create();
    dialog.setCancelable(false);
    dialog.show();

    final var res = lastResults;
    TaskExecutor.executeAsyncProvideError(
        () -> {
          var sb = editor.getText().toStringBuilder();
          int newLength = replacement.length();
          int delta = 0;
          for (int i = 0; i < res.size(); i++) {
            var region = res.get(i);
            var start = IntPair.getFirst(region);
            var end = IntPair.getSecond(region);
            var oldLength = end - start;
            sb.replace(start + delta, end + delta, replacement);
            delta += newLength - oldLength;
          }
          editor.postInLifecycle(
              () -> {
                var pos = editor.getCursor().left();
                editor
                    .getText()
                    .replace(
                        0,
                        0,
                        editor.getLineCount() - 1,
                        editor.getText().getColumnCount(editor.getLineCount() - 1),
                        sb);
                editor.setSelectionAround(pos.line, pos.column);
              });
          return whenSucceeded;
        },
        (result, error) -> {
          dialog.dismiss();
          if (error != null) {
            ToastUtils.showShort("Replace failed:" + error, ToastUtils.TYPE_ERROR);
            return;
          }
          if (result != null) {
            result.run();
          }
        });
  }
}
