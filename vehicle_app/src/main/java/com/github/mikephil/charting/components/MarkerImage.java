package com.github.mikephil.charting.components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.MPPointF;
import java.lang.ref.WeakReference;

public class MarkerImage implements IMarker {
    private Context mContext;
    private Drawable mDrawable;
    private Rect mDrawableBoundsCache = new Rect();
    private MPPointF mOffset = new MPPointF();
    private MPPointF mOffset2 = new MPPointF();
    private FSize mSize = new FSize();
    private WeakReference<Chart> mWeakChart;

    public MarkerImage(Context context, int drawableResourceId) {
        this.mContext = context;
        if (Build.VERSION.SDK_INT >= 21) {
            this.mDrawable = this.mContext.getResources().getDrawable(drawableResourceId, (Resources.Theme) null);
        } else {
            this.mDrawable = this.mContext.getResources().getDrawable(drawableResourceId);
        }
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

    public void setSize(FSize size) {
        this.mSize = size;
        if (this.mSize == null) {
            this.mSize = new FSize();
        }
    }

    public FSize getSize() {
        return this.mSize;
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
        float width = this.mSize.width;
        float height = this.mSize.height;
        if (width == 0.0f && this.mDrawable != null) {
            width = (float) this.mDrawable.getIntrinsicWidth();
        }
        if (height == 0.0f && this.mDrawable != null) {
            height = (float) this.mDrawable.getIntrinsicHeight();
        }
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
    }

    public void draw(Canvas canvas, float posX, float posY) {
        if (this.mDrawable != null) {
            MPPointF offset = getOffsetForDrawingAtPoint(posX, posY);
            float width = this.mSize.width;
            float height = this.mSize.height;
            if (width == 0.0f && this.mDrawable != null) {
                width = (float) this.mDrawable.getIntrinsicWidth();
            }
            if (height == 0.0f && this.mDrawable != null) {
                height = (float) this.mDrawable.getIntrinsicHeight();
            }
            this.mDrawable.copyBounds(this.mDrawableBoundsCache);
            this.mDrawable.setBounds(this.mDrawableBoundsCache.left, this.mDrawableBoundsCache.top, this.mDrawableBoundsCache.left + ((int) width), this.mDrawableBoundsCache.top + ((int) height));
            int saveId = canvas.save();
            canvas.translate(offset.x + posX, offset.y + posY);
            this.mDrawable.draw(canvas);
            canvas.restoreToCount(saveId);
            this.mDrawable.setBounds(this.mDrawableBoundsCache);
        }
    }
}
