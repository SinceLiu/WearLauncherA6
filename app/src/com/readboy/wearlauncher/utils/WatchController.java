package com.readboy.wearlauncher.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.readboy.wetalk.utils.WTContactUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.readboy.ReadboyWearManager;

/**
 * 时间、日期、天气（警报）、电话/未接提示泡、微聊/未读微聊信息、计步
 * Created by Administrator on 2017/6/13.
 */

public class WatchController extends BroadcastReceiver {
    public static final String TAG = "WatchController";
    //class disable
    public static final String TAG_CLASS_DISABLED = "class_disabled";
    public static final String TAG_CLASS_DISABLED_TIME = "class_disable_time";
    public static final String READBOY_ACTION_CLASS_DISABLE_CHANGED = "readboy.action.CLASS_DISABLE_CHANGED";
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("HH:mm");
    //Weather
    public static final String ACTION_WEATHER_RESULT = "com.readboy.wearlauncher.weather.WEATHER_RESULT";
    public static final String ACTION_WEATHER_GET = "com.readboy.wearlauncher.weather.GET_WEATHER";
    public static final Uri WEATHER_CONTENT_URI = Uri.parse("content://com.readboy.wearweather.provider/data");
    //Step
    public static final String ACTION_STEP_ADD = "com.readboy.action.StepCountService.stepAdd";

    //call
    private final static Uri MISSCALL_CONTENT_URI = CallLog.Calls.CONTENT_URI;
    //wetalk
    public static final Uri WETALK_CONTENT_URI = WTContactUtils.CONVERSATION_URI;
    //step
    public static final Uri STEPS_CONTENT_URI = Uri.parse("content://com.readboy.pedometer.contentProvider/pedometer");

    //lost
    public static final String ACTION_LOST_CHANGED = "readboy.action.LOST_CHANGED";
    //app control
    public static final String ACTION_APP_CTRL_CHANGED = "readboy.action.NOTIFY_APP_CTRL";

    private static final Object LOCK = new Object();
    private final static String MISSCALL_WHERE = "type = 3 and new = 1";
    private static final int CALL_MSG_WHAT = 0x10;
    private static final int WETALK_MSG_WHAT = 0x11;
    private static final int WEATHER_MSG_WHAT = 0x12;

    public static final String[] WEEK_NAME_CN_LONG = new String[]{"星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    public static final String[] WEEK_NAME_CN_SHORT = new String[]{"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    public static final String[] WEEK_NAME_EN_SHORT = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    public static final String[] MONTHS_NAME_EN_SHORT = {
            "Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec"};

    public static final String[] WEEK_NAME_EN_LONG = new String[]{
            "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    public static final String[] MONTHS_NAME_EN_LONG = {
            "January", "February", "March", "April",
            "May", "June", "July", "August",
            "September", "October", "November", "December"};

    Context mContext;
    int mStepCount;
    int mMissCallCount;
    int mMissWetalkCount;
    String mWeatherCode = "";

    public interface DateChangedCallback {
        void onDateChange();
    }

    private ArrayList<DateChangedCallback> mDateChangedCallback = new ArrayList<>();

    public void addDateChangedCallback(DateChangedCallback cb) {
        if (!mDateChangedCallback.contains(cb)) {
            mDateChangedCallback.add(cb);
        }
        cb.onDateChange();
    }

    public void removeDateChangedCallback(DateChangedCallback cb) {
        mDateChangedCallback.remove(cb);
    }

    public interface StepChangedCallback {
        void onStepChange(int step);
    }

    private ArrayList<StepChangedCallback> mStepChangedCallback = new ArrayList<>();

    public void addStepChangedCallback(StepChangedCallback cb) {
        if (!mStepChangedCallback.contains(cb)) {
            mStepChangedCallback.add(cb);
        }
        cb.onStepChange(mStepCount);
    }

    public void removeStepChangedCallback(StepChangedCallback cb) {
        mStepChangedCallback.remove(cb);
    }

    private void fireStepChanged() {
        for (StepChangedCallback cb : mStepChangedCallback) {
            cb.onStepChange(mStepCount);
        }
    }

    public interface CallUnreadChangedCallback {
        void onCallUnreadChanged(int count);
    }

    private ArrayList<CallUnreadChangedCallback> mCallUnreadChangedCallback = new ArrayList<>();

    public void addCallUnreadChangedCallback(CallUnreadChangedCallback cb) {
        if (!mCallUnreadChangedCallback.contains(cb)) {
            mCallUnreadChangedCallback.add(cb);
        }
        cb.onCallUnreadChanged(mMissCallCount);
    }

    public void removeCallUnreadChangedCallback(CallUnreadChangedCallback cb) {
        mCallUnreadChangedCallback.remove(cb);
    }

    public interface WeTalkUnreadChangedCallback {
        void onWeTalkUnreadChanged(int count);
    }

    private ArrayList<WeTalkUnreadChangedCallback> mWeTalkUnreadChangedCallback = new ArrayList<>();

    public void addWeTalkUnreadChangedCallback(WeTalkUnreadChangedCallback cb) {
        if (!mWeTalkUnreadChangedCallback.contains(cb)) {
            mWeTalkUnreadChangedCallback.add(cb);
        }
        cb.onWeTalkUnreadChanged(mMissWetalkCount);
    }

    public void removeWeTalkUnreadChangedCallback(WeTalkUnreadChangedCallback cb) {
        mWeTalkUnreadChangedCallback.remove(cb);
    }

    public interface WeatherChangedCallback {
        void onWeatherChanged(String weatherCode);
    }

    private ArrayList<WeatherChangedCallback> mWeatherChangedCallback = new ArrayList<>();

    public void addWeatherChangedCallback(WeatherChangedCallback cb) {
        if (!mWeatherChangedCallback.contains(cb)) {
            mWeatherChangedCallback.add(cb);
        }
        cb.onWeatherChanged(mWeatherCode);
    }

    public void removeWeatherChangedCallback(WeatherChangedCallback cb) {
        mWeatherChangedCallback.remove(cb);
    }

    public interface ClassDisableChangedCallback {
        void onClassDisableChange(boolean show);
    }

    private ArrayList<ClassDisableChangedCallback> mClassDisableChangedCallback = new ArrayList<>();

    public void addClassDisableChangedCallback(ClassDisableChangedCallback cb) {
        if (!mClassDisableChangedCallback.contains(cb)) {
            mClassDisableChangedCallback.add(cb);
        }
        /*boolean show = !TextUtils.isEmpty(mClassDisableData) && isNowEnable();*/
        ReadboyWearManager rwm = (ReadboyWearManager) mContext.getSystemService(Context.RBW_SERVICE);
        boolean show = rwm.isClassForbidOpen();
        cb.onClassDisableChange(show);
    }

    public void removeClassDisableChangedCallback(ClassDisableChangedCallback cb) {
        mClassDisableChangedCallback.remove(cb);
    }

    public interface LostChangedCallback {
        void onLostChange();
    }

    private LostChangedCallback mLostChangedCallback;

    public void setLostChangedCallback(LostChangedCallback cb) {
        mLostChangedCallback = cb;
        if (cb != null) {
            cb.onLostChange();
        }
    }

    public interface AppControlledChangedback {
        void onAppControlledChange();
    }

    private ArrayList<AppControlledChangedback> mAppControlledChangedback = new ArrayList<>();

    public void addAppControlledChangedback(AppControlledChangedback cb) {
        if (!mAppControlledChangedback.contains(cb)) {
            mAppControlledChangedback.add(cb);
        }
        cb.onAppControlledChange();
    }

    public void removeAppControlledChangedback(AppControlledChangedback cb) {
        mAppControlledChangedback.remove(cb);
    }

    public ScreenOff mScreenOffListener;

    public void setScreenOffListener(ScreenOff l) {
        mScreenOffListener = l;
    }

    public interface ScreenOff {
        void onScreenOff();

        void onScreenOn();
    }

    void classDisableChanged() {
        ReadboyWearManager rwm = (ReadboyWearManager) mContext.getSystemService(Context.RBW_SERVICE);
        boolean show = rwm.isClassForbidOpen();
        for (ClassDisableChangedCallback callback : mClassDisableChangedCallback) {
            callback.onClassDisableChange(show);
        }
    }

    public WatchController() {

    }

    public WatchController(Context context) {
        mContext = context;
        // broadcasts
        IntentFilter filter = new IntentFilter();
        //class disable
        filter.addAction(READBOY_ACTION_CLASS_DISABLE_CHANGED);
        //lost
        filter.addAction(ACTION_LOST_CHANGED);
        //app control
        filter.addAction(ACTION_APP_CTRL_CHANGED);
        //date
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        //time
        filter.addAction(Intent.ACTION_TIME_TICK);
        //weather
        filter.addAction(ACTION_WEATHER_RESULT);
        //step
        filter.addAction(ACTION_STEP_ADD);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        context.registerReceiver(this, filter);

        mContext.getContentResolver().registerContentObserver(MISSCALL_CONTENT_URI,
                true, sMissCallObserver);
        mContext.getContentResolver().registerContentObserver(WETALK_CONTENT_URI,
                true, sMissWeTalkObserver);
//        mContext.getContentResolver().registerContentObserver(WEATHER_CONTENT_URI,
//                true, sWeatherObserver);

        getMissCallCount();
        getAllContactsUnreadCount(mContext);
        mStepCount = getSteps();
    }

    public int getSteps() {
        int count = 0;
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(STEPS_CONTENT_URI, null, "date=" + Utils.getTodayStartTime(), null, null);
            if (c != null && c.moveToFirst()) {
                count = c.getInt(c.getColumnIndex("steps"));
            }
            if (c != null) {
                c.close();
                c = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }

        return count;
    }

    private int getContentCount(Uri uri, String where) {
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = mContext.getContentResolver().query(uri, null, where, null, null);
            count = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
                cursor = null;
            }
        }
        return count;
    }

    public int getMissCallCount() {
        mMissCallCount = getContentCount(MISSCALL_CONTENT_URI, MISSCALL_WHERE);
        return mMissCallCount;
    }

    public int getMissCallCountImmediately() {
        return mMissCallCount;
    }

    public int getAllContactsUnreadCount(Context context) {
        mMissWetalkCount = WTContactUtils.getUnreadMessageCount(context);
        return mMissWetalkCount;
    }

    public void getWeatherCode() {
        try {
            Cursor cursor = mContext.getContentResolver().query(WEATHER_CONTENT_URI, null, null, null, null);
            if (cursor != null && cursor.moveToLast()) {
                mWeatherCode = cursor.getString(cursor.getColumnIndex("weathercode"));
                Log.e("lxx", "getWeatherCode():" + cursor.getString(cursor.getColumnIndex("weathercode")));
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final ContentObserver sMissCallObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (LOCK) {
                        int phoneCount = getMissCallCount();
                        Message msg = mHandler.obtainMessage(CALL_MSG_WHAT, phoneCount, 0, null);
                        mHandler.sendMessage(msg);
                    }
                }
            }).start();
        }
    };

    private final ContentObserver sMissWeTalkObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (LOCK) {
                        int num = getAllContactsUnreadCount(mContext);
                        Message msg = mHandler.obtainMessage(WETALK_MSG_WHAT, num, 0, null);
                        mHandler.sendMessage(msg);
                    }
                }
            }).start();
        }
    };

    private final ContentObserver sWeatherObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (LOCK) {
                        Log.e("lxx", "weather:onChange()");
                        getWeatherCode();
                        Message msg = mHandler.obtainMessage(WEATHER_MSG_WHAT, null);
                        mHandler.sendMessage(msg);
                    }
                }
            }).start();
        }
    };

    Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case CALL_MSG_WHAT:
                    Log.i(TAG, "miss call mun :" + msg.arg1);
                    for (CallUnreadChangedCallback callback : mCallUnreadChangedCallback) {
                        callback.onCallUnreadChanged(msg.arg1);
                    }
                    return;
                case WETALK_MSG_WHAT:
                    Log.i(TAG, "miss wetalk mun :" + msg.arg1);
                    for (WeTalkUnreadChangedCallback callback : mWeTalkUnreadChangedCallback) {
                        callback.onWeTalkUnreadChanged(+msg.arg1);
                    }
                    return;
                case WEATHER_MSG_WHAT:
                    Log.e("lxx", "weather code :" + mWeatherCode);
                    for (WeatherChangedCallback callback : mWeatherChangedCallback) {
                        callback.onWeatherChanged(mWeatherCode);
                    }
                    return;
                default:
                    break;
            }
            super.dispatchMessage(msg);
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        if (TextUtils.equals(action, Intent.ACTION_DATE_CHANGED) ||
                TextUtils.equals(action, Intent.ACTION_TIMEZONE_CHANGED) ||
                TextUtils.equals(action, Intent.ACTION_TIME_CHANGED)) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int week = (calendar.get(Calendar.DAY_OF_WEEK) - 1) % WEEK_NAME_CN_SHORT.length;
            for (DateChangedCallback callback : mDateChangedCallback) {
                callback.onDateChange();
            }
        } else if (TextUtils.equals(action, Intent.ACTION_TIME_TICK)) {
            classDisableChanged();
        } else if (TextUtils.equals(action, ACTION_STEP_ADD)) {
            int steps = intent.getIntExtra("steps", 0);
            mStepCount = steps;
            fireStepChanged();
        } else if (TextUtils.equals(action, READBOY_ACTION_CLASS_DISABLE_CHANGED)) {
            classDisableChanged();
        } else if (TextUtils.equals(action, Intent.ACTION_SCREEN_OFF)) {
            if (mScreenOffListener != null) {
                mScreenOffListener.onScreenOff();
            }
        } else if (TextUtils.equals(action, Intent.ACTION_SCREEN_ON)) {
            if (mScreenOffListener != null) {
                mScreenOffListener.onScreenOn();
            }
        } else if (TextUtils.equals(action, ACTION_LOST_CHANGED)) {
            Log.e(TAG, "action:" + action);
            if (mLostChangedCallback != null) {
                mLostChangedCallback.onLostChange();
            }
        } else if (TextUtils.equals(action, ACTION_APP_CTRL_CHANGED)) {
            Log.e(TAG, "action:" + action);
            for (AppControlledChangedback callback : mAppControlledChangedback) {
                callback.onAppControlledChange();
            }
        }
    }

}
