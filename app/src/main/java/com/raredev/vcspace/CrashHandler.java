package com.raredev.vcspace;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import com.raredev.vcspace.activity.CrashActivity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

  // Declare variables
  private String newLine = "\n";
  private StringBuilder errorMessage = new StringBuilder();
  private StringBuilder softwareInfo = new StringBuilder();
  private StringBuilder dateInfo = new StringBuilder();

  private Context context;
  
  public CrashHandler(Context context) {
    this.context = context;
  }
  
  @Override
  public void uncaughtException(Thread thread, Throwable exception) {
    // Create a StringWriter to write stack trace to
    var stackTrace = new StringWriter();

    // Print the stack trace to the StringWriter
    exception.printStackTrace(new PrintWriter(stackTrace));

    // Append the stack trace to the error message
    errorMessage.append(stackTrace.toString());

    // Append software information to the software info
    softwareInfo
        .append("SDK: ")
        .append(Build.VERSION.SDK_INT)
        .append(newLine)
        .append("Android: ")
        .append(Build.VERSION.RELEASE)
        .append(newLine)
        .append("Model: ")
        .append(Build.VERSION.INCREMENTAL)
        .append(newLine);

    // Append the date information to the date info
    dateInfo.append(Calendar.getInstance().getTime()).append(newLine);

    // Log the error message, software info, and date info
    Log.d("Error", errorMessage.toString());
    Log.d("Software", softwareInfo.toString());
    Log.d("Date", dateInfo.toString());

    // Create an intent for the crash activity
    var intent = new Intent(context, CrashActivity.class);

    // Add the error message, software info, and date info as extras
    intent.putExtra("Error", errorMessage.toString());
    intent.putExtra("Software", softwareInfo.toString());
    intent.putExtra("Date", dateInfo.toString());

    // Start the crash activity
    context.startActivity(intent);

    // Kill the process
    Process.killProcess(Process.myPid());

    // Exit with a code of 2
    System.exit(2);
  }
}
