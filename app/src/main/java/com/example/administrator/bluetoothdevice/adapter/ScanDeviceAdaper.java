package com.example.administrator.bluetoothdevice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.bluetoothdevice.R;
import com.example.administrator.bluetoothdevice.bean.ScanDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2018/4/12 13:37
 * 创建：Administrator on
 * 描述:扫描设备显示adapter
 */

public class ScanDeviceAdaper extends BaseAdapter {
    private List<ScanDevice> device_List;
    private Context mcontext;
    private String time;

    public ScanDeviceAdaper(Context context) {
        super();
        if (device_List == null) {
            device_List = new ArrayList<>();
        }
        this.mcontext = context;
    }

    public void updateData(List<ScanDevice> device_List) {
        if (device_List != null) {
            this.device_List.clear();
            this.device_List = device_List;
            notifyDataSetChanged();
        }
    }

    public void addData(List<ScanDevice> device_List) {
        if (device_List != null) {
            this.device_List.addAll(device_List);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return device_List.size();
    }

    @Override
    public Object getItem(int position) {
        return device_List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        ScanDevice DeviceItem = device_List.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mcontext).inflate(
                    R.layout.scandevice_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.menu_item_title.setText(DeviceItem.getDevice().getAddress().replaceAll(":", ""));
        viewHolder.img_rssi.setImageResource(R.drawable.device_icon_rssi_level);
        viewHolder.img_rssi.setImageLevel(DeviceItem.getRssiLevel(DeviceItem.getRssi()));
        return convertView;
    }

    class ViewHolder {
        TextView menu_item_title;
        ImageView img_rssi;

        public ViewHolder(View convertView) {
            menu_item_title = (TextView) convertView
                    .findViewById(R.id.menu_item_title);
            img_rssi = (ImageView) convertView
                    .findViewById(R.id.img_rssi);
        }
    }

}
