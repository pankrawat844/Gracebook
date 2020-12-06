package com.zocia.book;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class OtherActivity extends AppCompatActivity {
    private Context mContext;
    private WebView mWebView;
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        mContext = this;
        initUI();
    }

    private void initUI() {
        url = "file:///android_asset/termscondition.html";
        findViewById(R.id.layoutBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mWebView = (WebView) findViewById(R.id.webview_terms);
        mWebView.getSettings().setJavaScriptEnabled(true);
        WebSettings webViewSettings = mWebView.getSettings();
        webViewSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webViewSettings.setJavaScriptEnabled(true);
        webViewSettings.setBuiltInZoomControls(false);
        webViewSettings.setPluginState(WebSettings.PluginState.ON);
        mWebView.setBackgroundColor(0);
        mWebView.setVisibility(View.VISIBLE);
        mWebView.loadUrl(url);

    }
}
