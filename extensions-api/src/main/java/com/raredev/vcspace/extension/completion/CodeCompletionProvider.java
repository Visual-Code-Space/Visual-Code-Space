package com.raredev.vcspace.extension.completion;

import java.util.List;

public interface CodeCompletionProvider {
  List<String> getCodeCompletions(String code);
}
