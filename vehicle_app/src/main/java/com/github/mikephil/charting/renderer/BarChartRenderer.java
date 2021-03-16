package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.Range;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class BarChartRenderer extends BarLineScatterCandleBubbleRenderer {
    protected Paint mBarBorderPaint;
    protected BarBuffer[] mBarBuffers;
    protected RectF mBarRect = new RectF();
    private RectF mBarShadowRectBuffer = new RectF();
    protected BarDataProvider mChart;
    protected Paint mShadowPaint;

    public BarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;
        this.mHighlightPaint = new Paint(1);
        this.mHighlightPaint.setStyle(Paint.Style.FILL);
        this.mHighlightPaint.setColor(Color.rgb(0, 0, 0));
        this.mHighlightPaint.setAlpha(120);
        this.mShadowPaint = new Paint(1);
        this.mShadowPaint.setStyle(Paint.Style.FILL);
        this.mBarBorderPaint = new Paint(1);
        this.mBarBorderPaint.setStyle(Paint.Style.STROKE);
    }

    public void initBuffers() {
        BarData barData = this.mChart.getBarData();
        this.mBarBuffers = new BarBuffer[barData.getDataSetCount()];
        for (int i = 0; i < this.mBarBuffers.length; i++) {
            IBarDataSet set = (IBarDataSet) barData.getDataSetByIndex(i);
            this.mBarBuffers[i] = new BarBuffer(set.getEntryCount() * 4 * (set.isStacked() ? set.getStackSize() : 1), barData.getDataSetCount(), set.isStacked());
        }
    }

    public void drawData(Canvas c) {
        BarData barData = this.mChart.getBarData();
        for (int i = 0; i < barData.getDataSetCount(); i++) {
            IBarDataSet set = (IBarDataSet) barData.getDataSetByIndex(i);
            if (set.isVisible()) {
                drawDataSet(c, set, i);
            }
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
                this.mBarShadowRectBuffer.left = x - barWidthHalf;
                this.mBarShadowRectBuffer.right = x + barWidthHalf;
                trans.rectValueToPixel(this.mBarShadowRectBuffer);
                if (!this.mViewPortHandler.isInBoundsLeft(this.mBarShadowRectBuffer.right)) {
                    barData = barData2;
                    Canvas canvas = c;
                } else if (!this.mViewPortHandler.isInBoundsRight(this.mBarShadowRectBuffer.left)) {
                    break;
                } else {
                    this.mBarShadowRectBuffer.top = this.mViewPortHandler.contentTop();
                    this.mBarShadowRectBuffer.bottom = this.mViewPortHandler.contentBottom();
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
            if (j2 < buffer.size()) {
                if (this.mViewPortHandler.isInBoundsLeft(buffer.buffer[j2 + 2])) {
                    if (this.mViewPortHandler.isInBoundsRight(buffer.buffer[j2])) {
                        if (!isSingleColor) {
                            this.mRenderPaint.setColor(iBarDataSet.getColor(j2 / 4));
                        }
                        c.drawRect(buffer.buffer[j2], buffer.buffer[j2 + 1], buffer.buffer[j2 + 2], buffer.buffer[j2 + 3], this.mRenderPaint);
                        if (drawBorder) {
                            c.drawRect(buffer.buffer[j2], buffer.buffer[j2 + 1], buffer.buffer[j2 + 2], buffer.buffer[j2 + 3], this.mBarBorderPaint);
                        }
                    } else {
                        return;
                    }
                }
                j = j2 + 4;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void prepareBarHighlight(float x, float y1, float y2, float barWidthHalf, Transformer trans) {
        RectF rectF = this.mBarRect;
        rectF.set(x - barWidthHalf, y1, x + barWidthHalf, y2);
        trans.rectToPixelPhase(this.mBarRect, this.mAnimator.getPhaseY());
    }

    /* JADX WARNING: Removed duplicated region for block: B:87:0x028b  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x028e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void drawValues(android.graphics.Canvas r36) {
        /*
            r35 = this;
            r9 = r35
            com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider r0 = r9.mChart
            boolean r0 = r9.isDrawingValuesAllowed(r0)
            if (r0 == 0) goto L_0x02ab
            com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider r0 = r9.mChart
            com.github.mikephil.charting.data.BarData r0 = r0.getBarData()
            java.util.List r10 = r0.getDataSets()
            r0 = 1083179008(0x40900000, float:4.5)
            float r11 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r0)
            r0 = 0
            r1 = 0
            com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider r2 = r9.mChart
            boolean r12 = r2.isDrawValueAboveBarEnabled()
            r2 = r1
            r1 = r0
            r0 = 0
        L_0x0025:
            r14 = r0
            com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider r0 = r9.mChart
            com.github.mikephil.charting.data.BarData r0 = r0.getBarData()
            int r0 = r0.getDataSetCount()
            if (r14 >= r0) goto L_0x02ab
            java.lang.Object r0 = r10.get(r14)
            r15 = r0
            com.github.mikephil.charting.interfaces.datasets.IBarDataSet r15 = (com.github.mikephil.charting.interfaces.datasets.IBarDataSet) r15
            boolean r0 = r9.shouldDrawValues(r15)
            if (r0 != 0) goto L_0x0046
            r29 = r10
            r30 = r11
            goto L_0x02a3
        L_0x0046:
            r9.applyValueTextStyle(r15)
            com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider r0 = r9.mChart
            com.github.mikephil.charting.components.YAxis$AxisDependency r3 = r15.getAxisDependency()
            boolean r16 = r0.isInverted(r3)
            android.graphics.Paint r0 = r9.mValuePaint
            java.lang.String r3 = "8"
            int r0 = com.github.mikephil.charting.utils.Utils.calcTextHeight(r0, r3)
            float r8 = (float) r0
            if (r12 == 0) goto L_0x0060
            float r0 = -r11
            goto L_0x0062
        L_0x0060:
            float r0 = r8 + r11
        L_0x0062:
            if (r12 == 0) goto L_0x0067
            float r1 = r8 + r11
            goto L_0x0068
        L_0x0067:
            float r1 = -r11
        L_0x0068:
            if (r16 == 0) goto L_0x0070
            float r2 = -r0
            float r0 = r2 - r8
            float r2 = -r1
            float r1 = r2 - r8
        L_0x0070:
            r18 = r0
            r17 = r1
            com.github.mikephil.charting.buffer.BarBuffer[] r0 = r9.mBarBuffers
            r7 = r0[r14]
            com.github.mikephil.charting.animation.ChartAnimator r0 = r9.mAnimator
            float r19 = r0.getPhaseY()
            boolean r0 = r15.isStacked()
            r20 = 1073741824(0x40000000, float:2.0)
            r21 = 0
            if (r0 != 0) goto L_0x0131
            r0 = 0
        L_0x0089:
            r6 = r0
            float r0 = (float) r6
            float[] r1 = r7.buffer
            int r1 = r1.length
            float r1 = (float) r1
            com.github.mikephil.charting.animation.ChartAnimator r2 = r9.mAnimator
            float r2 = r2.getPhaseX()
            float r1 = r1 * r2
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x0128
            float[] r0 = r7.buffer
            r0 = r0[r6]
            float[] r1 = r7.buffer
            int r2 = r6 + 2
            r1 = r1[r2]
            float r0 = r0 + r1
            float r5 = r0 / r20
            com.github.mikephil.charting.utils.ViewPortHandler r0 = r9.mViewPortHandler
            boolean r0 = r0.isInBoundsRight(r5)
            if (r0 != 0) goto L_0x00b5
            r13 = r7
            r24 = r8
            goto L_0x012b
        L_0x00b5:
            com.github.mikephil.charting.utils.ViewPortHandler r0 = r9.mViewPortHandler
            float[] r1 = r7.buffer
            int r2 = r6 + 1
            r1 = r1[r2]
            boolean r0 = r0.isInBoundsY(r1)
            if (r0 == 0) goto L_0x011c
            com.github.mikephil.charting.utils.ViewPortHandler r0 = r9.mViewPortHandler
            boolean r0 = r0.isInBoundsLeft(r5)
            if (r0 != 0) goto L_0x00d2
            r27 = r6
            r13 = r7
            r24 = r8
            goto L_0x0121
        L_0x00d2:
            int r0 = r6 / 4
            com.github.mikephil.charting.data.Entry r0 = r15.getEntryForIndex(r0)
            r22 = r0
            com.github.mikephil.charting.data.BarEntry r22 = (com.github.mikephil.charting.data.BarEntry) r22
            float r23 = r22.getY()
            com.github.mikephil.charting.formatter.IValueFormatter r2 = r15.getValueFormatter()
            int r0 = (r23 > r21 ? 1 : (r23 == r21 ? 0 : -1))
            if (r0 < 0) goto L_0x00f3
            float[] r0 = r7.buffer
            int r1 = r6 + 1
            r0 = r0[r1]
            float r0 = r0 + r18
        L_0x00f0:
            r24 = r0
            goto L_0x00fc
        L_0x00f3:
            float[] r0 = r7.buffer
            int r1 = r6 + 3
            r0 = r0[r1]
            float r0 = r0 + r17
            goto L_0x00f0
        L_0x00fc:
            int r0 = r6 / 4
            int r25 = r15.getValueTextColor(r0)
            r0 = r35
            r1 = r36
            r3 = r23
            r4 = r22
            r26 = r5
            r5 = r14
            r27 = r6
            r6 = r26
            r13 = r7
            r7 = r24
            r24 = r8
            r8 = r25
            r0.drawValue(r1, r2, r3, r4, r5, r6, r7, r8)
            goto L_0x0121
        L_0x011c:
            r27 = r6
            r13 = r7
            r24 = r8
        L_0x0121:
            int r0 = r27 + 4
            r7 = r13
            r8 = r24
            goto L_0x0089
        L_0x0128:
            r13 = r7
            r24 = r8
        L_0x012b:
            r29 = r10
            r30 = r11
            goto L_0x029f
        L_0x0131:
            r13 = r7
            r24 = r8
            com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider r0 = r9.mChart
            com.github.mikephil.charting.components.YAxis$AxisDependency r1 = r15.getAxisDependency()
            com.github.mikephil.charting.utils.Transformer r8 = r0.getTransformer(r1)
            r0 = 0
            r22 = r0
            r0 = 0
        L_0x0142:
            r7 = r0
            float r0 = (float) r7
            int r1 = r15.getEntryCount()
            float r1 = (float) r1
            com.github.mikephil.charting.animation.ChartAnimator r2 = r9.mAnimator
            float r2 = r2.getPhaseX()
            float r1 = r1 * r2
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x029b
            com.github.mikephil.charting.data.Entry r0 = r15.getEntryForIndex(r7)
            r23 = r0
            com.github.mikephil.charting.data.BarEntry r23 = (com.github.mikephil.charting.data.BarEntry) r23
            float[] r6 = r23.getYVals()
            float[] r0 = r13.buffer
            r0 = r0[r22]
            float[] r1 = r13.buffer
            int r2 = r22 + 2
            r1 = r1[r2]
            float r0 = r0 + r1
            float r5 = r0 / r20
            int r25 = r15.getValueTextColor(r7)
            if (r6 != 0) goto L_0x01e4
            com.github.mikephil.charting.utils.ViewPortHandler r0 = r9.mViewPortHandler
            boolean r0 = r0.isInBoundsRight(r5)
            if (r0 != 0) goto L_0x017c
            goto L_0x012b
        L_0x017c:
            com.github.mikephil.charting.utils.ViewPortHandler r0 = r9.mViewPortHandler
            float[] r1 = r13.buffer
            int r2 = r22 + 1
            r1 = r1[r2]
            boolean r0 = r0.isInBoundsY(r1)
            if (r0 == 0) goto L_0x01d4
            com.github.mikephil.charting.utils.ViewPortHandler r0 = r9.mViewPortHandler
            boolean r0 = r0.isInBoundsLeft(r5)
            if (r0 != 0) goto L_0x019b
            r27 = r7
            r29 = r10
            r30 = r11
            r11 = r8
            goto L_0x01db
        L_0x019b:
            com.github.mikephil.charting.formatter.IValueFormatter r2 = r15.getValueFormatter()
            float r3 = r23.getY()
            float[] r0 = r13.buffer
            int r1 = r22 + 1
            r0 = r0[r1]
            float r1 = r23.getY()
            int r1 = (r1 > r21 ? 1 : (r1 == r21 ? 0 : -1))
            if (r1 < 0) goto L_0x01b4
            r1 = r18
            goto L_0x01b6
        L_0x01b4:
            r1 = r17
        L_0x01b6:
            float r26 = r0 + r1
            r0 = r35
            r1 = r36
            r4 = r23
            r28 = r5
            r5 = r14
            r29 = r10
            r10 = r6
            r6 = r28
            r27 = r7
            r7 = r26
            r30 = r11
            r11 = r8
            r8 = r25
            r0.drawValue(r1, r2, r3, r4, r5, r6, r7, r8)
            goto L_0x0289
        L_0x01d4:
            r27 = r7
            r29 = r10
            r30 = r11
            r11 = r8
        L_0x01db:
            r8 = r11
            r0 = r27
        L_0x01de:
            r10 = r29
            r11 = r30
            goto L_0x0142
        L_0x01e4:
            r28 = r5
            r27 = r7
            r29 = r10
            r30 = r11
            r10 = r6
            r11 = r8
            int r0 = r10.length
            int r0 = r0 * 2
            float[] r8 = new float[r0]
            r0 = 0
            float r1 = r23.getNegativeSum()
            float r1 = -r1
            r2 = 0
            r26 = r0
            r31 = r1
            r0 = 0
        L_0x01ff:
            int r1 = r8.length
            if (r2 >= r1) goto L_0x021c
            r1 = r10[r0]
            int r3 = (r1 > r21 ? 1 : (r1 == r21 ? 0 : -1))
            if (r3 < 0) goto L_0x020d
            float r26 = r26 + r1
            r3 = r26
            goto L_0x0211
        L_0x020d:
            r3 = r31
            float r31 = r31 - r1
        L_0x0211:
            int r4 = r2 + 1
            float r5 = r3 * r19
            r8[r4] = r5
            int r2 = r2 + 2
            int r0 = r0 + 1
            goto L_0x01ff
        L_0x021c:
            r11.pointValuesToPixel(r8)
            r0 = 0
        L_0x0220:
            r7 = r0
            int r0 = r8.length
            if (r7 >= r0) goto L_0x0289
            int r0 = r7 + 1
            r0 = r8[r0]
            int r1 = r7 / 2
            r1 = r10[r1]
            int r1 = (r1 > r21 ? 1 : (r1 == r21 ? 0 : -1))
            if (r1 < 0) goto L_0x0233
            r1 = r18
            goto L_0x0235
        L_0x0233:
            r1 = r17
        L_0x0235:
            float r6 = r0 + r1
            com.github.mikephil.charting.utils.ViewPortHandler r0 = r9.mViewPortHandler
            r5 = r28
            boolean r0 = r0.isInBoundsRight(r5)
            if (r0 != 0) goto L_0x0245
            r28 = r5
            goto L_0x0289
        L_0x0245:
            com.github.mikephil.charting.utils.ViewPortHandler r0 = r9.mViewPortHandler
            boolean r0 = r0.isInBoundsY(r6)
            if (r0 == 0) goto L_0x027e
            com.github.mikephil.charting.utils.ViewPortHandler r0 = r9.mViewPortHandler
            boolean r0 = r0.isInBoundsLeft(r5)
            if (r0 != 0) goto L_0x025d
            r28 = r5
            r33 = r7
            r34 = r8
            goto L_0x0284
        L_0x025d:
            com.github.mikephil.charting.formatter.IValueFormatter r2 = r15.getValueFormatter()
            int r0 = r7 / 2
            r3 = r10[r0]
            r0 = r35
            r1 = r36
            r4 = r23
            r28 = r5
            r5 = r14
            r32 = r6
            r6 = r28
            r33 = r7
            r7 = r32
            r34 = r8
            r8 = r25
            r0.drawValue(r1, r2, r3, r4, r5, r6, r7, r8)
            goto L_0x0284
        L_0x027e:
            r28 = r5
            r33 = r7
            r34 = r8
        L_0x0284:
            int r0 = r33 + 2
            r8 = r34
            goto L_0x0220
        L_0x0289:
            if (r10 != 0) goto L_0x028e
            int r0 = r22 + 4
            goto L_0x0293
        L_0x028e:
            int r0 = r10.length
            int r0 = r0 * 4
            int r0 = r22 + r0
        L_0x0293:
            r22 = r0
            int r0 = r27 + 1
            r8 = r11
            goto L_0x01de
        L_0x029b:
            r29 = r10
            r30 = r11
        L_0x029f:
            r2 = r17
            r1 = r18
        L_0x02a3:
            int r0 = r14 + 1
            r10 = r29
            r11 = r30
            goto L_0x0025
        L_0x02ab:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.github.mikephil.charting.renderer.BarChartRenderer.drawValues(android.graphics.Canvas):void");
    }

    public void drawHighlighted(Canvas c, Highlight[] indices) {
        float y1;
        float y2;
        float f;
        float y12;
        BarData barData = this.mChart.getBarData();
        for (Highlight high : indices) {
            IBarDataSet set = (IBarDataSet) barData.getDataSetByIndex(high.getDataSetIndex());
            if (set != null && set.isHighlightEnabled()) {
                BarEntry e2 = (BarEntry) set.getEntryForXValue(high.getX(), high.getY());
                if (isInBoundsX(e2, set)) {
                    Transformer trans = this.mChart.getTransformer(set.getAxisDependency());
                    this.mHighlightPaint.setColor(set.getHighLightColor());
                    this.mHighlightPaint.setAlpha(set.getHighLightAlpha());
                    if (!(high.getStackIndex() >= 0 && e2.isStacked())) {
                        y12 = e2.getY();
                        f = 0.0f;
                    } else if (this.mChart.isHighlightFullBarEnabled()) {
                        y12 = e2.getPositiveSum();
                        f = -e2.getNegativeSum();
                    } else {
                        Range range = e2.getRanges()[high.getStackIndex()];
                        float y13 = range.from;
                        y2 = range.to;
                        y1 = y13;
                        prepareBarHighlight(e2.getX(), y1, y2, barData.getBarWidth() / 2.0f, trans);
                        setHighlightDrawPos(high, this.mBarRect);
                        c.drawRect(this.mBarRect, this.mHighlightPaint);
                    }
                    y1 = y12;
                    y2 = f;
                    prepareBarHighlight(e2.getX(), y1, y2, barData.getBarWidth() / 2.0f, trans);
                    setHighlightDrawPos(high, this.mBarRect);
                    c.drawRect(this.mBarRect, this.mHighlightPaint);
                }
            }
            Canvas canvas = c;
        }
        Canvas canvas2 = c;
    }

    /* access modifiers changed from: protected */
    public void setHighlightDrawPos(Highlight high, RectF bar) {
        high.setDraw(bar.centerX(), bar.top);
    }

    public void drawExtras(Canvas c) {
    }
}
