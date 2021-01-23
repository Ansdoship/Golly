package com.ansdoship.golly.util;

public class DensityUtils {

    public static float dp2px(float dpValue) {
        final float scale = ApplicationUtils.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }

    public static float px2dp(float pxValue) {
        final float scale = ApplicationUtils.getResources().getDisplayMetrics().density;
        return pxValue / scale + 0.5f;
    }

    public static double dp2px(double dpValue) {
        final float scale = ApplicationUtils.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5;
    }

    public static double px2dp(double pxValue) {
        final float scale = ApplicationUtils.getResources().getDisplayMetrics().density;
        return pxValue / scale + 0.5;
    }

}
