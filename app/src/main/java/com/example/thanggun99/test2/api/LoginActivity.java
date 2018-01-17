package com.example.thanggun99.test2.api;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.thanggun99.test2.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.tv_content)
    TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");


        progressDialog.show();
        LoginApi loginApi = new LoginApi();
        loginApi.setOnResonseTextCallback(response -> {
            tvContent.setText(response);
            progressDialog.dismiss();
        });
        loginApi.login("npFPOY81233sZOeHe2sG59Z6gF/W5ClRQLsp9VpcUwHpOmqRAHXmlMSwd/MOWAcBuHbiuFKcfZPm8qkDcQQfGuMk0hPMZRrq2mQshaj6mRk=");
    }
}
