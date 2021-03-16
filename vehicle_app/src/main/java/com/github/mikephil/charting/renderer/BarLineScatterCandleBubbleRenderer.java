package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

public abstract class BarLineScatterCandleBubbleRenderer extends DataRenderer {
    protected XBounds mXBounds = new XBounds();

    public BarLineScatterCandleBubbleRenderer(ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
    }

    /* access modifiers changed from: protected */
    public boolean shouldDrawValues(IDataSet set) {
        return set.isVisible() && set.isDrawValuesEnabled();
    }

    /* access modifiers changed from: protected */
    public boolean isInBoundsX(Entry e2, IBarLineScatterCandleBubbleDataSet set) {
        if (e2 == null) {
            return false;
        }
        float entryIndex = (float) set.getEntryIndex(e2);
        if (e2 == null || entryIndex >= ((float) set.getEntryCount()) * this.mAnimator.getPhaseX()) {
            return false;
        }
        return true;
    }

    protected class XBounds {
        public int max;
        public int min;
        public int range;

        protected XBounds() {
        }

        public void set(BarLineScatterCandleBubbleDataProvider chart, IBarLineScatterCandleBubbleDataSet dataSet) {
            float phaseX = Math.max(0.0f, Math.min(1.0f, BarLineScatterCandleBubbleRenderer.this.mAnimator.getPhaseX()));
            float low = chart.getLowestVisibleX();
            float high = chart.getHighestVisibleX();
            Entry entryFrom = dataSet.getEntryForXValue(low, Float.NaN, DataSet.Rounding.DOWN);
            Entry entryTo = dataSet.getEntryForXValue(high, Float.NaN, DataSet.Rounding.UP);
            int i = 0;
            this.min = entryFrom == null ? 0 : dataSet.getEntryIndex(entryFrom);
            if (entryTo != null) {
                i = dataSet.getEntryIndex(entryTo);
            }
            this.max = i;
            this.range = (int) (((float) (this.max - this.min)) * phaseX);
        }
    }
}
