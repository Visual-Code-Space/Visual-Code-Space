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

package com.vcspace.plugins

import java.io.Serializable

data class Script @JvmOverloads constructor(
  val name: String,
  val entryPoint: String = "main"
) : Serializable

data class Manifest @JvmOverloads constructor(
  val name: String,
  val packageName: String,
  val scripts: Array<Script> = arrayOf(),
  val versionCode: Int = 1,
  val versionName: String = "1.0.0",
  val author: String = "Unknown",
  val description: String = "No description provided.",
  val enabled: Boolean = true
) : Serializable {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Manifest

    if (name != other.name) return false
    if (packageName != other.packageName) return false
    if (!scripts.contentEquals(other.scripts)) return false
    if (versionCode != other.versionCode) return false
    if (versionName != other.versionName) return false
    if (author != other.author) return false
    if (description != other.description) return false
    if (enabled != other.enabled) return false

    return true
  }

  override fun hashCode(): Int {
    var result = name.hashCode()
    result = 31 * result + packageName.hashCode()
    result = 31 * result + scripts.contentHashCode()
    result = 31 * result + versionCode
    result = 31 * result + versionName.hashCode()
    result = 31 * result + author.hashCode()
    result = 31 * result + description.hashCode()
    result = 31 * result + enabled.hashCode()
    return result
  }
}
