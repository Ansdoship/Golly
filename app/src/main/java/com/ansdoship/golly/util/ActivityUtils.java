package com.ansdoship.golly.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * A utility class providing functions about activity.
 */
public class ActivityUtils {

    public static boolean isStatusBarShown(@NonNull Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        int flags = params.flags & (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return flags == params.flags;
    }

    public static int getStatusBarHeight() {
        return ApplicationUtils.getResources().getDimensionPixelSize(
                ApplicationUtils.getResources().getIdentifier("status_bar_height", "dimen","android"));
    }

    public static int getNavigationBarHeight() {
        return ApplicationUtils.getResources().getDimensionPixelSize(
                ApplicationUtils.getResources().getIdentifier("navigation_bar_height", "dimen","android"));
    }

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
            uiOptions =
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        }
        decorView.setSystemUiVisibility(uiOptions);
    }

    /**
     * Hide activity's title bar.
     * @param activity the activity which will be hide title bar
     */
    public static void hideStatusBar(@NonNull Activity activity) {
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
           ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
           if (actionBar != null) {
               actionBar.hide();
           }
        }
        else {
            android.app.ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }
    }

    public static void hideSoftKeyboard(@NonNull Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && activity.getCurrentFocus() != null) {
            if (activity.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

}
