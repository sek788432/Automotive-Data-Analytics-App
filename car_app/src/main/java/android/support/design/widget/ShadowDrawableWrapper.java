package android.support.design.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.design.R;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.drawable.DrawableWrapper;

public class ShadowDrawableWrapper extends DrawableWrapper {
    static final double COS_45 = Math.cos(Math.toRadians(45.0d));
    static final float SHADOW_BOTTOM_SCALE = 1.0f;
    static final float SHADOW_HORIZ_SCALE = 0.5f;
    static final float SHADOW_MULTIPLIER = 1.5f;
    static final float SHADOW_TOP_SCALE = 0.25f;
    private boolean addPaddingForCorners = true;
    final RectF contentBounds;
    float cornerRadius;
    final Paint cornerShadowPaint;
    Path cornerShadowPath;
    private boolean dirty = true;
    final Paint edgeShadowPaint;
    float maxShadowSize;
    private boolean printedShadowClipWarning = false;
    float rawMaxShadowSize;
    float rawShadowSize;
    private float rotation;
    private final int shadowEndColor;
    private final int shadowMiddleColor;
    float shadowSize;
    private final int shadowStartColor;

    public ShadowDrawableWrapper(Context context, Drawable content, float radius, float shadowSize2, float maxShadowSize2) {
        super(content);
        this.shadowStartColor = ContextCompat.getColor(context, R.color.design_fab_shadow_start_color);
        this.shadowMiddleColor = ContextCompat.getColor(context, R.color.design_fab_shadow_mid_color);
        this.shadowEndColor = ContextCompat.getColor(context, R.color.design_fab_shadow_end_color);
        this.cornerShadowPaint = new Paint(5);
        this.cornerShadowPaint.setStyle(Paint.Style.FILL);
        this.cornerRadius = (float) Math.round(radius);
        this.contentBounds = new RectF();
        this.edgeShadowPaint = new Paint(this.cornerShadowPaint);
        this.edgeShadowPaint.setAntiAlias(false);
        setShadowSize(shadowSize2, maxShadowSize2);
    }

    private static int toEven(float value) {
        int i = Math.round(value);
        return i % 2 == 1 ? i - 1 : i;
    }

    public void setAddPaddingForCorners(boolean addPaddingForCorners2) {
        this.addPaddingForCorners = addPaddingForCorners2;
        invalidateSelf();
    }

    public void setAlpha(int alpha) {
        super.setAlpha(alpha);
        this.cornerShadowPaint.setAlpha(alpha);
        this.edgeShadowPaint.setAlpha(alpha);
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        this.dirty = true;
    }

    public void setShadowSize(float shadowSize2, float maxShadowSize2) {
        if (shadowSize2 < 0.0f || maxShadowSize2 < 0.0f) {
            throw new IllegalArgumentException("invalid shadow size");
        }
        float shadowSize3 = (float) toEven(shadowSize2);
        float maxShadowSize3 = (float) toEven(maxShadowSize2);
        if (shadowSize3 > maxShadowSize3) {
            shadowSize3 = maxShadowSize3;
            if (!this.printedShadowClipWarning) {
                this.printedShadowClipWarning = true;
            }
        }
        if (this.rawShadowSize != shadowSize3 || this.rawMaxShadowSize != maxShadowSize3) {
            this.rawShadowSize = shadowSize3;
            this.rawMaxShadowSize = maxShadowSize3;
            this.shadowSize = (float) Math.round(SHADOW_MULTIPLIER * shadowSize3);
            this.maxShadowSize = maxShadowSize3;
            this.dirty = true;
            invalidateSelf();
        }
    }

    public void setShadowSize(float size) {
        setShadowSize(size, this.rawMaxShadowSize);
    }

    public float getShadowSize() {
        return this.rawShadowSize;
    }

    public boolean getPadding(Rect padding) {
        int vOffset = (int) Math.ceil((double) calculateVerticalPadding(this.rawMaxShadowSize, this.cornerRadius, this.addPaddingForCorners));
        int hOffset = (int) Math.ceil((double) calculateHorizontalPadding(this.rawMaxShadowSize, this.cornerRadius, this.addPaddingForCorners));
        padding.set(hOffset, vOffset, hOffset, vOffset);
        return true;
    }

    public static float calculateVerticalPadding(float maxShadowSize2, float cornerRadius2, boolean addPaddingForCorners2) {
        if (addPaddingForCorners2) {
            return (float) (((double) (SHADOW_MULTIPLIER * maxShadowSize2)) + ((1.0d - COS_45) * ((double) cornerRadius2)));
        }
        return SHADOW_MULTIPLIER * maxShadowSize2;
    }

    public static float calculateHorizontalPadding(float maxShadowSize2, float cornerRadius2, boolean addPaddingForCorners2) {
        if (addPaddingForCorners2) {
            return (float) (((double) maxShadowSize2) + ((1.0d - COS_45) * ((double) cornerRadius2)));
        }
        return maxShadowSize2;
    }

    public int getOpacity() {
        return -3;
    }

    public void setCornerRadius(float radius) {
        float radius2 = (float) Math.round(radius);
        if (this.cornerRadius != radius2) {
            this.cornerRadius = radius2;
            this.dirty = true;
            invalidateSelf();
        }
    }

    public void draw(Canvas canvas) {
        if (this.dirty) {
            buildComponents(getBounds());
            this.dirty = false;
        }
        drawShadow(canvas);
        super.draw(canvas);
    }

    public final void setRotation(float rotation2) {
        if (this.rotation != rotation2) {
            this.rotation = rotation2;
            invalidateSelf();
        }
    }

    private void drawShadow(Canvas canvas) {
        float shadowScaleHorizontal;
        float shadowScaleTop;
        float shadowOffsetHorizontal;
        int saved;
        float shadowScaleBottom;
        float shadowScaleHorizontal2;
        Canvas canvas2 = canvas;
        int rotateSaved = canvas.save();
        canvas2.rotate(this.rotation, this.contentBounds.centerX(), this.contentBounds.centerY());
        float edgeShadowTop = (-this.cornerRadius) - this.shadowSize;
        float shadowOffset = this.cornerRadius;
        boolean z = false;
        boolean drawHorizontalEdges = this.contentBounds.width() - (shadowOffset * 2.0f) > 0.0f;
        if (this.contentBounds.height() - (shadowOffset * 2.0f) > 0.0f) {
            z = true;
        }
        boolean drawVerticalEdges = z;
        float shadowOffsetTop = this.rawShadowSize - (this.rawShadowSize * SHADOW_TOP_SCALE);
        float shadowOffsetHorizontal2 = this.rawShadowSize - (this.rawShadowSize * SHADOW_HORIZ_SCALE);
        float shadowScaleHorizontal3 = shadowOffset / (shadowOffset + shadowOffsetHorizontal2);
        float shadowScaleTop2 = shadowOffset / (shadowOffset + shadowOffsetTop);
        float shadowScaleBottom2 = shadowOffset / (shadowOffset + (this.rawShadowSize - (this.rawShadowSize * SHADOW_BOTTOM_SCALE)));
        int saved2 = canvas.save();
        canvas2.translate(this.contentBounds.left + shadowOffset, this.contentBounds.top + shadowOffset);
        canvas2.scale(shadowScaleHorizontal3, shadowScaleTop2);
        canvas2.drawPath(this.cornerShadowPath, this.cornerShadowPaint);
        if (drawHorizontalEdges) {
            canvas2.scale(SHADOW_BOTTOM_SCALE / shadowScaleHorizontal3, SHADOW_BOTTOM_SCALE);
            float shadowScaleTop3 = this.contentBounds.width() - (shadowOffset * 2.0f);
            Paint paint = this.edgeShadowPaint;
            float shadowScaleHorizontal4 = -this.cornerRadius;
            float f = shadowOffsetTop;
            saved = saved2;
            shadowScaleBottom = shadowScaleBottom2;
            shadowScaleTop = shadowScaleTop2;
            shadowScaleHorizontal = shadowScaleHorizontal3;
            float f2 = shadowOffsetHorizontal2;
            shadowOffsetHorizontal = SHADOW_BOTTOM_SCALE;
            canvas.drawRect(0.0f, edgeShadowTop, shadowScaleTop3, shadowScaleHorizontal4, paint);
        } else {
            shadowScaleBottom = shadowScaleBottom2;
            shadowScaleTop = shadowScaleTop2;
            shadowScaleHorizontal = shadowScaleHorizontal3;
            float f3 = shadowOffsetTop;
            float f4 = shadowOffsetHorizontal2;
            saved = saved2;
            shadowOffsetHorizontal = 1.0f;
        }
        canvas2.restoreToCount(saved);
        int saved3 = canvas.save();
        canvas2.translate(this.contentBounds.right - shadowOffset, this.contentBounds.bottom - shadowOffset);
        float shadowScaleHorizontal5 = shadowScaleHorizontal;
        canvas2.scale(shadowScaleHorizontal5, shadowScaleBottom);
        canvas2.rotate(180.0f);
        canvas2.drawPath(this.cornerShadowPath, this.cornerShadowPaint);
        if (drawHorizontalEdges) {
            canvas2.scale(shadowOffsetHorizontal / shadowScaleHorizontal5, shadowOffsetHorizontal);
            shadowScaleHorizontal2 = shadowScaleHorizontal5;
            canvas.drawRect(0.0f, edgeShadowTop, this.contentBounds.width() - (shadowOffset * 2.0f), (-this.cornerRadius) + this.shadowSize, this.edgeShadowPaint);
        } else {
            shadowScaleHorizontal2 = shadowScaleHorizontal5;
        }
        canvas2.restoreToCount(saved3);
        int saved4 = canvas.save();
        canvas2.translate(this.contentBounds.left + shadowOffset, this.contentBounds.bottom - shadowOffset);
        canvas2.scale(shadowScaleHorizontal2, shadowScaleBottom);
        canvas2.rotate(270.0f);
        canvas2.drawPath(this.cornerShadowPath, this.cornerShadowPaint);
        if (drawVerticalEdges) {
            canvas2.scale(SHADOW_BOTTOM_SCALE / shadowScaleBottom, SHADOW_BOTTOM_SCALE);
            canvas.drawRect(0.0f, edgeShadowTop, this.contentBounds.height() - (shadowOffset * 2.0f), -this.cornerRadius, this.edgeShadowPaint);
        }
        canvas2.restoreToCount(saved4);
        int saved5 = canvas.save();
        canvas2.translate(this.contentBounds.right - shadowOffset, this.contentBounds.top + shadowOffset);
        float shadowScaleTop4 = shadowScaleTop;
        canvas2.scale(shadowScaleHorizontal2, shadowScaleTop4);
        canvas2.rotate(90.0f);
        canvas2.drawPath(this.cornerShadowPath, this.cornerShadowPaint);
        if (drawVerticalEdges) {
            canvas2.scale(SHADOW_BOTTOM_SCALE / shadowScaleTop4, SHADOW_BOTTOM_SCALE);
            float f5 = shadowScaleTop4;
            canvas.drawRect(0.0f, edgeShadowTop, this.contentBounds.height() - (2.0f * shadowOffset), -this.cornerRadius, this.edgeShadowPaint);
        }
        canvas2.restoreToCount(saved5);
        canvas2.restoreToCount(rotateSaved);
    }

    private void buildShadowCorners() {
        int i;
        RectF innerBounds = new RectF(-this.cornerRadius, -this.cornerRadius, this.cornerRadius, this.cornerRadius);
        RectF outerBounds = new RectF(innerBounds);
        outerBounds.inset(-this.shadowSize, -this.shadowSize);
        if (this.cornerShadowPath == null) {
            this.cornerShadowPath = new Path();
        } else {
            this.cornerShadowPath.reset();
        }
        this.cornerShadowPath.setFillType(Path.FillType.EVEN_ODD);
        this.cornerShadowPath.moveTo(-this.cornerRadius, 0.0f);
        this.cornerShadowPath.rLineTo(-this.shadowSize, 0.0f);
        this.cornerShadowPath.arcTo(outerBounds, 180.0f, 90.0f, false);
        this.cornerShadowPath.arcTo(innerBounds, 270.0f, -90.0f, false);
        this.cornerShadowPath.close();
        float shadowRadius = -outerBounds.top;
        if (shadowRadius > 0.0f) {
            float startRatio = this.cornerRadius / shadowRadius;
            float midRatio = startRatio + ((SHADOW_BOTTOM_SCALE - startRatio) / 2.0f);
            Paint paint = this.cornerShadowPaint;
            RadialGradient radialGradient = r8;
            i = 3;
            RadialGradient radialGradient2 = new RadialGradient(0.0f, 0.0f, shadowRadius, new int[]{0, this.shadowStartColor, this.shadowMiddleColor, this.shadowEndColor}, new float[]{0.0f, startRatio, midRatio, 1.0f}, Shader.TileMode.CLAMP);
            paint.setShader(radialGradient);
        } else {
            i = 3;
        }
        Paint paint2 = this.edgeShadowPaint;
        float f = innerBounds.top;
        float f2 = outerBounds.top;
        int[] iArr = new int[i];
        iArr[0] = this.shadowStartColor;
        iArr[1] = this.shadowMiddleColor;
        iArr[2] = this.shadowEndColor;
        float[] fArr = new float[i];
        // fill-array-data instruction
        fArr[0] = 0;
        fArr[1] = 1056964608;
        fArr[2] = 1065353216;
        paint2.setShader(new LinearGradient(0.0f, f, 0.0f, f2, iArr, fArr, Shader.TileMode.CLAMP));
        this.edgeShadowPaint.setAntiAlias(false);
    }

    private void buildComponents(Rect bounds) {
        float verticalOffset = this.rawMaxShadowSize * SHADOW_MULTIPLIER;
        this.contentBounds.set(((float) bounds.left) + this.rawMaxShadowSize, ((float) bounds.top) + verticalOffset, ((float) bounds.right) - this.rawMaxShadowSize, ((float) bounds.bottom) - verticalOffset);
        getWrappedDrawable().setBounds((int) this.contentBounds.left, (int) this.contentBounds.top, (int) this.contentBounds.right, (int) this.contentBounds.bottom);
        buildShadowCorners();
    }

    public float getCornerRadius() {
        return this.cornerRadius;
    }

    public void setMaxShadowSize(float size) {
        setShadowSize(this.rawShadowSize, size);
    }

    public float getMaxShadowSize() {
        return this.rawMaxShadowSize;
    }

    public float getMinWidth() {
        return (this.rawMaxShadowSize * 2.0f) + (Math.max(this.rawMaxShadowSize, this.cornerRadius + (this.rawMaxShadowSize / 2.0f)) * 2.0f);
    }

    public float getMinHeight() {
        return (this.rawMaxShadowSize * SHADOW_MULTIPLIER * 2.0f) + (Math.max(this.rawMaxShadowSize, this.cornerRadius + ((this.rawMaxShadowSize * SHADOW_MULTIPLIER) / 2.0f)) * 2.0f);
    }
}
