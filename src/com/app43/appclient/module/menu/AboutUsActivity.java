package com.app43.appclient.module.menu;

import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.R;
import com.app43.appclient.module.abstracts.activity.SendDataNoMenuActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AboutUsActivity extends SendDataNoMenuActivity {
    // TODO 开线程后载
    WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void handleViews(String jsonString) {

    }

    @Override
    protected Handler initHandle() {
        return null;
    }

    @Override
    public void setupViews() {
        setContentView(R.layout.about_us_activity);
        TextView textView = (TextView) findViewById(R.id.about_us_app_version);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.about_us_loading_progress);
        PackageInfo packageInfo;
        try {
            packageInfo = this.getPackageManager().getPackageInfo(
                    this.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            textView.setText("V " + packageInfo.versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        webView = (WebView) findViewById(R.id.about_us_webview);
        webView.loadUrl(SettingsUtils.URL_ABOUT);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        WebSettings webSettings = webView.getSettings(); // webView: 类WebView的实例
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        // webSettings.setUseWideViewPort(true);
        // webSettings.setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                onBackPressed();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        UMengAnalyseUtils.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UMengAnalyseUtils.onPause(this);
    }
}
