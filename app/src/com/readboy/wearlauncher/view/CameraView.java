package com.readboy.wearlauncher.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;


import com.readboy.wearlauncher.R;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class CameraView extends FrameLayout implements View.OnClickListener {
    private Context mContext;
    private ScrollView mScrollView;

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        OverScrollDecoratorHelper.setUpOverScroll(mScrollView);
        findViewById(R.id.photo).setOnClickListener(this);
        findViewById(R.id.video).setOnClickListener(this);
        findViewById(R.id.gallery).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        switch (vid) {
            case R.id.photo:
                Intent photoIntent = new Intent();
                photoIntent.setAction("com.readboy.camera3.camera");
                photoIntent.addCategory(Intent.CATEGORY_DEFAULT);
                mContext.startActivity(photoIntent);
                break;
            case R.id.video:
                Intent videoIntent = new Intent();
                videoIntent.setAction("com.readboy.camera3.record");
                videoIntent.addCategory(Intent.CATEGORY_DEFAULT);
                mContext.startActivity(videoIntent);
                break;
            case R.id.gallery:
                Intent galleryIntent = new Intent();
                galleryIntent.setAction("com.readboy.gallery3d");
                mContext.startActivity(galleryIntent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void moveToTop() {
        if (mScrollView != null) {
            mScrollView.scrollTo(0, 0);
        }
    }
}
