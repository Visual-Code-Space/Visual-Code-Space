package com.raredev.vcspace.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.lsp.client.connection.SocketStreamConnectionProvider;
import io.github.rosemoe.sora.lsp.client.connection.StreamConnectionProvider;
import io.github.rosemoe.sora.lsp.client.languageserver.serverdefinition.CustomLanguageServerDefinition;
import io.github.rosemoe.sora.lsp.client.languageserver.wrapper.EventHandler;
import io.github.rosemoe.sora.lsp.editor.LspEditor;
import io.github.rosemoe.sora.lsp.editor.LspEditorManager;
import io.github.rosemoe.sora.lsp.utils.URIUtils;
import io.github.rosemoe.sora.widget.CodeEditor;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import org.eclipse.lsp4j.DidChangeWorkspaceFoldersParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.lsp4j.WorkspaceFoldersChangeEvent;
import org.eclipse.lsp4j.services.LanguageServer;

public class LspConnector {
  private static final String TAG = "LspConnector";

  private LspEditor lspEditor;
  private File file;
  private static CodeEditor mCodeEditor;
  private static Class<?> mService;
  private static Context mContext;

  public LspConnector(File file) {
    this.file = file;
  }

  public void connectToLanguageServer(
      CodeEditor editor, Language wrapperLanguage, String fileExt, Class<?> service)
      throws IOException {
    int port = Utils.randomPort();
    mCodeEditor = editor;
    mService = service;
    mContext = editor.getContext();
    Executors.newSingleThreadExecutor()
        .execute(
            () -> {
              ThreadUtils.runOnUiThread(
                  () -> {
                    ToastUtils.showShort("Starting Language Server...");
                    editor.setEditable(false);
                  });
              ILogger.debug(
                  TAG, "Starting Language Server... for file: ".concat(file.getAbsolutePath()));

              Intent intent = new Intent(mContext, service);
              intent.putExtra("port", port);
              mContext.startService(intent);

              CustomLanguageServerDefinition serverDefinition =
                  new CustomLanguageServerDefinition(
                      fileExt,
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

              lspEditor =
                  LspEditorManager.getOrCreateEditorManager(file.getAbsolutePath())
                      .createEditor(URIUtils.fileToURI(file).toString(), serverDefinition);

              ThreadUtils.runOnUiThread(
                  () -> {
                    lspEditor.setWrapperLanguage(wrapperLanguage);
                    lspEditor.setEditor(editor);
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
                                editor.setEditable(true);
                                ToastUtils.showShort("Initialized Language server");
                              });
                          ILogger.debug(
                              TAG,
                              "Initialized Language server for file: "
                                  .concat(file.getAbsolutePath()));
                        } catch (Exception e) {
                          ThreadUtils.runOnUiThread(
                              () -> {
                                ToastUtils.showShort("Unable to connect language server");
                                editor.setEditable(true);
                              });
                          ILogger.error(TAG, Log.getStackTraceString(e));
                        }
                      })
                  .start();
            });
  }

  public static void shutdown() {
    if (mCodeEditor == null || mService == null) {
      return;
    }
    mCodeEditor.release();
    LspEditorManager.closeAllManager();
    mContext.stopService(new Intent(mContext, mService));
    ILogger.debug(TAG, "Shutting down Language server...");
  }

  public static void shutdown(CodeEditor editor, File file) {
    if (editor == null || mService == null || file == null) {
      return;
    }
    editor.release();
    LspEditorManager.getOrCreateEditorManager(file.getAbsolutePath())
        .getEditor(URIUtils.fileToURI(file).toString())
        .close();
    editor.getContext().stopService(new Intent(editor.getContext(), mService));
    ILogger.debug(TAG, "Shutting down Language server for file: " + file.getAbsolutePath());
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
