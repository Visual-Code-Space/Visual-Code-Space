package com.raredev.vcspace.editor.language.kotlin.completion;

import com.raredev.vcspace.editor.completion.CompletionParams;
import com.raredev.vcspace.editor.completion.CompletionProvider;
import com.raredev.vcspace.editor.completion.SimpleCompletionIconDrawer;
import com.raredev.vcspace.editor.completion.SimpleCompletionItem;
import com.raredev.vcspace.editor.completion.SimpleCompletionItemKind;
import com.raredev.vcspace.editor.completion.VCSpaceCompletionItem;
import java.util.ArrayList;
import java.util.List;

public class KotlinCompletionProvider extends CompletionProvider {

  @Override
  public List<VCSpaceCompletionItem> getCompletions(CompletionParams params) {
    String prefix = params.getPrefix();
    List<VCSpaceCompletionItem> completions = new ArrayList<>();
    for (String keyword : kotlinKeywords) {
      if (keyword.startsWith(prefix) && prefix.length() > 0 && completions.size() <= 20) {
        completions.add(
            new SimpleCompletionItem(
                keyword,
                null,
                "Keyword",
                SimpleCompletionIconDrawer.draw(SimpleCompletionItemKind.KEYWORD),
                prefix.length(),
                keyword));
      }
    }
    return completions;
  }

  public static final String[] kotlinKeywords = {
    "as",
    "as?",
    "break",
    "class",
    "continue",
    "do",
    "else",
    "false",
    "for",
    "fun",
    "if",
    "in",
    "interface",
    "is",
    "null",
    "object",
    "package",
    "return",
    "super",
    "this",
    "throw",
    "true",
    "try",
    "typealias",
    "val",
    "var",
    "when",
    "while",
    "print",
    "println",
    "setOnClickListener",
    "String",
    "Int",
    "Char",
    "Float",
    "Double",
    "lateinit",
    "ActivityMainBinding",
    "binding",
    "onCreate",
    "onResume",
    "onPause",
    "Handler",
    "Runnable",
    "inflate",
    "layoutInflater",
    "setContentView",
    "root",
    "Array",
    "arrayOf",
    "onItemSelectedListener",
    "OnItemSelectedListener",
    "onNothingSelected",
    "isEmpty",
    "toBoolean",
    "toString",
    "toInt",
    "toChar",
    "toFloat",
    "toDouble",
    "text",
    "setText",
    "string",
    "isChecked",
    "recreate",
    "android",
    "androidx",
    "appcompat",
    "AppCompatActivity",
    "os",
    "Bundle",
    "AppCompatDelegate",
    "MODE_NIGHT_YES",
    "MODE_NIGHT_NO",
    "setDefaultNightMode",
    "else if",
    "apply",
    "context",
    "Context",
    "content",
    "SharedPreferences",
    "getSharedPreferences",
    "MODE_PRIVATE",
    "state",
    "Math",
    "toDegrees",
    "toRadians",
    "asin",
    "acos",
    "atan",
    "sin",
    "cos",
    "tan",
    "sqrt",
    "abs"
  };
}
