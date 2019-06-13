package com.readboy.wearlauncher.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.BatteryManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.readboy.wearlauncher.R;

import static android.content.Context.BATTERY_SERVICE;

/**
 * TODO: document your custom view class.
 */
public class WatchDialTypeT extends DialBaseLayout {
    private int[] dateDrawable = new int[]{
            R.drawable.dial_t_date_0,
            R.drawable.dial_t_date_1,
            R.drawable.dial_t_date_2,
            R.drawable.dial_t_date_3,
            R.drawable.dial_t_date_4,
            R.drawable.dial_t_date_5,
            R.drawable.dial_t_date_6,
            R.drawable.dial_t_date_7,
            R.drawable.dial_t_date_8,
            R.drawable.dial_t_date_9,
    };
    private int[] weekDrawable = new int[]{
            R.drawable.dial_t_sunday,
            R.drawable.dial_t_monday,
            R.drawable.dial_t_tuesday,
            R.drawable.dial_t_wednesday,
            R.drawable.dial_t_thursday,
            R.drawable.dial_t_friday,
            R.drawable.dial_t_saturday,
    };

    private int[] weekIcon = new int[]{
            R.drawable.dial_t_sunday_icon,
            R.drawable.dial_t_monday_icon,
            R.drawable.dial_t_tuesday_icon,
            R.drawable.dial_t_wednesday_icon,
            R.drawable.dial_t_thursday_icon,
            R.drawable.dial_t_friday_icon,
            R.drawable.dial_t_saturday_icon,
    };

    private int[] batteryDrawable = new int[]{
            R.drawable.dial_t_battery_0,
            R.drawable.dial_t_battery_1,
            R.drawable.dial_t_battery_2,
            R.drawable.dial_t_battery_3,
            R.drawable.dial_t_battery_4,
            R.drawable.dial_t_battery_5,
            R.drawable.dial_t_battery_6,
            R.drawable.dial_t_battery_7,
            R.drawable.dial_t_battery_8,
            R.drawable.dial_t_battery_9,
    };
    private DigitClock mDigitClock;
    private ImageView mWeekIcon;
    private ImageView mBatteryIv;
    private ImageView mBatteryHundredsIv;
    private ImageView mBatteryDecimalIv;
    private ImageView mBatteryUnitIv;
    private int mLevel;

    private Bitmap bitmap;
    private Bitmap emptyBitmap;
    private Bitmap fullBitmap;
    private Bitmap lowBitmap;
    private int emptyBitmapWidth;
    private int emptyBitmapHeight;
    private Canvas canvas;
    private Rect rect;
    private Paint paint;

    public WatchDialTypeT(Context context) {
        super(context);
    }

    public WatchDialTypeT(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WatchDialTypeT(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    public void onPause() {
        mDigitClock.setTimePause();
    }

    @Override
    public void onResume() {
        mDigitClock.setTimeRunning();
    }

    @Override
    public void addChangedCallback() {
        addDateChangedCallback();
        addBatteryChangedCallback();
        //mDigitClock.setTimeRunning();
    }

    @Override
    public void setButtonEnable() {

    }

    @Override
    public void onStepChange(int step) {

    }

    @Override
    public void onCallUnreadChanged(int unreadNum) {

    }

    @Override
    public void onWeTalkUnreadChanged(int unreadNum) {

    }

    @Override
    public void onBatteryLevelChanged(int level, boolean pluggedIn, boolean charging) {
        if (mLevel != level && level > 0) {
            mLevel = level;
            setBattery();
        }
    }

    @Override
    public void onDateChange() {
        getDate();
        setDate();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDigitClock = (DigitClock) findViewById(R.id.digit_clock);
        mDigitClock.setCurTime();
        mWeekIcon = (ImageView) findViewById(R.id.iv_week_icon);
        mBatteryIv = (ImageView) findViewById(R.id.iv_battery);
        mBatteryHundredsIv = (ImageView) findViewById(R.id.iv_battery_hundreds);
        mBatteryDecimalIv = (ImageView) findViewById(R.id.iv_battery_decimal);
        mBatteryUnitIv = (ImageView) findViewById(R.id.iv_battery_unit);
        initDateView();
        initBattery();
        getDate();
        setDate();
        BatteryManager batteryManager = (BatteryManager) mContext.getSystemService(BATTERY_SERVICE);
        mLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        setBattery();
    }

    @Override
    protected void setDate() {
        if (mMonthDecimalIv == null) {
            return;
        }
        mMonthDecimalIv.setImageDrawable(getResources().getDrawable(dateDrawable[mMonthDecimal], null));
        mMonthUnitIv.setImageDrawable(getResources().getDrawable(dateDrawable[mMonthUnit], null));
        mDayDecimalIv.setImageDrawable(getResources().getDrawable(dateDrawable[mDayDecimal], null));
        mDayUnitIv.setImageDrawable(getResources().getDrawable(dateDrawable[mDayUnit], null));
        mWeekIv.setImageDrawable(getResources().getDrawable(weekDrawable[mWeek], null));
        mWeekIcon.setImageDrawable(getResources().getDrawable(weekIcon[mWeek], null));
    }

    public void initBattery() {
        emptyBitmap = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.battery_nor_empty_t);
        fullBitmap = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.battery_nor_full_t);
        lowBitmap = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.battery_nor_low_t);
        emptyBitmapWidth = emptyBitmap.getWidth();
        emptyBitmapHeight = emptyBitmap.getHeight();
        rect = new Rect(0, 0, 0, emptyBitmapHeight);
        bitmap = Bitmap.createBitmap(emptyBitmapWidth, emptyBitmapHeight, emptyBitmap.getConfig());
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    private Bitmap createBatteryImage(int level) {
        canvas.drawPaint(paint);
        canvas.drawBitmap(emptyBitmap, new Matrix(), null);
        rect.right = (emptyBitmapWidth * 24 / 29) * level / 100 + emptyBitmapWidth * 2 / 29;
        if (level < 20) {
            canvas.drawBitmap(lowBitmap, rect, rect, null);
        } else {
            canvas.drawBitmap(fullBitmap, rect, rect, null);
        }
        return bitmap;
    }

    protected void setBattery() {
        if (mBatteryHundredsIv == null) {
            return;
        }
        int mBatteryHundreds = mLevel / 100;
        int mBatteryDecimal = mLevel / 10;
        int mBatteryUnit = mLevel % 10;
        if (mBatteryHundreds == 1) {
            mBatteryHundredsIv.setVisibility(View.VISIBLE);
            mBatteryDecimalIv.setImageDrawable(getResources().getDrawable(batteryDrawable[0], null));
            mBatteryUnitIv.setImageDrawable(getResources().getDrawable(batteryDrawable[0], null));
        } else {
            mBatteryHundredsIv.setVisibility(View.GONE);
            mBatteryDecimalIv.setImageDrawable(getResources().getDrawable(batteryDrawable[mBatteryDecimal], null));
            mBatteryUnitIv.setImageDrawable(getResources().getDrawable(batteryDrawable[mBatteryUnit], null));
        }
        mBatteryIv.setImageBitmap(createBatteryImage(mLevel));
    }

}
