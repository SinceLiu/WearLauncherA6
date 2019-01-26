package com.readboy.wearlauncher.view;

import android.content.Context;
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

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        if (!isClassDisabled) {
            return super.onTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                dirX = ev.getX() - mLastX;
                if (getCurrentItem() == Launcher.POSITION_MAIN_PAGE) {
                    dirY = ev.getY() - mLastY;
                    if (Math.abs(dirY) > Math.abs(dirX)) {
                        break;
                    }
                }
                if (Math.abs(dirX) > 10) {
                    ClassDisableDialog.showClassDisableDialog(getContext());
                    Utils.checkAndDealWithAirPlanMode(getContext());
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
