package com.raredev.vcspace.ui.panels.file;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.PathUtils;
import com.raredev.vcspace.activity.EditorActivity;
import com.raredev.vcspace.adapters.FileAdapter;
import com.raredev.vcspace.databinding.LayoutFileExplorerPanelBinding;
import com.raredev.vcspace.events.OnFileRenamedEvent;
import com.raredev.vcspace.fragments.explorer.FileManagerDialogs;
import com.raredev.vcspace.models.FileModel;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.ui.panels.Panel;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.PreferencesUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

public class FileExplorerPanel extends Panel implements FileAdapter.FileListener {

  private LayoutFileExplorerPanelBinding binding;
  private FileAdapter mFilesAdapter;

  private FileModel currentDir =
      FileModel.fileToFileModel(new File(PathUtils.getRootPathExternalFirst()));

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
                getContext(),
                currentDir.toFile(),
                (output) -> setCurrentDir(FileModel.fileToFileModel(output))));
    setupRecyclerView();
    refreshFiles();
  }

  @Override
  public void onFileClick(FileModel file, View v) {
    if (!file.isFile()) {
      setCurrentDir(file);
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
  public void onFileMenuClick(List<FileModel> selectedFiles, FileModel file, View v) {
    PopupMenu pm = new PopupMenu(getContext(), v);
    if (pm.getMenu() instanceof MenuBuilder) {
      ((MenuBuilder) pm.getMenu()).setOptionalIconsVisible(true);
    }
    pm.getMenu().add(R.string.select_all).setIcon(R.drawable.ic_select_all);
    if (selectedFiles.isEmpty()) {
      pm.getMenu().add(R.string.copy_path).setIcon(R.drawable.content_copy);
      pm.getMenu().add(R.string.rename).setIcon(R.drawable.file_rename);
    } else {
      pm.getMenu().add(R.string.unselect_all).setIcon(R.drawable.ic_select_all);
    }
    pm.getMenu().add(R.string.delete).setIcon(R.drawable.delete_outline);
    pm.setOnMenuItemClickListener(
        item -> {
          if (item.getTitle().equals(getContext().getString(R.string.select_all))) {
            mFilesAdapter.selectAllFiles();
          } else if (item.getTitle().equals(getContext().getString(R.string.unselect_all))) {
            mFilesAdapter.unselectAllFiles();
          } else if (item.getTitle().equals(getContext().getString(R.string.copy_path))) {
            ClipboardUtils.copyText(file.getPath());
          } else if (item.getTitle().equals(getContext().getString(R.string.rename))) {
            FileManagerDialogs.renameFile(
                getContext(),
                file.toFile(),
                (oldFile, newFile) -> {
                  EventBus.getDefault().post(new OnFileRenamedEvent(oldFile, newFile));
                  refreshFiles();
                });
          } else if (item.getTitle() == getContext().getString(R.string.delete)) {
            FileManagerDialogs.deleteFile(
                getContext(), selectedFiles, file.toFile(), (deletedFile) -> refreshFiles());
          }
          return true;
        });
    pm.show();
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

  private void setupRecyclerView() {
    mFilesAdapter = new FileAdapter();

    mFilesAdapter.setFileListener(this);

    binding.rvFiles.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.rvFiles.setAdapter(mFilesAdapter);
  }

  public void setCurrentDir(FileModel dir) {
    listArchives(dir);
    currentDir = dir;
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
