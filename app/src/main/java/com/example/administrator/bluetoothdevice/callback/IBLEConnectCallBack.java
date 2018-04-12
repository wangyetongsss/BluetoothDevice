package com.example.administrator.bluetoothdevice.callback;

/**
 * Created by 2018/4/12 14:46
 * 创建：Administrator on
 * 描述:连接蓝牙设备回调
 */

public interface IBLEConnectCallBack {
    public final static int BLE_CONNECT_RESULT_ERROR = -1; //连接失败
    public final static int BLE_CONNECT_RESULT_SUCCESS = 0; //连接成功
    //连接成功
    public void onResult(int id);
}
