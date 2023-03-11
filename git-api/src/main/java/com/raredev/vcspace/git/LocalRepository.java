package com.raredev.vcspace.git;

import java.io.File;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.util.FS;

public class LocalRepository {
  private File repositoryDir;

  public LocalRepository() {}

  public boolean openRepository(File dir) {
    if (dir == null) {
      return false;
    }

    if (!dir.exists()) {
      return false;
    }
    
    if (isRepository(dir)) {
      return false;
    }

    if (repositoryDir != dir) {
      repositoryDir = dir;
      return true;
    }
    return false;
  }

  public File getRepositoryDir() {
    return repositoryDir;
  }

  public String getName() {
    return repositoryDir.getName();
  }

  public boolean isRepository(File dir) {
    if (dir == null) {
      return false;
    }
    return RepositoryCache.FileKey.resolve(dir, FS.DETECTED) != null;
  }
}
