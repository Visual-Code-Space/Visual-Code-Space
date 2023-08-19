package com.raredev.vcspace.compiler;

import com.google.gson.annotations.SerializedName;

public class CompilationResponse {
  
  @SerializedName("result")
  private String compilationResult;

  public String getCompilationResult() {
    return compilationResult;
  }
}
