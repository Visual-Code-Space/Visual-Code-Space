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

package com.raredev.vcspace.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.widget.TintTypedArray

/**
 * Retrieves the color value associated with a given attribute resource from the current theme.
 *
 * @return The color value associated with the attribute, as defined in the current theme.
 */
@SuppressLint("RestrictedApi")
@ColorInt
fun Context.getAttrColor(@AttrRes attr: Int): Int {
  return TintTypedArray.obtainStyledAttributes(this, null, intArrayOf(attr), 0, 0)
    .getColorStateList(0)
    .defaultColor
}
