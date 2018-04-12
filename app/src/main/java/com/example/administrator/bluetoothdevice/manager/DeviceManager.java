package com.example.administrator.bluetoothdevice.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;

import com.example.administrator.bluetoothdevice.bean.BlueToothDevice;
import com.example.administrator.bluetoothdevice.bean.DeviceModel;
import com.example.administrator.bluetoothdevice.callback.IBLEConnectCallBack;
import com.example.administrator.bluetoothdevice.eventbus.EventsID;
import com.example.administrator.bluetoothdevice.eventbus.ViewEvent;
import com.example.administrator.bluetoothdevice.utils.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by 2018/4/12 14:20
 * 创建：Administrator on
 * 描述:单例类，LED灯设备管理类的存储
 * 主要负责LED设备的重连，断开，连接逻辑
 */

public class DeviceManager extends Handler implements Runnable
        , BluetoothManager.IBluetoothStateChangCallBack {

    private final static String TAG = "DeviceManager";
    private BluetoothManager bluetoothManager;

    public DeviceManager(Context context) {
        EventBus.getDefault().register(this);
        bluetoothManager = new BluetoothManager(context).setStateChangCallBack(this);
    }


    //TODO EventBus 接收处理函数
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true) //在ui线程执
    public void onEventMainThread(ViewEvent events) {
        if (events.getEvent() == EventsID.START_AUTO_CONNECT) {
            start();
        }
    }

    //开始连接
    public void start() {
        Logger.i(TAG, "start()");
        post(this);
    }


    public void stop() {
        removeCallbacks(this);
    }

    public void release() {
        bluetoothManager.release();
        EventBus.getDefault().unregister(this);
        stop();
    }


    @Override
    public void run() {
        removeCallbacks(this);
        //连接操作
        connectDevices();
        //循环
        postDelayed(this, (60 * 1000));
    }

    //TODO 连接设备
    private int connectDevices() {
        int Ret = 0;
        if (!BluetoothManager.BluetoothState()) {
            return Ret;
        }
        List<BlueToothDevice> lst = DeviceModel.getInstance().getUseDevices();
        if (null != lst && lst.size() > 0) {
            for (BlueToothDevice device : lst) {
                final BlueToothDeviceManager deviceManager = device.getDeviceManager();
                if (null != deviceManager) {
                    if (deviceManager.getConnectionState() == BluetoothProfile.STATE_DISCONNECTED) {
                        deviceManager.connection(new IBLEConnectCallBack() {
                            @Override
                            public void onResult(int id) {
                                Logger.i(TAG, "connectDevices() " + id);
                                EventBus.getDefault().post(new ViewEvent(
                                        EventsID.DEVICE_CONNECT_SUCCESS).setMessage(deviceManager.getAddress()));
                            }
                        });
                        Ret++;
                    }
                }
            }
        }
        Logger.i(TAG, "connectDevices() " + Ret);
        return Ret;
    }

    @Override
    public void onStateChang(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_ON: {
                // TODO: 2018/3/30 蓝牙开启
                start();
            }
            break;
            case BluetoothAdapter.STATE_OFF: {
                // TODO: 2018/3/30 蓝牙关闭
                stop();
            }
            break;
        }
    }
}
