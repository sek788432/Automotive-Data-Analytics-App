package android.support.design.widget;

import android.animation.TimeInterpolator;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.design.animation.AnimationUtils;
import android.support.v4.math.MathUtils;
import android.support.v4.text.TextDirectionHeuristicsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.support.v7.widget.TintTypedArray;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public final class CollapsingTextHelper {
    private static final boolean DEBUG_DRAW = false;
    private static final Paint DEBUG_DRAW_PAINT = null;
    private static final boolean USE_SCALING_TEXTURE = (Build.VERSION.SDK_INT < 18);
    private boolean boundsChanged;
    private final Rect collapsedBounds;
    private float collapsedDrawX;
    private float collapsedDrawY;
    private int collapsedShadowColor;
    private float collapsedShadowDx;
    private float collapsedShadowDy;
    private float collapsedShadowRadius;
    private ColorStateList collapsedTextColor;
    private int collapsedTextGravity = 16;
    private float collapsedTextSize = 15.0f;
    private Typeface collapsedTypeface;
    private final RectF currentBounds;
    private float currentDrawX;
    private float currentDrawY;
    private float currentTextSize;
    private Typeface currentTypeface;
    private boolean drawTitle;
    private final Rect expandedBounds;
    private float expandedDrawX;
    private float expandedDrawY;
    private float expandedFraction;
    private int expandedShadowColor;
    private float expandedShadowDx;
    private float expandedShadowDy;
    private float expandedShadowRadius;
    private ColorStateList expandedTextColor;
    private int expandedTextGravity = 16;
    private float expandedTextSize = 15.0f;
    private Bitmap expandedTitleTexture;
    private Typeface expandedTypeface;
    private boolean isRtl;
    private TimeInterpolator positionInterpolator;
    private float scale;
    private int[] state;
    private CharSequence text;
    private final TextPaint textPaint;
    private TimeInterpolator textSizeInterpolator;
    private CharSequence textToDraw;
    private float textureAscent;
    private float textureDescent;
    private Paint texturePaint;
    private final TextPaint tmpPaint;
    private boolean useTexture;
    private final View view;

    static {
        if (DEBUG_DRAW_PAINT != null) {
            DEBUG_DRAW_PAINT.setAntiAlias(true);
            DEBUG_DRAW_PAINT.setColor(-65281);
        }
    }

    public CollapsingTextHelper(View view2) {
        this.view = view2;
        this.textPaint = new TextPaint(129);
        this.tmpPaint = new TextPaint(this.textPaint);
        this.collapsedBounds = new Rect();
        this.expandedBounds = new Rect();
        this.currentBounds = new RectF();
    }

    public void setTextSizeInterpolator(TimeInterpolator interpolator) {
        this.textSizeInterpolator = interpolator;
        recalculate();
    }

    public void setPositionInterpolator(TimeInterpolator interpolator) {
        this.positionInterpolator = interpolator;
        recalculate();
    }

    public void setExpandedTextSize(float textSize) {
        if (this.expandedTextSize != textSize) {
            this.expandedTextSize = textSize;
            recalculate();
        }
    }

    public void setCollapsedTextSize(float textSize) {
        if (this.collapsedTextSize != textSize) {
            this.collapsedTextSize = textSize;
            recalculate();
        }
    }

    public void setCollapsedTextColor(ColorStateList textColor) {
        if (this.collapsedTextColor != textColor) {
            this.collapsedTextColor = textColor;
            recalculate();
        }
    }

    public void setExpandedTextColor(ColorStateList textColor) {
        if (this.expandedTextColor != textColor) {
            this.expandedTextColor = textColor;
            recalculate();
        }
    }

    public void setExpandedBounds(int left, int top, int right, int bottom) {
        if (!rectEquals(this.expandedBounds, left, top, right, bottom)) {
            this.expandedBounds.set(left, top, right, bottom);
            this.boundsChanged = true;
            onBoundsChanged();
        }
    }

    public void setCollapsedBounds(int left, int top, int right, int bottom) {
        if (!rectEquals(this.collapsedBounds, left, top, right, bottom)) {
            this.collapsedBounds.set(left, top, right, bottom);
            this.boundsChanged = true;
            onBoundsChanged();
        }
    }

    public float calculateCollapsedTextWidth() {
        if (this.text == null) {
            return 0.0f;
        }
        getTextPaintCollapsed(this.tmpPaint);
        return this.tmpPaint.measureText(this.text, 0, this.text.length());
    }

    public float getCollapsedTextHeight() {
        getTextPaintCollapsed(this.tmpPaint);
        return -this.tmpPaint.ascent();
    }

    public void getCollapsedTextActualBounds(RectF bounds) {
        float f;
        boolean isRtl2 = calculateIsRtl(this.text);
        if (!isRtl2) {
            f = (float) this.collapsedBounds.left;
        } else {
            f = ((float) this.collapsedBounds.right) - calculateCollapsedTextWidth();
        }
        bounds.left = f;
        bounds.top = (float) this.collapsedBounds.top;
        bounds.right = !isRtl2 ? bounds.left + calculateCollapsedTextWidth() : (float) this.collapsedBounds.right;
        bounds.bottom = ((float) this.collapsedBounds.top) + getCollapsedTextHeight();
    }

    private void getTextPaintCollapsed(TextPaint textPaint2) {
        textPaint2.setTextSize(this.collapsedTextSize);
        textPaint2.setTypeface(this.collapsedTypeface);
    }

    /* access modifiers changed from: package-private */
    public void onBoundsChanged() {
        this.drawTitle = this.collapsedBounds.width() > 0 && this.collapsedBounds.height() > 0 && this.expandedBounds.width() > 0 && this.expandedBounds.height() > 0;
    }

    public void setExpandedTextGravity(int gravity) {
        if (this.expandedTextGravity != gravity) {
            this.expandedTextGravity = gravity;
            recalculate();
        }
    }

    public int getExpandedTextGravity() {
        return this.expandedTextGravity;
    }

    public void setCollapsedTextGravity(int gravity) {
        if (this.collapsedTextGravity != gravity) {
            this.collapsedTextGravity = gravity;
            recalculate();
        }
    }

    public int getCollapsedTextGravity() {
        return this.collapsedTextGravity;
    }

    public void setCollapsedTextAppearance(int resId) {
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(this.view.getContext(), resId, R.styleable.TextAppearance);
        if (a.hasValue(R.styleable.TextAppearance_android_textColor)) {
            this.collapsedTextColor = a.getColorStateList(R.styleable.TextAppearance_android_textColor);
        }
        if (a.hasValue(R.styleable.TextAppearance_android_textSize)) {
            this.collapsedTextSize = (float) a.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, (int) this.collapsedTextSize);
        }
        this.collapsedShadowColor = a.getInt(R.styleable.TextAppearance_android_shadowColor, 0);
        this.collapsedShadowDx = a.getFloat(R.styleable.TextAppearance_android_shadowDx, 0.0f);
        this.collapsedShadowDy = a.getFloat(R.styleable.TextAppearance_android_shadowDy, 0.0f);
        this.collapsedShadowRadius = a.getFloat(R.styleable.TextAppearance_android_shadowRadius, 0.0f);
        a.recycle();
        if (Build.VERSION.SDK_INT >= 16) {
            this.collapsedTypeface = readFontFamilyTypeface(resId);
        }
        recalculate();
    }

    public void setExpandedTextAppearance(int resId) {
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(this.view.getContext(), resId, R.styleable.TextAppearance);
        if (a.hasValue(R.styleable.TextAppearance_android_textColor)) {
            this.expandedTextColor = a.getColorStateList(R.styleable.TextAppearance_android_textColor);
        }
        if (a.hasValue(R.styleable.TextAppearance_android_textSize)) {
            this.expandedTextSize = (float) a.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, (int) this.expandedTextSize);
        }
        this.expandedShadowColor = a.getInt(R.styleable.TextAppearance_android_shadowColor, 0);
        this.expandedShadowDx = a.getFloat(R.styleable.TextAppearance_android_shadowDx, 0.0f);
        this.expandedShadowDy = a.getFloat(R.styleable.TextAppearance_android_shadowDy, 0.0f);
        this.expandedShadowRadius = a.getFloat(R.styleable.TextAppearance_android_shadowRadius, 0.0f);
        a.recycle();
        if (Build.VERSION.SDK_INT >= 16) {
            this.expandedTypeface = readFontFamilyTypeface(resId);
        }
        recalculate();
    }

    private Typeface readFontFamilyTypeface(int resId) {
        TypedArray a = this.view.getContext().obtainStyledAttributes(resId, new int[]{16843692});
        try {
            String family = a.getString(0);
            if (family != null) {
                return Typeface.create(family, 0);
            }
            a.recycle();
            return null;
        } finally {
            a.recycle();
        }
    }

    public void setCollapsedTypeface(Typeface typeface) {
        if (this.collapsedTypeface != typeface) {
            this.collapsedTypeface = typeface;
            recalculate();
        }
    }

    public void setExpandedTypeface(Typeface typeface) {
        if (this.expandedTypeface != typeface) {
            this.expandedTypeface = typeface;
            recalculate();
        }
    }

    public void setTypefaces(Typeface typeface) {
        this.expandedTypeface = typeface;
        this.collapsedTypeface = typeface;
        recalculate();
    }

    public Typeface getCollapsedTypeface() {
        return this.collapsedTypeface != null ? this.collapsedTypeface : Typeface.DEFAULT;
    }

    public Typeface getExpandedTypeface() {
        return this.expandedTypeface != null ? this.expandedTypeface : Typeface.DEFAULT;
    }

    public void setExpansionFraction(float fraction) {
        float fraction2 = MathUtils.clamp(fraction, 0.0f, 1.0f);
        if (fraction2 != this.expandedFraction) {
            this.expandedFraction = fraction2;
            calculateCurrentOffsets();
        }
    }

    public final boolean setState(int[] state2) {
        this.state = state2;
        if (!isStateful()) {
            return false;
        }
        recalculate();
        return true;
    }

    public final boolean isStateful() {
        return (this.collapsedTextColor != null && this.collapsedTextColor.isStateful()) || (this.expandedTextColor != null && this.expandedTextColor.isStateful());
    }

    public float getExpansionFraction() {
        return this.expandedFraction;
    }

    public float getCollapsedTextSize() {
        return this.collapsedTextSize;
    }

    public float getExpandedTextSize() {
        return this.expandedTextSize;
    }

    private void calculateCurrentOffsets() {
        calculateOffsets(this.expandedFraction);
    }

    private void calculateOffsets(float fraction) {
        interpolateBounds(fraction);
        this.currentDrawX = lerp(this.expandedDrawX, this.collapsedDrawX, fraction, this.positionInterpolator);
        this.currentDrawY = lerp(this.expandedDrawY, this.collapsedDrawY, fraction, this.positionInterpolator);
        setInterpolatedTextSize(lerp(this.expandedTextSize, this.collapsedTextSize, fraction, this.textSizeInterpolator));
        if (this.collapsedTextColor != this.expandedTextColor) {
            this.textPaint.setColor(blendColors(getCurrentExpandedTextColor(), getCurrentCollapsedTextColor(), fraction));
        } else {
            this.textPaint.setColor(getCurrentCollapsedTextColor());
        }
        this.textPaint.setShadowLayer(lerp(this.expandedShadowRadius, this.collapsedShadowRadius, fraction, (TimeInterpolator) null), lerp(this.expandedShadowDx, this.collapsedShadowDx, fraction, (TimeInterpolator) null), lerp(this.expandedShadowDy, this.collapsedShadowDy, fraction, (TimeInterpolator) null), blendColors(this.expandedShadowColor, this.collapsedShadowColor, fraction));
        ViewCompat.postInvalidateOnAnimation(this.view);
    }

    @ColorInt
    private int getCurrentExpandedTextColor() {
        if (this.state != null) {
            return this.expandedTextColor.getColorForState(this.state, 0);
        }
        return this.expandedTextColor.getDefaultColor();
    }

    @ColorInt
    @VisibleForTesting
    public int getCurrentCollapsedTextColor() {
        if (this.state != null) {
            return this.collapsedTextColor.getColorForState(this.state, 0);
        }
        return this.collapsedTextColor.getDefaultColor();
    }

    private void calculateBaseOffsets() {
        float currentTextSize2 = this.currentTextSize;
        calculateUsingTextSize(this.collapsedTextSize);
        float f = 0.0f;
        float width = this.textToDraw != null ? this.textPaint.measureText(this.textToDraw, 0, this.textToDraw.length()) : 0.0f;
        int collapsedAbsGravity = GravityCompat.getAbsoluteGravity(this.collapsedTextGravity, this.isRtl ? 1 : 0);
        int i = collapsedAbsGravity & 112;
        if (i == 48) {
            this.collapsedDrawY = ((float) this.collapsedBounds.top) - this.textPaint.ascent();
        } else if (i != 80) {
            this.collapsedDrawY = ((float) this.collapsedBounds.centerY()) + (((this.textPaint.descent() - this.textPaint.ascent()) / 2.0f) - this.textPaint.descent());
        } else {
            this.collapsedDrawY = (float) this.collapsedBounds.bottom;
        }
        int i2 = collapsedAbsGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        if (i2 == 1) {
            this.collapsedDrawX = ((float) this.collapsedBounds.centerX()) - (width / 2.0f);
        } else if (i2 != 5) {
            this.collapsedDrawX = (float) this.collapsedBounds.left;
        } else {
            this.collapsedDrawX = ((float) this.collapsedBounds.right) - width;
        }
        calculateUsingTextSize(this.expandedTextSize);
        if (this.textToDraw != null) {
            f = this.textPaint.measureText(this.textToDraw, 0, this.textToDraw.length());
        }
        float width2 = f;
        int expandedAbsGravity = GravityCompat.getAbsoluteGravity(this.expandedTextGravity, this.isRtl ? 1 : 0);
        int i3 = expandedAbsGravity & 112;
        if (i3 == 48) {
            this.expandedDrawY = ((float) this.expandedBounds.top) - this.textPaint.ascent();
        } else if (i3 != 80) {
            this.expandedDrawY = ((float) this.expandedBounds.centerY()) + (((this.textPaint.descent() - this.textPaint.ascent()) / 2.0f) - this.textPaint.descent());
        } else {
            this.expandedDrawY = (float) this.expandedBounds.bottom;
        }
        int i4 = expandedAbsGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        if (i4 == 1) {
            this.expandedDrawX = ((float) this.expandedBounds.centerX()) - (width2 / 2.0f);
        } else if (i4 != 5) {
            this.expandedDrawX = (float) this.expandedBounds.left;
        } else {
            this.expandedDrawX = ((float) this.expandedBounds.right) - width2;
        }
        clearTexture();
        setInterpolatedTextSize(currentTextSize2);
    }

    private void interpolateBounds(float fraction) {
        this.currentBounds.left = lerp((float) this.expandedBounds.left, (float) this.collapsedBounds.left, fraction, this.positionInterpolator);
        this.currentBounds.top = lerp(this.expandedDrawY, this.collapsedDrawY, fraction, this.positionInterpolator);
        this.currentBounds.right = lerp((float) this.expandedBounds.right, (float) this.collapsedBounds.right, fraction, this.positionInterpolator);
        this.currentBounds.bottom = lerp((float) this.expandedBounds.bottom, (float) this.collapsedBounds.bottom, fraction, this.positionInterpolator);
    }

    public void draw(Canvas canvas) {
        float ascent;
        float descent;
        int saveCount = canvas.save();
        if (this.textToDraw != null && this.drawTitle) {
            float x = this.currentDrawX;
            float y = this.currentDrawY;
            boolean drawTexture = this.useTexture && this.expandedTitleTexture != null;
            if (drawTexture) {
                ascent = this.textureAscent * this.scale;
                descent = this.textureDescent * this.scale;
            } else {
                ascent = this.textPaint.ascent() * this.scale;
                descent = this.textPaint.descent() * this.scale;
            }
            float ascent2 = ascent;
            float f = descent;
            if (drawTexture) {
                y += ascent2;
            }
            float y2 = y;
            if (this.scale != 1.0f) {
                canvas.scale(this.scale, this.scale, x, y2);
            }
            if (drawTexture) {
                canvas.drawBitmap(this.expandedTitleTexture, x, y2, this.texturePaint);
            } else {
                canvas.drawText(this.textToDraw, 0, this.textToDraw.length(), x, y2, this.textPaint);
            }
        }
        canvas.restoreToCount(saveCount);
    }

    private boolean calculateIsRtl(CharSequence text2) {
        boolean defaultIsRtl = true;
        if (ViewCompat.getLayoutDirection(this.view) != 1) {
            defaultIsRtl = false;
        }
        return (defaultIsRtl ? TextDirectionHeuristicsCompat.FIRSTSTRONG_RTL : TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR).isRtl(text2, 0, text2.length());
    }

    private void setInterpolatedTextSize(float textSize) {
        calculateUsingTextSize(textSize);
        this.useTexture = USE_SCALING_TEXTURE && this.scale != 1.0f;
        if (this.useTexture) {
            ensureExpandedTexture();
        }
        ViewCompat.postInvalidateOnAnimation(this.view);
    }

    private void calculateUsingTextSize(float textSize) {
        float textSizeRatio;
        float newTextSize;
        if (this.text != null) {
            float collapsedWidth = (float) this.collapsedBounds.width();
            float expandedWidth = (float) this.expandedBounds.width();
            boolean updateDrawText = false;
            if (isClose(textSize, this.collapsedTextSize)) {
                newTextSize = this.collapsedTextSize;
                this.scale = 1.0f;
                if (this.currentTypeface != this.collapsedTypeface) {
                    this.currentTypeface = this.collapsedTypeface;
                    updateDrawText = true;
                }
                textSizeRatio = collapsedWidth;
            } else {
                newTextSize = this.expandedTextSize;
                if (this.currentTypeface != this.expandedTypeface) {
                    this.currentTypeface = this.expandedTypeface;
                    updateDrawText = true;
                }
                if (isClose(textSize, this.expandedTextSize)) {
                    this.scale = 1.0f;
                } else {
                    this.scale = textSize / this.expandedTextSize;
                }
                float textSizeRatio2 = this.collapsedTextSize / this.expandedTextSize;
                if (expandedWidth * textSizeRatio2 > collapsedWidth) {
                    textSizeRatio = Math.min(collapsedWidth / textSizeRatio2, expandedWidth);
                } else {
                    textSizeRatio = expandedWidth;
                }
            }
            boolean z = true;
            if (textSizeRatio > 0.0f) {
                updateDrawText = this.currentTextSize != newTextSize || this.boundsChanged || updateDrawText;
                this.currentTextSize = newTextSize;
                this.boundsChanged = false;
            }
            if (this.textToDraw == null || updateDrawText) {
                this.textPaint.setTextSize(this.currentTextSize);
                this.textPaint.setTypeface(this.currentTypeface);
                TextPaint textPaint2 = this.textPaint;
                if (this.scale == 1.0f) {
                    z = false;
                }
                textPaint2.setLinearText(z);
                CharSequence title = TextUtils.ellipsize(this.text, this.textPaint, textSizeRatio, TextUtils.TruncateAt.END);
                if (!TextUtils.equals(title, this.textToDraw)) {
                    this.textToDraw = title;
                    this.isRtl = calculateIsRtl(this.textToDraw);
                }
            }
        }
    }

    private void ensureExpandedTexture() {
        if (this.expandedTitleTexture == null && !this.expandedBounds.isEmpty() && !TextUtils.isEmpty(this.textToDraw)) {
            calculateOffsets(0.0f);
            this.textureAscent = this.textPaint.ascent();
            this.textureDescent = this.textPaint.descent();
            int w = Math.round(this.textPaint.measureText(this.textToDraw, 0, this.textToDraw.length()));
            int h = Math.round(this.textureDescent - this.textureAscent);
            if (w > 0 && h > 0) {
                this.expandedTitleTexture = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                new Canvas(this.expandedTitleTexture).drawText(this.textToDraw, 0, this.textToDraw.length(), 0.0f, ((float) h) - this.textPaint.descent(), this.textPaint);
                if (this.texturePaint == null) {
                    this.texturePaint = new Paint(3);
                }
            }
        }
    }

    public void recalculate() {
        if (this.view.getHeight() > 0 && this.view.getWidth() > 0) {
            calculateBaseOffsets();
            calculateCurrentOffsets();
        }
    }

    public void setText(CharSequence text2) {
        if (text2 == null || !text2.equals(this.text)) {
            this.text = text2;
            this.textToDraw = null;
            clearTexture();
            recalculate();
        }
    }

    public CharSequence getText() {
        return this.text;
    }

    private void clearTexture() {
        if (this.expandedTitleTexture != null) {
            this.expandedTitleTexture.recycle();
            this.expandedTitleTexture = null;
        }
    }

    private static boolean isClose(float value, float targetValue) {
        return Math.abs(value - targetValue) < 0.001f;
    }

    public ColorStateList getExpandedTextColor() {
        return this.expandedTextColor;
    }

    public ColorStateList getCollapsedTextColor() {
        return this.collapsedTextColor;
    }

    private static int blendColors(int color1, int color2, float ratio) {
        float inverseRatio = 1.0f - ratio;
        return Color.argb((int) ((((float) Color.alpha(color1)) * inverseRatio) + (((float) Color.alpha(color2)) * ratio)), (int) ((((float) Color.red(color1)) * inverseRatio) + (((float) Color.red(color2)) * ratio)), (int) ((((float) Color.green(color1)) * inverseRatio) + (((float) Color.green(color2)) * ratio)), (int) ((((float) Color.blue(color1)) * inverseRatio) + (((float) Color.blue(color2)) * ratio)));
    }

    private static float lerp(float startValue, float endValue, float fraction, TimeInterpolator interpolator) {
        if (interpolator != null) {
            fraction = interpolator.getInterpolation(fraction);
        }
        return AnimationUtils.lerp(startValue, endValue, fraction);
    }

    private static boolean rectEquals(Rect r, int left, int top, int right, int bottom) {
        return r.left == left && r.top == top && r.right == right && r.bottom == bottom;
    }
}
