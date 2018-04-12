package com.example.administrator.bluetoothdevice.bean;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import com.example.administrator.bluetoothdevice.utils.Logger;

import java.util.UUID;

/**
 * Created by 2018/4/12 15:02
 * 创建：Administrator on
 * 描述:BLE设备控制虚拟类用于兼容不同设备
 */

public abstract class BLEControler {
    private final static String TAG = "BLEControler";
    protected final BluetoothGatt mBluetoothGatt;

    private int GattConnectState = BluetoothProfile.STATE_DISCONNECTED;
    protected BluetoothGattCharacteristic SendCallCharacteristic;//发送呼叫特征码
    protected BluetoothGattCharacteristic KeyEventCharacteristic;//按键事件特征码
    protected BluetoothGattCharacteristic LinkLossCharacteristic;//连接断开特征码
    protected BluetoothGattCharacteristic BatteryLevelCharacteristic;//电池特征码

    protected boolean WaringRing = false; //发送呼叫响铃中

    public BLEControler(BluetoothGatt mBluetoothGatt) {
        this.mBluetoothGatt = mBluetoothGatt;
        setState(BluetoothProfile.STATE_CONNECTING);
    }

    public BluetoothGatt getBluetoothGatt() {
        return mBluetoothGatt;
    }

    //释放资源
    public void release(Context context) {
        Logger.i(TAG, "release() " + getBluetoothGatt().getDevice().getAddress());
        setKeyNotification(false);
        if (null != mBluetoothGatt) {
            if (isConnected()) {
                if (!sendLinkLoss()) {
//                    mBluetoothGatt.close();
                }
            } else {
//                mBluetoothGatt.close();
            }
        }
        setState(BluetoothProfile.STATE_DISCONNECTED);
        SendCallCharacteristic = null;
        KeyEventCharacteristic = null;
        BatteryLevelCharacteristic = null;
    }

    //设置状态
    public void setState(int state) {
        Logger.i(TAG, "setState() " + state + " " + mBluetoothGatt.getDevice().getAddress());
        GattConnectState = state;
    }

    //获取状态
    public boolean isConnected() {
        return GattConnectState == BluetoothProfile.STATE_CONNECTED;
    }

    public int getState() {
        return GattConnectState;
    }

    //呼叫状态
    public boolean isCalling() {
        return WaringRing;
    }

    /////////////////////////////////////////////

    //发送呼叫警报
    public abstract boolean sendCallAlert(boolean alert);

    //注册按键信息上报
    public abstract boolean registeredKeyNotification();

    protected abstract boolean setKeyNotification(boolean set);

    //检查是否按键消息改变
    public abstract boolean checkKeyNotification(UUID uuid);

    //获取电池电量
    public abstract int getBatteryLevel();

    //用户主动断开设备
    public abstract boolean sendLinkLoss();

    //初始化特征码 必须在 onServicesDiscovered 后调用
    public abstract void initCharacteristic();

    //清空特征码，在断开的时候调用
    public void cleanCharacteristic() {
        SendCallCharacteristic = null;
        KeyEventCharacteristic = null;
//        LinkLossCharacteristic = null;
        BatteryLevelCharacteristic = null;
    }

    //关闭
    public void close() {
        mBluetoothGatt.close();
    }
}
