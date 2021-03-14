package android.support.design.internal;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.Dimension;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.design.R;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.util.Pools;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class BottomNavigationMenuView extends ViewGroup implements MenuView {
    private static final long ACTIVE_ANIMATION_DURATION_MS = 115;
    private static final int[] CHECKED_STATE_SET = {16842912};
    private static final int[] DISABLED_STATE_SET = {-16842910};
    private final int activeItemMaxWidth;
    private final int activeItemMinWidth;
    private BottomNavigationItemView[] buttons;
    private final int inactiveItemMaxWidth;
    private final int inactiveItemMinWidth;
    private Drawable itemBackground;
    private int itemBackgroundRes;
    private final int itemHeight;
    private boolean itemHorizontalTranslationEnabled;
    @Dimension
    private int itemIconSize;
    private ColorStateList itemIconTint;
    private final Pools.Pool<BottomNavigationItemView> itemPool;
    @StyleRes
    private int itemTextAppearanceActive;
    @StyleRes
    private int itemTextAppearanceInactive;
    private final ColorStateList itemTextColorDefault;
    private ColorStateList itemTextColorFromUser;
    private int labelVisibilityMode;
    /* access modifiers changed from: private */
    public MenuBuilder menu;
    private final View.OnClickListener onClickListener;
    /* access modifiers changed from: private */
    public BottomNavigationPresenter presenter;
    private int selectedItemId;
    private int selectedItemPosition;
    private final TransitionSet set;
    private int[] tempChildWidths;

    public BottomNavigationMenuView(Context context) {
        this(context, (AttributeSet) null);
    }

    public BottomNavigationMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.itemPool = new Pools.SynchronizedPool(5);
        this.selectedItemId = 0;
        this.selectedItemPosition = 0;
        Resources res = getResources();
        this.inactiveItemMaxWidth = res.getDimensionPixelSize(R.dimen.design_bottom_navigation_item_max_width);
        this.inactiveItemMinWidth = res.getDimensionPixelSize(R.dimen.design_bottom_navigation_item_min_width);
        this.activeItemMaxWidth = res.getDimensionPixelSize(R.dimen.design_bottom_navigation_active_item_max_width);
        this.activeItemMinWidth = res.getDimensionPixelSize(R.dimen.design_bottom_navigation_active_item_min_width);
        this.itemHeight = res.getDimensionPixelSize(R.dimen.design_bottom_navigation_height);
        this.itemTextColorDefault = createDefaultColorStateList(16842808);
        this.set = new AutoTransition();
        this.set.setOrdering(0);
        this.set.setDuration((long) ACTIVE_ANIMATION_DURATION_MS);
        this.set.setInterpolator((TimeInterpolator) new FastOutSlowInInterpolator());
        this.set.addTransition(new TextScale());
        this.onClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                MenuItem item = ((BottomNavigationItemView) v).getItemData();
                if (!BottomNavigationMenuView.this.menu.performItemAction(item, BottomNavigationMenuView.this.presenter, 0)) {
                    item.setChecked(true);
                }
            }
        };
        this.tempChildWidths = new int[5];
    }

    public void initialize(MenuBuilder menu2) {
        this.menu = menu2;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int visibleCount = this.menu.getVisibleItems().size();
        int totalCount = getChildCount();
        int heightSpec = View.MeasureSpec.makeMeasureSpec(this.itemHeight, 1073741824);
        int i = 8;
        if (isShifting(this.labelVisibilityMode, visibleCount) && this.itemHorizontalTranslationEnabled) {
            View activeChild = getChildAt(this.selectedItemPosition);
            int activeItemWidth = this.activeItemMinWidth;
            if (activeChild.getVisibility() != 8) {
                activeChild.measure(View.MeasureSpec.makeMeasureSpec(this.activeItemMaxWidth, Integer.MIN_VALUE), heightSpec);
                activeItemWidth = Math.max(activeItemWidth, activeChild.getMeasuredWidth());
            }
            int inactiveCount = visibleCount - (activeChild.getVisibility() != 8 ? 1 : 0);
            int activeWidth = Math.min(width - (this.inactiveItemMinWidth * inactiveCount), Math.min(activeItemWidth, this.activeItemMaxWidth));
            int inactiveWidth = Math.min((width - activeWidth) / (inactiveCount == 0 ? 1 : inactiveCount), this.inactiveItemMaxWidth);
            int extra = (width - activeWidth) - (inactiveWidth * inactiveCount);
            int extra2 = 0;
            while (true) {
                int i2 = extra2;
                if (i2 >= totalCount) {
                    break;
                }
                if (getChildAt(i2).getVisibility() != i) {
                    this.tempChildWidths[i2] = i2 == this.selectedItemPosition ? activeWidth : inactiveWidth;
                    if (extra > 0) {
                        int[] iArr = this.tempChildWidths;
                        iArr[i2] = iArr[i2] + 1;
                        extra--;
                    }
                } else {
                    this.tempChildWidths[i2] = 0;
                }
                extra2 = i2 + 1;
                i = 8;
            }
        } else {
            int childWidth = Math.min(width / (visibleCount == 0 ? 1 : visibleCount), this.activeItemMaxWidth);
            int extra3 = width - (childWidth * visibleCount);
            for (int i3 = 0; i3 < totalCount; i3++) {
                if (getChildAt(i3).getVisibility() != 8) {
                    this.tempChildWidths[i3] = childWidth;
                    if (extra3 > 0) {
                        int[] iArr2 = this.tempChildWidths;
                        iArr2[i3] = iArr2[i3] + 1;
                        extra3--;
                    }
                } else {
                    this.tempChildWidths[i3] = 0;
                }
            }
        }
        int totalWidth = 0;
        for (int i4 = 0; i4 < totalCount; i4++) {
            View child = getChildAt(i4);
            if (child.getVisibility() != 8) {
                child.measure(View.MeasureSpec.makeMeasureSpec(this.tempChildWidths[i4], 1073741824), heightSpec);
                child.getLayoutParams().width = child.getMeasuredWidth();
                totalWidth += child.getMeasuredWidth();
            }
        }
        setMeasuredDimension(View.resolveSizeAndState(totalWidth, View.MeasureSpec.makeMeasureSpec(totalWidth, 1073741824), 0), View.resolveSizeAndState(this.itemHeight, heightSpec, 0));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int count = getChildCount();
        int width = right - left;
        int height = bottom - top;
        int used = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                if (ViewCompat.getLayoutDirection(this) == 1) {
                    child.layout((width - used) - child.getMeasuredWidth(), 0, width - used, height);
                } else {
                    child.layout(used, 0, child.getMeasuredWidth() + used, height);
                }
                used += child.getMeasuredWidth();
            }
        }
    }

    public int getWindowAnimations() {
        return 0;
    }

    public void setIconTintList(ColorStateList tint) {
        this.itemIconTint = tint;
        if (this.buttons != null) {
            for (BottomNavigationItemView item : this.buttons) {
                item.setIconTintList(tint);
            }
        }
    }

    @Nullable
    public ColorStateList getIconTintList() {
        return this.itemIconTint;
    }

    public void setItemIconSize(@Dimension int iconSize) {
        this.itemIconSize = iconSize;
        if (this.buttons != null) {
            for (BottomNavigationItemView item : this.buttons) {
                item.setIconSize(iconSize);
            }
        }
    }

    @Dimension
    public int getItemIconSize() {
        return this.itemIconSize;
    }

    public void setItemTextColor(ColorStateList color) {
        this.itemTextColorFromUser = color;
        if (this.buttons != null) {
            for (BottomNavigationItemView item : this.buttons) {
                item.setTextColor(color);
            }
        }
    }

    public ColorStateList getItemTextColor() {
        return this.itemTextColorFromUser;
    }

    public void setItemTextAppearanceInactive(@StyleRes int textAppearanceRes) {
        this.itemTextAppearanceInactive = textAppearanceRes;
        if (this.buttons != null) {
            for (BottomNavigationItemView item : this.buttons) {
                item.setTextAppearanceInactive(textAppearanceRes);
                if (this.itemTextColorFromUser != null) {
                    item.setTextColor(this.itemTextColorFromUser);
                }
            }
        }
    }

    @StyleRes
    public int getItemTextAppearanceInactive() {
        return this.itemTextAppearanceInactive;
    }

    public void setItemTextAppearanceActive(@StyleRes int textAppearanceRes) {
        this.itemTextAppearanceActive = textAppearanceRes;
        if (this.buttons != null) {
            for (BottomNavigationItemView item : this.buttons) {
                item.setTextAppearanceActive(textAppearanceRes);
                if (this.itemTextColorFromUser != null) {
                    item.setTextColor(this.itemTextColorFromUser);
                }
            }
        }
    }

    @StyleRes
    public int getItemTextAppearanceActive() {
        return this.itemTextAppearanceActive;
    }

    public void setItemBackgroundRes(int background) {
        this.itemBackgroundRes = background;
        if (this.buttons != null) {
            for (BottomNavigationItemView item : this.buttons) {
                item.setItemBackground(background);
            }
        }
    }

    @Deprecated
    public int getItemBackgroundRes() {
        return this.itemBackgroundRes;
    }

    public void setItemBackground(@Nullable Drawable background) {
        this.itemBackground = background;
        if (this.buttons != null) {
            for (BottomNavigationItemView item : this.buttons) {
                item.setItemBackground(background);
            }
        }
    }

    @Nullable
    public Drawable getItemBackground() {
        if (this.buttons == null || this.buttons.length <= 0) {
            return this.itemBackground;
        }
        return this.buttons[0].getBackground();
    }

    public void setLabelVisibilityMode(int labelVisibilityMode2) {
        this.labelVisibilityMode = labelVisibilityMode2;
    }

    public int getLabelVisibilityMode() {
        return this.labelVisibilityMode;
    }

    public void setItemHorizontalTranslationEnabled(boolean itemHorizontalTranslationEnabled2) {
        this.itemHorizontalTranslationEnabled = itemHorizontalTranslationEnabled2;
    }

    public boolean isItemHorizontalTranslationEnabled() {
        return this.itemHorizontalTranslationEnabled;
    }

    public ColorStateList createDefaultColorStateList(int baseColorThemeAttr) {
        TypedValue value = new TypedValue();
        if (!getContext().getTheme().resolveAttribute(baseColorThemeAttr, value, true)) {
            return null;
        }
        ColorStateList baseColor = AppCompatResources.getColorStateList(getContext(), value.resourceId);
        if (!getContext().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.colorPrimary, value, true)) {
            return null;
        }
        int colorPrimary = value.data;
        int defaultColor = baseColor.getDefaultColor();
        return new ColorStateList(new int[][]{DISABLED_STATE_SET, CHECKED_STATE_SET, EMPTY_STATE_SET}, new int[]{baseColor.getColorForState(DISABLED_STATE_SET, defaultColor), colorPrimary, defaultColor});
    }

    public void setPresenter(BottomNavigationPresenter presenter2) {
        this.presenter = presenter2;
    }

    public void buildMenuView() {
        removeAllViews();
        if (this.buttons != null) {
            for (BottomNavigationItemView item : this.buttons) {
                if (item != null) {
                    this.itemPool.release(item);
                }
            }
        }
        if (this.menu.size() == 0) {
            this.selectedItemId = 0;
            this.selectedItemPosition = 0;
            this.buttons = null;
            return;
        }
        this.buttons = new BottomNavigationItemView[this.menu.size()];
        boolean shifting = isShifting(this.labelVisibilityMode, this.menu.getVisibleItems().size());
        for (int i = 0; i < this.menu.size(); i++) {
            this.presenter.setUpdateSuspended(true);
            this.menu.getItem(i).setCheckable(true);
            this.presenter.setUpdateSuspended(false);
            BottomNavigationItemView child = getNewItem();
            this.buttons[i] = child;
            child.setIconTintList(this.itemIconTint);
            child.setIconSize(this.itemIconSize);
            child.setTextColor(this.itemTextColorDefault);
            child.setTextAppearanceInactive(this.itemTextAppearanceInactive);
            child.setTextAppearanceActive(this.itemTextAppearanceActive);
            child.setTextColor(this.itemTextColorFromUser);
            if (this.itemBackground != null) {
                child.setItemBackground(this.itemBackground);
            } else {
                child.setItemBackground(this.itemBackgroundRes);
            }
            child.setShifting(shifting);
            child.setLabelVisibilityMode(this.labelVisibilityMode);
            child.initialize((MenuItemImpl) this.menu.getItem(i), 0);
            child.setItemPosition(i);
            child.setOnClickListener(this.onClickListener);
            addView(child);
        }
        this.selectedItemPosition = Math.min(this.menu.size() - 1, this.selectedItemPosition);
        this.menu.getItem(this.selectedItemPosition).setChecked(true);
    }

    public void updateMenuView() {
        if (this.menu != null && this.buttons != null) {
            int menuSize = this.menu.size();
            if (menuSize != this.buttons.length) {
                buildMenuView();
                return;
            }
            int previousSelectedId = this.selectedItemId;
            for (int i = 0; i < menuSize; i++) {
                MenuItem item = this.menu.getItem(i);
                if (item.isChecked()) {
                    this.selectedItemId = item.getItemId();
                    this.selectedItemPosition = i;
                }
            }
            if (previousSelectedId != this.selectedItemId) {
                TransitionManager.beginDelayedTransition(this, this.set);
            }
            boolean shifting = isShifting(this.labelVisibilityMode, this.menu.getVisibleItems().size());
            for (int i2 = 0; i2 < menuSize; i2++) {
                this.presenter.setUpdateSuspended(true);
                this.buttons[i2].setLabelVisibilityMode(this.labelVisibilityMode);
                this.buttons[i2].setShifting(shifting);
                this.buttons[i2].initialize((MenuItemImpl) this.menu.getItem(i2), 0);
                this.presenter.setUpdateSuspended(false);
            }
        }
    }

    private BottomNavigationItemView getNewItem() {
        BottomNavigationItemView item = this.itemPool.acquire();
        if (item == null) {
            return new BottomNavigationItemView(getContext());
        }
        return item;
    }

    public int getSelectedItemId() {
        return this.selectedItemId;
    }

    private boolean isShifting(int labelVisibilityMode2, int childCount) {
        if (labelVisibilityMode2 == -1) {
            if (childCount <= 3) {
                return false;
            }
        } else if (labelVisibilityMode2 != 0) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void tryRestoreSelectedItemId(int itemId) {
        int size = this.menu.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = this.menu.getItem(i);
            if (itemId == item.getItemId()) {
                this.selectedItemId = itemId;
                this.selectedItemPosition = i;
                item.setChecked(true);
                return;
            }
        }
    }
}
