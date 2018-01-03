package com.example.thanggun99.test2.firebase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.thanggun99.test2.R;
import com.google.firebase.database.FirebaseDatabase;

public class FireBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_base);
/*

        DatabaseReference account = FirebaseDatabase.getInstance()
                .getReference("Account")
                .child("4299A5DAAA3F74FE5F00FF878D8EC837");
*/

        FirebaseDatabase.getInstance().getReference("Thang")
        .child("hihi")
        .setValue("testttttttttt");
//        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.ERROR);
     /*   account.keepSynced(true);

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
        });*/
    }
}
