package com.ansdoship.golly.util;

import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class DrawUtils {

    private final static Paint eraser = new Paint();
    static {
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }
    private final static Paint bitmapPaint = new Paint();
    static {
        bitmapPaint.setAntiAlias(false);
    }

    public static Paint getEraser() {
        return eraser;
    }

    public static Paint getBitmapPaint() {
        return bitmapPaint;
    }

}
