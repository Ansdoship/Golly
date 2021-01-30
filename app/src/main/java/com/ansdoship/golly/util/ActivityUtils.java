package com.ansdoship.golly.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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

    public static void closeKeyboard(@NonNull Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && activity.getCurrentFocus() != null) {
            if (activity.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static boolean shouldCloseKeyboard(@NonNull Activity activity, MotionEvent event) {
        View focus = activity.getCurrentFocus();
        if (focus != null) {
            if (focus instanceof EditText) {
                int[] location = {0, 0};
                focus.getLocationInWindow(location);
                int left = location[0];
                int top = location[1];
                int bottom = top + focus.getHeight();
                int right = left + focus.getWidth();
                return !(event.getX() > left) || !(event.getX() < right)
                        || !(event.getY() > top) || !(event.getY() < bottom);
            }
        }
        return false;
    }

}
