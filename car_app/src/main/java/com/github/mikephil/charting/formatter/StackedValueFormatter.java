package com.github.mikephil.charting.formatter;

import java.text.DecimalFormat;

public class StackedValueFormatter implements IValueFormatter {
    private String mAppendix;
    private boolean mDrawWholeStack;
    private DecimalFormat mFormat;

    public StackedValueFormatter(boolean drawWholeStack, String appendix, int decimals) {
        this.mDrawWholeStack = drawWholeStack;
        this.mAppendix = appendix;
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < decimals; i++) {
            if (i == 0) {
                b.append(".");
            }
            b.append("0");
        }
        this.mFormat = new DecimalFormat("###,###,###,##0" + b.toString());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0008, code lost:
        r0 = (com.github.mikephil.charting.data.BarEntry) r8;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getFormattedValue(float r7, com.github.mikephil.charting.data.Entry r8, int r9, com.github.mikephil.charting.utils.ViewPortHandler r10) {
        /*
            r6 = this;
            boolean r0 = r6.mDrawWholeStack
            if (r0 != 0) goto L_0x003a
            boolean r0 = r8 instanceof com.github.mikephil.charting.data.BarEntry
            if (r0 == 0) goto L_0x003a
            r0 = r8
            com.github.mikephil.charting.data.BarEntry r0 = (com.github.mikephil.charting.data.BarEntry) r0
            float[] r1 = r0.getYVals()
            if (r1 == 0) goto L_0x003a
            int r2 = r1.length
            int r2 = r2 + -1
            r2 = r1[r2]
            int r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r2 != 0) goto L_0x0037
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.text.DecimalFormat r3 = r6.mFormat
            float r4 = r0.getY()
            double r4 = (double) r4
            java.lang.String r3 = r3.format(r4)
            r2.append(r3)
            java.lang.String r3 = r6.mAppendix
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            return r2
        L_0x0037:
            java.lang.String r2 = ""
            return r2
        L_0x003a:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.text.DecimalFormat r1 = r6.mFormat
            double r2 = (double) r7
            java.lang.String r1 = r1.format(r2)
            r0.append(r1)
            java.lang.String r1 = r6.mAppendix
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.github.mikephil.charting.formatter.StackedValueFormatter.getFormattedValue(float, com.github.mikephil.charting.data.Entry, int, com.github.mikephil.charting.utils.ViewPortHandler):java.lang.String");
    }
}
