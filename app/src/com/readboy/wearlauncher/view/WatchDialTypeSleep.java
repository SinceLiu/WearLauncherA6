package com.readboy.wearlauncher.view;

import android.content.Context;
import android.util.AttributeSet;
import com.readboy.wearlauncher.R;

/**
 * 睡眠界面
 */
public class WatchDialTypeSleep extends DialBaseLayout {

    public WatchDialTypeSleep(Context context) {
        super(context);
    }

    public WatchDialTypeSleep(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WatchDialTypeSleep(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if (isVisible) {
            mDigitClock.setTimeRunning();
            addChangedCallback();
        } else {
            mDigitClock.setTimePause();
            removeChangedCallback();
        }
    }

    @Override
    public void onPause() {
//        mDigitClock.setTimePause();
    }

    @Override
    public void onResume() {
//        mDigitClock.setTimeRunning();
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
        setTime();
    }

    private void setTime() {
        mDigitClock.setCurTime();
    }

}
