package android.support.transition;

import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(21)
class GhostViewApi21 implements GhostViewImpl {
    private static final String TAG = "GhostViewApi21";
    private static Method sAddGhostMethod;
    private static boolean sAddGhostMethodFetched;
    private static Class<?> sGhostViewClass;
    private static boolean sGhostViewClassFetched;
    private static Method sRemoveGhostMethod;
    private static boolean sRemoveGhostMethodFetched;
    private final View mGhostView;

    static GhostViewImpl addGhost(View view, ViewGroup viewGroup, Matrix matrix) {
        fetchAddGhostMethod();
        if (sAddGhostMethod != null) {
            try {
                return new GhostViewApi21((View) sAddGhostMethod.invoke((Object) null, new Object[]{view, viewGroup, matrix}));
            } catch (IllegalAccessException e2) {
            } catch (InvocationTargetException e3) {
                throw new RuntimeException(e3.getCause());
            }
        }
        return null;
    }

    static void removeGhost(View view) {
        fetchRemoveGhostMethod();
        if (sRemoveGhostMethod != null) {
            try {
                sRemoveGhostMethod.invoke((Object) null, new Object[]{view});
            } catch (IllegalAccessException e2) {
            } catch (InvocationTargetException e3) {
                throw new RuntimeException(e3.getCause());
            }
        }
    }

    private GhostViewApi21(@NonNull View ghostView) {
        this.mGhostView = ghostView;
    }

    public void setVisibility(int visibility) {
        this.mGhostView.setVisibility(visibility);
    }

    public void reserveEndViewTransition(ViewGroup viewGroup, View view) {
    }

    private static void fetchGhostViewClass() {
        if (!sGhostViewClassFetched) {
            try {
                sGhostViewClass = Class.forName("android.view.GhostView");
            } catch (ClassNotFoundException e2) {
                Log.i(TAG, "Failed to retrieve GhostView class", e2);
            }
            sGhostViewClassFetched = true;
        }
    }

    private static void fetchAddGhostMethod() {
        if (!sAddGhostMethodFetched) {
            try {
                fetchGhostViewClass();
                sAddGhostMethod = sGhostViewClass.getDeclaredMethod("addGhost", new Class[]{View.class, ViewGroup.class, Matrix.class});
                sAddGhostMethod.setAccessible(true);
            } catch (NoSuchMethodException e2) {
                Log.i(TAG, "Failed to retrieve addGhost method", e2);
            }
            sAddGhostMethodFetched = true;
        }
    }

    private static void fetchRemoveGhostMethod() {
        if (!sRemoveGhostMethodFetched) {
            try {
                fetchGhostViewClass();
                sRemoveGhostMethod = sGhostViewClass.getDeclaredMethod("removeGhost", new Class[]{View.class});
                sRemoveGhostMethod.setAccessible(true);
            } catch (NoSuchMethodException e2) {
                Log.i(TAG, "Failed to retrieve removeGhost method", e2);
            }
            sRemoveGhostMethodFetched = true;
        }
    }
}
