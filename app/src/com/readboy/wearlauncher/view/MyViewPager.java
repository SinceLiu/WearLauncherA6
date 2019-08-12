package com.readboy.wearlauncher.view;

import android.content.Context;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.readboy.wearlauncher.Launcher;
import com.readboy.wearlauncher.dialog.ClassDisableDialog;
import com.readboy.wearlauncher.utils.Utils;

public class MyViewPager extends ViewPager {
    private boolean isClassDisabled;
    private float mLastX;
    private float mLastY;
    private float dirX;
    private float dirY;
    private int currentPosition;
    private boolean isSpi;
    private int width;

    public MyViewPager(Context context) {
        this(context, null);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        isSpi = Settings.System.getInt(context.getContentResolver(), "is_spi", 0) == 1;
        width = context.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = ev.getX();
                mLastY = ev.getY();
                break;
            default:
                break;
        }
        if (isClassDisabled) {
            return false;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isClassDisabled && !isSpi) {
            return super.onTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                dirX = ev.getX() - mLastX;
                currentPosition = getCurrentItem();
                if (currentPosition == Launcher.POSITION_MAIN_PAGE) {
                    dirY = ev.getY() - mLastY;
                    if (Math.abs(dirY) > Math.abs(dirX)) {
                        break;
                    }
                }
                if (dirX < 0) {
                    if (isClassDisabled) {
                        ClassDisableDialog.showClassDisableDialog(getContext());
                    } else if (currentPosition < getAdapter().getCount() - 1) {
                        setCurrentItem(currentPosition + 1, false);
                    } else {
                        scrollTo((currentPosition - Launcher.POSITION_MAIN_PAGE) * width, 0);
                    }
                } else if (dirX > 0) {
                    if (isClassDisabled) {
                        ClassDisableDialog.showClassDisableDialog(getContext());
                    } else if (currentPosition > 0) {
                        setCurrentItem(currentPosition - 1, false);
                    } else {
                        scrollTo((currentPosition - Launcher.POSITION_MAIN_PAGE) * width, 0);
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void setClassDisabled(boolean isClassDisabled) {
        this.isClassDisabled = isClassDisabled;
    }
}
