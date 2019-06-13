package android.app.readboy;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gj on 2017/3/29.
 */

public class PersonalInfo implements Parcelable {
    private static final String DEFAULT_NAME = "宝贝";
    private static final String DEFAULT_BIRTHDAY = "2010-01-01";
    private static final int DEFAULT_GRADE = 1;
    private static final int DEFAULT_AGE = 0;
    private static final int DEFAULT_SEX = 0;
    private static final int DEFAULT_HEIGHT = 137;
    private static final double DEFAULT_WEIGHT = 32.5;

    private static final int DEFAULT_HAS_SIRI = 1;
    private static final int DEFAULT_HAS_CAPTURE = 1;

    private static final int DEFAULT_VOLUME = 3;
    private static final int DEFAULT_MODE = 0;

    private static final int DEFAULT_GPS_INTERVAL = 30;
    private static final int DEFAULT_WIFI_INTERVAL = 60;
    private static final int DEFAULT_LBS_INTERVAL = 180;

	private static final String DEFAULT_OPT_TIME = "180,180,180";
	
	private static final int DEFAULT_TRACK_TIME = 420; // 7min

	/***************base info******************/
    private String mName;
    private int mAge;
    private int mSex;
    private int mGrade;
    private int mHeight;//cm
    private double mWeight;//kg
    private String mBirthday;

    private String mUuid;
    private String mPhoneNum;
    private String mImei;
    private String mDeviceType;

    private String mAdminUuid;
    private String mAdminPhoneNum;
    private String mFamilyPhone;
    
    private String mFotaOem;
    private String mFotaModel;
    private String mFotaPlatform;
    private String mFotaType;
	
	private int mRtcSdk;
    
    private int mProtocolVersion;
    private int mInfoVersion;
    /***************base info******************/

    /***************Settings******************/
    private int mNoFind;
    private int mPower;
    private int mLocMode;
    private int mNoTrack;
    private int mTrackTime;
    private int mNoStrangerCall;
    private int mVolte;
    private int mDial;
    private int mWifiapp;
    private List<String> mWifiAppList = new ArrayList<String>();
    private int mSettingsVersion;
    /***************Settings******************/
    
    /***************Preference******************/
    private int mVolume;
    private int mHasSiri;
    private int mHasCapture;
    private int mGPSInterval;
    private int mWIFIInterval;
    private int mLBSInterval;
    private String mLocOptTime;
    private int mExprListen;
	private int mRtc;
    private int mPreferenceVersion;
    /***************Preference******************/

    private long mTimeStamp;
    
    // 0 : no flag;  
    private int mDebugFlag = 0;

    public PersonalInfo() {
        init();
    }

    public PersonalInfo(Parcel in) {
    	// infos
    	mName = in.readString();
        mAge = in.readInt();
        mSex = in.readInt();
        mGrade = in.readInt();
        mHeight = in.readInt();
        mWeight = in.readDouble();
        mBirthday = in.readString();

        mUuid = in.readString();
        mPhoneNum = in.readString();
        mImei = in.readString();
        mDeviceType = in.readString();

        mAdminUuid = in.readString();
        mAdminPhoneNum = in.readString();
        mFamilyPhone = in.readString();
        
        mFotaOem = in.readString();
        mFotaModel = in.readString();
        mFotaPlatform = in.readString();
        mFotaType = in.readString();
		
		mRtcSdk = in.readInt();
        
        mProtocolVersion = in.readInt();
        mInfoVersion = in.readInt();
        
        // settings
        mNoFind = in.readInt();
        mPower = in.readInt();
        mLocMode = in.readInt();
        mNoTrack = in.readInt();
        mTrackTime = in.readInt();
        mNoStrangerCall = in.readInt();
        mVolte = in.readInt();
        mDial = in.readInt();
        mWifiapp = in.readInt();
        in.readStringList(mWifiAppList);
        mSettingsVersion = in.readInt();
        
        // preference
        mVolume = in.readInt();
        mHasSiri = in.readInt();
        mHasCapture = in.readInt();
        mGPSInterval = in.readInt();
        mWIFIInterval = in.readInt();
        mLBSInterval = in.readInt();
        mLocOptTime = in.readString();
        mExprListen = in.readInt();
		mRtc = in.readInt();
        mPreferenceVersion = in.readInt();

        mTimeStamp = in.readLong();
        
        mDebugFlag = in.readInt();
    }

    public static Creator<PersonalInfo> CREATOR = new Creator<PersonalInfo>() {
        @Override
        public PersonalInfo createFromParcel(Parcel source) {
            return new PersonalInfo(source);
        }

        @Override
        public PersonalInfo[] newArray(int size) {
            return new PersonalInfo[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	// infos
        dest.writeString(mName);
        dest.writeInt(mAge);
        dest.writeInt(mSex);
        dest.writeInt(mGrade);
        dest.writeInt(mHeight);
        dest.writeDouble(mWeight);
        dest.writeString(mBirthday);

        dest.writeString(mUuid);
        dest.writeString(mPhoneNum);
        dest.writeString(mImei);
        dest.writeString(mDeviceType);

        dest.writeString(mAdminUuid);
        dest.writeString(mAdminPhoneNum);
        dest.writeString(mFamilyPhone);
        
        dest.writeString(mFotaOem);
        dest.writeString(mFotaModel);
        dest.writeString(mFotaPlatform);
        dest.writeString(mFotaType);
		
		dest.writeInt(mRtcSdk);
        
        dest.writeInt(mProtocolVersion);
        dest.writeInt(mInfoVersion);
        
        // settings
        dest.writeInt(mNoFind);
        dest.writeInt(mPower);
        dest.writeInt(mLocMode);
        dest.writeInt(mNoTrack);
        dest.writeInt(mTrackTime);
        dest.writeInt(mNoStrangerCall);
        dest.writeInt(mVolte);
        dest.writeInt(mDial);
        dest.writeInt(mWifiapp);
        dest.writeStringList(mWifiAppList);
        dest.writeInt(mSettingsVersion);

        // preference
        dest.writeInt(mVolume);
        dest.writeInt(mHasSiri);
        dest.writeInt(mHasCapture);
        dest.writeInt(mGPSInterval);
        dest.writeInt(mWIFIInterval);
        dest.writeInt(mLBSInterval);
        dest.writeString(mLocOptTime);
        dest.writeInt(mExprListen);
		dest.writeInt(mRtc);
        dest.writeInt(mPreferenceVersion);

        dest.writeLong(mTimeStamp);
        
        dest.writeInt(mDebugFlag);
    }

    public void init() {
    	// infos
        mName = DEFAULT_NAME;
        mAge = DEFAULT_AGE;
        mSex = DEFAULT_SEX;
        mGrade = DEFAULT_GRADE;
        mHeight = DEFAULT_HEIGHT;
        mWeight = DEFAULT_WEIGHT;
        mBirthday = DEFAULT_BIRTHDAY;

        mUuid = "";
        mPhoneNum = "";
        mImei = "";
        mDeviceType = "";

        mAdminUuid = "";
        mAdminPhoneNum = "";
        mFamilyPhone = "";
        
        mFotaOem = "";
        mFotaModel = "";
        mFotaPlatform = "";
        mFotaType = "";
		
		mRtcSdk = 0;
        
        mProtocolVersion = 0;
        mInfoVersion = 0;
        
        // settings
        mNoFind = 0;
        mPower = 0;
        mLocMode = DEFAULT_MODE;
        mNoTrack = 1;
        mTrackTime = DEFAULT_TRACK_TIME;
        mNoStrangerCall = 0;
        mVolte = 0;
        mDial = 1;
        mWifiapp = 0;
        //mWifiAppList.clear();
        mSettingsVersion = 0;
        
        // preference
        mVolume = DEFAULT_VOLUME;
        mHasSiri = DEFAULT_HAS_SIRI;
        mHasCapture = DEFAULT_HAS_CAPTURE;
        mGPSInterval = DEFAULT_GPS_INTERVAL;
        mWIFIInterval = DEFAULT_WIFI_INTERVAL;
        mLBSInterval = DEFAULT_LBS_INTERVAL;
        mLocOptTime = DEFAULT_OPT_TIME;
        mExprListen = 60;
		mRtc = 1;
        mPreferenceVersion = 0;
		
		mDebugFlag = 0;
    }

    public void setInfo(String info, int version) {
        if (TextUtils.isEmpty(info)) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(info);

            mName = jsonObject.optString("name", mName);
            mSex = jsonObject.optInt("sex", mSex);
            mGrade = jsonObject.optInt("grade", mGrade);
            mHeight = jsonObject.optInt("height", mHeight);
            mWeight = jsonObject.optDouble("weight", mWeight);
            mBirthday = jsonObject.optString("birthday", mBirthday);

            mUuid = jsonObject.optString("id", mUuid);
            mPhoneNum = jsonObject.optString("sim", mPhoneNum);
            mDeviceType = jsonObject.optString("type", mDeviceType);

            mAdminUuid = jsonObject.optString("adminId", mAdminUuid);
            mAdminPhoneNum = jsonObject.optString("adminPhone", mAdminPhoneNum);
            mFamilyPhone = jsonObject.optString("sphone", mFamilyPhone);
            
            JSONObject fota = jsonObject.optJSONObject("fota");
            if (fota != null) {
            	mFotaOem = fota.optString("oem");
                mFotaModel = fota.optString("model");
                mFotaPlatform = fota.optString("platform");
                mFotaType = fota.optString("type");
            }
			
			mRtcSdk = jsonObject.optInt("rtcsdk", mRtcSdk);
            
            mProtocolVersion = jsonObject.optInt("protocol", mProtocolVersion);
            
            mInfoVersion = version;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setSettings(String settings, int version) {
        if (TextUtils.isEmpty(settings)) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(settings);
            
            mNoFind = jsonObject.optInt("nofind", mNoFind);
            mPower = jsonObject.optInt("power", mPower);
            mLocMode = jsonObject.optInt("mode", mLocMode);
            mNoTrack = jsonObject.optInt("notrack", mNoTrack);
            mTrackTime = jsonObject.optInt("track", mTrackTime);
            mNoStrangerCall = jsonObject.optInt("nostrangercall", mNoStrangerCall);
            
            mVolte = jsonObject.optInt("volte", mVolte);
            mDial = jsonObject.optInt("dial", mDial);
            mWifiapp = jsonObject.optInt("wifiapp", mWifiapp);
            
            if (mWifiapp == 1) {
            	mWifiAppList.clear();
            	JSONArray array = jsonObject.optJSONArray("wifiapps");
            	for (int i = 0; i < array.length(); i++) {
            		String pkg = array.optString(i);
            		if (!TextUtils.isEmpty(pkg)) {
            			mWifiAppList.add(pkg);
            		}
            	}
            }
            
            mSettingsVersion = version;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPreference(String preference, int version) {
        if (TextUtils.isEmpty(preference)) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(preference);
            
            mVolume = jsonObject.optInt("volume", mVolume);
            mHasSiri = jsonObject.optInt("siri", mHasSiri);
            mHasCapture = jsonObject.optInt("capture", mHasCapture);
            mGPSInterval = jsonObject.optInt("gps", mGPSInterval);
            mWIFIInterval = jsonObject.optInt("wifi", mWIFIInterval);
            mLBSInterval = jsonObject.optInt("lbs", mLBSInterval);
            
            mLocOptTime = jsonObject.optString("opt_mode", mLocOptTime);
            mExprListen = jsonObject.optInt("expr_listen", mExprListen);
			
			mRtc = jsonObject.optInt("rtc", mRtc);
            
            mPreferenceVersion = version;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String toJsonString() {
        JSONObject jsonObject = new JSONObject();
        try {
        	JSONObject info = new JSONObject();
        	info.put("name", mName);
        	info.put("age", mAge);
        	info.put("sex", mSex);
        	info.put("grade", mGrade);
            info.put("height", mHeight);
            info.put("weight", mWeight);
            info.put("birthday", mBirthday);
            info.put("uuid", mUuid);
            info.put("phoneNum", mPhoneNum);
            info.put("imei", mImei);
            info.put("deviceType", mDeviceType);
            info.put("adminUuid", mAdminUuid);
            info.put("adminPhoneNum", mAdminPhoneNum);
            info.put("familyPhone", mFamilyPhone);
                        
            info.put("oem", mFotaOem);
            info.put("model", mFotaModel);
            info.put("platform", mFotaPlatform);
            info.put("type", mFotaType);
			
			jsonObject.put("rtcsdk", mRtcSdk);
			
			info.put("protocol", mProtocolVersion);
            info.put("infoVersion", mInfoVersion);

            jsonObject.put("info", info);

            JSONObject settings = new JSONObject();
            settings.put("noFind", mNoFind);
            settings.put("power", mPower);
            settings.put("mode", mLocMode);
            settings.put("noTrack", mNoTrack);
            settings.put("track", mTrackTime);
            settings.put("noStrangerCall", mNoStrangerCall);
            settings.put("volte", mVolte);
            settings.put("dial", mDial);
            settings.put("wifiApp", mWifiapp);
            if (mWifiapp == 1) {
            	JSONArray waArray = new JSONArray();
            	for (String app : mWifiAppList)
            		waArray.put(app);
            	settings.put("wifiAppList", waArray);
            }
            settings.put("settingsVersion", mSettingsVersion);
            jsonObject.put("settings", settings);
            
            JSONObject preference = new JSONObject();
            preference.put("volume", mVolume);
            preference.put("siri", mHasSiri);
            preference.put("capture", mHasCapture);
            preference.put("gps", mGPSInterval);
            preference.put("wifi", mWIFIInterval);
            preference.put("lbs", mLBSInterval);
            preference.put("optTime", mLocOptTime);
            preference.put("expr", mExprListen);
			jsonObject.put("rtc", mRtc);
            preference.put("preferenceVersion", mPreferenceVersion);
            jsonObject.put("preference", preference);

            jsonObject.put("timestamp", mTimeStamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void fromJsonString(String json) {
        if (TextUtils.isEmpty(json)) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            
            JSONObject info = jsonObject.optJSONObject("info");
            mName = info.optString("name", mName);
            mAge = info.optInt("age", mAge);
            mSex = info.optInt("sex", mSex);
            mGrade = info.optInt("grade", mGrade);
            mHeight = info.optInt("height", mHeight);
            mWeight = info.optDouble("weight", mWeight);
            mBirthday = info.optString("birthday", mBirthday);
            mUuid = info.optString("uuid", mUuid);
            mPhoneNum = info.optString("phoneNum", mPhoneNum);
            mImei = info.optString("imei", mImei);
            mDeviceType = info.optString("deviceType", mDeviceType);
            mAdminUuid = info.optString("adminUuid", mAdminUuid);
            mAdminPhoneNum = info.optString("adminPhoneNum", mAdminPhoneNum);
            mFamilyPhone = info.optString("familyPhone", mFamilyPhone);
            
            mFotaOem = info.optString("oem", mFotaOem);
            mFotaModel = info.optString("model", mFotaModel);
            mFotaPlatform = info.optString("platform", mFotaPlatform);
            mFotaType = info.optString("type", mFotaType);
			
			mRtcSdk = info.optInt("rtcsdk", mRtcSdk);

            mProtocolVersion = info.optInt("protocol", mProtocolVersion);
            mInfoVersion = info.optInt("infoVersion", mInfoVersion);
            
            JSONObject settings = jsonObject.optJSONObject("settings");
            mNoFind = settings.optInt("noFind", mNoFind);
            mPower = settings.optInt("power", mPower);
            mLocMode = settings.optInt("mode", mLocMode);
            mNoTrack = settings.optInt("noTrack", mNoTrack);
            mTrackTime = settings.optInt("track", mTrackTime);
            mNoStrangerCall = settings.optInt("noStrangerCall", mNoStrangerCall);
            mVolte = settings.optInt("volte", mVolte);
            mDial = settings.optInt("dial", mDial);
            mWifiapp = settings.optInt("wifiApp", mWifiapp);
            if (mWifiapp == 1) {
            	mWifiAppList.clear();
            	JSONArray array = settings.optJSONArray("wifiAppList");
            	for (int i = 0; i < array.length(); i++) {
            		String pkg = array.optString(i);
            		if (!TextUtils.isEmpty(pkg)) {
            			mWifiAppList.add(pkg);
            		}
            	}
            }
            mSettingsVersion = settings.optInt("settingsVersion", mSettingsVersion);
            
            JSONObject preference = jsonObject.optJSONObject("preference");
            mVolume = preference.optInt("volume", mVolume);
            mHasSiri = preference.optInt("siri", mHasSiri);
            mHasCapture = preference.optInt("capture", mHasCapture);
            mGPSInterval = preference.optInt("gps", mGPSInterval);
            mWIFIInterval = preference.optInt("wifi", mWIFIInterval);
            mLBSInterval = preference.optInt("lbs", mLBSInterval);
            mLocOptTime = preference.optString("optTime", mLocOptTime);
            mExprListen = preference.optInt("expr", mExprListen);
			mRtc = jsonObject.optInt("rtc", mRtc);
            mPreferenceVersion = preference.optInt("preferenceVersion", mPreferenceVersion);

            mTimeStamp = jsonObject.optLong("timestamp", mTimeStamp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return mName;
    }

    public int getAge() {
        return mAge;
    }

    public int getSex() {
        return mSex;
    }

    public int getGrade() {
        return mGrade;
    }

    public int getHeight() {
        return mHeight;
    }

    public double getWeight() {
        return mWeight;
    }

    public String getBirthday() {
        return mBirthday;
    }

    public String getUuid() {
        return mUuid;
    }

    public String getPhoneNum() {
        return mPhoneNum;
    }

    public String getImei() {
        return mImei;
    }

    public String getDeviceType() {
        return mDeviceType;
    }

    public String getAdminUuid() {
        return mAdminUuid;
    }

    public String getAdminPhoneNum() {
        return mAdminPhoneNum;
    }

    public String getFamilyPhone() {
        return mFamilyPhone;
    }
    
    public int getProtocolVersion() {
    	return mProtocolVersion;
    }
    
    public int getInfoVersion() {
    	return mInfoVersion;
    }
    
    public int getNoFind() {
    	return mNoFind;
    }
    
    public int getPower() {
    	return mPower;
    }
    
    public int getLocMode() {
        return mLocMode;
    }
    
    public int getNoTrack() {
        return mNoTrack;
    }

    public void setNoTrack(int noTrack) {
    	mNoTrack = noTrack;
    }
	
	public int getTrackTime() {
		return mTrackTime;
	}
	
	public int getNoStrangerCall() {
        return mNoStrangerCall;
    }

    public void setNoStrangerCall(int noStrangerCall) {
    	mNoStrangerCall = noStrangerCall;
    }
	
	public int getVolte() {
		return mVolte;
	}

	public int getDial() {
		return mDial;
	}
	
	public int getWifiAppEnable() {
		return mWifiapp;
	}
	
	public List<String> getWifiAppList() {
		return mWifiAppList;
	}
	
	public int getSettingsVersion() {
		return mSettingsVersion;
	}
	
	public int getVolume() {
        return mVolume;
    }
    
    public int isHasSiri() {
        return mHasSiri;
    }

    public int isHasCapture() {
        return mHasCapture;
    }

    public int getGpsInterval() {
        return mGPSInterval;
    }

    public int getWifiInterval() {
        return mWIFIInterval;
    }

    public int getLbsInterval() {
        return mLBSInterval;
    }

	public String getOptTime() {
        return mLocOptTime;
    }
	
	public int getExprListen() {
        return mExprListen;
    }
	
	public int getPreferenceVersion() {
        return mPreferenceVersion;
    }
	
	public void setTimestamp(long timeStamp) {
		mTimeStamp = timeStamp;
    }

    public long getTimestamp() {
        return mTimeStamp;
    }

	public int getDebugFlag() {
		return mDebugFlag;
	}

	public void setDebugFlag(int debugFlag) {
		this.mDebugFlag = debugFlag;
	}
	
	public String getFotaOem() {
		return mFotaOem;
	}
	
	public String getFotaModel() {
		return mFotaModel;
	}
	
	public String getFotaType() {
		return mFotaType;
	}
	
	public String getFotaPlatform() {
		return mFotaPlatform;
	}

	public int getRtc() {
		return mRtc;
	}

	public int getRtcSdk() {
		return mRtcSdk;
	}
	
	public void setRtcSdk(int rtcsdk) {
		mRtcSdk = rtcsdk;
	}
}
