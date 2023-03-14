package com.raredev.vcspace.events;

import java.io.File;

public class FileEvent {
  private File file;
  
  public FileEvent(File file) {
    this.file = file;
  }
  
  public File getFile() {
    return file;
  }
}
