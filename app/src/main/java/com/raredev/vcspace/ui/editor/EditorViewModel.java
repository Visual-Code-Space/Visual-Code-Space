package com.raredev.vcspace.ui.editor;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditorViewModel extends ViewModel {
  private final MutableLiveData<Boolean> mDrawerState = new MutableLiveData<>(false);
  private final MutableLiveData<Integer> currentPosition = new MutableLiveData<>(-1);

  private MutableLiveData<List<File>> mFiles;

  public LiveData<Boolean> getDrawerState() {
    return mDrawerState;
  }

  public void setDrawerState(boolean isOpen) {
    mDrawerState.setValue(isOpen);
  }

  public int getCurrentPosition() {
    return currentPosition.getValue();
  }

  public void setCurrentPosition(int pos) {
    currentPosition.setValue(pos);
  }

  public void setFiles(@NonNull List<File> files) {
    if (mFiles == null) {
      mFiles = new MutableLiveData<>(new ArrayList<>());
    }
    mFiles.setValue(files);
  }

  public LiveData<List<File>> getFiles() {
    if (mFiles == null) {
      mFiles = new MutableLiveData<>(new ArrayList<>());
    }
    return mFiles;
  }

  public File getCurrentFile() {
    List<File> files = getFiles().getValue();
    if (files == null) {
      return null;
    }

    return files.get(currentPosition.getValue());
  }

  public void clear() {
    List<File> value = getFiles().getValue();
    if (value != null) {
      value.clear();
      mFiles.setValue(value);
    }
  }

  public boolean openFile(File file) {
    setDrawerState(false);

    addFile(file);
    return true;
  }

  public void addFile(File file) {
    List<File> files = getFiles().getValue();
    if (files == null) {
      files = new ArrayList<>();
    }
    files.add(file);
    mFiles.setValue(files);
    setCurrentPosition(files.indexOf(file));
  }

  public void removeFile(@NonNull File file) {
    List<File> files = getFiles().getValue();
    if (files == null) {
      return;
    }

    files.remove(file);
    mFiles.setValue(files);
  }

  public boolean contains(Object obj) {
    return getFiles().getValue().contains(obj);
  }

  public int indexOf(Object obj) {
    return getFiles().getValue().indexOf(obj);
  }
}
