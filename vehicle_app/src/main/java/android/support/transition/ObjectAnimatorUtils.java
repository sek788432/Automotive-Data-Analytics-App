package android.support.transition;

import android.animation.ObjectAnimator;
import android.animation.TypeConverter;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.util.Property;

class ObjectAnimatorUtils {
    static <T> ObjectAnimator ofPointF(T target, Property<T, PointF> property, Path path) {
        if (Build.VERSION.SDK_INT >= 21) {
            return ObjectAnimator.ofObject(target, property, (TypeConverter) null, path);
        }
        return ObjectAnimator.ofFloat(target, new PathProperty(property, path), new float[]{0.0f, 1.0f});
    }

    private ObjectAnimatorUtils() {
    }
}
