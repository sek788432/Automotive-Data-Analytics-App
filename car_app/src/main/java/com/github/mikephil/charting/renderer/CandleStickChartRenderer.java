package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.List;

public class CandleStickChartRenderer extends LineScatterCandleRadarRenderer {
    private float[] mBodyBuffers = new float[4];
    protected CandleDataProvider mChart;
    private float[] mCloseBuffers = new float[4];
    private float[] mOpenBuffers = new float[4];
    private float[] mRangeBuffers = new float[4];
    private float[] mShadowBuffers = new float[8];

    public CandleStickChartRenderer(CandleDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;
    }

    public void initBuffers() {
    }

    public void drawData(Canvas c) {
        for (ICandleDataSet set : this.mChart.getCandleData().getDataSets()) {
            if (set.isVisible()) {
                drawDataSet(c, set);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawDataSet(Canvas c, ICandleDataSet dataSet) {
        int barColor;
        int i;
        int i2;
        int i3;
        int i4;
        ICandleDataSet iCandleDataSet = dataSet;
        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
        float phaseY = this.mAnimator.getPhaseY();
        float barSpace = dataSet.getBarSpace();
        boolean showCandleBar = dataSet.getShowCandleBar();
        this.mXBounds.set(this.mChart, iCandleDataSet);
        this.mRenderPaint.setStrokeWidth(dataSet.getShadowWidth());
        for (int j = this.mXBounds.min; j <= this.mXBounds.range + this.mXBounds.min; j++) {
            CandleEntry e2 = (CandleEntry) iCandleDataSet.getEntryForIndex(j);
            if (e2 == null) {
                Canvas canvas = c;
            } else {
                float xPos = e2.getX();
                float open = e2.getOpen();
                float close = e2.getClose();
                float high = e2.getHigh();
                float low = e2.getLow();
                if (showCandleBar) {
                    this.mShadowBuffers[0] = xPos;
                    this.mShadowBuffers[2] = xPos;
                    this.mShadowBuffers[4] = xPos;
                    this.mShadowBuffers[6] = xPos;
                    if (open > close) {
                        this.mShadowBuffers[1] = high * phaseY;
                        this.mShadowBuffers[3] = open * phaseY;
                        this.mShadowBuffers[5] = low * phaseY;
                        this.mShadowBuffers[7] = close * phaseY;
                    } else if (open < close) {
                        this.mShadowBuffers[1] = high * phaseY;
                        this.mShadowBuffers[3] = close * phaseY;
                        this.mShadowBuffers[5] = low * phaseY;
                        this.mShadowBuffers[7] = open * phaseY;
                    } else {
                        this.mShadowBuffers[1] = high * phaseY;
                        this.mShadowBuffers[3] = open * phaseY;
                        this.mShadowBuffers[5] = low * phaseY;
                        this.mShadowBuffers[7] = this.mShadowBuffers[3];
                    }
                    trans.pointValuesToPixel(this.mShadowBuffers);
                    if (!dataSet.getShadowColorSameAsCandle()) {
                        Paint paint = this.mRenderPaint;
                        if (dataSet.getShadowColor() == 1122867) {
                            i = iCandleDataSet.getColor(j);
                        } else {
                            i = dataSet.getShadowColor();
                        }
                        paint.setColor(i);
                    } else if (open > close) {
                        Paint paint2 = this.mRenderPaint;
                        if (dataSet.getDecreasingColor() == 1122867) {
                            i4 = iCandleDataSet.getColor(j);
                        } else {
                            i4 = dataSet.getDecreasingColor();
                        }
                        paint2.setColor(i4);
                    } else if (open < close) {
                        Paint paint3 = this.mRenderPaint;
                        if (dataSet.getIncreasingColor() == 1122867) {
                            i3 = iCandleDataSet.getColor(j);
                        } else {
                            i3 = dataSet.getIncreasingColor();
                        }
                        paint3.setColor(i3);
                    } else {
                        Paint paint4 = this.mRenderPaint;
                        if (dataSet.getNeutralColor() == 1122867) {
                            i2 = iCandleDataSet.getColor(j);
                        } else {
                            i2 = dataSet.getNeutralColor();
                        }
                        paint4.setColor(i2);
                    }
                    this.mRenderPaint.setStyle(Paint.Style.STROKE);
                    c.drawLines(this.mShadowBuffers, this.mRenderPaint);
                    this.mBodyBuffers[0] = (xPos - 0.5f) + barSpace;
                    this.mBodyBuffers[1] = close * phaseY;
                    this.mBodyBuffers[2] = (0.5f + xPos) - barSpace;
                    this.mBodyBuffers[3] = open * phaseY;
                    trans.pointValuesToPixel(this.mBodyBuffers);
                    if (open > close) {
                        if (dataSet.getDecreasingColor() == 1122867) {
                            this.mRenderPaint.setColor(iCandleDataSet.getColor(j));
                        } else {
                            this.mRenderPaint.setColor(dataSet.getDecreasingColor());
                        }
                        this.mRenderPaint.setStyle(dataSet.getDecreasingPaintStyle());
                        c.drawRect(this.mBodyBuffers[0], this.mBodyBuffers[3], this.mBodyBuffers[2], this.mBodyBuffers[1], this.mRenderPaint);
                    } else if (open < close) {
                        if (dataSet.getIncreasingColor() == 1122867) {
                            this.mRenderPaint.setColor(iCandleDataSet.getColor(j));
                        } else {
                            this.mRenderPaint.setColor(dataSet.getIncreasingColor());
                        }
                        this.mRenderPaint.setStyle(dataSet.getIncreasingPaintStyle());
                        c.drawRect(this.mBodyBuffers[0], this.mBodyBuffers[1], this.mBodyBuffers[2], this.mBodyBuffers[3], this.mRenderPaint);
                    } else {
                        if (dataSet.getNeutralColor() == 1122867) {
                            this.mRenderPaint.setColor(iCandleDataSet.getColor(j));
                        } else {
                            this.mRenderPaint.setColor(dataSet.getNeutralColor());
                        }
                        c.drawLine(this.mBodyBuffers[0], this.mBodyBuffers[1], this.mBodyBuffers[2], this.mBodyBuffers[3], this.mRenderPaint);
                    }
                } else {
                    Canvas canvas2 = c;
                    this.mRangeBuffers[0] = xPos;
                    this.mRangeBuffers[1] = high * phaseY;
                    this.mRangeBuffers[2] = xPos;
                    this.mRangeBuffers[3] = low * phaseY;
                    this.mOpenBuffers[0] = (xPos - 0.5f) + barSpace;
                    this.mOpenBuffers[1] = open * phaseY;
                    this.mOpenBuffers[2] = xPos;
                    this.mOpenBuffers[3] = open * phaseY;
                    this.mCloseBuffers[0] = (0.5f + xPos) - barSpace;
                    this.mCloseBuffers[1] = close * phaseY;
                    this.mCloseBuffers[2] = xPos;
                    this.mCloseBuffers[3] = close * phaseY;
                    trans.pointValuesToPixel(this.mRangeBuffers);
                    trans.pointValuesToPixel(this.mOpenBuffers);
                    trans.pointValuesToPixel(this.mCloseBuffers);
                    if (open > close) {
                        if (dataSet.getDecreasingColor() == 1122867) {
                            barColor = iCandleDataSet.getColor(j);
                        } else {
                            barColor = dataSet.getDecreasingColor();
                        }
                    } else if (open < close) {
                        if (dataSet.getIncreasingColor() == 1122867) {
                            barColor = iCandleDataSet.getColor(j);
                        } else {
                            barColor = dataSet.getIncreasingColor();
                        }
                    } else if (dataSet.getNeutralColor() == 1122867) {
                        barColor = iCandleDataSet.getColor(j);
                    } else {
                        barColor = dataSet.getNeutralColor();
                    }
                    this.mRenderPaint.setColor(barColor);
                    Canvas canvas3 = c;
                    canvas3.drawLine(this.mRangeBuffers[0], this.mRangeBuffers[1], this.mRangeBuffers[2], this.mRangeBuffers[3], this.mRenderPaint);
                    canvas3.drawLine(this.mOpenBuffers[0], this.mOpenBuffers[1], this.mOpenBuffers[2], this.mOpenBuffers[3], this.mRenderPaint);
                    canvas3.drawLine(this.mCloseBuffers[0], this.mCloseBuffers[1], this.mCloseBuffers[2], this.mCloseBuffers[3], this.mRenderPaint);
                }
            }
        }
        Canvas canvas4 = c;
    }

    public void drawValues(Canvas c) {
        int j;
        if (isDrawingValuesAllowed(this.mChart)) {
            List<ICandleDataSet> dataSets = this.mChart.getCandleData().getDataSets();
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 < dataSets.size()) {
                    ICandleDataSet dataSet = dataSets.get(i2);
                    if (shouldDrawValues(dataSet)) {
                        applyValueTextStyle(dataSet);
                        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
                        this.mXBounds.set(this.mChart, dataSet);
                        float[] positions = trans.generateTransformedValuesCandle(dataSet, this.mAnimator.getPhaseX(), this.mAnimator.getPhaseY(), this.mXBounds.min, this.mXBounds.max);
                        float yOffset = Utils.convertDpToPixel(5.0f);
                        int j2 = 0;
                        while (true) {
                            int j3 = j2;
                            if (j3 >= positions.length) {
                                break;
                            }
                            float x = positions[j3];
                            float y = positions[j3 + 1];
                            if (!this.mViewPortHandler.isInBoundsRight(x)) {
                                break;
                            }
                            if (!this.mViewPortHandler.isInBoundsLeft(x)) {
                                j = j3;
                            } else if (!this.mViewPortHandler.isInBoundsY(y)) {
                                j = j3;
                            } else {
                                CandleEntry entry = (CandleEntry) dataSet.getEntryForIndex((j3 / 2) + this.mXBounds.min);
                                float f = y - yOffset;
                                float f2 = y;
                                float y2 = x;
                                float f3 = x;
                                float x2 = f;
                                j = j3;
                                drawValue(c, dataSet.getValueFormatter(), entry.getHigh(), entry, i2, y2, x2, dataSet.getValueTextColor(j3 / 2));
                            }
                            j2 = j + 2;
                        }
                    }
                    i = i2 + 1;
                } else {
                    return;
                }
            }
        }
    }

    public void drawExtras(Canvas c) {
    }

    public void drawHighlighted(Canvas c, Highlight[] indices) {
        CandleData candleData = this.mChart.getCandleData();
        for (Highlight high : indices) {
            ICandleDataSet set = (ICandleDataSet) candleData.getDataSetByIndex(high.getDataSetIndex());
            if (set != null && set.isHighlightEnabled()) {
                CandleEntry e2 = (CandleEntry) set.getEntryForXValue(high.getX(), high.getY());
                if (isInBoundsX(e2, set)) {
                    MPPointD pix = this.mChart.getTransformer(set.getAxisDependency()).getPixelForValues(e2.getX(), ((e2.getLow() * this.mAnimator.getPhaseY()) + (e2.getHigh() * this.mAnimator.getPhaseY())) / 2.0f);
                    high.setDraw((float) pix.x, (float) pix.y);
                    drawHighlightLines(c, (float) pix.x, (float) pix.y, set);
                }
            }
        }
    }
}
