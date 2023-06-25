package com.raredev.vcspace.util;

import com.blankj.utilcode.util.FileUtils;
import java.io.File;

public class Environment {
  public static File ROOT;
  public static File JAVA_TOOLS;
  
  public static File VCSPACE_DIR;
  public static File PLUGINS_DIR;

  public static void init() {
    ROOT = new File("/data/data/com.raredev.vcspace/files");
    JAVA_TOOLS = mkdirIfNotExits(new File(ROOT, "java-tools"));
    VCSPACE_DIR = mkdirIfNotExits(new File("/storage/emulated/0/VCSpace"));
    PLUGINS_DIR = mkdirIfNotExits(new File(VCSPACE_DIR, "plugins"));
  }
  
  public static File mkdirIfNotExits(File in) {
    if (in != null && !in.exists()) {
      FileUtils.createOrExistsDir(in);
    }

    return in;
  }
}