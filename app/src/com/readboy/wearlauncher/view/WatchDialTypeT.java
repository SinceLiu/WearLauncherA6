package com.readboy.wearlauncher.view;

import android.content.Context;
import android.util.AttributeSet;

import com.readboy.wearlauncher.R;

/**
 * TODO: document your custom view class.
 */
public class WatchDialTypeR extends DialBaseLayout {
    private int[] dateDrawable = new int[]{
            R.drawable.dial_r_date_0,
            R.drawable.dial_r_date_1,
            R.drawable.dial_r_date_2,
            R.drawable.dial_r_date_3,
            R.drawable.dial_r_date_4,
            R.drawable.dial_r_date_5,
            R.drawable.dial_r_date_6,
            R.drawable.dial_r_date_7,
            R.drawable.dial_r_date_8,
            R.drawable.dial_r_date_9,
    };
    private int[] weekDrawable = new int[]{
            R.drawable.dial_r_Sunday,
            R.drawable.dial_r_Monday,
            R.drawable.dial_r_Tuesday,
            R.drawable.dial_r_Wednesday,
            R.drawable.dial_r_Thursday,
            R.drawable.dial_r_Friday,
            R.drawable.dial_r_Saturday,
    };
    private DigitClock mDigitClock;

    public WatchDialTypeR(Context context) {
        super(context);
    }

    public WatchDialTypeR(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WatchDialTypeR(Context context, AttributeSet attrs, int defStyle) {
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
        initDateView();
        getDate();
        setDate();
    }

    @Override
    protected void setDate() {
        if (mMonthDecimalIv == null) {
            initDateView();
        }
        if (mMonthDecimal == 0) {
            mMonthDecimalIv.setImageDrawable(null);
        } else {
            mMonthDecimalIv.setImageDrawable(getResources().getDrawable(dateDrawable[mMonthDecimal], null));
        }
        mMonthUnitIv.setImageDrawable(getResources().getDrawable(dateDrawable[mMonthUnit], null));
        mDayDecimalIv.setImageDrawable(getResources().getDrawable(dateDrawable[mDayDecimal], null));
        mDayUnitIv.setImageDrawable(getResources().getDrawable(dateDrawable[mDayUnit], null));
        mWeekIv.setImageDrawable(getResources().getDrawable(weekDrawable[mWeek], null));
    }

}
