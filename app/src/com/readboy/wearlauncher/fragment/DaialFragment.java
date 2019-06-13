package com.readboy.wearlauncher.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.readboy.wearlauncher.LauncherSharedPrefs;
import com.readboy.wearlauncher.R;
import com.readboy.wearlauncher.view.DaialParentLayout;
import com.readboy.wearlauncher.view.DialBaseLayout;
import com.readboy.wearlauncher.view.WatchDials;


/**
 * @author lxx
 * @date 2019/2/17
 */
public class DaialFragment extends Fragment {
    private DaialParentLayout mDaialView;
    private DialBaseLayout childDaialView;
    private LayoutInflater mInflater;
    private int mWatchType;
    private boolean isViewCreated;  //防止空指针，setUserVisibleHint()比onCreateView()快
    private boolean isPaused;
    private boolean isDialPaused;  //避免重复调用dialPause()
    private boolean isUIVisible;

    public DaialFragment() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isUIVisible = isVisibleToUser;
//        if (isVisibleToUser) {
//            dialResume();
//        } else {
//            dialPause();
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInflater = inflater;
        mDaialView = (DaialParentLayout) inflater.inflate(R.layout.watch_dial_layout, null);
        mDaialView.removeAllViews();
        mWatchType = LauncherSharedPrefs.getWatchType(getContext());
        childDaialView = (DialBaseLayout) inflater.inflate(WatchDials.mDialList.get(mWatchType % WatchDials.mDialList.size()), mDaialView, false);
        childDaialView.addChangedCallback();
        childDaialView.onResume();
        childDaialView.setButtonEnable();
        mDaialView.addView(childDaialView);
        return mDaialView;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewCreated = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        isPaused = false;
        dialResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        isPaused = true;
        dialPause();
    }

    public void setDialFromType(int type) {
        if (mDaialView == null) {
            return;
        }
        View view1 = mDaialView.getChildAt(0);
        if (view1 instanceof DialBaseLayout) {
            ((DialBaseLayout) view1).onPause();
            ((DialBaseLayout) view1).removeChangedCallback();
        }
        mDaialView.removeAllViews();
        DialBaseLayout childDaialView = (DialBaseLayout) mInflater.inflate(WatchDials.mDialList.get(type % WatchDials.mDialList.size()), mDaialView, false);
        childDaialView.addChangedCallback();
        childDaialView.onResume();
        childDaialView.setButtonEnable();
        mDaialView.addView(childDaialView);
        View view2 = mDaialView.getChildAt(0);
        if (view2 instanceof DialBaseLayout) {
            ((DialBaseLayout) view2).onResume();
            ((DialBaseLayout) view2).onDateChange();
            ((DialBaseLayout) view2).addChangedCallback();
        }
    }

    private void dialResume() {
//        if(!isViewCreated){
//            return;
//        }
//        if (!isPaused && isUIVisible) {
//            isDialPaused = false;
        View view = mDaialView.getChildAt(0);
        if (view instanceof DialBaseLayout) {
            ((DialBaseLayout) view).onResume();
            ((DialBaseLayout) view).onDateChange();
            ((DialBaseLayout) view).addChangedCallback();
        }
//        }
    }

    private void dialPause() {
//        if (!isViewCreated || isDialPaused) {
//            return;
//        }
//        if (isPaused || !isUIVisible) {
//            isDialPaused = true;
        View view = mDaialView.getChildAt(0);
        if (view instanceof DialBaseLayout) {
            ((DialBaseLayout) view).onPause();
            ((DialBaseLayout) view).removeChangedCallback();
        }
//        }
    }
}
