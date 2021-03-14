package com.github.mikephil.charting.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.drawable.Drawable;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.BarLineScatterCandleBubbleRenderer;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

public class LineChartRenderer extends LineRadarRenderer {
    protected Path cubicFillPath = new Path();
    protected Path cubicPath = new Path();
    protected Canvas mBitmapCanvas;
    protected Bitmap.Config mBitmapConfig = Bitmap.Config.ARGB_8888;
    protected LineDataProvider mChart;
    protected Paint mCirclePaintInner;
    private float[] mCirclesBuffer = new float[2];
    protected WeakReference<Bitmap> mDrawBitmap;
    protected Path mGenerateFilledPathBuffer = new Path();
    private HashMap<IDataSet, DataSetImageCache> mImageCaches = new HashMap<>();
    private float[] mLineBuffer = new float[4];

    public LineChartRenderer(LineDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;
        this.mCirclePaintInner = new Paint(1);
        this.mCirclePaintInner.setStyle(Paint.Style.FILL);
        this.mCirclePaintInner.setColor(-1);
    }

    public void initBuffers() {
    }

    public void drawData(Canvas c) {
        int width = (int) this.mViewPortHandler.getChartWidth();
        int height = (int) this.mViewPortHandler.getChartHeight();
        if (!(this.mDrawBitmap != null && ((Bitmap) this.mDrawBitmap.get()).getWidth() == width && ((Bitmap) this.mDrawBitmap.get()).getHeight() == height)) {
            if (width > 0 && height > 0) {
                this.mDrawBitmap = new WeakReference<>(Bitmap.createBitmap(width, height, this.mBitmapConfig));
                this.mBitmapCanvas = new Canvas((Bitmap) this.mDrawBitmap.get());
            } else {
                return;
            }
        }
        ((Bitmap) this.mDrawBitmap.get()).eraseColor(0);
        for (ILineDataSet set : this.mChart.getLineData().getDataSets()) {
            if (set.isVisible()) {
                drawDataSet(c, set);
            }
        }
        c.drawBitmap((Bitmap) this.mDrawBitmap.get(), 0.0f, 0.0f, this.mRenderPaint);
    }

    /* access modifiers changed from: protected */
    public void drawDataSet(Canvas c, ILineDataSet dataSet) {
        if (dataSet.getEntryCount() >= 1) {
            this.mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
            this.mRenderPaint.setPathEffect(dataSet.getDashPathEffect());
            switch (dataSet.getMode()) {
                case CUBIC_BEZIER:
                    drawCubicBezier(dataSet);
                    break;
                case HORIZONTAL_BEZIER:
                    drawHorizontalBezier(dataSet);
                    break;
                default:
                    drawLinear(c, dataSet);
                    break;
            }
            this.mRenderPaint.setPathEffect((PathEffect) null);
        }
    }

    /* access modifiers changed from: protected */
    public void drawHorizontalBezier(ILineDataSet dataSet) {
        float phaseY = this.mAnimator.getPhaseY();
        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
        this.mXBounds.set(this.mChart, dataSet);
        this.cubicPath.reset();
        if (this.mXBounds.range >= 1) {
            Entry cur = dataSet.getEntryForIndex(this.mXBounds.min);
            this.cubicPath.moveTo(cur.getX(), cur.getY() * phaseY);
            int j = this.mXBounds.min + 1;
            while (true) {
                int j2 = j;
                if (j2 > this.mXBounds.range + this.mXBounds.min) {
                    break;
                }
                Entry prev = cur;
                cur = dataSet.getEntryForIndex(j2);
                float cpx = prev.getX() + ((cur.getX() - prev.getX()) / 2.0f);
                this.cubicPath.cubicTo(cpx, prev.getY() * phaseY, cpx, cur.getY() * phaseY, cur.getX(), cur.getY() * phaseY);
                j = j2 + 1;
            }
        }
        if (dataSet.isDrawFilledEnabled()) {
            this.cubicFillPath.reset();
            this.cubicFillPath.addPath(this.cubicPath);
            drawCubicFill(this.mBitmapCanvas, dataSet, this.cubicFillPath, trans, this.mXBounds);
        }
        this.mRenderPaint.setColor(dataSet.getColor());
        this.mRenderPaint.setStyle(Paint.Style.STROKE);
        trans.pathValueToPixel(this.cubicPath);
        this.mBitmapCanvas.drawPath(this.cubicPath, this.mRenderPaint);
        this.mRenderPaint.setPathEffect((PathEffect) null);
    }

    /* access modifiers changed from: protected */
    public void drawCubicBezier(ILineDataSet dataSet) {
        ILineDataSet iLineDataSet = dataSet;
        float max = Math.max(0.0f, Math.min(1.0f, this.mAnimator.getPhaseX()));
        float phaseY = this.mAnimator.getPhaseY();
        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
        this.mXBounds.set(this.mChart, iLineDataSet);
        float intensity = dataSet.getCubicIntensity();
        this.cubicPath.reset();
        if (this.mXBounds.range >= 1) {
            float curDx = 0.0f;
            int firstIndex = this.mXBounds.min + 1;
            int i = this.mXBounds.min + this.mXBounds.range;
            Entry prev = iLineDataSet.getEntryForIndex(Math.max(firstIndex - 2, 0));
            Entry cur = iLineDataSet.getEntryForIndex(Math.max(firstIndex - 1, 0));
            Entry next = cur;
            if (cur != null) {
                this.cubicPath.moveTo(cur.getX(), cur.getY() * phaseY);
                int j = this.mXBounds.min + 1;
                int nextIndex = -1;
                while (true) {
                    float f = curDx;
                    if (j > this.mXBounds.range + this.mXBounds.min) {
                        break;
                    }
                    Entry prevPrev = prev;
                    prev = cur;
                    cur = nextIndex == j ? next : iLineDataSet.getEntryForIndex(j);
                    int i2 = nextIndex;
                    int nextIndex2 = j + 1 < dataSet.getEntryCount() ? j + 1 : j;
                    next = iLineDataSet.getEntryForIndex(nextIndex2);
                    float prevDx = (cur.getX() - prevPrev.getX()) * intensity;
                    float prevDy = (cur.getY() - prevPrev.getY()) * intensity;
                    curDx = (next.getX() - prev.getX()) * intensity;
                    this.cubicPath.cubicTo(prev.getX() + prevDx, (prev.getY() + prevDy) * phaseY, cur.getX() - curDx, (cur.getY() - ((next.getY() - prev.getY()) * intensity)) * phaseY, cur.getX(), cur.getY() * phaseY);
                    j++;
                    nextIndex = nextIndex2;
                }
            } else {
                return;
            }
        }
        if (dataSet.isDrawFilledEnabled() != 0) {
            this.cubicFillPath.reset();
            this.cubicFillPath.addPath(this.cubicPath);
            drawCubicFill(this.mBitmapCanvas, dataSet, this.cubicFillPath, trans, this.mXBounds);
        }
        this.mRenderPaint.setColor(dataSet.getColor());
        this.mRenderPaint.setStyle(Paint.Style.STROKE);
        trans.pathValueToPixel(this.cubicPath);
        this.mBitmapCanvas.drawPath(this.cubicPath, this.mRenderPaint);
        this.mRenderPaint.setPathEffect((PathEffect) null);
    }

    /* access modifiers changed from: protected */
    public void drawCubicFill(Canvas c, ILineDataSet dataSet, Path spline, Transformer trans, BarLineScatterCandleBubbleRenderer.XBounds bounds) {
        float fillMin = dataSet.getFillFormatter().getFillLinePosition(dataSet, this.mChart);
        spline.lineTo(dataSet.getEntryForIndex(bounds.min + bounds.range).getX(), fillMin);
        spline.lineTo(dataSet.getEntryForIndex(bounds.min).getX(), fillMin);
        spline.close();
        trans.pathValueToPixel(spline);
        Drawable drawable = dataSet.getFillDrawable();
        if (drawable != null) {
            drawFilledPath(c, spline, drawable);
        } else {
            drawFilledPath(c, spline, dataSet.getFillColor(), dataSet.getFillAlpha());
        }
    }

    /* access modifiers changed from: protected */
    public void drawLinear(Canvas c, ILineDataSet dataSet) {
        Canvas canvas;
        ILineDataSet iLineDataSet = dataSet;
        int entryCount = dataSet.getEntryCount();
        boolean isDrawSteppedEnabled = dataSet.isDrawSteppedEnabled();
        char c2 = 4;
        int pointsPerEntryPair = isDrawSteppedEnabled ? 4 : 2;
        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
        float phaseY = this.mAnimator.getPhaseY();
        this.mRenderPaint.setStyle(Paint.Style.STROKE);
        if (dataSet.isDashedLineEnabled()) {
            canvas = this.mBitmapCanvas;
        } else {
            canvas = c;
        }
        this.mXBounds.set(this.mChart, iLineDataSet);
        if (!dataSet.isDrawFilledEnabled() || entryCount <= 0) {
            Canvas canvas2 = c;
        } else {
            drawLinearFill(c, iLineDataSet, trans, this.mXBounds);
        }
        char c3 = 0;
        char c4 = 1;
        if (dataSet.getColors().size() > 1) {
            if (this.mLineBuffer.length <= pointsPerEntryPair * 2) {
                this.mLineBuffer = new float[(pointsPerEntryPair * 4)];
            }
            int j = this.mXBounds.min;
            while (j <= this.mXBounds.range + this.mXBounds.min) {
                Entry e2 = iLineDataSet.getEntryForIndex(j);
                if (e2 != null) {
                    this.mLineBuffer[c3] = e2.getX();
                    this.mLineBuffer[c4] = e2.getY() * phaseY;
                    if (j < this.mXBounds.max) {
                        Entry e3 = iLineDataSet.getEntryForIndex(j + 1);
                        if (e3 == null) {
                            break;
                        } else if (isDrawSteppedEnabled) {
                            this.mLineBuffer[2] = e3.getX();
                            this.mLineBuffer[3] = this.mLineBuffer[c4];
                            this.mLineBuffer[c2] = this.mLineBuffer[2];
                            this.mLineBuffer[5] = this.mLineBuffer[3];
                            this.mLineBuffer[6] = e3.getX();
                            this.mLineBuffer[7] = e3.getY() * phaseY;
                        } else {
                            this.mLineBuffer[2] = e3.getX();
                            this.mLineBuffer[3] = e3.getY() * phaseY;
                        }
                    } else {
                        this.mLineBuffer[2] = this.mLineBuffer[0];
                        this.mLineBuffer[3] = this.mLineBuffer[c4];
                    }
                    trans.pointValuesToPixel(this.mLineBuffer);
                    if (!this.mViewPortHandler.isInBoundsRight(this.mLineBuffer[0])) {
                        break;
                    } else if (this.mViewPortHandler.isInBoundsLeft(this.mLineBuffer[2]) && (this.mViewPortHandler.isInBoundsTop(this.mLineBuffer[c4]) || this.mViewPortHandler.isInBoundsBottom(this.mLineBuffer[3]))) {
                        this.mRenderPaint.setColor(iLineDataSet.getColor(j));
                        canvas.drawLines(this.mLineBuffer, 0, pointsPerEntryPair * 2, this.mRenderPaint);
                    }
                }
                j++;
                c2 = 4;
                c3 = 0;
                c4 = 1;
            }
        } else {
            if (this.mLineBuffer.length < Math.max(entryCount * pointsPerEntryPair, pointsPerEntryPair) * 2) {
                this.mLineBuffer = new float[(Math.max(entryCount * pointsPerEntryPair, pointsPerEntryPair) * 4)];
            }
            if (iLineDataSet.getEntryForIndex(this.mXBounds.min) != null) {
                int j2 = 0;
                int x = this.mXBounds.min;
                while (x <= this.mXBounds.range + this.mXBounds.min) {
                    Entry e1 = iLineDataSet.getEntryForIndex(x == 0 ? 0 : x - 1);
                    Entry e22 = iLineDataSet.getEntryForIndex(x);
                    if (!(e1 == null || e22 == null)) {
                        int j3 = j2 + 1;
                        this.mLineBuffer[j2] = e1.getX();
                        int j4 = j3 + 1;
                        this.mLineBuffer[j3] = e1.getY() * phaseY;
                        if (isDrawSteppedEnabled) {
                            int j5 = j4 + 1;
                            this.mLineBuffer[j4] = e22.getX();
                            int j6 = j5 + 1;
                            this.mLineBuffer[j5] = e1.getY() * phaseY;
                            int j7 = j6 + 1;
                            this.mLineBuffer[j6] = e22.getX();
                            j4 = j7 + 1;
                            this.mLineBuffer[j7] = e1.getY() * phaseY;
                        }
                        int j8 = j4 + 1;
                        this.mLineBuffer[j4] = e22.getX();
                        this.mLineBuffer[j8] = e22.getY() * phaseY;
                        j2 = j8 + 1;
                    }
                    x++;
                }
                if (j2 > 0) {
                    trans.pointValuesToPixel(this.mLineBuffer);
                    this.mRenderPaint.setColor(dataSet.getColor());
                    canvas.drawLines(this.mLineBuffer, 0, Math.max((this.mXBounds.range + 1) * pointsPerEntryPair, pointsPerEntryPair) * 2, this.mRenderPaint);
                }
            }
        }
        this.mRenderPaint.setPathEffect((PathEffect) null);
    }

    /* access modifiers changed from: protected */
    public void drawLinearFill(Canvas c, ILineDataSet dataSet, Transformer trans, BarLineScatterCandleBubbleRenderer.XBounds bounds) {
        int currentStartIndex;
        int currentEndIndex;
        Path filled = this.mGenerateFilledPathBuffer;
        int startingIndex = bounds.min;
        int endingIndex = bounds.range + bounds.min;
        int iterations = 0;
        do {
            currentStartIndex = startingIndex + (iterations * 128);
            int currentEndIndex2 = currentStartIndex + 128;
            currentEndIndex = currentEndIndex2 > endingIndex ? endingIndex : currentEndIndex2;
            if (currentStartIndex <= currentEndIndex) {
                generateFilledPath(dataSet, currentStartIndex, currentEndIndex, filled);
                trans.pathValueToPixel(filled);
                Drawable drawable = dataSet.getFillDrawable();
                if (drawable != null) {
                    drawFilledPath(c, filled, drawable);
                } else {
                    drawFilledPath(c, filled, dataSet.getFillColor(), dataSet.getFillAlpha());
                }
            }
            iterations++;
        } while (currentStartIndex <= currentEndIndex);
    }

    private void generateFilledPath(ILineDataSet dataSet, int startIndex, int endIndex, Path outputPath) {
        float fillMin = dataSet.getFillFormatter().getFillLinePosition(dataSet, this.mChart);
        float phaseY = this.mAnimator.getPhaseY();
        boolean isDrawSteppedEnabled = dataSet.getMode() == LineDataSet.Mode.STEPPED;
        Path filled = outputPath;
        filled.reset();
        Entry entry = dataSet.getEntryForIndex(startIndex);
        filled.moveTo(entry.getX(), fillMin);
        filled.lineTo(entry.getX(), entry.getY() * phaseY);
        Entry currentEntry = null;
        Entry previousEntry = null;
        for (int x = startIndex + 1; x <= endIndex; x++) {
            currentEntry = dataSet.getEntryForIndex(x);
            if (isDrawSteppedEnabled && previousEntry != null) {
                filled.lineTo(currentEntry.getX(), previousEntry.getY() * phaseY);
            }
            filled.lineTo(currentEntry.getX(), currentEntry.getY() * phaseY);
            previousEntry = currentEntry;
        }
        if (currentEntry != null) {
            filled.lineTo(currentEntry.getX(), fillMin);
        }
        filled.close();
    }

    public void drawValues(Canvas c) {
        int j;
        float[] positions;
        if (isDrawingValuesAllowed(this.mChart)) {
            List<ILineDataSet> dataSets = this.mChart.getLineData().getDataSets();
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 < dataSets.size()) {
                    ILineDataSet dataSet = dataSets.get(i2);
                    if (shouldDrawValues(dataSet)) {
                        applyValueTextStyle(dataSet);
                        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
                        int valOffset = (int) (dataSet.getCircleRadius() * 1.75f);
                        if (!dataSet.isDrawCirclesEnabled()) {
                            valOffset /= 2;
                        }
                        int valOffset2 = valOffset;
                        this.mXBounds.set(this.mChart, dataSet);
                        float[] positions2 = trans.generateTransformedValuesLine(dataSet, this.mAnimator.getPhaseX(), this.mAnimator.getPhaseY(), this.mXBounds.min, this.mXBounds.max);
                        int j2 = 0;
                        while (true) {
                            int j3 = j2;
                            if (j3 >= positions2.length) {
                                break;
                            }
                            float x = positions2[j3];
                            float y = positions2[j3 + 1];
                            if (!this.mViewPortHandler.isInBoundsRight(x)) {
                                break;
                            }
                            if (!this.mViewPortHandler.isInBoundsLeft(x)) {
                                j = j3;
                                positions = positions2;
                            } else if (!this.mViewPortHandler.isInBoundsY(y)) {
                                j = j3;
                                positions = positions2;
                            } else {
                                Entry entry = dataSet.getEntryForIndex((j3 / 2) + this.mXBounds.min);
                                IValueFormatter valueFormatter = dataSet.getValueFormatter();
                                float y2 = entry.getY();
                                int valueTextColor = dataSet.getValueTextColor(j3 / 2);
                                float f = y;
                                float f2 = x;
                                j = j3;
                                positions = positions2;
                                drawValue(c, valueFormatter, y2, entry, i2, x, y - ((float) valOffset2), valueTextColor);
                            }
                            j2 = j + 2;
                            positions2 = positions;
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
        drawCircles(c);
    }

    /* access modifiers changed from: protected */
    public void drawCircles(Canvas c) {
        List<ILineDataSet> dataSets;
        float phaseY;
        boolean z;
        DataSetImageCache imageCache;
        List<ILineDataSet> dataSets2;
        boolean z2;
        LineChartRenderer lineChartRenderer = this;
        lineChartRenderer.mRenderPaint.setStyle(Paint.Style.FILL);
        float phaseY2 = lineChartRenderer.mAnimator.getPhaseY();
        float f = 0.0f;
        boolean z3 = false;
        lineChartRenderer.mCirclesBuffer[0] = 0.0f;
        boolean z4 = true;
        lineChartRenderer.mCirclesBuffer[1] = 0.0f;
        List<ILineDataSet> dataSets3 = lineChartRenderer.mChart.getLineData().getDataSets();
        int i = 0;
        while (i < dataSets3.size()) {
            ILineDataSet dataSet = dataSets3.get(i);
            if (dataSet.isVisible() && dataSet.isDrawCirclesEnabled()) {
                if (dataSet.getEntryCount() != 0) {
                    lineChartRenderer.mCirclePaintInner.setColor(dataSet.getCircleHoleColor());
                    Transformer trans = lineChartRenderer.mChart.getTransformer(dataSet.getAxisDependency());
                    lineChartRenderer.mXBounds.set(lineChartRenderer.mChart, dataSet);
                    float circleRadius = dataSet.getCircleRadius();
                    float circleHoleRadius = dataSet.getCircleHoleRadius();
                    boolean drawCircleHole = (!dataSet.isDrawCircleHoleEnabled() || circleHoleRadius >= circleRadius || circleHoleRadius <= f) ? z3 : z4;
                    boolean drawTransparentCircleHole = (!drawCircleHole || dataSet.getCircleHoleColor() != 1122867) ? z3 : z4;
                    if (lineChartRenderer.mImageCaches.containsKey(dataSet)) {
                        imageCache = lineChartRenderer.mImageCaches.get(dataSet);
                    } else {
                        imageCache = new DataSetImageCache();
                        lineChartRenderer.mImageCaches.put(dataSet, imageCache);
                    }
                    if (imageCache.init(dataSet)) {
                        imageCache.fill(dataSet, drawCircleHole, drawTransparentCircleHole);
                    }
                    int boundsRangeCount = lineChartRenderer.mXBounds.range + lineChartRenderer.mXBounds.min;
                    int j = lineChartRenderer.mXBounds.min;
                    char c2 = z3;
                    while (true) {
                        if (j > boundsRangeCount) {
                            break;
                        }
                        Entry e2 = dataSet.getEntryForIndex(j);
                        if (e2 == null) {
                            break;
                        }
                        lineChartRenderer.mCirclesBuffer[c2] = e2.getX();
                        lineChartRenderer.mCirclesBuffer[1] = e2.getY() * phaseY2;
                        trans.pointValuesToPixel(lineChartRenderer.mCirclesBuffer);
                        phaseY = phaseY2;
                        if (!lineChartRenderer.mViewPortHandler.isInBoundsRight(lineChartRenderer.mCirclesBuffer[c2])) {
                            Canvas canvas = c;
                            dataSets = dataSets3;
                            z = true;
                            break;
                        }
                        if (lineChartRenderer.mViewPortHandler.isInBoundsLeft(lineChartRenderer.mCirclesBuffer[c2])) {
                            if (!lineChartRenderer.mViewPortHandler.isInBoundsY(lineChartRenderer.mCirclesBuffer[1])) {
                                Canvas canvas2 = c;
                                dataSets2 = dataSets3;
                                z2 = true;
                            } else {
                                Bitmap circleBitmap = imageCache.getBitmap(j);
                                if (circleBitmap != null) {
                                    z2 = true;
                                    dataSets2 = dataSets3;
                                    c.drawBitmap(circleBitmap, lineChartRenderer.mCirclesBuffer[c2] - circleRadius, lineChartRenderer.mCirclesBuffer[1] - circleRadius, lineChartRenderer.mRenderPaint);
                                }
                            }
                            j++;
                            z4 = z2;
                            phaseY2 = phaseY;
                            dataSets3 = dataSets2;
                            lineChartRenderer = this;
                            c2 = 0;
                        }
                        Canvas canvas3 = c;
                        dataSets2 = dataSets3;
                        z2 = true;
                        j++;
                        z4 = z2;
                        phaseY2 = phaseY;
                        dataSets3 = dataSets2;
                        lineChartRenderer = this;
                        c2 = 0;
                    }
                    i++;
                    z4 = z;
                    phaseY2 = phaseY;
                    dataSets3 = dataSets;
                    lineChartRenderer = this;
                    f = 0.0f;
                    z3 = false;
                }
                Canvas canvas4 = c;
                phaseY = phaseY2;
                dataSets = dataSets3;
                z = z4;
                i++;
                z4 = z;
                phaseY2 = phaseY;
                dataSets3 = dataSets;
                lineChartRenderer = this;
                f = 0.0f;
                z3 = false;
            }
            Canvas canvas5 = c;
            phaseY = phaseY2;
            dataSets = dataSets3;
            z = z4;
            i++;
            z4 = z;
            phaseY2 = phaseY;
            dataSets3 = dataSets;
            lineChartRenderer = this;
            f = 0.0f;
            z3 = false;
        }
        Canvas canvas6 = c;
        float f2 = phaseY2;
        List<ILineDataSet> list = dataSets3;
    }

    public void drawHighlighted(Canvas c, Highlight[] indices) {
        LineData lineData = this.mChart.getLineData();
        for (Highlight high : indices) {
            ILineDataSet set = (ILineDataSet) lineData.getDataSetByIndex(high.getDataSetIndex());
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

    public void setBitmapConfig(Bitmap.Config config) {
        this.mBitmapConfig = config;
        releaseBitmap();
    }

    public Bitmap.Config getBitmapConfig() {
        return this.mBitmapConfig;
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

    private class DataSetImageCache {
        private Bitmap[] circleBitmaps;
        private Path mCirclePathBuffer;

        private DataSetImageCache() {
            this.mCirclePathBuffer = new Path();
        }

        /* access modifiers changed from: protected */
        public boolean init(ILineDataSet set) {
            int size = set.getCircleColorCount();
            if (this.circleBitmaps == null) {
                this.circleBitmaps = new Bitmap[size];
                return true;
            } else if (this.circleBitmaps.length == size) {
                return false;
            } else {
                this.circleBitmaps = new Bitmap[size];
                return true;
            }
        }

        /* access modifiers changed from: protected */
        public void fill(ILineDataSet set, boolean drawCircleHole, boolean drawTransparentCircleHole) {
            int colorCount = set.getCircleColorCount();
            float circleRadius = set.getCircleRadius();
            float circleHoleRadius = set.getCircleHoleRadius();
            for (int i = 0; i < colorCount; i++) {
                Bitmap circleBitmap = Bitmap.createBitmap((int) (((double) circleRadius) * 2.1d), (int) (((double) circleRadius) * 2.1d), Bitmap.Config.ARGB_4444);
                Canvas canvas = new Canvas(circleBitmap);
                this.circleBitmaps[i] = circleBitmap;
                LineChartRenderer.this.mRenderPaint.setColor(set.getCircleColor(i));
                if (drawTransparentCircleHole) {
                    this.mCirclePathBuffer.reset();
                    this.mCirclePathBuffer.addCircle(circleRadius, circleRadius, circleRadius, Path.Direction.CW);
                    this.mCirclePathBuffer.addCircle(circleRadius, circleRadius, circleHoleRadius, Path.Direction.CCW);
                    canvas.drawPath(this.mCirclePathBuffer, LineChartRenderer.this.mRenderPaint);
                } else {
                    canvas.drawCircle(circleRadius, circleRadius, circleRadius, LineChartRenderer.this.mRenderPaint);
                    if (drawCircleHole) {
                        canvas.drawCircle(circleRadius, circleRadius, circleHoleRadius, LineChartRenderer.this.mCirclePaintInner);
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public Bitmap getBitmap(int index) {
            return this.circleBitmaps[index % this.circleBitmaps.length];
        }
    }
}
