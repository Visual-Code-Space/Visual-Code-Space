package com.raredev.vcspace.activity;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import com.raredev.vcspace.actions.ActionManager;
import com.raredev.vcspace.actions.editor.CloseAllAction;
import com.raredev.vcspace.actions.editor.CloseFileAction;
import com.raredev.vcspace.actions.editor.CloseOthersAction;
import com.raredev.vcspace.actions.file.CopyPathAction;
import com.raredev.vcspace.actions.file.CreateFileAction;
import com.raredev.vcspace.actions.file.CreateFolderAction;
import com.raredev.vcspace.actions.file.DeleteFileAction;
import com.raredev.vcspace.actions.file.RenameFileAction;
import com.raredev.vcspace.actions.main.EditActionGroup;
import com.raredev.vcspace.actions.main.FileActionGroup;
import com.raredev.vcspace.actions.main.other.ExecuteAction;
import com.raredev.vcspace.actions.main.other.OpenDrawerAction;
import com.raredev.vcspace.actions.main.text.RedoAction;
import com.raredev.vcspace.actions.main.text.UndoAction;

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
    manager.registerAction(new UndoAction());
    manager.registerAction(new RedoAction());
    manager.registerAction(new ExecuteAction());
    manager.registerAction(new OpenDrawerAction());
    manager.registerAction(new EditActionGroup());
    manager.registerAction(new FileActionGroup());

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
