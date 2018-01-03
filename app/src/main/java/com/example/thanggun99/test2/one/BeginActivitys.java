package com.example.thanggun99.test2.one;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public class BeginActivitys extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        new android.os.Handler().postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, 1000);
    }
}
