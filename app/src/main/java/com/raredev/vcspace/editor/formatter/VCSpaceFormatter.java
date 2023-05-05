package com.raredev.vcspace.editor.formatter;

import io.github.rosemoe.sora.lang.format.AsyncFormatter;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.TextRange;

public class VCSpaceFormatter extends AsyncFormatter {
  private String fileExtension;

  public VCSpaceFormatter(String fileExtension) {
    this.fileExtension = fileExtension;
  }

  @Override
  public TextRange formatAsync(Content text, TextRange range) {
    /*String code = text.toString();
    switch (fileExtension) {
      case "html":
        text.replace(0, code.length(), formatHtml(code));
        break;
      case "json":
        text.replace(0, code.length(), formatJson(code));
        break;
      case "java":
        text.replace(0, code.length(), formatJava(code));
        break;
    }*/
    return range;
  }

  @Override
  public TextRange formatRegionAsync(Content text, TextRange range1, TextRange range2) {
    return range2;
  }

  /*private String formatHtml(String code) {
    String html = code;
    org.jsoup.nodes.Document doc = Jsoup.parse(html);

    var outputSettings = new org.jsoup.nodes.Document.OutputSettings();
    outputSettings.indentAmount(PreferencesUtils.getEditorTABSize());

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
      prettyPrinter.indentObjectsWith(new DefaultIndenter(PreferencesUtils.getTab(), "\n"));

      Object jsonObject = mapper.readValue(code, Object.class);

      String prettyJson = mapper.writer(prettyPrinter).writeValueAsString(jsonObject);
      return prettyJson;
    } catch (IOException ioe) {
      return code;
    }
  }

  @SuppressWarnings("unchecked")
  private String formatJava(String code) {
    Map options = DefaultCodeFormatterConstants.getEclipse21Settings();

    options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_19);
    options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_19);
    options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_19);

    var tabSize = String.valueOf(PreferencesUtils.getEditorTABSize());
    options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, tabSize);
    options.put(
        DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ENUM_CONSTANTS,
        DefaultCodeFormatterConstants.createAlignmentValue(
            true,
            DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE,
            DefaultCodeFormatterConstants.INDENT_ON_COLUMN));
    options.put(
        DefaultCodeFormatterConstants.FORMATTER_ALIGN_TYPE_MEMBERS_ON_COLUMNS,
        DefaultCodeFormatterConstants.createAlignmentValue(
            true,
            DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE,
            DefaultCodeFormatterConstants.INDENT_ON_COLUMN));
    options.put(
        DefaultCodeFormatterConstants.FORMATTER_ALIGN_FIELDS_GROUPING_BLANK_LINES,
        DefaultCodeFormatterConstants.createAlignmentValue(
            true,
            DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE,
            DefaultCodeFormatterConstants.INDENT_ON_COLUMN));
    options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_IMPORTS, "1");
    options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AFTER_IMPORTS, "1");
    options.put(
        DefaultCodeFormatterConstants.FORMATTER_COMMENT_CLEAR_BLANK_LINES_IN_JAVADOC_COMMENT,
        DefaultCodeFormatterConstants.TRUE);
    options.put(
        DefaultCodeFormatterConstants.FORMATTER_COMMENT_CLEAR_BLANK_LINES_IN_BLOCK_COMMENT,
        DefaultCodeFormatterConstants.TRUE);
    options.put(
        DefaultCodeFormatterConstants
            .FORMATTER_COMMENT_PRESERVE_WHITE_SPACE_BETWEEN_CODE_AND_LINE_COMMENT,
        DefaultCodeFormatterConstants.TRUE);
    options.put(
        DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AT_END_OF_FILE_IF_MISSING,
        JavaCore.INSERT);
    final CodeFormatter formatter = ToolFactory.createCodeFormatter(options);
    final TextEdit te =
        formatter.format(
            CodeFormatter.K_COMPILATION_UNIT | CodeFormatter.F_INCLUDE_COMMENTS,
            code,
            0, // starting index
            code.length(), // length
            0, // initial indentation
            System.lineSeparator() // line separator
            );

    final IDocument document = new Document(code);
    final CountDownLatch latch = new CountDownLatch(1);

    Executors.newSingleThreadExecutor()
        .execute(
            () -> {
              try {
                te.apply(document);
              } catch (Exception e) {
                throw new IllegalStateException(e);
              }
              latch.countDown();
            });

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return document.get();
  }*/
}
