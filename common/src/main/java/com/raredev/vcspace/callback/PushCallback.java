package com.raredev.vcspace.callback;

public interface PushCallback<Type> {
  void onComplete(Type type);
}