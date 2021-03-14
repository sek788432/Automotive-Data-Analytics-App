package com.github.mikephil.charting.jobs;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.View;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

@SuppressLint({"NewApi"})
public abstract class AnimatedViewPortJob extends ViewPortJob implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
    protected ObjectAnimator animator = ObjectAnimator.ofFloat(this, "phase", new float[]{0.0f, 1.0f});
    protected float phase;
    protected float xOrigin;
    protected float yOrigin;

    public abstract void recycleSelf();

    public AnimatedViewPortJob(ViewPortHandler viewPortHandler, float xValue, float yValue, Transformer trans, View v, float xOrigin2, float yOrigin2, long duration) {
        super(viewPortHandler, xValue, yValue, trans, v);
        this.xOrigin = xOrigin2;
        this.yOrigin = yOrigin2;
        this.animator.setDuration(duration);
        this.animator.addUpdateListener(this);
        this.animator.addListener(this);
    }

    @SuppressLint({"NewApi"})
    public void run() {
        this.animator.start();
    }

    public float getPhase() {
        return this.phase;
    }

    public void setPhase(float phase2) {
        this.phase = phase2;
    }

    public float getXOrigin() {
        return this.xOrigin;
    }

    public float getYOrigin() {
        return this.yOrigin;
    }

    /* access modifiers changed from: protected */
    public void resetAnimator() {
        this.animator.removeAllListeners();
        this.animator.removeAllUpdateListeners();
        this.animator.reverse();
        this.animator.addUpdateListener(this);
        this.animator.addListener(this);
    }

    public void onAnimationStart(Animator animation) {
    }

    public void onAnimationEnd(Animator animation) {
        try {
            recycleSelf();
        } catch (IllegalArgumentException e2) {
        }
    }

    public void onAnimationCancel(Animator animation) {
        try {
            recycleSelf();
        } catch (IllegalArgumentException e2) {
        }
    }

    public void onAnimationRepeat(Animator animation) {
    }

    public void onAnimationUpdate(ValueAnimator animation) {
    }
}
