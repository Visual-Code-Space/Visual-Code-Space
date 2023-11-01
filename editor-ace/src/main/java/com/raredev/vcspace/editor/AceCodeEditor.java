package com.raredev.vcspace.editor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.WebView;
import androidx.javascriptengine.JavaScriptSandbox;
import com.google.common.util.concurrent.ListenableFuture;
import com.raredev.vcspace.editor.ace.OnEditorLoadedListener;
import com.raredev.vcspace.editor.ace.OnJsReturnValueCallback;
import com.raredev.vcspace.editor.ace.VCSpaceEditorLanguage;
import com.raredev.vcspace.editor.ace.VCSpaceEditorTheme;
import com.raredev.vcspace.editor.ace.VCSpaceWebChromeClient;
import com.raredev.vcspace.editor.ace.VCSpaceWebInterface;
import com.raredev.vcspace.editor.ace.VCSpaceWebViewClient;
import com.raredev.vcspace.editor.ace.options.EditorOptions;
import java.io.File;

public class AceCodeEditor extends WebView {

  private float x, y;
  private ListenableFuture<JavaScriptSandbox> jsSandbox;

  public OnEditorLoadedListener onEditorLoadedListener;
  private VCSpaceWebInterface webInterface;
  private EditorOptions options;

  private boolean modified;
  private File file;

  public AceCodeEditor(Context context) {
    this(context, null);
  }

  public AceCodeEditor(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public AceCodeEditor(Context context, AttributeSet attrs, int defStyleAttr) {
    this(context, attrs, defStyleAttr, 0);
  }

  public AceCodeEditor(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    jsSandbox = JavaScriptSandbox.createConnectedInstanceAsync(context.getApplicationContext());
    options = EditorOptions.getInstance();

    // setOnTouchListener(onTouchListener());
    addJavascriptInterface(webInterface = new VCSpaceWebInterface(this), "VCSpace");
    setWebChromeClient(new VCSpaceWebChromeClient());
    setWebViewClient(new VCSpaceWebViewClient(this));

    getSettings().setJavaScriptEnabled(true);
    loadUrl("file:///android_asset/ace-editor/editor.html");
  }

  public void release() {
    try {
      jsSandbox.get().unbindService();
      jsSandbox.get().close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    webInterface = null;
    destroy();
  }

  public void loadOptions() {
    var sb = new StringBuilder();
    sb.append("editor.setOptions({")
        .append("highlightActiveLine: ")
        .append(options.highlightActiveLine)
        .append(",")
        .append("highlightSelectedWord: ")
        .append(options.highlightSelectedWord)
        .append(",")
        .append("readOnly: ")
        .append(options.readOnly)
        .append(",")
        .append("enableAutoIndent: ")
        .append(options.enableAutoIndent)
        .append(",")
        .append("enableBasicAutocompletion: ")
        .append(options.enableBasicAutocompletion)
        .append(",")
        .append("enableLiveAutocompletion: ")
        .append(options.enableLiveAutocompletion)
        .append(",")
        .append("enableSnippets: ")
        .append(options.enableSnippets)
        .append(",")
        .append("hScrollBarAlwaysVisible: ")
        .append(options.hScrollBarAlwaysVisible)
        .append(",")
        .append("vScrollBarAlwaysVisible: ")
        .append(options.vScrollBarAlwaysVisible)
        .append(",")
        .append("highlightGutterLine: ")
        .append(options.highlightGutterLine)
        .append(",")
        .append("animatedScroll: ")
        .append(options.animatedScroll)
        .append(",")
        .append("showInvisibles: ")
        .append(options.showInvisibles)
        .append(",")
        .append("showPrintMargin: ")
        .append(options.showPrintMargin)
        .append(",")
        .append("fadeFoldWidgets: ")
        .append(options.fadeFoldWidgets)
        .append(",")
        .append("showFoldWidgets: ")
        .append(options.showFoldWidgets)
        .append(",")
        .append("showLineNumbers: ")
        .append(options.showLineNumbers)
        .append(",")
        .append("showGutter: ")
        .append(options.showGutter)
        .append(",")
        .append("displayIndentGuides: ")
        .append(options.displayIndentGuides)
        .append(",")
        .append("fixedWidthGutter: ")
        .append(options.fixedWidthGutter)
        .append(",")
        .append("useSvgGutterIcons: ")
        .append(options.useSvgGutterIcons)
        .append(",")
        .append("useSoftTabs: ")
        .append(options.useSoftTabs)
        .append(",")
        .append("enableEmmet: ")
        .append(options.enableEmmet)
        .append(",")
        .append("mergeUndoDeltas: ")
        .append(
            options.mergeUndoDeltas.equals(EditorOptions.MergeUndoDeltas.NEVER)
                ? false
                : options.mergeUndoDeltas.equals(EditorOptions.MergeUndoDeltas.TIMED)
                    ? true
                    : "'always'")
        .append(",")
        .append("selectionStyle: ")
        .append("\"" + options.selectionStyle.name().toLowerCase() + "\"")
        .append(",")
        .append("cursorStyle: ")
        .append("\"" + options.cursorStyle.name().toLowerCase() + "\"")
        .append(",")
        .append("theme: ")
        .append("\"ace/theme/" + options.theme.name().toLowerCase() + "\"")
        .append(",")
        .append("mode: ")
        .append("\"ace/mode/" + options.language.name().toLowerCase() + "\"")
        .append(",")
        .append("newLineMode: ")
        .append("\"" + options.newLineMode.name().toLowerCase() + "\"")
        .append(",")
        .append("foldStyle: ")
        .append("\"" + options.foldStyle.name().toLowerCase() + "\"")
        .append(",")
        .append("firstLineNumber: ")
        .append(options.firstLineNumber)
        .append(",")
        .append("fontSize: ")
        .append(options.fontSize)
        .append(",")
        .append("printMarginColumn: ")
        .append(options.printMarginColumn)
        .append(",")
        .append("tabSize: ")
        .append(options.tabSize)
        .append(",")
        .append("maxLines: ")
        .append(options.maxLines == 0 ? "undefined" : options.maxLines)
        .append(",")
        .append("minLines: ")
        .append(options.minLines == 0 ? "undefined" : options.minLines)
        .append(",")
        .append("scrollPastEnd: ")
        .append(options.scrollPastEnd)
        .append("})");

    loadJs(sb.toString());
  }

  public void setTheme(VCSpaceEditorTheme theme) {
    var sb = new StringBuilder();
    sb.append("editor.setTheme('ace/theme/").append(theme.name().toLowerCase()).append("')");
    loadJs(sb.toString());
  }

  public void setLanguage(VCSpaceEditorLanguage language) {
    var sb = new StringBuilder();
    sb.append("editor.session.setMode('ace/mode/")
        .append(language.name().toLowerCase())
        .append("')");
    loadJs(sb.toString());
  }

  public boolean hasUndo() {
    return webInterface.hasUndo;
  }

  public boolean hasRedo() {
    return webInterface.hasRedo;
  }

  public void setText(String text) {
    String escapedText = text.replace("\n", "\\n").replace("\r", "\\r").replace("'", "\\'");
    loadJs("editor.session.setValue('" + escapedText + "')");
  }

  public String getText() {
    return webInterface.value;
  }

  public void setFontSize(int fontSizeInPx) {
    loadJs("editor.setFontSize(" + fontSizeInPx + ")");
  }

  public void getFontSize(OnJsReturnValueCallback callback) {
    loadJs("editor.getFontSize()", callback);
  }

  public void setShowPrintMargin(boolean showPrintMargin) {
    loadJs("editor.setShowPrintMargin(" + showPrintMargin + ");");
  }

  public void setHighlightActiveLine(boolean highlightActiveLine) {
    loadJs("editor.setHighlightActiveLine(" + highlightActiveLine + ");");
  }

  public void setReadOnly(boolean readOnly) {
    loadJs("editor.setReadOnly(" + readOnly + ");");
  }

  public void setUseWrapMode(boolean useWrapMode) {
    loadJs("editor.getSession().setUseWrapMode(" + useWrapMode + ");");
  }

  public void setUseSoftTabs(boolean useSoftTabs) {
    loadJs("editor.getSession().setUseSoftTabs(" + useSoftTabs + ");");
  }

  public void setTabSize(int tabSize) {
    loadJs("editor.getSession().setTabSize(" + tabSize + ");");
  }

  public void gotoLine(int lineNumber) {
    loadJs("editor.gotoLine(" + lineNumber + ");");
  }

  public void getTotalLines(OnJsReturnValueCallback callback) {
    loadJs("editor.session.getLength()", callback);
  }

  public void insert(String text) {
    loadJs("editor.insert(\"" + text + "\");");
  }

  public void setLanguageFromFile(String filePath) {
    loadJs("setLanguageFromFile(\"" + filePath + "\");");
  }

  public void moveCursorTo(int row, int column) {
    loadJs("editor.session.getSelection().moveCursorTo(" + row + ", " + column + ");");
  }

  public void undo() {
    loadJs("editor.session.getUndoManager().undo()");
  }

  public void redo() {
    loadJs("editor.session.getUndoManager().redo()");
  }

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    return super.dispatchKeyEvent(event);
  }

  @Override
  public InputConnection onCreateInputConnection(EditorInfo outAttr) {
    // this is needed for #dispatchKeyEvent() to be notified.
    return new BaseInputConnection(this, false);
  }

  public void loadJs(String js, OnJsReturnValueCallback callback) {
    evaluateJavascript(
        js,
        (value) -> {
          if (callback != null) callback.onReturnValue(value);
        });
  }

  public void loadJs(String js) {
    loadJs(js, null);
  }

  public void setOnEditorLoadedListener(OnEditorLoadedListener onEditorLoadedListener) {
    this.onEditorLoadedListener = onEditorLoadedListener;
  }

  public EditorOptions getOptions() {
    return this.options;
  }

  public void setOptions(EditorOptions options) {
    this.options = options;
    loadOptions();
  }

  public File getFile() {
    return this.file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public boolean isModified() {
    return this.modified;
  }

  public void setModified(boolean modified) {
    this.modified = modified;
  }
}
