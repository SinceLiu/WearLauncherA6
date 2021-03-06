package com.readboy.wearlauncher;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.readboy.PersonalInfo;
import android.app.readboy.ReadboyWearManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.ServiceManager;
import android.os.IPowerManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import com.readboy.wearlauncher.battery.BatteryController;
import com.readboy.wearlauncher.dialog.ClassDisableDialog;
import com.readboy.wearlauncher.dialog.InstructionsDialog;
import com.readboy.wearlauncher.fragment.CameraFragment;
import com.readboy.wearlauncher.fragment.DaialFragment;
import com.readboy.wearlauncher.fragment.WatchAppFragment;
import com.readboy.wearlauncher.notification.NotificationActivity;
import com.readboy.wearlauncher.utils.ClassForbidUtils;
import com.readboy.wearlauncher.utils.StartFactoryModeService;
import com.readboy.wearlauncher.utils.Utils;
import com.readboy.wearlauncher.utils.WatchController;
import com.readboy.wearlauncher.view.DialBaseLayout;
import com.readboy.wearlauncher.view.GestureView;
import com.readboy.wearlauncher.view.MyViewPager;
import com.readboy.wearlauncher.view.WatchDials;
import com.readboy.wetalk.support.WetalkFragment;
import com.android.dialer.app.readboysupport.fragment.ContactsListFragment;
import com.readboy.mmsupport.fragment.MomentsFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.readboy.wearlauncher.utils.WatchController.READBOY_ACTION_SLEEPING_MODE_CHANGED;

public class Launcher extends FragmentActivity implements BatteryController.BatteryStateChangeCallback,
        GestureView.MyGestureListener, WatchController.ClassDisableChangedCallback, WatchController.ScreenOff,
        WatchController.LostChangedCallback, WatchController.AppControlledChangedback, WatchController.SleepingModeChangedCallback {
    public static final String TAG = Launcher.class.getSimpleName();

    private LauncherApplication mApplication;
    private static final int PERMISSIONS_REQUEST_CODE = 0x33;
    public static final int POSITION_MAIN_PAGE = 1;  //表盘屏的位置
    public static final int POSITION_CONTACT_PAGE = POSITION_MAIN_PAGE - 1;
    public static final int POSITION_WETALK_PAGE = POSITION_MAIN_PAGE + 1;
    public static final String ACTION_CLASS_DISABLE_STATUS_CHANGED = "android.intent.action.CLASS_DISABLE_STATUS_CHANGED";
    public static final String ACTION_APPCTRL_UPDATE_NOTIFICATION = "readboy.action.APPCTRL_UPDATE_NOTIFICATION";
    private static final int WAIT_IRWS = 0x11;
    private LocalBroadcastManager mLocalBroadcastManager;

    private GestureView mGestureView;
    private ViewStub mLowViewStub;
    private ViewStub mLossViewStub;
    private ViewStub mSleepViewStub;
    private DialBaseLayout mLowDialBaseLayout;
    private DialBaseLayout mLossDialBaseLayout;
    private DialBaseLayout mSleepDialBaseLayout;
    private MyViewPager mViewpager;
    private FragmentAdapter mFragmentAdapter;
    private List<Fragment> mFragmentList;
    private WatchDials mWatchDials;
    private ContactsListFragment mContactsListFragment;
    private DaialFragment mDaialFragment;
    private WetalkFragment mWetalkFragment;
    private MomentsFragment mMomentsFragment;
    private CameraFragment mCameraFragment;
    private WatchAppFragment mWatchAppFragment;
    int mTouchSlopSquare;
    int mViewPagerScrollState = ViewPager.SCROLL_STATE_IDLE;
    private int mWatchType;
    private Toast mToast;

    WatchController mWatchController;
    BatteryController mBatteryController;
    private int mBatteryLevel = -1;
    private int mSleepingMode = 1;   //1：飞行模式、2：关数据和wifi
    private boolean bIsSleeping = false;
    private boolean bIsAirPlaneModeChangedBySleep = false;
    private boolean bIsWifiOpen = false;
    private boolean bIsClassDisable = false;
    private boolean bIsLost = false;
    private boolean bIsMomentControlled = false;
    private boolean bIsTouchable = false;
    private boolean bIsWetalkOverScroll;  //可拖拽
    private boolean bIsContactsOverScroll;
    private boolean bIsSpi;
    private TelephonyManager mTelephonyManager;
    private WifiManager mWifiManager;
    private AlarmManager mAlarmManager;
    private Calendar openSleepingModeCalendar;
    private Calendar closeSleepingModeCalendar;
    private PendingIntent sleepPendingIntent;
    private ReadboyWearManager rwm;
    private static final String[] sPermissions = {
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate()");
        setContentView(R.layout.activity_launcher);
        //screen width:240、height:240,density:0.75,densityDpi:120

        mViewpager = (MyViewPager) findViewById(R.id.viewpager);
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        ViewConfiguration configuration = ViewConfiguration.get(Launcher.this);
        int touchSlop = configuration.getScaledTouchSlop();
        mTouchSlopSquare = touchSlop * 20;

        mApplication = (LauncherApplication) getApplication();
        mWatchType = LauncherSharedPrefs.getWatchType(this);
        mBatteryController = new BatteryController(this);
        mBatteryController.addStateChangedCallback(this);

        mWatchController = mApplication.getWatchController();
        mWatchController.addClassDisableChangedCallback(this);

        mGestureView = (GestureView) findViewById(R.id.content_container);
        mGestureView.setGestureListener(this);

        mLowViewStub = (ViewStub) findViewById(R.id.id_low);
        mLossViewStub = (ViewStub) findViewById(R.id.id_loss);
        mSleepViewStub = (ViewStub) findViewById(R.id.id_sleep);

        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == POSITION_CONTACT_PAGE || position == POSITION_WETALK_PAGE) {
                    if (!bIsWetalkOverScroll) {  //设置可拖拽效果
                        RecyclerView mWetalkRecyclerView = mWetalkFragment.getRecyclerView();
                        if (mWetalkRecyclerView != null) {
                            OverScrollDecoratorHelper.setUpOverScroll(mWetalkRecyclerView, 0);  //拖拽效果
                            bIsWetalkOverScroll = true;
                        }
                    }
                    if (!bIsContactsOverScroll) {
                        RecyclerView mContactsRecyclerView = mContactsListFragment.getRecyclerView();
                        if (mContactsRecyclerView != null) {
                            OverScrollDecoratorHelper.setUpOverScroll(mContactsRecyclerView, 0);  //拖拽效果
                            bIsContactsOverScroll = true;
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mViewPagerScrollState = state;
            }
        });
        mFragmentList = new ArrayList<Fragment>();
        mFragmentList.clear();
        mContactsListFragment = new ContactsListFragment();
        mFragmentList.add(mContactsListFragment);
        mDaialFragment = new DaialFragment();
        mFragmentList.add(mDaialFragment);
        mWetalkFragment = WetalkFragment.newInstance();
        mFragmentList.add(mWetalkFragment);
        mMomentsFragment = new MomentsFragment();
        mFragmentList.add(mMomentsFragment);
        mCameraFragment = new CameraFragment();
        mFragmentList.add(mCameraFragment);
        mWatchAppFragment = new WatchAppFragment();
        mFragmentList.add(mWatchAppFragment);
        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        mViewpager.setAdapter(mFragmentAdapter);
        mViewpager.setCurrentItem(POSITION_MAIN_PAGE);
        mViewpager.setOffscreenPageLimit(mFragmentList.size());
        OverScrollDecoratorHelper.setUpOverScroll(mViewpager);
        mWatchController.setScreenOffListener(this);
        mWatchController.setLostChangedCallback(this);
        mWatchController.addAppControlledChangedback(this);
        sleepPendingIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(READBOY_ACTION_SLEEPING_MODE_CHANGED), PendingIntent.FLAG_CANCEL_CURRENT);
        mWatchController.addSleepingModeChangedCallback(this);
//        startPowerAnimService();
        //Utils.setFirstBoot(Launcher.this,true);
        if (Utils.isFirstBoot(Launcher.this)) {
            InstructionsDialog.showInstructionsDialog(Launcher.this);
        }
        Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
        System.out.println("-----------  Provision USER_SETUP_COMPLETE");
        Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);
        bIsSpi = Settings.System.getInt(getContentResolver(), "is_spi", 0) == 1;
        rwm = (ReadboyWearManager) Launcher.this.getSystemService(Context.RBW_SERVICE);
        waitIRWS();
    }

    private void initLowDialBaseLayout() {
        mLowDialBaseLayout = (DialBaseLayout) findViewById(R.id.low);
        mLowDialBaseLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showToast(R.string.notice_low_power_for_phone);
                }
                return true;
            }
        });
    }

    private void initLossDialBaseLayout() {
        mLossDialBaseLayout = (DialBaseLayout) findViewById(R.id.loss);
        mLossDialBaseLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showToast(R.string.notice_loss_for_phone);
                }
                return true;
            }
        });
    }

    private void initSleepDialBaseLayout() {
        mSleepDialBaseLayout = (DialBaseLayout) findViewById(R.id.sleep);
        mSleepDialBaseLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mSleepDialBaseLayout.setVisibility(View.GONE);
                return false;
            }
        });
    }

    //等待IReadboyWearService
    public void waitIRWS() {
        if (rwm.getPersonalInfo() != null && mBatteryLevel != -1) {
            onAppControlledChange();
            if (rwm.getPersonalInfo().getLost() == 1) {
                onLostChange();
            } else if (rwm.isClassForbidOpen()) {
                onClassDisableChange(true);
            } else {
                startFactoryModeService();
            }
            mHandler.removeMessages(WAIT_IRWS);
        } else {
            mHandler.removeMessages(WAIT_IRWS);
            mHandler.sendEmptyMessageDelayed(WAIT_IRWS, 1000);
        }
    }

    //启动服务搜索特定的wifi，來启动工厂模式
    public void startFactoryModeService() {
        if (mBatteryLevel < lowPowerLevel) {
            Log.e(TAG, "Can't start FM because of low power.");
            return;
        }
        PersonalInfo info = rwm.getPersonalInfo();
        if (info != null) {
            if (info.getAdminUuid() != null && !"".equals(info.getAdminUuid())) {
                Log.e(TAG, "The watch has been bound!");
                return;
            }
        } else {
            return;
        }
        Intent intent = new Intent(Launcher.this, StartFactoryModeService.class);
        startService(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //禁止缓存，优化低内存主界面被杀，表盘切换不了的问题
//        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bIsTouchable = true;
        LauncherApplication.setTouchEnable(true);
        requestPermissions(sPermissions);
        forceUpdateDate();
//        dialResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bIsTouchable = false;
        closeDials(false);
//        dialPasue();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy() ");
        mWatchController.removeClassDisableChangedCallback(this);
        mWatchController.setLostChangedCallback(null);
        mWatchController.removeAppControlledChangedback(this);
        mWatchController.removeSleepingModeChangedCallback(this);
        mBatteryController.unregisterReceiver();
        mBatteryController.removeStateChangedCallback(this);
        mHandler.removeCallbacksAndMessages(null);
        ClassDisableDialog.recycle();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!LauncherApplication.isTouchEnable() || !bIsTouchable) {
            return true;
        }
        if (Utils.isFirstBoot(Launcher.this)) {
            if (ev.getAction() == MotionEvent.ACTION_UP) {
                InstructionsDialog.showInstructionsDialog(Launcher.this);
            }
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onLostChange() {
        if (rwm == null || rwm.getPersonalInfo() == null) {
            return;
        }
        if (rwm.getPersonalInfo().getLost() == 1) {
            if (bIsLost) {
                return;
            }
            if (mLossViewStub.getParent() != null) {
                mLossViewStub.inflate();
                initLossDialBaseLayout();
            }
            mLossDialBaseLayout.setVisibility(View.VISIBLE);
            ClassForbidUtils.killRecentTask(Launcher.this);
            if (needGoToHome(Launcher.this, 1)) {
                goHome();
            }
            bIsLost = true;
            //响铃加振动、音量最大
            AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            Settings.System.putInt(getContentResolver(), Settings.System.VIBRATE_WHEN_RINGING, 1);
            audio.setRingerMode(2);
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            audio.setStreamVolume(AudioManager.STREAM_SYSTEM, audio.getStreamMaxVolume(AudioManager.STREAM_SYSTEM), 0);
            audio.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audio.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
        } else if (mLossDialBaseLayout != null) {
            mLossDialBaseLayout.setVisibility(View.GONE);
            bIsLost = false;
        }
    }

    @Override
    public void onAppControlledChange() {
        if (rwm == null) {
            return;
        }
        if (mWatchAppFragment != null) {
            mWatchAppFragment.loadApps(true);
        }
        PersonalInfo info = rwm.getPersonalInfo();
        if (info == null) {
            return;
        }
        if (mCameraFragment != null) {
            mCameraFragment.setVideoVisible(!info.isAppCtrl(true, "miniVideo"));
        }
        updateViewPager(info.isAppCtrl(true, "moment"));
        Intent intent = new Intent(ACTION_APPCTRL_UPDATE_NOTIFICATION);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onSleepingModeChange() {
        boolean open = isSleepingModeOpen();
        Log.e("lxx", "sleepingModeChange:" + mSleepingMode);
        if (open) {
            if (isInSleepingTime()) {
                showSleepView();
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                //灭屏状态下开飞行模式
                if (!pm.isScreenOn() && !Utils.isAirplaneModeOn(this)) {
                    bIsAirPlaneModeChangedBySleep = true;
                    changeAirPlaneMode(true);
                }
                ClassForbidUtils.killRecentTask(this);
                if (needGoToHome(this, 1)) {
                    goHome();
                }
                if (mGestureView != null && mGestureView.getVisibility() == View.VISIBLE
                        && mViewpager.getCurrentItem() != POSITION_MAIN_PAGE) {
                    mViewpager.setCurrentItem(POSITION_MAIN_PAGE);
                }
                bIsSleeping = true;
                //set alarm to close
                setCloseSleepingModeTime();
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, closeSleepingModeCalendar.getTimeInMillis(), sleepPendingIntent);
            } else {
                hideSleepView();
                changeAirPlaneMode(false);
                bIsSleeping = false;
                //set alarm to open
                setOpenSleepingModeTime();
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, openSleepingModeCalendar.getTimeInMillis(), sleepPendingIntent);
            }
        } else {
            //cancel alarm
            mAlarmManager.cancel(sleepPendingIntent);
            if (bIsSleeping) {
                hideSleepView();
                changeAirPlaneMode(false);
                bIsSleeping = false;
            }
        }
    }

    @Override
    public void onClassDisableChange(boolean show) {
        Log.e("cwj", "onClassDisableChange: show=" + show);
        if (bIsClassDisable != show) {
            bIsClassDisable = show;
            mViewpager.setClassDisabled(show);
            ClassForbidUtils.handleClassForbid(bIsClassDisable, Launcher.this);
            if (bIsClassDisable) {
                closeDials(false);
                if (needGoToHome(Launcher.this, 0)) {
//                    startActivity(new Intent(Launcher.this, Launcher.class));
                    goHome();
                }
                if (mGestureView != null && mGestureView.getVisibility() == View.VISIBLE
                        && mViewpager.getCurrentItem() != POSITION_MAIN_PAGE) {
                    mViewpager.setCurrentItem(POSITION_MAIN_PAGE);
                }
                mViewpager.scrollTo(0, 0);
                mViewPagerScrollState = ViewPager.SCROLL_STATE_IDLE;
                if (isHome(Launcher.this)) {
                    ClassDisableDialog.showClassDisableDialog(Launcher.this);
                }
            }
            //通知通知栏
            Intent intent = new Intent(ACTION_CLASS_DISABLE_STATUS_CHANGED);
            mLocalBroadcastManager.sendBroadcast(intent);
        }
    }

    int lowPowerLevel = 15;

    @Override
    public void onBatteryLevelChanged(int level, boolean pluggedIn, boolean charging) {
        if (mGestureView == null || mBatteryLevel == level) {
            return;
        }
        if (level < lowPowerLevel) {
            if (mBatteryLevel != -1 && mBatteryLevel < lowPowerLevel) {
                mBatteryLevel = level;
                return;
            }
            mGestureView.setVisibility(View.GONE);
            if (mLowViewStub.getParent() != null) {
                mLowViewStub.inflate();
                initLowDialBaseLayout();
            }
            mLowDialBaseLayout.setVisibility(View.VISIBLE);
            mLowDialBaseLayout.addChangedCallback();
            mLowDialBaseLayout.onResume();
            mLowDialBaseLayout.setButtonEnable();
            if (mDaialFragment != null) {
                mDaialFragment.dialPause();
            }
            rwm.setLowPowerMode(true);
            PowerManager mPowerManager = (PowerManager) Launcher.this.getSystemService(Context.POWER_SERVICE);
            mPowerManager.setPowerSaveMode(true);
            ClassForbidUtils.killRecentTask(Launcher.this);
            if (needGoToHome(Launcher.this, 1)) {
                goHome();
            }
        } else if (mBatteryLevel == -1 || mBatteryLevel < lowPowerLevel) {
            if (mLowDialBaseLayout != null) {
                mLowDialBaseLayout.setVisibility(View.GONE);
                mGestureView.setVisibility(View.VISIBLE);
            }
            if (mDaialFragment != null) {
                mDaialFragment.dialResume();
            }
            rwm.setLowPowerMode(false);
            PowerManager mPowerManager = (PowerManager) Launcher.this.getSystemService(Context.POWER_SERVICE);
            mPowerManager.setPowerSaveMode(false);
        }
        mBatteryLevel = level;
    }

    @Override
    public void onPowerSaveChanged() {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (WatchDials.getWatchDialsStatus() == WatchDials.ANIMATE_STATE_OPENING ||
                WatchDials.getWatchDialsStatus() == WatchDials.ANIMATE_STATE_OPENED) {
            return false;
        }
        int cur = mViewpager.getCurrentItem();
        if (e1 == null || e2 == null || cur != POSITION_MAIN_PAGE) {
            return false;
        }
        float vDistance = e1.getY() - e2.getY();
        boolean bVerticalMove = Math.abs(velocityX) - Math.abs(velocityY) < 0;
        if (vDistance > mTouchSlopSquare / 5 && bVerticalMove && (bIsSpi || mViewPagerScrollState == ViewPager.SCROLL_STATE_IDLE)) {
            PersonalInfo info = rwm.getPersonalInfo();
            if (info != null && info.isHasSiri() == 1) {
                if (info.isAppCtrl(true, "voiceAssitant")) {
                    showToast(R.string.notice_application_control_speech);
                } else {
                    Log.e(TAG, "onFling: start Speech activity.");
                    Utils.startActivity(Launcher.this, "com.readboy.watch.speech", "com.readboy.watch.speech.Main2Activity");
                }
            }
            return true;
        } else if (vDistance < -mTouchSlopSquare / 2 && bVerticalMove && (bIsSpi || mViewPagerScrollState == ViewPager.SCROLL_STATE_IDLE)) {
            if (!isNotificationEnabled()) {
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            } else {
                startActivity(new Intent(Launcher.this, NotificationActivity.class));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (WatchDials.getWatchDialsStatus() == WatchDials.ANIMATE_STATE_OPENING ||
                WatchDials.getWatchDialsStatus() == WatchDials.ANIMATE_STATE_OPENED) {
            mGestureView.setIsGestureDrag(true);
            closeDials(true);
            mWatchType = LauncherSharedPrefs.getWatchType(Launcher.this);
            mDaialFragment.setDialFromType(mWatchType);
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (WatchDials.getWatchDialsStatus() == WatchDials.ANIMATE_STATE_OPENING ||
                WatchDials.getWatchDialsStatus() == WatchDials.ANIMATE_STATE_OPENED) {
            return;
        }
        int cur = mViewpager.getCurrentItem();
        if (cur == POSITION_MAIN_PAGE) {
            mGestureView.setIsGestureDrag(true);
            openDials();
        }
    }

    @Override
    public void onScreenOn() {
        if (bIsSleeping) {
            changeAirPlaneMode(false);
        }
    }

    @Override
    public void onScreenOff() {
        if (mGestureView != null && mGestureView.getVisibility() == View.VISIBLE && isHome(Launcher.this)
                && mViewpager.getCurrentItem() != POSITION_MAIN_PAGE) {
            mViewpager.setCurrentItem(POSITION_MAIN_PAGE);
        }
        if (bIsSleeping) {
            showSleepView();
            ClassForbidUtils.killRecentTask(this);
            if (!Utils.isAirplaneModeOn(this)) {
                bIsAirPlaneModeChangedBySleep = true;
                changeAirPlaneMode(true);
            } else {
                bIsAirPlaneModeChangedBySleep = false;
            }
        }
    }

    class FragmentAdapter extends FragmentStatePagerAdapter {
        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mFragmentList != null ? mFragmentList.size() : 0;
        }
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WAIT_IRWS:
                    waitIRWS();
                    break;
                default:
                    break;
            }
        }
    };

    private void requestPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                int permissionStatus = checkSelfPermission(permission);
                Log.v(TAG, "permissions=" + permissions + ",permissionStatus=" + permissionStatus);
                if (permissionStatus != PackageManager.PERMISSION_GRANTED/* && shouldShowRequestPermissionRationale(permission)*/) {
                    requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
                    break;
                }
            }
        }
    }

    private boolean isGrantedPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                int permissionStatus = checkSelfPermission(permission);
                if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isNotificationEnabled() {
        String pkgName = this.getPackageName();
        final String flat = Settings.Secure.getString(Launcher.this.getContentResolver(),
                "enabled_notification_listeners");
        Log.d(TAG, "notification flat:" + flat);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void forceUpdateDate() {
        if (mLowDialBaseLayout != null && mLowDialBaseLayout.isShown()) {
            mLowDialBaseLayout.onDateChange();
        }
        if (mLossDialBaseLayout != null && mLossDialBaseLayout.isShown()) {
            mLossDialBaseLayout.onDateChange();
        }
    }

    private void openDials() {
        mWatchDials = WatchDials.fromXml(Launcher.this);
        mGestureView.cancelLongPress();
        mGestureView.addView(mWatchDials);
        mWatchDials.animateOpen();
    }

    private void closeDials(boolean saveMode) {
        if (mWatchDials != null && mWatchDials.isShown()) {
            mWatchDials.animateClose(saveMode);
        }
    }

    private boolean needGoToHome(Context context, int type) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTask = manager.getRunningTasks(1);
        String _pkgName = null;
        String topActivityName = null;
        if (runningTask != null) {
            _pkgName = runningTask.get(0).topActivity.getPackageName();
            topActivityName = runningTask.get(0).topActivity.getClassName();
        } else {
            return false;
        }

        if (_pkgName != null && topActivityName != null) {
            if (type == 1 && "com.android.dialer".equals(_pkgName)) {
                return false;
            } else if ("com.android.dialer".equals(_pkgName) && "com.android.incallui.InCallActivity".equals(topActivityName)) {
                mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                return false;
            } else if ("com.readboy.wearlauncher".equals(_pkgName) && "com.readboy.wearlauncher.Launcher".equals(topActivityName)) {
                return false;
            }
        }
        return true;
    }

    private void goHome() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_IDLE) {
                mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /*boolean isEnable = ((LauncherApplication)
                                LauncherApplication.getApplication()).getWatchController().isNowEnable();*/
                        boolean isEnable = rwm.isClassForbidOpen();
                        Log.e("cwj", "needGoToHome isEnable " + isEnable);
                        if (isEnable) {
//                            startActivity(new Intent(Launcher.this, Launcher.class));
                            goHome();
                            if (isHome(Launcher.this)) {
                                ClassDisableDialog.showClassDisableDialog(Launcher.this);
                            }
                        }
                    }
                }, 500);
            }
        }
    };

    private boolean isHome(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTask = manager.getRunningTasks(1);
        String _pkgName = null;
        String topActivityName = null;
        if (runningTask != null) {
            _pkgName = runningTask.get(0).topActivity.getPackageName();
            topActivityName = runningTask.get(0).topActivity.getClassName();
        }
        if (_pkgName != null && topActivityName != null) {
            if ("com.readboy.wearlauncher".equals(_pkgName) && "com.readboy.wearlauncher.Launcher".equals(topActivityName)) {
                return true;
            }
        }
        return false;
    }

    private void showToast(int text) {
        if (mToast == null) {
            mToast = Toast.makeText(Launcher.this, text, Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            TextView textView = (TextView) mToast.getView().findViewById(android.R.id.message);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        }
        mToast.setText(text);
        mToast.show();
    }

    private void updateViewPager(boolean momentControlled) {
        if (bIsMomentControlled == momentControlled) {
            return;
        }
        bIsMomentControlled = momentControlled;
        mViewpager.setCurrentItem(POSITION_MAIN_PAGE);
        mFragmentList.clear();
        mFragmentList.add(mContactsListFragment);
        mFragmentList.add(mDaialFragment);
        mFragmentList.add(mWetalkFragment);
        if (!momentControlled) {
            mFragmentList.add(mMomentsFragment);
        }
        mFragmentList.add(mCameraFragment);
        mFragmentList.add(mWatchAppFragment);
        mFragmentAdapter.notifyDataSetChanged();
        mViewpager.setOffscreenPageLimit(mFragmentList.size());

        RecyclerView mWetalkRecyclerView = mWetalkFragment.getRecyclerView();
        if (mWetalkRecyclerView != null) {
            OverScrollDecoratorHelper.setUpOverScroll(mWetalkRecyclerView, 0);  //拖拽效果
            bIsWetalkOverScroll = true;
        }
        RecyclerView mContactsRecyclerView = mContactsListFragment.getRecyclerView();
        if (mContactsRecyclerView != null) {
            OverScrollDecoratorHelper.setUpOverScroll(mContactsRecyclerView, 0);  //拖拽效果
            bIsContactsOverScroll = true;
        }
    }

    public void showSleepView() {
        if (mSleepViewStub.getParent() != null) {
            mSleepViewStub.inflate();
            initSleepDialBaseLayout();
        }
        mSleepDialBaseLayout.setVisibility(View.VISIBLE);
    }

    public void hideSleepView() {
        if (mSleepDialBaseLayout != null) {
            mSleepDialBaseLayout.setVisibility(View.GONE);
        }
    }

    public boolean isSleepingModeOpen() {
        int mode = Settings.Global.getInt(getContentResolver(), "sleeping_mode", 0);
        if (mode == 0) {
            return false;
        } else {
            mSleepingMode = mode;
            return true;
        }
    }

    private void setOpenSleepingModeTime() {
        openSleepingModeCalendar = Calendar.getInstance(Locale.CHINA);
        openSleepingModeCalendar.set(Calendar.HOUR_OF_DAY, 22);
        openSleepingModeCalendar.set(Calendar.MINUTE, 0);
        openSleepingModeCalendar.set(Calendar.SECOND, 0);
        openSleepingModeCalendar.set(Calendar.MILLISECOND, 0);
        Log.e("lxx", (openSleepingModeCalendar.getTimeInMillis() - System.currentTimeMillis()) / 3600000
                + "小时" + (openSleepingModeCalendar.getTimeInMillis() - System.currentTimeMillis()) % 3600000 / 60000
                + "分钟后  " + openSleepingModeCalendar.get(Calendar.DAY_OF_MONTH) + "日" + openSleepingModeCalendar.get(Calendar.HOUR_OF_DAY)
                + ":" + openSleepingModeCalendar.get(Calendar.MINUTE) + ":" + openSleepingModeCalendar.get(Calendar.SECOND) + " 开启睡眠模式");
    }

    private void setCloseSleepingModeTime() {
        closeSleepingModeCalendar = Calendar.getInstance(Locale.CHINA);
        //晚上24点前设置为第二天
        if (closeSleepingModeCalendar.get(Calendar.HOUR_OF_DAY) > 21) {
            closeSleepingModeCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        closeSleepingModeCalendar.set(Calendar.HOUR_OF_DAY, 5);
        closeSleepingModeCalendar.set(Calendar.MINUTE, 30);
        closeSleepingModeCalendar.set(Calendar.SECOND, 0);
        closeSleepingModeCalendar.set(Calendar.MILLISECOND, 0);
        Log.e("lxx", (closeSleepingModeCalendar.getTimeInMillis() - System.currentTimeMillis()) / 3600000
                + "小时" + (closeSleepingModeCalendar.getTimeInMillis() - System.currentTimeMillis()) % 3600000 / 60000
                + "分钟后  " + closeSleepingModeCalendar.get(Calendar.DAY_OF_MONTH) + "日" + closeSleepingModeCalendar.get(Calendar.HOUR_OF_DAY)
                + ":" + closeSleepingModeCalendar.get(Calendar.MINUTE) + ":" + closeSleepingModeCalendar.get(Calendar.SECOND) + " 关闭睡眠模式");
    }

    //是否处于22:00～5:30之间
    public boolean isInSleepingTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour > 5 && hour < 22) {
            return false;
        } else if (hour == 5) {
            if (calendar.get(Calendar.MINUTE) < 30) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void changeAirPlaneMode(boolean open) {
        if (!bIsAirPlaneModeChangedBySleep) {
            return;
        }
        if (mSleepingMode == 1) {
            //飞行模式
            Settings.Global.putInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, open ? 1 : 0);
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", open);
            sendBroadcastAsUser(intent, UserHandle.ALL);
        } else {
            //移动数据和wifi
            mTelephonyManager.setDataEnabled(!open);
            if (open) {
                if (mWifiManager.isWifiEnabled()) {
                    bIsWifiOpen = true;
                    mWifiManager.setWifiEnabled(false);
                } else {
                    bIsWifiOpen = false;
                }
            } else if (bIsWifiOpen) {
                mWifiManager.setWifiEnabled(true);
            }
        }
    }
}
