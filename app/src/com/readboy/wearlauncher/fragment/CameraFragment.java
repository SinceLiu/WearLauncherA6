package com.readboy.wearlauncher.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import com.readboy.wearlauncher.R;

/**
 * @author lxx
 * @date 2019/2/17
 */
public class CameraFragment extends Fragment implements View.OnClickListener {
    private ScrollView mScrollView;
    private boolean isViewCreated;  //防止空指针，setUserVisibleHint()比onCreateView()快

    public CameraFragment() {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isViewCreated) {
            return;
        }
        if (!isVisibleToUser) {
            if (mScrollView != null) {
                mScrollView.scrollTo(0, 0);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_layout,container,false);
        mScrollView = (ScrollView) view.findViewById(R.id.scrollView);
        OverScrollDecoratorHelper.setUpOverScroll(mScrollView);
        view.findViewById(R.id.photo).setOnClickListener(this);
        view.findViewById(R.id.video).setOnClickListener(this);
        view.findViewById(R.id.gallery).setOnClickListener(this);
        return view;
    }
    @Override
    public void onActivityCreated(final Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        isViewCreated = true;
    }


    @Override
    public void onClick(View v) {
        int vid = v.getId();
        switch (vid) {
            case R.id.photo:
                Intent photoIntent = new Intent();
                photoIntent.setAction("com.readboy.camera3.camera");
                photoIntent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivity(photoIntent);
                break;
            case R.id.video:
                Intent videoIntent = new Intent();
                videoIntent.setAction("com.readboy.camera3.record");
                videoIntent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivity(videoIntent);
                break;
            case R.id.gallery:
                Intent galleryIntent = new Intent();
                galleryIntent.setAction("com.readboy.gallery3d");
                startActivity(galleryIntent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
