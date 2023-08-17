package com.raredev.vcspace.models.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.blankj.utilcode.util.SizeUtils;
import com.raredev.vcspace.databinding.LayoutFiletreeItemBinding;
import com.raredev.vcspace.models.FileModel;
import com.raredev.vcspace.res.R;
import com.unnamed.b.atv.model.TreeNode;
import java.io.File;

public class FileTreeViewHolder extends TreeNode.BaseNodeViewHolder<File> {

  private LayoutFiletreeItemBinding binding;

  public FileTreeViewHolder(Context context) {
    super(context);
  }

  @Override
  public View createNodeView(TreeNode node, File file) {
    this.binding = LayoutFiletreeItemBinding.inflate(LayoutInflater.from(context));

    final var dp15 = SizeUtils.dp2px(10);
    final var chevron = binding.chevron;
    binding.filetreeName.setText(file.getName());
    
    final var root = applyPadding(node, binding, dp15);

    if (file.isDirectory()) {
      updateChevronIcon(node.isExpanded());
    } else {
      chevron.setImageResource(getIconForFileName(file.getName()));
    }

    return root;
  }

  private void updateChevronIcon(boolean expanded) {
    final int chevronIcon;
    if (expanded) {
      chevronIcon = R.drawable.chevron_down;
    } else {
      chevronIcon = R.drawable.chevron_right;
    }

    binding.chevron.setImageResource(chevronIcon);
  }

  protected LinearLayout applyPadding(
      final TreeNode node, final LayoutFiletreeItemBinding binding, final int padding) {
    final var root = binding.getRoot();
    root.setPaddingRelative(
        root.getPaddingLeft() + (padding * (node.getLevel() - 1)),
        root.getPaddingTop(),
        root.getPaddingRight(),
        root.getPaddingBottom());
    return root;
  }

  public void updateChevron(boolean expanded) {
    setLoading(false);
    updateChevronIcon(expanded);
  }

  public void setLoading(boolean loading) {
    final int viewIndex;
    if (loading) {
      viewIndex = 1;
    } else {
      viewIndex = 0;
    }

    binding.switcher.setDisplayedChild(viewIndex);
  }
  
  private int getIconForFileName(String fileName) {
    int icon = R.drawable.ic_file;

    for (String extension : FileModel.TEXT_FILES) {
      if (fileName.endsWith(extension)) {
        icon = R.drawable.file_document_outline;
      }
    }
    return icon;
  }
}
