package com.raredev.vcspace.events

import java.io.File

data class OnDeleteFileEvent(val file: File)

data class OnRenameFileEvent(val oldFile: File, val newFile: File)
