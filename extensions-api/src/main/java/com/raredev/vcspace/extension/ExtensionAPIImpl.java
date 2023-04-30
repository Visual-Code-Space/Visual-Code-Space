package com.raredev.vcspace.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtensionAPIImpl implements ExtensionAPI {
  private static ExtensionAPIImpl instance;
  private Map<String, Object> extensions;

  public ExtensionAPIImpl() {
    extensions = new HashMap<>();
  }

  public static synchronized ExtensionAPIImpl getInstance() {
    if (instance == null) {
      instance = new ExtensionAPIImpl();
    }
    return instance;
  }

  @Override
  public void registerExtension(String extensionId, Object extension) {
    extensions.put(extensionId, extension);
  }

  @Override
  public Object getExtension(String extensionId) {
    return extensions.get(extensionId);
  }

  @Override
  public List<Object> getAllExtensions() {
    return new ArrayList<>(extensions.values());
  }
}
