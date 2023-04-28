package com.raredev.vcspace.fragments.filemanager.actions.git;

import android.content.Context;
import com.raredev.vcspace.fragments.filemanager.FileManagerFragment;
import com.raredev.vcspace.fragments.filemanager.actions.GitBaseAction;
import com.raredev.vcspace.git.CloneRepository;
import com.raredev.vcspace.util.DialogUtils;
import com.raredev.vcspace.util.ILogger;
import com.vcspace.actions.ActionData;
import java.io.File;

public class CloneRepositoryAction extends GitBaseAction {

  private static final String LOG_TAG = CloneRepositoryAction.class.getSimpleName();

  @Override
  public void performAction(ActionData data) {
    FileManagerFragment fragment = getFragment(data);

    CloneRepository cloneRepo = new CloneRepository(fragment.requireActivity());
    cloneRepo.setDirectory(getFolder(data));
    cloneRepo.cloneRepository();
    cloneRepo.setListener(
        new CloneRepository.CloneListener() {

          @Override
          public void onCloneSuccess(File output) {
            fragment.listArchives(output);
            ILogger.info(LOG_TAG, "Cloned to: " + output.toString());
          }

          @Override
          public void onCloneFailed(String message) {
            DialogUtils.newErrorDialog(fragment.requireActivity(), "Clone failed", message);
            ILogger.error(LOG_TAG, "Clone failed: " + message);
          }
        });
  }

  @Override
  public String getActionId() {
    return "clone.repository.action";
  }

  @Override
  public String getTitle(Context context) {
    return context.getString(com.raredev.vcspace.git.R.string.clone_repo);
  }
}
