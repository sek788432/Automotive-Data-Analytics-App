package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.BarHighlighter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;

public class BarChart extends BarLineChartBase<BarData> implements BarDataProvider {
    private boolean mDrawBarShadow = false;
    private boolean mDrawValueAboveBar = true;
    private boolean mFitBars = false;
    protected boolean mHighlightFullBarEnabled = false;

    public BarChart(Context context) {
        super(context);
    }

    public BarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* access modifiers changed from: protected */
    public void init() {
        super.init();
        this.mRenderer = new BarChartRenderer(this, this.mAnimator, this.mViewPortHandler);
        setHighlighter(new BarHighlighter(this));
        getXAxis().setSpaceMin(0.5f);
        getXAxis().setSpaceMax(0.5f);
    }

    /* access modifiers changed from: protected */
    public void calcMinMax() {
        if (this.mFitBars) {
            this.mXAxis.calculate(((BarData) this.mData).getXMin() - (((BarData) this.mData).getBarWidth() / 2.0f), ((BarData) this.mData).getXMax() + (((BarData) this.mData).getBarWidth() / 2.0f));
        } else {
            this.mXAxis.calculate(((BarData) this.mData).getXMin(), ((BarData) this.mData).getXMax());
        }
        this.mAxisLeft.calculate(((BarData) this.mData).getYMin(YAxis.AxisDependency.LEFT), ((BarData) this.mData).getYMax(YAxis.AxisDependency.LEFT));
        this.mAxisRight.calculate(((BarData) this.mData).getYMin(YAxis.AxisDependency.RIGHT), ((BarData) this.mData).getYMax(YAxis.AxisDependency.RIGHT));
    }

    public Highlight getHighlightByTouchPoint(float x, float y) {
        if (this.mData == null) {
            Log.e(Chart.LOG_TAG, "Can't select by touch. No data set.");
            return null;
        }
        Highlight h = getHighlighter().getHighlight(x, y);
        if (h == null || !isHighlightFullBarEnabled()) {
            return h;
        }
        return new Highlight(h.getX(), h.getY(), h.getXPx(), h.getYPx(), h.getDataSetIndex(), -1, h.getAxis());
    }

    public RectF getBarBounds(BarEntry e2) {
        RectF bounds = new RectF();
        getBarBounds(e2, bounds);
        return bounds;
    }

    public void getBarBounds(BarEntry e2, RectF outputRect) {
        RectF bounds = outputRect;
        IBarDataSet set = (IBarDataSet) ((BarData) this.mData).getDataSetForEntry(e2);
        if (set == null) {
            bounds.set(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
            return;
        }
        float y = e2.getY();
        float x = e2.getX();
        float barWidth = ((BarData) this.mData).getBarWidth();
        float left = x - (barWidth / 2.0f);
        float right = (barWidth / 2.0f) + x;
        float bottom = 0.0f;
        float top = y >= 0.0f ? y : 0.0f;
        if (y <= 0.0f) {
            bottom = y;
        }
        bounds.set(left, top, right, bottom);
        getTransformer(set.getAxisDependency()).rectValueToPixel(outputRect);
    }

    public void setDrawValueAboveBar(boolean enabled) {
        this.mDrawValueAboveBar = enabled;
    }

    public boolean isDrawValueAboveBarEnabled() {
        return this.mDrawValueAboveBar;
    }

    public void setDrawBarShadow(boolean enabled) {
        this.mDrawBarShadow = enabled;
    }

    public boolean isDrawBarShadowEnabled() {
        return this.mDrawBarShadow;
    }

    public void setHighlightFullBarEnabled(boolean enabled) {
        this.mHighlightFullBarEnabled = enabled;
    }

    public boolean isHighlightFullBarEnabled() {
        return this.mHighlightFullBarEnabled;
    }

    public void highlightValue(float x, int dataSetIndex, int stackIndex) {
        highlightValue(new Highlight(x, dataSetIndex, stackIndex), false);
    }

    public BarData getBarData() {
        return (BarData) this.mData;
    }

    public void setFitBars(boolean enabled) {
        this.mFitBars = enabled;
    }

    public void groupBars(float fromX, float groupSpace, float barSpace) {
        if (getBarData() != null) {
            getBarData().groupBars(fromX, groupSpace, barSpace);
            notifyDataSetChanged();
            return;
        }
        throw new RuntimeException("You need to set data for the chart before grouping bars.");
    }
}
