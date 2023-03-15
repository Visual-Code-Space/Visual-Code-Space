package com.raredev.vcspace.ui.editor;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditorViewModel extends ViewModel {
  private final MutableLiveData<Pair<Integer, File>> currentPosition = new MutableLiveData<>(new Pair(-1, null));

  private MutableLiveData<List<File>> mFiles = new MutableLiveData<>(new ArrayList<>());

  public LiveData<Pair<Integer, File>> getCurrentPositionPair() {
    return currentPosition;
  }
  
  public int getCurrentPosition() {
    return currentPosition.getValue().first;
  }

  public File getCurrentFile() {
    return currentPosition.getValue().second;
  }
  
  public void setCurrentPosition(int pos, File file) {
    currentPosition.setValue(new Pair(pos, file));
  }

  public LiveData<List<File>> getFiles() {
    return mFiles;
  }

  public void clear() {
    List<File> value = getFiles().getValue();

    value.clear();
    mFiles.setValue(value);
    setCurrentPosition(-1, null);
  }

  public void addFile(File file) {
    List<File> files = getFiles().getValue();

    files.add(file);
    mFiles.setValue(files);
  }

  public void removeFile(int index) {
    List<File> files = getFiles().getValue();

    files.remove(index);
    
    if (files.isEmpty()) {
      setCurrentPosition(-1, null);
    }
    mFiles.setValue(files);
  }

  public boolean contains(Object obj) {
    List<File> files = getFiles().getValue();
    if (files.isEmpty()) {
      return false;
    }
    return files.contains(obj);
  }

  public int indexOf(Object obj) {
    return getFiles().getValue().indexOf(obj);
  }
}
