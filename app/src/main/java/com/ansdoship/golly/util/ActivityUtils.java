package com.ansdoship.golly.util;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * A utility class providing functions about activity.
 */
public class ActivityUtils {

    /**
     * Hide activity's navigation bar.
     * @param activity the activity which will be hide navigation bar
     */
    public static void hideNavigationBar(@NonNull Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int uiOptions;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            uiOptions = View.GONE;
        }
        else {
            uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        decorView.setSystemUiVisibility(uiOptions);
    }

    /**
     * Hide activity's title bar.
     * @param activity the activity which will be hide title bar
     */
    public static void hideTitleBar(@NonNull Activity activity) {
        activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Hide activity's action bar.
     * @param activity the activity which will be hide action bar
     */
    public static void hideActionBar(@NonNull Activity activity) {
        if (activity instanceof AppCompatActivity) {
            ((AppCompatActivity) activity).getSupportActionBar().hide();
        }
        else {
            activity.getActionBar().hide();
        }
    }

}
