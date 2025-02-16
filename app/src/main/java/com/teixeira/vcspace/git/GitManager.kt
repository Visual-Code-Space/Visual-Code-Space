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

import com.teixeira.vcspace.extensions.toFile
import com.teixeira.vcspace.github.auth.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.CreateBranchCommand
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
import org.eclipse.jgit.errors.IncorrectObjectTypeException
import org.eclipse.jgit.errors.MissingObjectException
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.SubmoduleConfig
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File
import java.io.IOException

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
            .setProgressMonitor(object : SimpleProgressMonitor() {
                override fun onUpdate(progress: Int, taskName: String) {
                    onUpdate(progress, taskName)
                }
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

    fun getDefaultBranch(): String? {
        return git.repository.branch
    }

    fun addMainBranch() {
        if (getBranches().contains(VCSGitConstants.MAIN)) return

        if (getBranches().contains(GitConstants.MASTER)) {
            git.branchRename()
                .setOldName(GitConstants.MASTER)
                .setNewName(VCSGitConstants.MAIN)
                .call()
        } else {
            git.branchCreate()
                .setName(VCSGitConstants.MAIN)
                .setForce(true)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
                .call()
        }
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
        userInfo: UserInfo,
        amend: Boolean = false,
        sign: Boolean = false,
        vararg only: String
    ) {
        git.commit().apply {
            setMessage(message)
            setAmend(amend)
            setSign(sign)
            setCredentialsProvider(
                UsernamePasswordCredentialsProvider(
                    userInfo.user.username,
                    userInfo.accessToken.accessToken
                )
            )
            author = PersonIdent(userInfo.user.name, userInfo.user.email)

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
        password: String,
        remoteName: String = GitConstants.DEFAULT_REMOTE_NAME,
        branchName: String = VCSGitConstants.MAIN,
        onUpdate: (progress: Int, taskName: String) -> Unit = { _, _ -> }
    ) {
        git.push()
            .setRemote(remoteName)
            .add("refs/heads/$branchName")
            .setPushOptions(listOf("--set-upstream"))
            .setCredentialsProvider(UsernamePasswordCredentialsProvider(username, password))
            .setProgressMonitor(object : SimpleProgressMonitor() {
                override fun onUpdate(progress: Int, taskName: String) {
                    onUpdate(progress, taskName)
                }
            })
            .call()
    }

    fun fetch(
        username: String,
        password: String,
        remoteName: String = Constants.DEFAULT_REMOTE_NAME,
        onUpdate: (progress: Int, taskName: String) -> Unit = { _, _ -> }
    ) {
        git.fetch()
            .setRemote(remoteName)
            .setProgressMonitor(object : SimpleProgressMonitor() {
                override fun onUpdate(progress: Int, taskName: String) {
                    onUpdate(progress, taskName)
                }
            })
            .setCredentialsProvider(UsernamePasswordCredentialsProvider(username, password))
            .call()
    }

    fun refresh(
        username: String,
        password: String,
        force: Boolean = false,
        remoteName: String = Constants.DEFAULT_REMOTE_NAME,
        onUpdate: (progress: Int, taskName: String) -> Unit = { _, _ -> }
    ) {
        git.fetch()
            .setCheckFetchedObjects(true)
            .setRecurseSubmodules(SubmoduleConfig.FetchRecurseSubmodulesMode.ON_DEMAND)
            .setRemote(remoteName)
            .setRemoveDeletedRefs(true)
            .setProgressMonitor(object : SimpleProgressMonitor() {
                override fun onUpdate(progress: Int, taskName: String) {
                    onUpdate(progress, taskName)
                }
            })
            .setCredentialsProvider(UsernamePasswordCredentialsProvider(username, password))
            .setForceUpdate(force)
            .call()
    }

    fun pull(
        username: String,
        password: String,
        remoteName: String = Constants.DEFAULT_REMOTE_NAME,
        onUpdate: (progress: Int, taskName: String) -> Unit = { _, _ -> }
    ) {
        git.pull()
            .setRemote(remoteName)
            .setProgressMonitor(object : SimpleProgressMonitor() {
                override fun onUpdate(progress: Int, taskName: String) {
                    onUpdate(progress, taskName)
                }
            })
            .setCredentialsProvider(UsernamePasswordCredentialsProvider(username, password))
            .call()
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

    fun getUncommittedChangesStats(
        onUpdate: (progress: Int, taskName: String) -> Unit = { _, _ -> }
    ): ChangeStats {
        val status = git.status()
            .setProgressMonitor(object : SimpleProgressMonitor() {
                override fun onUpdate(progress: Int, taskName: String) {
                    onUpdate(progress, taskName)
                }
            }).call()

        val changes = status.uncommittedChanges
        return ChangeStats(changes.size, 0, 0)
    }

    suspend fun getLastCommitMessage(): String = withContext(Dispatchers.IO) {
        RevWalk(git.repository).use { revWalk ->
            val headCommit = git.repository.resolve("HEAD")
            val commit = revWalk.parseCommit(headCommit)
            commit.fullMessage
        }
    }
}

typealias VCSGit = GitManager
typealias GitConstants = Constants
