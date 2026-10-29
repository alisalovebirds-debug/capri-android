
package com.capri.android;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setStatusBarColor(Color.rgb(8, 12, 25));
        window.setNavigationBarColor(Color.rgb(8, 12, 25));
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        hideSystemBars();

        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(8, 12, 25));

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        setContentView(webView);

        // Temporary test page. We'll connect Capri's assets next.
        webView.loadDataWithBaseURL(
            "https://appassets.androidplatform.net/",
            "<!doctype html><html><head>" +
            "<meta name='viewport' content='width=device-width, initial-scale=1'>" +
            "</head><body style='margin:0;background:#080c19;color:#f1f3ff;" +
            "font-family:sans-serif;display:grid;place-items:center;height:100vh'>" +
            "<div style='text-align:center'><h1>Capri</h1>" +
            "<p>Android app shell is running.</p>" +
            "<p>Original animations are the next step.</p></div></body></html>",
            "text/html",
            "UTF-8",
            null
        );
    }

    private void hideSystemBars() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemBars();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null) {
            webView.evaluateJavascript(
                "document.activeElement.blur()", null
            );
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
