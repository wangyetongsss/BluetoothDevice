package com.example.administrator.bluetoothdevice.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.example.administrator.bluetoothdevice.R;
import com.example.administrator.bluetoothdevice.bean.BLEControler;
import com.example.administrator.bluetoothdevice.bean.BlueToothDevice;
import com.example.administrator.bluetoothdevice.bean.DeviceModel;
import com.example.administrator.bluetoothdevice.bean.MyBLEControl;
import com.example.administrator.bluetoothdevice.callback.IBLEConnectCallBack;
import com.example.administrator.bluetoothdevice.utils.Logger;

import java.util.Date;

/**
 * Created by 2018/4/12 14:24
 * 创建：Administrator on
 * 描述:
 */


public class BlueToothDeviceManager extends BluetoothGattCallback {
    private final static String TAG = "BlueToothDeviceManager";
    private final Context mContext;
    private final String DeviceAddress;
    private BLEControler mBLEControl = null;
    private IBLEConnectCallBack mBLEConnectCB = null;
    private Handler mMainHandler = null;
    private final int YOUR_KEY = 1;//你自己的按键指令
    private boolean bLinkLoss = false;

    private final static int CONNECT_TIME_ONE = 1000;
    private final static int CONNECT_TIME_OUT_NUMBER = (20 * CONNECT_TIME_ONE) + 200;
    private CountDownTimer TimeroutDown = null;
    private final static int CHECK_DISCON_TIME = (4 * CONNECT_TIME_ONE) + 200;
    private CountDownTimer checkDisconnect = null;

    public BlueToothDeviceManager(Context context, String mac) {
        this.mContext = context;
        this.DeviceAddress = mac.replace("-", ":");
    }

    protected Context getContext() {
        return this.mContext;
    }

    private BluetoothAdapter getBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }


    public String getAddress() {
        return this.DeviceAddress;
    }

    //建立控制适配类实例
    private void createControl(BluetoothGatt gatt) {
        mMainHandler = new Handler(Looper.getMainLooper());
        setbLinkLoss(false);
        mBLEControl = new MyBLEControl(gatt);
        Logger.i(TAG, "createControl() mBLEControl=" + mBLEControl);
    }

    private void cleanControl() {
        if (null != mBLEControl) {
            mBLEControl.release(mContext);
            mBLEControl = null;
        }
    }

    //TODO 连接设备
    public boolean connection(IBLEConnectCallBack callBack) {
        boolean bRet = false;
        Logger.i(TAG, "connection() " + DeviceAddress);
        mBLEConnectCB = callBack;
        if (connectionHandler()) {
            startConnectTimeout();
            return true;
        }
        return false;
    }

    //正真的连接处理

    private boolean connectionHandler() {
        if (BluetoothManager.BluetoothState()) {
            try {
                BluetoothDevice device = getBluetoothAdapter().getRemoteDevice(DeviceAddress);
                Logger.i(TAG, "connectionHandler() " + DeviceAddress);
                if (null != device) {
                    BluetoothGatt gatt = device.connectGatt(getContext(), false, this);
                    if (gatt.connect()) {
                        createControl(gatt);
                        return true;
                    }
                }
            } catch (IllegalArgumentException e) {
                Toast.makeText(getContext(), getContext().getString(R.string.error_device), Toast.LENGTH_SHORT).show();
                return false;
            } catch (Exception e) {
                Toast.makeText(getContext(), getContext().getString(R.string.error_device), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return false;
    }


    /**
     * ----------------------------------------------------------------------------------蓝牙连接回调----------------------------------------------------------------
     **/
    //TODO 回调函数
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        Logger.i(TAG, "onConnectionStateChange() newState=" + newState + " status=" + status);
        if (BluetoothGatt.GATT_SUCCESS == status) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                handlerStateConnection(gatt);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                handlerStateDisconnect(gatt);
            }
        } else if (BluetoothGatt.GATT_FAILURE == status) {
            // 133
            handlerStateDisconnect(gatt);
        } else {
            handlerStateDisconnect(gatt);
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        Logger.i(TAG, "onServicesDiscovered() status=" + status);
        if (BluetoothGatt.GATT_SUCCESS == status) {
            //获取服务成功，开始注册和回调连接成功
            if (null != mBLEControl) {
                mBLEControl.initCharacteristic();
                mBLEControl.registeredKeyNotification();
            }
            readRemoteRssi();
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        Logger.i(TAG, "onCharacteristicChanged() ");
        if (null != mBLEControl) {
            if (mBLEControl.checkKeyNotification(characteristic.getUuid())) {
                //按键消息
                byte[] data = characteristic.getValue();
                if (null != data && data.length >= 1) {
                    final int key = data[0];
                    MainHandlerPost(new Runnable() {
                        @Override
                        public void run() {
                            handlerKeyNotification(key);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
                                      int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        Logger.i(TAG, "onCharacteristicWrite characteristic=" + characteristic
                + " status=" + status + " address =" + gatt.getDevice().getAddress());
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        super.onReadRemoteRssi(gatt, rssi, status);
        updateRssi(rssi);
    }


    //TODO 按键上报处理函数,处理你自己定义的按键消息
    private void handlerKeyNotification(int key) {
        switch (key) {
            case YOUR_KEY: {

            }
            break;
        }
    }

    private void startCheckDisconnect() {
        if (null == checkDisconnect) {
            checkDisconnect = new CountDownTimer(
                    CHECK_DISCON_TIME,
                    CONNECT_TIME_ONE) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Logger.i(TAG, "handlerDisconnect onTick " + millisUntilFinished
                            + " " + DeviceAddress);
                    if (getConnectionState() == BluetoothProfile.STATE_CONNECTED) {
                        cancel();
                    }
                }

                @Override
                public void onFinish() {
                    if (getConnectionState() != BluetoothProfile.STATE_CONNECTED) {
                        cleanControl();
                        setConnectionDate(true);//清理掉连接时间
                        //通知界面处理
                        if (null != mBLEConnectCB) {
                            mBLEConnectCB.onResult(IBLEConnectCallBack.BLE_CONNECT_RESULT_ERROR);
                        }
                    }
                }
            };
        }
        checkDisconnect.cancel();
        checkDisconnect.start();
    }


    private void stopCheckDisconnect() {
        if (null != checkDisconnect) {
            checkDisconnect.cancel();
        }
    }

    //断开警报处理函数
    private void handlerDisconnectWarning() {
        Logger.i(TAG, "handlerDisconnectWarning() " + isLinkLoss()
                + " mBLEControl =" + mBLEControl + " " + DeviceAddress);
        if (!isLinkLoss()) {
            if (null != mBLEControl) {
                //非手机要求的断开;重连，如果4秒后还没有连接上则开始警报
                checkDisconnectConnect();
            }
        } else {
            cleanControl();
            Logger.i(TAG, "handlerDisconnect(Set) " + isLinkLoss() + " " + DeviceAddress);
        }
    }

    //连接成功的处理
    private void handlerStateConnection(BluetoothGatt gatt) {
        gatt.discoverServices();//获取服务
        stopConnectTimeout();//停止超时检测
        setConnectionDate(false);//设置连接时间
        setConnectionState(BluetoothProfile.STATE_CONNECTED);
        //回调成功
        if (null != mBLEConnectCB) {
            mBLEConnectCB.onResult(IBLEConnectCallBack.BLE_CONNECT_RESULT_SUCCESS);
        }
    }

    //断开的处理
    private void handlerStateDisconnect(BluetoothGatt gatt) {
        gatt.close();
        gatt.disconnect();
        //只有从连接到断开才进入断线警报处理流程
        if (getConnectionState() == BluetoothProfile.STATE_CONNECTED) {
            setConnectionState(BluetoothProfile.STATE_DISCONNECTED);
            MainHandlerPost(new Runnable() {
                @Override
                public void run() {
                    handlerDisconnectWarning();
                }
            });
        }
    }


    //TODO 获取当前状态
    public int getConnectionState() {
        int nRet = BluetoothProfile.STATE_DISCONNECTED;
        if (null != mBLEControl) {
            nRet = mBLEControl.getState();
        }
        Logger.i(TAG, "getConnectionState() " + nRet + " " + DeviceAddress);
        return nRet;
    }

    private void setConnectionState(int state) {
        if (null != mBLEControl) {
            mBLEControl.setState(state);
        }
    }

    //TODO 设置连接时间
    private void setConnectionDate(boolean clean) {
        BlueToothDevice device = DeviceModel.getInstance().getUseDevice(DeviceAddress);
        if (null != device) {
            if (clean) {
                device.setOnlineDate(null);
            } else {
                if (null == device.getOnlineDate()) {
                    device.setOnlineDate(new Date(System.currentTimeMillis()));
                }
            }

        }
    }

    //更新信号值
    private void updateRssi(int rssi) {
        BlueToothDevice device = DeviceModel.getInstance().getUseDevice(DeviceAddress);
        Logger.i(TAG, "updateRssi() " + device + " " + DeviceAddress + " rssi=" + rssi);
        if (null != device) {
            device.setRssi(rssi);
        }
    }

    /////////////////////////////////////////////
    //TODO 连接超时的处理

    private void startConnectTimeout() {
        Logger.i(TAG, "startConnectTimeout " + " " + DeviceAddress);
        if (null == TimeroutDown) {
            TimeroutDown = new CountDownTimer(CONNECT_TIME_OUT_NUMBER, CONNECT_TIME_ONE) {
                @Override
                public void onTick(long millisUntilFinished) {
//                    Logger.i(TAG, "ConnectTimeout::onTick " + millisUntilFinished + " " + DeviceAddress);
                    if (getConnectionState() == BluetoothProfile.STATE_CONNECTED) {
                        cancel();
                    }
                }

                @Override
                public void onFinish() {
                    Logger.i(TAG, "ConnectTimeout::onFinish " + mBLEConnectCB + " " + DeviceAddress);
                    if (getConnectionState() != BluetoothProfile.STATE_CONNECTED) {
                        //连接超时,清理控制器
                        cleanControl();
                        if (null != mBLEConnectCB) {
                            mBLEConnectCB.onResult(IBLEConnectCallBack.BLE_CONNECT_RESULT_ERROR);
                        }
                    }
                }
            };
        }
        TimeroutDown.cancel();
        TimeroutDown.start();
    }

    private void stopConnectTimeout() {
        Logger.i(TAG, "stopConnectTimeout ");
        if (null != TimeroutDown) {
            TimeroutDown.cancel();
        }
    }


    //TODO 读取信号值
    public void readRemoteRssi() {
//        Logger.i(TAG, "readRemoteRssi() " + mBLEControl);
        if (null != mBLEControl) {
            mBLEControl.getBluetoothGatt().readRemoteRssi();
        }
    }

    //设置主动断开
    private boolean isLinkLoss() {
        Logger.i(TAG, "isLinkLoss() " + bLinkLoss + " " + DeviceAddress);
        return bLinkLoss;
    }

    private void setbLinkLoss(boolean bset) {
        bLinkLoss = bset;
        Logger.i(TAG, "setbLinkLoss() " + bLinkLoss + " " + DeviceAddress);
    }

    //TODO 释放资源
    public void disConnect() {
        //断开设备并释放设备信息
        Logger.i(TAG, "release() " + DeviceAddress);
        //停掉超时计时
        stopConnectTimeout();
        //停掉断开延时计时
        stopCheckDisconnect();
        cleanControl();
        mMainHandler = null;
    }

    //自动重新连接设备，133和断开延时使用
    private void checkDisconnectConnect() {
        Logger.i(TAG, "checkDisconnectConnect() " + DeviceAddress);
        if (null == mBLEControl || !mBLEControl.isConnected()) {
            connectionHandler();
            startCheckDisconnect();
        }
    }

    //TODO 呼叫状态
    public boolean isCalling() {
        if (null != mBLEControl) {
            return mBLEControl.isCalling();
        }
        return false;
    }

    /**
     * 获取设备电量值
     *
     * @return
     */
    public int getBatteryLevel() {
        if (null != mBLEControl) {
            return mBLEControl.getBatteryLevel();
        }
        return 100;
    }

    private void MainHandlerPost(Runnable runnable) {
        if (null != mMainHandler) {
            mMainHandler.post(runnable);
        }
    }

    //关闭连接
    public void close() {
        if (null != mBLEControl) {
            mBLEControl.close();
        }
        setConnectionState(BluetoothProfile.STATE_DISCONNECTED);
    }
}
