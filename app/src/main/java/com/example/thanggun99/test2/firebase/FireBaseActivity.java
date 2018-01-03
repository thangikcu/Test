package com.example.thanggun99.test.firebase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.thanggun99.test.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rebtel.repackaged.com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class FireBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_base);

        DatabaseReference account = FirebaseDatabase.getInstance()
                .getReference("Account")
                .child("4299A5DAAA3F74FE5F00FF878D8EC837");

        FirebaseDatabase.getInstance().getReference("Thang")
        .child("hihi")
        .setValue("testttttttttt");
//        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.ERROR);
        account.keepSynced(true);

        account.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("thangggggggggg", "goto changeeeeeeeeeeeeeee");
                Gson gson = new Gson();
                String jsonString = gson.toJson(dataSnapshot.child("Info").getValue());
                String jsonStringBalance = gson.toJson(dataSnapshot.child("Balance").getValue());
                try {
                    JSONObject jsonUserInfo = new JSONObject(jsonString);
                    int idType = jsonUserInfo.getInt("idtype");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FireBaseActivity.this, "ha", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
