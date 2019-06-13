package android.app.readboy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.telecom.TelecomManager;
import android.util.Log;

/**
 * Created by gj on 2016/10/13.
 */
public final class ReadboyWearManager {

    private static final String TAG = "ReadboyWearManager";

    private Context mContext;
    private IReadboyWearService mService;

    private static final int RINGER_MODE_IDLE = -1;

    public static final String POWERON = "poweron";
    public static final String POWEROFF = "poweroff";
    public static final String RESTART = "reset";
    public static final String PROFILE = "profile";
    public static final String SOS = "SOS";
    public static final String BASE_INFO = "info";
    public static final String ALARM = "clock";
    public static final String FORBID_CLASS = "class";
    public static final String FORBID_SHUTDOWN = "power";
    public static final String WORK_MODE = "mode";
    public static final String FACTORY = "factory";
    public static final String LOCATION = "loc";
    public static final String LISTEN = "listen";
    public static final String CAPTURE = "capture";

	public static final String SCANWIFI = "scanwifi";
	public static final String SETWIFI = "setwifi";
	
	public static final String BTPAIR = "btpair";
	public static final String BTOPEN = "btopen";
	public static final String BTCLOSE = "btclose";
	public static final String BTNAME = "btname";

    public static final String CONTACTS_ADD = "add";
    public static final String CONTACTS_UPDATE = "update";
    public static final String CONTACTS_DELETE = "del";
    public static final String CONTACTS_RESET = "reset";

    public static final int DISCONNECTED = -2;
    
    public ReadboyWearManager(Context context, IReadboyWearService service, Handler handler) {
		mContext = context;
		mService = service;
		//mHandler = handler;
    }
    
    private boolean isInCall(String pkgName) {
    	if (!pkgName.equals("com.android.dialer")) {
    		return false;
    	}
    	TelecomManager tm = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
    	return tm.isInCall();
    }
    
    private void killRecentTask(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RecentTaskInfo> appRecentList = am.getRecentTasksForUser(100, 
        		ActivityManager.RECENT_IGNORE_HOME_AND_RECENTS_STACK_TASKS, UserHandle.CURRENT.getIdentifier());
        List<Integer> idList = new ArrayList<Integer>();
        int size = appRecentList.size();
        for(int i=0; i<size; i++) {
            RecentTaskInfo info = appRecentList.get(i);
            if(info.baseIntent != null) {
                Set<String> categories = info.baseIntent.getCategories();
                if(categories != null && categories.contains(Intent.CATEGORY_HOME)) {
                    continue;
                }
                
                ComponentName component = info.baseIntent.getComponent();
                if(component != null && component.getPackageName().equals(context.getPackageName())){
                    continue;
                }
                
                if (component != null  && isInCall(component.getPackageName())) {
                	continue;
                }
                
                if(component != null && component.getPackageName().equals("com.readboy.wearlauncher")){
                    continue;
                }
                
                if(component != null && component.getPackageName().equals("com.readboy.rbfota")){
                    continue;
                }
            }

            idList.add(info.id);
            idList.add(info.persistentId);
        }
        
        for(int id : idList) {
            am.removeTask(id);
        }
    }

	public boolean isClassForbidOpen() {
		try {
			return mService.isClassForbidOpen();
        }catch (RemoteException e){
            e.printStackTrace();
        }
		return false;
	}

	public void setClassForbidOpen(boolean isClassForbid) {
		try {
		    mService.setClassForbidOpen(isClassForbid);
		}catch (Exception e){
            e.printStackTrace();
        }
	}

	public boolean isLowPowerMode() {
		try {
            return mService.isLowPowerMode();
        }catch (RemoteException e){
            e.printStackTrace();
        }
		return false;
	}

	public void setLowPowerMode(boolean isLowPowerMode) {
		try {
			boolean lowPowerMode = mService.isLowPowerMode();
			if (lowPowerMode != isLowPowerMode) {
				mService.setLowPowerMode(isLowPowerMode);
			}
        }catch (Exception e){
            e.printStackTrace();
        }
	}

	public void setPersonalInfo(PersonalInfo info) {
		if (mService == null) {
    		Log.w(TAG, "setPersonalInfo IReadboyWearService is null");
			return;
    	}
		try {
            mService.setPersonalInfo(info);
        }catch (RemoteException e){
            e.printStackTrace();
        }
	}

    public PersonalInfo getPersonalInfo() {
		if (mService == null) {
    		Log.w(TAG, "getPersonalInfo IReadboyWearService is null");
			return null;
    	}
		try {
            return mService.getPersonalInfo();
        }catch (RemoteException e){
        	e.printStackTrace();
            return null;
        }
	}
    
    public void setDebugFlag(int flag) {
    	if (mService == null) {
    		Log.w(TAG, "setDebugFlag IReadboyWearService is null");
			return;
    	}
		try {
			mService.setDebugFlag(flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public int getDebugFlag() {
    	if (mService == null) {
    		Log.w(TAG, "getDebugFlag IReadboyWearService is null");
			return 0;
    	}
		try {
			return mService.getDebugFlag();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
    
    public void setPedometer(int pedometer) {
    	try {
			mService.setPedometer(pedometer);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public int getPedometer() {
    	try {
			return mService.getPedometer();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return 0;
    }

    public int getVolteSwitch() {
    	try {
			return mService.getVolteSwitch();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return 0;
    }
    
    /**
     * 返回连接状态
     * @return
     */
    public int getConnectStatus(){
		if (mService == null) {
    		Log.w(TAG, "getConnectStatus IReadboyWearService is null");
			return DISCONNECTED;
    	}
        try {
            return mService.getConnectStatus();
        }catch (RemoteException e){
        	e.printStackTrace();
            return DISCONNECTED;
        }
    }

	public String getLocationInfo(){
		if (mService == null) {
    		Log.w(TAG, "getLocationInfo IReadboyWearService is null");
			return null;
    	}
		try {
            return mService.getLocationInfo();
        }catch (RemoteException e){
            e.printStackTrace();
			return null;
        }
	}

    /**
     * 获取某个设备是否在线
     *
     * @param deviceId
     *            设备ID
     * @param pushInterface
     *            回调{"r":"online","data":0};data:0不在线 1在线
     */
    public void getDeviceOnline(String deviceId, IReadboyWearListener pushInterface){
		if (mService == null) {
    		Log.w(TAG, "getDeviceOnline IReadboyWearService is null");
			return;
    	}
        try {
            Log.i(TAG, "------------------ReadboyWearManager getDeviceOnline");
            mService.getDeviceOnline(deviceId, pushInterface);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

	public void reportLocation(String type, double latitude, char north_south, double longitude, char east_west, float speed, float precision, float angle, IReadboyWearListener pushInterface) {
		if (mService == null) {
    		Log.w(TAG, "reportLocation IReadboyWearService is null");
			return;
    	}
		try {
			Log.i(TAG, "------------------ReadboyWearManager reportLocation");
            mService.reportLocation(type, latitude, north_south, longitude, east_west, speed, precision, angle, pushInterface);
        }catch (RemoteException e){
            e.printStackTrace();
        }
	}

    /**
     * 获取设备某些信息
     *
     * @param infoKey
     *            要获取哪些信息就填哪个value，包括：ReadboyWearManager.BASE_INFO等等
     * @param pushInterface
     *            回调
     */
    public void getDeviceInfoWithKey(String infoKey, IReadboyWearListener pushInterface){
		if (mService == null) {
    		Log.w(TAG, "getDeviceInfoWithKey IReadboyWearService is null");
			return;
    	}
		try {
            Log.i(TAG, "------------------ReadboyWearManager getDeviceInfoWithKey");
            mService.getDeviceInfoWithKey(infoKey, pushInterface);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }
    
    public void getInfoWithKeyAndData(String key, String data, IReadboyWearListener pushInterface) {
    	if (mService == null) {
    		Log.w(TAG, "getInfoWithKeyAndData IReadboyWearService is null");
			return;
    	}
		try {
            Log.i(TAG, "------------------ReadboyWearManager getDeviceInfoWithKey");
            mService.getInfoWithKeyAndData(key, data, pushInterface);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

	public void putSimpleAction(String action, IReadboyWearListener pushInterface) {
		if (mService == null) {
    		Log.w(TAG, "putSimpleAction IReadboyWearService is null");
			return;
    	}
		try {
            Log.i(TAG, "------------------ReadboyWearManager getDeviceInfoWithKey");
            mService.putSimpleAction(action, pushInterface);
        }catch (RemoteException e){
            e.printStackTrace();
        }
	}

    /**
     * 获取设备的通讯录
     * 举例：t就是tag；data里面是联系人信息，分别是uuid，是否紧急联系人，手表头像索引，电话号码，亲情号，备注名
     * {"r":"contact","o":"0057343928get","t":"3","data":["UA56CAD016E7B341|0|0|15900034007||爸爸","M15911122233|1|2|15911122233|553|yy"]}
     * @param tag           同步时间, 上次服务器返回的tag
     * @param pushInterface 回调
     */
    public void getDeviceContacts(String tag, IReadboyWearListener pushInterface){
		if (mService == null) {
    		Log.w(TAG, "getDeviceContacts IReadboyWearService is null");
			return;
    	}
        try {
            Log.i(TAG, "------------------ReadboyWearManager getDeviceContacts");
            mService.getDeviceContacts(tag, pushInterface);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

    /**
     * 操作设备的通讯录，新增、删除、更新、重置，如果是修改联系人信息的话，一般指：双方的备注，关系，亲情号
     *
     * @param action        动作 ,add/update/del/reset
     * @param contactsId      联系人id，当为新增时，如果是其他设备，则需要id，如果是电话本，则不需要id。另外，reset不需要id,填null
     * @param dataString    联系人内容，新增、更新、才需要，其他填null
     * @param pushInterface 回调
     */
    public void operateDeviceContacts(String action, String contactsId, String dataString, IReadboyWearListener pushInterface){
		if (mService == null) {
    		Log.w(TAG, "operateDeviceContacts IReadboyWearService is null");
			return;
    	}
		try {
            Log.i(TAG, "------------------ReadboyWearManager operateDeviceContacts");
            mService.operateDeviceContacts(action, contactsId, dataString, pushInterface);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

    /**
     * 设置设备闹钟
     *
     * @param alarmString   闹钟JSONArray字符串
     * @param pushInterface 回调
     */
    public void setDeviceAlarm(String alarmString, IReadboyWearListener pushInterface){
		if (mService == null) {
    		Log.w(TAG, "setDeviceAlarm IReadboyWearService is null");
			return;
    	}
		try {
            Log.i(TAG, "------------------ReadboyWearManager setDeviceAlarm");
            mService.setDeviceAlarm(alarmString, pushInterface);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

    /**
     * 上报蓝牙随行状态
     *
     * @param status        蓝牙状态：0表示蓝牙连接断开 1表示已连接
     * @param pushInterface 回调
     */
    public void putDeviceBluetoothStatus(int status, IReadboyWearListener pushInterface){
		if (mService == null) {
    		Log.w(TAG, "putDeviceBluetoothStatus IReadboyWearService is null");
			return;
    	}
        try {
            Log.i(TAG, "------------------ReadboyWearManager putDeviceBluetoothStatus");
            mService.putDeviceBluetoothStatus(status, pushInterface);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

    /**
     * 上报计步步数
     *
     * @param steps 步数
     * @param duration
     * @param distance
     * @param pushInterface   回调
     */
	public void putDevicePedometer(int steps, int duration, double distance, IReadboyWearListener pushInterface) {
		if (mService == null) {
    		Log.w(TAG, "putDevicePedometer IReadboyWearService is null");
			return;
    	}
		try {
            Log.i(TAG, "------------------ReadboyWearManager putDevicePedometer222");
            mService.putDevicePedometer(steps, duration, distance, pushInterface);
        }catch (RemoteException e){
            e.printStackTrace();
        }
	}

    public void putDevicePedometerPraise(String targetId, IReadboyWearListener pushInterface) {
		if (mService == null) {
    		Log.w(TAG, "putDevicePedometerPraise IReadboyWearService is null");
			return;
    	}
		try {
            Log.i(TAG, "------------------ReadboyWearManager putDevicePedometerPraise");
            mService.putDevicePedometerPraise(targetId, pushInterface);
        }catch (RemoteException e){
            e.printStackTrace();
        }
	}

	public void putDeviceRunning(int start, int duration, double distance, double calorie, int hr_interval, String hrData, IReadboyWearListener pushInterface) {
		if (mService == null) {
    		Log.w(TAG, "putDeviceRunning IReadboyWearService is null");
			return;
    	}
		try {
            Log.i(TAG, "------------------ReadboyWearManager putDeviceRunning");
            mService.putDeviceRunning(start, duration, distance, calorie, hr_interval, hrData, pushInterface);
        }catch (RemoteException e){
            e.printStackTrace();
        }
	}

    public void putDeviceBattery(int battery, IReadboyWearListener pushInterface) {
		if (mService == null) {
    		Log.w(TAG, "putDeviceBattery IReadboyWearService is null");
			return;
    	}
		try {
            Log.i(TAG, "------------------ReadboyWearManager putDeviceBattery");
            mService.putDeviceBattery(battery, pushInterface);
        }catch (RemoteException e){
            e.printStackTrace();
        }
	}

    /**
     * 上报短信内容，比如：查询话费、查询流量等短信内容
     *
     * @param targetId      需要接收短信内容的uuid
     * @param smsString     短信内容
     * @param pushInterface 回调
     */
    public void putDeviceSms(String targetId, String smsString, IReadboyWearListener pushInterface){
		if (mService == null) {
    		Log.w(TAG, "putDeviceSms IReadboyWearService is null");
			return;
    	}
		try {
            Log.i(TAG, "------------------ReadboyWearManager putDeviceSms");
            mService.putDeviceSms(targetId, smsString, pushInterface);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

    /**
     * 上报监拍图片信息。上传监拍图片成功之后，再调用这个接口来上报监拍消息
     *
     * @param sendId      需要接收监拍内容的uuid
     * @param width 图片宽
     * @param height 图片高
     * @param thumbUrl 缩略图
     * @param fileUrl 图片URL
     * @param pushInterface 回调
     */
	public void putDeviceCapture(String sendId, int width, int height, String thumbUrl, String fileUrl, IReadboyWearListener pushInterface){
		if (mService == null) {
    		Log.w(TAG, "putDeviceCapture IReadboyWearService is null");
			return;
    	}
        try {
            Log.i(TAG, "------------------ReadboyWearManager putDeviceCapture222");
            mService.putDeviceCapture(sendId, width, height, thumbUrl, fileUrl, pushInterface);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

    /**
     * 给App发送通知消息
     *
     * @param targetId        需要接收消息的uuid
     * @param notifyMsgString 通知消息
     * @param pushInterface   回调
     */
    public void sendNotifyMsgToApp(String targetId, String notifyMsgString, IReadboyWearListener pushInterface){
		if (mService == null) {
    		Log.w(TAG, "sendNotifyMsgToApp IReadboyWearService is null");
			return;
    	}
		try {
            Log.i(TAG, "------------------ReadboyWearManager sendNotifyMsgToApp");
            mService.sendNotifyMsgToApp(targetId, notifyMsgString, pushInterface);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

    /**
     * 上报手表的通话记录
     *
     * @param recordString  手表通话记录
     * @param pushInterface 回调
     */
    public void putDeviceRecord(String recordString, IReadboyWearListener pushInterface){
    	if (mService == null) {
    		Log.w(TAG, "putDeviceRecord IReadboyWearService is null");
			return;
    	}
		try {
            Log.i(TAG, "------------------ReadboyWearManager putDeviceRecord");
            mService.putDeviceRecord(recordString, pushInterface);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

    /**
     * 获取最新聊天及系统消息。获取聊天消息后，保存服务器返回的标记t，以便下次获取。
     * 注意:如果之前没有tag就传“”；这时候服务器会返回tag，不会返回消息（极少可能会返回消息），
     *
     * @param tag           时间tag
     * @param pushInterface 回调
     */
    public void getAllMessage(String tag, IReadboyWearListener pushInterface){
		if (mService == null) {
    		Log.w(TAG, "getAllMessage IReadboyWearService is null");
			return;
    	}
        try {
            Log.i(TAG, "------------------ReadboyWearManager getAllMessage");
            mService.getAllMessage(tag, pushInterface);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

    /**
     * 发送聊天消息
     *
     * @param sendId      目标id，支持：群组id或者设备id，或者用户uuid
     * @param type    类型"text", "audio", "image", "video", "link" 
     * @param length    语音时长type = "audio"时使用
     * @param width	图片时使用
     * @param height	图片时使用
     * @param thumbUrl 图片缩略图地址
     * @param url    语音或图片地址
     * @param pushInterface 回调
     */
	public void sendChatMessage(String sendId, String type, int length, int width, int height, String thumbUrl, String url, IReadboyWearListener pushInterface){
		if (mService == null) {
    		Log.w(TAG, "sendChatMessage IReadboyWearService is null");
			return;
    	}
        try {
            Log.i(TAG, "------------------ReadboyWearManager sendChatMessage22222");
            mService.sendChatMessage(sendId, type, length, width, height, thumbUrl, url, pushInterface);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

	/**
     * Handle by customer.
     * @param cmd (q)
     * @param key (k)
     * @param data if not use set empty(null or "")
     * @param pushInterface
     */
	public void customRequest(String cmd, String key, String data, IReadboyWearListener pushInterface) {
		if (mService != null) {
			try {
				mService.customRequest(cmd, key, data, pushInterface);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Handle by customer.
	 * @param cmd (q)
	 * @param key (k)
	 * @param id
	 * @param data if not use set empty(null or "")
	 * @param pushInterface
	 */
	public void customRequest(String cmd, String key, String id, String data, IReadboyWearListener pushInterface) {
		if (mService != null) {
			try {
				mService.customRequestWithId(cmd, key, id, data, pushInterface);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void customResponse(String jsonString) throws RemoteException {
		if (mService != null) {
			try {
				mService.customResponse(jsonString);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
}
