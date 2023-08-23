package com.raredev.vcspace.events;

public class UpdateExecutePanelEvent extends PanelEvent {
  private String path;
  private String fileExtension;
  private String code;

  public UpdateExecutePanelEvent(String path, String fileExtension, String code) {
    this.path = path;
    this.fileExtension = fileExtension;
    this.code = code;
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

  public String getCode() {
    return this.code;
  }

  public void setCode(String code) {
    this.code = code;
  }
}
