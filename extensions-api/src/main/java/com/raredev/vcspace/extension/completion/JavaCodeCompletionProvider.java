package com.raredev.vcspace.extension.completion;

import java.util.ArrayList;
import java.util.List;

public class JavaCodeCompletionProvider implements CodeCompletionProvider {
  @Override
  public List<String> getCodeCompletions(String code) {
    // Here we would implement the code completion logic for Java
    // For the purpose of this example, we will return a static list of completions for
    // demonstration purposes
    List<String> completions = new ArrayList<>();
    completions.add("System.out.println");
    completions.add("String");
    completions.add("int");
    completions.add("if");
    return completions;
  }
}
