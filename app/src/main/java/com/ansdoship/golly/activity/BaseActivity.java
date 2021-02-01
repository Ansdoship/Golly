package com.ansdoship.golly.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ansdoship.golly.R;
import com.ansdoship.golly.util.ActivityUtils;
import com.ansdoship.golly.util.DialogUtils;

import java.lang.reflect.Method;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_anim_in_enter, R.anim.activity_anim_in_exit);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_anim_out_enter, R.anim.activity_anim_out_exit);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (!(getCurrentFocus() instanceof EditText)) {
                clearFocus();
            }
            ActivityUtils.setImmersiveMode(this);
        }
    }

    protected void clearFocus() {
        View focus = getCurrentFocus();
        if (focus != null) {
            focus.clearFocus();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.mi_help:
                AlertDialog helpDialog = new AlertDialog.Builder(this)
                        .setMessage(R.string.help_content).create();
                helpDialog.show();
                TextView mMessageView = DialogUtils.getMessageView(helpDialog);
                if (mMessageView != null) {
                    mMessageView.setTextSize(18);
                }
                return true;
            case R.id.mi_donate:
                //new AlertDialog.Builder(this).setView().create().show();
                return true;
            case R.id.mi_info:
                AlertDialog infoDialog = new AlertDialog.Builder(this).setView(R.layout.dialog_info).create();
                infoDialog.show();
                TextView tvSourceCodeUrl = infoDialog.findViewById(R.id.tv_source_code_url);
                if (tvSourceCodeUrl != null) {
                    tvSourceCodeUrl.setOnClickListener(view ->
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.source_code_url)))));
                }
                return true;
            case R.id.mi_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    protected void createComingSoonDialog() {
        AlertDialog comingSoonDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.coming_soon).create();
        comingSoonDialog.show();
        TextView mMessageView = DialogUtils.getMessageView(comingSoonDialog);
        if (mMessageView != null) {
            mMessageView.setTextSize(18);
        }
    }

}
