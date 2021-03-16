package android.support.transition;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

class ViewOverlayApi14 implements ViewOverlayImpl {
    protected OverlayViewGroup mOverlayViewGroup;

    ViewOverlayApi14(Context context, ViewGroup hostView, View requestingView) {
        this.mOverlayViewGroup = new OverlayViewGroup(context, hostView, requestingView, this);
    }

    /* JADX WARNING: type inference failed for: r1v4, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static android.view.ViewGroup getContentView(android.view.View r3) {
        /*
            r0 = r3
        L_0x0001:
            if (r0 == 0) goto L_0x0024
            int r1 = r0.getId()
            r2 = 16908290(0x1020002, float:2.3877235E-38)
            if (r1 != r2) goto L_0x0014
            boolean r1 = r0 instanceof android.view.ViewGroup
            if (r1 == 0) goto L_0x0014
            r1 = r0
            android.view.ViewGroup r1 = (android.view.ViewGroup) r1
            return r1
        L_0x0014:
            android.view.ViewParent r1 = r0.getParent()
            boolean r1 = r1 instanceof android.view.ViewGroup
            if (r1 == 0) goto L_0x0001
            android.view.ViewParent r1 = r0.getParent()
            r0 = r1
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            goto L_0x0001
        L_0x0024:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.transition.ViewOverlayApi14.getContentView(android.view.View):android.view.ViewGroup");
    }

    static ViewOverlayApi14 createFrom(View view) {
        ViewGroup contentView = getContentView(view);
        if (contentView == null) {
            return null;
        }
        int numChildren = contentView.getChildCount();
        for (int i = 0; i < numChildren; i++) {
            View child = contentView.getChildAt(i);
            if (child instanceof OverlayViewGroup) {
                return ((OverlayViewGroup) child).mViewOverlay;
            }
        }
        return new ViewGroupOverlayApi14(contentView.getContext(), contentView, view);
    }

    /* access modifiers changed from: package-private */
    public ViewGroup getOverlayView() {
        return this.mOverlayViewGroup;
    }

    public void add(@NonNull Drawable drawable) {
        this.mOverlayViewGroup.add(drawable);
    }

    public void clear() {
        this.mOverlayViewGroup.clear();
    }

    public void remove(@NonNull Drawable drawable) {
        this.mOverlayViewGroup.remove(drawable);
    }

    /* access modifiers changed from: package-private */
    public boolean isEmpty() {
        return this.mOverlayViewGroup.isEmpty();
    }

    static class OverlayViewGroup extends ViewGroup {
        static Method sInvalidateChildInParentFastMethod;
        ArrayList<Drawable> mDrawables = null;
        ViewGroup mHostView;
        View mRequestingView;
        ViewOverlayApi14 mViewOverlay;

        static {
            Class<ViewGroup> cls = ViewGroup.class;
            try {
                sInvalidateChildInParentFastMethod = cls.getDeclaredMethod("invalidateChildInParentFast", new Class[]{Integer.TYPE, Integer.TYPE, Rect.class});
            } catch (NoSuchMethodException e2) {
            }
        }

        OverlayViewGroup(Context context, ViewGroup hostView, View requestingView, ViewOverlayApi14 viewOverlay) {
            super(context);
            this.mHostView = hostView;
            this.mRequestingView = requestingView;
            setRight(hostView.getWidth());
            setBottom(hostView.getHeight());
            hostView.addView(this);
            this.mViewOverlay = viewOverlay;
        }

        public boolean dispatchTouchEvent(MotionEvent ev) {
            return false;
        }

        public void add(Drawable drawable) {
            if (this.mDrawables == null) {
                this.mDrawables = new ArrayList<>();
            }
            if (!this.mDrawables.contains(drawable)) {
                this.mDrawables.add(drawable);
                invalidate(drawable.getBounds());
                drawable.setCallback(this);
            }
        }

        public void remove(Drawable drawable) {
            if (this.mDrawables != null) {
                this.mDrawables.remove(drawable);
                invalidate(drawable.getBounds());
                drawable.setCallback((Drawable.Callback) null);
            }
        }

        /* access modifiers changed from: protected */
        public boolean verifyDrawable(@NonNull Drawable who) {
            return super.verifyDrawable(who) || (this.mDrawables != null && this.mDrawables.contains(who));
        }

        public void add(View child) {
            if (child.getParent() instanceof ViewGroup) {
                ViewGroup parent = (ViewGroup) child.getParent();
                if (!(parent == this.mHostView || parent.getParent() == null || !ViewCompat.isAttachedToWindow(parent))) {
                    int[] parentLocation = new int[2];
                    int[] hostViewLocation = new int[2];
                    parent.getLocationOnScreen(parentLocation);
                    this.mHostView.getLocationOnScreen(hostViewLocation);
                    ViewCompat.offsetLeftAndRight(child, parentLocation[0] - hostViewLocation[0]);
                    ViewCompat.offsetTopAndBottom(child, parentLocation[1] - hostViewLocation[1]);
                }
                parent.removeView(child);
                if (child.getParent() != null) {
                    parent.removeView(child);
                }
            }
            super.addView(child, getChildCount() - 1);
        }

        public void remove(View view) {
            super.removeView(view);
            if (isEmpty()) {
                this.mHostView.removeView(this);
            }
        }

        public void clear() {
            removeAllViews();
            if (this.mDrawables != null) {
                this.mDrawables.clear();
            }
        }

        /* access modifiers changed from: package-private */
        public boolean isEmpty() {
            return getChildCount() == 0 && (this.mDrawables == null || this.mDrawables.size() == 0);
        }

        public void invalidateDrawable(@NonNull Drawable drawable) {
            invalidate(drawable.getBounds());
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            int[] contentViewLocation = new int[2];
            int[] hostViewLocation = new int[2];
            this.mHostView.getLocationOnScreen(contentViewLocation);
            this.mRequestingView.getLocationOnScreen(hostViewLocation);
            canvas.translate((float) (hostViewLocation[0] - contentViewLocation[0]), (float) (hostViewLocation[1] - contentViewLocation[1]));
            canvas.clipRect(new Rect(0, 0, this.mRequestingView.getWidth(), this.mRequestingView.getHeight()));
            super.dispatchDraw(canvas);
            int numDrawables = this.mDrawables == null ? 0 : this.mDrawables.size();
            for (int i = 0; i < numDrawables; i++) {
                this.mDrawables.get(i).draw(canvas);
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
        }

        private void getOffset(int[] offset) {
            int[] contentViewLocation = new int[2];
            int[] hostViewLocation = new int[2];
            this.mHostView.getLocationOnScreen(contentViewLocation);
            this.mRequestingView.getLocationOnScreen(hostViewLocation);
            offset[0] = hostViewLocation[0] - contentViewLocation[0];
            offset[1] = hostViewLocation[1] - contentViewLocation[1];
        }

        public void invalidateChildFast(View child, Rect dirty) {
            if (this.mHostView != null) {
                int left = child.getLeft();
                int top = child.getTop();
                int[] offset = new int[2];
                getOffset(offset);
                dirty.offset(offset[0] + left, offset[1] + top);
                this.mHostView.invalidate(dirty);
            }
        }

        /* access modifiers changed from: protected */
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public ViewParent invalidateChildInParentFast(int left, int top, Rect dirty) {
            if (!(this.mHostView instanceof ViewGroup) || sInvalidateChildInParentFastMethod == null) {
                return null;
            }
            try {
                getOffset(new int[2]);
                sInvalidateChildInParentFastMethod.invoke(this.mHostView, new Object[]{Integer.valueOf(left), Integer.valueOf(top), dirty});
                return null;
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
                return null;
            } catch (InvocationTargetException e3) {
                e3.printStackTrace();
                return null;
            }
        }

        public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
            if (this.mHostView == null) {
                return null;
            }
            dirty.offset(location[0], location[1]);
            if (this.mHostView instanceof ViewGroup) {
                location[0] = 0;
                location[1] = 0;
                int[] offset = new int[2];
                getOffset(offset);
                dirty.offset(offset[0], offset[1]);
                return super.invalidateChildInParent(location, dirty);
            }
            invalidate(dirty);
            return null;
        }

        static class TouchInterceptor extends View {
            TouchInterceptor(Context context) {
                super(context);
            }
        }
    }

    private ViewOverlayApi14() {
    }
}
