package com.example.administrator.bluetoothdevice.service;

import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.administrator.bluetoothdevice.bean.DeviceModel;
import com.example.administrator.bluetoothdevice.eventbus.EventsID;
import com.example.administrator.bluetoothdevice.eventbus.ViewEvent;
import com.example.administrator.bluetoothdevice.manager.DeviceManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by 侯晓戬 on 2017/7/13.
 * 业务处理服务
 */

public class DeviceService extends android.app.Service {
    private final static String TAG = "DeviceService";
    private LocalBinder binder = new LocalBinder();
    //前台服务
    private ServiceForeground mServiceForeground = null;
    //LED自动连接操作
    private DeviceManager mAutoConnectManager = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind()");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate()");
        super.onCreate();
        EventBus.getDefault().register(this);
        mAutoConnectManager = new DeviceManager(getApplicationContext());
        mServiceForeground = new ServiceForeground(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mServiceForeground) {
            mServiceForeground.stopForeground();
            mServiceForeground.release();
            mServiceForeground = null;
        }
        EventBus.getDefault().unregister(this);
        Log.i(TAG, "onDestroy()");
    }

    private void killSelf() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "onRebind()");
        mServiceForeground.setForeground();
        super.onRebind(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");
        if (null != mServiceForeground) {
            mServiceForeground.setForeground();
        }
        if (null != mAutoConnectManager) {
            mAutoConnectManager.start();
        }
        return START_REDELIVER_INTENT;
    }

    //TODO EventBus 接收处理函数
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true) //在ui线程执
    public void onEventMainThread(ViewEvent events) {
        switch (events.getEvent()) {
            case EventsID.APP_OUTHANDLER:
                // TODO: 2018/4/2 退出
                OutHandler();
                break;
        }
    }

    /**
     * 退出应用注销
     */
    private void OutHandler() {
        //1、退出自动连接处理
        if (mAutoConnectManager != null) {
            mAutoConnectManager.release();
            mAutoConnectManager = null;
        }
        //2、断开连接的设备并清理设备信息
        cleanUseDevices(false, "");
        //5.结束自己
        stopSelf();
        EventBus.getDefault().post(new ViewEvent(
                EventsID.ALL_ACTIVITY_FINISH));
    }

    /**
     * 清除设备，收到解绑信息的时候断开设备
     */
    private void cleanUseDevices(boolean isShare, String Mac) {
        DeviceModel.getInstance().cleanUseDevices();
    }

    public class LocalBinder extends Binder {
        public DeviceService getServices() {
            return DeviceService.this;
        }
    }
}
