package com.raredev.vcspace.events;

import java.io.File;

public class OnFileRenamedEvent {

  public File oldFile;
  public File newFile;

  public OnFileRenamedEvent(File oldFile, File newFile) {
    this.oldFile = oldFile;
    this.newFile = newFile;
  }
}
