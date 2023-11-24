package com.raredev.vcspace.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.FileIOUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.errors.AmbiguousObjectException
import org.eclipse.jgit.errors.IncorrectObjectTypeException
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.merge.MergeStrategy
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.RefSpec
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.EmptyTreeIterator
import org.eclipse.jgit.treewalk.TreeWalk
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.nio.charset.StandardCharsets

class GitUtils {
    private var git: Git? = null
    private val repoPath: File

    constructor(git: Git) {
        this.git = git
        repoPath = git.repository.directory // including ".git" folder
    }

    /**
     * Constructor
     *
     * @param repoPath The path to the Git repository
     * @throws IOException
     */
    constructor(repoPath: File) {
        this.repoPath = repoPath
        open(repoPath)
    }

    @Throws(GitAPIException::class)
    fun init() {
        Git.init().setDirectory(repoPath.parentFile).call()
    }

    @Throws(IOException::class)
    private fun open(repoPath: File) {
        val repo = FileRepositoryBuilder().setGitDir(repoPath).build()
        git = Git(repo)
    }

    /**
     * Create a new branch
     *
     * @param branchName The name of the new branch
     * @throws GitAPIException
     */
    @Throws(GitAPIException::class)
    fun createBranch(branchName: String?) {
        git!!.branchCreate().setName(branchName).call()
    }

    @get:Throws(GitAPIException::class)
    val allBranches: List<Ref>
        /**
         * Get a list of all branches
         *
         * @return A list of all branches
         * @throws GitAPIException
         */
        get() = git!!.branchList().call()

    /**
     * Merge the given branch into the current branch
     *
     * @param branchName The name of the branch to merge
     * @throws GitAPIException
     * @throws IOException
     */
    @Throws(GitAPIException::class, IOException::class)
    fun mergeBranch(branchName: String) {
        git!!.merge().include(git!!.repository.exactRef("refs/heads/$branchName")).call()
    }

    /**
     * Commit changes to the repository
     *
     * @param message The commit message
     * @throws GitAPIException
     */
    @Throws(GitAPIException::class)
    fun commitChanges(message: String?) {
        git!!.commit().setMessage(message).call()
    }

    @get:Throws(
        AmbiguousObjectException::class,
        IncorrectObjectTypeException::class,
        IOException::class
    )
    val currentCommitId: ObjectId
        /**
         * Get the current commit ID
         *
         * @return The current commit ID
         * @throws AmbiguousObjectException
         * @throws IncorrectObjectTypeException
         * @throws IOException
         */
        get() = git!!.repository.resolve("HEAD")

    /**
     * Get the commit message for the given commit ID
     *
     * @param commitId The commit ID
     * @return The commit message
     * @throws IncorrectObjectTypeException
     * @throws IOException
     */
    @Throws(IncorrectObjectTypeException::class, IOException::class)
    fun getCommitMessage(commitId: ObjectId?): String {
        val commit = git!!.repository.parseCommit(commitId)
        return commit.fullMessage
    }

    /**
     * Get the diff between two commits
     *
     * @param oldCommitId The ID of the old commit
     * @param newCommitId The ID of the new commit
     * @return A string representing the diff
     * @throws GitAPIException
     * @throws IOException
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Throws(GitAPIException::class, IOException::class)
    fun getDiff(oldCommitId: ObjectId?, newCommitId: ObjectId?): String {
        val out = ByteArrayOutputStream()
        val diffCommand = git!!.diff().setOutputStream(out)
        if (oldCommitId != null) {
            git!!.repository.newObjectReader().use { reader ->
                val oldTree: AbstractTreeIterator = CanonicalTreeParser(null, reader, oldCommitId)
                diffCommand.setOldTree(oldTree)
            }
        }
        git!!.repository.newObjectReader().use { reader ->
            val newTree: AbstractTreeIterator = CanonicalTreeParser(null, reader, newCommitId)
            diffCommand.setNewTree(newTree).call()
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            out.toString(StandardCharsets.UTF_8)
        } else {
            TODO("VERSION.SDK_INT < TIRAMISU")
        }
    }

    @get:Throws(
        GitAPIException::class,
        IOException::class
    )
    val diff: String
        /**
         * Get the diff between the current commit and the previous commit
         *
         * @return A string representing the diff
         * @throws GitAPIException
         * @throws IOException
         */
        get() {
            val head = git!!.repository.resolve("HEAD")
            val commit = git!!.log().add(head).setMaxCount(1).call().iterator().next()
            val parent = commit.getParent(0)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getDiff(parent.id, head)
            } else {
                TODO("VERSION.SDK_INT < TIRAMISU")
            }
        }

    /**
     * Get the commit ID for the given branch name
     *
     * @param branchName The name of the branch
     * @return The commit ID for the branch
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getBranchCommitId(branchName: String): ObjectId {
        val branchRef = git!!.repository.findRef("refs/heads/$branchName")
        return branchRef.objectId
    }

    /**
     * Cherry-pick the given commit onto the current branch
     *
     * @param commitId The ID of the commit to cherry-pick
     * @throws GitAPIException
     */
    @Throws(GitAPIException::class)
    fun cherryPick(commitId: ObjectId?) {
        git!!.cherryPick().include(commitId).call()
    }

    /**
     * Push changes to remote repository
     *
     * @param remoteName The name of the remote repository to push to
     * @throws GitAPIException
     */
    @Throws(GitAPIException::class)
    fun push(remoteName: String?) {
        git!!.push().setRemote(remoteName).call()
    }

    /**
     * Push all committed changes to a remote branch
     *
     * @param remote The name of the remote
     * @param branch The name of the branch
     * @throws GitAPIException
     */
    @Throws(GitAPIException::class)
    fun push(remote: String?, branch: String?) {
        git!!.push().setRemote(remote).setRefSpecs(RefSpec(branch)).call()
    }

    /**
     * Pull changes from remote repository
     *
     * @param remoteName The name of the remote repository to pull from
     * @throws GitAPIException
     */
    @Throws(GitAPIException::class)
    fun pull(remoteName: String?) {
        git!!.pull().setRemote(remoteName).call()
    }

    /**
     * Pull changes from a remote branch into the current branch
     *
     * @param remote The name of the remote
     * @param branch The name of the branch
     * @throws GitAPIException
     */
    @Throws(GitAPIException::class)
    fun pull(remote: String?, branch: String?) {
        git!!.pull().setRemote(remote).setRemoteBranchName(branch).call()
    }

    /**
     * Add a file or directory to the index
     *
     * @param fileOrDirPath The path to the file or directory to add
     * @throws GitAPIException
     */
    @Throws(GitAPIException::class)
    fun add(fileOrDirPath: String?) {
        git!!.add().addFilepattern(fileOrDirPath).call()
    }

    /**
     * Checkout the given branch
     *
     * @param branchName The name of the branch to checkout
     * @throws GitAPIException
     */
    @Throws(GitAPIException::class)
    fun checkout(branchName: String?) {
        git!!.checkout().setName(branchName).call()
    }

    @get:Throws(GitAPIException::class)
    val status: Status
        /**
         * Get the status of the repository
         *
         * @return A Status object representing the repository status
         * @throws GitAPIException
         */
        get() = git!!.status().call()

    @get:Throws(GitAPIException::class)
    val statusAsString: String
        /**
         * Get the status of the repository as a string
         *
         * @return A string representation of the repository status
         * @throws GitAPIException
         */
        get() {
            val status = git!!.status().call()
            return """
                 Added: ${status.added}
                 Changed: ${status.changed}
                 Conflicting: ${status.conflicting}
                 IgnoredNotInIndex: ${status.ignoredNotInIndex}
                 Missing: ${status.missing}
                 Modified: ${status.modified}
                 Removed: ${status.removed}
                 Untracked: ${status.untracked}
                 UntrackedFolders: ${status.untrackedFolders}
                 
                 """.trimIndent()
        }

    @get:Throws(
        GitAPIException::class,
        IOException::class
    )
    val uncommittedChanges: List<DiffEntry>
        /**
         * Get the list of uncommitted changes in the repository
         *
         * @return A list of uncommitted changes as DiffEntry objects
         * @throws GitAPIException
         * @throws IOException
         */
        get() {
            val diffs: MutableList<DiffEntry> = ArrayList()
            val reader = git!!.repository.newObjectReader()
            RevWalk(reader).use { walk ->
                val commit = walk.parseCommit(git!!.repository.resolve(Constants.HEAD))
                val tree = commit.tree
                TreeWalk(reader).use { treeWalk ->
                    treeWalk.addTree(tree)
                    treeWalk.isRecursive = true
                    while (treeWalk.next()) {
                        diffs.addAll(
                            git!!.diff()
                                .setOldTree(EmptyTreeIterator())
                                .setNewTree(
                                    CanonicalTreeParser(
                                        null,
                                        reader,
                                        treeWalk.getObjectId(0)
                                    )
                                )
                                .call()
                        )
                    }
                }
            }
            return diffs
        }

    @get:Throws(
        GitAPIException::class,
        IOException::class
    )
    val uncommittedChangesAsString: String
        /**
         * Get the list of uncommitted changes in the repository as a string
         *
         * @return A string representing the uncommitted changes
         * @throws GitAPIException
         * @throws IOException
         */
        get() {
            val diffs = uncommittedChanges
            val outputStream = ByteArrayOutputStream()
            val formatter = DiffFormatter(outputStream)
            formatter.setRepository(git!!.repository)
            for (entry in diffs) {
                formatter.format(entry)
            }
            return outputStream.toString()
        }

    @get:Throws(IOException::class)
    val currentBranchName: String
        /**
         * Get the current branch name
         *
         * @return The name of the current branch
         * @throws IOException
         */
        get() = git!!.repository.branch

    @get:Throws(GitAPIException::class)
    val commitsForCurrentBranch: List<RevCommit>
        /**
         * Get the list of commits in the repository for the current branch
         *
         * @return A list of commit objects
         * @throws GitAPIException
         */
        get() {
            val commits: MutableList<RevCommit> = ArrayList()
            val it: Iterator<RevCommit> = git!!.log().call().iterator()
            while (it.hasNext()) {
                commits.add(it.next())
            }
            return commits
        }

    @get:Throws(
        GitAPIException::class,
        IOException::class
    )
    val commitsForCurrentBranchAsString: String
        /**
         * Get the list of commits on the current branch as a string
         *
         * @return A string representing the commits on the current branch
         * @throws GitAPIException
         * @throws IOException
         */
        get() {
            val commits = git!!.log().call()
            val outputStream = ByteArrayOutputStream()
            var commit: RevCommit
            val i: Iterator<RevCommit> = commits.iterator()
            while (i.hasNext()) {
                commit = i.next()
                outputStream.write(commit.fullMessage.toByteArray())
                outputStream.write(System.lineSeparator().toByteArray())
            }
            return outputStream.toString()
        }

    fun close() {
        git!!.close()
    }

    val remoteURL: String
        get() = git!!.repository.config.getString("remote", "origin", "url")

    @get:Throws(IOException::class)
    val remoteName: String?
        get() {
            var remoteName = git!!.repository.fullBranch
            if (remoteName != null) {
                remoteName =
                    remoteName.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            }
            return remoteName
        }

    @Throws(GitAPIException::class, URISyntaxException::class)
    fun addRemoteOrigin(remoteRepoUrl: String?) {
        git!!.remoteAdd().setName("origin").setUri(URIish(remoteRepoUrl)).call()
    }

    @Throws(GitAPIException::class)
    fun pushToOrigin(branchName: String, username: String?, password: String?) {
        val pushCmd = git!!.push()
        pushCmd
            .setRemote("origin")
            .setRefSpecs(RefSpec("refs/heads/$branchName"))
            .setCredentialsProvider(UsernamePasswordCredentialsProvider(username, password))
            .setPushAll()
        pushCmd.call()
    }

    @Throws(GitAPIException::class)
    fun pushAllToOrigin(username: String?, password: String?) {
        val pushCmd = git!!.push()
        pushCmd
            .setRemote("origin")
            .setCredentialsProvider(UsernamePasswordCredentialsProvider(username, password))
            .setPushAll()
        pushCmd.call()
    }

    @Throws(GitAPIException::class)
    fun renameBranch(newBranchName: String?) {
        git!!.branchRename().setNewName(newBranchName).call()
    }

    @Throws(GitAPIException::class)
    fun renameBranchToMain() {
        git!!.branchRename().setNewName("main").call()
    }

    @Throws(GitAPIException::class)
    fun addAllFiles() {
        val addCmd = git!!.add()
        addCmd.isUpdate = true
        addCmd.addFilepattern(".")
        addCmd.call()
    }

    // This method is not tested
    @Throws(GitAPIException::class, IOException::class)
    fun createPullRequest(
        username: String?,
        password: String?,
        remoteUrl: String?,
        branchName: String?,
        title: String,
        description: String,
        targetBranch: String
    ) {

        // Set up credentials for authentication
        val credentialsProvider = UsernamePasswordCredentialsProvider(username, password)

        // Create a new branch from the specified branch
        git!!.checkout().setName(branchName).setCreateBranch(true).call()

        // Push the changes to the remote repository
        var pushCommand = git!!.push()
        pushCommand.setCredentialsProvider(credentialsProvider)
        pushCommand.remote = remoteUrl
        pushCommand.setPushAll()
        pushCommand.call()

        // Merge the branch into the target branch
        val mergeCommand = git!!.merge()
        mergeCommand.include(git!!.repository.exactRef(branchName))
        mergeCommand.setStrategy(MergeStrategy.RECURSIVE)
        mergeCommand.setMessage(
            """
    $title
    
    $description
    """.trimIndent()
        )
        mergeCommand.call()

        // Create a new branch for the merge commit
        val head = git!!.repository.exactRef(Constants.HEAD)
        val headId = head.objectId
        val updateRef = git!!.repository.updateRef("refs/heads/$targetBranch")
        updateRef.setNewObjectId(headId)
        updateRef.forceUpdate()

        // Push the merge commit to the remote repository
        pushCommand = git!!.push()
        pushCommand.setCredentialsProvider(credentialsProvider)
        pushCommand.remote = remoteUrl
        pushCommand.setPushAll()
        pushCommand.call()
    }

    @get:Throws(
        GitAPIException::class,
        IOException::class
    )
    val statusLikeTerminal: String
        get() {
            var result = ""
            result += """
        On branch ${git!!.repository.branch}
        
        
        """.trimIndent()
            result += "Changes to be committed:\n"
            result += "  (use \"git reset HEAD <file>...\" to unstage)\n\n"
            result += formatFileList(status.added, "new file")
            result += formatFileList(status.changed, "modified")
            result += formatFileList(status.removed, "deleted")
            result += "\n\nChanges not staged for commit:\n"
            result += "  (use \"git add <file>...\" to update what will be committed)\n"
            result += "  (use \"git checkout -- <file>...\" to discard changes in working directory)\n\n"
            result += formatFileList(status.modified, "modified")
            result += formatFileList(status.missing, "deleted")
            result += "\n\nUntracked files:\n"
            result += "  (use \"git add <file>...\" to include in what will be committed)\n\n"
            result += formatFileList(status.untracked, "untracked")
            return result
        }

    private fun formatFileList(files: Set<String>, statusType: String): String {
        var result = ""
        if (files.isNotEmpty()) {
            result += "$statusType:\n"
            for (file in files) {
                result += "\t" + file + "\n"
            }
        }
        return result
    }

    companion object {
        fun createGitIgnore(path: File?) {
            val gitIgnore = """
      # Built application files
      /*.apk
      /*.ap_
      /bin/
      /gen/
      /out/
      # Local configuration file (sdk path, etc)
      local.properties
      # Gradle files
      /.gradle/
      /build/
      /captures/
      .idea/
      # Android Studio Navigation editor temp files
      .navigation/
      *.navigation.xml
      # Android Studio captures folder
      captures/
      # Proguard folder generated by Android Studio
      proguard/
      # Logs
      log/
      .log
      # Android bundle file
      *.aab
      # Android Studio generated files
      /gradle/
      .gradle/
      .idea/
      *.iml
      *.iws
      *.ipr
       
       """.trimIndent()
            FileIOUtils.writeFileFromString(path, gitIgnore)
        }
    }
}