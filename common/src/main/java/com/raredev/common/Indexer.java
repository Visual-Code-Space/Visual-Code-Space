package com.raredev.common;

import com.google.gson.Gson;
import com.raredev.common.util.FileUtil;
import java.util.Arrays;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class Indexer {
  private JSONObject json;
  private String filePath;

  public Indexer(String filePath) {
    this.filePath = filePath;
    load();
  }

  public void load() {
    File indexFile = new File(filePath);
    try {
      if (!indexFile.exists()) {
        FileUtil.writeFile(filePath, "{}");
      }
      String index = FileUtil.readFile(indexFile.toString());
      json = new JSONObject(index);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public Indexer put(String key, List<File> items) throws JSONException {
    String value = new Gson().toJson(items);
    json.put(key, value);
    return this;
  }

  public List<File> getList(String key) {
    try {
      String jsonData = getString(key);
      return new ArrayList<>(Arrays.asList(new Gson().fromJson(jsonData, File[].class)));
    } catch (Exception ignored) {
      return new ArrayList<>();
    }
  }

  public Indexer put(String key, String value) throws JSONException {
    json.put(key, value);
    return this;
  }

  public Indexer put(String key, long value) throws JSONException {
    json.put(key, value);
    return this;
  }

  public boolean notHas(String key) throws JSONException {
    return !json.has(key);
  }

  public String getString(String key) throws JSONException {
    return json.getString(key);
  }

  public long getLong(String key) {
    try {
      return json.getLong(key);
    } catch (JSONException e) {
      return 0;
    }
  }

  public String asString() throws JSONException {
    return json.toString(4);
  }

  public void flush() {
    try {
      FileUtil.writeFile(filePath, asString());
    } catch (Throwable ignore) {
    }
  }
}
