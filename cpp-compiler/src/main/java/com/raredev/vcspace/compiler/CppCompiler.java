package com.raredev.vcspace.compiler;

import android.content.Context;
import androidx.annotation.NonNull;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.raredev.vcspace.compiler.listener.CompileCallback;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CppCompiler {

  public void compile(
      @NonNull final List<File> src, @NonNull final CompileCallback compileCallback) {
    final Context context = Utils.getApp().getApplicationContext();
    ThreadUtils.executeByCached(
        new ThreadUtils.SimpleTask<String>() {
          @Override
          public String doInBackground() throws Throwable {

            if (!CppEngine.checkGcc(context)) {
              throw new Throwable("GCC environment does not exist");
            }

            final String SYS_PATH = System.getenv("PATH");
            final String GCC_BIN_PATH =
                CppEngine.getCompilerDirPath(context)
                    + File.separator
                    + "gcc"
                    + File.separator
                    + "bin";
            final String ARM_GCC_PATH =
                CppEngine.getCompilerDirPath(context)
                    + File.separator
                    + "gcc"
                    + File.separator
                    + "arm-linux-androideabi"
                    + File.separator
                    + "bin";
            final String OUT_FILE_PATH =
                CppEngine.getCompilerDirPath(context) + File.separator + "temp";

            List<String> args = new ArrayList<>();
            for (File file : src) {
              args.add(file.getAbsolutePath());
            }
            args.add("-Wfatal-errors"); // stop on error
            args.add("-pie");
            args.add("-std=c++14");
            args.add("-lz");
            args.add("-ldl");
            args.add("-lm");
            args.add("-llog");
            args.add("-lncurses");
            args.add("-Og");
            args.add("-o");
            args.add(OUT_FILE_PATH);

            String TEMPEnv = CppEngine.getCompilerDirPath(context) + "/gcc/tmpdir";
            String PATHEnv =
                CppEngine.getCompilerDirPath(context)
                    + File.pathSeparator
                    + GCC_BIN_PATH
                    + File.pathSeparator
                    + ARM_GCC_PATH
                    + File.pathSeparator
                    + SYS_PATH;
            Map<String, String> envMap = new HashMap<>();
            envMap.put("PATH", PATHEnv);
            envMap.put("TEMP", TEMPEnv);

            // compile command
            String cmd = "." + GCC_BIN_PATH + File.separator + "arm-linux-androideabi-g++";

            // operation result
            CompileHelper.CommandResult result = CompileHelper.execCommand(cmd, args, envMap);

            // Compile Error
            if (!result.isOk()) {
              throw new Throwable(result.getMsg());
            }

            // return output file
            return OUT_FILE_PATH;
          }

          @Override
          public void onFail(Throwable t) {
            compileCallback.onError(t.getMessage());
          }

          @Override
          public void onSuccess(String result) {
            compileCallback.onSuccess(result);
          }
        });
  }
}
