package com.raredev.vcspace.ui.editor.action;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.raredev.vcspace.ui.editor.CodeEditorView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class FormatterAction {
  private CodeEditorView editor;

  private String fileName;

  public FormatterAction(CodeEditorView editor) {
    this.editor = editor;

    fileName = editor.getFile().getName();
  }

  public void format() {
    if (fileName.endsWith(".html")) {
      editor.getEditor().setText(formatHtml());
    } else if (fileName.endsWith(".json")) {
      editor.getEditor().setText(formatJson());
    }
  }

  public boolean isValidLanguage() {
    if (fileName.endsWith(".html")) {
      return true;
    } else if (fileName.endsWith(".json")) {
      return true;
    } else {
      return false;
    }
  }

  private String formatHtml() {
    String html = editor.getEditor().getText().toString();
    Document doc = Jsoup.parse(html);
    doc.outputSettings(new Document.OutputSettings().prettyPrint(true));
    if (doc.toString().contains("<!doctype html>")) {
      return doc.html().replace("<!doctype html>", "<!DOCTYPE html>");
    }
    return doc.html();
  }

  private String formatJson() {
    try {
      ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
      Object jsonObject = mapper.readValue(editor.getEditor().getText().toString(), Object.class);
      String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
      return prettyJson;
    } catch (IOException ioe) {
      return editor.getEditor().getText().toString();
    }
  }
}
