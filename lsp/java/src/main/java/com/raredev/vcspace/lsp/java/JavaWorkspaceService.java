package com.raredev.vcspace.lsp.java;

import com.raredev.vcspace.util.ILogger;
import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.RenameFilesParams;
import org.eclipse.lsp4j.services.WorkspaceService;

public class JavaWorkspaceService implements WorkspaceService {
  private static final String TAG = JavaWorkspaceService.class.getSimpleName();

  private JavaLanguageServer languageServer;

  public JavaWorkspaceService(JavaLanguageServer languageServer) {
    this.languageServer = languageServer;
  }

  @Override
  public void didChangeConfiguration(DidChangeConfigurationParams didChangeConfigurationParams) {
    ILogger.info(TAG, "Operation 'workspace/didChangeConfiguration' Ack");
  }

  @Override
  public void didChangeWatchedFiles(DidChangeWatchedFilesParams didChangeWatchedFilesParams) {
    ILogger.info(TAG, "Operation 'workspace/didChangeWatchedFiles' Ack");
  }

  @Override
  public void didRenameFiles(RenameFilesParams params) {
    ILogger.info(TAG, "Operation 'workspace/didRenameFiles' Ack");
  }
}
