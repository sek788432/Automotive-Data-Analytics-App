package android.support.design.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.annotation.VisibleForTesting;
import android.support.design.R;
import android.support.design.animation.AnimationUtils;
import android.support.design.internal.ThemeEnforcement;
import android.support.design.internal.ViewUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.TintTypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TextInputLayout extends LinearLayout {
    public static final int BOX_BACKGROUND_FILLED = 1;
    public static final int BOX_BACKGROUND_NONE = 0;
    public static final int BOX_BACKGROUND_OUTLINE = 2;
    private static final int INVALID_MAX_LENGTH = -1;
    private static final int LABEL_SCALE_ANIMATION_DURATION = 167;
    private static final String LOG_TAG = "TextInputLayout";
    private ValueAnimator animator;
    private GradientDrawable boxBackground;
    @ColorInt
    private int boxBackgroundColor;
    private int boxBackgroundMode;
    private final int boxBottomOffsetPx;
    private final int boxCollapsedPaddingTopPx;
    private float boxCornerRadiusBottomEnd;
    private float boxCornerRadiusBottomStart;
    private float boxCornerRadiusTopEnd;
    private float boxCornerRadiusTopStart;
    private final int boxLabelCutoutPaddingPx;
    @ColorInt
    private int boxStrokeColor;
    private final int boxStrokeWidthDefaultPx;
    private final int boxStrokeWidthFocusedPx;
    private int boxStrokeWidthPx;
    final CollapsingTextHelper collapsingTextHelper;
    boolean counterEnabled;
    private int counterMaxLength;
    private final int counterOverflowTextAppearance;
    private boolean counterOverflowed;
    private final int counterTextAppearance;
    private TextView counterView;
    private ColorStateList defaultHintTextColor;
    @ColorInt
    private final int defaultStrokeColor;
    @ColorInt
    private final int disabledColor;
    EditText editText;
    private Drawable editTextOriginalDrawable;
    @ColorInt
    private int focusedStrokeColor;
    private ColorStateList focusedTextColor;
    private boolean hasPasswordToggleTintList;
    private boolean hasPasswordToggleTintMode;
    private boolean hasReconstructedEditTextBackground;
    private CharSequence hint;
    private boolean hintAnimationEnabled;
    private boolean hintEnabled;
    private boolean hintExpanded;
    @ColorInt
    private final int hoveredStrokeColor;
    private boolean inDrawableStateChanged;
    private final IndicatorViewController indicatorViewController;
    private final FrameLayout inputFrame;
    private boolean isProvidingHint;
    private Drawable originalEditTextEndDrawable;
    private CharSequence originalHint;
    private CharSequence passwordToggleContentDesc;
    private Drawable passwordToggleDrawable;
    private Drawable passwordToggleDummyDrawable;
    private boolean passwordToggleEnabled;
    private ColorStateList passwordToggleTintList;
    private PorterDuff.Mode passwordToggleTintMode;
    private CheckableImageButton passwordToggleView;
    private boolean passwordToggledVisible;
    /* access modifiers changed from: private */
    public boolean restoringSavedState;
    private final Rect tmpRect;
    private final RectF tmpRectF;
    private Typeface typeface;

    @Retention(RetentionPolicy.SOURCE)
    public @interface BoxBackgroundMode {
    }

    public TextInputLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public TextInputLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.textInputStyle);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public TextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Context context2 = context;
        this.indicatorViewController = new IndicatorViewController(this);
        this.tmpRect = new Rect();
        this.tmpRectF = new RectF();
        this.collapsingTextHelper = new CollapsingTextHelper(this);
        setOrientation(1);
        setWillNotDraw(false);
        setAddStatesFromChildren(true);
        this.inputFrame = new FrameLayout(context2);
        this.inputFrame.setAddStatesFromChildren(true);
        addView(this.inputFrame);
        this.collapsingTextHelper.setTextSizeInterpolator(AnimationUtils.LINEAR_INTERPOLATOR);
        this.collapsingTextHelper.setPositionInterpolator(AnimationUtils.LINEAR_INTERPOLATOR);
        this.collapsingTextHelper.setCollapsedTextGravity(8388659);
        TintTypedArray a = ThemeEnforcement.obtainTintedStyledAttributes(context, attrs, R.styleable.TextInputLayout, defStyleAttr, R.style.Widget_Design_TextInputLayout, new int[0]);
        this.hintEnabled = a.getBoolean(R.styleable.TextInputLayout_hintEnabled, true);
        setHint(a.getText(R.styleable.TextInputLayout_android_hint));
        this.hintAnimationEnabled = a.getBoolean(R.styleable.TextInputLayout_hintAnimationEnabled, true);
        this.boxBottomOffsetPx = context.getResources().getDimensionPixelOffset(R.dimen.mtrl_textinput_box_bottom_offset);
        this.boxLabelCutoutPaddingPx = context.getResources().getDimensionPixelOffset(R.dimen.mtrl_textinput_box_label_cutout_padding);
        this.boxCollapsedPaddingTopPx = a.getDimensionPixelOffset(R.styleable.TextInputLayout_boxCollapsedPaddingTop, 0);
        this.boxCornerRadiusTopStart = a.getDimension(R.styleable.TextInputLayout_boxCornerRadiusTopStart, 0.0f);
        this.boxCornerRadiusTopEnd = a.getDimension(R.styleable.TextInputLayout_boxCornerRadiusTopEnd, 0.0f);
        this.boxCornerRadiusBottomEnd = a.getDimension(R.styleable.TextInputLayout_boxCornerRadiusBottomEnd, 0.0f);
        this.boxCornerRadiusBottomStart = a.getDimension(R.styleable.TextInputLayout_boxCornerRadiusBottomStart, 0.0f);
        this.boxBackgroundColor = a.getColor(R.styleable.TextInputLayout_boxBackgroundColor, 0);
        this.focusedStrokeColor = a.getColor(R.styleable.TextInputLayout_boxStrokeColor, 0);
        this.boxStrokeWidthDefaultPx = context.getResources().getDimensionPixelSize(R.dimen.mtrl_textinput_box_stroke_width_default);
        this.boxStrokeWidthFocusedPx = context.getResources().getDimensionPixelSize(R.dimen.mtrl_textinput_box_stroke_width_focused);
        this.boxStrokeWidthPx = this.boxStrokeWidthDefaultPx;
        setBoxBackgroundMode(a.getInt(R.styleable.TextInputLayout_boxBackgroundMode, 0));
        if (a.hasValue(R.styleable.TextInputLayout_android_textColorHint)) {
            ColorStateList colorStateList = a.getColorStateList(R.styleable.TextInputLayout_android_textColorHint);
            this.focusedTextColor = colorStateList;
            this.defaultHintTextColor = colorStateList;
        }
        this.defaultStrokeColor = ContextCompat.getColor(context2, R.color.mtrl_textinput_default_box_stroke_color);
        this.disabledColor = ContextCompat.getColor(context2, R.color.mtrl_textinput_disabled_color);
        this.hoveredStrokeColor = ContextCompat.getColor(context2, R.color.mtrl_textinput_hovered_box_stroke_color);
        if (a.getResourceId(R.styleable.TextInputLayout_hintTextAppearance, -1) != -1) {
            setHintTextAppearance(a.getResourceId(R.styleable.TextInputLayout_hintTextAppearance, 0));
        }
        int errorTextAppearance = a.getResourceId(R.styleable.TextInputLayout_errorTextAppearance, 0);
        boolean errorEnabled = a.getBoolean(R.styleable.TextInputLayout_errorEnabled, false);
        int helperTextTextAppearance = a.getResourceId(R.styleable.TextInputLayout_helperTextTextAppearance, 0);
        boolean helperTextEnabled = a.getBoolean(R.styleable.TextInputLayout_helperTextEnabled, false);
        CharSequence helperText = a.getText(R.styleable.TextInputLayout_helperText);
        boolean counterEnabled2 = a.getBoolean(R.styleable.TextInputLayout_counterEnabled, false);
        setCounterMaxLength(a.getInt(R.styleable.TextInputLayout_counterMaxLength, -1));
        this.counterTextAppearance = a.getResourceId(R.styleable.TextInputLayout_counterTextAppearance, 0);
        this.counterOverflowTextAppearance = a.getResourceId(R.styleable.TextInputLayout_counterOverflowTextAppearance, 0);
        this.passwordToggleEnabled = a.getBoolean(R.styleable.TextInputLayout_passwordToggleEnabled, false);
        this.passwordToggleDrawable = a.getDrawable(R.styleable.TextInputLayout_passwordToggleDrawable);
        this.passwordToggleContentDesc = a.getText(R.styleable.TextInputLayout_passwordToggleContentDescription);
        if (a.hasValue(R.styleable.TextInputLayout_passwordToggleTint)) {
            this.hasPasswordToggleTintList = true;
            this.passwordToggleTintList = a.getColorStateList(R.styleable.TextInputLayout_passwordToggleTint);
        }
        if (a.hasValue(R.styleable.TextInputLayout_passwordToggleTintMode)) {
            this.hasPasswordToggleTintMode = true;
            this.passwordToggleTintMode = ViewUtils.parseTintMode(a.getInt(R.styleable.TextInputLayout_passwordToggleTintMode, -1), (PorterDuff.Mode) null);
        }
        a.recycle();
        setHelperTextEnabled(helperTextEnabled);
        setHelperText(helperText);
        setHelperTextTextAppearance(helperTextTextAppearance);
        setErrorEnabled(errorEnabled);
        setErrorTextAppearance(errorTextAppearance);
        setCounterEnabled(counterEnabled2);
        applyPasswordToggleTint();
        ViewCompat.setImportantForAccessibility(this, 2);
    }

    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof EditText) {
            FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(params);
            flp.gravity = (flp.gravity & -113) | 16;
            this.inputFrame.addView(child, flp);
            this.inputFrame.setLayoutParams(params);
            updateInputLayoutMargins();
            setEditText((EditText) child);
            return;
        }
        super.addView(child, index, params);
    }

    @NonNull
    private Drawable getBoxBackground() {
        if (this.boxBackgroundMode == 1 || this.boxBackgroundMode == 2) {
            return this.boxBackground;
        }
        throw new IllegalStateException();
    }

    public void setBoxBackgroundMode(int boxBackgroundMode2) {
        if (boxBackgroundMode2 != this.boxBackgroundMode) {
            this.boxBackgroundMode = boxBackgroundMode2;
            onApplyBoxBackgroundMode();
        }
    }

    private void onApplyBoxBackgroundMode() {
        assignBoxBackgroundByMode();
        if (this.boxBackgroundMode != 0) {
            updateInputLayoutMargins();
        }
        updateTextInputBoxBounds();
    }

    private void assignBoxBackgroundByMode() {
        if (this.boxBackgroundMode == 0) {
            this.boxBackground = null;
        } else if (this.boxBackgroundMode == 2 && this.hintEnabled && !(this.boxBackground instanceof CutoutDrawable)) {
            this.boxBackground = new CutoutDrawable();
        } else if (!(this.boxBackground instanceof GradientDrawable)) {
            this.boxBackground = new GradientDrawable();
        }
    }

    public void setBoxStrokeColor(@ColorInt int boxStrokeColor2) {
        if (this.focusedStrokeColor != boxStrokeColor2) {
            this.focusedStrokeColor = boxStrokeColor2;
            updateTextInputBoxState();
        }
    }

    public int getBoxStrokeColor() {
        return this.focusedStrokeColor;
    }

    public void setBoxBackgroundColorResource(@ColorRes int boxBackgroundColorId) {
        setBoxBackgroundColor(ContextCompat.getColor(getContext(), boxBackgroundColorId));
    }

    public void setBoxBackgroundColor(@ColorInt int boxBackgroundColor2) {
        if (this.boxBackgroundColor != boxBackgroundColor2) {
            this.boxBackgroundColor = boxBackgroundColor2;
            applyBoxAttributes();
        }
    }

    public int getBoxBackgroundColor() {
        return this.boxBackgroundColor;
    }

    public void setBoxCornerRadiiResources(@DimenRes int boxCornerRadiusTopStartId, @DimenRes int boxCornerRadiusTopEndId, @DimenRes int boxCornerRadiusBottomEndId, @DimenRes int boxCornerRadiusBottomStartId) {
        setBoxCornerRadii(getContext().getResources().getDimension(boxCornerRadiusTopStartId), getContext().getResources().getDimension(boxCornerRadiusTopEndId), getContext().getResources().getDimension(boxCornerRadiusBottomEndId), getContext().getResources().getDimension(boxCornerRadiusBottomStartId));
    }

    public void setBoxCornerRadii(float boxCornerRadiusTopStart2, float boxCornerRadiusTopEnd2, float boxCornerRadiusBottomStart2, float boxCornerRadiusBottomEnd2) {
        if (this.boxCornerRadiusTopStart != boxCornerRadiusTopStart2 || this.boxCornerRadiusTopEnd != boxCornerRadiusTopEnd2 || this.boxCornerRadiusBottomEnd != boxCornerRadiusBottomEnd2 || this.boxCornerRadiusBottomStart != boxCornerRadiusBottomStart2) {
            this.boxCornerRadiusTopStart = boxCornerRadiusTopStart2;
            this.boxCornerRadiusTopEnd = boxCornerRadiusTopEnd2;
            this.boxCornerRadiusBottomEnd = boxCornerRadiusBottomEnd2;
            this.boxCornerRadiusBottomStart = boxCornerRadiusBottomStart2;
            applyBoxAttributes();
        }
    }

    public float getBoxCornerRadiusTopStart() {
        return this.boxCornerRadiusTopStart;
    }

    public float getBoxCornerRadiusTopEnd() {
        return this.boxCornerRadiusTopEnd;
    }

    public float getBoxCornerRadiusBottomEnd() {
        return this.boxCornerRadiusBottomEnd;
    }

    public float getBoxCornerRadiusBottomStart() {
        return this.boxCornerRadiusBottomStart;
    }

    private float[] getCornerRadiiAsArray() {
        if (!ViewUtils.isLayoutRtl(this)) {
            return new float[]{this.boxCornerRadiusTopStart, this.boxCornerRadiusTopStart, this.boxCornerRadiusTopEnd, this.boxCornerRadiusTopEnd, this.boxCornerRadiusBottomEnd, this.boxCornerRadiusBottomEnd, this.boxCornerRadiusBottomStart, this.boxCornerRadiusBottomStart};
        }
        return new float[]{this.boxCornerRadiusTopEnd, this.boxCornerRadiusTopEnd, this.boxCornerRadiusTopStart, this.boxCornerRadiusTopStart, this.boxCornerRadiusBottomStart, this.boxCornerRadiusBottomStart, this.boxCornerRadiusBottomEnd, this.boxCornerRadiusBottomEnd};
    }

    public void setTypeface(@Nullable Typeface typeface2) {
        if (typeface2 != this.typeface) {
            this.typeface = typeface2;
            this.collapsingTextHelper.setTypefaces(typeface2);
            this.indicatorViewController.setTypefaces(typeface2);
            if (this.counterView != null) {
                this.counterView.setTypeface(typeface2);
            }
        }
    }

    @Nullable
    public Typeface getTypeface() {
        return this.typeface;
    }

    public void dispatchProvideAutofillStructure(ViewStructure structure, int flags) {
        if (this.originalHint == null || this.editText == null) {
            super.dispatchProvideAutofillStructure(structure, flags);
            return;
        }
        boolean wasProvidingHint = this.isProvidingHint;
        this.isProvidingHint = false;
        CharSequence hint2 = this.editText.getHint();
        this.editText.setHint(this.originalHint);
        try {
            super.dispatchProvideAutofillStructure(structure, flags);
        } finally {
            this.editText.setHint(hint2);
            this.isProvidingHint = wasProvidingHint;
        }
    }

    private void setEditText(EditText editText2) {
        if (this.editText == null) {
            if (!(editText2 instanceof TextInputEditText)) {
                Log.i(LOG_TAG, "EditText added is not a TextInputEditText. Please switch to using that class instead.");
            }
            this.editText = editText2;
            onApplyBoxBackgroundMode();
            setTextInputAccessibilityDelegate(new AccessibilityDelegate(this));
            if (!hasPasswordTransformation()) {
                this.collapsingTextHelper.setTypefaces(this.editText.getTypeface());
            }
            this.collapsingTextHelper.setExpandedTextSize(this.editText.getTextSize());
            int editTextGravity = this.editText.getGravity();
            this.collapsingTextHelper.setCollapsedTextGravity((editTextGravity & -113) | 48);
            this.collapsingTextHelper.setExpandedTextGravity(editTextGravity);
            this.editText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    TextInputLayout.this.updateLabelState(!TextInputLayout.this.restoringSavedState);
                    if (TextInputLayout.this.counterEnabled) {
                        TextInputLayout.this.updateCounter(s.length());
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
            if (this.defaultHintTextColor == null) {
                this.defaultHintTextColor = this.editText.getHintTextColors();
            }
            if (this.hintEnabled) {
                if (TextUtils.isEmpty(this.hint)) {
                    this.originalHint = this.editText.getHint();
                    setHint(this.originalHint);
                    this.editText.setHint((CharSequence) null);
                }
                this.isProvidingHint = true;
            }
            if (this.counterView != null) {
                updateCounter(this.editText.getText().length());
            }
            this.indicatorViewController.adjustIndicatorPadding();
            updatePasswordToggleView();
            updateLabelState(false, true);
            return;
        }
        throw new IllegalArgumentException("We already have an EditText, can only have one");
    }

    private void updateInputLayoutMargins() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.inputFrame.getLayoutParams();
        int newTopMargin = calculateLabelMarginTop();
        if (newTopMargin != lp.topMargin) {
            lp.topMargin = newTopMargin;
            this.inputFrame.requestLayout();
        }
    }

    /* access modifiers changed from: package-private */
    public void updateLabelState(boolean animate) {
        updateLabelState(animate, false);
    }

    private void updateLabelState(boolean animate, boolean force) {
        boolean isEnabled = isEnabled();
        boolean hasFocus = true;
        boolean hasText = this.editText != null && !TextUtils.isEmpty(this.editText.getText());
        if (this.editText == null || !this.editText.hasFocus()) {
            hasFocus = false;
        }
        boolean errorShouldBeShown = this.indicatorViewController.errorShouldBeShown();
        if (this.defaultHintTextColor != null) {
            this.collapsingTextHelper.setCollapsedTextColor(this.defaultHintTextColor);
            this.collapsingTextHelper.setExpandedTextColor(this.defaultHintTextColor);
        }
        if (!isEnabled) {
            this.collapsingTextHelper.setCollapsedTextColor(ColorStateList.valueOf(this.disabledColor));
            this.collapsingTextHelper.setExpandedTextColor(ColorStateList.valueOf(this.disabledColor));
        } else if (errorShouldBeShown) {
            this.collapsingTextHelper.setCollapsedTextColor(this.indicatorViewController.getErrorViewTextColors());
        } else if (this.counterOverflowed && this.counterView != null) {
            this.collapsingTextHelper.setCollapsedTextColor(this.counterView.getTextColors());
        } else if (hasFocus && this.focusedTextColor != null) {
            this.collapsingTextHelper.setCollapsedTextColor(this.focusedTextColor);
        }
        if (hasText || (isEnabled() && (hasFocus || errorShouldBeShown))) {
            if (force || this.hintExpanded) {
                collapseHint(animate);
            }
        } else if (force || !this.hintExpanded) {
            expandHint(animate);
        }
    }

    @Nullable
    public EditText getEditText() {
        return this.editText;
    }

    public void setHint(@Nullable CharSequence hint2) {
        if (this.hintEnabled) {
            setHintInternal(hint2);
            sendAccessibilityEvent(2048);
        }
    }

    private void setHintInternal(CharSequence hint2) {
        if (!TextUtils.equals(hint2, this.hint)) {
            this.hint = hint2;
            this.collapsingTextHelper.setText(hint2);
            if (!this.hintExpanded) {
                openCutout();
            }
        }
    }

    @Nullable
    public CharSequence getHint() {
        if (this.hintEnabled) {
            return this.hint;
        }
        return null;
    }

    public void setHintEnabled(boolean enabled) {
        if (enabled != this.hintEnabled) {
            this.hintEnabled = enabled;
            if (!this.hintEnabled) {
                this.isProvidingHint = false;
                if (!TextUtils.isEmpty(this.hint) && TextUtils.isEmpty(this.editText.getHint())) {
                    this.editText.setHint(this.hint);
                }
                setHintInternal((CharSequence) null);
            } else {
                CharSequence editTextHint = this.editText.getHint();
                if (!TextUtils.isEmpty(editTextHint)) {
                    if (TextUtils.isEmpty(this.hint)) {
                        setHint(editTextHint);
                    }
                    this.editText.setHint((CharSequence) null);
                }
                this.isProvidingHint = true;
            }
            if (this.editText != null) {
                updateInputLayoutMargins();
            }
        }
    }

    public boolean isHintEnabled() {
        return this.hintEnabled;
    }

    /* access modifiers changed from: package-private */
    public boolean isProvidingHint() {
        return this.isProvidingHint;
    }

    public void setHintTextAppearance(@StyleRes int resId) {
        this.collapsingTextHelper.setCollapsedTextAppearance(resId);
        this.focusedTextColor = this.collapsingTextHelper.getCollapsedTextColor();
        if (this.editText != null) {
            updateLabelState(false);
            updateInputLayoutMargins();
        }
    }

    public void setDefaultHintTextColor(@Nullable ColorStateList textColor) {
        this.defaultHintTextColor = textColor;
        this.focusedTextColor = textColor;
        if (this.editText != null) {
            updateLabelState(false);
        }
    }

    @Nullable
    public ColorStateList getDefaultHintTextColor() {
        return this.defaultHintTextColor;
    }

    public void setErrorEnabled(boolean enabled) {
        this.indicatorViewController.setErrorEnabled(enabled);
    }

    public void setErrorTextAppearance(@StyleRes int resId) {
        this.indicatorViewController.setErrorTextAppearance(resId);
    }

    public void setErrorTextColor(@Nullable ColorStateList textColors) {
        this.indicatorViewController.setErrorViewTextColor(textColors);
    }

    @ColorInt
    public int getErrorCurrentTextColors() {
        return this.indicatorViewController.getErrorViewCurrentTextColor();
    }

    public void setHelperTextTextAppearance(@StyleRes int resId) {
        this.indicatorViewController.setHelperTextAppearance(resId);
    }

    public boolean isErrorEnabled() {
        return this.indicatorViewController.isErrorEnabled();
    }

    public void setHelperTextEnabled(boolean enabled) {
        this.indicatorViewController.setHelperTextEnabled(enabled);
    }

    public void setHelperText(@Nullable CharSequence helperText) {
        if (!TextUtils.isEmpty(helperText)) {
            if (!isHelperTextEnabled()) {
                setHelperTextEnabled(true);
            }
            this.indicatorViewController.showHelper(helperText);
        } else if (isHelperTextEnabled()) {
            setHelperTextEnabled(false);
        }
    }

    public boolean isHelperTextEnabled() {
        return this.indicatorViewController.isHelperTextEnabled();
    }

    public void setHelperTextColor(@Nullable ColorStateList textColors) {
        this.indicatorViewController.setHelperTextViewTextColor(textColors);
    }

    @ColorInt
    public int getHelperTextCurrentTextColor() {
        return this.indicatorViewController.getHelperTextViewCurrentTextColor();
    }

    public void setError(@Nullable CharSequence errorText) {
        if (!this.indicatorViewController.isErrorEnabled()) {
            if (!TextUtils.isEmpty(errorText)) {
                setErrorEnabled(true);
            } else {
                return;
            }
        }
        if (!TextUtils.isEmpty(errorText)) {
            this.indicatorViewController.showError(errorText);
        } else {
            this.indicatorViewController.hideError();
        }
    }

    public void setCounterEnabled(boolean enabled) {
        if (this.counterEnabled != enabled) {
            if (enabled) {
                this.counterView = new AppCompatTextView(getContext());
                this.counterView.setId(R.id.textinput_counter);
                if (this.typeface != null) {
                    this.counterView.setTypeface(this.typeface);
                }
                this.counterView.setMaxLines(1);
                setTextAppearanceCompatWithErrorFallback(this.counterView, this.counterTextAppearance);
                this.indicatorViewController.addIndicator(this.counterView, 2);
                if (this.editText == null) {
                    updateCounter(0);
                } else {
                    updateCounter(this.editText.getText().length());
                }
            } else {
                this.indicatorViewController.removeIndicator(this.counterView, 2);
                this.counterView = null;
            }
            this.counterEnabled = enabled;
        }
    }

    public boolean isCounterEnabled() {
        return this.counterEnabled;
    }

    public void setCounterMaxLength(int maxLength) {
        if (this.counterMaxLength != maxLength) {
            if (maxLength > 0) {
                this.counterMaxLength = maxLength;
            } else {
                this.counterMaxLength = -1;
            }
            if (this.counterEnabled) {
                updateCounter(this.editText == null ? 0 : this.editText.getText().length());
            }
        }
    }

    public void setEnabled(boolean enabled) {
        recursiveSetEnabled(this, enabled);
        super.setEnabled(enabled);
    }

    private static void recursiveSetEnabled(ViewGroup vg, boolean enabled) {
        int count = vg.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = vg.getChildAt(i);
            child.setEnabled(enabled);
            if (child instanceof ViewGroup) {
                recursiveSetEnabled((ViewGroup) child, enabled);
            }
        }
    }

    public int getCounterMaxLength() {
        return this.counterMaxLength;
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public CharSequence getCounterOverflowDescription() {
        if (!this.counterEnabled || !this.counterOverflowed || this.counterView == null) {
            return null;
        }
        return this.counterView.getContentDescription();
    }

    /* access modifiers changed from: package-private */
    public void updateCounter(int length) {
        boolean wasCounterOverflowed = this.counterOverflowed;
        if (this.counterMaxLength == -1) {
            this.counterView.setText(String.valueOf(length));
            this.counterView.setContentDescription((CharSequence) null);
            this.counterOverflowed = false;
        } else {
            if (ViewCompat.getAccessibilityLiveRegion(this.counterView) == 1) {
                ViewCompat.setAccessibilityLiveRegion(this.counterView, 0);
            }
            this.counterOverflowed = length > this.counterMaxLength;
            if (wasCounterOverflowed != this.counterOverflowed) {
                setTextAppearanceCompatWithErrorFallback(this.counterView, this.counterOverflowed ? this.counterOverflowTextAppearance : this.counterTextAppearance);
                if (this.counterOverflowed) {
                    ViewCompat.setAccessibilityLiveRegion(this.counterView, 1);
                }
            }
            this.counterView.setText(getContext().getString(R.string.character_counter_pattern, new Object[]{Integer.valueOf(length), Integer.valueOf(this.counterMaxLength)}));
            this.counterView.setContentDescription(getContext().getString(R.string.character_counter_content_description, new Object[]{Integer.valueOf(length), Integer.valueOf(this.counterMaxLength)}));
        }
        if (this.editText != null && wasCounterOverflowed != this.counterOverflowed) {
            updateLabelState(false);
            updateTextInputBoxState();
            updateEditTextBackground();
        }
    }

    /* access modifiers changed from: package-private */
    public void setTextAppearanceCompatWithErrorFallback(TextView textView, @StyleRes int textAppearance) {
        boolean useDefaultColor = false;
        try {
            TextViewCompat.setTextAppearance(textView, textAppearance);
            if (Build.VERSION.SDK_INT >= 23 && textView.getTextColors().getDefaultColor() == -65281) {
                useDefaultColor = true;
            }
        } catch (Exception e2) {
            useDefaultColor = true;
        }
        if (useDefaultColor) {
            TextViewCompat.setTextAppearance(textView, R.style.TextAppearance_AppCompat_Caption);
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.design_error));
        }
    }

    private void updateTextInputBoxBounds() {
        if (this.boxBackgroundMode != 0 && this.boxBackground != null && this.editText != null && getRight() != 0) {
            int left = this.editText.getLeft();
            int top = calculateBoxBackgroundTop();
            int right = this.editText.getRight();
            int bottom = this.editText.getBottom() + this.boxBottomOffsetPx;
            if (this.boxBackgroundMode == 2) {
                left += this.boxStrokeWidthFocusedPx / 2;
                top -= this.boxStrokeWidthFocusedPx / 2;
                right -= this.boxStrokeWidthFocusedPx / 2;
                bottom += this.boxStrokeWidthFocusedPx / 2;
            }
            this.boxBackground.setBounds(left, top, right, bottom);
            applyBoxAttributes();
            updateEditTextBackgroundBounds();
        }
    }

    private int calculateBoxBackgroundTop() {
        if (this.editText == null) {
            return 0;
        }
        switch (this.boxBackgroundMode) {
            case 1:
                return this.editText.getTop();
            case 2:
                return this.editText.getTop() + calculateLabelMarginTop();
            default:
                return 0;
        }
    }

    private int calculateLabelMarginTop() {
        if (!this.hintEnabled) {
            return 0;
        }
        switch (this.boxBackgroundMode) {
            case 0:
            case 1:
                return (int) this.collapsingTextHelper.getCollapsedTextHeight();
            case 2:
                return (int) (this.collapsingTextHelper.getCollapsedTextHeight() / 2.0f);
            default:
                return 0;
        }
    }

    private int calculateCollapsedTextTopBounds() {
        switch (this.boxBackgroundMode) {
            case 1:
                return getBoxBackground().getBounds().top + this.boxCollapsedPaddingTopPx;
            case 2:
                return getBoxBackground().getBounds().top - calculateLabelMarginTop();
            default:
                return getPaddingTop();
        }
    }

    private void updateEditTextBackgroundBounds() {
        Drawable editTextBackground;
        if (this.editText != null && (editTextBackground = this.editText.getBackground()) != null) {
            if (DrawableUtils.canSafelyMutateDrawable(editTextBackground)) {
                editTextBackground = editTextBackground.mutate();
            }
            DescendantOffsetUtils.getDescendantRect(this, this.editText, new Rect());
            Rect editTextBackgroundBounds = editTextBackground.getBounds();
            if (editTextBackgroundBounds.left != editTextBackgroundBounds.right) {
                Rect editTextBackgroundPadding = new Rect();
                editTextBackground.getPadding(editTextBackgroundPadding);
                editTextBackground.setBounds(editTextBackgroundBounds.left - editTextBackgroundPadding.left, editTextBackgroundBounds.top, editTextBackgroundBounds.right + (editTextBackgroundPadding.right * 2), this.editText.getBottom());
            }
        }
    }

    private void setBoxAttributes() {
        switch (this.boxBackgroundMode) {
            case 1:
                this.boxStrokeWidthPx = 0;
                return;
            case 2:
                if (this.focusedStrokeColor == 0) {
                    this.focusedStrokeColor = this.focusedTextColor.getColorForState(getDrawableState(), this.focusedTextColor.getDefaultColor());
                    return;
                }
                return;
            default:
                return;
        }
    }

    private void applyBoxAttributes() {
        if (this.boxBackground != null) {
            setBoxAttributes();
            if (this.editText != null && this.boxBackgroundMode == 2) {
                if (this.editText.getBackground() != null) {
                    this.editTextOriginalDrawable = this.editText.getBackground();
                }
                ViewCompat.setBackground(this.editText, (Drawable) null);
            }
            if (!(this.editText == null || this.boxBackgroundMode != 1 || this.editTextOriginalDrawable == null)) {
                ViewCompat.setBackground(this.editText, this.editTextOriginalDrawable);
            }
            if (this.boxStrokeWidthPx > -1 && this.boxStrokeColor != 0) {
                this.boxBackground.setStroke(this.boxStrokeWidthPx, this.boxStrokeColor);
            }
            this.boxBackground.setCornerRadii(getCornerRadiiAsArray());
            this.boxBackground.setColor(this.boxBackgroundColor);
            invalidate();
        }
    }

    /* access modifiers changed from: package-private */
    public void updateEditTextBackground() {
        Drawable editTextBackground;
        if (this.editText != null && (editTextBackground = this.editText.getBackground()) != null) {
            ensureBackgroundDrawableStateWorkaround();
            if (DrawableUtils.canSafelyMutateDrawable(editTextBackground)) {
                editTextBackground = editTextBackground.mutate();
            }
            if (this.indicatorViewController.errorShouldBeShown()) {
                editTextBackground.setColorFilter(AppCompatDrawableManager.getPorterDuffColorFilter(this.indicatorViewController.getErrorViewCurrentTextColor(), PorterDuff.Mode.SRC_IN));
            } else if (!this.counterOverflowed || this.counterView == null) {
                DrawableCompat.clearColorFilter(editTextBackground);
                this.editText.refreshDrawableState();
            } else {
                editTextBackground.setColorFilter(AppCompatDrawableManager.getPorterDuffColorFilter(this.counterView.getCurrentTextColor(), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    private void ensureBackgroundDrawableStateWorkaround() {
        Drawable bg;
        int sdk = Build.VERSION.SDK_INT;
        if ((sdk == 21 || sdk == 22) && (bg = this.editText.getBackground()) != null && !this.hasReconstructedEditTextBackground) {
            Drawable newBg = bg.getConstantState().newDrawable();
            if (bg instanceof DrawableContainer) {
                this.hasReconstructedEditTextBackground = DrawableUtils.setContainerConstantState((DrawableContainer) bg, newBg.getConstantState());
            }
            if (!this.hasReconstructedEditTextBackground) {
                ViewCompat.setBackground(this.editText, newBg);
                this.hasReconstructedEditTextBackground = true;
                onApplyBoxBackgroundMode();
            }
        }
    }

    static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() {
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, (ClassLoader) null);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        CharSequence error;
        boolean isPasswordToggledVisible;

        SavedState(Parcelable superState) {
            super(superState);
        }

        SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            this.error = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
            this.isPasswordToggledVisible = source.readInt() != 1 ? false : true;
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            TextUtils.writeToParcel(this.error, dest, flags);
            dest.writeInt(this.isPasswordToggledVisible ? 1 : 0);
        }

        public String toString() {
            return "TextInputLayout.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " error=" + this.error + "}";
        }
    }

    public Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState(super.onSaveInstanceState());
        if (this.indicatorViewController.errorShouldBeShown()) {
            ss.error = getError();
        }
        ss.isPasswordToggledVisible = this.passwordToggledVisible;
        return ss;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setError(ss.error);
        if (ss.isPasswordToggledVisible) {
            passwordVisibilityToggleRequested(true);
        }
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        this.restoringSavedState = true;
        super.dispatchRestoreInstanceState(container);
        this.restoringSavedState = false;
    }

    @Nullable
    public CharSequence getError() {
        if (this.indicatorViewController.isErrorEnabled()) {
            return this.indicatorViewController.getErrorText();
        }
        return null;
    }

    @Nullable
    public CharSequence getHelperText() {
        if (this.indicatorViewController.isHelperTextEnabled()) {
            return this.indicatorViewController.getHelperText();
        }
        return null;
    }

    public boolean isHintAnimationEnabled() {
        return this.hintAnimationEnabled;
    }

    public void setHintAnimationEnabled(boolean enabled) {
        this.hintAnimationEnabled = enabled;
    }

    public void draw(Canvas canvas) {
        if (this.boxBackground != null) {
            this.boxBackground.draw(canvas);
        }
        super.draw(canvas);
        if (this.hintEnabled) {
            this.collapsingTextHelper.draw(canvas);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        updatePasswordToggleView();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void updatePasswordToggleView() {
        if (this.editText != null) {
            if (shouldShowPasswordIcon()) {
                if (this.passwordToggleView == null) {
                    this.passwordToggleView = (CheckableImageButton) LayoutInflater.from(getContext()).inflate(R.layout.design_text_input_password_icon, this.inputFrame, false);
                    this.passwordToggleView.setImageDrawable(this.passwordToggleDrawable);
                    this.passwordToggleView.setContentDescription(this.passwordToggleContentDesc);
                    this.inputFrame.addView(this.passwordToggleView);
                    this.passwordToggleView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            TextInputLayout.this.passwordVisibilityToggleRequested(false);
                        }
                    });
                }
                if (this.editText != null && ViewCompat.getMinimumHeight(this.editText) <= 0) {
                    this.editText.setMinimumHeight(ViewCompat.getMinimumHeight(this.passwordToggleView));
                }
                this.passwordToggleView.setVisibility(0);
                this.passwordToggleView.setChecked(this.passwordToggledVisible);
                if (this.passwordToggleDummyDrawable == null) {
                    this.passwordToggleDummyDrawable = new ColorDrawable();
                }
                this.passwordToggleDummyDrawable.setBounds(0, 0, this.passwordToggleView.getMeasuredWidth(), 1);
                Drawable[] compounds = TextViewCompat.getCompoundDrawablesRelative(this.editText);
                if (compounds[2] != this.passwordToggleDummyDrawable) {
                    this.originalEditTextEndDrawable = compounds[2];
                }
                TextViewCompat.setCompoundDrawablesRelative(this.editText, compounds[0], compounds[1], this.passwordToggleDummyDrawable, compounds[3]);
                this.passwordToggleView.setPadding(this.editText.getPaddingLeft(), this.editText.getPaddingTop(), this.editText.getPaddingRight(), this.editText.getPaddingBottom());
                return;
            }
            if (this.passwordToggleView != null && this.passwordToggleView.getVisibility() == 0) {
                this.passwordToggleView.setVisibility(8);
            }
            if (this.passwordToggleDummyDrawable != null) {
                Drawable[] compounds2 = TextViewCompat.getCompoundDrawablesRelative(this.editText);
                if (compounds2[2] == this.passwordToggleDummyDrawable) {
                    TextViewCompat.setCompoundDrawablesRelative(this.editText, compounds2[0], compounds2[1], this.originalEditTextEndDrawable, compounds2[3]);
                    this.passwordToggleDummyDrawable = null;
                }
            }
        }
    }

    public void setPasswordVisibilityToggleDrawable(@DrawableRes int resId) {
        setPasswordVisibilityToggleDrawable(resId != 0 ? AppCompatResources.getDrawable(getContext(), resId) : null);
    }

    public void setPasswordVisibilityToggleDrawable(@Nullable Drawable icon) {
        this.passwordToggleDrawable = icon;
        if (this.passwordToggleView != null) {
            this.passwordToggleView.setImageDrawable(icon);
        }
    }

    public void setPasswordVisibilityToggleContentDescription(@StringRes int resId) {
        setPasswordVisibilityToggleContentDescription(resId != 0 ? getResources().getText(resId) : null);
    }

    public void setPasswordVisibilityToggleContentDescription(@Nullable CharSequence description) {
        this.passwordToggleContentDesc = description;
        if (this.passwordToggleView != null) {
            this.passwordToggleView.setContentDescription(description);
        }
    }

    @Nullable
    public Drawable getPasswordVisibilityToggleDrawable() {
        return this.passwordToggleDrawable;
    }

    @Nullable
    public CharSequence getPasswordVisibilityToggleContentDescription() {
        return this.passwordToggleContentDesc;
    }

    public boolean isPasswordVisibilityToggleEnabled() {
        return this.passwordToggleEnabled;
    }

    public void setPasswordVisibilityToggleEnabled(boolean enabled) {
        if (this.passwordToggleEnabled != enabled) {
            this.passwordToggleEnabled = enabled;
            if (!enabled && this.passwordToggledVisible && this.editText != null) {
                this.editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            this.passwordToggledVisible = false;
            updatePasswordToggleView();
        }
    }

    public void setPasswordVisibilityToggleTintList(@Nullable ColorStateList tintList) {
        this.passwordToggleTintList = tintList;
        this.hasPasswordToggleTintList = true;
        applyPasswordToggleTint();
    }

    public void setPasswordVisibilityToggleTintMode(@Nullable PorterDuff.Mode mode) {
        this.passwordToggleTintMode = mode;
        this.hasPasswordToggleTintMode = true;
        applyPasswordToggleTint();
    }

    public void passwordVisibilityToggleRequested(boolean shouldSkipAnimations) {
        if (this.passwordToggleEnabled) {
            int selection = this.editText.getSelectionEnd();
            if (hasPasswordTransformation()) {
                this.editText.setTransformationMethod((TransformationMethod) null);
                this.passwordToggledVisible = true;
            } else {
                this.editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.passwordToggledVisible = false;
            }
            this.passwordToggleView.setChecked(this.passwordToggledVisible);
            if (shouldSkipAnimations) {
                this.passwordToggleView.jumpDrawablesToCurrentState();
            }
            this.editText.setSelection(selection);
        }
    }

    public void setTextInputAccessibilityDelegate(AccessibilityDelegate delegate) {
        if (this.editText != null) {
            ViewCompat.setAccessibilityDelegate(this.editText, delegate);
        }
    }

    private boolean hasPasswordTransformation() {
        return this.editText != null && (this.editText.getTransformationMethod() instanceof PasswordTransformationMethod);
    }

    private boolean shouldShowPasswordIcon() {
        return this.passwordToggleEnabled && (hasPasswordTransformation() || this.passwordToggledVisible);
    }

    private void applyPasswordToggleTint() {
        if (this.passwordToggleDrawable == null) {
            return;
        }
        if (this.hasPasswordToggleTintList || this.hasPasswordToggleTintMode) {
            this.passwordToggleDrawable = DrawableCompat.wrap(this.passwordToggleDrawable).mutate();
            if (this.hasPasswordToggleTintList) {
                DrawableCompat.setTintList(this.passwordToggleDrawable, this.passwordToggleTintList);
            }
            if (this.hasPasswordToggleTintMode) {
                DrawableCompat.setTintMode(this.passwordToggleDrawable, this.passwordToggleTintMode);
            }
            if (this.passwordToggleView != null && this.passwordToggleView.getDrawable() != this.passwordToggleDrawable) {
                this.passwordToggleView.setImageDrawable(this.passwordToggleDrawable);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.boxBackground != null) {
            updateTextInputBoxBounds();
        }
        if (this.hintEnabled && this.editText != null) {
            Rect rect = this.tmpRect;
            DescendantOffsetUtils.getDescendantRect(this, this.editText, rect);
            int l = rect.left + this.editText.getCompoundPaddingLeft();
            int r = rect.right - this.editText.getCompoundPaddingRight();
            int t = calculateCollapsedTextTopBounds();
            this.collapsingTextHelper.setExpandedBounds(l, rect.top + this.editText.getCompoundPaddingTop(), r, rect.bottom - this.editText.getCompoundPaddingBottom());
            this.collapsingTextHelper.setCollapsedBounds(l, t, r, (bottom - top) - getPaddingBottom());
            this.collapsingTextHelper.recalculate();
            if (cutoutEnabled() && !this.hintExpanded) {
                openCutout();
            }
        }
    }

    private void collapseHint(boolean animate) {
        if (this.animator != null && this.animator.isRunning()) {
            this.animator.cancel();
        }
        if (!animate || !this.hintAnimationEnabled) {
            this.collapsingTextHelper.setExpansionFraction(1.0f);
        } else {
            animateToExpansionFraction(1.0f);
        }
        this.hintExpanded = false;
        if (cutoutEnabled()) {
            openCutout();
        }
    }

    private boolean cutoutEnabled() {
        return this.hintEnabled && !TextUtils.isEmpty(this.hint) && (this.boxBackground instanceof CutoutDrawable);
    }

    private void openCutout() {
        if (cutoutEnabled()) {
            RectF cutoutBounds = this.tmpRectF;
            this.collapsingTextHelper.getCollapsedTextActualBounds(cutoutBounds);
            applyCutoutPadding(cutoutBounds);
            ((CutoutDrawable) this.boxBackground).setCutout(cutoutBounds);
        }
    }

    private void closeCutout() {
        if (cutoutEnabled()) {
            ((CutoutDrawable) this.boxBackground).removeCutout();
        }
    }

    private void applyCutoutPadding(RectF cutoutBounds) {
        cutoutBounds.left -= (float) this.boxLabelCutoutPaddingPx;
        cutoutBounds.top -= (float) this.boxLabelCutoutPaddingPx;
        cutoutBounds.right += (float) this.boxLabelCutoutPaddingPx;
        cutoutBounds.bottom += (float) this.boxLabelCutoutPaddingPx;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean cutoutIsOpen() {
        return cutoutEnabled() && ((CutoutDrawable) this.boxBackground).hasCutout();
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        if (!this.inDrawableStateChanged) {
            boolean z = true;
            this.inDrawableStateChanged = true;
            super.drawableStateChanged();
            int[] state = getDrawableState();
            boolean changed = false;
            if (!ViewCompat.isLaidOut(this) || !isEnabled()) {
                z = false;
            }
            updateLabelState(z);
            updateEditTextBackground();
            updateTextInputBoxBounds();
            updateTextInputBoxState();
            if (this.collapsingTextHelper != null) {
                changed = false | this.collapsingTextHelper.setState(state);
            }
            if (changed) {
                invalidate();
            }
            this.inDrawableStateChanged = false;
        }
    }

    /* access modifiers changed from: package-private */
    public void updateTextInputBoxState() {
        if (this.boxBackground != null && this.boxBackgroundMode != 0) {
            boolean isHovered = true;
            boolean hasFocus = this.editText != null && this.editText.hasFocus();
            if (this.editText == null || !this.editText.isHovered()) {
                isHovered = false;
            }
            if (this.boxBackgroundMode == 2) {
                if (!isEnabled()) {
                    this.boxStrokeColor = this.disabledColor;
                } else if (this.indicatorViewController.errorShouldBeShown()) {
                    this.boxStrokeColor = this.indicatorViewController.getErrorViewCurrentTextColor();
                } else if (this.counterOverflowed && this.counterView != null) {
                    this.boxStrokeColor = this.counterView.getCurrentTextColor();
                } else if (hasFocus) {
                    this.boxStrokeColor = this.focusedStrokeColor;
                } else if (isHovered) {
                    this.boxStrokeColor = this.hoveredStrokeColor;
                } else {
                    this.boxStrokeColor = this.defaultStrokeColor;
                }
                if ((isHovered || hasFocus) && isEnabled()) {
                    this.boxStrokeWidthPx = this.boxStrokeWidthFocusedPx;
                } else {
                    this.boxStrokeWidthPx = this.boxStrokeWidthDefaultPx;
                }
                applyBoxAttributes();
            }
        }
    }

    private void expandHint(boolean animate) {
        if (this.animator != null && this.animator.isRunning()) {
            this.animator.cancel();
        }
        if (!animate || !this.hintAnimationEnabled) {
            this.collapsingTextHelper.setExpansionFraction(0.0f);
        } else {
            animateToExpansionFraction(0.0f);
        }
        if (cutoutEnabled() && ((CutoutDrawable) this.boxBackground).hasCutout()) {
            closeCutout();
        }
        this.hintExpanded = true;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void animateToExpansionFraction(float target) {
        if (this.collapsingTextHelper.getExpansionFraction() != target) {
            if (this.animator == null) {
                this.animator = new ValueAnimator();
                this.animator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
                this.animator.setDuration(167);
                this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animator) {
                        TextInputLayout.this.collapsingTextHelper.setExpansionFraction(((Float) animator.getAnimatedValue()).floatValue());
                    }
                });
            }
            this.animator.setFloatValues(new float[]{this.collapsingTextHelper.getExpansionFraction(), target});
            this.animator.start();
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public final boolean isHintExpanded() {
        return this.hintExpanded;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public final boolean isHelperTextDisplayed() {
        return this.indicatorViewController.helperTextIsDisplayed();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public final int getHintCurrentCollapsedTextColor() {
        return this.collapsingTextHelper.getCurrentCollapsedTextColor();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public final float getHintCollapsedTextHeight() {
        return this.collapsingTextHelper.getCollapsedTextHeight();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public final int getErrorTextCurrentColor() {
        return this.indicatorViewController.getErrorViewCurrentTextColor();
    }

    public static class AccessibilityDelegate extends AccessibilityDelegateCompat {
        private final TextInputLayout layout;

        public AccessibilityDelegate(TextInputLayout layout2) {
            this.layout = layout2;
        }

        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            EditText editText = this.layout.getEditText();
            CharSequence text = editText != null ? editText.getText() : null;
            CharSequence hintText = this.layout.getHint();
            CharSequence errorText = this.layout.getError();
            CharSequence counterDesc = this.layout.getCounterOverflowDescription();
            boolean showingText = !TextUtils.isEmpty(text);
            boolean hasHint = !TextUtils.isEmpty(hintText);
            boolean showingError = !TextUtils.isEmpty(errorText);
            boolean z = false;
            boolean contentInvalid = showingError || !TextUtils.isEmpty(counterDesc);
            if (showingText) {
                info.setText(text);
            } else if (hasHint) {
                info.setText(hintText);
            }
            if (hasHint) {
                info.setHintText(hintText);
                if (!showingText && hasHint) {
                    z = true;
                }
                info.setShowingHintText(z);
            }
            if (contentInvalid) {
                info.setError(showingError ? errorText : counterDesc);
                info.setContentInvalid(true);
            }
        }

        public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
            super.onPopulateAccessibilityEvent(host, event);
            EditText editText = this.layout.getEditText();
            CharSequence text = editText != null ? editText.getText() : null;
            CharSequence eventText = TextUtils.isEmpty(text) ? this.layout.getHint() : text;
            if (!TextUtils.isEmpty(eventText)) {
                event.getText().add(eventText);
            }
        }
    }
}
