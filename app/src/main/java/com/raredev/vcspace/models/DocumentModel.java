package com.raredev.vcspace.models;

import com.raredev.vcspace.fragments.filemanager.models.FileModel;
import java.io.File;

public class DocumentModel {

  private String path, name;
  private byte[] content;
  private boolean modified, pinned;
  private int positionLine, positionColumn;

  public DocumentModel(String path, String name) {
    this(path, name, null, false, false, 0, 0);
  }

  public DocumentModel(
      String path,
      String name,
      byte[] content,
      boolean modified,
      boolean pinned,
      int positionLine,
      int positionColumn) {
    this.path = path;
    this.name = name;
    this.content = content;
    this.modified = modified;
    this.pinned = pinned;
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

  public boolean isPinned() {
    return this.pinned;
  }

  public void setPinned(boolean pinned) {
    this.pinned = pinned;
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

  public static DocumentModel fileToDocument(File file) {
    return new DocumentModel(file.getAbsolutePath(), file.getName());
  }

  public static DocumentModel fileModelToDocument(FileModel file) {
    return new DocumentModel(file.getPath(), file.getName());
  }
}
