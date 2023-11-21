package com.raredev.vcspace.editor.completion

import android.view.ViewGroup
import com.raredev.vcspace.res.R
import io.github.rosemoe.sora.widget.component.DefaultCompletionLayout
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

class CustomCompletionLayout : DefaultCompletionLayout() {

  override fun onApplyColorScheme(colorScheme: EditorColorScheme) {
    (completionList.parent as? ViewGroup)?.setBackgroundResource(R.drawable.toast_bg)
  }
}
