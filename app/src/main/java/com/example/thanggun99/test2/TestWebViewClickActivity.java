package com.example.thanggun99.test2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thanggun99 on 10/4/17.
 */

public class TestWebViewClickActivity extends AppCompatActivity {
    @BindView(R.id.web_view)
    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new OnSuccessListener(), "Android");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return true;
            }
        });
//        file:///android_asset/Test.html

        webView.loadUrl("https://sandbox.nganluong.vn:8088/nl30/checkout.php?merchant_site_code=45881&return_url=https://www.google.com.vn&receiver=thanggun99@gmail.com&transaction_info=&order_code=12345678&price=50000&currency=vnd&quantity=1&tax=0&discount=0&fee_cal=0&fee_shipping=0&order_description=Nap tien&buyer_info=thang*|*thang@gmail.com*|*0915194096&affiliate_code&secure_code=2fc7cbabe12c5c7c49bbfd69c499598b");
    }

    public class OnSuccessListener {

        @JavascriptInterface
        void onSuccess(String message, String path) {
            Toast.makeText(TestWebViewClickActivity.this, message + "\n" + path, Toast.LENGTH_SHORT).show();
        }
    }

}
