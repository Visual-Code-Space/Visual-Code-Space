package com.raredev.vcspace.activity;

import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.common.util.FileUtil;
import com.raredev.vcspace.R;
import com.raredev.vcspace.adapters.FileTemplateAdapter;
import com.raredev.vcspace.adapters.model.FileTemplateModel;
import com.raredev.vcspace.databinding.ActivityFileTemplatesBinding;
import com.raredev.vcspace.databinding.LayoutFileTemplateDialogBinding;
import com.raredev.vcspace.tools.TemplatesParser;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.json.JSONException;
import org.json.JSONObject;

public class FileTemplatesActivity extends VCSpaceActivity {
  private ActivityFileTemplatesBinding binding;

  private List<FileTemplateModel> mTemplates;
  private FileTemplateAdapter mAdapter;

  @Override
  public void findBinding() {
    binding = ActivityFileTemplatesBinding.inflate(getLayoutInflater());
  }

  @Override
  public View getLayout() {
    return binding.getRoot();
  }

  @Override
  public void onCreate() {
    setSupportActionBar(binding.toolbar);
    binding.toolbar.setNavigationOnClickListener((v) -> onBackPressed());
    mTemplates = TemplatesParser.getTemplates();
    mAdapter = new FileTemplateAdapter(mTemplates);

    binding.fab.setOnClickListener(
        v -> {
          addNewTemplateDialog();
        });
    binding.rvTemplates.setLayoutManager(new LinearLayoutManager(this));
    binding.rvTemplates.setAdapter(mAdapter);
  }

  @Override
  protected void onPause() {
    super.onPause();
    saveTemplates();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    saveTemplates();
  }

  private void addNewTemplateDialog() {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
    builder.setTitle(R.string.new_file_template);

    LayoutFileTemplateDialogBinding bind =
        LayoutFileTemplateDialogBinding.inflate(getLayoutInflater());

    builder.setView(bind.getRoot());

    builder.setPositiveButton(
        R.string.create,
        (dlg, i) -> {
          String fileExtension = bind.etFileExtension.getText().toString();
          String templateCode = bind.etTemplateCode.getText().toString();

          mTemplates.add(new FileTemplateModel(fileExtension, templateCode));
        });
    builder.setNegativeButton(R.string.cancel, null);
    builder.show();
  }

  private void saveTemplates() {
    binding.progress.setVisibility(View.VISIBLE);
    CompletableFuture.runAsync(
        () -> {
          String templatesFile = getExternalFilesDir("template") + "/templates.json";

          try {
            JSONObject jsonObj = new JSONObject();
            for (FileTemplateModel template : mTemplates) {
              jsonObj.put(template.getFileExtension(), template.getTemplateContent());
            }
            FileUtil.writeFile(templatesFile, jsonObj.toString());
          } catch (JSONException jsone) {
            jsone.printStackTrace();
          }
          runOnUiThread(
              () -> {
                binding.progress.setVisibility(View.GONE);
              });
        });
  }
}
