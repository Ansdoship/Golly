package com.ansdoship.golly.util;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public class ScreenUtils {

    public static @NonNull Point getScreenSize() {
        return new Point(getScreenWidth(), getScreenHeight());
    }

    public static int getScreenWidth() {
        return ApplicationUtils.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return ApplicationUtils.getResources().getDisplayMetrics().heightPixels;
    }

    public static @NonNull Point getScreenRealSize() {
        WindowManager windowManager =
                (WindowManager) ApplicationUtils.getApplication().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            windowManager.getDefaultDisplay().getRealSize(point);
        }
        else {
            windowManager.getDefaultDisplay().getSize(point);
        }
        return point;
    }

    public static int getScreenRealWidth() {
        return getScreenRealSize().x;
    }

    public static int getScreenRealHeight() {
        return getScreenRealSize().y;
    }

}
