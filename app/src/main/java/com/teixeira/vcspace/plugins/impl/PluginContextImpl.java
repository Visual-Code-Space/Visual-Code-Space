/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.teixeira.vcspace.plugins.impl;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.itsvks.monaco.MonacoEditor;
import com.teixeira.vcspace.activities.EditorActivity;
import com.teixeira.vcspace.core.MenuManager;
import com.teixeira.vcspace.file.JavaFileWrapperKt;
import com.teixeira.vcspace.keyboard.CommandPaletteManager;
import com.teixeira.vcspace.keyboard.model.Command;
import com.teixeira.vcspace.ui.screens.editor.EditorViewModel;
import com.teixeira.vcspace.ui.screens.editor.components.view.CodeEditorView;
import com.vcspace.plugins.Editor;
import com.vcspace.plugins.PluginContext;
import com.vcspace.plugins.command.EditorCommand;
import com.vcspace.plugins.editor.Position;
import com.vcspace.plugins.menu.MenuItem;

import java.io.File;

public class PluginContextImpl implements PluginContext {
  private final Context context;
  private final EditorActivity activity;
  private final Editor editor;
  private EditorViewModel editorViewModel;

  public PluginContextImpl(EditorActivity editorActivity, EditorViewModel editorViewModel) {
    this.context = editorActivity;
    this.activity = editorActivity;
    this.editorViewModel = editorViewModel;
    this.editor = new EditorImpl(new EditorListener());
  }

  @NonNull
  @Override
  public Context getAppContext() {
    return context;
  }

  @Override
  public void registerCommand(@NonNull EditorCommand command) {
    var commandManager = CommandPaletteManager.getInstance();
    commandManager.addCommand(Command.getNewCommand().invoke(
      command.getName(),
      command.getKeyBinding(),
      (cmd, compositionContext) -> {
        command.execute(editor);
        return null;
      }
    ));
  }

  @Override
  public void addMenu(@NonNull MenuItem menuItem) {
    MenuManager menuManager = MenuManager.getInstance();
    menuManager.addMenu(new com.teixeira.vcspace.core.menu.MenuItem(
      menuItem.getTitle(),
      menuItem.getId(),
      true,
      true,
      menuItem.getShortcut(),
      null,
      null,
      () -> {
        menuItem.getAction().doAction();
        return null;
      }
    ));
  }

  @NonNull
  @Override
  public Editor getEditor() {
    return editor;
  }

  @Override
  public void openFile(@NonNull File file) {
    activity.openFile.invoke(JavaFileWrapperKt.wrapFile(file));
  }

  private class EditorListener implements EditorImpl.Listener {
    @Nullable
    @Override
    public File getCurrentFile() {
      var selectedFile = editorViewModel.getUiState().getValue().getSelectedFile();
      if (selectedFile == null) return null;
      return selectedFile.getFile().asRawFile();
    }

    @NonNull
    @Override
    public Context getContext() {
      return context;
    }

    @NonNull
    @Override
    public Position getCursorPosition() {
      var editor = activity.getCurrentEditor();
      if (editor instanceof MonacoEditor) {
        var position = ((MonacoEditor) editor).getPosition();
        return new Position(position.getLineNumber(), position.getColumn());
      } else if (editor instanceof CodeEditorView) {
        var cursor = ((CodeEditorView) editor).getEditor().getCursor();
        return new Position(cursor.getLeftLine(), cursor.getLeftColumn());
      }
      return new Position();
    }

    @Override
    public void setCursorPosition(@NonNull Position position) {
      var editor = activity.getCurrentEditor();
      if (editor instanceof MonacoEditor) {
        ((MonacoEditor) editor).setPosition(new com.itsvks.monaco.option.Position(position.getLineNumber(), position.getColumn()));
      } else if (editor instanceof CodeEditorView) {
        var cursor = ((CodeEditorView) editor).getEditor().getCursor();
        cursor.set(Math.max(position.getLineNumber() - 1, 0), Math.max(position.getColumn() - 1, 0));
      }
    }
  }

  public void setEditorViewModel(EditorViewModel editorViewModel) {
    this.editorViewModel = editorViewModel;
  }
}
