package com.raredev.vcspace.actions;

import java.util.HashMap;
import java.util.Map;

public class ActionData<T> {
  private Map<String, T> data = new HashMap<>();

  public void put(String key, T classe) {
    data.put(key, classe);
  }

  public T get(String key) {
    return data.get(key);
  }

  public void clear() {
    data.clear();
  }
}
