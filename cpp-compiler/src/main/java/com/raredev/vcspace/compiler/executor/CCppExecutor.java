package com.raredev.vcspace.compiler.executor;

import com.raredev.vcspace.compiler.CppEngine;
import java.io.File;
import com.raredev.vcspace.compiler.listener.ExecuteListener;
import androidx.annotation.NonNull;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.util.Log;

public class CCppExecutor {
  private Process currentProcess;
  private static boolean OutFinishFlag = true;
  private static boolean ErrFinishFlag = true;
  private static boolean WaitFinishFlag = true;
  private static int waitFor = -1;
  private static int exitValue = -1;

  /**
   * run the binary
   *
   * @param cFile binary file
   * @param executeListener Running result monitoring
   */
  public void exec(final File cFile, @NonNull final ExecuteListener executeListener) {
    CppEngine.changeToExecutable(cFile);

    if (currentProcess != null) {
      currentProcess.destroy();
      currentProcess = null;
    }

    new Thread(
            new Runnable() {
              @Override
              public void run() {
                reSetFlag();
                try {
                  currentProcess = Runtime.getRuntime().exec(cFile.getAbsolutePath());

                  // Callback program process
                  executeListener.onExecuteStart(currentProcess);

                  // extract output
                  Thread outThread =
                      new Thread(new OutputRunnable(currentProcess, executeListener));
                  outThread.start();
                  Thread errThread = new Thread(new ErrRunnable(currentProcess, executeListener));
                  errThread.start();

                  // 等待结束进程
                  waitFor = currentProcess.waitFor();
                  exitValue = currentProcess.exitValue();

                  // 检测所有进程是否完成
                  WaitFinishFlag = true;
                  checkFinish(executeListener);
                } catch (Exception e) {
                  executeListener.printStderr(e);
                }
              }
            })
        .start();
  }

  private static void reSetFlag() {
    OutFinishFlag = false;
    ErrFinishFlag = false;
    WaitFinishFlag = false;
    waitFor = -1;
    exitValue = -1;
  }

  /**
   * 向进程输入
   *
   * @param stdin 输入内容
   */
  public void input(String stdin) {
    if (currentProcess == null) {
      return;
    }
    new Thread(new CCppExecutor.InputRunnable(currentProcess, stdin)).start();
  }

  /** 进程输入线程 */
  public static class InputRunnable implements Runnable {
    private final String stdin;
    private final Process process;

    InputRunnable(Process process, String stdin) {
      this.process = process;
      this.stdin = stdin;
    }

    public void run() {
      OutputStream fos = process.getOutputStream();
      PrintStream ps = new PrintStream(fos);
      ps.print(stdin + "\n");
      ps.flush();
      ps.close();
    }
  }

  /** 进程输出线程 */
  public static class OutputRunnable implements Runnable {
    private final ExecuteListener executeListener;
    private final Process process;

    OutputRunnable(@NonNull Process process, @NonNull ExecuteListener executeListener) {
      this.process = process;
      this.executeListener = executeListener;
    }

    public void run() {
      try {
        InputStream ios = process.getInputStream();
        BufferedReader brd = new BufferedReader(new InputStreamReader(ios));

        String line;
        while ((line = brd.readLine()) != null) {
          executeListener.printStdout(line + "\n");
        }
      } catch (Exception e) {
        // e.printStackTrace();
      }
      Log.e("output", "Finish");
      OutFinishFlag = true;
      checkFinish(executeListener);
    }
  }

  /** 进程错误输出线程 */
  public static class ErrRunnable implements Runnable {
    private final ExecuteListener executeListener;
    private final Process process;

    ErrRunnable(@NonNull Process process, @NonNull ExecuteListener executeListener) {
      this.process = process;
      this.executeListener = executeListener;
    }

    public void run() {
      try {
        InputStream ios = process.getErrorStream();

        BufferedReader brd = new BufferedReader(new InputStreamReader(ios));
        String line;
        while ((line = brd.readLine()) != null) {
          executeListener.printStderr(new Throwable(line + "\n"));
        }
      } catch (Exception e) {
        // e.printStackTrace();
      }
      Log.e("错误", "完成");
      ErrFinishFlag = true;
      checkFinish(executeListener);
    }
  }

  private static boolean checkFinish() {
    return OutFinishFlag && ErrFinishFlag && WaitFinishFlag;
  }

  private static void checkFinish(ExecuteListener executeListener) {
    if (checkFinish()) {
      executeListener.onExecuteFinish(waitFor, exitValue);
    }
  }
}
