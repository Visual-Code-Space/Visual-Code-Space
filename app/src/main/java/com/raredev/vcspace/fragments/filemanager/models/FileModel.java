package com.raredev.vcspace.fragments.filemanager.models;

import com.raredev.vcspace.R;
import java.io.File;

public class FileModel {

  private String path;
  private String name;

  private boolean isFile;

  public FileModel(String path, String name, boolean isFile) {
    this.path = path;
    this.name = name;
    this.isFile = isFile;
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

  public boolean isFile() {
    return this.isFile;
  }

  public int getIcon() {
    if (isFile) {
      return getIconForFileName(name);
    }
    return R.drawable.ic_folder;
  }

  private int getIconForFileName(String fileName) {
    int icon = R.drawable.ic_file;

    for (String extension : TEXT_FILE) {
      if (fileName.endsWith(extension)) {
        icon = R.drawable.file_document_outline;
      }
    }
    return icon;
  }
  
  public File toFile() {
    return new File(path);
  }
  
  public static FileModel fileToFileModel(File file) {
    return new FileModel(file.getAbsolutePath(), file.getName(), file.isFile());
  }

  private static String[] TEXT_FILE = {
    ".txt", ".js", ".ji", ".json", ".java", ".kt", ".kts", ".md", ".lua", ".cs", ".css", ".c", ".cpp", ".h",
    ".hpp", ".py", ".htm", ".html", ".xhtml", ".xht", ".xaml", ".xdf", ".xmpp", ".xml", ".sh",
    ".ksh", ".bsh", ".csh", ".tcsh", ".zsh", ".bash", ".groovy", ".gvy", ".gy", ".gsh", ".php",
    ".php3", ".php4", ".php5", ".phps", ".phtml", ".ts", ".log", ".yaml", ".yml", ".toml",
    ".gradle", ".mts", ".cts", ".smali",
  };
}
