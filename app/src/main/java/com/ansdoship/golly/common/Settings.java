package com.ansdoship.golly.common;

import android.graphics.Color;

import java.util.concurrent.locks.ReentrantLock;

public class Settings {

    private enum Singleton {
        INSTANCE;
        private final Settings instance;
        Singleton() {
            instance = new Settings();
        }
        public Settings getInstance() {
            return instance;
        }
    }

    public static Settings getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private Settings() {
        paletteColorLock = new ReentrantLock(true);
        paletteColor = PALETTE_COLOR_DEFAULT;
        drawFpsLimitLock = new ReentrantLock(true);
        drawFpsLimit = DRAW_FPS_LIMIT_DEFAULT;
        iterationLandFpsLimitLock = new ReentrantLock(true);
        iterationLandFpsLimit = ITERATION_LAND_FPS_LIMIT_DEFAULT;
    }

    public static final int PALETTE_COLOR_DEFAULT = Color.BLACK;
    private final ReentrantLock paletteColorLock;
    private int paletteColor;

    public static final int DRAW_FPS_LIMIT_DEFAULT = 30;
    private final ReentrantLock drawFpsLimitLock;
    private int drawFpsLimit;

    public static final int ITERATION_LAND_FPS_LIMIT_DEFAULT = 5;
    private final ReentrantLock iterationLandFpsLimitLock;
    private int iterationLandFpsLimit;

    public int getPaletteColor() {
        return paletteColor;
    }

    public void setPaletteColor(int paletteColor) {
        paletteColorLock.lock();
        try {
            this.paletteColor = paletteColor;
        }
        finally {
            paletteColorLock.unlock();
        }
    }

    public int getDrawFpsLimit() {
        return drawFpsLimit;
    }

    public void setDrawFpsLimit(int drawFpsLimit) {
        drawFpsLimitLock.lock();
        try {
            this.drawFpsLimit = drawFpsLimit;
        }
        finally {
            drawFpsLimitLock.unlock();
        }
    }

    public int getIterationLandFpsLimit() {
        return iterationLandFpsLimit;
    }

    public void setIterationLandFpsLimit(int iterationLandFpsLimit) {
        iterationLandFpsLimitLock.lock();
        try {
            this.iterationLandFpsLimit = iterationLandFpsLimit;
        }
        finally {
            iterationLandFpsLimitLock.unlock();
        }
    }

}
