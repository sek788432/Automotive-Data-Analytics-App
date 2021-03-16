package com.github.mikephil.charting.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.lang.ref.WeakReference;
import java.util.List;

public class PieChartRenderer extends DataRenderer {
    protected Canvas mBitmapCanvas;
    private RectF mCenterTextLastBounds = new RectF();
    private CharSequence mCenterTextLastValue;
    private StaticLayout mCenterTextLayout;
    private TextPaint mCenterTextPaint;
    protected PieChart mChart;
    protected WeakReference<Bitmap> mDrawBitmap;
    protected Path mDrawCenterTextPathBuffer = new Path();
    protected RectF mDrawHighlightedRectF = new RectF();
    private Paint mEntryLabelsPaint;
    private Path mHoleCirclePath = new Path();
    protected Paint mHolePaint;
    private RectF mInnerRectBuffer = new RectF();
    private Path mPathBuffer = new Path();
    private RectF[] mRectBuffer = {new RectF(), new RectF(), new RectF()};
    protected Paint mTransparentCirclePaint;
    protected Paint mValueLinePaint;

    public PieChartRenderer(PieChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;
        this.mHolePaint = new Paint(1);
        this.mHolePaint.setColor(-1);
        this.mHolePaint.setStyle(Paint.Style.FILL);
        this.mTransparentCirclePaint = new Paint(1);
        this.mTransparentCirclePaint.setColor(-1);
        this.mTransparentCirclePaint.setStyle(Paint.Style.FILL);
        this.mTransparentCirclePaint.setAlpha(105);
        this.mCenterTextPaint = new TextPaint(1);
        this.mCenterTextPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.mCenterTextPaint.setTextSize(Utils.convertDpToPixel(12.0f));
        this.mValuePaint.setTextSize(Utils.convertDpToPixel(13.0f));
        this.mValuePaint.setColor(-1);
        this.mValuePaint.setTextAlign(Paint.Align.CENTER);
        this.mEntryLabelsPaint = new Paint(1);
        this.mEntryLabelsPaint.setColor(-1);
        this.mEntryLabelsPaint.setTextAlign(Paint.Align.CENTER);
        this.mEntryLabelsPaint.setTextSize(Utils.convertDpToPixel(13.0f));
        this.mValueLinePaint = new Paint(1);
        this.mValueLinePaint.setStyle(Paint.Style.STROKE);
    }

    public Paint getPaintHole() {
        return this.mHolePaint;
    }

    public Paint getPaintTransparentCircle() {
        return this.mTransparentCirclePaint;
    }

    public TextPaint getPaintCenterText() {
        return this.mCenterTextPaint;
    }

    public Paint getPaintEntryLabels() {
        return this.mEntryLabelsPaint;
    }

    public void initBuffers() {
    }

    public void drawData(Canvas c) {
        int width = (int) this.mViewPortHandler.getChartWidth();
        int height = (int) this.mViewPortHandler.getChartHeight();
        if (!(this.mDrawBitmap != null && ((Bitmap) this.mDrawBitmap.get()).getWidth() == width && ((Bitmap) this.mDrawBitmap.get()).getHeight() == height)) {
            if (width > 0 && height > 0) {
                this.mDrawBitmap = new WeakReference<>(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444));
                this.mBitmapCanvas = new Canvas((Bitmap) this.mDrawBitmap.get());
            } else {
                return;
            }
        }
        ((Bitmap) this.mDrawBitmap.get()).eraseColor(0);
        for (IPieDataSet set : ((PieData) this.mChart.getData()).getDataSets()) {
            if (set.isVisible() && set.getEntryCount() > 0) {
                drawDataSet(c, set);
            }
        }
    }

    /* access modifiers changed from: protected */
    public float calculateMinimumRadiusForSpacedSlice(MPPointF center, float radius, float angle, float arcStartPointX, float arcStartPointY, float startAngle, float sweepAngle) {
        MPPointF mPPointF = center;
        float angleMiddle = startAngle + (sweepAngle / 2.0f);
        float arcEndPointX = mPPointF.x + (((float) Math.cos((double) ((startAngle + sweepAngle) * 0.017453292f))) * radius);
        float arcEndPointY = mPPointF.y + (((float) Math.sin((double) ((startAngle + sweepAngle) * 0.017453292f))) * radius);
        float arcMidPointX = mPPointF.x + (((float) Math.cos((double) (angleMiddle * 0.017453292f))) * radius);
        float arcMidPointY = mPPointF.y + (((float) Math.sin((double) (0.017453292f * angleMiddle))) * radius);
        float f = angleMiddle;
        double basePointsDistance = Math.sqrt(Math.pow((double) (arcEndPointX - arcStartPointX), 2.0d) + Math.pow((double) (arcEndPointY - arcStartPointY), 2.0d));
        double d = basePointsDistance;
        return (float) (((double) (radius - ((float) ((basePointsDistance / 2.0d) * Math.tan(((180.0d - ((double) angle)) / 2.0d) * 0.017453292519943295d))))) - Math.sqrt(Math.pow((double) (arcMidPointX - ((arcEndPointX + arcStartPointX) / 2.0f)), 2.0d) + Math.pow((double) (arcMidPointY - ((arcEndPointY + arcStartPointY) / 2.0f)), 2.0d)));
    }

    /* access modifiers changed from: protected */
    public float getSliceSpace(IPieDataSet dataSet) {
        if (!dataSet.isAutomaticallyDisableSliceSpacingEnabled()) {
            return dataSet.getSliceSpace();
        }
        if (dataSet.getSliceSpace() / this.mViewPortHandler.getSmallestContentExtension() > (dataSet.getYMin() / ((PieData) this.mChart.getData()).getYValueSum()) * 2.0f) {
            return 0.0f;
        }
        return dataSet.getSliceSpace();
    }

    /* access modifiers changed from: protected */
    public void drawDataSet(Canvas c, IPieDataSet dataSet) {
        float rotationAngle;
        float phaseX;
        RectF circleBox;
        float[] drawAngles;
        int entryCount;
        int visibleAngleCount;
        float radius;
        int j;
        int visibleAngleCount2;
        MPPointF center;
        float arcStartPointX;
        float startAngleOuter;
        float phaseX2;
        float sweepAngleOuter;
        float innerRadius;
        float rotationAngle2;
        float phaseX3;
        RectF circleBox2;
        int j2;
        int visibleAngleCount3;
        int visibleAngleCount4;
        float radius2;
        MPPointF center2;
        float sweepAngleOuter2;
        int i;
        MPPointF center3;
        PieChartRenderer pieChartRenderer = this;
        IPieDataSet iPieDataSet = dataSet;
        float rotationAngle3 = pieChartRenderer.mChart.getRotationAngle();
        float phaseX4 = pieChartRenderer.mAnimator.getPhaseX();
        float phaseY = pieChartRenderer.mAnimator.getPhaseY();
        RectF circleBox3 = pieChartRenderer.mChart.getCircleBox();
        int entryCount2 = dataSet.getEntryCount();
        float[] drawAngles2 = pieChartRenderer.mChart.getDrawAngles();
        MPPointF center4 = pieChartRenderer.mChart.getCenterCircleBox();
        float radius3 = pieChartRenderer.mChart.getRadius();
        int i2 = 1;
        boolean drawInnerArc = pieChartRenderer.mChart.isDrawHoleEnabled() && !pieChartRenderer.mChart.isDrawSlicesUnderHoleEnabled();
        float userInnerRadius = drawInnerArc ? (pieChartRenderer.mChart.getHoleRadius() / 100.0f) * radius3 : 0.0f;
        int visibleAngleCount5 = 0;
        for (int j3 = 0; j3 < entryCount2; j3++) {
            if (Math.abs(((PieEntry) iPieDataSet.getEntryForIndex(j3)).getY()) > Utils.FLOAT_EPSILON) {
                visibleAngleCount5++;
            }
        }
        float sliceSpace = visibleAngleCount5 <= 1 ? 0.0f : pieChartRenderer.getSliceSpace(iPieDataSet);
        float angle = 0.0f;
        int j4 = 0;
        while (true) {
            int j5 = j4;
            if (j5 < entryCount2) {
                float sliceAngle = drawAngles2[j5];
                float innerRadius2 = userInnerRadius;
                if (Math.abs(iPieDataSet.getEntryForIndex(j5).getY()) <= Utils.FLOAT_EPSILON || pieChartRenderer.mChart.needsHighlight(j5)) {
                    j = j5;
                    visibleAngleCount = i2;
                    radius = radius3;
                    rotationAngle = rotationAngle3;
                    phaseX = phaseX4;
                    circleBox = circleBox3;
                    entryCount = entryCount2;
                    drawAngles = drawAngles2;
                    visibleAngleCount2 = visibleAngleCount5;
                    center = center4;
                } else {
                    int accountForSliceSpacing = (sliceSpace <= 0.0f || sliceAngle > 180.0f) ? 0 : i2;
                    pieChartRenderer.mRenderPaint.setColor(iPieDataSet.getColor(j5));
                    float sliceSpaceAngleOuter = visibleAngleCount5 == i2 ? 0.0f : sliceSpace / (radius3 * 0.017453292f);
                    float startAngleOuter2 = rotationAngle3 + ((angle + (sliceSpaceAngleOuter / 2.0f)) * phaseY);
                    float sweepAngleOuter3 = (sliceAngle - sliceSpaceAngleOuter) * phaseY;
                    if (sweepAngleOuter3 < 0.0f) {
                        sweepAngleOuter3 = 0.0f;
                    }
                    pieChartRenderer.mPathBuffer.reset();
                    int j6 = j5;
                    int visibleAngleCount6 = visibleAngleCount5;
                    float arcStartPointX2 = center4.x + (((float) Math.cos((double) (startAngleOuter2 * 0.017453292f))) * radius3);
                    entryCount = entryCount2;
                    drawAngles = drawAngles2;
                    float arcStartPointY = center4.y + (((float) Math.sin((double) (startAngleOuter2 * 0.017453292f))) * radius3);
                    if (sweepAngleOuter3 < 360.0f || sweepAngleOuter3 % 360.0f > Utils.FLOAT_EPSILON) {
                        pieChartRenderer.mPathBuffer.moveTo(arcStartPointX2, arcStartPointY);
                        pieChartRenderer.mPathBuffer.arcTo(circleBox3, startAngleOuter2, sweepAngleOuter3);
                    } else {
                        pieChartRenderer.mPathBuffer.addCircle(center4.x, center4.y, radius3, Path.Direction.CW);
                    }
                    float sweepAngleOuter4 = sweepAngleOuter3;
                    pieChartRenderer.mInnerRectBuffer.set(center4.x - innerRadius2, center4.y - innerRadius2, center4.x + innerRadius2, center4.y + innerRadius2);
                    if (!drawInnerArc) {
                        startAngleOuter = startAngleOuter2;
                        arcStartPointX = arcStartPointX2;
                        rotationAngle2 = rotationAngle3;
                        phaseX3 = phaseX4;
                        circleBox2 = circleBox3;
                        j2 = j6;
                        visibleAngleCount3 = visibleAngleCount6;
                        sweepAngleOuter = sweepAngleOuter4;
                        visibleAngleCount4 = 1;
                        phaseX2 = innerRadius2;
                        radius2 = radius3;
                    } else if (innerRadius2 > 0.0f || accountForSliceSpacing != 0) {
                        if (accountForSliceSpacing != 0) {
                            sweepAngleOuter2 = sweepAngleOuter4;
                            j = j6;
                            phaseX = phaseX4;
                            circleBox = circleBox3;
                            visibleAngleCount2 = visibleAngleCount6;
                            float innerRadius3 = innerRadius2;
                            float f = arcStartPointX2;
                            i = 1;
                            radius = radius3;
                            center2 = center4;
                            float minSpacedRadius = calculateMinimumRadiusForSpacedSlice(center4, radius3, sliceAngle * phaseY, arcStartPointX2, arcStartPointY, startAngleOuter2, sweepAngleOuter2);
                            if (minSpacedRadius < 0.0f) {
                                minSpacedRadius = -minSpacedRadius;
                            }
                            innerRadius2 = Math.max(innerRadius3, minSpacedRadius);
                        } else {
                            float f2 = arcStartPointX2;
                            center2 = center4;
                            phaseX = phaseX4;
                            circleBox = circleBox3;
                            j = j6;
                            visibleAngleCount2 = visibleAngleCount6;
                            sweepAngleOuter2 = sweepAngleOuter4;
                            i = 1;
                            float phaseX5 = innerRadius2;
                            radius = radius3;
                        }
                        float sliceSpaceAngleInner = (visibleAngleCount2 == i || innerRadius2 == 0.0f) ? 0.0f : sliceSpace / (innerRadius2 * 0.017453292f);
                        float startAngleInner = ((angle + (sliceSpaceAngleInner / 2.0f)) * phaseY) + rotationAngle3;
                        float sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY;
                        if (sweepAngleInner < 0.0f) {
                            sweepAngleInner = 0.0f;
                        }
                        float endAngleInner = startAngleInner + sweepAngleInner;
                        if (sweepAngleOuter2 < 360.0f || sweepAngleOuter2 % 360.0f > Utils.FLOAT_EPSILON) {
                            visibleAngleCount = i;
                            center3 = center2;
                            pieChartRenderer = this;
                            rotationAngle = rotationAngle3;
                            pieChartRenderer.mPathBuffer.lineTo(center3.x + (((float) Math.cos((double) (endAngleInner * 0.017453292f))) * innerRadius2), center3.y + (((float) Math.sin((double) (endAngleInner * 0.017453292f))) * innerRadius2));
                            pieChartRenderer.mPathBuffer.arcTo(pieChartRenderer.mInnerRectBuffer, endAngleInner, -sweepAngleInner);
                        } else {
                            visibleAngleCount = i;
                            pieChartRenderer = this;
                            center3 = center2;
                            float f3 = sliceSpaceAngleInner;
                            pieChartRenderer.mPathBuffer.addCircle(center3.x, center3.y, innerRadius2, Path.Direction.CCW);
                            rotationAngle = rotationAngle3;
                        }
                        center = center3;
                        pieChartRenderer.mPathBuffer.close();
                        pieChartRenderer.mBitmapCanvas.drawPath(pieChartRenderer.mPathBuffer, pieChartRenderer.mRenderPaint);
                    } else {
                        startAngleOuter = startAngleOuter2;
                        arcStartPointX = arcStartPointX2;
                        rotationAngle2 = rotationAngle3;
                        phaseX3 = phaseX4;
                        circleBox2 = circleBox3;
                        j2 = j6;
                        visibleAngleCount3 = visibleAngleCount6;
                        sweepAngleOuter = sweepAngleOuter4;
                        visibleAngleCount4 = 1;
                        phaseX2 = innerRadius2;
                        radius2 = radius3;
                    }
                    if (sweepAngleOuter % 360.0f <= Utils.FLOAT_EPSILON) {
                        innerRadius = phaseX2;
                        center = center4;
                    } else if (accountForSliceSpacing != 0) {
                        float angleMiddle = startAngleOuter + (sweepAngleOuter / 2.0f);
                        innerRadius = phaseX2;
                        center = center4;
                        float sliceSpaceOffset = calculateMinimumRadiusForSpacedSlice(center4, radius, sliceAngle * phaseY, arcStartPointX, arcStartPointY, startAngleOuter, sweepAngleOuter);
                        pieChartRenderer.mPathBuffer.lineTo(center.x + (((float) Math.cos((double) (angleMiddle * 0.017453292f))) * sliceSpaceOffset), center.y + (((float) Math.sin((double) (angleMiddle * 0.017453292f))) * sliceSpaceOffset));
                    } else {
                        innerRadius = phaseX2;
                        center = center4;
                        pieChartRenderer.mPathBuffer.lineTo(center.x, center.y);
                    }
                    float f4 = innerRadius;
                    pieChartRenderer.mPathBuffer.close();
                    pieChartRenderer.mBitmapCanvas.drawPath(pieChartRenderer.mPathBuffer, pieChartRenderer.mRenderPaint);
                }
                angle += sliceAngle * phaseX;
                j4 = j + 1;
                center4 = center;
                visibleAngleCount5 = visibleAngleCount2;
                radius3 = radius;
                i2 = visibleAngleCount;
                entryCount2 = entryCount;
                drawAngles2 = drawAngles;
                circleBox3 = circleBox;
                phaseX4 = phaseX;
                rotationAngle3 = rotationAngle;
                iPieDataSet = dataSet;
            } else {
                float f5 = rotationAngle3;
                float f6 = phaseX4;
                RectF rectF = circleBox3;
                int i3 = entryCount2;
                float[] fArr = drawAngles2;
                int i4 = visibleAngleCount5;
                MPPointF.recycleInstance(center4);
                return;
            }
        }
    }

    public void drawValues(Canvas c) {
        List<IPieDataSet> dataSets;
        int i;
        float angle;
        float value;
        List<IPieDataSet> dataSets2;
        PieDataSet.ValuePosition xValuePosition;
        PieDataSet.ValuePosition yValuePosition;
        int entryCount;
        float sliceXBase;
        float sliceYBase;
        int i2;
        IPieDataSet dataSet;
        int j;
        float line1Radius;
        List<IPieDataSet> dataSets3;
        float f;
        float pt2y;
        float pt2x;
        float pt2y2;
        float labelPtx;
        int j2;
        Canvas canvas = c;
        MPPointF center = this.mChart.getCenterCircleBox();
        float radius = this.mChart.getRadius();
        float rotationAngle = this.mChart.getRotationAngle();
        float[] drawAngles = this.mChart.getDrawAngles();
        float[] absoluteAngles = this.mChart.getAbsoluteAngles();
        float phaseX = this.mAnimator.getPhaseX();
        float phaseY = this.mAnimator.getPhaseY();
        float holeRadiusPercent = this.mChart.getHoleRadius() / 100.0f;
        float labelRadiusOffset = (radius / 10.0f) * 3.6f;
        if (this.mChart.isDrawHoleEnabled()) {
            labelRadiusOffset = (radius - (radius * holeRadiusPercent)) / 2.0f;
        }
        float labelRadius = radius - labelRadiusOffset;
        PieData data = (PieData) this.mChart.getData();
        List<IPieDataSet> dataSets4 = data.getDataSets();
        float yValueSum = data.getYValueSum();
        boolean drawEntryLabels = this.mChart.isDrawEntryLabelsEnabled();
        c.save();
        float offset = Utils.convertDpToPixel(5.0f);
        int xIndex = 0;
        int xIndex2 = 0;
        while (true) {
            int i3 = xIndex2;
            if (i3 < dataSets4.size()) {
                IPieDataSet dataSet2 = dataSets4.get(i3);
                boolean drawValues = dataSet2.isDrawValuesEnabled();
                if (drawValues || drawEntryLabels) {
                    PieDataSet.ValuePosition xValuePosition2 = dataSet2.getXValuePosition();
                    PieDataSet.ValuePosition yValuePosition2 = dataSet2.getYValuePosition();
                    applyValueTextStyle(dataSet2);
                    float lineHeight = ((float) Utils.calcTextHeight(this.mValuePaint, "Q")) + Utils.convertDpToPixel(4.0f);
                    IValueFormatter formatter = dataSet2.getValueFormatter();
                    int entryCount2 = dataSet2.getEntryCount();
                    this.mValueLinePaint.setColor(dataSet2.getValueLineColor());
                    this.mValueLinePaint.setStrokeWidth(Utils.convertDpToPixel(dataSet2.getValueLineWidth()));
                    float sliceSpace = getSliceSpace(dataSet2);
                    int xIndex3 = xIndex;
                    int xIndex4 = 0;
                    while (xIndex4 < entryCount2) {
                        PieEntry entry = (PieEntry) dataSet2.getEntryForIndex(xIndex4);
                        if (xIndex3 == 0) {
                            angle = 0.0f;
                        } else {
                            angle = absoluteAngles[xIndex3 - 1] * phaseX;
                        }
                        float transformedAngle = rotationAngle + ((angle + ((drawAngles[xIndex3] - ((sliceSpace / (labelRadius * 0.017453292f)) / 2.0f)) / 2.0f)) * phaseY);
                        if (this.mChart.isUsePercentValuesEnabled()) {
                            value = (entry.getY() / yValueSum) * 100.0f;
                        } else {
                            value = entry.getY();
                        }
                        float sliceXBase2 = (float) Math.cos((double) (transformedAngle * 0.017453292f));
                        float sliceYBase2 = (float) Math.sin((double) (transformedAngle * 0.017453292f));
                        boolean drawXOutside = drawEntryLabels && xValuePosition2 == PieDataSet.ValuePosition.OUTSIDE_SLICE;
                        boolean drawYOutside = drawValues && yValuePosition2 == PieDataSet.ValuePosition.OUTSIDE_SLICE;
                        boolean drawXInside = drawEntryLabels && xValuePosition2 == PieDataSet.ValuePosition.INSIDE_SLICE;
                        boolean drawYInside = drawValues && yValuePosition2 == PieDataSet.ValuePosition.INSIDE_SLICE;
                        if (drawXOutside || drawYOutside) {
                            float valueLineLength1 = dataSet2.getValueLinePart1Length();
                            float valueLineLength2 = dataSet2.getValueLinePart2Length();
                            float valueLinePart1OffsetPercentage = dataSet2.getValueLinePart1OffsetPercentage() / 100.0f;
                            if (this.mChart.isDrawHoleEnabled()) {
                                line1Radius = ((radius - (radius * holeRadiusPercent)) * valueLinePart1OffsetPercentage) + (radius * holeRadiusPercent);
                            } else {
                                line1Radius = radius * valueLinePart1OffsetPercentage;
                            }
                            float line1Radius2 = line1Radius;
                            if (dataSet2.isValueLineVariableLength()) {
                                i2 = i3;
                                dataSets3 = dataSets4;
                                f = labelRadius * valueLineLength2 * ((float) Math.abs(Math.sin((double) (0.017453292f * transformedAngle))));
                            } else {
                                i2 = i3;
                                dataSets3 = dataSets4;
                                f = labelRadius * valueLineLength2;
                            }
                            float polyline2Width = f;
                            float pt0x = (line1Radius2 * sliceXBase2) + center.x;
                            float pt0y = (line1Radius2 * sliceYBase2) + center.y;
                            float pt1x = ((valueLineLength1 + 1.0f) * labelRadius * sliceXBase2) + center.x;
                            float pt1y = ((valueLineLength1 + 1.0f) * labelRadius * sliceYBase2) + center.y;
                            if (((double) transformedAngle) % 360.0d < 90.0d || ((double) transformedAngle) % 360.0d > 270.0d) {
                                float pt2x2 = pt1x + polyline2Width;
                                pt2y2 = pt1y;
                                this.mValuePaint.setTextAlign(Paint.Align.LEFT);
                                if (drawXOutside) {
                                    this.mEntryLabelsPaint.setTextAlign(Paint.Align.LEFT);
                                }
                                pt2x = pt2x2;
                                labelPtx = pt2x2 + offset;
                                pt2y = pt2y2;
                            } else {
                                float pt2x3 = pt1x - polyline2Width;
                                float pt2y3 = pt1y;
                                this.mValuePaint.setTextAlign(Paint.Align.RIGHT);
                                if (drawXOutside) {
                                    this.mEntryLabelsPaint.setTextAlign(Paint.Align.RIGHT);
                                }
                                labelPtx = pt2x3 - offset;
                                pt2y2 = pt2y3;
                                pt2x = pt2x3;
                                pt2y = pt2y3;
                            }
                            float labelPty = pt2y2;
                            if (dataSet2.getValueLineColor() != 1122867) {
                                Canvas canvas2 = c;
                                sliceYBase = sliceYBase2;
                                sliceXBase = sliceXBase2;
                                float f2 = transformedAngle;
                                entryCount = entryCount2;
                                canvas2.drawLine(pt0x, pt0y, pt1x, pt1y, this.mValueLinePaint);
                                canvas2.drawLine(pt1x, pt1y, pt2x, pt2y, this.mValueLinePaint);
                            } else {
                                sliceYBase = sliceYBase2;
                                sliceXBase = sliceXBase2;
                                float f3 = transformedAngle;
                                entryCount = entryCount2;
                            }
                            if (!drawXOutside || !drawYOutside) {
                                yValuePosition = yValuePosition2;
                                j2 = xIndex4;
                                xValuePosition = xValuePosition2;
                                if (drawXOutside) {
                                    if (j2 < data.getEntryCount() && entry.getLabel() != null) {
                                        drawEntryLabel(canvas, entry.getLabel(), labelPtx, labelPty + (lineHeight / 2.0f));
                                    }
                                } else if (drawYOutside) {
                                    dataSet = dataSet2;
                                    float f4 = labelPtx;
                                    j = j2;
                                    dataSets2 = dataSets3;
                                    drawValue(c, formatter, value, entry, 0, labelPtx, labelPty + (lineHeight / 2.0f), dataSet2.getValueTextColor(j2));
                                } else {
                                    dataSet = dataSet2;
                                    j = j2;
                                    dataSets2 = dataSets3;
                                }
                            } else {
                                yValuePosition = yValuePosition2;
                                j2 = xIndex4;
                                xValuePosition = xValuePosition2;
                                drawValue(c, formatter, value, entry, 0, labelPtx, labelPty, dataSet2.getValueTextColor(xIndex4));
                                if (j2 < data.getEntryCount() && entry.getLabel() != null) {
                                    drawEntryLabel(canvas, entry.getLabel(), labelPtx, labelPty + lineHeight);
                                }
                            }
                            dataSet = dataSet2;
                            j = j2;
                            dataSets2 = dataSets3;
                        } else {
                            yValuePosition = yValuePosition2;
                            j = xIndex4;
                            xValuePosition = xValuePosition2;
                            sliceYBase = sliceYBase2;
                            sliceXBase = sliceXBase2;
                            float f5 = transformedAngle;
                            entryCount = entryCount2;
                            dataSet = dataSet2;
                            i2 = i3;
                            dataSets2 = dataSets4;
                        }
                        if (drawXInside || drawYInside) {
                            float x = center.x + (labelRadius * sliceXBase);
                            float y = (labelRadius * sliceYBase) + center.y;
                            this.mValuePaint.setTextAlign(Paint.Align.CENTER);
                            if (drawXInside && drawYInside) {
                                drawValue(c, formatter, value, entry, 0, x, y, dataSet.getValueTextColor(j));
                                if (j < data.getEntryCount() && entry.getLabel() != null) {
                                    drawEntryLabel(canvas, entry.getLabel(), x, y + lineHeight);
                                }
                            } else if (drawXInside) {
                                if (j < data.getEntryCount() && entry.getLabel() != null) {
                                    drawEntryLabel(canvas, entry.getLabel(), x, y + (lineHeight / 2.0f));
                                }
                            } else if (drawYInside) {
                                drawValue(c, formatter, value, entry, 0, x, y + (lineHeight / 2.0f), dataSet.getValueTextColor(j));
                            }
                        }
                        xIndex3++;
                        xIndex4 = j + 1;
                        dataSet2 = dataSet;
                        i3 = i2;
                        entryCount2 = entryCount;
                        yValuePosition2 = yValuePosition;
                        xValuePosition2 = xValuePosition;
                        dataSets4 = dataSets2;
                    }
                    i = i3;
                    dataSets = dataSets4;
                    xIndex = xIndex3;
                } else {
                    i = i3;
                    dataSets = dataSets4;
                }
                xIndex2 = i + 1;
                dataSets4 = dataSets;
            } else {
                MPPointF.recycleInstance(center);
                c.restore();
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawEntryLabel(Canvas c, String label, float x, float y) {
        c.drawText(label, x, y, this.mEntryLabelsPaint);
    }

    public void drawExtras(Canvas c) {
        drawHole(c);
        c.drawBitmap((Bitmap) this.mDrawBitmap.get(), 0.0f, 0.0f, (Paint) null);
        drawCenterText(c);
    }

    /* access modifiers changed from: protected */
    public void drawHole(Canvas c) {
        if (this.mChart.isDrawHoleEnabled() && this.mBitmapCanvas != null) {
            float radius = this.mChart.getRadius();
            float holeRadius = (this.mChart.getHoleRadius() / 100.0f) * radius;
            MPPointF center = this.mChart.getCenterCircleBox();
            if (Color.alpha(this.mHolePaint.getColor()) > 0) {
                this.mBitmapCanvas.drawCircle(center.x, center.y, holeRadius, this.mHolePaint);
            }
            if (Color.alpha(this.mTransparentCirclePaint.getColor()) > 0 && this.mChart.getTransparentCircleRadius() > this.mChart.getHoleRadius()) {
                int alpha = this.mTransparentCirclePaint.getAlpha();
                this.mTransparentCirclePaint.setAlpha((int) (((float) alpha) * this.mAnimator.getPhaseX() * this.mAnimator.getPhaseY()));
                this.mHoleCirclePath.reset();
                this.mHoleCirclePath.addCircle(center.x, center.y, (this.mChart.getTransparentCircleRadius() / 100.0f) * radius, Path.Direction.CW);
                this.mHoleCirclePath.addCircle(center.x, center.y, holeRadius, Path.Direction.CCW);
                this.mBitmapCanvas.drawPath(this.mHoleCirclePath, this.mTransparentCirclePaint);
                this.mTransparentCirclePaint.setAlpha(alpha);
            }
            MPPointF.recycleInstance(center);
        }
    }

    /* access modifiers changed from: protected */
    public void drawCenterText(Canvas c) {
        float f;
        RectF boundingRect;
        RectF holeRect;
        Canvas canvas = c;
        CharSequence centerText = this.mChart.getCenterText();
        if (!this.mChart.isDrawCenterTextEnabled() || centerText == null) {
            return;
        }
        MPPointF center = this.mChart.getCenterCircleBox();
        MPPointF offset = this.mChart.getCenterTextOffset();
        float x = center.x + offset.x;
        float y = center.y + offset.y;
        if (!this.mChart.isDrawHoleEnabled() || this.mChart.isDrawSlicesUnderHoleEnabled()) {
            f = this.mChart.getRadius();
        } else {
            f = this.mChart.getRadius() * (this.mChart.getHoleRadius() / 100.0f);
        }
        float innerRadius = f;
        RectF holeRect2 = this.mRectBuffer[0];
        holeRect2.left = x - innerRadius;
        holeRect2.top = y - innerRadius;
        holeRect2.right = x + innerRadius;
        holeRect2.bottom = y + innerRadius;
        RectF boundingRect2 = this.mRectBuffer[1];
        boundingRect2.set(holeRect2);
        float radiusPercent = this.mChart.getCenterTextRadiusPercent() / 100.0f;
        if (((double) radiusPercent) > Utils.DOUBLE_EPSILON) {
            boundingRect2.inset((boundingRect2.width() - (boundingRect2.width() * radiusPercent)) / 2.0f, (boundingRect2.height() - (boundingRect2.height() * radiusPercent)) / 2.0f);
        }
        if (!centerText.equals(this.mCenterTextLastValue) || !boundingRect2.equals(this.mCenterTextLastBounds)) {
            this.mCenterTextLastBounds.set(boundingRect2);
            this.mCenterTextLastValue = centerText;
            float width = this.mCenterTextLastBounds.width();
            int length = centerText.length();
            TextPaint textPaint = this.mCenterTextPaint;
            StaticLayout staticLayout = r3;
            int max = (int) Math.max(Math.ceil((double) width), 1.0d);
            float f2 = width;
            float f3 = radiusPercent;
            boundingRect = boundingRect2;
            CharSequence charSequence = centerText;
            holeRect = holeRect2;
            StaticLayout staticLayout2 = new StaticLayout(centerText, 0, length, textPaint, max, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            this.mCenterTextLayout = staticLayout;
        } else {
            CharSequence charSequence2 = centerText;
            float f4 = radiusPercent;
            boundingRect = boundingRect2;
            holeRect = holeRect2;
        }
        float layoutHeight = (float) this.mCenterTextLayout.getHeight();
        c.save();
        if (Build.VERSION.SDK_INT >= 18) {
            Path path = this.mDrawCenterTextPathBuffer;
            path.reset();
            path.addOval(holeRect, Path.Direction.CW);
            canvas.clipPath(path);
        }
        RectF boundingRect3 = boundingRect;
        canvas.translate(boundingRect3.left, boundingRect3.top + ((boundingRect3.height() - layoutHeight) / 2.0f));
        this.mCenterTextLayout.draw(canvas);
        c.restore();
        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(offset);
    }

    public void drawHighlighted(Canvas c, Highlight[] indices) {
        float[] absoluteAngles;
        float[] drawAngles;
        RectF highlightedCircleBox;
        int i;
        float phaseX;
        boolean z;
        float angle;
        float shift;
        int visibleAngleCount;
        int i2;
        float sweepAngleShifted;
        int visibleAngleCount2;
        float minSpacedRadius;
        Highlight[] highlightArr = indices;
        float phaseX2 = this.mAnimator.getPhaseX();
        float phaseY = this.mAnimator.getPhaseY();
        float rotationAngle = this.mChart.getRotationAngle();
        float[] drawAngles2 = this.mChart.getDrawAngles();
        float[] absoluteAngles2 = this.mChart.getAbsoluteAngles();
        MPPointF center = this.mChart.getCenterCircleBox();
        float radius = this.mChart.getRadius();
        int i3 = 1;
        boolean drawInnerArc = this.mChart.isDrawHoleEnabled() && !this.mChart.isDrawSlicesUnderHoleEnabled();
        boolean z2 = false;
        float userInnerRadius = drawInnerArc ? (this.mChart.getHoleRadius() / 100.0f) * radius : 0.0f;
        RectF highlightedCircleBox2 = this.mDrawHighlightedRectF;
        highlightedCircleBox2.set(0.0f, 0.0f, 0.0f, 0.0f);
        int i4 = 0;
        while (true) {
            int i5 = i4;
            if (i5 < highlightArr.length) {
                int index = (int) highlightArr[i5].getX();
                if (index < drawAngles2.length) {
                    IPieDataSet set = ((PieData) this.mChart.getData()).getDataSetByIndex(highlightArr[i5].getDataSetIndex());
                    if (set == null) {
                        i = i5;
                        highlightedCircleBox = highlightedCircleBox2;
                        z = z2;
                        phaseX = phaseX2;
                        drawAngles = drawAngles2;
                        absoluteAngles = absoluteAngles2;
                    } else if (set.isHighlightEnabled()) {
                        int entryCount = set.getEntryCount();
                        int visibleAngleCount3 = 0;
                        for (int j = 0; j < entryCount; j++) {
                            if (Math.abs(((PieEntry) set.getEntryForIndex(j)).getY()) > Utils.FLOAT_EPSILON) {
                                visibleAngleCount3++;
                            }
                        }
                        if (index == 0) {
                            angle = 0.0f;
                        } else {
                            angle = absoluteAngles2[index - 1] * phaseX2;
                        }
                        float angle2 = angle;
                        float sliceSpace = visibleAngleCount3 <= i3 ? 0.0f : set.getSliceSpace();
                        float sliceAngle = drawAngles2[index];
                        float shift2 = set.getSelectionShift();
                        float highlightedRadius = radius + shift2;
                        float innerRadius = userInnerRadius;
                        highlightedCircleBox2.set(this.mChart.getCircleBox());
                        int i6 = entryCount;
                        highlightedCircleBox2.inset(-shift2, -shift2);
                        boolean accountForSliceSpacing = sliceSpace > 0.0f && sliceAngle <= 180.0f;
                        this.mRenderPaint.setColor(set.getColor(index));
                        float sliceSpaceAngleOuter = visibleAngleCount3 == 1 ? 0.0f : sliceSpace / (radius * 0.017453292f);
                        float sliceSpaceAngleShifted = visibleAngleCount3 == 1 ? 0.0f : sliceSpace / (highlightedRadius * 0.017453292f);
                        float startAngleOuter = rotationAngle + ((angle2 + (sliceSpaceAngleOuter / 2.0f)) * phaseY);
                        float sweepAngleOuter = (sliceAngle - sliceSpaceAngleOuter) * phaseY;
                        if (sweepAngleOuter < 0.0f) {
                            sweepAngleOuter = 0.0f;
                        }
                        float sweepAngleOuter2 = sweepAngleOuter;
                        float startAngleShifted = ((angle2 + (sliceSpaceAngleShifted / 2.0f)) * phaseY) + rotationAngle;
                        float sweepAngleShifted2 = (sliceAngle - sliceSpaceAngleShifted) * phaseY;
                        z = false;
                        if (sweepAngleShifted2 < 0.0f) {
                            sweepAngleShifted2 = 0.0f;
                        }
                        phaseX = phaseX2;
                        float sweepAngleShifted3 = sweepAngleShifted2;
                        this.mPathBuffer.reset();
                        if (sweepAngleOuter2 < 360.0f || sweepAngleOuter2 % 360.0f > Utils.FLOAT_EPSILON) {
                            int i7 = index;
                            i = i5;
                            visibleAngleCount = visibleAngleCount3;
                            shift = shift2;
                            this.mPathBuffer.moveTo(center.x + (((float) Math.cos((double) (startAngleShifted * 0.017453292f))) * highlightedRadius), center.y + (((float) Math.sin((double) (startAngleShifted * 0.017453292f))) * highlightedRadius));
                            this.mPathBuffer.arcTo(highlightedCircleBox2, startAngleShifted, sweepAngleShifted3);
                        } else {
                            IPieDataSet iPieDataSet = set;
                            int i8 = index;
                            i = i5;
                            this.mPathBuffer.addCircle(center.x, center.y, highlightedRadius, Path.Direction.CW);
                            visibleAngleCount = visibleAngleCount3;
                            shift = shift2;
                        }
                        float sliceSpaceRadius = 0.0f;
                        if (accountForSliceSpacing) {
                            float innerRadius2 = innerRadius;
                            float innerRadius3 = startAngleShifted;
                            highlightedCircleBox = highlightedCircleBox2;
                            float f = highlightedRadius;
                            visibleAngleCount2 = visibleAngleCount;
                            float f2 = sweepAngleShifted3;
                            drawAngles = drawAngles2;
                            i2 = 1;
                            float f3 = shift;
                            sweepAngleShifted = innerRadius2;
                            sliceSpaceRadius = calculateMinimumRadiusForSpacedSlice(center, radius, sliceAngle * phaseY, center.x + (((float) Math.cos((double) (startAngleOuter * 0.017453292f))) * radius), center.y + (((float) Math.sin((double) (startAngleOuter * 0.017453292f))) * radius), startAngleOuter, sweepAngleOuter2);
                        } else {
                            highlightedCircleBox = highlightedCircleBox2;
                            float f4 = highlightedRadius;
                            float f5 = sweepAngleShifted3;
                            drawAngles = drawAngles2;
                            sweepAngleShifted = innerRadius;
                            visibleAngleCount2 = visibleAngleCount;
                            float f6 = shift;
                            i2 = 1;
                            float innerRadius4 = startAngleShifted;
                        }
                        this.mInnerRectBuffer.set(center.x - sweepAngleShifted, center.y - sweepAngleShifted, center.x + sweepAngleShifted, center.y + sweepAngleShifted);
                        if (!drawInnerArc) {
                            absoluteAngles = absoluteAngles2;
                        } else if (sweepAngleShifted > 0.0f || accountForSliceSpacing) {
                            if (accountForSliceSpacing) {
                                float minSpacedRadius2 = sliceSpaceRadius;
                                if (minSpacedRadius2 < 0.0f) {
                                    minSpacedRadius2 = -minSpacedRadius2;
                                }
                                minSpacedRadius = Math.max(sweepAngleShifted, minSpacedRadius2);
                            } else {
                                minSpacedRadius = sweepAngleShifted;
                            }
                            float sliceSpaceAngleInner = (visibleAngleCount2 == i2 || minSpacedRadius == 0.0f) ? 0.0f : sliceSpace / (minSpacedRadius * 0.017453292f);
                            float startAngleInner = ((angle2 + (sliceSpaceAngleInner / 2.0f)) * phaseY) + rotationAngle;
                            float sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY;
                            if (sweepAngleInner < 0.0f) {
                                sweepAngleInner = 0.0f;
                            }
                            float endAngleInner = startAngleInner + sweepAngleInner;
                            if (sweepAngleOuter2 < 360.0f || sweepAngleOuter2 % 360.0f > Utils.FLOAT_EPSILON) {
                                absoluteAngles = absoluteAngles2;
                                this.mPathBuffer.lineTo(center.x + (((float) Math.cos((double) (endAngleInner * 0.017453292f))) * minSpacedRadius), center.y + (((float) Math.sin((double) (endAngleInner * 0.017453292f))) * minSpacedRadius));
                                this.mPathBuffer.arcTo(this.mInnerRectBuffer, endAngleInner, -sweepAngleInner);
                            } else {
                                this.mPathBuffer.addCircle(center.x, center.y, minSpacedRadius, Path.Direction.CCW);
                                absoluteAngles = absoluteAngles2;
                            }
                            this.mPathBuffer.close();
                            this.mBitmapCanvas.drawPath(this.mPathBuffer, this.mRenderPaint);
                        } else {
                            absoluteAngles = absoluteAngles2;
                        }
                        if (sweepAngleOuter2 % 360.0f > Utils.FLOAT_EPSILON) {
                            if (accountForSliceSpacing) {
                                float angleMiddle = startAngleOuter + (sweepAngleOuter2 / 2.0f);
                                this.mPathBuffer.lineTo(center.x + (((float) Math.cos((double) (angleMiddle * 0.017453292f))) * sliceSpaceRadius), center.y + (((float) Math.sin((double) (angleMiddle * 0.017453292f))) * sliceSpaceRadius));
                            } else {
                                this.mPathBuffer.lineTo(center.x, center.y);
                            }
                        }
                        float f7 = sweepAngleShifted;
                        this.mPathBuffer.close();
                        this.mBitmapCanvas.drawPath(this.mPathBuffer, this.mRenderPaint);
                    }
                    i4 = i + 1;
                    z2 = z;
                    phaseX2 = phaseX;
                    highlightedCircleBox2 = highlightedCircleBox;
                    drawAngles2 = drawAngles;
                    absoluteAngles2 = absoluteAngles;
                    i3 = 1;
                    highlightArr = indices;
                }
                i = i5;
                highlightedCircleBox = highlightedCircleBox2;
                z = z2;
                phaseX = phaseX2;
                drawAngles = drawAngles2;
                absoluteAngles = absoluteAngles2;
                i4 = i + 1;
                z2 = z;
                phaseX2 = phaseX;
                highlightedCircleBox2 = highlightedCircleBox;
                drawAngles2 = drawAngles;
                absoluteAngles2 = absoluteAngles;
                i3 = 1;
                highlightArr = indices;
            } else {
                float f8 = phaseX2;
                float[] fArr = drawAngles2;
                float[] fArr2 = absoluteAngles2;
                MPPointF.recycleInstance(center);
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawRoundedSlices(Canvas c) {
        float angle;
        float[] drawAngles;
        if (this.mChart.isDrawRoundedSlicesEnabled()) {
            IPieDataSet dataSet = ((PieData) this.mChart.getData()).getDataSet();
            if (dataSet.isVisible()) {
                float phaseX = this.mAnimator.getPhaseX();
                float phaseY = this.mAnimator.getPhaseY();
                MPPointF center = this.mChart.getCenterCircleBox();
                float r = this.mChart.getRadius();
                float circleRadius = (r - ((this.mChart.getHoleRadius() * r) / 100.0f)) / 2.0f;
                float[] drawAngles2 = this.mChart.getDrawAngles();
                float angle2 = this.mChart.getRotationAngle();
                int j = 0;
                while (j < dataSet.getEntryCount()) {
                    float sliceAngle = drawAngles2[j];
                    if (Math.abs(dataSet.getEntryForIndex(j).getY()) > Utils.FLOAT_EPSILON) {
                        drawAngles = drawAngles2;
                        angle = angle2;
                        float y = (float) ((((double) (r - circleRadius)) * Math.sin(Math.toRadians((double) ((angle2 + sliceAngle) * phaseY)))) + ((double) center.y));
                        this.mRenderPaint.setColor(dataSet.getColor(j));
                        this.mBitmapCanvas.drawCircle((float) ((((double) (r - circleRadius)) * Math.cos(Math.toRadians((double) ((angle2 + sliceAngle) * phaseY)))) + ((double) center.x)), y, circleRadius, this.mRenderPaint);
                    } else {
                        drawAngles = drawAngles2;
                        angle = angle2;
                    }
                    angle2 = angle + (sliceAngle * phaseX);
                    j++;
                    drawAngles2 = drawAngles;
                }
                float f = angle2;
                MPPointF.recycleInstance(center);
            }
        }
    }

    public void releaseBitmap() {
        if (this.mBitmapCanvas != null) {
            this.mBitmapCanvas.setBitmap((Bitmap) null);
            this.mBitmapCanvas = null;
        }
        if (this.mDrawBitmap != null) {
            ((Bitmap) this.mDrawBitmap.get()).recycle();
            this.mDrawBitmap.clear();
            this.mDrawBitmap = null;
        }
    }
}
