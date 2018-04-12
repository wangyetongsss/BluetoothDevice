package com.example.administrator.bluetoothdevice.bean;

import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.administrator.bluetoothdevice.manager.BlueToothDeviceManager;

import java.util.Date;


/**
 * Created by 2018/4/12 14:21
 * 创建：Administrator on
 * 描述:连接后的设备对象
 */

public class BlueToothDevice {
    private int Rssi;            //信号值
    //蓝牙设备管理器，包括有 BluetoothGatt 和 Device 设备类型
    private BlueToothDeviceManager deviceManager;
    private Date OnlineDate;    //连接的时间，用于算连接了多久

    // TODO 初始化数据
    public void init() {
        Rssi = 10;
        deviceManager = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public String getAddress() {
        if (null != deviceManager) {
            return deviceManager.getAddress();
        }
        return "";
    }

    public int getRssi() {
        return Rssi;
    }

    public void setRssi(int rssi) {
        Rssi = rssi;
    }

    public Date getOnlineDate() {
        return OnlineDate;
    }

    public void setOnlineDate(Date onlineDate) {
        OnlineDate = onlineDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean isConnection() {
        boolean bRet = false;
        if (null != deviceManager) {
            if (deviceManager.getConnectionState() == BluetoothProfile.STATE_CONNECTED) {
                bRet = true;
            }
        }
        return bRet;
    }

    public BlueToothDeviceManager getDeviceManager() {
        if (deviceManager != null) {
            return deviceManager;
        }
        return null;
    }

    public void setDeviceManager(BlueToothDeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public BlueToothDeviceManager createDeviceManager(Context context, String mac) {
        Log.i("", "createDeviceManager() deviceManager=" + deviceManager);
        if (null == deviceManager) {
            deviceManager = new BlueToothDeviceManager(context,
                    mac);
        }
        return this.deviceManager;
    }
}
