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

import com.readboy.wearlauncher.R;

public class CallTypeActivity extends Activity implements OnClickListener {

	private String uuid;

	private String phoneNumber;

	private String name;

	private String avatar;

	private ImageView blurBg;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dialog_call_type);

		Intent intent = getIntent();

		phoneNumber = intent.getStringExtra("phoneNumber");
		uuid = intent.getStringExtra("uuid");
		name = intent.getStringExtra("name");
		avatar = intent.getStringExtra("photoUri");
		findViewById(R.id.video_call).setOnClickListener(this);
		findViewById(R.id.voice_call).setOnClickListener(this);
		findViewById(R.id.mask).setOnClickListener(this);

		blurBg = findViewById(R.id.blur_bg);
		blurBg.setImageBitmap(BlurBuilder.blur(blurBg));
		if (BlurBuilder.isBlurFlag()) {
			blurBg.setVisibility(View.VISIBLE);
		}
	}

	private long currentTime = 0;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.video_call:
			if(System.currentTimeMillis() - currentTime <= 2000){
				return;
			}
			currentTime = System.currentTimeMillis();
			makeVideoCall(uuid, name, avatar);
			break;
		case R.id.voice_call:
			if(System.currentTimeMillis() - currentTime <= 2000){
				return;
			}
			currentTime = System.currentTimeMillis();
			final TelecomManager telecomManager =
					(TelecomManager) getSystemService(Context.TELECOM_SERVICE);
			telecomManager.placeCall(Uri.fromParts(PhoneAccount.SCHEME_TEL,phoneNumber, null), new Bundle());
			finish();
			break;
		case R.id.mask:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(android.view.animation.Animation.INFINITE,android.view.animation.Animation.INFINITE);
	}

	private void makeVideoCall(String uuid, String name, String iconUri) {
		Intent intent = new Intent();
		intent.setPackage("com.readboy.videocall");
		intent.putExtra("uuid", uuid);
		intent.putExtra("iconUri", iconUri);
		intent.putExtra("nickname", name);
		intent.setClassName("com.readboy.videocall", "com.readboy.videocall.VideoCallingActivity");
		try {
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finish();
	}

}
