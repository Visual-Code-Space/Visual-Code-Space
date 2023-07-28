package com.raredev.vcspace.plugin;

public class Plugin {

  private String name;
  private String version;
  private String creator;

  public CompletionPlugin completion;

  public Plugin(String name, String version, String creator) {
    this.name = name;
    this.version = version;
    this.creator = creator;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return this.version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getCreator() {
    return this.creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public CompletionPlugin getCompletion() {
    return this.completion;
  }

  public void setCompletion(CompletionPlugin completion) {
    this.completion = completion;
  }
}
