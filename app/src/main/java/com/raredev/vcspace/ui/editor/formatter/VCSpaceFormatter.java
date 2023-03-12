package com.raredev.vcspace.ui.editor.formatter;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.rosemoe.sora.lang.format.AsyncFormatter;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.TextRange;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class VCSpaceFormatter extends AsyncFormatter {
  private String fileExtension;

  public VCSpaceFormatter(String fileExtension) {
    this.fileExtension = fileExtension;
  }

  @Override
  public TextRange formatAsync(Content text, TextRange range) {
    String code = text.toString();
    switch (fileExtension) {
      case "html":
        text.replace(0, code.length(), formatHtml(code));
        break;
      case "json":
        text.replace(0, code.length(), formatJson(code));
        break;
    }
    return range;
  }

  @Override
  public TextRange formatRegionAsync(Content text, TextRange range1, TextRange range2) {
    return range2;
  }

  private String formatHtml(String code) {
    String html = code;
    Document doc = Jsoup.parse(html);

    var outputSettings = new Document.OutputSettings();
    outputSettings.indentAmount(4);

    doc.outputSettings(outputSettings.prettyPrint(true));
    if (doc.toString().contains("<!doctype html>")) {
      return doc.html().replace("<!doctype html>", "<!DOCTYPE html>");
    }
    return doc.html();
  }
  
  private String formatJson(String code) {
    try {
      ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

      DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
      prettyPrinter.indentObjectsWith(new DefaultIndenter("    ", "\n"));

      Object jsonObject = mapper.readValue(code, Object.class);

      String prettyJson = mapper.writer(prettyPrinter).writeValueAsString(jsonObject);
      return prettyJson;
    } catch (IOException ioe) {
      return code;
    }
  }
}
