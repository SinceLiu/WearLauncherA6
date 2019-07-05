/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.readboy.wearlauncher.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.readboy.wearlauncher.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * This widget display an analogic clock with two hands for hours and
 * minutes.
 */
public class AnalogClock extends View {
    private boolean mAttached;
    private Drawable mHourHand;
    private Drawable mMinuteHand;
    private Drawable mSecondHand;

    private float mSeconds;
    private float mMinutes;
    private float mHour;
    private boolean mNoSeconds = false;
    private boolean mNoHour = false;
    private boolean mNominute = false;

    private Paint mDotPaint;
    private int x;
    private int y;

    public AnalogClock(Context context) {
        this(context, null);
    }

    public AnalogClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnalogClock(Context context, AttributeSet attrs,
                       int defStyle) {
        super(context, attrs, defStyle);
        Resources r = context.getResources();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnalogClock);
        mHourHand = a.getDrawable(R.styleable.AnalogClock_analogHour);
        mMinuteHand = a.getDrawable(R.styleable.AnalogClock_analogMinute);
        mSecondHand = a.getDrawable(R.styleable.AnalogClock_analogSecond);
        mNoSeconds = a.getBoolean(R.styleable.AnalogClock_noSecond, false);
        mNoHour = a.getBoolean(R.styleable.AnalogClock_noHour, false);
        mNominute = a.getBoolean(R.styleable.AnalogClock_noMinute, false);

        if (mHourHand == null) {
            mHourHand = r.getDrawable(R.drawable.watch_type_e_analog_hour);
        }
        if (mMinuteHand == null) {
            mMinuteHand = r.getDrawable(R.drawable.watch_type_e_analog_min);
        }
        if (mSecondHand == null) {
            mSecondHand = r.getDrawable(R.drawable.watch_type_e_analog_second);
        }

        final int dotColor = a.getColor(R.styleable.AnalogClock_jewelColor, Color.TRANSPARENT);
        if (dotColor != 0) {
            mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mDotPaint.setColor(dotColor);
        }
        a.recycle();
    }

    private void init() {
        x = getWidth() / 2;
        y = getHeight() / 2;
        int hourHandWidth = mHourHand.getIntrinsicWidth() * 2 * x / getContext().getResources().getDisplayMetrics().widthPixels;
        int hourHandHeight = mHourHand.getIntrinsicHeight() * 2 * y / getContext().getResources().getDisplayMetrics().heightPixels;
        mHourHand.setBounds(x - hourHandWidth / 2, y - hourHandHeight, x + hourHandWidth / 2, y);
        int minuteHandWidth = mMinuteHand.getIntrinsicWidth() * 2 * x / getContext().getResources().getDisplayMetrics().widthPixels;
        int minuteHandHeight = mMinuteHand.getIntrinsicHeight() * 2 * y / getContext().getResources().getDisplayMetrics().heightPixels;
        mMinuteHand.setBounds(x - minuteHandWidth / 2, y - minuteHandHeight, x + minuteHandWidth / 2, y);
        int secondHandWidth = mSecondHand.getIntrinsicWidth() * 2 * x / getContext().getResources().getDisplayMetrics().widthPixels;
        int secondHandHeight = mSecondHand.getIntrinsicHeight() * 2 * y / getContext().getResources().getDisplayMetrics().heightPixels;
        mSecondHand.setBounds(x - secondHandWidth / 2, 0, x + secondHandWidth / 2, secondHandHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mAttached) {
            mAttached = true;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            removeCallbacks(mClockTick);
            mAttached = false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (x == 0) {
            init();
        }
        if (!mNoHour) {
            drawHand(canvas, mHourHand, mHour / 12.0f * 360.0f);
        }
        if (!mNominute) {
            drawHand(canvas, mMinuteHand, mMinutes / 60.0f * 360.0f);
        }
        if (!mNoSeconds) {
            drawHand(canvas, mSecondHand, mSeconds / 60.0f * 360.0f);
        }
    }

    public void recycle() {
        if (mHourHand != null) {
            recyclDrawable(mHourHand);
            mHourHand = null;
        }
        if (mMinuteHand != null) {
            recyclDrawable(mMinuteHand);
            mMinuteHand = null;
        }
        if (mSecondHand != null) {
            recyclDrawable(mSecondHand);
            mSecondHand = null;
        }
        System.gc();
    }

    private void recyclDrawable(Drawable drawable) {
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    private void drawHand(Canvas canvas, Drawable hand, float angle) {
        canvas.save();
        canvas.rotate(angle, x, y);
        hand.draw(canvas);
        canvas.restore();
    }

    private void onTimeChanged() {
        GregorianCalendar time = new GregorianCalendar();
        time.setTime(new Date());
        int hour = time.get(Calendar.HOUR);
        int minute = time.get(Calendar.MINUTE);
        int second = time.get(Calendar.SECOND);
//        int am_pm = time.get(Calendar.AM_PM);

        mSeconds = second;
        mMinutes = minute + second / 60.0f;
        mHour = hour + mMinutes / 60.0f;
        invalidate();
    }

    private final Runnable mClockTick = new Runnable() {

        @Override
        public void run() {
            onTimeChanged();
            Calendar calendar = Calendar.getInstance();
            int currentMillisecond = calendar.get(Calendar.MILLISECOND);
            AnalogClock.this.postDelayed(mClockTick, 1000 - currentMillisecond);
        }
    };

    public void setTimePause() {
        removeCallbacks(mClockTick);
    }

    public void setTimeRunning() {
        removeCallbacks(mClockTick);
        post(mClockTick);
    }

    public void setCurTime() {
        GregorianCalendar time = new GregorianCalendar();
        time.setTime(new Date());
        int hour = time.get(Calendar.HOUR);
        int minute = time.get(Calendar.MINUTE);
        int second = time.get(Calendar.SECOND);
//        int am_pm = time.get(Calendar.AM_PM);
        mSeconds = second;
        mMinutes = minute + second / 60.0f;
        mHour = hour + mMinutes / 60.0f;

        postInvalidate();
    }
}

