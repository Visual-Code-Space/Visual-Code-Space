package com.raredev.vcspace.events;

public class UpdateExecutePanelEvent extends PanelEvent {
  private String path;
  private String fileExtension;

  public UpdateExecutePanelEvent(String path, String fileExtension) {
    this.path = path;
    this.fileExtension = fileExtension;
  }

  public String getPath() {
    return this.path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getFileExtension() {
    return this.fileExtension;
  }

  public void setFileExtension(String fileExtension) {
    this.fileExtension = fileExtension;
  }
}
