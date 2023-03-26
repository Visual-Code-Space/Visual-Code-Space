package com.raredev.vcspace.actions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActionData<T> {
  private final Map<Class<?>, Object> data = new HashMap<>();

  public <T> void put(@NonNull Class<T> type, @Nullable T object) {
    Objects.requireNonNull(type);
    data.put(type, object);
  }

  @Nullable
  public <T> T get(@NonNull Class<T> type) {
    Objects.requireNonNull(type);

    final var object = data.get(type);
    if (object == null) {
      return null;
    }

    return type.cast(object);
  }

  public void clear() {
    data.clear();
  }
}
