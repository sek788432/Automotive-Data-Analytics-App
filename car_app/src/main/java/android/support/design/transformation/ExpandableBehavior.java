package android.support.design.transformation;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.design.expandable.ExpandableWidget;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public abstract class ExpandableBehavior extends CoordinatorLayout.Behavior<View> {
    private static final int STATE_COLLAPSED = 2;
    private static final int STATE_EXPANDED = 1;
    private static final int STATE_UNINITIALIZED = 0;
    /* access modifiers changed from: private */
    public int currentState = 0;

    public abstract boolean layoutDependsOn(CoordinatorLayout coordinatorLayout, View view, View view2);

    /* access modifiers changed from: protected */
    public abstract boolean onExpandedStateChange(View view, View view2, boolean z, boolean z2);

    public ExpandableBehavior() {
    }

    public ExpandableBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = findExpandableWidget(r5, r6);
     */
    @android.support.annotation.CallSuper
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onLayoutChild(android.support.design.widget.CoordinatorLayout r5, final android.view.View r6, int r7) {
        /*
            r4 = this;
            boolean r0 = android.support.v4.view.ViewCompat.isLaidOut(r6)
            if (r0 != 0) goto L_0x0030
            android.support.design.expandable.ExpandableWidget r0 = r4.findExpandableWidget(r5, r6)
            if (r0 == 0) goto L_0x0030
            boolean r1 = r0.isExpanded()
            boolean r1 = r4.didStateChange(r1)
            if (r1 == 0) goto L_0x0030
            boolean r1 = r0.isExpanded()
            if (r1 == 0) goto L_0x001e
            r1 = 1
            goto L_0x001f
        L_0x001e:
            r1 = 2
        L_0x001f:
            r4.currentState = r1
            int r1 = r4.currentState
            android.view.ViewTreeObserver r2 = r6.getViewTreeObserver()
            android.support.design.transformation.ExpandableBehavior$1 r3 = new android.support.design.transformation.ExpandableBehavior$1
            r3.<init>(r6, r1, r0)
            r2.addOnPreDrawListener(r3)
        L_0x0030:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.design.transformation.ExpandableBehavior.onLayoutChild(android.support.design.widget.CoordinatorLayout, android.view.View, int):boolean");
    }

    @CallSuper
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        ExpandableWidget dep = (ExpandableWidget) dependency;
        if (!didStateChange(dep.isExpanded())) {
            return false;
        }
        this.currentState = dep.isExpanded() ? 1 : 2;
        return onExpandedStateChange((View) dep, child, dep.isExpanded(), true);
    }

    /* access modifiers changed from: protected */
    @Nullable
    public ExpandableWidget findExpandableWidget(CoordinatorLayout parent, View child) {
        List<View> dependencies = parent.getDependencies(child);
        int size = dependencies.size();
        for (int i = 0; i < size; i++) {
            View dependency = dependencies.get(i);
            if (layoutDependsOn(parent, child, dependency)) {
                return (ExpandableWidget) dependency;
            }
        }
        return null;
    }

    private boolean didStateChange(boolean expanded) {
        if (expanded) {
            if (this.currentState == 0 || this.currentState == 2) {
                return true;
            }
            return false;
        } else if (this.currentState == 1) {
            return true;
        } else {
            return false;
        }
    }

    public static <T extends ExpandableBehavior> T from(View view, Class<T> klass) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.Behavior<?> behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
            if (behavior instanceof ExpandableBehavior) {
                return (ExpandableBehavior) klass.cast(behavior);
            }
            throw new IllegalArgumentException("The view is not associated with ExpandableBehavior");
        }
        throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
    }
}
