package com.raredev.vcspace.plugin;

public class SnippetPlugin {

  private String languageScope;
  private String snippetFilePath;

  public SnippetPlugin(String languageScope, String snippetFilePath) {
    this.languageScope = languageScope;
    this.snippetFilePath = snippetFilePath;
  }

  public String getLanguageScope() {
    return this.languageScope;
  }

  public void setLanguageScope(String languageScope) {
    this.languageScope = languageScope;
  }

  public String getSnippetFilePath() {
    return this.snippetFilePath;
  }

  public void setSnippetFilePath(String snippetFilePath) {
    this.snippetFilePath = snippetFilePath;
  }
}
