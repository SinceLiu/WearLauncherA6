package com.readboy.wearlauncher.view;

import android.app.readboy.ReadboyWearManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.os.SystemProperties;
import com.readboy.wearlauncher.R;

/**
 * 低电/挂失界面
 */
public class WatchDialTypeO extends DialBaseLayout {

    private boolean unknownSim = false;
    private Button mCallOwner;

    public WatchDialTypeO(Context context) {
        super(context);
    }

    public WatchDialTypeO(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WatchDialTypeO(Context context, AttributeSet attrs, int defStyle) {
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
        if (mDialerBtn != null) {
            mDialerBtn.setEnabled(true);
        }
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
        mCallOwner = (Button) findViewById(R.id.btn_id_call_owner);
        if (mCallOwner != null) {
            mCallOwner.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkSim();
                    if (unknownSim) {
                        Toast.makeText(mContext, R.string.notice_loss_for_no_sim, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String phoneNumber = getAdminPhone();
                    if (!TextUtils.isEmpty(phoneNumber)) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                        try {
                            mContext.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(mContext, R.string.notice_loss_for_call_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        setDate();
        setTime();
    }

    private void checkSim(){
        String simOper = SystemProperties.get("gsm.sim.operator.numeric", "46099");
        if ("46000".equals(simOper) || "46002".equals(simOper) || "46007".equals(simOper) || "46008".equals(simOper)) {
            unknownSim = false;
        } else if ("46001".equals(simOper) || "46006".equals(simOper) || "46009".equals(simOper)) {
            unknownSim = false;
        } else if ("46003".equals(simOper) || "46005".equals(simOper) || "46011".equals(simOper)) {
            unknownSim = false;
        } else {
            unknownSim = true;
        }
    }


    private void setTime() {
        mDigitClock.setCurTime();
    }

    private String getAdminPhone() {
        ReadboyWearManager mRBManager = (ReadboyWearManager) mContext.getSystemService(Context.RBW_SERVICE);
        if (mRBManager == null) {
            Log.e("LOSS", "RBWearManager is null");
            return null;
        }
        String adminPhone = null;
        try {
            adminPhone = mRBManager.getPersonalInfo().getAdminPhoneNum().trim();
        } catch (Exception e) {
            Log.e("LOSS", "error to get admin phone number ");
        }
        return adminPhone;
    }

}
