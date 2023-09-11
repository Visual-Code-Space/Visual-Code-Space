package com.raredev.vcspace.ui.panels.file;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.PathUtils;
import com.raredev.vcspace.activity.EditorActivity;
import com.raredev.vcspace.adapters.FileAdapter;
import com.raredev.vcspace.databinding.LayoutFileExplorerPanelBinding;
import com.raredev.vcspace.events.OnFileRenamedEvent;
import com.raredev.vcspace.fragments.sheets.OptionsBottomSheet;
import com.raredev.vcspace.models.FileModel;
import com.raredev.vcspace.models.OptionModel;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.ui.panels.Panel;
import com.raredev.vcspace.util.FileManagerDialogs;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.PreferencesUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

public class FileExplorerPanel extends Panel implements FileAdapter.FileListener {

  private LayoutFileExplorerPanelBinding binding;
  private FileAdapter mFilesAdapter;

  private FileModel currentDir = new FileModel(PathUtils.getRootPathExternalFirst(), "", false);

  public FileExplorerPanel(Context context) {
    super(context);
    setTitle("Explorer");
  }

  @Override
  public View createView() {
    binding = LayoutFileExplorerPanelBinding.inflate(LayoutInflater.from(getContext()));
    return binding.getRoot();
  }

  @Override
  public void viewCreated(View view) {
    super.viewCreated(view);
    binding.pathList.setFileExplorerPanel(this);

    binding.navigationSpace.addItem(
        getContext(), R.string.refresh, R.drawable.ic_refresh, (v) -> refreshFiles());
    binding.navigationSpace.addItem(
        getContext(),
        R.string.create,
        R.drawable.ic_add,
        (v) -> {
          FileManagerDialogs.createNew(getContext(), currentDir.toFile(), (file) -> refreshFiles());
        });
    binding.navigationSpace.addItem(
        getContext(),
        R.string.clone_repo,
        R.drawable.git,
        (v) ->
            FileManagerDialogs.cloneRepoDialog(
                getContext(), currentDir.toFile(), (output) -> setCurrentDir(output.getPath())));
    binding.backFolder.setOnClickListener(
        v -> {
          if (currentDir.getPath().equals(PathUtils.getRootPathExternalFirst())) return;
          var parentPath = FileUtil.getParentPath(currentDir.getPath());
          if (parentPath != null) {
            setCurrentDir(parentPath);
          }
        });
    setupRecyclerView();
    refreshFiles();
  }

  @Override
  public void onFileClick(FileModel file, View v) {
    if (!file.isFile()) {
      setCurrentDir(file.getPath());
    } else {
      if (FileUtil.isValidTextFile(file.getName())) {
        ((EditorActivity) getContext()).openFile(file);
      }
      if (file.getName().endsWith(".apk")) {
        FileManagerDialogs.showApkInfoDialog(getContext(), file.toFile());
      }
    }
  }

  @Override
  public boolean onFileLongClick(List<FileModel> selectedFiles, FileModel file, View v) {
    var optionsSheet = new OptionsBottomSheet();

    optionsSheet.addOption(
        new OptionModel(R.drawable.ic_select_all, getString(R.string.select_all)));
    if (selectedFiles.isEmpty()) {
      optionsSheet.addOption(
          new OptionModel(R.drawable.content_copy, getString(R.string.copy_path)));
      optionsSheet.addOption(new OptionModel(R.drawable.file_rename, getString(R.string.rename)));
    } else {
      optionsSheet.addOption(
          new OptionModel(R.drawable.ic_select_all, getString(R.string.unselect_all)));
    }
    optionsSheet.addOption(new OptionModel(R.drawable.delete_outline, getString(R.string.delete)));

    optionsSheet.setOptionListener(
        (option) -> {
          if (option.getName().equals(getContext().getString(R.string.select_all))) {
            mFilesAdapter.selectAllFiles();
          } else if (option.getName().equals(getContext().getString(R.string.unselect_all))) {
            mFilesAdapter.unselectAllFiles();
          } else if (option.getName().equals(getContext().getString(R.string.copy_path))) {
            ClipboardUtils.copyText(file.getPath());
          } else if (option.getName().equals(getContext().getString(R.string.rename))) {
            FileManagerDialogs.renameFile(
                getContext(),
                file.toFile(),
                (oldFile, newFile) -> {
                  EventBus.getDefault().post(new OnFileRenamedEvent(oldFile, newFile));
                  refreshFiles();
                });
          } else if (option.getName().equals(getContext().getString(R.string.delete))) {
            FileManagerDialogs.deleteFile(
                getContext(), selectedFiles, file.toFile(), (deletedFile) -> refreshFiles());
          }
          optionsSheet.dismiss();
        });
    optionsSheet.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "");
    return true;
  }

  @Override
  public void destroy() {
    binding = null;
  }

  @Override
  public void unselected() {
    mFilesAdapter.clear();
  }

  @Override
  public void selected() {
    refreshFiles();
  }

  public String getString(int resId) {
    return getContext().getString(resId);
  }

  private void setupRecyclerView() {
    mFilesAdapter = new FileAdapter();

    mFilesAdapter.setFileListener(this);

    binding.rvFiles.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.rvFiles.setAdapter(mFilesAdapter);
  }

  public void setCurrentDir(String path) {
    currentDir.setPath(path);
    refreshFiles();
  }

  public void refreshFiles() {
    listArchives(currentDir);
  }

  public void listArchives(FileModel path) {
    if (!isViewCreated()) return;
    binding.container.setDisplayedChild(1);
    TaskExecutor.executeAsyncProvideError(
        () -> {
          List<FileModel> mFiles = new ArrayList<>();
          path.listFiles(
              result -> {
                if (result.length == 0) {
                  return;
                }
                Arrays.sort(result, FILE_FIRST_ORDER);
                for (FileModel file : result) {
                  if (file.getName().startsWith(".") && !PreferencesUtils.showHiddenFiles()) {
                    continue;
                  }
                  mFiles.add(file);
                }
              });
          return mFiles;
        },
        (result, error) -> {
          if (binding == null) return;
          if (result == null || error != null) {
            binding.container.setDisplayedChild(2);
            return;
          }
          binding.container.setDisplayedChild(result.isEmpty() ? 2 : 0);
          binding.pathList.setPath(path.getPath());
          mFilesAdapter.setFiles(result);
        });
  }

  public FileModel getCurrentDir() {
    return currentDir;
  }

  public static final Comparator<FileModel> FILE_FIRST_ORDER =
      (file1, file2) -> {
        if (file1.isFile() && !file2.isFile()) {
          return 1;
        } else if (file2.isFile() && !file1.isFile()) {
          return -1;
        } else {
          return String.CASE_INSENSITIVE_ORDER.compare(file1.getName(), file2.getName());
        }
      };
}
