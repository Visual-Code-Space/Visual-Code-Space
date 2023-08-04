package com.raredev.vcspace.models;

import com.raredev.vcspace.fragments.filemanager.models.FileModel;
import java.io.File;

public class DocumentModel extends FileModel {

  private String content;
  private boolean modified;
  private int positionLine, positionColumn;

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
    super(path, name, true);
    this.content = content;
    this.modified = modified;
    this.positionLine = positionLine;
    this.positionColumn = positionColumn;
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

  public static FileModel fileToDocument(File file) {
    return new DocumentModel(file.getAbsolutePath(), file.getName());
  }

  public static DocumentModel fileModelToDocument(FileModel file) {
    return new DocumentModel(file.getPath(), file.getName());
  }
}
