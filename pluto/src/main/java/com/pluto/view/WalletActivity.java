package com.pluto.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pluto.config.Config;
import com.pluto.sdk.CoreSDK;
import com.pluto.sdk.R;

public class WalletActivity extends PlutoActivity {
    //
    private static final String TAG = "WalletActivity";
    //
    private RelativeLayout mLayoutLoading;
    //
    private TextView mTxtLoading;
    //
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.pluto_wallet_view);
        //
        ImageView imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(v -> {
            finish();
        });
        mLayoutLoading = findViewById(R.id.layout_loading);
        mTxtLoading = findViewById(R.id.txt_loading);
        mWebView = findViewById(R.id.wv_content);
        mWebView.clearCache(true);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        String url = CoreSDK.getIsDebug() ? Config.DebugWalletUrl : Config.WalletUrl;
        url += "?token=" + CoreSDK.getInstance().getAccount().getToken() + "&plantformtype=android&hideback=0&t=" + System.currentTimeMillis();
        mWebView.loadUrl(url);
        mWebView.addJavascriptInterface(jsCallback, "PlutoJS");
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.i(TAG, "Wallet page load: " + newProgress);
                if (newProgress == 100) {
                    mTxtLoading.setText("Loading 100%");
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        mLayoutLoading.setVisibility(View.GONE);
                    }, 1000);
                } else {
                    mTxtLoading.setText("Loading " + newProgress + "%");
                }
            }
        });
    }

    /**
     *
     */
    public final Object jsCallback = new Object() {
        @JavascriptInterface
        public void nv_closeWallet() {
            finish();
        }
    };
}
