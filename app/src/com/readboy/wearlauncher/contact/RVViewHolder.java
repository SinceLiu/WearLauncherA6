package com.readboy.wearlauncher.contact;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.readboy.wearlauncher.R;


public class RVViewHolder extends RecyclerView.ViewHolder{

	TextView tv_name,longNumber,shortNumber;

	RImageView iv_protrait;

	ImageView callTypeIcon;

	View item;

	public RVViewHolder(View itemView) {
		super(itemView);
		// TODO Auto-generated constructor stub
		item = itemView.findViewById(R.id.contact_item);
		tv_name = (TextView) itemView.findViewById(R.id.name);
		longNumber = (TextView) itemView.findViewById(R.id.tv_longnumber);
		shortNumber = (TextView) itemView.findViewById(R.id.tv_shortnumber);
		final Drawable d = itemView.getContext().getDrawable(R.drawable.short_d_l);
		d.setBounds(0, 0, 24, 24);
		shortNumber.setCompoundDrawables(d, null, null, null);
		iv_protrait = (RImageView) itemView.findViewById(R.id.protrait);
		callTypeIcon = (ImageView) itemView.findViewById(R.id.call_type_icon);
	}


}
