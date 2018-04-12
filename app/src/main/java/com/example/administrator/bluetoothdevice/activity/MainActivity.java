package com.example.administrator.bluetoothdevice.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.bluetoothdevice.R;
import com.example.administrator.bluetoothdevice.adapter.ScanDeviceAdaper;
import com.example.administrator.bluetoothdevice.base.BaseActivity;
import com.example.administrator.bluetoothdevice.bean.ScanDevice;
import com.example.administrator.bluetoothdevice.contract.BLEScanContract;
import com.example.administrator.bluetoothdevice.eventbus.EventsID;
import com.example.administrator.bluetoothdevice.eventbus.ViewEvent;
import com.example.administrator.bluetoothdevice.manager.BluetoothManager;
import com.example.administrator.bluetoothdevice.presenter.BLEScanPresenter;
import com.example.administrator.bluetoothdevice.service.DeviceService;
import com.example.administrator.bluetoothdevice.utils.Logger;
import com.example.administrator.bluetoothdevice.utils.Utils;
import com.example.administrator.bluetoothdevice.view.WaitingDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 主要展示界面
 */
public class MainActivity extends BaseActivity implements BLEScanContract.View, AdapterView.OnItemClickListener {
    private final static String TAG = "ScanDeviceActivity";
    private ListView ScanDeviceListView = null;
    private BluetoothManager bluetoothManager;
    //存储记录的已经绑定设备
    private BLEScanContract.Presenter IBLEScanPresenter = null;
    private final String[] PERMISSION = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private List<ScanDevice> deivce_List = null;
    private ScanDeviceAdaper scanDeviceAdaper = null;
    private List<ScanDevice> BindedDeviceList = new ArrayList<>();
    private DevicesServiceConnection mServiceConnection;
    private DeviceService mDeviceService = null;
    private WaitingDialog mWaitingDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this, DeviceService.class));
        initService();
        initView();
        IBLEScanPresenter = new BLEScanPresenter(this);
    }

    //TODO 初始化并连接服务
    private void initService() {
        Intent service = new Intent(MainActivity.this, DeviceService.class);
        mServiceConnection = new DevicesServiceConnection();
        bindService(service, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    //TODO 连接定义
////////////////////////////////////////////////////
    class DevicesServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDeviceService = ((DeviceService.LocalBinder) service).getServices();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (null != mDeviceService) {
                mDeviceService = null;
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothManager.release();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopScan();
    }

    private void initView() {
        if (scanDeviceAdaper == null) {
            scanDeviceAdaper = new ScanDeviceAdaper(MainActivity.this);
        }
        ScanDeviceListView = (ListView) findViewById(R.id.scan_device_list);
        ScanDeviceListView.setAdapter(scanDeviceAdaper);
        ScanDeviceListView.setOnItemClickListener(this);
        bluetoothManager = new BluetoothManager(this)
                .setTipsView(findViewById(R.id.text_view_bluetooth_state))
                .setStateChangCallBack(BluetoothStateChangCallBack);
    }

    private BluetoothManager.IBluetoothStateChangCallBack BluetoothStateChangCallBack
            = new BluetoothManager.IBluetoothStateChangCallBack() {
        @Override
        public void onStateChang(int state) {
            switch (state) {
                case BluetoothAdapter.STATE_ON: {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Logger.i(TAG, "IBluetoothStateChangCallBack " + hasWindowFocus());
                            startScan();
                        }
                    });
                }
                break;
                case BluetoothAdapter.STATE_OFF: {
                    stopScan();
                }
                break;
            }
        }
    };

    public void refreshUIView() {
        if (null != IBLEScanPresenter) {
            deivce_List = IBLEScanPresenter.getScanBluetooth();
            if (null != deivce_List) {
                Logger.i("", "notifyDataSetChanged() len=" + deivce_List.size());
                setUIVisibility(deivce_List.size() > 0);
                updateListView(deivce_List);
            }
        }
    }

    //切换控制列表和提示信息
    private void setUIVisibility(boolean list) {
        if (list) {
            ScanDeviceListView.setVisibility(View.VISIBLE);
        } else {
            ScanDeviceListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshUIView();
            }
        });
    }

    @Override
    public void showWaiting(boolean show) {

    }

    @Override
    public void onResult(int id, final String message) {
        final int result = id;
        final String msg = message;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (result) {
                    case BLEScanContract.RESULT_SUCCESS: {
                        EventBus.getDefault().post(new ViewEvent(EventsID.START_AUTO_CONNECT).setMessage(message));
                    }
                    break;
                    default: {
                        Utils.showToast(MainActivity.this, msg);
                    }
                    break;
                }
            }
        });
    }


    //TODO 开始扫描
    private void startScan() {
        Logger.i(TAG, "startScan() " + BluetoothManager.BluetoothState());
        if (BluetoothManager.BluetoothState()) {
            if (null != IBLEScanPresenter && !IBLEScanPresenter.isScaning()) {
                IBLEScanPresenter.scanBluetooth();
            }
        }
    }

    //TODO 停止扫描
    private void stopScan() {
        Logger.i(TAG, "stopScan()");
        if (null != IBLEScanPresenter) {
            IBLEScanPresenter.stopScan();
        }
    }

    //TODO 添加数据到列表
    private void updateListView(List<ScanDevice> lst) {
        if (null != ScanDeviceListView && null != lst) {
            BindedDeviceList.addAll(lst);
            scanDeviceAdaper.addData(BindedDeviceList);
        }
    }

    /**
     * 创建蓝牙连接的弹窗
     */
    private void createDialog() {
        if (null == mWaitingDialog) {
            mWaitingDialog = new WaitingDialog(this);
            mWaitingDialog.setIsClick(true);
            mWaitingDialog.setWaitText(getString(R.string.str_connect_led));
            mWaitingDialog.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO: 2017/12/19 listView添加了下拉刷新，使用onItemClick需要减1
        if (null != IBLEScanPresenter && position < BindedDeviceList.size()) {
            createDialog();
            IBLEScanPresenter.startConnect(BindedDeviceList.get(position).getDevice().getAddress(), BindedDeviceList.get(position).getType());
        }
    }

    @Override
    protected String[] getRuntimePermissions() {
        return PERMISSION;
    }

    @Override
    protected void onSuccess() {
        super.onSuccess();
        stopScan();
        startScan();
    }

    //TODO 进入展示页面
    private void startBindingActivity(String mac) {
        if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
            mWaitingDialog.dismiss();
        }
        startActivity(new Intent(this, DeviceActivity.class)
                .putExtra("MAC", mac)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    protected void onEventHandler(ViewEvent event) {
        super.onEventHandler(event);
        if (event.getEvent() == EventsID.DEVICE_CONNECT_SUCCESS) {
            startBindingActivity(event.getMessage());
            finish();
        }
    }
}
