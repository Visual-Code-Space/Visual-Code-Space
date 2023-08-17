package com.raredev.vcspace.editor.completion;

/** Completion item kinds. */
public enum SimpleCompletionItemKind {
  TEXT(0, 0xFFABB6BD),
  METHOD(1, 0xFF7D4F99),
  FUNCTION(2, 0xff4db5e4),
  CONSTRUCTOR(3, 0xff4db5e4),
  FIELD(4, 0xffd3a76b),
  VARIABLE(5, 0xFF3E88A3),
  CLASS(6, 0xffa67c00),
  INTERFACE(7, 0xff76c561),
  MODULE(8, 0xff85cce5),
  PROPERTY(9, 0xffd2bde6),
  UNIT(10, 0xffb5e2ff),
  VALUE(11, 0xFF596B7A),
  ENUM(12, 0xff85cce5),
  KEYWORD(13, 0xFF737373),
  SNIPPET(14, 0xffb5e2ff),
  COLOR(15, 0xff4db5e4),
  FILE(16, 0xffb5e2ff),
  REFERENCE(17, 0xffb5e2ff),
  FOLDER(18, 0xffb5e2ff),
  ENUM_MEMBER(19, 0xff85cce5),
  CONSTANT(20, 0xffd3a76b),
  STRUCT(21, 0xffd2bde6),
  EVENT(22, 0xffb5e2ff),
  OPERATOR(23, 0xffeaabb6),
  TYPE_PARAMETER(24, 0xffd3a76b),
  USER(25, 0xffb5e2ff),
  ISSUE(26, 0xffb5e2ff),
  TAG(27, 0xFF3E88A3),
  ATTRIBUTE(28, 0xFF7D4F99),
  IDENTIFIER(29, 0xFF737373),
  UNKNOWN(30, 0xFF3E88A3);

  private final int value;
  private final long defaultDisplayBackgroundColor;
  private final String displayString;

  SimpleCompletionItemKind(int value, long defaultDisplayBackgroundColor) {
    this.value = value;
    this.defaultDisplayBackgroundColor = defaultDisplayBackgroundColor;
    this.displayString = name().substring(0, 1);
  }

  SimpleCompletionItemKind(int value) {
    this(value, 0);
  }

  public String getDisplayChar() {
    return displayString;
  }

  public long getDefaultDisplayBackgroundColor() {
    return this.defaultDisplayBackgroundColor;
  }
}
