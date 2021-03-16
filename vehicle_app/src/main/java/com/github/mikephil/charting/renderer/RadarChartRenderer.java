package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class RadarChartRenderer extends LineRadarRenderer {
    protected RadarChart mChart;
    protected Path mDrawDataSetSurfacePathBuffer = new Path();
    protected Path mDrawHighlightCirclePathBuffer = new Path();
    protected Paint mHighlightCirclePaint;
    protected Paint mWebPaint;

    public RadarChartRenderer(RadarChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;
        this.mHighlightPaint = new Paint(1);
        this.mHighlightPaint.setStyle(Paint.Style.STROKE);
        this.mHighlightPaint.setStrokeWidth(2.0f);
        this.mHighlightPaint.setColor(Color.rgb(255, 187, 115));
        this.mWebPaint = new Paint(1);
        this.mWebPaint.setStyle(Paint.Style.STROKE);
        this.mHighlightCirclePaint = new Paint(1);
    }

    public Paint getWebPaint() {
        return this.mWebPaint;
    }

    public void initBuffers() {
    }

    public void drawData(Canvas c) {
        RadarData radarData = (RadarData) this.mChart.getData();
        int mostEntries = ((IRadarDataSet) radarData.getMaxEntryCountSet()).getEntryCount();
        for (IRadarDataSet set : radarData.getDataSets()) {
            if (set.isVisible()) {
                drawDataSet(c, set, mostEntries);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawDataSet(Canvas c, IRadarDataSet dataSet, int mostEntries) {
        Canvas canvas = c;
        IRadarDataSet iRadarDataSet = dataSet;
        float phaseX = this.mAnimator.getPhaseX();
        float phaseY = this.mAnimator.getPhaseY();
        float sliceangle = this.mChart.getSliceAngle();
        float factor = this.mChart.getFactor();
        MPPointF center = this.mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0.0f, 0.0f);
        Path surface = this.mDrawDataSetSurfacePathBuffer;
        surface.reset();
        boolean hasMovedToPoint = false;
        for (int j = 0; j < dataSet.getEntryCount(); j++) {
            this.mRenderPaint.setColor(iRadarDataSet.getColor(j));
            Utils.getPosition(center, (((RadarEntry) iRadarDataSet.getEntryForIndex(j)).getY() - this.mChart.getYChartMin()) * factor * phaseY, (((float) j) * sliceangle * phaseX) + this.mChart.getRotationAngle(), pOut);
            if (!Float.isNaN(pOut.x)) {
                if (!hasMovedToPoint) {
                    surface.moveTo(pOut.x, pOut.y);
                    hasMovedToPoint = true;
                } else {
                    surface.lineTo(pOut.x, pOut.y);
                }
            }
        }
        if (dataSet.getEntryCount() > mostEntries) {
            surface.lineTo(center.x, center.y);
        }
        surface.close();
        if (dataSet.isDrawFilledEnabled()) {
            Drawable drawable = dataSet.getFillDrawable();
            if (drawable != null) {
                drawFilledPath(canvas, surface, drawable);
            } else {
                drawFilledPath(canvas, surface, dataSet.getFillColor(), dataSet.getFillAlpha());
            }
        }
        this.mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
        this.mRenderPaint.setStyle(Paint.Style.STROKE);
        if (!dataSet.isDrawFilledEnabled() || dataSet.getFillAlpha() < 255) {
            canvas.drawPath(surface, this.mRenderPaint);
        }
        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
    }

    public void drawValues(Canvas c) {
        float phaseX = this.mAnimator.getPhaseX();
        float phaseY = this.mAnimator.getPhaseY();
        float sliceangle = this.mChart.getSliceAngle();
        float factor = this.mChart.getFactor();
        MPPointF center = this.mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0.0f, 0.0f);
        float yoffset = Utils.convertDpToPixel(5.0f);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < ((RadarData) this.mChart.getData()).getDataSetCount()) {
                IRadarDataSet dataSet = (IRadarDataSet) ((RadarData) this.mChart.getData()).getDataSetByIndex(i2);
                if (shouldDrawValues(dataSet)) {
                    applyValueTextStyle(dataSet);
                    int j = 0;
                    while (true) {
                        int j2 = j;
                        if (j2 >= dataSet.getEntryCount()) {
                            break;
                        }
                        RadarEntry entry = (RadarEntry) dataSet.getEntryForIndex(j2);
                        Utils.getPosition(center, (entry.getY() - this.mChart.getYChartMin()) * factor * phaseY, (((float) j2) * sliceangle * phaseX) + this.mChart.getRotationAngle(), pOut);
                        IValueFormatter valueFormatter = dataSet.getValueFormatter();
                        float y = entry.getY();
                        float f = pOut.x;
                        float f2 = pOut.y - yoffset;
                        int i3 = dataSet.getValueTextColor(j2);
                        int j3 = j2;
                        IRadarDataSet dataSet2 = dataSet;
                        drawValue(c, valueFormatter, y, entry, i2, f, f2, i3);
                        j = j3 + 1;
                        i2 = i2;
                        dataSet = dataSet2;
                    }
                }
                i = i2 + 1;
            } else {
                MPPointF.recycleInstance(center);
                MPPointF.recycleInstance(pOut);
                return;
            }
        }
    }

    public void drawExtras(Canvas c) {
        drawWeb(c);
    }

    /* access modifiers changed from: protected */
    public void drawWeb(Canvas c) {
        float sliceangle = this.mChart.getSliceAngle();
        float factor = this.mChart.getFactor();
        float rotationangle = this.mChart.getRotationAngle();
        MPPointF center = this.mChart.getCenterOffsets();
        this.mWebPaint.setStrokeWidth(this.mChart.getWebLineWidth());
        this.mWebPaint.setColor(this.mChart.getWebColor());
        this.mWebPaint.setAlpha(this.mChart.getWebAlpha());
        int xIncrements = this.mChart.getSkipWebLineCount() + 1;
        int maxEntryCount = ((IRadarDataSet) ((RadarData) this.mChart.getData()).getMaxEntryCountSet()).getEntryCount();
        MPPointF p = MPPointF.getInstance(0.0f, 0.0f);
        for (int i = 0; i < maxEntryCount; i += xIncrements) {
            Utils.getPosition(center, this.mChart.getYRange() * factor, (((float) i) * sliceangle) + rotationangle, p);
            c.drawLine(center.x, center.y, p.x, p.y, this.mWebPaint);
        }
        MPPointF.recycleInstance(p);
        this.mWebPaint.setStrokeWidth(this.mChart.getWebLineWidthInner());
        this.mWebPaint.setColor(this.mChart.getWebColorInner());
        this.mWebPaint.setAlpha(this.mChart.getWebAlpha());
        int labelCount = this.mChart.getYAxis().mEntryCount;
        MPPointF p1out = MPPointF.getInstance(0.0f, 0.0f);
        MPPointF p2out = MPPointF.getInstance(0.0f, 0.0f);
        for (int j = 0; j < labelCount; j++) {
            int i2 = 0;
            while (i2 < ((RadarData) this.mChart.getData()).getEntryCount()) {
                float r = (this.mChart.getYAxis().mEntries[j] - this.mChart.getYChartMin()) * factor;
                Utils.getPosition(center, r, (((float) i2) * sliceangle) + rotationangle, p1out);
                Utils.getPosition(center, r, (((float) (i2 + 1)) * sliceangle) + rotationangle, p2out);
                float sliceangle2 = sliceangle;
                float factor2 = factor;
                c.drawLine(p1out.x, p1out.y, p2out.x, p2out.y, this.mWebPaint);
                i2++;
                sliceangle = sliceangle2;
                factor = factor2;
                rotationangle = rotationangle;
            }
            float f = factor;
            float f2 = rotationangle;
        }
        float f3 = factor;
        float f4 = rotationangle;
        MPPointF.recycleInstance(p1out);
        MPPointF.recycleInstance(p2out);
    }

    public void drawHighlighted(Canvas c, Highlight[] indices) {
        int i;
        int i2;
        Highlight[] highlightArr = indices;
        float sliceangle = this.mChart.getSliceAngle();
        float factor = this.mChart.getFactor();
        MPPointF center = this.mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0.0f, 0.0f);
        RadarData radarData = (RadarData) this.mChart.getData();
        int length = highlightArr.length;
        int i3 = 0;
        int i4 = 0;
        while (i4 < length) {
            Highlight high = highlightArr[i4];
            IRadarDataSet set = (IRadarDataSet) radarData.getDataSetByIndex(high.getDataSetIndex());
            if (set != null && set.isHighlightEnabled()) {
                RadarEntry e2 = (RadarEntry) set.getEntryForIndex((int) high.getX());
                if (isInBoundsX(e2, set)) {
                    Utils.getPosition(center, (e2.getY() - this.mChart.getYChartMin()) * factor * this.mAnimator.getPhaseY(), (high.getX() * sliceangle * this.mAnimator.getPhaseX()) + this.mChart.getRotationAngle(), pOut);
                    high.setDraw(pOut.x, pOut.y);
                    drawHighlightLines(c, pOut.x, pOut.y, set);
                    if (set.isDrawHighlightCircleEnabled() && !Float.isNaN(pOut.x) && !Float.isNaN(pOut.y)) {
                        int strokeColor = set.getHighlightCircleStrokeColor();
                        if (strokeColor == 1122867) {
                            strokeColor = set.getColor(i3);
                        }
                        if (set.getHighlightCircleStrokeAlpha() < 255) {
                            strokeColor = ColorTemplate.colorWithAlpha(strokeColor, set.getHighlightCircleStrokeAlpha());
                        }
                        float highlightCircleInnerRadius = set.getHighlightCircleInnerRadius();
                        float highlightCircleOuterRadius = set.getHighlightCircleOuterRadius();
                        int highlightCircleFillColor = set.getHighlightCircleFillColor();
                        float highlightCircleStrokeWidth = set.getHighlightCircleStrokeWidth();
                        RadarEntry radarEntry = e2;
                        IRadarDataSet iRadarDataSet = set;
                        float f = highlightCircleOuterRadius;
                        Highlight highlight = high;
                        int i5 = highlightCircleFillColor;
                        i = i4;
                        i2 = 0;
                        drawHighlightCircle(c, pOut, highlightCircleInnerRadius, f, i5, strokeColor, highlightCircleStrokeWidth);
                        i4 = i + 1;
                        i3 = i2;
                    }
                }
            }
            i = i4;
            i2 = i3;
            i4 = i + 1;
            i3 = i2;
        }
        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
    }

    public void drawHighlightCircle(Canvas c, MPPointF point, float innerRadius, float outerRadius, int fillColor, int strokeColor, float strokeWidth) {
        c.save();
        float outerRadius2 = Utils.convertDpToPixel(outerRadius);
        float innerRadius2 = Utils.convertDpToPixel(innerRadius);
        if (fillColor != 1122867) {
            Path p = this.mDrawHighlightCirclePathBuffer;
            p.reset();
            p.addCircle(point.x, point.y, outerRadius2, Path.Direction.CW);
            if (innerRadius2 > 0.0f) {
                p.addCircle(point.x, point.y, innerRadius2, Path.Direction.CCW);
            }
            this.mHighlightCirclePaint.setColor(fillColor);
            this.mHighlightCirclePaint.setStyle(Paint.Style.FILL);
            c.drawPath(p, this.mHighlightCirclePaint);
        }
        if (strokeColor != 1122867) {
            this.mHighlightCirclePaint.setColor(strokeColor);
            this.mHighlightCirclePaint.setStyle(Paint.Style.STROKE);
            this.mHighlightCirclePaint.setStrokeWidth(Utils.convertDpToPixel(strokeWidth));
            c.drawCircle(point.x, point.y, outerRadius2, this.mHighlightCirclePaint);
        }
        c.restore();
    }
}
