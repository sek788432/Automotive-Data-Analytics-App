package com.github.mikephil.charting.data;

import android.graphics.Typeface;
import android.util.Log;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import java.util.ArrayList;
import java.util.List;

public abstract class ChartData<T extends IDataSet<? extends Entry>> {
    protected List<T> mDataSets;
    protected float mLeftAxisMax;
    protected float mLeftAxisMin;
    protected float mRightAxisMax;
    protected float mRightAxisMin;
    protected float mXMax;
    protected float mXMin;
    protected float mYMax;
    protected float mYMin;

    public ChartData() {
        this.mYMax = -3.4028235E38f;
        this.mYMin = Float.MAX_VALUE;
        this.mXMax = -3.4028235E38f;
        this.mXMin = Float.MAX_VALUE;
        this.mLeftAxisMax = -3.4028235E38f;
        this.mLeftAxisMin = Float.MAX_VALUE;
        this.mRightAxisMax = -3.4028235E38f;
        this.mRightAxisMin = Float.MAX_VALUE;
        this.mDataSets = new ArrayList();
    }

    public ChartData(T... dataSets) {
        this.mYMax = -3.4028235E38f;
        this.mYMin = Float.MAX_VALUE;
        this.mXMax = -3.4028235E38f;
        this.mXMin = Float.MAX_VALUE;
        this.mLeftAxisMax = -3.4028235E38f;
        this.mLeftAxisMin = Float.MAX_VALUE;
        this.mRightAxisMax = -3.4028235E38f;
        this.mRightAxisMin = Float.MAX_VALUE;
        this.mDataSets = arrayToList(dataSets);
        notifyDataChanged();
    }

    private List<T> arrayToList(T[] array) {
        List<T> list = new ArrayList<>();
        for (T set : array) {
            list.add(set);
        }
        return list;
    }

    public ChartData(List<T> sets) {
        this.mYMax = -3.4028235E38f;
        this.mYMin = Float.MAX_VALUE;
        this.mXMax = -3.4028235E38f;
        this.mXMin = Float.MAX_VALUE;
        this.mLeftAxisMax = -3.4028235E38f;
        this.mLeftAxisMin = Float.MAX_VALUE;
        this.mRightAxisMax = -3.4028235E38f;
        this.mRightAxisMin = Float.MAX_VALUE;
        this.mDataSets = sets;
        notifyDataChanged();
    }

    public void notifyDataChanged() {
        calcMinMax();
    }

    public void calcMinMaxY(float fromX, float toX) {
        for (T set : this.mDataSets) {
            set.calcMinMaxY(fromX, toX);
        }
        calcMinMax();
    }

    /* access modifiers changed from: protected */
    public void calcMinMax() {
        if (this.mDataSets != null) {
            this.mYMax = -3.4028235E38f;
            this.mYMin = Float.MAX_VALUE;
            this.mXMax = -3.4028235E38f;
            this.mXMin = Float.MAX_VALUE;
            for (T set : this.mDataSets) {
                calcMinMax(set);
            }
            this.mLeftAxisMax = -3.4028235E38f;
            this.mLeftAxisMin = Float.MAX_VALUE;
            this.mRightAxisMax = -3.4028235E38f;
            this.mRightAxisMin = Float.MAX_VALUE;
            T firstLeft = getFirstLeft(this.mDataSets);
            if (firstLeft != null) {
                this.mLeftAxisMax = firstLeft.getYMax();
                this.mLeftAxisMin = firstLeft.getYMin();
                for (T dataSet : this.mDataSets) {
                    if (dataSet.getAxisDependency() == YAxis.AxisDependency.LEFT) {
                        if (dataSet.getYMin() < this.mLeftAxisMin) {
                            this.mLeftAxisMin = dataSet.getYMin();
                        }
                        if (dataSet.getYMax() > this.mLeftAxisMax) {
                            this.mLeftAxisMax = dataSet.getYMax();
                        }
                    }
                }
            }
            T firstRight = getFirstRight(this.mDataSets);
            if (firstRight != null) {
                this.mRightAxisMax = firstRight.getYMax();
                this.mRightAxisMin = firstRight.getYMin();
                for (T dataSet2 : this.mDataSets) {
                    if (dataSet2.getAxisDependency() == YAxis.AxisDependency.RIGHT) {
                        if (dataSet2.getYMin() < this.mRightAxisMin) {
                            this.mRightAxisMin = dataSet2.getYMin();
                        }
                        if (dataSet2.getYMax() > this.mRightAxisMax) {
                            this.mRightAxisMax = dataSet2.getYMax();
                        }
                    }
                }
            }
        }
    }

    public int getDataSetCount() {
        if (this.mDataSets == null) {
            return 0;
        }
        return this.mDataSets.size();
    }

    public float getYMin() {
        return this.mYMin;
    }

    public float getYMin(YAxis.AxisDependency axis) {
        if (axis == YAxis.AxisDependency.LEFT) {
            if (this.mLeftAxisMin == Float.MAX_VALUE) {
                return this.mRightAxisMin;
            }
            return this.mLeftAxisMin;
        } else if (this.mRightAxisMin == Float.MAX_VALUE) {
            return this.mLeftAxisMin;
        } else {
            return this.mRightAxisMin;
        }
    }

    public float getYMax() {
        return this.mYMax;
    }

    public float getYMax(YAxis.AxisDependency axis) {
        if (axis == YAxis.AxisDependency.LEFT) {
            if (this.mLeftAxisMax == -3.4028235E38f) {
                return this.mRightAxisMax;
            }
            return this.mLeftAxisMax;
        } else if (this.mRightAxisMax == -3.4028235E38f) {
            return this.mLeftAxisMax;
        } else {
            return this.mRightAxisMax;
        }
    }

    public float getXMin() {
        return this.mXMin;
    }

    public float getXMax() {
        return this.mXMax;
    }

    public List<T> getDataSets() {
        return this.mDataSets;
    }

    /* access modifiers changed from: protected */
    public int getDataSetIndexByLabel(List<T> dataSets, String label, boolean ignorecase) {
        int i = 0;
        if (ignorecase) {
            while (i < dataSets.size()) {
                if (label.equalsIgnoreCase(((IDataSet) dataSets.get(i)).getLabel())) {
                    return i;
                }
                i++;
            }
            return -1;
        }
        while (i < dataSets.size()) {
            if (label.equals(((IDataSet) dataSets.get(i)).getLabel())) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public String[] getDataSetLabels() {
        String[] types = new String[this.mDataSets.size()];
        for (int i = 0; i < this.mDataSets.size(); i++) {
            types[i] = ((IDataSet) this.mDataSets.get(i)).getLabel();
        }
        return types;
    }

    public Entry getEntryForHighlight(Highlight highlight) {
        if (highlight.getDataSetIndex() >= this.mDataSets.size()) {
            return null;
        }
        return ((IDataSet) this.mDataSets.get(highlight.getDataSetIndex())).getEntryForXValue(highlight.getX(), highlight.getY());
    }

    public T getDataSetByLabel(String label, boolean ignorecase) {
        int index = getDataSetIndexByLabel(this.mDataSets, label, ignorecase);
        if (index < 0 || index >= this.mDataSets.size()) {
            return null;
        }
        return (IDataSet) this.mDataSets.get(index);
    }

    public T getDataSetByIndex(int index) {
        if (this.mDataSets == null || index < 0 || index >= this.mDataSets.size()) {
            return null;
        }
        return (IDataSet) this.mDataSets.get(index);
    }

    public void addDataSet(T d) {
        if (d != null) {
            calcMinMax(d);
            this.mDataSets.add(d);
        }
    }

    public boolean removeDataSet(T d) {
        if (d == null) {
            return false;
        }
        boolean removed = this.mDataSets.remove(d);
        if (removed) {
            calcMinMax();
        }
        return removed;
    }

    public boolean removeDataSet(int index) {
        if (index >= this.mDataSets.size() || index < 0) {
            return false;
        }
        return removeDataSet((IDataSet) this.mDataSets.get(index));
    }

    public void addEntry(Entry e2, int dataSetIndex) {
        if (this.mDataSets.size() <= dataSetIndex || dataSetIndex < 0) {
            Log.e("addEntry", "Cannot add Entry because dataSetIndex too high or too low.");
            return;
        }
        IDataSet set = (IDataSet) this.mDataSets.get(dataSetIndex);
        if (set.addEntry(e2)) {
            calcMinMax(e2, set.getAxisDependency());
        }
    }

    /* access modifiers changed from: protected */
    public void calcMinMax(Entry e2, YAxis.AxisDependency axis) {
        if (this.mYMax < e2.getY()) {
            this.mYMax = e2.getY();
        }
        if (this.mYMin > e2.getY()) {
            this.mYMin = e2.getY();
        }
        if (this.mXMax < e2.getX()) {
            this.mXMax = e2.getX();
        }
        if (this.mXMin > e2.getX()) {
            this.mXMin = e2.getX();
        }
        if (axis == YAxis.AxisDependency.LEFT) {
            if (this.mLeftAxisMax < e2.getY()) {
                this.mLeftAxisMax = e2.getY();
            }
            if (this.mLeftAxisMin > e2.getY()) {
                this.mLeftAxisMin = e2.getY();
                return;
            }
            return;
        }
        if (this.mRightAxisMax < e2.getY()) {
            this.mRightAxisMax = e2.getY();
        }
        if (this.mRightAxisMin > e2.getY()) {
            this.mRightAxisMin = e2.getY();
        }
    }

    /* access modifiers changed from: protected */
    public void calcMinMax(T d) {
        if (this.mYMax < d.getYMax()) {
            this.mYMax = d.getYMax();
        }
        if (this.mYMin > d.getYMin()) {
            this.mYMin = d.getYMin();
        }
        if (this.mXMax < d.getXMax()) {
            this.mXMax = d.getXMax();
        }
        if (this.mXMin > d.getXMin()) {
            this.mXMin = d.getXMin();
        }
        if (d.getAxisDependency() == YAxis.AxisDependency.LEFT) {
            if (this.mLeftAxisMax < d.getYMax()) {
                this.mLeftAxisMax = d.getYMax();
            }
            if (this.mLeftAxisMin > d.getYMin()) {
                this.mLeftAxisMin = d.getYMin();
                return;
            }
            return;
        }
        if (this.mRightAxisMax < d.getYMax()) {
            this.mRightAxisMax = d.getYMax();
        }
        if (this.mRightAxisMin > d.getYMin()) {
            this.mRightAxisMin = d.getYMin();
        }
    }

    public boolean removeEntry(Entry e2, int dataSetIndex) {
        IDataSet set;
        if (e2 == null || dataSetIndex >= this.mDataSets.size() || (set = (IDataSet) this.mDataSets.get(dataSetIndex)) == null) {
            return false;
        }
        boolean removed = set.removeEntry(e2);
        if (removed) {
            calcMinMax();
        }
        return removed;
    }

    public boolean removeEntry(float xValue, int dataSetIndex) {
        Entry e2;
        if (dataSetIndex < this.mDataSets.size() && (e2 = ((IDataSet) this.mDataSets.get(dataSetIndex)).getEntryForXValue(xValue, Float.NaN)) != null) {
            return removeEntry(e2, dataSetIndex);
        }
        return false;
    }

    public T getDataSetForEntry(Entry e2) {
        if (e2 == null) {
            return null;
        }
        for (int i = 0; i < this.mDataSets.size(); i++) {
            T set = (IDataSet) this.mDataSets.get(i);
            for (int j = 0; j < set.getEntryCount(); j++) {
                if (e2.equalTo(set.getEntryForXValue(e2.getX(), e2.getY()))) {
                    return set;
                }
            }
        }
        return null;
    }

    public int[] getColors() {
        if (this.mDataSets == null) {
            return null;
        }
        int clrcnt = 0;
        for (int i = 0; i < this.mDataSets.size(); i++) {
            clrcnt += ((IDataSet) this.mDataSets.get(i)).getColors().size();
        }
        int[] colors = new int[clrcnt];
        int cnt = 0;
        for (int i2 = 0; i2 < this.mDataSets.size(); i2++) {
            for (Integer clr : ((IDataSet) this.mDataSets.get(i2)).getColors()) {
                colors[cnt] = clr.intValue();
                cnt++;
            }
        }
        return colors;
    }

    public int getIndexOfDataSet(T dataSet) {
        return this.mDataSets.indexOf(dataSet);
    }

    /* access modifiers changed from: protected */
    public T getFirstLeft(List<T> sets) {
        for (T dataSet : sets) {
            if (dataSet.getAxisDependency() == YAxis.AxisDependency.LEFT) {
                return dataSet;
            }
        }
        return null;
    }

    public T getFirstRight(List<T> sets) {
        for (T dataSet : sets) {
            if (dataSet.getAxisDependency() == YAxis.AxisDependency.RIGHT) {
                return dataSet;
            }
        }
        return null;
    }

    public void setValueFormatter(IValueFormatter f) {
        if (f != null) {
            for (T set : this.mDataSets) {
                set.setValueFormatter(f);
            }
        }
    }

    public void setValueTextColor(int color) {
        for (T set : this.mDataSets) {
            set.setValueTextColor(color);
        }
    }

    public void setValueTextColors(List<Integer> colors) {
        for (T set : this.mDataSets) {
            set.setValueTextColors(colors);
        }
    }

    public void setValueTypeface(Typeface tf) {
        for (T set : this.mDataSets) {
            set.setValueTypeface(tf);
        }
    }

    public void setValueTextSize(float size) {
        for (T set : this.mDataSets) {
            set.setValueTextSize(size);
        }
    }

    public void setDrawValues(boolean enabled) {
        for (T set : this.mDataSets) {
            set.setDrawValues(enabled);
        }
    }

    public void setHighlightEnabled(boolean enabled) {
        for (T set : this.mDataSets) {
            set.setHighlightEnabled(enabled);
        }
    }

    public boolean isHighlightEnabled() {
        for (T set : this.mDataSets) {
            if (!set.isHighlightEnabled()) {
                return false;
            }
        }
        return true;
    }

    public void clearValues() {
        if (this.mDataSets != null) {
            this.mDataSets.clear();
        }
        notifyDataChanged();
    }

    public boolean contains(T dataSet) {
        for (T set : this.mDataSets) {
            if (set.equals(dataSet)) {
                return true;
            }
        }
        return false;
    }

    public int getEntryCount() {
        int count = 0;
        for (T set : this.mDataSets) {
            count += set.getEntryCount();
        }
        return count;
    }

    public T getMaxEntryCountSet() {
        if (this.mDataSets == null || this.mDataSets.isEmpty()) {
            return null;
        }
        T max = (IDataSet) this.mDataSets.get(0);
        for (T set : this.mDataSets) {
            if (set.getEntryCount() > max.getEntryCount()) {
                max = set;
            }
        }
        return max;
    }
}
