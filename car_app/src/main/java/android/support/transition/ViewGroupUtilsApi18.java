package android.support.transition;

import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.ViewGroup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(18)
class ViewGroupUtilsApi18 {
    private static final String TAG = "ViewUtilsApi18";
    private static Method sSuppressLayoutMethod;
    private static boolean sSuppressLayoutMethodFetched;

    static void suppressLayout(@NonNull ViewGroup group, boolean suppress) {
        fetchSuppressLayoutMethod();
        if (sSuppressLayoutMethod != null) {
            try {
                sSuppressLayoutMethod.invoke(group, new Object[]{Boolean.valueOf(suppress)});
            } catch (IllegalAccessException e2) {
                Log.i(TAG, "Failed to invoke suppressLayout method", e2);
            } catch (InvocationTargetException e3) {
                Log.i(TAG, "Error invoking suppressLayout method", e3);
            }
        }
    }

    private static void fetchSuppressLayoutMethod() {
        if (!sSuppressLayoutMethodFetched) {
            Class<ViewGroup> cls = ViewGroup.class;
            try {
                sSuppressLayoutMethod = cls.getDeclaredMethod("suppressLayout", new Class[]{Boolean.TYPE});
                sSuppressLayoutMethod.setAccessible(true);
            } catch (NoSuchMethodException e2) {
                Log.i(TAG, "Failed to retrieve suppressLayout method", e2);
            }
            sSuppressLayoutMethodFetched = true;
        }
    }

    private ViewGroupUtilsApi18() {
    }
}
