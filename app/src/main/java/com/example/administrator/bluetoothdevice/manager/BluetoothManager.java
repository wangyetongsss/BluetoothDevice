package com.example.administrator.bluetoothdevice.manager;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;

import com.example.administrator.bluetoothdevice.base.BaseApplication;
import com.example.administrator.bluetoothdevice.utils.Logger;

/**
 * Created by 2018/4/12 11:56
 * 创建：Administrator on
 * 描述:蓝牙状态处理
 */

public class BluetoothManager extends BroadcastReceiver implements View.OnClickListener {

    public interface IBluetoothStateChangCallBack {
        public void onStateChang(int state);
    }

    private String[] REQUEST_PERMISSIONS = new String[]{
            Manifest.permission_group.LOCATION
    };

    private final Context mContext;

    private View TipsView;
    private IBluetoothStateChangCallBack IStateCallBack = null;

    public BluetoothManager(Context context) {
        this.mContext = context;
        initView();
        initReceiver();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == BluetoothAdapter.ACTION_STATE_CHANGED) {
            int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
            switch (blueState) {
                case BluetoothAdapter.STATE_TURNING_ON: {
                }
                break;
                case BluetoothAdapter.STATE_ON: {
                    if (null != TipsView) {
                        TipsView.setVisibility(View.GONE);
                    }
                }
                break;
                case BluetoothAdapter.STATE_TURNING_OFF: {
                }
                break;
                case BluetoothAdapter.STATE_OFF: {
                    if (null != TipsView) {
                        TipsView.setVisibility(View.VISIBLE);
                    }
                }
                break;
            }
            if (null != IStateCallBack) {
                IStateCallBack.onStateChang(blueState);
            }
        }
    }

    private void initView() {
        if (null != TipsView) {
            TipsView.setOnClickListener(this);
            TipsView.setVisibility(View.GONE);
            if (!BluetoothState()) {
                TipsView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(this, filter);
    }

    public void release() {
        try {
            mContext.unregisterReceiver(this);
        } catch (IllegalArgumentException e) {
            Logger.e("BluetoothManager", e.toString());
        }
    }


    //TODO 获取手机蓝牙状态
    public static boolean BluetoothState() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }

    //TODO 开启手机蓝牙设备
    public static boolean openBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (null != bluetoothAdapter) {
            if (bluetoothAdapter.enable()) {
                return bluetoothAdapter.enable();
            } else {
                // TODO: 2017/12/18 类似vivo手机被禁止打开后的处理方法
                Intent requestBluetoothOn = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                BaseApplication.getAppContext().startActivity(requestBluetoothOn);
            }
        }
        return false;
    }


    //TODO 关闭蓝牙设备
    public static boolean closeBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (null != bluetoothAdapter) {
            return bluetoothAdapter.disable();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == TipsView) {
            if (openBluetooth()) {
                if (null != TipsView) {
                    TipsView.setVisibility(View.GONE);
                }
            }
        }
    }

    public BluetoothManager setTipsView(View view) {
        this.TipsView = view;
        if (null != TipsView) {
            if (BluetoothState()) {
                TipsView.setVisibility(View.GONE);
            } else {
                TipsView.setVisibility(View.VISIBLE);
            }
            TipsView.setOnClickListener(this);
        }
        return this;
    }

    /**
     * 是否显示此管理控件
     *
     * @param isShow true显示  false隐藏
     * @return
     */
    public BluetoothManager setShowTip(boolean isShow) {
        if (isShow) {
            TipsView.setVisibility(View.VISIBLE);
        } else {
            TipsView.setVisibility(View.GONE);
        }
        return this;
    }

    public BluetoothManager setStateChangCallBack(IBluetoothStateChangCallBack cb) {
        this.IStateCallBack = cb;
        return this;
    }

}
