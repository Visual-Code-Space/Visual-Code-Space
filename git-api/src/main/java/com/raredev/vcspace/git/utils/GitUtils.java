package com.raredev.vcspace.git.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

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
}
