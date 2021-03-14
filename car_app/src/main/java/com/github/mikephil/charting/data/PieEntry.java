package com.github.mikephil.charting.data;

import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint({"ParcelCreator"})
public class PieEntry extends Entry {
    private String label;

    public PieEntry(float value) {
        super(0.0f, value);
    }

    public PieEntry(float value, Object data) {
        super(0.0f, value, data);
    }

    public PieEntry(float value, String label2) {
        super(0.0f, value);
        this.label = label2;
    }

    public PieEntry(float value, String label2, Object data) {
        super(0.0f, value, data);
        this.label = label2;
    }

    public float getValue() {
        return getY();
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label2) {
        this.label = label2;
    }

    @Deprecated
    public void setX(float x) {
        super.setX(x);
        Log.i("DEPRECATED", "Pie entries do not have x values");
    }

    @Deprecated
    public float getX() {
        Log.i("DEPRECATED", "Pie entries do not have x values");
        return super.getX();
    }

    public PieEntry copy() {
        return new PieEntry(getY(), this.label, getData());
    }
}
