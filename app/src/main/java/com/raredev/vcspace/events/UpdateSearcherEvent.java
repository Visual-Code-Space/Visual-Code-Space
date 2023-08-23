package com.raredev.vcspace.events;

import io.github.rosemoe.sora.widget.EditorSearcher;

public class UpdateSearcherEvent extends PanelEvent {

  private EditorSearcher searcher;

  public UpdateSearcherEvent(EditorSearcher searcher) {
    this.searcher = searcher;
  }

  public EditorSearcher getSearcher() {
    return this.searcher;
  }

  public void setSearcher(EditorSearcher searcher) {
    this.searcher = searcher;
  }
}
