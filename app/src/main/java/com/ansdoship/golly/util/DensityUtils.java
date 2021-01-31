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

    /**
     * convert px to its equivalent sp
     * @param pxValue the px value
     * @return the sp value
     */
    public static float px2sp(float pxValue) {
        final float fontScale = ApplicationUtils.getResources().getDisplayMetrics().scaledDensity;
        return pxValue / fontScale + 0.5f;
    }


    /**
     * convert sp to its equivalent px
     * @param spValue the sp value
     * @return the px value
     */
    public static float sp2px(float spValue) {
        final float fontScale = ApplicationUtils.getResources().getDisplayMetrics().scaledDensity;
        return spValue * fontScale + 0.5f;
    }

    /**
     * convert px to its equivalent sp
     * @param pxValue the px value
     * @return the sp value
     */
    public static double px2sp(double pxValue) {
        final float fontScale = ApplicationUtils.getResources().getDisplayMetrics().scaledDensity;
        return pxValue / fontScale + 0.5;
    }


    /**
     * convert sp to its equivalent px
     * @param spValue the sp value
     * @return the px value
     */
    public static double sp2px(double spValue) {
        final float fontScale = ApplicationUtils.getResources().getDisplayMetrics().scaledDensity;
        return spValue * fontScale + 0.5;
    }

}
