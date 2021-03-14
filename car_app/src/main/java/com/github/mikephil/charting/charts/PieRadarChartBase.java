package com.github.mikephil.charting.charts;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.PieRadarChartTouchListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

public abstract class PieRadarChartBase<T extends ChartData<? extends IDataSet<? extends Entry>>> extends Chart<T> {
    protected float mMinOffset = 0.0f;
    private float mRawRotationAngle = 270.0f;
    protected boolean mRotateEnabled = true;
    private float mRotationAngle = 270.0f;

    public abstract int getIndexForAngle(float f);

    public abstract float getRadius();

    /* access modifiers changed from: protected */
    public abstract float getRequiredBaseOffset();

    /* access modifiers changed from: protected */
    public abstract float getRequiredLegendOffset();

    public PieRadarChartBase(Context context) {
        super(context);
    }

    public PieRadarChartBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PieRadarChartBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* access modifiers changed from: protected */
    public void init() {
        super.init();
        this.mChartTouchListener = new PieRadarChartTouchListener(this);
    }

    /* access modifiers changed from: protected */
    public void calcMinMax() {
    }

    public int getMaxVisibleCount() {
        return this.mData.getEntryCount();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!this.mTouchEnabled || this.mChartTouchListener == null) {
            return super.onTouchEvent(event);
        }
        return this.mChartTouchListener.onTouch(this, event);
    }

    public void computeScroll() {
        if (this.mChartTouchListener instanceof PieRadarChartTouchListener) {
            ((PieRadarChartTouchListener) this.mChartTouchListener).computeScroll();
        }
    }

    public void notifyDataSetChanged() {
        if (this.mData != null) {
            calcMinMax();
            if (this.mLegend != null) {
                this.mLegendRenderer.computeLegend(this.mData);
            }
            calculateOffsets();
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x01a0, code lost:
        r1 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x01a2, code lost:
        r2 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x01a4, code lost:
        r3 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x01a7, code lost:
        r1 = r16;
        r2 = r17;
        r3 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x01ad, code lost:
        r1 = r1 + getRequiredBaseOffset();
        r2 = r2 + getRequiredBaseOffset();
        r4 = r4 + getRequiredBaseOffset();
        r16 = r1;
        r17 = r2;
        r18 = r3 + getRequiredBaseOffset();
     */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0137  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0153  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0198  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x019e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void calculateOffsets() {
        /*
            r19 = this;
            r0 = r19
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            com.github.mikephil.charting.components.Legend r5 = r0.mLegend
            if (r5 == 0) goto L_0x01c8
            com.github.mikephil.charting.components.Legend r5 = r0.mLegend
            boolean r5 = r5.isEnabled()
            if (r5 == 0) goto L_0x01c8
            com.github.mikephil.charting.components.Legend r5 = r0.mLegend
            boolean r5 = r5.isDrawInsideEnabled()
            if (r5 != 0) goto L_0x01c8
            com.github.mikephil.charting.components.Legend r5 = r0.mLegend
            float r5 = r5.mNeededWidth
            com.github.mikephil.charting.utils.ViewPortHandler r6 = r0.mViewPortHandler
            float r6 = r6.getChartWidth()
            com.github.mikephil.charting.components.Legend r7 = r0.mLegend
            float r7 = r7.getMaxSizePercent()
            float r6 = r6 * r7
            float r5 = java.lang.Math.min(r5, r6)
            int[] r6 = com.github.mikephil.charting.charts.PieRadarChartBase.AnonymousClass2.$SwitchMap$com$github$mikephil$charting$components$Legend$LegendOrientation
            com.github.mikephil.charting.components.Legend r7 = r0.mLegend
            com.github.mikephil.charting.components.Legend$LegendOrientation r7 = r7.getOrientation()
            int r7 = r7.ordinal()
            r6 = r6[r7]
            switch(r6) {
                case 1: goto L_0x0098;
                case 2: goto L_0x0048;
                default: goto L_0x0040;
            }
        L_0x0040:
            r16 = r1
            r17 = r2
            r18 = r3
            goto L_0x01a7
        L_0x0048:
            r6 = 0
            com.github.mikephil.charting.components.Legend r7 = r0.mLegend
            com.github.mikephil.charting.components.Legend$LegendVerticalAlignment r7 = r7.getVerticalAlignment()
            com.github.mikephil.charting.components.Legend$LegendVerticalAlignment r8 = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.TOP
            if (r7 == r8) goto L_0x0066
            com.github.mikephil.charting.components.Legend r7 = r0.mLegend
            com.github.mikephil.charting.components.Legend$LegendVerticalAlignment r7 = r7.getVerticalAlignment()
            com.github.mikephil.charting.components.Legend$LegendVerticalAlignment r8 = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
            if (r7 != r8) goto L_0x005e
            goto L_0x0066
        L_0x005e:
            r16 = r1
            r17 = r2
            r18 = r3
            goto L_0x01a7
        L_0x0066:
            float r7 = r19.getRequiredLegendOffset()
            com.github.mikephil.charting.components.Legend r8 = r0.mLegend
            float r8 = r8.mNeededHeight
            float r8 = r8 + r7
            com.github.mikephil.charting.utils.ViewPortHandler r9 = r0.mViewPortHandler
            float r9 = r9.getChartHeight()
            com.github.mikephil.charting.components.Legend r10 = r0.mLegend
            float r10 = r10.getMaxSizePercent()
            float r9 = r9 * r10
            float r6 = java.lang.Math.min(r8, r9)
            int[] r8 = com.github.mikephil.charting.charts.PieRadarChartBase.AnonymousClass2.$SwitchMap$com$github$mikephil$charting$components$Legend$LegendVerticalAlignment
            com.github.mikephil.charting.components.Legend r9 = r0.mLegend
            com.github.mikephil.charting.components.Legend$LegendVerticalAlignment r9 = r9.getVerticalAlignment()
            int r9 = r9.ordinal()
            r8 = r8[r9]
            switch(r8) {
                case 1: goto L_0x0095;
                case 2: goto L_0x0092;
                default: goto L_0x0091;
            }
        L_0x0091:
            goto L_0x005e
        L_0x0092:
            r3 = r6
            goto L_0x01ad
        L_0x0095:
            r4 = r6
            goto L_0x01ad
        L_0x0098:
            r6 = 0
            com.github.mikephil.charting.components.Legend r7 = r0.mLegend
            com.github.mikephil.charting.components.Legend$LegendHorizontalAlignment r7 = r7.getHorizontalAlignment()
            com.github.mikephil.charting.components.Legend$LegendHorizontalAlignment r8 = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.LEFT
            if (r7 == r8) goto L_0x00b6
            com.github.mikephil.charting.components.Legend r7 = r0.mLegend
            com.github.mikephil.charting.components.Legend$LegendHorizontalAlignment r7 = r7.getHorizontalAlignment()
            com.github.mikephil.charting.components.Legend$LegendHorizontalAlignment r8 = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.RIGHT
            if (r7 != r8) goto L_0x00ae
            goto L_0x00b6
        L_0x00ae:
            r16 = r1
            r17 = r2
            r18 = r3
            goto L_0x0141
        L_0x00b6:
            com.github.mikephil.charting.components.Legend r7 = r0.mLegend
            com.github.mikephil.charting.components.Legend$LegendVerticalAlignment r7 = r7.getVerticalAlignment()
            com.github.mikephil.charting.components.Legend$LegendVerticalAlignment r8 = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.CENTER
            if (r7 != r8) goto L_0x00c9
            r7 = 1095761920(0x41500000, float:13.0)
            float r7 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r7)
            float r6 = r5 + r7
            goto L_0x00ae
        L_0x00c9:
            r7 = 1090519040(0x41000000, float:8.0)
            float r7 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r7)
            float r8 = r5 + r7
            com.github.mikephil.charting.components.Legend r9 = r0.mLegend
            float r9 = r9.mNeededHeight
            com.github.mikephil.charting.components.Legend r10 = r0.mLegend
            float r10 = r10.mTextHeightMax
            float r9 = r9 + r10
            com.github.mikephil.charting.utils.MPPointF r10 = r19.getCenter()
            com.github.mikephil.charting.components.Legend r11 = r0.mLegend
            com.github.mikephil.charting.components.Legend$LegendHorizontalAlignment r11 = r11.getHorizontalAlignment()
            com.github.mikephil.charting.components.Legend$LegendHorizontalAlignment r12 = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.RIGHT
            r13 = 1097859072(0x41700000, float:15.0)
            if (r11 != r12) goto L_0x00f2
            int r11 = r19.getWidth()
            float r11 = (float) r11
            float r11 = r11 - r8
            float r11 = r11 + r13
            goto L_0x00f4
        L_0x00f2:
            float r11 = r8 - r13
        L_0x00f4:
            float r13 = r13 + r9
            float r12 = r0.distanceToCenter(r11, r13)
            float r14 = r19.getRadius()
            float r15 = r0.getAngleForPoint(r11, r13)
            com.github.mikephil.charting.utils.MPPointF r14 = r0.getPosition(r10, r14, r15)
            float r15 = r14.x
            r16 = r1
            float r1 = r14.y
            float r1 = r0.distanceToCenter(r15, r1)
            r15 = 1084227584(0x40a00000, float:5.0)
            float r15 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r15)
            r17 = r2
            float r2 = r10.y
            int r2 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1))
            if (r2 < 0) goto L_0x0131
            int r2 = r19.getHeight()
            float r2 = (float) r2
            float r2 = r2 - r8
            r18 = r3
            int r3 = r19.getWidth()
            float r3 = (float) r3
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x0133
            r2 = r8
        L_0x012f:
            r6 = r2
            goto L_0x013b
        L_0x0131:
            r18 = r3
        L_0x0133:
            int r2 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x013b
            float r2 = r1 - r12
            float r2 = r2 + r15
            goto L_0x012f
        L_0x013b:
            com.github.mikephil.charting.utils.MPPointF.recycleInstance(r10)
            com.github.mikephil.charting.utils.MPPointF.recycleInstance(r14)
        L_0x0141:
            int[] r1 = com.github.mikephil.charting.charts.PieRadarChartBase.AnonymousClass2.$SwitchMap$com$github$mikephil$charting$components$Legend$LegendHorizontalAlignment
            com.github.mikephil.charting.components.Legend r2 = r0.mLegend
            com.github.mikephil.charting.components.Legend$LegendHorizontalAlignment r2 = r2.getHorizontalAlignment()
            int r2 = r2.ordinal()
            r1 = r1[r2]
            switch(r1) {
                case 1: goto L_0x019e;
                case 2: goto L_0x0198;
                case 3: goto L_0x0153;
                default: goto L_0x0152;
            }
        L_0x0152:
            goto L_0x01a0
        L_0x0153:
            int[] r1 = com.github.mikephil.charting.charts.PieRadarChartBase.AnonymousClass2.$SwitchMap$com$github$mikephil$charting$components$Legend$LegendVerticalAlignment
            com.github.mikephil.charting.components.Legend r2 = r0.mLegend
            com.github.mikephil.charting.components.Legend$LegendVerticalAlignment r2 = r2.getVerticalAlignment()
            int r2 = r2.ordinal()
            r1 = r1[r2]
            switch(r1) {
                case 1: goto L_0x0180;
                case 2: goto L_0x0165;
                default: goto L_0x0164;
            }
        L_0x0164:
            goto L_0x01a0
        L_0x0165:
            com.github.mikephil.charting.components.Legend r1 = r0.mLegend
            float r1 = r1.mNeededHeight
            com.github.mikephil.charting.utils.ViewPortHandler r2 = r0.mViewPortHandler
            float r2 = r2.getChartHeight()
            com.github.mikephil.charting.components.Legend r3 = r0.mLegend
            float r3 = r3.getMaxSizePercent()
            float r2 = r2 * r3
            float r1 = java.lang.Math.min(r1, r2)
            r3 = r1
            r1 = r16
            r2 = r17
            goto L_0x01a6
        L_0x0180:
            com.github.mikephil.charting.components.Legend r1 = r0.mLegend
            float r1 = r1.mNeededHeight
            com.github.mikephil.charting.utils.ViewPortHandler r2 = r0.mViewPortHandler
            float r2 = r2.getChartHeight()
            com.github.mikephil.charting.components.Legend r3 = r0.mLegend
            float r3 = r3.getMaxSizePercent()
            float r2 = r2 * r3
            float r1 = java.lang.Math.min(r1, r2)
            r4 = r1
            goto L_0x01a0
        L_0x0198:
            r1 = r6
            r2 = r1
            r1 = r16
            goto L_0x01a4
        L_0x019e:
            r1 = r6
            goto L_0x01a2
        L_0x01a0:
            r1 = r16
        L_0x01a2:
            r2 = r17
        L_0x01a4:
            r3 = r18
        L_0x01a6:
            goto L_0x01ad
        L_0x01a7:
            r1 = r16
            r2 = r17
            r3 = r18
        L_0x01ad:
            float r6 = r19.getRequiredBaseOffset()
            float r1 = r1 + r6
            float r6 = r19.getRequiredBaseOffset()
            float r2 = r2 + r6
            float r6 = r19.getRequiredBaseOffset()
            float r4 = r4 + r6
            float r6 = r19.getRequiredBaseOffset()
            float r3 = r3 + r6
            r16 = r1
            r17 = r2
            r18 = r3
            goto L_0x01ce
        L_0x01c8:
            r16 = r1
            r17 = r2
            r18 = r3
        L_0x01ce:
            float r1 = r0.mMinOffset
            float r1 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r1)
            boolean r2 = r0 instanceof com.github.mikephil.charting.charts.RadarChart
            if (r2 == 0) goto L_0x01ef
            com.github.mikephil.charting.components.XAxis r2 = r19.getXAxis()
            boolean r3 = r2.isEnabled()
            if (r3 == 0) goto L_0x01ef
            boolean r3 = r2.isDrawLabelsEnabled()
            if (r3 == 0) goto L_0x01ef
            int r3 = r2.mLabelRotatedWidth
            float r3 = (float) r3
            float r1 = java.lang.Math.max(r1, r3)
        L_0x01ef:
            float r2 = r19.getExtraTopOffset()
            float r4 = r4 + r2
            float r2 = r19.getExtraRightOffset()
            float r2 = r17 + r2
            float r3 = r19.getExtraBottomOffset()
            float r3 = r18 + r3
            float r5 = r19.getExtraLeftOffset()
            float r5 = r16 + r5
            float r6 = java.lang.Math.max(r1, r5)
            float r7 = java.lang.Math.max(r1, r4)
            float r8 = java.lang.Math.max(r1, r2)
            float r9 = r19.getRequiredBaseOffset()
            float r9 = java.lang.Math.max(r9, r3)
            float r9 = java.lang.Math.max(r1, r9)
            com.github.mikephil.charting.utils.ViewPortHandler r10 = r0.mViewPortHandler
            r10.restrainViewPort(r6, r7, r8, r9)
            boolean r10 = r0.mLogEnabled
            if (r10 == 0) goto L_0x0255
            java.lang.String r10 = "MPAndroidChart"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "offsetLeft: "
            r11.append(r12)
            r11.append(r6)
            java.lang.String r12 = ", offsetTop: "
            r11.append(r12)
            r11.append(r7)
            java.lang.String r12 = ", offsetRight: "
            r11.append(r12)
            r11.append(r8)
            java.lang.String r12 = ", offsetBottom: "
            r11.append(r12)
            r11.append(r9)
            java.lang.String r11 = r11.toString()
            android.util.Log.i(r10, r11)
        L_0x0255:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.github.mikephil.charting.charts.PieRadarChartBase.calculateOffsets():void");
    }

    public float getAngleForPoint(float x, float y) {
        MPPointF c = getCenterOffsets();
        double tx = (double) (x - c.x);
        double ty = (double) (y - c.y);
        float angle = (float) Math.toDegrees(Math.acos(ty / Math.sqrt((tx * tx) + (ty * ty))));
        if (x > c.x) {
            angle = 360.0f - angle;
        }
        float angle2 = angle + 90.0f;
        if (angle2 > 360.0f) {
            angle2 -= 360.0f;
        }
        MPPointF.recycleInstance(c);
        return angle2;
    }

    public MPPointF getPosition(MPPointF center, float dist, float angle) {
        MPPointF p = MPPointF.getInstance(0.0f, 0.0f);
        getPosition(center, dist, angle, p);
        return p;
    }

    public void getPosition(MPPointF center, float dist, float angle, MPPointF outputPoint) {
        outputPoint.x = (float) (((double) center.x) + (((double) dist) * Math.cos(Math.toRadians((double) angle))));
        outputPoint.y = (float) (((double) center.y) + (((double) dist) * Math.sin(Math.toRadians((double) angle))));
    }

    public float distanceToCenter(float x, float y) {
        float xDist;
        float yDist;
        MPPointF c = getCenterOffsets();
        if (x > c.x) {
            xDist = x - c.x;
        } else {
            xDist = c.x - x;
        }
        if (y > c.y) {
            yDist = y - c.y;
        } else {
            yDist = c.y - y;
        }
        float dist = (float) Math.sqrt(Math.pow((double) xDist, 2.0d) + Math.pow((double) yDist, 2.0d));
        MPPointF.recycleInstance(c);
        return dist;
    }

    public void setRotationAngle(float angle) {
        this.mRawRotationAngle = angle;
        this.mRotationAngle = Utils.getNormalizedAngle(this.mRawRotationAngle);
    }

    public float getRawRotationAngle() {
        return this.mRawRotationAngle;
    }

    public float getRotationAngle() {
        return this.mRotationAngle;
    }

    public void setRotationEnabled(boolean enabled) {
        this.mRotateEnabled = enabled;
    }

    public boolean isRotationEnabled() {
        return this.mRotateEnabled;
    }

    public float getMinOffset() {
        return this.mMinOffset;
    }

    public void setMinOffset(float minOffset) {
        this.mMinOffset = minOffset;
    }

    public float getDiameter() {
        RectF content = this.mViewPortHandler.getContentRect();
        content.left += getExtraLeftOffset();
        content.top += getExtraTopOffset();
        content.right -= getExtraRightOffset();
        content.bottom -= getExtraBottomOffset();
        return Math.min(content.width(), content.height());
    }

    public float getYChartMax() {
        return 0.0f;
    }

    public float getYChartMin() {
        return 0.0f;
    }

    @SuppressLint({"NewApi"})
    public void spin(int durationmillis, float fromangle, float toangle, Easing.EasingOption easing) {
        if (Build.VERSION.SDK_INT >= 11) {
            setRotationAngle(fromangle);
            ObjectAnimator spinAnimator = ObjectAnimator.ofFloat(this, "rotationAngle", new float[]{fromangle, toangle});
            spinAnimator.setDuration((long) durationmillis);
            spinAnimator.setInterpolator(Easing.getEasingFunctionFromOption(easing));
            spinAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    PieRadarChartBase.this.postInvalidate();
                }
            });
            spinAnimator.start();
        }
    }
}
