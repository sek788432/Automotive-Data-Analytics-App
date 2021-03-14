package com.github.mikephil.charting.data;

public abstract class BaseEntry {
    private Object mData;
    private float y;

    public BaseEntry() {
        this.y = 0.0f;
        this.mData = null;
    }

    public BaseEntry(float y2) {
        this.y = 0.0f;
        this.mData = null;
        this.y = y2;
    }

    public BaseEntry(float y2, Object data) {
        this(y2);
        this.mData = data;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y2) {
        this.y = y2;
    }

    public Object getData() {
        return this.mData;
    }

    public void setData(Object data) {
        this.mData = data;
    }
}
