package com.raredev.vcspace.ui.panels.compiler;

import android.content.Context;
import android.view.LayoutInflater;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.raredev.vcspace.compiler.databinding.LayoutWebviewPanelBinding;
import com.raredev.vcspace.compiler.html.SimpleHttpServer;
import com.raredev.vcspace.res.databinding.LayoutTextinputBinding;
import com.raredev.vcspace.ui.panels.Panel;

@SuppressWarnings("deprecation")
public class WebViewPanel extends Panel {

  private LayoutWebviewPanelBinding binding;

  private SimpleHttpServer httpServer;

  public WebViewPanel(Context context) {
    super(context);
    binding = LayoutWebviewPanelBinding.inflate(LayoutInflater.from(getContext()));
    httpServer = new SimpleHttpServer(8080);

    WebSettings webSettings = binding.webView.getSettings();
    webSettings.setAllowContentAccess(true);
    webSettings.setAllowFileAccess(true);
    webSettings.setAllowFileAccessFromFileURLs(true);
    webSettings.setJavaScriptEnabled(true);
    webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    webSettings.setSupportZoom(true);
    webSettings.setBuiltInZoomControls(true);
    webSettings.setDisplayZoomControls(false);

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
            /*setTitle(
            view.getTitle() == "about:blank"
                ? getContext().getString(string.app_name)
                : view.getTitle());*/
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
    setContentView(binding.getRoot());
    setTitle("WebView");
  }

  public void loadFile(String path) {
    httpServer.setFolderAndFile(
        (String) path.subSequence(0, path.lastIndexOf("/")),
        path.substring(path.lastIndexOf("/") + 1));
    binding.webView.loadUrl(httpServer.getLocalIpAddress());
  }

  @Override
  public void destroy() {
    if (httpServer != null) {
      httpServer.stopServer();
    }
    httpServer = null;
    binding = null;
  }

  @Override
  public void unselected() {
    if (httpServer != null) {
      httpServer.stopServer();
    }
  }

  @Override
  public void selected() {
    if (binding == null) return;
    httpServer.startServer();
    binding.webView.loadUrl(httpServer.getLocalIpAddress());
  }

  private void showAlertDialog(String url, String message, JsResult result) {
    new MaterialAlertDialogBuilder(getContext())
        .setTitle(url)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(android.R.string.ok, (witch, di) -> result.confirm())
        .setNegativeButton(android.R.string.cancel, (witch, di) -> result.cancel())
        .show();
  }

  private void showPromptDialog(
      String url, String message, String defaultValue, JsPromptResult result) {
    LayoutTextinputBinding bind = LayoutTextinputBinding.inflate(LayoutInflater.from(getContext()));

    AlertDialog dialog =
        new MaterialAlertDialogBuilder(getContext())
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
}
