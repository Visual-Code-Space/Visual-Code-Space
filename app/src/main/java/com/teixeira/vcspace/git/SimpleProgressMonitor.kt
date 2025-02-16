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

import org.eclipse.jgit.lib.BatchingProgressMonitor
import java.time.Duration

abstract class SimpleProgressMonitor : BatchingProgressMonitor() {
    override fun onUpdate(taskName: String?, workCurr: Int, duration: Duration?) {}

    override fun onUpdate(
        taskName: String,
        workCurr: Int,
        workTotal: Int,
        percentDone: Int,
        duration: Duration?
    ) {
        onUpdate(percentDone, taskName)
    }

    override fun onEndTask(taskName: String?, workCurr: Int, duration: Duration?) {}

    override fun onEndTask(
        taskName: String?,
        workCurr: Int,
        workTotal: Int,
        percentDone: Int,
        duration: Duration?
    ) {
    }

    abstract fun onUpdate(progress: Int, taskName: String)
}