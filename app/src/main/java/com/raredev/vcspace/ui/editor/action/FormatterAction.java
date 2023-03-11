package com.raredev.vcspace.ui.editor.action;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.raredev.vcspace.ui.editor.CodeEditorView;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class FormatterAction {
  private CodeEditorView editor;

  private String fileName;

  public FormatterAction(CodeEditorView editor) {
    this.editor = editor;

    fileName = editor.getFile().getName();
  }

  public void format() {
    String codeFormatted = editor.getEditor().getText().toString();
    if (fileName.endsWith(".html")) {
      codeFormatted = formatHtml();
    } else if (fileName.endsWith(".json")) {
      codeFormatted = formatJson();
    }
    editor.getEditor().getText().replace(0, editor.getEditor().getText().length(), codeFormatted);
  }

  private String formatHtml() {
    String html = editor.getEditor().getText().toString();
    Document doc = Jsoup.parse(html);

    var outputSettings = new Document.OutputSettings();
    outputSettings.indentAmount(4);

    doc.outputSettings(outputSettings.prettyPrint(true));
    if (doc.toString().contains("<!doctype html>")) {
      return doc.html().replace("<!doctype html>", "<!DOCTYPE html>");
    }
    return doc.html();
  }

  private String formatJson() {
    try {
      ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

      DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();

      prettyPrinter.indentArraysWith(new DefaultPrettyPrinter.FixedSpaceIndenter());
      prettyPrinter.indentObjectsWith(new DefaultIndenter("    ", "\n"));

      Object jsonObject = mapper.readValue(editor.getEditor().getText().toString(), Object.class);

      String prettyJson = mapper.writer(prettyPrinter).writeValueAsString(jsonObject);
      return prettyJson;
    } catch (IOException ioe) {
      return editor.getEditor().getText().toString();
    }
  }
}
