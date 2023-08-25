package com.raredev.vcspace.models;

import java.io.File;

public class UserSnippetModel {
  private String languageName;
  private File snippetFile;

  public UserSnippetModel(String languageName, File snippetFile) {
    this.languageName = languageName;
    this.snippetFile = snippetFile;
  }

  public String getLanguageName() {
    return this.languageName;
  }

  public void setLanguageName(String languageName) {
    this.languageName = languageName;
  }

  public File getSnippetFile() {
    return this.snippetFile;
  }

  public void setSnippetFile(File snippetFile) {
    this.snippetFile = snippetFile;
  }
}
