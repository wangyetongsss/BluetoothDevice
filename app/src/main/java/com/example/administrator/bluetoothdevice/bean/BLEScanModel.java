package com.example.administrator.bluetoothdevice.bean;

import android.bluetooth.BluetoothDevice;

import com.example.administrator.bluetoothdevice.callback.IBLEScanCallBack;
import com.example.administrator.bluetoothdevice.contract.BLEScanContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 2018/4/12 11:54
 * 创建：Administrator on
 * 描述:
 */

public class BLEScanModel implements BLEScanContract.Model {
    //数据定义
    private static BLEScanModel Instance;
    private Map<String, ScanDevice> mListDevice = new LinkedHashMap<>();

    //静态单例定义
    public static BLEScanModel getInstance() {
        if (null == Instance) {
            Instance = new BLEScanModel();
        }
        return Instance;
    }

    @Override
    public boolean addBluetoothDevice(BluetoothDevice device, int rssi, IBLEScanCallBack.Type type) {
        boolean bRet = false;
        ScanDevice item = mListDevice.get(device.getAddress());
        if (null == item) {
            mListDevice.put(device.getAddress(), new ScanDevice(device, rssi, type));
            bRet = true;
        }
        return bRet;
    }

    @Override
    public ScanDevice getDeviceByMAC(String mac) {
        return mListDevice.get(mac);
    }

    @Override
    public void cleanDevice() {
        mListDevice.clear();
    }

    @Override
    public List<ScanDevice> getDevices() {
        List<ScanDevice> lst = new ArrayList<>(mListDevice.values());
        Collections.reverse(lst);
        return lst;
    }
}
