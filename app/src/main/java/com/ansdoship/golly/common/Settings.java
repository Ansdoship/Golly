package com.ansdoship.golly.common;

import android.graphics.Color;

public class Settings {

    private Settings(){}

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

    public static final int PALETTE_COLOR_DEFAULT = Color.BLACK;

    private int paletteColor = PALETTE_COLOR_DEFAULT;

    public int getPaletteColor() {
        return paletteColor;
    }

    public void setPaletteColor(int paletteColor) {
        this.paletteColor = paletteColor;
    }

}
