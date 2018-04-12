package com.example.administrator.bluetoothdevice.bean;

import com.example.administrator.bluetoothdevice.manager.BlueToothDeviceManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by 2018/4/12 14:31
 * 创建：Administrator on
 * 描述:用来存储MAC对应的设备
 */

public class DeviceModel {
    private LinkedHashMap<String, BlueToothDevice> UseDeviceMap = new LinkedHashMap<>();
    //定义单例句柄
    private static DeviceModel Instance;

    public static DeviceModel getInstance() {
        if (null == Instance) {
            Instance = new DeviceModel();
        }
        return Instance;
    }

    private DeviceModel() {
        UseDeviceMap.clear();
    }

    public List<BlueToothDevice> getUseDevices() {
        List<BlueToothDevice> lst = new ArrayList<>(UseDeviceMap.values());
        return lst;
    }

    public void setUseDevices(List<BlueToothDevice> lst) {
        UseDeviceMap.clear();
        if (null != lst) {
            for (BlueToothDevice device : lst) {
                UseDeviceMap.put(device.getAddress(), device);
            }
        }
    }

    public BlueToothDevice addDevice(String address) {
        address = address.replace("-", ":");
        BlueToothDevice item = UseDeviceMap.get(address);
        if (null == item) {
            item = new BlueToothDevice();
            UseDeviceMap.put(address, item);
        }
        return item;
    }

    public boolean EditDeviceInfo(BlueToothDevice device) {
        boolean bRet = false;
        BlueToothDevice item = UseDeviceMap.get(device.getAddress());
        if (null == item) {
            UseDeviceMap.put(device.getAddress(), device);
            bRet = true;
        }
        return bRet;
    }

    //TODO 查询获取设备
    public BlueToothDevice getUseDevice(String mac) {
        String address = mac.replace("-", ":");
        return UseDeviceMap.get(address);
    }

    //TODO 删除设备信息
    public BlueToothDevice removeUseDevice(String mac) {
        return UseDeviceMap.remove(mac);
    }

    //获取当前连接的设备数量
    public int getConnectNumber() {
        int number = 0;
        if (null != UseDeviceMap) {
            for (BlueToothDevice device : UseDeviceMap.values()) {
                if (device.isConnection()) {
                    number++;
                }
            }
        }
        return number;
    }

    //获取离线的设备数量
    public int getDisconnectNumber() {
        int number = 0;
        if (null != UseDeviceMap) {
            for (BlueToothDevice device : UseDeviceMap.values()) {
                if (!device.isConnection()) {
                    number++;
                }
            }
        }
        return number;
    }


    //TODO 清空数据
    public void cleanUseDevices() {
        if (null != UseDeviceMap) {
            for (BlueToothDevice device : UseDeviceMap.values()) {
                BlueToothDeviceManager deviceManager = device.getDeviceManager();
                if (null != deviceManager) {
                    deviceManager.disConnect();
                }
                device.init();
            }
            UseDeviceMap.clear();
        }
        UseDeviceMap = new LinkedHashMap<>();
    }

    //TODO 重置蓝牙连接
    public void ResetDevices() {
        if (null != UseDeviceMap) {
            for (BlueToothDevice device : UseDeviceMap.values()) {
                BlueToothDeviceManager deviceManager = device.getDeviceManager();
                if (null != deviceManager) {
                    deviceManager.disConnect();
                }
            }
        }
    }
}
