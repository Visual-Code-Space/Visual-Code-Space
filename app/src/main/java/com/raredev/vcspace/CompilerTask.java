package com.raredev.vcspace;

iimportmport android.app.ProgressDialog;
import android.content.Context;

import com.blankj.utilcode.util.FileUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CompilerTask {
  private Context mContext;
  private ProgressDialog mProgressDialog;

  public CompilerTask(Context context) {
    mContext = context;
  }

  public void compileCpp(String filePath, final OnCompleteListener<String> onCompleteListener) {
    mProgressDialog = new ProgressDialog(mContext);
    mProgressDialog.setMessage("Compiling C++ file...");
    mProgressDialog.setIndeterminate(false);
    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    mProgressDialog.setCancelable(false);
    mProgressDialog.show();

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<String> future =
        executor.submit(
            () -> {
              String result = "";
              try {
                // Navigate to the external storage directory
                String cmd =
                    "cd " + FileUtils.getDirName(FileUtils.getFileByPath(filePath).getParentFile());

                // Install GCC if necessary
                cmd += " && pkg install -y gcc";

                // Compile the C++ file using GCC
                cmd +=
                    " && g++ "
                        + FileUtils.getFileName(filePath)
                        + " -o "
                        + FileUtils.getFileName(filePath).replace(".cpp", "");

                // Run the compilation command and capture the output
                Process process = Runtime.getRuntime().exec(new String[] {"sh", "-c", cmd});
                BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                int progress = 0;
                while ((line = reader.readLine()) != null) {
                  result += line + "\n";
                  progress++;
                  mProgressDialog.setProgress(progress);
                }
import android.app.ProgressDialog;
import android.content.Context;
import com.blankj.utilcode.util.FileUtils;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
* How to using?
* CompilerTask compilerTask = new CompilerTask(this);
		compilerTask.compileCpp(filePath, new CompilerTask.OnCompleteListener<String>() {
			@Override
			public void onComplete(String result) {
				// نتیجه کامپایل شده فایل C++ در اینجا قرار می گیرد
				//Result Cpp Code
			}
		});

*/

public class CompilerTask {
	private Context mContext;
	private ProgressDialog mProgressDialog;

	public CompilerTask(Context context) {
		mContext = context;
	}

	public void compileCpp(String filePath, final OnCompleteListener<String> onCompleteListener) {
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage("Compiling C++ file...");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();

		AsyncTask.execute(() -> {
			String result = "";
			try {
				// Extract the external storage directory path
				String externalStoragePath = Environment.getExternalStorageDirectory().getPath();

				// Navigate to the external storage directory
				String cmd = "cd " + externalStoragePath;

				// Extract the app's media directory path
				String mediaPath = externalStoragePath + "/Android/media/" + mContext.getPackageName();

				// Extract the required files to the app's media directory
				cmd += " && tar -xvf " + mediaPath + "/files.tar -C " + mediaPath;

				// Install GCC if necessary
				cmd += " && pkg install -y gcc";

				// Compile the C++ file using GCC
				cmd += " && g++ " + FileUtils.getFileName(filePath) + " -o "
						+ FileUtils.getFileName(filePath).replace(".cpp", "");

				// Run the compilation command and capture the output
				Process process = Runtime.getRuntime().exec(new String[] { "sh", "-c", cmd });
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				int progress = 0;
				while ((line = reader.readLine()) != null) {
					result += line + "\n";
					progress++;
					mProgressDialog.setProgress(progress);
				}
			} catch (IOException e) {
				e.printStackTrace();
				result = e.getMessage();
			}
			mProgressDialog.dismiss();
			onCompleteListener.onComplete(result);
		});
	}

	public interface OnCompleteListener<T> {
		void onComplete(T result);
	}
}
