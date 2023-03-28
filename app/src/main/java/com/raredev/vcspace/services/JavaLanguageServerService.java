package com.raredev.vcspace.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.raredev.vcspace.ui.language.java.lsp.JavaLanguageServer;
import com.raredev.vcspace.util.ILogger;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;

public class JavaLanguageServerService extends Service {

  private static final String TAG = "LanguageServer";

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    new Thread(
            () -> {
              int port = intent != null ? intent.getIntExtra("port", 0) : 0;
              ServerSocket socket = null;
              try {
                socket = new ServerSocket(port);
                ILogger.debug(TAG, "Starting socket on port " + socket.getLocalPort());
                Socket socketClient = socket.accept();
                ILogger.debug(
                    TAG, "connected to the client on port " + socketClient.getLocalPort());
                try {
                  JavaLanguageServer server = new JavaLanguageServer();
                  InputStream inputStream = socketClient.getInputStream();
                  OutputStream outputStream = socketClient.getOutputStream();
                  Launcher<LanguageClient> launcher =
                      Launcher.createLauncher(
                          server, LanguageClient.class, inputStream, outputStream);
                  LanguageClient client = launcher.getRemoteProxy();
                  server.connect(client);
                  launcher.startListening().get(Long.MAX_VALUE, TimeUnit.SECONDS);
                } catch (Exception e) {
                  ILogger.error(
                      TAG,
                      "Unexpected exception is thrown in the Language Server Thread.\n"
                          + Log.getStackTraceString(e));
                }
                socketClient.close();
              } catch (IOException e) {
                ILogger.error(
                    TAG,
                    "Unexpected exception is thrown in the Language Server Thread.\n"
                        + Log.getStackTraceString(e));
              } finally {
                try {
                  if (socket != null) {
                    socket.close();
                  }
                } catch (IOException e) {
                  ILogger.error(
                      TAG,
                      "Unexpected exception is thrown in the Language Server Thread.\n"
                          + Log.getStackTraceString(e));
                }
              }
            })
        .start();
    return START_STICKY;
  }
}
