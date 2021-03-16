package com.github.mikephil.charting.data;

import android.graphics.Paint;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class CandleDataSet extends LineScatterCandleRadarDataSet<CandleEntry> implements ICandleDataSet {
    private float mBarSpace = 0.1f;
    protected int mDecreasingColor = ColorTemplate.COLOR_SKIP;
    protected Paint.Style mDecreasingPaintStyle = Paint.Style.FILL;
    protected int mIncreasingColor = ColorTemplate.COLOR_SKIP;
    protected Paint.Style mIncreasingPaintStyle = Paint.Style.STROKE;
    protected int mNeutralColor = ColorTemplate.COLOR_SKIP;
    protected int mShadowColor = ColorTemplate.COLOR_SKIP;
    private boolean mShadowColorSameAsCandle = false;
    private float mShadowWidth = 3.0f;
    private boolean mShowCandleBar = true;

    public CandleDataSet(List<CandleEntry> yVals, String label) {
        super(yVals, label);
    }

    public DataSet<CandleEntry> copy() {
        List<CandleEntry> yVals = new ArrayList<>();
        yVals.clear();
        for (int i = 0; i < this.mValues.size(); i++) {
            yVals.add(((CandleEntry) this.mValues.get(i)).copy());
        }
        CandleDataSet copied = new CandleDataSet(yVals, getLabel());
        copied.mColors = this.mColors;
        copied.mShadowWidth = this.mShadowWidth;
        copied.mShowCandleBar = this.mShowCandleBar;
        copied.mBarSpace = this.mBarSpace;
        copied.mHighLightColor = this.mHighLightColor;
        copied.mIncreasingPaintStyle = this.mIncreasingPaintStyle;
        copied.mDecreasingPaintStyle = this.mDecreasingPaintStyle;
        copied.mShadowColor = this.mShadowColor;
        return copied;
    }

    /* access modifiers changed from: protected */
    public void calcMinMax(CandleEntry e2) {
        if (e2.getLow() < this.mYMin) {
            this.mYMin = e2.getLow();
        }
        if (e2.getHigh() > this.mYMax) {
            this.mYMax = e2.getHigh();
        }
        calcMinMaxX(e2);
    }

    /* access modifiers changed from: protected */
    public void calcMinMaxY(CandleEntry e2) {
        if (e2.getHigh() < this.mYMin) {
            this.mYMin = e2.getHigh();
        }
        if (e2.getHigh() > this.mYMax) {
            this.mYMax = e2.getHigh();
        }
        if (e2.getLow() < this.mYMin) {
            this.mYMin = e2.getLow();
        }
        if (e2.getLow() > this.mYMax) {
            this.mYMax = e2.getLow();
        }
    }

    public void setBarSpace(float space) {
        if (space < 0.0f) {
            space = 0.0f;
        }
        if (space > 0.45f) {
            space = 0.45f;
        }
        this.mBarSpace = space;
    }

    public float getBarSpace() {
        return this.mBarSpace;
    }

    public void setShadowWidth(float width) {
        this.mShadowWidth = Utils.convertDpToPixel(width);
    }

    public float getShadowWidth() {
        return this.mShadowWidth;
    }

    public void setShowCandleBar(boolean showCandleBar) {
        this.mShowCandleBar = showCandleBar;
    }

    public boolean getShowCandleBar() {
        return this.mShowCandleBar;
    }

    public void setNeutralColor(int color) {
        this.mNeutralColor = color;
    }

    public int getNeutralColor() {
        return this.mNeutralColor;
    }

    public void setIncreasingColor(int color) {
        this.mIncreasingColor = color;
    }

    public int getIncreasingColor() {
        return this.mIncreasingColor;
    }

    public void setDecreasingColor(int color) {
        this.mDecreasingColor = color;
    }

    public int getDecreasingColor() {
        return this.mDecreasingColor;
    }

    public Paint.Style getIncreasingPaintStyle() {
        return this.mIncreasingPaintStyle;
    }

    public void setIncreasingPaintStyle(Paint.Style paintStyle) {
        this.mIncreasingPaintStyle = paintStyle;
    }

    public Paint.Style getDecreasingPaintStyle() {
        return this.mDecreasingPaintStyle;
    }

    public void setDecreasingPaintStyle(Paint.Style decreasingPaintStyle) {
        this.mDecreasingPaintStyle = decreasingPaintStyle;
    }

    public int getShadowColor() {
        return this.mShadowColor;
    }

    public void setShadowColor(int shadowColor) {
        this.mShadowColor = shadowColor;
    }

    public boolean getShadowColorSameAsCandle() {
        return this.mShadowColorSameAsCandle;
    }

    public void setShadowColorSameAsCandle(boolean shadowColorSameAsCandle) {
        this.mShadowColorSameAsCandle = shadowColorSameAsCandle;
    }
}
