package com.raredev.vcspace.fragments.filemanager.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.raredev.vcspace.fragments.filemanager.models.DirectoryModel;
import com.raredev.vcspace.fragments.filemanager.models.FileModel;
import com.raredev.vcspace.util.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListViewModel extends ViewModel {

  private MutableLiveData<List<FileModel>> files = new MutableLiveData<>(new ArrayList<>());

  private MutableLiveData<List<DirectoryModel>> directories =
      new MutableLiveData<>(new ArrayList<>());
  private MutableLiveData<DirectoryModel> currentDir =
      new MutableLiveData<>(DirectoryModel.fileToDirectoryModel(FileUtil.getDeviceDirectory()));

  public LiveData<List<FileModel>> getFilesLiveData() {
    return files;
  }

  public List<FileModel> getFiles() {
    return files.getValue();
  }

  public LiveData<List<DirectoryModel>> getDirectoriesLiveData() {
    return directories;
  }

  public List<DirectoryModel> getDirectories() {
    return directories.getValue();
  }

  public LiveData<DirectoryModel> getCurrentDirLiveData() {
    return currentDir;
  }

  public DirectoryModel getCurrentDir() {
    return currentDir.getValue();
  }

  public File getCurrentDirFile() {
    return currentDir.getValue().toFile();
  }

  public void setFiles(List<FileModel> files) {
    this.files.setValue(files);
  }

  public void setCurrentDir(FileModel dir) {
    setCurrentDir(DirectoryModel.fileModelToDirectoryModel(dir));
  }

  public void setCurrentDir(DirectoryModel dir) {
    currentDir.setValue(dir);
  }

  public void setDirectories(List<DirectoryModel> dirs) {
    directories.setValue(dirs);
  }

  public void openDirectory(DirectoryModel dir) {
    List<DirectoryModel> dirs = getDirectories();
    int index = findIndexOfDir(dir);
    if (index != -1) {
      return;
    }
    dirs.add(dir);
    setDirectories(dirs);
  }

  public void removeAllDirectoriesAfter(int index) {
    List<DirectoryModel> dirs = getDirectories();
    int temp = index + 1;
    if (temp > dirs.size()) {
      return;
    }
    dirs.subList(temp, dirs.size()).clear();
    setDirectories(dirs);
  }

  public int findIndexOfDir(DirectoryModel dir) {
    for (int i = 0; i < getDirectories().size(); i++) {
      DirectoryModel temp = getDirectories().get(i);
      if (temp.getPath().equals(dir.getPath())) {
        return i;
      }
    }
    return -1;
  }

  public void clearFiles() {
    List<FileModel> files = getFiles();
    files.clear();
    setFiles(files);
  }
}
