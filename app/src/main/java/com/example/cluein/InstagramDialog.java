package com.example.cluein;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;

public class InstagramDialog extends Dialog {

    private final String authUrl;
    private final String redirectUri;
    private final AuthenticationListener listener;

    public interface AuthenticationListener {
        void onCodeReceived(String code);
    }

    public InstagramDialog(@NonNull Context context, String authUrl, String redirectUri, AuthenticationListener listener) {
        super(context);
        this.authUrl = authUrl;
        this.redirectUri = redirectUri;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_instagram);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(authUrl);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.startsWith(redirectUri)) {
                    if (url.contains("code=")) {
                        String temp = url.substring(url.indexOf("code=") + 5);
                        String code = temp.split("#")[0]; // Remove fragment if present
                        listener.onCodeReceived(code);
                        dismiss();
                    } else if (url.contains("error=")) {
                        Log.e("INSTAGRAM_AUTH", "Error: " + url);
                        dismiss();
                    }
                }
            }
        });
    }
}
