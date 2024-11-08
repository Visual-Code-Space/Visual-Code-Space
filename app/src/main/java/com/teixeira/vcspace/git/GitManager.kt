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

package com.teixeira.vcspace.git

import com.teixeira.vcspace.app.DoNothing
import com.teixeira.vcspace.extensions.toFile
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.BatchingProgressMonitor
import java.time.Duration

class GitManager private constructor() {
  companion object {
    @JvmStatic
    val instance by lazy { GitManager() }
  }

  private var _git: Git? = null
  val git get() = checkNotNull(_git) { "Git is not initialized." }

  fun init(git: Git) {
    _git = git
  }

  @JvmOverloads
  fun clone(
    url: String,
    destination: String,
    onUpdate: (progress: Int, taskName: String) -> Unit = { _, _ -> }
  ): Git {
    _git = Git.cloneRepository()
      .setURI(url)
      .setProgressMonitor(object : BatchingProgressMonitor() {
        override fun onUpdate(
          taskName: String,
          workCurr: Int,
          duration: Duration
        ) = DoNothing

        override fun onUpdate(
          taskName: String,
          workCurr: Int,
          workTotal: Int,
          percentDone: Int,
          duration: Duration
        ) {
          onUpdate(percentDone, taskName)
        }

        override fun onEndTask(
          taskName: String,
          workCurr: Int,
          duration: Duration
        ) = DoNothing

        override fun onEndTask(
          taskName: String,
          workCurr: Int,
          workTotal: Int,
          percentDone: Int,
          duration: Duration
        ) = DoNothing
      })
      .setDirectory(destination.toFile())
      .call()
    return git
  }
}