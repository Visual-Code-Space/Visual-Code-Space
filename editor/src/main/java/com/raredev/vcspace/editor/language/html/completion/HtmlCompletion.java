package com.raredev.vcspace.editor.language.html.completion;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class HtmlCompletion {

  @SerializedName("tags")
  private Map<String, HTMLTag> tags;

  @SerializedName("attributes")
  private Map<String, Attribute> attributes;

  public Map<String, HTMLTag> getTags() {
    return tags;
  }

  public void setTags(Map<String, HTMLTag> tags) {
    this.tags = tags;
  }

  public Map<String, Attribute> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, Attribute> attributes) {
    this.attributes = attributes;
  }

  public static class HTMLTag {

    @SerializedName("attributes")
    private String[] attributes;

    @SerializedName("description")
    private String description;

    public String[] getAttributes() {
      return attributes;
    }

    public void setAttributes(String[] attributes) {
      this.attributes = attributes;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }
  }

  public static class Attribute {

    @SerializedName("global")
    private String global;
    
    @SerializedName("attribOption")
    private String[] attribOption;

    @SerializedName("description")
    private String description;
    
    @SerializedName("type")
    private String type;
    
    public String getGlobal() {
      return global;
    }

    public void setGlobal(String global) {
      this.global = global;
    }
    
    public String[] getAttribOption() {
      return attribOption;
    }

    public void setAttribOption(String[] attribOption) {
      this.attribOption = attribOption;
    }


    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }
    
    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }
}
