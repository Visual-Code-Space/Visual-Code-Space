package com.raredev.vcspace.ui.tree.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.raredev.common.util.Utils;
import com.raredev.vcspace.databinding.LayoutFileItemBinding;
import com.raredev.vcspace.models.FileExtension;
import com.raredev.vcspace.util.ViewUtils;
import com.unnamed.b.atv.model.TreeNode;
import com.raredev.vcspace.R;
import java.io.File;

public class FileViewHolder extends TreeNode.BaseNodeViewHolder<File> {
  private LayoutFileItemBinding binding;

  public FileViewHolder(Context context) {
    super(context);
  }

  @Override
  public View createNodeView(TreeNode node, File file) {
    binding = LayoutFileItemBinding.inflate(LayoutInflater.from(context));

    var root = applyPadding(node, binding, Utils.pxToDp(context, 10));
    binding.name.setText(file.getName());

    if (file.isFile()) {
      binding.icon.setImageResource(FileExtension.getIcon(file.getName()).icon);
    } else {
      binding.icon.setImageResource(R.drawable.ic_folder);
    }

    if (file.isDirectory()) {
      binding.toggleRoot.setVisibility(View.VISIBLE);
      rotateChevron(node.isExpanded());
    }
    return root;
  }

  protected LinearLayout applyPadding(
      final TreeNode node, final LayoutFileItemBinding binding, final int padding) {
    final var root = binding.getRoot();
    root.setPaddingRelative(
        root.getPaddingLeft() + (padding * (node.getLevel() - 1)),
        root.getPaddingTop(),
        root.getPaddingRight(),
        root.getPaddingBottom());
    return root;
  }

  public void rotateChevron(boolean expanded) {
    setLoading(false);
    ViewUtils.rotateChevron(expanded, binding.toggle);
  }

  public void setLoading(boolean loading) {
    int index = -1;
    if (loading) {
      index = 1;
    } else {
      index = 0;
    }
    binding.toggleRoot.setDisplayedChild(index);
  }
}
