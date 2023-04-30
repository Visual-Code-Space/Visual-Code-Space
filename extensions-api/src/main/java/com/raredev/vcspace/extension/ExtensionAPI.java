package com.raredev.vcspace.extension;

import java.util.List;

public interface ExtensionAPI {
  void registerExtension(String extensionId, Object extension);

  Object getExtension(String extensionId);

  List<Object> getAllExtensions();
}
