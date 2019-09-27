package com.albert.okrouter.demo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.albert.okrouter.annotation.Route;
import com.albert.okrouter.module1.BaseWebView;

/**
 * 主进程里WebView
 */
@Route(adress = "/app/WebViewTestActivity")
public class WebViewTestActivity extends AppCompatActivity {

    BaseWebView webView;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_test);
        Bundle bundle = getIntent().getExtras();
        url = bundle.getString("URL", "https://www.baidu.com/");

        Log.e("webview-onCreate-time1:", System.currentTimeMillis() + "");
        webView = findViewById(R.id.wv);

        WebSettings webSettings = webView.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setNeedInitialFocus(true);
        webSettings.setPluginState(WebSettings.PluginState.OFF);
        webSettings.setAllowFileAccess(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);


        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);

        webView.loadUrl(url);
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
        Log.e("webview-onResume-time1:", System.currentTimeMillis() + "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("webview-Destroy-time1:", System.currentTimeMillis() + "");
    }
}
