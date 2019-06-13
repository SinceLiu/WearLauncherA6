package com.readboy.wearlauncher.view;

import android.app.readboy.ReadboyWearManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;

import com.readboy.wearlauncher.R;
import com.readboy.wearlauncher.dialog.ClassDisableDialog;
import com.readboy.wearlauncher.utils.Utils;

import static android.content.Context.BATTERY_SERVICE;

/**
 * TODO: document your custom view class.
 */
public class WatchDialTypeU extends DialBaseLayout {
    private int[] dateDrawable = new int[]{
            R.drawable.dial_u_date_0,
            R.drawable.dial_u_date_1,
            R.drawable.dial_u_date_2,
            R.drawable.dial_u_date_3,
            R.drawable.dial_u_date_4,
            R.drawable.dial_u_date_5,
            R.drawable.dial_u_date_6,
            R.drawable.dial_u_date_7,
            R.drawable.dial_u_date_8,
            R.drawable.dial_u_date_9,
    };
    private int[] weekDrawable = new int[]{
            R.drawable.dial_u_sunday,
            R.drawable.dial_u_monday,
            R.drawable.dial_u_tuesday,
            R.drawable.dial_u_wednesday,
            R.drawable.dial_u_thursday,
            R.drawable.dial_u_friday,
            R.drawable.dial_u_saturday,
    };

    private DigitClock mDigitClock;
    private ImageView mYearDecimalIv;
    private ImageView mYearUnitIv;
    private ImageView mBatteryIv;
    private ImageButton mDialerIB;
    private ImageButton mWetalkIB;
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

    public WatchDialTypeU(Context context) {
        super(context);
    }

    public WatchDialTypeU(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WatchDialTypeU(Context context, AttributeSet attrs, int defStyle) {
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
        addCallUnreadChangedCallback();
        addDateChangedCallback();
        addWeTalkUnreadChangedCallback();
        addBatteryChangedCallback();
//        mDigitClock.setTimeRunning();
    }

    @Override
    public void setButtonEnable() {
        if (mDialerIB != null) {
            mDialerIB.setEnabled(true);
        }
        if (mWetalkIB != null) {
            mWetalkIB.setEnabled(true);
        }
    }

    @Override
    public void onStepChange(int step) {

    }

    @Override
    public void onCallUnreadChanged(int unreadNum) {
        if (mDialerNum != null) {
            if (unreadNum > 0) {
                mDialerNum.setVisibility(VISIBLE);
            } else {
                mDialerNum.setVisibility(GONE);
            }
        }
    }

    @Override
    public void onWeTalkUnreadChanged(int unreadNum) {
        if (mWeTalkNum != null) {
            if (unreadNum > 0) {
                mWeTalkNum.setVisibility(VISIBLE);
            } else {
                mWeTalkNum.setVisibility(GONE);
            }
        }
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
        mYearDecimalIv = (ImageView) findViewById(R.id.iv_year_decimal);
        mYearUnitIv = (ImageView) findViewById(R.id.iv_year_unit);
        mBatteryIv = (ImageView) findViewById(R.id.iv_battery);
        mDialerIB = (ImageButton) findViewById(R.id.ib_id_dialer);
        mWetalkIB = (ImageButton) findViewById(R.id.ib_id_mms);
        mDialerIB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startActivity(mContext, DIALER_PACKAGE_NAME, DIALER_CLASS_NAME);
            }
        });
        mWetalkIB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        mDialerIB.setEnabled(false);
        mWetalkIB.setEnabled(false);
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
        mYearDecimalIv.setImageDrawable(getResources().getDrawable(dateDrawable[mYearDecimal], null));
        mYearUnitIv.setImageDrawable(getResources().getDrawable(dateDrawable[mYearUnit], null));
        if (mMonthDecimal == 0) {
            mMonthDecimalIv.setVisibility(View.GONE);
        } else {
            mMonthDecimalIv.setVisibility(View.VISIBLE);
            mMonthDecimalIv.setImageDrawable(getResources().getDrawable(dateDrawable[mMonthDecimal], null));
        }
        mMonthUnitIv.setImageDrawable(getResources().getDrawable(dateDrawable[mMonthUnit], null));
        if (mDayDecimal == 0) {
            mDayDecimalIv.setVisibility(View.GONE);
        } else {
            mDayDecimalIv.setVisibility(View.VISIBLE);
            mDayDecimalIv.setImageDrawable(getResources().getDrawable(dateDrawable[mDayDecimal], null));
        }
        mDayUnitIv.setImageDrawable(getResources().getDrawable(dateDrawable[mDayUnit], null));
        mWeekIv.setImageDrawable(getResources().getDrawable(weekDrawable[mWeek], null));
    }

    public void initBattery() {
        emptyBitmap = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.battery_nor_empty_u);
        fullBitmap = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.battery_nor_full_u);
        lowBitmap = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.battery_nor_low_u);
        emptyBitmapWidth = emptyBitmap.getWidth();
        emptyBitmapHeight = emptyBitmap.getHeight();
        rect = new Rect(0, 0, emptyBitmapWidth, emptyBitmapHeight);
        bitmap = Bitmap.createBitmap(emptyBitmapWidth, emptyBitmapHeight, emptyBitmap.getConfig());
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    private Bitmap createBatteryImage(int level) {
        canvas.drawPaint(paint);
        canvas.drawBitmap(emptyBitmap, new Matrix(), null);
        rect.left = emptyBitmapWidth - (emptyBitmapWidth * 23 / 28) * level / 100 - emptyBitmapWidth * 3 / 28;
        if (level < 20) {
            canvas.drawBitmap(lowBitmap, rect, rect, null);
        } else {
            canvas.drawBitmap(fullBitmap, rect, rect, null);
        }
        return bitmap;
    }

    protected void setBattery() {
        if (mBatteryIv == null) {
            return;
        }
        mBatteryIv.setImageBitmap(createBatteryImage(mLevel));
    }

}
