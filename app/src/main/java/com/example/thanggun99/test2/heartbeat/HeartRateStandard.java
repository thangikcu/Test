package com.example.thanggun99.test2.heartbeat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.thanggun99.test2.R;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

/**
 * Created by thanggun99 on 10/17/17.
 */

public class HeartRateStandard extends AppCompatActivity implements HeartBeatProcessing.HeartBeatCallback {

    private HeartBeatProcessing heartBeatProcessing;

    private TextView tvNhipTim;
    private ImageView ivHeart;
    private TextView tvDangDo;

    private PowerManager.WakeLock wakeLock;
    private AlertDialog alertDialog;
    private YoYo.YoYoString animation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);

        tvDangDo = findViewById(R.id.title);
        alertDialog = new AlertDialog.Builder(HeartRateStandard.this)
                .setMessage("Vui lòng đặt ngón tay vào trước camera để lấy số đo nhịp tim !")
                .create();

        SurfaceView preview = findViewById(R.id.preview);

        heartBeatProcessing = new HeartBeatProcessing(preview.getHolder(), this);

        ivHeart = findViewById(R.id.heart);
        tvNhipTim = findViewById(R.id.text);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
        }

        Price priceObj = new Price(50000, new Date("17:35 PM 29/10/2017"));
        scheduleUpdateGiaSP(priceObj);
    }

    class Price {

        Price(int price, Date dateEnd) {
            this.price = price;
            this.dateEnd = dateEnd;
        }

        int price;
        Date dateEnd;
    }

    class Produce {
        Price price;
    }

    public void scheduleUpdateGiaSP(Price priceObj) {
        updatePriceSP(priceObj.price);
        Price newPriceObj = getNextPrice(priceObj);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                scheduleUpdateGiaSP(newPriceObj);
            }
        }, priceObj.dateEnd);
    }

    public void updatePriceSP(int price) {
        // TODO: 24/10/2017 cap nhat gia san pham
    }

    public Price getNextPrice(Price priceObj) {
        // TODO: 24/10/2017 lay gia san pham tiep theo tren CSDL
        return new Price(100000, new Date("19:20 PM 05/11/2017"));
    }

    @SuppressLint("WakelockTimeout")
    @Override
    public void onResume() {
        super.onResume();

        wakeLock.acquire();
//        heartBeatProcessing.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();

        wakeLock.release();

        heartBeatProcessing.stop();
    }

    @Override
    public void onInvalid() {
        tvNhipTim.setText(R.string.default_text);
        if (animation != null) {
            animation.stop(true);
        }
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
        tvDangDo.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onComplete(int bpm) {
        tvNhipTim.setText(bpm + "");
        heartBeatProcessing.stop();
    }

    @Override
    public void onProcessing() {
        tvDangDo.setVisibility(View.VISIBLE);
        tvNhipTim.setText(R.string.default_text);
        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        if (animation == null || !animation.isStarted()) {
            animation = YoYo.with(Techniques.Pulse)
                    .repeat(YoYo.INFINITE)
                    .duration(500)
                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .playOn(ivHeart);
        }
    }

    @Override
    public void onStopMeasure() {
        if (animation != null) {
            animation.stop(true);
        }
        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        tvDangDo.setVisibility(View.INVISIBLE);
    }

    public void doNhipTim(View view) {
        heartBeatProcessing.start();
    }

    public void dungDo(View view) {
        heartBeatProcessing.stop();
    }
}
