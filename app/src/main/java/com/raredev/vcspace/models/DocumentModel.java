package com.raredev.vcspace.models;

import com.raredev.vcspace.fragments.filemanager.models.FileModel;
import java.io.File;

public class DocumentModel extends FileModel {

  private byte[] content;
  private boolean modified;
  private int positionLine, positionColumn;

  public DocumentModel(String path, String name) {
    this(path, name, null, false, 0, 0);
  }

  public DocumentModel(
      String path,
      String name,
      byte[] content,
      boolean modified,
      int positionLine,
      int positionColumn) {
    super(path, name, true);
    this.content = content;
    this.modified = modified;
    this.positionLine = positionLine;
    this.positionColumn = positionColumn;
  }

  public byte[] getContent() {
    return this.content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

  public boolean isModified() {
    return this.modified;
  }
  
  public void setModified(boolean modified) {
    this.modified = modified;
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

  public static DocumentModel fileToDocument(File file) {
    return new DocumentModel(file.getAbsolutePath(), file.getName());
  }

  public static DocumentModel fileModelToDocument(FileModel file) {
    return new DocumentModel(file.getPath(), file.getName());
  }
}
