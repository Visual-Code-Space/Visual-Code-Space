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

package com.teixeira.vcspace.plugins.helper;

import com.teixeira.vcspace.activities.EditorActivity;
import com.teixeira.vcspace.editor.VCSpaceEditor;
import com.teixeira.vcspace.ui.screens.editor.components.view.CodeEditorView;

import java.io.File;

public class EditorHelper {
  private final EditorActivity editorActivity;

  public EditorHelper(EditorActivity editorActivity) {
    this.editorActivity = editorActivity;
  }

  public CodeEditorView getCurrentEditorView() {
    if (editorActivity.getCurrentEditor() instanceof CodeEditorView) {
      return (CodeEditorView) editorActivity.getCurrentEditor();
    }

    return null;
  }

  public CodeEditorView getEditorViewForFile(File file) {
    return editorActivity.getEditorForFile().invoke(file);
  }

  public VCSpaceEditor getEditorForFile(File file) {
    return getEditorViewForFile(file).getEditor();
  }

  public VCSpaceEditor getCurrentEditor() {
    return getCurrentEditorView().getEditor();
  }

  public void openFile(File file) {
    editorActivity.openFile.invoke(file);
  }

  public void closeFile(int index) {
    editorActivity.closeFile.invoke(index);
  }

  public void closeCurrentFile() {
    closeFile(getSelectedFileIndex());
  }

  public void closeAll() {
    editorActivity.closeAll.invoke();
  }

  public void closeOthers(int index) {
    editorActivity.closeOthers.invoke(index);
  }

  public void closeOthers() {
    closeOthers(getSelectedFileIndex());
  }

  public int getSelectedFileIndex() {
    return editorActivity.getSelectedFileIndex();
  }

  public void saveAll() {
    editorActivity.saveAll.invoke();
  }

  public void saveFile(CodeEditorView editorView) {
    editorActivity.saveFile(editorView);
  }

  public void saveFile() {
    editorActivity.saveFile();
  }

  public boolean canEditorHandleCurrentKeyBinding() {
    return editorActivity.getCanEditorHandleCurrentKeyBinding();
  }
}
