package com.raredev.vcspace.editor.completion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CompletionProvider {

  private static final Map<Class<?>, CompletionProvider> providers = new HashMap<>();

  public abstract List<VCSpaceCompletionItem> getCompletions(CompletionParams params);

  public static void registerCompletionProviders() {
    if (providers.isEmpty()) {
      
    }
  }

  public static CompletionProvider getCompletionProvider(Class<?> clss) {
    return providers.get(clss);
  }
}
