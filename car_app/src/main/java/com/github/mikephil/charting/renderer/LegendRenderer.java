package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.ArrayList;
import java.util.List;

public class LegendRenderer extends Renderer {
    protected List<LegendEntry> computedEntries = new ArrayList(16);
    protected Paint.FontMetrics legendFontMetrics = new Paint.FontMetrics();
    protected Legend mLegend;
    protected Paint mLegendFormPaint;
    protected Paint mLegendLabelPaint;
    private Path mLineFormPath = new Path();

    public LegendRenderer(ViewPortHandler viewPortHandler, Legend legend) {
        super(viewPortHandler);
        this.mLegend = legend;
        this.mLegendLabelPaint = new Paint(1);
        this.mLegendLabelPaint.setTextSize(Utils.convertDpToPixel(9.0f));
        this.mLegendLabelPaint.setTextAlign(Paint.Align.LEFT);
        this.mLegendFormPaint = new Paint(1);
        this.mLegendFormPaint.setStyle(Paint.Style.FILL);
    }

    public Paint getLabelPaint() {
        return this.mLegendLabelPaint;
    }

    public Paint getFormPaint() {
        return this.mLegendFormPaint;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: com.github.mikephil.charting.data.ChartData<?>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: com.github.mikephil.charting.data.ChartData<?>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: com.github.mikephil.charting.data.ChartData<?>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v11, resolved type: com.github.mikephil.charting.data.ChartData<?>} */
    /* JADX WARNING: type inference failed for: r4v6, types: [com.github.mikephil.charting.interfaces.datasets.IDataSet] */
    /* JADX WARNING: type inference failed for: r7v2, types: [com.github.mikephil.charting.interfaces.datasets.IDataSet] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void computeLegend(com.github.mikephil.charting.data.ChartData<?> r21) {
        /*
            r20 = this;
            r0 = r20
            r1 = r21
            com.github.mikephil.charting.components.Legend r2 = r0.mLegend
            boolean r2 = r2.isLegendCustom()
            if (r2 != 0) goto L_0x01d5
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r2 = r0.computedEntries
            r2.clear()
            r3 = 0
        L_0x0012:
            int r4 = r21.getDataSetCount()
            if (r3 >= r4) goto L_0x01b9
            com.github.mikephil.charting.interfaces.datasets.IDataSet r4 = r1.getDataSetByIndex(r3)
            java.util.List r5 = r4.getColors()
            int r6 = r4.getEntryCount()
            boolean r7 = r4 instanceof com.github.mikephil.charting.interfaces.datasets.IBarDataSet
            if (r7 == 0) goto L_0x00a1
            r7 = r4
            com.github.mikephil.charting.interfaces.datasets.IBarDataSet r7 = (com.github.mikephil.charting.interfaces.datasets.IBarDataSet) r7
            boolean r7 = r7.isStacked()
            if (r7 == 0) goto L_0x00a1
            r7 = r4
            com.github.mikephil.charting.interfaces.datasets.IBarDataSet r7 = (com.github.mikephil.charting.interfaces.datasets.IBarDataSet) r7
            java.lang.String[] r8 = r7.getStackLabels()
            r9 = 0
        L_0x0039:
            int r10 = r5.size()
            if (r9 >= r10) goto L_0x0079
            int r10 = r7.getStackSize()
            if (r9 >= r10) goto L_0x0079
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r10 = r0.computedEntries
            com.github.mikephil.charting.components.LegendEntry r15 = new com.github.mikephil.charting.components.LegendEntry
            int r11 = r8.length
            int r11 = r9 % r11
            r12 = r8[r11]
            com.github.mikephil.charting.components.Legend$LegendForm r13 = r4.getForm()
            float r14 = r4.getFormSize()
            float r16 = r4.getFormLineWidth()
            android.graphics.DashPathEffect r17 = r4.getFormLineDashEffect()
            java.lang.Object r11 = r5.get(r9)
            java.lang.Integer r11 = (java.lang.Integer) r11
            int r18 = r11.intValue()
            r11 = r15
            r2 = r15
            r15 = r16
            r16 = r17
            r17 = r18
            r11.<init>(r12, r13, r14, r15, r16, r17)
            r10.add(r2)
            int r9 = r9 + 1
            goto L_0x0039
        L_0x0079:
            java.lang.String r2 = r7.getLabel()
            if (r2 == 0) goto L_0x009d
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r2 = r0.computedEntries
            com.github.mikephil.charting.components.LegendEntry r15 = new com.github.mikephil.charting.components.LegendEntry
            java.lang.String r10 = r4.getLabel()
            com.github.mikephil.charting.components.Legend$LegendForm r11 = com.github.mikephil.charting.components.Legend.LegendForm.NONE
            r12 = 2143289344(0x7fc00000, float:NaN)
            r13 = 2143289344(0x7fc00000, float:NaN)
            r14 = 0
            r16 = 1122867(0x112233, float:1.573472E-39)
            r9 = r15
            r19 = r7
            r7 = r15
            r15 = r16
            r9.<init>(r10, r11, r12, r13, r14, r15)
            r2.add(r7)
        L_0x009d:
            r2 = r1
            goto L_0x01b4
        L_0x00a1:
            boolean r2 = r4 instanceof com.github.mikephil.charting.interfaces.datasets.IPieDataSet
            if (r2 == 0) goto L_0x0108
            r2 = r4
            com.github.mikephil.charting.interfaces.datasets.IPieDataSet r2 = (com.github.mikephil.charting.interfaces.datasets.IPieDataSet) r2
            r7 = 0
        L_0x00a9:
            int r8 = r5.size()
            if (r7 >= r8) goto L_0x00e8
            if (r7 >= r6) goto L_0x00e8
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r8 = r0.computedEntries
            com.github.mikephil.charting.components.LegendEntry r15 = new com.github.mikephil.charting.components.LegendEntry
            com.github.mikephil.charting.data.Entry r9 = r2.getEntryForIndex(r7)
            com.github.mikephil.charting.data.PieEntry r9 = (com.github.mikephil.charting.data.PieEntry) r9
            java.lang.String r10 = r9.getLabel()
            com.github.mikephil.charting.components.Legend$LegendForm r11 = r4.getForm()
            float r12 = r4.getFormSize()
            float r13 = r4.getFormLineWidth()
            android.graphics.DashPathEffect r14 = r4.getFormLineDashEffect()
            java.lang.Object r9 = r5.get(r7)
            java.lang.Integer r9 = (java.lang.Integer) r9
            int r16 = r9.intValue()
            r9 = r15
            r1 = r15
            r15 = r16
            r9.<init>(r10, r11, r12, r13, r14, r15)
            r8.add(r1)
            int r7 = r7 + 1
            r1 = r21
            goto L_0x00a9
        L_0x00e8:
            java.lang.String r1 = r2.getLabel()
            if (r1 == 0) goto L_0x0107
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r1 = r0.computedEntries
            com.github.mikephil.charting.components.LegendEntry r14 = new com.github.mikephil.charting.components.LegendEntry
            java.lang.String r8 = r4.getLabel()
            com.github.mikephil.charting.components.Legend$LegendForm r9 = com.github.mikephil.charting.components.Legend.LegendForm.NONE
            r10 = 2143289344(0x7fc00000, float:NaN)
            r11 = 2143289344(0x7fc00000, float:NaN)
            r12 = 0
            r13 = 1122867(0x112233, float:1.573472E-39)
            r7 = r14
            r7.<init>(r8, r9, r10, r11, r12, r13)
            r1.add(r14)
        L_0x0107:
            goto L_0x0164
        L_0x0108:
            boolean r1 = r4 instanceof com.github.mikephil.charting.interfaces.datasets.ICandleDataSet
            if (r1 == 0) goto L_0x0167
            r1 = r4
            com.github.mikephil.charting.interfaces.datasets.ICandleDataSet r1 = (com.github.mikephil.charting.interfaces.datasets.ICandleDataSet) r1
            int r1 = r1.getDecreasingColor()
            r2 = 1122867(0x112233, float:1.573472E-39)
            if (r1 == r2) goto L_0x0167
            r1 = r4
            com.github.mikephil.charting.interfaces.datasets.ICandleDataSet r1 = (com.github.mikephil.charting.interfaces.datasets.ICandleDataSet) r1
            int r1 = r1.getDecreasingColor()
            r2 = r4
            com.github.mikephil.charting.interfaces.datasets.ICandleDataSet r2 = (com.github.mikephil.charting.interfaces.datasets.ICandleDataSet) r2
            int r2 = r2.getIncreasingColor()
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r14 = r0.computedEntries
            com.github.mikephil.charting.components.LegendEntry r15 = new com.github.mikephil.charting.components.LegendEntry
            r8 = 0
            com.github.mikephil.charting.components.Legend$LegendForm r9 = r4.getForm()
            float r10 = r4.getFormSize()
            float r11 = r4.getFormLineWidth()
            android.graphics.DashPathEffect r12 = r4.getFormLineDashEffect()
            r7 = r15
            r13 = r1
            r7.<init>(r8, r9, r10, r11, r12, r13)
            r14.add(r15)
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r14 = r0.computedEntries
            com.github.mikephil.charting.components.LegendEntry r15 = new com.github.mikephil.charting.components.LegendEntry
            java.lang.String r8 = r4.getLabel()
            com.github.mikephil.charting.components.Legend$LegendForm r9 = r4.getForm()
            float r10 = r4.getFormSize()
            float r11 = r4.getFormLineWidth()
            android.graphics.DashPathEffect r12 = r4.getFormLineDashEffect()
            r7 = r15
            r13 = r2
            r7.<init>(r8, r9, r10, r11, r12, r13)
            r14.add(r15)
        L_0x0164:
            r2 = r21
            goto L_0x01b4
        L_0x0167:
            r1 = 0
        L_0x0168:
            int r2 = r5.size()
            if (r1 >= r2) goto L_0x0164
            if (r1 >= r6) goto L_0x0164
            int r2 = r5.size()
            int r2 = r2 + -1
            if (r1 >= r2) goto L_0x0181
            int r2 = r6 + -1
            if (r1 >= r2) goto L_0x0181
            r2 = 0
            r9 = r2
            r2 = r21
            goto L_0x018c
        L_0x0181:
            r2 = r21
            com.github.mikephil.charting.interfaces.datasets.IDataSet r7 = r2.getDataSetByIndex(r3)
            java.lang.String r7 = r7.getLabel()
            r9 = r7
        L_0x018c:
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r7 = r0.computedEntries
            com.github.mikephil.charting.components.LegendEntry r15 = new com.github.mikephil.charting.components.LegendEntry
            com.github.mikephil.charting.components.Legend$LegendForm r10 = r4.getForm()
            float r11 = r4.getFormSize()
            float r12 = r4.getFormLineWidth()
            android.graphics.DashPathEffect r13 = r4.getFormLineDashEffect()
            java.lang.Object r8 = r5.get(r1)
            java.lang.Integer r8 = (java.lang.Integer) r8
            int r14 = r8.intValue()
            r8 = r15
            r8.<init>(r9, r10, r11, r12, r13, r14)
            r7.add(r15)
            int r1 = r1 + 1
            goto L_0x0168
        L_0x01b4:
            int r3 = r3 + 1
            r1 = r2
            goto L_0x0012
        L_0x01b9:
            r2 = r1
            com.github.mikephil.charting.components.Legend r1 = r0.mLegend
            com.github.mikephil.charting.components.LegendEntry[] r1 = r1.getExtraEntries()
            if (r1 == 0) goto L_0x01cd
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r1 = r0.computedEntries
            com.github.mikephil.charting.components.Legend r3 = r0.mLegend
            com.github.mikephil.charting.components.LegendEntry[] r3 = r3.getExtraEntries()
            java.util.Collections.addAll(r1, r3)
        L_0x01cd:
            com.github.mikephil.charting.components.Legend r1 = r0.mLegend
            java.util.List<com.github.mikephil.charting.components.LegendEntry> r3 = r0.computedEntries
            r1.setEntries(r3)
            goto L_0x01d6
        L_0x01d5:
            r2 = r1
        L_0x01d6:
            com.github.mikephil.charting.components.Legend r1 = r0.mLegend
            android.graphics.Typeface r1 = r1.getTypeface()
            if (r1 == 0) goto L_0x01e3
            android.graphics.Paint r3 = r0.mLegendLabelPaint
            r3.setTypeface(r1)
        L_0x01e3:
            android.graphics.Paint r3 = r0.mLegendLabelPaint
            com.github.mikephil.charting.components.Legend r4 = r0.mLegend
            float r4 = r4.getTextSize()
            r3.setTextSize(r4)
            android.graphics.Paint r3 = r0.mLegendLabelPaint
            com.github.mikephil.charting.components.Legend r4 = r0.mLegend
            int r4 = r4.getTextColor()
            r3.setColor(r4)
            com.github.mikephil.charting.components.Legend r3 = r0.mLegend
            android.graphics.Paint r4 = r0.mLegendLabelPaint
            com.github.mikephil.charting.utils.ViewPortHandler r5 = r0.mViewPortHandler
            r3.calculateDimensions(r4, r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.github.mikephil.charting.renderer.LegendRenderer.computeLegend(com.github.mikephil.charting.data.ChartData):void");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void renderLegend(android.graphics.Canvas r40) {
        /*
            r39 = this;
            r6 = r39
            r7 = r40
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            boolean r0 = r0.isEnabled()
            if (r0 != 0) goto L_0x000d
            return
        L_0x000d:
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            android.graphics.Typeface r8 = r0.getTypeface()
            if (r8 == 0) goto L_0x001a
            android.graphics.Paint r0 = r6.mLegendLabelPaint
            r0.setTypeface(r8)
        L_0x001a:
            android.graphics.Paint r0 = r6.mLegendLabelPaint
            com.github.mikephil.charting.components.Legend r1 = r6.mLegend
            float r1 = r1.getTextSize()
            r0.setTextSize(r1)
            android.graphics.Paint r0 = r6.mLegendLabelPaint
            com.github.mikephil.charting.components.Legend r1 = r6.mLegend
            int r1 = r1.getTextColor()
            r0.setColor(r1)
            android.graphics.Paint r0 = r6.mLegendLabelPaint
            android.graphics.Paint$FontMetrics r1 = r6.legendFontMetrics
            float r9 = com.github.mikephil.charting.utils.Utils.getLineHeight(r0, r1)
            android.graphics.Paint r0 = r6.mLegendLabelPaint
            android.graphics.Paint$FontMetrics r1 = r6.legendFontMetrics
            float r0 = com.github.mikephil.charting.utils.Utils.getLineSpacing(r0, r1)
            com.github.mikephil.charting.components.Legend r1 = r6.mLegend
            float r1 = r1.getYEntrySpace()
            float r1 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r1)
            float r10 = r0 + r1
            android.graphics.Paint r0 = r6.mLegendLabelPaint
            java.lang.String r1 = "ABC"
            int r0 = com.github.mikephil.charting.utils.Utils.calcTextHeight(r0, r1)
            float r0 = (float) r0
            r11 = 1073741824(0x40000000, float:2.0)
            float r0 = r0 / r11
            float r12 = r9 - r0
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            com.github.mikephil.charting.components.LegendEntry[] r13 = r0.getEntries()
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            float r0 = r0.getFormToTextSpace()
            float r14 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r0)
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            float r0 = r0.getXEntrySpace()
            float r15 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r0)
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            com.github.mikephil.charting.components.Legend$LegendOrientation r5 = r0.getOrientation()
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            com.github.mikephil.charting.components.Legend$LegendHorizontalAlignment r4 = r0.getHorizontalAlignment()
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            com.github.mikephil.charting.components.Legend$LegendVerticalAlignment r16 = r0.getVerticalAlignment()
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            com.github.mikephil.charting.components.Legend$LegendDirection r3 = r0.getDirection()
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            float r0 = r0.getFormSize()
            float r17 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r0)
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            float r0 = r0.getStackSpace()
            float r2 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r0)
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            float r18 = r0.getYOffset()
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            float r1 = r0.getXOffset()
            r0 = 0
            int[] r19 = com.github.mikephil.charting.renderer.LegendRenderer.AnonymousClass1.$SwitchMap$com$github$mikephil$charting$components$Legend$LegendHorizontalAlignment
            int r20 = r4.ordinal()
            r19 = r19[r20]
            switch(r19) {
                case 1: goto L_0x0154;
                case 2: goto L_0x012d;
                case 3: goto L_0x00c4;
                default: goto L_0x00b8;
            }
        L_0x00b8:
            r22 = r0
            r25 = r8
            r23 = r10
            r26 = r14
            r27 = r15
            goto L_0x0176
        L_0x00c4:
            com.github.mikephil.charting.components.Legend$LegendOrientation r11 = com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
            if (r5 != r11) goto L_0x00d3
            com.github.mikephil.charting.utils.ViewPortHandler r11 = r6.mViewPortHandler
            float r11 = r11.getChartWidth()
            r19 = 1073741824(0x40000000, float:2.0)
            float r11 = r11 / r19
            goto L_0x00e6
        L_0x00d3:
            r19 = 1073741824(0x40000000, float:2.0)
            com.github.mikephil.charting.utils.ViewPortHandler r11 = r6.mViewPortHandler
            float r11 = r11.contentLeft()
            r22 = r0
            com.github.mikephil.charting.utils.ViewPortHandler r0 = r6.mViewPortHandler
            float r0 = r0.contentWidth()
            float r0 = r0 / r19
            float r11 = r11 + r0
        L_0x00e6:
            com.github.mikephil.charting.components.Legend$LegendDirection r0 = com.github.mikephil.charting.components.Legend.LegendDirection.LEFT_TO_RIGHT
            if (r3 != r0) goto L_0x00ec
            r0 = r1
            goto L_0x00ed
        L_0x00ec:
            float r0 = -r1
        L_0x00ed:
            float r0 = r0 + r11
            com.github.mikephil.charting.components.Legend$LegendOrientation r11 = com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
            if (r5 != r11) goto L_0x0120
            r23 = r10
            double r10 = (double) r0
            r24 = r0
            com.github.mikephil.charting.components.Legend$LegendDirection r0 = com.github.mikephil.charting.components.Legend.LegendDirection.LEFT_TO_RIGHT
            r19 = 4611686018427387904(0x4000000000000000, double:2.0)
            if (r3 != r0) goto L_0x010e
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            float r0 = r0.mNeededWidth
            float r0 = -r0
            r25 = r8
            double r7 = (double) r0
            double r7 = r7 / r19
            r26 = r14
            r27 = r15
            double r14 = (double) r1
            double r7 = r7 + r14
            goto L_0x011d
        L_0x010e:
            r25 = r8
            r26 = r14
            r27 = r15
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            float r0 = r0.mNeededWidth
            double r7 = (double) r0
            double r7 = r7 / r19
            double r14 = (double) r1
            double r7 = r7 - r14
        L_0x011d:
            double r10 = r10 + r7
            float r0 = (float) r10
            goto L_0x0174
        L_0x0120:
            r24 = r0
            r25 = r8
            r23 = r10
            r26 = r14
            r27 = r15
            r22 = r24
            goto L_0x0176
        L_0x012d:
            r22 = r0
            r25 = r8
            r23 = r10
            r26 = r14
            r27 = r15
            com.github.mikephil.charting.components.Legend$LegendOrientation r0 = com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
            if (r5 != r0) goto L_0x0143
            com.github.mikephil.charting.utils.ViewPortHandler r0 = r6.mViewPortHandler
            float r0 = r0.getChartWidth()
            float r0 = r0 - r1
            goto L_0x014a
        L_0x0143:
            com.github.mikephil.charting.utils.ViewPortHandler r0 = r6.mViewPortHandler
            float r0 = r0.contentRight()
            float r0 = r0 - r1
        L_0x014a:
            com.github.mikephil.charting.components.Legend$LegendDirection r7 = com.github.mikephil.charting.components.Legend.LegendDirection.LEFT_TO_RIGHT
            if (r3 != r7) goto L_0x0174
            com.github.mikephil.charting.components.Legend r7 = r6.mLegend
            float r7 = r7.mNeededWidth
            float r0 = r0 - r7
            goto L_0x0174
        L_0x0154:
            r22 = r0
            r25 = r8
            r23 = r10
            r26 = r14
            r27 = r15
            com.github.mikephil.charting.components.Legend$LegendOrientation r0 = com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
            if (r5 != r0) goto L_0x0164
            r0 = r1
            goto L_0x016b
        L_0x0164:
            com.github.mikephil.charting.utils.ViewPortHandler r0 = r6.mViewPortHandler
            float r0 = r0.contentLeft()
            float r0 = r0 + r1
        L_0x016b:
            com.github.mikephil.charting.components.Legend$LegendDirection r7 = com.github.mikephil.charting.components.Legend.LegendDirection.RIGHT_TO_LEFT
            if (r3 != r7) goto L_0x0174
            com.github.mikephil.charting.components.Legend r7 = r6.mLegend
            float r7 = r7.mNeededWidth
            float r0 = r0 + r7
        L_0x0174:
            r22 = r0
        L_0x0176:
            int[] r0 = com.github.mikephil.charting.renderer.LegendRenderer.AnonymousClass1.$SwitchMap$com$github$mikephil$charting$components$Legend$LegendOrientation
            int r7 = r5.ordinal()
            r0 = r0[r7]
            switch(r0) {
                case 1: goto L_0x02cd;
                case 2: goto L_0x0194;
                default: goto L_0x0181;
            }
        L_0x0181:
            r24 = r1
            r37 = r4
            r32 = r5
            r29 = r12
            r11 = r13
            r10 = r26
            r7 = r40
            r12 = r2
            r13 = r3
            r3 = r27
            goto L_0x044e
        L_0x0194:
            r0 = 0
            r10 = 0
            r11 = 0
            int[] r14 = com.github.mikephil.charting.renderer.LegendRenderer.AnonymousClass1.$SwitchMap$com$github$mikephil$charting$components$Legend$LegendVerticalAlignment
            int r15 = r16.ordinal()
            r14 = r14[r15]
            switch(r14) {
                case 1: goto L_0x01d5;
                case 2: goto L_0x01bb;
                case 3: goto L_0x01a3;
                default: goto L_0x01a2;
            }
        L_0x01a2:
            goto L_0x01e4
        L_0x01a3:
            com.github.mikephil.charting.utils.ViewPortHandler r14 = r6.mViewPortHandler
            float r14 = r14.getChartHeight()
            r15 = 1073741824(0x40000000, float:2.0)
            float r14 = r14 / r15
            com.github.mikephil.charting.components.Legend r7 = r6.mLegend
            float r7 = r7.mNeededHeight
            float r7 = r7 / r15
            float r14 = r14 - r7
            com.github.mikephil.charting.components.Legend r7 = r6.mLegend
            float r7 = r7.getYOffset()
            float r11 = r14 + r7
            goto L_0x01e4
        L_0x01bb:
            com.github.mikephil.charting.components.Legend$LegendHorizontalAlignment r7 = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
            if (r4 != r7) goto L_0x01c6
            com.github.mikephil.charting.utils.ViewPortHandler r7 = r6.mViewPortHandler
            float r7 = r7.getChartHeight()
            goto L_0x01cc
        L_0x01c6:
            com.github.mikephil.charting.utils.ViewPortHandler r7 = r6.mViewPortHandler
            float r7 = r7.contentBottom()
        L_0x01cc:
            com.github.mikephil.charting.components.Legend r11 = r6.mLegend
            float r11 = r11.mNeededHeight
            float r11 = r11 + r18
            float r11 = r7 - r11
            goto L_0x01e4
        L_0x01d5:
            com.github.mikephil.charting.components.Legend$LegendHorizontalAlignment r7 = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
            if (r4 != r7) goto L_0x01db
            r7 = 0
            goto L_0x01e1
        L_0x01db:
            com.github.mikephil.charting.utils.ViewPortHandler r7 = r6.mViewPortHandler
            float r7 = r7.contentTop()
        L_0x01e1:
            float r11 = r7 + r18
        L_0x01e4:
            r7 = r0
            r0 = 0
        L_0x01e6:
            r14 = r0
            int r0 = r13.length
            if (r14 >= r0) goto L_0x02b3
            r15 = r13[r14]
            com.github.mikephil.charting.components.Legend$LegendForm r0 = r15.form
            com.github.mikephil.charting.components.Legend$LegendForm r8 = com.github.mikephil.charting.components.Legend.LegendForm.NONE
            if (r0 == r8) goto L_0x01f4
            r0 = 1
            goto L_0x01f5
        L_0x01f4:
            r0 = 0
        L_0x01f5:
            r8 = r0
            float r0 = r15.formSize
            boolean r0 = java.lang.Float.isNaN(r0)
            if (r0 == 0) goto L_0x0201
            r0 = r17
            goto L_0x0207
        L_0x0201:
            float r0 = r15.formSize
            float r0 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r0)
        L_0x0207:
            r19 = r0
            r0 = r22
            if (r8 == 0) goto L_0x0245
            r28 = r1
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.LEFT_TO_RIGHT
            if (r3 != r1) goto L_0x0217
            float r0 = r0 + r7
        L_0x0214:
            r20 = r0
            goto L_0x021b
        L_0x0217:
            float r1 = r19 - r7
            float r0 = r0 - r1
            goto L_0x0214
        L_0x021b:
            float r21 = r11 + r12
            com.github.mikephil.charting.components.Legend r1 = r6.mLegend
            r0 = r39
            r24 = r28
            r28 = r1
            r1 = r40
            r29 = r12
            r12 = r2
            r2 = r20
            r30 = r13
            r13 = r3
            r3 = r21
            r31 = r4
            r4 = r15
            r32 = r5
            r5 = r28
            r0.drawForm(r1, r2, r3, r4, r5)
            com.github.mikephil.charting.components.Legend$LegendDirection r0 = com.github.mikephil.charting.components.Legend.LegendDirection.LEFT_TO_RIGHT
            if (r13 != r0) goto L_0x0242
            float r0 = r20 + r19
            goto L_0x0251
        L_0x0242:
            r0 = r20
            goto L_0x0251
        L_0x0245:
            r24 = r1
            r31 = r4
            r32 = r5
            r29 = r12
            r30 = r13
            r12 = r2
            r13 = r3
        L_0x0251:
            java.lang.String r1 = r15.label
            if (r1 == 0) goto L_0x0298
            if (r8 == 0) goto L_0x0266
            if (r10 != 0) goto L_0x0266
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.LEFT_TO_RIGHT
            if (r13 != r1) goto L_0x0261
            r1 = r26
            r5 = r1
            goto L_0x0264
        L_0x0261:
            r5 = r26
            float r1 = -r5
        L_0x0264:
            float r0 = r0 + r1
            goto L_0x026c
        L_0x0266:
            r5 = r26
            if (r10 == 0) goto L_0x026c
            r0 = r22
        L_0x026c:
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.RIGHT_TO_LEFT
            if (r13 != r1) goto L_0x027a
            android.graphics.Paint r1 = r6.mLegendLabelPaint
            java.lang.String r2 = r15.label
            int r1 = com.github.mikephil.charting.utils.Utils.calcTextWidth(r1, r2)
            float r1 = (float) r1
            float r0 = r0 - r1
        L_0x027a:
            if (r10 != 0) goto L_0x0286
            float r1 = r11 + r9
            java.lang.String r2 = r15.label
            r4 = r40
            r6.drawLabel(r4, r0, r1, r2)
            goto L_0x0292
        L_0x0286:
            r4 = r40
            float r1 = r9 + r23
            float r11 = r11 + r1
            float r1 = r11 + r9
            java.lang.String r2 = r15.label
            r6.drawLabel(r4, r0, r1, r2)
        L_0x0292:
            float r1 = r9 + r23
            float r11 = r11 + r1
            r1 = 0
            r7 = r1
            goto L_0x02a1
        L_0x0298:
            r5 = r26
            r4 = r40
            float r2 = r19 + r12
            float r7 = r7 + r2
            r0 = 1
            r10 = r0
        L_0x02a1:
            int r0 = r14 + 1
            r26 = r5
            r2 = r12
            r3 = r13
            r1 = r24
            r12 = r29
            r13 = r30
            r4 = r31
            r5 = r32
            goto L_0x01e6
        L_0x02b3:
            r24 = r1
            r31 = r4
            r32 = r5
            r29 = r12
            r30 = r13
            r5 = r26
            r4 = r40
            r12 = r2
            r13 = r3
            r7 = r4
            r10 = r5
            r3 = r27
            r11 = r30
            r37 = r31
            goto L_0x044e
        L_0x02cd:
            r24 = r1
            r31 = r4
            r32 = r5
            r29 = r12
            r30 = r13
            r5 = r26
            r4 = r40
            r12 = r2
            r13 = r3
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            java.util.List r7 = r0.getCalculatedLineSizes()
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            java.util.List r8 = r0.getCalculatedLabelSizes()
            com.github.mikephil.charting.components.Legend r0 = r6.mLegend
            java.util.List r10 = r0.getCalculatedLabelBreakPoints()
            r0 = r22
            r1 = 0
            int[] r2 = com.github.mikephil.charting.renderer.LegendRenderer.AnonymousClass1.$SwitchMap$com$github$mikephil$charting$components$Legend$LegendVerticalAlignment
            int r3 = r16.ordinal()
            r2 = r2[r3]
            switch(r2) {
                case 1: goto L_0x031e;
                case 2: goto L_0x030f;
                case 3: goto L_0x02fe;
                default: goto L_0x02fd;
            }
        L_0x02fd:
            goto L_0x0321
        L_0x02fe:
            com.github.mikephil.charting.utils.ViewPortHandler r2 = r6.mViewPortHandler
            float r2 = r2.getChartHeight()
            com.github.mikephil.charting.components.Legend r3 = r6.mLegend
            float r3 = r3.mNeededHeight
            float r2 = r2 - r3
            r3 = 1073741824(0x40000000, float:2.0)
            float r2 = r2 / r3
            float r1 = r2 + r18
            goto L_0x0321
        L_0x030f:
            com.github.mikephil.charting.utils.ViewPortHandler r2 = r6.mViewPortHandler
            float r2 = r2.getChartHeight()
            float r2 = r2 - r18
            com.github.mikephil.charting.components.Legend r3 = r6.mLegend
            float r3 = r3.mNeededHeight
            float r1 = r2 - r3
            goto L_0x0321
        L_0x031e:
            r1 = r18
        L_0x0321:
            r2 = 0
            r3 = 0
            r11 = r30
            int r14 = r11.length
            r15 = r3
        L_0x0327:
            if (r15 >= r14) goto L_0x0442
            r3 = r11[r15]
            r33 = r0
            com.github.mikephil.charting.components.Legend$LegendForm r0 = r3.form
            com.github.mikephil.charting.components.Legend$LegendForm r4 = com.github.mikephil.charting.components.Legend.LegendForm.NONE
            if (r0 == r4) goto L_0x0335
            r0 = 1
            goto L_0x0336
        L_0x0335:
            r0 = 0
        L_0x0336:
            r19 = r0
            float r0 = r3.formSize
            boolean r0 = java.lang.Float.isNaN(r0)
            if (r0 == 0) goto L_0x0343
            r0 = r17
            goto L_0x0349
        L_0x0343:
            float r0 = r3.formSize
            float r0 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r0)
        L_0x0349:
            r20 = r0
            int r0 = r10.size()
            if (r15 >= r0) goto L_0x0365
            java.lang.Object r0 = r10.get(r15)
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            if (r0 == 0) goto L_0x0365
            r0 = r22
            float r4 = r9 + r23
            float r1 = r1 + r4
            r26 = r1
            goto L_0x0369
        L_0x0365:
            r26 = r1
            r0 = r33
        L_0x0369:
            int r1 = (r0 > r22 ? 1 : (r0 == r22 ? 0 : -1))
            if (r1 != 0) goto L_0x039a
            com.github.mikephil.charting.components.Legend$LegendHorizontalAlignment r1 = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
            r4 = r31
            if (r4 != r1) goto L_0x0397
            int r1 = r7.size()
            if (r2 >= r1) goto L_0x0397
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.RIGHT_TO_LEFT
            if (r13 != r1) goto L_0x0386
            java.lang.Object r1 = r7.get(r2)
            com.github.mikephil.charting.utils.FSize r1 = (com.github.mikephil.charting.utils.FSize) r1
            float r1 = r1.width
            goto L_0x038f
        L_0x0386:
            java.lang.Object r1 = r7.get(r2)
            com.github.mikephil.charting.utils.FSize r1 = (com.github.mikephil.charting.utils.FSize) r1
            float r1 = r1.width
            float r1 = -r1
        L_0x038f:
            r21 = 1073741824(0x40000000, float:2.0)
            float r1 = r1 / r21
            float r0 = r0 + r1
            int r2 = r2 + 1
            goto L_0x039e
        L_0x0397:
            r21 = 1073741824(0x40000000, float:2.0)
            goto L_0x039e
        L_0x039a:
            r4 = r31
            r21 = 1073741824(0x40000000, float:2.0)
        L_0x039e:
            r28 = r2
            java.lang.String r1 = r3.label
            if (r1 != 0) goto L_0x03a6
            r1 = 1
            goto L_0x03a7
        L_0x03a6:
            r1 = 0
        L_0x03a7:
            r30 = r1
            if (r19 == 0) goto L_0x03dd
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.RIGHT_TO_LEFT
            if (r13 != r1) goto L_0x03b1
            float r0 = r0 - r20
        L_0x03b1:
            r31 = r0
            float r33 = r26 + r29
            com.github.mikephil.charting.components.Legend r2 = r6.mLegend
            r0 = r39
            r1 = r40
            r34 = r2
            r2 = r31
            r35 = r3
            r3 = r33
            r37 = r4
            r36 = r7
            r7 = r40
            r4 = r35
            r38 = r10
            r10 = r5
            r5 = r34
            r0.drawForm(r1, r2, r3, r4, r5)
            com.github.mikephil.charting.components.Legend$LegendDirection r0 = com.github.mikephil.charting.components.Legend.LegendDirection.LEFT_TO_RIGHT
            if (r13 != r0) goto L_0x03da
            float r0 = r31 + r20
            goto L_0x03e8
        L_0x03da:
            r0 = r31
            goto L_0x03e8
        L_0x03dd:
            r35 = r3
            r37 = r4
            r36 = r7
            r38 = r10
            r7 = r40
            r10 = r5
        L_0x03e8:
            if (r30 != 0) goto L_0x0424
            if (r19 == 0) goto L_0x03f4
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.RIGHT_TO_LEFT
            if (r13 != r1) goto L_0x03f2
            float r1 = -r10
            goto L_0x03f3
        L_0x03f2:
            r1 = r10
        L_0x03f3:
            float r0 = r0 + r1
        L_0x03f4:
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.RIGHT_TO_LEFT
            if (r13 != r1) goto L_0x0401
            java.lang.Object r1 = r8.get(r15)
            com.github.mikephil.charting.utils.FSize r1 = (com.github.mikephil.charting.utils.FSize) r1
            float r1 = r1.width
            float r0 = r0 - r1
        L_0x0401:
            float r1 = r26 + r9
            r2 = r35
            java.lang.String r3 = r2.label
            r6.drawLabel(r7, r0, r1, r3)
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.LEFT_TO_RIGHT
            if (r13 != r1) goto L_0x0417
            java.lang.Object r1 = r8.get(r15)
            com.github.mikephil.charting.utils.FSize r1 = (com.github.mikephil.charting.utils.FSize) r1
            float r1 = r1.width
            float r0 = r0 + r1
        L_0x0417:
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.RIGHT_TO_LEFT
            if (r13 != r1) goto L_0x041f
            r3 = r27
            float r1 = -r3
            goto L_0x0422
        L_0x041f:
            r3 = r27
            r1 = r3
        L_0x0422:
            float r0 = r0 + r1
            goto L_0x0430
        L_0x0424:
            r3 = r27
            r2 = r35
            com.github.mikephil.charting.components.Legend$LegendDirection r1 = com.github.mikephil.charting.components.Legend.LegendDirection.RIGHT_TO_LEFT
            if (r13 != r1) goto L_0x042e
            float r1 = -r12
            goto L_0x042f
        L_0x042e:
            r1 = r12
        L_0x042f:
            float r0 = r0 + r1
        L_0x0430:
            int r15 = r15 + 1
            r27 = r3
            r4 = r7
            r5 = r10
            r1 = r26
            r2 = r28
            r7 = r36
            r31 = r37
            r10 = r38
            goto L_0x0327
        L_0x0442:
            r33 = r0
            r36 = r7
            r38 = r10
            r3 = r27
            r37 = r31
            r7 = r4
            r10 = r5
        L_0x044e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.github.mikephil.charting.renderer.LegendRenderer.renderLegend(android.graphics.Canvas):void");
    }

    /* access modifiers changed from: protected */
    public void drawForm(Canvas c, float x, float y, LegendEntry entry, Legend legend) {
        Canvas canvas = c;
        float f = x;
        float f2 = y;
        LegendEntry legendEntry = entry;
        if (legendEntry.formColor != 1122868 && legendEntry.formColor != 1122867 && legendEntry.formColor != 0) {
            int restoreCount = c.save();
            Legend.LegendForm form = legendEntry.form;
            if (form == Legend.LegendForm.DEFAULT) {
                form = legend.getForm();
            }
            Legend.LegendForm form2 = form;
            this.mLegendFormPaint.setColor(legendEntry.formColor);
            float formSize = Utils.convertDpToPixel(Float.isNaN(legendEntry.formSize) ? legend.getFormSize() : legendEntry.formSize);
            float half = formSize / 2.0f;
            switch (form2) {
                case DEFAULT:
                case CIRCLE:
                    this.mLegendFormPaint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(f + half, f2, half, this.mLegendFormPaint);
                    break;
                case SQUARE:
                    this.mLegendFormPaint.setStyle(Paint.Style.FILL);
                    c.drawRect(x, f2 - half, f + formSize, f2 + half, this.mLegendFormPaint);
                    break;
                case LINE:
                    float formLineWidth = Utils.convertDpToPixel(Float.isNaN(legendEntry.formLineWidth) ? legend.getFormLineWidth() : legendEntry.formLineWidth);
                    DashPathEffect formLineDashEffect = legendEntry.formLineDashEffect == null ? legend.getFormLineDashEffect() : legendEntry.formLineDashEffect;
                    this.mLegendFormPaint.setStyle(Paint.Style.STROKE);
                    this.mLegendFormPaint.setStrokeWidth(formLineWidth);
                    this.mLegendFormPaint.setPathEffect(formLineDashEffect);
                    this.mLineFormPath.reset();
                    this.mLineFormPath.moveTo(f, f2);
                    this.mLineFormPath.lineTo(f + formSize, f2);
                    canvas.drawPath(this.mLineFormPath, this.mLegendFormPaint);
                    break;
            }
            canvas.restoreToCount(restoreCount);
        }
    }

    /* access modifiers changed from: protected */
    public void drawLabel(Canvas c, float x, float y, String label) {
        c.drawText(label, x, y, this.mLegendLabelPaint);
    }
}
