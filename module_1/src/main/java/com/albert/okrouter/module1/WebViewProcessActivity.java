package com.albert.okrouter.module1;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.albert.okrouter.annotation.Route;

/**
 * 子进程webview
 */
@Route(address = "/app/WebViewProcessActivity")
public class WebViewProcessActivity extends AppCompatActivity {

    BaseWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_process);
        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString("URL", "https://www.baidu.com/");

        Log.e("webview-onCreate-time:", System.currentTimeMillis() + "");
        webView = findViewById(R.id.wv);

        WebSettings webSettings = webView.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setDomStorageEnabled(true);

        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);

        webView.loadUrl(url);

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        //manager
    }

    WebViewClient webViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.e("webview-onPageStarted:", System.currentTimeMillis() + "");
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            //Log.e("webview-onLoadResource:", System.currentTimeMillis() + "");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e("webview-onPageFinished:", System.currentTimeMillis() + "");
        }
    };

    WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("webview-onResume-time:", System.currentTimeMillis() + "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("webview-onDestroy-time:", System.currentTimeMillis() + "");
    }
}
