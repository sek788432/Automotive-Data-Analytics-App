package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.MPPointD;
import java.util.ArrayList;
import java.util.List;

public class ChartHighlighter<T extends BarLineScatterCandleBubbleDataProvider> implements IHighlighter {
    protected T mChart;
    protected List<Highlight> mHighlightBuffer = new ArrayList();

    public ChartHighlighter(T chart) {
        this.mChart = chart;
    }

    public Highlight getHighlight(float x, float y) {
        MPPointD pos = getValsForTouch(x, y);
        MPPointD.recycleInstance(pos);
        return getHighlightForX((float) pos.x, x, y);
    }

    /* access modifiers changed from: protected */
    public MPPointD getValsForTouch(float x, float y) {
        return this.mChart.getTransformer(YAxis.AxisDependency.LEFT).getValuesByTouchPoint(x, y);
    }

    /* access modifiers changed from: protected */
    public Highlight getHighlightForX(float xVal, float x, float y) {
        List<Highlight> closestValues = getHighlightsAtXValue(xVal, x, y);
        if (closestValues.isEmpty()) {
            return null;
        }
        return getClosestHighlightByPixel(closestValues, x, y, getMinimumDistance(closestValues, y, YAxis.AxisDependency.LEFT) < getMinimumDistance(closestValues, y, YAxis.AxisDependency.RIGHT) ? YAxis.AxisDependency.LEFT : YAxis.AxisDependency.RIGHT, this.mChart.getMaxHighlightDistance());
    }

    /* access modifiers changed from: protected */
    public float getMinimumDistance(List<Highlight> closestValues, float pos, YAxis.AxisDependency axis) {
        float distance = Float.MAX_VALUE;
        for (int i = 0; i < closestValues.size(); i++) {
            Highlight high = closestValues.get(i);
            if (high.getAxis() == axis) {
                float tempDistance = Math.abs(getHighlightPos(high) - pos);
                if (tempDistance < distance) {
                    distance = tempDistance;
                }
            }
        }
        return distance;
    }

    /* access modifiers changed from: protected */
    public float getHighlightPos(Highlight h) {
        return h.getYPx();
    }

    /* access modifiers changed from: protected */
    public List<Highlight> getHighlightsAtXValue(float xVal, float x, float y) {
        this.mHighlightBuffer.clear();
        BarLineScatterCandleBubbleData data = getData();
        if (data == null) {
            return this.mHighlightBuffer;
        }
        int dataSetCount = data.getDataSetCount();
        for (int i = 0; i < dataSetCount; i++) {
            IDataSet dataSet = data.getDataSetByIndex(i);
            if (dataSet.isHighlightEnabled()) {
                this.mHighlightBuffer.addAll(buildHighlights(dataSet, i, xVal, DataSet.Rounding.CLOSEST));
            }
        }
        return this.mHighlightBuffer;
    }

    /* access modifiers changed from: protected */
    public List<Highlight> buildHighlights(IDataSet set, int dataSetIndex, float xVal, DataSet.Rounding rounding) {
        IDataSet iDataSet = set;
        float f = xVal;
        ArrayList<Highlight> highlights = new ArrayList<>();
        List<Entry> entries = iDataSet.getEntriesForXValue(f);
        if (entries.size() == 0) {
            Entry closest = iDataSet.getEntryForXValue(f, Float.NaN, rounding);
            if (closest != null) {
                entries = iDataSet.getEntriesForXValue(closest.getX());
            }
        } else {
            DataSet.Rounding rounding2 = rounding;
        }
        if (entries.size() == 0) {
            return highlights;
        }
        for (Entry e2 : entries) {
            MPPointD pixels = this.mChart.getTransformer(set.getAxisDependency()).getPixelForValues(e2.getX(), e2.getY());
            Highlight highlight = r9;
            Highlight highlight2 = new Highlight(e2.getX(), e2.getY(), (float) pixels.x, (float) pixels.y, dataSetIndex, set.getAxisDependency());
            highlights.add(highlight);
            IDataSet iDataSet2 = set;
        }
        return highlights;
    }

    public Highlight getClosestHighlightByPixel(List<Highlight> closestValues, float x, float y, YAxis.AxisDependency axis, float minSelectionDistance) {
        Highlight closest = null;
        float distance = minSelectionDistance;
        for (int i = 0; i < closestValues.size(); i++) {
            Highlight high = closestValues.get(i);
            if (axis == null || high.getAxis() == axis) {
                float cDistance = getDistance(x, y, high.getXPx(), high.getYPx());
                if (cDistance < distance) {
                    closest = high;
                    distance = cDistance;
                }
            }
        }
        return closest;
    }

    /* access modifiers changed from: protected */
    public float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.hypot((double) (x1 - x2), (double) (y1 - y2));
    }

    /* access modifiers changed from: protected */
    public BarLineScatterCandleBubbleData getData() {
        return this.mChart.getData();
    }
}
