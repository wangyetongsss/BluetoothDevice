package com.example.administrator.bluetoothdevice.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.administrator.bluetoothdevice.R;
import com.example.administrator.bluetoothdevice.base.BaseActivity;

/**
 * 设备连接上之后等界面
 */
public class DeviceActivity extends BaseActivity {
    private TextView device_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        initView();
    }

    private void initView() {
        device_name = (TextView) findViewById(R.id.device_name);
        device_name.setText(getIntent().getExtras().getString("MAC"));
    }
}
