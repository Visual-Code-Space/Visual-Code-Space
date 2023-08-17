package com.raredev.vcspace.models;

import com.raredev.vcspace.res.R;
import java.io.File;

public class FileModel {

  private String path, name;
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

    for (String extension : TEXT_FILES) {
      if (fileName.endsWith(extension)) {
        icon = R.drawable.file_document_outline;
      }
    }
    return icon;
  }

  public void listFiles(FileListResultListener listener) {
    if (listener == null) throw new NullPointerException();

    File[] files = toFile().listFiles();
    if (files.length == 0) {
      listener.onResult(new FileModel[0]);
      return;
    }
    FileModel[] localFiles = new FileModel[files.length];
    for (int i = 0; i < files.length; i++) {
      localFiles[i] = fileToFileModel(files[i]);
    }

    listener.onResult(localFiles);
  }

  public File toFile() {
    return new File(path);
  }

  public static FileModel fileToFileModel(File file) {
    return new FileModel(file.getAbsolutePath(), file.getName(), file.isFile());
  }

  public static String[] TEXT_FILES = {
    ".bat", ".txt", ".js", ".ji", ".json", ".java", ".kt", ".kts", ".md", ".lua", ".cs", ".css",
    ".c", ".cpp", ".h", ".hpp", ".py", ".htm", ".html", ".xhtml", ".xht", ".xaml", ".xdf", ".xmpp",
    ".xml", ".sh", ".ksh", ".bsh", ".csh", ".tcsh", ".zsh", ".bash", ".groovy", ".gvy", ".gy",
    ".gsh", ".php", ".php3", ".php4", ".php5", ".phps", ".phtml", ".ts", ".log", ".yaml", ".yml",
    ".toml", ".gradle", ".mts", ".cts", ".smali",
  };

  public interface FileListResultListener {
    void onResult(FileModel[] result);
  }
}
