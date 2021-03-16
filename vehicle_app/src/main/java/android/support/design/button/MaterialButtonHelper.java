package android.support.design.button;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.design.R;
import android.support.design.internal.ViewUtils;
import android.support.design.resources.MaterialResources;
import android.support.design.ripple.RippleUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
class MaterialButtonHelper {
    private static final float CORNER_RADIUS_ADJUSTMENT = 1.0E-5f;
    private static final int DEFAULT_BACKGROUND_COLOR = -1;
    private static final boolean IS_LOLLIPOP = (Build.VERSION.SDK_INT >= 21);
    @Nullable
    private GradientDrawable backgroundDrawableLollipop;
    private boolean backgroundOverwritten = false;
    @Nullable
    private ColorStateList backgroundTint;
    @Nullable
    private PorterDuff.Mode backgroundTintMode;
    private final Rect bounds = new Rect();
    private final Paint buttonStrokePaint = new Paint(1);
    @Nullable
    private GradientDrawable colorableBackgroundDrawableCompat;
    private int cornerRadius;
    private int insetBottom;
    private int insetLeft;
    private int insetRight;
    private int insetTop;
    @Nullable
    private GradientDrawable maskDrawableLollipop;
    private final MaterialButton materialButton;
    private final RectF rectF = new RectF();
    @Nullable
    private ColorStateList rippleColor;
    @Nullable
    private GradientDrawable rippleDrawableCompat;
    @Nullable
    private ColorStateList strokeColor;
    @Nullable
    private GradientDrawable strokeDrawableLollipop;
    private int strokeWidth;
    @Nullable
    private Drawable tintableBackgroundDrawableCompat;
    @Nullable
    private Drawable tintableRippleDrawableCompat;

    public MaterialButtonHelper(MaterialButton button) {
        this.materialButton = button;
    }

    public void loadFromAttributes(TypedArray attributes) {
        int i = 0;
        this.insetLeft = attributes.getDimensionPixelOffset(R.styleable.MaterialButton_android_insetLeft, 0);
        this.insetRight = attributes.getDimensionPixelOffset(R.styleable.MaterialButton_android_insetRight, 0);
        this.insetTop = attributes.getDimensionPixelOffset(R.styleable.MaterialButton_android_insetTop, 0);
        this.insetBottom = attributes.getDimensionPixelOffset(R.styleable.MaterialButton_android_insetBottom, 0);
        this.cornerRadius = attributes.getDimensionPixelSize(R.styleable.MaterialButton_cornerRadius, 0);
        this.strokeWidth = attributes.getDimensionPixelSize(R.styleable.MaterialButton_strokeWidth, 0);
        this.backgroundTintMode = ViewUtils.parseTintMode(attributes.getInt(R.styleable.MaterialButton_backgroundTintMode, -1), PorterDuff.Mode.SRC_IN);
        this.backgroundTint = MaterialResources.getColorStateList(this.materialButton.getContext(), attributes, R.styleable.MaterialButton_backgroundTint);
        this.strokeColor = MaterialResources.getColorStateList(this.materialButton.getContext(), attributes, R.styleable.MaterialButton_strokeColor);
        this.rippleColor = MaterialResources.getColorStateList(this.materialButton.getContext(), attributes, R.styleable.MaterialButton_rippleColor);
        this.buttonStrokePaint.setStyle(Paint.Style.STROKE);
        this.buttonStrokePaint.setStrokeWidth((float) this.strokeWidth);
        Paint paint = this.buttonStrokePaint;
        if (this.strokeColor != null) {
            i = this.strokeColor.getColorForState(this.materialButton.getDrawableState(), 0);
        }
        paint.setColor(i);
        int paddingStart = ViewCompat.getPaddingStart(this.materialButton);
        int paddingTop = this.materialButton.getPaddingTop();
        int paddingEnd = ViewCompat.getPaddingEnd(this.materialButton);
        int paddingBottom = this.materialButton.getPaddingBottom();
        this.materialButton.setInternalBackground(IS_LOLLIPOP ? createBackgroundLollipop() : createBackgroundCompat());
        ViewCompat.setPaddingRelative(this.materialButton, this.insetLeft + paddingStart, this.insetTop + paddingTop, this.insetRight + paddingEnd, this.insetBottom + paddingBottom);
    }

    /* access modifiers changed from: package-private */
    public void setBackgroundOverwritten() {
        this.backgroundOverwritten = true;
        this.materialButton.setSupportBackgroundTintList(this.backgroundTint);
        this.materialButton.setSupportBackgroundTintMode(this.backgroundTintMode);
    }

    /* access modifiers changed from: package-private */
    public boolean isBackgroundOverwritten() {
        return this.backgroundOverwritten;
    }

    /* access modifiers changed from: package-private */
    public void drawStroke(@Nullable Canvas canvas) {
        if (canvas != null && this.strokeColor != null && this.strokeWidth > 0) {
            this.bounds.set(this.materialButton.getBackground().getBounds());
            this.rectF.set(((float) this.bounds.left) + (((float) this.strokeWidth) / 2.0f) + ((float) this.insetLeft), ((float) this.bounds.top) + (((float) this.strokeWidth) / 2.0f) + ((float) this.insetTop), (((float) this.bounds.right) - (((float) this.strokeWidth) / 2.0f)) - ((float) this.insetRight), (((float) this.bounds.bottom) - (((float) this.strokeWidth) / 2.0f)) - ((float) this.insetBottom));
            float strokeCornerRadius = ((float) this.cornerRadius) - (((float) this.strokeWidth) / 2.0f);
            canvas.drawRoundRect(this.rectF, strokeCornerRadius, strokeCornerRadius, this.buttonStrokePaint);
        }
    }

    private Drawable createBackgroundCompat() {
        this.colorableBackgroundDrawableCompat = new GradientDrawable();
        this.colorableBackgroundDrawableCompat.setCornerRadius(((float) this.cornerRadius) + CORNER_RADIUS_ADJUSTMENT);
        this.colorableBackgroundDrawableCompat.setColor(-1);
        this.tintableBackgroundDrawableCompat = DrawableCompat.wrap(this.colorableBackgroundDrawableCompat);
        DrawableCompat.setTintList(this.tintableBackgroundDrawableCompat, this.backgroundTint);
        if (this.backgroundTintMode != null) {
            DrawableCompat.setTintMode(this.tintableBackgroundDrawableCompat, this.backgroundTintMode);
        }
        this.rippleDrawableCompat = new GradientDrawable();
        this.rippleDrawableCompat.setCornerRadius(((float) this.cornerRadius) + CORNER_RADIUS_ADJUSTMENT);
        this.rippleDrawableCompat.setColor(-1);
        this.tintableRippleDrawableCompat = DrawableCompat.wrap(this.rippleDrawableCompat);
        DrawableCompat.setTintList(this.tintableRippleDrawableCompat, this.rippleColor);
        return wrapDrawableWithInset(new LayerDrawable(new Drawable[]{this.tintableBackgroundDrawableCompat, this.tintableRippleDrawableCompat}));
    }

    private InsetDrawable wrapDrawableWithInset(Drawable drawable) {
        return new InsetDrawable(drawable, this.insetLeft, this.insetTop, this.insetRight, this.insetBottom);
    }

    /* access modifiers changed from: package-private */
    public void setSupportBackgroundTintList(@Nullable ColorStateList tintList) {
        if (this.backgroundTint != tintList) {
            this.backgroundTint = tintList;
            if (IS_LOLLIPOP) {
                updateTintAndTintModeLollipop();
            } else if (this.tintableBackgroundDrawableCompat != null) {
                DrawableCompat.setTintList(this.tintableBackgroundDrawableCompat, this.backgroundTint);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public ColorStateList getSupportBackgroundTintList() {
        return this.backgroundTint;
    }

    /* access modifiers changed from: package-private */
    public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode mode) {
        if (this.backgroundTintMode != mode) {
            this.backgroundTintMode = mode;
            if (IS_LOLLIPOP) {
                updateTintAndTintModeLollipop();
            } else if (this.tintableBackgroundDrawableCompat != null && this.backgroundTintMode != null) {
                DrawableCompat.setTintMode(this.tintableBackgroundDrawableCompat, this.backgroundTintMode);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        return this.backgroundTintMode;
    }

    private void updateTintAndTintModeLollipop() {
        if (this.backgroundDrawableLollipop != null) {
            DrawableCompat.setTintList(this.backgroundDrawableLollipop, this.backgroundTint);
            if (this.backgroundTintMode != null) {
                DrawableCompat.setTintMode(this.backgroundDrawableLollipop, this.backgroundTintMode);
            }
        }
    }

    @TargetApi(21)
    private Drawable createBackgroundLollipop() {
        this.backgroundDrawableLollipop = new GradientDrawable();
        this.backgroundDrawableLollipop.setCornerRadius(((float) this.cornerRadius) + CORNER_RADIUS_ADJUSTMENT);
        this.backgroundDrawableLollipop.setColor(-1);
        updateTintAndTintModeLollipop();
        this.strokeDrawableLollipop = new GradientDrawable();
        this.strokeDrawableLollipop.setCornerRadius(((float) this.cornerRadius) + CORNER_RADIUS_ADJUSTMENT);
        this.strokeDrawableLollipop.setColor(0);
        this.strokeDrawableLollipop.setStroke(this.strokeWidth, this.strokeColor);
        InsetDrawable bgInsetDrawable = wrapDrawableWithInset(new LayerDrawable(new Drawable[]{this.backgroundDrawableLollipop, this.strokeDrawableLollipop}));
        this.maskDrawableLollipop = new GradientDrawable();
        this.maskDrawableLollipop.setCornerRadius(((float) this.cornerRadius) + CORNER_RADIUS_ADJUSTMENT);
        this.maskDrawableLollipop.setColor(-1);
        return new MaterialButtonBackgroundDrawable(RippleUtils.convertToRippleDrawableColor(this.rippleColor), bgInsetDrawable, this.maskDrawableLollipop);
    }

    /* access modifiers changed from: package-private */
    public void updateMaskBounds(int height, int width) {
        if (this.maskDrawableLollipop != null) {
            this.maskDrawableLollipop.setBounds(this.insetLeft, this.insetTop, width - this.insetRight, height - this.insetBottom);
        }
    }

    /* access modifiers changed from: package-private */
    public void setBackgroundColor(int color) {
        if (IS_LOLLIPOP && this.backgroundDrawableLollipop != null) {
            this.backgroundDrawableLollipop.setColor(color);
        } else if (!IS_LOLLIPOP && this.colorableBackgroundDrawableCompat != null) {
            this.colorableBackgroundDrawableCompat.setColor(color);
        }
    }

    /* access modifiers changed from: package-private */
    public void setRippleColor(@Nullable ColorStateList rippleColor2) {
        if (this.rippleColor != rippleColor2) {
            this.rippleColor = rippleColor2;
            if (IS_LOLLIPOP && (this.materialButton.getBackground() instanceof RippleDrawable)) {
                ((RippleDrawable) this.materialButton.getBackground()).setColor(rippleColor2);
            } else if (!IS_LOLLIPOP && this.tintableRippleDrawableCompat != null) {
                DrawableCompat.setTintList(this.tintableRippleDrawableCompat, rippleColor2);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public ColorStateList getRippleColor() {
        return this.rippleColor;
    }

    /* access modifiers changed from: package-private */
    public void setStrokeColor(@Nullable ColorStateList strokeColor2) {
        if (this.strokeColor != strokeColor2) {
            this.strokeColor = strokeColor2;
            Paint paint = this.buttonStrokePaint;
            int i = 0;
            if (strokeColor2 != null) {
                i = strokeColor2.getColorForState(this.materialButton.getDrawableState(), 0);
            }
            paint.setColor(i);
            updateStroke();
        }
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public ColorStateList getStrokeColor() {
        return this.strokeColor;
    }

    /* access modifiers changed from: package-private */
    public void setStrokeWidth(int strokeWidth2) {
        if (this.strokeWidth != strokeWidth2) {
            this.strokeWidth = strokeWidth2;
            this.buttonStrokePaint.setStrokeWidth((float) strokeWidth2);
            updateStroke();
        }
    }

    /* access modifiers changed from: package-private */
    public int getStrokeWidth() {
        return this.strokeWidth;
    }

    private void updateStroke() {
        if (IS_LOLLIPOP && this.strokeDrawableLollipop != null) {
            this.materialButton.setInternalBackground(createBackgroundLollipop());
        } else if (!IS_LOLLIPOP) {
            this.materialButton.invalidate();
        }
    }

    /* access modifiers changed from: package-private */
    public void setCornerRadius(int cornerRadius2) {
        if (this.cornerRadius != cornerRadius2) {
            this.cornerRadius = cornerRadius2;
            if (IS_LOLLIPOP && this.backgroundDrawableLollipop != null && this.strokeDrawableLollipop != null && this.maskDrawableLollipop != null) {
                if (Build.VERSION.SDK_INT == 21) {
                    unwrapBackgroundDrawable().setCornerRadius(((float) cornerRadius2) + CORNER_RADIUS_ADJUSTMENT);
                    unwrapStrokeDrawable().setCornerRadius(((float) cornerRadius2) + CORNER_RADIUS_ADJUSTMENT);
                }
                this.backgroundDrawableLollipop.setCornerRadius(((float) cornerRadius2) + CORNER_RADIUS_ADJUSTMENT);
                this.strokeDrawableLollipop.setCornerRadius(((float) cornerRadius2) + CORNER_RADIUS_ADJUSTMENT);
                this.maskDrawableLollipop.setCornerRadius(((float) cornerRadius2) + CORNER_RADIUS_ADJUSTMENT);
            } else if (!IS_LOLLIPOP && this.colorableBackgroundDrawableCompat != null && this.rippleDrawableCompat != null) {
                this.colorableBackgroundDrawableCompat.setCornerRadius(((float) cornerRadius2) + CORNER_RADIUS_ADJUSTMENT);
                this.rippleDrawableCompat.setCornerRadius(((float) cornerRadius2) + CORNER_RADIUS_ADJUSTMENT);
                this.materialButton.invalidate();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int getCornerRadius() {
        return this.cornerRadius;
    }

    @Nullable
    private GradientDrawable unwrapStrokeDrawable() {
        if (!IS_LOLLIPOP || this.materialButton.getBackground() == null) {
            return null;
        }
        return (GradientDrawable) ((LayerDrawable) ((InsetDrawable) ((RippleDrawable) this.materialButton.getBackground()).getDrawable(0)).getDrawable()).getDrawable(1);
    }

    @Nullable
    private GradientDrawable unwrapBackgroundDrawable() {
        if (!IS_LOLLIPOP || this.materialButton.getBackground() == null) {
            return null;
        }
        return (GradientDrawable) ((LayerDrawable) ((InsetDrawable) ((RippleDrawable) this.materialButton.getBackground()).getDrawable(0)).getDrawable()).getDrawable(0);
    }
}
