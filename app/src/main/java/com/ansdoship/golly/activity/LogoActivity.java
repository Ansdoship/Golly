package com.ansdoship.golly.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ansdoship.golly.R;
import com.ansdoship.golly.util.ActivityUtils;

import java.util.Timer;
import java.util.TimerTask;

public class LogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.hideActionBar(this);
        ActivityUtils.hideStatusBar(this);
        setContentView(R.layout.activity_logo);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(LogoActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2500);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ActivityUtils.hideNavigationBar(this);
            View focus = getCurrentFocus();
            if (focus != null) {
                focus.clearFocus();
            }
        }
    }

}