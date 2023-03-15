package com.raredev.vcspace;

import android.content.Context;
import android.content.Intent;
import com.raredev.common.util.DialogUtils;
import com.raredev.vcspace.activity.WebViewActivity;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class SimpleExecuter {

  public SimpleExecuter(Context context, File file) {
    String fileName = file.getName();
    switch (fileName.substring(fileName.lastIndexOf("."), fileName.length()).toLowerCase()) {
      case ".html":
        execute(context, file, file);
        break;
      case ".md":
        try {
          File htmlFile = convertMarkdownToHtml(file.getAbsolutePath());
          execute(context, htmlFile, file);
        } catch (IOException e) {
          DialogUtils.newErrorDialog(context, "Error", e.toString());
          e.printStackTrace();
        }
        break;
    }
  }

  public static boolean isExecutable(File file) {
    if (file == null) return false;
    String fileName = file.getName();
    switch (fileName.substring(fileName.lastIndexOf("."), fileName.length()).toLowerCase()) {
      case ".html":
      case ".md":
        return true;
      default:
        return false;
    }
  }

  private void execute(Context context, File executableFile, File realFile) {
    Intent it = new Intent(context, WebViewActivity.class);
    it.putExtra("executable_file", executableFile.getAbsolutePath());
    it.putExtra("real_file", realFile.getAbsolutePath());
    context.startActivity(it);
  }

  private File convertMarkdownToHtml(String markdownFilePath) throws IOException {
    // Read the Markdown file contents into a string
    Path path = Paths.get(markdownFilePath);
    byte[] bytes = Files.readAllBytes(path);
    String markdownText = new String(bytes, StandardCharsets.UTF_8);

    // Convert the Markdown to HTML
    Parser parser = Parser.builder().build();
    Node document = parser.parse(markdownText);
    HtmlRenderer renderer = HtmlRenderer.builder().build();
    String html = renderer.render(document);

    // Write the HTML to a temporary file
    File tempFile = File.createTempFile("markdown", ".html");
    BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
    writer.write(html);
    writer.close();

    return tempFile;
  }
}
