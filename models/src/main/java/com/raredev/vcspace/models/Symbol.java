package com.raredev.vcspace.models;

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

  public static final String[] baseSymbols = {
    "(", ")", "{", "}", ";", "\"", "'", ":", "[", "]", "=", "+", "-", "*", "/", "%", "&", "|", "^",
    "!", "?", "<", ">"
  };

  public static List<Symbol> baseSymbols() {
    List<Symbol> symbols = new ArrayList<>();
    symbols.add(new Symbol("→"));

    for (String symbol : baseSymbols) {
      symbols.add(new Symbol(symbol, getClosingPair(symbol)));
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
        return openingSymbol;
    }
  }
}
