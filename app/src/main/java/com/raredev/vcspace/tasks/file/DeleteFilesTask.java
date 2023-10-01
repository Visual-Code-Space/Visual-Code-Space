package com.raredev.vcspace.tasks.file;

import com.blankj.utilcode.util.FileUtils;
import com.raredev.vcspace.callback.MessageCallback;
import com.raredev.vcspace.models.FileModel;
import java.util.List;
import java.util.concurrent.Callable;

public class DeleteFilesTask implements Callable<Boolean> {

  private List<FileModel> files;
  private MessageCallback callback;

  public DeleteFilesTask(List<FileModel> files, MessageCallback callback) {
    this.files = files;
    this.callback = callback;
  }

  @Override
  public Boolean call() throws Exception {
    int count = 0;
    for (FileModel file : files) {
      callback.sendMessage("Deleting: " + file.getName());
      if (FileUtils.delete(file.getPath())) {
        callback.sendMessage(file.getName() + " deleted!");
        count++;
      }
    }

    return count == files.size();
  }
}
