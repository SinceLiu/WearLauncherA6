package com.readboy.wearlauncher.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.readboy.wearlauncher.R;

import java.util.Calendar;

public class DigitClock extends LinearLayout {

    private Context mContext;
    private ImageView hourImage0;
    private ImageView hourImage1;
    private ImageView dotImage;
    private ImageView minImage0;
    private ImageView minImage1;

    private int mMilliSeconds;
    private int mMinutes;
    private int mHour;
    private int dialType = 0;
    private boolean hasDot = true;

    private int[] clockDrawable_normal = new int[]{
            R.drawable.num_clock_g_0,
            R.drawable.num_clock_g_1,
            R.drawable.num_clock_g_2,
            R.drawable.num_clock_g_3,
            R.drawable.num_clock_g_4,
            R.drawable.num_clock_g_5,
            R.drawable.num_clock_g_6,
            R.drawable.num_clock_g_7,
            R.drawable.num_clock_g_8,
            R.drawable.num_clock_g_9
    };

    private int[] clockDrawable_type_g = new int[]{
            R.drawable.num_clock_g_0,
            R.drawable.num_clock_g_1,
            R.drawable.num_clock_g_2,
            R.drawable.num_clock_g_3,
            R.drawable.num_clock_g_4,
            R.drawable.num_clock_g_5,
            R.drawable.num_clock_g_6,
            R.drawable.num_clock_g_7,
            R.drawable.num_clock_g_8,
            R.drawable.num_clock_g_9
    };

    //type o
    private int[] clockDrawable_type_o = new int[]{
            R.drawable.num_clock_o_0,
            R.drawable.num_clock_o_1,
            R.drawable.num_clock_o_2,
            R.drawable.num_clock_o_3,
            R.drawable.num_clock_o_4,
            R.drawable.num_clock_o_5,
            R.drawable.num_clock_o_6,
            R.drawable.num_clock_o_7,
            R.drawable.num_clock_o_8,
            R.drawable.num_clock_o_9
    };

    //type p
    private int[] clockDrawable_type_p = new int[]{
            R.drawable.num_clock_p_0,
            R.drawable.num_clock_p_1,
            R.drawable.num_clock_p_2,
            R.drawable.num_clock_p_3,
            R.drawable.num_clock_p_4,
            R.drawable.num_clock_p_5,
            R.drawable.num_clock_p_6,
            R.drawable.num_clock_p_7,
            R.drawable.num_clock_p_8,
            R.drawable.num_clock_p_9
    };

    //type r
    private int[] clockDrawable_type_r = new int[]{
            R.drawable.num_clock_r_0,
            R.drawable.num_clock_r_1,
            R.drawable.num_clock_r_2,
            R.drawable.num_clock_r_3,
            R.drawable.num_clock_r_4,
            R.drawable.num_clock_r_5,
            R.drawable.num_clock_r_6,
            R.drawable.num_clock_r_7,
            R.drawable.num_clock_r_8,
            R.drawable.num_clock_r_9
    };

    //type s
    private int[] clockDrawable_type_s = new int[]{
            R.drawable.num_clock_s_0,
            R.drawable.num_clock_s_1,
            R.drawable.num_clock_s_2,
            R.drawable.num_clock_s_3,
            R.drawable.num_clock_s_4,
            R.drawable.num_clock_s_5,
            R.drawable.num_clock_s_6,
            R.drawable.num_clock_s_7,
            R.drawable.num_clock_s_8,
            R.drawable.num_clock_s_9
    };

    //type t
    private int[] clockDrawable_type_t_hour = new int[]{
            R.drawable.num_clock_t_hour_0,
            R.drawable.num_clock_t_hour_1,
            R.drawable.num_clock_t_hour_2,
            R.drawable.num_clock_t_hour_3,
            R.drawable.num_clock_t_hour_4,
            R.drawable.num_clock_t_hour_5,
            R.drawable.num_clock_t_hour_6,
            R.drawable.num_clock_t_hour_7,
            R.drawable.num_clock_t_hour_8,
            R.drawable.num_clock_t_hour_9
    };
    private int[] clockDrawable_type_t_minute = new int[]{
            R.drawable.num_clock_t_minute_0,
            R.drawable.num_clock_t_minute_1,
            R.drawable.num_clock_t_minute_2,
            R.drawable.num_clock_t_minute_3,
            R.drawable.num_clock_t_minute_4,
            R.drawable.num_clock_t_minute_5,
            R.drawable.num_clock_t_minute_6,
            R.drawable.num_clock_t_minute_7,
            R.drawable.num_clock_t_minute_8,
            R.drawable.num_clock_t_minute_9
    };

    //type u
    private int[] clockDrawable_type_u_hour = new int[]{
            R.drawable.num_clock_u_hour_0,
            R.drawable.num_clock_u_hour_1,
            R.drawable.num_clock_u_hour_2,
            R.drawable.num_clock_u_hour_3,
            R.drawable.num_clock_u_hour_4,
            R.drawable.num_clock_u_hour_5,
            R.drawable.num_clock_u_hour_6,
            R.drawable.num_clock_u_hour_7,
            R.drawable.num_clock_u_hour_8,
            R.drawable.num_clock_u_hour_9
    };
    private int[] clockDrawable_type_u_minute = new int[]{
            R.drawable.num_clock_u_minute_0,
            R.drawable.num_clock_u_minute_1,
            R.drawable.num_clock_u_minute_2,
            R.drawable.num_clock_u_minute_3,
            R.drawable.num_clock_u_minute_4,
            R.drawable.num_clock_u_minute_5,
            R.drawable.num_clock_u_minute_6,
            R.drawable.num_clock_u_minute_7,
            R.drawable.num_clock_u_minute_8,
            R.drawable.num_clock_u_minute_9
    };

    public DigitClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        mContext = context;


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DigitClock);

        dialType = a.getInteger(R.styleable.DigitClock_clockType, 0);
        hasDot = a.getBoolean(R.styleable.DigitClock_dot, true);
        a.recycle();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mClockTick);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        try {
            hourImage0 = (ImageView) findViewById(R.id.hour0);
            hourImage1 = (ImageView) findViewById(R.id.hour1);
            dotImage = (ImageView) findViewById(R.id.dot);
            minImage0 = (ImageView) findViewById(R.id.min0);
            minImage1 = (ImageView) findViewById(R.id.min1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateClock() {
        int TYPE_A = mContext.getResources().getInteger(R.integer.dial_type_a);
        int TYPE_D = mContext.getResources().getInteger(R.integer.dial_type_d);
        int TYPE_G = mContext.getResources().getInteger(R.integer.dial_type_g);
        int TYPE_H = mContext.getResources().getInteger(R.integer.dial_type_h);
        int TYPE_J = mContext.getResources().getInteger(R.integer.dial_type_j);
        int TYPE_K = mContext.getResources().getInteger(R.integer.dial_type_k);
        int TYPE_M = mContext.getResources().getInteger(R.integer.dial_type_m);
        int TYPE_N = mContext.getResources().getInteger(R.integer.dial_type_n);
        int TYPE_O = mContext.getResources().getInteger(R.integer.dial_type_o);
        int TYPE_P = mContext.getResources().getInteger(R.integer.dial_type_p);
        int TYPE_R = mContext.getResources().getInteger(R.integer.dial_type_r);
        int TYPE_S = mContext.getResources().getInteger(R.integer.dial_type_s);
        int TYPE_T = mContext.getResources().getInteger(R.integer.dial_type_t);
        int TYPE_U = mContext.getResources().getInteger(R.integer.dial_type_u);

        int currentHour = mHour;
        int currentMinute = mMinutes;
        int currentMillisecond = mMilliSeconds;

        int hour0 = currentHour / 10;
        int hour1 = currentHour % 10;
        int min0 = currentMinute / 10;
        int min1 = currentMinute % 10;
         if (dialType == TYPE_G) {
            hourImage0.setBackgroundResource(clockDrawable_type_g[hour0]);
            hourImage1.setBackgroundResource(clockDrawable_type_g[hour1]);
            minImage0.setBackgroundResource(clockDrawable_type_g[min0]);
            minImage1.setBackgroundResource(clockDrawable_type_g[min1]);
        } else if (dialType == TYPE_O) {
            hourImage0.setBackgroundResource(clockDrawable_type_o[hour0]);
            hourImage1.setBackgroundResource(clockDrawable_type_o[hour1]);
            minImage0.setBackgroundResource(clockDrawable_type_o[min0]);
            minImage1.setBackgroundResource(clockDrawable_type_o[min1]);
            dotImage.setBackgroundResource(R.drawable.num_clock_o_dot);
        } else if (dialType == TYPE_P) {
            hourImage0.setBackgroundResource(clockDrawable_type_p[hour0]);
            hourImage1.setBackgroundResource(clockDrawable_type_p[hour1]);
            minImage0.setBackgroundResource(clockDrawable_type_p[min0]);
            minImage1.setBackgroundResource(clockDrawable_type_p[min1]);
            dotImage.setBackgroundResource(R.drawable.num_clock_p_dot);
        } else if (dialType == TYPE_R) {
            hourImage0.setBackgroundResource(clockDrawable_type_r[hour0]);
            hourImage1.setBackgroundResource(clockDrawable_type_r[hour1]);
            minImage0.setBackgroundResource(clockDrawable_type_r[min0]);
            minImage1.setBackgroundResource(clockDrawable_type_r[min1]);
        } else if (dialType == TYPE_S) {
            hourImage0.setBackgroundResource(clockDrawable_type_s[hour0]);
            hourImage1.setBackgroundResource(clockDrawable_type_s[hour1]);
            minImage0.setBackgroundResource(clockDrawable_type_s[min0]);
            minImage1.setBackgroundResource(clockDrawable_type_s[min1]);
            dotImage.setBackgroundResource(R.drawable.num_clock_s_dot);
        } else if (dialType == TYPE_T) {
            hourImage0.setBackgroundResource(clockDrawable_type_t_hour[hour0]);
            hourImage1.setBackgroundResource(clockDrawable_type_t_hour[hour1]);
            minImage0.setBackgroundResource(clockDrawable_type_t_minute[min0]);
            minImage1.setBackgroundResource(clockDrawable_type_t_minute[min1]);
        } else if (dialType == TYPE_U) {
            hourImage0.setBackgroundResource(clockDrawable_type_u_hour[hour0]);
            hourImage1.setBackgroundResource(clockDrawable_type_u_hour[hour1]);
            minImage0.setBackgroundResource(clockDrawable_type_u_minute[min0]);
            minImage1.setBackgroundResource(clockDrawable_type_u_minute[min1]);
            dotImage.setBackgroundResource(R.drawable.num_clock_u_dot);
        } else {
            hourImage0.setBackgroundResource(clockDrawable_normal[hour0]);
            hourImage1.setBackgroundResource(clockDrawable_normal[hour1]);
            minImage0.setBackgroundResource(clockDrawable_normal[min0]);
            minImage1.setBackgroundResource(clockDrawable_normal[min1]);
            dotImage.setBackgroundResource(R.drawable.num_clock_u_dot);
        }

//        if (hasDot) {
//            if (currentMillisecond < 500) {
//                dotImage.setVisibility(VISIBLE);
//            } else {
//                dotImage.setVisibility(INVISIBLE);
//            }
//        }
    }

    public void setCurTime() {
        Calendar calendar = Calendar.getInstance();
        if (DateFormat.is24HourFormat(mContext)) {
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
        } else {
            mHour = calendar.get(Calendar.HOUR);
        }
        mMinutes = calendar.get(Calendar.MINUTE);
        mMilliSeconds = calendar.get(Calendar.MILLISECOND);
        mMilliSeconds = 0;
        updateClock();
    }

    public void setTimePause() {
        removeCallbacks(mClockTick);
    }

    public void setTimeRunning() {
        removeCallbacks(mClockTick);
        post(mClockTick);
    }

    private final Runnable mClockTick = new Runnable() {

        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance();
            if (DateFormat.is24HourFormat(mContext)) {
                mHour = calendar.get(Calendar.HOUR_OF_DAY);
            } else {
                mHour = calendar.get(Calendar.HOUR);
            }
            mMinutes = calendar.get(Calendar.MINUTE);
            mMilliSeconds = calendar.get(Calendar.MILLISECOND);
            int currentMillisecond = calendar.get(Calendar.MILLISECOND);
            updateClock();
            DigitClock.this.postDelayed(mClockTick, 1000 - currentMillisecond);
        }
    };
}
