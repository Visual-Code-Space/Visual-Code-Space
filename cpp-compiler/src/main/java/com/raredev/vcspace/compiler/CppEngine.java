package com.raredev.vcspace.compiler;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.ShellUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.blankj.utilcode.util.ZipUtils;
import com.raredev.vcspace.compiler.executor.CCppExecutor;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class CppEngine {
  private static final String C_COMPILER_DIR = "c_compiler";

  private static final String GCC_VERSION = "7.2.0";

  /** install compiler */
  public static void install(Application application) {
    // Install tools
    Utils.init(application);

    if (checkIntent(application)) {
      return;
    }
    installIntent(application);
  }

  /** Install Intent */
  private static void installIntent(Application application) {
    // Install intent (format c code)
    File filesDir = application.getFilesDir();
    // If it does not exist, decompress it from Assets
    try {
      String dest =
          application.getFilesDir()
              + File.separator
              + C_COMPILER_DIR
              + File.separator
              + "install"
              + File.separator
              + "bin.zip";
      // copy to storage
      ResourceUtils.copyFileFromAssets("c_compiler/bin.zip", dest);
      // decompress
      ZipUtils.unzipFile(dest, filesDir.getAbsolutePath() + File.separator + C_COMPILER_DIR);

      File intent =
          new File(
              filesDir.getAbsolutePath()
                  + File.separator
                  + C_COMPILER_DIR
                  + File.separator
                  + "indent");

      // Change permissions to executable
      changeToExecutable(intent);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Install Gcc
   *
   * @param gccFile gccFile
   */
  public static void installGcc(
      Context context,
      @NonNull final File gccFile,
      @NonNull final OnInstallListener onInstallListener) {
    final String filesDir = context.getFilesDir().getAbsolutePath();
    ThreadUtils.getCachedPool()
        .execute(
            new Runnable() {
              @Override
              public void run() {
                try {
                  // Unzip to the gcc directory of getFilesDir
                  ZipUtils.unzipFile(
                      gccFile.getAbsolutePath(), filesDir + File.separator + C_COMPILER_DIR);

                  // Change permissions after successful decompression
                  File binDir1 =
                      new File(
                          filesDir
                              + File.separator
                              + C_COMPILER_DIR
                              + File.separator
                              + "gcc"
                              + File.separator
                              + "bin");
                  File binDir2 =
                      new File(
                          filesDir
                              + File.separator
                              + C_COMPILER_DIR
                              + File.separator
                              + "gcc"
                              + File.separator
                              + "arm-linux-androideabi"
                              + File.separator
                              + "bin");
                  File binDir3 =
                      new File(
                          filesDir
                              + File.separator
                              + C_COMPILER_DIR
                              + File.separator
                              + "gcc"
                              + File.separator
                              + "libexec/gcc/arm-linux-androideabi/"
                              + GCC_VERSION);
                  // check file
                  if (!binDir1.exists() || !binDir2.exists() || !binDir3.exists()) {
                    ThreadUtils.runOnUiThread(
                        () -> {
                          onInstallListener.onError("GCC documentation is incomplete");
                        });
                    return;
                  }

                  File[] listFiles1 = binDir1.listFiles();
                  if (listFiles1 != null)
                    for (File f : listFiles1) {
                      if (f.isFile()) changeToExecutable(f);
                    }

                  File[] listFiles2 = binDir2.listFiles();
                  if (listFiles2 != null)
                    for (File f : listFiles2) {
                      if (f.isFile()) changeToExecutable(f);
                    }

                  File[] listFiles3 = binDir3.listFiles();
                  if (listFiles3 != null)
                    for (File f : listFiles3) {
                      if (f.isFile()) changeToExecutable(f);
                    }

                  ThreadUtils.runOnUiThread(
                      () -> {
                        onInstallListener.onSuccess();
                      });
                } catch (final Exception e) {
                  e.printStackTrace();
                  ThreadUtils.runOnUiThread(
                      () -> {
                        onInstallListener.onError(e.toString());
                      });
                }
              }
            });
  }

  /** Detect GCC environment */
  public static boolean checkGcc(Context context) {
    File gccFile =
        new File(
            context.getFilesDir().getAbsolutePath()
                + File.separator
                + C_COMPILER_DIR
                + File.separator
                + "gcc");
    return gccFile.exists() && gccFile.listFiles() != null;
  }

  /** Detect Intent environment */
  public static boolean checkIntent(Context context) {
    File intent =
        new File(
            context.getFilesDir().getAbsolutePath()
                + File.separator
                + C_COMPILER_DIR
                + File.separator
                + "indent");
    return intent.exists();
  }

  /** C compiler */
  private static volatile CCompiler cCompiler;

  public static CCompiler getCCompiler() {
    if (cCompiler == null) {
      synchronized (CCompiler.class) {
        if (cCompiler == null) {
          cCompiler = new CCompiler();
        }
      }
    }
    return cCompiler;
  }

  /** CPP compiler */
  private static volatile CppCompiler cppCompiler;

  public static CppCompiler getCppCompiler() {
    if (cppCompiler == null) {
      synchronized (CppCompiler.class) {
        if (cppCompiler == null) {
          cppCompiler = new CppCompiler();
        }
      }
    }
    return cppCompiler;
  }

  /** CPP translater */
  private static volatile CCppExecutor CppExecutor;

  public static CCppExecutor getCExecutor() {
    if (CppExecutor == null) {
      synchronized (CCppExecutor.class) {
        if (CppExecutor == null) {
          CppExecutor = new CCppExecutor();
        }
      }
    }
    return CppExecutor;
  }

  /** Get the directory of the compiler */
  public static String getCompilerDirPath(Context context) {
    return context.getFilesDir().getAbsolutePath() + File.separator + C_COMPILER_DIR;
  }

  /**
   * Change a file to be executable
   *
   * @param file file
   */
  public static void changeToExecutable(File file) {
    ShellUtils.execCmd("chmod 777 " + file.getAbsolutePath(), false);
  }

  private static class InterceptorOutputStream extends PrintStream {
    private ArrayList<PrintStream> streams;

    InterceptorOutputStream(@NonNull OutputStream file, ArrayList<PrintStream> streams) {
      super(file, true);
      this.streams = streams;
    }

    public void add(PrintStream out) {
      this.streams.add(out);
    }

    public void remove(PrintStream out) {
      this.streams.remove(out);
    }

    @Override
    public void write(@NonNull byte[] buf, int off, int len) {
      super.write(buf, off, len);
      if (streams != null) {
        for (PrintStream printStream : streams) {
          printStream.write(buf, off, len);
        }
      }
    }
  }

  public interface OnInstallListener {
    void onSuccess();

    void onError(String error);
  }
}
