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
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.api.errors.AbortedByHookException
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.api.errors.InvalidRemoteException
import org.eclipse.jgit.api.errors.NoFilepatternException
import org.eclipse.jgit.api.errors.NoHeadException
import org.eclipse.jgit.api.errors.NoMessageException
import org.eclipse.jgit.api.errors.ServiceUnavailableException
import org.eclipse.jgit.api.errors.TransportException
import org.eclipse.jgit.api.errors.UnmergedPathsException
import org.eclipse.jgit.api.errors.WrongRepositoryStateException
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.errors.IncorrectObjectTypeException
import org.eclipse.jgit.errors.MissingObjectException
import org.eclipse.jgit.lib.BatchingProgressMonitor
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.merge.ContentMergeStrategy
import org.eclipse.jgit.merge.MergeStrategy
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.eclipse.jgit.util.io.NullOutputStream
import java.io.File
import java.io.IOException
import java.time.Duration

class GitManager private constructor() {
  companion object {
    @JvmStatic
    val instance by lazy { GitManager() }

    @JvmStatic
    fun isGitRepository(folder: File): Boolean {
      return try {
        val builder = FileRepositoryBuilder()
        val repository: Repository = builder.setGitDir(File(folder, GitConstants.DOT_GIT))
          .readEnvironment()
          .findGitDir()
          .build()
        repository.objectDatabase.exists()
      } catch (e: IOException) {
        false // Not a Git repository or error accessing it
      }
    }
  }

  private var _git: Git? = null
  val git get() = checkNotNull(_git) { "Git is not initialized." }

  @Throws(IOException::class)
  fun initialize(folder: File) {
    _git = Git.open(folder)
  }

  fun close() {
    _git ?: return
    git.close()
  }

  @Throws(GitAPIException::class, InvalidRemoteException::class, TransportException::class)
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

  @Throws(GitAPIException::class)
  fun init(path: File): Git {
    _git = Git.init().setDirectory(path).call()
    return git
  }

  @Throws(GitAPIException::class)
  fun getBranches(): List<String> {
    return git.branchList().call().map { it.name }
  }

  @Throws(GitAPIException::class)
  fun getUnversionedFiles(): List<String> {
    val status: Status = git.status().call()
    return status.untracked.map { it }
  }

  @Throws(GitAPIException::class)
  fun getChanges(): List<String> {
    val status = git.status().call()
    return status.uncommittedChanges.map { it }
  }

  @Throws(GitAPIException::class, NoFilepatternException::class)
  fun add(file: File) {
    git.add().addFilepattern(file.path).call()
  }

  @Throws(
    GitAPIException::class,
    AbortedByHookException::class,
    ConcurrentRefUpdateException::class,
    NoHeadException::class,
    NoMessageException::class,
    ServiceUnavailableException::class,
    UnmergedPathsException::class,
    WrongRepositoryStateException::class
  )
  fun commit(
    message: String,
    amend: Boolean = false,
    sign: Boolean = false,
    vararg only: String
  ) {
    git.commit().apply {
      setMessage(message)
      setAmend(amend)
      setSign(sign)

      only.forEach { setOnly(it) }
    }.call()
  }

  @Throws(GitAPIException::class, NoFilepatternException::class)
  fun addAll() {
    git.add().addFilepattern(".").call()
  }

  @Throws(GitAPIException::class)
  fun isRemoteExists(remoteName: String): Boolean {
    return git.remoteList().call().any { it.name == remoteName }
  }

  fun addRemote(
    remoteUrl: String,
    remoteName: String = Constants.DEFAULT_REMOTE_NAME
  ) {
    git.remoteAdd().setUri(URIish(remoteUrl)).setName(remoteName).call()
  }

  @Throws(
    GitAPIException::class,
    InvalidRemoteException::class,
    TransportException::class
  )
  fun push(
    username: String,
    password: String
  ) {
    git.push()
      .setRemote(GitConstants.DEFAULT_REMOTE_NAME)
      .setCredentialsProvider(UsernamePasswordCredentialsProvider(username, password))
      .call()
  }

  fun fetch(
    remoteName: String = Constants.DEFAULT_REMOTE_NAME,
    onUpdate: (progress: Int, taskName: String) -> Unit = { _, _ -> }
  ) {
    git.fetch().setRemote(remoteName).setProgressMonitor(object : BatchingProgressMonitor() {
      override fun onUpdate(taskName: String?, workCurr: Int, duration: Duration?) {

      }

      override fun onUpdate(
        taskName: String,
        workCurr: Int,
        workTotal: Int,
        percentDone: Int,
        duration: Duration
      ) {
        onUpdate(percentDone, taskName)
      }

      override fun onEndTask(taskName: String?, workCurr: Int, duration: Duration?) {

      }

      override fun onEndTask(
        taskName: String?,
        workCurr: Int,
        workTotal: Int,
        percentDone: Int,
        duration: Duration?
      ) {

      }
    }).call()
  }

  fun getUncommittedChangesStats(): ChangeStats {
    var totalInsertions = 0
    var totalDeletions = 0
    var totalFilesChanged: Int

    git.use {
      val diffs = it.diff().setShowNameAndStatusOnly(true).call()
      totalFilesChanged = diffs.size

      DiffFormatter(NullOutputStream.INSTANCE).use { diffFormatter ->
        diffFormatter.setRepository(it.repository)

        for (entry in diffs) {
          val editList = diffFormatter.toFileHeader(entry).toEditList()
          for (edit in editList) {
            totalInsertions += edit.endB - edit.beginB
            totalDeletions += edit.endA - edit.beginA
          }
        }
      }
    }

    return ChangeStats(
      filesChanged = totalFilesChanged,
      insertions = totalInsertions,
      deletions = totalDeletions
    )
  }

  fun getRepositoryName(): String? {
    val remoteConfig = git.remoteList().call().firstOrNull()
    val remoteUrl = remoteConfig?.urIs?.firstOrNull()

    if (remoteUrl != null) {
      return remoteUrl.humanishName
    }

    return null // Return null if no remote is set or name cannot be determined
  }

  @Throws(
    MissingObjectException::class,
    IncorrectObjectTypeException::class,
    IOException::class
  )
  fun getUnpushedCommits(remoteName: String = GitConstants.DEFAULT_REMOTE_NAME): List<RevCommit> {
    val unpushedCommits = mutableListOf<RevCommit>()

    val repository = git.repository
    val branch = repository.branch
    val remoteBranchName = "refs/remotes/$remoteName/$branch"

    val remoteBranchRef = repository.findRef(remoteBranchName)
    if (remoteBranchRef == null) {
      println("No remote tracking branch for $branch")
      return emptyList()
    }

    val localBranchId = repository.resolve(branch)
    val remoteBranchId = remoteBranchRef.objectId

    RevWalk(repository).use { revWalk ->
      val localCommit = revWalk.parseCommit(localBranchId)
      val remoteCommit = revWalk.parseCommit(remoteBranchId)
      revWalk.markStart(localCommit)
      revWalk.markUninteresting(remoteCommit)

      unpushedCommits.addAll(revWalk)
    }

    return unpushedCommits
  }

  fun pull(
    username: String,
    password: String,
    onUpdate: (progress: Int, taskName: String) -> Unit = { _, _ -> }
  ) {
    git.pull()
      .setRemote(GitConstants.DEFAULT_REMOTE_NAME)
      .setRemoteBranchName(GitConstants.MASTER)
      .setProgressMonitor(object : BatchingProgressMonitor() {
        override fun onUpdate(taskName: String?, workCurr: Int, duration: Duration?) = DoNothing

        override fun onUpdate(
          taskName: String,
          workCurr: Int,
          workTotal: Int,
          percentDone: Int,
          duration: Duration
        ) {
          onUpdate(percentDone, taskName)
        }

        override fun onEndTask(taskName: String?, workCurr: Int, duration: Duration?) = DoNothing

        override fun onEndTask(
          taskName: String?,
          workCurr: Int,
          workTotal: Int,
          percentDone: Int,
          duration: Duration?
        ) = DoNothing
      })
      .setContentMergeStrategy(ContentMergeStrategy.OURS)
      .setStrategy(MergeStrategy.RESOLVE)
      .setCredentialsProvider(UsernamePasswordCredentialsProvider(username, password))
      .call()
  }

  fun fetch(
    username: String,
    password: String
  ) {
    git.fetch()
      .setRemote(GitConstants.DEFAULT_REMOTE_NAME)
      .setCredentialsProvider(UsernamePasswordCredentialsProvider(username, password))
      .setProgressMonitor(object : BatchingProgressMonitor() {
        override fun onUpdate(taskName: String?, workCurr: Int, duration: Duration?) {
          TODO("Not yet implemented")
        }

        override fun onUpdate(
          taskName: String?,
          workCurr: Int,
          workTotal: Int,
          percentDone: Int,
          duration: Duration?
        ) {
          TODO("Not yet implemented")
        }

        override fun onEndTask(taskName: String?, workCurr: Int, duration: Duration?) {
          TODO("Not yet implemented")
        }

        override fun onEndTask(
          taskName: String?,
          workCurr: Int,
          workTotal: Int,
          percentDone: Int,
          duration: Duration?
        ) {
          TODO("Not yet implemented")
        }

      })
  }
}

typealias VCSGit = GitManager
typealias GitConstants = Constants
