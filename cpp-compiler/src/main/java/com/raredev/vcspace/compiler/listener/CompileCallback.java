package com.raredev.vcspace.compiler.listener;

public interface CompileCallback {
  void onSuccess(String outPath);

  void onError(String error);
}
