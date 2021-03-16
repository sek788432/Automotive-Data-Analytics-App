package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

public class BarBuffer extends AbstractBuffer<IBarDataSet> {
    protected float mBarWidth = 1.0f;
    protected boolean mContainsStacks = false;
    protected int mDataSetCount = 1;
    protected int mDataSetIndex = 0;
    protected boolean mInverted = false;

    public BarBuffer(int size, int dataSetCount, boolean containsStacks) {
        super(size);
        this.mDataSetCount = dataSetCount;
        this.mContainsStacks = containsStacks;
    }

    public void setBarWidth(float barWidth) {
        this.mBarWidth = barWidth;
    }

    public void setDataSet(int index) {
        this.mDataSetIndex = index;
    }

    public void setInverted(boolean inverted) {
        this.mInverted = inverted;
    }

    /* access modifiers changed from: protected */
    public void addBar(float left, float top, float right, float bottom) {
        float[] fArr = this.buffer;
        int i = this.index;
        this.index = i + 1;
        fArr[i] = left;
        float[] fArr2 = this.buffer;
        int i2 = this.index;
        this.index = i2 + 1;
        fArr2[i2] = top;
        float[] fArr3 = this.buffer;
        int i3 = this.index;
        this.index = i3 + 1;
        fArr3[i3] = right;
        float[] fArr4 = this.buffer;
        int i4 = this.index;
        this.index = i4 + 1;
        fArr4[i4] = bottom;
    }

    public void feed(IBarDataSet data) {
        float size;
        float top;
        float bottom;
        float f;
        float yStart;
        float y;
        float top2;
        float bottom2;
        float size2 = ((float) data.getEntryCount()) * this.phaseX;
        float barWidthHalf = this.mBarWidth / 2.0f;
        int i = 0;
        while (((float) i) < size2) {
            BarEntry e2 = (BarEntry) data.getEntryForIndex(i);
            if (e2 == null) {
                size = size2;
            } else {
                float x = e2.getX();
                float y2 = e2.getY();
                float[] vals = e2.getYVals();
                float f2 = 0.0f;
                if (!this.mContainsStacks) {
                    size = size2;
                    BarEntry barEntry = e2;
                } else if (vals == null) {
                    size = size2;
                    BarEntry barEntry2 = e2;
                } else {
                    float negY = -e2.getNegativeSum();
                    float posY = 0.0f;
                    float posY2 = y2;
                    int k = 0;
                    while (k < vals.length) {
                        float value = vals[k];
                        if (value >= f2) {
                            y = posY;
                            yStart = posY + value;
                            posY = yStart;
                        } else {
                            y = negY;
                            float yStart2 = negY + Math.abs(value);
                            negY += Math.abs(value);
                            yStart = yStart2;
                        }
                        float left = x - barWidthHalf;
                        float right = x + barWidthHalf;
                        float size3 = size2;
                        if (this.mInverted) {
                            bottom2 = y >= yStart ? y : yStart;
                            top2 = y <= yStart ? y : yStart;
                        } else {
                            top2 = y >= yStart ? y : yStart;
                            bottom2 = y <= yStart ? y : yStart;
                        }
                        addBar(left, this.phaseY * top2, right, bottom2 * this.phaseY);
                        k++;
                        size2 = size3;
                        e2 = e2;
                        IBarDataSet iBarDataSet = data;
                        f2 = 0.0f;
                    }
                    size = size2;
                }
                float left2 = x - barWidthHalf;
                float right2 = x + barWidthHalf;
                if (this.mInverted) {
                    f = 0.0f;
                    bottom = y2 >= 0.0f ? y2 : 0.0f;
                    top = y2 <= 0.0f ? y2 : 0.0f;
                } else {
                    f = 0.0f;
                    top = y2 >= 0.0f ? y2 : 0.0f;
                    bottom = y2 <= 0.0f ? y2 : 0.0f;
                }
                if (top > f) {
                    top *= this.phaseY;
                } else {
                    bottom *= this.phaseY;
                }
                addBar(left2, top, right2, bottom);
            }
            i++;
            size2 = size;
        }
        reset();
    }
}
