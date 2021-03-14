package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Matrix;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ImageViewUtils {
    private static final String TAG = "ImageViewUtils";
    private static Method sAnimateTransformMethod;
    private static boolean sAnimateTransformMethodFetched;

    static void startAnimateTransform(ImageView view) {
        if (Build.VERSION.SDK_INT < 21) {
            ImageView.ScaleType scaleType = view.getScaleType();
            view.setTag(R.id.save_scale_type, scaleType);
            if (scaleType == ImageView.ScaleType.MATRIX) {
                view.setTag(R.id.save_image_matrix, view.getImageMatrix());
            } else {
                view.setScaleType(ImageView.ScaleType.MATRIX);
            }
            view.setImageMatrix(MatrixUtils.IDENTITY_MATRIX);
        }
    }

    static void animateTransform(ImageView view, Matrix matrix) {
        if (Build.VERSION.SDK_INT < 21) {
            view.setImageMatrix(matrix);
            return;
        }
        fetchAnimateTransformMethod();
        if (sAnimateTransformMethod != null) {
            try {
                sAnimateTransformMethod.invoke(view, new Object[]{matrix});
            } catch (IllegalAccessException e2) {
            } catch (InvocationTargetException e3) {
                throw new RuntimeException(e3.getCause());
            }
        }
    }

    private static void fetchAnimateTransformMethod() {
        if (!sAnimateTransformMethodFetched) {
            try {
                sAnimateTransformMethod = ImageView.class.getDeclaredMethod("animateTransform", new Class[]{Matrix.class});
                sAnimateTransformMethod.setAccessible(true);
            } catch (NoSuchMethodException e2) {
                Log.i(TAG, "Failed to retrieve animateTransform method", e2);
            }
            sAnimateTransformMethodFetched = true;
        }
    }

    static void reserveEndAnimateTransform(final ImageView view, Animator animator) {
        if (Build.VERSION.SDK_INT < 21) {
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    ImageView.ScaleType scaleType = (ImageView.ScaleType) view.getTag(R.id.save_scale_type);
                    view.setScaleType(scaleType);
                    view.setTag(R.id.save_scale_type, (Object) null);
                    if (scaleType == ImageView.ScaleType.MATRIX) {
                        view.setImageMatrix((Matrix) view.getTag(R.id.save_image_matrix));
                        view.setTag(R.id.save_image_matrix, (Object) null);
                    }
                    animation.removeListener(this);
                }
            });
        }
    }

    private ImageViewUtils() {
    }
}
