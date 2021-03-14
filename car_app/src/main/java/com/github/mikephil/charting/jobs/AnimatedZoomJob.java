package com.github.mikephil.charting.jobs;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.view.View;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.utils.ObjectPool;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

@SuppressLint({"NewApi"})
public class AnimatedZoomJob extends AnimatedViewPortJob implements Animator.AnimatorListener {
    private static ObjectPool<AnimatedZoomJob> pool;
    protected Matrix mOnAnimationUpdateMatrixBuffer = new Matrix();
    protected float xAxisRange;
    protected YAxis yAxis;
    protected float zoomCenterX;
    protected float zoomCenterY;
    protected float zoomOriginX;
    protected float zoomOriginY;

    static {
        AnimatedZoomJob animatedZoomJob = r0;
        AnimatedZoomJob animatedZoomJob2 = new AnimatedZoomJob((ViewPortHandler) null, (View) null, (Transformer) null, (YAxis) null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0);
        pool = ObjectPool.create(8, animatedZoomJob);
    }

    public static AnimatedZoomJob getInstance(ViewPortHandler viewPortHandler, View v, Transformer trans, YAxis axis, float xAxisRange2, float scaleX, float scaleY, float xOrigin, float yOrigin, float zoomCenterX2, float zoomCenterY2, float zoomOriginX2, float zoomOriginY2, long duration) {
        AnimatedZoomJob result = pool.get();
        result.mViewPortHandler = viewPortHandler;
        result.xValue = scaleX;
        result.yValue = scaleY;
        result.mTrans = trans;
        result.view = v;
        result.xOrigin = xOrigin;
        result.yOrigin = yOrigin;
        result.resetAnimator();
        result.animator.setDuration(duration);
        return result;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    @SuppressLint({"NewApi"})
    public AnimatedZoomJob(ViewPortHandler viewPortHandler, View v, Transformer trans, YAxis axis, float xAxisRange2, float scaleX, float scaleY, float xOrigin, float yOrigin, float zoomCenterX2, float zoomCenterY2, float zoomOriginX2, float zoomOriginY2, long duration) {
        super(viewPortHandler, scaleX, scaleY, trans, v, xOrigin, yOrigin, duration);
        this.zoomCenterX = zoomCenterX2;
        this.zoomCenterY = zoomCenterY2;
        this.zoomOriginX = zoomOriginX2;
        this.zoomOriginY = zoomOriginY2;
        this.animator.addListener(this);
        this.yAxis = axis;
        this.xAxisRange = xAxisRange2;
    }

    public void onAnimationUpdate(ValueAnimator animation) {
        float scaleX = this.xOrigin + ((this.xValue - this.xOrigin) * this.phase);
        float scaleY = this.yOrigin + ((this.yValue - this.yOrigin) * this.phase);
        Matrix save = this.mOnAnimationUpdateMatrixBuffer;
        this.mViewPortHandler.setZoom(scaleX, scaleY, save);
        this.mViewPortHandler.refresh(save, this.view, false);
        float valsInView = this.yAxis.mAxisRange / this.mViewPortHandler.getScaleY();
        this.pts[0] = this.zoomOriginX + (((this.zoomCenterX - ((this.xAxisRange / this.mViewPortHandler.getScaleX()) / 2.0f)) - this.zoomOriginX) * this.phase);
        this.pts[1] = this.zoomOriginY + (((this.zoomCenterY + (valsInView / 2.0f)) - this.zoomOriginY) * this.phase);
        this.mTrans.pointValuesToPixel(this.pts);
        this.mViewPortHandler.translate(this.pts, save);
        this.mViewPortHandler.refresh(save, this.view, true);
    }

    public void onAnimationEnd(Animator animation) {
        ((BarLineChartBase) this.view).calculateOffsets();
        this.view.postInvalidate();
    }

    public void onAnimationCancel(Animator animation) {
    }

    public void onAnimationRepeat(Animator animation) {
    }

    public void recycleSelf() {
    }

    public void onAnimationStart(Animator animation) {
    }

    /* access modifiers changed from: protected */
    public ObjectPool.Poolable instantiate() {
        return new AnimatedZoomJob((ViewPortHandler) null, (View) null, (Transformer) null, (YAxis) null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0);
    }
}
