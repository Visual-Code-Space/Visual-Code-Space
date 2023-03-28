package com.raredev.vcspace.ui.language.java.lsp;

import com.raredev.vcspace.util.ILogger;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JavaTextDocumentService implements TextDocumentService {
  private static final String TAG = JavaTextDocumentService.class.getSimpleName();

  private JavaLanguageServer languageServer;

  public JavaTextDocumentService(JavaLanguageServer languageServer) {
    this.languageServer = languageServer;
  }

  @Override
  public void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
    ILogger.info(
        TAG,
        "Operation '"
            + "text/didOpen"
            + "' {fileUri: '"
            + didOpenTextDocumentParams.getTextDocument().getUri()
            + "'} opened");
  }

  @Override
  public void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {
    ILogger.info(
        TAG,
        "Operation '"
            + "text/didChange"
            + "' {fileUri: '"
            + didChangeTextDocumentParams.getTextDocument().getUri()
            + "'} Changed");
  }

  @Override
  public void didClose(DidCloseTextDocumentParams didCloseTextDocumentParams) {
    ILogger.info(
        TAG,
        "Operation '"
            + "text/didClose"
            + "' {fileUri: '"
            + didCloseTextDocumentParams.getTextDocument().getUri()
            + "'} Closed");
  }

  @Override
  public void didSave(DidSaveTextDocumentParams didSaveTextDocumentParams) {
    ILogger.info(
        TAG,
        "Operation '"
            + "text/didSave"
            + "' {fileUri: '"
            + didSaveTextDocumentParams.getTextDocument().getUri()
            + "'} Saved");
  }

  @Override
  public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
      CompletionParams position) {
    return CompletableFuture.supplyAsync(
        () -> {
          ILogger.info(TAG, "Operation '" + "text/completion");
          CompletionItem completionItem = new CompletionItem();
          completionItem.setLabel("Test completion item");
          completionItem.setInsertText("Test");
          completionItem.setDetail("Snippet");
          completionItem.setKind(CompletionItemKind.Snippet);
          return Either.forLeft(Arrays.asList(completionItem));
        });
  }
}
