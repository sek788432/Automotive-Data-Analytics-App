package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.buffer.HorizontalBarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.dataprovider.ChartInterface;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.List;

public class HorizontalBarChartRenderer extends BarChartRenderer {
    private RectF mBarShadowRectBuffer = new RectF();

    public HorizontalBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
        this.mValuePaint.setTextAlign(Paint.Align.LEFT);
    }

    public void initBuffers() {
        BarData barData = this.mChart.getBarData();
        this.mBarBuffers = new HorizontalBarBuffer[barData.getDataSetCount()];
        for (int i = 0; i < this.mBarBuffers.length; i++) {
            IBarDataSet set = (IBarDataSet) barData.getDataSetByIndex(i);
            this.mBarBuffers[i] = new HorizontalBarBuffer(set.getEntryCount() * 4 * (set.isStacked() ? set.getStackSize() : 1), barData.getDataSetCount(), set.isStacked());
        }
    }

    /* access modifiers changed from: protected */
    public void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        BarData barData;
        IBarDataSet iBarDataSet = dataSet;
        int i = index;
        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
        this.mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        this.mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));
        boolean drawBorder = dataSet.getBarBorderWidth() > 0.0f;
        float phaseX = this.mAnimator.getPhaseX();
        float phaseY = this.mAnimator.getPhaseY();
        if (this.mChart.isDrawBarShadowEnabled()) {
            this.mShadowPaint.setColor(dataSet.getBarShadowColor());
            BarData barData2 = this.mChart.getBarData();
            float barWidthHalf = barData2.getBarWidth() / 2.0f;
            int i2 = 0;
            int count = Math.min((int) Math.ceil((double) (((float) dataSet.getEntryCount()) * phaseX)), dataSet.getEntryCount());
            while (i2 < count) {
                float x = ((BarEntry) iBarDataSet.getEntryForIndex(i2)).getX();
                this.mBarShadowRectBuffer.top = x - barWidthHalf;
                this.mBarShadowRectBuffer.bottom = x + barWidthHalf;
                trans.rectValueToPixel(this.mBarShadowRectBuffer);
                if (!this.mViewPortHandler.isInBoundsTop(this.mBarShadowRectBuffer.bottom)) {
                    barData = barData2;
                    Canvas canvas = c;
                } else if (!this.mViewPortHandler.isInBoundsBottom(this.mBarShadowRectBuffer.top)) {
                    break;
                } else {
                    this.mBarShadowRectBuffer.left = this.mViewPortHandler.contentLeft();
                    this.mBarShadowRectBuffer.right = this.mViewPortHandler.contentRight();
                    barData = barData2;
                    c.drawRect(this.mBarShadowRectBuffer, this.mShadowPaint);
                }
                i2++;
                barData2 = barData;
            }
        }
        Canvas canvas2 = c;
        BarBuffer buffer = this.mBarBuffers[i];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(i);
        buffer.setInverted(this.mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(this.mChart.getBarData().getBarWidth());
        buffer.feed(iBarDataSet);
        trans.pointValuesToPixel(buffer.buffer);
        boolean z = true;
        if (dataSet.getColors().size() != 1) {
            z = false;
        }
        boolean isSingleColor = z;
        if (isSingleColor) {
            this.mRenderPaint.setColor(dataSet.getColor());
        }
        int j = 0;
        while (true) {
            int j2 = j;
            if (j2 < buffer.size() && this.mViewPortHandler.isInBoundsTop(buffer.buffer[j2 + 3])) {
                if (this.mViewPortHandler.isInBoundsBottom(buffer.buffer[j2 + 1])) {
                    if (!isSingleColor) {
                        this.mRenderPaint.setColor(iBarDataSet.getColor(j2 / 4));
                    }
                    c.drawRect(buffer.buffer[j2], buffer.buffer[j2 + 1], buffer.buffer[j2 + 2], buffer.buffer[j2 + 3], this.mRenderPaint);
                    if (drawBorder) {
                        c.drawRect(buffer.buffer[j2], buffer.buffer[j2 + 1], buffer.buffer[j2 + 2], buffer.buffer[j2 + 3], this.mBarBorderPaint);
                    }
                }
                j = j2 + 4;
            } else {
                return;
            }
        }
    }

    public void drawValues(Canvas c) {
        List<IBarDataSet> dataSets;
        float posOffset;
        float negOffset;
        IValueFormatter formatter;
        IBarDataSet dataSet;
        int index;
        float[] vals;
        String formattedValue;
        float posOffset2;
        BarEntry e2;
        float negOffset2;
        int k;
        BarEntry e3;
        float[] transformed;
        float y;
        String formattedValue2;
        float posOffset3;
        float negOffset3;
        float[] vals2;
        float negOffset4;
        float posOffset4;
        float posOffset5;
        float negOffset5;
        int j;
        List<IBarDataSet> dataSets2;
        BarBuffer buffer;
        IValueFormatter formatter2;
        String formattedValue3;
        float posOffset6;
        IValueFormatter formatter3;
        float negOffset6;
        float negOffset7;
        float posOffset7;
        if (isDrawingValuesAllowed(this.mChart)) {
            List<IBarDataSet> dataSets3 = this.mChart.getBarData().getDataSets();
            float valueOffsetPlus = Utils.convertDpToPixel(5.0f);
            boolean drawValueAboveBar = this.mChart.isDrawValueAboveBarEnabled();
            float negOffset8 = 0.0f;
            float posOffset8 = 0.0f;
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 < this.mChart.getBarData().getDataSetCount()) {
                    IBarDataSet dataSet2 = dataSets3.get(i2);
                    if (!shouldDrawValues(dataSet2)) {
                        dataSets = dataSets3;
                    } else {
                        boolean isInverted = this.mChart.isInverted(dataSet2.getAxisDependency());
                        applyValueTextStyle(dataSet2);
                        float halfTextHeight = ((float) Utils.calcTextHeight(this.mValuePaint, "10")) / 2.0f;
                        IValueFormatter formatter4 = dataSet2.getValueFormatter();
                        BarBuffer buffer2 = this.mBarBuffers[i2];
                        float phaseY = this.mAnimator.getPhaseY();
                        if (!dataSet2.isStacked()) {
                            int j2 = 0;
                            while (true) {
                                int j3 = j2;
                                if (((float) j3) >= ((float) buffer2.buffer.length) * this.mAnimator.getPhaseX()) {
                                    posOffset5 = posOffset8;
                                    negOffset5 = negOffset8;
                                    BarBuffer barBuffer = buffer2;
                                    dataSets = dataSets3;
                                    IValueFormatter iValueFormatter = formatter4;
                                    break;
                                }
                                float y2 = (buffer2.buffer[j3 + 1] + buffer2.buffer[j3 + 3]) / 2.0f;
                                if (!this.mViewPortHandler.isInBoundsTop(buffer2.buffer[j3 + 1])) {
                                    posOffset5 = posOffset8;
                                    negOffset5 = negOffset8;
                                    BarBuffer barBuffer2 = buffer2;
                                    dataSets = dataSets3;
                                    IValueFormatter iValueFormatter2 = formatter4;
                                    break;
                                }
                                if (this.mViewPortHandler.isInBoundsX(buffer2.buffer[j3]) && this.mViewPortHandler.isInBoundsBottom(buffer2.buffer[j3 + 1])) {
                                    BarEntry e4 = (BarEntry) dataSet2.getEntryForIndex(j3 / 4);
                                    float val = e4.getY();
                                    float f = posOffset8;
                                    String formattedValue4 = formatter4.getFormattedValue(val, e4, i2, this.mViewPortHandler);
                                    float f2 = negOffset8;
                                    float valueTextWidth = (float) Utils.calcTextWidth(this.mValuePaint, formattedValue4);
                                    if (drawValueAboveBar) {
                                        formattedValue3 = formattedValue4;
                                        posOffset6 = valueOffsetPlus;
                                    } else {
                                        formattedValue3 = formattedValue4;
                                        posOffset6 = -(valueTextWidth + valueOffsetPlus);
                                    }
                                    if (drawValueAboveBar) {
                                        formatter3 = formatter4;
                                        negOffset6 = -(valueTextWidth + valueOffsetPlus);
                                    } else {
                                        formatter3 = formatter4;
                                        negOffset6 = valueOffsetPlus;
                                    }
                                    if (isInverted) {
                                        dataSets2 = dataSets3;
                                        posOffset7 = (-posOffset6) - valueTextWidth;
                                        negOffset7 = (-negOffset6) - valueTextWidth;
                                    } else {
                                        dataSets2 = dataSets3;
                                        posOffset7 = posOffset6;
                                        negOffset7 = negOffset6;
                                    }
                                    float f3 = val;
                                    float f4 = valueTextWidth;
                                    j = j3;
                                    float posOffset9 = posOffset7;
                                    BarEntry barEntry = e4;
                                    formatter2 = formatter3;
                                    buffer = buffer2;
                                    drawValue(c, formattedValue3, (val >= 0.0f ? posOffset7 : negOffset7) + buffer2.buffer[j3 + 2], y2 + halfTextHeight, dataSet2.getValueTextColor(j3 / 2));
                                    negOffset8 = negOffset7;
                                    posOffset8 = posOffset9;
                                } else {
                                    j = j3;
                                    buffer = buffer2;
                                    dataSets2 = dataSets3;
                                    formatter2 = formatter4;
                                }
                                j2 = j + 4;
                                formatter4 = formatter2;
                                buffer2 = buffer;
                                dataSets3 = dataSets2;
                            }
                            posOffset8 = posOffset5;
                            negOffset8 = negOffset5;
                        } else {
                            BarBuffer buffer3 = buffer2;
                            dataSets = dataSets3;
                            IValueFormatter formatter5 = formatter4;
                            Transformer trans = this.mChart.getTransformer(dataSet2.getAxisDependency());
                            int bufferIndex = 0;
                            int bufferIndex2 = 0;
                            while (true) {
                                int index2 = bufferIndex2;
                                if (((float) index2) >= ((float) dataSet2.getEntryCount()) * this.mAnimator.getPhaseX()) {
                                    posOffset = posOffset8;
                                    negOffset = negOffset8;
                                    break;
                                }
                                BarEntry e5 = (BarEntry) dataSet2.getEntryForIndex(index2);
                                int color = dataSet2.getValueTextColor(index2);
                                float[] vals3 = e5.getYVals();
                                if (vals3 != null) {
                                    float posOffset10 = posOffset8;
                                    float negOffset9 = negOffset8;
                                    BarEntry e6 = e5;
                                    index = index2;
                                    dataSet = dataSet2;
                                    vals = vals3;
                                    float[] transformed2 = new float[(vals.length * 2)];
                                    int k2 = 0;
                                    float posY = 0.0f;
                                    float negY = -e6.getNegativeSum();
                                    int idx = 0;
                                    while (k2 < transformed2.length) {
                                        float value = vals[idx];
                                        if (value >= 0.0f) {
                                            posY += value;
                                            y = posY;
                                        } else {
                                            y = negY;
                                            negY -= value;
                                        }
                                        transformed2[k2] = y * phaseY;
                                        k2 += 2;
                                        idx++;
                                    }
                                    trans.pointValuesToPixel(transformed2);
                                    int k3 = 0;
                                    while (true) {
                                        int k4 = k3;
                                        if (k4 >= transformed2.length) {
                                            formatter = formatter5;
                                            BarEntry barEntry2 = e6;
                                            posOffset8 = posOffset10;
                                            negOffset8 = negOffset9;
                                            break;
                                        }
                                        float val2 = vals[k4 / 2];
                                        BarEntry e7 = e6;
                                        String formattedValue5 = formatter5.getFormattedValue(val2, e7, i2, this.mViewPortHandler);
                                        float valueTextWidth2 = (float) Utils.calcTextWidth(this.mValuePaint, formattedValue5);
                                        if (drawValueAboveBar) {
                                            formattedValue = formattedValue5;
                                            posOffset2 = valueOffsetPlus;
                                        } else {
                                            formattedValue = formattedValue5;
                                            posOffset2 = -(valueTextWidth2 + valueOffsetPlus);
                                        }
                                        if (drawValueAboveBar) {
                                            e2 = e7;
                                            negOffset2 = -(valueTextWidth2 + valueOffsetPlus);
                                        } else {
                                            e2 = e7;
                                            negOffset2 = valueOffsetPlus;
                                        }
                                        if (isInverted) {
                                            formatter = formatter5;
                                            negOffset9 = (-negOffset2) - valueTextWidth2;
                                            posOffset10 = (-posOffset2) - valueTextWidth2;
                                        } else {
                                            formatter = formatter5;
                                            posOffset10 = posOffset2;
                                            negOffset9 = negOffset2;
                                        }
                                        float x = transformed2[k4] + (val2 >= 0.0f ? posOffset10 : negOffset9);
                                        float y3 = (buffer3.buffer[bufferIndex + 1] + buffer3.buffer[bufferIndex + 3]) / 2.0f;
                                        if (!this.mViewPortHandler.isInBoundsTop(y3)) {
                                            posOffset8 = posOffset10;
                                            negOffset8 = negOffset9;
                                            BarEntry barEntry3 = e2;
                                            break;
                                        }
                                        if (this.mViewPortHandler.isInBoundsX(x) && this.mViewPortHandler.isInBoundsBottom(y3)) {
                                            float f5 = valueTextWidth2;
                                            float f6 = y3;
                                            e3 = e2;
                                            float f7 = val2;
                                            k = k4;
                                            transformed = transformed2;
                                            drawValue(c, formattedValue, x, y3 + halfTextHeight, color);
                                        } else {
                                            k = k4;
                                            transformed = transformed2;
                                            e3 = e2;
                                        }
                                        k3 = k + 2;
                                        transformed2 = transformed;
                                        e6 = e3;
                                        formatter5 = formatter;
                                    }
                                } else {
                                    posOffset = posOffset8;
                                    if (!this.mViewPortHandler.isInBoundsTop(buffer3.buffer[bufferIndex + 1])) {
                                        negOffset = negOffset8;
                                        break;
                                    } else if (this.mViewPortHandler.isInBoundsX(buffer3.buffer[bufferIndex]) && this.mViewPortHandler.isInBoundsBottom(buffer3.buffer[bufferIndex + 1])) {
                                        float val3 = e5.getY();
                                        String formattedValue6 = formatter5.getFormattedValue(val3, e5, i2, this.mViewPortHandler);
                                        float val4 = val3;
                                        float valueTextWidth3 = (float) Utils.calcTextWidth(this.mValuePaint, formattedValue6);
                                        if (drawValueAboveBar) {
                                            formattedValue2 = formattedValue6;
                                            posOffset3 = valueOffsetPlus;
                                        } else {
                                            formattedValue2 = formattedValue6;
                                            posOffset3 = -(valueTextWidth3 + valueOffsetPlus);
                                        }
                                        if (drawValueAboveBar) {
                                            float f8 = negOffset8;
                                            negOffset3 = -(valueTextWidth3 + valueOffsetPlus);
                                        } else {
                                            negOffset3 = valueOffsetPlus;
                                        }
                                        if (isInverted) {
                                            vals2 = vals3;
                                            posOffset4 = (-posOffset3) - valueTextWidth3;
                                            negOffset4 = (-negOffset3) - valueTextWidth3;
                                        } else {
                                            vals2 = vals3;
                                            posOffset4 = posOffset3;
                                            negOffset4 = negOffset3;
                                        }
                                        float f9 = valueTextWidth3;
                                        float f10 = val4;
                                        dataSet = dataSet2;
                                        vals = vals2;
                                        index = index2;
                                        drawValue(c, formattedValue2, buffer3.buffer[bufferIndex + 2] + (e5.getY() >= 0.0f ? posOffset4 : negOffset4), buffer3.buffer[bufferIndex + 1] + halfTextHeight, color);
                                        formatter = formatter5;
                                        posOffset8 = posOffset4;
                                        negOffset8 = negOffset4;
                                        BarEntry barEntry4 = e5;
                                    } else {
                                        bufferIndex2 = index2;
                                        posOffset8 = posOffset;
                                    }
                                }
                                bufferIndex = vals == null ? bufferIndex + 4 : bufferIndex + (vals.length * 4);
                                bufferIndex2 = index + 1;
                                dataSet2 = dataSet;
                                formatter5 = formatter;
                            }
                            posOffset8 = posOffset;
                            negOffset8 = negOffset;
                        }
                    }
                    i = i2 + 1;
                    dataSets3 = dataSets;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawValue(Canvas c, String valueText, float x, float y, int color) {
        this.mValuePaint.setColor(color);
        c.drawText(valueText, x, y, this.mValuePaint);
    }

    /* access modifiers changed from: protected */
    public void prepareBarHighlight(float x, float y1, float y2, float barWidthHalf, Transformer trans) {
        RectF rectF = this.mBarRect;
        rectF.set(y1, x - barWidthHalf, y2, x + barWidthHalf);
        trans.rectToPixelPhaseHorizontal(this.mBarRect, this.mAnimator.getPhaseY());
    }

    /* access modifiers changed from: protected */
    public void setHighlightDrawPos(Highlight high, RectF bar) {
        high.setDraw(bar.centerY(), bar.right);
    }

    /* access modifiers changed from: protected */
    public boolean isDrawingValuesAllowed(ChartInterface chart) {
        return ((float) chart.getData().getEntryCount()) < ((float) chart.getMaxVisibleCount()) * this.mViewPortHandler.getScaleY();
    }
}
