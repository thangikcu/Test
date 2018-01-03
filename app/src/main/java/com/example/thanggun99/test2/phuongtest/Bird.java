package com.example.thanggun99.test2.phuongtest;

import android.util.Log;

/**
 * Created by thanggun99 on 10/20/17.
 */

public class Bird extends Animal {
    @Override
    public void doSomeThing() {
        bay();
    }

    void bay() {
        Log.d("CHIMMMMMMMMM", "bayyyyyyyyyyyyy");
    }
}
