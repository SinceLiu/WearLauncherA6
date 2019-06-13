// IReadboyWearService.aidl
package android.app.readboy;

import android.app.readboy.PersonalInfo;
import android.app.readboy.IReadboyWearListener;
// Declare any non-default types here with import statements

interface IReadboyWearService {
    /* set connect status with server */
    int getConnectStatus();

    /* set info */
    void setPersonalInfo(in PersonalInfo info);

    /* get device information */
    PersonalInfo getPersonalInfo();
    
    /* setLowPowerMode */
    void setLowPowerMode(boolean lowPowerMode);
    boolean isLowPowerMode();
	
    /* setClassForbidOpen */
    void setClassForbidOpen(boolean classForbidOpen);
    boolean isClassForbidOpen();
	
    /* setDebugFlag */
    void setDebugFlag(int flag);
    
    /* getDebugFlag */
    int getDebugFlag();
    
    void setPedometer(int pedometer);
    int getPedometer();
	
    /* volte: 0 off, 1 on */
    int getVolteSwitch();

    String getLocationInfo();

    /* check online */
    void getDeviceOnline(String deviceId, in IReadboyWearListener pushInterface);

    /* location */
    void reportLocation(String type, double latitude, char north_south, double longitude, char east_west, float speed, float precision, float angle, in IReadboyWearListener pushInterface);

    /* key =  clock, class, power, mode, info, time, pedrank, pedrank_site */
    void getDeviceInfoWithKey(String key, in IReadboyWearListener pushInterface);
    
    void getInfoWithKeyAndData(String key, String data, in IReadboyWearListener pushInterface);

    /* action = poweron, poweroff, reset, SOS */
    void putSimpleAction(String action, in IReadboyWearListener pushInterface);

    /* get contacts list */
    void getDeviceContacts(String tag, in IReadboyWearListener pushInterface);
    
    /* contact add update delete */
    void operateDeviceContacts(String action, String contactsId, String dataString, in IReadboyWearListener pushInterface);

    /* send local alarm to server */
    void setDeviceAlarm(String alarmString, in IReadboyWearListener pushInterface);

    /* send bluetooth status to server */
    void putDeviceBluetoothStatus(int status, in IReadboyWearListener pushInterface);

    /* send pedometer to server per hour */
    void putDevicePedometer(int steps, int duration, double distance, in IReadboyWearListener pushInterface);

    /* send praise to server */
    void putDevicePedometerPraise(String targetId, in IReadboyWearListener pushInterface);

    /* send running data to server */
    void putDeviceRunning(int start, int duration, double distance, double calorie, int hr_interval, String hrData, in IReadboyWearListener pushInterface);

    /* send battery to server */
    void putDeviceBattery(int battery, in IReadboyWearListener pushInterface);

    /* send sms to server */
    void putDeviceSms(String targetId, String smsString, in IReadboyWearListener pushInterface);

    /* send picture captured secret */
    void putDeviceCapture(String sendId, int width, int height, String thumbUrl, String fileUrl, in IReadboyWearListener pushInterface);

    /* notify msg */
    void sendNotifyMsgToApp(String targetId, String notifyMsgString, in IReadboyWearListener pushInterface);

    /* send record  */
    void putDeviceRecord(String recordString, in IReadboyWearListener pushInterface);

    /* get message */
    void getAllMessage(String tag, in IReadboyWearListener pushInterface);
   	
    /* after send msg file, send a nofity to server*/
    void sendChatMessage(String sendId, String type, int length, int width, int height, String thumbUrl, String url, in IReadboyWearListener pushInterface);
	
    /* Handle by customer. */
    void customRequest(String cmd, String key, String data, in IReadboyWearListener pushInterface);
    
    /* Handle by customer. */
    void customRequestWithId(String cmd, String key, String id, String data, IReadboyWearListener pushInterface);
    
    void customResponse(String jsonString);
}
