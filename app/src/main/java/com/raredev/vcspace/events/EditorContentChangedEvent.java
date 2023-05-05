package com.raredev.vcspace.events;

import com.raredev.vcspace.models.DocumentModel;
import java.io.File;

public class EditorContentChangedEvent {

  private DocumentModel document;

  public EditorContentChangedEvent(DocumentModel document) {
    this.document = document;
  }

  public DocumentModel getDocument() {
    return this.document;
  }

  public void setDocument(DocumentModel document) {
    this.document = document;
  }
}
