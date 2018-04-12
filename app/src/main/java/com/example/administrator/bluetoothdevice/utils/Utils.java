package com.example.administrator.bluetoothdevice.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.example.administrator.bluetoothdevice.base.BaseActivity;

/**
 * Created by 2018/4/12 12:02
 * 创建：Administrator on
 * 描述:通用性接口
 */

public class Utils {

    public static void showToast(BaseActivity activity, String string) {
        showToast(activity, string, null);
    }

    public static void showToast(BaseActivity activity, String string,
                                 final IToastCallBack callBack) {
        View view = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        if (null != view && null != string) {
            Snackbar snackbar = Snackbar.make(view, string, Snackbar.LENGTH_SHORT);
            snackbar.setAction("", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != callBack) {
                        callBack.onStateChang(IToastCallBack.STATE_CLICK);
                    }
                }
            });
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    if (null != callBack) {
                        callBack.onStateChang(IToastCallBack.STATE_DISMISSED);
                    }
                    super.onDismissed(transientBottomBar, event);
                }
            });
            snackbar.show();
        }
    }

    public interface IToastCallBack {
        public final static int STATE_CLICK = 1;
        public final static int STATE_DISMISSED = 2;

        public void onStateChang(int state);
    }
}
