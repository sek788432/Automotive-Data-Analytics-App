package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import java.util.Map;

public class ChangeBounds extends Transition {
    private static final Property<View, PointF> BOTTOM_RIGHT_ONLY_PROPERTY = new Property<View, PointF>(PointF.class, "bottomRight") {
        public void set(View view, PointF bottomRight) {
            ViewUtils.setLeftTopRightBottom(view, view.getLeft(), view.getTop(), Math.round(bottomRight.x), Math.round(bottomRight.y));
        }

        public PointF get(View view) {
            return null;
        }
    };
    private static final Property<ViewBounds, PointF> BOTTOM_RIGHT_PROPERTY = new Property<ViewBounds, PointF>(PointF.class, "bottomRight") {
        public void set(ViewBounds viewBounds, PointF bottomRight) {
            viewBounds.setBottomRight(bottomRight);
        }

        public PointF get(ViewBounds viewBounds) {
            return null;
        }
    };
    private static final Property<Drawable, PointF> DRAWABLE_ORIGIN_PROPERTY = new Property<Drawable, PointF>(PointF.class, "boundsOrigin") {
        private Rect mBounds = new Rect();

        public void set(Drawable object, PointF value) {
            object.copyBounds(this.mBounds);
            this.mBounds.offsetTo(Math.round(value.x), Math.round(value.y));
            object.setBounds(this.mBounds);
        }

        public PointF get(Drawable object) {
            object.copyBounds(this.mBounds);
            return new PointF((float) this.mBounds.left, (float) this.mBounds.top);
        }
    };
    private static final Property<View, PointF> POSITION_PROPERTY = new Property<View, PointF>(PointF.class, "position") {
        public void set(View view, PointF topLeft) {
            int left = Math.round(topLeft.x);
            int top = Math.round(topLeft.y);
            ViewUtils.setLeftTopRightBottom(view, left, top, view.getWidth() + left, view.getHeight() + top);
        }

        public PointF get(View view) {
            return null;
        }
    };
    private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";
    private static final String PROPNAME_CLIP = "android:changeBounds:clip";
    private static final String PROPNAME_PARENT = "android:changeBounds:parent";
    private static final String PROPNAME_WINDOW_X = "android:changeBounds:windowX";
    private static final String PROPNAME_WINDOW_Y = "android:changeBounds:windowY";
    private static final Property<View, PointF> TOP_LEFT_ONLY_PROPERTY = new Property<View, PointF>(PointF.class, "topLeft") {
        public void set(View view, PointF topLeft) {
            ViewUtils.setLeftTopRightBottom(view, Math.round(topLeft.x), Math.round(topLeft.y), view.getRight(), view.getBottom());
        }

        public PointF get(View view) {
            return null;
        }
    };
    private static final Property<ViewBounds, PointF> TOP_LEFT_PROPERTY = new Property<ViewBounds, PointF>(PointF.class, "topLeft") {
        public void set(ViewBounds viewBounds, PointF topLeft) {
            viewBounds.setTopLeft(topLeft);
        }

        public PointF get(ViewBounds viewBounds) {
            return null;
        }
    };
    private static RectEvaluator sRectEvaluator = new RectEvaluator();
    private static final String[] sTransitionProperties = {PROPNAME_BOUNDS, PROPNAME_CLIP, PROPNAME_PARENT, PROPNAME_WINDOW_X, PROPNAME_WINDOW_Y};
    private boolean mReparent = false;
    private boolean mResizeClip = false;
    private int[] mTempLocation = new int[2];

    public ChangeBounds() {
    }

    public ChangeBounds(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, Styleable.CHANGE_BOUNDS);
        boolean resizeClip = TypedArrayUtils.getNamedBoolean(a, (XmlResourceParser) attrs, "resizeClip", 0, false);
        a.recycle();
        setResizeClip(resizeClip);
    }

    @Nullable
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    public void setResizeClip(boolean resizeClip) {
        this.mResizeClip = resizeClip;
    }

    public boolean getResizeClip() {
        return this.mResizeClip;
    }

    private void captureValues(TransitionValues values) {
        View view = values.view;
        if (ViewCompat.isLaidOut(view) || view.getWidth() != 0 || view.getHeight() != 0) {
            values.values.put(PROPNAME_BOUNDS, new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
            values.values.put(PROPNAME_PARENT, values.view.getParent());
            if (this.mReparent) {
                values.view.getLocationInWindow(this.mTempLocation);
                values.values.put(PROPNAME_WINDOW_X, Integer.valueOf(this.mTempLocation[0]));
                values.values.put(PROPNAME_WINDOW_Y, Integer.valueOf(this.mTempLocation[1]));
            }
            if (this.mResizeClip) {
                values.values.put(PROPNAME_CLIP, ViewCompat.getClipBounds(view));
            }
        }
    }

    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    private boolean parentMatches(View startParent, View endParent) {
        if (!this.mReparent) {
            return true;
        }
        boolean parentMatches = true;
        TransitionValues endValues = getMatchedTransitionValues(startParent, true);
        if (endValues == null) {
            if (startParent != endParent) {
                parentMatches = false;
            }
            return parentMatches;
        }
        if (endParent != endValues.view) {
            parentMatches = false;
        }
        return parentMatches;
    }

    @Nullable
    public Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
        boolean z;
        View view;
        Animator anim;
        int endLeft;
        int startTop;
        int startLeft;
        ObjectAnimator positionAnimator;
        Rect startClip;
        int i;
        Rect endClip;
        ObjectAnimator clipAnimator;
        int endWidth;
        int startHeight;
        TransitionValues transitionValues = startValues;
        TransitionValues transitionValues2 = endValues;
        if (transitionValues == null || transitionValues2 == null) {
            ViewGroup viewGroup = sceneRoot;
            TransitionValues transitionValues3 = transitionValues2;
            return null;
        }
        Map<String, Object> startParentVals = transitionValues.values;
        Map<String, Object> endParentVals = transitionValues2.values;
        ViewGroup startParent = (ViewGroup) startParentVals.get(PROPNAME_PARENT);
        ViewGroup endParent = (ViewGroup) endParentVals.get(PROPNAME_PARENT);
        if (startParent == null) {
            ViewGroup viewGroup2 = sceneRoot;
            Map<String, Object> map = startParentVals;
            Map<String, Object> map2 = endParentVals;
            ViewGroup viewGroup3 = startParent;
            ViewGroup viewGroup4 = endParent;
            TransitionValues transitionValues4 = transitionValues2;
            return null;
        } else if (endParent == null) {
            ViewGroup viewGroup5 = sceneRoot;
            Map<String, Object> map3 = startParentVals;
            Map<String, Object> map4 = endParentVals;
            ViewGroup viewGroup6 = startParent;
            ViewGroup viewGroup7 = endParent;
            TransitionValues transitionValues5 = transitionValues2;
            return null;
        } else {
            View view2 = transitionValues2.view;
            if (parentMatches(startParent, endParent)) {
                Rect startBounds = (Rect) transitionValues.values.get(PROPNAME_BOUNDS);
                Rect endBounds = (Rect) transitionValues2.values.get(PROPNAME_BOUNDS);
                int startLeft2 = startBounds.left;
                int endLeft2 = endBounds.left;
                int startTop2 = startBounds.top;
                int endTop = endBounds.top;
                int startRight = startBounds.right;
                Map<String, Object> map5 = startParentVals;
                int endRight = endBounds.right;
                Map<String, Object> map6 = endParentVals;
                int startBottom = startBounds.bottom;
                ViewGroup viewGroup8 = startParent;
                int endBottom = endBounds.bottom;
                ViewGroup viewGroup9 = endParent;
                int startWidth = startRight - startLeft2;
                Rect rect = startBounds;
                int startHeight2 = startBottom - startTop2;
                Rect rect2 = endBounds;
                int endWidth2 = endRight - endLeft2;
                int endHeight = endBottom - endTop;
                View view3 = view2;
                Rect startClip2 = (Rect) transitionValues.values.get(PROPNAME_CLIP);
                Rect endClip2 = (Rect) transitionValues2.values.get(PROPNAME_CLIP);
                int numChanges = 0;
                if (!((startWidth == 0 || startHeight2 == 0) && (endWidth2 == 0 || endHeight == 0))) {
                    if (!(startLeft2 == endLeft2 && startTop2 == endTop)) {
                        numChanges = 0 + 1;
                    }
                    if (!(startRight == endRight && startBottom == endBottom)) {
                        numChanges++;
                    }
                }
                if ((startClip2 != null && !startClip2.equals(endClip2)) || (startClip2 == null && endClip2 != null)) {
                    numChanges++;
                }
                if (numChanges > 0) {
                    Rect startClip3 = startClip2;
                    Rect endClip3 = endClip2;
                    if (!this.mResizeClip) {
                        view = view3;
                        ViewUtils.setLeftTopRightBottom(view, startLeft2, startTop2, startRight, startBottom);
                        if (numChanges != 2) {
                            int i2 = endHeight;
                            endWidth = endWidth2;
                            startHeight = startHeight2;
                            View view4 = view;
                            int i3 = startWidth;
                            int i4 = numChanges;
                            if (startLeft2 != endLeft2) {
                                view = view4;
                            } else if (startTop2 != endTop) {
                                view = view4;
                            } else {
                                view = view4;
                                anim = ObjectAnimatorUtils.ofPointF(view, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) endRight, (float) endBottom));
                                int endHeight2 = i2;
                                int startWidth2 = i3;
                            }
                            anim = ObjectAnimatorUtils.ofPointF(view, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft2, (float) startTop2, (float) endLeft2, (float) endTop));
                            int endHeight22 = i2;
                            int startWidth22 = i3;
                        } else if (startWidth == endWidth2 && startHeight2 == endHeight) {
                            int i5 = numChanges;
                            int endHeight3 = endHeight;
                            startHeight = startHeight2;
                            endWidth = endWidth2;
                            anim = ObjectAnimatorUtils.ofPointF(view, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft2, (float) startTop2, (float) endLeft2, (float) endTop));
                            int i6 = endLeft2;
                            int i7 = startRight;
                            int i8 = startTop2;
                            int i9 = endTop;
                            int i10 = endRight;
                            int i11 = startBottom;
                            int i12 = startWidth;
                            int i13 = endHeight3;
                        } else {
                            int endHeight4 = endHeight;
                            endWidth = endWidth2;
                            startHeight = startHeight2;
                            int i14 = numChanges;
                            final ViewBounds viewBounds = new ViewBounds(view);
                            Path topLeftPath = getPathMotion().getPath((float) startLeft2, (float) startTop2, (float) endLeft2, (float) endTop);
                            ObjectAnimator topLeftAnimator = ObjectAnimatorUtils.ofPointF(viewBounds, TOP_LEFT_PROPERTY, topLeftPath);
                            Path path = topLeftPath;
                            View view5 = view;
                            ObjectAnimator bottomRightAnimator = ObjectAnimatorUtils.ofPointF(viewBounds, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) endRight, (float) endBottom));
                            AnimatorSet set = new AnimatorSet();
                            set.playTogether(new Animator[]{topLeftAnimator, bottomRightAnimator});
                            set.addListener(new AnimatorListenerAdapter() {
                                private ViewBounds mViewBounds = viewBounds;
                            });
                            int i15 = endLeft2;
                            int i16 = startRight;
                            int i17 = startTop2;
                            int i18 = endTop;
                            int i19 = endRight;
                            int i20 = startBottom;
                            anim = set;
                            int i21 = endHeight4;
                            int i22 = startWidth;
                            view = view5;
                        }
                        z = true;
                        int i23 = endWidth;
                        int endWidth3 = i23;
                    } else {
                        int i24 = endHeight;
                        int i25 = endWidth2;
                        int i26 = startHeight2;
                        int i27 = numChanges;
                        view = view3;
                        int startWidth3 = startWidth;
                        int maxWidth = Math.max(startWidth3, endWidth2);
                        int startRight2 = startRight;
                        int i28 = startBottom;
                        ViewUtils.setLeftTopRightBottom(view, startLeft2, startTop2, startLeft2 + maxWidth, startTop2 + Math.max(startHeight2, endHeight));
                        if (startLeft2 == endLeft2 && startTop2 == endTop) {
                            endLeft = endLeft2;
                            positionAnimator = null;
                            startTop = startTop2;
                            startLeft = startLeft2;
                        } else {
                            startLeft = startLeft2;
                            startTop = startTop2;
                            endLeft = endLeft2;
                            positionAnimator = ObjectAnimatorUtils.ofPointF(view, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft2, (float) startTop2, (float) endLeft2, (float) endTop));
                        }
                        int i29 = startTop;
                        final Rect finalClip = endClip3;
                        if (startClip3 == null) {
                            i = 0;
                            startClip = new Rect(0, 0, startWidth3, startHeight2);
                        } else {
                            i = 0;
                            startClip = startClip3;
                        }
                        if (endClip3 == null) {
                            endClip = new Rect(i, i, endWidth2, endHeight);
                        } else {
                            endClip = endClip3;
                        }
                        if (!startClip.equals(endClip)) {
                            ViewCompat.setClipBounds(view, startClip);
                            int i30 = endHeight;
                            int i31 = startWidth3;
                            AnonymousClass8 r10 = r0;
                            Rect rect3 = endClip;
                            int i32 = startRight2;
                            final View view6 = view;
                            Rect rect4 = startClip;
                            int i33 = startLeft;
                            final int i34 = endLeft;
                            int i35 = maxWidth;
                            int i36 = endWidth2;
                            clipAnimator = ObjectAnimator.ofObject(view, "clipBounds", sRectEvaluator, new Object[]{startClip, endClip});
                            final int i37 = endTop;
                            int i38 = startHeight2;
                            final int startHeight3 = endRight;
                            int i39 = endRight;
                            z = true;
                            int i40 = endTop;
                            final int endTop2 = endBottom;
                            AnonymousClass8 r0 = new AnimatorListenerAdapter() {
                                private boolean mIsCanceled;

                                public void onAnimationCancel(Animator animation) {
                                    this.mIsCanceled = true;
                                }

                                public void onAnimationEnd(Animator animation) {
                                    if (!this.mIsCanceled) {
                                        ViewCompat.setClipBounds(view6, finalClip);
                                        ViewUtils.setLeftTopRightBottom(view6, i34, i37, startHeight3, endTop2);
                                    }
                                }
                            };
                            clipAnimator.addListener(r10);
                        } else {
                            Rect rect5 = endClip;
                            Rect rect6 = startClip;
                            int i41 = endWidth2;
                            int i42 = startHeight2;
                            int i43 = endTop;
                            int i44 = startWidth3;
                            int i45 = endRight;
                            int i46 = maxWidth;
                            int i47 = startRight2;
                            int i48 = startLeft;
                            int i49 = endLeft;
                            z = true;
                            clipAnimator = null;
                        }
                        anim = TransitionUtils.mergeAnimators(positionAnimator, clipAnimator);
                    }
                    if (view.getParent() instanceof ViewGroup) {
                        final ViewGroup parent = (ViewGroup) view.getParent();
                        ViewGroupUtils.suppressLayout(parent, z);
                        addListener(new TransitionListenerAdapter() {
                            boolean mCanceled = false;

                            public void onTransitionCancel(@NonNull Transition transition) {
                                ViewGroupUtils.suppressLayout(parent, false);
                                this.mCanceled = true;
                            }

                            public void onTransitionEnd(@NonNull Transition transition) {
                                if (!this.mCanceled) {
                                    ViewGroupUtils.suppressLayout(parent, false);
                                }
                                transition.removeListener(this);
                            }

                            public void onTransitionPause(@NonNull Transition transition) {
                                ViewGroupUtils.suppressLayout(parent, false);
                            }

                            public void onTransitionResume(@NonNull Transition transition) {
                                ViewGroupUtils.suppressLayout(parent, true);
                            }
                        });
                    }
                    return anim;
                }
                TransitionValues transitionValues6 = startValues;
                TransitionValues transitionValues7 = endValues;
                return null;
            }
            Map<String, Object> map7 = endParentVals;
            ViewGroup viewGroup10 = startParent;
            ViewGroup viewGroup11 = endParent;
            View view7 = view2;
            TransitionValues transitionValues8 = startValues;
            int startX = ((Integer) transitionValues8.values.get(PROPNAME_WINDOW_X)).intValue();
            int startY = ((Integer) transitionValues8.values.get(PROPNAME_WINDOW_Y)).intValue();
            TransitionValues transitionValues9 = endValues;
            int endX = ((Integer) transitionValues9.values.get(PROPNAME_WINDOW_X)).intValue();
            int endY = ((Integer) transitionValues9.values.get(PROPNAME_WINDOW_Y)).intValue();
            if (startX == endX && startY == endY) {
                return null;
            }
            sceneRoot.getLocationInWindow(this.mTempLocation);
            Bitmap bitmap = Bitmap.createBitmap(view7.getWidth(), view7.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view7.draw(canvas);
            final BitmapDrawable drawable = new BitmapDrawable(bitmap);
            float transitionAlpha = ViewUtils.getTransitionAlpha(view7);
            ViewUtils.setTransitionAlpha(view7, 0.0f);
            ViewUtils.getOverlay(sceneRoot).add(drawable);
            final ViewGroup viewGroup12 = sceneRoot;
            BitmapDrawable bitmapDrawable = drawable;
            AnonymousClass10 r6 = r0;
            Canvas canvas2 = canvas;
            final View view8 = view7;
            int i50 = startX;
            Bitmap bitmap2 = bitmap;
            ObjectAnimator anim2 = ObjectAnimator.ofPropertyValuesHolder(drawable, new PropertyValuesHolder[]{PropertyValuesHolderUtils.ofPointF(DRAWABLE_ORIGIN_PROPERTY, getPathMotion().getPath((float) (startX - this.mTempLocation[0]), (float) (startY - this.mTempLocation[1]), (float) (endX - this.mTempLocation[0]), (float) (endY - this.mTempLocation[1])))});
            final float f = transitionAlpha;
            AnonymousClass10 r02 = new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    ViewUtils.getOverlay(viewGroup12).remove(drawable);
                    ViewUtils.setTransitionAlpha(view8, f);
                }
            };
            anim2.addListener(r6);
            return anim2;
        }
    }

    private static class ViewBounds {
        private int mBottom;
        private int mBottomRightCalls;
        private int mLeft;
        private int mRight;
        private int mTop;
        private int mTopLeftCalls;
        private View mView;

        ViewBounds(View view) {
            this.mView = view;
        }

        /* access modifiers changed from: package-private */
        public void setTopLeft(PointF topLeft) {
            this.mLeft = Math.round(topLeft.x);
            this.mTop = Math.round(topLeft.y);
            this.mTopLeftCalls++;
            if (this.mTopLeftCalls == this.mBottomRightCalls) {
                setLeftTopRightBottom();
            }
        }

        /* access modifiers changed from: package-private */
        public void setBottomRight(PointF bottomRight) {
            this.mRight = Math.round(bottomRight.x);
            this.mBottom = Math.round(bottomRight.y);
            this.mBottomRightCalls++;
            if (this.mTopLeftCalls == this.mBottomRightCalls) {
                setLeftTopRightBottom();
            }
        }

        private void setLeftTopRightBottom() {
            ViewUtils.setLeftTopRightBottom(this.mView, this.mLeft, this.mTop, this.mRight, this.mBottom);
            this.mTopLeftCalls = 0;
            this.mBottomRightCalls = 0;
        }
    }
}
