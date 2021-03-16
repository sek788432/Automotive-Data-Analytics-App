package android.support.design.widget;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.ColorUtils;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class CircularBorderDrawable extends Drawable {
    private static final float DRAW_STROKE_WIDTH_MULTIPLE = 1.3333f;
    private ColorStateList borderTint;
    @Dimension
    float borderWidth;
    @ColorInt
    private int bottomInnerStrokeColor;
    @ColorInt
    private int bottomOuterStrokeColor;
    @ColorInt
    private int currentBorderTintColor;
    private boolean invalidateShader = true;
    final Paint paint = new Paint(1);
    final Rect rect = new Rect();
    final RectF rectF = new RectF();
    @FloatRange(from = 0.0d, to = 360.0d)
    private float rotation;
    final CircularBorderState state = new CircularBorderState();
    @ColorInt
    private int topInnerStrokeColor;
    @ColorInt
    private int topOuterStrokeColor;

    public CircularBorderDrawable() {
        this.paint.setStyle(Paint.Style.STROKE);
    }

    @Nullable
    public Drawable.ConstantState getConstantState() {
        return this.state;
    }

    public void setGradientColors(@ColorInt int topOuterStrokeColor2, @ColorInt int topInnerStrokeColor2, @ColorInt int bottomOuterStrokeColor2, @ColorInt int bottomInnerStrokeColor2) {
        this.topOuterStrokeColor = topOuterStrokeColor2;
        this.topInnerStrokeColor = topInnerStrokeColor2;
        this.bottomOuterStrokeColor = bottomOuterStrokeColor2;
        this.bottomInnerStrokeColor = bottomInnerStrokeColor2;
    }

    public void setBorderWidth(@Dimension float width) {
        if (this.borderWidth != width) {
            this.borderWidth = width;
            this.paint.setStrokeWidth(DRAW_STROKE_WIDTH_MULTIPLE * width);
            this.invalidateShader = true;
            invalidateSelf();
        }
    }

    public void draw(Canvas canvas) {
        if (this.invalidateShader) {
            this.paint.setShader(createGradientShader());
            this.invalidateShader = false;
        }
        float halfBorderWidth = this.paint.getStrokeWidth() / 2.0f;
        RectF rectF2 = this.rectF;
        copyBounds(this.rect);
        rectF2.set(this.rect);
        rectF2.left += halfBorderWidth;
        rectF2.top += halfBorderWidth;
        rectF2.right -= halfBorderWidth;
        rectF2.bottom -= halfBorderWidth;
        canvas.save();
        canvas.rotate(this.rotation, rectF2.centerX(), rectF2.centerY());
        canvas.drawOval(rectF2, this.paint);
        canvas.restore();
    }

    public boolean getPadding(Rect padding) {
        int borderWidth2 = Math.round(this.borderWidth);
        padding.set(borderWidth2, borderWidth2, borderWidth2, borderWidth2);
        return true;
    }

    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        this.paint.setAlpha(alpha);
        invalidateSelf();
    }

    public void setBorderTint(ColorStateList tint) {
        if (tint != null) {
            this.currentBorderTintColor = tint.getColorForState(getState(), this.currentBorderTintColor);
        }
        this.borderTint = tint;
        this.invalidateShader = true;
        invalidateSelf();
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    public int getOpacity() {
        return this.borderWidth > 0.0f ? -3 : -2;
    }

    public final void setRotation(float rotation2) {
        if (rotation2 != this.rotation) {
            this.rotation = rotation2;
            invalidateSelf();
        }
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        this.invalidateShader = true;
    }

    public boolean isStateful() {
        return (this.borderTint != null && this.borderTint.isStateful()) || super.isStateful();
    }

    /* access modifiers changed from: protected */
    public boolean onStateChange(int[] state2) {
        int newColor;
        if (!(this.borderTint == null || (newColor = this.borderTint.getColorForState(state2, this.currentBorderTintColor)) == this.currentBorderTintColor)) {
            this.invalidateShader = true;
            this.currentBorderTintColor = newColor;
        }
        if (this.invalidateShader != 0) {
            invalidateSelf();
        }
        return this.invalidateShader;
    }

    private Shader createGradientShader() {
        Rect rect2 = this.rect;
        copyBounds(rect2);
        float borderRatio = this.borderWidth / ((float) rect2.height());
        return new LinearGradient(0.0f, (float) rect2.top, 0.0f, (float) rect2.bottom, new int[]{ColorUtils.compositeColors(this.topOuterStrokeColor, this.currentBorderTintColor), ColorUtils.compositeColors(this.topInnerStrokeColor, this.currentBorderTintColor), ColorUtils.compositeColors(ColorUtils.setAlphaComponent(this.topInnerStrokeColor, 0), this.currentBorderTintColor), ColorUtils.compositeColors(ColorUtils.setAlphaComponent(this.bottomInnerStrokeColor, 0), this.currentBorderTintColor), ColorUtils.compositeColors(this.bottomInnerStrokeColor, this.currentBorderTintColor), ColorUtils.compositeColors(this.bottomOuterStrokeColor, this.currentBorderTintColor)}, new float[]{0.0f, borderRatio, 0.5f, 0.5f, 1.0f - borderRatio, 1.0f}, Shader.TileMode.CLAMP);
    }

    private class CircularBorderState extends Drawable.ConstantState {
        private CircularBorderState() {
        }

        @NonNull
        public Drawable newDrawable() {
            return CircularBorderDrawable.this;
        }

        public int getChangingConfigurations() {
            return 0;
        }
    }
}
