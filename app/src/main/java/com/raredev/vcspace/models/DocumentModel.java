package com.raredev.vcspace.models;

import com.raredev.vcspace.fragments.filemanager.models.FileModel;
import java.io.File;

public class DocumentModel {

  private String path;
  private String name;
  private String content;
  private boolean modified;
  private int positionLine;
  private int positionColumn;

  public DocumentModel(String path, String name) {
    this(path, name, null, false, 0, 0);
  }

  public DocumentModel(
      String path,
      String name,
      String content,
      boolean modified,
      int positionLine,
      int positionColumn) {
    this.path = path;
    this.name = name;
    this.content = content;
    this.modified = modified;
    this.positionLine = positionLine;
    this.positionColumn = positionColumn;
  }

  public String getPath() {
    return this.path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getContent() {
    return this.content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public boolean isModified() {
    return this.modified;
  }

  public void markModified() {
    this.modified = true;
  }

  public void markUnmodified() {
    this.modified = false;
  }

  public int getPositionLine() {
    return this.positionLine;
  }

  public void setPositionLine(int positionLine) {
    this.positionLine = positionLine;
  }

  public int getPositionColumn() {
    return this.positionColumn;
  }

  public void setPositionColumn(int positionColumn) {
    this.positionColumn = positionColumn;
  }

  public File toFile() {
    return new File(path);
  }

  public static DocumentModel fileModelToDocument(FileModel file) {
    return new DocumentModel(file.getPath(), file.getName());
  }
}
