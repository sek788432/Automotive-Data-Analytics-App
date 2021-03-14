package com.github.mikephil.charting.components;

import android.graphics.DashPathEffect;
import android.graphics.Paint;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class Legend extends ComponentBase {
    private List<Boolean> mCalculatedLabelBreakPoints;
    private List<FSize> mCalculatedLabelSizes;
    private List<FSize> mCalculatedLineSizes;
    private LegendDirection mDirection;
    private boolean mDrawInside;
    private LegendEntry[] mEntries;
    private LegendEntry[] mExtraEntries;
    private DashPathEffect mFormLineDashEffect;
    private float mFormLineWidth;
    private float mFormSize;
    private float mFormToTextSpace;
    private LegendHorizontalAlignment mHorizontalAlignment;
    private boolean mIsLegendCustom;
    private float mMaxSizePercent;
    public float mNeededHeight;
    public float mNeededWidth;
    private LegendOrientation mOrientation;
    private LegendForm mShape;
    private float mStackSpace;
    public float mTextHeightMax;
    public float mTextWidthMax;
    private LegendVerticalAlignment mVerticalAlignment;
    private boolean mWordWrapEnabled;
    private float mXEntrySpace;
    private float mYEntrySpace;

    public enum LegendDirection {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT
    }

    public enum LegendForm {
        NONE,
        EMPTY,
        DEFAULT,
        SQUARE,
        CIRCLE,
        LINE
    }

    public enum LegendHorizontalAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum LegendOrientation {
        HORIZONTAL,
        VERTICAL
    }

    @Deprecated
    public enum LegendPosition {
        RIGHT_OF_CHART,
        RIGHT_OF_CHART_CENTER,
        RIGHT_OF_CHART_INSIDE,
        LEFT_OF_CHART,
        LEFT_OF_CHART_CENTER,
        LEFT_OF_CHART_INSIDE,
        BELOW_CHART_LEFT,
        BELOW_CHART_RIGHT,
        BELOW_CHART_CENTER,
        ABOVE_CHART_LEFT,
        ABOVE_CHART_RIGHT,
        ABOVE_CHART_CENTER,
        PIECHART_CENTER
    }

    public enum LegendVerticalAlignment {
        TOP,
        CENTER,
        BOTTOM
    }

    public Legend() {
        this.mEntries = new LegendEntry[0];
        this.mIsLegendCustom = false;
        this.mHorizontalAlignment = LegendHorizontalAlignment.LEFT;
        this.mVerticalAlignment = LegendVerticalAlignment.BOTTOM;
        this.mOrientation = LegendOrientation.HORIZONTAL;
        this.mDrawInside = false;
        this.mDirection = LegendDirection.LEFT_TO_RIGHT;
        this.mShape = LegendForm.SQUARE;
        this.mFormSize = 8.0f;
        this.mFormLineWidth = 3.0f;
        this.mFormLineDashEffect = null;
        this.mXEntrySpace = 6.0f;
        this.mYEntrySpace = 0.0f;
        this.mFormToTextSpace = 5.0f;
        this.mStackSpace = 3.0f;
        this.mMaxSizePercent = 0.95f;
        this.mNeededWidth = 0.0f;
        this.mNeededHeight = 0.0f;
        this.mTextHeightMax = 0.0f;
        this.mTextWidthMax = 0.0f;
        this.mWordWrapEnabled = false;
        this.mCalculatedLabelSizes = new ArrayList(16);
        this.mCalculatedLabelBreakPoints = new ArrayList(16);
        this.mCalculatedLineSizes = new ArrayList(16);
        this.mTextSize = Utils.convertDpToPixel(10.0f);
        this.mXOffset = Utils.convertDpToPixel(5.0f);
        this.mYOffset = Utils.convertDpToPixel(3.0f);
    }

    public Legend(LegendEntry[] entries) {
        this();
        if (entries != null) {
            this.mEntries = entries;
            return;
        }
        throw new IllegalArgumentException("entries array is NULL");
    }

    @Deprecated
    public Legend(int[] colors, String[] labels) {
        this();
        if (colors == null || labels == null) {
            throw new IllegalArgumentException("colors array or labels array is NULL");
        } else if (colors.length == labels.length) {
            List<LegendEntry> entries = new ArrayList<>();
            for (int i = 0; i < Math.min(colors.length, labels.length); i++) {
                LegendEntry entry = new LegendEntry();
                entry.formColor = colors[i];
                entry.label = labels[i];
                if (entry.formColor == 1122868) {
                    entry.form = LegendForm.NONE;
                } else if (entry.formColor == 1122867 || entry.formColor == 0) {
                    entry.form = LegendForm.EMPTY;
                }
                entries.add(entry);
            }
            this.mEntries = (LegendEntry[]) entries.toArray(new LegendEntry[entries.size()]);
        } else {
            throw new IllegalArgumentException("colors array and labels array need to be of same size");
        }
    }

    @Deprecated
    public Legend(List<Integer> colors, List<String> labels) {
        this(Utils.convertIntegers(colors), Utils.convertStrings(labels));
    }

    public void setEntries(List<LegendEntry> entries) {
        this.mEntries = (LegendEntry[]) entries.toArray(new LegendEntry[entries.size()]);
    }

    public LegendEntry[] getEntries() {
        return this.mEntries;
    }

    public float getMaximumEntryWidth(Paint p) {
        float max = 0.0f;
        float maxFormSize = 0.0f;
        float formToTextSpace = Utils.convertDpToPixel(this.mFormToTextSpace);
        for (LegendEntry entry : this.mEntries) {
            float formSize = Utils.convertDpToPixel(Float.isNaN(entry.formSize) ? this.mFormSize : entry.formSize);
            if (formSize > maxFormSize) {
                maxFormSize = formSize;
            }
            String label = entry.label;
            if (label != null) {
                float length = (float) Utils.calcTextWidth(p, label);
                if (length > max) {
                    max = length;
                }
            }
        }
        return max + maxFormSize + formToTextSpace;
    }

    public float getMaximumEntryHeight(Paint p) {
        float max = 0.0f;
        for (LegendEntry entry : this.mEntries) {
            String label = entry.label;
            if (label != null) {
                float length = (float) Utils.calcTextHeight(p, label);
                if (length > max) {
                    max = length;
                }
            }
        }
        return max;
    }

    @Deprecated
    public int[] getColors() {
        int[] old = new int[this.mEntries.length];
        for (int i = 0; i < this.mEntries.length; i++) {
            old[i] = this.mEntries[i].form == LegendForm.NONE ? ColorTemplate.COLOR_SKIP : this.mEntries[i].form == LegendForm.EMPTY ? ColorTemplate.COLOR_NONE : this.mEntries[i].formColor;
        }
        return old;
    }

    @Deprecated
    public String[] getLabels() {
        String[] old = new String[this.mEntries.length];
        for (int i = 0; i < this.mEntries.length; i++) {
            old[i] = this.mEntries[i].label;
        }
        return old;
    }

    @Deprecated
    public int[] getExtraColors() {
        int[] old = new int[this.mExtraEntries.length];
        for (int i = 0; i < this.mExtraEntries.length; i++) {
            old[i] = this.mExtraEntries[i].form == LegendForm.NONE ? ColorTemplate.COLOR_SKIP : this.mExtraEntries[i].form == LegendForm.EMPTY ? ColorTemplate.COLOR_NONE : this.mExtraEntries[i].formColor;
        }
        return old;
    }

    @Deprecated
    public String[] getExtraLabels() {
        String[] old = new String[this.mExtraEntries.length];
        for (int i = 0; i < this.mExtraEntries.length; i++) {
            old[i] = this.mExtraEntries[i].label;
        }
        return old;
    }

    public LegendEntry[] getExtraEntries() {
        return this.mExtraEntries;
    }

    public void setExtra(List<LegendEntry> entries) {
        this.mExtraEntries = (LegendEntry[]) entries.toArray(new LegendEntry[entries.size()]);
    }

    public void setExtra(LegendEntry[] entries) {
        if (entries == null) {
            entries = new LegendEntry[0];
        }
        this.mExtraEntries = entries;
    }

    @Deprecated
    public void setExtra(List<Integer> colors, List<String> labels) {
        setExtra(Utils.convertIntegers(colors), Utils.convertStrings(labels));
    }

    public void setExtra(int[] colors, String[] labels) {
        List<LegendEntry> entries = new ArrayList<>();
        for (int i = 0; i < Math.min(colors.length, labels.length); i++) {
            LegendEntry entry = new LegendEntry();
            entry.formColor = colors[i];
            entry.label = labels[i];
            if (entry.formColor == 1122868 || entry.formColor == 0) {
                entry.form = LegendForm.NONE;
            } else if (entry.formColor == 1122867) {
                entry.form = LegendForm.EMPTY;
            }
            entries.add(entry);
        }
        this.mExtraEntries = (LegendEntry[]) entries.toArray(new LegendEntry[entries.size()]);
    }

    public void setCustom(LegendEntry[] entries) {
        this.mEntries = entries;
        this.mIsLegendCustom = true;
    }

    public void setCustom(List<LegendEntry> entries) {
        this.mEntries = (LegendEntry[]) entries.toArray(new LegendEntry[entries.size()]);
        this.mIsLegendCustom = true;
    }

    public void resetCustom() {
        this.mIsLegendCustom = false;
    }

    public boolean isLegendCustom() {
        return this.mIsLegendCustom;
    }

    @Deprecated
    public LegendPosition getPosition() {
        if (this.mOrientation == LegendOrientation.VERTICAL && this.mHorizontalAlignment == LegendHorizontalAlignment.CENTER && this.mVerticalAlignment == LegendVerticalAlignment.CENTER) {
            return LegendPosition.PIECHART_CENTER;
        }
        if (this.mOrientation == LegendOrientation.HORIZONTAL) {
            if (this.mVerticalAlignment == LegendVerticalAlignment.TOP) {
                if (this.mHorizontalAlignment == LegendHorizontalAlignment.LEFT) {
                    return LegendPosition.ABOVE_CHART_LEFT;
                }
                return this.mHorizontalAlignment == LegendHorizontalAlignment.RIGHT ? LegendPosition.ABOVE_CHART_RIGHT : LegendPosition.ABOVE_CHART_CENTER;
            } else if (this.mHorizontalAlignment == LegendHorizontalAlignment.LEFT) {
                return LegendPosition.BELOW_CHART_LEFT;
            } else {
                return this.mHorizontalAlignment == LegendHorizontalAlignment.RIGHT ? LegendPosition.BELOW_CHART_RIGHT : LegendPosition.BELOW_CHART_CENTER;
            }
        } else if (this.mHorizontalAlignment == LegendHorizontalAlignment.LEFT) {
            if (this.mVerticalAlignment != LegendVerticalAlignment.TOP || !this.mDrawInside) {
                return this.mVerticalAlignment == LegendVerticalAlignment.CENTER ? LegendPosition.LEFT_OF_CHART_CENTER : LegendPosition.LEFT_OF_CHART;
            }
            return LegendPosition.LEFT_OF_CHART_INSIDE;
        } else if (this.mVerticalAlignment != LegendVerticalAlignment.TOP || !this.mDrawInside) {
            return this.mVerticalAlignment == LegendVerticalAlignment.CENTER ? LegendPosition.RIGHT_OF_CHART_CENTER : LegendPosition.RIGHT_OF_CHART;
        } else {
            return LegendPosition.RIGHT_OF_CHART_INSIDE;
        }
    }

    @Deprecated
    public void setPosition(LegendPosition newValue) {
        switch (newValue) {
            case LEFT_OF_CHART:
            case LEFT_OF_CHART_INSIDE:
            case LEFT_OF_CHART_CENTER:
                this.mHorizontalAlignment = LegendHorizontalAlignment.LEFT;
                this.mVerticalAlignment = newValue == LegendPosition.LEFT_OF_CHART_CENTER ? LegendVerticalAlignment.CENTER : LegendVerticalAlignment.TOP;
                this.mOrientation = LegendOrientation.VERTICAL;
                break;
            case RIGHT_OF_CHART:
            case RIGHT_OF_CHART_INSIDE:
            case RIGHT_OF_CHART_CENTER:
                this.mHorizontalAlignment = LegendHorizontalAlignment.RIGHT;
                this.mVerticalAlignment = newValue == LegendPosition.RIGHT_OF_CHART_CENTER ? LegendVerticalAlignment.CENTER : LegendVerticalAlignment.TOP;
                this.mOrientation = LegendOrientation.VERTICAL;
                break;
            case ABOVE_CHART_LEFT:
            case ABOVE_CHART_CENTER:
            case ABOVE_CHART_RIGHT:
                this.mHorizontalAlignment = newValue == LegendPosition.ABOVE_CHART_LEFT ? LegendHorizontalAlignment.LEFT : newValue == LegendPosition.ABOVE_CHART_RIGHT ? LegendHorizontalAlignment.RIGHT : LegendHorizontalAlignment.CENTER;
                this.mVerticalAlignment = LegendVerticalAlignment.TOP;
                this.mOrientation = LegendOrientation.HORIZONTAL;
                break;
            case BELOW_CHART_LEFT:
            case BELOW_CHART_CENTER:
            case BELOW_CHART_RIGHT:
                this.mHorizontalAlignment = newValue == LegendPosition.BELOW_CHART_LEFT ? LegendHorizontalAlignment.LEFT : newValue == LegendPosition.BELOW_CHART_RIGHT ? LegendHorizontalAlignment.RIGHT : LegendHorizontalAlignment.CENTER;
                this.mVerticalAlignment = LegendVerticalAlignment.BOTTOM;
                this.mOrientation = LegendOrientation.HORIZONTAL;
                break;
            case PIECHART_CENTER:
                this.mHorizontalAlignment = LegendHorizontalAlignment.CENTER;
                this.mVerticalAlignment = LegendVerticalAlignment.CENTER;
                this.mOrientation = LegendOrientation.VERTICAL;
                break;
        }
        this.mDrawInside = newValue == LegendPosition.LEFT_OF_CHART_INSIDE || newValue == LegendPosition.RIGHT_OF_CHART_INSIDE;
    }

    public LegendHorizontalAlignment getHorizontalAlignment() {
        return this.mHorizontalAlignment;
    }

    public void setHorizontalAlignment(LegendHorizontalAlignment value) {
        this.mHorizontalAlignment = value;
    }

    public LegendVerticalAlignment getVerticalAlignment() {
        return this.mVerticalAlignment;
    }

    public void setVerticalAlignment(LegendVerticalAlignment value) {
        this.mVerticalAlignment = value;
    }

    public LegendOrientation getOrientation() {
        return this.mOrientation;
    }

    public void setOrientation(LegendOrientation value) {
        this.mOrientation = value;
    }

    public boolean isDrawInsideEnabled() {
        return this.mDrawInside;
    }

    public void setDrawInside(boolean value) {
        this.mDrawInside = value;
    }

    public LegendDirection getDirection() {
        return this.mDirection;
    }

    public void setDirection(LegendDirection pos) {
        this.mDirection = pos;
    }

    public LegendForm getForm() {
        return this.mShape;
    }

    public void setForm(LegendForm shape) {
        this.mShape = shape;
    }

    public void setFormSize(float size) {
        this.mFormSize = size;
    }

    public float getFormSize() {
        return this.mFormSize;
    }

    public void setFormLineWidth(float size) {
        this.mFormLineWidth = size;
    }

    public float getFormLineWidth() {
        return this.mFormLineWidth;
    }

    public void setFormLineDashEffect(DashPathEffect dashPathEffect) {
        this.mFormLineDashEffect = dashPathEffect;
    }

    public DashPathEffect getFormLineDashEffect() {
        return this.mFormLineDashEffect;
    }

    public float getXEntrySpace() {
        return this.mXEntrySpace;
    }

    public void setXEntrySpace(float space) {
        this.mXEntrySpace = space;
    }

    public float getYEntrySpace() {
        return this.mYEntrySpace;
    }

    public void setYEntrySpace(float space) {
        this.mYEntrySpace = space;
    }

    public float getFormToTextSpace() {
        return this.mFormToTextSpace;
    }

    public void setFormToTextSpace(float space) {
        this.mFormToTextSpace = space;
    }

    public float getStackSpace() {
        return this.mStackSpace;
    }

    public void setStackSpace(float space) {
        this.mStackSpace = space;
    }

    public void setWordWrapEnabled(boolean enabled) {
        this.mWordWrapEnabled = enabled;
    }

    public boolean isWordWrapEnabled() {
        return this.mWordWrapEnabled;
    }

    public float getMaxSizePercent() {
        return this.mMaxSizePercent;
    }

    public void setMaxSizePercent(float maxSize) {
        this.mMaxSizePercent = maxSize;
    }

    public List<FSize> getCalculatedLabelSizes() {
        return this.mCalculatedLabelSizes;
    }

    public List<Boolean> getCalculatedLabelBreakPoints() {
        return this.mCalculatedLabelBreakPoints;
    }

    public List<FSize> getCalculatedLineSizes() {
        return this.mCalculatedLineSizes;
    }

    /* JADX WARNING: Removed duplicated region for block: B:53:0x0154  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0165  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void calculateDimensions(android.graphics.Paint r33, com.github.mikephil.charting.utils.ViewPortHandler r34) {
        /*
            r32 = this;
            r0 = r32
            r1 = r33
            float r2 = r0.mFormSize
            float r2 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r2)
            float r3 = r0.mStackSpace
            float r3 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r3)
            float r4 = r0.mFormToTextSpace
            float r4 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r4)
            float r5 = r0.mXEntrySpace
            float r5 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r5)
            float r6 = r0.mYEntrySpace
            float r6 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r6)
            boolean r7 = r0.mWordWrapEnabled
            com.github.mikephil.charting.components.LegendEntry[] r8 = r0.mEntries
            int r9 = r8.length
            float r10 = r32.getMaximumEntryWidth(r33)
            r0.mTextWidthMax = r10
            float r10 = r32.getMaximumEntryHeight(r33)
            r0.mTextHeightMax = r10
            int[] r10 = com.github.mikephil.charting.components.Legend.AnonymousClass1.$SwitchMap$com$github$mikephil$charting$components$Legend$LegendOrientation
            com.github.mikephil.charting.components.Legend$LegendOrientation r11 = r0.mOrientation
            int r11 = r11.ordinal()
            r10 = r10[r11]
            switch(r10) {
                case 1: goto L_0x01b8;
                case 2: goto L_0x004e;
                default: goto L_0x0040;
            }
        L_0x0040:
            r22 = r2
            r27 = r3
            r23 = r5
            r24 = r6
            r31 = r7
            r26 = r8
            goto L_0x0237
        L_0x004e:
            float r10 = com.github.mikephil.charting.utils.Utils.getLineHeight(r33)
            float r13 = com.github.mikephil.charting.utils.Utils.getLineSpacing(r33)
            float r13 = r13 + r6
            float r14 = r34.contentWidth()
            float r15 = r0.mMaxSizePercent
            float r14 = r14 * r15
            r15 = 0
            r16 = 0
            r17 = 0
            r18 = -1
            java.util.List<java.lang.Boolean> r12 = r0.mCalculatedLabelBreakPoints
            r12.clear()
            java.util.List<com.github.mikephil.charting.utils.FSize> r12 = r0.mCalculatedLabelSizes
            r12.clear()
            java.util.List<com.github.mikephil.charting.utils.FSize> r12 = r0.mCalculatedLineSizes
            r12.clear()
            r19 = r16
            r20 = r18
            r12 = 0
        L_0x0079:
            if (r12 >= r9) goto L_0x0180
            r11 = r8[r12]
            r22 = r2
            com.github.mikephil.charting.components.Legend$LegendForm r2 = r11.form
            r23 = r5
            com.github.mikephil.charting.components.Legend$LegendForm r5 = com.github.mikephil.charting.components.Legend.LegendForm.NONE
            if (r2 == r5) goto L_0x0089
            r2 = 1
            goto L_0x008a
        L_0x0089:
            r2 = 0
        L_0x008a:
            float r5 = r11.formSize
            boolean r5 = java.lang.Float.isNaN(r5)
            if (r5 == 0) goto L_0x0095
            r5 = r22
            goto L_0x009b
        L_0x0095:
            float r5 = r11.formSize
            float r5 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r5)
        L_0x009b:
            r24 = r6
            java.lang.String r6 = r11.label
            r25 = r11
            java.util.List<java.lang.Boolean> r11 = r0.mCalculatedLabelBreakPoints
            r26 = r8
            r16 = 0
            java.lang.Boolean r8 = java.lang.Boolean.valueOf(r16)
            r11.add(r8)
            r8 = -1
            r11 = r20
            if (r11 != r8) goto L_0x00b6
            r17 = 0
            goto L_0x00b8
        L_0x00b6:
            float r17 = r17 + r3
        L_0x00b8:
            if (r6 == 0) goto L_0x00dc
            java.util.List<com.github.mikephil.charting.utils.FSize> r8 = r0.mCalculatedLabelSizes
            r27 = r3
            com.github.mikephil.charting.utils.FSize r3 = com.github.mikephil.charting.utils.Utils.calcTextSize(r1, r6)
            r8.add(r3)
            if (r2 == 0) goto L_0x00ca
            float r8 = r4 + r5
            goto L_0x00cb
        L_0x00ca:
            r8 = 0
        L_0x00cb:
            float r17 = r17 + r8
            java.util.List<com.github.mikephil.charting.utils.FSize> r3 = r0.mCalculatedLabelSizes
            java.lang.Object r3 = r3.get(r12)
            com.github.mikephil.charting.utils.FSize r3 = (com.github.mikephil.charting.utils.FSize) r3
            float r3 = r3.width
            float r17 = r17 + r3
            r28 = r5
            goto L_0x00f9
        L_0x00dc:
            r27 = r3
            java.util.List<com.github.mikephil.charting.utils.FSize> r3 = r0.mCalculatedLabelSizes
            r28 = r5
            r8 = 0
            com.github.mikephil.charting.utils.FSize r5 = com.github.mikephil.charting.utils.FSize.getInstance(r8, r8)
            r3.add(r5)
            if (r2 == 0) goto L_0x00ef
            r3 = r28
            goto L_0x00f0
        L_0x00ef:
            r3 = 0
        L_0x00f0:
            float r17 = r17 + r3
            r3 = -1
            if (r11 != r3) goto L_0x00f9
            r20 = r12
            r11 = r20
        L_0x00f9:
            if (r6 != 0) goto L_0x0106
            int r3 = r9 + -1
            if (r12 != r3) goto L_0x0100
            goto L_0x0106
        L_0x0100:
            r29 = r2
            r31 = r7
            goto L_0x0167
        L_0x0106:
            r3 = r19
            r8 = 0
            int r5 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r5 != 0) goto L_0x010f
            r5 = r8
            goto L_0x0111
        L_0x010f:
            r5 = r23
        L_0x0111:
            if (r7 == 0) goto L_0x0149
            int r8 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r8 == 0) goto L_0x0149
            float r8 = r14 - r3
            float r18 = r5 + r17
            int r8 = (r8 > r18 ? 1 : (r8 == r18 ? 0 : -1))
            if (r8 < 0) goto L_0x0124
            r29 = r2
            r31 = r7
            goto L_0x014d
        L_0x0124:
            java.util.List<com.github.mikephil.charting.utils.FSize> r8 = r0.mCalculatedLineSizes
            r29 = r2
            com.github.mikephil.charting.utils.FSize r2 = com.github.mikephil.charting.utils.FSize.getInstance(r3, r10)
            r8.add(r2)
            float r15 = java.lang.Math.max(r15, r3)
            java.util.List<java.lang.Boolean> r2 = r0.mCalculatedLabelBreakPoints
            r8 = -1
            if (r11 <= r8) goto L_0x013a
            r8 = r11
            goto L_0x013b
        L_0x013a:
            r8 = r12
        L_0x013b:
            r31 = r7
            r18 = 1
            java.lang.Boolean r7 = java.lang.Boolean.valueOf(r18)
            r2.set(r8, r7)
            r2 = r17
            goto L_0x0150
        L_0x0149:
            r29 = r2
            r31 = r7
        L_0x014d:
            float r2 = r5 + r17
            float r2 = r2 + r3
        L_0x0150:
            int r3 = r9 + -1
            if (r12 != r3) goto L_0x0165
            java.util.List<com.github.mikephil.charting.utils.FSize> r3 = r0.mCalculatedLineSizes
            com.github.mikephil.charting.utils.FSize r7 = com.github.mikephil.charting.utils.FSize.getInstance(r2, r10)
            r3.add(r7)
            float r3 = java.lang.Math.max(r15, r2)
            r19 = r2
            r15 = r3
            goto L_0x0167
        L_0x0165:
            r19 = r2
        L_0x0167:
            if (r6 == 0) goto L_0x016c
            r30 = -1
            goto L_0x016e
        L_0x016c:
            r30 = r11
        L_0x016e:
            r20 = r30
            int r12 = r12 + 1
            r2 = r22
            r5 = r23
            r6 = r24
            r8 = r26
            r3 = r27
            r7 = r31
            goto L_0x0079
        L_0x0180:
            r22 = r2
            r27 = r3
            r23 = r5
            r24 = r6
            r31 = r7
            r26 = r8
            r3 = r19
            r11 = r20
            r16 = 0
            r0.mNeededWidth = r15
            java.util.List<com.github.mikephil.charting.utils.FSize> r2 = r0.mCalculatedLineSizes
            int r2 = r2.size()
            float r2 = (float) r2
            float r2 = r2 * r10
            java.util.List<com.github.mikephil.charting.utils.FSize> r5 = r0.mCalculatedLineSizes
            int r5 = r5.size()
            if (r5 != 0) goto L_0x01a7
            r5 = r16
            goto L_0x01b1
        L_0x01a7:
            java.util.List<com.github.mikephil.charting.utils.FSize> r5 = r0.mCalculatedLineSizes
            int r5 = r5.size()
            r21 = 1
            int r5 = r5 + -1
        L_0x01b1:
            float r5 = (float) r5
            float r5 = r5 * r13
            float r2 = r2 + r5
            r0.mNeededHeight = r2
            goto L_0x0237
        L_0x01b8:
            r22 = r2
            r27 = r3
            r23 = r5
            r24 = r6
            r31 = r7
            r26 = r8
            r16 = 0
            r21 = 1
            r2 = 0
            r3 = 0
            r5 = 0
            float r6 = com.github.mikephil.charting.utils.Utils.getLineHeight(r33)
            r7 = 0
            r8 = r7
            r7 = r5
            r5 = r3
            r3 = r2
            r2 = r16
        L_0x01d6:
            if (r2 >= r9) goto L_0x0232
            r10 = r26[r2]
            com.github.mikephil.charting.components.Legend$LegendForm r11 = r10.form
            com.github.mikephil.charting.components.Legend$LegendForm r12 = com.github.mikephil.charting.components.Legend.LegendForm.NONE
            if (r11 == r12) goto L_0x01e3
            r11 = r21
            goto L_0x01e5
        L_0x01e3:
            r11 = r16
        L_0x01e5:
            float r12 = r10.formSize
            boolean r12 = java.lang.Float.isNaN(r12)
            if (r12 == 0) goto L_0x01f0
            r12 = r22
            goto L_0x01f6
        L_0x01f0:
            float r12 = r10.formSize
            float r12 = com.github.mikephil.charting.utils.Utils.convertDpToPixel(r12)
        L_0x01f6:
            java.lang.String r13 = r10.label
            if (r8 != 0) goto L_0x01fb
            r7 = 0
        L_0x01fb:
            if (r11 == 0) goto L_0x0202
            if (r8 == 0) goto L_0x0201
            float r7 = r7 + r27
        L_0x0201:
            float r7 = r7 + r12
        L_0x0202:
            if (r13 == 0) goto L_0x0223
            if (r11 == 0) goto L_0x020a
            if (r8 != 0) goto L_0x020a
            float r7 = r7 + r4
            goto L_0x0215
        L_0x020a:
            if (r8 == 0) goto L_0x0215
            float r3 = java.lang.Math.max(r3, r7)
            float r14 = r6 + r24
            float r5 = r5 + r14
            r7 = 0
            r8 = 0
        L_0x0215:
            int r14 = com.github.mikephil.charting.utils.Utils.calcTextWidth(r1, r13)
            float r14 = (float) r14
            float r7 = r7 + r14
            int r14 = r9 + -1
            if (r2 >= r14) goto L_0x022b
            float r14 = r6 + r24
            float r5 = r5 + r14
            goto L_0x022b
        L_0x0223:
            r8 = 1
            float r7 = r7 + r12
            int r14 = r9 + -1
            if (r2 >= r14) goto L_0x022b
            float r7 = r7 + r27
        L_0x022b:
            float r3 = java.lang.Math.max(r3, r7)
            int r2 = r2 + 1
            goto L_0x01d6
        L_0x0232:
            r0.mNeededWidth = r3
            r0.mNeededHeight = r5
        L_0x0237:
            float r2 = r0.mNeededHeight
            float r3 = r0.mYOffset
            float r2 = r2 + r3
            r0.mNeededHeight = r2
            float r2 = r0.mNeededWidth
            float r3 = r0.mXOffset
            float r2 = r2 + r3
            r0.mNeededWidth = r2
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.github.mikephil.charting.components.Legend.calculateDimensions(android.graphics.Paint, com.github.mikephil.charting.utils.ViewPortHandler):void");
    }
}
