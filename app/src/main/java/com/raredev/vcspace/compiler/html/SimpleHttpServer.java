package com.raredev.vcspace.compiler.html;

import com.raredev.vcspace.util.ILogger;
import fi.iki.elonen.NanoHTTPD;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class SimpleHttpServer {
  public WebServer server;

  private int port;

  private String rootFolder;
  private String indexName;

  public SimpleHttpServer(int port, String rootFolder, String indexName) {
    this.port = port;
    this.rootFolder = rootFolder;
    this.indexName = indexName;
  }

  public void startServer() {
    try {
      server = new WebServer(port);
      server.start();
    } catch (IOException e) {
      ILogger.error("SimpleHttpServer", "Failed to start the server: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void stopServer() {
    if (server != null) {
      server.stop();
    }
  }

  public String getLocalIpAddress() {
    try {
      Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
      while (networkInterfaces.hasMoreElements()) {
        NetworkInterface networkInterface = networkInterfaces.nextElement();
        Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
        while (inetAddresses.hasMoreElements()) {
          InetAddress inetAddress = inetAddresses.nextElement();
          if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length == 4) {
            return inetAddress.getHostAddress() + ":" + port;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private class WebServer extends NanoHTTPD {

    public WebServer(int port) {
      super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
      String uri = session.getUri();
      if (uri.endsWith("/")) {
        uri += indexName;
      }
      String filePath = rootFolder + uri;

      try {
        if (new File(filePath).exists()) {
          FileInputStream fis = new FileInputStream(filePath);

          int contentLength = fis.available();

          return newFixedLengthResponse(
              NanoHTTPD.Response.Status.OK, getMimeTypeForFile(filePath), fis, contentLength);
        } else {
          return newFixedLengthResponse(
              NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "File not found.");
        }
      } catch (IOException e) {
        ILogger.error("SimpleHttpServer", e);
        e.printStackTrace();
        return newFixedLengthResponse(
            NanoHTTPD.Response.Status.INTERNAL_ERROR,
            NanoHTTPD.MIME_PLAINTEXT,
            "Internal server error.");
      }
    }
  }
}
