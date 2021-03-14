package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;

public class PieHighlighter extends PieRadarHighlighter<PieChart> {
    public PieHighlighter(PieChart chart) {
        super(chart);
    }

    /* access modifiers changed from: protected */
    public Highlight getClosestHighlight(int index, float x, float y) {
        IPieDataSet set = ((PieData) ((PieChart) this.mChart).getData()).getDataSet();
        return new Highlight((float) index, set.getEntryForIndex(index).getY(), x, y, 0, set.getAxisDependency());
    }
}
