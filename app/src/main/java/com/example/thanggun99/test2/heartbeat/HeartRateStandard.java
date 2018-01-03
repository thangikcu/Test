package com.example.thanggun99.test.heartbeat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.example.thanggun99.test.R;
import com.example.thanggun99.test.phuongtest.Animal;
import com.example.thanggun99.test.phuongtest.Bird;
import com.example.thanggun99.test.phuongtest.Dog;
import com.example.thanggun99.test.phuongtest.Fish;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by thanggun99 on 10/17/17.
 */

public class HeartRateStandard extends AppCompatActivity {
    private static HeartRateMonitor.TYPE currentType = HeartRateMonitor.TYPE.GREEN;

    private final AtomicBoolean processing = new AtomicBoolean(false);
    private final int averageArraySize = 4;
    private final int beatsArraySize = 3;
    private final int[] beatsArray = new int[beatsArraySize];
    private int[] averageArray = new int[averageArraySize];
    private Camera camera = null;
    private PowerManager.WakeLock wakeLock = null;
    private int averageIndex = 0;
    private int beatsIndex = 0;
    private double beats = 0;
    private long startTime = 0;
    private TextView text = null;
    private HeartbeatView heart;
    private AlertDialog alertDialog;
    private TextView tvTitle;

    public static HeartRateMonitor.TYPE getCurrent() {
        return currentType;
    }

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        List<Animal> animalList = new ArrayList<>();

        animalList.add(new Fish());
        animalList.add(new Dog());
        animalList.add(new Bird());
        animalList.add(new Fish());
        animalList.add(new Dog());
        animalList.add(new Bird());
        animalList.add(new Fish());
        animalList.add(new Bird());

//        Animal animal = new Fish();

        for (Animal animal : animalList) {
            animal.doSomeThing();
        }

        tvTitle = findViewById(R.id.title);
        alertDialog = new AlertDialog.Builder(HeartRateStandard.this)
                .setMessage("Vui lòng đặt ngón tay vào trước camera để lấy số đo nhịp tim !")
                .create();

        SurfaceView preview = findViewById(R.id.preview);
        SurfaceHolder previewHolder = preview.getHolder();
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        previewHolder.addCallback(new SurfaceHolder.Callback() {

            @SuppressLint("LongLogTag")
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    camera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.setPreviewCallback((data, c) -> {
                    Log.d("thanggun99", "surfaceCreated: ");
                    Camera.Size size = camera.getParameters().getPreviewSize();
                    if (data == null || size == null) {
                        onInvalid();
                        return;
                    }
                    if (!processing.compareAndSet(false, true)) {
                        onInvalid();
                        return;
                    }

                    int width = size.width;
                    int height = size.height;

                    int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), width, height);
                    if (imgAvg < 190 || imgAvg >= 255) {
                        processing.set(false);
                        onInvalid();
                        return;
                    }

                    tvTitle.setVisibility(View.VISIBLE);
                    if (alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                    if (!heart.isHeartBeating()) {
                        heart.start();
                    }

                    int averageArrayAvg = 0;
                    int averageArrayCnt = 0;
                    for (int anAverageArray : averageArray) {
                        if (anAverageArray > 0) {
                            averageArrayAvg += anAverageArray;
                            averageArrayCnt++;
                        }
                    }

                    int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
                    HeartRateMonitor.TYPE newType = currentType;
                    if (imgAvg < rollingAverage) {
                        newType = HeartRateMonitor.TYPE.RED;
                        if (newType != currentType) {
                            beats++;
                        }
                    } else if (imgAvg > rollingAverage) {
                        newType = HeartRateMonitor.TYPE.GREEN;
                    }

                    if (averageIndex == averageArraySize) {
                        averageIndex = 0;
                    }

                    averageArray[averageIndex] = imgAvg;
                    averageIndex++;

                    if (newType != currentType) {
                        currentType = newType;
                    }

                    long endTime = System.currentTimeMillis();
                    double totalTimeInSecs = (endTime - startTime) / 1000d;
                    if (totalTimeInSecs >= 10) {
                        double bps = (beats / totalTimeInSecs);
                        int dpm = (int) (bps * 60d);
                        if (dpm < 30 || dpm > 180) {
                            onInvalid();
                            return;
                        }

                        if (beatsIndex == beatsArraySize) {
                            beatsIndex = 0;
                        }
                        beatsArray[beatsIndex] = dpm;
                        beatsIndex++;

                        int beatsArrayAvg = 0;
                        int beatsArrayCnt = 0;
                        for (int aBeatsArray : beatsArray) {
                            if (aBeatsArray > 0) {
                                beatsArrayAvg += aBeatsArray;
                                beatsArrayCnt++;
                            }
                        }
                        int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
                        text.setText(String.valueOf(beatsAvg));
                        startTime = System.currentTimeMillis();
                        beats = 0;
                    }
                    processing.set(false);
                });
            }


            private void onInvalid() {
                beats = 0;
                text.setText(R.string.default_text);
                startTime = System.currentTimeMillis();
               /* averageArray = new int[averageArraySize];
                averageIndex = 0;*/
                if (heart.isHeartBeating()) {
                    heart.stop();
                }
                if (!alertDialog.isShowing()) {
                    alertDialog.show();
                }
                tvTitle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                Camera.Size size = getSmallestPreviewSize(width, height, parameters);
                if (size != null) {
                    parameters.setPreviewSize(size.width, size.height);
                    Log.d("Heart", "Using width=" + size.width + " height=" + size.height);
                }

                camera.setParameters(parameters);
                camera.startPreview();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // Ignore
            }
        });

        heart = findViewById(R.id.heart);
        heart.setDurationBasedOnBPM(100);
        text = findViewById(R.id.text);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
        }
    }

    @SuppressLint("WakelockTimeout")
    @Override
    public void onResume() {
        super.onResume();

        wakeLock.acquire();

        camera = Camera.open();

        startTime = System.currentTimeMillis();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();

        wakeLock.release();

        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public enum TYPE {
        GREEN, RED
    }
}
