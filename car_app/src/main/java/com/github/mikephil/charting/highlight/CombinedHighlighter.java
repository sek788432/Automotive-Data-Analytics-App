package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.dataprovider.CombinedDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import java.util.List;

public class CombinedHighlighter extends ChartHighlighter<CombinedDataProvider> implements IHighlighter {
    protected BarHighlighter barHighlighter;

    public CombinedHighlighter(CombinedDataProvider chart, BarDataProvider barChart) {
        super(chart);
        this.barHighlighter = barChart.getBarData() == null ? null : new BarHighlighter(barChart);
    }

    /* access modifiers changed from: protected */
    public List<Highlight> getHighlightsAtXValue(float xVal, float x, float y) {
        this.mHighlightBuffer.clear();
        List<BarLineScatterCandleBubbleData> dataObjects = ((CombinedDataProvider) this.mChart).getCombinedData().getAllData();
        for (int i = 0; i < dataObjects.size(); i++) {
            ChartData dataObject = dataObjects.get(i);
            if (this.barHighlighter == null || !(dataObject instanceof BarData)) {
                int dataSetCount = dataObject.getDataSetCount();
                for (int j = 0; j < dataSetCount; j++) {
                    IDataSet dataSet = dataObjects.get(i).getDataSetByIndex(j);
                    if (dataSet.isHighlightEnabled()) {
                        for (Highlight high : buildHighlights(dataSet, j, xVal, DataSet.Rounding.CLOSEST)) {
                            high.setDataIndex(i);
                            this.mHighlightBuffer.add(high);
                        }
                    }
                }
            } else {
                Highlight high2 = this.barHighlighter.getHighlight(x, y);
                if (high2 != null) {
                    high2.setDataIndex(i);
                    this.mHighlightBuffer.add(high2);
                }
            }
        }
        return this.mHighlightBuffer;
    }
}
