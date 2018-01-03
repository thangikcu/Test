package com.example.thanggun99.test2.one;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.thanggun99.test2.R;

public class MyService extends Service {
    private static final String TAG = "thanggun99";
    private MediaPlayer mediaPlayer;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.incoming);
        mediaPlayer.setLooping(true);
        return new MyMediaPlayer(mediaPlayer);
    }

    public class MyMediaPlayer extends Binder {

        private MediaPlayer mediaPlayer;

        MyMediaPlayer(MediaPlayer mediaPlayer) {

            this.mediaPlayer = mediaPlayer;
        }
    }


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: " + startId + " and " + flags);
        mediaPlayer.start();
        return START_STICKY;
    }
}
