package com.raredev.vcspace.ui.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import com.raredev.vcspace.models.DocumentModel;
import java.util.ArrayList;
import java.util.List;

public class EditorViewModel extends ViewModel {

  private MutableLiveData<List<DocumentModel>> documents = new MutableLiveData<>(new ArrayList<>());
  private MutableLiveData<Integer> currentPosition = new MutableLiveData<>(-1);

  private MutableLiveData<Boolean> drawerState = new MutableLiveData<>(false);

  public LiveData<List<DocumentModel>> getDocumentsLiveData() {
    return documents;
  }

  public List<DocumentModel> getDocuments() {
    return getDocumentsLiveData().getValue();
  }

  public void addDocument(DocumentModel document) {
    List<DocumentModel> documentList = getDocuments();
    documentList.add(document);

    documents.setValue(documentList);
  }

  public DocumentModel getDocument(int index) {
    return getDocuments().get(index);
  }

  public int indexOf(String path) {
    for (int i = 0; i < getOpenedDocumentCount(); i++) {
      DocumentModel temp = getDocument(i);
      if (path.equals(temp.getPath())) {
        return i;
      }
    }
    return -1;
  }

  public int indexOf(DocumentModel doc) {
    for (int i = 0; i < getOpenedDocumentCount(); i++) {
      DocumentModel temp = getDocument(i);
      if (doc.getPath().equals(temp.getPath())) {
        return i;
      }
    }
    return -1;
  }

  public int getOpenedDocumentCount() {
    return getDocuments().size();
  }

  public void observeDocuments(LifecycleOwner owner, Observer<List<DocumentModel>> obs) {
    getDocumentsLiveData().observe(owner, obs);
  }

  public void removeDocument(int index) {
    List<DocumentModel> documentList = getDocuments();
    documentList.remove(index);
    documents.setValue(documentList);
  }

  public void clearDocuments() {
    List<DocumentModel> documents = getDocuments();
    documents.clear();
    this.documents.setValue(documents);
  }

  public LiveData<Boolean> getDrawerStateLiveData() {
    return drawerState;
  }

  public void setCurrentPosition(int position) {
    currentPosition.setValue(position);
  }

  public int getCurrentPosition() {
    return currentPosition.getValue();
  }

  public DocumentModel getCurrentDocument() {
    int currentPosition = getCurrentPosition();
    if (currentPosition == -1) {
      return null;
    }
    return getDocuments().get(currentPosition);
  }

  public void observeCurrentPosition(LifecycleOwner owner, Observer<Integer> obs) {
    currentPosition.observe(owner, obs);
  }

  public boolean getDrawerState() {
    return getDrawerStateLiveData().getValue();
  }

  public void setDrawerState(boolean state) {
    drawerState.setValue(state);
  }

  public void observeDrawerState(LifecycleOwner owner, Observer<Boolean> obs) {
    drawerState.observe(owner, obs);
  }
}
