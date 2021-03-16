package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import java.util.List;

public class BarData extends BarLineScatterCandleBubbleData<IBarDataSet> {
    private float mBarWidth = 0.85f;

    public BarData() {
    }

    public BarData(IBarDataSet... dataSets) {
        super((T[]) dataSets);
    }

    public BarData(List<IBarDataSet> dataSets) {
        super(dataSets);
    }

    public void setBarWidth(float mBarWidth2) {
        this.mBarWidth = mBarWidth2;
    }

    public float getBarWidth() {
        return this.mBarWidth;
    }

    public void groupBars(float fromX, float groupSpace, float barSpace) {
        BarEntry entry;
        float f = groupSpace;
        float f2 = barSpace;
        if (this.mDataSets.size() > 1) {
            int maxEntryCount = ((IBarDataSet) getMaxEntryCountSet()).getEntryCount();
            float groupSpaceWidthHalf = f / 2.0f;
            float barSpaceHalf = f2 / 2.0f;
            float barWidthHalf = this.mBarWidth / 2.0f;
            float interval = getGroupWidth(f, f2);
            float fromX2 = fromX;
            for (int i = 0; i < maxEntryCount; i++) {
                float start = fromX2;
                float fromX3 = fromX2 + groupSpaceWidthHalf;
                for (IBarDataSet set : this.mDataSets) {
                    float fromX4 = fromX3 + barSpaceHalf + barWidthHalf;
                    if (i < set.getEntryCount() && (entry = (BarEntry) set.getEntryForIndex(i)) != null) {
                        entry.setX(fromX4);
                    }
                    fromX3 = fromX4 + barWidthHalf + barSpaceHalf;
                }
                fromX2 = fromX3 + groupSpaceWidthHalf;
                float diff = interval - (fromX2 - start);
                if (diff > 0.0f || diff < 0.0f) {
                    fromX2 += diff;
                }
            }
            notifyDataChanged();
            return;
        }
        throw new RuntimeException("BarData needs to hold at least 2 BarDataSets to allow grouping.");
    }

    public float getGroupWidth(float groupSpace, float barSpace) {
        return (((float) this.mDataSets.size()) * (this.mBarWidth + barSpace)) + groupSpace;
    }
}
