package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Path;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.List;

public class YAxisRendererRadarChart extends YAxisRenderer {
    private RadarChart mChart;
    private Path mRenderLimitLinesPathBuffer = new Path();

    public YAxisRendererRadarChart(ViewPortHandler viewPortHandler, YAxis yAxis, RadarChart chart) {
        super(viewPortHandler, yAxis, (Transformer) null);
        this.mChart = chart;
    }

    /* access modifiers changed from: protected */
    public void computeAxisValues(float min, float max) {
        int n;
        int n2;
        double last;
        int n3;
        float yMin = min;
        float yMax = max;
        int labelCount = this.mAxis.getLabelCount();
        double range = (double) Math.abs(yMax - yMin);
        if (labelCount == 0 || range <= Utils.DOUBLE_EPSILON) {
            float f = yMax;
            int i = labelCount;
            double d = range;
        } else if (Double.isInfinite(range)) {
            float f2 = yMin;
            float f3 = yMax;
            int i2 = labelCount;
            double d2 = range;
        } else {
            double rawInterval = range / ((double) labelCount);
            double interval = (double) Utils.roundToNextSignificant(rawInterval);
            if (this.mAxis.isGranularityEnabled()) {
                interval = interval < ((double) this.mAxis.getGranularity()) ? (double) this.mAxis.getGranularity() : interval;
            }
            double intervalMagnitude = (double) Utils.roundToNextSignificant(Math.pow(10.0d, (double) ((int) Math.log10(interval))));
            int intervalSigDigit = (int) (interval / intervalMagnitude);
            if (intervalSigDigit > 5) {
                interval = Math.floor(10.0d * intervalMagnitude);
            }
            int centeringEnabled = this.mAxis.isCenterAxisLabelsEnabled();
            int n4 = centeringEnabled;
            if (this.mAxis.isForceLabelsEnabled()) {
                double d3 = range;
                float step = ((float) range) / ((float) (labelCount - 1));
                this.mAxis.mEntryCount = labelCount;
                if (this.mAxis.mEntries.length < labelCount) {
                    this.mAxis.mEntries = new float[labelCount];
                }
                float v = min;
                int i3 = 0;
                while (i3 < labelCount) {
                    this.mAxis.mEntries[i3] = v;
                    v += step;
                    i3++;
                    intervalSigDigit = intervalSigDigit;
                }
                n = labelCount;
                float f4 = yMin;
                float f5 = yMax;
                int i4 = labelCount;
                double d4 = rawInterval;
            } else {
                int i5 = intervalSigDigit;
                double first = interval == Utils.DOUBLE_EPSILON ? Utils.DOUBLE_EPSILON : Math.ceil(((double) yMin) / interval) * interval;
                if (centeringEnabled != 0) {
                    first -= interval;
                }
                if (interval == Utils.DOUBLE_EPSILON) {
                    n2 = n4;
                    double d5 = rawInterval;
                    last = Utils.DOUBLE_EPSILON;
                } else {
                    n2 = n4;
                    double d6 = rawInterval;
                    last = Utils.nextUp(Math.floor(((double) yMax) / interval) * interval);
                }
                if (interval != Utils.DOUBLE_EPSILON) {
                    n3 = n2;
                    for (double f6 = first; f6 <= last; f6 += interval) {
                        n3++;
                    }
                } else {
                    n3 = n2;
                }
                int n5 = ((int) n3) + 1;
                this.mAxis.mEntryCount = n5;
                if (this.mAxis.mEntries.length < n5) {
                    this.mAxis.mEntries = new float[n5];
                }
                double f7 = first;
                int i6 = 0;
                while (i6 < n5) {
                    if (f7 == Utils.DOUBLE_EPSILON) {
                        f7 = Utils.DOUBLE_EPSILON;
                    }
                    float yMin2 = yMin;
                    double f8 = f7;
                    this.mAxis.mEntries[i6] = (float) f8;
                    f7 = f8 + interval;
                    i6++;
                    yMin = yMin2;
                    yMax = yMax;
                    labelCount = labelCount;
                }
                float f9 = yMax;
                int i7 = labelCount;
                n = n5;
            }
            if (interval < 1.0d) {
                this.mAxis.mDecimals = (int) Math.ceil(-Math.log10(interval));
            } else {
                this.mAxis.mDecimals = 0;
            }
            if (centeringEnabled != 0) {
                if (this.mAxis.mCenteredEntries.length < n) {
                    this.mAxis.mCenteredEntries = new float[n];
                }
                float offset = (this.mAxis.mEntries[1] - this.mAxis.mEntries[0]) / 2.0f;
                for (int i8 = 0; i8 < n; i8++) {
                    this.mAxis.mCenteredEntries[i8] = this.mAxis.mEntries[i8] + offset;
                }
            }
            this.mAxis.mAxisMinimum = this.mAxis.mEntries[0];
            this.mAxis.mAxisMaximum = this.mAxis.mEntries[n - 1];
            this.mAxis.mAxisRange = Math.abs(this.mAxis.mAxisMaximum - this.mAxis.mAxisMinimum);
            return;
        }
        this.mAxis.mEntries = new float[0];
        this.mAxis.mCenteredEntries = new float[0];
        this.mAxis.mEntryCount = 0;
    }

    public void renderAxisLabels(Canvas c) {
        if (this.mYAxis.isEnabled() && this.mYAxis.isDrawLabelsEnabled()) {
            this.mAxisLabelPaint.setTypeface(this.mYAxis.getTypeface());
            this.mAxisLabelPaint.setTextSize(this.mYAxis.getTextSize());
            this.mAxisLabelPaint.setColor(this.mYAxis.getTextColor());
            MPPointF center = this.mChart.getCenterOffsets();
            MPPointF pOut = MPPointF.getInstance(0.0f, 0.0f);
            float factor = this.mChart.getFactor();
            int labelCount = this.mYAxis.mEntryCount;
            int j = 0;
            while (j < labelCount && (j != labelCount - 1 || this.mYAxis.isDrawTopYLabelEntryEnabled())) {
                Utils.getPosition(center, (this.mYAxis.mEntries[j] - this.mYAxis.mAxisMinimum) * factor, this.mChart.getRotationAngle(), pOut);
                c.drawText(this.mYAxis.getFormattedLabel(j), pOut.x + 10.0f, pOut.y, this.mAxisLabelPaint);
                j++;
            }
            MPPointF.recycleInstance(center);
            MPPointF.recycleInstance(pOut);
        }
    }

    public void renderLimitLines(Canvas c) {
        List<LimitLine> limitLines = this.mYAxis.getLimitLines();
        if (limitLines != null) {
            float sliceangle = this.mChart.getSliceAngle();
            float factor = this.mChart.getFactor();
            MPPointF center = this.mChart.getCenterOffsets();
            MPPointF pOut = MPPointF.getInstance(0.0f, 0.0f);
            for (int i = 0; i < limitLines.size(); i++) {
                LimitLine l = limitLines.get(i);
                if (l.isEnabled()) {
                    this.mLimitLinePaint.setColor(l.getLineColor());
                    this.mLimitLinePaint.setPathEffect(l.getDashPathEffect());
                    this.mLimitLinePaint.setStrokeWidth(l.getLineWidth());
                    float r = (l.getLimit() - this.mChart.getYChartMin()) * factor;
                    Path limitPath = this.mRenderLimitLinesPathBuffer;
                    limitPath.reset();
                    for (int j = 0; j < ((IRadarDataSet) ((RadarData) this.mChart.getData()).getMaxEntryCountSet()).getEntryCount(); j++) {
                        Utils.getPosition(center, r, (((float) j) * sliceangle) + this.mChart.getRotationAngle(), pOut);
                        if (j == 0) {
                            limitPath.moveTo(pOut.x, pOut.y);
                        } else {
                            limitPath.lineTo(pOut.x, pOut.y);
                        }
                    }
                    limitPath.close();
                    c.drawPath(limitPath, this.mLimitLinePaint);
                }
            }
            MPPointF.recycleInstance(center);
            MPPointF.recycleInstance(pOut);
        }
    }
}
