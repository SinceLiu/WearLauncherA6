package com.readboy.wearlauncher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.readboy.wearlauncher.R;

/**
 * TODO: document your custom view class.
 */
public class WatchDialTypeS extends DialBaseLayout {
    private int[] dateDrawable = new int[]{
            R.drawable.dial_s_date_0,
            R.drawable.dial_s_date_1,
            R.drawable.dial_s_date_2,
            R.drawable.dial_s_date_3,
            R.drawable.dial_s_date_4,
            R.drawable.dial_s_date_5,
            R.drawable.dial_s_date_6,
            R.drawable.dial_s_date_7,
            R.drawable.dial_s_date_8,
            R.drawable.dial_s_date_9,
    };
    private int[] weekDrawable = new int[]{
            R.drawable.dial_s_sunday,
            R.drawable.dial_s_monday,
            R.drawable.dial_s_tuesday,
            R.drawable.dial_s_wednesday,
            R.drawable.dial_s_thursday,
            R.drawable.dial_s_friday,
            R.drawable.dial_s_saturday,
    };
    private DigitClock mDigitClock;

    public WatchDialTypeS(Context context) {
        super(context);
    }

    public WatchDialTypeS(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WatchDialTypeS(Context context, AttributeSet attrs, int defStyle) {
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
        mDigitClock = (DigitClock) findViewById(R.id.digit_clock);
        mDigitClock.setCurTime();
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
