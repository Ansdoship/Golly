package com.ansdoship.golly.activity;

import android.content.Intent;
import android.os.Bundle;

import com.ansdoship.golly.R;
import com.ansdoship.golly.util.ActivityUtils;

import java.util.Timer;
import java.util.TimerTask;

public class LogoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.hideActionBar(this);
        setContentView(R.layout.activity_logo);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(LogoActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2500);
    }

}