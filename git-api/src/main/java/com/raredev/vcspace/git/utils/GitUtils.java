package com.raredev.vcspace.git.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;

public class GitUtils {
  private Git git;

  /**
   * Constructor
   *
   * @param repoPath The path to the Git repository
   * @throws IOException
   */
  public GitUtils(String repoPath) throws IOException {
    Repository repo = new FileRepositoryBuilder().setGitDir(new File(repoPath + "/.git")).build();
    git = new Git(repo);
  }

  /**
   * Create a new branch
   *
   * @param branchName The name of the new branch
   * @throws GitAPIException
   */
  public void createBranch(String branchName) throws GitAPIException {
    git.branchCreate().setName(branchName).call();
  }

  /**
   * Get a list of all branches
   *
   * @return A list of all branches
   * @throws GitAPIException
   */
  public List<Ref> getAllBranches() throws GitAPIException {
    return git.branchList().call();
  }

  /**
   * Merge the given branch into the current branch
   *
   * @param branchName The name of the branch to merge
   * @throws GitAPIException
   * @throws IOException
   */
  public void mergeBranch(String branchName) throws GitAPIException, IOException {
    git.merge().include(git.getRepository().exactRef("refs/heads/" + branchName)).call();
  }

  /**
   * Commit changes to the repository
   *
   * @param message The commit message
   * @throws GitAPIException
   */
  public void commitChanges(String message) throws GitAPIException {
    git.commit().setMessage(message).call();
  }

  /**
   * Get the current commit ID
   *
   * @return The current commit ID
   * @throws AmbiguousObjectException
   * @throws IncorrectObjectTypeException
   * @throws IOException
   */
  public ObjectId getCurrentCommitId()
      throws AmbiguousObjectException, IncorrectObjectTypeException, IOException {
    return git.getRepository().resolve("HEAD");
  }

  /**
   * Get the commit message for the given commit ID
   *
   * @param commitId The commit ID
   * @return The commit message
   * @throws IncorrectObjectTypeException
   * @throws IOException
   */
  public String getCommitMessage(ObjectId commitId)
      throws IncorrectObjectTypeException, IOException {
    RevCommit commit = git.getRepository().parseCommit(commitId);
    return commit.getFullMessage();
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
  public String getDiff(ObjectId oldCommitId, ObjectId newCommitId)
      throws GitAPIException, IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    DiffCommand diffCommand = git.diff().setOutputStream(out);
    if (oldCommitId != null) {
      try (ObjectReader reader = git.getRepository().newObjectReader()) {
        AbstractTreeIterator oldTree = new CanonicalTreeParser(null, reader, oldCommitId);
        diffCommand.setOldTree(oldTree);
      }
    }
    try (ObjectReader reader = git.getRepository().newObjectReader()) {
      AbstractTreeIterator newTree = new CanonicalTreeParser(null, reader, newCommitId);
      diffCommand.setNewTree(newTree).call();
    }
    return out.toString(StandardCharsets.UTF_8.name());
  }

  /**
   * Get the diff between the current commit and the previous commit
   *
   * @return A string representing the diff
   * @throws GitAPIException
   * @throws IOException
   */
  public String getDiff() throws GitAPIException, IOException {
    ObjectId head = git.getRepository().resolve("HEAD");
    RevCommit commit = git.log().add(head).setMaxCount(1).call().iterator().next();
    RevCommit parent = commit.getParent(0);
    return getDiff(parent.getId(), head);
  }

  /**
   * Get the commit ID for the given branch name
   *
   * @param branchName The name of the branch
   * @return The commit ID for the branch
   * @throws IOException
   */
  public ObjectId getBranchCommitId(String branchName) throws IOException {
    Ref branchRef = git.getRepository().findRef("refs/heads/" + branchName);
    return branchRef.getObjectId();
  }

  /**
   * Cherry-pick the given commit onto the current branch
   *
   * @param commitId The ID of the commit to cherry-pick
   * @throws GitAPIException
   */
  public void cherryPick(ObjectId commitId) throws GitAPIException {
    git.cherryPick().include(commitId).call();
  }

  /**
   * Push changes to remote repository
   *
   * @param remoteName The name of the remote repository to push to
   * @throws GitAPIException
   */
  public void push(String remoteName) throws GitAPIException {
    git.push().setRemote(remoteName).call();
  }

  /**
   * Push all committed changes to a remote branch
   *
   * @param remote The name of the remote
   * @param branch The name of the branch
   * @throws GitAPIException
   */
  public void push(String remote, String branch) throws GitAPIException {
    git.push().setRemote(remote).setRefSpecs(new RefSpec(branch)).call();
  }

  /**
   * Pull changes from remote repository
   *
   * @param remoteName The name of the remote repository to pull from
   * @throws GitAPIException
   */
  public void pull(String remoteName) throws GitAPIException {
    git.pull().setRemote(remoteName).call();
  }

  /**
   * Pull changes from a remote branch into the current branch
   *
   * @param remote The name of the remote
   * @param branch The name of the branch
   * @throws GitAPIException
   */
  public void pull(String remote, String branch) throws GitAPIException {
    git.pull().setRemote(remote).setRemoteBranchName(branch).call();
  }

  /**
   * Add a file or directory to the index
   *
   * @param fileOrDirPath The path to the file or directory to add
   * @throws GitAPIException
   */
  public void add(String fileOrDirPath) throws GitAPIException {
    git.add().addFilepattern(fileOrDirPath).call();
  }

  /**
   * Checkout the given branch
   *
   * @param branchName The name of the branch to checkout
   * @throws GitAPIException
   */
  public void checkout(String branchName) throws GitAPIException {
    git.checkout().setName(branchName).call();
  }

  /**
   * Get the status of the repository
   *
   * @return A Status object representing the repository status
   * @throws GitAPIException
   */
  public Status getStatus() throws GitAPIException {
    return git.status().call();
  }

  /**
   * Get the status of the repository as a string
   *
   * @return A string representation of the repository status
   * @throws GitAPIException
   */
  public String getStatusAsString() throws GitAPIException {
    Status status = git.status().call();
    StringBuilder sb = new StringBuilder();
    sb.append("Added: " + status.getAdded() + "\n");
    sb.append("Changed: " + status.getChanged() + "\n");
    sb.append("Conflicting: " + status.getConflicting() + "\n");
    sb.append("IgnoredNotInIndex: " + status.getIgnoredNotInIndex() + "\n");
    sb.append("Missing: " + status.getMissing() + "\n");
    sb.append("Modified: " + status.getModified() + "\n");
    sb.append("Removed: " + status.getRemoved() + "\n");
    sb.append("Untracked: " + status.getUntracked() + "\n");
    sb.append("UntrackedFolders: " + status.getUntrackedFolders() + "\n");
    return sb.toString();
  }

  /**
   * Get the list of uncommitted changes in the repository
   *
   * @return A list of uncommitted changes as DiffEntry objects
   * @throws GitAPIException
   * @throws IOException
   */
  public List<DiffEntry> getUncommittedChanges() throws GitAPIException, IOException {
    List<DiffEntry> diffs = new ArrayList<>();
    ObjectReader reader = git.getRepository().newObjectReader();
    try (RevWalk walk = new RevWalk(reader)) {
      RevCommit commit = walk.parseCommit(git.getRepository().resolve(Constants.HEAD));
      RevTree tree = commit.getTree();
      try (TreeWalk treeWalk = new TreeWalk(reader)) {
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);
        while (treeWalk.next()) {
          diffs.addAll(
              git.diff()
                  .setOldTree(new EmptyTreeIterator())
                  .setNewTree(new CanonicalTreeParser(null, reader, treeWalk.getObjectId(0)))
                  .call());
        }
      }
    }
    return diffs;
  }

  /**
   * Get the list of uncommitted changes in the repository as a string
   *
   * @return A string representing the uncommitted changes
   * @throws GitAPIException
   * @throws IOException
   */
  public String getUncommittedChangesAsString() throws GitAPIException, IOException {
    List<DiffEntry> diffs = getUncommittedChanges();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    DiffFormatter formatter = new DiffFormatter(outputStream);
    formatter.setRepository(git.getRepository());
    for (DiffEntry entry : diffs) {
      formatter.format(entry);
    }
    return outputStream.toString();
  }

  /**
   * Get the current branch name
   *
   * @return The name of the current branch
   * @throws IOException
   */
  public String getCurrentBranchName() throws IOException {
    return git.getRepository().getBranch();
  }

  /**
   * Get the list of commits in the repository for the current branch
   *
   * @return A list of commit objects
   * @throws GitAPIException
   */
  public List<RevCommit> getCommitsForCurrentBranch() throws GitAPIException {
    List<RevCommit> commits = new ArrayList<>();
    Iterator<RevCommit> it = git.log().call().iterator();
    while (it.hasNext()) {
      commits.add(it.next());
    }
    return commits;
  }

  /**
   * Get the list of commits on the current branch as a string
   *
   * @return A string representing the commits on the current branch
   * @throws GitAPIException
   * @throws IOException
   */
  public String getCommitsForCurrentBranchAsString() throws GitAPIException, IOException {
    Iterable<RevCommit> commits = git.log().call();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    RevCommit commit;
    for (Iterator<RevCommit> i = commits.iterator(); i.hasNext(); ) {
      commit = i.next();
      outputStream.write(commit.getFullMessage().getBytes());
      outputStream.write(System.lineSeparator().getBytes());
    }
    return outputStream.toString();
  }
}
