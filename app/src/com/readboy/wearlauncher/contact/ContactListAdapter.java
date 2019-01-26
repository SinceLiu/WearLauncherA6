package com.readboy.wearlauncher.contact;

import java.util.List;

import android.app.Activity;
import android.app.readboy.ReadboyWearManager;
import android.support.v7.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.readboy.wearlauncher.R;

public class ContactListAdapter extends RecyclerView.Adapter<RVViewHolder> implements OnClickListener {

    private long currentTime = 0;
    List<RBContact> datas;
    Context context;
    Activity activity;
    private ReadboyWearManager mRBManager;

    public ContactListAdapter(Context context, List<RBContact> datas) {
        // TODO Auto-generated constructor stub
        this.datas = datas;
        this.context = context;
        mRBManager = (ReadboyWearManager) context.getSystemService(Context.RBW_SERVICE);
    }

    @Override
    public RVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // TODO Auto-generated method stub
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new RVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RVViewHolder holder, int position) {
        // TODO Auto-generated method stub
        RBContact contact = datas.get(position);
        holder.tv_name.setText(contact.getName());
        if (mRBManager.getPersonalInfo() != null) {
            setSupportVideoCall(mRBManager.getPersonalInfo().getRtc() == 1 && mRBManager.getPersonalInfo().getRtcSdk() == 1
                    && (contact.getUuid().startsWith("U") || contact.getUuid().startsWith("D")));
        }
        if (!TextUtils.isEmpty(contact.getIconUri())) {
            Picasso.with(context).load(Uri.parse(contact.getIconUri())).into(holder.iv_protrait);
        } else {
            int iconResId = R.drawable.default_icon_sq;
            switch (contact.getRel()) {
                case 0:
                    iconResId = R.drawable.protrait0;
                    break;
                case 1:
                    iconResId = R.drawable.protrait1;
                    break;
                case 2:
                    iconResId = R.drawable.protrait2;
                    break;
                case 3:
                    iconResId = R.drawable.protrait3;
                    break;
                case 4:
                    iconResId = R.drawable.protrait4;
                    break;
                case 5:
                    iconResId = R.drawable.protrait5;
                    break;
                case 6:
                    iconResId = R.drawable.protrait6;
                    break;
                case 7:
                    iconResId = R.drawable.protrait7;
                    break;
                case 8:
                    iconResId = R.drawable.protrait8;
                    break;
                case 9:
                    iconResId = R.drawable.protrait9;
                    break;
                case 10:
                    iconResId = R.drawable.protrait10;
                    break;
                case 11:
                    iconResId = R.drawable.protrait11;
                    break;
                default:
                    break;
            }
            Picasso.with(context).load(iconResId).into(holder.iv_protrait);
        }
        holder.longNumber.setText(contact.getTphone());
        if (!TextUtils.isEmpty(contact.getTsphone())) {
            holder.shortNumber.setVisibility(View.VISIBLE);
            holder.shortNumber.setText(contact.getTsphone());
        } else {
            holder.shortNumber.setVisibility(View.GONE);
        }
        if (contact.isInWeTalk() && !lowPower) {
            if (supportVideoCall) {
                holder.callTypeIcon.setImageResource(R.drawable.call_video);
            } else {
                holder.callTypeIcon.setImageResource(R.drawable.call_normal);
            }
        } else {
            holder.callTypeIcon.setImageResource(R.drawable.call_normal);
        }
        holder.item.setTag(contact);
        holder.item.setOnClickListener(this);
    }

    private boolean lowPower = false;

    public void setLowPower(boolean lp) {
        lowPower = lp;
    }

    private boolean supportVideoCall = false;

    public void setSupportVideoCall(boolean supportVideoCall) {
        this.supportVideoCall = supportVideoCall;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return datas == null ? 0 : datas.size();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (System.currentTimeMillis() - currentTime <= 1000) {
            return;
        }
        BlurBuilder.snapShotWithoutStatusBar(activity);
        currentTime = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.contact_item:
                RBContact contact = (RBContact) v.getTag();
                Intent intent;
                if (contact.getUuid().startsWith("U") || contact.getUuid().startsWith("D")) {
                    // 支持视频和拨号
                    intent = new Intent(context, com.readboy.wearlauncher.contact.CallTypeActivity.class);
                } else {
                    // 支持拨号
                    intent = new Intent(context, com.readboy.wearlauncher.contact.CallActivity.class);
                }

                //设置了短号的拨打短号
                if (TextUtils.isEmpty(contact.getTsphone())) {
                    intent.putExtra("phoneNumber", contact.getTphone());
                } else {
                    intent.putExtra("phoneNumber", contact.getTsphone());
                }
                intent.putExtra("uuid", contact.getUuid());
                intent.putExtra("photoUri", contact.getIconUri());
                intent.putExtra("name", contact.getName());
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(android.view.animation.Animation.INFINITE,
                        android.view.animation.Animation.INFINITE);
                break;
            default:
                break;
        }
    }

}

