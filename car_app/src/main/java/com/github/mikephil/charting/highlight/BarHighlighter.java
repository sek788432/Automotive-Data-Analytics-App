package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.MPPointD;

public class BarHighlighter extends ChartHighlighter<BarDataProvider> {
    public BarHighlighter(BarDataProvider chart) {
        super(chart);
    }

    public Highlight getHighlight(float x, float y) {
        Highlight high = super.getHighlight(x, y);
        if (high == null) {
            return null;
        }
        MPPointD pos = getValsForTouch(x, y);
        IBarDataSet set = (IBarDataSet) ((BarDataProvider) this.mChart).getBarData().getDataSetByIndex(high.getDataSetIndex());
        if (set.isStacked()) {
            return getStackedHighlight(high, set, (float) pos.x, (float) pos.y);
        }
        MPPointD.recycleInstance(pos);
        return high;
    }

    public Highlight getStackedHighlight(Highlight high, IBarDataSet set, float xVal, float yVal) {
        BarEntry entry = (BarEntry) set.getEntryForXValue(xVal, yVal);
        if (entry == null) {
            return null;
        }
        if (entry.getYVals() == null) {
            return high;
        }
        Range[] ranges = entry.getRanges();
        if (ranges.length > 0) {
            int stackIndex = getClosestStackIndex(ranges, yVal);
            MPPointD pixels = ((BarDataProvider) this.mChart).getTransformer(set.getAxisDependency()).getPixelForValues(high.getX(), ranges[stackIndex].to);
            Highlight stackedHigh = new Highlight(entry.getX(), entry.getY(), (float) pixels.x, (float) pixels.y, high.getDataSetIndex(), stackIndex, high.getAxis());
            MPPointD.recycleInstance(pixels);
            return stackedHigh;
        }
        float f = yVal;
        return null;
    }

    /* access modifiers changed from: protected */
    public int getClosestStackIndex(Range[] ranges, float value) {
        if (ranges == null || ranges.length == 0) {
            return 0;
        }
        int stackIndex = 0;
        for (Range range : ranges) {
            if (range.contains(value)) {
                return stackIndex;
            }
            stackIndex++;
        }
        int length = Math.max(ranges.length - 1, 0);
        if (value > ranges[length].to) {
            return length;
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public float getDistance(float x1, float y1, float x2, float y2) {
        return Math.abs(x1 - x2);
    }

    /* access modifiers changed from: protected */
    public BarLineScatterCandleBubbleData getData() {
        return ((BarDataProvider) this.mChart).getBarData();
    }
}
