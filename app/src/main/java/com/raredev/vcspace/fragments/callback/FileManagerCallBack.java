package com.raredev.vcspace.fragments.callback;

import java.io.File;
import java.util.*;

public interface FileManagerCallBack {

  void onFileClicked(File file);

  void onFileRenamed(File oldFile, File newFile);

  void onFileDeleted();
}
