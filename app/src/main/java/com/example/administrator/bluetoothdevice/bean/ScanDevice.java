package com.example.administrator.bluetoothdevice.bean;

import android.bluetooth.BluetoothDevice;

import com.example.administrator.bluetoothdevice.callback.IBLEScanCallBack;

/**
 * Created by 2018/4/12 11:50
 * 创建：Administrator on
 * 描述:扫描到的设备对象
 */

public class ScanDevice {
    BluetoothDevice device;
    int Rssi;
    IBLEScanCallBack.Type type;

    public ScanDevice(BluetoothDevice device,int rssi, IBLEScanCallBack.Type type){
        this.device = device;
        this.Rssi = rssi;
        this.type = type;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getRssi() {
        return Rssi;
    }

    public void setRssi(int rssi) {
        Rssi = rssi;
    }

    public IBLEScanCallBack.Type getType() {
        return type;
    }

    public void setType(IBLEScanCallBack.Type type) {
        this.type = type;
    }

    //信号强度转等级
    public static int getRssiLevel(int dbm){
        int mydbm = Math.abs(dbm);
        if(mydbm >= 100){
            mydbm = 99;
        }
        return mydbm;
    }
}
