package com.raredev.vcspace.interfaces

import java.io.File

interface IEditorPanel {

  fun setModified(modified: Boolean)

  fun isModified(): Boolean

  fun setFile(file: File)

  fun getFile(): File?

  fun undo()

  fun redo()

  fun canUndo(): Boolean

  fun canRedo(): Boolean

  fun release()

  fun saveFile()

  fun beginSearcher()

  fun setLoading(loading: Boolean)
}
