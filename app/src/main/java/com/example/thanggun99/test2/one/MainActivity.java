package com.example.thanggun99.test2.one;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.thanggun99.test2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection {
    private static final String TAG = "thanggun99";
    private int exampleIndex = 0;

    private static String doubleEscapeTeX(String s) {
        String t = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\'') t += '\\';
            if (s.charAt(i) != '\n') t += s.charAt(i);
            if (s.charAt(i) == '\\') t += "\\";
        }
        return t;
    }

    private String getExample(int index) {
        return getResources().getStringArray(R.array.tex_examples)[index];
    }

    public void onClick(View v) {

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
/*
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.loadUrl("file:///android_asset/MathJax.html");
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return true;
            }
        });
        Log.d(TAG, "onCreate: "+getExample(0));
        Log.d(TAG, "onCreate: "+doubleEscapeTeX(getExample(0)));
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webview.evaluateJavascript("javascript:document.getElementById('MathPreview').innerHTML=' " + doubleEscapeTeX(getExample(0)) + "'", null);
//                webview.loadUrl("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);");
                webview.loadUrl("javascript:Preview.Update();");
            }
        });
        startService(new Intent(this, MyService.class));*/

        DatabaseReference timeRef = FirebaseDatabase.getInstance().getReference("Test").child("Time");

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "run: ");
                timeRef.setValue(System.currentTimeMillis());
            }
        }, 0, 3000);

        timeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void startService(View view) {
//        startService(new Intent(this, MyService.class));
        bindService(new Intent(this, MyService.class), this, BIND_AUTO_CREATE);
    }

    public void stopService(View view) {
//        stopService(new Intent(this, MyService.class));
        unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        startService(new Intent(this, MyService.class));
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
