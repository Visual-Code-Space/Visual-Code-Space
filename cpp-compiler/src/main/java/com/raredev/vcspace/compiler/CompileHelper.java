package com.raredev.vcspace.compiler;

import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class CompileHelper {
  private CompileHelper() {
    throw new AssertionError();
  }

  /**
   * Execute shell commands with environment variables
   *
   * @param command shell command
   * @param args parameter
   * @param env environment variable
   * @return 
   */
  static CommandResult execCommand(String command, List<String> args, Map<String, String> env) {
    int result = -1;
    if (command == null || command.length() == 0) {
      return new CommandResult(result, "Shell command does not exist");
    }

    Process process = null;
    BufferedReader inputBufferedReader = null;
    StringBuilder msg = null;
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(command);
      List<String> argsList = processBuilder.command();
      argsList.addAll(args);
      processBuilder.redirectErrorStream(true); // Combine input and error streams
      Map<String, String> map = processBuilder.environment();
      map.clear();
      for (Map.Entry<String, String> entry : env.entrySet()) {
        map.put(entry.getKey(), entry.getValue());
      }
      process = processBuilder.start();
      result = process.waitFor();
      msg = new StringBuilder();

      inputBufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

      String s;
      while ((s = inputBufferedReader.readLine()) != null) {
        msg.append(s);
        msg.append("\n");
      }
    } catch (Exception e) {
      e.printStackTrace();
      if (msg != null) msg.append(e.toString());
    } finally {
      try {
        if (inputBufferedReader != null) {
          inputBufferedReader.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

      if (process != null) {
        process.destroy();
      }
    }
    return new CommandResult(result, msg == null ? "" : msg.toString());
  }

  public static class CommandResult {
    private int result;
    private String msg;

    CommandResult(int result) {
      this.result = result;
    }

    CommandResult(int result, String msg) {
      this.result = result;
      this.msg = msg;
    }

    public String getMsg() {
      return msg;
    }

    public boolean isOk() {
      return result == 0;
    }
  }

  /*    public static void compile(final File cFile, CompilerListener compilerListener) {
      // Find the error line number
      Matcher matcher = Pattern.compile(":(\\d+):").matcher("");
      String pos = "";
      if (matcher.find()) {
          pos = matcher.group(1);
      }
  }*/

}
