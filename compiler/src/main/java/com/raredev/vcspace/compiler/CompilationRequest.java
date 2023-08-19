package com.raredev.vcspace.compiler;

import com.google.gson.annotations.SerializedName;

public class CompilationRequest {

  @SerializedName("language")
  private String language;

  @SerializedName("version")
  private String version;

  @SerializedName("code")
  private String code;

  @SerializedName("input")
  private String input;

  public CompilationRequest(String language, String version, String code, String input) {
    this.language = language;
    this.version = version;
    this.code = code;
    this.input = input;
  }
}
