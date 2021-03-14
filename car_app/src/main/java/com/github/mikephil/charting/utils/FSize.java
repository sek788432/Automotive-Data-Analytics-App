package com.github.mikephil.charting.utils;

import com.github.mikephil.charting.utils.ObjectPool;
import java.util.List;

public final class FSize extends ObjectPool.Poolable {
    private static ObjectPool<FSize> pool = ObjectPool.create(256, new FSize(0.0f, 0.0f));
    public float height;
    public float width;

    static {
        pool.setReplenishPercentage(0.5f);
    }

    /* access modifiers changed from: protected */
    public ObjectPool.Poolable instantiate() {
        return new FSize(0.0f, 0.0f);
    }

    public static FSize getInstance(float width2, float height2) {
        FSize result = pool.get();
        result.width = width2;
        result.height = height2;
        return result;
    }

    public static void recycleInstance(FSize instance) {
        pool.recycle(instance);
    }

    public static void recycleInstances(List<FSize> instances) {
        pool.recycle(instances);
    }

    public FSize() {
    }

    public FSize(float width2, float height2) {
        this.width = width2;
        this.height = height2;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FSize)) {
            return false;
        }
        FSize other = (FSize) obj;
        if (this.width == other.width && this.height == other.height) {
            return true;
        }
        return false;
    }

    public String toString() {
        return this.width + "x" + this.height;
    }

    public int hashCode() {
        return Float.floatToIntBits(this.width) ^ Float.floatToIntBits(this.height);
    }
}
