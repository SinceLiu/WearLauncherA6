package com.readboy.wearlauncher.utils;

import android.app.ActivityManager;
import android.app.readboy.ReadboyWearManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.UserHandle;
import android.telecom.TelecomManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oubin
 * @date 2019/1/23
 */
public class ClassForbidUtils {
    private static final String TAG = "ClassForbidUtils";
    private static final int RINGER_MODE_IDLE = -1;
    private static int mLastRingerMode = RINGER_MODE_IDLE;

    private static String CATEGORY_CLASS_FORBID = "readboy.intent.category.CLASS_FORBID";

    private ClassForbidUtils() {
    }

    public static void killRecentTask(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> appRunningList = am.getRunningAppProcesses();
        List<String> packageNameList = new ArrayList<String>();
        int size = appRunningList.size();
        for (int i = 0; i < size; i++) {
            ActivityManager.RunningAppProcessInfo info = appRunningList.get(i);
            if (info.processName != null) {
                //过滤以下进程
                if (info.processName.equals("android.process.acore")
                        || info.processName.equals("android.process.media")
                        || info.processName.equals("com.android.defcontainer")
                        || info.processName.equals("com.android.packageinstaller")
                        || info.processName.equals("com.android.keychain")
                        || info.processName.equals("com.android.settings")
                        || info.processName.equals("com.android.bluetooth")
                        || info.processName.equals("com.android.systemui")
                        || info.processName.equals("com.android.phone")
                        || info.processName.equals("system")
                        || info.processName.equals("com.mediatek.ims")
                        || info.processName.equals("com.readboy.corepush")
                        || info.processName.equals("com.readboy.rbfota")
                        || info.processName.equals("com.readboy.wear.rbsos")
                        || info.processName.equals("com.readboy.floatwindow")
                        || info.processName.equals("com.readboy.wearlauncher"))
                {
                    continue;
                }
                //过滤特定Category
                if (hasClassForbidCategory(info, pm)) {
                    continue;
                }
                //若是通话中，挂断电话
//                if (isInCall(info.processName, context)) {
//                    TelecomManager manager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
//                    manager.endCall();
//                    continue;
//                }

            }
            packageNameList.add(info.processName);
        }

        Log.e(TAG, "上课禁用、低电或挂失杀进程：" + packageNameList);
        for (String pkgName : packageNameList) {
            am.forceStopPackageAsUser(pkgName, UserHandle.CURRENT.getIdentifier());
        }

    }

    private static boolean hasClassForbidCategory(ActivityManager.RunningAppProcessInfo info, PackageManager pm) {
        List<IntentFilter> intentFilterList = pm.getAllIntentFilters(info.processName);
        for (IntentFilter intentFilter : intentFilterList) {
            if (intentFilter.hasCategory(CATEGORY_CLASS_FORBID)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInCall(String pkgName, Context context) {
        if (!pkgName.equals("com.android.dialer")) {
            return false;
        }
        TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        return tm.isInCall();
    }

    private static void setClassDisableAudioMode(boolean enable, Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int mode = am.getRingerMode();
        if (enable) {
            //am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            if (mode == AudioManager.RINGER_MODE_SILENT || mode == AudioManager.RINGER_MODE_VIBRATE) {
                return;
            }
            if (mLastRingerMode == RINGER_MODE_IDLE) {
                mLastRingerMode = mode;
            }
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        } else {
            if (mLastRingerMode != RINGER_MODE_IDLE && mode == AudioManager.RINGER_MODE_VIBRATE) {
                am.setRingerMode(mLastRingerMode);
            }
            mLastRingerMode = RINGER_MODE_IDLE;
        }
    }

    public static void handleClassForbid(boolean enable, Context context) {
        ReadboyWearManager rwm = (ReadboyWearManager) context.getSystemService(Context.RBW_SERVICE);
        rwm.setClassForbidOpen(enable);
        if (enable) {
            killRecentTask(context);
        }
        setClassDisableAudioMode(enable, context);
    }
}
