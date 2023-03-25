package com.raredev.vcspace;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.color.DynamicColors;
import com.raredev.common.util.ILogger;
import com.raredev.vcspace.actions.ActionManager;
import com.raredev.vcspace.actions.editor.CloseAllAction;
import com.raredev.vcspace.actions.editor.CloseFileAction;
import com.raredev.vcspace.actions.editor.CloseOthersAction;
import com.raredev.vcspace.actions.file.CopyPathAction;
import com.raredev.vcspace.actions.file.CreateFileAction;
import com.raredev.vcspace.actions.file.CreateFolderAction;
import com.raredev.vcspace.actions.file.DeleteFileAction;
import com.raredev.vcspace.actions.file.RenameFileAction;
import com.raredev.vcspace.fragments.SettingsFragment;
import com.raredev.vcspace.util.PreferencesUtils;

public class VCSpaceApplication extends Application {
  public static Context appContext;
  private ShutdownReceiver shutdownReceiver;

  @Override
  public void onCreate() {
    super.onCreate();
    registerShutdownReceiver();
    appContext = this;
    AppCompatDelegate.setDefaultNightMode(SettingsFragment.getThemeFromPrefs());
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
    registerActions();
  }

  private void registerActions() {
    ActionManager manager = ActionManager.getInstance();
    manager.clear();
    // Editor
    manager.registerAction(new CloseFileAction());
    manager.registerAction(new CloseOthersAction());
    manager.registerAction(new CloseAllAction());

    // File tree
    manager.registerAction(new CopyPathAction());
    manager.registerAction(new CreateFileAction());
    manager.registerAction(new CreateFolderAction());
    manager.registerAction(new RenameFileAction());
    manager.registerAction(new DeleteFileAction());
  }

  @Override
  public void onTerminate() {
    unregisterShutdownReceiver();
    super.onTerminate();
  }

  private void registerShutdownReceiver() {
    shutdownReceiver = new ShutdownReceiver();
    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SHUTDOWN);
    registerReceiver(shutdownReceiver, intentFilter);
  }

  private void unregisterShutdownReceiver() {
    unregisterReceiver(shutdownReceiver);
  }

  private static class ShutdownReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      ILogger.clear();
    }
  }
}
