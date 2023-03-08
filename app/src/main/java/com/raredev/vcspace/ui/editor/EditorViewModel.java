package com.raredev.vcspace.ui.editor;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditorViewModel extends ViewModel {
  private final MutableLiveData<Integer> currentPosition = new MutableLiveData<>(-1);

  private MutableLiveData<List<File>> mFiles = new MutableLiveData<>(new ArrayList<>());

  public int getCurrentPosition() {
    return currentPosition.getValue();
  }

  public void setCurrentPosition(int pos) {
    currentPosition.setValue(pos);
  }

  public LiveData<List<File>> getFiles() {
    return mFiles;
  }

  public File getCurrentFile() {
    return getFiles().getValue().get(currentPosition.getValue());
  }

  public void clear() {
    List<File> value = getFiles().getValue();

    value.clear();
    mFiles.setValue(value);
  }

  public void openFile(File file) {
    List<File> files = getFiles().getValue();

    files.add(file);
    mFiles.setValue(files);
  }

  public void removeFile(int index) {
    List<File> files = getFiles().getValue();

    files.remove(index);
    mFiles.setValue(files);
  }

  public boolean contains(Object obj) {
    return getFiles().getValue().contains(obj);
  }

  public int indexOf(Object obj) {
    return getFiles().getValue().indexOf(obj);
  }
}
