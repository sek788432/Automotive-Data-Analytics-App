package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.util.Log;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.ScatterDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.renderer.scatter.IShapeRenderer;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.List;

public class ScatterChartRenderer extends LineScatterCandleRadarRenderer {
    protected ScatterDataProvider mChart;
    float[] mPixelBuffer = new float[2];

    public ScatterChartRenderer(ScatterDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;
    }

    public void initBuffers() {
    }

    public void drawData(Canvas c) {
        for (IScatterDataSet set : this.mChart.getScatterData().getDataSets()) {
            if (set.isVisible()) {
                drawDataSet(c, set);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawDataSet(Canvas c, IScatterDataSet dataSet) {
        IScatterDataSet iScatterDataSet = dataSet;
        ViewPortHandler viewPortHandler = this.mViewPortHandler;
        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
        float phaseY = this.mAnimator.getPhaseY();
        IShapeRenderer renderer = dataSet.getShapeRenderer();
        if (renderer == null) {
            Log.i("MISSING", "There's no IShapeRenderer specified for ScatterDataSet");
            return;
        }
        int max = (int) Math.min(Math.ceil((double) (((float) dataSet.getEntryCount()) * this.mAnimator.getPhaseX())), (double) ((float) dataSet.getEntryCount()));
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < max) {
                Entry e2 = iScatterDataSet.getEntryForIndex(i2);
                this.mPixelBuffer[0] = e2.getX();
                this.mPixelBuffer[1] = e2.getY() * phaseY;
                trans.pointValuesToPixel(this.mPixelBuffer);
                if (viewPortHandler.isInBoundsRight(this.mPixelBuffer[0])) {
                    if (viewPortHandler.isInBoundsLeft(this.mPixelBuffer[0]) && viewPortHandler.isInBoundsY(this.mPixelBuffer[1])) {
                        this.mRenderPaint.setColor(iScatterDataSet.getColor(i2 / 2));
                        renderer.renderShape(c, dataSet, this.mViewPortHandler, this.mPixelBuffer[0], this.mPixelBuffer[1], this.mRenderPaint);
                    }
                    i = i2 + 1;
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    public void drawValues(Canvas c) {
        int j;
        if (isDrawingValuesAllowed(this.mChart)) {
            List<IScatterDataSet> dataSets = this.mChart.getScatterData().getDataSets();
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 < this.mChart.getScatterData().getDataSetCount()) {
                    IScatterDataSet dataSet = dataSets.get(i2);
                    if (shouldDrawValues(dataSet)) {
                        applyValueTextStyle(dataSet);
                        this.mXBounds.set(this.mChart, dataSet);
                        float[] positions = this.mChart.getTransformer(dataSet.getAxisDependency()).generateTransformedValuesScatter(dataSet, this.mAnimator.getPhaseX(), this.mAnimator.getPhaseY(), this.mXBounds.min, this.mXBounds.max);
                        float shapeSize = Utils.convertDpToPixel(dataSet.getScatterShapeSize());
                        int j2 = 0;
                        while (true) {
                            int j3 = j2;
                            if (j3 >= positions.length || !this.mViewPortHandler.isInBoundsRight(positions[j3])) {
                                break;
                            }
                            if (!this.mViewPortHandler.isInBoundsLeft(positions[j3])) {
                                j = j3;
                            } else if (!this.mViewPortHandler.isInBoundsY(positions[j3 + 1])) {
                                j = j3;
                            } else {
                                Entry entry = dataSet.getEntryForIndex((j3 / 2) + this.mXBounds.min);
                                j = j3;
                                drawValue(c, dataSet.getValueFormatter(), entry.getY(), entry, i2, positions[j3], positions[j3 + 1] - shapeSize, dataSet.getValueTextColor((j3 / 2) + this.mXBounds.min));
                            }
                            j2 = j + 2;
                        }
                    }
                    i = i2 + 1;
                } else {
                    return;
                }
            }
        }
    }

    public void drawExtras(Canvas c) {
    }

    public void drawHighlighted(Canvas c, Highlight[] indices) {
        ScatterData scatterData = this.mChart.getScatterData();
        for (Highlight high : indices) {
            IScatterDataSet set = (IScatterDataSet) scatterData.getDataSetByIndex(high.getDataSetIndex());
            if (set != null && set.isHighlightEnabled()) {
                Entry e2 = set.getEntryForXValue(high.getX(), high.getY());
                if (isInBoundsX(e2, set)) {
                    MPPointD pix = this.mChart.getTransformer(set.getAxisDependency()).getPixelForValues(e2.getX(), e2.getY() * this.mAnimator.getPhaseY());
                    high.setDraw((float) pix.x, (float) pix.y);
                    drawHighlightLines(c, (float) pix.x, (float) pix.y, set);
                }
            }
        }
    }
}
