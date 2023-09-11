package com.raredev.vcspace.progressdialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.vcspace.res.databinding.LayoutProgressDialogBinding;
import com.raredev.vcspace.utils.Utils;

public class ProgressDialog {

  private String title, message, loadingMessage;
  private int progress, max, min;
  private Context context;
  private MaterialAlertDialogBuilder builder;
  private LayoutProgressDialogBinding binding;
  private boolean showPercentage;
  private ProgressStyle progressStyle;

  public ProgressDialog(Context context) {
    this.context = context;
    init(context);
  }

  private void init(Context context) {
    binding = LayoutProgressDialogBinding.inflate(LayoutInflater.from(context));
    builder = new MaterialAlertDialogBuilder(context);
    progressStyle = ProgressStyle.CIRCULAR;
    invalidatePercentText();
    invalidateProgressStyle();
    builder.setView(binding.getRoot());
  }

  private void invalidateProgressStyle() {
    binding.circularProgressIndicator.setVisibility(
        progressStyle.equals(ProgressStyle.CIRCULAR) ? View.VISIBLE : View.GONE);
    binding.linearProgressIndicator.setVisibility(
        progressStyle.equals(ProgressStyle.LINEAR) ? View.VISIBLE : View.GONE);
    binding.loadingMessage.setPadding(
        progressStyle.equals(ProgressStyle.CIRCULAR) ? Utils.pxToDp(10) : 0, 0, 0, 0);
  }

  private void invalidatePercentText() {
    binding.percent.setText("(" + progress + "%)");
    binding.percent.setVisibility(showPercentage ? View.VISIBLE : View.GONE);
  }

  public static ProgressDialog create(Context context) {
    return new ProgressDialog(context);
  }

  public MaterialAlertDialogBuilder getDialog() {
    return this.builder;
  }

  public ProgressDialog setDialog(MaterialAlertDialogBuilder builder) {
    this.builder = builder;
    return this;
  }

  public ProgressDialog setPositiveButton(
      String text, DialogInterface.OnClickListener clickListener) {
    builder.setPositiveButton(text, clickListener);
    return this;
  }

  public ProgressDialog setPositiveButton(
      @StringRes int text, DialogInterface.OnClickListener clickListener) {
    builder.setPositiveButton(text, clickListener);
    return this;
  }

  public ProgressDialog setNegativeButton(
      String text, DialogInterface.OnClickListener clickListener) {
    builder.setNegativeButton(text, clickListener);
    return this;
  }

  public ProgressDialog setNegativeButton(
      @StringRes int text, DialogInterface.OnClickListener clickListener) {
    builder.setNegativeButton(text, clickListener);
    return this;
  }

  public ProgressDialog setNeutralButton(
      String text, DialogInterface.OnClickListener clickListener) {
    builder.setNeutralButton(text, clickListener);
    return this;
  }

  public ProgressDialog setNeutralButton(
      @StringRes int text, DialogInterface.OnClickListener clickListener) {
    builder.setNeutralButton(text, clickListener);
    return this;
  }

  public AlertDialog show() {
    return builder.show();
  }
  
  public AlertDialog create() {
    return builder.create();
  }

  public String getTitle() {
    return this.title;
  }

  public ProgressDialog setTitle(String title) {
    this.title = title;
    builder.setTitle(title);
    return this;
  }

  public ProgressDialog setTitle(@StringRes int title) {
    this.title = context.getString(title);
    builder.setTitle(title);
    return this;
  }

  public String getMessage() {
    return this.message;
  }

  public ProgressDialog setMessage(String message) {
    this.message = message;
    builder.setMessage(message);
    return this;
  }

  public ProgressDialog setMessage(@StringRes int message) {
    this.message = context.getString(message);
    builder.setMessage(message);
    return this;
  }

  public boolean isShowPercentage() {
    return this.showPercentage;
  }

  public ProgressDialog setShowPercentage(boolean showPercentage) {
    this.showPercentage = showPercentage;
    invalidatePercentText();
    return this;
  }

  public ProgressStyle getProgressStyle() {
    return this.progressStyle;
  }

  public ProgressDialog setProgressStyle(ProgressStyle progressStyle) {
    this.progressStyle = progressStyle;
    invalidateProgressStyle();
    return this;
  }

  public int getProgress() {
    return this.progress;
  }

  public ProgressDialog setProgress(int progress) {
    this.progress = progress;
    binding.circularProgressIndicator.setProgressCompat(progress, true);
    binding.linearProgressIndicator.setProgressCompat(progress, true);
    invalidatePercentText();
    return this;
  }

  public int getMax() {
    return this.max;
  }

  public ProgressDialog setMax(int max) {
    this.max = max;
    binding.circularProgressIndicator.setMax(max);
    binding.linearProgressIndicator.setMax(max);
    return this;
  }

  public int getMin() {
    return this.min;
  }

  public ProgressDialog setMin(int min) {
    this.min = min;
    binding.circularProgressIndicator.setMin(min);
    binding.linearProgressIndicator.setMin(min);
    return this;
  }

  public String getLoadingMessage() {
    return this.loadingMessage;
  }

  public ProgressDialog setLoadingMessage(String loadingMessage) {
    this.loadingMessage = loadingMessage;
    binding.loadingMessage.setText(loadingMessage);
    return this;
  }

  public ProgressDialog setLoadingMessage(@StringRes int loadingMessage) {
    this.loadingMessage = context.getString(loadingMessage);
    binding.loadingMessage.setText(loadingMessage);
    return this;
  }

  public ProgressDialog setCancelable(boolean cancelable) {
    builder.setCancelable(cancelable);
    return this;
  }
}
