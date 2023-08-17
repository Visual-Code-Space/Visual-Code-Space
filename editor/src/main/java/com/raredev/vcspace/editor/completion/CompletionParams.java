package com.raredev.vcspace.editor.completion;

import com.raredev.vcspace.editor.IDECodeEditor;

public class CompletionParams {

  private IDECodeEditor editor;
  private String content;
  private String prefix;
  private int line;
  private int column;
  private int index;

  public CompletionParams(
      IDECodeEditor editor, String content, String prefix, int line, int column, int index) {
    this.editor = editor;
    this.content = content;
    this.prefix = prefix;
    this.line = line;
    this.column = column;
    this.index = index;
  }

  public IDECodeEditor getEditor() {
    return this.editor;
  }

  public void setEditor(IDECodeEditor editor) {
    this.editor = editor;
  }

  public String getContent() {
    return this.content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getPrefix() {
    return this.prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public int getLine() {
    return this.line;
  }

  public void setLine(int line) {
    this.line = line;
  }

  public int getColumn() {
    return this.column;
  }

  public void setColumn(int column) {
    this.column = column;
  }

  public int getIndex() {
    return this.index;
  }

  public void setIndex(int index) {
    this.index = index;
  }
}
