package com.readboy.wearlauncher.view;

import android.app.readboy.ReadboyWearManager;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.readboy.wearlauncher.LauncherApplication;
import com.readboy.wearlauncher.R;
import com.readboy.wearlauncher.battery.BatteryController;
import com.readboy.wearlauncher.dialog.ClassDisableDialog;
import com.readboy.wearlauncher.utils.Utils;
import com.readboy.wearlauncher.utils.WatchController;

import java.util.Calendar;

/**
 * 时间、日期、天气（警报）、电话/未接提示泡、微聊/未读微聊信息、计步
 * TODO: document your custom view class.
 */
public abstract class DialBaseLayout extends RelativeLayout implements View.OnClickListener,
        WatchController.DateChangedCallback,
        WatchController.CallUnreadChangedCallback,
        WatchController.StepChangedCallback, WatchController.WeTalkUnreadChangedCallback, WatchController.WeatherChangedCallback,
        BatteryController.BatteryStateChangeCallback {
    //打电话
    public static final String DIALER_PACKAGE_NAME = "com.android.dialer";
    public static final String DIALER_CLASS_NAME = "com.android.dialer.DialtactsActivity";
    //天气
    public static final String WEATHER_PACKAGE_NAME = "com.readboy.wearweather";
    public static final String WEATHER_CLASS_NAME = "com.readboy.wearweather.MainActivity";

    protected LauncherApplication mApplication;
    protected Context mContext;
    protected WatchController mWatchController;
    protected BatteryController mBatteryController;

    TextView mDialerNum;
    TextView mWeTalkNum;
    Button mDialerBtn;
    Button mWetalkBtn;
    Button mWeatherBtn;
    AnalogClock mAnalogClock;
    DigitClock mDigitClock;
    ImageView mWeekIv;
    ImageView mMonthDecimalIv;
    ImageView mMonthUnitIv;
    ImageView mDayDecimalIv;
    ImageView mDayUnitIv;

    /**
     * 日期十位、个位数值
     */
    int mYearDecimal;
    int mYearUnit;
    int mMonthDecimal;
    int mMonthUnit;
    int mDayDecimal;
    int mDayUnit;
    int mWeek;

    public DialBaseLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public DialBaseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public DialBaseLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Load attributes
        mContext = context;
        mApplication = (LauncherApplication) context.getApplicationContext();
        mWatchController = mApplication.getWatchController();
        mBatteryController = new BatteryController(mContext);
    }

    public abstract void addChangedCallback();

    public abstract void setButtonEnable();

    public abstract void onPause();

    public abstract void onResume();

    public void addDateChangedCallback() {
        mWatchController.addDateChangedCallback(this);
    }

    public void addCallUnreadChangedCallback() {
        mWatchController.addCallUnreadChangedCallback(this);
    }

    public void addStepChangedCallback() {
        mWatchController.addStepChangedCallback(this);
    }

    public void addWeTalkUnreadChangedCallback() {
        mWatchController.addWeTalkUnreadChangedCallback(this);
    }

    public void addWeatherChangedCallback() {
        mWatchController.addWeatherChangedCallback(this);
    }

    public void addBatteryChangedCallback(){
        mBatteryController.addStateChangedCallback(this);
    }

    public void removeChangedCallback() {
        mWatchController.removeDateChangedCallback(this);
        mWatchController.removeCallUnreadChangedCallback(this);
        mWatchController.removeWeTalkUnreadChangedCallback(this);
        mWatchController.removeStepChangedCallback(this);
        mWatchController.removeWeatherChangedCallback(this);
        mBatteryController.removeStateChangedCallback(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDialerNum = (TextView) findViewById(R.id.text_id_dialer_num);
        mWeTalkNum = (TextView) findViewById(R.id.text_id_mss_num);
        //按钮点击
        mDialerBtn = (Button) findViewById(R.id.btn_id_dialer);
        mWetalkBtn = (Button) findViewById(R.id.btn_id_mms);
        mWeatherBtn = (Button) findViewById(R.id.btn_id_weather);

        if (mDialerBtn != null) {
            mDialerBtn.setOnClickListener(this);
        }
        if (mWetalkBtn != null) {
            mWetalkBtn.setOnClickListener(this);
        }
        if (mWeatherBtn != null) {
            mWeatherBtn.setOnClickListener(this);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        addChangedCallback();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeChangedCallback();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_id_dialer:
                Utils.startActivity(mContext, DIALER_PACKAGE_NAME, DIALER_CLASS_NAME);
                break;
            case R.id.btn_id_mms:
                ReadboyWearManager rwm = (ReadboyWearManager) mContext.getSystemService(Context.RBW_SERVICE);
                boolean isEnable = rwm.isClassForbidOpen();
                if (isEnable) {
                    ClassDisableDialog.showClassDisableDialog(mContext);
                    return;
                }
                Intent wetalkIntent = new Intent();
                wetalkIntent.setAction("readboy.intent.action.wetalk");
                wetalkIntent.addCategory(Intent.CATEGORY_DEFAULT);
                mContext.startActivity(wetalkIntent);
                break;
            case R.id.btn_id_weather:
                Utils.startActivity(mContext, WEATHER_PACKAGE_NAME, WEATHER_CLASS_NAME);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDateChange() {

    }

    @Override
    public void onCallUnreadChanged(int count) {

    }

    @Override
    public void onStepChange(int step) {

    }

    @Override
    public void onWeTalkUnreadChanged(int count) {

    }

    @Override
    public void onWeatherChanged(String weatherCode) {

    }

    @Override
    public void onBatteryLevelChanged(int level, boolean pluggedIn, boolean charging) {

    }

    @Override
    public void onPowerSaveChanged() {

    }

    protected void setDate() {
        TextView mDateText = (TextView) findViewById(R.id.date_tvid);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        /*int month = calendar.get(Calendar.MONTH) + 1;*/
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int week = (calendar.get(Calendar.DAY_OF_WEEK) - 1) % WatchController.WEEK_NAME_CN_LONG.length;
        //String dateFormat = String.format("%d %s %d",day, WatchController.MONTHS_NAME_EN_SHORT[month],year);
        String dateFormat = String.format("%d/%d  %s", month + 1, day, WatchController.WEEK_NAME_CN_LONG[week]);
        //String dateFormat = String.format("%s, %d  %s",
        //        WatchController.WEEK_NAME_EN_LONG[week],day,WatchController.MONTHS_NAME_EN_LONG[month]);
        mDateText.setText(dateFormat);
    }

    //用ImageView显示日期
    protected void initDateView() {
        mMonthDecimalIv = (ImageView) findViewById(R.id.iv_month_decimal);
        mMonthUnitIv = (ImageView) findViewById(R.id.iv_month_unit);
        mDayDecimalIv = (ImageView) findViewById(R.id.iv_day_decimal);
        mDayUnitIv = (ImageView) findViewById(R.id.iv_day_unit);
        mWeekIv = (ImageView) findViewById(R.id.iv_week);
    }

    protected void getDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mWeek = (calendar.get(Calendar.DAY_OF_WEEK) - 1) % WatchController.WEEK_NAME_CN_LONG.length;
        mYearDecimal = (year - 2000) / 10;
        mYearUnit = year % 10;
        mMonthDecimal = month / 10;
        mMonthUnit = month % 10;
        mDayDecimal = day / 10;
        mDayUnit = day % 10;
    }
}
