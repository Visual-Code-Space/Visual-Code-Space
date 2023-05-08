package com.raredev.vcspace.managers;

import android.content.Context;
import com.blankj.utilcode.util.ResourceUtils;
import com.raredev.vcspace.util.Environment;
import java.io.File;

public class ToolsManager {

  private static File ANDROIDJAR;
  private static File CORELAMBDA;

  public static File getAndroidJar() {
    if (ANDROIDJAR == null) {

      ANDROIDJAR = new File(Environment.JAVA_TOOLS, "android.jar");
      if (!ANDROIDJAR.exists()) {
        ResourceUtils.copyFileFromAssets("tools/android.jar", ANDROIDJAR.getAbsolutePath());
      }
    }

    return ANDROIDJAR;
  }

  public static File getLambdaStubs() {
    if (CORELAMBDA == null) {

      CORELAMBDA = new File(Environment.JAVA_TOOLS, "core-lambda-stubs.jar");
      if (!CORELAMBDA.exists()) {
        ResourceUtils.copyFileFromAssets(
            "tools/core-lambda-stubs.jar", CORELAMBDA.getAbsolutePath());
      }
    }

    return CORELAMBDA;
  }
}
