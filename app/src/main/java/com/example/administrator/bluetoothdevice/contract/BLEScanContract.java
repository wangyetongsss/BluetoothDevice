package com.example.administrator.bluetoothdevice.contract;

import android.bluetooth.BluetoothDevice;

import com.example.administrator.bluetoothdevice.bean.ScanDevice;
import com.example.administrator.bluetoothdevice.callback.IBLEScanCallBack;

import java.util.List;

/**
 * Created by 2018/4/12 11:49
 * 创建：Administrator on
 * 描述:mvp模式
 */

public interface BLEScanContract {
    public final static int RESULT_SUCCESS = 0;     //执行成功


    interface Model {
        //添加扫描到的设备
        public boolean addBluetoothDevice(BluetoothDevice device, int rssi, IBLEScanCallBack.Type type);

        //获取指定MAC的设备信息
        public ScanDevice getDeviceByMAC(String mac);

        //清空扫描设备信息
        public void cleanDevice();

        //获取扫描到的设备列表
        public List<ScanDevice> getDevices();
    }

    public interface View {
        //指示扫描数据改变
        public void notifyDataSetChanged();

        //显示等待界面
        public void showWaiting(boolean show);

        //执行结果返回
        public void onResult(int id, String message);
    }

    interface Presenter {
        //扫描周边蓝牙
        public void scanBluetooth();

        //停止蓝牙的扫描
        public void stopScan();

        //获取扫描状态
        public boolean isScaning();

        //获取扫描到的周边蓝牙设备
        public List<ScanDevice> getScanBluetooth();

        //开始连接设备
        public void startConnect(String Mac,IBLEScanCallBack.Type Type);
    }
}
