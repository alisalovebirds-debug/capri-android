
package com.capri.android;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.webkit.WebViewAssetLoader;

public class MainActivity extends Activity {

    private WebView webView;
    private WebViewAssetLoader assetLoader;
    private TextView debugView;
    private String debugText = "Capri diagnostics\nLoading page...\n";

    private void showDebug(String message) {
        android.util.Log.e("CapriDebug", message);

        debugText += message + "\n";

        if (debugText.length() > 5000) {
            debugText = debugText.substring(
                debugText.length() - 4000
            );
        }

        if (debugView != null) {
            debugView.setText(debugText);
        }
    }

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

        assetLoader = new WebViewAssetLoader.Builder()
            .addPathHandler(
                "/assets/",
                new WebViewAssetLoader.AssetsPathHandler(this)
            )
            .build();

        FrameLayout root = new FrameLayout(this);

        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(8, 12, 25));

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(
                WebView view,
                WebResourceRequest request
            ) {
                return assetLoader.shouldInterceptRequest(
                    request.getUrl()
                );
            }

            @Override
            public void onReceivedError(
                WebView view,
                WebResourceRequest request,
                WebResourceError error
            ) {
                super.onReceivedError(view, request, error);

                if (request.isForMainFrame()) {
                    showDebug(
                        "PAGE ERROR: " + error.getErrorCode()
                        + " " + error.getDescription()
                    );
                }
            }

            @Override
            public void onPageFinished(
                WebView view,
                String url
            ) {
                super.onPageFinished(view, url);
                showDebug("Page finished: " + url);

                view.evaluateJavascript(
                    "(function(){return 'Title: '+document.title"
                    + "+' | JS: '+typeof window.host"
                    + "+' | App: '+"
                    + "!!document.querySelector('#capri')})()",
                    result -> showDebug("Page check: " + result)
                );
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage message) {
                showDebug(
                    "JS " + message.messageLevel() + ": "
                    + message.message() + " (line "
                    + message.lineNumber() + ")"
                );
                return true;
            }
        });

        root.addView(
            webView,
            new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        );

        debugView = new TextView(this);
        debugView.setTextColor(Color.WHITE);
        debugView.setTextSize(11);
        debugView.setBackgroundColor(0xDD101522);
        debugView.setPadding(12, 12, 12, 12);
        debugView.setText(debugText);
        debugView.setGravity(Gravity.BOTTOM);
        debugView.setTextIsSelectable(true);

        FrameLayout.LayoutParams debugParams =
            new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            );

        root.addView(debugView, debugParams);
        setContentView(root);

        webView.loadUrl(
            "https://appassets.androidplatform.net/assets/src/renderer/index.html"
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
            if (webView.canGoBack()) {
                webView.goBack();
                return;
            }

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
