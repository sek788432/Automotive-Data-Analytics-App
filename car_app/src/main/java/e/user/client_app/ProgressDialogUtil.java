package e.user.client_app;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProgressDialogUtil {
    private static AlertDialog mAlertDialog;

    public static void showProgressDialog(Context context) {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(context, R.style.CustomProgressDialog).create();
        }
        View loadView = LayoutInflater.from(context).inflate(R.layout.progress_view, (ViewGroup) null);
        mAlertDialog.setView(loadView, 0, 0, 0, 0);
        mAlertDialog.setCanceledOnTouchOutside(false);
        ((TextView) loadView.findViewById(R.id.tvTip)).setText("運算中...");
        mAlertDialog.show();
    }

    public static void showProgressDialog(Context context, String tip) {
        if (TextUtils.isEmpty(tip)) {
            tip = "載入中...";
        }
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(context, R.style.CustomProgressDialog).create();
        }
        View loadView = LayoutInflater.from(context).inflate(R.layout.progress_view, (ViewGroup) null);
        mAlertDialog.setView(loadView, 0, 0, 0, 0);
        mAlertDialog.setCanceledOnTouchOutside(false);
        ((TextView) loadView.findViewById(R.id.tvTip)).setText(tip);
        mAlertDialog.show();
    }

    public static void dismiss() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    public static void hide() {
        if (mAlertDialog != null) {
            mAlertDialog.hide();
        }
    }
}
