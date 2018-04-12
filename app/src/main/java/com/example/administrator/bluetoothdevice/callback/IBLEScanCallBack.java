package com.example.administrator.bluetoothdevice.callback;

import android.bluetooth.BluetoothDevice;

/**
 * Created by 2018/4/12 11:27
 * 创建：Administrator on
 * 描述:蓝牙扫描设备的分类
 */

public interface IBLEScanCallBack {
    enum Type {
        DEVICE_TYPE_ONE(0),
        DEVICE_TYPE_TWO(1),
        DEVICE_TYPE_THREE(2);

        private int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static Type valueOf(int value) {
            Type tyep = DEVICE_TYPE_ONE;
            switch (value) {
                case 0: {
                    tyep = DEVICE_TYPE_ONE;
                }
                break;
                case 1: {
                    tyep = DEVICE_TYPE_TWO;
                }
                break;
                case 2: {
                    tyep = DEVICE_TYPE_THREE;
                }
                break;
                default: {
                    tyep = DEVICE_TYPE_ONE;
                }
                break;
            }
            return tyep;
        }
    }

    public void OnScan(BluetoothDevice device, int rssi, Type type);
}
