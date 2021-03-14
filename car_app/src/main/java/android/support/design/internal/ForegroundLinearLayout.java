package android.support.design.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.design.R;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.Gravity;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class ForegroundLinearLayout extends LinearLayoutCompat {
    private Drawable foreground;
    boolean foregroundBoundsChanged;
    private int foregroundGravity;
    protected boolean mForegroundInPadding;
    private final Rect overlayBounds;
    private final Rect selfBounds;

    public ForegroundLinearLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public ForegroundLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ForegroundLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.selfBounds = new Rect();
        this.overlayBounds = new Rect();
        this.foregroundGravity = 119;
        this.mForegroundInPadding = true;
        this.foregroundBoundsChanged = false;
        TypedArray a = ThemeEnforcement.obtainStyledAttributes(context, attrs, R.styleable.ForegroundLinearLayout, defStyle, 0, new int[0]);
        this.foregroundGravity = a.getInt(R.styleable.ForegroundLinearLayout_android_foregroundGravity, this.foregroundGravity);
        Drawable d = a.getDrawable(R.styleable.ForegroundLinearLayout_android_foreground);
        if (d != null) {
            setForeground(d);
        }
        this.mForegroundInPadding = a.getBoolean(R.styleable.ForegroundLinearLayout_foregroundInsidePadding, true);
        a.recycle();
    }

    public int getForegroundGravity() {
        return this.foregroundGravity;
    }

    public void setForegroundGravity(int foregroundGravity2) {
        if (this.foregroundGravity != foregroundGravity2) {
            if ((8388615 & foregroundGravity2) == 0) {
                foregroundGravity2 |= GravityCompat.START;
            }
            if ((foregroundGravity2 & 112) == 0) {
                foregroundGravity2 |= 48;
            }
            this.foregroundGravity = foregroundGravity2;
            if (this.foregroundGravity == 119 && this.foreground != null) {
                this.foreground.getPadding(new Rect());
            }
            requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.foreground;
    }

    @RequiresApi(11)
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.foreground != null) {
            this.foreground.jumpToCurrentState();
        }
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.foreground != null && this.foreground.isStateful()) {
            this.foreground.setState(getDrawableState());
        }
    }

    public void setForeground(Drawable drawable) {
        if (this.foreground != drawable) {
            if (this.foreground != null) {
                this.foreground.setCallback((Drawable.Callback) null);
                unscheduleDrawable(this.foreground);
            }
            this.foreground = drawable;
            if (drawable != null) {
                setWillNotDraw(false);
                drawable.setCallback(this);
                if (drawable.isStateful()) {
                    drawable.setState(getDrawableState());
                }
                if (this.foregroundGravity == 119) {
                    drawable.getPadding(new Rect());
                }
            } else {
                setWillNotDraw(true);
            }
            requestLayout();
            invalidate();
        }
    }

    public Drawable getForeground() {
        return this.foreground;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.foregroundBoundsChanged |= changed;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.foregroundBoundsChanged = true;
    }

    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        if (this.foreground != null) {
            Drawable foreground2 = this.foreground;
            if (this.foregroundBoundsChanged) {
                this.foregroundBoundsChanged = false;
                Rect selfBounds2 = this.selfBounds;
                Rect overlayBounds2 = this.overlayBounds;
                int w = getRight() - getLeft();
                int h = getBottom() - getTop();
                if (this.mForegroundInPadding) {
                    selfBounds2.set(0, 0, w, h);
                } else {
                    selfBounds2.set(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - getPaddingBottom());
                }
                Gravity.apply(this.foregroundGravity, foreground2.getIntrinsicWidth(), foreground2.getIntrinsicHeight(), selfBounds2, overlayBounds2);
                foreground2.setBounds(overlayBounds2);
            }
            foreground2.draw(canvas);
        }
    }

    @TargetApi(21)
    @RequiresApi(21)
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        if (this.foreground != null) {
            this.foreground.setHotspot(x, y);
        }
    }
}
