package android.support.design.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.R;
import android.support.design.animation.AnimationUtils;
import android.support.design.animation.AnimatorSetCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.Space;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

final class IndicatorViewController {
    private static final int CAPTION_OPACITY_FADE_ANIMATION_DURATION = 167;
    private static final int CAPTION_STATE_ERROR = 1;
    private static final int CAPTION_STATE_HELPER_TEXT = 2;
    private static final int CAPTION_STATE_NONE = 0;
    private static final int CAPTION_TRANSLATE_Y_ANIMATION_DURATION = 217;
    static final int COUNTER_INDEX = 2;
    static final int ERROR_INDEX = 0;
    static final int HELPER_INDEX = 1;
    /* access modifiers changed from: private */
    @Nullable
    public Animator captionAnimator;
    private FrameLayout captionArea;
    /* access modifiers changed from: private */
    public int captionDisplayed;
    private int captionToShow;
    private final float captionTranslationYPx = ((float) this.context.getResources().getDimensionPixelSize(R.dimen.design_textinput_caption_translate_y));
    private int captionViewsAdded;
    private final Context context;
    private boolean errorEnabled;
    private CharSequence errorText;
    private int errorTextAppearance;
    /* access modifiers changed from: private */
    public TextView errorView;
    private CharSequence helperText;
    private boolean helperTextEnabled;
    private int helperTextTextAppearance;
    private TextView helperTextView;
    private LinearLayout indicatorArea;
    private int indicatorsAdded;
    private final TextInputLayout textInputView;
    private Typeface typeface;

    public IndicatorViewController(TextInputLayout textInputView2) {
        this.context = textInputView2.getContext();
        this.textInputView = textInputView2;
    }

    /* access modifiers changed from: package-private */
    public void showHelper(CharSequence helperText2) {
        cancelCaptionAnimator();
        this.helperText = helperText2;
        this.helperTextView.setText(helperText2);
        if (this.captionDisplayed != 2) {
            this.captionToShow = 2;
        }
        updateCaptionViewsVisibility(this.captionDisplayed, this.captionToShow, shouldAnimateCaptionView(this.helperTextView, helperText2));
    }

    /* access modifiers changed from: package-private */
    public void hideHelperText() {
        cancelCaptionAnimator();
        if (this.captionDisplayed == 2) {
            this.captionToShow = 0;
        }
        updateCaptionViewsVisibility(this.captionDisplayed, this.captionToShow, shouldAnimateCaptionView(this.helperTextView, (CharSequence) null));
    }

    /* access modifiers changed from: package-private */
    public void showError(CharSequence errorText2) {
        cancelCaptionAnimator();
        this.errorText = errorText2;
        this.errorView.setText(errorText2);
        if (this.captionDisplayed != 1) {
            this.captionToShow = 1;
        }
        updateCaptionViewsVisibility(this.captionDisplayed, this.captionToShow, shouldAnimateCaptionView(this.errorView, errorText2));
    }

    /* access modifiers changed from: package-private */
    public void hideError() {
        this.errorText = null;
        cancelCaptionAnimator();
        if (this.captionDisplayed == 1) {
            if (!this.helperTextEnabled || TextUtils.isEmpty(this.helperText)) {
                this.captionToShow = 0;
            } else {
                this.captionToShow = 2;
            }
        }
        updateCaptionViewsVisibility(this.captionDisplayed, this.captionToShow, shouldAnimateCaptionView(this.errorView, (CharSequence) null));
    }

    private boolean shouldAnimateCaptionView(TextView captionView, @Nullable CharSequence captionText) {
        return ViewCompat.isLaidOut(this.textInputView) && this.textInputView.isEnabled() && (this.captionToShow != this.captionDisplayed || captionView == null || !TextUtils.equals(captionView.getText(), captionText));
    }

    private void updateCaptionViewsVisibility(int captionToHide, int captionToShow2, boolean animate) {
        boolean z = animate;
        if (z) {
            AnimatorSet captionAnimator2 = new AnimatorSet();
            this.captionAnimator = captionAnimator2;
            List<Animator> captionAnimatorList = new ArrayList<>();
            List<Animator> list = captionAnimatorList;
            int i = captionToHide;
            int i2 = captionToShow2;
            createCaptionAnimators(list, this.helperTextEnabled, this.helperTextView, 2, i, i2);
            createCaptionAnimators(list, this.errorEnabled, this.errorView, 1, i, i2);
            AnimatorSetCompat.playTogether(captionAnimator2, captionAnimatorList);
            final int i3 = captionToShow2;
            final TextView captionViewFromDisplayState = getCaptionViewFromDisplayState(captionToHide);
            final int i4 = captionToHide;
            final TextView captionViewFromDisplayState2 = getCaptionViewFromDisplayState(captionToShow2);
            captionAnimator2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    int unused = IndicatorViewController.this.captionDisplayed = i3;
                    Animator unused2 = IndicatorViewController.this.captionAnimator = null;
                    if (captionViewFromDisplayState != null) {
                        captionViewFromDisplayState.setVisibility(4);
                        if (i4 == 1 && IndicatorViewController.this.errorView != null) {
                            IndicatorViewController.this.errorView.setText((CharSequence) null);
                        }
                    }
                }

                public void onAnimationStart(Animator animator) {
                    if (captionViewFromDisplayState2 != null) {
                        captionViewFromDisplayState2.setVisibility(0);
                    }
                }
            });
            captionAnimator2.start();
        } else {
            int i5 = captionToShow2;
            setCaptionViewVisibilities(captionToHide, captionToShow2);
        }
        this.textInputView.updateEditTextBackground();
        this.textInputView.updateLabelState(z);
        this.textInputView.updateTextInputBoxState();
    }

    private void setCaptionViewVisibilities(int captionToHide, int captionToShow2) {
        TextView captionViewDisplayed;
        TextView captionViewToShow;
        if (captionToHide != captionToShow2) {
            if (!(captionToShow2 == 0 || (captionViewToShow = getCaptionViewFromDisplayState(captionToShow2)) == null)) {
                captionViewToShow.setVisibility(0);
                captionViewToShow.setAlpha(1.0f);
            }
            if (!(captionToHide == 0 || (captionViewDisplayed = getCaptionViewFromDisplayState(captionToHide)) == null)) {
                captionViewDisplayed.setVisibility(4);
                if (captionToHide == 1) {
                    captionViewDisplayed.setText((CharSequence) null);
                }
            }
            this.captionDisplayed = captionToShow2;
        }
    }

    private void createCaptionAnimators(List<Animator> captionAnimatorList, boolean captionEnabled, TextView captionView, int captionState, int captionToHide, int captionToShow2) {
        if (captionView != null && captionEnabled) {
            if (captionState == captionToShow2 || captionState == captionToHide) {
                captionAnimatorList.add(createCaptionOpacityAnimator(captionView, captionToShow2 == captionState));
                if (captionToShow2 == captionState) {
                    captionAnimatorList.add(createCaptionTranslationYAnimator(captionView));
                }
            }
        }
    }

    private ObjectAnimator createCaptionOpacityAnimator(TextView captionView, boolean display) {
        ObjectAnimator opacityAnimator = ObjectAnimator.ofFloat(captionView, View.ALPHA, new float[]{display ? 1.0f : 0.0f});
        opacityAnimator.setDuration(167);
        opacityAnimator.setInterpolator(AnimationUtils.LINEAR_INTERPOLATOR);
        return opacityAnimator;
    }

    private ObjectAnimator createCaptionTranslationYAnimator(TextView captionView) {
        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(captionView, View.TRANSLATION_Y, new float[]{-this.captionTranslationYPx, 0.0f});
        translationYAnimator.setDuration(217);
        translationYAnimator.setInterpolator(AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR);
        return translationYAnimator;
    }

    /* access modifiers changed from: package-private */
    public void cancelCaptionAnimator() {
        if (this.captionAnimator != null) {
            this.captionAnimator.cancel();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isCaptionView(int index) {
        return index == 0 || index == 1;
    }

    @Nullable
    private TextView getCaptionViewFromDisplayState(int captionDisplayState) {
        switch (captionDisplayState) {
            case 1:
                return this.errorView;
            case 2:
                return this.helperTextView;
            default:
                return null;
        }
    }

    /* access modifiers changed from: package-private */
    public void adjustIndicatorPadding() {
        if (canAdjustIndicatorPadding()) {
            ViewCompat.setPaddingRelative(this.indicatorArea, ViewCompat.getPaddingStart(this.textInputView.getEditText()), 0, ViewCompat.getPaddingEnd(this.textInputView.getEditText()), 0);
        }
    }

    private boolean canAdjustIndicatorPadding() {
        return (this.indicatorArea == null || this.textInputView.getEditText() == null) ? false : true;
    }

    /* access modifiers changed from: package-private */
    public void addIndicator(TextView indicator, int index) {
        if (this.indicatorArea == null && this.captionArea == null) {
            this.indicatorArea = new LinearLayout(this.context);
            this.indicatorArea.setOrientation(0);
            this.textInputView.addView(this.indicatorArea, -1, -2);
            this.captionArea = new FrameLayout(this.context);
            this.indicatorArea.addView(this.captionArea, -1, new FrameLayout.LayoutParams(-2, -2));
            this.indicatorArea.addView(new Space(this.context), new LinearLayout.LayoutParams(0, 0, 1.0f));
            if (this.textInputView.getEditText() != null) {
                adjustIndicatorPadding();
            }
        }
        if (isCaptionView(index)) {
            this.captionArea.setVisibility(0);
            this.captionArea.addView(indicator);
            this.captionViewsAdded++;
        } else {
            this.indicatorArea.addView(indicator, index);
        }
        this.indicatorArea.setVisibility(0);
        this.indicatorsAdded++;
    }

    /* access modifiers changed from: package-private */
    public void removeIndicator(TextView indicator, int index) {
        if (this.indicatorArea != null) {
            if (!isCaptionView(index) || this.captionArea == null) {
                this.indicatorArea.removeView(indicator);
            } else {
                this.captionViewsAdded--;
                setViewGroupGoneIfEmpty(this.captionArea, this.captionViewsAdded);
                this.captionArea.removeView(indicator);
            }
            this.indicatorsAdded--;
            setViewGroupGoneIfEmpty(this.indicatorArea, this.indicatorsAdded);
        }
    }

    private void setViewGroupGoneIfEmpty(ViewGroup viewGroup, int indicatorsAdded2) {
        if (indicatorsAdded2 == 0) {
            viewGroup.setVisibility(8);
        }
    }

    /* access modifiers changed from: package-private */
    public void setErrorEnabled(boolean enabled) {
        if (this.errorEnabled != enabled) {
            cancelCaptionAnimator();
            if (enabled) {
                this.errorView = new AppCompatTextView(this.context);
                this.errorView.setId(R.id.textinput_error);
                if (this.typeface != null) {
                    this.errorView.setTypeface(this.typeface);
                }
                setErrorTextAppearance(this.errorTextAppearance);
                this.errorView.setVisibility(4);
                ViewCompat.setAccessibilityLiveRegion(this.errorView, 1);
                addIndicator(this.errorView, 0);
            } else {
                hideError();
                removeIndicator(this.errorView, 0);
                this.errorView = null;
                this.textInputView.updateEditTextBackground();
                this.textInputView.updateTextInputBoxState();
            }
            this.errorEnabled = enabled;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isErrorEnabled() {
        return this.errorEnabled;
    }

    /* access modifiers changed from: package-private */
    public boolean isHelperTextEnabled() {
        return this.helperTextEnabled;
    }

    /* access modifiers changed from: package-private */
    public void setHelperTextEnabled(boolean enabled) {
        if (this.helperTextEnabled != enabled) {
            cancelCaptionAnimator();
            if (enabled) {
                this.helperTextView = new AppCompatTextView(this.context);
                this.helperTextView.setId(R.id.textinput_helper_text);
                if (this.typeface != null) {
                    this.helperTextView.setTypeface(this.typeface);
                }
                this.helperTextView.setVisibility(4);
                ViewCompat.setAccessibilityLiveRegion(this.helperTextView, 1);
                setHelperTextAppearance(this.helperTextTextAppearance);
                addIndicator(this.helperTextView, 1);
            } else {
                hideHelperText();
                removeIndicator(this.helperTextView, 1);
                this.helperTextView = null;
                this.textInputView.updateEditTextBackground();
                this.textInputView.updateTextInputBoxState();
            }
            this.helperTextEnabled = enabled;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean errorIsDisplayed() {
        return isCaptionStateError(this.captionDisplayed);
    }

    /* access modifiers changed from: package-private */
    public boolean errorShouldBeShown() {
        return isCaptionStateError(this.captionToShow);
    }

    private boolean isCaptionStateError(int captionState) {
        if (captionState != 1 || this.errorView == null || TextUtils.isEmpty(this.errorText)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean helperTextIsDisplayed() {
        return isCaptionStateHelperText(this.captionDisplayed);
    }

    /* access modifiers changed from: package-private */
    public boolean helperTextShouldBeShown() {
        return isCaptionStateHelperText(this.captionToShow);
    }

    private boolean isCaptionStateHelperText(int captionState) {
        return captionState == 2 && this.helperTextView != null && !TextUtils.isEmpty(this.helperText);
    }

    /* access modifiers changed from: package-private */
    public CharSequence getErrorText() {
        return this.errorText;
    }

    /* access modifiers changed from: package-private */
    public CharSequence getHelperText() {
        return this.helperText;
    }

    /* access modifiers changed from: package-private */
    public void setTypefaces(Typeface typeface2) {
        if (typeface2 != this.typeface) {
            this.typeface = typeface2;
            setTextViewTypeface(this.errorView, typeface2);
            setTextViewTypeface(this.helperTextView, typeface2);
        }
    }

    private void setTextViewTypeface(@Nullable TextView captionView, Typeface typeface2) {
        if (captionView != null) {
            captionView.setTypeface(typeface2);
        }
    }

    /* access modifiers changed from: package-private */
    @ColorInt
    public int getErrorViewCurrentTextColor() {
        if (this.errorView != null) {
            return this.errorView.getCurrentTextColor();
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public ColorStateList getErrorViewTextColors() {
        if (this.errorView != null) {
            return this.errorView.getTextColors();
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void setErrorViewTextColor(@Nullable ColorStateList textColors) {
        if (this.errorView != null) {
            this.errorView.setTextColor(textColors);
        }
    }

    /* access modifiers changed from: package-private */
    public void setErrorTextAppearance(@StyleRes int resId) {
        this.errorTextAppearance = resId;
        if (this.errorView != null) {
            this.textInputView.setTextAppearanceCompatWithErrorFallback(this.errorView, resId);
        }
    }

    /* access modifiers changed from: package-private */
    @ColorInt
    public int getHelperTextViewCurrentTextColor() {
        if (this.helperTextView != null) {
            return this.helperTextView.getCurrentTextColor();
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public ColorStateList getHelperTextViewColors() {
        if (this.helperTextView != null) {
            return this.helperTextView.getTextColors();
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void setHelperTextViewTextColor(@Nullable ColorStateList textColors) {
        if (this.helperTextView != null) {
            this.helperTextView.setTextColor(textColors);
        }
    }

    /* access modifiers changed from: package-private */
    public void setHelperTextAppearance(@StyleRes int resId) {
        this.helperTextTextAppearance = resId;
        if (this.helperTextView != null) {
            TextViewCompat.setTextAppearance(this.helperTextView, resId);
        }
    }
}
