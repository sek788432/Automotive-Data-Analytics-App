package com.github.mikephil.charting.jobs;

import android.view.View;
import com.github.mikephil.charting.utils.ObjectPool;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class MoveViewJob extends ViewPortJob {
    private static ObjectPool<MoveViewJob> pool = ObjectPool.create(2, new MoveViewJob((ViewPortHandler) null, 0.0f, 0.0f, (Transformer) null, (View) null));

    static {
        pool.setReplenishPercentage(0.5f);
    }

    public static MoveViewJob getInstance(ViewPortHandler viewPortHandler, float xValue, float yValue, Transformer trans, View v) {
        MoveViewJob result = pool.get();
        result.mViewPortHandler = viewPortHandler;
        result.xValue = xValue;
        result.yValue = yValue;
        result.mTrans = trans;
        result.view = v;
        return result;
    }

    public static void recycleInstance(MoveViewJob instance) {
        pool.recycle(instance);
    }

    public MoveViewJob(ViewPortHandler viewPortHandler, float xValue, float yValue, Transformer trans, View v) {
        super(viewPortHandler, xValue, yValue, trans, v);
    }

    public void run() {
        this.pts[0] = this.xValue;
        this.pts[1] = this.yValue;
        this.mTrans.pointValuesToPixel(this.pts);
        this.mViewPortHandler.centerViewPort(this.pts, this.view);
        recycleInstance(this);
    }

    /* access modifiers changed from: protected */
    public ObjectPool.Poolable instantiate() {
        return new MoveViewJob(this.mViewPortHandler, this.xValue, this.yValue, this.mTrans, this.view);
    }
}
