package com.raredev.vcspace.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.ActivityWebviewBinding;
import com.raredev.vcspace.databinding.LayoutTextinputBinding;
import com.raredev.vcspace.util.ToastUtils;
import java.io.File;

@SuppressWarnings("deprecation")
public class WebViewActivity extends BaseActivity {
  public ActivityWebviewBinding binding;

  @Override
  public View getLayout() {
    binding = ActivityWebviewBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setSupportActionBar(binding.toolbar);

    binding.toolbar.setNavigationOnClickListener((v) -> onBackPressed());

    WebSettings webSettings = binding.webView.getSettings();
    webSettings.setAllowContentAccess(true);
    webSettings.setAllowFileAccess(true);
    webSettings.setAllowFileAccessFromFileURLs(true);
    webSettings.setJavaScriptEnabled(true);
    webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    webSettings.setSupportZoom(true);
    webSettings.setBuiltInZoomControls(true);
    webSettings.setDisplayZoomControls(false);

    String executableFilePath = getIntent().getStringExtra("executable_file");
    String htmlContent = getIntent().getStringExtra("html_content");
    if (executableFilePath != null) {
      binding.webView.loadUrl("file://" + executableFilePath);
    } else {
      binding.webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
    }

    binding.webView.setWebChromeClient(
        new WebChromeClient() {

          @Override
          public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            showAlertDialog(url, message, result);
            return true;
          }

          @Override
          public boolean onJsPrompt(
              WebView view,
              String url,
              String message,
              String defaultValue,
              JsPromptResult result) {
            showPromptDialog(url, message, defaultValue, result);
            return true;
          }

          @Override
          public void onProgressChanged(WebView view, int progress) {
            binding.progressIndicator.setVisibility(progress == 100 ? View.GONE : View.VISIBLE);
            binding.progressIndicator.setProgressCompat(progress, true);
            getSupportActionBar()
                .setTitle(
                    view.getTitle() == "about:blank"
                        ? getString(R.string.app_name)
                        : view.getTitle());
            getSupportActionBar()
                .setSubtitle(view.getUrl() == "about:blank" ? "Preview" : view.getUrl());
          }
        });
    binding.webView.setWebViewClient(
        new WebViewClient() {
          @Override
          public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
          }
        });
  }

  @Override
  public void onBackPressed() {
    if (binding.webView.canGoBack()) {
      binding.webView.goBack();
      return;
    }
    super.onBackPressed();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    binding = null;
  }

  private void showAlertDialog(String url, String message, JsResult result) {
    new MaterialAlertDialogBuilder(WebViewActivity.this)
        .setTitle(url)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(android.R.string.ok, (witch, di) -> result.confirm())
        .setNegativeButton(android.R.string.cancel, (witch, di) -> result.cancel())
        .show();
  }

  private void showPromptDialog(
      String url, String message, String defaultValue, JsPromptResult result) {
    LayoutTextinputBinding bind = LayoutTextinputBinding.inflate(getLayoutInflater());

    AlertDialog dialog =
        new MaterialAlertDialogBuilder(this)
            .setView(bind.getRoot())
            .setTitle(url)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(android.R.string.cancel, null)
            .create();

    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    dialog.setOnShowListener(
        (p1) -> {
          TextInputEditText editText = bind.etInput;
          bind.tvInputLayout.setHint(message);
          editText.setText(defaultValue);
          Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
          Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

          editText.requestFocus();
          positive.setOnClickListener(
              v -> {
                result.confirm(editText.getText().toString());
                dialog.dismiss();
              });
          negative.setOnClickListener(
              v -> {
                result.cancel();
                dialog.dismiss();
              });
        });
    dialog.show();
  }

  public void setDesktopMode(boolean enabled) {
    WebSettings webSettings = binding.webView.getSettings();

    // Check if the desktop mode is enabled or not
    if (enabled) {
      // Enable desktop mode by changing the user agent string
      webSettings.setUserAgentString(
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
      webSettings.setUseWideViewPort(true);
      webSettings.setLoadWithOverviewMode(true);
    } else {
      // Disable desktop mode by resetting the user agent string to default
      webSettings.setUserAgentString(null);
      webSettings.setUseWideViewPort(false);
      webSettings.setLoadWithOverviewMode(false);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.webview_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    var id = item.getItemId();
    var webView = binding.webView;

    if (id == R.id.back) {
      if (webView.canGoBack()) webView.goBack();
      else ToastUtils.showShort("Can't go back...", ToastUtils.TYPE_ERROR);
    } else if (id == R.id.forward) {
      if (webView.canGoForward()) webView.goForward();
      else ToastUtils.showShort("Can't go forward...", ToastUtils.TYPE_ERROR);
    } else if (id == R.id.zooming) {
      webView.getSettings().setSupportZoom(!item.isChecked());
      item.setChecked(!item.isChecked());
    } else if (id == R.id.desktop_mode) {
      setDesktopMode(!item.isChecked());
      item.setChecked(!item.isChecked());
    } else if (id == R.id.refresh) {
      webView.reload();
    } else if (id == R.id.open_in_browser) {
      openInBrowser(webView);
    } else if (id == R.id.exit) {
      super.onBackPressed();
    }
    return true;
  }

  private void openInBrowser(WebView webView) {
    String currentUrl = webView.getUrl();
    boolean isFilePath = isFilePath(currentUrl);

    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
    browserIntent.setData(Uri.parse(currentUrl));

    if (isFilePath) {
      File file = new File(currentUrl);
      Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
      browserIntent.setData(contentUri);
      browserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    startActivity(browserIntent);
  }

  private boolean isFilePath(String url) {
    Uri uri = Uri.parse(url);
    return uri.getScheme() != null && uri.getScheme().equals("file");
  }
}
