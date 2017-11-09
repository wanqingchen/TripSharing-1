package com.example.tainingzhang.tripsharing_v0;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class profileActivity extends AppCompatActivity {

    private TextView tvEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        tvEmail = (TextView) findViewById(R.id.txtemailregistration);
        tvEmail.setText(getIntent().getExtras().getString("Email"));
    }

    public void btnInfo_Click(View v) {
        Intent i = new Intent(profileActivity.this, userinfoActivity.class);
        startActivity(i);
    }
}
