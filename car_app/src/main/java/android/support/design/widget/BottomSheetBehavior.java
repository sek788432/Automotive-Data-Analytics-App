package android.support.design.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.design.R;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.math.MathUtils;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class BottomSheetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
    private static final float HIDE_FRICTION = 0.1f;
    private static final float HIDE_THRESHOLD = 0.5f;
    public static final int PEEK_HEIGHT_AUTO = -1;
    public static final int STATE_COLLAPSED = 4;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_EXPANDED = 3;
    public static final int STATE_HALF_EXPANDED = 6;
    public static final int STATE_HIDDEN = 5;
    public static final int STATE_SETTLING = 2;
    int activePointerId;
    private BottomSheetCallback callback;
    int collapsedOffset;
    private final ViewDragHelper.Callback dragCallback = new ViewDragHelper.Callback() {
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            View scroll;
            if (BottomSheetBehavior.this.state == 1 || BottomSheetBehavior.this.touchingScrollingChild) {
                return false;
            }
            if (BottomSheetBehavior.this.state == 3 && BottomSheetBehavior.this.activePointerId == pointerId && (scroll = (View) BottomSheetBehavior.this.nestedScrollingChildRef.get()) != null && scroll.canScrollVertically(-1)) {
                return false;
            }
            if (BottomSheetBehavior.this.viewRef == null || BottomSheetBehavior.this.viewRef.get() != child) {
                return false;
            }
            return true;
        }

        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            BottomSheetBehavior.this.dispatchOnSlide(top);
        }

        public void onViewDragStateChanged(int state) {
            if (state == 1) {
                BottomSheetBehavior.this.setStateInternal(1);
            }
        }

        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            int top;
            int top2;
            int top3;
            int targetState = 4;
            if (yvel < 0.0f) {
                if (BottomSheetBehavior.this.fitToContents) {
                    top = BottomSheetBehavior.this.fitToContentsOffset;
                    targetState = 3;
                } else {
                    if (releasedChild.getTop() > BottomSheetBehavior.this.halfExpandedOffset) {
                        top3 = BottomSheetBehavior.this.halfExpandedOffset;
                        targetState = 6;
                    } else {
                        top3 = 0;
                        targetState = 3;
                    }
                    top = top3;
                }
            } else if (BottomSheetBehavior.this.hideable && BottomSheetBehavior.this.shouldHide(releasedChild, yvel) && (releasedChild.getTop() > BottomSheetBehavior.this.collapsedOffset || Math.abs(xvel) < Math.abs(yvel))) {
                top = BottomSheetBehavior.this.parentHeight;
                targetState = 5;
            } else if (yvel == 0.0f || Math.abs(xvel) > Math.abs(yvel)) {
                int currentTop = releasedChild.getTop();
                if (BottomSheetBehavior.this.fitToContents) {
                    if (Math.abs(currentTop - BottomSheetBehavior.this.fitToContentsOffset) < Math.abs(currentTop - BottomSheetBehavior.this.collapsedOffset)) {
                        top2 = BottomSheetBehavior.this.fitToContentsOffset;
                        targetState = 3;
                    } else {
                        top2 = BottomSheetBehavior.this.collapsedOffset;
                        targetState = 4;
                    }
                } else if (currentTop < BottomSheetBehavior.this.halfExpandedOffset) {
                    if (currentTop < Math.abs(currentTop - BottomSheetBehavior.this.collapsedOffset)) {
                        top2 = 0;
                        targetState = 3;
                    } else {
                        top2 = BottomSheetBehavior.this.halfExpandedOffset;
                        targetState = 6;
                    }
                } else if (Math.abs(currentTop - BottomSheetBehavior.this.halfExpandedOffset) < Math.abs(currentTop - BottomSheetBehavior.this.collapsedOffset)) {
                    top2 = BottomSheetBehavior.this.halfExpandedOffset;
                    targetState = 6;
                } else {
                    top2 = BottomSheetBehavior.this.collapsedOffset;
                }
                top = top2;
            } else {
                top = BottomSheetBehavior.this.collapsedOffset;
            }
            int targetState2 = targetState;
            if (BottomSheetBehavior.this.viewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top)) {
                BottomSheetBehavior.this.setStateInternal(2);
                ViewCompat.postOnAnimation(releasedChild, new SettleRunnable(releasedChild, targetState2));
                return;
            }
            BottomSheetBehavior.this.setStateInternal(targetState2);
        }

        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return MathUtils.clamp(top, BottomSheetBehavior.this.getExpandedOffset(), BottomSheetBehavior.this.hideable ? BottomSheetBehavior.this.parentHeight : BottomSheetBehavior.this.collapsedOffset);
        }

        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            return child.getLeft();
        }

        public int getViewVerticalDragRange(@NonNull View child) {
            if (BottomSheetBehavior.this.hideable) {
                return BottomSheetBehavior.this.parentHeight;
            }
            return BottomSheetBehavior.this.collapsedOffset;
        }
    };
    /* access modifiers changed from: private */
    public boolean fitToContents = true;
    int fitToContentsOffset;
    int halfExpandedOffset;
    boolean hideable;
    private boolean ignoreEvents;
    private Map<View, Integer> importantForAccessibilityMap;
    private int initialY;
    private int lastNestedScrollDy;
    private int lastPeekHeight;
    private float maximumVelocity;
    private boolean nestedScrolled;
    WeakReference<View> nestedScrollingChildRef;
    int parentHeight;
    private int peekHeight;
    private boolean peekHeightAuto;
    private int peekHeightMin;
    private boolean skipCollapsed;
    int state = 4;
    boolean touchingScrollingChild;
    private VelocityTracker velocityTracker;
    ViewDragHelper viewDragHelper;
    WeakReference<V> viewRef;

    public static abstract class BottomSheetCallback {
        public abstract void onSlide(@NonNull View view, float f);

        public abstract void onStateChanged(@NonNull View view, int i);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    public BottomSheetBehavior() {
    }

    public BottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BottomSheetBehavior_Layout);
        TypedValue value = a.peekValue(R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight);
        if (value == null || value.data != -1) {
            setPeekHeight(a.getDimensionPixelSize(R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight, -1));
        } else {
            setPeekHeight(value.data);
        }
        setHideable(a.getBoolean(R.styleable.BottomSheetBehavior_Layout_behavior_hideable, false));
        setFitToContents(a.getBoolean(R.styleable.BottomSheetBehavior_Layout_behavior_fitToContents, true));
        setSkipCollapsed(a.getBoolean(R.styleable.BottomSheetBehavior_Layout_behavior_skipCollapsed, false));
        a.recycle();
        this.maximumVelocity = (float) ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
    }

    public Parcelable onSaveInstanceState(CoordinatorLayout parent, V child) {
        return new SavedState(super.onSaveInstanceState(parent, child), this.state);
    }

    public void onRestoreInstanceState(CoordinatorLayout parent, V child, Parcelable state2) {
        SavedState ss = (SavedState) state2;
        super.onRestoreInstanceState(parent, child, ss.getSuperState());
        if (ss.state == 1 || ss.state == 2) {
            this.state = 4;
        } else {
            this.state = ss.state;
        }
    }

    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        if (ViewCompat.getFitsSystemWindows(parent) && !ViewCompat.getFitsSystemWindows(child)) {
            child.setFitsSystemWindows(true);
        }
        int savedTop = child.getTop();
        parent.onLayoutChild(child, layoutDirection);
        this.parentHeight = parent.getHeight();
        if (this.peekHeightAuto) {
            if (this.peekHeightMin == 0) {
                this.peekHeightMin = parent.getResources().getDimensionPixelSize(R.dimen.design_bottom_sheet_peek_height_min);
            }
            this.lastPeekHeight = Math.max(this.peekHeightMin, this.parentHeight - ((parent.getWidth() * 9) / 16));
        } else {
            this.lastPeekHeight = this.peekHeight;
        }
        this.fitToContentsOffset = Math.max(0, this.parentHeight - child.getHeight());
        this.halfExpandedOffset = this.parentHeight / 2;
        calculateCollapsedOffset();
        if (this.state == 3) {
            ViewCompat.offsetTopAndBottom(child, getExpandedOffset());
        } else if (this.state == 6) {
            ViewCompat.offsetTopAndBottom(child, this.halfExpandedOffset);
        } else if (this.hideable && this.state == 5) {
            ViewCompat.offsetTopAndBottom(child, this.parentHeight);
        } else if (this.state == 4) {
            ViewCompat.offsetTopAndBottom(child, this.collapsedOffset);
        } else if (this.state == 1 || this.state == 2) {
            ViewCompat.offsetTopAndBottom(child, savedTop - child.getTop());
        }
        if (this.viewDragHelper == null) {
            this.viewDragHelper = ViewDragHelper.create(parent, this.dragCallback);
        }
        this.viewRef = new WeakReference<>(child);
        this.nestedScrollingChildRef = new WeakReference<>(findScrollingChild(child));
        return true;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v14, resolved type: android.view.View} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onInterceptTouchEvent(android.support.design.widget.CoordinatorLayout r9, V r10, android.view.MotionEvent r11) {
        /*
            r8 = this;
            boolean r0 = r10.isShown()
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x000b
            r8.ignoreEvents = r2
            return r1
        L_0x000b:
            int r0 = r11.getActionMasked()
            if (r0 != 0) goto L_0x0014
            r8.reset()
        L_0x0014:
            android.view.VelocityTracker r3 = r8.velocityTracker
            if (r3 != 0) goto L_0x001e
            android.view.VelocityTracker r3 = android.view.VelocityTracker.obtain()
            r8.velocityTracker = r3
        L_0x001e:
            android.view.VelocityTracker r3 = r8.velocityTracker
            r3.addMovement(r11)
            r3 = 3
            r4 = 0
            r5 = -1
            if (r0 == r3) goto L_0x006e
            switch(r0) {
                case 0: goto L_0x002c;
                case 1: goto L_0x006e;
                default: goto L_0x002b;
            }
        L_0x002b:
            goto L_0x0079
        L_0x002c:
            float r3 = r11.getX()
            int r3 = (int) r3
            float r6 = r11.getY()
            int r6 = (int) r6
            r8.initialY = r6
            java.lang.ref.WeakReference<android.view.View> r6 = r8.nestedScrollingChildRef
            if (r6 == 0) goto L_0x0045
            java.lang.ref.WeakReference<android.view.View> r6 = r8.nestedScrollingChildRef
            java.lang.Object r6 = r6.get()
            android.view.View r6 = (android.view.View) r6
            goto L_0x0046
        L_0x0045:
            r6 = r4
        L_0x0046:
            if (r6 == 0) goto L_0x005c
            int r7 = r8.initialY
            boolean r7 = r9.isPointInChildBounds(r6, r3, r7)
            if (r7 == 0) goto L_0x005c
            int r7 = r11.getActionIndex()
            int r7 = r11.getPointerId(r7)
            r8.activePointerId = r7
            r8.touchingScrollingChild = r2
        L_0x005c:
            int r7 = r8.activePointerId
            if (r7 != r5) goto L_0x006a
            int r5 = r8.initialY
            boolean r5 = r9.isPointInChildBounds(r10, r3, r5)
            if (r5 != 0) goto L_0x006a
            r5 = r2
            goto L_0x006b
        L_0x006a:
            r5 = r1
        L_0x006b:
            r8.ignoreEvents = r5
            goto L_0x0079
        L_0x006e:
            r8.touchingScrollingChild = r1
            r8.activePointerId = r5
            boolean r3 = r8.ignoreEvents
            if (r3 == 0) goto L_0x0079
            r8.ignoreEvents = r1
            return r1
        L_0x0079:
            boolean r3 = r8.ignoreEvents
            if (r3 != 0) goto L_0x008a
            android.support.v4.widget.ViewDragHelper r3 = r8.viewDragHelper
            if (r3 == 0) goto L_0x008a
            android.support.v4.widget.ViewDragHelper r3 = r8.viewDragHelper
            boolean r3 = r3.shouldInterceptTouchEvent(r11)
            if (r3 == 0) goto L_0x008a
            return r2
        L_0x008a:
            java.lang.ref.WeakReference<android.view.View> r3 = r8.nestedScrollingChildRef
            if (r3 == 0) goto L_0x0098
            java.lang.ref.WeakReference<android.view.View> r3 = r8.nestedScrollingChildRef
            java.lang.Object r3 = r3.get()
            r4 = r3
            android.view.View r4 = (android.view.View) r4
        L_0x0098:
            r3 = r4
            r4 = 2
            if (r0 != r4) goto L_0x00d3
            if (r3 == 0) goto L_0x00d3
            boolean r4 = r8.ignoreEvents
            if (r4 != 0) goto L_0x00d3
            int r4 = r8.state
            if (r4 == r2) goto L_0x00d3
            float r4 = r11.getX()
            int r4 = (int) r4
            float r5 = r11.getY()
            int r5 = (int) r5
            boolean r4 = r9.isPointInChildBounds(r3, r4, r5)
            if (r4 != 0) goto L_0x00d3
            android.support.v4.widget.ViewDragHelper r4 = r8.viewDragHelper
            if (r4 == 0) goto L_0x00d3
            int r4 = r8.initialY
            float r4 = (float) r4
            float r5 = r11.getY()
            float r4 = r4 - r5
            float r4 = java.lang.Math.abs(r4)
            android.support.v4.widget.ViewDragHelper r5 = r8.viewDragHelper
            int r5 = r5.getTouchSlop()
            float r5 = (float) r5
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 <= 0) goto L_0x00d3
            r1 = r2
            goto L_0x00d4
        L_0x00d3:
        L_0x00d4:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.design.widget.BottomSheetBehavior.onInterceptTouchEvent(android.support.design.widget.CoordinatorLayout, android.view.View, android.view.MotionEvent):boolean");
    }

    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!child.isShown()) {
            return false;
        }
        int action = event.getActionMasked();
        if (this.state == 1 && action == 0) {
            return true;
        }
        if (this.viewDragHelper != null) {
            this.viewDragHelper.processTouchEvent(event);
        }
        if (action == 0) {
            reset();
        }
        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
        this.velocityTracker.addMovement(event);
        if (action == 2 && !this.ignoreEvents && Math.abs(((float) this.initialY) - event.getY()) > ((float) this.viewDragHelper.getTouchSlop())) {
            this.viewDragHelper.captureChildView(child, event.getPointerId(event.getActionIndex()));
        }
        return !this.ignoreEvents;
    }

    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V v, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        this.lastNestedScrollDy = 0;
        this.nestedScrolled = false;
        if ((axes & 2) != 0) {
            return true;
        }
        return false;
    }

    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (type != 1 && target == ((View) this.nestedScrollingChildRef.get())) {
            int currentTop = child.getTop();
            int newTop = currentTop - dy;
            if (dy > 0) {
                if (newTop < getExpandedOffset()) {
                    consumed[1] = currentTop - getExpandedOffset();
                    ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                    setStateInternal(3);
                } else {
                    consumed[1] = dy;
                    ViewCompat.offsetTopAndBottom(child, -dy);
                    setStateInternal(1);
                }
            } else if (dy < 0 && !target.canScrollVertically(-1)) {
                if (newTop <= this.collapsedOffset || this.hideable) {
                    consumed[1] = dy;
                    ViewCompat.offsetTopAndBottom(child, -dy);
                    setStateInternal(1);
                } else {
                    consumed[1] = currentTop - this.collapsedOffset;
                    ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                    setStateInternal(4);
                }
            }
            dispatchOnSlide(child.getTop());
            this.lastNestedScrollDy = dy;
            this.nestedScrolled = true;
        }
    }

    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int type) {
        int top;
        int targetState;
        int top2;
        if (child.getTop() == getExpandedOffset()) {
            setStateInternal(3);
        } else if (target == this.nestedScrollingChildRef.get() && this.nestedScrolled) {
            int targetState2 = 4;
            if (this.lastNestedScrollDy > 0) {
                top = getExpandedOffset();
                targetState2 = 3;
            } else if (this.hideable != 0 && shouldHide(child, getYVelocity())) {
                top = this.parentHeight;
                targetState2 = 5;
            } else if (this.lastNestedScrollDy == 0) {
                int currentTop = child.getTop();
                if (this.fitToContents) {
                    if (Math.abs(currentTop - this.fitToContentsOffset) < Math.abs(currentTop - this.collapsedOffset)) {
                        top2 = this.fitToContentsOffset;
                        targetState = 3;
                    } else {
                        top2 = this.collapsedOffset;
                        targetState = 4;
                    }
                } else if (currentTop < this.halfExpandedOffset) {
                    if (currentTop < Math.abs(currentTop - this.collapsedOffset)) {
                        top2 = 0;
                        targetState = 3;
                    } else {
                        top2 = this.halfExpandedOffset;
                        targetState = 6;
                    }
                } else if (Math.abs(currentTop - this.halfExpandedOffset) < Math.abs(currentTop - this.collapsedOffset)) {
                    top2 = this.halfExpandedOffset;
                    targetState = 6;
                } else {
                    top = this.collapsedOffset;
                    targetState = 4;
                    targetState2 = targetState;
                }
                top = top2;
                targetState2 = targetState;
            } else {
                top = this.collapsedOffset;
            }
            if (this.viewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
                setStateInternal(2);
                ViewCompat.postOnAnimation(child, new SettleRunnable(child, targetState2));
            } else {
                setStateInternal(targetState2);
            }
            this.nestedScrolled = false;
        }
    }

    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, float velocityX, float velocityY) {
        return target == this.nestedScrollingChildRef.get() && (this.state != 3 || super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY));
    }

    public boolean isFitToContents() {
        return this.fitToContents;
    }

    public void setFitToContents(boolean fitToContents2) {
        if (this.fitToContents != fitToContents2) {
            this.fitToContents = fitToContents2;
            if (this.viewRef != null) {
                calculateCollapsedOffset();
            }
            setStateInternal((!this.fitToContents || this.state != 6) ? this.state : 3);
        }
    }

    public final void setPeekHeight(int peekHeight2) {
        V view;
        boolean layout = false;
        if (peekHeight2 == -1) {
            if (!this.peekHeightAuto) {
                this.peekHeightAuto = true;
                layout = true;
            }
        } else if (this.peekHeightAuto || this.peekHeight != peekHeight2) {
            this.peekHeightAuto = false;
            this.peekHeight = Math.max(0, peekHeight2);
            this.collapsedOffset = this.parentHeight - peekHeight2;
            layout = true;
        }
        if (layout && this.state == 4 && this.viewRef != null && (view = (View) this.viewRef.get()) != null) {
            view.requestLayout();
        }
    }

    public final int getPeekHeight() {
        if (this.peekHeightAuto) {
            return -1;
        }
        return this.peekHeight;
    }

    public void setHideable(boolean hideable2) {
        this.hideable = hideable2;
    }

    public boolean isHideable() {
        return this.hideable;
    }

    public void setSkipCollapsed(boolean skipCollapsed2) {
        this.skipCollapsed = skipCollapsed2;
    }

    public boolean getSkipCollapsed() {
        return this.skipCollapsed;
    }

    public void setBottomSheetCallback(BottomSheetCallback callback2) {
        this.callback = callback2;
    }

    public final void setState(int state2) {
        if (state2 != this.state) {
            if (this.viewRef != null) {
                final V child = (View) this.viewRef.get();
                if (child != null) {
                    ViewParent parent = child.getParent();
                    if (parent == null || !parent.isLayoutRequested() || !ViewCompat.isAttachedToWindow(child)) {
                        startSettlingAnimation(child, state2);
                        return;
                    }
                    final int finalState = state2;
                    child.post(new Runnable() {
                        public void run() {
                            BottomSheetBehavior.this.startSettlingAnimation(child, finalState);
                        }
                    });
                }
            } else if (state2 == 4 || state2 == 3 || state2 == 6 || (this.hideable && state2 == 5)) {
                this.state = state2;
            }
        }
    }

    public final int getState() {
        return this.state;
    }

    /* access modifiers changed from: package-private */
    public void setStateInternal(int state2) {
        if (this.state != state2) {
            this.state = state2;
            if (state2 == 6 || state2 == 3) {
                updateImportantForAccessibility(true);
            } else if (state2 == 5 || state2 == 4) {
                updateImportantForAccessibility(false);
            }
            View bottomSheet = (View) this.viewRef.get();
            if (bottomSheet != null && this.callback != null) {
                this.callback.onStateChanged(bottomSheet, state2);
            }
        }
    }

    private void calculateCollapsedOffset() {
        if (this.fitToContents) {
            this.collapsedOffset = Math.max(this.parentHeight - this.lastPeekHeight, this.fitToContentsOffset);
        } else {
            this.collapsedOffset = this.parentHeight - this.lastPeekHeight;
        }
    }

    private void reset() {
        this.activePointerId = -1;
        if (this.velocityTracker != null) {
            this.velocityTracker.recycle();
            this.velocityTracker = null;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean shouldHide(View child, float yvel) {
        if (this.skipCollapsed) {
            return true;
        }
        if (child.getTop() < this.collapsedOffset) {
            return false;
        }
        if (Math.abs((((float) child.getTop()) + (HIDE_FRICTION * yvel)) - ((float) this.collapsedOffset)) / ((float) this.peekHeight) > HIDE_THRESHOLD) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public View findScrollingChild(View view) {
        if (ViewCompat.isNestedScrollingEnabled(view)) {
            return view;
        }
        if (!(view instanceof ViewGroup)) {
            return null;
        }
        ViewGroup group = (ViewGroup) view;
        int count = group.getChildCount();
        for (int i = 0; i < count; i++) {
            View scrollingChild = findScrollingChild(group.getChildAt(i));
            if (scrollingChild != null) {
                return scrollingChild;
            }
        }
        return null;
    }

    private float getYVelocity() {
        if (this.velocityTracker == null) {
            return 0.0f;
        }
        this.velocityTracker.computeCurrentVelocity(1000, this.maximumVelocity);
        return this.velocityTracker.getYVelocity(this.activePointerId);
    }

    /* access modifiers changed from: private */
    public int getExpandedOffset() {
        if (this.fitToContents) {
            return this.fitToContentsOffset;
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public void startSettlingAnimation(View child, int state2) {
        int top;
        if (state2 == 4) {
            top = this.collapsedOffset;
        } else if (state2 == 6) {
            top = this.halfExpandedOffset;
            if (this.fitToContents && top <= this.fitToContentsOffset) {
                state2 = 3;
                top = this.fitToContentsOffset;
            }
        } else if (state2 == 3) {
            top = getExpandedOffset();
        } else if (!this.hideable || state2 != 5) {
            throw new IllegalArgumentException("Illegal state argument: " + state2);
        } else {
            top = this.parentHeight;
        }
        if (this.viewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
            setStateInternal(2);
            ViewCompat.postOnAnimation(child, new SettleRunnable(child, state2));
            return;
        }
        setStateInternal(state2);
    }

    /* access modifiers changed from: package-private */
    public void dispatchOnSlide(int top) {
        View bottomSheet = (View) this.viewRef.get();
        if (bottomSheet != null && this.callback != null) {
            if (top > this.collapsedOffset) {
                this.callback.onSlide(bottomSheet, ((float) (this.collapsedOffset - top)) / ((float) (this.parentHeight - this.collapsedOffset)));
            } else {
                this.callback.onSlide(bottomSheet, ((float) (this.collapsedOffset - top)) / ((float) (this.collapsedOffset - getExpandedOffset())));
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int getPeekHeightMin() {
        return this.peekHeightMin;
    }

    private class SettleRunnable implements Runnable {
        private final int targetState;
        private final View view;

        SettleRunnable(View view2, int targetState2) {
            this.view = view2;
            this.targetState = targetState2;
        }

        public void run() {
            if (BottomSheetBehavior.this.viewDragHelper == null || !BottomSheetBehavior.this.viewDragHelper.continueSettling(true)) {
                BottomSheetBehavior.this.setStateInternal(this.targetState);
            } else {
                ViewCompat.postOnAnimation(this.view, this);
            }
        }
    }

    protected static class SavedState extends AbsSavedState {
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
        final int state;

        public SavedState(Parcel source) {
            this(source, (ClassLoader) null);
        }

        public SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            this.state = source.readInt();
        }

        public SavedState(Parcelable superState, int state2) {
            super(superState);
            this.state = state2;
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.state);
        }
    }

    public static <V extends View> BottomSheetBehavior<V> from(V view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
            if (behavior instanceof BottomSheetBehavior) {
                return (BottomSheetBehavior) behavior;
            }
            throw new IllegalArgumentException("The view is not associated with BottomSheetBehavior");
        }
        throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
    }

    private void updateImportantForAccessibility(boolean expanded) {
        if (this.viewRef != null) {
            ViewParent viewParent = ((View) this.viewRef.get()).getParent();
            if (viewParent instanceof CoordinatorLayout) {
                CoordinatorLayout parent = (CoordinatorLayout) viewParent;
                int childCount = parent.getChildCount();
                if (Build.VERSION.SDK_INT >= 16 && expanded) {
                    if (this.importantForAccessibilityMap == null) {
                        this.importantForAccessibilityMap = new HashMap(childCount);
                    } else {
                        return;
                    }
                }
                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);
                    if (child != this.viewRef.get()) {
                        if (expanded) {
                            if (Build.VERSION.SDK_INT >= 16) {
                                this.importantForAccessibilityMap.put(child, Integer.valueOf(child.getImportantForAccessibility()));
                            }
                            ViewCompat.setImportantForAccessibility(child, 4);
                        } else if (this.importantForAccessibilityMap != null && this.importantForAccessibilityMap.containsKey(child)) {
                            ViewCompat.setImportantForAccessibility(child, this.importantForAccessibilityMap.get(child).intValue());
                        }
                    }
                }
                if (!expanded) {
                    this.importantForAccessibilityMap = null;
                }
            }
        }
    }
}
