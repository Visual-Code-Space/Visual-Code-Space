package com.raredev.vcspace;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import com.google.android.material.color.DynamicColors;
import com.raredev.vcspace.actions.main.EditActionGroup;
import com.raredev.vcspace.actions.main.FileActionGroup;
import com.raredev.vcspace.actions.main.filetab.*;
import com.raredev.vcspace.actions.main.other.*;
import com.raredev.vcspace.actions.main.text.*;
import com.raredev.vcspace.fragments.SettingsFragment;
import com.raredev.vcspace.fragments.filemanager.actions.file.*;
import com.raredev.vcspace.fragments.filemanager.actions.git.*;
import com.raredev.vcspace.fragments.filemanager.actions.topbar.*;
import com.raredev.vcspace.util.ILogger;
import com.vcspace.actions.ActionManager;
import io.github.rosemoe.sora.langs.textmate.provider.TextMateProvider;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;

public class VCSpaceApplication extends Application {

  private static VCSpaceApplication instance;

  private ShutdownReceiver shutdownReceiver;

  private SharedPreferences defaultPref;

  public static VCSpaceApplication getInstance() {
    return instance;
  }

  @Override
  public void onCreate() {
    instance = this;
    super.onCreate();
    defaultPref = PreferenceManager.getDefaultSharedPreferences(this);
    DynamicColors.applyToActivitiesIfAvailable(this);
    AppCompatDelegate.setDefaultNightMode(SettingsFragment.getThemeFromPrefs());
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    registerShutdownReceiver();
    registerActions();
    loadTextMate();
  }

  @Override
  public void onTerminate() {
    unregisterShutdownReceiver();
    super.onTerminate();
  }

  private void loadTextMate() {
    FileProviderRegistry.getInstance().addFileProvider(new AssetsFileResolver(getAssets()));
    // Load editor languages
    try {
      TextMateProvider.registerLanguages();
    } catch (Exception e) {
      ILogger.error("LanguageLoader", "Error when trying to load languages:", e);
    }
  }

  private void registerActions() {
    ActionManager manager = ActionManager.getInstance();
    manager.clear();
    // Main toolbar
    manager.registerAction(new ExecuteAction());
    manager.registerAction(new UndoAction());
    manager.registerAction(new RedoAction());
    manager.registerAction(new EditActionGroup());
    manager.registerAction(new FileActionGroup());
    manager.registerAction(new ViewLogsAction());
    manager.registerAction(new SettingsAction());

    // File Tab
    manager.registerAction(new CloseFileAction());
    manager.registerAction(new CloseOthersAction());
    manager.registerAction(new CloseAllAction());

    // Git
    manager.registerAction(new CloneRepositoryAction());

    // File manager topbar
    manager.registerAction(new RefreshFilesAction());
    manager.registerAction(new CreateFileAction());
    manager.registerAction(new CreateFolderAction());

    // File manager
    manager.registerAction(new CopyPathAction());
    manager.registerAction(new RenameFileAction());
    manager.registerAction(new DeleteFileAction());
  }

  public SharedPreferences getDefaultPref() {
    return defaultPref;
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
