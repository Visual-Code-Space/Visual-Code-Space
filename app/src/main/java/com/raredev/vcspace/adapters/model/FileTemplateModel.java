package com.raredev.vcspace.adapters.model;

public class FileTemplateModel {
  private String fileExtension;
  private String template;

  public FileTemplateModel(String fileExtension, String template) {
    this.fileExtension = fileExtension;
    this.template = template;
  }

  public String getFileExtension() {
    return this.fileExtension;
  }

  public void setFileExtension(String fileExtension) {
    this.fileExtension = fileExtension;
  }

  public String getTemplateContent() {
    return this.template;
  }

  public void setTemplateContent(String template) {
    this.template = template;
  }
}
