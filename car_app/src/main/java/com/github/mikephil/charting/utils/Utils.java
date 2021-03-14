package com.github.mikephil.charting.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import java.util.List;

public abstract class Utils {
    public static final double DEG2RAD = 0.017453292519943295d;
    public static final double DOUBLE_EPSILON = Double.longBitsToDouble(1);
    public static final float FDEG2RAD = 0.017453292f;
    public static final float FLOAT_EPSILON = Float.intBitsToFloat(1);
    private static final int[] POW_10 = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000};
    private static Rect mCalcTextHeightRect = new Rect();
    private static Rect mCalcTextSizeRect = new Rect();
    private static IValueFormatter mDefaultValueFormatter = generateDefaultValueFormatter();
    private static Rect mDrawTextRectBuffer = new Rect();
    private static Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();
    private static Paint.FontMetrics mFontMetricsBuffer = new Paint.FontMetrics();
    private static int mMaximumFlingVelocity = 8000;
    private static DisplayMetrics mMetrics;
    private static int mMinimumFlingVelocity = 50;

    public static void init(Context context) {
        if (context == null) {
            mMinimumFlingVelocity = ViewConfiguration.getMinimumFlingVelocity();
            mMaximumFlingVelocity = ViewConfiguration.getMaximumFlingVelocity();
            Log.e("MPChartLib-Utils", "Utils.init(...) PROVIDED CONTEXT OBJECT IS NULL");
            return;
        }
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        mMetrics = context.getResources().getDisplayMetrics();
    }

    @Deprecated
    public static void init(Resources res) {
        mMetrics = res.getDisplayMetrics();
        mMinimumFlingVelocity = ViewConfiguration.getMinimumFlingVelocity();
        mMaximumFlingVelocity = ViewConfiguration.getMaximumFlingVelocity();
    }

    public static float convertDpToPixel(float dp) {
        if (mMetrics != null) {
            return (((float) mMetrics.densityDpi) / 160.0f) * dp;
        }
        Log.e("MPChartLib-Utils", "Utils NOT INITIALIZED. You need to call Utils.init(...) at least once before calling Utils.convertDpToPixel(...). Otherwise conversion does not take place.");
        return dp;
    }

    public static float convertPixelsToDp(float px) {
        if (mMetrics != null) {
            return px / (((float) mMetrics.densityDpi) / 160.0f);
        }
        Log.e("MPChartLib-Utils", "Utils NOT INITIALIZED. You need to call Utils.init(...) at least once before calling Utils.convertPixelsToDp(...). Otherwise conversion does not take place.");
        return px;
    }

    public static int calcTextWidth(Paint paint, String demoText) {
        return (int) paint.measureText(demoText);
    }

    public static int calcTextHeight(Paint paint, String demoText) {
        Rect r = mCalcTextHeightRect;
        r.set(0, 0, 0, 0);
        paint.getTextBounds(demoText, 0, demoText.length(), r);
        return r.height();
    }

    public static float getLineHeight(Paint paint) {
        return getLineHeight(paint, mFontMetrics);
    }

    public static float getLineHeight(Paint paint, Paint.FontMetrics fontMetrics) {
        paint.getFontMetrics(fontMetrics);
        return fontMetrics.descent - fontMetrics.ascent;
    }

    public static float getLineSpacing(Paint paint) {
        return getLineSpacing(paint, mFontMetrics);
    }

    public static float getLineSpacing(Paint paint, Paint.FontMetrics fontMetrics) {
        paint.getFontMetrics(fontMetrics);
        return (fontMetrics.ascent - fontMetrics.top) + fontMetrics.bottom;
    }

    public static FSize calcTextSize(Paint paint, String demoText) {
        FSize result = FSize.getInstance(0.0f, 0.0f);
        calcTextSize(paint, demoText, result);
        return result;
    }

    public static void calcTextSize(Paint paint, String demoText, FSize outputFSize) {
        Rect r = mCalcTextSizeRect;
        r.set(0, 0, 0, 0);
        paint.getTextBounds(demoText, 0, demoText.length(), r);
        outputFSize.width = (float) r.width();
        outputFSize.height = (float) r.height();
    }

    private static IValueFormatter generateDefaultValueFormatter() {
        return new DefaultValueFormatter(1);
    }

    public static IValueFormatter getDefaultValueFormatter() {
        return mDefaultValueFormatter;
    }

    public static String formatNumber(float number, int digitCount, boolean separateThousands) {
        return formatNumber(number, digitCount, separateThousands, '.');
    }

    public static String formatNumber(float number, int digitCount, boolean separateThousands, char separateChar) {
        int digitCount2;
        int ind;
        float number2 = number;
        char[] out = new char[35];
        boolean neg = false;
        if (number2 == 0.0f) {
            return "0";
        }
        boolean zero = false;
        if (number2 < 1.0f && number2 > -1.0f) {
            zero = true;
        }
        if (number2 < 0.0f) {
            neg = true;
            number2 = -number2;
        }
        int i = digitCount;
        if (i > POW_10.length) {
            digitCount2 = POW_10.length - 1;
        } else {
            digitCount2 = i;
        }
        long lval = (long) Math.round(number2 * ((float) POW_10[digitCount2]));
        int ind2 = out.length - 1;
        int charCount = 0;
        boolean decimalPointAdded = false;
        while (true) {
            if (lval == 0 && charCount >= digitCount2 + 1) {
                break;
            }
            lval /= 10;
            int ind3 = ind2 - 1;
            out[ind2] = (char) (((int) (lval % 10)) + 48);
            charCount++;
            if (charCount == digitCount2) {
                out[ind3] = ',';
                charCount++;
                decimalPointAdded = true;
                ind = ind3 - 1;
            } else {
                if (separateThousands && lval != 0 && charCount > digitCount2) {
                    if (decimalPointAdded) {
                        if ((charCount - digitCount2) % 4 == 0) {
                            ind = ind3 - 1;
                            out[ind3] = separateChar;
                            charCount++;
                        }
                    } else if ((charCount - digitCount2) % 4 == 3) {
                        ind = ind3 - 1;
                        out[ind3] = separateChar;
                        charCount++;
                    }
                }
                ind = ind3;
            }
            ind2 = ind;
        }
        if (zero) {
            out[ind2] = '0';
            charCount++;
            ind2--;
        }
        if (neg) {
            out[ind2] = '-';
            charCount++;
            int i2 = ind2 - 1;
        }
        int start = out.length - charCount;
        return String.valueOf(out, start, out.length - start);
    }

    public static float roundToNextSignificant(double number) {
        if (Double.isInfinite(number) || Double.isNaN(number) || number == DOUBLE_EPSILON) {
            return 0.0f;
        }
        float magnitude = (float) Math.pow(10.0d, (double) (1 - ((int) ((float) Math.ceil((double) ((float) Math.log10(number < DOUBLE_EPSILON ? -number : number)))))));
        return ((float) Math.round(((double) magnitude) * number)) / magnitude;
    }

    public static int getDecimals(float number) {
        float i = roundToNextSignificant((double) number);
        if (Float.isInfinite(i)) {
            return 0;
        }
        return ((int) Math.ceil(-Math.log10((double) i))) + 2;
    }

    public static int[] convertIntegers(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        copyIntegers(integers, ret);
        return ret;
    }

    public static void copyIntegers(List<Integer> from, int[] to) {
        int count = to.length < from.size() ? to.length : from.size();
        for (int i = 0; i < count; i++) {
            to[i] = from.get(i).intValue();
        }
    }

    public static String[] convertStrings(List<String> strings) {
        String[] ret = new String[strings.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = strings.get(i);
        }
        return ret;
    }

    public static void copyStrings(List<String> from, String[] to) {
        int count = to.length < from.size() ? to.length : from.size();
        for (int i = 0; i < count; i++) {
            to[i] = from.get(i);
        }
    }

    public static double nextUp(double d) {
        if (d == Double.POSITIVE_INFINITY) {
            return d;
        }
        double d2 = d + DOUBLE_EPSILON;
        return Double.longBitsToDouble(Double.doubleToRawLongBits(d2) + (d2 >= DOUBLE_EPSILON ? 1 : -1));
    }

    public static MPPointF getPosition(MPPointF center, float dist, float angle) {
        MPPointF p = MPPointF.getInstance(0.0f, 0.0f);
        getPosition(center, dist, angle, p);
        return p;
    }

    public static void getPosition(MPPointF center, float dist, float angle, MPPointF outputPoint) {
        outputPoint.x = (float) (((double) center.x) + (((double) dist) * Math.cos(Math.toRadians((double) angle))));
        outputPoint.y = (float) (((double) center.y) + (((double) dist) * Math.sin(Math.toRadians((double) angle))));
    }

    public static void velocityTrackerPointerUpCleanUpIfNecessary(MotionEvent ev, VelocityTracker tracker) {
        tracker.computeCurrentVelocity(1000, (float) mMaximumFlingVelocity);
        int upIndex = ev.getActionIndex();
        int id1 = ev.getPointerId(upIndex);
        float x1 = tracker.getXVelocity(id1);
        float y1 = tracker.getYVelocity(id1);
        int count = ev.getPointerCount();
        for (int i = 0; i < count; i++) {
            if (i != upIndex) {
                int id2 = ev.getPointerId(i);
                if ((tracker.getXVelocity(id2) * x1) + (tracker.getYVelocity(id2) * y1) < 0.0f) {
                    tracker.clear();
                    return;
                }
            }
        }
    }

    @SuppressLint({"NewApi"})
    public static void postInvalidateOnAnimation(View view) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.postInvalidateOnAnimation();
        } else {
            view.postInvalidateDelayed(10);
        }
    }

    public static int getMinimumFlingVelocity() {
        return mMinimumFlingVelocity;
    }

    public static int getMaximumFlingVelocity() {
        return mMaximumFlingVelocity;
    }

    public static float getNormalizedAngle(float angle) {
        while (angle < 0.0f) {
            angle += 360.0f;
        }
        return angle % 360.0f;
    }

    public static void drawXAxisValue(Canvas c, String text, float x, float y, Paint paint, MPPointF anchor, float angleDegrees) {
        Canvas canvas = c;
        String str = text;
        Paint paint2 = paint;
        MPPointF mPPointF = anchor;
        float f = angleDegrees;
        float lineHeight = paint2.getFontMetrics(mFontMetricsBuffer);
        paint2.getTextBounds(str, 0, text.length(), mDrawTextRectBuffer);
        float drawOffsetX = 0.0f - ((float) mDrawTextRectBuffer.left);
        float drawOffsetY = 0.0f + (-mFontMetricsBuffer.ascent);
        Paint.Align originalTextAlign = paint.getTextAlign();
        paint2.setTextAlign(Paint.Align.LEFT);
        if (f != 0.0f) {
            float drawOffsetX2 = drawOffsetX - (((float) mDrawTextRectBuffer.width()) * 0.5f);
            float drawOffsetY2 = drawOffsetY - (lineHeight * 0.5f);
            float translateX = x;
            float translateY = y;
            if (!(mPPointF.x == 0.5f && mPPointF.y == 0.5f)) {
                FSize rotatedSize = getSizeOfRotatedRectangleByDegrees((float) mDrawTextRectBuffer.width(), lineHeight, f);
                translateX -= rotatedSize.width * (mPPointF.x - 0.5f);
                translateY -= rotatedSize.height * (mPPointF.y - 0.5f);
                FSize.recycleInstance(rotatedSize);
            }
            c.save();
            c.translate(translateX, translateY);
            c.rotate(f);
            c.drawText(str, drawOffsetX2, drawOffsetY2, paint2);
            c.restore();
        } else {
            if (!(mPPointF.x == 0.0f && mPPointF.y == 0.0f)) {
                drawOffsetX -= ((float) mDrawTextRectBuffer.width()) * mPPointF.x;
                drawOffsetY -= mPPointF.y * lineHeight;
            }
            c.drawText(str, drawOffsetX + x, drawOffsetY + y, paint2);
        }
        paint2.setTextAlign(originalTextAlign);
    }

    public static void drawMultilineText(Canvas c, StaticLayout textLayout, float x, float y, TextPaint paint, MPPointF anchor, float angleDegrees) {
        Canvas canvas = c;
        StaticLayout staticLayout = textLayout;
        TextPaint textPaint = paint;
        MPPointF mPPointF = anchor;
        float f = angleDegrees;
        float lineHeight = textPaint.getFontMetrics(mFontMetricsBuffer);
        float drawWidth = (float) textLayout.getWidth();
        float drawHeight = ((float) textLayout.getLineCount()) * lineHeight;
        float drawOffsetX = 0.0f - ((float) mDrawTextRectBuffer.left);
        float drawOffsetY = 0.0f + drawHeight;
        Paint.Align originalTextAlign = paint.getTextAlign();
        textPaint.setTextAlign(Paint.Align.LEFT);
        if (f != 0.0f) {
            float drawOffsetX2 = drawOffsetX - (drawWidth * 0.5f);
            float drawOffsetY2 = drawOffsetY - (drawHeight * 0.5f);
            float translateX = x;
            float translateY = y;
            if (mPPointF.x == 0.5f && mPPointF.y == 0.5f) {
                float f2 = lineHeight;
            } else {
                FSize rotatedSize = getSizeOfRotatedRectangleByDegrees(drawWidth, drawHeight, f);
                float f3 = lineHeight;
                translateX -= rotatedSize.width * (mPPointF.x - 0.5f);
                translateY -= rotatedSize.height * (mPPointF.y - 0.5f);
                FSize.recycleInstance(rotatedSize);
            }
            c.save();
            canvas.translate(translateX, translateY);
            canvas.rotate(f);
            canvas.translate(drawOffsetX2, drawOffsetY2);
            staticLayout.draw(canvas);
            c.restore();
        } else {
            if (!(mPPointF.x == 0.0f && mPPointF.y == 0.0f)) {
                drawOffsetX -= mPPointF.x * drawWidth;
                drawOffsetY -= mPPointF.y * drawHeight;
            }
            c.save();
            canvas.translate(drawOffsetX + x, drawOffsetY + y);
            staticLayout.draw(canvas);
            c.restore();
        }
        textPaint.setTextAlign(originalTextAlign);
    }

    public static void drawMultilineText(Canvas c, String text, float x, float y, TextPaint paint, FSize constrainedToSize, MPPointF anchor, float angleDegrees) {
        TextPaint textPaint = paint;
        drawMultilineText(c, new StaticLayout(text, 0, text.length(), textPaint, (int) Math.max(Math.ceil((double) constrainedToSize.width), 1.0d), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false), x, y, textPaint, anchor, angleDegrees);
    }

    public static FSize getSizeOfRotatedRectangleByDegrees(FSize rectangleSize, float degrees) {
        return getSizeOfRotatedRectangleByRadians(rectangleSize.width, rectangleSize.height, 0.017453292f * degrees);
    }

    public static FSize getSizeOfRotatedRectangleByRadians(FSize rectangleSize, float radians) {
        return getSizeOfRotatedRectangleByRadians(rectangleSize.width, rectangleSize.height, radians);
    }

    public static FSize getSizeOfRotatedRectangleByDegrees(float rectangleWidth, float rectangleHeight, float degrees) {
        return getSizeOfRotatedRectangleByRadians(rectangleWidth, rectangleHeight, 0.017453292f * degrees);
    }

    public static FSize getSizeOfRotatedRectangleByRadians(float rectangleWidth, float rectangleHeight, float radians) {
        return FSize.getInstance(Math.abs(((float) Math.cos((double) radians)) * rectangleWidth) + Math.abs(((float) Math.sin((double) radians)) * rectangleHeight), Math.abs(((float) Math.sin((double) radians)) * rectangleWidth) + Math.abs(((float) Math.cos((double) radians)) * rectangleHeight));
    }

    public static int getSDKInt() {
        return Build.VERSION.SDK_INT;
    }
}
