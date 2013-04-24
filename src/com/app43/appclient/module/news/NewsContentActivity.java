package com.app43.appclient.module.news;

import com.alvin.api.utils.SettingsUtils;
import com.app43.appclient.R;
import com.app43.appclient.module.abstracts.activity.SendDataNoMenuActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NewsContentActivity extends SendDataNoMenuActivity {

    // TODO 开线程后载
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
        setContentView(R.layout.news_content_activity);
        TextView textView = (TextView) findViewById(R.id.news_content_top_title);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.news_content_loading_progress);
        Bundle bundle = getIntent().getExtras();
        textView.setText(bundle.getInt(SettingsUtils.CATEGORY_NAME));
        WebView webView = (WebView) findViewById(R.id.news_content_webview);
        webView.loadUrl(bundle.getString(SettingsUtils.PAGEURL));
        // webView.loadDataWithBaseURL(bundle.getString(Settings.PAGEURL), data,
        // "text/html", "utf-8", null);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
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

}
