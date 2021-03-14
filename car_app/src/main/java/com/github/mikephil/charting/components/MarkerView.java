package com.github.mikephil.charting.components;

import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import java.lang.ref.WeakReference;

public class MarkerView extends RelativeLayout implements IMarker {
    private MPPointF mOffset = new MPPointF();
    private MPPointF mOffset2 = new MPPointF();
    private WeakReference<Chart> mWeakChart;

    public MarkerView(Context context, int layoutResource) {
        super(context);
        setupLayoutResource(layoutResource);
    }

    private void setupLayoutResource(int layoutResource) {
        View inflated = LayoutInflater.from(getContext()).inflate(layoutResource, this);
        inflated.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
        inflated.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        inflated.layout(0, 0, inflated.getMeasuredWidth(), inflated.getMeasuredHeight());
    }

    public void setOffset(MPPointF offset) {
        this.mOffset = offset;
        if (this.mOffset == null) {
            this.mOffset = new MPPointF();
        }
    }

    public void setOffset(float offsetX, float offsetY) {
        this.mOffset.x = offsetX;
        this.mOffset.y = offsetY;
    }

    public MPPointF getOffset() {
        return this.mOffset;
    }

    public void setChartView(Chart chart) {
        this.mWeakChart = new WeakReference<>(chart);
    }

    public Chart getChartView() {
        if (this.mWeakChart == null) {
            return null;
        }
        return (Chart) this.mWeakChart.get();
    }

    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
        MPPointF offset = getOffset();
        this.mOffset2.x = offset.x;
        this.mOffset2.y = offset.y;
        Chart chart = getChartView();
        float width = (float) getWidth();
        float height = (float) getHeight();
        if (this.mOffset2.x + posX < 0.0f) {
            this.mOffset2.x = -posX;
        } else if (chart != null && posX + width + this.mOffset2.x > ((float) chart.getWidth())) {
            this.mOffset2.x = (((float) chart.getWidth()) - posX) - width;
        }
        if (this.mOffset2.y + posY < 0.0f) {
            this.mOffset2.y = -posY;
        } else if (chart != null && posY + height + this.mOffset2.y > ((float) chart.getHeight())) {
            this.mOffset2.y = (((float) chart.getHeight()) - posY) - height;
        }
        return this.mOffset2;
    }

    public void refreshContent(Entry e2, Highlight highlight) {
        measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    public void draw(Canvas canvas, float posX, float posY) {
        MPPointF offset = getOffsetForDrawingAtPoint(posX, posY);
        int saveId = canvas.save();
        canvas.translate(offset.x + posX, offset.y + posY);
        draw(canvas);
        canvas.restoreToCount(saveId);
    }
}
