package com.raredev.vcspace.ui.language.java;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.raredev.vcspace.util.PreferencesUtils;
import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.completion.SimpleSnippetCompletionItem;
import io.github.rosemoe.sora.lang.completion.SnippetDescription;
import io.github.rosemoe.sora.lang.completion.snippet.CodeSnippet;
import io.github.rosemoe.sora.lang.completion.snippet.parser.CodeSnippetParser;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;
import io.github.rosemoe.sora.widget.SymbolPairMatch;

public class JavaLanguage extends TextMateLanguage {

  private static final CodeSnippet FOR_SNIPPET =
      CodeSnippetParser.parse("for(int ${1:i} = 0;$1 < ${2:count};$1++) {\n    $0\n}");
  private static final CodeSnippet STATIC_CONST_SNIPPET =
      CodeSnippetParser.parse(
          "private final static ${1:type} ${2/(.*)/${1:/upcase}/} = ${3:value};");
  /// Class
  private static CodeSnippet CLASS_SNIPPET = CodeSnippetParser.parse("class ${1:Name} {\n	$0\n}");
  private static CodeSnippet CLASS_INHERITANCE_SNIPPET =
      CodeSnippetParser.parse("public class ${1:Name} extends ${2:Parent} {\n	$0\n}");
  private static CodeSnippet INTERFACE_SNIPPET =
      CodeSnippetParser.parse("interface ${1:Name} {\n	$0\n}");
  private static CodeSnippet INTERFACE_INHERITANCE_SNIPPET =
      CodeSnippetParser.parse("interface ${1:Name} extends ${2:Parent} {\n	$0\n}");

  /// Comments
  private static CodeSnippet MULTILINE_COMMENT_SNIPPET = CodeSnippetParser.parse("/*\n*$0\n*/");

  /// Constants
  private static CodeSnippet CONSTANT_SNIPPET =
      CodeSnippetParser.parse("static public final ${1:String} ${2:var} = ${3:value};$0");
  private static CodeSnippet STRING_CONSTANT_SNIPPET =
      CodeSnippetParser.parse("static public final String ${1:var} = \"${2:value}\";$0");

  /// Control Statements
  private static CodeSnippet CASE_SNIPPET = CodeSnippetParser.parse("case ${1}:\n	$0");
  private static CodeSnippet DEFAULT_SNIPPET = CodeSnippetParser.parse("default:\n	$0");
  private static CodeSnippet ELSE_SNIPPET = CodeSnippetParser.parse("else {\n	$0\n}");
  private static CodeSnippet ELSE_IF_SNIPPET = CodeSnippetParser.parse("else if (${1}) {\n	$0\n}");
  private static CodeSnippet IF_SNIPPET = CodeSnippetParser.parse("if (${1}) {\n	$0\n}");
  private static CodeSnippet SWITCH_SNIPPET = CodeSnippetParser.parse("switch (${1}) {\n	$0\n}");

  /// Create a Method
  private static CodeSnippet METHOD_SNIPPET =
      CodeSnippetParser.parse("${1:void} ${2:method}(${3}) ${4:throws} {\n	$0\n}");

  /// Create a Variable
  private static CodeSnippet VARIABLE_SNIPPET =
      CodeSnippetParser.parse("${1:String} ${2:varName}${3: = null}${4};$0");

  /// Annotations
  private static CodeSnippet Before_ANNOTATION_SNIPPET =
      CodeSnippetParser.parse("@Before\nstatic void ${1:intercept}(${2:args}) {\n	$0\n}");
  private static CodeSnippet ManyToMany_ANNOTATION_SNIPPET =
      CodeSnippetParser.parse("@ManyToMany\n$0");
  private static CodeSnippet ManyToOne_ANNOTATION_SNIPPET =
      CodeSnippetParser.parse("@ManyToOne\n$0");
  public static CodeSnippet OneToMany_ANNOTATION_SNIPPET =
      CodeSnippetParser.parse("@OneToMany${1:(cascade=CascadeType.ALL)}\n$0");
  private static CodeSnippet OneToOne_ANNOTATION_SNIPPET = CodeSnippetParser.parse("@OneToOne\n$0");
  private static CodeSnippet Override_ANNOTATION_SNIPPET = CodeSnippetParser.parse("@Override\n$0");

  /// Basic Java packages and import
  private static CodeSnippet JAVA_BEANS_SNIPPET = CodeSnippetParser.parse("java.beans.");
  private static CodeSnippet JAVA_IO_SNIPPET = CodeSnippetParser.parse("java.io.");
  private static CodeSnippet JAVA_MATH_SNIPPET = CodeSnippetParser.parse("java.math.");
  private static CodeSnippet JAVA_NET_SNIPPET = CodeSnippetParser.parse("java.net.");
  private static CodeSnippet JAVA_UTIL_SNIPPET = CodeSnippetParser.parse("java.util.");

  /// Error Methods
  private static CodeSnippet ERROR_SNIPPET =
      CodeSnippetParser.parse("System.err.print(\"${1:Message}\");");
  private static CodeSnippet ERROR_F_SNIPPET =
      CodeSnippetParser.parse("System.err.printf(\"${1:Message}\", ${2:exception});");
  private static CodeSnippet ERROR_LN_SNIPPET =
      CodeSnippetParser.parse("System.err.println(\"${1:Message}\");");

  /// Exception Handling
  private static CodeSnippet ASSERT_SNIPPET =
      CodeSnippetParser.parse("assert ${1:test} : \"${2:Failure message}\";$0");
  private static CodeSnippet CATCH_SNIPPET =
      CodeSnippetParser.parse("catch(${1:Exception} ${2:e}) $0");
  private static CodeSnippet TRY_SNIPPET =
      CodeSnippetParser.parse("try {\n	${3}\n} catch(${1:Exception} ${2:e}) {\n	$0\n}");
  private static CodeSnippet TRY_F_SNIPPET =
      CodeSnippetParser.parse(
          "try {\n	${3}\n} catch(${1:Exception} ${2:e}) {\n	${4}\n} finally {\n	$0\n}");

  /// Main method
  private static CodeSnippet MAIN_METHOD_SNIPPET =
      CodeSnippetParser.parse("public static void main (String[] args) {\n	${1:/* code */}$0\n}");

  /// Print Methods
  private static CodeSnippet PRINT_SNIPPET =
      CodeSnippetParser.parse("System.out.print(\"${1:Message}\");$0");
  private static CodeSnippet PRINT_F_SNIPPET =
      CodeSnippetParser.parse("System.out.printf(\"${1:Message}\", ${2:args});$0");
  private static CodeSnippet PRINT_LN_SNIPPET =
      CodeSnippetParser.parse("System.out.println(${1});$0");
  private static CodeSnippet SOUT_SNIPPET = CodeSnippetParser.parse("System.out.println(${1});$0");

  String prefix;
  CompletionPublisher publisher;

  public JavaLanguage() {
    super(
        GrammarRegistry.getInstance().findGrammar("source.java"),
        GrammarRegistry.getInstance().findLanguageConfiguration("source.java"),
        null,
        ThemeRegistry.getInstance(),
        true);

    setCompleterKeywords(javaKeywords);
    loadSymbolPairs();
  }

  @Override
  public void requireAutoComplete(
      @NonNull ContentReference content,
      @NonNull CharPosition position,
      @NonNull CompletionPublisher publisher,
      @NonNull Bundle extraArguments) {
    super.requireAutoComplete(content, position, publisher, extraArguments);
    var prefix =
        CompletionHelper.computePrefix(content, position, MyCharacter::isJavaIdentifierPart);
    this.prefix = prefix;
    this.publisher = publisher;

    addSnippet("fori", "For loop on index", FOR_SNIPPET);
    addSnippet("sconst", "Static Constant", STATIC_CONST_SNIPPET);

    /// Class
    addSnippet("cl", "Create a class", CLASS_SNIPPET);
    addSnippet("clext", "Create a class with inheritance", CLASS_INHERITANCE_SNIPPET);
    addSnippet("in", "Interface", INTERFACE_SNIPPET);
    addSnippet("inext", "Interface with inheritance", INTERFACE_INHERITANCE_SNIPPET);

    /// Comments
    addSnippet("/*", "Multiline comment", MULTILINE_COMMENT_SNIPPET);

    /// Constants
    addSnippet("co", "Create a constant", CONSTANT_SNIPPET);
    addSnippet("cos", "Create a String constant", STRING_CONSTANT_SNIPPET);

    /// Control Statements
    addSnippet("case", "case", CASE_SNIPPET);
    addSnippet("def", "default", DEFAULT_SNIPPET);
    addSnippet("el", "else statement", ELSE_SNIPPET);
    addSnippet("elif", "else if statement", ELSE_IF_SNIPPET);
    addSnippet("if", "if statement", IF_SNIPPET);
    addSnippet("sw", "switch", SWITCH_SNIPPET);

    /// Create a Method
    addSnippet("m", "Create a method", METHOD_SNIPPET);

    /// Annotations
    addSnippet("before", "@Before", Before_ANNOTATION_SNIPPET);
    addSnippet("mm", "@ManyToMany", ManyToMany_ANNOTATION_SNIPPET);
    addSnippet("mo", "@ManyToOne", ManyToOne_ANNOTATION_SNIPPET);
    addSnippet("om", "@OneToMany", OneToMany_ANNOTATION_SNIPPET);
    addSnippet("oo", "@OneToOne", OneToOne_ANNOTATION_SNIPPET);
    addSnippet("over", "@Override", Override_ANNOTATION_SNIPPET);

    /// Basic Java packages and import
    addSnippet("j.b", "java.beans", JAVA_BEANS_SNIPPET);
    addSnippet("j.i", "java.io", JAVA_IO_SNIPPET);
    addSnippet("j.m", "java.math", JAVA_MATH_SNIPPET);
    addSnippet("j.n", "java.net", JAVA_NET_SNIPPET);
    addSnippet("j.u", "java.util", JAVA_UTIL_SNIPPET);

    /// Create a Variable
    addSnippet("v", "Create a variable", VARIABLE_SNIPPET);

    /// Error Methods
    addSnippet("err", "Print error", ERROR_SNIPPET);
    addSnippet("errf", "Print error with format", ERROR_F_SNIPPET);
    addSnippet("errln", "Print error with new line", ERROR_LN_SNIPPET);

    /// Exception Handling
    addSnippet("as", "assert", ASSERT_SNIPPET);
    addSnippet("ca", "catch", CATCH_SNIPPET);
    addSnippet("try", "try-catch", TRY_SNIPPET);
    addSnippet("tryf", "try-catch with finally", TRY_F_SNIPPET);

    /// Main method
    addSnippet("main", "Main method", MAIN_METHOD_SNIPPET);

    /// Print Methods
    addSnippet("print", "System.out.print()", PRINT_SNIPPET);
    addSnippet("printf", "System.out.printf()", PRINT_F_SNIPPET);
    addSnippet("println", "System.out.println()", PRINT_LN_SNIPPET);
    addSnippet("sout", "System.out.println()", SOUT_SNIPPET);
  }

  public void addSnippet(String name, String desc, CodeSnippet code) {
    if (name.startsWith(prefix) && prefix.length() > 0) {
      publisher.addItem(
          new SimpleSnippetCompletionItem(
              name, "Snippet - " + desc, new SnippetDescription(prefix.length(), code, true)));
    }
  }

  @Override
  public boolean useTab() {
    return !PreferencesUtils.useUseSpaces();
  }

  private void loadSymbolPairs() {
    SymbolPairMatch symbolPairs = getSymbolPairs();
    symbolPairs.putPair("(", new SymbolPairMatch.SymbolPair("(", ")"));
    symbolPairs.putPair("{", new SymbolPairMatch.SymbolPair("{", "}"));
    symbolPairs.putPair("[", new SymbolPairMatch.SymbolPair("[", "]"));
    symbolPairs.putPair("\"", new SymbolPairMatch.SymbolPair("\"", "\""));
    symbolPairs.putPair("'", new SymbolPairMatch.SymbolPair("'", "'"));
  }

  private static final String[] javaKeywords = {
    "assert",
    "abstract",
    "boolean",
    "byte",
    "char",
    "class",
    "do",
    "double",
    "final",
    "float",
    "for",
    "if",
    "int",
    "long",
    "new",
    "public",
    "private",
    "protected",
    "package",
    "return",
    "static",
    "short",
    "super",
    "switch",
    "else",
    "volatile",
    "synchronized",
    "strictfp",
    "goto",
    "continue",
    "break",
    "transient",
    "void",
    "try",
    "catch",
    "finally",
    "while",
    "case",
    "default",
    "const",
    "enum",
    "extends",
    "implements",
    "import",
    "instanceof",
    "interface",
    "native",
    "this",
    "throw",
    "throws",
    "true",
    "false",
    "null",
    "var",
    "sealed",
    "permits"
  };
}
