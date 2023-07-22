package com.raredev.vcspace.plugin;

public class Plugin {

  private String name;
  private String version;
  private String creator;
  private String type;

  public Plugin(String name, String version, String creator, String type) {
    this.name = name;
    this.version = version;
    this.creator = creator;
    this.type = type;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return this.version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getCreator() {
    return this.creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
