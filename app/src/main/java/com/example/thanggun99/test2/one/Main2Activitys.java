package com.example.thanggun99.test2.one;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.thanggun99.test2.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Main2Activitys extends AppCompatActivity {


    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.edt_size)
    EditText edtSize;
    @BindView(R.id.btn_init)
    Button btnInit;
    @BindView(R.id.btn_find)
    Button btnFind;
    @BindView(R.id.tv_array)
    TextView tvArray;
    private int[] array;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

        tvArray.setMovementMethod(new ScrollingMovementMethod());

        btnFind.setOnClickListener(v -> {
            long millis = System.currentTimeMillis();
            long nanos = System.nanoTime();
            int missingElement = findMissingElement(array);
            millis = System.currentTimeMillis() - millis;
            nanos = System.nanoTime() - nanos;
            tvResult.setText(String.format("Element missing: %d\nMillis: %d\nNano: %d", missingElement, millis, nanos));
        });

        btnInit.setOnClickListener(v -> {
            String sizeInput = edtSize.getText().toString();
            int size;
            if (!sizeInput.isEmpty() && (size = Integer.parseInt(sizeInput)) >= 3) {
                array = makeArray(size);
                StringBuilder s = new StringBuilder();
                for (int i = 0; i < array.length; i++) {
                    s.append(array[i]);
                    if (i < array.length - 1) {
                        s.append(", ");
                    }
                }
                tvArray.setText(s.toString());
                btnFind.setEnabled(true);
            } else {
                btnFind.setEnabled(false);
            }
        });
    }

    private int findMissingElement(int[] array) {
        int temp;
        for (int i = 0; i < array.length; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (array[i] > array[j]) {
                    temp = array[j];
                    array[j] = array[i];
                    array[i] = temp;
                }
            }
            if (i != 0 && array[i] != (temp = array[i - 1] + 1)) {
                return temp;
            }
        }
        return 0;
    }

    private int[] makeArray(int size) {
        ArrayList<Integer> arrayList = new ArrayList();
        for (int i = 0; i < size; i++) {
            arrayList.add(i, i + 1);
        }
        while (true) {
            int index = new Random().nextInt(size - 1);
            if (index != 0) {
                arrayList.remove(index);
                break;
            }
        }
        Collections.shuffle(arrayList);
        int[] array = new int[size - 1];
        Iterator<Integer> iterator = arrayList.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            array[i++] = iterator.next();
        }
        return array;
    }
}
