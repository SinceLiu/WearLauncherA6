package com.readboy.wearlauncher;

import android.Manifest;
import android.app.ActivityManager;
import android.app.readboy.PersonalInfo;
import android.app.readboy.ReadboyWearManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.ServiceManager;
import android.os.IPowerManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
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
//import com.readboy.pedometer.Fragment.PedometerMainFragment;
//import com.readboy.wearweather.fragment.WeatherMainFragment;
import com.android.dialer.app.readboysupport.fragment.ContactsListFragment;
import com.readboy.mmsupport.fragment.MomentsFragment;

import java.util.ArrayList;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


public class Launcher extends FragmentActivity implements BatteryController.BatteryStateChangeCallback,
        GestureView.MyGestureListener, WatchController.ClassDisableChangedCallback, WatchController.ScreenOff {
    public static final String TAG = Launcher.class.getSimpleName();

    private LauncherApplication mApplication;
    private FragmentManager mFragmentManager;
    private static final int PERMISSIONS_REQUEST_CODE = 0x33;
    public static final int POSITION_MAIN_PAGE = 1;  //表盘屏的位置
    public static final int POSITION_CONTACT_PAGE = POSITION_MAIN_PAGE - 1;
    public static final int POSITION_WETALK_PAGE = POSITION_MAIN_PAGE + 1;
    public static final String ACTION_CLASS_DISABLE_STATUS_CHANGED = "android.intent.action.CLASS_DISABLE_STATUS_CHANGED";
    private static final int TURN_TO_MAIN_PAGE = 0x10;
    private static final int WAIT_IRWS = 0x11;
    private LocalBroadcastManager mLocalBroadcastManager;

    private GestureView mGestureView;
    private DialBaseLayout mLowDialBaseLayout;
    private MyViewPager mViewpager;
    private FragmentAdapter mFragmentAdapter;
    private List<Fragment> mFragmentList;
    private WatchDials mWatchDials;
    //    private PedometerMainFragment mPedometerFragment;
//    private WeatherMainFragment mWeatherFragment;
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
    private boolean bIsClassDisable = false;
    private boolean bIsTouchable = false;
    private boolean bIsWetalkOverScroll;  //可拖拽
    private boolean bIsContactsOverScroll;
    private boolean bIsSpi;
    private TelephonyManager mTelephonyManager;
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
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Log.e("lxx",dm.toString());
        //screen width:240、height:240,density:0.75,densityDpi:120

        mViewpager = (MyViewPager) findViewById(R.id.viewpager);
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mFragmentManager = getSupportFragmentManager();
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

        mLowDialBaseLayout = (DialBaseLayout) findViewById(R.id.low);
        mLowDialBaseLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mToast == null) {
                        mToast = Toast.makeText(Launcher.this, R.string.notice_low_power_for_phone, Toast.LENGTH_SHORT);
                        mToast.setGravity(Gravity.CENTER, 0, 0);
                        TextView textView = (TextView) mToast.getView().findViewById(android.R.id.message);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                    }
                    mToast.setText(R.string.notice_low_power_for_phone);
                    mToast.show();
                }
                return true;
            }
        });
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
                if (bIsClassDisable && position != POSITION_MAIN_PAGE) {
                    mHandler.removeMessages(TURN_TO_MAIN_PAGE);
                    mHandler.sendEmptyMessageDelayed(TURN_TO_MAIN_PAGE, 1000 * 2);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mViewPagerScrollState = state;
            }
        });
        mFragmentList = new ArrayList<Fragment>();
        mFragmentList.clear();

//        mWeatherFragment = new WeatherMainFragment();
//        mFragmentList.add(mWeatherFragment);
//        mPedometerFragment = new PedometerMainFragment();
//        mFragmentList.add(mPedometerFragment);
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
        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), mFragmentList);
        mViewpager.setAdapter(mFragmentAdapter);
        mViewpager.setCurrentItem(POSITION_MAIN_PAGE);
        mViewpager.setOffscreenPageLimit(mFragmentList.size() + 1);
        OverScrollDecoratorHelper.setUpOverScroll(mViewpager);
        mWatchController.setScreenOffListener(this);
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

    //等待IReadboyWearService
    public void waitIRWS() {
        if (rwm.getPersonalInfo() != null && mBatteryLevel != -1) {
            if (rwm.isClassForbidOpen()) {
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
        // add by divhee start
        if (mViewpager != null && mViewpager.getAdapter() != null) {
            mViewpager.getAdapter().notifyDataSetChanged();
        }
        // add by divhee end
        bIsTouchable = true;
        LauncherApplication.setTouchEnable(true);
        requestPermissions(sPermissions);
        forceUpdateDate();
        dialResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bIsTouchable = false;
        closeDials(false);
        dialPasue();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy() ");
        mWatchController.removeClassDisableChangedCallback(this);
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
    public void onClassDisableChange(boolean show) {
        Log.e("cwj", "onClassDisableChange: show=" + show);
        if (bIsClassDisable != show) {
            bIsClassDisable = show;
            mViewpager.setClassDisabled(show);
            ClassForbidUtils.handleClassForbid(bIsClassDisable, Launcher.this);
//            ReadboyWearManager rwm = (ReadboyWearManager)Launcher.this.getSystemService(Context.RBW_SERVICE);
//            rwm.setClassForbidOpen(show);
            if (bIsClassDisable) {
                closeDials(false);
                if (needGoToHome(Launcher.this, 0)) {
//                    startActivity(new Intent(Launcher.this, Launcher.class));
                    goHome();
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (needGoToHome(Launcher.this, 0)) {
//                            startActivity(new Intent(Launcher.this, Launcher.class));
                            goHome();
                        }
                    }
                }, 500);
                if (mGestureView != null && mGestureView.getVisibility() == View.VISIBLE
                        && mViewpager.getCurrentItem() != POSITION_MAIN_PAGE) {
                    mViewpager.setCurrentItem(POSITION_MAIN_PAGE);
                }
                mViewpager.scrollTo(0, 0);
                mViewPagerScrollState = ViewPager.SCROLL_STATE_IDLE;
                if (isHome(Launcher.this)) {
                    ClassDisableDialog.showClassDisableDialog(Launcher.this);
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Utils.checkAndDealWithAirPlanMode(Launcher.this);
                    }
                }, 1000);
            } else {
                mHandler.removeMessages(TURN_TO_MAIN_PAGE);
            }
            //通知通知栏
            Intent intent = new Intent(ACTION_CLASS_DISABLE_STATUS_CHANGED);
            mLocalBroadcastManager.sendBroadcast(intent);
        }
    }

    int lowPowerLevel = 15;

    @Override
    public void onBatteryLevelChanged(int level, boolean pluggedIn, boolean charging) {
        if (mGestureView == null || mLowDialBaseLayout == null) {
            return;
        }
        if (mBatteryLevel == -1 || mBatteryLevel != level) {
            PowerManager mPowerManager = (PowerManager) Launcher.this.getSystemService(Context.POWER_SERVICE);
            if (level < lowPowerLevel) {//low powe
                mGestureView.setVisibility(View.GONE);
                mLowDialBaseLayout.setVisibility(View.VISIBLE);
                mLowDialBaseLayout.addChangedCallback();
                mLowDialBaseLayout.onResume();
                mLowDialBaseLayout.setButtonEnable();
                if (mBatteryLevel >= lowPowerLevel) {
                    rwm.setLowPowerMode(true);
                    mPowerManager.setPowerSaveMode(true);
                    ClassForbidUtils.killRecentTask(Launcher.this);
                }
                if (needGoToHome(Launcher.this, 1)) {
//                    startActivity(new Intent(Launcher.this, Launcher.class));
                    goHome();
                }
            } else {
                mLowDialBaseLayout.setVisibility(View.GONE);
                mGestureView.setVisibility(View.VISIBLE);
                rwm.setLowPowerMode(false);
                if (mPowerManager.isPowerSaveMode()) {
                    mPowerManager.setPowerSaveMode(false);
                }
            }
            mBatteryLevel = level;
        } else {
            PowerManager mPowerManager = (PowerManager) Launcher.this.getSystemService(Context.POWER_SERVICE);
            if (level < lowPowerLevel && !mPowerManager.isPowerSaveMode()) {
                if (!rwm.isLowPowerMode()) {
                    rwm.setLowPowerMode(true);
                    ClassForbidUtils.killRecentTask(Launcher.this);
                }
                mPowerManager.setPowerSaveMode(true);
            } else if (level >= lowPowerLevel && mPowerManager.isPowerSaveMode()) {
                rwm.setLowPowerMode(false);
                mPowerManager.setPowerSaveMode(false);
            }
        }
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
                Log.e(TAG, "onFling: start Speech activity.");
                Utils.startActivity(Launcher.this, "com.readboy.watch.speech", "com.readboy.watch.speech.Main2Activity");
            }
//            else {
//                Utils.startActivity(Launcher.this, "com.android.settings", "com.android.qrcode.MainActivity");
//            }
            return true;
        } else if (vDistance < -mTouchSlopSquare / 2 && bVerticalMove && (bIsSpi || mViewPagerScrollState == ViewPager.SCROLL_STATE_IDLE)) {
            /*boolean isEnable = ((LauncherApplication) LauncherApplication.getApplication()).getWatchController().isNowEnable();*/

//            ReadboyWearManager rwm = (ReadboyWearManager) Launcher.this.getSystemService(Context.RBW_SERVICE);
//            boolean isEnable = rwm.isClassForbidOpen();
//            if (isEnable) {
//                ClassDisableDialog.showClassDisableDialog(Launcher.this);
//                Utils.checkAndDealWithAirPlanMode(Launcher.this);
//                return true;
//            }
            if (!isNotificationEnabled()) {
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            } else {
                startActivity(new Intent(Launcher.this, NotificationActivity.class));
                //Utils.startActivity(Launcher.this, "com.readboy.wearlauncher",NotificationActivity.class.getName());
                //((Launcher)getActivity()).switchToFragment(NotificationFragment.class.getName(),null,true,true);
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
        if (mBatteryLevel != -1 && mBatteryLevel < lowPowerLevel) {
            mLowDialBaseLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onScreenOff() {
        if (mGestureView != null && mGestureView.getVisibility() == View.VISIBLE && isHome(Launcher.this)
                && mViewpager.getCurrentItem() != POSITION_MAIN_PAGE) {
            mViewpager.setCurrentItem(POSITION_MAIN_PAGE);
        }
        mLowDialBaseLayout.setVisibility(View.GONE);   //灭屏后不刷新低电电量
    }

    class FragmentAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList;

        public FragmentAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            mFragmentList = list;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
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
                case TURN_TO_MAIN_PAGE:
                    if (mGestureView != null && mGestureView.getVisibility() == View.VISIBLE && mViewpager.getCurrentItem() != POSITION_MAIN_PAGE) {
                        mViewpager.setCurrentItem(POSITION_MAIN_PAGE);
                    }
                    break;
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
    }

    private void dialPasue() {
        if (mLowDialBaseLayout != null && mLowDialBaseLayout.isShown()) {
            mLowDialBaseLayout.onPause();
            mLowDialBaseLayout.removeChangedCallback();
        }
    }

    private void dialResume() {
        if (mLowDialBaseLayout != null && mLowDialBaseLayout.isShown()) {
            mLowDialBaseLayout.onResume();
            mLowDialBaseLayout.addChangedCallback();
        }
    }

    private void forceCloseWakeLock() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
        synchronized (TAG) {
            if (wakeLock != null) {
                Log.v(TAG, "Releasing wakelock");
                try {
                    wakeLock.release();
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            } else {
                Log.e(TAG, "Wakelock reference is null");
            }
        }
    }

    public void startPowerAnimService() {
        Intent intent = new Intent();
        ComponentName component = new ComponentName("com.readboy.floatwindow", "com.readboy.floatwindow.FloatWindowService");
        intent.setComponent(component);
        Intent tmp = Utils.createExplicitFromImplicitIntent(Launcher.this, intent);
        if (tmp != null) {
            try {
                Intent eintent = new Intent(tmp);
                Launcher.this.startService(eintent);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            if (type == 1 && _pkgName.equals("com.android.dialer")) {
                return false;
            } else if (_pkgName.equals("com.android.dialer") && topActivityName.equals("com.android.incallui.InCallActivity")) {
                mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                return false;
            } else if (_pkgName.equals("com.readboy.wearlauncher") && topActivityName.equals("com.readboy.wearlauncher.Launcher")) {
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
            if (_pkgName.equals("com.readboy.wearlauncher") && topActivityName.equals("com.readboy.wearlauncher.Launcher")) {
                return true;
            }
        }
        return false;
    }

    private Fragment getVisibleFragment(FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null) {
            return null;
        }
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible()
                    && fragment.getUserVisibleHint()) {
                return fragment;
            }
        }

        return null;
    }

    public Fragment switchToFragment(String fragmentName, Bundle args,
                                     boolean addToBackStack, boolean withTransition) {

        Fragment f = Fragment.instantiate(this, fragmentName, args);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
//        if (withTransition) {
//            transaction.setCustomAnimations(R.anim.push_in_right,
//                    R.anim.push_out_right, R.anim.back_in_left,
//                    R.anim.back_out_left);
//        }
        if (addToBackStack) {
            transaction.addToBackStack(fragmentName.getClass().getSimpleName());
        }
        transaction.replace(R.id.content_container, f, fragmentName);
        transaction.commitAllowingStateLoss();
        mFragmentManager.executePendingTransactions();
        return f;
    }
}
