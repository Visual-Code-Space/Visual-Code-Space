package com.raredev.vcspace.util;

import android.content.Context;
import android.content.res.Configuration;
import android.util.TypedValue;
import java.io.IOException;
import java.net.ServerSocket;

public class Utils {
  public static int pxToDp(Context ctx, int value) {
    return (int)
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, value, ctx.getResources().getDisplayMetrics());
  }

  public static boolean isDarkMode(Context context) {
    int uiMode =
        context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    return uiMode == Configuration.UI_MODE_NIGHT_YES;
  }

  public static int randomPort() throws IOException {
    ServerSocket serverSocket = new ServerSocket(0);
    int port = serverSocket.getLocalPort();
    serverSocket.close();
    return port;
  }
}
