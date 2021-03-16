package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.text.DecimalFormat;

public class LargeValueFormatter implements IValueFormatter, IAxisValueFormatter {
    private static final int MAX_LENGTH = 5;
    private static String[] SUFFIX = {"", "k", "m", "b", "t"};
    private DecimalFormat mFormat;
    private String mText;

    public LargeValueFormatter() {
        this.mText = "";
        this.mFormat = new DecimalFormat("###E00");
    }

    public LargeValueFormatter(String appendix) {
        this();
        this.mText = appendix;
    }

    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return makePretty((double) value) + this.mText;
    }

    public String getFormattedValue(float value, AxisBase axis) {
        return makePretty((double) value) + this.mText;
    }

    public void setAppendix(String appendix) {
        this.mText = appendix;
    }

    public void setSuffix(String[] suff) {
        SUFFIX = suff;
    }

    private String makePretty(double number) {
        String r = this.mFormat.format(number);
        int numericValue1 = Character.getNumericValue(r.charAt(r.length() - 1));
        String r2 = r.replaceAll("E[0-9][0-9]", SUFFIX[Integer.valueOf(Character.getNumericValue(r.charAt(r.length() - 2)) + "" + numericValue1).intValue() / 3]);
        while (true) {
            if (r2.length() <= 5 && !r2.matches("[0-9]+\\.[a-z]")) {
                return r2;
            }
            r2 = r2.substring(0, r2.length() - 2) + r2.substring(r2.length() - 1);
        }
    }

    public int getDecimalDigits() {
        return 0;
    }
}
