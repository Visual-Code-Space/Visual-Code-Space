package com.raredev.vcspace.util;

import com.blankj.utilcode.util.FileUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class Environment {

  public static final Map<String, String> ENV_VARS = new HashMap<>();

  public static final String DEFAULT_ROOT = "/data/data/com.raredev.vcspace/files";
  public static final String DEFAULT_HOME = DEFAULT_ROOT + "/home";

  public static File ROOT;

  public static File HOME;
  public static File PREFIX;
  public static File TMP_DIR;
  public static File BIN_DIR;

  public static File SHELL;

  public static void init() {
    ROOT = new File(DEFAULT_ROOT);
    HOME = mkdirIfNotExits(new File(DEFAULT_HOME));
    PREFIX = mkdirIfNotExits(new File(ROOT, "usr"));
    TMP_DIR = mkdirIfNotExits(new File(PREFIX, "tmp"));
    BIN_DIR = mkdirIfNotExits(new File(PREFIX, "bin"));

    SHELL = new File(BIN_DIR, "bash");

    SHELL.setExecutable(true);

    System.setProperty("user.home", HOME.getAbsolutePath());
  }

  public static File mkdirIfNotExits(File in) {
    if (in != null && !in.exists()) {
      FileUtils.createOrExistsDir(in);
    }

    return in;
  }

  public static Map<String, String> getEnvironment() {
    if (!ENV_VARS.isEmpty()) {
      return ENV_VARS;
    }

    ENV_VARS.put("HOME", HOME.getAbsolutePath());
    ENV_VARS.put("ANDROID_USER_HOME", HOME.getAbsolutePath() + "/.android");
    ENV_VARS.put("TMPDIR", TMP_DIR.getAbsolutePath());
    ENV_VARS.put("LANG", "en_US.UTF-8");
    ENV_VARS.put("LC_ALL", "en_US.UTF-8");

    ENV_VARS.put("SHELL", SHELL.getAbsolutePath());
    ENV_VARS.put("CONFIG_SHELL", SHELL.getAbsolutePath());
    ENV_VARS.put("TERM", "xterm");

    return ENV_VARS;
  }
}
