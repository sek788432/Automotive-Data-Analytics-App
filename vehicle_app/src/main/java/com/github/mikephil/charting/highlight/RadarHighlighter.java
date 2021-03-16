package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import java.util.List;

public class RadarHighlighter extends PieRadarHighlighter<RadarChart> {
    public RadarHighlighter(RadarChart chart) {
        super(chart);
    }

    /* access modifiers changed from: protected */
    public Highlight getClosestHighlight(int index, float x, float y) {
        List<Highlight> highlights = getHighlightsAtIndex(index);
        float distanceToCenter = ((RadarChart) this.mChart).distanceToCenter(x, y) / ((RadarChart) this.mChart).getFactor();
        Highlight closest = null;
        float distance = Float.MAX_VALUE;
        for (int i = 0; i < highlights.size(); i++) {
            Highlight high = highlights.get(i);
            float cdistance = Math.abs(high.getY() - distanceToCenter);
            if (cdistance < distance) {
                closest = high;
                distance = cdistance;
            }
        }
        return closest;
    }

    /* access modifiers changed from: protected */
    public List<Highlight> getHighlightsAtIndex(int index) {
        int i = index;
        this.mHighlightBuffer.clear();
        float phaseX = ((RadarChart) this.mChart).getAnimator().getPhaseX();
        float phaseY = ((RadarChart) this.mChart).getAnimator().getPhaseY();
        float sliceangle = ((RadarChart) this.mChart).getSliceAngle();
        float factor = ((RadarChart) this.mChart).getFactor();
        MPPointF pOut = MPPointF.getInstance(0.0f, 0.0f);
        int i2 = 0;
        while (i2 < ((RadarData) ((RadarChart) this.mChart).getData()).getDataSetCount()) {
            IDataSet dataSetByIndex = ((RadarData) ((RadarChart) this.mChart).getData()).getDataSetByIndex(i2);
            Entry entry = dataSetByIndex.getEntryForIndex(i);
            Utils.getPosition(((RadarChart) this.mChart).getCenterOffsets(), (entry.getY() - ((RadarChart) this.mChart).getYChartMin()) * factor * phaseY, (((float) i) * sliceangle * phaseX) + ((RadarChart) this.mChart).getRotationAngle(), pOut);
            Highlight highlight = r8;
            float phaseX2 = phaseX;
            Highlight highlight2 = new Highlight((float) i, entry.getY(), pOut.x, pOut.y, i2, dataSetByIndex.getAxisDependency());
            this.mHighlightBuffer.add(highlight);
            i2++;
            phaseX = phaseX2;
            i = index;
        }
        return this.mHighlightBuffer;
    }
}
