package com.raredev.vcspace.activity;

import android.os.*;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.ActivityWebviewBinding;

public class WebViewActivity extends VCSpaceActivity {
    private ActivityWebviewBinding mBinding;
    
    @Override
    public void findBinding() {
        mBinding = ActivityWebviewBinding.inflate(getLayoutInflater());
    }
    
    @Override
    public View getLayout() {
        return mBinding.getRoot();
    }
    
    @Override
    public void onCreate() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mBinding.toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
        mBinding.webView.loadUrl(getIntent().getStringExtra("html_file"));
        mBinding.webView.getSettings().setAllowContentAccess(true);
        mBinding.webView.getSettings().setAllowFileAccess(true);
        mBinding.webView.getSettings().setJavaScriptEnabled(true);
        mBinding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mBinding.webView.getSettings().setSupportZoom(true);
        mBinding.webView.getSettings().setBuiltInZoomControls(true);
        mBinding.webView.getSettings().setDisplayZoomControls(false);
        
        mBinding.webView.setWebChromeClient(
                new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int progress) {
                        getSupportActionBar().setTitle(view.getTitle());
                        getSupportActionBar().setSubtitle(view.getUrl());
                    }
                });
        mBinding.webView.setWebViewClient(
                new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.webviewact_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    private boolean isDesktopMode = false;
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_desktop:
                if (isDesktopMode) {
                    isDesktopMode = false;
                    item.setChecked(false);
                } else {
                    isDesktopMode = true;
                    item.setChecked(true);
                }
                mBinding.webView.getSettings().setUseWideViewPort(item.isChecked());
                mBinding.webView.reload();
                break;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        if (mBinding.webView.canGoBack()) {
            mBinding.webView.goBack();
            return;
        }
        finish();
    }
}