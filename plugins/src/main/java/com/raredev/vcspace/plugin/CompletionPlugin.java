package com.raredev.vcspace.plugin;

public class CompletionPlugin {

  private String languageScope;
  private String[] completionFiles;

  public CompletionPlugin(String languageScope, String[] completionFiles) {
    this.languageScope = languageScope;
    this.completionFiles = completionFiles;
  }

  public String getLanguageScope() {
    return this.languageScope;
  }

  public void setLanguageScope(String languageScope) {
    this.languageScope = languageScope;
  }

  public String[] getCompletionFiles() {
    return this.completionFiles;
  }

  public void setCompletionFiles(String[] completionFiles) {
    this.completionFiles = completionFiles;
  }
}
