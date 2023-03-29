package com.raredev.vcspace.ui.editor;

import com.raredev.vcspace.util.PreferencesUtils;
import java.util.ArrayList;
import java.util.List;

public class Symbol {
  private String label;
  private String insert;

  public Symbol(String label) {
    this(label, label);
  }

  public Symbol(String label, String insert) {
    this.label = label;
    this.insert = insert;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getInsert() {
    return this.insert;
  }

  public void setInsert(String insert) {
    this.insert = insert;
  }

  public static List<Symbol> baseSymbols() {
    String[] baseSymbols = {
      "(", ")", "{", "}", ";", "\"", "'", ":", "[", "]", "=", "+", "-", "*", "/", "%", "&", "|",
      "^", "!", "?", "<", ">"
    };
    List<Symbol> symbols = new ArrayList<>();
    symbols.add(new Symbol("→", PreferencesUtils.getTab()));

    for (String symbol : baseSymbols) {
      if (symbol.equals("(")
          || symbol.equals("{")
          || symbol.equals("[")
          || symbol.equals("<")
          || symbol.equals("\"")
          || symbol.equals("'")) {
        symbols.add(new Symbol(symbol, getClosingPair(symbol)));
      } else {
        symbols.add(new Symbol(symbol));
      }
    }

    return symbols;
  }

  private static String getClosingPair(String openingSymbol) {
    switch (openingSymbol) {
      case "(":
        return "()";
      case "{":
        return "{}";
      case "[":
        return "[]";
      case "<":
        return "<>";
      case "\"":
        return "\"\"";
      case "'":
        return "''";
      default:
        return "";
    }
  }
}
