package com.example.administrator.bluetoothdevice.bean;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;

import com.example.administrator.bluetoothdevice.utils.Logger;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by 2018/4/12 15:04
 * 创建：Administrator on
 * 描述:
 */

public class MyBLEControl extends BLEControler {
    private final static String TAG = "MyBLEControl";
    //断开特征UUID
    private final static UUID LINK_LOSS_SERVICE_UUID = UUID.fromString("00001803-0000-1000-8000-00805f9b34fb");
    private final static UUID LINK_LOSS_CHARACTERISTIC_UUID = UUID.fromString("00002A06-0000-1000-8000-00805f9b34fb");

    public MyBLEControl(BluetoothGatt mBluetoothGatt) {
        super(mBluetoothGatt);
    }

    private int COUNT = 0;
    private Timer timer_manager = new Timer();
    private TimerTask task;

    @Override
    public boolean sendCallAlert(boolean alert) {
        boolean bRet = false;

        Logger.i(TAG, "sendCallAlert() WaringRing=" + WaringRing + " bRet=" + bRet);
        return bRet;
    }

    @Override
    public boolean registeredKeyNotification() {
        return setKeyNotification(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected boolean setKeyNotification(boolean set) {
        boolean bRet = false;
        if (null != KeyEventCharacteristic && null != mBluetoothGatt) {
            bRet = mBluetoothGatt.setCharacteristicNotification(KeyEventCharacteristic, set);
            if (bRet && set) {
                List<BluetoothGattDescriptor> descriptorList = KeyEventCharacteristic.getDescriptors();
                for (BluetoothGattDescriptor descriptor : descriptorList) {
                    KeyEventCharacteristic.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descriptor);
                }
            }
        }
        Logger.i(TAG, "setKeyNotification() bRet=" + bRet + " set=" + set);
        return bRet;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public boolean checkKeyNotification(UUID uuid) {
        Logger.i(TAG, "checkKeyNotification()" + KeyEventCharacteristic.getUuid().toString());
        Logger.i(TAG, "checkKeyNotification()" + uuid.toString());
        return KeyEventCharacteristic.getUuid().equals(uuid);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public int getBatteryLevel() {
        int nRet = 100;
        if (null != BatteryLevelCharacteristic && null != mBluetoothGatt) {
            boolean bRet = mBluetoothGatt.readCharacteristic(BatteryLevelCharacteristic);
            byte[] data = BatteryLevelCharacteristic.getValue();
            if (null != data) {
                nRet = data[0];
            }
        }
        return nRet;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public boolean sendLinkLoss() {
        // TODO: 2017/12/19 断开时停止呼叫的指令唐工建议添加在应用端
        sendCallAlert(false);
        boolean bRet = false;
        if (null != LinkLossCharacteristic && null != mBluetoothGatt) {
            byte[] data = new byte[]{0x01};
            LinkLossCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            LinkLossCharacteristic.setValue(data);
            bRet = mBluetoothGatt.writeCharacteristic(LinkLossCharacteristic);
        }
        if (!bRet) {
//           mBluetoothGatt.close();
            //TODO 解绑失败操作
            task = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            };
            timer_manager.schedule(task, 0, 500);
        } else {
            //TODO 解绑成功操作
            LinkLossCharacteristic = null;
        }
        Logger.i(TAG, "sendLinkLoss() " + bRet);
        return bRet;
    }

    /**
     * 解绑失败，连续解绑三次操作
     */
    public Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    COUNT++;
                    boolean isBRet = false;
                    if (COUNT > 3 || isBRet) {
                        COUNT = 0;
                        if (timer_manager != null) {
                            timer_manager.cancel();
                            timer_manager.purge();
                            timer_manager = null;
                        }
                    }
                    if (null != LinkLossCharacteristic && null != mBluetoothGatt) {
                        byte[] data = new byte[]{0x01};
                        LinkLossCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                        LinkLossCharacteristic.setValue(data);
                        isBRet = mBluetoothGatt.writeCharacteristic(LinkLossCharacteristic);
                        if (isBRet) {
                            LinkLossCharacteristic = null;
                        }
                    }
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void initCharacteristic() {
        List<BluetoothGattService> services = mBluetoothGatt.getServices();
        cleanCharacteristic();
        if (null != services) {
            for (BluetoothGattService service : services) {
                //断开
                if (service.getUuid().equals(LINK_LOSS_SERVICE_UUID)) {
                    List<BluetoothGattCharacteristic> Characteristics = service.getCharacteristics();
                    for (BluetoothGattCharacteristic value : Characteristics) {
                        if (value.getUuid().equals(LINK_LOSS_CHARACTERISTIC_UUID)) {
                            LinkLossCharacteristic = value;
                            break;
                        }
                    }
                }
            }
        }
        //设置KEY ACK
        setKeyAckCharacteristic();
    }

    //设置Key Ack
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void setKeyAckCharacteristic() {
        if (null != LinkLossCharacteristic && null != mBluetoothGatt) {
            byte[] value1 = "AcCrEdItiSOK".getBytes();
            LinkLossCharacteristic.setValue(value1);
            mBluetoothGatt.writeCharacteristic(LinkLossCharacteristic);
        }
    }

}