package com.github.mikephil.charting.jobs;

import android.graphics.Matrix;
import android.view.View;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.utils.ObjectPool;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class ZoomJob extends ViewPortJob {
    private static ObjectPool<ZoomJob> pool = ObjectPool.create(1, new ZoomJob((ViewPortHandler) null, 0.0f, 0.0f, 0.0f, 0.0f, (Transformer) null, (YAxis.AxisDependency) null, (View) null));
    protected YAxis.AxisDependency axisDependency;
    protected Matrix mRunMatrixBuffer = new Matrix();
    protected float scaleX;
    protected float scaleY;

    static {
        pool.setReplenishPercentage(0.5f);
    }

    public static ZoomJob getInstance(ViewPortHandler viewPortHandler, float scaleX2, float scaleY2, float xValue, float yValue, Transformer trans, YAxis.AxisDependency axis, View v) {
        ZoomJob result = pool.get();
        result.xValue = xValue;
        result.yValue = yValue;
        result.scaleX = scaleX2;
        result.scaleY = scaleY2;
        result.mViewPortHandler = viewPortHandler;
        result.mTrans = trans;
        result.axisDependency = axis;
        result.view = v;
        return result;
    }

    public static void recycleInstance(ZoomJob instance) {
        pool.recycle(instance);
    }

    public ZoomJob(ViewPortHandler viewPortHandler, float scaleX2, float scaleY2, float xValue, float yValue, Transformer trans, YAxis.AxisDependency axis, View v) {
        super(viewPortHandler, xValue, yValue, trans, v);
        this.scaleX = scaleX2;
        this.scaleY = scaleY2;
        this.axisDependency = axis;
    }

    public void run() {
        Matrix save = this.mRunMatrixBuffer;
        this.mViewPortHandler.zoom(this.scaleX, this.scaleY, save);
        this.mViewPortHandler.refresh(save, this.view, false);
        float yValsInView = ((BarLineChartBase) this.view).getAxis(this.axisDependency).mAxisRange / this.mViewPortHandler.getScaleY();
        this.pts[0] = this.xValue - ((((BarLineChartBase) this.view).getXAxis().mAxisRange / this.mViewPortHandler.getScaleX()) / 2.0f);
        this.pts[1] = this.yValue + (yValsInView / 2.0f);
        this.mTrans.pointValuesToPixel(this.pts);
        this.mViewPortHandler.translate(this.pts, save);
        this.mViewPortHandler.refresh(save, this.view, false);
        ((BarLineChartBase) this.view).calculateOffsets();
        this.view.postInvalidate();
        recycleInstance(this);
    }

    /* access modifiers changed from: protected */
    public ObjectPool.Poolable instantiate() {
        return new ZoomJob((ViewPortHandler) null, 0.0f, 0.0f, 0.0f, 0.0f, (Transformer) null, (YAxis.AxisDependency) null, (View) null);
    }
}
