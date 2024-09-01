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

package com.teixeira.vcspace.extensions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat

fun <T> Context.open(clazz: Class<T>, newTask: Boolean = false) {
  val intent = Intent(this, clazz)
  if (newTask)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

  startActivity(intent, getEmptyActivityBundle())
}

fun Context.getEmptyActivityBundle(): Bundle? {
  return ActivityOptionsCompat.makeCustomAnimation(
    this,
    android.R.anim.fade_in,
    android.R.anim.fade_out
  ).toBundle()
}