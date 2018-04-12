package com.example.administrator.bluetoothdevice.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.administrator.bluetoothdevice.R;
import com.example.administrator.bluetoothdevice.activity.MainActivity;
import com.example.administrator.bluetoothdevice.utils.Logger;


/**
 * Created by 侯晓戬 on 2017/8/9.
 * 将服务设置成前台服务的类
 */

public class ServiceForeground extends BroadcastReceiver {
    private final static int NOTIFICATION_ID = 0x2105;
    private final static String NOTIFICATION_DELETED_ACTION = "NOTIFICATION_DELETED_ACTION";
    private final Service mService;

    private Notification mNotification;

    public ServiceForeground(Service service) {
        mService = service;
        initBroadcast();
    }

    protected Service getService() {
        return mService;
    }

    //将服务设置成前台服务
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setForeground() {
        createNotification();
        if (null != mNotification) {
            getService().startForeground(NOTIFICATION_ID, mNotification);
        }
    }

    //取消前台服务
    public void stopForeground() {
        getService().stopForeground(true);
    }

    public void release() {
        unInitBroadcast();
    }

    //建立状态栏信息
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void createNotification() {
        Notification.Builder builder = new Notification.Builder(getService());
        PendingIntent contentIntent = PendingIntent.getActivity(getService(), NOTIFICATION_ID,
                new Intent(getService(), MainActivity.class), 0);
        builder.setContentIntent(contentIntent);
        builder.setDeleteIntent(PendingIntent.getBroadcast(getService(), NOTIFICATION_ID,
                new Intent(NOTIFICATION_DELETED_ACTION), 0));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker(getService().getString(R.string.app_name));
        builder.setOngoing(true);//设置不能移除
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setContentTitle(getService().getString(R.string.app_name));
        builder.setContentText(getService().getString(R.string.app_name));
        mNotification = builder.build();
    }

    private void initBroadcast() {
        IntentFilter filter = new IntentFilter(NOTIFICATION_DELETED_ACTION);
        getService().registerReceiver(this, filter);
    }

    private void unInitBroadcast() {
        getService().unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d("", "onReceive() " + intent.getAction());
        if (NOTIFICATION_DELETED_ACTION.equals(intent.getAction())) {
            stopForeground();
        }
    }
}
