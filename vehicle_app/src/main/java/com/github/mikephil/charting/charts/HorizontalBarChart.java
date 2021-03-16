package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.HorizontalBarHighlighter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.HorizontalBarChartRenderer;
import com.github.mikephil.charting.renderer.XAxisRendererHorizontalBarChart;
import com.github.mikephil.charting.renderer.YAxisRendererHorizontalBarChart;
import com.github.mikephil.charting.utils.HorizontalViewPortHandler;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.TransformerHorizontalBarChart;
import com.github.mikephil.charting.utils.Utils;

public class HorizontalBarChart extends BarChart {
    protected float[] mGetPositionBuffer = new float[2];
    private RectF mOffsetsBuffer = new RectF();

    public HorizontalBarChart(Context context) {
        super(context);
    }

    public HorizontalBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* access modifiers changed from: protected */
    public void init() {
        this.mViewPortHandler = new HorizontalViewPortHandler();
        super.init();
        this.mLeftAxisTransformer = new TransformerHorizontalBarChart(this.mViewPortHandler);
        this.mRightAxisTransformer = new TransformerHorizontalBarChart(this.mViewPortHandler);
        this.mRenderer = new HorizontalBarChartRenderer(this, this.mAnimator, this.mViewPortHandler);
        setHighlighter(new HorizontalBarHighlighter(this));
        this.mAxisRendererLeft = new YAxisRendererHorizontalBarChart(this.mViewPortHandler, this.mAxisLeft, this.mLeftAxisTransformer);
        this.mAxisRendererRight = new YAxisRendererHorizontalBarChart(this.mViewPortHandler, this.mAxisRight, this.mRightAxisTransformer);
        this.mXAxisRenderer = new XAxisRendererHorizontalBarChart(this.mViewPortHandler, this.mXAxis, this.mLeftAxisTransformer, this);
    }

    public void calculateOffsets() {
        calculateLegendOffsets(this.mOffsetsBuffer);
        float offsetLeft = 0.0f + this.mOffsetsBuffer.left;
        float offsetTop = 0.0f + this.mOffsetsBuffer.top;
        float offsetRight = 0.0f + this.mOffsetsBuffer.right;
        float offsetBottom = 0.0f + this.mOffsetsBuffer.bottom;
        if (this.mAxisLeft.needsOffset()) {
            offsetTop += this.mAxisLeft.getRequiredHeightSpace(this.mAxisRendererLeft.getPaintAxisLabels());
        }
        if (this.mAxisRight.needsOffset()) {
            offsetBottom += this.mAxisRight.getRequiredHeightSpace(this.mAxisRendererRight.getPaintAxisLabels());
        }
        float xlabelwidth = (float) this.mXAxis.mLabelRotatedWidth;
        if (this.mXAxis.isEnabled()) {
            if (this.mXAxis.getPosition() == XAxis.XAxisPosition.BOTTOM) {
                offsetLeft += xlabelwidth;
            } else if (this.mXAxis.getPosition() == XAxis.XAxisPosition.TOP) {
                offsetRight += xlabelwidth;
            } else if (this.mXAxis.getPosition() == XAxis.XAxisPosition.BOTH_SIDED) {
                offsetLeft += xlabelwidth;
                offsetRight += xlabelwidth;
            }
        }
        float offsetTop2 = offsetTop + getExtraTopOffset();
        float offsetRight2 = offsetRight + getExtraRightOffset();
        float offsetBottom2 = offsetBottom + getExtraBottomOffset();
        float offsetLeft2 = offsetLeft + getExtraLeftOffset();
        float minOffset = Utils.convertDpToPixel(this.mMinOffset);
        this.mViewPortHandler.restrainViewPort(Math.max(minOffset, offsetLeft2), Math.max(minOffset, offsetTop2), Math.max(minOffset, offsetRight2), Math.max(minOffset, offsetBottom2));
        if (this.mLogEnabled) {
            Log.i(Chart.LOG_TAG, "offsetLeft: " + offsetLeft2 + ", offsetTop: " + offsetTop2 + ", offsetRight: " + offsetRight2 + ", offsetBottom: " + offsetBottom2);
            StringBuilder sb = new StringBuilder();
            sb.append("Content: ");
            sb.append(this.mViewPortHandler.getContentRect().toString());
            Log.i(Chart.LOG_TAG, sb.toString());
        }
        prepareOffsetMatrix();
        prepareValuePxMatrix();
    }

    /* access modifiers changed from: protected */
    public void prepareValuePxMatrix() {
        this.mRightAxisTransformer.prepareMatrixValuePx(this.mAxisRight.mAxisMinimum, this.mAxisRight.mAxisRange, this.mXAxis.mAxisRange, this.mXAxis.mAxisMinimum);
        this.mLeftAxisTransformer.prepareMatrixValuePx(this.mAxisLeft.mAxisMinimum, this.mAxisLeft.mAxisRange, this.mXAxis.mAxisRange, this.mXAxis.mAxisMinimum);
    }

    /* access modifiers changed from: protected */
    public float[] getMarkerPosition(Highlight high) {
        return new float[]{high.getDrawY(), high.getDrawX()};
    }

    public void getBarBounds(BarEntry e2, RectF outputRect) {
        RectF bounds = outputRect;
        IBarDataSet set = (IBarDataSet) ((BarData) this.mData).getDataSetForEntry(e2);
        if (set == null) {
            outputRect.set(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
            return;
        }
        float y = e2.getY();
        float x = e2.getX();
        float barWidth = ((BarData) this.mData).getBarWidth();
        float top = x - (barWidth / 2.0f);
        float bottom = (barWidth / 2.0f) + x;
        float right = 0.0f;
        float left = y >= 0.0f ? y : 0.0f;
        if (y <= 0.0f) {
            right = y;
        }
        bounds.set(left, top, right, bottom);
        getTransformer(set.getAxisDependency()).rectValueToPixel(bounds);
    }

    public MPPointF getPosition(Entry e2, YAxis.AxisDependency axis) {
        if (e2 == null) {
            return null;
        }
        float[] vals = this.mGetPositionBuffer;
        vals[0] = e2.getY();
        vals[1] = e2.getX();
        getTransformer(axis).pointValuesToPixel(vals);
        return MPPointF.getInstance(vals[0], vals[1]);
    }

    public Highlight getHighlightByTouchPoint(float x, float y) {
        if (this.mData != null) {
            return getHighlighter().getHighlight(y, x);
        }
        if (!this.mLogEnabled) {
            return null;
        }
        Log.e(Chart.LOG_TAG, "Can't select by touch. No data set.");
        return null;
    }

    public float getLowestVisibleX() {
        getTransformer(YAxis.AxisDependency.LEFT).getValuesByTouchPoint(this.mViewPortHandler.contentLeft(), this.mViewPortHandler.contentBottom(), this.posForGetLowestVisibleX);
        return (float) Math.max((double) this.mXAxis.mAxisMinimum, this.posForGetLowestVisibleX.y);
    }

    public float getHighestVisibleX() {
        getTransformer(YAxis.AxisDependency.LEFT).getValuesByTouchPoint(this.mViewPortHandler.contentLeft(), this.mViewPortHandler.contentTop(), this.posForGetHighestVisibleX);
        return (float) Math.min((double) this.mXAxis.mAxisMaximum, this.posForGetHighestVisibleX.y);
    }

    public void setVisibleXRangeMaximum(float maxXRange) {
        this.mViewPortHandler.setMinimumScaleY(this.mXAxis.mAxisRange / maxXRange);
    }

    public void setVisibleXRangeMinimum(float minXRange) {
        this.mViewPortHandler.setMaximumScaleY(this.mXAxis.mAxisRange / minXRange);
    }

    public void setVisibleXRange(float minXRange, float maxXRange) {
        this.mViewPortHandler.setMinMaxScaleY(this.mXAxis.mAxisRange / minXRange, this.mXAxis.mAxisRange / maxXRange);
    }

    public void setVisibleYRangeMaximum(float maxYRange, YAxis.AxisDependency axis) {
        this.mViewPortHandler.setMinimumScaleX(getAxisRange(axis) / maxYRange);
    }

    public void setVisibleYRangeMinimum(float minYRange, YAxis.AxisDependency axis) {
        this.mViewPortHandler.setMaximumScaleX(getAxisRange(axis) / minYRange);
    }

    public void setVisibleYRange(float minYRange, float maxYRange, YAxis.AxisDependency axis) {
        this.mViewPortHandler.setMinMaxScaleX(getAxisRange(axis) / minYRange, getAxisRange(axis) / maxYRange);
    }
}
