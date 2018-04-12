package com.example.administrator.bluetoothdevice.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.util.SparseArray;

import com.example.administrator.bluetoothdevice.base.BaseApplication;

import java.util.Map;

/**
 * Created by 2018/4/12 10:58
 * 创建：Administrator on
 * 描述:打印logger工具，debug模式打印
 */

public class Logger {

    public static void exception(Exception e) {
        e.printStackTrace();
    }

    public static void v(String tag, String msg) {
        if (canPrintLog())
            Log.v(tag, msg);
    }

    public static void v(String tag, String msg, Throwable t) {
        if (canPrintLog())
            Log.v(tag, msg, t);
    }

    public static void d(String tag, String msg) {
        if (canPrintLog())
            Log.d(tag, msg);
    }

    public static void d(String tag, String msg, Throwable t) {
        if (canPrintLog())
            Log.d(tag, msg, t);
    }

    public static void i(String tag, String msg) {
        if (canPrintLog())
            Log.i(tag, msg);
    }

    public static void i(String tag, String msg, Throwable t) {
        if (canPrintLog())
            Log.i(tag, msg, t);
    }

    public static void w(String tag, String msg) {
        if (canPrintLog())
            Log.w(tag, msg);
    }

    public static void w(String tag, String msg, Throwable t) {
        if (canPrintLog())
            Log.w(tag, msg, t);
    }

    public static void e(String tag, String msg) {
        if (canPrintLog())
            Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable t) {
        if (canPrintLog())
            Log.e(tag, msg, t);
    }

    /**
     * 判断apk当前是否是debug模式
     *
     * @return
     */
    public static boolean isDebugMode() {
        try {
            Context context = BaseApplication.getAppContext();
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    //TODO 是否打印到输入
    private static boolean canPrintLog() {
//        return isDebugMode();
        return true;
    }

    //TODO 打印HASH表数据
    public static void map(String tag, Map<String, String> maps) {
        if (canPrintLog()) {
            StringBuffer text = new StringBuffer();
            for (Map.Entry<String, String> map : maps.entrySet()) {
                text.append(map.getKey());
                text.append(" = ");
                text.append(map.getValue() == null ? "null" : map.getValue());
                text.append("\r\n");
            }
            i(tag, text.toString());
        }
    }

    public static void map(String tag, SparseArray<Object> maps) {
        if (canPrintLog()) {
            StringBuffer text = new StringBuffer();
            int len = maps.size();
            for (int i = 0; i < len; i++) {
                text.append(maps.keyAt(i));
                text.append(" = ");
                text.append(maps.valueAt(i) == null ? "null" : maps.valueAt(i).toString());
                text.append("\r\n");
            }
            i(tag, text.toString());
        }
    }
}
