package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

public class HorizontalBarBuffer extends BarBuffer {
    public HorizontalBarBuffer(int size, int dataSetCount, boolean containsStacks) {
        super(size, dataSetCount, containsStacks);
    }

    public void feed(IBarDataSet data) {
        float size;
        float right;
        float left;
        float f;
        float yStart;
        float y;
        float right2;
        float left2;
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
                        float bottom = x - barWidthHalf;
                        float top = x + barWidthHalf;
                        float size3 = size2;
                        if (this.mInverted) {
                            left2 = y >= yStart ? y : yStart;
                            right2 = y <= yStart ? y : yStart;
                        } else {
                            right2 = y >= yStart ? y : yStart;
                            left2 = y <= yStart ? y : yStart;
                        }
                        addBar(left2 * this.phaseY, top, this.phaseY * right2, bottom);
                        k++;
                        size2 = size3;
                        e2 = e2;
                        IBarDataSet iBarDataSet = data;
                        f2 = 0.0f;
                    }
                    size = size2;
                }
                float size4 = x - barWidthHalf;
                float top2 = x + barWidthHalf;
                if (this.mInverted) {
                    f = 0.0f;
                    left = y2 >= 0.0f ? y2 : 0.0f;
                    right = y2 <= 0.0f ? y2 : 0.0f;
                } else {
                    f = 0.0f;
                    right = y2 >= 0.0f ? y2 : 0.0f;
                    left = y2 <= 0.0f ? y2 : 0.0f;
                }
                if (right > f) {
                    right *= this.phaseY;
                } else {
                    left *= this.phaseY;
                }
                addBar(left, top2, right, size4);
            }
            i++;
            size2 = size;
        }
        reset();
    }
}
