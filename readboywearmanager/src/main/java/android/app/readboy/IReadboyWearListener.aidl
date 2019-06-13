// IReadboyWearListener.aidl
package android.app.readboy;

// Declare any non-default types here with import statements

interface IReadboyWearListener {

    //正常、数据未更改(（如获取通讯录、消息时返回，可以不用返回未改变的数据)
    void pushSuc(String cmd, String serial, int code, String data, String result);
    //超时、出错
    void pushFail(String cmd, String serial, int code, String errorMsg);
}
