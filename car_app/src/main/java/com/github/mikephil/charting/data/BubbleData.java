package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import java.util.List;

public class BubbleData extends BarLineScatterCandleBubbleData<IBubbleDataSet> {
    public BubbleData() {
    }

    public BubbleData(IBubbleDataSet... dataSets) {
        super((T[]) dataSets);
    }

    public BubbleData(List<IBubbleDataSet> dataSets) {
        super(dataSets);
    }

    public void setHighlightCircleWidth(float width) {
        for (IBubbleDataSet set : this.mDataSets) {
            set.setHighlightCircleWidth(width);
        }
    }
}
