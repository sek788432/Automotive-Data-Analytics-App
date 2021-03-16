package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.components.AxisBase;
import java.util.Collection;

public class IndexAxisValueFormatter implements IAxisValueFormatter {
    private int mValueCount = 0;
    private String[] mValues = new String[0];

    public IndexAxisValueFormatter() {
    }

    public IndexAxisValueFormatter(String[] values) {
        if (values != null) {
            setValues(values);
        }
    }

    public IndexAxisValueFormatter(Collection<String> values) {
        if (values != null) {
            setValues((String[]) values.toArray(new String[values.size()]));
        }
    }

    public String getFormattedValue(float value, AxisBase axis) {
        int index = Math.round(value);
        if (index < 0 || index >= this.mValueCount || index != ((int) value)) {
            return "";
        }
        return this.mValues[index];
    }

    public String[] getValues() {
        return this.mValues;
    }

    public void setValues(String[] values) {
        if (values == null) {
            values = new String[0];
        }
        this.mValues = values;
        this.mValueCount = values.length;
    }
}
