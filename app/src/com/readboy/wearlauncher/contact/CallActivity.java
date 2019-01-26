package com.readboy.wearlauncher.contact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.telecom.TelecomManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.readboy.wearlauncher.R;


public class CallActivity extends Activity implements OnClickListener {

	private TextView mTitle;

	private String name;

	private String number;

	private ImageView blurBg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dialog_call);
		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.confirm).setOnClickListener(this);
		Intent intent = getIntent();
		name = intent.getStringExtra("name");
		number = intent.getStringExtra("phoneNumber");
		mTitle = findViewById(R.id.call_title);
		mTitle.setText(getString(R.string.call_option, name));
		blurBg = findViewById(R.id.blur_bg);
		blurBg.setImageBitmap(BlurBuilder.blur(blurBg));
		if (BlurBuilder.isBlurFlag()) {
			blurBg.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(android.view.animation.Animation.INFINITE,android.view.animation.Animation.INFINITE);
	}

	private long currentTime = 0;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel:
			finish();
			break;
		case R.id.confirm:
			if(System.currentTimeMillis() - currentTime <= 2000){
				return;
			}
			currentTime = System.currentTimeMillis();
			Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+number));
			final TelecomManager telecomManager =
					(TelecomManager) getSystemService(Context.TELECOM_SERVICE);
			telecomManager.placeCall(Uri.fromParts(PhoneAccount.SCHEME_TEL,number, null), new Bundle());
			overridePendingTransition(android.view.animation.Animation.INFINITE,android.view.animation.Animation.INFINITE);
			finish();
			break;
		default:
			break;
		}
	}
}
