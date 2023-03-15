package com.raredev.vcspace.activity;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.raredev.common.util.FileUtil;
import com.raredev.vcspace.databinding.ActivityWebviewBinding;

public class WebViewActivity extends VCSpaceActivity {
  private ActivityWebviewBinding binding;

  @Override
  public View getLayout() {
    binding = ActivityWebviewBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onCreate() {
    setSupportActionBar(binding.toolbar);

    binding.toolbar.setNavigationOnClickListener((v) -> onBackPressed());
    binding.webView.getSettings().setAllowContentAccess(true);
    binding.webView.getSettings().setAllowFileAccess(true);
    binding.webView.getSettings().setAllowFileAccessFromFileURLs(true);
    binding.webView.getSettings().setJavaScriptEnabled(true);
    binding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    binding.webView.getSettings().setSupportZoom(true);
    binding.webView.getSettings().setBuiltInZoomControls(true);
    binding.webView.getSettings().setDisplayZoomControls(false);

    String htmlPath = getIntent().getStringExtra("html_file");
    binding.webView.loadUrl("file://" + htmlPath);

    binding.webView.setWebChromeClient(
        new WebChromeClient() {
          @Override
          public void onProgressChanged(WebView view, int progress) {
            getSupportActionBar().setTitle(view.getTitle());
            getSupportActionBar().setSubtitle(view.getUrl());
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
}
