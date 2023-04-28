package com.raredev.vcspace.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListViewModel extends ViewModel {

  private MutableLiveData<List<File>> files = new MutableLiveData<>(new ArrayList<>());
  private MutableLiveData<List<File>> selectedFiles = new MutableLiveData<>(new ArrayList<>());

  public LiveData<List<File>> getFilesLiveData() {
    return files;
  }
  
  public List<File> getFiles() {
    return files.getValue();
  }

  public LiveData<List<File>> getSelectedFilesLiveData() {
    return selectedFiles;
  }
  
  public List<File> getSelectedFiles() {
    return selectedFiles.getValue();
  }

  public void setFiles(List<File> files) {
    this.files.setValue(files);
  }

  public void selectFile(File file) {
    List<File> selectedFiles = getSelectedFiles();
    selectedFiles.add(file);

    this.selectedFiles.setValue(selectedFiles);
  }
  
  public void removeSelectFile(File file) {
    List<File> selectedFiles = getSelectedFiles();
    selectedFiles.remove(file);

    this.selectedFiles.setValue(selectedFiles);
  }

  public void clearFiles() {
    files.setValue(new ArrayList<>());
  }

  public void clearSelectedFiles() {
    selectedFiles.setValue(new ArrayList<>());
  }
}
