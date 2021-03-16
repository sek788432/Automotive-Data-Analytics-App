package android.support.design.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StringRes;
import android.support.design.R;
import android.support.design.snackbar.ContentViewCallback;
import android.support.design.widget.BaseTransientBottomBar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Snackbar extends BaseTransientBottomBar<Snackbar> {
    public static final int LENGTH_INDEFINITE = -2;
    public static final int LENGTH_LONG = 0;
    public static final int LENGTH_SHORT = -1;
    private static final int[] SNACKBAR_BUTTON_STYLE_ATTR = {R.attr.snackbarButtonStyle};
    private final AccessibilityManager accessibilityManager;
    @Nullable
    private BaseTransientBottomBar.BaseCallback<Snackbar> callback;
    private boolean hasAction;

    @IntRange(from = 1)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

    public static class Callback extends BaseTransientBottomBar.BaseCallback<Snackbar> {
        public static final int DISMISS_EVENT_ACTION = 1;
        public static final int DISMISS_EVENT_CONSECUTIVE = 4;
        public static final int DISMISS_EVENT_MANUAL = 3;
        public static final int DISMISS_EVENT_SWIPE = 0;
        public static final int DISMISS_EVENT_TIMEOUT = 2;

        public void onShown(Snackbar sb) {
        }

        public void onDismissed(Snackbar transientBottomBar, int event) {
        }
    }

    private Snackbar(ViewGroup parent, View content, ContentViewCallback contentViewCallback) {
        super(parent, content, contentViewCallback);
        this.accessibilityManager = (AccessibilityManager) parent.getContext().getSystemService("accessibility");
    }

    public void show() {
        super.show();
    }

    public void dismiss() {
        super.dismiss();
    }

    public boolean isShown() {
        return super.isShown();
    }

    @NonNull
    public static Snackbar make(@NonNull View view, @NonNull CharSequence text, int duration) {
        ViewGroup parent = findSuitableParent(view);
        if (parent != null) {
            SnackbarContentLayout content = (SnackbarContentLayout) LayoutInflater.from(parent.getContext()).inflate(hasSnackbarButtonStyleAttr(parent.getContext()) ? R.layout.mtrl_layout_snackbar_include : R.layout.design_layout_snackbar_include, parent, false);
            Snackbar snackbar = new Snackbar(parent, content, content);
            snackbar.setText(text);
            snackbar.setDuration(duration);
            return snackbar;
        }
        throw new IllegalArgumentException("No suitable parent found from the given view. Please provide a valid view.");
    }

    protected static boolean hasSnackbarButtonStyleAttr(Context context) {
        TypedArray a = context.obtainStyledAttributes(SNACKBAR_BUTTON_STYLE_ATTR);
        int snackbarButtonStyleResId = a.getResourceId(0, -1);
        a.recycle();
        if (snackbarButtonStyleResId != -1) {
            return true;
        }
        return false;
    }

    @NonNull
    public static Snackbar make(@NonNull View view, @StringRes int resId, int duration) {
        return make(view, view.getResources().getText(resId), duration);
    }

    /* JADX WARNING: type inference failed for: r2v2, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.view.ViewGroup findSuitableParent(android.view.View r4) {
        /*
            r0 = 0
            r1 = r4
            r4 = r0
        L_0x0003:
            boolean r2 = r1 instanceof android.support.design.widget.CoordinatorLayout
            if (r2 == 0) goto L_0x000b
            r0 = r1
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            return r0
        L_0x000b:
            boolean r2 = r1 instanceof android.widget.FrameLayout
            if (r2 == 0) goto L_0x001f
            int r2 = r1.getId()
            r3 = 16908290(0x1020002, float:2.3877235E-38)
            if (r2 != r3) goto L_0x001c
            r0 = r1
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            return r0
        L_0x001c:
            r4 = r1
            android.view.ViewGroup r4 = (android.view.ViewGroup) r4
        L_0x001f:
            if (r1 == 0) goto L_0x002f
            android.view.ViewParent r2 = r1.getParent()
            boolean r3 = r2 instanceof android.view.View
            if (r3 == 0) goto L_0x002d
            r3 = r2
            android.view.View r3 = (android.view.View) r3
            goto L_0x002e
        L_0x002d:
            r3 = r0
        L_0x002e:
            r1 = r3
        L_0x002f:
            if (r1 != 0) goto L_0x0003
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.design.widget.Snackbar.findSuitableParent(android.view.View):android.view.ViewGroup");
    }

    @NonNull
    public Snackbar setText(@NonNull CharSequence message) {
        ((SnackbarContentLayout) this.view.getChildAt(0)).getMessageView().setText(message);
        return this;
    }

    @NonNull
    public Snackbar setText(@StringRes int resId) {
        return setText(getContext().getText(resId));
    }

    @NonNull
    public Snackbar setAction(@StringRes int resId, View.OnClickListener listener) {
        return setAction(getContext().getText(resId), listener);
    }

    @NonNull
    public Snackbar setAction(CharSequence text, final View.OnClickListener listener) {
        TextView tv = ((SnackbarContentLayout) this.view.getChildAt(0)).getActionView();
        if (TextUtils.isEmpty(text) || listener == null) {
            tv.setVisibility(8);
            tv.setOnClickListener((View.OnClickListener) null);
            this.hasAction = false;
        } else {
            this.hasAction = true;
            tv.setVisibility(0);
            tv.setText(text);
            tv.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    listener.onClick(view);
                    Snackbar.this.dispatchDismiss(1);
                }
            });
        }
        return this;
    }

    public int getDuration() {
        if (!this.hasAction || !this.accessibilityManager.isTouchExplorationEnabled()) {
            return super.getDuration();
        }
        return -2;
    }

    @NonNull
    public Snackbar setActionTextColor(ColorStateList colors) {
        ((SnackbarContentLayout) this.view.getChildAt(0)).getActionView().setTextColor(colors);
        return this;
    }

    @NonNull
    public Snackbar setActionTextColor(@ColorInt int color) {
        ((SnackbarContentLayout) this.view.getChildAt(0)).getActionView().setTextColor(color);
        return this;
    }

    @Deprecated
    @NonNull
    public Snackbar setCallback(Callback callback2) {
        if (this.callback != null) {
            removeCallback(this.callback);
        }
        if (callback2 != null) {
            addCallback(callback2);
        }
        this.callback = callback2;
        return this;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static final class SnackbarLayout extends BaseTransientBottomBar.SnackbarBaseLayout {
        public SnackbarLayout(Context context) {
            super(context);
        }

        public SnackbarLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int childCount = getChildCount();
            int availableWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child.getLayoutParams().width == -1) {
                    child.measure(View.MeasureSpec.makeMeasureSpec(availableWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(), 1073741824));
                }
            }
        }
    }
}
