package com.example.thanggun99.test2.heartbeat;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;


public final class HeartBeatProcessing {

    private final AtomicBoolean processing = new AtomicBoolean(false);
    private final int averageArraySize = 4;
    private final int beatsArraySize = 3;
    private final int[] beatsArray = new int[beatsArraySize];
    private SurfaceHolder surfaceHolder;
    private HeartBeatCallback heartBeatCallback;
    private Camera camera;
    private int[] averageArray = new int[averageArraySize];
    private int averageIndex = 0;
    private int beatsIndex = 0;
    private double beats = 0;
    private long startTime = 0;
    private boolean isStarted = false;
    private TYPE currentType;

    private Camera.PreviewCallback previewCallback = (data, c) -> {
        Camera.Size size = camera.getParameters().getPreviewSize();
        if (data == null || size == null) {
            heartBeatCallback.onInvalid();
            resetValue();
            return;
        }
        if (!processing.compareAndSet(false, true)) {
            heartBeatCallback.onInvalid();
            resetValue();
            return;
        }

        int width = size.width;
        int height = size.height;

        int imgAvg = decodeYUV420SPtoRedAvg(data.clone(), width, height);
        if (imgAvg < 190 || imgAvg >= 255) {
            processing.set(false);
            heartBeatCallback.onInvalid();
            resetValue();
            return;
        }

        heartBeatCallback.onProcessing();

        int averageArrayAvg = 0;
        int averageArrayCnt = 0;
        for (int anAverageArray : averageArray) {
            if (anAverageArray > 0) {
                averageArrayAvg += anAverageArray;
                averageArrayCnt++;
            }
        }

        int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
        TYPE newType = currentType;
        if (imgAvg < rollingAverage) {
            newType = TYPE.RED;
            if (newType != currentType) {
                beats++;
            }
        } else if (imgAvg > rollingAverage) {
            newType = TYPE.GREEN;
        }
        if (newType != currentType) {
            currentType = newType;
        }

        if (averageIndex == averageArraySize) {
            averageIndex = 0;
        }

        averageArray[averageIndex] = imgAvg;
        averageIndex++;


        long endTime = System.currentTimeMillis();
        double totalTimeInSecs = (endTime - startTime) / 1000d;
        if (totalTimeInSecs >= 10) {
            double bps = (beats / totalTimeInSecs);
            int dpm = (int) (bps * 60d);
            if (dpm < 30 || dpm > 180) {
                heartBeatCallback.onInvalid();
                resetValue();
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
            heartBeatCallback.onComplete(beatsAvg);
            startTime = System.currentTimeMillis();
            beats = 0;
        }
        processing.set(false);
    };

    public HeartBeatProcessing(SurfaceHolder surfaceHolder, HeartBeatCallback heartBeatCallback) {
        this.surfaceHolder = surfaceHolder;
        this.heartBeatCallback = heartBeatCallback;
        this.surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (camera == null) {
                    return;
                }
                try {
                    camera.setPreviewDisplay(surfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                camera.setPreviewCallback(previewCallback);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (camera == null) {
                    return;
                }
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                Camera.Size size = getSmallestPreviewSize(width, height, parameters);
                if (size != null) {
                    parameters.setPreviewSize(size.width, size.height);
                }

                camera.setParameters(parameters);
                camera.startPreview();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
    }

    public static Camera getCameraInstance() {
        try {
            return Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void resetValue() {
        beats = 0;
        startTime = System.currentTimeMillis();
        averageArray = new int[averageArraySize];
        averageIndex = 0;
    }

    public void stop() {
        isStarted = false;
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        heartBeatCallback.onStopMeasure();
    }

    public void start() {
        resetValue();
        if (!isStarted) {
            if (camera == null) {
                camera = getCameraInstance();
                if (camera == null) {
                    return;
                }
            }
            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.setPreviewCallback(previewCallback);

            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(surfaceHolder.getSurfaceFrame().width(),
                    surfaceHolder.getSurfaceFrame().height(), parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
            }

            camera.setParameters(parameters);
            camera.startPreview();
            isStarted = true;
        }
    }

    private Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
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

    private int decodeYUV420SPtoRedSum(byte[] yuv420sp, int width, int height) {
        if (yuv420sp == null) return 0;

        final int frameSize = width * height;

        int sum = 0;
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & yuv420sp[yp]) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0;
                else if (r > 262143) r = 262143;
                if (g < 0) g = 0;
                else if (g > 262143) g = 262143;
                if (b < 0) b = 0;
                else if (b > 262143) b = 262143;

                int pixel = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
                int red = (pixel >> 16) & 0xff;
                sum += red;
            }
        }
        return sum;
    }

    private int decodeYUV420SPtoRedAvg(byte[] yuv420sp, int width, int height) {
        if (yuv420sp == null) return 0;

        final int frameSize = width * height;

        int sum = decodeYUV420SPtoRedSum(yuv420sp, width, height);
        return (sum / frameSize);
    }

    public enum TYPE {
        GREEN, RED
    }

    public interface HeartBeatCallback {
        void onInvalid();

        void onProcessing();

        void onComplete(int bpm);

        void onStopMeasure();

    }

}
