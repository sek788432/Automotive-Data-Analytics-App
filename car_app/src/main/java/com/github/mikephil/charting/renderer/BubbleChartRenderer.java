package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BubbleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.List;

public class BubbleChartRenderer extends BarLineScatterCandleBubbleRenderer {
    private float[] _hsvBuffer = new float[3];
    protected BubbleDataProvider mChart;
    private float[] pointBuffer = new float[2];
    private float[] sizeBuffer = new float[4];

    public BubbleChartRenderer(BubbleDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;
        this.mRenderPaint.setStyle(Paint.Style.FILL);
        this.mHighlightPaint.setStyle(Paint.Style.STROKE);
        this.mHighlightPaint.setStrokeWidth(Utils.convertDpToPixel(1.5f));
    }

    public void initBuffers() {
    }

    public void drawData(Canvas c) {
        for (IBubbleDataSet set : this.mChart.getBubbleData().getDataSets()) {
            if (set.isVisible()) {
                drawDataSet(c, set);
            }
        }
    }

    /* access modifiers changed from: protected */
    public float getShapeSize(float entrySize, float maxSize, float reference, boolean normalizeSize) {
        return reference * (normalizeSize ? maxSize == 0.0f ? 1.0f : (float) Math.sqrt((double) (entrySize / maxSize)) : entrySize);
    }

    /* access modifiers changed from: protected */
    public void drawDataSet(Canvas c, IBubbleDataSet dataSet) {
        IBubbleDataSet iBubbleDataSet = dataSet;
        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
        float phaseY = this.mAnimator.getPhaseY();
        this.mXBounds.set(this.mChart, iBubbleDataSet);
        char c2 = 0;
        this.sizeBuffer[0] = 0.0f;
        this.sizeBuffer[2] = 1.0f;
        trans.pointValuesToPixel(this.sizeBuffer);
        boolean normalizeSize = dataSet.isNormalizeSizeEnabled();
        float referenceSize = Math.min(Math.abs(this.mViewPortHandler.contentBottom() - this.mViewPortHandler.contentTop()), Math.abs(this.sizeBuffer[2] - this.sizeBuffer[0]));
        int j = this.mXBounds.min;
        while (j <= this.mXBounds.range + this.mXBounds.min) {
            BubbleEntry entry = (BubbleEntry) iBubbleDataSet.getEntryForIndex(j);
            this.pointBuffer[c2] = entry.getX();
            this.pointBuffer[1] = entry.getY() * phaseY;
            trans.pointValuesToPixel(this.pointBuffer);
            float shapeHalf = getShapeSize(entry.getSize(), dataSet.getMaxSize(), referenceSize, normalizeSize) / 2.0f;
            if (!this.mViewPortHandler.isInBoundsTop(this.pointBuffer[1] + shapeHalf) || !this.mViewPortHandler.isInBoundsBottom(this.pointBuffer[1] - shapeHalf) || !this.mViewPortHandler.isInBoundsLeft(this.pointBuffer[c2] + shapeHalf)) {
                Canvas canvas = c;
            } else if (!this.mViewPortHandler.isInBoundsRight(this.pointBuffer[c2] - shapeHalf)) {
                break;
            } else {
                this.mRenderPaint.setColor(iBubbleDataSet.getColor((int) entry.getX()));
                c.drawCircle(this.pointBuffer[c2], this.pointBuffer[1], shapeHalf, this.mRenderPaint);
            }
            j++;
            c2 = 0;
        }
        Canvas canvas2 = c;
    }

    public void drawValues(Canvas c) {
        float[] positions;
        int j;
        float phaseY;
        BubbleData bubbleData = this.mChart.getBubbleData();
        if (bubbleData != null && isDrawingValuesAllowed(this.mChart)) {
            List<IBubbleDataSet> dataSets = bubbleData.getDataSets();
            float lineHeight = (float) Utils.calcTextHeight(this.mValuePaint, "1");
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 < dataSets.size()) {
                    IBubbleDataSet dataSet = dataSets.get(i2);
                    if (shouldDrawValues(dataSet)) {
                        applyValueTextStyle(dataSet);
                        float phaseX = Math.max(0.0f, Math.min(1.0f, this.mAnimator.getPhaseX()));
                        float phaseY2 = this.mAnimator.getPhaseY();
                        this.mXBounds.set(this.mChart, dataSet);
                        float[] positions2 = this.mChart.getTransformer(dataSet.getAxisDependency()).generateTransformedValuesBubble(dataSet, phaseY2, this.mXBounds.min, this.mXBounds.max);
                        float alpha = phaseX == 1.0f ? phaseY2 : phaseX;
                        int j2 = 0;
                        while (true) {
                            int j3 = j2;
                            if (j3 >= positions2.length) {
                                break;
                            }
                            int valueTextColor = dataSet.getValueTextColor((j3 / 2) + this.mXBounds.min);
                            int valueTextColor2 = Color.argb(Math.round(255.0f * alpha), Color.red(valueTextColor), Color.green(valueTextColor), Color.blue(valueTextColor));
                            float x = positions2[j3];
                            float y = positions2[j3 + 1];
                            if (!this.mViewPortHandler.isInBoundsRight(x)) {
                                break;
                            }
                            if (!this.mViewPortHandler.isInBoundsLeft(x)) {
                                j = j3;
                                positions = positions2;
                                phaseY = phaseY2;
                            } else if (!this.mViewPortHandler.isInBoundsY(y)) {
                                j = j3;
                                positions = positions2;
                                phaseY = phaseY2;
                            } else {
                                BubbleEntry entry = (BubbleEntry) dataSet.getEntryForIndex((j3 / 2) + this.mXBounds.min);
                                float f = y;
                                j = j3;
                                positions = positions2;
                                phaseY = phaseY2;
                                drawValue(c, dataSet.getValueFormatter(), entry.getSize(), entry, i2, x, y + (0.5f * lineHeight), valueTextColor2);
                            }
                            j2 = j + 2;
                            phaseY2 = phaseY;
                            positions2 = positions;
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
        int i;
        float phaseY;
        BubbleData bubbleData;
        BubbleChartRenderer bubbleChartRenderer = this;
        Highlight[] highlightArr = indices;
        BubbleData bubbleData2 = bubbleChartRenderer.mChart.getBubbleData();
        float phaseY2 = bubbleChartRenderer.mAnimator.getPhaseY();
        int length = highlightArr.length;
        char c2 = 0;
        int i2 = 0;
        while (i2 < length) {
            Highlight high = highlightArr[i2];
            IBubbleDataSet set = (IBubbleDataSet) bubbleData2.getDataSetByIndex(high.getDataSetIndex());
            if (set != null) {
                if (set.isHighlightEnabled()) {
                    BubbleEntry entry = (BubbleEntry) set.getEntryForXValue(high.getX(), high.getY());
                    if (entry.getY() == high.getY() && bubbleChartRenderer.isInBoundsX(entry, set)) {
                        Transformer trans = bubbleChartRenderer.mChart.getTransformer(set.getAxisDependency());
                        bubbleChartRenderer.sizeBuffer[c2] = 0.0f;
                        bubbleChartRenderer.sizeBuffer[2] = 1.0f;
                        trans.pointValuesToPixel(bubbleChartRenderer.sizeBuffer);
                        boolean normalizeSize = set.isNormalizeSizeEnabled();
                        float referenceSize = Math.min(Math.abs(bubbleChartRenderer.mViewPortHandler.contentBottom() - bubbleChartRenderer.mViewPortHandler.contentTop()), Math.abs(bubbleChartRenderer.sizeBuffer[2] - bubbleChartRenderer.sizeBuffer[c2]));
                        bubbleChartRenderer.pointBuffer[c2] = entry.getX();
                        bubbleChartRenderer.pointBuffer[1] = entry.getY() * phaseY2;
                        trans.pointValuesToPixel(bubbleChartRenderer.pointBuffer);
                        high.setDraw(bubbleChartRenderer.pointBuffer[c2], bubbleChartRenderer.pointBuffer[1]);
                        float shapeHalf = bubbleChartRenderer.getShapeSize(entry.getSize(), set.getMaxSize(), referenceSize, normalizeSize) / 2.0f;
                        if (bubbleChartRenderer.mViewPortHandler.isInBoundsTop(bubbleChartRenderer.pointBuffer[1] + shapeHalf)) {
                            if (bubbleChartRenderer.mViewPortHandler.isInBoundsBottom(bubbleChartRenderer.pointBuffer[1] - shapeHalf) && bubbleChartRenderer.mViewPortHandler.isInBoundsLeft(bubbleChartRenderer.pointBuffer[0] + shapeHalf)) {
                                if (!bubbleChartRenderer.mViewPortHandler.isInBoundsRight(bubbleChartRenderer.pointBuffer[0] - shapeHalf)) {
                                    Canvas canvas = c;
                                    BubbleData bubbleData3 = bubbleData2;
                                    float f = phaseY2;
                                    return;
                                }
                                int originalColor = set.getColor((int) entry.getX());
                                bubbleData = bubbleData2;
                                phaseY = phaseY2;
                                i = length;
                                Color.RGBToHSV(Color.red(originalColor), Color.green(originalColor), Color.blue(originalColor), bubbleChartRenderer._hsvBuffer);
                                float[] fArr = bubbleChartRenderer._hsvBuffer;
                                fArr[2] = fArr[2] * 0.5f;
                                bubbleChartRenderer.mHighlightPaint.setColor(Color.HSVToColor(Color.alpha(originalColor), bubbleChartRenderer._hsvBuffer));
                                bubbleChartRenderer.mHighlightPaint.setStrokeWidth(set.getHighlightCircleWidth());
                                c.drawCircle(bubbleChartRenderer.pointBuffer[0], bubbleChartRenderer.pointBuffer[1], shapeHalf, bubbleChartRenderer.mHighlightPaint);
                                i2++;
                                bubbleData2 = bubbleData;
                                phaseY2 = phaseY;
                                length = i;
                                bubbleChartRenderer = this;
                                highlightArr = indices;
                                c2 = 0;
                            }
                        }
                    }
                }
                Canvas canvas2 = c;
                bubbleData = bubbleData2;
                phaseY = phaseY2;
                i = length;
                i2++;
                bubbleData2 = bubbleData;
                phaseY2 = phaseY;
                length = i;
                bubbleChartRenderer = this;
                highlightArr = indices;
                c2 = 0;
            }
            Canvas canvas3 = c;
            bubbleData = bubbleData2;
            phaseY = phaseY2;
            i = length;
            i2++;
            bubbleData2 = bubbleData;
            phaseY2 = phaseY;
            length = i;
            bubbleChartRenderer = this;
            highlightArr = indices;
            c2 = 0;
        }
        Canvas canvas4 = c;
        BubbleData bubbleData4 = bubbleData2;
        float f2 = phaseY2;
    }
}
