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

package com.teixeira.vcspace.editor.completion

import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.utils.getAttrColor
import io.github.rosemoe.sora.widget.component.DefaultCompletionLayout
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

class CustomCompletionLayout : DefaultCompletionLayout() {
  override fun onApplyColorScheme(colorScheme: EditorColorScheme) {
    (completionList.parent as? ViewGroup)?.background =
      GradientDrawable().apply {
        setStroke(
          2,
          completionList.context.getAttrColor(com.google.android.material.R.attr.colorOutline),
        )
        setColor(
          completionList.context.getAttrColor(com.google.android.material.R.attr.colorSurface)
        )
        setCornerRadius(25f)
      }
  }
}
