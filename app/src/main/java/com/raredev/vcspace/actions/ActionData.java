package com.raredev.vcspace.actions;

import java.util.HashMap;
import java.util.Map;

public class ActionData<T> {
  private Map<Class, T> data = new HashMap<>();

  public void put(Class key, T classe) {
    data.put(key, classe);
  }

  public T get(Class key) {
    return data.get(key);
  }

  public void clear() {
    data.clear();
  }
}
