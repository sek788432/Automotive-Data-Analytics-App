package com.github.mikephil.charting.data;

import android.annotation.SuppressLint;

@SuppressLint({"ParcelCreator"})
public class RadarEntry extends Entry {
    public RadarEntry(float value) {
        super(0.0f, value);
    }

    public RadarEntry(float value, Object data) {
        super(0.0f, value, data);
    }

    public float getValue() {
        return getY();
    }

    public RadarEntry copy() {
        return new RadarEntry(getY(), getData());
    }

    @Deprecated
    public void setX(float x) {
        super.setX(x);
    }

    @Deprecated
    public float getX() {
        return super.getX();
    }
}
