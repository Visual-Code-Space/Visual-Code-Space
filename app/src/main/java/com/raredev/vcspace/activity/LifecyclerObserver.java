package com.raredev.vcspace.activity;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import com.raredev.vcspace.actions.main.EditActionGroup;
import com.raredev.vcspace.actions.main.FileActionGroup;
import com.raredev.vcspace.actions.main.filetab.*;
import com.raredev.vcspace.actions.main.other.*;
import com.raredev.vcspace.actions.main.text.*;
import com.raredev.vcspace.fragments.filemanager.actions.file.*;
import com.raredev.vcspace.fragments.filemanager.actions.git.*;
import com.raredev.vcspace.fragments.filemanager.actions.topbar.*;
import com.vcspace.actions.ActionManager;

public class LifecyclerObserver implements DefaultLifecycleObserver {

  @Override
  public void onCreate(LifecycleOwner owner) {
    registerActions();
  }

  @Override
  public void onResume(LifecycleOwner owner) {
    registerActions();
  }

  @Override
  public void onPause(LifecycleOwner owner) {
    unregisterActions();
  }

  private void unregisterActions() {
    ActionManager.getInstance().clear();
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
}
