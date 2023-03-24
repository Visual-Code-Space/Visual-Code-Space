package com.raredev.vcspace;

import android.app.Application;
import android.content.Context;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.color.DynamicColors;
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

  @Override
  public void onCreate() {
    super.onCreate();
    appContext = this;
    AppCompatDelegate.setDefaultNightMode(SettingsFragment.getThemeFromPrefs());
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
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
}
