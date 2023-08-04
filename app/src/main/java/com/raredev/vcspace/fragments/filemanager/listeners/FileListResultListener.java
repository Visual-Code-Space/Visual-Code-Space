package com.raredev.vcspace.fragments.filemanager.listeners;

import com.raredev.vcspace.fragments.filemanager.models.FileModel;

public interface FileListResultListener {
  void onResult(FileModel[] result);
}
