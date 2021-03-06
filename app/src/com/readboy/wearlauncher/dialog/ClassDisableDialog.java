package com.readboy.wearlauncher.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.readboy.wearlauncher.R;

/**
 * Created by Administrator on 2017/7/11.
 */

public class ClassDisableDialog {
    private static final String TAG = "ClassDisableDialog";

    private static Dialog dialog = null;
    private static Context mContext;

    public static void showClassDisableDialog(Context context) {
        if (dialog == null) {
            mContext = context;
            dialog = new Dialog(context, R.style.dialog_fs);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            dialog.getWindow().setContentView(R.layout.dialog_class_disable);
            dialog.getWindow().getDecorView().findViewById(R.id.imageButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissClassDisableDialog();
                }
            });
        }
        dialog.show();
        mHandler.removeMessages(0x10);
        mHandler.sendEmptyMessageDelayed(0x10, 1000 * 2);
    }

    public static void recycle() {
        mHandler.removeMessages(0x10);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = null;
    }

    public static void dismissClassDisableDialog() {
        if (!isValidContext(mContext)) {
            Log.e(TAG, "dismissClassDisableDialog: context is disabled");
            mHandler.removeMessages(0x10);
            dialog = null;
            mContext = null;
            return;
        }

        if (dialog != null && dialog.isShowing()) {
            mHandler.removeMessages(0x10);
            dialog.dismiss();
            dialog = null;
        }
    }

    private static boolean isValidContext(Context c) {
        if (c instanceof Activity) {
            Activity a = (Activity) c;
            if (a.isDestroyed() || a.isFinishing()) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    static Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            dismissClassDisableDialog();
            super.dispatchMessage(msg);
        }
    };
}
