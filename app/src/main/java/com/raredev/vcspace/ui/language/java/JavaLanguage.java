package com.raredev.vcspace.ui.language.java;

import android.os.Bundle;
import androidx.annotation.NonNull;
import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.completion.SimpleSnippetCompletionItem;
import io.github.rosemoe.sora.lang.completion.SnippetDescription;
import io.github.rosemoe.sora.lang.completion.snippet.CodeSnippet;
import io.github.rosemoe.sora.lang.completion.snippet.parser.CodeSnippetParser;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;
import io.github.rosemoe.sora.widget.SymbolPairMatch;

public class JavaLanguage extends VCSpaceTMLanguage {

  private static final CodeSnippet FOR_SNIPPET =
      CodeSnippetParser.parse("for(int ${1:i} = 0;$1 < ${2:count};$1++) {\n    $0\n}");
  private static final CodeSnippet STATIC_CONST_SNIPPET =
      CodeSnippetParser.parse(
          "private final static ${1:type} ${2/(.*)/${1:/upcase}/} = ${3:value};");
  /// Class
  private static final CodeSnippet CLASS_SNIPPET =
      CodeSnippetParser.parse("class ${1:Name} {\n	$0\n}");
  private static final CodeSnippet CLASS_INHERITANCE_SNIPPET =
      CodeSnippetParser.parse("public class ${1:Name} extends ${2:Parent} {\n	$0\n}");
  private static final CodeSnippet INTERFACE_SNIPPET =
      CodeSnippetParser.parse("interface ${1:Name} {\n	$0\n}");
  private static final CodeSnippet INTERFACE_INHERITANCE_SNIPPET =
      CodeSnippetParser.parse("interface ${1:Name} extends ${2:Parent} {\n	$0\n}");

  /// Comments
  private static final CodeSnippet MULTILINE_COMMENT_SNIPPET =
      CodeSnippetParser.parse("/*\n*$0\n*/");

  /// Constants
  private static final CodeSnippet CONSTANT_SNIPPET =
      CodeSnippetParser.parse("static public final ${1:String} ${2:var} = ${3:value};$0");
  private static final CodeSnippet STRING_CONSTANT_SNIPPET =
      CodeSnippetParser.parse("static public final String ${1:var} = \"${2:value}\";$0");

  /// Control Statements
  private static final CodeSnippet CASE_SNIPPET = CodeSnippetParser.parse("case ${1}:\n	$0");
  private static final CodeSnippet DEFAULT_SNIPPET = CodeSnippetParser.parse("default:\n	$0");
  private static final CodeSnippet ELSE_SNIPPET = CodeSnippetParser.parse("else {\n	$0\n}");
  private static final CodeSnippet ELSE_IF_SNIPPET =
      CodeSnippetParser.parse("else if (${1}) {\n	$0\n}");
  private static final CodeSnippet IF_SNIPPET = CodeSnippetParser.parse("if (${1}) {\n	$0\n}");
  private static final CodeSnippet SWITCH_SNIPPET =
      CodeSnippetParser.parse("switch (${1}) {\n	$0\n}");

  /// Create a Method
  private static final CodeSnippet METHOD_SNIPPET =
      CodeSnippetParser.parse("${1:void} ${2:method}(${3}) ${4:throws} {\n	$0\n}");

  /// Create a Variable
  private static final CodeSnippet VARIABLE_SNIPPET =
      CodeSnippetParser.parse("${1:String} ${2:varName}${3: = null}${4};$0");

  /// Annotations
  private static final CodeSnippet Before_ANNOTATION_SNIPPET =
      CodeSnippetParser.parse("@Before\nstatic void ${1:intercept}(${2:args}) {\n	$0\n}");
  private static final CodeSnippet ManyToMany_ANNOTATION_SNIPPET =
      CodeSnippetParser.parse("@ManyToMany\n$0");
  private static final CodeSnippet ManyToOne_ANNOTATION_SNIPPET =
      CodeSnippetParser.parse("@ManyToOne\n$0");
  public static final CodeSnippet OneToMany_ANNOTATION_SNIPPET =
      CodeSnippetParser.parse("@OneToMany${1:(cascade=CascadeType.ALL)}\n$0");
  private static final CodeSnippet OneToOne_ANNOTATION_SNIPPET =
      CodeSnippetParser.parse("@OneToOne\n$0");
  private static final CodeSnippet Override_ANNOTATION_SNIPPET =
      CodeSnippetParser.parse("@Override\n$0");

  /// Basic Java packages and import
  private static final CodeSnippet JAVA_BEANS_SNIPPET = CodeSnippetParser.parse("java.beans.");
  private static final CodeSnippet JAVA_IO_SNIPPET = CodeSnippetParser.parse("java.io.");
  private static final CodeSnippet JAVA_MATH_SNIPPET = CodeSnippetParser.parse("java.math.");
  private static final CodeSnippet JAVA_NET_SNIPPET = CodeSnippetParser.parse("java.net.");
  private static final CodeSnippet JAVA_UTIL_SNIPPET = CodeSnippetParser.parse("java.util.");

  /// Error Methods
  private static final CodeSnippet ERROR_SNIPPET =
      CodeSnippetParser.parse("System.err.print(\"${1:Message}\");");
  private static final CodeSnippet ERROR_F_SNIPPET =
      CodeSnippetParser.parse("System.err.printf(\"${1:Message}\", ${2:exception});");
  private static final CodeSnippet ERROR_LN_SNIPPET =
      CodeSnippetParser.parse("System.err.println(\"${1:Message}\");");

  /// Exception Handling
  private static final CodeSnippet ASSERT_SNIPPET =
      CodeSnippetParser.parse("assert ${1:test} : \"${2:Failure message}\";$0");
  private static final CodeSnippet CATCH_SNIPPET =
      CodeSnippetParser.parse("catch(${1:Exception} ${2:e}) $0");
  private static final CodeSnippet TRY_SNIPPET =
      CodeSnippetParser.parse("try {\n	${3}\n} catch(${1:Exception} ${2:e}) {\n	$0\n}");
  private static final CodeSnippet TRY_F_SNIPPET =
      CodeSnippetParser.parse(
          "try {\n	${3}\n} catch(${1:Exception} ${2:e}) {\n	${4}\n} finally {\n	$0\n}");

  /// Main method
  private static final CodeSnippet MAIN_METHOD_SNIPPET =
      CodeSnippetParser.parse("public static void main (String[] args) {\n	${1:/* code */}$0\n}");

  /// Print Methods
  private static final CodeSnippet PRINT_SNIPPET =
      CodeSnippetParser.parse("System.out.print(\"${1:Message}\");$0");
  private static final CodeSnippet PRINT_F_SNIPPET =
      CodeSnippetParser.parse("System.out.printf(\"${1:Message}\", ${2:args});$0");
  private static final CodeSnippet PRINT_LN_SNIPPET =
      CodeSnippetParser.parse("System.out.println(${1});$0");
  private static final CodeSnippet SOUT_SNIPPET =
      CodeSnippetParser.parse("System.out.println(${1});$0");

  private String prefix;
  private CompletionPublisher publisher;

  public JavaLanguage() {
    super(
        GrammarRegistry.getInstance().findGrammar("source.java"),
        GrammarRegistry.getInstance().findLanguageConfiguration("source.java"),
        ThemeRegistry.getInstance(),
        true);

    setCompleterKeywords(javaKeywords);
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