package com.github.mikephil.charting.interfaces.datasets;

import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import java.util.List;

public interface IDataSet<T extends Entry> {
    boolean addEntry(T t);

    void addEntryOrdered(T t);

    void calcMinMax();

    void calcMinMaxY(float f, float f2);

    void clear();

    boolean contains(T t);

    YAxis.AxisDependency getAxisDependency();

    int getColor();

    int getColor(int i);

    List<Integer> getColors();

    List<T> getEntriesForXValue(float f);

    int getEntryCount();

    T getEntryForIndex(int i);

    T getEntryForXValue(float f, float f2);

    T getEntryForXValue(float f, float f2, DataSet.Rounding rounding);

    int getEntryIndex(float f, float f2, DataSet.Rounding rounding);

    int getEntryIndex(T t);

    Legend.LegendForm getForm();

    DashPathEffect getFormLineDashEffect();

    float getFormLineWidth();

    float getFormSize();

    int getIndexInEntries(int i);

    String getLabel();

    IValueFormatter getValueFormatter();

    int getValueTextColor();

    int getValueTextColor(int i);

    float getValueTextSize();

    Typeface getValueTypeface();

    float getXMax();

    float getXMin();

    float getYMax();

    float getYMin();

    boolean isDrawValuesEnabled();

    boolean isHighlightEnabled();

    boolean isVisible();

    boolean needsFormatter();

    boolean removeEntry(int i);

    boolean removeEntry(T t);

    boolean removeEntryByXValue(float f);

    boolean removeFirst();

    boolean removeLast();

    void setAxisDependency(YAxis.AxisDependency axisDependency);

    void setDrawValues(boolean z);

    void setHighlightEnabled(boolean z);

    void setLabel(String str);

    void setValueFormatter(IValueFormatter iValueFormatter);

    void setValueTextColor(int i);

    void setValueTextColors(List<Integer> list);

    void setValueTextSize(float f);

    void setValueTypeface(Typeface typeface);

    void setVisible(boolean z);
}
