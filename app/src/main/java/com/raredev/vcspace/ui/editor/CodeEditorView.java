package com.raredev.vcspace.ui.editor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.core.content.res.ResourcesCompat;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.raredev.vcspace.services.JavaLanguageServerService;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.models.LanguageScope;
import com.raredev.vcspace.ui.editor.completion.CompletionItemAdapter;
import com.raredev.vcspace.ui.editor.completion.CustomCompletionLayout;
import com.raredev.vcspace.ui.language.html.HtmlLanguage;
import com.raredev.vcspace.ui.language.java.JavaLanguage;
import com.raredev.vcspace.ui.language.lua.LuaLanguage;
import com.raredev.vcspace.util.ILogger;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.Utils;
import io.github.rosemoe.sora.event.ContentChangeEvent;
import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTextMateColorScheme;
import io.github.rosemoe.sora.lsp.client.connection.SocketStreamConnectionProvider;
import io.github.rosemoe.sora.lsp.client.connection.StreamConnectionProvider;
import io.github.rosemoe.sora.lsp.client.languageserver.serverdefinition.CustomLanguageServerDefinition;
import io.github.rosemoe.sora.lsp.client.languageserver.wrapper.EventHandler;
import io.github.rosemoe.sora.lsp.editor.LspEditor;
import io.github.rosemoe.sora.lsp.editor.LspEditorManager;
import io.github.rosemoe.sora.lsp.utils.URIUtils;
import io.github.rosemoe.sora.text.LineSeparator;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import org.eclipse.lsp4j.DidChangeWorkspaceFoldersParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.lsp4j.WorkspaceFoldersChangeEvent;
import org.eclipse.lsp4j.services.LanguageServer;

public class CodeEditorView extends CodeEditor {
  private File file;
  private LspEditor lspEditor;

  public CodeEditorView(Context context, File file) {
    super(context);
    this.file = file;
    setHighlightCurrentBlock(true);
    getProps().autoCompletionOnComposing = true;
    setColorScheme(createScheme());
    setLineSeparator(LineSeparator.LF);

    CompletableFuture.runAsync(
        () -> {
          var content = FileUtil.readFile(file.getAbsolutePath());
          post(
              () -> {
                setText(content, null);
                Language lang = createLanguage();
                if (lang != null) {
                  setEditorLanguage(lang);
                }
              });
        });

    if (file.getAbsolutePath().endsWith(".java")) {
      try {
        connectToLanguageServer();
      } catch (IOException e) {
        ILogger.error("CodeEditorView", Log.getStackTraceString(e));
      }
    }

    final EditorTextActions textActions = new EditorTextActions(this);
    getComponent(EditorAutoCompletion.class).setLayout(new CustomCompletionLayout());
    getComponent(EditorAutoCompletion.class).setAdapter(new CompletionItemAdapter());
    replaceComponent(EditorTextActionWindow.class, textActions);

    configureEditor();
  }

  @Override
  public void release() {
    super.release();
    file = null;
  }

  private void configureEditor() {
    updateEditorFont();
    updateTextSize();
    updateTABSize();
    updateDeleteEmptyLineFast();
  }

  public void onSharedPreferenceChanged(String key) {
    switch (key) {
      case "pref_editortextsize":
        updateTextSize();
        break;
      case "pref_editortabsize":
        updateTABSize();
        break;
      case "pref_deleteemptylinefast":
        updateDeleteEmptyLineFast();
        break;
      case "pref_editorfont":
        updateEditorFont();
        break;
    }
  }

  public void subscribeContentChangeEvent(Runnable runnable) {
    subscribeEvent(
        ContentChangeEvent.class,
        (event, subscribe) -> {
          switch (event.getAction()) {
            case ContentChangeEvent.ACTION_INSERT:
            case ContentChangeEvent.ACTION_DELETE:
            case ContentChangeEvent.ACTION_SET_NEW_TEXT:
              new Handler(Looper.getMainLooper()).postDelayed(runnable, 10);
              break;
          }
        });
  }

  public File getFile() {
    return file;
  }

  public void save() {
    if (file != null && file.exists()) {
      FileUtil.writeFile(file.getAbsolutePath(), getText().toString());
    }
  }

  @Override
  public void undo() {
    if (canUndo()) super.undo();
  }

  @Override
  public void redo() {
    if (canRedo()) super.redo();
  }

  private EditorColorScheme createScheme() {
    try {
      return VCSpaceTextMateColorScheme.create(getContext());
    } catch (Exception e) {
      return null;
    }
  }

  private Language createLanguage() {
    try {
      final LanguageScope langScope = LanguageScope.Factory.forFile(file);

      switch (langScope) {
        case JAVA:
          return new JavaLanguage();
        case HTML:
          return new HtmlLanguage();
        case LUA:
          return new LuaLanguage();
      }

      return TextMateLanguage.create(langScope.getScope(), true);
    } catch (Exception e) {
      return new EmptyLanguage();
    }
  }

  private void updateTextSize() {
    int textSize = PreferencesUtils.getEditorTextSize();
    setTextSize(textSize);
  }

  private void updateTABSize() {
    int tabSize = PreferencesUtils.getEditorTABSize();
    setTabWidth(tabSize);
  }

  private void updateEditorFont() {
    setTypefaceText(ResourcesCompat.getFont(getContext(), PreferencesUtils.getSelectedFont()));
    setTypefaceLineNumber(
        ResourcesCompat.getFont(getContext(), PreferencesUtils.getSelectedFont()));
  }

  private void updateDeleteEmptyLineFast() {
    getProps().deleteEmptyLineFast = PreferencesUtils.useDeleteEmptyLineFast();
  }

  private void updateNonPrintablePaintingFlags() {
    /*binding.editor.setNonPrintablePaintingFlags(
    CodeEditor.FLAG_DRAW_WHITESPACE_LEADING
        | CodeEditor.FLAG_DRAW_WHITESPACE_INNER
        | CodeEditor.FLAG_DRAW_WHITESPACE_FOR_EMPTY_LINE);*/
  }

  private void connectToLanguageServer() throws IOException {
    int port = Utils.randomPort();
    Executors.newSingleThreadExecutor()
        .execute(
            () -> {
              ThreadUtils.runOnUiThread(
                  () -> {
                    ToastUtils.showShort("Starting Language Server...");
                    setEditable(false);
                  });

              Intent intent = new Intent(getContext(), JavaLanguageServerService.class);
              intent.putExtra("port", port);
              getContext().startService(intent);

              CustomLanguageServerDefinition serverDefinition =
                  new CustomLanguageServerDefinition(
                      ".java",
                      new CustomLanguageServerDefinition.ConnectProvider() {
                        @Override
                        public StreamConnectionProvider createConnectionProvider(
                            String workingDir) {
                          return new SocketStreamConnectionProvider(() -> port);
                        }
                      }) {

                    @Override
                    public EventHandler.EventListener getEventListener() {
                      return new EventListener();
                    }
                  };

              ThreadUtils.runOnUiThread(
                  () -> {
                    lspEditor =
                        LspEditorManager.getOrCreateEditorManager(file.getAbsolutePath())
                            .createEditor(URIUtils.fileToURI(file).toString(), serverDefinition);
                    Language wrapperLanguage = createLanguage();
                    lspEditor.setWrapperLanguage(wrapperLanguage);
                    lspEditor.setEditor(this);
                  });

              new Thread(
                      () -> {
                        try {
                          WorkspaceFoldersChangeEvent mWorkspaceFoldersChangeEvent =
                              new WorkspaceFoldersChangeEvent();
                          DidChangeWorkspaceFoldersParams mDidChangeWorkspaceFoldersParams =
                              new DidChangeWorkspaceFoldersParams();
                          mWorkspaceFoldersChangeEvent.setAdded(
                              Arrays.asList(new WorkspaceFolder(Uri.fromFile(file).toString())));
                          mDidChangeWorkspaceFoldersParams.setEvent(mWorkspaceFoldersChangeEvent);

                          lspEditor.connectWithTimeout();
                          lspEditor
                              .getRequestManager()
                              .didChangeWorkspaceFolders(mDidChangeWorkspaceFoldersParams);
                          ThreadUtils.runOnUiThread(
                              () -> {
                                setEditable(true);
                                ToastUtils.showShort("Initialized Language server");
                              });
                        } catch (Exception e) {
                          ThreadUtils.runOnUiThread(
                              () -> {
                                ToastUtils.showShort("Unable to connect language server");
                                setEditable(true);
                              });
                          ILogger.error("CodeEditorView", Log.getStackTraceString(e));
                        }
                      })
                  .start();
            });
  }

  class EventListener implements EventHandler.EventListener {
    @Override
    public void initialize(LanguageServer server, InitializeResult result) {
      //      runOnUiThread(
      //          () -> {
      //            rootMenu
      //                .findItem(R.id.code_format)
      //                .setEnabled(result.getCapabilities().getDocumentFormattingProvider() !=
      // null);
      //          });
    }
  }
}
