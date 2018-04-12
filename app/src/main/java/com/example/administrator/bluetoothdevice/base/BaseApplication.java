package com.example.administrator.bluetoothdevice.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by 2018/4/12 10:59
 * 创建：Administrator on
 * 描述:程序BaseApplication
 */

public class BaseApplication extends Application {

    //全局静态应用上下文对象
    private static Context AppContext;
    private static BaseApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext = getApplicationContext();
        mApplication = this;
    }


    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public synchronized static BaseApplication getInstance() {
        return mApplication;
    }

    //TODO 静态全局获取应用上下文对象
    public static Context getAppContext() {
        return AppContext;
    }

    //TODO 统一的启动ACTIVITY的方法
    public static void startActivity(Class<?> cls) {
        //FLAG_ACTIVITY_NEW_TASK注释掉打开界面会崩溃
        AppContext.startActivity(new Intent(AppContext, cls)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
