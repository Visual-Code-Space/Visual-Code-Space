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

  private static List<Symbol> baseSymbols;

  public static List<Symbol> baseSymbols() {
    if (baseSymbols != null) return baseSymbols;
    baseSymbols = new ArrayList<>();
    baseSymbols.add(new Symbol("→"));

    String[] baseSymbolsArray = {
      "(", ")", "{", "}", ";", "\"", "'", ":", "[", "]", "=", "+", "-", "*", "/", "%", "&", "|",
      "^", "!", "?", "<", ">"
    };

    for (String symbol : baseSymbolsArray) {
      baseSymbols.add(new Symbol(symbol, getClosingPair(symbol)));
    }
    return baseSymbols;
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
