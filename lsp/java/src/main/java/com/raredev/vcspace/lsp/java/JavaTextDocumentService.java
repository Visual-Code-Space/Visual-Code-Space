package com.raredev.vcspace.lsp.java;

import com.raredev.vcspace.util.ILogger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SignatureHelpParams;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
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

  @Override
  public CompletableFuture<SignatureHelp> signatureHelp(SignatureHelpParams params) {
    return CompletableFutures.computeAsync(
        (cancelChecker) -> {
          String uri = params.getTextDocument().getUri();
          Optional<Path> sigFilePath = getPathFromURI(uri);

          // Note: If the path does not exist, then return early and ignore
          if (sigFilePath.isEmpty()) {
            return new SignatureHelp();
          }
          return null;
        });
  }

  /**
   * Get the path from given string URI. Even if the given URI's scheme is expr or bala, we convert
   * it to file scheme and provide a valid Path.
   *
   * @param fileUri file uri
   * @return {@link Optional} Path from the URI
   */
  public static Optional<Path> getPathFromURI(String fileUri) {
    URI uri = URI.create(fileUri);
    String scheme = uri.getScheme();
    try {
      URI converted =
          new URI(
              scheme,
              uri.getUserInfo(),
              uri.getHost(),
              uri.getPort(),
              uri.getPath(),
              uri.getQuery(),
              uri.getFragment());
      return Optional.of(Paths.get(converted));
    } catch (URISyntaxException e) {
      return Optional.empty();
    }
  }
}
