package com.raredev.vcspace.fragments.filemanager.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.raredev.vcspace.fragments.filemanager.models.FileModel;
import com.raredev.vcspace.util.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListViewModel extends ViewModel {

  private MutableLiveData<List<FileModel>> files = new MutableLiveData<>(new ArrayList<>());

  private MutableLiveData<FileModel> currentDir =
      new MutableLiveData<>(FileModel.fileToFileModel(FileUtil.getDeviceDirectory()));

  public LiveData<List<FileModel>> getFilesLiveData() {
    return files;
  }

  public List<FileModel> getFiles() {
    return files.getValue();
  }

  public LiveData<FileModel> getCurrentDirLiveData() {
    return currentDir;
  }

  public FileModel getCurrentDir() {
    return currentDir.getValue();
  }

  public File getCurrentDirFile() {
    return currentDir.getValue().toFile();
  }

  public void setFiles(List<FileModel> files) {
    this.files.setValue(files);
  }

  public void setCurrentDir(FileModel dir) {
    currentDir.setValue(dir);
  }

  public void clearFiles() {
    List<FileModel> files = getFiles();
    files.clear();
    setFiles(files);
  }
}
