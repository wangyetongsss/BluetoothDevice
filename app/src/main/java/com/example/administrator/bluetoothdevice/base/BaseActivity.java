package com.example.administrator.bluetoothdevice.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.example.administrator.bluetoothdevice.eventbus.EventsID;
import com.example.administrator.bluetoothdevice.eventbus.ViewEvent;
import com.example.administrator.bluetoothdevice.utils.Logger;
import com.example.administrator.bluetoothdevice.utils.PermissionsUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by 2018/4/12 10:57
 * 创建：Administrator on
 * 描述: Activity基类
 * 主要有eventbus接收发送数据
 * 权限检测处理
 */

public class BaseActivity extends Activity implements PermissionsUtils.IResultCallBack, ActivityCompat.OnRequestPermissionsResultCallback {
    private final static String TAG = "BaseActivity";
    private PermissionsUtils mPermissionsUtils;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        EventBus.getDefault().register(this);
        mPermissionsUtils = new PermissionsUtils(this)
                .setResultCallBack(this);
        checkPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Logger.i(TAG, "onDestroy() " + this.getClass().getName());
    }

    //TODO 检查权限
    private void checkPermission() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(580);
                    if (null != mPermissionsUtils) {
                        mPermissionsUtils.checkRuntimePermissions(getRuntimePermissions());
                    }
                } catch (Exception e) {
                    Logger.w(TAG, "checkPermission() " + e.getMessage());
                }

            }
        });
    }

    //TODO 是否已经授权运行时权限
    public boolean authorizeRuntimePermission() {
        if (null != mPermissionsUtils) {
            return mPermissionsUtils.authorizeRuntimePermissions(getRuntimePermissions());
        }
        return false;
    }

    //TODO 返回当前界面需要申请的权限
    protected String[] getRuntimePermissions() {
        return null;
    }

    //TODO 权限申请成功的回调
    protected void onSuccess() {
    }

    @Override
    public void onRequestPermissionsSuccess() {
        onSuccess();
    }

    //EventBus 回调接口
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true) //在ui线程执
    public void onEventMainThread(ViewEvent events) {
        onEventHandler(events);
        if (events.getEvent() == EventsID.ALL_ACTIVITY_FINISH) {
            finish();
        }
    }

    protected void onEventHandler(ViewEvent event) {

    }
}
