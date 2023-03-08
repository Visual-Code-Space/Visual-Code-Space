package com.raredev.vcspace.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.vcspace.R;
import com.raredev.vcspace.adapters.model.FileTemplateModel;
import com.raredev.vcspace.databinding.LayoutFileTemplateDialogBinding;
import com.raredev.vcspace.databinding.LayoutFileTemplateItemBinding;
import java.util.List;

public class FileTemplateAdapter extends RecyclerView.Adapter<FileTemplateAdapter.VH> {
  private List<FileTemplateModel> mTemplates;

  public FileTemplateAdapter(List<FileTemplateModel> mTemplates) {
    this.mTemplates = mTemplates;
  }

  @Override
  public VH onCreateViewHolder(ViewGroup parent, int position) {
    return new VH(
        LayoutFileTemplateItemBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    FileTemplateModel template = mTemplates.get(position);

    holder.fileExtension.setText(template.getFileExtension());
    holder.templateCode.setText(template.getTemplateContent());

    holder.itemView.setOnClickListener(
        v -> {
          editTemplateDialog(v, position);
        });
  }

  @Override
  public int getItemCount() {
    return mTemplates.size();
  }

  private void editTemplateDialog(View v, int pos) {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
    builder.setTitle(R.string.edit_file_template);

    LayoutFileTemplateDialogBinding bind =
        LayoutFileTemplateDialogBinding.inflate(builder.create().getLayoutInflater());

    FileTemplateModel template = mTemplates.get(pos);

    bind.etFileExtension.setText(template.getFileExtension());
    bind.etTemplateCode.setText(template.getTemplateContent());
    builder.setView(bind.getRoot());

    builder.setPositiveButton(
        R.string.menu_save,
        (dlg, i) -> {
          String newFileExtension = bind.etFileExtension.getText().toString();
          String newTemplateCode = bind.etTemplateCode.getText().toString();

          template.setFileExtension(newFileExtension);
          template.setTemplateContent(newTemplateCode);

          mTemplates.set(pos, template);
          notifyItemChanged(pos, template);
        });
    builder.setNegativeButton(R.string.cancel, null);
    builder.show();
  }

  public class VH extends RecyclerView.ViewHolder {
    TextView fileExtension, templateCode;

    public VH(LayoutFileTemplateItemBinding binding) {
      super(binding.getRoot());
      fileExtension = binding.fileExtension;
      templateCode = binding.template;
    }
  }
}
