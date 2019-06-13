package com.readboy.wearlauncher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.readboy.wearlauncher.R;

/**
 * TODO: document your custom view class.
 */
public class WatchDialTypeQ extends DialBaseLayout {
    private int[] dateDrawable = new int[]{
            R.drawable.dial_q_date_0,
            R.drawable.dial_q_date_1,
            R.drawable.dial_q_date_2,
            R.drawable.dial_q_date_3,
            R.drawable.dial_q_date_4,
            R.drawable.dial_q_date_5,
            R.drawable.dial_q_date_6,
            R.drawable.dial_q_date_7,
            R.drawable.dial_q_date_8,
            R.drawable.dial_q_date_9,
    };
    private int[] weekDrawable = new int[]{
            R.drawable.dial_q_sunday,
            R.drawable.dial_q_monday,
            R.drawable.dial_q_tuesday,
            R.drawable.dial_q_wednesday,
            R.drawable.dial_q_thursday,
            R.drawable.dial_q_friday,
            R.drawable.dial_q_saturday,
    };

    public WatchDialTypeQ(Context context) {
        super(context);
    }

    public WatchDialTypeQ(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WatchDialTypeQ(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onPause() {
        mAnalogClock.setTimePause();
    }

    @Override
    public void onResume() {
        mAnalogClock.setTimeRunning();
    }

    @Override
    public void addChangedCallback() {
//        addCallUnreadChangedCallback();
        addDateChangedCallback();
//        addWeTalkUnreadChangedCallback();
//        mDigitClock.setTimeRunning();
    }

    @Override
    public void setButtonEnable() {
        if (mDialerBtn != null) {
            mDialerBtn.setEnabled(true);
        }
        if (mWetalkBtn != null) {
            mWetalkBtn.setEnabled(true);
        }
        if (mWeatherBtn != null) {
            mWeatherBtn.setEnabled(true);
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
                String num = Integer.toString(unreadNum);
                if (unreadNum > getResources().getInteger(R.integer.unread_num_max)) {
                    num = getResources().getString(R.string.unread_num_max_display);
                    mDialerNum.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            getResources().getDimensionPixelSize(R.dimen.corner_font_text_size_min));
                } else {
                    mDialerNum.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            getResources().getDimensionPixelSize(R.dimen.corner_font_text_size));
                }
                mDialerNum.setText(num);
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
                String num = Integer.toString(unreadNum);
                if (unreadNum > getResources().getInteger(R.integer.unread_num_max)) {
                    num = getResources().getString(R.string.unread_num_max_display);
                    mWeTalkNum.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            getResources().getDimensionPixelSize(R.dimen.corner_font_text_size_min));
                } else {
                    mWeTalkNum.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            getResources().getDimensionPixelSize(R.dimen.corner_font_text_size));
                }
                mWeTalkNum.setText(num);
            } else {
                mWeTalkNum.setVisibility(GONE);
            }
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
        mAnalogClock = (AnalogClock) findViewById(R.id.analog_clock);
        mAnalogClock.setCurTime();
        initDateView();
        getDate();
        setDate();
    }

    @Override
    protected void setDate() {
        if (mMonthDecimalIv == null) {
            return;
        }
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
}
