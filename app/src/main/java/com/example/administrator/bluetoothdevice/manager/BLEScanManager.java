package com.example.administrator.bluetoothdevice.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.administrator.bluetoothdevice.callback.IBLEScanCallBack;
import com.example.administrator.bluetoothdevice.utils.Logger;
import com.example.administrator.bluetoothdevice.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 2018/4/12 11:20
 * 创建：Administrator on
 * 描述:蓝牙设备扫描统一管理
 */

public class BLEScanManager {
    private final BluetoothAdapter mBluetoothAdapter;

    private IBLEScanCallBack mBLEScanCB = null;
    private Object mLeScanCallback = null;
    private boolean isScan = false; //扫描状态记录

    public BLEScanManager() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private Object getLeScanCallback() {
        if (null == mLeScanCallback) {
            if (Build.VERSION.SDK_INT < 21) {
                mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                        scanHandler(device, rssi, scanRecord);
                    }
                };
            } else {
                mLeScanCallback = new ScanCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {

                        scanHandler(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
                    }
                };
            }
        }
        return mLeScanCallback;
    }

    private void scanHandler(BluetoothDevice device, int rssi, byte[] scanRecord) {
        try {
            JSONObject scanRecordobj = null;
            String data = null;
            String localName = null;
            try {
                scanRecordobj = Tools.decodeAdvData(scanRecord);
                data = scanRecordobj.getString("serviceData");
                localName = scanRecordobj.getString("localName");
            } catch (JSONException e) {
                Logger.e("", device.getAddress() + " not serviceData");
            }
            IBLEScanCallBack.Type type = IBLEScanCallBack.Type.DEVICE_TYPE_ONE;
//            if (null != data) {
                // TODO: 2018/4/12 设备类别设定和过滤
//                if (data.startsWith("")) {
//                    type = IBLEScanCallBack.Type.DEVICE_TYPE_TWO;
//                } else if (data.startsWith("")) {
//                    type = IBLEScanCallBack.Type.DEVICE_TYPE_THREE;
//                }
                if (null != mBLEScanCB) {
                    mBLEScanCB.OnScan(device, rssi, type);
                }
//            }
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public boolean Scan(IBLEScanCallBack callBack) {
        mBLEScanCB = callBack;
        if (mBluetoothAdapter.isEnabled()) {
            if (Build.VERSION.SDK_INT < 21) {
                isScan = mBluetoothAdapter.startLeScan((BluetoothAdapter.LeScanCallback) getLeScanCallback());
            } else {
                BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
                if (null != scanner) {
                    scanner.startScan((ScanCallback) getLeScanCallback());
                    isScan = true;
                }
            }
        }
        return isScan;
    }


    public void stopScan() {
        if (mBluetoothAdapter.isEnabled()) {
            if (Build.VERSION.SDK_INT < 21) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    mBluetoothAdapter.stopLeScan((BluetoothAdapter.LeScanCallback) getLeScanCallback());
                }
            } else {
                BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
                if (null != scanner) {
                    scanner.stopScan((ScanCallback) getLeScanCallback());
                }
            }
        }
        isScan = false;
        mLeScanCallback = null;
    }

    //获取扫描状态
    public boolean isScaning() {
        return isScan;
    }

}
