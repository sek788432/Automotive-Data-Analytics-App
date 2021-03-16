package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.components.AxisBase;
import java.text.DecimalFormat;

public class DefaultAxisValueFormatter implements IAxisValueFormatter {
    protected int digits = 0;
    protected DecimalFormat mFormat;

    public DefaultAxisValueFormatter(int digits2) {
        this.digits = digits2;
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < digits2; i++) {
            if (i == 0) {
                b.append(".");
            }
            b.append("0");
        }
        this.mFormat = new DecimalFormat("###,###,###,##0" + b.toString());
    }

    public String getFormattedValue(float value, AxisBase axis) {
        return this.mFormat.format((double) value);
    }

    public int getDecimalDigits() {
        return this.digits;
    }
}
