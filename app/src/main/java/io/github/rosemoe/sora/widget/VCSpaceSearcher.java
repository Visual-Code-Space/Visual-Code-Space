package io.github.rosemoe.sora.widget;

import com.raredev.vcspace.R;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.util.DialogUtils;
import com.raredev.vcspace.util.ToastUtils;
import io.github.rosemoe.sora.util.IntPair;

public class VCSpaceSearcher extends EditorSearcher {

  private CodeEditor editor;

  public VCSpaceSearcher(CodeEditor editor) {
    super(editor);
    this.editor = editor;
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
