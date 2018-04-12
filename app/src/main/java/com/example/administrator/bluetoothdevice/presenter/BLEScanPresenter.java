package com.example.administrator.bluetoothdevice.presenter;

import android.bluetooth.BluetoothDevice;

import com.example.administrator.bluetoothdevice.base.BaseApplication;
import com.example.administrator.bluetoothdevice.bean.BLEScanModel;
import com.example.administrator.bluetoothdevice.bean.BlueToothDevice;
import com.example.administrator.bluetoothdevice.bean.DeviceModel;
import com.example.administrator.bluetoothdevice.bean.ScanDevice;
import com.example.administrator.bluetoothdevice.callback.IBLEScanCallBack;
import com.example.administrator.bluetoothdevice.contract.BLEScanContract;
import com.example.administrator.bluetoothdevice.eventbus.EventsID;
import com.example.administrator.bluetoothdevice.eventbus.ViewEvent;
import com.example.administrator.bluetoothdevice.manager.BLEScanManager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by 2018/4/12 11:52
 * 创建：Administrator on
 * 描述:业务逻辑处理
 */

public class BLEScanPresenter implements BLEScanContract.Presenter {
    private final static String TAG = "BLEScanPresenter";
    private final BLEScanContract.View IBindView;
    private final BLEScanManager mScanManager;
    private IBLEScanCallBack mScanCB = null;


    public BLEScanPresenter(BLEScanContract.View iBindView) {
        IBindView = iBindView;
        mScanManager = new BLEScanManager();
    }

    @Override
    public void scanBluetooth() {
        BLEScanModel.getInstance().cleanDevice();
        mScanManager.Scan(getBLEScanCallBack());
    }

    @Override
    public void stopScan() {
        mScanManager.stopScan();
        BLEScanModel.getInstance().cleanDevice();
        IBindView.notifyDataSetChanged();
    }

    @Override
    public boolean isScaning() {
        return mScanManager.isScaning();
    }

    @Override
    public List<ScanDevice> getScanBluetooth() {
        return BLEScanModel.getInstance().getDevices();
    }

    @Override
    public void startConnect(String Mac, IBLEScanCallBack.Type Type) {
        DeviceModel Model = DeviceModel.getInstance();
        BlueToothDevice use = Model.addDevice(Mac);
        use.createDeviceManager(BaseApplication.getAppContext(),
                Mac);
        IBindView.onResult(BLEScanContract.RESULT_SUCCESS, Mac);
    }


    //TODO 获取扫描回调
    private IBLEScanCallBack getBLEScanCallBack() {
        if (null == mScanCB) {
            mScanCB = new IBLEScanCallBack() {
                @Override
                public void OnScan(BluetoothDevice device, int rssi, Type type) {
                    if (BLEScanModel.getInstance().addBluetoothDevice(device, rssi, type)) {
                        IBindView.notifyDataSetChanged();
                    }
                }
            };
        }
        return mScanCB;
    }
}