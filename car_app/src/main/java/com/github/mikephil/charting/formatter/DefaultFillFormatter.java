package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class DefaultFillFormatter implements IFillFormatter {
    public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
        float max;
        float min;
        float chartMaxY = dataProvider.getYChartMax();
        float chartMinY = dataProvider.getYChartMin();
        LineData data = dataProvider.getLineData();
        if (dataSet.getYMax() > 0.0f && dataSet.getYMin() < 0.0f) {
            return 0.0f;
        }
        if (data.getYMax() > 0.0f) {
            max = 0.0f;
        } else {
            max = chartMaxY;
        }
        if (data.getYMin() < 0.0f) {
            min = 0.0f;
        } else {
            min = chartMinY;
        }
        return dataSet.getYMin() >= 0.0f ? min : max;
    }
}
