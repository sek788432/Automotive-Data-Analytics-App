package android.support.design.widget;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

class ViewOffsetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
    private int tempLeftRightOffset = 0;
    private int tempTopBottomOffset = 0;
    private ViewOffsetHelper viewOffsetHelper;

    public ViewOffsetBehavior() {
    }

    public ViewOffsetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        layoutChild(parent, child, layoutDirection);
        if (this.viewOffsetHelper == null) {
            this.viewOffsetHelper = new ViewOffsetHelper(child);
        }
        this.viewOffsetHelper.onViewLayout();
        if (this.tempTopBottomOffset != 0) {
            this.viewOffsetHelper.setTopAndBottomOffset(this.tempTopBottomOffset);
            this.tempTopBottomOffset = 0;
        }
        if (this.tempLeftRightOffset == 0) {
            return true;
        }
        this.viewOffsetHelper.setLeftAndRightOffset(this.tempLeftRightOffset);
        this.tempLeftRightOffset = 0;
        return true;
    }

    /* access modifiers changed from: protected */
    public void layoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        parent.onLayoutChild(child, layoutDirection);
    }

    public boolean setTopAndBottomOffset(int offset) {
        if (this.viewOffsetHelper != null) {
            return this.viewOffsetHelper.setTopAndBottomOffset(offset);
        }
        this.tempTopBottomOffset = offset;
        return false;
    }

    public boolean setLeftAndRightOffset(int offset) {
        if (this.viewOffsetHelper != null) {
            return this.viewOffsetHelper.setLeftAndRightOffset(offset);
        }
        this.tempLeftRightOffset = offset;
        return false;
    }

    public int getTopAndBottomOffset() {
        if (this.viewOffsetHelper != null) {
            return this.viewOffsetHelper.getTopAndBottomOffset();
        }
        return 0;
    }

    public int getLeftAndRightOffset() {
        if (this.viewOffsetHelper != null) {
            return this.viewOffsetHelper.getLeftAndRightOffset();
        }
        return 0;
    }
}
